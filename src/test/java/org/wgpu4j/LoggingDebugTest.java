package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.core.Instance;
import org.wgpu4j.core.Adapter;

import java.util.concurrent.TimeUnit;

/**
 * Test with native WGPU logging enabled
 */
public class LoggingDebugTest {

    @Test
    public void testWithNativeLogging() throws Exception {
        System.out.println("=== Enabling Native WGPU Logging ===");


        WgpuLogging.setLogLevel(WgpuLogging.WGPU_LOG_LEVEL_INFO);
        WgpuLogging.setLogCallback((level, message) -> {
            String levelStr = switch (level) {
                case WgpuLogging.WGPU_LOG_LEVEL_ERROR -> "ERROR";
                case WgpuLogging.WGPU_LOG_LEVEL_WARN -> "WARN";
                case WgpuLogging.WGPU_LOG_LEVEL_INFO -> "INFO";
                case WgpuLogging.WGPU_LOG_LEVEL_DEBUG -> "DEBUG";
                case WgpuLogging.WGPU_LOG_LEVEL_TRACE -> "TRACE";
                default -> "UNKNOWN";
            };
            System.out.println("[WGPU-" + levelStr + "] " + message);
        });

        System.out.println("\n=== Starting WebGPU Operations ===");

        try (Instance instance = Instance.create()) {
            System.out.println("✓ Instance created");

            var adapterFuture = instance.requestAdapter();
            try (Adapter adapter = adapterFuture.get(5, TimeUnit.SECONDS)) {
                System.out.println("✓ Adapter acquired");

                System.out.println("\n→ About to call requestDevice with native logging enabled...");
                System.out.flush();

                var deviceFuture = adapter.requestDevice();

                try {
                    deviceFuture.get(5, TimeUnit.SECONDS).close();
                    System.out.println("✓ SUCCESS: Device created!");
                } catch (Exception e) {
                    System.out.println("✗ Device request failed: " + e.getMessage());
                    throw e;
                }
            }
        }
    }
}