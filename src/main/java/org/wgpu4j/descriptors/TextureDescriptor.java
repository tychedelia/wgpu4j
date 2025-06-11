package org.wgpu4j.descriptors;

import org.wgpu4j.enums.TextureFormat;
import org.wgpu4j.enums.TextureDimension;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Configuration for creating a texture.
 * Supports 1D, 2D, and 3D textures with comprehensive options for sampling, mipmapping, and multisampling.
 */
public class TextureDescriptor {
    private final String label;
    private final int width;
    private final int height;
    private final int depthOrArrayLayers;
    private final TextureFormat format;
    private final long usage;
    private final TextureDimension dimension;
    private final int mipLevelCount;
    private final int sampleCount;

    private TextureDescriptor(Builder builder) {
        this.label = builder.label;
        this.width = builder.width;
        this.height = builder.height;
        this.depthOrArrayLayers = builder.depthOrArrayLayers;
        this.format = builder.format;
        this.usage = builder.usage;
        this.dimension = builder.dimension;
        this.mipLevelCount = builder.mipLevelCount;
        this.sampleCount = builder.sampleCount;
    }

    public String getLabel() {
        return label;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepthOrArrayLayers() {
        return depthOrArrayLayers;
    }

    public TextureFormat getFormat() {
        return format;
    }

    public long getUsage() {
        return usage;
    }

    public TextureDimension getDimension() {
        return dimension;
    }

    public int getMipLevelCount() {
        return mipLevelCount;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUTextureDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUTextureDescriptor.allocate(arena);

        WGPUTextureDescriptor.nextInChain(struct, MemorySegment.NULL);

        MemorySegment labelView = WGPUStringView.allocate(arena);
        if (label != null && !label.isEmpty()) {
            MemorySegment labelData = arena.allocateFrom(label);
            WGPUStringView.data(labelView, labelData);
            WGPUStringView.length(labelView, label.length());
        } else {
            WGPUStringView.data(labelView, MemorySegment.NULL);
            WGPUStringView.length(labelView, 0);
        }
        MemorySegment.copy(labelView, 0, struct, WGPUTextureDescriptor.label$offset(), WGPUStringView.sizeof());

        WGPUTextureDescriptor.usage(struct, usage);

        WGPUTextureDescriptor.dimension(struct, dimension.getValue());

        MemorySegment sizeStruct = WGPUExtent3D.allocate(arena);
        WGPUExtent3D.width(sizeStruct, width);
        WGPUExtent3D.height(sizeStruct, height);
        WGPUExtent3D.depthOrArrayLayers(sizeStruct, depthOrArrayLayers);
        MemorySegment.copy(sizeStruct, 0, struct, WGPUTextureDescriptor.size$offset(), WGPUExtent3D.sizeof());

        WGPUTextureDescriptor.format(struct, format.getValue());

        WGPUTextureDescriptor.mipLevelCount(struct, mipLevelCount);

        WGPUTextureDescriptor.sampleCount(struct, sampleCount);

        WGPUTextureDescriptor.viewFormatCount(struct, 0);

        WGPUTextureDescriptor.viewFormats(struct, MemorySegment.NULL);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label;
        private int width = 1;
        private int height = 1;
        private int depthOrArrayLayers = 1;
        private TextureFormat format = TextureFormat.BGRA8_UNORM;
        private long usage = 0x10;
        private TextureDimension dimension = TextureDimension.TWO_D;
        private int mipLevelCount = 1;
        private int sampleCount = 1;

        /**
         * Sets the debug label for the texture.
         *
         * @param label The debug label for the texture
         * @return this builder
         */
        public Builder label(String label) {
            this.label = label;
            return this;
        }

        /**
         * Sets the texture size for 2D textures.
         *
         * @param width  The texture width in pixels
         * @param height The texture height in pixels
         * @return this builder
         */
        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Sets the texture size for 3D textures or texture arrays.
         *
         * @param width              The texture width in pixels
         * @param height             The texture height in pixels
         * @param depthOrArrayLayers The depth (for 3D textures) or array layers (for texture arrays)
         * @return this builder
         */
        public Builder size(int width, int height, int depthOrArrayLayers) {
            this.width = width;
            this.height = height;
            this.depthOrArrayLayers = depthOrArrayLayers;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        /**
         * Sets the depth (for 3D textures) or array layers (for texture arrays).
         *
         * @param depthOrArrayLayers The depth or array layer count
         * @return this builder
         */
        public Builder depthOrArrayLayers(int depthOrArrayLayers) {
            this.depthOrArrayLayers = depthOrArrayLayers;
            return this;
        }

        /**
         * Sets the texture format.
         *
         * @param format The pixel format of the texture
         * @return this builder
         */
        public Builder format(TextureFormat format) {
            this.format = format;
            return this;
        }

        /**
         * Sets the texture usage flags.
         *
         * @param usage Bitwise OR of usage flags
         * @return this builder
         */
        public Builder usage(long usage) {
            this.usage = usage;
            return this;
        }

        /**
         * Sets the texture dimension.
         *
         * @param dimension The dimension type (1D, 2D, or 3D)
         * @return this builder
         */
        public Builder dimension(TextureDimension dimension) {
            this.dimension = dimension;
            return this;
        }

        /**
         * Sets the number of mip levels.
         * Higher values enable better texture filtering at different distances.
         *
         * @param mipLevelCount The number of mip levels (1 = no mipmaps)
         * @return this builder
         */
        public Builder mipLevelCount(int mipLevelCount) {
            this.mipLevelCount = mipLevelCount;
            return this;
        }

        /**
         * Sets the sample count for multisampling.
         *
         * @param sampleCount The number of samples per pixel (1 = no multisampling, 4 = 4x MSAA, etc.)
         * @return this builder
         */
        public Builder sampleCount(int sampleCount) {
            this.sampleCount = sampleCount;
            return this;
        }

        public TextureDescriptor build() {
            return new TextureDescriptor(this);
        }
    }
}