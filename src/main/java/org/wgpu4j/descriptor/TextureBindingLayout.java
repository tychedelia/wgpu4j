package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.constant.*;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Layout configuration for texture bindings in a bind group.
 */
public class TextureBindingLayout implements Marshalable {
    private final TextureSampleType sampleType;
    private final TextureViewDimension viewDimension;
    private final boolean multisampled;

    private TextureBindingLayout(Builder builder) {
        this.sampleType = builder.sampleType;
        this.viewDimension = builder.viewDimension;
        this.multisampled = builder.multisampled;
    }

    public TextureSampleType getSampleType() {
        return sampleType;
    }

    public TextureViewDimension getViewDimension() {
        return viewDimension;
    }

    public boolean isMultisampled() {
        return multisampled;
    }

    /**
     * Converts this layout to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUTextureBindingLayout struct
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPUTextureBindingLayout.allocate(arena);

        WGPUTextureBindingLayout.nextInChain(struct, MemorySegment.NULL);

        WGPUTextureBindingLayout.sampleType(struct, sampleType.getValue());

        WGPUTextureBindingLayout.viewDimension(struct, viewDimension.getValue());

        WGPUTextureBindingLayout.multisampled(struct, multisampled ? 1 : 0);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TextureSampleType sampleType = TextureSampleType.FLOAT;
        private TextureViewDimension viewDimension = TextureViewDimension.TWO_D;
        private boolean multisampled = false;

        public Builder sampleType(TextureSampleType sampleType) {
            this.sampleType = sampleType;
            return this;
        }

        public Builder viewDimension(TextureViewDimension viewDimension) {
            this.viewDimension = viewDimension;
            return this;
        }

        public Builder multisampled(boolean multisampled) {
            this.multisampled = multisampled;
            return this;
        }

        public TextureBindingLayout build() {
            return new TextureBindingLayout(this);
        }
    }
}