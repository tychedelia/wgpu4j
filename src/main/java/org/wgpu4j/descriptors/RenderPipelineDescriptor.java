package org.wgpu4j.descriptors;

import org.wgpu4j.core.ShaderModule;
import org.wgpu4j.core.PipelineLayout;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for creating a render pipeline.
 * Supports vertex buffers, shaders, and render state configuration.
 */
public class RenderPipelineDescriptor {
    private final String label;
    private final PipelineLayout pipelineLayout;
    private final ShaderModule vertexShader;
    private final String vertexEntryPoint;
    private final ShaderModule fragmentShader;
    private final String fragmentEntryPoint;
    private final List<VertexBufferLayout> vertexBuffers;
    private final PrimitiveState primitiveState;
    private final DepthStencilState depthStencilState;
    private final MultisampleState multisampleState;
    private final List<ColorTargetState> colorTargets;

    private RenderPipelineDescriptor(Builder builder) {
        this.label = builder.label;
        this.pipelineLayout = builder.pipelineLayout;
        this.vertexShader = builder.vertexShader;
        this.vertexEntryPoint = builder.vertexEntryPoint;
        this.fragmentShader = builder.fragmentShader;
        this.fragmentEntryPoint = builder.fragmentEntryPoint;
        this.vertexBuffers = new ArrayList<>(builder.vertexBuffers);
        this.primitiveState = builder.primitiveState;
        this.depthStencilState = builder.depthStencilState;
        this.multisampleState = builder.multisampleState;
        this.colorTargets = new ArrayList<>(builder.colorTargets);
    }

    public String getLabel() {
        return label;
    }

    public PipelineLayout getPipelineLayout() {
        return pipelineLayout;
    }

    public ShaderModule getVertexShader() {
        return vertexShader;
    }

    public String getVertexEntryPoint() {
        return vertexEntryPoint;
    }

    public ShaderModule getFragmentShader() {
        return fragmentShader;
    }

    public String getFragmentEntryPoint() {
        return fragmentEntryPoint;
    }

    public List<VertexBufferLayout> getVertexBuffers() {
        return new ArrayList<>(vertexBuffers);
    }

    public PrimitiveState getPrimitiveState() {
        return primitiveState;
    }

    public DepthStencilState getDepthStencilState() {
        return depthStencilState;
    }

    public MultisampleState getMultisampleState() {
        return multisampleState;
    }

    public List<ColorTargetState> getColorTargets() {
        return new ArrayList<>(colorTargets);
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPURenderPipelineDescriptor struct
     */
    public MemorySegment toCStruct(Arena arena) {
        MemorySegment descriptor = WGPURenderPipelineDescriptor.allocate(arena);

        WGPURenderPipelineDescriptor.nextInChain(descriptor, MemorySegment.NULL);

        MemorySegment labelView = WGPUStringView.allocate(arena);
        if (label != null && !label.isEmpty()) {
            MemorySegment labelData = arena.allocateFrom(label);
            WGPUStringView.data(labelView, labelData);
            WGPUStringView.length(labelView, label.length());
        } else {
            WGPUStringView.data(labelView, MemorySegment.NULL);
            WGPUStringView.length(labelView, 0);
        }
        MemorySegment.copy(labelView, 0, descriptor, WGPURenderPipelineDescriptor.label$offset(), WGPUStringView.sizeof());

        if (pipelineLayout != null) {
            WGPURenderPipelineDescriptor.layout(descriptor, pipelineLayout.getHandle());
        } else {
            WGPURenderPipelineDescriptor.layout(descriptor, MemorySegment.NULL);
        }

        MemorySegment vertexState = WGPURenderPipelineDescriptor.vertex(descriptor);
        setupVertexState(vertexState, arena);

        MemorySegment primitiveStateStruct = primitiveState.toCStruct(arena);
        MemorySegment primitiveStateField = WGPURenderPipelineDescriptor.primitive(descriptor);
        MemorySegment.copy(primitiveStateStruct, 0, primitiveStateField, 0, WGPUPrimitiveState.sizeof());

        if (depthStencilState != null) {
            MemorySegment depthStencilPtr = depthStencilState.toCStruct(arena);
            WGPURenderPipelineDescriptor.depthStencil(descriptor, depthStencilPtr);
        } else {
            WGPURenderPipelineDescriptor.depthStencil(descriptor, MemorySegment.NULL);
        }

        MemorySegment multisampleStateStruct = multisampleState.toCStruct(arena);
        MemorySegment multisampleStateField = WGPURenderPipelineDescriptor.multisample(descriptor);
        MemorySegment.copy(multisampleStateStruct, 0, multisampleStateField, 0, WGPUMultisampleState.sizeof());

        if (fragmentShader != null) {
            MemorySegment fragmentStatePtr = arena.allocate(WGPUFragmentState.layout());
            setupFragmentState(fragmentStatePtr, arena);
            WGPURenderPipelineDescriptor.fragment(descriptor, fragmentStatePtr);
        } else {
            WGPURenderPipelineDescriptor.fragment(descriptor, MemorySegment.NULL);
        }

        return descriptor;
    }

    private void setupVertexState(MemorySegment vertexState, Arena arena) {
        WGPUVertexState.nextInChain(vertexState, MemorySegment.NULL);
        WGPUVertexState.module(vertexState, vertexShader.getHandle());

        MemorySegment entryPointView = WGPUVertexState.entryPoint(vertexState);
        MemorySegment entryPointData = arena.allocateFrom(vertexEntryPoint, StandardCharsets.UTF_8);
        WGPUStringView.data(entryPointView, entryPointData);
        WGPUStringView.length(entryPointView, vertexEntryPoint.length());

        WGPUVertexState.constantCount(vertexState, 0);
        WGPUVertexState.constants(vertexState, MemorySegment.NULL);

        WGPUVertexState.bufferCount(vertexState, vertexBuffers.size());

        if (!vertexBuffers.isEmpty()) {
            MemorySegment bufferArray = WGPUVertexBufferLayout.allocateArray(vertexBuffers.size(), arena);

            for (int i = 0; i < vertexBuffers.size(); i++) {
                MemorySegment bufferLayout = vertexBuffers.get(i).toCStruct(arena);
                MemorySegment target = WGPUVertexBufferLayout.asSlice(bufferArray, i);
                MemorySegment.copy(bufferLayout, 0, target, 0, WGPUVertexBufferLayout.sizeof());
            }

            WGPUVertexState.buffers(vertexState, bufferArray);
        } else {
            WGPUVertexState.buffers(vertexState, MemorySegment.NULL);
        }
    }

    private void setupFragmentState(MemorySegment fragmentState, Arena arena) {
        WGPUFragmentState.nextInChain(fragmentState, MemorySegment.NULL);
        WGPUFragmentState.module(fragmentState, fragmentShader.getHandle());

        MemorySegment entryPointView = WGPUFragmentState.entryPoint(fragmentState);
        MemorySegment entryPointData = arena.allocateFrom(fragmentEntryPoint, StandardCharsets.UTF_8);
        WGPUStringView.data(entryPointView, entryPointData);
        WGPUStringView.length(entryPointView, fragmentEntryPoint.length());

        WGPUFragmentState.constantCount(fragmentState, 0);
        WGPUFragmentState.constants(fragmentState, MemorySegment.NULL);

        if (!colorTargets.isEmpty()) {
            MemorySegment targetArray = WGPUColorTargetState.allocateArray(colorTargets.size(), arena);

            for (int i = 0; i < colorTargets.size(); i++) {
                MemorySegment colorTargetStruct = colorTargets.get(i).toCStruct(arena);
                MemorySegment target = WGPUColorTargetState.asSlice(targetArray, i);
                MemorySegment.copy(colorTargetStruct, 0, target, 0, WGPUColorTargetState.sizeof());
            }

            WGPUFragmentState.targetCount(fragmentState, colorTargets.size());
            WGPUFragmentState.targets(fragmentState, targetArray);
        } else {
            WGPUFragmentState.targetCount(fragmentState, 0);
            WGPUFragmentState.targets(fragmentState, MemorySegment.NULL);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label = "";
        private PipelineLayout pipelineLayout;
        private ShaderModule vertexShader;
        private String vertexEntryPoint = "vs_main";
        private ShaderModule fragmentShader;
        private String fragmentEntryPoint = "fs_main";
        private List<VertexBufferLayout> vertexBuffers = new ArrayList<>();
        private PrimitiveState primitiveState = PrimitiveState.builder().build();
        private DepthStencilState depthStencilState;
        private MultisampleState multisampleState = MultisampleState.builder().build();
        private List<ColorTargetState> colorTargets = new ArrayList<>();

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder layout(PipelineLayout layout) {
            this.pipelineLayout = layout;
            return this;
        }

        public Builder vertexShader(ShaderModule shader) {
            this.vertexShader = shader;
            return this;
        }

        public Builder vertexEntryPoint(String entryPoint) {
            this.vertexEntryPoint = entryPoint;
            return this;
        }

        public Builder fragmentShader(ShaderModule shader) {
            this.fragmentShader = shader;
            return this;
        }

        public Builder fragmentEntryPoint(String entryPoint) {
            this.fragmentEntryPoint = entryPoint;
            return this;
        }

        public Builder vertexBuffer(VertexBufferLayout buffer) {
            this.vertexBuffers.add(buffer);
            return this;
        }

        public Builder vertexBuffers(List<VertexBufferLayout> buffers) {
            this.vertexBuffers.clear();
            this.vertexBuffers.addAll(buffers);
            return this;
        }

        public Builder primitiveState(PrimitiveState primitiveState) {
            this.primitiveState = primitiveState;
            return this;
        }

        public Builder depthStencilState(DepthStencilState depthStencilState) {
            this.depthStencilState = depthStencilState;
            return this;
        }

        public Builder multisampleState(MultisampleState multisampleState) {
            this.multisampleState = multisampleState;
            return this;
        }

        public Builder colorTarget(ColorTargetState target) {
            this.colorTargets.add(target);
            return this;
        }

        public Builder colorTargets(List<ColorTargetState> targets) {
            this.colorTargets.clear();
            this.colorTargets.addAll(targets);
            return this;
        }

        public RenderPipelineDescriptor build() {
            if (vertexShader == null) {
                throw new IllegalArgumentException("Vertex shader is required");
            }
            return new RenderPipelineDescriptor(this);
        }
    }
}