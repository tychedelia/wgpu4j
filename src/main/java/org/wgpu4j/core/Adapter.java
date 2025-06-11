package org.wgpu4j.core;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.descriptors.DeviceRequestOptions;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a graphics adapter (GPU).
 * An Adapter is used to create a Device for actual graphics operations.
 */
public class Adapter extends WgpuResource {

    static {
        WgpuNative.ensureLoaded();
    }

    protected Adapter(MemorySegment handle) {
        super(handle);
    }

    /**
     * Requests a device from this adapter asynchronously using real WGPU callbacks.
     *
     * @param options Configuration for device creation
     * @return A future that will complete with a Device
     */
    public CompletableFuture<Device> requestDevice(DeviceRequestOptions options) {
        checkNotClosed();

        CompletableFuture<Device> future = new CompletableFuture<>();

        Arena callbackArena = Arena.ofShared();

        try (Arena tempArena = Arena.ofConfined()) {
            WGPURequestDeviceCallback.Function callback = (status, device, message, userdata1, userdata2) -> {
                try {
                    if (status == 1) {
                        if (!device.equals(MemorySegment.NULL)) {
                            future.complete(new Device(device));
                        } else {
                            future.completeExceptionally(new WgpuException("Device is null despite success status"));
                        }
                    } else {
                        String errorMessage = extractStringView(message);
                        future.completeExceptionally(new WgpuException("Device request failed: " + errorMessage));
                    }
                } catch (Exception e) {
                    future.completeExceptionally(new WgpuException("Callback error", e));
                } finally {
                    callbackArena.close();
                }
            };

            MemorySegment callbackStub = WGPURequestDeviceCallback.allocate(callback, callbackArena);

            MemorySegment deviceDescriptor = MemorySegment.NULL;

            MemorySegment callbackInfo = WGPURequestDeviceCallbackInfo.allocate(tempArena);
            WGPURequestDeviceCallbackInfo.callback(callbackInfo, callbackStub);

            MemorySegment wgpuFuture = webgpu_h.wgpuAdapterRequestDevice(tempArena, handle, deviceDescriptor, callbackInfo);


        } catch (Exception e) {
            callbackArena.close();
            future.completeExceptionally(new WgpuException("Failed to request device", e));
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

            if (dataPtr.equals(MemorySegment.NULL) || length == 0) {
                return "Unknown error";
            }

            return dataPtr.reinterpret(length).getString(0);

        } catch (Exception e) {
            return "Error reading message: " + e.getMessage();
        }
    }

    /**
     * Requests a device with default options.
     *
     * @return A future that will complete with a Device
     */
    public CompletableFuture<Device> requestDevice() {
        return requestDevice(DeviceRequestOptions.builder().build());
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuAdapterRelease(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to release adapter", e);
        }
    }

}