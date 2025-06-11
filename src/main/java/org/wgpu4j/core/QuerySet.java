package org.wgpu4j.core;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.bindings.*;
import org.wgpu4j.enums.QueryType;

import java.lang.foreign.*;

/**
 * Represents a query set for GPU performance profiling.
 * QuerySets can measure occlusion or timestamps for performance analysis.
 */
public class QuerySet extends WgpuResource {

    static {
        WgpuNative.ensureLoaded();
    }

    private final QueryType type;
    private final int count;

    protected QuerySet(MemorySegment handle, QueryType type, int count) {
        super(handle);
        this.type = type;
        this.count = count;
    }

    /**
     * Gets the type of queries in this set.
     */
    public QueryType getType() {
        return type;
    }

    /**
     * Gets the number of queries in this set.
     */
    public int getCount() {
        return count;
    }

    /**
     * Destroys the query set, making it invalid for use.
     * This should be called when the query set is no longer needed.
     */
    public void destroy() {
        checkNotClosed();
        webgpu_h.wgpuQuerySetDestroy(handle);
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuQuerySetRelease(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to release query set", e);
        }
    }

    @Override
    public String toString() {
        return String.format("QuerySet[handle=%s, type=%s, count=%d, closed=%s]",
                handle == null ? "null" : "0x" + Long.toHexString(handle.address()),
                type, count, isClosed());
    }
}