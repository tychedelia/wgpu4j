package org.wgpu4j.descriptors;

import org.wgpu4j.enums.*;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Configuration for creating a texture sampler.
 * Defines how textures are filtered and addressed when sampled in shaders.
 */
public class SamplerDescriptor {
    private final String label;
    private final AddressMode addressModeU;
    private final AddressMode addressModeV;
    private final AddressMode addressModeW;
    private final FilterMode magFilter;
    private final FilterMode minFilter;
    private final MipmapFilterMode mipmapFilter;
    private final float lodMinClamp;
    private final float lodMaxClamp;
    private final CompareFunction compare;
    private final int maxAnisotropy;

    private SamplerDescriptor(Builder builder) {
        this.label = builder.label;
        this.addressModeU = builder.addressModeU;
        this.addressModeV = builder.addressModeV;
        this.addressModeW = builder.addressModeW;
        this.magFilter = builder.magFilter;
        this.minFilter = builder.minFilter;
        this.mipmapFilter = builder.mipmapFilter;
        this.lodMinClamp = builder.lodMinClamp;
        this.lodMaxClamp = builder.lodMaxClamp;
        this.compare = builder.compare;
        this.maxAnisotropy = builder.maxAnisotropy;
    }

    public String getLabel() {
        return label;
    }

    public AddressMode getAddressModeU() {
        return addressModeU;
    }

    public AddressMode getAddressModeV() {
        return addressModeV;
    }

    public AddressMode getAddressModeW() {
        return addressModeW;
    }

    public FilterMode getMagFilter() {
        return magFilter;
    }

    public FilterMode getMinFilter() {
        return minFilter;
    }

    public MipmapFilterMode getMipmapFilter() {
        return mipmapFilter;
    }

    public float getLodMinClamp() {
        return lodMinClamp;
    }

    public float getLodMaxClamp() {
        return lodMaxClamp;
    }

    public CompareFunction getCompare() {
        return compare;
    }

    public int getMaxAnisotropy() {
        return maxAnisotropy;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUSamplerDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUSamplerDescriptor.allocate(arena);

        WGPUSamplerDescriptor.nextInChain(struct, MemorySegment.NULL);

        MemorySegment labelView = WGPUStringView.allocate(arena);
        if (label != null && !label.isEmpty()) {
            MemorySegment labelData = arena.allocateFrom(label);
            WGPUStringView.data(labelView, labelData);
            WGPUStringView.length(labelView, label.length());
        } else {
            WGPUStringView.data(labelView, MemorySegment.NULL);
            WGPUStringView.length(labelView, 0);
        }
        MemorySegment.copy(labelView, 0, struct, WGPUSamplerDescriptor.label$offset(), WGPUStringView.sizeof());

        WGPUSamplerDescriptor.addressModeU(struct, addressModeU.getValue());
        WGPUSamplerDescriptor.addressModeV(struct, addressModeV.getValue());
        WGPUSamplerDescriptor.addressModeW(struct, addressModeW.getValue());

        WGPUSamplerDescriptor.magFilter(struct, magFilter.getValue());
        WGPUSamplerDescriptor.minFilter(struct, minFilter.getValue());
        WGPUSamplerDescriptor.mipmapFilter(struct, mipmapFilter.getValue());

        WGPUSamplerDescriptor.lodMinClamp(struct, lodMinClamp);
        WGPUSamplerDescriptor.lodMaxClamp(struct, lodMaxClamp);

        if (compare != null) {
            WGPUSamplerDescriptor.compare(struct, compare.getValue());
        } else {
            WGPUSamplerDescriptor.compare(struct, webgpu_h.WGPUCompareFunction_Undefined());
        }

        WGPUSamplerDescriptor.maxAnisotropy(struct, (short) maxAnisotropy);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label;
        private AddressMode addressModeU = AddressMode.CLAMP_TO_EDGE;
        private AddressMode addressModeV = AddressMode.CLAMP_TO_EDGE;
        private AddressMode addressModeW = AddressMode.CLAMP_TO_EDGE;
        private FilterMode magFilter = FilterMode.NEAREST;
        private FilterMode minFilter = FilterMode.NEAREST;
        private MipmapFilterMode mipmapFilter = MipmapFilterMode.NEAREST;
        private float lodMinClamp = 0.0f;
        private float lodMaxClamp = 32.0f;
        private CompareFunction compare;
        private int maxAnisotropy = 1;

        /**
         * Sets the debug label for the sampler.
         *
         * @param label The debug label for the sampler
         * @return this builder
         */
        public Builder label(String label) {
            this.label = label;
            return this;
        }

        /**
         * Sets the address mode for all texture coordinates.
         *
         * @param addressMode The address mode for U, V, and W coordinates
         * @return this builder
         */
        public Builder addressMode(AddressMode addressMode) {
            this.addressModeU = addressMode;
            this.addressModeV = addressMode;
            this.addressModeW = addressMode;
            return this;
        }

        /**
         * Sets the address mode for U (horizontal) texture coordinates.
         *
         * @param addressModeU The address mode for U coordinates
         * @return this builder
         */
        public Builder addressModeU(AddressMode addressModeU) {
            this.addressModeU = addressModeU;
            return this;
        }

        /**
         * Sets the address mode for V (vertical) texture coordinates.
         *
         * @param addressModeV The address mode for V coordinates
         * @return this builder
         */
        public Builder addressModeV(AddressMode addressModeV) {
            this.addressModeV = addressModeV;
            return this;
        }

        /**
         * Sets the address mode for W (depth) texture coordinates.
         *
         * @param addressModeW The address mode for W coordinates
         * @return this builder
         */
        public Builder addressModeW(AddressMode addressModeW) {
            this.addressModeW = addressModeW;
            return this;
        }

        /**
         * Sets the magnification filter mode.
         * Used when the texture is larger than the screen space it covers.
         *
         * @param magFilter The magnification filter mode
         * @return this builder
         */
        public Builder magFilter(FilterMode magFilter) {
            this.magFilter = magFilter;
            return this;
        }

        /**
         * Sets the minification filter mode.
         * Used when the texture is smaller than the screen space it covers.
         *
         * @param minFilter The minification filter mode
         * @return this builder
         */
        public Builder minFilter(FilterMode minFilter) {
            this.minFilter = minFilter;
            return this;
        }

        /**
         * Sets the mipmap filter mode.
         * Controls how mipmap levels are blended together.
         *
         * @param mipmapFilter The mipmap filter mode
         * @return this builder
         */
        public Builder mipmapFilter(MipmapFilterMode mipmapFilter) {
            this.mipmapFilter = mipmapFilter;
            return this;
        }

        /**
         * Sets the filter modes for both magnification and minification.
         *
         * @param filterMode The filter mode for both mag and min filtering
         * @return this builder
         */
        public Builder filterMode(FilterMode filterMode) {
            this.magFilter = filterMode;
            this.minFilter = filterMode;
            return this;
        }

        /**
         * Sets the minimum level of detail clamp.
         *
         * @param lodMinClamp The minimum LOD clamp value
         * @return this builder
         */
        public Builder lodMinClamp(float lodMinClamp) {
            this.lodMinClamp = lodMinClamp;
            return this;
        }

        /**
         * Sets the maximum level of detail clamp.
         *
         * @param lodMaxClamp The maximum LOD clamp value
         * @return this builder
         */
        public Builder lodMaxClamp(float lodMaxClamp) {
            this.lodMaxClamp = lodMaxClamp;
            return this;
        }

        /**
         * Sets the comparison function for shadow/depth samplers.
         *
         * @param compare The comparison function, or null for regular sampling
         * @return this builder
         */
        public Builder compare(CompareFunction compare) {
            this.compare = compare;
            return this;
        }

        /**
         * Sets the maximum anisotropy level.
         * Higher values provide better texture quality at oblique viewing angles.
         *
         * @param maxAnisotropy The maximum anisotropy level (1-16)
         * @return this builder
         */
        public Builder maxAnisotropy(int maxAnisotropy) {
            this.maxAnisotropy = Math.max(1, Math.min(16, maxAnisotropy));
            return this;
        }

        public SamplerDescriptor build() {
            return new SamplerDescriptor(this);
        }
    }
}