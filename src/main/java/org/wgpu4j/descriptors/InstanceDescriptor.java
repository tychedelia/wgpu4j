package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Configuration for creating a WebGPU instance.
 */
public class InstanceDescriptor {
    private final String label;
    private final InstanceExtras extras;

    private InstanceDescriptor(String label, InstanceExtras extras) {
        this.label = label;
        this.extras = extras;
    }

    public String getLabel() {
        return label;
    }

    public InstanceExtras getExtras() {
        return extras;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUInstanceDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUInstanceDescriptor.allocate(arena);

        // Set next in chain to extras if provided
        if (extras != null) {
            MemorySegment extrasStruct = extras.toCStruct(arena);
            WGPUInstanceDescriptor.nextInChain(struct, extrasStruct);
        } else {
            WGPUInstanceDescriptor.nextInChain(struct, MemorySegment.NULL);
        }

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label = "";
        private InstanceExtras extras;

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder extras(InstanceExtras extras) {
            this.extras = extras;
            return this;
        }

        public InstanceDescriptor build() {
            return new InstanceDescriptor(label, extras);
        }
    }
}