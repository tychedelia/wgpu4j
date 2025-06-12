package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.*;
import org.wgpu4j.constant.TextureFormat;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for creating a render bundle encoder for recording GPU rendering commands.
 * The encoder records commands that can be replayed multiple times in different render passes.
 */
public class RenderBundleEncoderDescriptor implements Marshalable {
    private final String label;
    private final List<TextureFormat> colorFormats;
    private final TextureFormat depthStencilFormat;
    private final int sampleCount;
    private final boolean depthReadOnly;
    private final boolean stencilReadOnly;

    private RenderBundleEncoderDescriptor(String label, List<TextureFormat> colorFormats,
                                          TextureFormat depthStencilFormat, int sampleCount,
                                          boolean depthReadOnly, boolean stencilReadOnly) {
        this.label = label;
        this.colorFormats = new ArrayList<>(colorFormats);
        this.depthStencilFormat = depthStencilFormat;
        this.sampleCount = sampleCount;
        this.depthReadOnly = depthReadOnly;
        this.stencilReadOnly = stencilReadOnly;
    }

    public String getLabel() {
        return label;
    }

    public List<TextureFormat> getColorFormats() {
        return new ArrayList<>(colorFormats);
    }

    public TextureFormat getDepthStencilFormat() {
        return depthStencilFormat;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public boolean isDepthReadOnly() {
        return depthReadOnly;
    }

    public boolean isStencilReadOnly() {
        return stencilReadOnly;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPURenderBundleEncoderDescriptor struct
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPURenderBundleEncoderDescriptor.allocate(arena);

        WGPURenderBundleEncoderDescriptor.nextInChain(struct, MemorySegment.NULL);

        if (label != null && !label.isEmpty()) {
            MemorySegment labelBytes = arena.allocateFrom(label, StandardCharsets.UTF_8);
            MemorySegment labelStringView = WGPURenderBundleEncoderDescriptor.label(struct);
            WGPUStringView.data(labelStringView, labelBytes);
            WGPUStringView.length(labelStringView, label.length());
        } else {
            MemorySegment labelStringView = WGPURenderBundleEncoderDescriptor.label(struct);
            WGPUStringView.data(labelStringView, MemorySegment.NULL);
            WGPUStringView.length(labelStringView, 0);
        }

        WGPURenderBundleEncoderDescriptor.colorFormatCount(struct, colorFormats.size());
        if (!colorFormats.isEmpty()) {
            MemorySegment colorFormatsArray = arena.allocate(ValueLayout.JAVA_INT, colorFormats.size());
            for (int i = 0; i < colorFormats.size(); i++) {
                colorFormatsArray.setAtIndex(ValueLayout.JAVA_INT, i, colorFormats.get(i).getValue());
            }
            WGPURenderBundleEncoderDescriptor.colorFormats(struct, colorFormatsArray);
        } else {
            WGPURenderBundleEncoderDescriptor.colorFormats(struct, MemorySegment.NULL);
        }

        WGPURenderBundleEncoderDescriptor.depthStencilFormat(struct,
                depthStencilFormat != null ? depthStencilFormat.getValue() : TextureFormat.UNDEFINED.getValue());

        WGPURenderBundleEncoderDescriptor.sampleCount(struct, sampleCount);

        WGPURenderBundleEncoderDescriptor.depthReadOnly(struct, depthReadOnly ? 1 : 0);

        WGPURenderBundleEncoderDescriptor.stencilReadOnly(struct, stencilReadOnly ? 1 : 0);

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label = "";
        private List<TextureFormat> colorFormats = new ArrayList<>();
        private TextureFormat depthStencilFormat = null;
        private int sampleCount = 1;
        private boolean depthReadOnly = false;
        private boolean stencilReadOnly = false;

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder colorFormat(TextureFormat format) {
            this.colorFormats.add(format);
            return this;
        }

        public Builder colorFormats(List<TextureFormat> formats) {
            this.colorFormats.clear();
            this.colorFormats.addAll(formats);
            return this;
        }

        public Builder depthStencilFormat(TextureFormat format) {
            this.depthStencilFormat = format;
            return this;
        }

        public Builder sampleCount(int count) {
            if (count < 1) {
                throw new IllegalArgumentException("Sample count must be at least 1");
            }
            this.sampleCount = count;
            return this;
        }

        public Builder depthReadOnly(boolean readOnly) {
            this.depthReadOnly = readOnly;
            return this;
        }

        public Builder stencilReadOnly(boolean readOnly) {
            this.stencilReadOnly = readOnly;
            return this;
        }

        public RenderBundleEncoderDescriptor build() {
            return new RenderBundleEncoderDescriptor(label, colorFormats, depthStencilFormat,
                    sampleCount, depthReadOnly, stencilReadOnly);
        }
    }
}