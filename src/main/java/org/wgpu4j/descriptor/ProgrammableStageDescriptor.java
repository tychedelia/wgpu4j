package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.*;
import org.wgpu4j.resource.ShaderModule;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Configuration for a programmable shader stage (vertex, fragment, or compute).
 * Contains the shader module, entry point function name, and shader constants.
 */
public class ProgrammableStageDescriptor implements Marshalable {
    private final ShaderModule module;
    private final String entryPoint;
    private final List<ConstantEntry> constants;

    private ProgrammableStageDescriptor(ShaderModule module, String entryPoint, List<ConstantEntry> constants) {
        this.module = module;
        this.entryPoint = entryPoint;
        this.constants = new ArrayList<>(constants);
    }

    public ShaderModule getModule() {
        return module;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public List<ConstantEntry> getConstants() {
        return new ArrayList<>(constants);
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUProgrammableStageDescriptor struct
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPUProgrammableStageDescriptor.allocate(arena);

        WGPUProgrammableStageDescriptor.nextInChain(struct, MemorySegment.NULL);

        WGPUProgrammableStageDescriptor.module(struct, module.getHandle());

        if (entryPoint != null && !entryPoint.isEmpty()) {
            MemorySegment entryPointBytes = arena.allocateFrom(entryPoint, StandardCharsets.UTF_8);
            MemorySegment entryPointStringView = WGPUProgrammableStageDescriptor.entryPoint(struct);
            WGPUStringView.data(entryPointStringView, entryPointBytes);
            WGPUStringView.length(entryPointStringView, entryPoint.length());
        } else {
            MemorySegment entryPointStringView = WGPUProgrammableStageDescriptor.entryPoint(struct);
            WGPUStringView.data(entryPointStringView, MemorySegment.NULL);
            WGPUStringView.length(entryPointStringView, 0);
        }

        WGPUProgrammableStageDescriptor.constantCount(struct, constants.size());
        if (!constants.isEmpty()) {
            MemorySegment constantsArray = WGPUConstantEntry.allocateArray(constants.size(), arena);
            for (int i = 0; i < constants.size(); i++) {
                MemorySegment constantStruct = WGPUConstantEntry.asSlice(constantsArray, i);
                MemorySegment.copy(constants.get(i).marshal(arena), 0L, constantStruct, 0L, WGPUConstantEntry.sizeof());
            }
            WGPUProgrammableStageDescriptor.constants(struct, constantsArray);
        } else {
            WGPUProgrammableStageDescriptor.constants(struct, MemorySegment.NULL);
        }

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ShaderModule module;
        private String entryPoint = "main";
        private List<ConstantEntry> constants = new ArrayList<>();

        public Builder module(ShaderModule module) {
            this.module = module;
            return this;
        }

        public Builder entryPoint(String entryPoint) {
            this.entryPoint = entryPoint;
            return this;
        }

        public Builder constant(String name, double value) {
            this.constants.add(new ConstantEntry(name, value));
            return this;
        }

        public Builder constants(Map<String, Double> constants) {
            this.constants.clear();
            for (Map.Entry<String, Double> entry : constants.entrySet()) {
                this.constants.add(new ConstantEntry(entry.getKey(), entry.getValue()));
            }
            return this;
        }

        public Builder constants(List<ConstantEntry> constants) {
            this.constants.clear();
            this.constants.addAll(constants);
            return this;
        }

        public ProgrammableStageDescriptor build() {
            if (module == null) {
                throw new IllegalArgumentException("Shader module is required");
            }
            if (entryPoint == null || entryPoint.isEmpty()) {
                throw new IllegalArgumentException("Entry point is required");
            }
            return new ProgrammableStageDescriptor(module, entryPoint, constants);
        }
    }
}