package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;

/**
 * Configuration for creating a render bundle from a render bundle encoder.
 * Render bundles are pre-recorded command sequences that can be replayed multiple times.
 */
public class RenderBundleDescriptor {
    private final String label;

    private RenderBundleDescriptor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPURenderBundleDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPURenderBundleDescriptor.allocate(arena);

        WGPURenderBundleDescriptor.nextInChain(struct, MemorySegment.NULL);

        if (label != null && !label.isEmpty()) {
            MemorySegment labelBytes = arena.allocateFrom(label, StandardCharsets.UTF_8);
            MemorySegment labelStringView = WGPURenderBundleDescriptor.label(struct);
            WGPUStringView.data(labelStringView, labelBytes);
            WGPUStringView.length(labelStringView, label.length());
        } else {
            MemorySegment labelStringView = WGPURenderBundleDescriptor.label(struct);
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

        public RenderBundleDescriptor build() {
            return new RenderBundleDescriptor(label);
        }
    }
}