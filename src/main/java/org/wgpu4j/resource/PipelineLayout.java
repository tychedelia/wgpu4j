package org.wgpu4j.resource;

import org.wgpu4j.WgpuResource;
import org.wgpu4j.bindings.webgpu_h;

import java.lang.foreign.MemorySegment;

/**
 * A pipeline layout defines the bind group layouts used by a render or compute pipeline.
 */
public class PipelineLayout extends WgpuResource {

    /**
     * Creates a new PipelineLayout wrapper around the given native handle.
     *
     * @param handle The native WGPUPipelineLayout handle
     */
    public PipelineLayout(MemorySegment handle) {
        super(handle);
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuPipelineLayoutRelease(handle);
        } catch (Exception e) {
            throw new RuntimeException("Failed to release pipeline layout", e);
        }
    }
}