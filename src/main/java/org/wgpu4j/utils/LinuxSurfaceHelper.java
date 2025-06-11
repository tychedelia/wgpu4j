package org.wgpu4j.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Helper class for creating Linux X11 surface sources.
 */
public class LinuxSurfaceHelper {
    private static final Logger logger = LoggerFactory.getLogger(LinuxSurfaceHelper.class);

    /**
     * Creates an X11 surface source for Linux platforms.
     *
     * @param arena      The memory arena to allocate in
     * @param x11Window  The X11 window handle
     * @param x11Display The X11 display handle
     * @return MemorySegment representing the surface source
     */
    public static MemorySegment createX11SurfaceSource(Arena arena, long x11Window, long x11Display) {
        validateHandle(x11Window, "X11 Window");
        validateHandle(x11Display, "X11 Display");

        MemorySegment surfaceSource = WGPUSurfaceSourceXlibWindow.allocate(arena);
        MemorySegment chain = WGPUSurfaceSourceXlibWindow.chain(surfaceSource);
        WGPUChainedStruct.next(chain, MemorySegment.NULL);
        WGPUChainedStruct.sType(chain, webgpu_h.WGPUSType_SurfaceSourceXlibWindow());
        WGPUSurfaceSourceXlibWindow.display(surfaceSource, MemorySegment.ofAddress(x11Display));
        WGPUSurfaceSourceXlibWindow.window(surfaceSource, (int) x11Window);

        logger.info("Created X11 surface source with window: 0x{}, display: 0x{}",
                Long.toHexString(x11Window), Long.toHexString(x11Display));

        return surfaceSource;
    }

    /**
     * Validates that a native handle is not null/zero.
     *
     * @param handle     The handle to validate
     * @param handleType The type of handle for error messages
     * @throws IllegalArgumentException if the handle is invalid
     */
    public static void validateHandle(long handle, String handleType) {
        if (handle == 0) {
            throw new IllegalArgumentException(handleType + " handle cannot be null/zero");
        }
    }
}