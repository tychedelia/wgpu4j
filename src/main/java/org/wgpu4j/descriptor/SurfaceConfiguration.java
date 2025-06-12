package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.*;
import org.wgpu4j.resource.Device;
import org.wgpu4j.constant.*;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.List;

/**
 * Configuration for a surface, defining how it should be presented.
 */
public class SurfaceConfiguration implements Marshalable {
    private final Device device;
    private final TextureFormat format;
    private final long usage;
    private final int width;
    private final int height;
    private final List<TextureFormat> viewFormats;
    private final CompositeAlphaMode alphaMode;
    private final PresentMode presentMode;

    private SurfaceConfiguration(Builder builder) {
        this.device = builder.device;
        this.format = builder.format;
        this.usage = builder.usage;
        this.width = builder.width;
        this.height = builder.height;
        this.viewFormats = List.copyOf(builder.viewFormats);
        this.alphaMode = builder.alphaMode;
        this.presentMode = builder.presentMode;
    }

    /**
     * Creates a C struct representing this surface configuration.
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPUSurfaceConfiguration.allocate(arena);

        WGPUSurfaceConfiguration.nextInChain(struct, MemorySegment.NULL);
        WGPUSurfaceConfiguration.device(struct, device.getHandle());
        WGPUSurfaceConfiguration.format(struct, format.getValue());
        WGPUSurfaceConfiguration.usage(struct, usage);
        WGPUSurfaceConfiguration.width(struct, width);
        WGPUSurfaceConfiguration.height(struct, height);
        WGPUSurfaceConfiguration.alphaMode(struct, alphaMode.getValue());
        WGPUSurfaceConfiguration.presentMode(struct, presentMode.getValue());

        if (viewFormats.isEmpty()) {
            WGPUSurfaceConfiguration.viewFormatCount(struct, 0);
            WGPUSurfaceConfiguration.viewFormats(struct, MemorySegment.NULL);
        } else {
            MemorySegment formatsArray = arena.allocate(ValueLayout.JAVA_INT, viewFormats.size());
            for (int i = 0; i < viewFormats.size(); i++) {
                formatsArray.setAtIndex(ValueLayout.JAVA_INT, i, viewFormats.get(i).getValue());
            }
            WGPUSurfaceConfiguration.viewFormatCount(struct, viewFormats.size());
            WGPUSurfaceConfiguration.viewFormats(struct, formatsArray);
        }

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Device device;
        private TextureFormat format = TextureFormat.BGRA8_UNORM;
        private long usage = TextureUsage.RENDER_ATTACHMENT;
        private int width = 800;
        private int height = 600;
        private List<TextureFormat> viewFormats = List.of();
        private CompositeAlphaMode alphaMode = CompositeAlphaMode.AUTO;
        private PresentMode presentMode = PresentMode.FIFO;

        /**
         * Sets the device that will render to this surface.
         */
        public Builder device(Device device) {
            this.device = device;
            return this;
        }

        /**
         * Sets the texture format for the surface.
         */
        public Builder format(TextureFormat format) {
            this.format = format;
            return this;
        }

        /**
         * Sets the texture usage flags for the surface.
         */
        public Builder usage(long usage) {
            this.usage = usage;
            return this;
        }

        /**
         * Sets the surface dimensions.
         */
        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Sets the width of the surface.
         */
        public Builder width(int width) {
            this.width = width;
            return this;
        }

        /**
         * Sets the height of the surface.
         */
        public Builder height(int height) {
            this.height = height;
            return this;
        }

        /**
         * Sets additional view formats that can be used with this surface.
         */
        public Builder viewFormats(List<TextureFormat> viewFormats) {
            this.viewFormats = viewFormats;
            return this;
        }

        /**
         * Sets the alpha compositing mode.
         */
        public Builder alphaMode(CompositeAlphaMode alphaMode) {
            this.alphaMode = alphaMode;
            return this;
        }

        /**
         * Sets the present mode (VSync behavior).
         */
        public Builder presentMode(PresentMode presentMode) {
            this.presentMode = presentMode;
            return this;
        }

        public SurfaceConfiguration build() {
            if (device == null) {
                throw new IllegalStateException("Device must be specified");
            }
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Width and height must be positive");
            }
            return new SurfaceConfiguration(this);
        }
    }
}