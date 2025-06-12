package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Filtering mode for mipmap level selection.
 */
public enum MipmapFilterMode {
    /**
     * Nearest mipmap level selection.
     * Uses the mipmap level closest to the computed level of detail.
     */
    NEAREST(webgpu_h.WGPUMipmapFilterMode_Nearest()),

    /**
     * Linear mipmap level interpolation.
     * Linearly interpolates between the two closest mipmap levels.
     */
    LINEAR(webgpu_h.WGPUMipmapFilterMode_Linear());

    private final int value;

    MipmapFilterMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts a WGPU mipmap filter mode value to the corresponding enum.
     *
     * @param value The WGPU mipmap filter mode value
     * @return The corresponding MipmapFilterMode enum
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static MipmapFilterMode fromValue(int value) {
        for (MipmapFilterMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown mipmap filter mode value: " + value);
    }
}