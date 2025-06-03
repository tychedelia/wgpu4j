package org.wgpu4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Native library loader for wgpu-native.
 */
public class WgpuNative {
    
    private static boolean loaded = false;
    
    static {
        loadNativeLibrary();
    }
    
    /**
     * Loads the appropriate wgpu-native library for the current platform.
     */
    private static void loadNativeLibrary() {
        if (loaded) {
            return;
        }
        
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();
        
        String libraryName = getLibraryName(os, arch);
        String resourcePath = "/native/" + libraryName;
        
        try {
            // Try to load from external path first (for development)
            String externalLibPath = System.getProperty("wgpu4j.library.path");
            if (externalLibPath != null) {
                System.load(externalLibPath);
                loaded = true;
                return;
            }
            
            // Load from resources
            InputStream in = WgpuNative.class.getResourceAsStream(resourcePath);
            if (in == null) {
                throw new UnsatisfiedLinkError("Cannot find native library: " + resourcePath);
            }
            
            // Create temporary file
            Path tempFile = Files.createTempFile("wgpu_native", getLibraryExtension(os));
            tempFile.toFile().deleteOnExit();
            
            // Copy library to temp file
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            in.close();
            
            // Load the library
            System.load(tempFile.toAbsolutePath().toString());
            loaded = true;
            
        } catch (IOException e) {
            throw new UnsatisfiedLinkError("Failed to load native library: " + e.getMessage());
        }
    }
    
    /**
     * Gets the platform-specific library name.
     */
    private static String getLibraryName(String os, String arch) {
        if (os.contains("mac")) {
            return "libwgpu_native.dylib";
        } else if (os.contains("linux")) {
            return "libwgpu_native.so";
        } else if (os.contains("windows")) {
            return "wgpu_native.dll";
        }
        throw new UnsupportedOperationException("Unsupported platform: " + os + "/" + arch);
    }
    
    /**
     * Gets the platform-specific library file extension.
     */
    private static String getLibraryExtension(String os) {
        if (os.contains("mac")) {
            return ".dylib";
        } else if (os.contains("linux")) {
            return ".so";
        } else if (os.contains("windows")) {
            return ".dll";
        }
        return ".so";
    }
    
    /**
     * Ensures the native library is loaded.
     * Call this method before using any wgpu functionality.
     */
    public static void ensureLoaded() {
        // Static initializer will handle loading
    }
}