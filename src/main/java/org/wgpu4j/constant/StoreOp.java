package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Store operations for render pass attachments.
 * Determines what happens to attachment contents at the end of a render pass.
 */
public enum StoreOp {
    UNDEFINED(webgpu_h.WGPUStoreOp_Undefined()),
    STORE(webgpu_h.WGPUStoreOp_Store()),
    DISCARD(webgpu_h.WGPUStoreOp_Discard());

    private final int value;

    StoreOp(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static StoreOp fromValue(int value) {
        for (StoreOp op : values()) {
            if (op.value == value) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown StoreOp value: " + value);
    }
}