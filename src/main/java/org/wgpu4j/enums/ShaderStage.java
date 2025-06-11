package org.wgpu4j.enums;

/**
 * Shader stage flags that determine which shader stages can access a binding.
 * These flags can be combined using bitwise OR operations.
 */
public class ShaderStage {
    public static final long NONE = 0x0000000000000000L;
    public static final long VERTEX = 0x0000000000000001L;
    public static final long FRAGMENT = 0x0000000000000002L;
    public static final long COMPUTE = 0x0000000000000004L;

    private ShaderStage() {
    }

    /**
     * Combines multiple shader stage flags.
     *
     * @param flags The shader stage flags to combine
     * @return The combined shader stage flags
     */
    public static long combine(long... flags) {
        long result = 0;
        for (long flag : flags) {
            result |= flag;
        }
        return result;
    }

    /**
     * Checks if a shader stage value contains a specific flag.
     *
     * @param stages The shader stage value to check
     * @param flag   The flag to check for
     * @return true if the stages contains the flag
     */
    public static boolean has(long stages, long flag) {
        return (stages & flag) != 0;
    }
}