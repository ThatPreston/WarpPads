package io.github.thatpreston.warppads.network;

import dev.architectury.networking.NetworkManager;
import io.github.thatpreston.warppads.server.WarpPadData;
import io.github.thatpreston.warppads.server.WarpPadInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public class EditWarpPad {
    private final BlockPos pos;
    private final String name;
    private final int priority;
    public EditWarpPad(FriendlyByteBuf data) {
        this.pos = data.readBlockPos();
        this.name = data.readUtf();
        this.priority = Mth.clamp(data.readInt(), 0, 99);
    }
    public EditWarpPad(BlockPos pos, String name, int priority) {
        this.pos = pos;
        this.name = name;
        this.priority = priority;
    }
    public void encode(FriendlyByteBuf data) {
        data.writeBlockPos(pos);
        data.writeUtf(name);
        data.writeInt(priority);
    }
    public void apply(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();
        context.queue(() -> {
            Player player = context.getPlayer();
            if(player instanceof ServerPlayer serverPlayer) {
                ServerLevel level = serverPlayer.serverLevel();
                WarpPadData.getGroup(level).addWarpPad(new WarpPadInfo(pos, name, priority));
            }
        });
    }
}