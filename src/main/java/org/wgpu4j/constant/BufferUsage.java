package org.wgpu4j.constant;

/**
 * Buffer usage flags that determine how a buffer can be used in the graphics pipeline.
 * These flags can be combined using bitwise OR operations.
 */
public class BufferUsage {
    public static final long NONE = 0x0000000000000000L;
    public static final long MAP_READ = 0x0000000000000001L;
    public static final long MAP_WRITE = 0x0000000000000002L;
    public static final long COPY_SRC = 0x0000000000000004L;
    public static final long COPY_DST = 0x0000000000000008L;
    public static final long INDEX = 0x0000000000000010L;
    public static final long VERTEX = 0x0000000000000020L;
    public static final long UNIFORM = 0x0000000000000040L;
    public static final long STORAGE = 0x0000000000000080L;
    public static final long INDIRECT = 0x0000000000000100L;
    public static final long QUERY_RESOLVE = 0x0000000000000200L;

    private BufferUsage() {
    }

    /**
     * Combines multiple usage flags.
     *
     * @param flags The usage flags to combine
     * @return The combined usage flags
     */
    public static long combine(long... flags) {
        long result = 0;
        for (long flag : flags) {
            result |= flag;
        }
        return result;
    }

    /**
     * Checks if a usage value contains a specific flag.
     *
     * @param usage The usage value to check
     * @param flag  The flag to check for
     * @return true if the usage contains the flag
     */
    public static boolean has(long usage, long flag) {
        return (usage & flag) != 0;
    }
}