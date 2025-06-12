package org.wgpu4j.resource;

import org.wgpu4j.WgpuResource;
import org.wgpu4j.bindings.webgpu_h;

import java.lang.foreign.MemorySegment;

/**
 * A sampler defines how textures are filtered and addressed when sampled in shaders.
 * Configures magnification/minification filtering, mipmap filtering, address modes, and anisotropy.
 */
public class Sampler extends WgpuResource {

    /**
     * Creates a new Sampler wrapper around the given native handle.
     *
     * @param handle The native WGPUSampler handle
     */
    public Sampler(MemorySegment handle) {
        super(handle);
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuSamplerRelease(handle);
        } catch (Exception e) {
            throw new RuntimeException("Failed to release sampler", e);
        }
    }
}