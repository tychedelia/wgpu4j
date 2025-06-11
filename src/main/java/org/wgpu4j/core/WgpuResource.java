package org.wgpu4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.MemorySegment;

/**
 * Base class for all WGPU resources that need cleanup.
 * Implements AutoCloseable for try-with-resources support.
 */
public abstract class WgpuResource implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(WgpuResource.class);

    protected final MemorySegment handle;
    private volatile boolean closed = false;

    protected WgpuResource(MemorySegment handle) {
        if (handle == null || handle.equals(MemorySegment.NULL)) {
            throw new IllegalArgumentException("Invalid WGPU resource handle");
        }
        this.handle = handle;

        if (logger.isDebugEnabled()) {
            logger.debug("Created {} with handle 0x{}",
                    getClass().getSimpleName(),
                    Long.toHexString(handle.address()));
        }
    }

    /**
     * Gets the raw native handle for this resource.
     * This is for internal use by the binding layer.
     */
    public MemorySegment getHandle() {
        checkNotClosed();
        return handle;
    }

    /**
     * Checks if this resource has been closed.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Throws an exception if this resource has been closed.
     */
    protected void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("WGPU resource has been closed: " + getClass().getSimpleName());
        }
    }

    /**
     * Releases the native resources.
     * Subclasses should override this to call the appropriate WGPU release function.
     */
    protected abstract void releaseNative();

    @Override
    public void close() {
        if (!closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Closing {} with handle 0x{}",
                        getClass().getSimpleName(),
                        Long.toHexString(handle.address()));
            }

            closed = true;
            try {
                releaseNative();
            } catch (Exception e) {
                logger.warn("Failed to release WGPU resource {}: {}",
                        getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (!closed) {
                logger.warn("WGPU resource {} was not explicitly closed - performing cleanup in finalizer",
                        getClass().getSimpleName());
                close();
            }
        } finally {
            super.finalize();
        }
    }
}