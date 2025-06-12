package org.wgpu4j.resource;

import org.wgpu4j.WgpuResource;
import org.wgpu4j.bindings.webgpu_h;

import java.lang.foreign.MemorySegment;

/**
 * Represents a pre-recorded sequence of rendering commands that can be replayed efficiently.
 * <p>
 * A RenderBundle is created from a RenderBundleEncoder and contains a sequence of commands
 * that can be executed multiple times within different render passes. This allows for
 * efficient command reuse and reduces CPU overhead for repeated rendering operations.
 */
public class RenderBundle extends WgpuResource {

    /**
     * Creates a RenderBundle from a native WGPU handle.
     *
     * @param handle The native WGPURenderBundle handle
     */
    public RenderBundle(MemorySegment handle) {
        super(handle);
    }

    @Override
    protected void releaseNative() {
        webgpu_h.wgpuRenderBundleRelease(handle);
    }

    @Override
    public String toString() {
        return String.format("RenderBundle[handle=%s, closed=%s]",
                handle == null ? "null" : "0x" + Long.toHexString(handle.address()),
                isClosed());
    }
}