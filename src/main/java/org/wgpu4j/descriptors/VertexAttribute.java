package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.WGPUVertexAttribute;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Descriptor for a vertex attribute.
 */
public class VertexAttribute {
    private final int format;
    private final long offset;
    private final int shaderLocation;

    private VertexAttribute(Builder builder) {
        this.format = builder.format;
        this.offset = builder.offset;
        this.shaderLocation = builder.shaderLocation;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Converts this descriptor to a C struct.
     *
     * @param arena Arena for memory allocation
     * @return Memory segment containing the C struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment attribute = WGPUVertexAttribute.allocate(arena);
        WGPUVertexAttribute.format(attribute, format);
        WGPUVertexAttribute.offset(attribute, offset);
        WGPUVertexAttribute.shaderLocation(attribute, shaderLocation);
        return attribute;
    }

    public int getFormat() {
        return format;
    }

    public long getOffset() {
        return offset;
    }

    public int getShaderLocation() {
        return shaderLocation;
    }

    public static class Builder {
        private int format;
        private long offset;
        private int shaderLocation;

        public Builder format(int format) {
            this.format = format;
            return this;
        }

        public Builder offset(long offset) {
            this.offset = offset;
            return this;
        }

        public Builder shaderLocation(int shaderLocation) {
            this.shaderLocation = shaderLocation;
            return this;
        }

        public VertexAttribute build() {
            return new VertexAttribute(this);
        }
    }
}