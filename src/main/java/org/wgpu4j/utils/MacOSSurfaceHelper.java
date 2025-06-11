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
            // Use reflection to access LWJGL classes if available
            Class<?> objcRuntimeClass = Class.forName("org.lwjgl.system.macosx.ObjCRuntime");
            Class<?> jniClass = Class.forName("org.lwjgl.system.JNI");
            
            // Get required methods via reflection
            var getLibrary = objcRuntimeClass.getMethod("getLibrary");
            var getFunctionAddress = getLibrary.getReturnType().getMethod("getFunctionAddress", String.class);
            var selGetUid = objcRuntimeClass.getMethod("sel_getUid", String.class);
            var objcGetClass = objcRuntimeClass.getMethod("objc_getClass", String.class);
            var invokePPP = jniClass.getMethod("invokePPP", long.class, long.class, long.class);
            var invokePPPV = jniClass.getMethod("invokePPPV", long.class, long.class, long.class, long.class);

            // Get function pointers
            Object library = getLibrary.invoke(null);
            long objc_msgSend = (Long) getFunctionAddress.invoke(library, "objc_msgSend");

            // Get selectors and classes
            long sel_alloc = (Long) selGetUid.invoke(null, "alloc");
            long sel_init = (Long) selGetUid.invoke(null, "init");
            long sel_contentView = (Long) selGetUid.invoke(null, "contentView");
            long sel_setLayer = (Long) selGetUid.invoke(null, "setLayer:");
            long sel_setWantsLayer = (Long) selGetUid.invoke(null, "setWantsLayer:");
            long class_CAMetalLayer = (Long) objcGetClass.invoke(null, "CAMetalLayer");

            // Create CAMetalLayer instance: [[CAMetalLayer alloc] init]
            long metalLayer = (Long) invokePPP.invoke(null,
                    invokePPP.invoke(null, class_CAMetalLayer, sel_alloc, objc_msgSend),
                    sel_init,
                    objc_msgSend);

            if (metalLayer == 0) {
                throw new RuntimeException("Failed to create CAMetalLayer");
            }

            // Get the content view and set up the layer
            long contentView = (Long) invokePPP.invoke(null, nsWindow, sel_contentView, objc_msgSend);
            if (contentView == 0) {
                throw new RuntimeException("Failed to get NSWindow content view");
            }

            // Set the layer: [contentView setLayer:metalLayer]
            invokePPPV.invoke(null, contentView, sel_setLayer, metalLayer, objc_msgSend);
            // Enable layer backing: [contentView setWantsLayer:YES]
            var invokePPPVInt = jniClass.getMethod("invokePPPV", long.class, long.class, int.class, long.class);
            invokePPPVInt.invoke(null, contentView, sel_setWantsLayer, 1, objc_msgSend);

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