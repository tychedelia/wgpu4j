package org.wgpu4j.constant;

/**
 * Buffer mapping access modes.
 * These flags can be combined using bitwise OR operations.
 */
public class MapMode {
    public static final long NONE = 0x0000000000000000L;
    public static final long READ = 0x0000000000000001L;
    public static final long WRITE = 0x0000000000000002L;

    private MapMode() {
    }

    /**
     * Combines multiple map mode flags.
     *
     * @param flags The map mode flags to combine
     * @return The combined map mode flags
     */
    public static long combine(long... flags) {
        long result = 0;
        for (long flag : flags) {
            result |= flag;
        }
        return result;
    }

    /**
     * Checks if a map mode value contains a specific flag.
     *
     * @param mode The map mode value to check
     * @param flag The flag to check for
     * @return true if the mode contains the flag
     */
    public static boolean has(long mode, long flag) {
        return (mode & flag) != 0;
    }
}