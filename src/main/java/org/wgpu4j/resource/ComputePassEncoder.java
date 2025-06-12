package org.wgpu4j.resource;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuResource;
import org.wgpu4j.bindings.webgpu_h;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.Arena;

/**
 * Encoder for recording compute commands.
 * <p>
 * Compute pass encoders are used to dispatch compute shader workgroups
 * and manage compute pipeline state and resource bindings.
 */
public class ComputePassEncoder extends WgpuResource {

    protected ComputePassEncoder(MemorySegment handle) {
        super(handle);
    }

    /**
     * Sets the compute pipeline to use for subsequent dispatch operations.
     *
     * @param pipeline The compute pipeline to set
     */
    public void setPipeline(ComputePipeline pipeline) {
        checkNotClosed();
        if (pipeline.isClosed()) {
            throw new WgpuException("Cannot use closed compute pipeline");
        }

        try {
            webgpu_h.wgpuComputePassEncoderSetPipeline(handle, pipeline.getHandle());
        } catch (Exception e) {
            throw new WgpuException("Failed to set compute pipeline", e);
        }
    }

    /**
     * Sets a bind group for resource bindings.
     *
     * @param groupIndex The index of the bind group layout in the pipeline
     * @param bindGroup  The bind group containing resources
     */
    public void setBindGroup(int groupIndex, BindGroup bindGroup) {
        checkNotClosed();
        if (bindGroup.isClosed()) {
            throw new WgpuException("Cannot use closed bind group");
        }

        try {
            webgpu_h.wgpuComputePassEncoderSetBindGroup(handle, groupIndex, bindGroup.getHandle(), 0, MemorySegment.NULL);
        } catch (Exception e) {
            throw new WgpuException("Failed to set bind group", e);
        }
    }

    /**
     * Sets a bind group with dynamic offsets.
     *
     * @param groupIndex     The index of the bind group layout in the pipeline
     * @param bindGroup      The bind group containing resources
     * @param dynamicOffsets Array of byte offsets for dynamic buffer bindings
     */
    public void setBindGroup(int groupIndex, BindGroup bindGroup, long[] dynamicOffsets) {
        checkNotClosed();
        if (bindGroup.isClosed()) {
            throw new WgpuException("Cannot use closed bind group");
        }

        try {
            if (dynamicOffsets == null || dynamicOffsets.length == 0) {
                setBindGroup(groupIndex, bindGroup);
                return;
            }

            webgpu_h.wgpuComputePassEncoderSetBindGroup(handle, groupIndex, bindGroup.getHandle(), 0, MemorySegment.NULL);
        } catch (Exception e) {
            throw new WgpuException("Failed to set bind group with dynamic offsets", e);
        }
    }

    /**
     * Dispatches compute workgroups.
     *
     * @param workgroupCountX Number of workgroups in the X dimension
     * @param workgroupCountY Number of workgroups in the Y dimension
     * @param workgroupCountZ Number of workgroups in the Z dimension
     */
    public void dispatchWorkgroups(int workgroupCountX, int workgroupCountY, int workgroupCountZ) {
        checkNotClosed();

        try {
            webgpu_h.wgpuComputePassEncoderDispatchWorkgroups(handle, workgroupCountX, workgroupCountY, workgroupCountZ);
        } catch (Exception e) {
            throw new WgpuException("Failed to dispatch workgroups", e);
        }
    }

    /**
     * Dispatches compute workgroups with parameters from a buffer.
     *
     * @param indirectBuffer Buffer containing dispatch parameters
     * @param indirectOffset Byte offset into the buffer
     */
    public void dispatchWorkgroupsIndirect(Buffer indirectBuffer, long indirectOffset) {
        checkNotClosed();
        if (indirectBuffer.isClosed()) {
            throw new WgpuException("Cannot use closed buffer for indirect dispatch");
        }

        try {
            webgpu_h.wgpuComputePassEncoderDispatchWorkgroupsIndirect(handle, indirectBuffer.getHandle(), indirectOffset);
        } catch (Exception e) {
            throw new WgpuException("Failed to dispatch workgroups indirectly", e);
        }
    }

    /**
     * Inserts a debug marker for profiling and debugging.
     *
     * @param markerLabel The debug marker label
     */
    public void insertDebugMarker(String markerLabel) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment labelData = arena.allocateFrom(markerLabel);
            webgpu_h.wgpuComputePassEncoderInsertDebugMarker(handle, labelData);
        } catch (Exception e) {
            throw new WgpuException("Failed to insert debug marker", e);
        }
    }

    /**
     * Pushes a debug group for hierarchical profiling.
     *
     * @param groupLabel The debug group label
     */
    public void pushDebugGroup(String groupLabel) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment labelData = arena.allocateFrom(groupLabel);
            webgpu_h.wgpuComputePassEncoderPushDebugGroup(handle, labelData);
        } catch (Exception e) {
            throw new WgpuException("Failed to push debug group", e);
        }
    }

    /**
     * Pops the current debug group.
     */
    public void popDebugGroup() {
        checkNotClosed();

        try {
            webgpu_h.wgpuComputePassEncoderPopDebugGroup(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to pop debug group", e);
        }
    }

    /**
     * Sets the debug label for this compute pass encoder.
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
            webgpu_h.wgpuComputePassEncoderSetLabel(handle, labelData);
        } catch (Exception e) {
            throw new WgpuException("Failed to set compute pass encoder label", e);
        }
    }

    /**
     * Ends the compute pass, returning control to the command encoder.
     * After calling this method, the compute pass encoder becomes invalid.
     */
    public void end() {
        checkNotClosed();

        try {
            webgpu_h.wgpuComputePassEncoderEnd(handle);
            close();
        } catch (Exception e) {
            throw new WgpuException("Failed to end compute pass", e);
        }
    }

    @Override
    protected void releaseNative() {
        webgpu_h.wgpuComputePassEncoderRelease(handle);
    }
}