package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Represents WebGPU device limits.
 * These specify the maximum capabilities that a device can support.
 */
public class Limits implements Marshalable {
    private final int maxTextureDimension1D;
    private final int maxTextureDimension2D;
    private final int maxTextureDimension3D;
    private final int maxTextureArrayLayers;
    private final int maxBindGroups;
    private final int maxBindGroupsPlusVertexBuffers;
    private final int maxBindingsPerBindGroup;
    private final int maxDynamicUniformBuffersPerPipelineLayout;
    private final int maxDynamicStorageBuffersPerPipelineLayout;
    private final int maxSampledTexturesPerShaderStage;
    private final int maxSamplersPerShaderStage;
    private final int maxStorageBuffersPerShaderStage;
    private final int maxStorageTexturesPerShaderStage;
    private final int maxUniformBuffersPerShaderStage;
    private final long maxUniformBufferBindingSize;
    private final long maxStorageBufferBindingSize;
    private final int minUniformBufferOffsetAlignment;
    private final int minStorageBufferOffsetAlignment;
    private final int maxVertexBuffers;
    private final long maxBufferSize;
    private final int maxVertexAttributes;
    private final int maxVertexBufferArrayStride;
    private final int maxInterStageShaderVariables;
    private final int maxColorAttachments;
    private final int maxColorAttachmentBytesPerSample;
    private final int maxComputeWorkgroupStorageSize;
    private final int maxComputeInvocationsPerWorkgroup;
    private final int maxComputeWorkgroupSizeX;
    private final int maxComputeWorkgroupSizeY;
    private final int maxComputeWorkgroupSizeZ;
    private final int maxComputeWorkgroupsPerDimension;

    private Limits(Builder builder) {
        this.maxTextureDimension1D = builder.maxTextureDimension1D;
        this.maxTextureDimension2D = builder.maxTextureDimension2D;
        this.maxTextureDimension3D = builder.maxTextureDimension3D;
        this.maxTextureArrayLayers = builder.maxTextureArrayLayers;
        this.maxBindGroups = builder.maxBindGroups;
        this.maxBindGroupsPlusVertexBuffers = builder.maxBindGroupsPlusVertexBuffers;
        this.maxBindingsPerBindGroup = builder.maxBindingsPerBindGroup;
        this.maxDynamicUniformBuffersPerPipelineLayout = builder.maxDynamicUniformBuffersPerPipelineLayout;
        this.maxDynamicStorageBuffersPerPipelineLayout = builder.maxDynamicStorageBuffersPerPipelineLayout;
        this.maxSampledTexturesPerShaderStage = builder.maxSampledTexturesPerShaderStage;
        this.maxSamplersPerShaderStage = builder.maxSamplersPerShaderStage;
        this.maxStorageBuffersPerShaderStage = builder.maxStorageBuffersPerShaderStage;
        this.maxStorageTexturesPerShaderStage = builder.maxStorageTexturesPerShaderStage;
        this.maxUniformBuffersPerShaderStage = builder.maxUniformBuffersPerShaderStage;
        this.maxUniformBufferBindingSize = builder.maxUniformBufferBindingSize;
        this.maxStorageBufferBindingSize = builder.maxStorageBufferBindingSize;
        this.minUniformBufferOffsetAlignment = builder.minUniformBufferOffsetAlignment;
        this.minStorageBufferOffsetAlignment = builder.minStorageBufferOffsetAlignment;
        this.maxVertexBuffers = builder.maxVertexBuffers;
        this.maxBufferSize = builder.maxBufferSize;
        this.maxVertexAttributes = builder.maxVertexAttributes;
        this.maxVertexBufferArrayStride = builder.maxVertexBufferArrayStride;
        this.maxInterStageShaderVariables = builder.maxInterStageShaderVariables;
        this.maxColorAttachments = builder.maxColorAttachments;
        this.maxColorAttachmentBytesPerSample = builder.maxColorAttachmentBytesPerSample;
        this.maxComputeWorkgroupStorageSize = builder.maxComputeWorkgroupStorageSize;
        this.maxComputeInvocationsPerWorkgroup = builder.maxComputeInvocationsPerWorkgroup;
        this.maxComputeWorkgroupSizeX = builder.maxComputeWorkgroupSizeX;
        this.maxComputeWorkgroupSizeY = builder.maxComputeWorkgroupSizeY;
        this.maxComputeWorkgroupSizeZ = builder.maxComputeWorkgroupSizeZ;
        this.maxComputeWorkgroupsPerDimension = builder.maxComputeWorkgroupsPerDimension;
    }

    /**
     * Creates a Limits object from a native WGPULimits structure.
     */
    public static Limits fromNative(MemorySegment nativeLimits) {
        return builder()
                .maxTextureDimension1D(WGPULimits.maxTextureDimension1D(nativeLimits))
                .maxTextureDimension2D(WGPULimits.maxTextureDimension2D(nativeLimits))
                .maxTextureDimension3D(WGPULimits.maxTextureDimension3D(nativeLimits))
                .maxTextureArrayLayers(WGPULimits.maxTextureArrayLayers(nativeLimits))
                .maxBindGroups(WGPULimits.maxBindGroups(nativeLimits))
                .maxBindGroupsPlusVertexBuffers(WGPULimits.maxBindGroupsPlusVertexBuffers(nativeLimits))
                .maxBindingsPerBindGroup(WGPULimits.maxBindingsPerBindGroup(nativeLimits))
                .maxDynamicUniformBuffersPerPipelineLayout(WGPULimits.maxDynamicUniformBuffersPerPipelineLayout(nativeLimits))
                .maxDynamicStorageBuffersPerPipelineLayout(WGPULimits.maxDynamicStorageBuffersPerPipelineLayout(nativeLimits))
                .maxSampledTexturesPerShaderStage(WGPULimits.maxSampledTexturesPerShaderStage(nativeLimits))
                .maxSamplersPerShaderStage(WGPULimits.maxSamplersPerShaderStage(nativeLimits))
                .maxStorageBuffersPerShaderStage(WGPULimits.maxStorageBuffersPerShaderStage(nativeLimits))
                .maxStorageTexturesPerShaderStage(WGPULimits.maxStorageTexturesPerShaderStage(nativeLimits))
                .maxUniformBuffersPerShaderStage(WGPULimits.maxUniformBuffersPerShaderStage(nativeLimits))
                .maxUniformBufferBindingSize(WGPULimits.maxUniformBufferBindingSize(nativeLimits))
                .maxStorageBufferBindingSize(WGPULimits.maxStorageBufferBindingSize(nativeLimits))
                .minUniformBufferOffsetAlignment(WGPULimits.minUniformBufferOffsetAlignment(nativeLimits))
                .minStorageBufferOffsetAlignment(WGPULimits.minStorageBufferOffsetAlignment(nativeLimits))
                .maxVertexBuffers(WGPULimits.maxVertexBuffers(nativeLimits))
                .maxBufferSize(WGPULimits.maxBufferSize(nativeLimits))
                .maxVertexAttributes(WGPULimits.maxVertexAttributes(nativeLimits))
                .maxVertexBufferArrayStride(WGPULimits.maxVertexBufferArrayStride(nativeLimits))
                .maxInterStageShaderVariables(WGPULimits.maxInterStageShaderVariables(nativeLimits))
                .maxColorAttachments(WGPULimits.maxColorAttachments(nativeLimits))
                .maxColorAttachmentBytesPerSample(WGPULimits.maxColorAttachmentBytesPerSample(nativeLimits))
                .maxComputeWorkgroupStorageSize(WGPULimits.maxComputeWorkgroupStorageSize(nativeLimits))
                .maxComputeInvocationsPerWorkgroup(WGPULimits.maxComputeInvocationsPerWorkgroup(nativeLimits))
                .maxComputeWorkgroupSizeX(WGPULimits.maxComputeWorkgroupSizeX(nativeLimits))
                .maxComputeWorkgroupSizeY(WGPULimits.maxComputeWorkgroupSizeY(nativeLimits))
                .maxComputeWorkgroupSizeZ(WGPULimits.maxComputeWorkgroupSizeZ(nativeLimits))
                .maxComputeWorkgroupsPerDimension(WGPULimits.maxComputeWorkgroupsPerDimension(nativeLimits))
                .build();
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPULimits struct
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPULimits.allocate(arena);

        WGPULimits.nextInChain(struct, MemorySegment.NULL);
        WGPULimits.maxTextureDimension1D(struct, maxTextureDimension1D);
        WGPULimits.maxTextureDimension2D(struct, maxTextureDimension2D);
        WGPULimits.maxTextureDimension3D(struct, maxTextureDimension3D);
        WGPULimits.maxTextureArrayLayers(struct, maxTextureArrayLayers);
        WGPULimits.maxBindGroups(struct, maxBindGroups);
        WGPULimits.maxBindGroupsPlusVertexBuffers(struct, maxBindGroupsPlusVertexBuffers);
        WGPULimits.maxBindingsPerBindGroup(struct, maxBindingsPerBindGroup);
        WGPULimits.maxDynamicUniformBuffersPerPipelineLayout(struct, maxDynamicUniformBuffersPerPipelineLayout);
        WGPULimits.maxDynamicStorageBuffersPerPipelineLayout(struct, maxDynamicStorageBuffersPerPipelineLayout);
        WGPULimits.maxSampledTexturesPerShaderStage(struct, maxSampledTexturesPerShaderStage);
        WGPULimits.maxSamplersPerShaderStage(struct, maxSamplersPerShaderStage);
        WGPULimits.maxStorageBuffersPerShaderStage(struct, maxStorageBuffersPerShaderStage);
        WGPULimits.maxStorageTexturesPerShaderStage(struct, maxStorageTexturesPerShaderStage);
        WGPULimits.maxUniformBuffersPerShaderStage(struct, maxUniformBuffersPerShaderStage);
        WGPULimits.maxUniformBufferBindingSize(struct, maxUniformBufferBindingSize);
        WGPULimits.maxStorageBufferBindingSize(struct, maxStorageBufferBindingSize);
        WGPULimits.minUniformBufferOffsetAlignment(struct, minUniformBufferOffsetAlignment);
        WGPULimits.minStorageBufferOffsetAlignment(struct, minStorageBufferOffsetAlignment);
        WGPULimits.maxVertexBuffers(struct, maxVertexBuffers);
        WGPULimits.maxBufferSize(struct, maxBufferSize);
        WGPULimits.maxVertexAttributes(struct, maxVertexAttributes);
        WGPULimits.maxVertexBufferArrayStride(struct, maxVertexBufferArrayStride);
        WGPULimits.maxInterStageShaderVariables(struct, maxInterStageShaderVariables);
        WGPULimits.maxColorAttachments(struct, maxColorAttachments);
        WGPULimits.maxColorAttachmentBytesPerSample(struct, maxColorAttachmentBytesPerSample);
        WGPULimits.maxComputeWorkgroupStorageSize(struct, maxComputeWorkgroupStorageSize);
        WGPULimits.maxComputeInvocationsPerWorkgroup(struct, maxComputeInvocationsPerWorkgroup);
        WGPULimits.maxComputeWorkgroupSizeX(struct, maxComputeWorkgroupSizeX);
        WGPULimits.maxComputeWorkgroupSizeY(struct, maxComputeWorkgroupSizeY);
        WGPULimits.maxComputeWorkgroupSizeZ(struct, maxComputeWorkgroupSizeZ);
        WGPULimits.maxComputeWorkgroupsPerDimension(struct, maxComputeWorkgroupsPerDimension);

        return struct;
    }


    public int getMaxTextureDimension1D() {
        return maxTextureDimension1D;
    }

    public int getMaxTextureDimension2D() {
        return maxTextureDimension2D;
    }

    public int getMaxTextureDimension3D() {
        return maxTextureDimension3D;
    }

    public int getMaxTextureArrayLayers() {
        return maxTextureArrayLayers;
    }

    public int getMaxBindGroups() {
        return maxBindGroups;
    }

    public int getMaxBindGroupsPlusVertexBuffers() {
        return maxBindGroupsPlusVertexBuffers;
    }

    public int getMaxBindingsPerBindGroup() {
        return maxBindingsPerBindGroup;
    }

    public int getMaxDynamicUniformBuffersPerPipelineLayout() {
        return maxDynamicUniformBuffersPerPipelineLayout;
    }

    public int getMaxDynamicStorageBuffersPerPipelineLayout() {
        return maxDynamicStorageBuffersPerPipelineLayout;
    }

    public int getMaxSampledTexturesPerShaderStage() {
        return maxSampledTexturesPerShaderStage;
    }

    public int getMaxSamplersPerShaderStage() {
        return maxSamplersPerShaderStage;
    }

    public int getMaxStorageBuffersPerShaderStage() {
        return maxStorageBuffersPerShaderStage;
    }

    public int getMaxStorageTexturesPerShaderStage() {
        return maxStorageTexturesPerShaderStage;
    }

    public int getMaxUniformBuffersPerShaderStage() {
        return maxUniformBuffersPerShaderStage;
    }

    public long getMaxUniformBufferBindingSize() {
        return maxUniformBufferBindingSize;
    }

    public long getMaxStorageBufferBindingSize() {
        return maxStorageBufferBindingSize;
    }

    public int getMinUniformBufferOffsetAlignment() {
        return minUniformBufferOffsetAlignment;
    }

    public int getMinStorageBufferOffsetAlignment() {
        return minStorageBufferOffsetAlignment;
    }

    public int getMaxVertexBuffers() {
        return maxVertexBuffers;
    }

    public long getMaxBufferSize() {
        return maxBufferSize;
    }

    public int getMaxVertexAttributes() {
        return maxVertexAttributes;
    }

    public int getMaxVertexBufferArrayStride() {
        return maxVertexBufferArrayStride;
    }

    public int getMaxInterStageShaderVariables() {
        return maxInterStageShaderVariables;
    }

    public int getMaxColorAttachments() {
        return maxColorAttachments;
    }

    public int getMaxColorAttachmentBytesPerSample() {
        return maxColorAttachmentBytesPerSample;
    }

    public int getMaxComputeWorkgroupStorageSize() {
        return maxComputeWorkgroupStorageSize;
    }

    public int getMaxComputeInvocationsPerWorkgroup() {
        return maxComputeInvocationsPerWorkgroup;
    }

    public int getMaxComputeWorkgroupSizeX() {
        return maxComputeWorkgroupSizeX;
    }

    public int getMaxComputeWorkgroupSizeY() {
        return maxComputeWorkgroupSizeY;
    }

    public int getMaxComputeWorkgroupSizeZ() {
        return maxComputeWorkgroupSizeZ;
    }

    public int getMaxComputeWorkgroupsPerDimension() {
        return maxComputeWorkgroupsPerDimension;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int maxTextureDimension1D = 8192;
        private int maxTextureDimension2D = 8192;
        private int maxTextureDimension3D = 2048;
        private int maxTextureArrayLayers = 256;
        private int maxBindGroups = 4;
        private int maxBindGroupsPlusVertexBuffers = 24;
        private int maxBindingsPerBindGroup = 1000;
        private int maxDynamicUniformBuffersPerPipelineLayout = 8;
        private int maxDynamicStorageBuffersPerPipelineLayout = 4;
        private int maxSampledTexturesPerShaderStage = 16;
        private int maxSamplersPerShaderStage = 16;
        private int maxStorageBuffersPerShaderStage = 8;
        private int maxStorageTexturesPerShaderStage = 4;
        private int maxUniformBuffersPerShaderStage = 12;
        private long maxUniformBufferBindingSize = 65536;
        private long maxStorageBufferBindingSize = 134217728;
        private int minUniformBufferOffsetAlignment = 256;
        private int minStorageBufferOffsetAlignment = 256;
        private int maxVertexBuffers = 8;
        private long maxBufferSize = 268435456;
        private int maxVertexAttributes = 16;
        private int maxVertexBufferArrayStride = 2048;
        private int maxInterStageShaderVariables = 16;
        private int maxColorAttachments = 8;
        private int maxColorAttachmentBytesPerSample = 32;
        private int maxComputeWorkgroupStorageSize = 16384;
        private int maxComputeInvocationsPerWorkgroup = 256;
        private int maxComputeWorkgroupSizeX = 256;
        private int maxComputeWorkgroupSizeY = 256;
        private int maxComputeWorkgroupSizeZ = 64;
        private int maxComputeWorkgroupsPerDimension = 65535;


        public Builder maxTextureDimension1D(int value) {
            this.maxTextureDimension1D = value;
            return this;
        }

        public Builder maxTextureDimension2D(int value) {
            this.maxTextureDimension2D = value;
            return this;
        }

        public Builder maxTextureDimension3D(int value) {
            this.maxTextureDimension3D = value;
            return this;
        }

        public Builder maxTextureArrayLayers(int value) {
            this.maxTextureArrayLayers = value;
            return this;
        }

        public Builder maxBindGroups(int value) {
            this.maxBindGroups = value;
            return this;
        }

        public Builder maxBindGroupsPlusVertexBuffers(int value) {
            this.maxBindGroupsPlusVertexBuffers = value;
            return this;
        }

        public Builder maxBindingsPerBindGroup(int value) {
            this.maxBindingsPerBindGroup = value;
            return this;
        }

        public Builder maxDynamicUniformBuffersPerPipelineLayout(int value) {
            this.maxDynamicUniformBuffersPerPipelineLayout = value;
            return this;
        }

        public Builder maxDynamicStorageBuffersPerPipelineLayout(int value) {
            this.maxDynamicStorageBuffersPerPipelineLayout = value;
            return this;
        }

        public Builder maxSampledTexturesPerShaderStage(int value) {
            this.maxSampledTexturesPerShaderStage = value;
            return this;
        }

        public Builder maxSamplersPerShaderStage(int value) {
            this.maxSamplersPerShaderStage = value;
            return this;
        }

        public Builder maxStorageBuffersPerShaderStage(int value) {
            this.maxStorageBuffersPerShaderStage = value;
            return this;
        }

        public Builder maxStorageTexturesPerShaderStage(int value) {
            this.maxStorageTexturesPerShaderStage = value;
            return this;
        }

        public Builder maxUniformBuffersPerShaderStage(int value) {
            this.maxUniformBuffersPerShaderStage = value;
            return this;
        }

        public Builder maxUniformBufferBindingSize(long value) {
            this.maxUniformBufferBindingSize = value;
            return this;
        }

        public Builder maxStorageBufferBindingSize(long value) {
            this.maxStorageBufferBindingSize = value;
            return this;
        }

        public Builder minUniformBufferOffsetAlignment(int value) {
            this.minUniformBufferOffsetAlignment = value;
            return this;
        }

        public Builder minStorageBufferOffsetAlignment(int value) {
            this.minStorageBufferOffsetAlignment = value;
            return this;
        }

        public Builder maxVertexBuffers(int value) {
            this.maxVertexBuffers = value;
            return this;
        }

        public Builder maxBufferSize(long value) {
            this.maxBufferSize = value;
            return this;
        }

        public Builder maxVertexAttributes(int value) {
            this.maxVertexAttributes = value;
            return this;
        }

        public Builder maxVertexBufferArrayStride(int value) {
            this.maxVertexBufferArrayStride = value;
            return this;
        }

        public Builder maxInterStageShaderVariables(int value) {
            this.maxInterStageShaderVariables = value;
            return this;
        }

        public Builder maxColorAttachments(int value) {
            this.maxColorAttachments = value;
            return this;
        }

        public Builder maxColorAttachmentBytesPerSample(int value) {
            this.maxColorAttachmentBytesPerSample = value;
            return this;
        }

        public Builder maxComputeWorkgroupStorageSize(int value) {
            this.maxComputeWorkgroupStorageSize = value;
            return this;
        }

        public Builder maxComputeInvocationsPerWorkgroup(int value) {
            this.maxComputeInvocationsPerWorkgroup = value;
            return this;
        }

        public Builder maxComputeWorkgroupSizeX(int value) {
            this.maxComputeWorkgroupSizeX = value;
            return this;
        }

        public Builder maxComputeWorkgroupSizeY(int value) {
            this.maxComputeWorkgroupSizeY = value;
            return this;
        }

        public Builder maxComputeWorkgroupSizeZ(int value) {
            this.maxComputeWorkgroupSizeZ = value;
            return this;
        }

        public Builder maxComputeWorkgroupsPerDimension(int value) {
            this.maxComputeWorkgroupsPerDimension = value;
            return this;
        }

        public Limits build() {
            return new Limits(this);
        }
    }
}