package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Address mode for texture coordinates outside the [0, 1] range.
 */
public enum AddressMode {
    /**
     * Clamp texture coordinates to the edge.
     * Coordinates outside [0, 1] will use the edge pixel.
     */
    CLAMP_TO_EDGE(webgpu_h.WGPUAddressMode_ClampToEdge()),

    /**
     * Repeat the texture.
     * Coordinates outside [0, 1] will wrap around.
     */
    REPEAT(webgpu_h.WGPUAddressMode_Repeat()),

    /**
     * Mirror and repeat the texture.
     * Coordinates outside [0, 1] will mirror and then wrap.
     */
    MIRROR_REPEAT(webgpu_h.WGPUAddressMode_MirrorRepeat());

    private final int value;

    AddressMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts a WGPU address mode value to the corresponding enum.
     *
     * @param value The WGPU address mode value
     * @return The corresponding AddressMode enum
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static AddressMode fromValue(int value) {
        for (AddressMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown address mode value: " + value);
    }
}