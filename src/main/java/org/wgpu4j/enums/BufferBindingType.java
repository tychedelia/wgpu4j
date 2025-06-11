package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Type-safe buffer binding types for bind group layouts.
 */
public enum BufferBindingType {
    /**
     * Binding not used.
     */
    BINDING_NOT_USED(webgpu_h.WGPUBufferBindingType_BindingNotUsed()),

    /**
     * Undefined binding type.
     */
    UNDEFINED(webgpu_h.WGPUBufferBindingType_Undefined()),

    /**
     * Uniform buffer binding - read-only data accessible to shaders.
     * Used for constants like transformation matrices, material properties, etc.
     */
    UNIFORM(webgpu_h.WGPUBufferBindingType_Uniform()),

    /**
     * Storage buffer binding - read-write data accessible to shaders.
     * Used for large datasets that need to be modified by shaders.
     */
    STORAGE(webgpu_h.WGPUBufferBindingType_Storage()),

    /**
     * Read-only storage buffer binding.
     * Used for large read-only datasets like vertex data or lookup tables.
     */
    READ_ONLY_STORAGE(webgpu_h.WGPUBufferBindingType_ReadOnlyStorage());

    private final int value;

    BufferBindingType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts an integer value back to the enum.
     *
     * @param value The integer value
     * @return The corresponding enum value
     * @throws IllegalArgumentException if the value doesn't match any enum
     */
    public static BufferBindingType fromValue(int value) {
        for (BufferBindingType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown buffer binding type: " + value);
    }
}