package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;

/**
 * Configuration for creating a GPU buffer.
 */
public class BufferDescriptor {
    private final String label;
    private final long usage;
    private final long size;
    private final boolean mappedAtCreation;

    private BufferDescriptor(String label, long usage, long size, boolean mappedAtCreation) {
        this.label = label;
        this.usage = usage;
        this.size = size;
        this.mappedAtCreation = mappedAtCreation;
    }

    public String getLabel() {
        return label;
    }

    public long getUsage() {
        return usage;
    }

    public long getSize() {
        return size;
    }

    public boolean isMappedAtCreation() {
        return mappedAtCreation;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUBufferDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUBufferDescriptor.allocate(arena);

        WGPUBufferDescriptor.nextInChain(struct, MemorySegment.NULL);

        if (label != null && !label.isEmpty()) {
            MemorySegment labelBytes = arena.allocateFrom(label, StandardCharsets.UTF_8);
            MemorySegment labelStringView = WGPUBufferDescriptor.label(struct);
            WGPUStringView.data(labelStringView, labelBytes);
            WGPUStringView.length(labelStringView, label.length());
        } else {
            MemorySegment labelStringView = WGPUBufferDescriptor.label(struct);
            WGPUStringView.data(labelStringView, MemorySegment.NULL);
            WGPUStringView.length(labelStringView, 0);
        }

        WGPUBufferDescriptor.usage(struct, usage);

        WGPUBufferDescriptor.size(struct, size);

        WGPUBufferDescriptor.mappedAtCreation(struct, mappedAtCreation ? 1 : 0);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label = "";
        private long usage = 0;
        private long size = 0;
        private boolean mappedAtCreation = false;

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder usage(long usage) {
            this.usage = usage;
            return this;
        }

        public Builder size(long size) {
            this.size = size;
            return this;
        }

        public Builder mappedAtCreation(boolean mappedAtCreation) {
            this.mappedAtCreation = mappedAtCreation;
            return this;
        }

        public BufferDescriptor build() {
            if (size <= 0) {
                throw new IllegalArgumentException("Buffer size must be greater than 0");
            }
            if (usage == 0) {
                throw new IllegalArgumentException("Buffer usage must be specified");
            }
            return new BufferDescriptor(label, usage, size, mappedAtCreation);
        }
    }
}