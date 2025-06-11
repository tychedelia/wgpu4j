package org.wgpu4j.core;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.descriptors.*;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Used to record commands that will be submitted to the GPU.
 */
public class CommandEncoder extends WgpuResource {

    static {
        WgpuNative.ensureLoaded();
    }

    protected CommandEncoder(MemorySegment handle) {
        super(handle);
    }

    /**
     * Begins a render pass.
     *
     * @param descriptor Configuration for the render pass
     * @return A render pass encoder for recording draw commands
     */
    public RenderPassEncoder beginRenderPass(RenderPassDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment renderPassDesc = descriptor.toCStruct(arena);
            MemorySegment renderPassHandle = webgpu_h.wgpuCommandEncoderBeginRenderPass(handle, renderPassDesc);

            if (renderPassHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to begin render pass");
            }

            return new RenderPassEncoder(renderPassHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to begin render pass", e);
        }
    }

    /**
     * Begins a compute pass for recording compute commands.
     *
     * @param descriptor Configuration for the compute pass
     * @return A compute pass encoder for recording compute commands
     */
    public ComputePassEncoder beginComputePass(ComputePassDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment computePassDesc = descriptor.toCStruct(arena);
            MemorySegment computePassHandle = webgpu_h.wgpuCommandEncoderBeginComputePass(handle, computePassDesc);

            if (computePassHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to begin compute pass");
            }

            return new ComputePassEncoder(computePassHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to begin compute pass", e);
        }
    }

    /**
     * Copies data from one buffer to another.
     *
     * @param source            The source buffer to copy from
     * @param sourceOffset      Byte offset into the source buffer
     * @param destination       The destination buffer to copy to
     * @param destinationOffset Byte offset into the destination buffer
     * @param size              Number of bytes to copy
     */
    public void copyBufferToBuffer(Buffer source, long sourceOffset, Buffer destination, long destinationOffset, long size) {
        checkNotClosed();

        try {
            webgpu_h.wgpuCommandEncoderCopyBufferToBuffer(
                    handle,
                    source.getHandle(),
                    sourceOffset,
                    destination.getHandle(),
                    destinationOffset,
                    size
            );
        } catch (Exception e) {
            throw new WgpuException("Failed to copy buffer to buffer", e);
        }
    }

    /**
     * Copies data from a buffer to a texture.
     *
     * @param source      Source buffer descriptor
     * @param destination Destination texture descriptor
     * @param copySize    Size of the region to copy
     */
    public void copyBufferToTexture(ImageCopyBuffer source, ImageCopyTexture destination, Extent3D copySize) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment sourceStruct = source.toCStruct(arena);
            MemorySegment destinationStruct = destination.toCStruct(arena);
            MemorySegment copySizeStruct = copySize.toCStruct(arena);

            webgpu_h.wgpuCommandEncoderCopyBufferToTexture(
                    handle,
                    sourceStruct,
                    destinationStruct,
                    copySizeStruct
            );
        } catch (Exception e) {
            throw new WgpuException("Failed to copy buffer to texture", e);
        }
    }

    /**
     * Copies data from a texture to a buffer.
     *
     * @param source      Source texture descriptor
     * @param destination Destination buffer descriptor
     * @param copySize    Size of the region to copy
     */
    public void copyTextureToBuffer(ImageCopyTexture source, ImageCopyBuffer destination, Extent3D copySize) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment sourceStruct = source.toCStruct(arena);
            MemorySegment destinationStruct = destination.toCStruct(arena);
            MemorySegment copySizeStruct = copySize.toCStruct(arena);

            webgpu_h.wgpuCommandEncoderCopyTextureToBuffer(
                    handle,
                    sourceStruct,
                    destinationStruct,
                    copySizeStruct
            );
        } catch (Exception e) {
            throw new WgpuException("Failed to copy texture to buffer", e);
        }
    }

    /**
     * Copies data from one texture to another.
     *
     * @param source      Source texture descriptor
     * @param destination Destination texture descriptor
     * @param copySize    Size of the region to copy
     */
    public void copyTextureToTexture(ImageCopyTexture source, ImageCopyTexture destination, Extent3D copySize) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment sourceStruct = source.toCStruct(arena);
            MemorySegment destinationStruct = destination.toCStruct(arena);
            MemorySegment copySizeStruct = copySize.toCStruct(arena);

            webgpu_h.wgpuCommandEncoderCopyTextureToTexture(
                    handle,
                    sourceStruct,
                    destinationStruct,
                    copySizeStruct
            );
        } catch (Exception e) {
            throw new WgpuException("Failed to copy texture to texture", e);
        }
    }

    /**
     * Resolves query results into a buffer for CPU readback.
     * This copies query results from the QuerySet into a buffer that can be mapped for reading.
     *
     * @param querySet          The query set containing the query results
     * @param firstQuery        Index of the first query to resolve
     * @param queryCount        Number of queries to resolve
     * @param destination       Buffer to write the query results to
     * @param destinationOffset Byte offset in the destination buffer
     */
    public void resolveQuerySet(QuerySet querySet, int firstQuery, int queryCount, Buffer destination, long destinationOffset) {
        checkNotClosed();

        if (querySet.isClosed() || destination.isClosed()) {
            throw new WgpuException("Cannot resolve queries to/from closed resources");
        }

        if (firstQuery < 0 || queryCount <= 0 || firstQuery + queryCount > querySet.getCount()) {
            throw new WgpuException("Invalid query range: firstQuery=" + firstQuery + ", queryCount=" + queryCount + ", totalQueries=" + querySet.getCount());
        }

        try {
            webgpu_h.wgpuCommandEncoderResolveQuerySet(
                    handle,
                    querySet.getHandle(),
                    firstQuery,
                    queryCount,
                    destination.getHandle(),
                    destinationOffset
            );
        } catch (Exception e) {
            throw new WgpuException("Failed to resolve query set", e);
        }
    }

    /**
     * Finishes recording and creates a command buffer.
     *
     * @return A command buffer ready for submission
     */
    public CommandBuffer finish() {
        checkNotClosed();

        try {
            MemorySegment commandBufferHandle = webgpu_h.wgpuCommandEncoderFinish(handle, MemorySegment.NULL);

            if (commandBufferHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to finish command encoder");
            }

            return new CommandBuffer(commandBufferHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to finish command encoder", e);
        }
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuCommandEncoderRelease(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to release command encoder", e);
        }
    }

}