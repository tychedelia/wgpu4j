package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Defines how vertices are assembled into primitives for rendering.
 */
public enum PrimitiveTopology {
    /**
     * Undefined/default primitive topology.
     */
    UNDEFINED(webgpu_h.WGPUPrimitiveTopology_Undefined()),

    /**
     * Each vertex represents a separate point.
     */
    POINT_LIST(webgpu_h.WGPUPrimitiveTopology_PointList()),

    /**
     * Every two vertices form a line segment.
     */
    LINE_LIST(webgpu_h.WGPUPrimitiveTopology_LineList()),

    /**
     * Vertices form a connected line strip.
     */
    LINE_STRIP(webgpu_h.WGPUPrimitiveTopology_LineStrip()),

    /**
     * Every three vertices form a triangle (most common for 3D graphics).
     */
    TRIANGLE_LIST(webgpu_h.WGPUPrimitiveTopology_TriangleList()),

    /**
     * Vertices form a connected triangle strip.
     */
    TRIANGLE_STRIP(webgpu_h.WGPUPrimitiveTopology_TriangleStrip());

    private final int value;

    PrimitiveTopology(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Convert from native WGPU value to enum.
     */
    public static PrimitiveTopology fromValue(int value) {
        for (PrimitiveTopology topology : values()) {
            if (topology.value == value) {
                return topology;
            }
        }
        throw new IllegalArgumentException("Unknown primitive topology value: " + value);
    }
}