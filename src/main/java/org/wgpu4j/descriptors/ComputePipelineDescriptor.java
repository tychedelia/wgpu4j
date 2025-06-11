package org.wgpu4j.descriptors;

import org.wgpu4j.core.ShaderModule;
import org.wgpu4j.core.PipelineLayout;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;

/**
 * Configuration for creating a compute pipeline.
 * Compute pipelines execute compute shaders for general-purpose GPU computing.
 */
public class ComputePipelineDescriptor {
    private final String label;
    private final PipelineLayout pipelineLayout;
    private final ShaderModule computeShader;
    private final String entryPoint;

    private ComputePipelineDescriptor(Builder builder) {
        this.label = builder.label;
        this.pipelineLayout = builder.pipelineLayout;
        this.computeShader = builder.computeShader;
        this.entryPoint = builder.entryPoint;
    }

    public String getLabel() {
        return label;
    }

    public PipelineLayout getPipelineLayout() {
        return pipelineLayout;
    }

    public ShaderModule getComputeShader() {
        return computeShader;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUComputePipelineDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment descriptor = WGPUComputePipelineDescriptor.allocate(arena);

        WGPUComputePipelineDescriptor.nextInChain(descriptor, MemorySegment.NULL);

        MemorySegment labelView = WGPUStringView.allocate(arena);
        if (label != null && !label.isEmpty()) {
            MemorySegment labelData = arena.allocateFrom(label);
            WGPUStringView.data(labelView, labelData);
            WGPUStringView.length(labelView, label.length());
        } else {
            WGPUStringView.data(labelView, MemorySegment.NULL);
            WGPUStringView.length(labelView, 0);
        }
        MemorySegment.copy(labelView, 0, descriptor, WGPUComputePipelineDescriptor.label$offset(), WGPUStringView.sizeof());

        if (pipelineLayout != null) {
            WGPUComputePipelineDescriptor.layout(descriptor, pipelineLayout.getHandle());
        } else {
            WGPUComputePipelineDescriptor.layout(descriptor, MemorySegment.NULL);
        }

        MemorySegment computeStage = WGPUComputePipelineDescriptor.compute(descriptor);
        setupComputeStage(computeStage, arena);

        return descriptor;
    }

    private void setupComputeStage(MemorySegment computeStage, Arena arena) {
        WGPUProgrammableStageDescriptor.nextInChain(computeStage, MemorySegment.NULL);

        WGPUProgrammableStageDescriptor.module(computeStage, computeShader.getHandle());

        MemorySegment entryPointView = WGPUProgrammableStageDescriptor.entryPoint(computeStage);
        MemorySegment entryPointData = arena.allocateFrom(entryPoint, StandardCharsets.UTF_8);
        WGPUStringView.data(entryPointView, entryPointData);
        WGPUStringView.length(entryPointView, entryPoint.length());

        WGPUProgrammableStageDescriptor.constantCount(computeStage, 0);
        WGPUProgrammableStageDescriptor.constants(computeStage, MemorySegment.NULL);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label = "";
        private PipelineLayout pipelineLayout;
        private ShaderModule computeShader;
        private String entryPoint = "main";

        /**
         * Sets the debug label for the compute pipeline.
         *
         * @param label The debug label
         * @return this builder
         */
        public Builder label(String label) {
            this.label = label;
            return this;
        }

        /**
         * Sets the pipeline layout defining resource binding groups.
         * If not set, auto-layout will be used.
         *
         * @param pipelineLayout The pipeline layout
         * @return this builder
         */
        public Builder pipelineLayout(PipelineLayout pipelineLayout) {
            this.pipelineLayout = pipelineLayout;
            return this;
        }

        /**
         * Sets the compute shader module.
         *
         * @param computeShader The compute shader module
         * @return this builder
         */
        public Builder computeShader(ShaderModule computeShader) {
            this.computeShader = computeShader;
            return this;
        }

        /**
         * Sets the entry point function name in the compute shader.
         * Default is "main".
         *
         * @param entryPoint The entry point function name
         * @return this builder
         */
        public Builder entryPoint(String entryPoint) {
            this.entryPoint = entryPoint;
            return this;
        }

        public ComputePipelineDescriptor build() {
            if (computeShader == null) {
                throw new IllegalArgumentException("Compute shader is required");
            }
            if (entryPoint == null || entryPoint.isEmpty()) {
                throw new IllegalArgumentException("Entry point is required");
            }
            return new ComputePipelineDescriptor(this);
        }
    }
}