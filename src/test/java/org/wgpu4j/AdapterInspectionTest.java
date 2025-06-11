package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.core.Instance;
import org.wgpu4j.core.Adapter;
import org.wgpu4j.descriptors.AdapterRequestOptions;
import org.wgpu4j.enums.PowerPreference;

import java.util.concurrent.TimeUnit;

/**
 * Inspect adapter properties to understand Windows vs macOS differences
 */
public class AdapterInspectionTest {

    @Test
    public void inspectAdapter() throws Exception {
        System.out.println("=== Inspecting Windows Adapter Properties ===");

        try (Instance instance = Instance.create()) {
            System.out.println("✓ Instance created");


            System.out.println("\n--- Testing High Performance Adapter ---");
            var adapterFuture = instance.requestAdapter(
                    AdapterRequestOptions.builder()
                            .powerPreference(PowerPreference.HIGH_PERFORMANCE)
                            .build()
            );

            try (Adapter adapter = adapterFuture.get(5, TimeUnit.SECONDS)) {
                System.out.println("✓ High performance adapter acquired");


                System.out.println("Adapter handle: " + adapter.getHandle());

                System.out.println("\n→ Attempting device request with HIGH_PERFORMANCE adapter...");
                var deviceFuture = adapter.requestDevice();

                try {
                    deviceFuture.get(5, TimeUnit.SECONDS).close();
                    System.out.println("✓ Device created successfully!");
                } catch (Exception e) {
                    System.out.println("✗ Device request failed: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }

            System.out.println("\n--- Testing Low Power Adapter ---");
            var lowPowerFuture = instance.requestAdapter(
                    AdapterRequestOptions.builder()
                            .powerPreference(PowerPreference.LOW_POWER)
                            .build()
            );

            try (Adapter adapter = lowPowerFuture.get(5, TimeUnit.SECONDS)) {
                System.out.println("✓ Low power adapter acquired");
                System.out.println("Adapter handle: " + adapter.getHandle());

                System.out.println("\n→ Attempting device request with LOW_POWER adapter...");
                var deviceFuture = adapter.requestDevice();

                try {
                    deviceFuture.get(5, TimeUnit.SECONDS).close();
                    System.out.println("✓ Device created successfully!");
                } catch (Exception e) {
                    System.out.println("✗ Device request failed: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            throw e;
        }
    }
}