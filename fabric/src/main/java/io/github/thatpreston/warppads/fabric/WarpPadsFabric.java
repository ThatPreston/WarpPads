package io.github.thatpreston.warppads.fabric;

import io.github.thatpreston.warppads.WarpPads;
import net.fabricmc.api.ModInitializer;

public class WarpPadsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        WarpPads.init();
    }
}