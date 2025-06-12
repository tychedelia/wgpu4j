package org.wgpu4j.resource;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.WgpuResource;
import org.wgpu4j.bindings.webgpu_h;

import java.lang.foreign.MemorySegment;

/**
 * Defines the interface between a set of resources bound in a bind group
 * and their accessibility in shaders.
 */
public class BindGroupLayout extends WgpuResource {

    static {
        WgpuNative.ensureLoaded();
    }

    protected BindGroupLayout(MemorySegment handle) {
        super(handle);
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuBindGroupLayoutRelease(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to release bind group layout", e);
        }
    }
}