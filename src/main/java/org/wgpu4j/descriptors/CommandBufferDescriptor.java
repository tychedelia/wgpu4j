package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;

/**
 * Configuration for creating a command buffer from a command encoder.
 */
public class CommandBufferDescriptor {
    private final String label;

    private CommandBufferDescriptor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUCommandBufferDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUCommandBufferDescriptor.allocate(arena);

        WGPUCommandBufferDescriptor.nextInChain(struct, MemorySegment.NULL);

        if (label != null && !label.isEmpty()) {
            MemorySegment labelBytes = arena.allocateFrom(label, StandardCharsets.UTF_8);
            MemorySegment labelStringView = WGPUCommandBufferDescriptor.label(struct);
            WGPUStringView.data(labelStringView, labelBytes);
            WGPUStringView.length(labelStringView, label.length());
        } else {
            MemorySegment labelStringView = WGPUCommandBufferDescriptor.label(struct);
            WGPUStringView.data(labelStringView, MemorySegment.NULL);
            WGPUStringView.length(labelStringView, 0);
        }

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label = "";

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public CommandBufferDescriptor build() {
            return new CommandBufferDescriptor(label);
        }
    }
}