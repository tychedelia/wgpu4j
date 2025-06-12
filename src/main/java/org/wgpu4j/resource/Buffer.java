package org.wgpu4j.resource;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuResource;
import org.wgpu4j.bindings.*;
import org.wgpu4j.constant.BufferMapState;
import org.wgpu4j.constant.MapAsyncStatus;

import java.lang.foreign.*;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a GPU buffer that can store vertex data, uniform data, or other GPU-accessible data.
 * <p>
 * Buffers are created with specific usage flags that determine how they can be used in the graphics pipeline.
 * They can be mapped for CPU access, written to by the GPU, or used as vertex/index buffers.
 */
public class Buffer extends WgpuResource {

    /**
     * Creates a Buffer from a native WGPU handle.
     *
     * @param handle The native WGPUBuffer handle
     */
    public Buffer(MemorySegment handle) {
        super(handle);
    }

    /**
     * Gets the size of this buffer in bytes using the native WGPU getter.
     */
    public long getSize() {
        checkNotClosed();

        try {
            return webgpu_h.wgpuBufferGetSize(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to get buffer size", e);
        }
    }

    /**
     * Gets the usage flags for this buffer using the native WGPU getter.
     */
    public long getUsage() {
        checkNotClosed();

        try {
            return webgpu_h.wgpuBufferGetUsage(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to get buffer usage", e);
        }
    }

    /**
     * Asynchronously maps this buffer for CPU access.
     *
     * @param mode   The mapping mode (read/write permissions)
     * @param offset Byte offset into the buffer
     * @param size   Number of bytes to map
     * @return CompletableFuture that completes when mapping is done
     */
    public CompletableFuture<Void> mapAsync(long mode, long offset, long size) {
        checkNotClosed();

        CompletableFuture<Void> future = new CompletableFuture<>();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment callback = createMapCallback(arena, future);

            MemorySegment callbackInfo = WGPUBufferMapCallbackInfo.allocate(arena);
            WGPUBufferMapCallbackInfo.nextInChain(callbackInfo, MemorySegment.NULL);
            WGPUBufferMapCallbackInfo.mode(callbackInfo, webgpu_h.WGPUCallbackMode_AllowSpontaneous());
            WGPUBufferMapCallbackInfo.callback(callbackInfo, callback);
            WGPUBufferMapCallbackInfo.userdata1(callbackInfo, MemorySegment.NULL);
            WGPUBufferMapCallbackInfo.userdata2(callbackInfo, MemorySegment.NULL);

            MemorySegment futureHandle = webgpu_h.wgpuBufferMapAsync(
                    arena, handle, mode, offset, size, callbackInfo);


        } catch (Exception e) {
            future.completeExceptionally(new WgpuException("Failed to start buffer mapping", e));
        }

        return future;
    }

    /**
     * Gets a writable memory segment for the mapped buffer range.
     * The buffer must be successfully mapped before calling this method.
     *
     * @param offset Byte offset into the buffer
     * @param size   Number of bytes to access
     * @return MemorySegment for reading/writing buffer data
     */
    public MemorySegment getMappedRange(long offset, long size) {
        checkNotClosed();

        try {
            MemorySegment dataPtr = webgpu_h.wgpuBufferGetMappedRange(handle, offset, size);
            if (dataPtr.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to get mapped range - buffer may not be mapped");
            }

            return dataPtr.reinterpret(size);

        } catch (Exception e) {
            throw new WgpuException("Failed to get mapped range", e);
        }
    }

    /**
     * Gets a read-only memory segment for the mapped buffer range.
     * The buffer must be successfully mapped before calling this method.
     *
     * @param offset Byte offset into the buffer
     * @param size   Number of bytes to access
     * @return MemorySegment for reading buffer data
     */
    public MemorySegment getConstMappedRange(long offset, long size) {
        checkNotClosed();

        try {
            MemorySegment dataPtr = webgpu_h.wgpuBufferGetConstMappedRange(handle, offset, size);
            if (dataPtr.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to get const mapped range - buffer may not be mapped");
            }

            return dataPtr.reinterpret(size);

        } catch (Exception e) {
            throw new WgpuException("Failed to get const mapped range", e);
        }
    }

    /**
     * Unmaps the buffer, making it available for GPU operations again.
     * Any MemorySegments obtained from getMappedRange become invalid.
     */
    public void unmap() {
        checkNotClosed();

        try {
            webgpu_h.wgpuBufferUnmap(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to unmap buffer", e);
        }
    }

    /**
     * Gets the current mapping state of this buffer.
     *
     * @return The current BufferMapState
     */
    public BufferMapState getMapState() {
        checkNotClosed();

        try {
            int state = webgpu_h.wgpuBufferGetMapState(handle);
            return BufferMapState.fromValue(state);
        } catch (Exception e) {
            throw new WgpuException("Failed to get buffer map state", e);
        }
    }

    /**
     * Creates a native callback for buffer mapping operations.
     * This callback will complete the provided CompletableFuture.
     */
    private MemorySegment createMapCallback(Arena arena, CompletableFuture<Void> future) {
        return WGPUBufferMapCallback.allocate((status, message, userdata1, userdata2) -> {
            try {
                MapAsyncStatus mapStatus = MapAsyncStatus.fromValue(status);

                if (mapStatus == MapAsyncStatus.SUCCESS) {
                    future.complete(null);
                } else {
                    String errorMessage = "Buffer mapping failed with status: " + mapStatus;

                    if (!message.equals(MemorySegment.NULL)) {
                        try {
                            MemorySegment dataPtr = WGPUStringView.data(message);
                            long length = WGPUStringView.length(message);
                            if (!dataPtr.equals(MemorySegment.NULL) && length > 0) {
                                String msgStr = dataPtr.reinterpret(length).getString(0);
                                errorMessage += " - " + msgStr;
                            }
                        } catch (Exception e) {
                        }
                    }

                    future.completeExceptionally(new WgpuException(errorMessage));
                }
            } catch (Exception e) {
                future.completeExceptionally(new WgpuException("Error in buffer map callback", e));
            }
        }, arena);
    }

    /**
     * Destroys the buffer, making it invalid for use.
     * This should be called when the buffer is no longer needed.
     */
    public void destroy() {
        checkNotClosed();
        webgpu_h.wgpuBufferDestroy(handle);
    }

    @Override
    protected void releaseNative() {
        webgpu_h.wgpuBufferRelease(handle);
    }

    @Override
    public String toString() {
        if (isClosed()) {
            return String.format("Buffer[handle=%s, closed=true]",
                    handle == null ? "null" : "0x" + Long.toHexString(handle.address()));
        }
        try {
            return String.format("Buffer[handle=%s, size=%d, usage=0x%x, closed=%s]",
                    handle == null ? "null" : "0x" + Long.toHexString(handle.address()),
                    getSize(), getUsage(), false);
        } catch (Exception e) {
            return String.format("Buffer[handle=%s, closed=%s, error=%s]",
                    handle == null ? "null" : "0x" + Long.toHexString(handle.address()),
                    isClosed(), e.getMessage());
        }
    }
}