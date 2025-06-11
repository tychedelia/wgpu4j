package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;
import org.wgpu4j.core.TextureView;
import org.wgpu4j.enums.LoadOp;
import org.wgpu4j.enums.StoreOp;

import java.lang.foreign.*;

/**
 * Configuration for a depth-stencil attachment in a render pass.
 * Defines the depth/stencil texture and how to handle the contents.
 */
public class RenderPassDepthStencilAttachment {
    private final TextureView view;
    private final LoadOp depthLoadOp;
    private final StoreOp depthStoreOp;
    private final float depthClearValue;
    private final boolean depthReadOnly;
    private final LoadOp stencilLoadOp;
    private final StoreOp stencilStoreOp;
    private final int stencilClearValue;
    private final boolean stencilReadOnly;

    private RenderPassDepthStencilAttachment(TextureView view,
                                             LoadOp depthLoadOp, StoreOp depthStoreOp, float depthClearValue, boolean depthReadOnly,
                                             LoadOp stencilLoadOp, StoreOp stencilStoreOp, int stencilClearValue, boolean stencilReadOnly) {
        this.view = view;
        this.depthLoadOp = depthLoadOp;
        this.depthStoreOp = depthStoreOp;
        this.depthClearValue = depthClearValue;
        this.depthReadOnly = depthReadOnly;
        this.stencilLoadOp = stencilLoadOp;
        this.stencilStoreOp = stencilStoreOp;
        this.stencilClearValue = stencilClearValue;
        this.stencilReadOnly = stencilReadOnly;
    }

    public TextureView getView() {
        return view;
    }

    public LoadOp getDepthLoadOp() {
        return depthLoadOp;
    }

    public StoreOp getDepthStoreOp() {
        return depthStoreOp;
    }

    public float getDepthClearValue() {
        return depthClearValue;
    }

    public boolean isDepthReadOnly() {
        return depthReadOnly;
    }

    public LoadOp getStencilLoadOp() {
        return stencilLoadOp;
    }

    public StoreOp getStencilStoreOp() {
        return stencilStoreOp;
    }

    public int getStencilClearValue() {
        return stencilClearValue;
    }

    public boolean isStencilReadOnly() {
        return stencilReadOnly;
    }

    /**
     * Converts this attachment to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPURenderPassDepthStencilAttachment struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPURenderPassDepthStencilAttachment.allocate(arena);

        WGPURenderPassDepthStencilAttachment.view(struct, view.getHandle());
        WGPURenderPassDepthStencilAttachment.depthLoadOp(struct, depthLoadOp.getValue());
        WGPURenderPassDepthStencilAttachment.depthStoreOp(struct, depthStoreOp.getValue());
        WGPURenderPassDepthStencilAttachment.depthClearValue(struct, depthClearValue);
        WGPURenderPassDepthStencilAttachment.depthReadOnly(struct, depthReadOnly ? 1 : 0);
        WGPURenderPassDepthStencilAttachment.stencilLoadOp(struct, stencilLoadOp.getValue());
        WGPURenderPassDepthStencilAttachment.stencilStoreOp(struct, stencilStoreOp.getValue());
        WGPURenderPassDepthStencilAttachment.stencilClearValue(struct, stencilClearValue);
        WGPURenderPassDepthStencilAttachment.stencilReadOnly(struct, stencilReadOnly ? 1 : 0);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TextureView view;
        private LoadOp depthLoadOp = LoadOp.CLEAR;
        private StoreOp depthStoreOp = StoreOp.STORE;
        private float depthClearValue = 1.0f;
        private boolean depthReadOnly = false;
        private LoadOp stencilLoadOp = LoadOp.CLEAR;
        private StoreOp stencilStoreOp = StoreOp.STORE;
        private int stencilClearValue = 0;
        private boolean stencilReadOnly = false;

        /**
         * Sets the depth-stencil texture view.
         */
        public Builder view(TextureView view) {
            this.view = view;
            return this;
        }

        /**
         * Sets what happens to the depth buffer at the start of the render pass.
         */
        public Builder depthLoadOp(LoadOp depthLoadOp) {
            this.depthLoadOp = depthLoadOp;
            return this;
        }

        /**
         * Sets what happens to the depth buffer at the end of the render pass.
         */
        public Builder depthStoreOp(StoreOp depthStoreOp) {
            this.depthStoreOp = depthStoreOp;
            return this;
        }

        /**
         * Sets the clear value for the depth buffer (used when depthLoadOp is CLEAR).
         */
        public Builder depthClearValue(float depthClearValue) {
            this.depthClearValue = depthClearValue;
            return this;
        }

        /**
         * Sets whether the depth buffer is read-only during this render pass.
         */
        public Builder depthReadOnly(boolean depthReadOnly) {
            this.depthReadOnly = depthReadOnly;
            return this;
        }

        /**
         * Sets what happens to the stencil buffer at the start of the render pass.
         */
        public Builder stencilLoadOp(LoadOp stencilLoadOp) {
            this.stencilLoadOp = stencilLoadOp;
            return this;
        }

        /**
         * Sets what happens to the stencil buffer at the end of the render pass.
         */
        public Builder stencilStoreOp(StoreOp stencilStoreOp) {
            this.stencilStoreOp = stencilStoreOp;
            return this;
        }

        /**
         * Sets the clear value for the stencil buffer (used when stencilLoadOp is CLEAR).
         */
        public Builder stencilClearValue(int stencilClearValue) {
            this.stencilClearValue = stencilClearValue;
            return this;
        }

        /**
         * Sets whether the stencil buffer is read-only during this render pass.
         */
        public Builder stencilReadOnly(boolean stencilReadOnly) {
            this.stencilReadOnly = stencilReadOnly;
            return this;
        }

        public RenderPassDepthStencilAttachment build() {
            if (view == null) {
                throw new IllegalArgumentException("Texture view is required");
            }
            return new RenderPassDepthStencilAttachment(view, depthLoadOp, depthStoreOp, depthClearValue, depthReadOnly,
                    stencilLoadOp, stencilStoreOp, stencilClearValue, stencilReadOnly);
        }
    }
}