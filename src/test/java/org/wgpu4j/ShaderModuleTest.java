package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.resource.*;
import org.wgpu4j.descriptor.*;
import org.wgpu4j.constant.PowerPreference;

import java.util.concurrent.TimeUnit;

/**
 * Test for shader module creation and compilation.
 */
public class ShaderModuleTest {

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
    public void testShaderModuleCreation() throws Exception {
        System.out.println("Testing shader module creation...");

        try (Instance instance = Instance.create()) {
            System.out.println("Created WGPU instance");

            instance.requestAdapter(AdapterRequestOptions.builder()
                            .powerPreference(PowerPreference.UNDEFINED)
                            .build())
                    .thenCompose(adapter -> adapter.requestDevice(
                            DeviceRequestOptions.builder()
                                    .label("Shader Test Device")
                                    .build()))
                    .thenAccept(device -> {
                        System.out.println("Got device");

                        try (device) {
                            try (ShaderModule vertexShader = device.createShaderModule(
                                    ShaderModuleDescriptor.builder()
                                            .label("Vertex Shader")
                                            .wgslCode(VERTEX_SHADER)
                                            .build())) {
                                System.out.println("Created vertex shader module");
                            }

                            try (ShaderModule fragmentShader = device.createShaderModule(
                                    ShaderModuleDescriptor.builder()
                                            .label("Fragment Shader")
                                            .wgslCode(FRAGMENT_SHADER)
                                            .build())) {
                                System.out.println("Created fragment shader module");
                            }

                            System.out.println("Shader module test completed successfully.");

                        } catch (Exception e) {
                            throw new RuntimeException("Shader test failed", e);
                        }
                    })
                    .get(10, TimeUnit.SECONDS);
        }
    }
}