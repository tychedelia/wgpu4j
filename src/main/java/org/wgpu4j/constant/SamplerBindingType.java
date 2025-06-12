package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Type of sampler binding in a bind group layout.
 */
public enum SamplerBindingType {
    /**
     * Filtering sampler that can use linear interpolation.
     * Most common sampler type for textures.
     */
    FILTERING(webgpu_h.WGPUSamplerBindingType_Filtering()),

    /**
     * Non-filtering sampler that only supports nearest neighbor sampling.
     * Used for textures that should not be interpolated.
     */
    NON_FILTERING(webgpu_h.WGPUSamplerBindingType_NonFiltering()),

    /**
     * Comparison sampler for shadow mapping.
     * Used with depth textures for shadow comparisons.
     */
    COMPARISON(webgpu_h.WGPUSamplerBindingType_Comparison());

    private final int value;

    SamplerBindingType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts a WGPU sampler binding type value to the corresponding enum.
     *
     * @param value The WGPU sampler binding type value
     * @return The corresponding SamplerBindingType enum
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static SamplerBindingType fromValue(int value) {
        for (SamplerBindingType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown sampler binding type value: " + value);
    }
}