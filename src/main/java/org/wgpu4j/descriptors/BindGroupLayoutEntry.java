package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;
import org.wgpu4j.enums.BufferBindingType;
import org.wgpu4j.enums.ShaderStageFlags;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.EnumSet;

/**
 * Descriptor for a bind group layout entry.
 * Supports buffer, sampler, and texture bindings.
 */
public class BindGroupLayoutEntry {
    public enum BindingType {
        BUFFER, SAMPLER, TEXTURE
    }

    private final int binding;
    private final EnumSet<ShaderStageFlags> visibility;
    private final BindingType bindingType;

    private final BufferBindingType bufferType;
    private final boolean hasDynamicOffset;
    private final long minBindingSize;

    private final SamplerBindingLayout samplerBindingLayout;

    private final TextureBindingLayout textureBindingLayout;

    private BindGroupLayoutEntry(Builder builder) {
        this.binding = builder.binding;
        this.visibility = builder.visibility;
        this.bindingType = builder.bindingType;
        this.bufferType = builder.bufferType;
        this.hasDynamicOffset = builder.hasDynamicOffset;
        this.minBindingSize = builder.minBindingSize;
        this.samplerBindingLayout = builder.samplerBindingLayout;
        this.textureBindingLayout = builder.textureBindingLayout;
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
        MemorySegment entry = WGPUBindGroupLayoutEntry.allocate(arena);

        WGPUBindGroupLayoutEntry.nextInChain(entry, MemorySegment.NULL);
        WGPUBindGroupLayoutEntry.binding(entry, binding);
        WGPUBindGroupLayoutEntry.visibility(entry, ShaderStageFlags.toBitFlags(visibility));

        switch (bindingType) {
            case BUFFER -> {
                MemorySegment bufferLayout = WGPUBindGroupLayoutEntry.buffer(entry);
                WGPUBufferBindingLayout.nextInChain(bufferLayout, MemorySegment.NULL);
                WGPUBufferBindingLayout.type(bufferLayout, bufferType.getValue());
                WGPUBufferBindingLayout.hasDynamicOffset(bufferLayout, hasDynamicOffset ? 1 : 0);
                WGPUBufferBindingLayout.minBindingSize(bufferLayout, minBindingSize);

                setUnusedSamplerLayout(entry);
                setUnusedTextureLayout(entry);
                setUnusedStorageTextureLayout(entry);
            }
            case SAMPLER -> {
                MemorySegment samplerLayout = WGPUBindGroupLayoutEntry.sampler(entry);
                MemorySegment samplerStruct = samplerBindingLayout.toCStruct(arena);
                MemorySegment.copy(samplerStruct, 0, samplerLayout, 0, WGPUSamplerBindingLayout.sizeof());

                setUnusedBufferLayout(entry);
                setUnusedTextureLayout(entry);
                setUnusedStorageTextureLayout(entry);
            }
            case TEXTURE -> {
                MemorySegment textureLayout = WGPUBindGroupLayoutEntry.texture(entry);
                MemorySegment textureStruct = textureBindingLayout.toCStruct(arena);
                MemorySegment.copy(textureStruct, 0, textureLayout, 0, WGPUTextureBindingLayout.sizeof());

                setUnusedBufferLayout(entry);
                setUnusedSamplerLayout(entry);
                setUnusedStorageTextureLayout(entry);
            }
        }

        return entry;
    }

    private void setUnusedBufferLayout(MemorySegment entry) {
        MemorySegment bufferLayout = WGPUBindGroupLayoutEntry.buffer(entry);
        WGPUBufferBindingLayout.nextInChain(bufferLayout, MemorySegment.NULL);
        WGPUBufferBindingLayout.type(bufferLayout, 0);
    }

    private void setUnusedSamplerLayout(MemorySegment entry) {
        MemorySegment samplerLayout = WGPUBindGroupLayoutEntry.sampler(entry);
        WGPUSamplerBindingLayout.nextInChain(samplerLayout, MemorySegment.NULL);
        WGPUSamplerBindingLayout.type(samplerLayout, 0);
    }

    private void setUnusedTextureLayout(MemorySegment entry) {
        MemorySegment textureLayout = WGPUBindGroupLayoutEntry.texture(entry);
        WGPUTextureBindingLayout.nextInChain(textureLayout, MemorySegment.NULL);
        WGPUTextureBindingLayout.sampleType(textureLayout, 0);
    }

    private void setUnusedStorageTextureLayout(MemorySegment entry) {
        MemorySegment storageTextureLayout = WGPUBindGroupLayoutEntry.storageTexture(entry);
        WGPUStorageTextureBindingLayout.nextInChain(storageTextureLayout, MemorySegment.NULL);
    }

    public int getBinding() {
        return binding;
    }

    public EnumSet<ShaderStageFlags> getVisibility() {
        return EnumSet.copyOf(visibility);
    }

    public BindingType getBindingType() {
        return bindingType;
    }

    public BufferBindingType getBufferType() {
        return bufferType;
    }

    public boolean hasDynamicOffset() {
        return hasDynamicOffset;
    }

    public long getMinBindingSize() {
        return minBindingSize;
    }

    public SamplerBindingLayout getSamplerBindingLayout() {
        return samplerBindingLayout;
    }

    public TextureBindingLayout getTextureBindingLayout() {
        return textureBindingLayout;
    }

    public static class Builder {
        private int binding;
        private EnumSet<ShaderStageFlags> visibility = ShaderStageFlags.allGraphics();
        private BindingType bindingType = BindingType.BUFFER;

        private BufferBindingType bufferType = BufferBindingType.UNIFORM;
        private boolean hasDynamicOffset = false;
        private long minBindingSize = 0;

        private SamplerBindingLayout samplerBindingLayout;

        private TextureBindingLayout textureBindingLayout;

        public Builder binding(int binding) {
            this.binding = binding;
            return this;
        }

        public Builder visibility(EnumSet<ShaderStageFlags> visibility) {
            this.visibility = EnumSet.copyOf(visibility);
            return this;
        }

        public Builder visibility(ShaderStageFlags... stages) {
            this.visibility = stages.length == 0 ? EnumSet.noneOf(ShaderStageFlags.class)
                    : EnumSet.of(stages[0], stages);
            return this;
        }

        public Builder bufferType(BufferBindingType bufferType) {
            this.bufferType = bufferType;
            this.bindingType = BindingType.BUFFER;
            return this;
        }

        public Builder hasDynamicOffset(boolean hasDynamicOffset) {
            this.hasDynamicOffset = hasDynamicOffset;
            return this;
        }

        public Builder minBindingSize(long minBindingSize) {
            this.minBindingSize = minBindingSize;
            return this;
        }

        public Builder samplerBindingLayout(SamplerBindingLayout samplerBindingLayout) {
            this.samplerBindingLayout = samplerBindingLayout;
            this.bindingType = BindingType.SAMPLER;
            return this;
        }

        public Builder textureBindingLayout(TextureBindingLayout textureBindingLayout) {
            this.textureBindingLayout = textureBindingLayout;
            this.bindingType = BindingType.TEXTURE;
            return this;
        }

        public BindGroupLayoutEntry build() {
            switch (bindingType) {
                case SAMPLER -> {
                    if (samplerBindingLayout == null) {
                        throw new IllegalArgumentException("Sampler binding layout is required for sampler bindings");
                    }
                }
                case TEXTURE -> {
                    if (textureBindingLayout == null) {
                        throw new IllegalArgumentException("Texture binding layout is required for texture bindings");
                    }
                }
            }

            return new BindGroupLayoutEntry(this);
        }
    }
}