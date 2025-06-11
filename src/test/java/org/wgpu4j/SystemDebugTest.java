package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.core.Instance;
import org.wgpu4j.core.Adapter;

import java.util.concurrent.TimeUnit;

/**
 * Test with system-level debug information
 */
public class SystemDebugTest {

    @Test
    public void testWithSystemDebug() throws Exception {
        System.out.println("=== System Debug Information ===");


        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("OS Version: " + System.getProperty("os.version"));
        System.out.println("Architecture: " + System.getProperty("os.arch"));
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
        System.out.println("JVM: " + System.getProperty("java.vm.name"));


        Runtime runtime = Runtime.getRuntime();
        System.out.println("Max Memory: " + runtime.maxMemory() / (1024 * 1024) + " MB");
        System.out.println("Total Memory: " + runtime.totalMemory() / (1024 * 1024) + " MB");
        System.out.println("Free Memory: " + runtime.freeMemory() / (1024 * 1024) + " MB");

        System.out.println("\n=== Thread Information ===");
        System.out.println("Main Thread: " + Thread.currentThread().getName());
        System.out.println("Thread ID: " + Thread.currentThread().getId());

        System.out.println("\n=== Testing Device Request ===");

        try (Instance instance = Instance.create()) {
            System.out.println("✓ Instance created");

            var adapterFuture = instance.requestAdapter();
            try (Adapter adapter = adapterFuture.get(5, TimeUnit.SECONDS)) {
                System.out.println("✓ Adapter acquired");
                System.out.println("Adapter Handle: " + adapter.getHandle());
                System.out.println("Handle Address: " + adapter.getHandle().address());

                System.out.println("\n→ Starting device request...");
                System.out.println("Current time: " + System.currentTimeMillis());
                System.out.flush();


                Thread currentThread = Thread.currentThread();
                System.out.println("Current thread state: " + currentThread.getState());
                System.out.println("Is daemon: " + currentThread.isDaemon());

                var deviceFuture = adapter.requestDevice();

                System.out.println("Device future created: " + deviceFuture);
                System.out.println("Is done: " + deviceFuture.isDone());
                System.out.println("Is cancelled: " + deviceFuture.isCancelled());


                try {
                    System.out.println("Waiting for device...");
                    deviceFuture.get(1, TimeUnit.SECONDS).close();
                    System.out.println("✓ SUCCESS: Device created!");
                } catch (Exception e) {
                    System.out.println("✗ Device request failed: " + e.getClass().getSimpleName());
                    System.out.println("Message: " + e.getMessage());
                    if (e.getCause() != null) {
                        System.out.println("Cause: " + e.getCause().getClass().getSimpleName());
                        System.out.println("Cause message: " + e.getCause().getMessage());
                    }
                    throw e;
                }
            }
        }
    }
}