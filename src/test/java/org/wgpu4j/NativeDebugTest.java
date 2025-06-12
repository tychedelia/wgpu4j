package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.resource.Instance;
import org.wgpu4j.resource.Adapter;

import java.util.concurrent.TimeUnit;

/**
 * Test with native debug logging enabled
 */
public class NativeDebugTest {

    @Test
    public void testWithNativeDebug() throws Exception {

        System.setProperty("WGPU_LOG_LEVEL", "TRACE");
        System.setProperty("RUST_LOG", "trace");
        System.setProperty("RUST_BACKTRACE", "1");


        System.setProperty("_CRT_DEBUG_DUMP_HEAP", "1");

        System.out.println("=== Native Debug Test ===");
        System.out.println("Environment variables set for maximum debug output");

        try (Instance instance = Instance.create()) {
            System.out.println("✓ Instance created");

            var adapterFuture = instance.requestAdapter();
            try (Adapter adapter = adapterFuture.get(5, TimeUnit.SECONDS)) {
                System.out.println("✓ Adapter acquired");

                System.out.println("\n→ About to call requestDevice with full debug logging...");
                System.out.flush();

                var deviceFuture = adapter.requestDevice();


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