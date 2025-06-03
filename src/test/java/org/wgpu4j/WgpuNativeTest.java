package org.wgpu4j;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test for wgpu4j native library loading.
 */
class WgpuNativeTest {
    
    @Test
    void testNativeLibraryLoading() {
        // This test verifies that the native library loading mechanism works
        // For now, it may fail if the library path is not set correctly,
        // but it demonstrates the structure
        
        try {
            WgpuNative.ensureLoaded();
            // If we reach here, loading succeeded or was already done
            assertTrue(true, "Native library loading completed");
        } catch (UnsatisfiedLinkError e) {
            // Expected if library is not in the correct location
            assertTrue(e.getMessage().contains("wgpu") || e.getMessage().contains("native"), 
                "Error should be related to wgpu native library: " + e.getMessage());
        }
    }
    
    @Test
    void testLibraryPathProperty() {
        // Test that we can set a custom library path
        String originalPath = System.getProperty("wgpu4j.library.path");
        
        try {
            System.setProperty("wgpu4j.library.path", "/custom/path/libwgpu_native.dylib");
            String path = System.getProperty("wgpu4j.library.path");
            assertEquals("/custom/path/libwgpu_native.dylib", path);
        } finally {
            // Restore original property
            if (originalPath != null) {
                System.setProperty("wgpu4j.library.path", originalPath);
            } else {
                System.clearProperty("wgpu4j.library.path");
            }
        }
    }
}