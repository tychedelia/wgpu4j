package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.resource.BindGroupLayout;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Configuration for creating a pipeline layout.
 */
public class PipelineLayoutDescriptor implements Marshalable {
    private final String label;
    private final List<BindGroupLayout> bindGroupLayouts;

    private PipelineLayoutDescriptor(Builder builder) {
        this.label = builder.label;
        this.bindGroupLayouts = new ArrayList<>(builder.bindGroupLayouts);
    }

    public String getLabel() {
        return label;
    }

    public List<BindGroupLayout> getBindGroupLayouts() {
        return bindGroupLayouts;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUPipelineLayoutDescriptor struct
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPUPipelineLayoutDescriptor.allocate(arena);

        WGPUPipelineLayoutDescriptor.nextInChain(struct, MemorySegment.NULL);

        MemorySegment labelView = WGPUStringView.allocate(arena);
        if (label != null && !label.isEmpty()) {
            MemorySegment labelData = arena.allocateFrom(label);
            WGPUStringView.data(labelView, labelData);
            WGPUStringView.length(labelView, label.length());
        } else {
            WGPUStringView.data(labelView, MemorySegment.NULL);
            WGPUStringView.length(labelView, 0);
        }
        MemorySegment.copy(labelView, 0, struct, WGPUPipelineLayoutDescriptor.label$offset(), WGPUStringView.sizeof());

        WGPUPipelineLayoutDescriptor.bindGroupLayoutCount(struct, bindGroupLayouts.size());

        if (!bindGroupLayouts.isEmpty()) {
            MemorySegment layoutsArray = arena.allocate(ValueLayout.ADDRESS, bindGroupLayouts.size());
            for (int i = 0; i < bindGroupLayouts.size(); i++) {
                layoutsArray.setAtIndex(ValueLayout.ADDRESS, i, bindGroupLayouts.get(i).getHandle());
            }
            WGPUPipelineLayoutDescriptor.bindGroupLayouts(struct, layoutsArray);
        } else {
            WGPUPipelineLayoutDescriptor.bindGroupLayouts(struct, MemorySegment.NULL);
        }

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label;
        private List<BindGroupLayout> bindGroupLayouts = new ArrayList<>();

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder bindGroupLayout(BindGroupLayout layout) {
            this.bindGroupLayouts.add(layout);
            return this;
        }

        public Builder bindGroupLayouts(List<BindGroupLayout> layouts) {
            this.bindGroupLayouts.addAll(layouts);
            return this;
        }

        public PipelineLayoutDescriptor build() {
            return new PipelineLayoutDescriptor(this);
        }
    }
}