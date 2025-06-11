package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Describes the size of a 3D extent.
 */
public class Extent3D {
    private final int width;
    private final int height;
    private final int depthOrArrayLayers;

    private Extent3D(Builder builder) {
        this.width = builder.width;
        this.height = builder.height;
        this.depthOrArrayLayers = builder.depthOrArrayLayers;
    }

    /**
     * Creates a C struct representing this extent.
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUExtent3D.allocate(arena);

        WGPUExtent3D.width(struct, width);
        WGPUExtent3D.height(struct, height);
        WGPUExtent3D.depthOrArrayLayers(struct, depthOrArrayLayers);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a 2D extent.
     */
    public static Extent3D of(int width, int height) {
        return builder().size(width, height).build();
    }

    /**
     * Creates a 3D extent.
     */
    public static Extent3D of(int width, int height, int depthOrArrayLayers) {
        return builder().size(width, height, depthOrArrayLayers).build();
    }

    public static class Builder {
        private int width = 1;
        private int height = 1;
        private int depthOrArrayLayers = 1;

        /**
         * Sets the width of the extent.
         */
        public Builder width(int width) {
            this.width = width;
            return this;
        }

        /**
         * Sets the height of the extent.
         */
        public Builder height(int height) {
            this.height = height;
            return this;
        }

        /**
         * Sets the depth or array layers of the extent.
         */
        public Builder depthOrArrayLayers(int depthOrArrayLayers) {
            this.depthOrArrayLayers = depthOrArrayLayers;
            return this;
        }

        /**
         * Sets the size (2D).
         */
        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Sets the size (3D).
         */
        public Builder size(int width, int height, int depthOrArrayLayers) {
            this.width = width;
            this.height = height;
            this.depthOrArrayLayers = depthOrArrayLayers;
            return this;
        }

        public Extent3D build() {
            if (width <= 0 || height <= 0 || depthOrArrayLayers <= 0) {
                throw new IllegalArgumentException("All extent dimensions must be positive");
            }
            return new Extent3D(this);
        }
    }
}