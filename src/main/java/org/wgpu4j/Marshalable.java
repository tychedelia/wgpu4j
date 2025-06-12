package org.wgpu4j;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public interface Marshalable {
    /**
     * Converts this object to a C struct using jextract layouts.
     *
     * @param arena The arena to allocate the struct in
     * @return MemorySegment representing the C struct
     */
    MemorySegment marshal(Arena arena);

}
