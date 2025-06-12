package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.*;
import org.wgpu4j.resource.Buffer;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Descriptor for copying data from/to a buffer.
 */
public class ImageCopyBuffer implements Marshalable {
    private final Buffer buffer;
    private final ImageCopyTextureLayout layout;

    private ImageCopyBuffer(Builder builder) {
        this.buffer = builder.buffer;
        this.layout = builder.layout;
    }

    /**
     * Creates a C struct representing this buffer copy descriptor.
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPUTexelCopyBufferInfo.allocate(arena);

        WGPUTexelCopyBufferInfo.buffer(struct, buffer.getHandle());

        MemorySegment layoutStruct = layout.marshal(arena);
        WGPUTexelCopyBufferInfo.layout(struct, layoutStruct);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Buffer buffer;
        private ImageCopyTextureLayout layout = ImageCopyTextureLayout.builder().build();

        /**
         * Sets the buffer to copy from/to.
         */
        public Builder buffer(Buffer buffer) {
            this.buffer = buffer;
            return this;
        }

        /**
         * Sets the layout of the data in the buffer.
         */
        public Builder layout(ImageCopyTextureLayout layout) {
            this.layout = layout;
            return this;
        }

        /**
         * Sets the offset in bytes to the start of the texture data.
         */
        public Builder offset(long offset) {
            this.layout = ImageCopyTextureLayout.builder()
                    .offset(offset)
                    .bytesPerRow(this.layout != null ? 0 : 0).rowsPerImage(this.layout != null ? 0 : 0)
                    .build();
            return this;
        }

        /**
         * Sets the number of bytes per row of the texture data.
         */
        public Builder bytesPerRow(int bytesPerRow) {
            long currentOffset = 0;
            int currentRowsPerImage = 0;
            this.layout = ImageCopyTextureLayout.builder()
                    .offset(currentOffset)
                    .bytesPerRow(bytesPerRow)
                    .rowsPerImage(currentRowsPerImage)
                    .build();
            return this;
        }

        public ImageCopyBuffer build() {
            if (buffer == null) {
                throw new IllegalStateException("Buffer must be specified");
            }
            return new ImageCopyBuffer(this);
        }
    }
}