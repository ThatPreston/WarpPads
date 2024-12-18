package io.github.thatpreston.warppads.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class WarpParticle extends TextureSheetParticle {
    public SpriteSet sprites;
    protected WarpParticle(ClientLevel level, double x, double y, double z, double r, double g, double b, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        setColor((float)r, (float)g, (float)b);
        quadSize = 0.3F;
        lifetime = 6;
        setSpriteFromAge(sprites);
    }
    @Override
    public void tick() {
        super.tick();
        if(!removed) {
            setSpriteFromAge(sprites);
        }
    }
    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double r, double g, double b) {
            return new WarpParticle(level, x, y, z, r, g, b, sprites);
        }
    }
}