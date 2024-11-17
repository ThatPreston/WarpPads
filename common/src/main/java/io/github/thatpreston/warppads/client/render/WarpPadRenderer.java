package io.github.thatpreston.warppads.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.thatpreston.warppads.block.WarpPadBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WarpPadRenderer implements BlockEntityRenderer<WarpPadBlockEntity> {
    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("warppads", "textures/warp_beam.png");
    @Override
    public void render(WarpPadBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        if(entity.shouldRender()) {
            float animation = entity.getAnimation() + partialTicks;
            float y1 = 0;
            float y2 = 6;
            float v = 0.75F;
            if(animation <= 10) {
                y2 = Mth.lerp(animation / 10, 0, y2);
            } else if(animation >= 30 && animation <= 40) {
                animation -= 30;
                y1 = Mth.lerp(animation / 10, 0, y2);
                v = Mth.clampedLerp(0.75F, 1, animation / 5);
            } else if(animation > 40) {
                return;
            }
            float[] beamColor = entity.getBeamColor();
            stack.pushPose();
            stack.translate(0.5F, 1, 0.5F);
            VertexConsumer consumer = source.getBuffer(WarpPadsRenderTypes.getUnlitTranslucent(BEAM_TEXTURE));
            renderBeam(stack, consumer, 1.5F, y1, y2, v, beamColor[0], beamColor[1], beamColor[2]);
            stack.popPose();
        }
    }
    private void renderBeam(PoseStack stack, VertexConsumer consumer, float radius, float y1, float y2, float v, float r, float g, float b) {
        PoseStack.Pose pose = stack.last();
        renderQuad(pose, consumer, -radius, radius, y1, y2, -radius, -radius, 0, 1, v, 0, 0, 0, -1, r, g, b);
        renderQuad(pose, consumer, radius, radius, y1, y2, -radius, radius, 0, 1, v, 0, 1, 0, 0, r, g, b);
        renderQuad(pose, consumer, radius, -radius, y1, y2, radius, radius, 0, 1, v, 0, 0, 0, 1, r, g, b);
        renderQuad(pose, consumer, -radius, -radius, y1, y2, radius, -radius, 0, 1, v, 0, -1, 0, 0, r, g, b);
    }
    private void renderQuad(PoseStack.Pose pose, VertexConsumer consumer, float x1, float x2, float y1, float y2, float z1, float z2, float u1, float u2, float v1, float v2, float nx, float ny, float nz, float r, float g, float b) {
        renderVertex(consumer, pose, x1, y1, z1, u1, v1, nx, ny, nz, r, g, b);
        renderVertex(consumer, pose, x1, y2, z1, u1, v2, nx, ny, nz, r, g, b);
        renderVertex(consumer, pose, x2, y2, z2, u2, v2, nx, ny, nz, r, g, b);
        renderVertex(consumer, pose, x2, y1, z2, u2, v1, nx, ny, nz, r, g, b);
    }
    private void renderVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, float nx, float ny, float nz, float r, float g, float b) {
        consumer.vertex(pose.pose(), x, y, z).color(r, g, b, 0.5F).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), nx, ny, nz).endVertex();
    }
}