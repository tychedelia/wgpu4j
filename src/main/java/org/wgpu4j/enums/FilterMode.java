package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Filtering mode for texture sampling.
 */
public enum FilterMode {
    /**
     * Nearest neighbor filtering.
     * Returns the value of the texel nearest to the texture coordinate.
     */
    NEAREST(webgpu_h.WGPUFilterMode_Nearest()),

    /**
     * Linear filtering.
     * Returns the weighted average of the four texels nearest to the texture coordinate.
     */
    LINEAR(webgpu_h.WGPUFilterMode_Linear());

    private final int value;

    FilterMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts a WGPU filter mode value to the corresponding enum.
     *
     * @param value The WGPU filter mode value
     * @return The corresponding FilterMode enum
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static FilterMode fromValue(int value) {
        for (FilterMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown filter mode value: " + value);
    }
}