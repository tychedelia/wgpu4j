package org.wgpu4j.examples;

import org.lwjgl.glfw.*;
import org.lwjgl.system.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wgpu4j.WgpuLogging;
import org.wgpu4j.resource.*;
import org.wgpu4j.descriptor.*;
import org.wgpu4j.constant.*;
import org.wgpu4j.utils.SurfaceUtils;

import java.util.concurrent.CompletableFuture;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Complete triangle rendering example using WGPU4J with GLFW.
 * This is a proper end-to-end test that creates a window, sets up WebGPU,
 * and renders a red triangle.
 */
public class TriangleExample {

    private static final Logger logger = LoggerFactory.getLogger(TriangleExample.class);
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    private Instance instance;
    private Surface surface;
    private Adapter adapter;
    private Device device;
    private Queue queue;
    private ShaderModule shaderModule;
    private RenderPipeline renderPipeline;
    private long window;

    public static void main(String[] args) {
        new TriangleExample().run();
    }

    public void run() {
        try {
            initWindow();
            initWebGPU();
            createSurface();
            createRenderResources();
            runRenderLoop();
        } catch (Exception e) {
            logger.error("Triangle example failed", e);
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void initWindow() {
        logger.info("Initializing GLFW window...");

        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }

        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "WGPU4J Triangle Example", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            var pWidth = stack.mallocInt(1);
            var pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidmode != null) {
                glfwSetWindowPos(window,
                        (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2
                );
            }
        }

        logger.info("GLFW window created successfully");
    }

    private void initWebGPU() throws Exception {
        logger.info("Initializing WebGPU...");


        WgpuLogging.setLogLevel(WgpuLogging.WGPU_LOG_LEVEL_WARN);
        WgpuLogging.setLogCallback((level, message) -> {
            String levelStr = switch (level) {
                case WgpuLogging.WGPU_LOG_LEVEL_ERROR -> "ERROR";
                case WgpuLogging.WGPU_LOG_LEVEL_WARN -> "WARN";
                case WgpuLogging.WGPU_LOG_LEVEL_INFO -> "INFO";
                case WgpuLogging.WGPU_LOG_LEVEL_DEBUG -> "DEBUG";
                case WgpuLogging.WGPU_LOG_LEVEL_TRACE -> "TRACE";
                default -> "UNKNOWN";
            };
            logger.info("[WGPU-{}] {}", levelStr, message);
        });

        instance = Instance.create();
        logger.info("WebGPU instance created");


        CompletableFuture<Adapter> adapterFuture = instance.requestAdapter(
                AdapterRequestOptions.builder()
                        .powerPreference(PowerPreference.HIGH_PERFORMANCE)
                        .build()
        );

        adapter = adapterFuture.get(5, java.util.concurrent.TimeUnit.SECONDS);
        logger.info("WebGPU adapter acquired");

        logger.info("Requesting WebGPU device...");
        CompletableFuture<Device> deviceFuture = adapter.requestDevice(
                DeviceRequestOptions.builder()
                        .label("Triangle Device")
                        .build()
        );

        device = deviceFuture.get(5, java.util.concurrent.TimeUnit.SECONDS);
        logger.info("WebGPU device acquired");

        queue = device.getQueue();
        logger.info("WebGPU initialization complete");
    }

    private void createSurface() throws Exception {
        logger.info("Creating platform-specific surface...");
        
        surface = SurfaceUtils.createFromGLFWWindow(instance, window);
        
        configureSurface();
    }

    private void configureSurface() throws Exception {
        SurfaceConfiguration config = SurfaceConfiguration.builder()
                .device(device)
                .format(TextureFormat.BGRA8_UNORM)
                .usage(TextureUsage.RENDER_ATTACHMENT)
                .size(WINDOW_WIDTH, WINDOW_HEIGHT)
                .presentMode(PresentMode.FIFO)
                .alphaMode(CompositeAlphaMode.OPAQUE)
                .build();

        surface.configure(config);

        logger.info("Surface configured: {}x{}, format: BGRA8_UNORM",
                WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private void createRenderResources() throws Exception {
        logger.info("Creating render resources...");

        String shaderSource = """
                struct VertexOutput {
                    @builtin(position) position: vec4<f32>,
                    @location(0) color: vec3<f32>,
                }
                
                @vertex
                fn vs_main(@builtin(vertex_index) vertex_index: u32) -> VertexOutput {
                    var positions = array<vec2<f32>, 3>(
                        vec2<f32>( 0.0,  0.5),
                        vec2<f32>(-0.5, -0.5),
                        vec2<f32>( 0.5, -0.5)
                    );
                
                    var colors = array<vec3<f32>, 3>(
                        vec3<f32>(1.0, 0.0, 0.0),
                        vec3<f32>(0.0, 1.0, 0.0),
                        vec3<f32>(0.0, 0.0, 1.0)
                    );
                
                    var output: VertexOutput;
                    output.position = vec4<f32>(positions[vertex_index], 0.0, 1.0);
                    output.color = colors[vertex_index];
                
                    return output;
                }
                
                @fragment
                fn fs_main(input: VertexOutput) -> @location(0) vec4<f32> {
                    return vec4<f32>(input.color, 1.0);
                }
                """;

        shaderModule = device.createShaderModule(
                ShaderModuleDescriptor.builder()
                        .label("Triangle Shader")
                        .wgslCode(shaderSource)
                        .build()
        );

        PipelineLayout pipelineLayout = device.createPipelineLayout(
                PipelineLayoutDescriptor.builder()
                        .label("Empty Pipeline Layout")
                        .build()
        );

        renderPipeline = device.createRenderPipeline(
                RenderPipelineDescriptor.builder()
                        .label("Triangle Pipeline")
                        .layout(pipelineLayout)
                        .vertexShader(shaderModule)
                        .vertexEntryPoint("vs_main")
                        .fragmentShader(shaderModule)
                        .fragmentEntryPoint("fs_main")
                        .primitiveState(PrimitiveState.builder()
                                .topology(PrimitiveTopology.TRIANGLE_LIST)
                                .build())
                        .depthStencilState(null)
                        .multisampleState(MultisampleState.builder()
                                .count(1)
                                .build())
                        .colorTarget(ColorTargetState.builder()
                                .format(TextureFormat.BGRA8_UNORM)
                                .build())
                        .build()
        );

        logger.info("Render resources created successfully");
    }

    private void runRenderLoop() throws Exception {
        logger.info("Starting render loop...");

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            renderFrame();
            instance.processEvents();
            Thread.sleep(16);
        }

        logger.info("Render loop ended");
    }

    private void renderFrame() throws Exception {
        Surface.SurfaceTexture surfaceTexture = surface.getCurrentTexture();
        if (!surfaceTexture.isSuccess()) {
            logger.warn("Failed to get current surface texture, status: {}", surfaceTexture.getStatus());
            return;
        }

        try (TextureView textureView = surfaceTexture.getTexture().createView();
             CommandEncoder encoder = device.createCommandEncoder()) {

            RenderPassColorAttachment colorAttachment = RenderPassColorAttachment.builder()
                    .view(textureView)
                    .loadOp(LoadOp.CLEAR)
                    .storeOp(StoreOp.STORE)
                    .clearColor(0.1, 0.1, 0.1, 1.0)
                    .build();

            RenderPassDescriptor renderPassDesc = RenderPassDescriptor.builder()
                    .label("Triangle Render Pass")
                    .colorAttachment(colorAttachment)
                    .build();

            try (RenderPassEncoder renderPass = encoder.beginRenderPass(renderPassDesc)) {
                renderPass.setPipeline(renderPipeline);
                renderPass.draw(3, 1);
                renderPass.end();
            }

            CommandBuffer commandBuffer = encoder.finish();
            queue.submit(commandBuffer);
            surface.present();

            commandBuffer.close();
        }
    }

    private void cleanup() {
        logger.info("Cleaning up resources...");


        if (renderPipeline != null) renderPipeline.close();
        if (shaderModule != null) shaderModule.close();
        if (surface != null) surface.close();
        if (queue != null) queue.close();
        if (device != null) device.close();
        if (adapter != null) adapter.close();
        if (instance != null) instance.close();


        if (window != NULL) {
            glfwDestroyWindow(window);
        }
        glfwTerminate();

        logger.info("Cleanup complete");
    }
}