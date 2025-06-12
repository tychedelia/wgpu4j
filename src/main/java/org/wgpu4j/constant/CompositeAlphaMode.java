package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Alpha compositing mode for surface presentation.
 */
public enum CompositeAlphaMode {
    /**
     * Automatic selection based on platform capabilities.
     */
    AUTO(webgpu_h.WGPUCompositeAlphaMode_Auto()),

    /**
     * Opaque compositing - alpha is ignored.
     */
    OPAQUE(webgpu_h.WGPUCompositeAlphaMode_Opaque()),

    /**
     * Premultiplied alpha compositing.
     */
    PREMULTIPLIED(webgpu_h.WGPUCompositeAlphaMode_Premultiplied()),

    /**
     * Unpremultiplied alpha compositing.
     */
    UNPREMULTIPLIED(webgpu_h.WGPUCompositeAlphaMode_Unpremultiplied()),

    /**
     * Inherit alpha mode from parent.
     */
    INHERIT(webgpu_h.WGPUCompositeAlphaMode_Inherit());

    private final int value;

    CompositeAlphaMode(int value) {
        this.value = value;
    }

    /**
     * Gets the native WGPU value for this composite alpha mode.
     */
    public int getValue() {
        return value;
    }

    /**
     * Converts a native WGPU composite alpha mode value to the corresponding enum.
     */
    public static CompositeAlphaMode fromValue(int value) {
        for (CompositeAlphaMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown composite alpha mode value: " + value);
    }
}