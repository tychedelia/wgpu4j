package org.wgpu4j.enums;

import org.wgpu4j.bindings.webgpu_h;

/**
 * Vertex attribute format constants for describing vertex buffer layouts.
 */
public final class VertexFormat {

    private VertexFormat() {
    }

    public static final int UINT8 = webgpu_h.WGPUVertexFormat_Uint8();
    public static final int UINT8X2 = webgpu_h.WGPUVertexFormat_Uint8x2();
    public static final int UINT8X4 = webgpu_h.WGPUVertexFormat_Uint8x4();
    public static final int SINT8 = webgpu_h.WGPUVertexFormat_Sint8();
    public static final int SINT8X2 = webgpu_h.WGPUVertexFormat_Sint8x2();
    public static final int SINT8X4 = webgpu_h.WGPUVertexFormat_Sint8x4();

    public static final int UINT16 = webgpu_h.WGPUVertexFormat_Uint16();
    public static final int UINT16X2 = webgpu_h.WGPUVertexFormat_Uint16x2();
    public static final int UINT16X4 = webgpu_h.WGPUVertexFormat_Uint16x4();
    public static final int SINT16 = webgpu_h.WGPUVertexFormat_Sint16();
    public static final int SINT16X2 = webgpu_h.WGPUVertexFormat_Sint16x2();
    public static final int SINT16X4 = webgpu_h.WGPUVertexFormat_Sint16x4();

    public static final int UINT32 = webgpu_h.WGPUVertexFormat_Uint32();
    public static final int UINT32X2 = webgpu_h.WGPUVertexFormat_Uint32x2();
    public static final int UINT32X3 = webgpu_h.WGPUVertexFormat_Uint32x3();
    public static final int UINT32X4 = webgpu_h.WGPUVertexFormat_Uint32x4();
    public static final int SINT32 = webgpu_h.WGPUVertexFormat_Sint32();
    public static final int SINT32X2 = webgpu_h.WGPUVertexFormat_Sint32x2();
    public static final int SINT32X3 = webgpu_h.WGPUVertexFormat_Sint32x3();
    public static final int SINT32X4 = webgpu_h.WGPUVertexFormat_Sint32x4();

    public static final int FLOAT16 = webgpu_h.WGPUVertexFormat_Float16();
    public static final int FLOAT16X2 = webgpu_h.WGPUVertexFormat_Float16x2();
    public static final int FLOAT16X4 = webgpu_h.WGPUVertexFormat_Float16x4();

    public static final int FLOAT32 = webgpu_h.WGPUVertexFormat_Float32();
    public static final int FLOAT32X2 = webgpu_h.WGPUVertexFormat_Float32x2();
    public static final int FLOAT32X3 = webgpu_h.WGPUVertexFormat_Float32x3();
    public static final int FLOAT32X4 = webgpu_h.WGPUVertexFormat_Float32x4();

    public static final int UNORM8 = webgpu_h.WGPUVertexFormat_Unorm8();
    public static final int UNORM8X2 = webgpu_h.WGPUVertexFormat_Unorm8x2();
    public static final int UNORM8X4 = webgpu_h.WGPUVertexFormat_Unorm8x4();
    public static final int SNORM8 = webgpu_h.WGPUVertexFormat_Snorm8();
    public static final int SNORM8X2 = webgpu_h.WGPUVertexFormat_Snorm8x2();
    public static final int SNORM8X4 = webgpu_h.WGPUVertexFormat_Snorm8x4();

    public static final int UNORM16 = webgpu_h.WGPUVertexFormat_Unorm16();
    public static final int UNORM16X2 = webgpu_h.WGPUVertexFormat_Unorm16x2();
    public static final int UNORM16X4 = webgpu_h.WGPUVertexFormat_Unorm16x4();
    public static final int SNORM16 = webgpu_h.WGPUVertexFormat_Snorm16();
    public static final int SNORM16X2 = webgpu_h.WGPUVertexFormat_Snorm16x2();
    public static final int SNORM16X4 = webgpu_h.WGPUVertexFormat_Snorm16x4();

    /**
     * Gets the byte size of a vertex format.
     *
     * @param format The vertex format constant
     * @return The size in bytes
     */
    public static int getSizeInBytes(int format) {
        if (format == UINT8 || format == SINT8 || format == UNORM8 || format == SNORM8) return 1;
        if (format == UINT8X2 || format == SINT8X2 || format == UNORM8X2 || format == SNORM8X2) return 2;
        if (format == UINT8X4 || format == SINT8X4 || format == UNORM8X4 || format == SNORM8X4) return 4;
        if (format == UINT16 || format == SINT16 || format == UNORM16 || format == SNORM16 || format == FLOAT16)
            return 2;
        if (format == UINT16X2 || format == SINT16X2 || format == UNORM16X2 || format == SNORM16X2 || format == FLOAT16X2)
            return 4;
        if (format == UINT16X4 || format == SINT16X4 || format == UNORM16X4 || format == SNORM16X4 || format == FLOAT16X4)
            return 8;
        if (format == UINT32 || format == SINT32 || format == FLOAT32) return 4;
        if (format == UINT32X2 || format == SINT32X2 || format == FLOAT32X2) return 8;
        if (format == UINT32X3 || format == SINT32X3 || format == FLOAT32X3) return 12;
        if (format == UINT32X4 || format == SINT32X4 || format == FLOAT32X4) return 16;
        throw new IllegalArgumentException("Unknown vertex format: " + format);
    }
}