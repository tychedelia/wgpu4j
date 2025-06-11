package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Index buffer format specifying the size of index values.
 */
public enum IndexFormat {
    /**
     * Undefined index format.
     */
    UNDEFINED(webgpu_h.WGPUIndexFormat_Undefined()),

    /**
     * 16-bit unsigned integer indices.
     * Allows up to 65,535 unique vertices.
     */
    UINT16(webgpu_h.WGPUIndexFormat_Uint16()),

    /**
     * 32-bit unsigned integer indices.
     * Allows up to 4,294,967,295 unique vertices.
     */
    UINT32(webgpu_h.WGPUIndexFormat_Uint32());

    private final int value;

    IndexFormat(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Gets the size in bytes of each index for this format.
     *
     * @return Index size in bytes (2 for UINT16, 4 for UINT32, 0 for UNDEFINED)
     */
    public int getIndexSize() {
        return switch (this) {
            case UINT16 -> 2;
            case UINT32 -> 4;
            case UNDEFINED -> 0;
        };
    }

    /**
     * Converts a WGPU index format value to the corresponding enum.
     *
     * @param value The WGPU index format value
     * @return The corresponding IndexFormat enum
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static IndexFormat fromValue(int value) {
        for (IndexFormat format : values()) {
            if (format.value == value) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown index format value: " + value);
    }
}