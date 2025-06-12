package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.WGPUBindGroupLayoutDescriptor;
import org.wgpu4j.bindings.WGPUBindGroupLayoutEntry;
import org.wgpu4j.bindings.WGPUStringView;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Descriptor for creating a bind group layout.
 */
public class BindGroupLayoutDescriptor implements Marshalable {
    private final String label;
    private final List<BindGroupLayoutEntry> entries;

    private BindGroupLayoutDescriptor(Builder builder) {
        this.label = builder.label;
        this.entries = new ArrayList<>(builder.entries);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Converts this descriptor to a C struct.
     *
     * @param arena Arena for memory allocation
     * @return Memory segment containing the C struct
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment descriptor = WGPUBindGroupLayoutDescriptor.allocate(arena);

        WGPUBindGroupLayoutDescriptor.nextInChain(descriptor, MemorySegment.NULL);

        if (label != null && !label.isEmpty()) {
            MemorySegment labelBytes = arena.allocateFrom(label, StandardCharsets.UTF_8);
            MemorySegment labelStringView = WGPUBindGroupLayoutDescriptor.label(descriptor);
            WGPUStringView.data(labelStringView, labelBytes);
            WGPUStringView.length(labelStringView, label.length());
        } else {
            MemorySegment labelStringView = WGPUBindGroupLayoutDescriptor.label(descriptor);
            WGPUStringView.data(labelStringView, MemorySegment.NULL);
            WGPUStringView.length(labelStringView, 0);
        }

        WGPUBindGroupLayoutDescriptor.entryCount(descriptor, entries.size());

        if (!entries.isEmpty()) {
            MemorySegment entryArray = WGPUBindGroupLayoutEntry.allocateArray(entries.size(), arena);

            for (int i = 0; i < entries.size(); i++) {
                MemorySegment entryStruct = entries.get(i).marshal(arena);
                MemorySegment target = WGPUBindGroupLayoutEntry.asSlice(entryArray, i);
                MemorySegment.copy(entryStruct, 0, target, 0, WGPUBindGroupLayoutEntry.sizeof());
            }

            WGPUBindGroupLayoutDescriptor.entries(descriptor, entryArray);
        } else {
            WGPUBindGroupLayoutDescriptor.entries(descriptor, MemorySegment.NULL);
        }

        return descriptor;
    }

    public String getLabel() {
        return label;
    }

    public List<BindGroupLayoutEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public static class Builder {
        private String label = "";
        private final List<BindGroupLayoutEntry> entries = new ArrayList<>();

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder entry(BindGroupLayoutEntry entry) {
            this.entries.add(entry);
            return this;
        }

        public BindGroupLayoutDescriptor build() {
            return new BindGroupLayoutDescriptor(this);
        }
    }
}