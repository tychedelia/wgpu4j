package org.wgpu4j.resource;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.WgpuResource;
import org.wgpu4j.bindings.*;
import org.wgpu4j.descriptor.*;

import java.lang.foreign.*;
import java.util.List;

/**
 * Command queue for submitting work to the GPU.
 */
public class Queue extends WgpuResource {

    static {
        WgpuNative.ensureLoaded();
    }

    protected Queue(MemorySegment handle) {
        super(handle);
    }

    /**
     * Submits command buffers to the GPU for execution.
     *
     * @param commandBuffers The command buffers to submit
     */
    public void submit(List<CommandBuffer> commandBuffers) {
        checkNotClosed();

        if (commandBuffers.isEmpty()) {
            return;
        }

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment commandArray = arena.allocate(ValueLayout.ADDRESS, commandBuffers.size());

            for (int i = 0; i < commandBuffers.size(); i++) {
                CommandBuffer commandBuffer = commandBuffers.get(i);
                if (commandBuffer.isClosed()) {
                    throw new WgpuException("Cannot submit closed command buffer at index " + i);
                }
                commandArray.setAtIndex(ValueLayout.ADDRESS, i, commandBuffer.getHandle());
            }

            webgpu_h.wgpuQueueSubmit(handle, commandBuffers.size(), commandArray);

        } catch (Exception e) {
            throw new WgpuException("Failed to submit command buffers", e);
        }
    }

    /**
     * Submits a single command buffer.
     *
     * @param commandBuffer The command buffer to submit
     */
    public void submit(CommandBuffer commandBuffer) {
        submit(List.of(commandBuffer));
    }

    /**
     * Writes data to a buffer on the GPU.
     *
     * @param buffer       The buffer to write to
     * @param bufferOffset The offset in the buffer to start writing at
     * @param data         The data to write
     */
    public void writeBuffer(Buffer buffer, long bufferOffset, byte[] data) {
        checkNotClosed();
        if (buffer.isClosed()) {
            throw new WgpuException("Cannot write to closed buffer");
        }

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment dataSegment = arena.allocate(data.length);
            MemorySegment.copy(data, 0, dataSegment, ValueLayout.JAVA_BYTE, 0, data.length);
            webgpu_h.wgpuQueueWriteBuffer(handle, buffer.getHandle(), bufferOffset, dataSegment, data.length);
        } catch (Exception e) {
            throw new WgpuException("Failed to write buffer", e);
        }
    }

    /**
     * Writes data to a texture on the GPU.
     *
     * @param destination The texture destination descriptor
     * @param data        The texture data to write
     * @param dataLayout  The layout of the data in memory
     * @param writeSize   The size of the region to write
     */
    public void writeTexture(ImageCopyTexture destination, byte[] data, ImageCopyTextureLayout dataLayout, Extent3D writeSize) {
        checkNotClosed();
        if (destination == null) {
            throw new WgpuException("Destination cannot be null");
        }
        if (data == null || data.length == 0) {
            throw new WgpuException("Data cannot be null or empty");
        }
        if (dataLayout == null) {
            throw new WgpuException("Data layout cannot be null");
        }
        if (writeSize == null) {
            throw new WgpuException("Write size cannot be null");
        }

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment destinationStruct = destination.marshal(arena);
            MemorySegment dataLayoutStruct = dataLayout.marshal(arena);
            MemorySegment writeSizeStruct = writeSize.marshal(arena);

            MemorySegment dataSegment = arena.allocate(data.length);
            MemorySegment.copy(data, 0, dataSegment, ValueLayout.JAVA_BYTE, 0, data.length);

            webgpu_h.wgpuQueueWriteTexture(
                    handle,
                    destinationStruct,
                    dataSegment,
                    data.length,
                    dataLayoutStruct,
                    writeSizeStruct
            );
        } catch (Exception e) {
            throw new WgpuException("Failed to write texture", e);
        }
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuQueueRelease(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to release queue", e);
        }
    }
}