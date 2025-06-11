package org.wgpu4j.enums;

/**
 * Texture formats supported by WGPU.
 */
public enum TextureFormat {
    UNDEFINED(0),

    R8_UNORM(1),
    R8_SNORM(2),
    R8_UINT(3),
    R8_SINT(4),

    R16_UINT(5),
    R16_SINT(6),
    R16_FLOAT(7),
    RG8_UNORM(8),
    RG8_SNORM(9),
    RG8_UINT(10),
    RG8_SINT(11),

    R32_FLOAT(12),
    R32_UINT(13),
    R32_SINT(14),
    RG16_UINT(15),
    RG16_SINT(16),
    RG16_FLOAT(17),
    RGBA8_UNORM(18),
    RGBA8_UNORM_SRGB(19),
    RGBA8_SNORM(20),
    RGBA8_UINT(21),
    RGBA8_SINT(22),
    BGRA8_UNORM(23),
    BGRA8_UNORM_SRGB(24),

    RGB10A2_UINT(25),
    RGB10A2_UNORM(26),
    RG11B10_UFLOAT(27),
    RGB9E5_UFLOAT(28),

    RG32_FLOAT(29),
    RG32_UINT(30),
    RG32_SINT(31),
    RGBA16_UINT(32),
    RGBA16_SINT(33),
    RGBA16_FLOAT(34),

    RGBA32_FLOAT(35),
    RGBA32_UINT(36),
    RGBA32_SINT(37),

    STENCIL8(38),
    DEPTH16_UNORM(39),
    DEPTH24_PLUS(40),
    DEPTH24_PLUS_STENCIL8(41),
    DEPTH32_FLOAT(42),
    DEPTH32_FLOAT_STENCIL8(43);

    private final int value;

    TextureFormat(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TextureFormat fromValue(int value) {
        for (TextureFormat format : values()) {
            if (format.value == value) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown TextureFormat value: " + value);
    }
}