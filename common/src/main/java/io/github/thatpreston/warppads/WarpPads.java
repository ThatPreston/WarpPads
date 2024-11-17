package io.github.thatpreston.warppads;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import io.github.thatpreston.warppads.network.PacketHandler;

public class WarpPads {
    public static final String MOD_ID = "warppads";
    public static void init() {
        LifecycleEvent.SETUP.register(PacketHandler::registerPackets);
        if(Platform.getEnvironment() == Env.CLIENT) {
            WarpPadsClient.init();
        }
        RegistryHandler.register();
    }
}