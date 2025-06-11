package org.wgpu4j.descriptors;

import org.wgpu4j.enums.SamplerBindingType;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Layout configuration for sampler bindings in a bind group.
 */
public class SamplerBindingLayout {
    private final SamplerBindingType type;

    private SamplerBindingLayout(Builder builder) {
        this.type = builder.type;
    }

    public SamplerBindingType getType() {
        return type;
    }

    /**
     * Converts this layout to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUSamplerBindingLayout struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUSamplerBindingLayout.allocate(arena);

        WGPUSamplerBindingLayout.nextInChain(struct, MemorySegment.NULL);

        WGPUSamplerBindingLayout.type(struct, type.getValue());

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SamplerBindingType type = SamplerBindingType.FILTERING;

        public Builder type(SamplerBindingType type) {
            this.type = type;
            return this;
        }

        public SamplerBindingLayout build() {
            return new SamplerBindingLayout(this);
        }
    }
}