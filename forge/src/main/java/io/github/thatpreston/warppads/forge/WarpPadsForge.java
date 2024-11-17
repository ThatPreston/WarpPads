package io.github.thatpreston.warppads.forge;

import dev.architectury.platform.forge.EventBuses;
import io.github.thatpreston.warppads.WarpPads;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WarpPads.MOD_ID)
public class WarpPadsForge {
    public WarpPadsForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(WarpPads.MOD_ID, eventBus);
        WarpPads.init();
    }
}