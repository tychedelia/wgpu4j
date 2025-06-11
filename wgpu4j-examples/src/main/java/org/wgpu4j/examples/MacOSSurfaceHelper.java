package org.wgpu4j.examples;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.lwjgl.system.macosx.ObjCRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.system.JNI.invokePPP;
import static org.lwjgl.system.JNI.invokePPPV;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.macosx.ObjCRuntime.*;

/**
 * Helper class for creating CAMetalLayer on macOS using LWJGL's Objective-C runtime bindings.
 * This is necessary for proper WebGPU surface creation on macOS.
 */
public class MacOSSurfaceHelper {

    private static final Logger logger = LoggerFactory.getLogger(MacOSSurfaceHelper.class);


    private static final long objc_msgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");


    private static final long sel_alloc = sel_getUid("alloc");
    private static final long sel_init = sel_getUid("init");
    private static final long sel_contentView = sel_getUid("contentView");
    private static final long sel_setLayer = sel_getUid("setLayer:");
    private static final long sel_setWantsLayer = sel_getUid("setWantsLayer:");
    private static final long sel_layer = sel_getUid("layer");


    private static final long class_CAMetalLayer = objc_getClass("CAMetalLayer");

    static {
        if (class_CAMetalLayer == NULL) {
            throw new RuntimeException("CAMetalLayer class not found - QuartzCore framework not available");
        }
        if (objc_msgSend == NULL) {
            throw new RuntimeException("objc_msgSend function not found - Objective-C runtime not available");
        }
    }

    /**
     * Creates a CAMetalLayer and attaches it to the NSWindow's content view.
     * This is required for WebGPU surface creation on macOS.
     *
     * @param nsWindow The NSWindow pointer from glfwGetCocoaWindow
     * @return The CAMetalLayer pointer that can be used for surface creation
     */
    public static long createCAMetalLayer(long nsWindow) {
        logger.info("Creating CAMetalLayer for NSWindow: 0x{}", Long.toHexString(nsWindow));

        try {
            long metalLayer = invokePPP(
                    invokePPP(class_CAMetalLayer, sel_alloc, objc_msgSend),
                    sel_init,
                    objc_msgSend
            );

            if (metalLayer == NULL) {
                throw new RuntimeException("Failed to create CAMetalLayer");
            }

            logger.debug("Created CAMetalLayer: 0x{}", Long.toHexString(metalLayer));
            long contentView = invokePPP(nsWindow, sel_contentView, objc_msgSend);

            if (contentView == NULL) {
                throw new RuntimeException("Failed to get NSWindow content view");
            }
            logger.debug("Got content view: 0x{}", Long.toHexString(contentView));


            invokePPPV(contentView, sel_setLayer, metalLayer, objc_msgSend);
            invokePPPV(contentView, sel_setWantsLayer, 1, objc_msgSend);
            logger.info("Successfully attached CAMetalLayer to NSWindow content view");

            return metalLayer;

        } catch (Exception e) {
            logger.error("Failed to create CAMetalLayer", e);
            throw new RuntimeException("CAMetalLayer creation failed", e);
        }
    }

    /**
     * Gets the CAMetalLayer from an NSView if it exists.
     *
     * @param nsView The NSView pointer
     * @return The CAMetalLayer pointer, or NULL if not a CAMetalLayer
     */
    public static long getCAMetalLayer(long nsView) {
        try {
            long layer = invokePPP(nsView, sel_layer, objc_msgSend);

            if (layer == NULL) {
                return NULL;
            }
            return layer;

        } catch (Exception e) {
            logger.warn("Failed to get layer from NSView", e);
            return NULL;
        }
    }
}