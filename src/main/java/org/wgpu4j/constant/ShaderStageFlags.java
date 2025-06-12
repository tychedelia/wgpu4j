package org.wgpu4j.constant;

import java.util.EnumSet;

/**
 * Type-safe shader stage flags for bind group layouts.
 * Can be combined using EnumSet operations.
 */
public enum ShaderStageFlags {
    VERTEX(1L),
    FRAGMENT(2L),
    COMPUTE(4L);

    private final long value;

    ShaderStageFlags(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    /**
     * Converts an EnumSet of shader stages to a combined bit flag value.
     *
     * @param stages The set of shader stages
     * @return Combined bit flag value
     */
    public static long toBitFlags(EnumSet<ShaderStageFlags> stages) {
        return stages.stream()
                .mapToLong(ShaderStageFlags::getValue)
                .reduce(0L, (a, b) -> a | b);
    }

    /**
     * Converts a bit flag value back to an EnumSet.
     *
     * @param bitFlags Combined bit flag value
     * @return EnumSet of shader stages
     */
    public static EnumSet<ShaderStageFlags> fromBitFlags(long bitFlags) {
        EnumSet<ShaderStageFlags> result = EnumSet.noneOf(ShaderStageFlags.class);
        for (ShaderStageFlags stage : values()) {
            if ((bitFlags & stage.value) != 0) {
                result.add(stage);
            }
        }
        return result;
    }

    /**
     * Convenience method for vertex and fragment stages (common for graphics).
     *
     * @return EnumSet containing VERTEX and FRAGMENT
     */
    public static EnumSet<ShaderStageFlags> allGraphics() {
        return EnumSet.of(VERTEX, FRAGMENT);
    }

    /**
     * Convenience method for all shader stages.
     *
     * @return EnumSet containing all stages
     */
    public static EnumSet<ShaderStageFlags> all() {
        return EnumSet.allOf(ShaderStageFlags.class);
    }
}