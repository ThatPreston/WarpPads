package io.github.thatpreston.warppads.forge;

import net.minecraft.core.particles.SimpleParticleType;

public class RegistryHandlerImpl {
    public static SimpleParticleType createParticleType() {
        return new SimpleParticleType(false);
    }
}