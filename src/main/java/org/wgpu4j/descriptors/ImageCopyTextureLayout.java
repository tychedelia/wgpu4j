package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Descriptor for the layout of texture data in memory.
 */
public class ImageCopyTextureLayout {
    private final long offset;
    private final int bytesPerRow;
    private final int rowsPerImage;

    private ImageCopyTextureLayout(Builder builder) {
        this.offset = builder.offset;
        this.bytesPerRow = builder.bytesPerRow;
        this.rowsPerImage = builder.rowsPerImage;
    }

    /**
     * Creates a C struct representing this texture layout descriptor.
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUTexelCopyBufferLayout.allocate(arena);

        WGPUTexelCopyBufferLayout.offset(struct, offset);
        WGPUTexelCopyBufferLayout.bytesPerRow(struct, bytesPerRow);
        WGPUTexelCopyBufferLayout.rowsPerImage(struct, rowsPerImage);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long offset = 0;
        private int bytesPerRow = 0;
        private int rowsPerImage = 0;

        /**
         * Sets the offset in bytes to the start of the texture data.
         */
        public Builder offset(long offset) {
            this.offset = offset;
            return this;
        }

        /**
         * Sets the number of bytes per row of the texture data.
         * Must be a multiple of the texture format's block size.
         */
        public Builder bytesPerRow(int bytesPerRow) {
            this.bytesPerRow = bytesPerRow;
            return this;
        }

        /**
         * Sets the number of rows per image for 3D textures.
         * For 2D textures, this should be 0.
         */
        public Builder rowsPerImage(int rowsPerImage) {
            this.rowsPerImage = rowsPerImage;
            return this;
        }

        public ImageCopyTextureLayout build() {
            return new ImageCopyTextureLayout(this);
        }
    }
}