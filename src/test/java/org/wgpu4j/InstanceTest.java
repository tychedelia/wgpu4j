package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.resource.Instance;
import org.wgpu4j.resource.Adapter;

import java.util.concurrent.TimeUnit;

/**
 * Tests for WGPU Instance creation and basic adapter functionality.
 */
public class InstanceTest {

    @Test
    public void testInstanceCreation() throws Exception {
        try (Instance instance = Instance.create()) {
            System.out.println("Created WGPU instance successfully");

            var adapterFuture = instance.requestAdapter();

            try (Adapter adapter = adapterFuture.get(5, TimeUnit.SECONDS)) {
                System.out.println("Requested adapter successfully");

                var deviceFuture = adapter.requestDevice();
                deviceFuture.get(5, TimeUnit.SECONDS).close();

                System.out.println("Basic WGPU initialization works");
            }
        }
    }
}