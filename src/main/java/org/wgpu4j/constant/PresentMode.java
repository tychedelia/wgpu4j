package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Present mode for surface presentation.
 */
public enum PresentMode {
    /**
     * VSync enabled - waits for vertical blanking.
     * Guaranteed to be supported on all platforms.
     */
    FIFO(webgpu_h.WGPUPresentMode_Fifo()),

    /**
     * VSync enabled, but allows tearing in case of late frames.
     */
    FIFO_RELAXED(webgpu_h.WGPUPresentMode_FifoRelaxed()),

    /**
     * No VSync - immediate presentation, may cause tearing.
     */
    IMMEDIATE(webgpu_h.WGPUPresentMode_Immediate()),

    /**
     * Triple buffering - low latency with smooth presentation.
     */
    MAILBOX(webgpu_h.WGPUPresentMode_Mailbox());

    private final int value;

    PresentMode(int value) {
        this.value = value;
    }

    /**
     * Gets the native WGPU value for this present mode.
     */
    public int getValue() {
        return value;
    }

    /**
     * Converts a native WGPU present mode value to the corresponding enum.
     */
    public static PresentMode fromValue(int value) {
        for (PresentMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown present mode value: " + value);
    }
}