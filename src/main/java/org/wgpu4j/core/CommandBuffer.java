package org.wgpu4j.core;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * A buffer of recorded commands ready for submission to the GPU.
 */
public class CommandBuffer extends WgpuResource {

    static {
        WgpuNative.ensureLoaded();
    }

    protected CommandBuffer(MemorySegment handle) {
        super(handle);
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuCommandBufferRelease(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to release command buffer", e);
        }
    }
}