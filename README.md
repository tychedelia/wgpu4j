# wgpu4j

Java bindings for [wgpu-native](https://github.com/gfx-rs/wgpu-native) using Project Panama.

## Requirements

- Java 22+
- [jextract](https://jdk.java.net/jextract/) (for generating bindings)
- macOS, Linux, or Windows (x86_64/aarch64)

## Setup

**Option 1: Automatic (Recommended)**
```bash
./gradlew downloadJextract
```

**Option 2: Manual Installation**
1. Download the appropriate build for your platform from [jdk.java.net/jextract](https://jdk.java.net/jextract/)
2. Extract to a standard location:
   - Linux/macOS: `~/.local/jextract` or `/usr/local/jextract`
   - Windows: `%USERPROFILE%\AppData\Local\Programs\jextract` or `C:\Program Files\jextract`
3. Add the `bin` directory to your PATH, or set `JEXTRACT_HOME` environment variable
4. On macOS: Run `sudo xattr -r -d com.apple.quarantine /path/to/jextract` to remove quarantine

## Quick Start

```bash
./gradlew downloadWgpuNative
./gradlew generateBindings
./gradlew build
```

Run the triangle example:
```bash
cd wgpu4j-examples
../gradlew run
```

## Usage

```java
try (Instance instance = Instance.create()) {
    Adapter adapter = instance.requestAdapter().join();
    Device device = adapter.requestDevice().join();
    
    // Create buffers, textures, render pipelines...
}
```

Resources are automatically cleaned up when closed.