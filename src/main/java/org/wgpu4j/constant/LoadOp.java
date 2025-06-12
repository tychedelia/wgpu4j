package org.wgpu4j.constant;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Load operations for render pass attachments.
 * Determines what happens to attachment contents at the start of a render pass.
 */
public enum LoadOp {
    UNDEFINED(webgpu_h.WGPULoadOp_Undefined()),
    CLEAR(webgpu_h.WGPULoadOp_Clear()),
    LOAD(webgpu_h.WGPULoadOp_Load());

    private final int value;

    LoadOp(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LoadOp fromValue(int value) {
        for (LoadOp op : values()) {
            if (op.value == value) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown LoadOp value: " + value);
    }
}