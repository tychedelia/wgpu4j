package org.wgpu4j.core;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.enums.IndexFormat;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Used to record rendering commands within a render pass.
 */
public class RenderPassEncoder extends WgpuResource {

    static {
        WgpuNative.ensureLoaded();
    }

    protected RenderPassEncoder(MemorySegment handle) {
        super(handle);
    }

    /**
     * Sets the render pipeline for this render pass.
     *
     * @param pipeline The render pipeline to use
     */
    public void setPipeline(RenderPipeline pipeline) {
        checkNotClosed();

        try {
            webgpu_h.wgpuRenderPassEncoderSetPipeline(handle, pipeline.getHandle());
        } catch (Exception e) {
            throw new WgpuException("Failed to set render pipeline", e);
        }
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
            webgpu_h.wgpuRenderPassEncoderSetVertexBuffer(handle, slot, buffer.getHandle(), offset, size);
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
            webgpu_h.wgpuRenderPassEncoderSetIndexBuffer(handle, buffer.getHandle(), format.getValue(), offset, size);
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
     */
    public void draw(int vertexCount, int instanceCount) {
        checkNotClosed();

        try {
            webgpu_h.wgpuRenderPassEncoderDraw(handle, vertexCount, instanceCount, 0, 0);
        } catch (Exception e) {
            throw new WgpuException("Failed to record draw command", e);
        }
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
            webgpu_h.wgpuRenderPassEncoderDrawIndexed(handle, indexCount, instanceCount, firstIndex, baseVertex, firstInstance);
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
     * Sets the viewport for rendering.
     * The viewport defines the area of the framebuffer that will be rendered to.
     *
     * @param x        X coordinate of the viewport's top-left corner
     * @param y        Y coordinate of the viewport's top-left corner
     * @param width    Width of the viewport
     * @param height   Height of the viewport
     * @param minDepth Minimum depth value (typically 0.0)
     * @param maxDepth Maximum depth value (typically 1.0)
     */
    public void setViewport(float x, float y, float width, float height, float minDepth, float maxDepth) {
        checkNotClosed();

        try {
            webgpu_h.wgpuRenderPassEncoderSetViewport(handle, x, y, width, height, minDepth, maxDepth);
        } catch (Exception e) {
            throw new WgpuException("Failed to set viewport", e);
        }
    }

    /**
     * Sets the scissor rectangle for rendering.
     * Only pixels within the scissor rectangle will be rendered.
     *
     * @param x      X coordinate of the scissor rectangle's top-left corner
     * @param y      Y coordinate of the scissor rectangle's top-left corner
     * @param width  Width of the scissor rectangle
     * @param height Height of the scissor rectangle
     */
    public void setScissorRect(int x, int y, int width, int height) {
        checkNotClosed();

        try {
            webgpu_h.wgpuRenderPassEncoderSetScissorRect(handle, x, y, width, height);
        } catch (Exception e) {
            throw new WgpuException("Failed to set scissor rectangle", e);
        }
    }

    /**
     * Sets the blend constant color used for constant blend factors.
     *
     * @param r Red component (0.0 to 1.0)
     * @param g Green component (0.0 to 1.0)
     * @param b Blue component (0.0 to 1.0)
     * @param a Alpha component (0.0 to 1.0)
     */
    public void setBlendConstant(double r, double g, double b, double a) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment color = WGPUColor.allocate(arena);
            WGPUColor.r(color, r);
            WGPUColor.g(color, g);
            WGPUColor.b(color, b);
            WGPUColor.a(color, a);

            webgpu_h.wgpuRenderPassEncoderSetBlendConstant(handle, color);
        } catch (Exception e) {
            throw new WgpuException("Failed to set blend constant", e);
        }
    }

    /**
     * Begins an occlusion query at the specified query index.
     * Occlusion queries measure how many samples pass depth/stencil tests.
     *
     * @param queryIndex Index in the query set where results will be stored
     */
    public void beginOcclusionQuery(int queryIndex) {
        checkNotClosed();

        try {
            webgpu_h.wgpuRenderPassEncoderBeginOcclusionQuery(handle, queryIndex);
        } catch (Exception e) {
            throw new WgpuException("Failed to begin occlusion query", e);
        }
    }

    /**
     * Ends the current occlusion query.
     */
    public void endOcclusionQuery() {
        checkNotClosed();

        try {
            webgpu_h.wgpuRenderPassEncoderEndOcclusionQuery(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to end occlusion query", e);
        }
    }

    /**
     * Executes the commands in the given render bundles.
     *
     * @param renderBundles Array of render bundles to execute
     */
    public void executeBundles(RenderBundle... renderBundles) {
        checkNotClosed();

        if (renderBundles == null || renderBundles.length == 0) {
            return;
        }

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment bundleArray = arena.allocate(ValueLayout.ADDRESS, renderBundles.length);
            for (int i = 0; i < renderBundles.length; i++) {
                if (renderBundles[i].isClosed()) {
                    throw new WgpuException("Cannot execute closed render bundle");
                }
                bundleArray.setAtIndex(ValueLayout.ADDRESS, i, renderBundles[i].getHandle());
            }

            webgpu_h.wgpuRenderPassEncoderExecuteBundles(handle, renderBundles.length, bundleArray);
        } catch (Exception e) {
            throw new WgpuException("Failed to execute render bundles", e);
        }
    }

    /**
     * Sets a bind group for this render pass encoder.
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

            webgpu_h.wgpuRenderPassEncoderSetBindGroup(handle, groupIndex, bindGroup.getHandle(), offsetCount, offsetsSegment);
        } catch (Exception e) {
            throw new WgpuException("Failed to set bind group", e);
        }
    }

    /**
     * Sets a bind group for this render pass encoder with no dynamic offsets.
     *
     * @param groupIndex The bind group index
     * @param bindGroup  The bind group to bind
     */
    public void setBindGroup(int groupIndex, BindGroup bindGroup) {
        setBindGroup(groupIndex, bindGroup, null);
    }

    /**
     * Ends the render pass.
     */
    public void end() {
        checkNotClosed();

        try {
            webgpu_h.wgpuRenderPassEncoderEnd(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to end render pass", e);
        }
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuRenderPassEncoderRelease(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to release render pass encoder", e);
        }
    }
}