package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;
import org.wgpu4j.enums.FeatureName;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for creating a GPU device.
 */
public class DeviceDescriptor {
    private final String label;
    private final List<FeatureName> requiredFeatures;
    private final QueueDescriptor defaultQueue;

    private DeviceDescriptor(String label, List<FeatureName> requiredFeatures, QueueDescriptor defaultQueue) {
        this.label = label;
        this.requiredFeatures = new ArrayList<>(requiredFeatures);
        this.defaultQueue = defaultQueue;
    }

    public String getLabel() {
        return label;
    }

    public List<FeatureName> getRequiredFeatures() {
        return new ArrayList<>(requiredFeatures);
    }

    public QueueDescriptor getDefaultQueue() {
        return defaultQueue;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUDeviceDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUDeviceDescriptor.allocate(arena);

        WGPUDeviceDescriptor.nextInChain(struct, MemorySegment.NULL);

        if (label != null && !label.isEmpty()) {
            MemorySegment labelBytes = arena.allocateFrom(label, StandardCharsets.UTF_8);
            MemorySegment labelStringView = WGPUDeviceDescriptor.label(struct);
            WGPUStringView.data(labelStringView, labelBytes);
            WGPUStringView.length(labelStringView, label.length());
        } else {
            MemorySegment labelStringView = WGPUDeviceDescriptor.label(struct);
            WGPUStringView.data(labelStringView, MemorySegment.NULL);
            WGPUStringView.length(labelStringView, 0);
        }

        WGPUDeviceDescriptor.requiredFeatureCount(struct, requiredFeatures.size());
        if (!requiredFeatures.isEmpty()) {
            MemorySegment featuresArray = arena.allocate(ValueLayout.JAVA_INT, requiredFeatures.size());
            for (int i = 0; i < requiredFeatures.size(); i++) {
                featuresArray.setAtIndex(ValueLayout.JAVA_INT, i, requiredFeatures.get(i).getValue());
            }
            WGPUDeviceDescriptor.requiredFeatures(struct, featuresArray);
        } else {
            WGPUDeviceDescriptor.requiredFeatures(struct, MemorySegment.NULL);
        }

        WGPUDeviceDescriptor.requiredLimits(struct, MemorySegment.NULL);

        MemorySegment queueStruct = defaultQueue.toCStruct(arena);
        MemorySegment defaultQueueField = WGPUDeviceDescriptor.defaultQueue(struct);
        MemorySegment.copy(queueStruct, 0L, defaultQueueField, 0L, WGPUQueueDescriptor.sizeof());

        MemorySegment deviceLostCallbackInfo = WGPUDeviceDescriptor.deviceLostCallbackInfo(struct);
        deviceLostCallbackInfo.fill((byte) 0);

        MemorySegment uncapturedErrorCallbackInfo = WGPUDeviceDescriptor.uncapturedErrorCallbackInfo(struct);
        uncapturedErrorCallbackInfo.fill((byte) 0);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label = "";
        private List<FeatureName> requiredFeatures = new ArrayList<>();
        private QueueDescriptor defaultQueue = QueueDescriptor.builder().build();

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder requiredFeature(FeatureName feature) {
            this.requiredFeatures.add(feature);
            return this;
        }

        public Builder requiredFeatures(List<FeatureName> features) {
            this.requiredFeatures.clear();
            this.requiredFeatures.addAll(features);
            return this;
        }

        public Builder defaultQueue(QueueDescriptor queue) {
            this.defaultQueue = queue;
            return this;
        }

        public DeviceDescriptor build() {
            return new DeviceDescriptor(label, requiredFeatures, defaultQueue);
        }
    }
}