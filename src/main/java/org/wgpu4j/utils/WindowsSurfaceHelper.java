package org.wgpu4j.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Helper class for creating Windows-specific surface sources for WebGPU.
 * This handles the platform-specific details of setting up HWND and HINSTANCE
 * for Windows surface creation.
 */
public class WindowsSurfaceHelper {

    private static final Logger logger = LoggerFactory.getLogger(WindowsSurfaceHelper.class);

    /**
     * Creates a Windows surface source from a GLFW window handle.
     *
     * @param arena The arena to allocate the surface source in
     * @param hwnd  The window handle from glfwGetWin32Window
     * @return MemorySegment representing the WGPUSurfaceSourceWindowsHWND struct
     */
    public static MemorySegment createWindowsSurfaceSource(Arena arena, long hwnd) {
        long hinstance = getCurrentProcessInstance();
        return createWindowsSurfaceSource(arena, hwnd, hinstance);
    }

    /**
     * Creates a Windows surface source from explicit HWND and HINSTANCE handles.
     *
     * @param arena The arena to allocate the surface source in
     * @param hwnd  The window handle
     * @param hinstance The instance handle
     * @return MemorySegment representing the WGPUSurfaceSourceWindowsHWND struct
     */
    public static MemorySegment createWindowsSurfaceSource(Arena arena, long hwnd, long hinstance) {
        validateHandle(hwnd, "HWND");
        
        logger.info("Creating Windows surface source for HWND: 0x{}", Long.toHexString(hwnd));

        MemorySegment surfaceSource = WGPUSurfaceSourceWindowsHWND.allocate(arena);
        MemorySegment chain = WGPUSurfaceSourceWindowsHWND.chain(surfaceSource);

        WGPUChainedStruct.next(chain, MemorySegment.NULL);
        WGPUChainedStruct.sType(chain, webgpu_h.WGPUSType_SurfaceSourceWindowsHWND());

        WGPUSurfaceSourceWindowsHWND.hwnd(surfaceSource, MemorySegment.ofAddress(hwnd));
        WGPUSurfaceSourceWindowsHWND.hinstance(surfaceSource, MemorySegment.ofAddress(hinstance));

        logger.info("Created Windows surface source with HWND: 0x{}, HINSTANCE: 0x{}",
                Long.toHexString(hwnd), Long.toHexString(hinstance));

        return surfaceSource;
    }

    /**
     * Gets the actual process instance handle using a more robust method.
     * This is a placeholder for future enhancement - could use JNI to call
     * GetModuleHandle(NULL) from kernel32.dll for the real process handle.
     *
     * @return The process instance handle
     */
    public static long getCurrentProcessInstance() {


        return 0x400000;
    }

    /**
     * Validates that a Windows handle is non-zero (basic sanity check).
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
}