package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;
import org.wgpu4j.enums.PrimitiveTopology;
import org.wgpu4j.enums.IndexFormat;
import org.wgpu4j.enums.FrontFace;
import org.wgpu4j.enums.CullMode;

import java.lang.foreign.*;

/**
 * Configuration for primitive assembly and rasterization state.
 * Controls how vertices are assembled into primitives and how they are rasterized.
 */
public class PrimitiveState {
    private final PrimitiveTopology topology;
    private final IndexFormat stripIndexFormat;
    private final FrontFace frontFace;
    private final CullMode cullMode;
    private final boolean unclippedDepth;

    private PrimitiveState(PrimitiveTopology topology, IndexFormat stripIndexFormat,
                           FrontFace frontFace, CullMode cullMode, boolean unclippedDepth) {
        this.topology = topology;
        this.stripIndexFormat = stripIndexFormat;
        this.frontFace = frontFace;
        this.cullMode = cullMode;
        this.unclippedDepth = unclippedDepth;
    }

    public PrimitiveTopology getTopology() {
        return topology;
    }

    public IndexFormat getStripIndexFormat() {
        return stripIndexFormat;
    }

    public FrontFace getFrontFace() {
        return frontFace;
    }

    public CullMode getCullMode() {
        return cullMode;
    }

    public boolean isUnclippedDepth() {
        return unclippedDepth;
    }

    /**
     * Converts this primitive state to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUPrimitiveState struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUPrimitiveState.allocate(arena);

        WGPUPrimitiveState.nextInChain(struct, MemorySegment.NULL);
        WGPUPrimitiveState.topology(struct, topology.getValue());
        WGPUPrimitiveState.stripIndexFormat(struct,
                stripIndexFormat != null ? stripIndexFormat.getValue() : IndexFormat.UNDEFINED.getValue());
        WGPUPrimitiveState.frontFace(struct, frontFace.getValue());
        WGPUPrimitiveState.cullMode(struct, cullMode.getValue());
        WGPUPrimitiveState.unclippedDepth(struct, unclippedDepth ? 1 : 0);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PrimitiveTopology topology = PrimitiveTopology.TRIANGLE_LIST;
        private IndexFormat stripIndexFormat = null;
        private FrontFace frontFace = FrontFace.CCW;
        private CullMode cullMode = CullMode.NONE;
        private boolean unclippedDepth = false;

        /**
         * Sets the primitive topology (how vertices are assembled into primitives).
         */
        public Builder topology(PrimitiveTopology topology) {
            this.topology = topology;
            return this;
        }

        /**
         * Sets the index format for strip topologies.
         * Only used with TRIANGLE_STRIP and LINE_STRIP topologies.
         */
        public Builder stripIndexFormat(IndexFormat stripIndexFormat) {
            this.stripIndexFormat = stripIndexFormat;
            return this;
        }

        /**
         * Sets which triangle winding order is considered front-facing.
         */
        public Builder frontFace(FrontFace frontFace) {
            this.frontFace = frontFace;
            return this;
        }

        /**
         * Sets which triangles to cull (discard during rasterization).
         */
        public Builder cullMode(CullMode cullMode) {
            this.cullMode = cullMode;
            return this;
        }

        /**
         * Sets whether depth values outside [0, 1] should be clamped.
         */
        public Builder unclippedDepth(boolean unclippedDepth) {
            this.unclippedDepth = unclippedDepth;
            return this;
        }

        public PrimitiveState build() {
            return new PrimitiveState(topology, stripIndexFormat, frontFace, cullMode, unclippedDepth);
        }
    }
}