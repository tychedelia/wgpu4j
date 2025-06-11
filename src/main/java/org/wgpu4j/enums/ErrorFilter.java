package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Error filter types for WGPU error scopes.
 */
public enum ErrorFilter {
    /**
     * Validation errors - errors that occur due to invalid API usage.
     */
    VALIDATION(webgpu_h.WGPUErrorFilter_Validation()),

    /**
     * Out of memory errors - errors that occur when GPU runs out of memory.
     */
    OUT_OF_MEMORY(webgpu_h.WGPUErrorFilter_OutOfMemory()),

    /**
     * Internal errors - errors that occur due to GPU driver or system issues.
     */
    INTERNAL(webgpu_h.WGPUErrorFilter_Internal());

    private final int value;

    ErrorFilter(int value) {
        this.value = value;
    }

    /**
     * Gets the native WGPU value for this error filter.
     */
    public int getValue() {
        return value;
    }

    /**
     * Converts a native WGPU error filter value to the corresponding enum.
     */
    public static ErrorFilter fromValue(int value) {
        for (ErrorFilter filter : values()) {
            if (filter.value == value) {
                return filter;
            }
        }
        throw new IllegalArgumentException("Unknown error filter value: " + value);
    }
}