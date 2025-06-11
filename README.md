# wgpu4j

Java bindings for [wgpu-native](https://github.com/gfx-rs/wgpu-native) using Project Panama.

## Requirements

- Java 22+
- macOS, Linux, or Windows (x86_64/aarch64)

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