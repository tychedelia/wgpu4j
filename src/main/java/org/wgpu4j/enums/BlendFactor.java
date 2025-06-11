package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Blend factors used in blending operations.
 * Determines how source and destination colors are combined.
 */
public enum BlendFactor {
    UNDEFINED(webgpu_h.WGPUBlendFactor_Undefined()),
    ZERO(webgpu_h.WGPUBlendFactor_Zero()),
    ONE(webgpu_h.WGPUBlendFactor_One()),
    SRC(webgpu_h.WGPUBlendFactor_Src()),
    ONE_MINUS_SRC(webgpu_h.WGPUBlendFactor_OneMinusSrc()),
    SRC_ALPHA(webgpu_h.WGPUBlendFactor_SrcAlpha()),
    ONE_MINUS_SRC_ALPHA(webgpu_h.WGPUBlendFactor_OneMinusSrcAlpha()),
    DST(webgpu_h.WGPUBlendFactor_Dst()),
    ONE_MINUS_DST(webgpu_h.WGPUBlendFactor_OneMinusDst()),
    DST_ALPHA(webgpu_h.WGPUBlendFactor_DstAlpha()),
    ONE_MINUS_DST_ALPHA(webgpu_h.WGPUBlendFactor_OneMinusDstAlpha()),
    SRC_ALPHA_SATURATED(webgpu_h.WGPUBlendFactor_SrcAlphaSaturated()),
    CONSTANT(webgpu_h.WGPUBlendFactor_Constant()),
    ONE_MINUS_CONSTANT(webgpu_h.WGPUBlendFactor_OneMinusConstant()),
    SRC1(webgpu_h.WGPUBlendFactor_Src1()),
    ONE_MINUS_SRC1(webgpu_h.WGPUBlendFactor_OneMinusSrc1()),
    SRC1_ALPHA(webgpu_h.WGPUBlendFactor_Src1Alpha()),
    ONE_MINUS_SRC1_ALPHA(webgpu_h.WGPUBlendFactor_OneMinusSrc1Alpha());

    private final int value;

    BlendFactor(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BlendFactor fromValue(int value) {
        for (BlendFactor factor : values()) {
            if (factor.value == value) {
                return factor;
            }
        }
        throw new IllegalArgumentException("Unknown BlendFactor value: " + value);
    }
}