package io.github.thatpreston.warppads.menu;

import io.github.thatpreston.warppads.RegistryHandler;
import io.github.thatpreston.warppads.network.PacketHandler;
import io.github.thatpreston.warppads.network.WarpRequest;
import io.github.thatpreston.warppads.server.WarpPadInfo;
import io.github.thatpreston.warppads.server.WarpPadInfoGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;

public class WarpSelectionMenu extends AbstractContainerMenu {
    public static final Component TITLE = Component.translatable("container.warppads.warp_selection");
    private final ContainerLevelAccess levelAccess;
    private final BlockPos pos;
    private final List<WarpOption> warpOptions;
    public WarpSelectionMenu(int id, ContainerLevelAccess levelAccess, BlockPos pos, List<WarpPadInfoGroup> warpPadGroups) {
        super(RegistryHandler.WARP_SELECTION.get(), id);
        this.levelAccess = levelAccess;
        this.pos = pos;
        this.warpOptions = new ArrayList<>();
        for(WarpPadInfoGroup group : warpPadGroups) {
            for(WarpPadInfo info : group.getWarpPads()) {
                if(!info.getPos().equals(pos)) {
                    warpOptions.add(new WarpOption(info, group.getLevelKey()));
                }
            }
        }
        warpOptions.sort(Comparator.comparingInt(WarpOption::getPriority).reversed());
    }
    public WarpSelectionMenu(int id, Inventory inventory, FriendlyByteBuf data) {
        this(id, ContainerLevelAccess.NULL, data.readBlockPos(), WarpPadInfoGroup.readList(data));
    }
    public static MenuProvider getMenuProvider(BlockPos pos, List<WarpPadInfoGroup> warpPadGroups) {
        return new SimpleMenuProvider((id, inventory, player) -> new WarpSelectionMenu(id, ContainerLevelAccess.create(player.level(), pos), pos, warpPadGroups), TITLE);
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(levelAccess, player, RegistryHandler.WARP_PAD_BLOCK.get()) || stillValid(levelAccess, player, RegistryHandler.INTERDIMENSIONAL_WARP_PAD_BLOCK.get());
    }
    public List<WarpOption> getWarpOptions() {
        return warpOptions;
    }
    public void selectWarpOption(WarpOption option) {
        PacketHandler.CHANNEL.sendToServer(new WarpRequest(pos, option.getPos(), option.levelKey()));
    }
    public record WarpOption(WarpPadInfo info, ResourceKey<Level> levelKey) {
        public BlockPos getPos() {
            return info.getPos();
        }
        public String getName() {
            return info.getName();
        }
        public int getPriority() {
            return info.getPriority();
        }
    }
}