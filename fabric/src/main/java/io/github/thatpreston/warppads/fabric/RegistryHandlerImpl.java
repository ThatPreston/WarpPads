package io.github.thatpreston.warppads.fabric;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class RegistryHandlerImpl {
    public static SimpleParticleType createParticleType() {
        return FabricParticleTypes.simple();
    }
}