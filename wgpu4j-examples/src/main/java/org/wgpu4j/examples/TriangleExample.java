package org.wgpu4j.examples;

import org.lwjgl.glfw.*;
import org.lwjgl.system.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wgpu4j.WgpuException;
import org.wgpu4j.core.*;
import org.wgpu4j.descriptors.*;
import org.wgpu4j.enums.*;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;
import java.util.concurrent.CompletableFuture;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWNativeCocoa.*;
import static org.lwjgl.glfw.GLFWNativeWin32.*;
import static org.lwjgl.glfw.GLFWNativeX11.*;
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
        instance = Instance.create();
        logger.info("WebGPU instance created");


        CompletableFuture<Adapter> adapterFuture = instance.requestAdapter(
                AdapterRequestOptions.builder()
                        .powerPreference(PowerPreference.HIGH_PERFORMANCE)
                        .build()
        );

        adapter = adapterFuture.get(5, java.util.concurrent.TimeUnit.SECONDS);
        logger.info("WebGPU adapter acquired");

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
        String os = System.getProperty("os.name").toLowerCase();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment surfaceDesc = WGPUSurfaceDescriptor.allocate(arena);
            MemorySegment surfaceSource;

            if (os.contains("win")) {
                long hwnd = glfwGetWin32Window(window);

                surfaceSource = WGPUSurfaceSourceWindowsHWND.allocate(arena);
                MemorySegment chain = WGPUSurfaceSourceWindowsHWND.chain(surfaceSource);
                WGPUChainedStruct.next(chain, MemorySegment.NULL);
                WGPUChainedStruct.sType(chain, webgpu_h.WGPUSType_SurfaceSourceWindowsHWND());
                WGPUSurfaceSourceWindowsHWND.hwnd(surfaceSource, MemorySegment.ofAddress(hwnd));

                logger.info("Created Windows surface source with HWND: 0x{}", Long.toHexString(hwnd));

            } else if (os.contains("mac")) {

                long nsWindow = glfwGetCocoaWindow(window);
                long metalLayer = MacOSSurfaceHelper.createCAMetalLayer(nsWindow);

                surfaceSource = WGPUSurfaceSourceMetalLayer.allocate(arena);
                MemorySegment chain = WGPUSurfaceSourceMetalLayer.chain(surfaceSource);
                WGPUChainedStruct.next(chain, MemorySegment.NULL);
                WGPUChainedStruct.sType(chain, webgpu_h.WGPUSType_SurfaceSourceMetalLayer());
                WGPUSurfaceSourceMetalLayer.layer(surfaceSource, MemorySegment.ofAddress(metalLayer));

                logger.info("Created macOS surface source with CAMetalLayer: 0x{}", Long.toHexString(metalLayer));

            } else {

                long x11Window = glfwGetX11Window(window);
                long x11Display = glfwGetX11Display();

                surfaceSource = WGPUSurfaceSourceXlibWindow.allocate(arena);
                MemorySegment chain = WGPUSurfaceSourceXlibWindow.chain(surfaceSource);
                WGPUChainedStruct.next(chain, MemorySegment.NULL);
                WGPUChainedStruct.sType(chain, webgpu_h.WGPUSType_SurfaceSourceXlibWindow());
                WGPUSurfaceSourceXlibWindow.window(surfaceSource, x11Window);
                WGPUSurfaceSourceXlibWindow.display(surfaceSource, MemorySegment.ofAddress(x11Display));

                logger.info("Created X11 surface source with Window: 0x{}, Display: 0x{}",
                        Long.toHexString(x11Window), Long.toHexString(x11Display));
            }

            WGPUSurfaceDescriptor.nextInChain(surfaceDesc, surfaceSource);
            MemorySegment labelData = arena.allocateFrom("Triangle Surface");
            MemorySegment labelView = WGPUSurfaceDescriptor.label(surfaceDesc);
            WGPUStringView.data(labelView, labelData);
            WGPUStringView.length(labelView, "Triangle Surface".length());

            MemorySegment surfaceHandle = webgpu_h.wgpuInstanceCreateSurface(
                    instance.getHandle(), surfaceDesc);

            if (surfaceHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create surface");
            }

            var constructor = Surface.class.getDeclaredConstructor(MemorySegment.class);
            constructor.setAccessible(true);
            surface = constructor.newInstance(surfaceHandle);

            logger.info("Surface created successfully");
        }


        configureSurface();
    }

    private void configureSurface() throws Exception {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment config = WGPUSurfaceConfiguration.allocate(arena);
            WGPUSurfaceConfiguration.device(config, device.getHandle());
            WGPUSurfaceConfiguration.format(config, TextureFormat.BGRA8_UNORM.getValue());
            WGPUSurfaceConfiguration.usage(config, TextureUsage.RENDER_ATTACHMENT);
            WGPUSurfaceConfiguration.width(config, WINDOW_WIDTH);
            WGPUSurfaceConfiguration.height(config, WINDOW_HEIGHT);
            WGPUSurfaceConfiguration.presentMode(config, PresentMode.FIFO.getValue());
            WGPUSurfaceConfiguration.alphaMode(config, CompositeAlphaMode.OPAQUE.getValue());
            WGPUSurfaceConfiguration.viewFormatCount(config, 0);
            WGPUSurfaceConfiguration.viewFormats(config, MemorySegment.NULL);

            webgpu_h.wgpuSurfaceConfigure(surface.getHandle(), config);

            logger.info("Surface configured: {}x{}, format: BGRA8_UNORM",
                    WINDOW_WIDTH, WINDOW_HEIGHT);
        }
    }

    private void createRenderResources() throws Exception {
        logger.info("Creating render resources...");

        String shaderSource = """
                @vertex
                fn vs_main(@builtin(vertex_index) vertexIndex: u32) -> @builtin(position) vec4<f32> {
                    var pos = array<vec2<f32>, 3>(
                        vec2<f32>( 0.0,  0.5),
                        vec2<f32>(-0.5, -0.5),
                        vec2<f32>( 0.5, -0.5)
                    );
                    return vec4<f32>(pos[vertexIndex], 0.0, 1.0);
                }
                
                @fragment
                fn fs_main() -> @location(0) vec4<f32> {
                    return vec4<f32>(1.0, 0.0, 0.0, 1.0); 
                }
                """;

        shaderModule = device.createShaderModule(
                ShaderModuleDescriptor.builder()
                        .label("Triangle Shader")
                        .wgslCode(shaderSource)
                        .build()
        );

        renderPipeline = device.createRenderPipeline(
                RenderPipelineDescriptor.builder()
                        .label("Triangle Pipeline")
                        .vertexShader(shaderModule)
                        .vertexEntryPoint("vs_main")
                        .fragmentShader(shaderModule)
                        .fragmentEntryPoint("fs_main")
                        .primitiveState(PrimitiveState.builder()
                                .topology(PrimitiveTopology.TRIANGLE_LIST)
                                .build())
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
        try (Arena arena = Arena.ofConfined()) {

            MemorySegment surfaceTexture = WGPUSurfaceTexture.allocate(arena);
            webgpu_h.wgpuSurfaceGetCurrentTexture(surface.getHandle(), surfaceTexture);

            MemorySegment textureHandle = WGPUSurfaceTexture.texture(surfaceTexture);
            if (textureHandle.equals(MemorySegment.NULL)) {
                logger.warn("Failed to get current surface texture");
                return;
            }

            MemorySegment textureView = webgpu_h.wgpuTextureCreateView(
                    textureHandle, MemorySegment.NULL);

            CommandEncoder encoder = device.createCommandEncoder();

            MemorySegment renderPassDesc = WGPURenderPassDescriptor.allocate(arena);
            MemorySegment colorAttachment = WGPURenderPassColorAttachment.allocate(arena);

            WGPURenderPassColorAttachment.view(colorAttachment, textureView);
            WGPURenderPassColorAttachment.loadOp(colorAttachment, LoadOp.CLEAR.getValue());
            WGPURenderPassColorAttachment.storeOp(colorAttachment, StoreOp.STORE.getValue());

            MemorySegment clearColor = WGPUColor.allocate(arena);
            WGPUColor.r(clearColor, 0.3);
            WGPUColor.g(clearColor, 0.3);
            WGPUColor.b(clearColor, 0.8);
            WGPUColor.a(clearColor, 1.0);
            WGPURenderPassColorAttachment.clearValue(colorAttachment, clearColor);

            MemorySegment colorAttachments = arena.allocate(WGPURenderPassColorAttachment.sizeof());
            MemorySegment.copy(colorAttachment, 0, colorAttachments, 0, WGPURenderPassColorAttachment.sizeof());

            WGPURenderPassDescriptor.colorAttachmentCount(renderPassDesc, 1);
            WGPURenderPassDescriptor.colorAttachments(renderPassDesc, colorAttachments);
            WGPURenderPassDescriptor.depthStencilAttachment(renderPassDesc, MemorySegment.NULL);

            MemorySegment renderPass = webgpu_h.wgpuCommandEncoderBeginRenderPass(
                    encoder.getHandle(), renderPassDesc);

            webgpu_h.wgpuRenderPassEncoderSetPipeline(renderPass, renderPipeline.getHandle());
            webgpu_h.wgpuRenderPassEncoderDraw(renderPass, 3, 1, 0, 0);
            webgpu_h.wgpuRenderPassEncoderEnd(renderPass);

            CommandBuffer commandBuffer = encoder.finish();

            queue.submit(commandBuffer);

            webgpu_h.wgpuSurfacePresent(surface.getHandle());

            webgpu_h.wgpuTextureViewRelease(textureView);
            webgpu_h.wgpuRenderPassEncoderRelease(renderPass);

            commandBuffer.close();
            encoder.close();
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