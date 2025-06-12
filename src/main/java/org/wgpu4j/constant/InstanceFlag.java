package org.wgpu4j.constant;

/**
 * WebGPU instance configuration flags.
 * Based on WGPUInstanceFlag from wgpu.h
 */
public enum InstanceFlag {
    DEFAULT(0x00000000),
    DEBUG(1 << 0),
    VALIDATION(1 << 1),
    DISCARD_HAL_LABELS(1 << 2);

    private final int value;

    InstanceFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static InstanceFlag fromValue(int value) {
        for (InstanceFlag flag : values()) {
            if (flag.value == value) {
                return flag;
            }
        }
        throw new IllegalArgumentException("Unknown InstanceFlag value: " + value);
    }
}