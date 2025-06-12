package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Status of an asynchronous buffer mapping operation.
 */
public enum MapAsyncStatus {
    /**
     * Buffer mapping completed successfully.
     */
    SUCCESS(webgpu_h.WGPUMapAsyncStatus_Success()),

    /**
     * WGPU instance was dropped before mapping completed.
     */
    INSTANCE_DROPPED(webgpu_h.WGPUMapAsyncStatus_InstanceDropped()),

    /**
     * Buffer mapping failed with an error.
     */
    ERROR(webgpu_h.WGPUMapAsyncStatus_Error()),

    /**
     * Buffer mapping operation was aborted.
     */
    ABORTED(webgpu_h.WGPUMapAsyncStatus_Aborted()),

    /**
     * Unknown status.
     */
    UNKNOWN(webgpu_h.WGPUMapAsyncStatus_Unknown());

    private final int value;

    MapAsyncStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts a WGPU map async status value to the corresponding enum.
     *
     * @param value The WGPU map async status value
     * @return The corresponding MapAsyncStatus enum
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static MapAsyncStatus fromValue(int value) {
        for (MapAsyncStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown map async status value: " + value);
    }
}