package org.wgpu4j.descriptors;

/**
 * Configuration for creating a WGPU instance.
 */
public class InstanceDescriptor {

    private InstanceDescriptor() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public InstanceDescriptor build() {
            return new InstanceDescriptor();
        }
    }
}