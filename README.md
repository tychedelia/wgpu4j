# wgpu4j

Java bindings for [wgpu-native](https://github.com/gfx-rs/wgpu-native) using Project Panama.

## Requirements

- Java 21 or later
- [jextract](https://jdk.java.net/jextract/) (optional, for automatic binding generation)

## Building

### Download wgpu-native

```bash
./gradlew downloadWgpuNative
```

This downloads the appropriate wgpu-native binaries and headers for your platform.

### Generate Bindings (Optional)

If you have jextract installed:

```bash
./gradlew generateBindings
```

To install jextract:
1. Download from https://jdk.java.net/jextract/
2. Extract to a directory (e.g., `/usr/local/jextract`)
3. Set `JEXTRACT_HOME` environment variable or add to PATH

### Build the Project

```bash
./gradlew build
```

## Project Structure

```
src/
├── main/java/           # Hand-written Java code
├── generated/java/      # Auto-generated bindings (from jextract)
└── test/java/          # Tests

native/                  # Downloaded wgpu-native files
├── include/            # Header files
│   └── webgpu/
│       ├── webgpu.h
│       └── wgpu.h
└── lib/                # Native libraries
    ├── libwgpu_native.a
    └── libwgpu_native.dylib (macOS)
```

## Usage

### Basic Setup

```java
import org.wgpu4j.WgpuNative;

// Ensure native library is loaded
WgpuNative.ensureLoaded();

// Use wgpu functionality...
```

### Custom Library Path

You can specify a custom path to the native library:

```bash
java -Dwgpu4j.library.path=/path/to/libwgpu_native.dylib YourApp
```

## Development Notes

- The project uses Java 21+ with `--enable-preview` for Panama features
- Native library loading is handled automatically
- Generated bindings (when using jextract) are placed in `src/generated/java/`
- Manual bindings can be written in `src/main/java/`

## Platform Support

Currently supports:
- macOS (x86_64, aarch64)
- Linux (x86_64, aarch64)  
- Windows (x86_64, aarch64)

Native libraries are automatically downloaded for your platform during the build process.