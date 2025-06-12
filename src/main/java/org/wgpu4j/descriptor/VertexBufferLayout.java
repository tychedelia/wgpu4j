package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.WGPUVertexAttribute;
import org.wgpu4j.bindings.WGPUVertexBufferLayout;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;

/**
 * Descriptor for vertex buffer layout.
 */
public class VertexBufferLayout implements Marshalable {
    private final int stepMode;
    private final long arrayStride;
    private final List<VertexAttribute> attributes;

    private VertexBufferLayout(Builder builder) {
        this.stepMode = builder.stepMode;
        this.arrayStride = builder.arrayStride;
        this.attributes = new ArrayList<>(builder.attributes);
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
    public MemorySegment marshal(Arena arena) {
        MemorySegment layout = WGPUVertexBufferLayout.allocate(arena);

        WGPUVertexBufferLayout.stepMode(layout, stepMode);
        WGPUVertexBufferLayout.arrayStride(layout, arrayStride);
        WGPUVertexBufferLayout.attributeCount(layout, attributes.size());

        if (!attributes.isEmpty()) {
            MemorySegment attributeArray = WGPUVertexAttribute.allocateArray(attributes.size(), arena);

            for (int i = 0; i < attributes.size(); i++) {
                MemorySegment attributeStruct = attributes.get(i).marshal(arena);
                MemorySegment target = WGPUVertexAttribute.asSlice(attributeArray, i);
                MemorySegment.copy(attributeStruct, 0, target, 0, WGPUVertexAttribute.sizeof());
            }

            WGPUVertexBufferLayout.attributes(layout, attributeArray);
        } else {
            WGPUVertexBufferLayout.attributes(layout, MemorySegment.NULL);
        }

        return layout;
    }

    public int getStepMode() {
        return stepMode;
    }

    public long getArrayStride() {
        return arrayStride;
    }

    public List<VertexAttribute> getAttributes() {
        return new ArrayList<>(attributes);
    }

    public static class Builder {
        private int stepMode;
        private long arrayStride;
        private final List<VertexAttribute> attributes = new ArrayList<>();

        public Builder stepMode(int stepMode) {
            this.stepMode = stepMode;
            return this;
        }

        public Builder arrayStride(long arrayStride) {
            this.arrayStride = arrayStride;
            return this;
        }

        public Builder attribute(VertexAttribute attribute) {
            this.attributes.add(attribute);
            return this;
        }

        public VertexBufferLayout build() {
            return new VertexBufferLayout(this);
        }
    }
}