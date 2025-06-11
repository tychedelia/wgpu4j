package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;

/**
 * A shader constant entry that maps a name to a value.
 * Used to provide compile-time constants to shaders.
 */
public class ConstantEntry {
    private final String key;
    private final double value;

    public ConstantEntry(String key, double value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public double getValue() {
        return value;
    }

    /**
     * Converts this constant entry to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUConstantEntry struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUConstantEntry.allocate(arena);

        WGPUConstantEntry.nextInChain(struct, MemorySegment.NULL);

        if (key != null && !key.isEmpty()) {
            MemorySegment keyBytes = arena.allocateFrom(key, StandardCharsets.UTF_8);
            MemorySegment keyStringView = WGPUConstantEntry.key(struct);
            WGPUStringView.data(keyStringView, keyBytes);
            WGPUStringView.length(keyStringView, key.length());
        } else {
            MemorySegment keyStringView = WGPUConstantEntry.key(struct);
            WGPUStringView.data(keyStringView, MemorySegment.NULL);
            WGPUStringView.length(keyStringView, 0);
        }

        WGPUConstantEntry.value(struct, value);

        return struct;
    }
}