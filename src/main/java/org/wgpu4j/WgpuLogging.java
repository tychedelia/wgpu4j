package org.wgpu4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wgpu4j.bindings.wgpu_h;
import org.wgpu4j.bindings.WGPULogCallback;
import org.wgpu4j.bindings.WGPUStringView;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * WGPU native logging utilities using generated bindings
 */
public class WgpuLogging {

    private static final Logger logger = LoggerFactory.getLogger(WgpuLogging.class);

    static {

        WgpuNative.ensureLoaded();
    }


    public static final int WGPU_LOG_LEVEL_OFF = 0x00000000;
    public static final int WGPU_LOG_LEVEL_ERROR = 0x00000001;
    public static final int WGPU_LOG_LEVEL_WARN = 0x00000002;
    public static final int WGPU_LOG_LEVEL_INFO = 0x00000003;
    public static final int WGPU_LOG_LEVEL_DEBUG = 0x00000004;
    public static final int WGPU_LOG_LEVEL_TRACE = 0x00000005;

    private static final Linker LINKER = Linker.nativeLinker();


    private static LogCallback currentCallback = null;
    private static Arena callbackArena = null;

    /**
     * Log callback interface
     */
    @FunctionalInterface
    public interface LogCallback {
        void log(int level, String message);
    }

    /**
     * Set WGPU log level using generated bindings
     */
    public static void setLogLevel(int level) {
        try {
            wgpu_h.wgpuSetLogLevel(level);
            logger.debug("Set WGPU log level to: {}", level);
        } catch (Exception e) {
            logger.error("Failed to set log level: {}", level, e);
        }
    }

    /**
     * Set WGPU log callback using generated bindings
     */
    public static void setLogCallback(LogCallback callback) {
        try {

            if (callbackArena != null) {
                callbackArena.close();
                callbackArena = null;
            }

            if (callback == null) {
                wgpu_h.wgpuSetLogCallback(MemorySegment.NULL, MemorySegment.NULL);
                currentCallback = null;
                return;
            }


            callbackArena = Arena.ofShared();


            currentCallback = callback;


            var callbackDesc = WGPULogCallback.descriptor();
            MethodHandle callbackMH = MethodHandles.lookup().findStatic(
                    WgpuLogging.class, "nativeLogCallback",
                    MethodType.methodType(void.class, int.class, MemorySegment.class, MemorySegment.class)
            );

            MemorySegment callbackStub = LINKER.upcallStub(callbackMH, callbackDesc, callbackArena);

            wgpu_h.wgpuSetLogCallback(callbackStub, MemorySegment.NULL);
            logger.debug("Set WGPU log callback");

        } catch (Exception e) {
            logger.error("Failed to set log callback", e);
        }
    }

    /**
     * Native callback method (must be static and public)
     */
    public static void nativeLogCallback(int level, MemorySegment messagePtr, MemorySegment userdata) {
        try {
            String message = "Unknown";


            if (!messagePtr.equals(MemorySegment.NULL)) {
                try {

                    MemorySegment dataPtr = WGPUStringView.data(messagePtr);
                    long length = WGPUStringView.length(messagePtr);

                    if (!dataPtr.equals(MemorySegment.NULL) && length > 0 && length < 100000) {

                        MemorySegment stringData = dataPtr.reinterpret(length);
                        byte[] bytes = stringData.toArray(ValueLayout.JAVA_BYTE);
                        message = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
                    } else {
                        message = "Invalid log message data";
                    }
                } catch (Exception e) {
                    message = "Log message unavailable: " + e.getMessage();
                    logger.warn("Failed to parse WGPU log message", e);
                }
            }

            if (currentCallback != null) {
                currentCallback.log(level, message);
            }
        } catch (Exception e) {
            logger.error("Error in native log callback", e);
        }
    }

    /**
     * Cleanup resources
     */
    public static void cleanup() {
        setLogCallback(null);
        if (callbackArena != null) {
            callbackArena.close();
            callbackArena = null;
        }
    }
}