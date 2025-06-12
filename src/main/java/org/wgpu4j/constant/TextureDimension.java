package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Texture dimension specifying the type of texture.
 */
public enum TextureDimension {
    /**
     * 1D texture - a texture with only width.
     */
    ONE_D(webgpu_h.WGPUTextureDimension_1D()),

    /**
     * 2D texture - a texture with width and height.
     * This is the most common texture type.
     */
    TWO_D(webgpu_h.WGPUTextureDimension_2D()),

    /**
     * 3D texture - a texture with width, height, and depth.
     * Used for volume textures and 3D effects.
     */
    THREE_D(webgpu_h.WGPUTextureDimension_3D());

    private final int value;

    TextureDimension(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts a WGPU texture dimension value to the corresponding enum.
     *
     * @param value The WGPU texture dimension value
     * @return The corresponding TextureDimension enum
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static TextureDimension fromValue(int value) {
        for (TextureDimension dimension : values()) {
            if (dimension.value == value) {
                return dimension;
            }
        }
        throw new IllegalArgumentException("Unknown texture dimension value: " + value);
    }
}