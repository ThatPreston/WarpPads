package io.github.thatpreston.warppads.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.thatpreston.warppads.WarpPadsClient;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class WarpPadsRenderTypes {
    public static RenderType getUnlitTranslucent(ResourceLocation textureLocation) {
        return Internal.UNLIT_TRANSLUCENT.apply(textureLocation);
    }
    private static class Internal extends RenderType {
        private static final ShaderStateShard ENTITY_UNLIT_TRANSLUCENT_SHADER = new ShaderStateShard(WarpPadsClient::getEntityUnlitTranslucentShader);
        private Internal(String name, VertexFormat format, VertexFormat.Mode mode, int size, boolean crumbling, boolean sorting, Runnable onEnable, Runnable onDisable) {
            super(name, format, mode, size, crumbling, sorting, onEnable, onDisable);
        }
        public static Function<ResourceLocation, RenderType> UNLIT_TRANSLUCENT = Util.memoize(Internal::unlitTranslucent);
        private static RenderType unlitTranslucent(ResourceLocation textureLocation) {
            CompositeState renderState = CompositeState.builder()
                    .setShaderState(ENTITY_UNLIT_TRANSLUCENT_SHADER)
                    .setTextureState(new TextureStateShard(textureLocation, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("warppads_entity_unlit_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, renderState);
        }
    }
}