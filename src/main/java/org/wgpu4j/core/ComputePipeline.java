package org.wgpu4j.core;

import org.wgpu4j.WgpuException;
import org.wgpu4j.bindings.webgpu_h;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.Arena;

/**
 * Represents a compute pipeline that can execute compute shaders on the GPU.
 * <p>
 * Compute pipelines are used for general-purpose GPU computing (GPGPU),
 * including data processing, simulations, and parallel algorithms.
 */
public class ComputePipeline extends WgpuResource {

    protected ComputePipeline(MemorySegment handle) {
        super(handle);
    }

    /**
     * Gets the bind group layout at the specified index.
     * This layout describes the resource bindings used by the compute shader.
     *
     * @param groupIndex The index of the bind group layout
     * @return The bind group layout at the specified index
     */
    public BindGroupLayout getBindGroupLayout(int groupIndex) {
        checkNotClosed();

        try {
            MemorySegment layoutHandle = webgpu_h.wgpuComputePipelineGetBindGroupLayout(handle, groupIndex);
            if (layoutHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to get bind group layout at index " + groupIndex);
            }
            return new BindGroupLayout(layoutHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to get bind group layout", e);
        }
    }

    /**
     * Sets the debug label for this compute pipeline.
     * Useful for debugging and profiling GPU workloads.
     *
     * @param label The debug label to set
     */
    public void setLabel(String label) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            if (label == null) {
                label = "";
            }
            MemorySegment labelData = arena.allocateFrom(label);
            webgpu_h.wgpuComputePipelineSetLabel(handle, labelData);
        } catch (Exception e) {
            throw new WgpuException("Failed to set compute pipeline label", e);
        }
    }

    @Override
    protected void releaseNative() {
        webgpu_h.wgpuComputePipelineRelease(handle);
    }
}