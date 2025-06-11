package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.core.Instance;
import org.wgpu4j.core.Adapter;
import org.wgpu4j.bindings.webgpu_h;

import java.util.concurrent.TimeUnit;

/**
 * Test with native call tracing to see exactly what's happening
 */
public class TracingTest {

    @Test
    public void testWithTracing() throws Exception {
        // Enable tracing if possible
        System.setProperty("jdk.foreign.traceDowncalls", "true");
        
        System.out.println("=== Testing with tracing enabled ===");
        
        try (Instance instance = Instance.create()) {
            System.out.println("✓ Instance created");

            // Check if we can resolve the function address
            try {
                var adapterAddr = webgpu_h.wgpuAdapterRequestDevice$address();
                System.out.println("wgpuAdapterRequestDevice address: " + adapterAddr);
            } catch (Exception e) {
                System.out.println("Failed to resolve wgpuAdapterRequestDevice: " + e.getMessage());
            }

            var adapterFuture = instance.requestAdapter();
            try (Adapter adapter = adapterFuture.get(5, TimeUnit.SECONDS)) {
                System.out.println("✓ Adapter acquired");
                System.out.println("Adapter handle: " + adapter.getHandle());

                System.out.println("\n→ About to call requestDevice with tracing...");
                var deviceFuture = adapter.requestDevice();
                
                try {
                    deviceFuture.get(5, TimeUnit.SECONDS).close();
                    System.out.println("✓ Device created successfully!");
                } catch (Exception e) {
                    System.out.println("✗ Device request failed: " + e.getMessage());
                    throw e;
                }
            }
        }
    }
}