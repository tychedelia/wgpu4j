package org.wgpu4j.enums;

/**
 * Optional features that can be requested when creating a device.
 */
public enum FeatureName {
    UNDEFINED(0),
    DEPTH_CLIP_CONTROL(1),
    DEPTH32_FLOAT_STENCIL8(2),
    TIMESTAMP_QUERY(3),
    TEXTURE_COMPRESSION_BC(4),
    TEXTURE_COMPRESSION_BC_SLICED_3D(5),
    TEXTURE_COMPRESSION_ETC2(6),
    TEXTURE_COMPRESSION_ASTC(7),
    TEXTURE_COMPRESSION_ASTC_SLICED_3D(8),
    INDIRECT_FIRST_INSTANCE(9),
    SHADER_F16(10),
    RG11B10_UFLOAT_RENDERABLE(11),
    BGRA8_UNORM_STORAGE(12),
    FLOAT32_FILTERABLE(13),
    FLOAT32_BLENDABLE(14),
    CLIP_DISTANCES(15),
    DUAL_SOURCE_BLENDING(16);

    private final int value;

    FeatureName(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static FeatureName fromValue(int value) {
        for (FeatureName feature : values()) {
            if (feature.value == value) {
                return feature;
            }
        }
        throw new IllegalArgumentException("Unknown FeatureName value: " + value);
    }
}