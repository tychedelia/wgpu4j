package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Aspect of a texture to be viewed.
 * Used primarily for depth/stencil textures to specify which component to access.
 */
public enum TextureAspect {
    /**
     * All aspects of the texture.
     * For color textures, this includes all color channels.
     * For depth/stencil textures, this includes both depth and stencil data.
     */
    ALL(webgpu_h.WGPUTextureAspect_All()),

    /**
     * Only the stencil component of depth/stencil textures.
     * Only valid for textures with stencil formats.
     */
    STENCIL_ONLY(webgpu_h.WGPUTextureAspect_StencilOnly()),

    /**
     * Only the depth component of depth/stencil textures.
     * Only valid for textures with depth formats.
     */
    DEPTH_ONLY(webgpu_h.WGPUTextureAspect_DepthOnly());

    private final int value;

    TextureAspect(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts a WGPU texture aspect value to the corresponding enum.
     *
     * @param value The WGPU texture aspect value
     * @return The corresponding TextureAspect enum
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static TextureAspect fromValue(int value) {
        for (TextureAspect aspect : values()) {
            if (aspect.value == value) {
                return aspect;
            }
        }
        throw new IllegalArgumentException("Unknown texture aspect value: " + value);
    }
}