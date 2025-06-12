package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.WGPUBindGroupEntry;
import org.wgpu4j.resource.Buffer;
import org.wgpu4j.resource.TextureView;
import org.wgpu4j.resource.Sampler;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class BindGroupEntry implements Marshalable {
    private final int binding;
    private final Buffer buffer;
    private final long offset;
    private final long size;
    private final TextureView textureView;
    private final Sampler sampler;

    private BindGroupEntry(Builder builder) {
        this.binding = builder.binding;
        this.buffer = builder.buffer;
        this.offset = builder.offset;
        this.size = builder.size;
        this.textureView = builder.textureView;
        this.sampler = builder.sampler;
    }

    public static Builder builder() {
        return new Builder();
    }

    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPUBindGroupEntry.allocate(arena);

        WGPUBindGroupEntry.nextInChain(struct, MemorySegment.NULL);
        WGPUBindGroupEntry.binding(struct, binding);

        if (buffer != null) {
            WGPUBindGroupEntry.buffer(struct, buffer.getHandle());
            WGPUBindGroupEntry.offset(struct, offset);
            WGPUBindGroupEntry.size(struct, size);
        } else {
            WGPUBindGroupEntry.buffer(struct, MemorySegment.NULL);
            WGPUBindGroupEntry.offset(struct, 0);
            WGPUBindGroupEntry.size(struct, 0);
        }

        if (sampler != null) {
            WGPUBindGroupEntry.sampler(struct, sampler.getHandle());
        } else {
            WGPUBindGroupEntry.sampler(struct, MemorySegment.NULL);
        }

        if (textureView != null) {
            WGPUBindGroupEntry.textureView(struct, textureView.getHandle());
        } else {
            WGPUBindGroupEntry.textureView(struct, MemorySegment.NULL);
        }

        return struct;
    }

    public static class Builder {
        private int binding;
        private Buffer buffer;
        private long offset = 0;
        private long size = -1;
        private TextureView textureView;
        private Sampler sampler;

        public Builder binding(int binding) {
            this.binding = binding;
            return this;
        }

        public Builder buffer(Buffer buffer) {
            this.buffer = buffer;
            return this;
        }

        public Builder buffer(Buffer buffer, long offset, long size) {
            this.buffer = buffer;
            this.offset = offset;
            this.size = size;
            return this;
        }


        public Builder textureView(TextureView textureView) {
            this.textureView = textureView;
            return this;
        }

        public Builder sampler(Sampler sampler) {
            this.sampler = sampler;
            return this;
        }

        public BindGroupEntry build() {
            return new BindGroupEntry(this);
        }
    }
}