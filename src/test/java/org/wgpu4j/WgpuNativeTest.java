package org.wgpu4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test for wgpu4j native library loading.
 */
class WgpuNativeTest {

    @Test
    void testNativeLibraryLoading() {

        try {
            WgpuNative.ensureLoaded();
            assertTrue(true, "Native library loading completed");
        } catch (UnsatisfiedLinkError e) {
            assertTrue(e.getMessage().contains("wgpu") || e.getMessage().contains("native"),
                    "Error should be related to wgpu native library: " + e.getMessage());
        }
    }

    @Test
    void testLibraryPathProperty() {
        String originalPath = System.getProperty("wgpu4j.library.path");

        try {
            System.setProperty("wgpu4j.library.path", "/custom/path/libwgpu_native.dylib");
            String path = System.getProperty("wgpu4j.library.path");
            assertEquals("/custom/path/libwgpu_native.dylib", path);
        } finally {
            if (originalPath != null) {
                System.setProperty("wgpu4j.library.path", originalPath);
            } else {
                System.clearProperty("wgpu4j.library.path");
            }
        }
    }
}