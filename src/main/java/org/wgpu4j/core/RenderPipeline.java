package org.wgpu4j.core;

import org.wgpu4j.bindings.webgpu_h;

import java.lang.foreign.MemorySegment;

/**
 * Represents a configured graphics pipeline that can render primitives.
 * <p>
 * A RenderPipeline defines the complete graphics state including vertex processing,
 * primitive assembly, rasterization, and fragment processing. Once created, it can
 * be set on a render pass to configure the GPU for drawing operations.
 */
public class RenderPipeline extends WgpuResource {

    /**
     * Creates a RenderPipeline from a native WGPU handle.
     *
     * @param handle The native WGPURenderPipeline handle
     */
    public RenderPipeline(MemorySegment handle) {
        super(handle);
    }

    @Override
    protected void releaseNative() {
        webgpu_h.wgpuRenderPipelineRelease(handle);
    }

    @Override
    public String toString() {
        return String.format("RenderPipeline[handle=%s, closed=%s]",
                handle == null ? "null" : "0x" + Long.toHexString(handle.address()),
                isClosed());
    }
}