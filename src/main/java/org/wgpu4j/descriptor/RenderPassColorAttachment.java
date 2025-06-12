package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.*;
import org.wgpu4j.resource.TextureView;
import org.wgpu4j.constant.LoadOp;
import org.wgpu4j.constant.StoreOp;

import java.lang.foreign.*;

/**
 * Configuration for a color attachment in a render pass.
 * Defines what texture to render to and how to handle the contents.
 */
public class RenderPassColorAttachment implements Marshalable {
    private final TextureView view;
    private final TextureView resolveTarget;
    private final LoadOp loadOp;
    private final StoreOp storeOp;
    private final double clearR;
    private final double clearG;
    private final double clearB;
    private final double clearA;

    private RenderPassColorAttachment(TextureView view, TextureView resolveTarget,
                                      LoadOp loadOp, StoreOp storeOp,
                                      double clearR, double clearG, double clearB, double clearA) {
        this.view = view;
        this.resolveTarget = resolveTarget;
        this.loadOp = loadOp;
        this.storeOp = storeOp;
        this.clearR = clearR;
        this.clearG = clearG;
        this.clearB = clearB;
        this.clearA = clearA;
    }

    public TextureView getView() {
        return view;
    }

    public TextureView getResolveTarget() {
        return resolveTarget;
    }

    public LoadOp getLoadOp() {
        return loadOp;
    }

    public StoreOp getStoreOp() {
        return storeOp;
    }

    public double getClearR() {
        return clearR;
    }

    public double getClearG() {
        return clearG;
    }

    public double getClearB() {
        return clearB;
    }

    public double getClearA() {
        return clearA;
    }

    /**
     * Converts this attachment to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPURenderPassColorAttachment struct
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPURenderPassColorAttachment.allocate(arena);

        WGPURenderPassColorAttachment.nextInChain(struct, MemorySegment.NULL);
        WGPURenderPassColorAttachment.view(struct, view.getHandle());

        if (resolveTarget != null) {
            WGPURenderPassColorAttachment.resolveTarget(struct, resolveTarget.getHandle());
        } else {
            WGPURenderPassColorAttachment.resolveTarget(struct, MemorySegment.NULL);
        }

        WGPURenderPassColorAttachment.loadOp(struct, loadOp.getValue());
        WGPURenderPassColorAttachment.storeOp(struct, storeOp.getValue());

        MemorySegment clearValue = WGPURenderPassColorAttachment.clearValue(struct);
        WGPUColor.r(clearValue, clearR);
        WGPUColor.g(clearValue, clearG);
        WGPUColor.b(clearValue, clearB);
        WGPUColor.a(clearValue, clearA);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TextureView view;
        private TextureView resolveTarget = null;
        private LoadOp loadOp = LoadOp.CLEAR;
        private StoreOp storeOp = StoreOp.STORE;
        private double clearR = 0.0;
        private double clearG = 0.0;
        private double clearB = 0.0;
        private double clearA = 1.0;

        /**
         * Sets the texture view to render to.
         */
        public Builder view(TextureView view) {
            this.view = view;
            return this;
        }

        /**
         * Sets the resolve target for multisampled attachments.
         */
        public Builder resolveTarget(TextureView resolveTarget) {
            this.resolveTarget = resolveTarget;
            return this;
        }

        /**
         * Sets what happens to the attachment at the start of the render pass.
         */
        public Builder loadOp(LoadOp loadOp) {
            this.loadOp = loadOp;
            return this;
        }

        /**
         * Sets what happens to the attachment at the end of the render pass.
         */
        public Builder storeOp(StoreOp storeOp) {
            this.storeOp = storeOp;
            return this;
        }

        /**
         * Sets the clear color (used when loadOp is CLEAR).
         */
        public Builder clearColor(double r, double g, double b, double a) {
            this.clearR = r;
            this.clearG = g;
            this.clearB = b;
            this.clearA = a;
            return this;
        }

        /**
         * Sets the clear color to black with full alpha.
         */
        public Builder clearBlack() {
            return clearColor(0.0, 0.0, 0.0, 1.0);
        }

        /**
         * Sets the clear color to white with full alpha.
         */
        public Builder clearWhite() {
            return clearColor(1.0, 1.0, 1.0, 1.0);
        }

        public RenderPassColorAttachment build() {
            if (view == null) {
                throw new IllegalArgumentException("Texture view is required");
            }
            return new RenderPassColorAttachment(view, resolveTarget, loadOp, storeOp,
                    clearR, clearG, clearB, clearA);
        }
    }
}