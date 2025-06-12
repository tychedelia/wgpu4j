package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;

/**
 * Configuration for creating a WGPU surface for rendering to a window.
 */
public class SurfaceDescriptor implements Marshalable {
    private final String label;

    private SurfaceDescriptor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUSurfaceDescriptor struct
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPUSurfaceDescriptor.allocate(arena);

        WGPUSurfaceDescriptor.nextInChain(struct, MemorySegment.NULL);

        if (label != null && !label.isEmpty()) {
            MemorySegment labelBytes = arena.allocateFrom(label, StandardCharsets.UTF_8);
            MemorySegment labelStringView = WGPUSurfaceDescriptor.label(struct);
            WGPUStringView.data(labelStringView, labelBytes);
            WGPUStringView.length(labelStringView, label.length());
        } else {
            MemorySegment labelStringView = WGPUSurfaceDescriptor.label(struct);
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

        public SurfaceDescriptor build() {
            return new SurfaceDescriptor(label);
        }
    }
}