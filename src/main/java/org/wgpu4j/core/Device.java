package org.wgpu4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.descriptors.TextureDescriptor;
import org.wgpu4j.descriptors.ShaderModuleDescriptor;
import org.wgpu4j.descriptors.RenderPipelineDescriptor;
import org.wgpu4j.descriptors.ComputePipelineDescriptor;
import org.wgpu4j.descriptors.BufferDescriptor;
import org.wgpu4j.descriptors.BindGroupLayoutDescriptor;
import org.wgpu4j.descriptors.BindGroupDescriptor;
import org.wgpu4j.descriptors.SamplerDescriptor;
import org.wgpu4j.descriptors.PipelineLayoutDescriptor;
import org.wgpu4j.descriptors.QuerySetDescriptor;
import org.wgpu4j.enums.ErrorFilter;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a graphics device.
 * A Device is used to create resources like textures, buffers, and render pipelines.
 */
public class Device extends WgpuResource {

    private static final Logger logger = LoggerFactory.getLogger(Device.class);

    static {
        WgpuNative.ensureLoaded();
    }

    protected Device(MemorySegment handle) {
        super(handle);
    }
    
    protected Device(MemorySegment handle, java.lang.foreign.Arena arena) {
        super(handle, arena);
    }

    /**
     * Gets the command queue for this device.
     * The queue is used to submit commands for execution.
     *
     * @return The device's command queue
     */
    public Queue getQueue() {
        checkNotClosed();

        try {
            MemorySegment queueHandle = webgpu_h.wgpuDeviceGetQueue(handle);
            if (queueHandle.equals(MemorySegment.NULL)) {
                logger.error("Failed to get device queue - wgpuDeviceGetQueue returned NULL");
                throw new WgpuException("Failed to get device queue");
            }
            return new Queue(queueHandle);
        } catch (Exception e) {
            logger.error("Failed to get device queue", e);
            throw new WgpuException("Failed to get device queue", e);
        }
    }

    /**
     * Creates a new texture with the specified description.
     *
     * @param descriptor Configuration for the texture
     * @return A new texture
     */
    public Texture createTexture(TextureDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment textureDesc = descriptor.toCStruct(arena);

            MemorySegment textureHandle = webgpu_h.wgpuDeviceCreateTexture(handle, textureDesc);

            if (textureHandle.equals(MemorySegment.NULL)) {
                logger.error("Failed to create texture - wgpuDeviceCreateTexture returned NULL");
                throw new WgpuException("Failed to create texture");
            }

            return new Texture(textureHandle);
        } catch (Exception e) {
            logger.error("Failed to create texture", e);
            throw new WgpuException("Failed to create texture", e);
        }
    }

    /**
     * Creates a shader module from WGSL source code.
     *
     * @param descriptor Configuration for the shader module including WGSL code
     * @return A new shader module
     */
    public ShaderModule createShaderModule(ShaderModuleDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment shaderDesc = descriptor.toCStruct(arena);

            MemorySegment shaderHandle = webgpu_h.wgpuDeviceCreateShaderModule(handle, shaderDesc);

            if (shaderHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create shader module");
            }

            return new ShaderModule(shaderHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to create shader module", e);
        }
    }

    /**
     * Creates a render pipeline from the specified descriptor.
     *
     * @param descriptor Configuration for the render pipeline
     * @return A new render pipeline
     */
    public RenderPipeline createRenderPipeline(RenderPipelineDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment pipelineDesc = descriptor.toCStruct(arena);

            MemorySegment pipelineHandle = webgpu_h.wgpuDeviceCreateRenderPipeline(handle, pipelineDesc);

            if (pipelineHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create render pipeline");
            }

            return new RenderPipeline(pipelineHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to create render pipeline", e);
        }
    }

    /**
     * Creates a compute pipeline with the specified descriptor.
     *
     * @param descriptor Configuration for the compute pipeline
     * @return A new compute pipeline
     */
    public ComputePipeline createComputePipeline(ComputePipelineDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment pipelineDesc = descriptor.toCStruct(arena);

            MemorySegment pipelineHandle = webgpu_h.wgpuDeviceCreateComputePipeline(handle, pipelineDesc);

            if (pipelineHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create compute pipeline");
            }

            return new ComputePipeline(pipelineHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to create compute pipeline", e);
        }
    }

    /**
     * Creates a buffer with the specified descriptor.
     *
     * @param descriptor Configuration for the buffer
     * @return A new buffer
     */
    public Buffer createBuffer(BufferDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment bufferDesc = descriptor.toCStruct(arena);

            MemorySegment bufferHandle = webgpu_h.wgpuDeviceCreateBuffer(handle, bufferDesc);

            if (bufferHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create buffer");
            }

            return new Buffer(bufferHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to create buffer", e);
        }
    }

    /**
     * Creates a bind group layout with the specified descriptor.
     *
     * @param descriptor Configuration for the bind group layout
     * @return A new bind group layout
     */
    public BindGroupLayout createBindGroupLayout(BindGroupLayoutDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment layoutDesc = descriptor.toCStruct(arena);

            MemorySegment layoutHandle = webgpu_h.wgpuDeviceCreateBindGroupLayout(handle, layoutDesc);

            if (layoutHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create bind group layout");
            }

            return new BindGroupLayout(layoutHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to create bind group layout", e);
        }
    }

    /**
     * Creates a bind group with the specified descriptor.
     *
     * @param descriptor Configuration for the bind group
     * @return A new bind group
     */
    public BindGroup createBindGroup(BindGroupDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment bindGroupDesc = descriptor.toCStruct(arena);

            MemorySegment bindGroupHandle = webgpu_h.wgpuDeviceCreateBindGroup(handle, bindGroupDesc);

            if (bindGroupHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create bind group");
            }

            return new BindGroup(bindGroupHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to create bind group", e);
        }
    }

    /**
     * Creates a sampler with the specified descriptor.
     *
     * @param descriptor Configuration for the sampler
     * @return A new sampler
     */
    public Sampler createSampler(SamplerDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment samplerDesc = descriptor.toCStruct(arena);

            MemorySegment samplerHandle = webgpu_h.wgpuDeviceCreateSampler(handle, samplerDesc);

            if (samplerHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create sampler");
            }

            return new Sampler(samplerHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to create sampler", e);
        }
    }

    /**
     * Creates a pipeline layout with the specified descriptor.
     *
     * @param descriptor Configuration for the pipeline layout
     * @return A new pipeline layout
     */
    public PipelineLayout createPipelineLayout(PipelineLayoutDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment layoutDesc = descriptor.toCStruct(arena);

            MemorySegment layoutHandle = webgpu_h.wgpuDeviceCreatePipelineLayout(handle, layoutDesc);

            if (layoutHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create pipeline layout");
            }

            return new PipelineLayout(layoutHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to create pipeline layout", e);
        }
    }

    /**
     * Creates a query set for GPU performance profiling.
     *
     * @param descriptor Configuration for the query set
     * @return A new query set
     */
    public QuerySet createQuerySet(QuerySetDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment querySetDesc = descriptor.toCStruct(arena);

            MemorySegment querySetHandle = webgpu_h.wgpuDeviceCreateQuerySet(handle, querySetDesc);

            if (querySetHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create query set");
            }

            return new QuerySet(querySetHandle, descriptor.getType(), descriptor.getCount());
        } catch (Exception e) {
            throw new WgpuException("Failed to create query set", e);
        }
    }

    /**
     * Creates a command encoder for recording commands.
     *
     * @return A new command encoder
     */
    public CommandEncoder createCommandEncoder() {
        checkNotClosed();

        try {
            MemorySegment encoderHandle = webgpu_h.wgpuDeviceCreateCommandEncoder(handle, MemorySegment.NULL);
            if (encoderHandle.equals(MemorySegment.NULL)) {
                logger.error("Failed to create command encoder - wgpuDeviceCreateCommandEncoder returned NULL");
                throw new WgpuException("Failed to create command encoder");
            }
            return new CommandEncoder(encoderHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to create command encoder", e);
        }
    }

    /**
     * Pushes an error scope onto the device's error scope stack.
     * This allows you to capture errors that occur during subsequent operations.
     *
     * @param filter The type of errors to capture in this scope
     */
    public void pushErrorScope(ErrorFilter filter) {
        checkNotClosed();

        try {
            webgpu_h.wgpuDevicePushErrorScope(handle, filter.getValue());
        } catch (Exception e) {
            throw new WgpuException("Failed to push error scope", e);
        }
    }

    /**
     * Pops an error scope from the device's error scope stack.
     * Returns a CompletableFuture that will complete with any error that occurred
     * within the scope, or null if no error occurred.
     *
     * @return CompletableFuture that completes with error information or null
     */
    public CompletableFuture<String> popErrorScope() {
        checkNotClosed();

        CompletableFuture<String> future = new CompletableFuture<>();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment callback = createErrorScopeCallback(arena, future);

            MemorySegment callbackInfo = WGPUPopErrorScopeCallbackInfo.allocate(arena);
            WGPUPopErrorScopeCallbackInfo.nextInChain(callbackInfo, MemorySegment.NULL);
            WGPUPopErrorScopeCallbackInfo.mode(callbackInfo, webgpu_h.WGPUCallbackMode_AllowSpontaneous());
            WGPUPopErrorScopeCallbackInfo.callback(callbackInfo, callback);
            WGPUPopErrorScopeCallbackInfo.userdata1(callbackInfo, MemorySegment.NULL);
            WGPUPopErrorScopeCallbackInfo.userdata2(callbackInfo, MemorySegment.NULL);

            MemorySegment futureHandle = webgpu_h.wgpuDevicePopErrorScope(arena, handle, callbackInfo);


        } catch (Exception e) {
            future.completeExceptionally(new WgpuException("Failed to pop error scope", e));
        }

        return future;
    }

    /**
     * Gets a CompletableFuture that will complete when the device is lost.
     * The future will complete with the reason for the device loss.
     *
     * @return CompletableFuture that completes when device is lost
     */
    public CompletableFuture<String> getLostFuture() {
        checkNotClosed();

        CompletableFuture<String> future = new CompletableFuture<>();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment futureHandle = webgpu_h.wgpuDeviceGetLostFuture(arena, handle);


        } catch (Exception e) {
            future.completeExceptionally(new WgpuException("Failed to get device lost future", e));
        }

        return future;
    }

    /**
     * Creates a render bundle encoder with the specified descriptor.
     *
     * @param descriptor Configuration for the render bundle encoder
     * @return A new render bundle encoder
     */
    public RenderBundleEncoder createRenderBundleEncoder(org.wgpu4j.descriptors.RenderBundleEncoderDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment encoderDesc = descriptor.toCStruct(arena);

            MemorySegment encoderHandle = webgpu_h.wgpuDeviceCreateRenderBundleEncoder(handle, encoderDesc);

            if (encoderHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create render bundle encoder");
            }

            return new RenderBundleEncoder(encoderHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to create render bundle encoder", e);
        }
    }

    /**
     * Creates a native callback for error scope operations.
     */
    private MemorySegment createErrorScopeCallback(Arena arena, CompletableFuture<String> future) {
        return WGPUPopErrorScopeCallback.allocate((status, type, message, userdata1, userdata2) -> {
            try {
                if (type == webgpu_h.WGPUErrorType_NoError()) {
                    future.complete(null);
                } else {
                    String errorMessage = "Error occurred";

                    if (!message.equals(MemorySegment.NULL)) {
                        try {
                            MemorySegment dataPtr = WGPUStringView.data(message);
                            long length = WGPUStringView.length(message);
                            if (!dataPtr.equals(MemorySegment.NULL) && length > 0) {
                                errorMessage = dataPtr.reinterpret(length).getString(0);
                            }
                        } catch (Exception e) {
                        }
                    }

                    future.complete(errorMessage);
                }
            } catch (Exception e) {
                future.completeExceptionally(new WgpuException("Error in error scope callback", e));
            }
        }, arena);
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuDeviceRelease(handle);
        } catch (Exception e) {
            logger.error("Failed to release device", e);
            throw new WgpuException("Failed to release device", e);
        }
    }

}