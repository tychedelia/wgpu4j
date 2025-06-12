package org.wgpu4j.resource;

import org.wgpu4j.WgpuResource;
import org.wgpu4j.bindings.webgpu_h;

import java.lang.foreign.MemorySegment;

public class BindGroup extends WgpuResource {

    BindGroup(MemorySegment handle) {
        super(handle);
    }

    @Override
    protected void releaseNative() {
        webgpu_h.wgpuBindGroupRelease(handle);
    }

}