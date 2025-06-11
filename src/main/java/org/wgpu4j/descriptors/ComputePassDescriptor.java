package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Configuration for creating a compute pass.
 * Compute passes record compute shader dispatches and resource bindings.
 */
public class ComputePassDescriptor {
    private final String label;

    private ComputePassDescriptor(Builder builder) {
        this.label = builder.label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUComputePassDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment descriptor = WGPUComputePassDescriptor.allocate(arena);

        WGPUComputePassDescriptor.nextInChain(descriptor, MemorySegment.NULL);

        MemorySegment labelView = WGPUStringView.allocate(arena);
        if (label != null && !label.isEmpty()) {
            MemorySegment labelData = arena.allocateFrom(label);
            WGPUStringView.data(labelView, labelData);
            WGPUStringView.length(labelView, label.length());
        } else {
            WGPUStringView.data(labelView, MemorySegment.NULL);
            WGPUStringView.length(labelView, 0);
        }
        MemorySegment.copy(labelView, 0, descriptor, WGPUComputePassDescriptor.label$offset(), WGPUStringView.sizeof());

        WGPUComputePassDescriptor.timestampWrites(descriptor, MemorySegment.NULL);

        return descriptor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label = "";

        /**
         * Sets the debug label for the compute pass.
         *
         * @param label The debug label
         * @return this builder
         */
        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public ComputePassDescriptor build() {
            return new ComputePassDescriptor(this);
        }
    }
}