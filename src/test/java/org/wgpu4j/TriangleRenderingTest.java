package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.core.*;
import org.wgpu4j.descriptors.*;
import org.wgpu4j.enums.*;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.concurrent.TimeUnit;

/**
 * Tests for basic triangle rendering functionality.
 */
public class TriangleRenderingTest {

    @Test
    public void testBasicTriangleRendering() throws Exception {
        try (Instance instance = Instance.create()) {
            var adapterFuture = instance.requestAdapter();

            try (Adapter adapter = adapterFuture.get(5, TimeUnit.SECONDS)) {
                var deviceFuture = adapter.requestDevice();

                try (Device device = deviceFuture.get(5, TimeUnit.SECONDS)) {
                    Queue queue = device.getQueue();

                    System.out.println("Device setup complete for triangle rendering test");

                    float[] triangleData = {
                            0.0f, 0.5f, 0.0f, -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f};

                    try (Buffer vertexBuffer = device.createBuffer(
                            BufferDescriptor.builder()
                                    .label("Triangle Vertex Buffer")
                                    .size(triangleData.length * Float.BYTES)
                                    .usage(BufferUsage.VERTEX | BufferUsage.COPY_DST)
                                    .build())) {

                        try (Arena arena = Arena.ofConfined()) {
                            MemorySegment data = arena.allocate(triangleData.length * Float.BYTES);
                            for (int i = 0; i < triangleData.length; i++) {
                                data.setAtIndex(ValueLayout.JAVA_FLOAT, i, triangleData[i]);
                            }
                            queue.writeBuffer(vertexBuffer, 0, data.toArray(ValueLayout.JAVA_BYTE));
                        }

                        System.out.println("Created and uploaded vertex buffer");

                        try (ShaderModule vertexShader = device.createShaderModule(
                                ShaderModuleDescriptor.builder()
                                        .label("Triangle Vertex Shader")
                                        .wgslCode("""
                                                @vertex
                                                fn vs_main(@location(0) position: vec3<f32>) -> @builtin(position) vec4<f32> {
                                                    return vec4<f32>(position, 1.0);
                                                }
                                                """)
                                        .build());
                             ShaderModule fragmentShader = device.createShaderModule(
                                     ShaderModuleDescriptor.builder()
                                             .label("Triangle Fragment Shader")
                                             .wgslCode("""
                                                     @fragment
                                                     fn fs_main() -> @location(0) vec4<f32> {
                                                         return vec4<f32>(1.0, 0.0, 0.0, 1.0);                                         }
                                                     """)
                                             .build())) {

                            System.out.println("Created shaders for triangle rendering");

                            try (Texture colorTexture = device.createTexture(
                                    TextureDescriptor.builder()
                                            .label("Triangle Color Target")
                                            .size(256, 256)
                                            .format(TextureFormat.BGRA8_UNORM)
                                            .usage(TextureUsage.RENDER_ATTACHMENT)
                                            .build());
                                 TextureView colorView = colorTexture.createView()) {

                                System.out.println("Created color texture and view");

                                try (RenderPipeline pipeline = device.createRenderPipeline(
                                        RenderPipelineDescriptor.builder()
                                                .label("Triangle Pipeline")
                                                .vertexShader(vertexShader)
                                                .fragmentShader(fragmentShader)
                                                .colorTarget(ColorTargetState.builder()
                                                        .format(TextureFormat.BGRA8_UNORM)
                                                        .build())
                                                .primitiveState(PrimitiveState.builder()
                                                        .topology(PrimitiveTopology.TRIANGLE_LIST)
                                                        .build())
                                                .vertexBuffer(VertexBufferLayout.builder()
                                                        .stepMode(VertexStepMode.VERTEX)
                                                        .arrayStride(3 * Float.BYTES)
                                                        .attribute(VertexAttribute.builder()
                                                                .shaderLocation(0)
                                                                .format(VertexFormat.FLOAT32X3)
                                                                .offset(0)
                                                                .build())
                                                        .build())
                                                .build())) {

                                    System.out.println("Created triangle render pipeline");

                                    try (CommandEncoder encoder = device.createCommandEncoder()) {

                                        try (RenderPassEncoder renderPass = encoder.beginRenderPass(
                                                RenderPassDescriptor.builder()
                                                        .colorAttachment(RenderPassColorAttachment.builder()
                                                                .view(colorView)
                                                                .clearBlack()
                                                                .build())
                                                        .build())) {

                                            renderPass.setPipeline(pipeline);
                                            renderPass.setVertexBuffer(0, vertexBuffer);
                                            renderPass.draw(3, 1);
                                            System.out.println("Recorded triangle render commands");

                                            renderPass.end();
                                        }

                                        try (CommandBuffer commandBuffer = encoder.finish()) {
                                            queue.submit(commandBuffer);
                                            System.out.println("Triangle rendering test completed successfully.");
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