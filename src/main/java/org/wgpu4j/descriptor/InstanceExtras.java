package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.*;
import org.wgpu4j.constant.InstanceBackend;
import org.wgpu4j.constant.InstanceFlag;

import java.lang.foreign.*;
import java.util.Set;

/**
 * Configuration for native wgpu instance extensions.
 * This configures backend selection, debug flags, and platform-specific options.
 */
public class InstanceExtras implements Marshalable {
    private final Set<InstanceBackend> backends;
    private final Set<InstanceFlag> flags;

    private InstanceExtras(Set<InstanceBackend> backends, Set<InstanceFlag> flags) {
        this.backends = backends;
        this.flags = flags;
    }

    public Set<InstanceBackend> getBackends() {
        return backends;
    }

    public Set<InstanceFlag> getFlags() {
        return flags;
    }

    /**
     * Converts this descriptor to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the WGPUInstanceExtras struct
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPUInstanceExtras.allocate(arena);


        MemorySegment chain = WGPUInstanceExtras.chain(struct);
        WGPUChainedStruct.next(chain, MemorySegment.NULL);
        WGPUChainedStruct.sType(chain, wgpu_h.WGPUSType_InstanceExtras());


        int backendFlags = 0;
        for (InstanceBackend backend : backends) {
            backendFlags |= backend.getValue();
        }
        WGPUInstanceExtras.backends(struct, backendFlags);


        int instanceFlags = 0;
        for (InstanceFlag flag : flags) {
            instanceFlags |= flag.getValue();
        }
        WGPUInstanceExtras.flags(struct, instanceFlags);


        WGPUInstanceExtras.dx12ShaderCompiler(struct, wgpu_h.WGPUDx12Compiler_Dxc());
        WGPUInstanceExtras.gles3MinorVersion(struct, wgpu_h.WGPUGles3MinorVersion_Automatic());
        WGPUInstanceExtras.glFenceBehaviour(struct, wgpu_h.WGPUGLFenceBehaviour_Normal());


        MemorySegment dxilPath = WGPUInstanceExtras.dxilPath(struct);
        WGPUStringView.data(dxilPath, MemorySegment.NULL);
        WGPUStringView.length(dxilPath, 0);

        MemorySegment dxcPath = WGPUInstanceExtras.dxcPath(struct);
        WGPUStringView.data(dxcPath, MemorySegment.NULL);
        WGPUStringView.length(dxcPath, 0);

        WGPUInstanceExtras.dxcMaxShaderModel(struct, wgpu_h.WGPUDxcMaxShaderModel_V6_7());

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Set<InstanceBackend> backends = Set.of(InstanceBackend.VULKAN, InstanceBackend.DX12);
        private Set<InstanceFlag> flags = Set.of(InstanceFlag.DEBUG, InstanceFlag.VALIDATION);

        public Builder backends(Set<InstanceBackend> backends) {
            this.backends = backends;
            return this;
        }

        public Builder backend(InstanceBackend backend) {
            this.backends = Set.of(backend);
            return this;
        }

        public Builder flags(Set<InstanceFlag> flags) {
            this.flags = flags;
            return this;
        }

        public Builder enableDebug() {
            this.flags = Set.of(InstanceFlag.DEBUG, InstanceFlag.VALIDATION);
            return this;
        }

        public Builder production() {
            this.flags = Set.of(InstanceFlag.DEFAULT);
            return this;
        }

        public InstanceExtras build() {
            return new InstanceExtras(backends, flags);
        }
    }
}