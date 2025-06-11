package org.wgpu4j.core;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.enums.TextureFormat;
import org.wgpu4j.enums.TextureDimension;
import org.wgpu4j.descriptors.TextureViewDescriptor;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Represents a GPU texture resource.
 */
public class Texture extends WgpuResource {

    static {
        WgpuNative.ensureLoaded();
    }

    protected Texture(MemorySegment handle) {
        super(handle);
    }

    /**
     * Gets the width of this texture using the native WGPU getter.
     */
    public int getWidth() {
        checkNotClosed();

        try {
            return webgpu_h.wgpuTextureGetWidth(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to get texture width", e);
        }
    }

    /**
     * Gets the height of this texture using the native WGPU getter.
     */
    public int getHeight() {
        checkNotClosed();

        try {
            return webgpu_h.wgpuTextureGetHeight(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to get texture height", e);
        }
    }

    /**
     * Gets the format of this texture using the native WGPU getter.
     */
    public TextureFormat getFormat() {
        checkNotClosed();

        try {
            int formatValue = webgpu_h.wgpuTextureGetFormat(handle);
            return TextureFormat.fromValue(formatValue);
        } catch (Exception e) {
            throw new WgpuException("Failed to get texture format", e);
        }
    }

    /**
     * Gets the depth or array layers of this texture using the native WGPU getter.
     */
    public int getDepthOrArrayLayers() {
        checkNotClosed();

        try {
            return webgpu_h.wgpuTextureGetDepthOrArrayLayers(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to get texture depth or array layers", e);
        }
    }

    /**
     * Gets the mip level count of this texture using the native WGPU getter.
     */
    public int getMipLevelCount() {
        checkNotClosed();

        try {
            return webgpu_h.wgpuTextureGetMipLevelCount(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to get texture mip level count", e);
        }
    }

    /**
     * Gets the sample count of this texture using the native WGPU getter.
     */
    public int getSampleCount() {
        checkNotClosed();

        try {
            return webgpu_h.wgpuTextureGetSampleCount(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to get texture sample count", e);
        }
    }

    /**
     * Gets the dimension of this texture using the native WGPU getter.
     */
    public TextureDimension getDimension() {
        checkNotClosed();

        try {
            int dimensionValue = webgpu_h.wgpuTextureGetDimension(handle);
            return TextureDimension.fromValue(dimensionValue);
        } catch (Exception e) {
            throw new WgpuException("Failed to get texture dimension", e);
        }
    }

    /**
     * Gets the usage flags of this texture using the native WGPU getter.
     */
    public long getUsage() {
        checkNotClosed();

        try {
            return webgpu_h.wgpuTextureGetUsage(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to get texture usage", e);
        }
    }

    /**
     * Creates a view into this texture with default settings.
     *
     * @return A new texture view
     */
    public TextureView createView() {
        checkNotClosed();

        try {
            MemorySegment textureViewHandle = webgpu_h.wgpuTextureCreateView(handle, MemorySegment.NULL);

            if (textureViewHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create texture view");
            }

            return new TextureView(textureViewHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to create texture view", e);
        }
    }

    /**
     * Creates a view into this texture with the specified descriptor.
     *
     * @param descriptor Configuration for the texture view
     * @return A new texture view
     */
    public TextureView createView(TextureViewDescriptor descriptor) {
        checkNotClosed();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment viewDesc = descriptor.toCStruct(arena);

            MemorySegment textureViewHandle = webgpu_h.wgpuTextureCreateView(handle, viewDesc);

            if (textureViewHandle.equals(MemorySegment.NULL)) {
                throw new WgpuException("Failed to create texture view");
            }

            return new TextureView(textureViewHandle);
        } catch (Exception e) {
            throw new WgpuException("Failed to create texture view", e);
        }
    }

    /**
     * Destroys the texture, making it invalid for use.
     * This should be called when the texture is no longer needed.
     */
    public void destroy() {
        checkNotClosed();

        try {
            webgpu_h.wgpuTextureDestroy(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to destroy texture", e);
        }
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuTextureRelease(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to release texture", e);
        }
    }

    @Override
    public String toString() {
        if (isClosed()) {
            return String.format("Texture[handle=%s, closed=true]",
                    handle == null ? "null" : "0x" + Long.toHexString(handle.address()));
        }
        try {
            return String.format("Texture[handle=%s, %dx%d, format=%s, mips=%d, samples=%d, dimension=%s, usage=0x%x, closed=%s]",
                    handle == null ? "null" : "0x" + Long.toHexString(handle.address()),
                    getWidth(), getHeight(), getFormat(), getMipLevelCount(), getSampleCount(),
                    getDimension(), getUsage(), false);
        } catch (Exception e) {
            return String.format("Texture[handle=%s, closed=%s, error=%s]",
                    handle == null ? "null" : "0x" + Long.toHexString(handle.address()),
                    isClosed(), e.getMessage());
        }
    }
}