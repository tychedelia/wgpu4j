package org.wgpu4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.descriptors.AdapterRequestOptions;
import org.wgpu4j.descriptors.InstanceDescriptor;
import org.wgpu4j.descriptors.InstanceExtras;
import org.wgpu4j.enums.InstanceBackend;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.util.concurrent.CompletableFuture;

/**
 * The main entry point for WGPU. An Instance represents a context for all WGPU operations.
 * This is a high-level, idiomatic Java wrapper around the WGPU instance.
 */
public class Instance extends WgpuResource {

    private static final Logger logger = LoggerFactory.getLogger(Instance.class);

    static {
        WgpuNative.ensureLoaded();
    }

    private Instance(MemorySegment handle) {
        super(handle);
    }

    /**
     * Creates a new WGPU instance with default settings optimized for debugging.
     * This is the starting point for all WGPU operations.
     *
     * @return A new WGPU instance
     * @throws WgpuException if instance creation fails
     */
    public static Instance create() {

        InstanceExtras extras = InstanceExtras.builder()
                .backend(InstanceBackend.PRIMARY)
                .enableDebug()
                .build();

        InstanceDescriptor descriptor = InstanceDescriptor.builder()
                .label("wgpu4j Instance")
                .extras(extras)
                .build();

        return create(descriptor);
    }

    /**
     * Creates a new WGPU instance with custom settings.
     *
     * @param descriptor Configuration for the instance
     * @return A new WGPU instance
     * @throws WgpuException if instance creation fails
     */
    public static Instance create(InstanceDescriptor descriptor) {
        try (Arena arena = Arena.ofConfined()) {
            logger.info("Creating WGPU instance with proper descriptor (following Bevy pattern)...");


            MemorySegment instanceDesc = descriptor.toCStruct(arena);
            logger.info("Instance descriptor created: {}", instanceDesc);

            MemorySegment handle = webgpu_h.wgpuCreateInstance(instanceDesc);

            if (handle.equals(MemorySegment.NULL)) {
                logger.error("Failed to create WGPU instance - wgpuCreateInstance returned NULL");
                throw new WgpuException("Failed to create WGPU instance");
            }

            logger.info("Created WGPU instance with handle: {}", handle);
            return new Instance(handle);

        } catch (Exception e) {
            logger.error("Failed to create WGPU instance", e);
            throw new WgpuException("Failed to create WGPU instance", e);
        }
    }

    /**
     * Processes any pending events for this instance.
     * This should be called regularly to handle callbacks and internal operations.
     */
    public void processEvents() {
        checkNotClosed();
        try {
            webgpu_h.wgpuInstanceProcessEvents(handle);
        } catch (Exception e) {
            logger.error("Failed to process instance events", e);
            throw new WgpuException("Failed to process instance events", e);
        }
    }

    /**
     * Requests a graphics adapter asynchronously using real WGPU callbacks.
     *
     * @param options Configuration for adapter selection
     * @return A future that will complete with an Adapter
     */
    public CompletableFuture<Adapter> requestAdapter(AdapterRequestOptions options) {
        checkNotClosed();

        CompletableFuture<Adapter> future = new CompletableFuture<>();

        Arena callbackArena = Arena.ofShared();

        try {
            WGPURequestAdapterCallback.Function callback = (status, adapter, message, userdata1, userdata2) -> {
                try {
                    if (status == 1) {
                        if (!adapter.equals(MemorySegment.NULL)) {
                            logger.info("Adapter request succeeded");
                            MemorySegment persistentHandle = MemorySegment.ofAddress(adapter.address());
                            future.complete(new Adapter(persistentHandle, callbackArena, this));
                        } else {
                            logger.error("Adapter request succeeded but returned null handle");
                            future.completeExceptionally(new WgpuException("Adapter is null despite success status"));
                            callbackArena.close();
                        }
                    } else {
                        String errorMessage = extractStringView(message);
                        logger.error("Adapter request failed: {}", errorMessage);
                        future.completeExceptionally(new WgpuException("Adapter request failed: " + errorMessage));
                        callbackArena.close();
                    }
                } catch (Exception e) {
                    logger.error("Error in adapter request callback", e);
                    future.completeExceptionally(new WgpuException("Callback error", e));
                    callbackArena.close();
                }
            };

            MemorySegment callbackStub = WGPURequestAdapterCallback.allocate(callback, callbackArena);

            MemorySegment adapterOptions = options.toCStruct(callbackArena);

            MemorySegment callbackInfo = WGPURequestAdapterCallbackInfo.allocate(callbackArena);
            WGPURequestAdapterCallbackInfo.callback(callbackInfo, callbackStub);

            MemorySegment wgpuFuture = webgpu_h.wgpuInstanceRequestAdapter(callbackArena, handle, adapterOptions, callbackInfo);

        } catch (Exception e) {
            logger.error("Exception during adapter request setup", e);
            callbackArena.close();
            future.completeExceptionally(new WgpuException("Failed to request adapter", e));
        }

        return future;
    }

    /**
     * Extracts a Java string from a WGPUStringView.
     */
    private String extractStringView(MemorySegment stringView) {
        try {
            if (stringView.equals(MemorySegment.NULL)) {
                return "Unknown error";
            }

            MemorySegment dataPtr = WGPUStringView.data(stringView);
            long length = WGPUStringView.length(stringView);

            if (dataPtr.equals(MemorySegment.NULL) || length <= 0) {
                return "Unknown error";
            }

            // Check if the data pointer can be safely read
            MemorySegment validatedPtr = dataPtr.reinterpret(length);
            return validatedPtr.getString(0, java.nio.charset.StandardCharsets.UTF_8);

        } catch (Exception e) {
            return "Error reading message: " + e.getMessage();
        }
    }

    /**
     * Requests a graphics adapter with default options.
     *
     * @return A future that will complete with an Adapter
     */
    public CompletableFuture<Adapter> requestAdapter() {
        return requestAdapter(AdapterRequestOptions.builder().build());
    }

    /**
     * Creates a surface from a platform-specific window handle.
     *
     * @param platformHandle Platform-specific window handle
     * @return A new Surface for rendering
     */
    public Surface createSurface(long platformHandle) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment surfaceDesc = WGPUSurfaceDescriptor.allocate(arena);
            WGPUSurfaceDescriptor.nextInChain(surfaceDesc, MemorySegment.NULL);

            MemorySegment surfaceHandle = webgpu_h.wgpuInstanceCreateSurface(handle, surfaceDesc);

            if (surfaceHandle.equals(MemorySegment.NULL)) {
                logger.error("Failed to create surface - wgpuInstanceCreateSurface returned NULL");
                throw new WgpuException("Failed to create surface");
            }

            return new Surface(surfaceHandle);
        } catch (Exception e) {
            logger.error("Failed to create surface", e);
            throw new WgpuException("Failed to create surface", e);
        }
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuInstanceRelease(handle);
        } catch (Exception e) {
            logger.error("Failed to release instance", e);
            throw new WgpuException("Failed to release instance", e);
        }
    }

}