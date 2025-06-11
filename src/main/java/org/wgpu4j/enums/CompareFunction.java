package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Comparison function used for depth and stencil tests.
 */
public enum CompareFunction {
    /**
     * Undefined comparison function.
     */
    UNDEFINED(webgpu_h.WGPUCompareFunction_Undefined()),

    /**
     * Comparison test never passes.
     */
    NEVER(webgpu_h.WGPUCompareFunction_Never()),

    /**
     * Comparison test passes if new value is less than existing value.
     */
    LESS(webgpu_h.WGPUCompareFunction_Less()),

    /**
     * Comparison test passes if new value is less than or equal to existing value.
     */
    LESS_EQUAL(webgpu_h.WGPUCompareFunction_LessEqual()),

    /**
     * Comparison test passes if new value is greater than existing value.
     */
    GREATER(webgpu_h.WGPUCompareFunction_Greater()),

    /**
     * Comparison test passes if new value is greater than or equal to existing value.
     */
    GREATER_EQUAL(webgpu_h.WGPUCompareFunction_GreaterEqual()),

    /**
     * Comparison test passes if new value is equal to existing value.
     */
    EQUAL(webgpu_h.WGPUCompareFunction_Equal()),

    /**
     * Comparison test passes if new value is not equal to existing value.
     */
    NOT_EQUAL(webgpu_h.WGPUCompareFunction_NotEqual()),

    /**
     * Comparison test always passes.
     */
    ALWAYS(webgpu_h.WGPUCompareFunction_Always());

    private final int value;

    CompareFunction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts a WGPU compare function value to the corresponding enum.
     *
     * @param value The WGPU compare function value
     * @return The corresponding CompareFunction enum
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static CompareFunction fromValue(int value) {
        for (CompareFunction func : values()) {
            if (func.value == value) {
                return func;
            }
        }
        throw new IllegalArgumentException("Unknown compare function value: " + value);
    }
}