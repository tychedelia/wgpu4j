package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Dimension of a texture view.
 * Defines how the texture data is interpreted (1D, 2D, 3D, cube maps, arrays).
 */
public enum TextureViewDimension {
    /**
     * 1D texture view.
     */
    ONE_D(webgpu_h.WGPUTextureViewDimension_1D()),

    /**
     * 2D texture view.
     * Most common texture view type.
     */
    TWO_D(webgpu_h.WGPUTextureViewDimension_2D()),

    /**
     * 2D array texture view.
     * Multiple 2D textures accessible as an array in shaders.
     */
    TWO_D_ARRAY(webgpu_h.WGPUTextureViewDimension_2DArray()),

    /**
     * Cube map texture view.
     * 6 faces forming a cube, used for environment mapping.
     */
    CUBE(webgpu_h.WGPUTextureViewDimension_Cube()),

    /**
     * Cube map array texture view.
     * Multiple cube maps accessible as an array in shaders.
     */
    CUBE_ARRAY(webgpu_h.WGPUTextureViewDimension_CubeArray()),

    /**
     * 3D texture view.
     * Volume texture with width, height, and depth.
     */
    THREE_D(webgpu_h.WGPUTextureViewDimension_3D());

    private final int value;

    TextureViewDimension(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts a WGPU texture view dimension value to the corresponding enum.
     *
     * @param value The WGPU texture view dimension value
     * @return The corresponding TextureViewDimension enum
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static TextureViewDimension fromValue(int value) {
        for (TextureViewDimension dimension : values()) {
            if (dimension.value == value) {
                return dimension;
            }
        }
        throw new IllegalArgumentException("Unknown texture view dimension value: " + value);
    }
}