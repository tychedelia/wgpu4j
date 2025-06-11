package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.WGPUBindGroupDescriptor;
import org.wgpu4j.bindings.WGPUBindGroupEntry;
import org.wgpu4j.bindings.WGPUStringView;
import org.wgpu4j.core.BindGroupLayout;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.List;
import java.util.ArrayList;

public class BindGroupDescriptor {
    private final String label;
    private final BindGroupLayout layout;
    private final List<BindGroupEntry> entries;

    private BindGroupDescriptor(Builder builder) {
        this.label = builder.label;
        this.layout = builder.layout;
        this.entries = List.copyOf(builder.entries);
    }

    public static Builder builder() {
        return new Builder();
    }

    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUBindGroupDescriptor.allocate(arena);

        WGPUBindGroupDescriptor.nextInChain(struct, MemorySegment.NULL);

        MemorySegment labelView = WGPUStringView.allocate(arena);
        if (label != null) {
            MemorySegment labelData = arena.allocateFrom(label);
            WGPUStringView.data(labelView, labelData);
            WGPUStringView.length(labelView, label.length());
        } else {
            WGPUStringView.data(labelView, MemorySegment.NULL);
            WGPUStringView.length(labelView, 0);
        }
        MemorySegment.copy(labelView, 0, struct, WGPUBindGroupDescriptor.label$offset(), WGPUStringView.sizeof());

        WGPUBindGroupDescriptor.layout(struct, layout.getHandle());

        if (!entries.isEmpty()) {
            MemorySegment entriesArray = WGPUBindGroupEntry.allocateArray(entries.size(), arena);
            for (int i = 0; i < entries.size(); i++) {
                MemorySegment entryStruct = entries.get(i).toCStruct(arena);
                MemorySegment.copy(entryStruct, 0, entriesArray,
                        WGPUBindGroupEntry.sizeof() * i, WGPUBindGroupEntry.sizeof());
            }
            WGPUBindGroupDescriptor.entries(struct, entriesArray);
            WGPUBindGroupDescriptor.entryCount(struct, entries.size());
        } else {
            WGPUBindGroupDescriptor.entries(struct, MemorySegment.NULL);
            WGPUBindGroupDescriptor.entryCount(struct, 0);
        }

        return struct;
    }

    public static class Builder {
        private String label;
        private BindGroupLayout layout;
        private List<BindGroupEntry> entries = new ArrayList<>();

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder layout(BindGroupLayout layout) {
            this.layout = layout;
            return this;
        }

        public Builder entries(List<BindGroupEntry> entries) {
            this.entries = new ArrayList<>(entries);
            return this;
        }

        public Builder entry(BindGroupEntry entry) {
            this.entries.add(entry);
            return this;
        }

        public BindGroupDescriptor build() {
            if (layout == null) {
                throw new IllegalStateException("Layout is required");
            }
            return new BindGroupDescriptor(this);
        }
    }
}