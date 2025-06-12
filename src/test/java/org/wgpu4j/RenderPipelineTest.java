package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.resource.*;
import org.wgpu4j.descriptor.*;
import org.wgpu4j.constant.PowerPreference;
import org.wgpu4j.constant.TextureFormat;

import java.util.concurrent.TimeUnit;

/**
 * Test for render pipeline creation.
 */
public class RenderPipelineTest {

    private static final String VERTEX_SHADER = """
            @vertex
            fn vs_main(@builtin(vertex_index) vertexIndex: u32) -> @builtin(position) vec4<f32> {
                var pos = array<vec2<f32>, 3>(
                    vec2<f32>( 0.0,  0.5),
                    vec2<f32>(-0.5, -0.5),
                    vec2<f32>( 0.5, -0.5)
                );
                return vec4<f32>(pos[vertexIndex], 0.0, 1.0);
            }
            """;

    private static final String FRAGMENT_SHADER = """
            @fragment
            fn fs_main() -> @location(0) vec4<f32> {
                return vec4<f32>(1.0, 0.0, 0.0, 1.0);
            }
            """;

    @Test
    public void testRenderPipelineCreation() throws Exception {
        System.out.println("Testing render pipeline creation...");

        try (Instance instance = Instance.create()) {
            System.out.println("Created WGPU instance");

            instance.requestAdapter(AdapterRequestOptions.builder()
                            .powerPreference(PowerPreference.UNDEFINED)
                            .build())
                    .thenCompose(adapter -> adapter.requestDevice(
                            DeviceRequestOptions.builder()
                                    .label("Pipeline Test Device")
                                    .build()))
                    .thenAccept(device -> {
                        System.out.println("Got device");

                        try (device) {
                            try (ShaderModule vertexShader = device.createShaderModule(
                                    ShaderModuleDescriptor.builder()
                                            .label("Vertex Shader")
                                            .wgslCode(VERTEX_SHADER)
                                            .build());
                                 ShaderModule fragmentShader = device.createShaderModule(
                                         ShaderModuleDescriptor.builder()
                                                 .label("Fragment Shader")
                                                 .wgslCode(FRAGMENT_SHADER)
                                                 .build())) {

                                System.out.println("Created shaders");

                                try (RenderPipeline pipeline = device.createRenderPipeline(
                                        RenderPipelineDescriptor.builder()
                                                .label("Triangle Pipeline")
                                                .vertexShader(vertexShader)
                                                .fragmentShader(fragmentShader)
                                                .colorTarget(ColorTargetState.builder()
                                                        .format(TextureFormat.BGRA8_UNORM)
                                                        .build())
                                                .build())) {

                                    System.out.println("Created render pipeline");
                                    System.out.println("Render pipeline test completed successfully.");
                                }
                            }

                        } catch (Exception e) {
                            throw new RuntimeException("Pipeline test failed", e);
                        }
                    })
                    .get(10, TimeUnit.SECONDS);
        }
    }
}