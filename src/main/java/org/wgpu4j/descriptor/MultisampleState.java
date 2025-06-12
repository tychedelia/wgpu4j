package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Configuration for multisampling state.
 * Controls how many samples are used for anti-aliasing and alpha-to-coverage.
 */
public class MultisampleState implements Marshalable {
    private final int count;
    private final int mask;
    private final boolean alphaToCoverageEnabled;

    private MultisampleState(int count, int mask, boolean alphaToCoverageEnabled) {
        this.count = count;
        this.mask = mask;
        this.alphaToCoverageEnabled = alphaToCoverageEnabled;
    }

    public int getCount() {
        return count;
    }

    public int getMask() {
        return mask;
    }

    public boolean isAlphaToCoverageEnabled() {
        return alphaToCoverageEnabled;
    }

    /**
     * Converts this multisample state to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUMultisampleState struct
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPUMultisampleState.allocate(arena);

        WGPUMultisampleState.nextInChain(struct, MemorySegment.NULL);
        WGPUMultisampleState.count(struct, count);
        WGPUMultisampleState.mask(struct, mask);
        WGPUMultisampleState.alphaToCoverageEnabled(struct, alphaToCoverageEnabled ? 1 : 0);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int count = 1;
        private int mask = 0xFFFFFFFF;
        private boolean alphaToCoverageEnabled = false;

        /**
         * Sets the number of samples per pixel.
         */
        public Builder count(int count) {
            if (count < 1) {
                throw new IllegalArgumentException("Sample count must be at least 1");
            }
            this.count = count;
            return this;
        }

        /**
         * Sets the sample mask (which samples are active).
         */
        public Builder mask(int mask) {
            this.mask = mask;
            return this;
        }

        /**
         * Enables or disables alpha-to-coverage.
         */
        public Builder alphaToCoverageEnabled(boolean enabled) {
            this.alphaToCoverageEnabled = enabled;
            return this;
        }

        public MultisampleState build() {
            return new MultisampleState(count, mask, alphaToCoverageEnabled);
        }
    }
}