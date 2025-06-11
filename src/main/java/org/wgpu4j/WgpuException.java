package org.wgpu4j;

/**
 * Exception thrown when WGPU operations fail.
 */
public class WgpuException extends RuntimeException {

    public WgpuException(String message) {
        super(message);
    }

    public WgpuException(String message, Throwable cause) {
        super(message, cause);
    }
}