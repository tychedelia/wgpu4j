package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Face culling modes for triangle primitives.
 * Determines which triangles are discarded during rasterization.
 */
public enum CullMode {
    UNDEFINED(webgpu_h.WGPUCullMode_Undefined()),
    NONE(webgpu_h.WGPUCullMode_None()),
    FRONT(webgpu_h.WGPUCullMode_Front()),
    BACK(webgpu_h.WGPUCullMode_Back());

    private final int value;

    CullMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CullMode fromValue(int value) {
        for (CullMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown CullMode value: " + value);
    }
}