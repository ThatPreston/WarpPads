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

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class WarpSelectionMenu extends AbstractContainerMenu {
    public static final Component TITLE = Component.translatable("container.warppads.warp_selection");
    private final ContainerLevelAccess levelAccess;
    private final BlockPos pos;
    private final Set<WarpOption> sortedWarpOptions;
    public WarpSelectionMenu(int id, ContainerLevelAccess levelAccess, BlockPos pos, List<WarpPadInfoGroup> warpPadGroups) {
        super(RegistryHandler.WARP_SELECTION.get(), id);
        this.levelAccess = levelAccess;
        this.pos = pos;
        this.sortedWarpOptions = new TreeSet<>(Comparator.comparingInt(WarpOption::getPriority).reversed());
        for(WarpPadInfoGroup group : warpPadGroups) {
            for(WarpPadInfo info : group.getWarpPads()) {
                if(!info.getPos().equals(pos)) {
                    sortedWarpOptions.add(new WarpOption(info, group.getLevelKey()));
                }
            }
        }
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
    public Set<WarpOption> getWarpOptions() {
        return sortedWarpOptions;
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