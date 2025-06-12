package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Access modes for storage textures in shaders.
 */
public enum StorageTextureAccess {
    UNDEFINED(webgpu_h.WGPUStorageTextureAccess_Undefined()),
    WRITE_ONLY(webgpu_h.WGPUStorageTextureAccess_WriteOnly()),
    READ_ONLY(webgpu_h.WGPUStorageTextureAccess_ReadOnly()),
    READ_WRITE(webgpu_h.WGPUStorageTextureAccess_ReadWrite());

    private final int value;

    StorageTextureAccess(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static StorageTextureAccess fromValue(int value) {
        for (StorageTextureAccess access : values()) {
            if (access.value == value) {
                return access;
            }
        }
        throw new IllegalArgumentException("Unknown StorageTextureAccess value: " + value);
    }
}