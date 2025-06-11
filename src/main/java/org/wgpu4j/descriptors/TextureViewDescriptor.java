package org.wgpu4j.descriptors;

import org.wgpu4j.enums.*;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Configuration for creating a texture view.
 * Defines how a texture is interpreted when bound to shaders, including format, dimension, and range.
 */
public class TextureViewDescriptor {
    private final String label;
    private final TextureFormat format;
    private final TextureViewDimension dimension;
    private final int baseMipLevel;
    private final int mipLevelCount;
    private final int baseArrayLayer;
    private final int arrayLayerCount;
    private final TextureAspect aspect;

    private TextureViewDescriptor(Builder builder) {
        this.label = builder.label;
        this.format = builder.format;
        this.dimension = builder.dimension;
        this.baseMipLevel = builder.baseMipLevel;
        this.mipLevelCount = builder.mipLevelCount;
        this.baseArrayLayer = builder.baseArrayLayer;
        this.arrayLayerCount = builder.arrayLayerCount;
        this.aspect = builder.aspect;
    }

    public String getLabel() {
        return label;
    }

    public TextureFormat getFormat() {
        return format;
    }

    public TextureViewDimension getDimension() {
        return dimension;
    }

    public int getBaseMipLevel() {
        return baseMipLevel;
    }

    public int getMipLevelCount() {
        return mipLevelCount;
    }

    public int getBaseArrayLayer() {
        return baseArrayLayer;
    }

    public int getArrayLayerCount() {
        return arrayLayerCount;
    }

    public TextureAspect getAspect() {
        return aspect;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUTextureViewDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUTextureViewDescriptor.allocate(arena);

        WGPUTextureViewDescriptor.nextInChain(struct, MemorySegment.NULL);

        MemorySegment labelView = WGPUStringView.allocate(arena);
        if (label != null && !label.isEmpty()) {
            MemorySegment labelData = arena.allocateFrom(label);
            WGPUStringView.data(labelView, labelData);
            WGPUStringView.length(labelView, label.length());
        } else {
            WGPUStringView.data(labelView, MemorySegment.NULL);
            WGPUStringView.length(labelView, 0);
        }
        MemorySegment.copy(labelView, 0, struct, WGPUTextureViewDescriptor.label$offset(), WGPUStringView.sizeof());

        if (format != null) {
            WGPUTextureViewDescriptor.format(struct, format.getValue());
        } else {
            WGPUTextureViewDescriptor.format(struct, webgpu_h.WGPUTextureFormat_Undefined());
        }

        if (dimension != null) {
            WGPUTextureViewDescriptor.dimension(struct, dimension.getValue());
        } else {
            WGPUTextureViewDescriptor.dimension(struct, webgpu_h.WGPUTextureViewDimension_Undefined());
        }

        WGPUTextureViewDescriptor.baseMipLevel(struct, baseMipLevel);
        WGPUTextureViewDescriptor.mipLevelCount(struct, mipLevelCount);

        WGPUTextureViewDescriptor.baseArrayLayer(struct, baseArrayLayer);
        WGPUTextureViewDescriptor.arrayLayerCount(struct, arrayLayerCount);

        WGPUTextureViewDescriptor.aspect(struct, aspect.getValue());

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label;
        private TextureFormat format;
        private TextureViewDimension dimension;
        private int baseMipLevel = 0;
        private int mipLevelCount = 1;
        private int baseArrayLayer = 0;
        private int arrayLayerCount = 1;
        private TextureAspect aspect = TextureAspect.ALL;

        /**
         * Sets the debug label for the texture view.
         *
         * @param label The debug label for the texture view
         * @return this builder
         */
        public Builder label(String label) {
            this.label = label;
            return this;
        }

        /**
         * Sets the texture format for the view.
         * If not set, inherits the format from the parent texture.
         *
         * @param format The texture format for the view
         * @return this builder
         */
        public Builder format(TextureFormat format) {
            this.format = format;
            return this;
        }

        /**
         * Sets the texture view dimension.
         * If not set, inherits the dimension from the parent texture.
         *
         * @param dimension The texture view dimension
         * @return this builder
         */
        public Builder dimension(TextureViewDimension dimension) {
            this.dimension = dimension;
            return this;
        }

        /**
         * Sets the base mip level for the view.
         *
         * @param baseMipLevel The starting mip level (0-based)
         * @return this builder
         */
        public Builder baseMipLevel(int baseMipLevel) {
            this.baseMipLevel = baseMipLevel;
            return this;
        }

        /**
         * Sets the number of mip levels for the view.
         *
         * @param mipLevelCount The number of mip levels to include
         * @return this builder
         */
        public Builder mipLevelCount(int mipLevelCount) {
            this.mipLevelCount = mipLevelCount;
            return this;
        }

        /**
         * Sets the mip level range for the view.
         *
         * @param baseMipLevel  The starting mip level (0-based)
         * @param mipLevelCount The number of mip levels to include
         * @return this builder
         */
        public Builder mipLevelRange(int baseMipLevel, int mipLevelCount) {
            this.baseMipLevel = baseMipLevel;
            this.mipLevelCount = mipLevelCount;
            return this;
        }

        /**
         * Sets the base array layer for the view.
         *
         * @param baseArrayLayer The starting array layer (0-based)
         * @return this builder
         */
        public Builder baseArrayLayer(int baseArrayLayer) {
            this.baseArrayLayer = baseArrayLayer;
            return this;
        }

        /**
         * Sets the number of array layers for the view.
         *
         * @param arrayLayerCount The number of array layers to include
         * @return this builder
         */
        public Builder arrayLayerCount(int arrayLayerCount) {
            this.arrayLayerCount = arrayLayerCount;
            return this;
        }

        /**
         * Sets the array layer range for the view.
         *
         * @param baseArrayLayer  The starting array layer (0-based)
         * @param arrayLayerCount The number of array layers to include
         * @return this builder
         */
        public Builder arrayLayerRange(int baseArrayLayer, int arrayLayerCount) {
            this.baseArrayLayer = baseArrayLayer;
            this.arrayLayerCount = arrayLayerCount;
            return this;
        }

        /**
         * Sets the texture aspect for the view.
         * Primarily used for depth/stencil textures.
         *
         * @param aspect The texture aspect to view
         * @return this builder
         */
        public Builder aspect(TextureAspect aspect) {
            this.aspect = aspect;
            return this;
        }

        public TextureViewDescriptor build() {
            return new TextureViewDescriptor(this);
        }
    }
}