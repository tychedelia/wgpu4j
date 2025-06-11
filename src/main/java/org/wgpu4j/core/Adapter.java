package org.wgpu4j.core;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.descriptors.DeviceRequestOptions;
import org.wgpu4j.descriptors.DeviceDescriptor;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a graphics adapter (GPU).
 * An Adapter is used to create a Device for actual graphics operations.
 */
public class Adapter extends WgpuResource {

    private final Instance instance;
    private static final Arena GLOBAL_ARENA = Arena.ofShared();

    static {
        WgpuNative.ensureLoaded();
    }

    protected Adapter(MemorySegment handle) {
        super(handle);
        this.instance = null;
    }

    protected Adapter(MemorySegment handle, java.lang.foreign.Arena arena) {
        super(handle, arena);
        this.instance = null;
    }

    protected Adapter(MemorySegment handle, java.lang.foreign.Arena arena, Instance instance) {
        super(handle, arena);
        this.instance = instance;
    }

    /**
     * Gets the limits supported by this adapter.
     *
     * @return The limits supported by this adapter
     */
    public org.wgpu4j.descriptors.Limits getLimits() {
        checkNotClosed();

        MemorySegment limitsStruct = WGPULimits.allocate(GLOBAL_ARENA);
        WGPULimits.nextInChain(limitsStruct, MemorySegment.NULL);

        int status = webgpu_h.wgpuAdapterGetLimits(handle, limitsStruct);

        if (status != 1) {
            throw new WgpuException("Failed to get adapter limits, status: " + status);
        }

        return org.wgpu4j.descriptors.Limits.fromNative(limitsStruct);
    }

    /**
     * Requests a device from this adapter asynchronously using a DeviceDescriptor.
     * This is the core method that handles the mechanics of device creation.
     * <p>
     * Example usage:
     * <pre>{@code
     *      * DeviceDescriptor deviceDesc = DeviceDescriptor.builder()
     *     .label("My Custom Device")
     *     .requiredFeatures(Set.of(FeatureName.DEPTH_CLIP_CONTROL))
     *     .build();
     *
     *
     * DeviceDescriptor deviceDesc = adapter.createDeviceDescriptorWithAdapterLimits("My Device");
     *
     *
     * CompletableFuture<Device> deviceFuture = adapter.requestDevice(deviceDesc);
     * }</pre>
     *
     * @param deviceDescriptor The device descriptor specifying requirements
     * @return A future that will complete with a Device
     */
    public CompletableFuture<Device> requestDevice(DeviceDescriptor deviceDescriptor) {
        checkNotClosed();

        CompletableFuture<Device> future = new CompletableFuture<>();
        Arena callbackArena = Arena.ofShared();

        try {
            WGPURequestDeviceCallback.Function callback = (status, device, message, userdata1, userdata2) -> {
                try {
                    if (status == 1) {
                        if (!device.equals(MemorySegment.NULL)) {
                            MemorySegment persistentDeviceHandle = MemorySegment.ofAddress(device.address());
                            future.complete(new Device(persistentDeviceHandle, callbackArena));
                        } else {
                            future.completeExceptionally(new WgpuException("Device is null despite success status"));
                            callbackArena.close();
                        }
                    } else {
                        String errorMessage = extractStringView(message);
                        future.completeExceptionally(new WgpuException("Device request failed: " + errorMessage));
                        callbackArena.close();
                    }
                } catch (Exception e) {
                    future.completeExceptionally(new WgpuException("Callback error", e));
                    callbackArena.close();
                }
            };

            MemorySegment callbackStub = WGPURequestDeviceCallback.allocate(callback, callbackArena);
            MemorySegment deviceDescriptorStruct = deviceDescriptor.toCStruct(callbackArena);


            MemorySegment callbackInfo = WGPURequestDeviceCallbackInfo.allocate(callbackArena);
            WGPURequestDeviceCallbackInfo.callback(callbackInfo, callbackStub);
            WGPURequestDeviceCallbackInfo.mode(callbackInfo, webgpu_h.WGPUCallbackMode_AllowSpontaneous());

            MemorySegment wgpuFuture = webgpu_h.wgpuAdapterRequestDevice(callbackArena, handle, deviceDescriptorStruct, callbackInfo);


            if (wgpuFuture.equals(MemorySegment.NULL)) {
                throw new WgpuException("wgpuAdapterRequestDevice returned NULL future");
            }

        } catch (Exception e) {
            callbackArena.close();
            future.completeExceptionally(new WgpuException("Failed to request device", e));
        }

        return future;
    }

    /**
     * Convenience method that requests a device using DeviceRequestOptions.
     * Automatically queries adapter limits and creates a device descriptor.
     *
     * @param options Configuration for device creation
     * @return A future that will complete with a Device
     */
    public CompletableFuture<Device> requestDevice(DeviceRequestOptions options) {

        DeviceDescriptor deviceDesc = createDeviceDescriptorWithAdapterLimits(
                options != null ? options.getLabel() : "Device"
        );
        return requestDevice(deviceDesc);
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
     * Utility method to create a DeviceDescriptor using this adapter's limits.
     * This follows the Bevy pattern of querying adapter capabilities first.
     *
     * @param label The label for the device
     * @return A DeviceDescriptor configured with adapter limits
     */
    public DeviceDescriptor createDeviceDescriptorWithAdapterLimits(String label) {
        org.wgpu4j.descriptors.Limits adapterLimits = getLimits();
        return DeviceDescriptor.builder()
                .label(label != null ? label : "Device")
                .requiredLimits(adapterLimits)
                .build();
    }

    /**
     * Utility method to create a basic DeviceDescriptor with minimal requirements.
     *
     * @param label The label for the device
     * @return A basic DeviceDescriptor
     */
    public DeviceDescriptor createBasicDeviceDescriptor(String label) {
        return DeviceDescriptor.builder()
                .label(label != null ? label : "Device")
                .build();
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