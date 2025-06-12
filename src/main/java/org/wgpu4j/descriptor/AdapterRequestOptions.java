package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.constant.PowerPreference;
import org.wgpu4j.bindings.WGPURequestAdapterOptions;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Options for requesting a graphics adapter.
 */
public class AdapterRequestOptions implements Marshalable {
    private final PowerPreference powerPreference;
    private final boolean forceFallbackAdapter;

    private AdapterRequestOptions(PowerPreference powerPreference, boolean forceFallbackAdapter) {
        this.powerPreference = powerPreference;
        this.forceFallbackAdapter = forceFallbackAdapter;
    }

    public PowerPreference getPowerPreference() {
        return powerPreference;
    }

    public boolean isForceFallbackAdapter() {
        return forceFallbackAdapter;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPURequestAdapterOptions struct
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPURequestAdapterOptions.allocate(arena);

        WGPURequestAdapterOptions.nextInChain(struct, MemorySegment.NULL);

        WGPURequestAdapterOptions.featureLevel(struct, 0);

        WGPURequestAdapterOptions.powerPreference(struct, powerPreference.getValue());

        WGPURequestAdapterOptions.forceFallbackAdapter(struct, forceFallbackAdapter ? 1 : 0);

        WGPURequestAdapterOptions.backendType(struct, 0);

        WGPURequestAdapterOptions.compatibleSurface(struct, MemorySegment.NULL);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PowerPreference powerPreference = PowerPreference.UNDEFINED;
        private boolean forceFallbackAdapter = false;

        public Builder powerPreference(PowerPreference powerPreference) {
            this.powerPreference = powerPreference;
            return this;
        }

        public Builder forceFallbackAdapter(boolean force) {
            this.forceFallbackAdapter = force;
            return this;
        }

        public AdapterRequestOptions build() {
            return new AdapterRequestOptions(powerPreference, forceFallbackAdapter);
        }
    }
}