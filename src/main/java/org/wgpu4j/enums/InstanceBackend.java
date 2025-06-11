package org.wgpu4j.enums;

/**
 * WebGPU backend selection flags.
 * Based on WGPUInstanceBackend from wgpu.h
 */
public enum InstanceBackend {
    ALL(0x00000000),
    VULKAN(1 << 0),
    GL(1 << 1),
    METAL(1 << 2),
    DX12(1 << 3),
    DX11(1 << 4),
    BROWSER_WEBGPU(1 << 5),
    PRIMARY((1 << 0) | (1 << 2) | (1 << 3) | (1 << 5)),
    SECONDARY((1 << 1) | (1 << 4));

    private final int value;

    InstanceBackend(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static InstanceBackend fromValue(int value) {
        for (InstanceBackend backend : values()) {
            if (backend.value == value) {
                return backend;
            }
        }
        throw new IllegalArgumentException("Unknown InstanceBackend value: " + value);
    }
}