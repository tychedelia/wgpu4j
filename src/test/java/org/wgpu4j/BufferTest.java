package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.resource.*;
import org.wgpu4j.descriptor.*;
import org.wgpu4j.constant.BufferUsage;
import org.wgpu4j.constant.PowerPreference;

import java.util.concurrent.TimeUnit;

/**
 * Test for buffer creation and management.
 */
public class BufferTest {

    @Test
    public void testBufferCreation() throws Exception {
        System.out.println("Testing buffer creation...");

        try (Instance instance = Instance.create()) {
            System.out.println("Created WGPU instance");

            instance.requestAdapter(AdapterRequestOptions.builder()
                            .powerPreference(PowerPreference.UNDEFINED)
                            .build())
                    .thenCompose(adapter -> adapter.requestDevice(
                            DeviceRequestOptions.builder()
                                    .label("Buffer Test Device")
                                    .build()))
                    .thenAccept(device -> {
                        System.out.println("Got device");

                        try (device) {
                            try (Buffer vertexBuffer = device.createBuffer(
                                    BufferDescriptor.builder()
                                            .label("Vertex Buffer")
                                            .size(1024).usage(BufferUsage.VERTEX)
                                            .build())) {
                                System.out.println("Created vertex buffer (size: " + vertexBuffer.getSize() + ")");
                            }

                            try (Buffer uniformBuffer = device.createBuffer(
                                    BufferDescriptor.builder()
                                            .label("Uniform Buffer")
                                            .size(256).usage(BufferUsage.UNIFORM)
                                            .build())) {
                                System.out.println("Created uniform buffer (size: " + uniformBuffer.getSize() + ")");
                            }

                            try (Buffer combinedBuffer = device.createBuffer(
                                    BufferDescriptor.builder()
                                            .label("Combined Buffer")
                                            .size(512)
                                            .usage(BufferUsage.combine(BufferUsage.VERTEX, BufferUsage.COPY_DST))
                                            .build())) {
                                System.out.println("Created combined usage buffer (usage: 0x" +
                                        Long.toHexString(combinedBuffer.getUsage()) + ")");
                            }

                            System.out.println("Buffer test completed successfully.");

                        } catch (Exception e) {
                            throw new RuntimeException("Buffer test failed", e);
                        }
                    })
                    .get(10, TimeUnit.SECONDS);
        }
    }
}