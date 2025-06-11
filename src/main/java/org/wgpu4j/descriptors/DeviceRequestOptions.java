package org.wgpu4j.descriptors;

import org.wgpu4j.enums.FeatureName;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Options for requesting a device from an adapter.
 */
public class DeviceRequestOptions {
    private final Set<FeatureName> requiredFeatures;
    private final String label;

    private DeviceRequestOptions(Set<FeatureName> requiredFeatures, String label) {
        this.requiredFeatures = Collections.unmodifiableSet(new HashSet<>(requiredFeatures));
        this.label = label;
    }

    public Set<FeatureName> getRequiredFeatures() {
        return requiredFeatures;
    }

    public String getLabel() {
        return label;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Set<FeatureName> requiredFeatures = new HashSet<>();
        private String label;

        public Builder requiredFeature(FeatureName feature) {
            this.requiredFeatures.add(feature);
            return this;
        }

        public Builder requiredFeatures(FeatureName... features) {
            Collections.addAll(this.requiredFeatures, features);
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public DeviceRequestOptions build() {
            return new DeviceRequestOptions(requiredFeatures, label);
        }
    }
}