package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Blend operations that determine how source and destination colors are combined.
 */
public enum BlendOperation {
    UNDEFINED(webgpu_h.WGPUBlendOperation_Undefined()),
    ADD(webgpu_h.WGPUBlendOperation_Add()),
    SUBTRACT(webgpu_h.WGPUBlendOperation_Subtract()),
    REVERSE_SUBTRACT(webgpu_h.WGPUBlendOperation_ReverseSubtract()),
    MIN(webgpu_h.WGPUBlendOperation_Min()),
    MAX(webgpu_h.WGPUBlendOperation_Max());

    private final int value;

    BlendOperation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BlendOperation fromValue(int value) {
        for (BlendOperation operation : values()) {
            if (operation.value == value) {
                return operation;
            }
        }
        throw new IllegalArgumentException("Unknown BlendOperation value: " + value);
    }
}