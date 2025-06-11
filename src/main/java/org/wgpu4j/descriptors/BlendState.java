package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;
import org.wgpu4j.enums.BlendFactor;
import org.wgpu4j.enums.BlendOperation;

import java.lang.foreign.*;

/**
 * Configuration for blending operations in a render pipeline.
 * Defines how source and destination colors are combined.
 */
public class BlendState {
    private final BlendComponent color;
    private final BlendComponent alpha;

    private BlendState(BlendComponent color, BlendComponent alpha) {
        this.color = color;
        this.alpha = alpha;
    }

    public BlendComponent getColor() {
        return color;
    }

    public BlendComponent getAlpha() {
        return alpha;
    }

    /**
     * Converts this blend state to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUBlendState struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPUBlendState.allocate(arena);

        MemorySegment colorComponent = WGPUBlendState.color(struct);
        MemorySegment.copy(color.toCStruct(arena), 0L, colorComponent, 0L, WGPUBlendComponent.sizeof());

        MemorySegment alphaComponent = WGPUBlendState.alpha(struct);
        MemorySegment.copy(alpha.toCStruct(arena), 0L, alphaComponent, 0L, WGPUBlendComponent.sizeof());

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a blend state for alpha blending (typical transparency).
     */
    public static BlendState alphaBlending() {
        return builder()
                .color(BlendComponent.builder()
                        .srcFactor(BlendFactor.SRC_ALPHA)
                        .dstFactor(BlendFactor.ONE_MINUS_SRC_ALPHA)
                        .operation(BlendOperation.ADD)
                        .build())
                .alpha(BlendComponent.builder()
                        .srcFactor(BlendFactor.ONE)
                        .dstFactor(BlendFactor.ZERO)
                        .operation(BlendOperation.ADD)
                        .build())
                .build();
    }

    /**
     * Creates a blend state with no blending (replace destination).
     */
    public static BlendState replace() {
        BlendComponent component = BlendComponent.builder()
                .srcFactor(BlendFactor.ONE)
                .dstFactor(BlendFactor.ZERO)
                .operation(BlendOperation.ADD)
                .build();
        return new BlendState(component, component);
    }

    public static class Builder {
        private BlendComponent color = BlendComponent.builder()
                .srcFactor(BlendFactor.ONE)
                .dstFactor(BlendFactor.ZERO)
                .operation(BlendOperation.ADD)
                .build();
        private BlendComponent alpha = BlendComponent.builder()
                .srcFactor(BlendFactor.ONE)
                .dstFactor(BlendFactor.ZERO)
                .operation(BlendOperation.ADD)
                .build();

        public Builder color(BlendComponent color) {
            this.color = color;
            return this;
        }

        public Builder alpha(BlendComponent alpha) {
            this.alpha = alpha;
            return this;
        }

        public BlendState build() {
            return new BlendState(color, alpha);
        }
    }

    /**
     * Configuration for a single blend component (color or alpha).
     */
    public static class BlendComponent {
        private final BlendFactor srcFactor;
        private final BlendFactor dstFactor;
        private final BlendOperation operation;

        private BlendComponent(BlendFactor srcFactor, BlendFactor dstFactor, BlendOperation operation) {
            this.srcFactor = srcFactor;
            this.dstFactor = dstFactor;
            this.operation = operation;
        }

        public BlendFactor getSrcFactor() {
            return srcFactor;
        }

        public BlendFactor getDstFactor() {
            return dstFactor;
        }

        public BlendOperation getOperation() {
            return operation;
        }

        public MemorySegment toCStruct(Arena arena) {
            MemorySegment struct = WGPUBlendComponent.allocate(arena);

            WGPUBlendComponent.operation(struct, operation.getValue());
            WGPUBlendComponent.srcFactor(struct, srcFactor.getValue());
            WGPUBlendComponent.dstFactor(struct, dstFactor.getValue());

            return struct;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private BlendFactor srcFactor = BlendFactor.ONE;
            private BlendFactor dstFactor = BlendFactor.ZERO;
            private BlendOperation operation = BlendOperation.ADD;

            public Builder srcFactor(BlendFactor srcFactor) {
                this.srcFactor = srcFactor;
                return this;
            }

            public Builder dstFactor(BlendFactor dstFactor) {
                this.dstFactor = dstFactor;
                return this;
            }

            public Builder operation(BlendOperation operation) {
                this.operation = operation;
                return this;
            }

            public BlendComponent build() {
                return new BlendComponent(srcFactor, dstFactor, operation);
            }
        }
    }
}