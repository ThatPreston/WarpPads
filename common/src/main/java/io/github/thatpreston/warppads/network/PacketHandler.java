package io.github.thatpreston.warppads.network;

import dev.architectury.networking.NetworkChannel;
import io.github.thatpreston.warppads.WarpPads;
import net.minecraft.resources.ResourceLocation;

public class PacketHandler {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(WarpPads.MOD_ID, "networking_channel"));
    public static void registerPackets() {
        CHANNEL.register(WarpRequest.class, WarpRequest::encode, WarpRequest::new, WarpRequest::apply);
        CHANNEL.register(EditWarpPad.class, EditWarpPad::encode, EditWarpPad::new, EditWarpPad::apply);
    }
}