package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.core.Instance;
import org.wgpu4j.core.Adapter;

import java.util.concurrent.TimeUnit;

/**
 * Test with native debug logging enabled
 */
public class NativeDebugTest {

    @Test
    public void testWithNativeDebug() throws Exception {
        // Set environment variables for wgpu debug logging
        System.setProperty("WGPU_LOG_LEVEL", "TRACE");
        System.setProperty("RUST_LOG", "trace");
        System.setProperty("RUST_BACKTRACE", "1");
        
        // Enable Windows debug heap
        System.setProperty("_CRT_DEBUG_DUMP_HEAP", "1");
        
        System.out.println("=== Native Debug Test ===");
        System.out.println("Environment variables set for maximum debug output");
        
        try (Instance instance = Instance.create()) {
            System.out.println("✓ Instance created");

            var adapterFuture = instance.requestAdapter();
            try (Adapter adapter = adapterFuture.get(5, TimeUnit.SECONDS)) {
                System.out.println("✓ Adapter acquired");

                System.out.println("\n→ About to call requestDevice with full debug logging...");
                System.out.flush(); // Ensure output is written
                
                var deviceFuture = adapter.requestDevice();
                
                // Short timeout to see immediate behavior
                try {
                    deviceFuture.get(3, TimeUnit.SECONDS).close();
                    System.out.println("✓ Device created successfully!");
                } catch (Exception e) {
                    System.out.println("✗ Device request failed: " + e.getMessage());
                    System.out.println("Exception type: " + e.getClass().getName());
                    throw e;
                }
            }
        }
    }
}