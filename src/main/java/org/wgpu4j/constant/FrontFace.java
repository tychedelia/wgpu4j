package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Determines which winding order is considered front-facing for triangle primitives.
 */
public enum FrontFace {
    UNDEFINED(webgpu_h.WGPUFrontFace_Undefined()),
    CCW(webgpu_h.WGPUFrontFace_CCW()), CW(webgpu_h.WGPUFrontFace_CW());
    private final int value;

    FrontFace(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static FrontFace fromValue(int value) {
        for (FrontFace face : values()) {
            if (face.value == value) {
                return face;
            }
        }
        throw new IllegalArgumentException("Unknown FrontFace value: " + value);
    }
}