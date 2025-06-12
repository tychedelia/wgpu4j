package org.wgpu4j.resource;

import org.wgpu4j.WgpuResource;
import org.wgpu4j.bindings.webgpu_h;

import java.lang.foreign.MemorySegment;

/**
 * Represents a compiled shader module that can be used in render or compute pipelines.
 * <p>
 * ShaderModules are created from WGSL shader source code and can contain vertex shaders,
 * fragment shaders, or compute shaders. Once created, they can be referenced by render
 * pipelines and compute pipelines.
 */
public class ShaderModule extends WgpuResource {

    /**
     * Creates a ShaderModule from a native WGPU handle.
     *
     * @param handle The native WGPUShaderModule handle
     */
    public ShaderModule(MemorySegment handle) {
        super(handle);
    }

    @Override
    protected void releaseNative() {
        webgpu_h.wgpuShaderModuleRelease(handle);
    }

    @Override
    public String toString() {
        return String.format("ShaderModule[handle=%s, closed=%s]",
                handle == null ? "null" : "0x" + Long.toHexString(handle.address()),
                isClosed());
    }
}