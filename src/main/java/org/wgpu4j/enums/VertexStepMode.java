package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Vertex step mode constants for vertex buffer layouts.
 */
public final class VertexStepMode {

    private VertexStepMode() {
    }

    /**
     * Vertex buffer is not used.
     */
    public static final int VERTEX_BUFFER_NOT_USED = webgpu_h.WGPUVertexStepMode_VertexBufferNotUsed();

    /**
     * Undefined step mode.
     */
    public static final int UNDEFINED = webgpu_h.WGPUVertexStepMode_Undefined();

    /**
     * Vertex step mode - attributes advance per vertex.
     */
    public static final int VERTEX = webgpu_h.WGPUVertexStepMode_Vertex();

    /**
     * Instance step mode - attributes advance per instance.
     */
    public static final int INSTANCE = webgpu_h.WGPUVertexStepMode_Instance();
}