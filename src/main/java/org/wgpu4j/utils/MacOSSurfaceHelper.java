package org.wgpu4j.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for creating CAMetalLayer on macOS.
 * This class uses reflection to optionally use LWJGL's Objective-C runtime bindings.
 * If LWJGL is not available, it will throw an exception indicating the dependency is needed.
 */
public class MacOSSurfaceHelper {

    private static final Logger logger = LoggerFactory.getLogger(MacOSSurfaceHelper.class);

    /**
     * Creates a CAMetalLayer from an NSWindow handle.
     * This method requires LWJGL to be on the classpath for Objective-C runtime support.
     *
     * @param nsWindow The NSWindow handle from glfwGetCocoaWindow
     * @return The CAMetalLayer handle
     * @throws RuntimeException if LWJGL is not available or if layer creation fails
     */
    public static long createCAMetalLayer(long nsWindow) {
        if (nsWindow == 0) {
            throw new IllegalArgumentException("NSWindow handle cannot be null/zero");
        }

        try {
            Class<?> objcRuntimeClass = Class.forName("org.lwjgl.system.macosx.ObjCRuntime");
            Class<?> jniClass = Class.forName("org.lwjgl.system.JNI");
            Class<?> memoryUtilClass = Class.forName("org.lwjgl.system.MemoryUtil");

            var getLibrary = objcRuntimeClass.getDeclaredMethod("getLibrary");
            var selGetUid = objcRuntimeClass.getDeclaredMethod("sel_getUid", CharSequence.class);
            var objcGetClass = objcRuntimeClass.getDeclaredMethod("objc_getClass", CharSequence.class);
            var invokePPP = jniClass.getDeclaredMethod("invokePPP", long.class, long.class, long.class);
            var invokePPPV = jniClass.getDeclaredMethod("invokePPPV", long.class, long.class, long.class, long.class);
            var nullValue = memoryUtilClass.getDeclaredField("NULL");

            Object library = getLibrary.invoke(null);
            var getFunctionAddress = library.getClass().getMethod("getFunctionAddress", CharSequence.class);
            long objc_msgSend = (Long) getFunctionAddress.invoke(library, "objc_msgSend");

            long sel_alloc = (Long) selGetUid.invoke(null, "alloc");
            long sel_init = (Long) selGetUid.invoke(null, "init");
            long sel_contentView = (Long) selGetUid.invoke(null, "contentView");
            long sel_setLayer = (Long) selGetUid.invoke(null, "setLayer:");
            long sel_setWantsLayer = (Long) selGetUid.invoke(null, "setWantsLayer:");
            long class_CAMetalLayer = (Long) objcGetClass.invoke(null, "CAMetalLayer");
            long NULL = (Long) nullValue.get(null);

            if (class_CAMetalLayer == NULL) {
                throw new RuntimeException("CAMetalLayer class not found - QuartzCore framework not available");
            }
            if (objc_msgSend == NULL) {
                throw new RuntimeException("objc_msgSend function not found - Objective-C runtime not available");
            }

            long metalLayer = (Long) invokePPP.invoke(null,
                    invokePPP.invoke(null, class_CAMetalLayer, sel_alloc, objc_msgSend),
                    sel_init,
                    objc_msgSend);

            if (metalLayer == NULL) {
                throw new RuntimeException("Failed to create CAMetalLayer");
            }

            long contentView = (Long) invokePPP.invoke(null, nsWindow, sel_contentView, objc_msgSend);
            if (contentView == NULL) {
                throw new RuntimeException("Failed to get NSWindow content view");
            }

            invokePPPV.invoke(null, contentView, sel_setLayer, metalLayer, objc_msgSend);
            invokePPPV.invoke(null, contentView, sel_setWantsLayer, 1L, objc_msgSend);

            logger.info("Created CAMetalLayer: 0x{} for NSWindow: 0x{}",
                    Long.toHexString(metalLayer), Long.toHexString(nsWindow));

            return metalLayer;

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "LWJGL is required for macOS surface creation. Please add LWJGL to your classpath.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create CAMetalLayer using Objective-C runtime", e);
        }
    }
}