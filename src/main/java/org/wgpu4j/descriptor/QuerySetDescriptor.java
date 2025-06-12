package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.*;
import org.wgpu4j.constant.QueryType;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Descriptor for creating a query set for GPU performance profiling.
 */
public class QuerySetDescriptor implements Marshalable {
    private final String label;
    private final QueryType type;
    private final int count;

    private QuerySetDescriptor(Builder builder) {
        this.label = builder.label;
        this.type = builder.type;
        this.count = builder.count;
    }

    /**
     * Creates a C struct representing this query set descriptor.
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPUQuerySetDescriptor.allocate(arena);

        WGPUQuerySetDescriptor.nextInChain(struct, MemorySegment.NULL);
        WGPUQuerySetDescriptor.type(struct, type.getValue());
        WGPUQuerySetDescriptor.count(struct, count);

        if (label != null && !label.isEmpty()) {
            MemorySegment labelStruct = WGPUStringView.allocate(arena);
            MemorySegment labelData = arena.allocateFrom(label);
            WGPUStringView.data(labelStruct, labelData);
            WGPUStringView.length(labelStruct, label.length());
            WGPUQuerySetDescriptor.label(struct, labelStruct);
        } else {
            MemorySegment labelStruct = WGPUStringView.allocate(arena);
            WGPUStringView.data(labelStruct, MemorySegment.NULL);
            WGPUStringView.length(labelStruct, 0);
            WGPUQuerySetDescriptor.label(struct, labelStruct);
        }

        return struct;
    }

    /**
     * Gets the query type for this descriptor.
     */
    public QueryType getType() {
        return type;
    }

    /**
     * Gets the query count for this descriptor.
     */
    public int getCount() {
        return count;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label;
        private QueryType type = QueryType.TIMESTAMP;
        private int count = 1;

        /**
         * Sets the debug label for this query set.
         */
        public Builder label(String label) {
            this.label = label;
            return this;
        }

        /**
         * Sets the type of queries this set will contain.
         */
        public Builder type(QueryType type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the number of queries in this set.
         */
        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public QuerySetDescriptor build() {
            if (count <= 0) {
                throw new IllegalArgumentException("Query count must be positive");
            }
            return new QuerySetDescriptor(this);
        }
    }
}