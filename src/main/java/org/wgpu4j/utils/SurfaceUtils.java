package org.wgpu4j.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wgpu4j.WgpuException;
import org.wgpu4j.core.Instance;
import org.wgpu4j.core.Surface;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.lang.reflect.Constructor;

/**
 * Utility class for creating WebGPU surfaces from various window systems.
 * Provides both convenience methods for common cases and advanced methods for custom window handling.
 */
public class SurfaceUtils {
    private static final Logger logger = LoggerFactory.getLogger(SurfaceUtils.class);

    /**
     * Platform types for surface creation.
     */
    public enum Platform {
        WINDOWS,
        MACOS,
        LINUX_X11
    }

    /**
     * Creates a surface from a GLFW window handle (convenience method).
     * Automatically detects the platform and uses appropriate native calls.
     *
     * @param instance The WebGPU instance
     * @param glfwWindow The GLFW window handle
     * @return A new Surface instance
     * @throws WgpuException if surface creation fails
     */
    public static Surface createFromGLFWWindow(Instance instance, long glfwWindow) {
        if (glfwWindow == 0) {
            throw new IllegalArgumentException("GLFW window handle cannot be null/zero");
        }

        String os = System.getProperty("os.name").toLowerCase();
        
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment surfaceDesc = WGPUSurfaceDescriptor.allocate(arena);
            MemorySegment surfaceSource;

            if (os.contains("win")) {
                // Use LWJGL's GLFW native bindings
                try {
                    Class<?> glfwNativeWin32 = Class.forName("org.lwjgl.glfw.GLFWNativeWin32");
                    var getWin32Window = glfwNativeWin32.getMethod("glfwGetWin32Window", long.class);
                    long hwnd = (Long) getWin32Window.invoke(null, glfwWindow);
                    
                    surfaceSource = WindowsSurfaceHelper.createWindowsSurfaceSource(arena, hwnd);
                } catch (Exception e) {
                    throw new WgpuException("Failed to get Windows window handle from GLFW", e);
                }

            } else if (os.contains("mac")) {
                try {
                    Class<?> glfwNativeCocoa = Class.forName("org.lwjgl.glfw.GLFWNativeCocoa");
                    var getCocoaWindow = glfwNativeCocoa.getMethod("glfwGetCocoaWindow", long.class);
                    long nsWindow = (Long) getCocoaWindow.invoke(null, glfwWindow);
                    long metalLayer = MacOSSurfaceHelper.createCAMetalLayer(nsWindow);

                    surfaceSource = WGPUSurfaceSourceMetalLayer.allocate(arena);
                    MemorySegment chain = WGPUSurfaceSourceMetalLayer.chain(surfaceSource);
                    WGPUChainedStruct.next(chain, MemorySegment.NULL);
                    WGPUChainedStruct.sType(chain, webgpu_h.WGPUSType_SurfaceSourceMetalLayer());
                    WGPUSurfaceSourceMetalLayer.layer(surfaceSource, MemorySegment.ofAddress(metalLayer));
                } catch (Exception e) {
                    throw new WgpuException("Failed to get macOS window handle from GLFW", e);
                }

            } else {
                try {
                    Class<?> glfwNativeX11 = Class.forName("org.lwjgl.glfw.GLFWNativeX11");
                    var getX11Window = glfwNativeX11.getMethod("glfwGetX11Window", long.class);
                    var getX11Display = glfwNativeX11.getMethod("glfwGetX11Display");
                    long x11Window = (Long) getX11Window.invoke(null, glfwWindow);
                    long x11Display = (Long) getX11Display.invoke(null);
                    
                    surfaceSource = LinuxSurfaceHelper.createX11SurfaceSource(arena, x11Window, x11Display);
                } catch (Exception e) {
                    throw new WgpuException("Failed to get X11 window handle from GLFW", e);
                }
            }

            return createSurfaceInternal(instance, arena, surfaceDesc, surfaceSource, "GLFW Surface");
        }
    }

    /**
     * Creates a surface from native window handles (advanced method).
     * Allows users to provide their own window system integration.
     *
     * @param instance The WebGPU instance
     * @param platform The target platform
     * @param windowHandle The native window handle
     * @param extraHandle Additional handle (HINSTANCE for Windows, X11 display for Linux, unused for macOS)
     * @return A new Surface instance
     * @throws WgpuException if surface creation fails
     */
    public static Surface createFromNativeHandle(Instance instance, Platform platform, 
                                               long windowHandle, long extraHandle) {
        if (windowHandle == 0) {
            throw new IllegalArgumentException("Window handle cannot be null/zero");
        }

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment surfaceDesc = WGPUSurfaceDescriptor.allocate(arena);
            MemorySegment surfaceSource;

            switch (platform) {
                case WINDOWS -> {
                    surfaceSource = WindowsSurfaceHelper.createWindowsSurfaceSource(arena, windowHandle, extraHandle);
                }
                case MACOS -> {
                    long metalLayer = MacOSSurfaceHelper.createCAMetalLayer(windowHandle);
                    surfaceSource = WGPUSurfaceSourceMetalLayer.allocate(arena);
                    MemorySegment chain = WGPUSurfaceSourceMetalLayer.chain(surfaceSource);
                    WGPUChainedStruct.next(chain, MemorySegment.NULL);
                    WGPUChainedStruct.sType(chain, webgpu_h.WGPUSType_SurfaceSourceMetalLayer());
                    WGPUSurfaceSourceMetalLayer.layer(surfaceSource, MemorySegment.ofAddress(metalLayer));
                }
                case LINUX_X11 -> {
                    if (extraHandle == 0) {
                        throw new IllegalArgumentException("X11 display handle is required for Linux");
                    }
                    surfaceSource = LinuxSurfaceHelper.createX11SurfaceSource(arena, windowHandle, extraHandle);
                }
                default -> throw new IllegalArgumentException("Unsupported platform: " + platform);
            }

            return createSurfaceInternal(instance, arena, surfaceDesc, surfaceSource, "Native Surface");
        }
    }

    /**
     * Internal method to create the surface from prepared descriptors.
     */
    private static Surface createSurfaceInternal(Instance instance, Arena arena, 
                                               MemorySegment surfaceDesc, MemorySegment surfaceSource, 
                                               String label) {
        try {
            WGPUSurfaceDescriptor.nextInChain(surfaceDesc, surfaceSource);
            MemorySegment labelData = arena.allocateFrom(label);
            MemorySegment labelView = WGPUSurfaceDescriptor.label(surfaceDesc);
            WGPUStringView.data(labelView, labelData);
            WGPUStringView.length(labelView, label.length());

            MemorySegment surfaceHandle = webgpu_h.wgpuInstanceCreateSurface(
                    instance.getHandle(), surfaceDesc);

            if (surfaceHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create surface");
            }

            // Use reflection to create Surface instance (since constructor is package-private)
            Constructor<Surface> constructor = Surface.class.getDeclaredConstructor(MemorySegment.class);
            constructor.setAccessible(true);
            Surface surface = constructor.newInstance(surfaceHandle);

            logger.info("Surface created successfully with label: {}", label);
            return surface;

        } catch (WgpuException e) {
            throw e;
        } catch (Exception e) {
            throw new WgpuException("Failed to create surface", e);
        }
    }
}