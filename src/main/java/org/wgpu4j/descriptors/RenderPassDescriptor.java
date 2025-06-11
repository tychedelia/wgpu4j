package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for a render pass.
 */
public class RenderPassDescriptor {
    private final String label;
    private final List<RenderPassColorAttachment> colorAttachments;
    private final RenderPassDepthStencilAttachment depthStencilAttachment;

    private RenderPassDescriptor(String label, List<RenderPassColorAttachment> colorAttachments,
                                 RenderPassDepthStencilAttachment depthStencilAttachment) {
        this.label = label;
        this.colorAttachments = new ArrayList<>(colorAttachments);
        this.depthStencilAttachment = depthStencilAttachment;
    }

    public String getLabel() {
        return label;
    }

    public List<RenderPassColorAttachment> getColorAttachments() {
        return new ArrayList<>(colorAttachments);
    }

    public RenderPassDepthStencilAttachment getDepthStencilAttachment() {
        return depthStencilAttachment;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPURenderPassDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment struct = WGPURenderPassDescriptor.allocate(arena);

        WGPURenderPassDescriptor.nextInChain(struct, MemorySegment.NULL);

        MemorySegment labelView = WGPUStringView.allocate(arena);
        if (label != null && !label.isEmpty()) {
            MemorySegment labelData = arena.allocateFrom(label);
            WGPUStringView.data(labelView, labelData);
            WGPUStringView.length(labelView, label.length());
        } else {
            WGPUStringView.data(labelView, MemorySegment.NULL);
            WGPUStringView.length(labelView, 0);
        }
        MemorySegment.copy(labelView, 0, struct, WGPURenderPassDescriptor.label$offset(), WGPUStringView.sizeof());

        if (!colorAttachments.isEmpty()) {
            MemorySegment colorAttachmentArray = WGPURenderPassColorAttachment.allocateArray(colorAttachments.size(), arena);

            for (int i = 0; i < colorAttachments.size(); i++) {
                MemorySegment attachmentStruct = colorAttachments.get(i).toCStruct(arena);
                MemorySegment targetSlice = WGPURenderPassColorAttachment.asSlice(colorAttachmentArray, i);
                MemorySegment.copy(attachmentStruct, 0, targetSlice, 0, WGPURenderPassColorAttachment.sizeof());
            }

            WGPURenderPassDescriptor.colorAttachmentCount(struct, colorAttachments.size());
            WGPURenderPassDescriptor.colorAttachments(struct, colorAttachmentArray);
        } else {
            WGPURenderPassDescriptor.colorAttachmentCount(struct, 0);
            WGPURenderPassDescriptor.colorAttachments(struct, MemorySegment.NULL);
        }

        if (depthStencilAttachment != null) {
            MemorySegment depthStencilStruct = depthStencilAttachment.toCStruct(arena);
            WGPURenderPassDescriptor.depthStencilAttachment(struct, depthStencilStruct);
        } else {
            WGPURenderPassDescriptor.depthStencilAttachment(struct, MemorySegment.NULL);
        }

        WGPURenderPassDescriptor.occlusionQuerySet(struct, MemorySegment.NULL);
        WGPURenderPassDescriptor.timestampWrites(struct, MemorySegment.NULL);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label;
        private List<RenderPassColorAttachment> colorAttachments = new ArrayList<>();
        private RenderPassDepthStencilAttachment depthStencilAttachment;

        /**
         * Sets the debug label for the render pass.
         */
        public Builder label(String label) {
            this.label = label;
            return this;
        }

        /**
         * Adds a color attachment to the render pass.
         */
        public Builder colorAttachment(RenderPassColorAttachment attachment) {
            this.colorAttachments.add(attachment);
            return this;
        }

        /**
         * Sets all color attachments for the render pass.
         */
        public Builder colorAttachments(List<RenderPassColorAttachment> attachments) {
            this.colorAttachments.clear();
            this.colorAttachments.addAll(attachments);
            return this;
        }

        /**
         * Sets the depth-stencil attachment for the render pass.
         */
        public Builder depthStencilAttachment(RenderPassDepthStencilAttachment attachment) {
            this.depthStencilAttachment = attachment;
            return this;
        }

        public RenderPassDescriptor build() {
            return new RenderPassDescriptor(label, colorAttachments, depthStencilAttachment);
        }
    }
}