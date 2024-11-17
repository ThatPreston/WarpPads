package io.github.thatpreston.warppads;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientReloadShadersEvent;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.thatpreston.warppads.client.gui.WarpConfigScreen;
import io.github.thatpreston.warppads.client.gui.WarpSelectionScreen;
import io.github.thatpreston.warppads.client.render.WarpPadRenderer;
import io.github.thatpreston.warppads.particle.WarpParticle;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;

public class WarpPadsClient {
    private static ShaderInstance entityUnlitTranslucentShader;
    public static void init() {
        ParticleProviderRegistry.register(RegistryHandler.WARP_PARTICLE, WarpParticle.Provider::new);
        ClientLifecycleEvent.CLIENT_SETUP.register(minecraft -> {
            MenuRegistry.registerScreenFactory(RegistryHandler.WARP_SELECTION.get(), WarpSelectionScreen::new);
            MenuRegistry.registerScreenFactory(RegistryHandler.WARP_CONFIG.get(), WarpConfigScreen::new);
            BlockEntityRendererRegistry.register(RegistryHandler.WARP_PAD.get(), context -> new WarpPadRenderer());
        });
        ClientReloadShadersEvent.EVENT.register(WarpPadsClient::registerShaders);
    }
    private static void registerShaders(ResourceProvider provider, ClientReloadShadersEvent.ShadersSink sink) {
        try {
            sink.registerShader(new ShaderInstance(provider, "rendertype_entity_unlit_translucent", DefaultVertexFormat.NEW_ENTITY), shader -> entityUnlitTranslucentShader = shader);
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }
    public static ShaderInstance getEntityUnlitTranslucentShader() {
        return entityUnlitTranslucentShader;
    }
}