package org.wgpu4j.descriptor;

import org.wgpu4j.Marshalable;
import org.wgpu4j.bindings.*;
import org.wgpu4j.resource.Texture;
import org.wgpu4j.constant.TextureAspect;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Descriptor for copying data to a texture.
 */
public class ImageCopyTexture implements Marshalable {
    private final Texture texture;
    private final int mipLevel;
    private final int originX;
    private final int originY;
    private final int originZ;
    private final TextureAspect aspect;

    private ImageCopyTexture(Builder builder) {
        this.texture = builder.texture;
        this.mipLevel = builder.mipLevel;
        this.originX = builder.originX;
        this.originY = builder.originY;
        this.originZ = builder.originZ;
        this.aspect = builder.aspect;
    }

    /**
     * Creates a C struct representing this image copy texture descriptor.
     */
    public MemorySegment marshal(Arena arena) {
        MemorySegment struct = WGPUTexelCopyTextureInfo.allocate(arena);

        WGPUTexelCopyTextureInfo.texture(struct, texture.getHandle());

        WGPUTexelCopyTextureInfo.mipLevel(struct, mipLevel);

        MemorySegment origin = WGPUOrigin3D.allocate(arena);
        WGPUOrigin3D.x(origin, originX);
        WGPUOrigin3D.y(origin, originY);
        WGPUOrigin3D.z(origin, originZ);
        WGPUTexelCopyTextureInfo.origin(struct, origin);

        WGPUTexelCopyTextureInfo.aspect(struct, aspect.getValue());

        return struct;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Texture texture;
        private int mipLevel = 0;
        private int originX = 0;
        private int originY = 0;
        private int originZ = 0;
        private TextureAspect aspect = TextureAspect.ALL;

        /**
         * Sets the texture to copy to.
         */
        public Builder texture(Texture texture) {
            this.texture = texture;
            return this;
        }

        /**
         * Sets the mip level of the texture to copy to.
         */
        public Builder mipLevel(int mipLevel) {
            this.mipLevel = mipLevel;
            return this;
        }

        /**
         * Sets the origin coordinates for the copy.
         */
        public Builder origin(int x, int y, int z) {
            this.originX = x;
            this.originY = y;
            this.originZ = z;
            return this;
        }

        /**
         * Sets the origin coordinates for the copy (2D).
         */
        public Builder origin(int x, int y) {
            return origin(x, y, 0);
        }

        /**
         * Sets the texture aspect to copy to.
         */
        public Builder aspect(TextureAspect aspect) {
            this.aspect = aspect;
            return this;
        }

        public ImageCopyTexture build() {
            if (texture == null) {
                throw new IllegalStateException("Texture must be specified");
            }
            return new ImageCopyTexture(this);
        }
    }
}