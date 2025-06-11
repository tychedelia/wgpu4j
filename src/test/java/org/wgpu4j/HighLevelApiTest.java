package org.wgpu4j;

import org.junit.jupiter.api.Test;
import org.wgpu4j.core.Instance;
import org.wgpu4j.descriptors.AdapterRequestOptions;
import org.wgpu4j.descriptors.TextureDescriptor;
import org.wgpu4j.enums.PowerPreference;
import org.wgpu4j.enums.TextureFormat;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that demonstrates the high-level, idiomatic Java API.
 */
class HighLevelApiTest {

    @Test
    void testInstanceCreation() {
        try (Instance instance = Instance.create()) {
            assertNotNull(instance);
            assertFalse(instance.isClosed());

            assertDoesNotThrow(instance::processEvents);

            System.out.println("High-level Instance API works");
        }

    }

    @Test
    void testResourceManagement() {
        Instance instance = Instance.create();
        assertFalse(instance.isClosed());

        instance.close();
        assertTrue(instance.isClosed());

        assertThrows(IllegalStateException.class, instance::processEvents);

        System.out.println("Resource management works correctly");
    }


    @Test
    void testBuilderPatterns() {
        assertDoesNotThrow(() -> {
            var textureDesc = TextureDescriptor.builder()
                    .size(512, 512)
                    .format(TextureFormat.BGRA8_UNORM)
                    .usage(0x10)
                    .build();

            assertEquals(512, textureDesc.getWidth());
            assertEquals(512, textureDesc.getHeight());
            assertEquals(TextureFormat.BGRA8_UNORM, textureDesc.getFormat());

            var adapterOptions = AdapterRequestOptions.builder()
                    .powerPreference(PowerPreference.HIGH_PERFORMANCE)
                    .build();

            assertEquals(PowerPreference.HIGH_PERFORMANCE, adapterOptions.getPowerPreference());

            System.out.println("Builder patterns work correctly");
        });
    }
}