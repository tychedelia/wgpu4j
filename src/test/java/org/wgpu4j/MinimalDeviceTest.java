package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.core.Instance;
import org.wgpu4j.core.Adapter;
import org.wgpu4j.core.Device;

import java.util.concurrent.TimeUnit;

/**
 * Minimal test to isolate device request from any windowing/GLFW interactions
 */
public class MinimalDeviceTest {

    @Test
    public void testDeviceRequestWithoutGLFW() throws Exception {
        System.out.println("=== Testing device request WITHOUT any GLFW/windowing ===");

        try (Instance instance = Instance.create()) {
            System.out.println("✓ Instance created");

            var adapterFuture = instance.requestAdapter();
            try (Adapter adapter = adapterFuture.get(5, TimeUnit.SECONDS)) {
                System.out.println("✓ Adapter acquired");

                System.out.println("→ Requesting device (no GLFW context)...");
                var deviceFuture = adapter.requestDevice();


                long startTime = System.currentTimeMillis();
                while (!deviceFuture.isDone() && (System.currentTimeMillis() - startTime) < 10000) {
                    instance.processEvents();
                    Thread.sleep(10);
                }

                try (Device device = deviceFuture.get(1, TimeUnit.SECONDS)) {
                    System.out.println("✓ Device acquired WITHOUT windowing!");
                } catch (Exception e) {
                    System.out.println("✗ Device request failed: " + e.getMessage());
                    throw e;
                }
            }
        }
    }
}