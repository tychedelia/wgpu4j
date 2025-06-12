package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.resource.*;
import org.wgpu4j.descriptor.*;
import org.wgpu4j.constant.*;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.concurrent.TimeUnit;

/**
 * Tests for compute shader functionality and GPU computing.
 */
public class ComputeShaderTest {

    @Test
    public void testBasicComputeShader() throws Exception {
        try (Instance instance = Instance.create()) {
            var adapterFuture = instance.requestAdapter();

            try (Adapter adapter = adapterFuture.get(5, TimeUnit.SECONDS)) {
                var deviceFuture = adapter.requestDevice();

                try (Device device = deviceFuture.get(5, TimeUnit.SECONDS)) {
                    Queue queue = device.getQueue();

                    System.out.println("Device setup complete for compute shader test");

                    try (ShaderModule computeShader = device.createShaderModule(
                            ShaderModuleDescriptor.builder()
                                    .label("Compute Test Shader")
                                    .wgslCode("""
                                            @group(0) @binding(0)
                                            var<storage, read_write> data: array<f32>;
                                            
                                            @compute @workgroup_size(1)
                                            fn main(@builtin(global_invocation_id) global_id: vec3<u32>) {
                                                data[global_id.x] = data[global_id.x] * 2.0;
                                            }
                                            """)
                                    .build())) {

                        System.out.println("Created compute shader");

                        float[] inputData = {1.0f, 2.0f, 3.0f, 4.0f};

                        try (Buffer storageBuffer = device.createBuffer(
                                BufferDescriptor.builder()
                                        .label("Compute Storage Buffer")
                                        .size(inputData.length * Float.BYTES)
                                        .usage(BufferUsage.STORAGE | BufferUsage.COPY_DST | BufferUsage.COPY_SRC)
                                        .build())) {

                            try (Arena arena = Arena.ofConfined()) {
                                MemorySegment data = arena.allocate(inputData.length * Float.BYTES);
                                for (int i = 0; i < inputData.length; i++) {
                                    data.setAtIndex(ValueLayout.JAVA_FLOAT, i, inputData[i]);
                                }
                                queue.writeBuffer(storageBuffer, 0, data.toArray(ValueLayout.JAVA_BYTE));
                            }

                            System.out.println("Created and uploaded storage buffer");

                            try (BindGroupLayout bindGroupLayout = device.createBindGroupLayout(
                                    BindGroupLayoutDescriptor.builder()
                                            .label("Compute Bind Group Layout")
                                            .entry(BindGroupLayoutEntry.builder()
                                                    .binding(0)
                                                    .visibility(ShaderStageFlags.COMPUTE)
                                                    .bufferType(BufferBindingType.STORAGE)
                                                    .build())
                                            .build())) {

                                System.out.println("Created bind group layout");

                                try (PipelineLayout pipelineLayout = device.createPipelineLayout(
                                        PipelineLayoutDescriptor.builder()
                                                .label("Compute Pipeline Layout")
                                                .bindGroupLayout(bindGroupLayout)
                                                .build())) {

                                    try (ComputePipeline computePipeline = device.createComputePipeline(
                                            ComputePipelineDescriptor.builder()
                                                    .label("Compute Test Pipeline")
                                                    .pipelineLayout(pipelineLayout)
                                                    .computeShader(computeShader)
                                                    .entryPoint("main")
                                                    .build())) {

                                        System.out.println("Created compute pipeline");

                                        try (BindGroup bindGroup = device.createBindGroup(
                                                BindGroupDescriptor.builder()
                                                        .label("Compute Bind Group")
                                                        .layout(bindGroupLayout)
                                                        .entry(BindGroupEntry.builder()
                                                                .binding(0)
                                                                .buffer(storageBuffer)
                                                                .build())
                                                        .build())) {

                                            System.out.println("Created bind group");

                                            try (CommandEncoder encoder = device.createCommandEncoder()) {

                                                try (ComputePassEncoder computePass = encoder.beginComputePass(
                                                        ComputePassDescriptor.builder()
                                                                .label("Compute Test Pass")
                                                                .build())) {

                                                    computePass.setPipeline(computePipeline);
                                                    computePass.setBindGroup(0, bindGroup);
                                                    computePass.dispatchWorkgroups(inputData.length, 1, 1);

                                                    System.out.println("Recorded compute commands");

                                                    computePass.end();
                                                }

                                                try (CommandBuffer commandBuffer = encoder.finish()) {
                                                    queue.submit(commandBuffer);
                                                    System.out.println("Compute shader test completed successfully.");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}