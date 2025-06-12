package org.wgpu4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Native library loader for wgpu-native.
 * Handles cross-platform loading of native libraries packaged in the JAR.
 */
public class WgpuNative {
    private static final Logger logger = LoggerFactory.getLogger(WgpuNative.class);
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

        String platform = detectPlatform();
        String libraryName = getLibraryName(platform);

        logger.debug("Loading wgpu-native for platform: {} (library: {})", platform, libraryName);

        try {
            String externalLibPath = System.getProperty("wgpu4j.library.path");
            if (externalLibPath != null) {
                logger.debug("Loading library from external path: {}", externalLibPath);
                System.load(externalLibPath);
                loaded = true;
                logger.info("Successfully loaded wgpu-native from external path");
                return;
            }

            if (tryLoadFromDevelopmentPath(platform, libraryName)) {
                return;
            }

            if (tryLoadFromResources(platform, libraryName)) {
                return;
            }

            String errorMsg = String.format(
                    "Failed to load wgpu-native library for platform %s. " +
                            "Searched for library '%s' in:\n" +
                            "1. External path (wgpu4j.library.path system property)\n" +
                            "2. Development directory (native/%s/lib/%s)\n" +
                            "3. JAR resources (/native/%s/%s)\n\n" +
                            "Make sure you're using a supported platform or set the wgpu4j.library.path property.",
                    platform, libraryName, platform, libraryName, platform, libraryName);

            throw new UnsatisfiedLinkError(errorMsg);

        } catch (UnsatisfiedLinkError e) {
            throw e;
        } catch (Exception e) {
            throw new UnsatisfiedLinkError("Failed to load wgpu-native library: " + e.getMessage());
        }
    }

    /**
     * Attempts to load the library from the development directory structure.
     */
    private static boolean tryLoadFromDevelopmentPath(String platform, String libraryName) {
        String userDir = System.getProperty("user.dir");
        if (userDir == null) return false;

        Path devLibPath = Path.of(userDir, "native", platform, "lib", libraryName);
        if (Files.exists(devLibPath)) {
            logger.debug("Loading library from development path: {}", devLibPath);
            System.load(devLibPath.toAbsolutePath().toString());
            loaded = true;
            logger.info("Successfully loaded wgpu-native from development directory");
            return true;
        }

        Path oldDevLibPath = Path.of(userDir, "native", "lib", libraryName);
        if (Files.exists(oldDevLibPath)) {
            logger.debug("Loading library from old development path: {}", oldDevLibPath);
            System.load(oldDevLibPath.toAbsolutePath().toString());
            loaded = true;
            logger.info("Successfully loaded wgpu-native from old development directory");
            return true;
        }

        return false;
    }

    /**
     * Attempts to load the library from JAR resources.
     */
    private static boolean tryLoadFromResources(String platform, String libraryName) throws IOException {
        String resourcePath = "/native/" + platform + "/" + libraryName;

        logger.debug("Attempting to load library from JAR resource: {}", resourcePath);
        InputStream in = WgpuNative.class.getResourceAsStream(resourcePath);
        if (in == null) {
            logger.debug("Library not found at resource path: {}", resourcePath);
            return false;
        }

        String extension = getLibraryExtension(platform);
        Path tempFile = Files.createTempFile("wgpu_native_" + platform, extension);
        tempFile.toFile().deleteOnExit();

        logger.debug("Extracting library to temporary file: {}", tempFile);
        Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        in.close();

        System.load(tempFile.toAbsolutePath().toString());
        loaded = true;
        logger.info("Successfully loaded wgpu-native from JAR resources for platform: {}", platform);
        return true;
    }

    /**
     * Detects the current platform in the format used by wgpu-native releases.
     */
    private static String detectPlatform() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        if (arch.equals("amd64") || arch.equals("x86_64")) {
            arch = "x86_64";
        } else if (arch.equals("aarch64") || arch.equals("arm64")) {
            arch = "aarch64";
        }

        if (os.contains("mac")) {
            return "macos-" + arch;
        } else if (os.contains("linux")) {
            return "linux-" + arch;
        } else if (os.contains("windows")) {
            return "windows-" + arch + "-msvc";
        }

        throw new UnsupportedOperationException(
                String.format("Unsupported platform: %s/%s. Supported platforms: " +
                        "macos-aarch64, macos-x86_64, linux-aarch64, linux-x86_64, " +
                        "windows-aarch64-msvc, windows-x86_64-msvc", os, arch));
    }

    /**
     * Gets the platform-specific library name.
     */
    private static String getLibraryName(String platform) {
        if (platform.startsWith("macos")) {
            return "libwgpu_native.dylib";
        } else if (platform.startsWith("linux")) {
            return "libwgpu_native.so";
        } else if (platform.startsWith("windows")) {
            return "wgpu_native.dll";
        }
        throw new UnsupportedOperationException("Unknown platform: " + platform);
    }

    /**
     * Gets the platform-specific library file extension.
     */
    private static String getLibraryExtension(String platform) {
        if (platform.startsWith("macos")) {
            return ".dylib";
        } else if (platform.startsWith("linux")) {
            return ".so";
        } else if (platform.startsWith("windows")) {
            return ".dll";
        }
        return ".so";
    }

    /**
     * Ensures the native library is loaded.
     * This method is called automatically when the class is first used.
     */
    public static void ensureLoaded() {
        if (!loaded) {
            throw new IllegalStateException("Native library failed to load");
        }
    }

    /**
     * Returns true if the native library has been successfully loaded.
     */
    public static boolean isLoaded() {
        return loaded;
    }

    /**
     * Returns the detected platform string.
     */
    public static String getPlatform() {
        return detectPlatform();
    }
}