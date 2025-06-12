package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Operations that can be performed on stencil buffer values.
 */
public enum StencilOperation {
    UNDEFINED(webgpu_h.WGPUStencilOperation_Undefined()),
    KEEP(webgpu_h.WGPUStencilOperation_Keep()),
    ZERO(webgpu_h.WGPUStencilOperation_Zero()),
    REPLACE(webgpu_h.WGPUStencilOperation_Replace()),
    INVERT(webgpu_h.WGPUStencilOperation_Invert()),
    INCREMENT_CLAMP(webgpu_h.WGPUStencilOperation_IncrementClamp()),
    DECREMENT_CLAMP(webgpu_h.WGPUStencilOperation_DecrementClamp()),
    INCREMENT_WRAP(webgpu_h.WGPUStencilOperation_IncrementWrap()),
    DECREMENT_WRAP(webgpu_h.WGPUStencilOperation_DecrementWrap());

    private final int value;

    StencilOperation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static StencilOperation fromValue(int value) {
        for (StencilOperation operation : values()) {
            if (operation.value == value) {
                return operation;
            }
        }
        throw new IllegalArgumentException("Unknown StencilOperation value: " + value);
    }
}