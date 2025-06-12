package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.WGPUDepthStencilState;
import org.wgpu4j.bindings.WGPUStencilFaceState;
import org.wgpu4j.constant.CompareFunction;
import org.wgpu4j.constant.TextureFormat;
import org.wgpu4j.constant.StencilOperation;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Depth and stencil state configuration for a render pipeline.
 * Controls depth testing, depth writing, and stencil operations.
 */
public class DepthStencilState implements Marshalable {
    private final TextureFormat format;
    private final boolean depthWriteEnabled;
    private final CompareFunction depthCompare;

    private DepthStencilState(Builder builder) {
        this.format = builder.format;
        this.depthWriteEnabled = builder.depthWriteEnabled;
        this.depthCompare = builder.depthCompare;
    }

    public static Builder builder() {
        return new Builder();
    }

    public TextureFormat getFormat() {
        return format;
    }

    public boolean isDepthWriteEnabled() {
        return depthWriteEnabled;
    }

    public CompareFunction getDepthCompare() {
        return depthCompare;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUDepthStencilState struct
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPUDepthStencilState.allocate(arena);

        WGPUDepthStencilState.nextInChain(struct, MemorySegment.NULL);
        WGPUDepthStencilState.format(struct, format.getValue());
        WGPUDepthStencilState.depthWriteEnabled(struct, depthWriteEnabled ? 1 : 0);
        WGPUDepthStencilState.depthCompare(struct, depthCompare.getValue());

        WGPUDepthStencilState.stencilReadMask(struct, 0xFFFFFFFF);
        WGPUDepthStencilState.stencilWriteMask(struct, 0xFFFFFFFF);

        WGPUDepthStencilState.depthBias(struct, 0);
        WGPUDepthStencilState.depthBiasSlopeScale(struct, 0.0f);
        WGPUDepthStencilState.depthBiasClamp(struct, 0.0f);

        MemorySegment stencilFront = WGPUDepthStencilState.stencilFront(struct);
        WGPUStencilFaceState.compare(stencilFront, CompareFunction.ALWAYS.getValue());
        WGPUStencilFaceState.failOp(stencilFront, StencilOperation.KEEP.getValue());
        WGPUStencilFaceState.depthFailOp(stencilFront, StencilOperation.KEEP.getValue());
        WGPUStencilFaceState.passOp(stencilFront, StencilOperation.KEEP.getValue());

        MemorySegment stencilBack = WGPUDepthStencilState.stencilBack(struct);
        WGPUStencilFaceState.compare(stencilBack, CompareFunction.ALWAYS.getValue());
        WGPUStencilFaceState.failOp(stencilBack, StencilOperation.KEEP.getValue());
        WGPUStencilFaceState.depthFailOp(stencilBack, StencilOperation.KEEP.getValue());
        WGPUStencilFaceState.passOp(stencilBack, StencilOperation.KEEP.getValue());

        return struct;
    }

    public static class Builder {
        private TextureFormat format = TextureFormat.DEPTH24_PLUS;
        private boolean depthWriteEnabled = true;
        private CompareFunction depthCompare = CompareFunction.LESS;

        /**
         * Sets the depth buffer format.
         * Common formats: DEPTH16_UNORM, DEPTH24_PLUS, DEPTH32_FLOAT
         *
         * @param format The depth texture format
         * @return this builder
         */
        public Builder format(TextureFormat format) {
            this.format = format;
            return this;
        }

        /**
         * Sets whether depth values should be written to the depth buffer.
         *
         * @param depthWriteEnabled true to enable depth writes, false to disable
         * @return this builder
         */
        public Builder depthWriteEnabled(boolean depthWriteEnabled) {
            this.depthWriteEnabled = depthWriteEnabled;
            return this;
        }

        /**
         * Sets the depth comparison function.
         * LESS means closer objects (smaller depth values) pass the test.
         *
         * @param depthCompare The comparison function for depth testing
         * @return this builder
         */
        public Builder depthCompare(CompareFunction depthCompare) {
            this.depthCompare = depthCompare;
            return this;
        }

        public DepthStencilState build() {
            if (format == null) {
                throw new IllegalStateException("Format is required");
            }
            if (depthCompare == null) {
                throw new IllegalStateException("Depth compare function is required");
            }
            return new DepthStencilState(this);
        }
    }
}