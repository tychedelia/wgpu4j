package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;
import org.wgpu4j.enums.TextureFormat;

import java.lang.foreign.*;

/**
 * Configuration for a color render target in a render pipeline.
 * Defines the texture format and blending behavior for a color attachment.
 */
public class ColorTargetState {
    private final TextureFormat format;
    private final BlendState blend;
    private final int writeMask;

    private ColorTargetState(TextureFormat format, BlendState blend, int writeMask) {
        this.format = format;
        this.blend = blend;
        this.writeMask = writeMask;
    }

    public TextureFormat getFormat() {
        return format;
    }

    public BlendState getBlend() {
        return blend;
    }

    public int getWriteMask() {
        return writeMask;
    }

    /**
     * Converts this color target state to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUColorTargetState struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUColorTargetState.allocate(arena);

        WGPUColorTargetState.nextInChain(struct, MemorySegment.NULL);
        WGPUColorTargetState.format(struct, format.getValue());

        if (blend != null) {
            MemorySegment blendStruct = blend.toCStruct(arena);
            WGPUColorTargetState.blend(struct, blendStruct);
        } else {
            WGPUColorTargetState.blend(struct, MemorySegment.NULL);
        }

        WGPUColorTargetState.writeMask(struct, writeMask);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TextureFormat format = TextureFormat.BGRA8_UNORM;
        private BlendState blend = null;
        private int writeMask = 0xF;

        /**
         * Sets the texture format for this color target.
         */
        public Builder format(TextureFormat format) {
            this.format = format;
            return this;
        }

        /**
         * Sets the blend state for this color target.
         * Set to null to disable blending (replace mode).
         */
        public Builder blend(BlendState blend) {
            this.blend = blend;
            return this;
        }

        /**
         * Sets the color write mask (which color channels to write to).
         * Use bitwise OR of ColorWriteMask constants.
         */
        public Builder writeMask(int writeMask) {
            this.writeMask = writeMask;
            return this;
        }

        public ColorTargetState build() {
            return new ColorTargetState(format, blend, writeMask);
        }
    }

    /**
     * Constants for color write mask bits.
     */
    public static class ColorWriteMask {
        public static final int RED = 0x1;
        public static final int GREEN = 0x2;
        public static final int BLUE = 0x4;
        public static final int ALPHA = 0x8;
        public static final int ALL = RED | GREEN | BLUE | ALPHA;
    }
}