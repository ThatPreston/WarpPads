package io.github.thatpreston.warppads.network;

import dev.architectury.networking.NetworkManager;
import io.github.thatpreston.warppads.block.WarpPadBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class WarpRequest {
    private final BlockPos fromPos;
    private final BlockPos toPos;
    private final ResourceKey<Level> toLevelKey;
    public WarpRequest(FriendlyByteBuf data) {
        this.fromPos = data.readBlockPos();
        this.toPos = data.readBlockPos();
        this.toLevelKey = data.readResourceKey(Registries.DIMENSION);
    }
    public WarpRequest(BlockPos fromPos, BlockPos toPos, ResourceKey<Level> toLevelKey) {
        this.fromPos = fromPos;
        this.toPos = toPos;
        this.toLevelKey = toLevelKey;
    }
    public void encode(FriendlyByteBuf data) {
        data.writeBlockPos(fromPos);
        data.writeBlockPos(toPos);
        data.writeResourceKey(toLevelKey);
    }
    public void apply(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();
        context.queue(() -> {
            Player player = context.getPlayer();
            if(player instanceof ServerPlayer serverPlayer) {
                WarpPadBlockEntity.handleWarpRequest(serverPlayer, fromPos, toPos, toLevelKey);
            }
        });
    }
}