package org.wgpu4j.resource;

import org.wgpu4j.WgpuException;
import org.wgpu4j.WgpuNative;
import org.wgpu4j.WgpuResource;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * A view into a texture, used for rendering operations.
 */
public class TextureView extends WgpuResource {

    static {
        WgpuNative.ensureLoaded();
    }

    protected TextureView(MemorySegment handle) {
        super(handle);
    }

    @Override
    protected void releaseNative() {
        try {
            webgpu_h.wgpuTextureViewRelease(handle);
        } catch (Exception e) {
            throw new WgpuException("Failed to release texture view", e);
        }
    }
}