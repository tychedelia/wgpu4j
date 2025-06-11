package org.wgpu4j.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Helper class for creating Linux-specific surface sources for WebGPU.
 * This handles X11 display and window setup for Linux surface creation.
 */
public class LinuxSurfaceHelper {

    private static final Logger logger = LoggerFactory.getLogger(LinuxSurfaceHelper.class);

    /**
     * Creates an X11 surface source from GLFW window handles.
     *
     * @param arena      The arena to allocate the surface source in
     * @param x11Window  The X11 window handle from glfwGetX11Window
     * @param x11Display The X11 display handle from glfwGetX11Display
     * @return MemorySegment representing the WGPUSurfaceSourceXlibWindow struct
     */
    public static MemorySegment createX11SurfaceSource(Arena arena, long x11Window, long x11Display) {
        logger.info("Creating X11 surface source for Window: 0x{}, Display: 0x{}",
                Long.toHexString(x11Window), Long.toHexString(x11Display));

        validateHandle(x11Window, "X11 Window");
        validateHandle(x11Display, "X11 Display");

        MemorySegment surfaceSource = WGPUSurfaceSourceXlibWindow.allocate(arena);
        MemorySegment chain = WGPUSurfaceSourceXlibWindow.chain(surfaceSource);


        WGPUChainedStruct.next(chain, MemorySegment.NULL);
        WGPUChainedStruct.sType(chain, webgpu_h.WGPUSType_SurfaceSourceXlibWindow());


        WGPUSurfaceSourceXlibWindow.window(surfaceSource, x11Window);
        WGPUSurfaceSourceXlibWindow.display(surfaceSource, MemorySegment.ofAddress(x11Display));

        logger.info("Created X11 surface source successfully");

        return surfaceSource;
    }

    /**
     * Validates that an X11 handle is non-zero (basic sanity check).
     *
     * @param handle     The handle to validate
     * @param handleName The name of the handle for error messages
     * @throws IllegalArgumentException if the handle is invalid
     */
    public static void validateHandle(long handle, String handleName) {
        if (handle == 0) {
            throw new IllegalArgumentException(handleName + " cannot be NULL (0)");
        }
        logger.debug("Validated {}: 0x{}", handleName, Long.toHexString(handle));
    }

    /**
     * Checks if the current system is running X11 by examining environment variables.
     * This is a basic heuristic and may not be 100% accurate in all cases.
     *
     * @return true if X11 is likely being used
     */
    public static boolean isX11Available() {
        String display = System.getenv("DISPLAY");
        String waylandDisplay = System.getenv("WAYLAND_DISPLAY");


        boolean hasX11 = display != null && !display.isEmpty();
        boolean hasWayland = waylandDisplay != null && !waylandDisplay.isEmpty();

        logger.debug("X11 detection - DISPLAY: {}, WAYLAND_DISPLAY: {}", display, waylandDisplay);

        return hasX11 && !hasWayland;
    }
}