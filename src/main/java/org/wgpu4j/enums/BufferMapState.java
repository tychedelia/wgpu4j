package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Current mapping state of a buffer.
 */
public enum BufferMapState {
    /**
     * Buffer is not currently mapped to CPU memory.
     */
    UNMAPPED(webgpu_h.WGPUBufferMapState_Unmapped()),

    /**
     * Buffer mapping operation is in progress.
     */
    PENDING(webgpu_h.WGPUBufferMapState_Pending()),

    /**
     * Buffer is currently mapped and accessible from CPU.
     */
    MAPPED(webgpu_h.WGPUBufferMapState_Mapped());

    private final int value;

    BufferMapState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts a WGPU buffer map state value to the corresponding enum.
     *
     * @param value The WGPU buffer map state value
     * @return The corresponding BufferMapState enum
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static BufferMapState fromValue(int value) {
        for (BufferMapState state : values()) {
            if (state.value == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown buffer map state value: " + value);
    }
}