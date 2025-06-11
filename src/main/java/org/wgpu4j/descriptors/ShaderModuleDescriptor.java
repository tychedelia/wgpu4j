package org.wgpu4j.descriptors;

import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;

/**
 * Configuration for creating a shader module from WGSL source code.
 */
public class ShaderModuleDescriptor {
    private final String label;
    private final String wgslCode;

    private ShaderModuleDescriptor(String label, String wgslCode) {
        this.label = label;
        this.wgslCode = wgslCode;
    }

    public String getLabel() {
        return label;
    }

    public String getWgslCode() {
        return wgslCode;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUShaderModuleDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment descriptor = WGPUShaderModuleDescriptor.allocate(arena);

        MemorySegment wgslSource = WGPUShaderSourceWGSL.allocate(arena);

        MemorySegment chain = WGPUShaderSourceWGSL.chain(wgslSource);
        WGPUChainedStruct.next(chain, MemorySegment.NULL);
        WGPUChainedStruct.sType(chain, webgpu_h.WGPUSType_ShaderSourceWGSL());

        MemorySegment codeBytes = arena.allocateFrom(wgslCode, StandardCharsets.UTF_8);
        MemorySegment codeStringView = WGPUShaderSourceWGSL.code(wgslSource);
        WGPUStringView.data(codeStringView, codeBytes);
        WGPUStringView.length(codeStringView, wgslCode.length());

        WGPUShaderModuleDescriptor.nextInChain(descriptor, wgslSource);

        if (label != null && !label.isEmpty()) {
            MemorySegment labelBytes = arena.allocateFrom(label, StandardCharsets.UTF_8);
            MemorySegment labelStringView = WGPUShaderModuleDescriptor.label(descriptor);
            WGPUStringView.data(labelStringView, labelBytes);
            WGPUStringView.length(labelStringView, label.length());
        } else {
            MemorySegment labelStringView = WGPUShaderModuleDescriptor.label(descriptor);
            WGPUStringView.data(labelStringView, MemorySegment.NULL);
            WGPUStringView.length(labelStringView, 0);
        }

        return descriptor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label = "";
        private String wgslCode = "";

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder wgslCode(String wgslCode) {
            this.wgslCode = wgslCode;
            return this;
        }

        public ShaderModuleDescriptor build() {
            if (wgslCode == null || wgslCode.trim().isEmpty()) {
                throw new IllegalArgumentException("WGSL code cannot be null or empty");
            }
            return new ShaderModuleDescriptor(label, wgslCode);
        }
    }
}