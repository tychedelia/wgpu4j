package org.wgpu4j.core;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.descriptors.RenderBundleDescriptor;
import org.wgpu4j.enums.IndexFormat;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Used to record rendering commands that can be replayed later as a RenderBundle.
 * <p>
 * A RenderBundleEncoder records commands similar to a RenderPassEncoder, but the
 * recorded commands are stored in a RenderBundle that can be executed multiple times
 * across different render passes for better performance.
 */
public class RenderBundleEncoder extends WgpuResource {

    static {
        WgpuNative.ensureLoaded();
    }

    protected RenderBundleEncoder(MemorySegment handle) {
        super(handle);
    }

    /**
     * Sets the render pipeline for this render bundle encoder.
     *
     * @param pipeline The render pipeline to use
     */
    public void setPipeline(RenderPipeline pipeline) {
        checkNotClosed();

        try {
            webgpu_h.wgpuRenderBundleEncoderSetPipeline(handle, pipeline.getHandle());
        } catch (Exception e) {
            throw new WgpuException("Failed to set render pipeline", e);
        }
    }

    /**
     * Sets a bind group for this render bundle encoder.
     *
     * @param groupIndex     The bind group index
     * @param bindGroup      The bind group to bind
     * @param dynamicOffsets Optional array of dynamic offsets
     */
    public void setBindGroup(int groupIndex, BindGroup bindGroup, int[] dynamicOffsets) {
        checkNotClosed();
        if (bindGroup.isClosed()) {
            throw new WgpuException("Cannot bind closed bind group");
        }

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment offsetsSegment = MemorySegment.NULL;
            long offsetCount = 0;

            if (dynamicOffsets != null && dynamicOffsets.length > 0) {
                offsetsSegment = arena.allocate(ValueLayout.JAVA_INT, dynamicOffsets.length);
                for (int i = 0; i < dynamicOffsets.length; i++) {
                    offsetsSegment.setAtIndex(ValueLayout.JAVA_INT, i, dynamicOffsets[i]);
                }
                offsetCount = dynamicOffsets.length;
            }

            webgpu_h.wgpuRenderBundleEncoderSetBindGroup(handle, groupIndex, bindGroup.getHandle(), offsetCount, offsetsSegment);
        } catch (Exception e) {
            throw new WgpuException("Failed to set bind group", e);
        }
    }

    /**
     * Sets a bind group for this render bundle encoder with no dynamic offsets.
     *
     * @param groupIndex The bind group index
     * @param bindGroup  The bind group to bind
     */
    public void setBindGroup(int groupIndex, BindGroup bindGroup) {
        setBindGroup(groupIndex, bindGroup, null);
    }

    /**
     * Sets the vertex buffer for the given slot.
     *
     * @param slot   The vertex buffer slot index
     * @param buffer The buffer to bind
     * @param offset The offset in the buffer
     * @param size   The size of the data to bind (or 0 for whole buffer)
     */
    public void setVertexBuffer(int slot, Buffer buffer, long offset, long size) {
        checkNotClosed();
        if (buffer.isClosed()) {
            throw new WgpuException("Cannot bind closed buffer");
        }

        try {
            webgpu_h.wgpuRenderBundleEncoderSetVertexBuffer(handle, slot, buffer.getHandle(), offset, size);
        } catch (Exception e) {
            throw new WgpuException("Failed to set vertex buffer", e);
        }
    }

    /**
     * Sets the vertex buffer for the given slot, binding the entire buffer.
     *
     * @param slot   The vertex buffer slot index
     * @param buffer The buffer to bind
     */
    public void setVertexBuffer(int slot, Buffer buffer) {
        setVertexBuffer(slot, buffer, 0, buffer.getSize());
    }

    /**
     * Sets the index buffer for indexed drawing.
     *
     * @param buffer The index buffer to bind
     * @param format The format of the index data (UINT16 or UINT32)
     * @param offset The offset in the buffer
     * @param size   The size of the data to bind (or 0 for whole buffer)
     */
    public void setIndexBuffer(Buffer buffer, IndexFormat format, long offset, long size) {
        checkNotClosed();
        if (buffer.isClosed()) {
            throw new WgpuException("Cannot bind closed buffer");
        }

        try {
            webgpu_h.wgpuRenderBundleEncoderSetIndexBuffer(handle, buffer.getHandle(), format.getValue(), offset, size);
        } catch (Exception e) {
            throw new WgpuException("Failed to set index buffer", e);
        }
    }

    /**
     * Sets the index buffer for indexed drawing, binding the entire buffer.
     *
     * @param buffer The index buffer to bind
     * @param format The format of the index data (UINT16 or UINT32)
     */
    public void setIndexBuffer(Buffer buffer, IndexFormat format) {
        setIndexBuffer(buffer, format, 0, buffer.getSize());
    }

    /**
     * Records a draw command.
     *
     * @param vertexCount   Number of vertices to draw
     * @param instanceCount Number of instances to draw
     * @param firstVertex   Offset into the vertex buffer
     * @param firstInstance First instance to draw
     */
    public void draw(int vertexCount, int instanceCount, int firstVertex, int firstInstance) {
        checkNotClosed();

        try {
            webgpu_h.wgpuRenderBundleEncoderDraw(handle, vertexCount, instanceCount, firstVertex, firstInstance);
        } catch (Exception e) {
            throw new WgpuException("Failed to record draw command", e);
        }
    }

    /**
     * Records a draw command with default parameters.
     *
     * @param vertexCount   Number of vertices to draw
     * @param instanceCount Number of instances to draw
     */
    public void draw(int vertexCount, int instanceCount) {
        draw(vertexCount, instanceCount, 0, 0);
    }

    /**
     * Records an indexed draw command.
     *
     * @param indexCount    Number of indices to draw
     * @param instanceCount Number of instances to draw
     * @param firstIndex    Offset into the index buffer
     * @param baseVertex    Value added to each index before indexing into the vertex buffer
     * @param firstInstance First instance to draw
     */
    public void drawIndexed(int indexCount, int instanceCount, int firstIndex, int baseVertex, int firstInstance) {
        checkNotClosed();

        try {
            webgpu_h.wgpuRenderBundleEncoderDrawIndexed(handle, indexCount, instanceCount, firstIndex, baseVertex, firstInstance);
        } catch (Exception e) {
            throw new WgpuException("Failed to record indexed draw command", e);
        }
    }

    /**
     * Records an indexed draw command with default parameters.
     *
     * @param indexCount    Number of indices to draw
     * @param instanceCount Number of instances to draw
     */
    public void drawIndexed(int indexCount, int instanceCount) {
        drawIndexed(indexCount, instanceCount, 0, 0, 0);
    }

    /**
     * Inserts a debug marker into the command stream.
     *
     * @param markerLabel The debug marker label
     */
    public void insertDebugMarker(String markerLabel) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment labelSegment = arena.allocateFrom(markerLabel);
            webgpu_h.wgpuRenderBundleEncoderInsertDebugMarker(handle, labelSegment);
        } catch (Exception e) {
            throw new WgpuException("Failed to insert debug marker", e);
        }
    }

    /**
     * Pushes a debug group onto the debug group stack.
     *
     * @param groupLabel The debug group label
     */
    public void pushDebugGroup(String groupLabel) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment labelSegment = arena.allocateFrom(groupLabel);
            webgpu_h.wgpuRenderBundleEncoderPushDebugGroup(handle, labelSegment);
        } catch (Exception e) {
            throw new WgpuException("Failed to push debug group", e);
        }
    }

    /**
     * Pops the top debug group from the debug group stack.
     */
    public void popDebugGroup() {
        checkNotClosed();

        try {
            webgpu_h.wgpuRenderBundleEncoderPopDebugGroup(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to pop debug group", e);
        }
    }

    /**
     * Finishes the render bundle encoder and creates a RenderBundle.
     *
     * @param descriptor Configuration for the render bundle
     * @return The created RenderBundle
     */
    public RenderBundle finish(RenderBundleDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment descriptorStruct = descriptor.toCStruct(arena);
            MemorySegment bundleHandle = webgpu_h.wgpuRenderBundleEncoderFinish(handle, descriptorStruct);
            return new RenderBundle(bundleHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to finish render bundle encoder", e);
        }
    }

    /**
     * Finishes the render bundle encoder with default configuration.
     *
     * @return The created RenderBundle
     */
    public RenderBundle finish() {
        return finish(RenderBundleDescriptor.builder().build());
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuRenderBundleEncoderRelease(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to release render bundle encoder", e);
        }
    }
}