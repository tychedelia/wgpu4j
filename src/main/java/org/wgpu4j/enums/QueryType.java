package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Query types for GPU profiling and measurement.
 */
public enum QueryType {
    /**
     * Occlusion queries - measure how many samples pass depth/stencil tests.
     * Useful for occlusion culling optimizations.
     */
    OCCLUSION(webgpu_h.WGPUQueryType_Occlusion()),

    /**
     * Timestamp queries - measure GPU execution time.
     * Useful for performance profiling and optimization.
     */
    TIMESTAMP(webgpu_h.WGPUQueryType_Timestamp());

    private final int value;

    QueryType(int value) {
        this.value = value;
    }

    /**
     * Gets the native WGPU value for this query type.
     */
    public int getValue() {
        return value;
    }

    /**
     * Converts a native WGPU query type value to the corresponding enum.
     */
    public static QueryType fromValue(int value) {
        for (QueryType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown query type value: " + value);
    }
}