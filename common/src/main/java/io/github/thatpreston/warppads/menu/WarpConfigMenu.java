package io.github.thatpreston.warppads.menu;

import io.github.thatpreston.warppads.RegistryHandler;
import io.github.thatpreston.warppads.network.EditWarpPad;
import io.github.thatpreston.warppads.network.PacketHandler;
import io.github.thatpreston.warppads.server.WarpPadInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

public class WarpConfigMenu extends AbstractContainerMenu {
    public static final Component TITLE = Component.translatable("container.warppads.warp_config");
    private final ContainerLevelAccess levelAccess;
    private final WarpPadInfo info;
    private final Slot dyeSlot;
    public WarpConfigMenu(int id, Inventory inventory, ContainerLevelAccess levelAccess, WarpPadInfo info, Container container) {
        super(RegistryHandler.WARP_CONFIG.get(), id);
        this.levelAccess = levelAccess;
        this.info = info;
        this.dyeSlot = this.addSlot(new DyeSlot(container, 0, 126, 19));
        for(int x = 0; x < 9; x++) {
            for(int y = 0; y < 3; y++) {
                this.addSlot(new Slot(inventory, x + 9 * (y + 1), 8 + x * 18, 60 + y * 18));
            }
        }
        for(int x = 0; x < 9; x++) {
            this.addSlot(new Slot(inventory, x, 8 + x * 18, 118));
        }
    }
    public WarpConfigMenu(int id, Inventory inventory, FriendlyByteBuf data) {
        this(id, inventory, ContainerLevelAccess.NULL, new WarpPadInfo(data), new SimpleContainer(1));
    }
    public static MenuProvider getMenuProvider(WarpPadInfo info, Container container) {
        return new SimpleMenuProvider((id, inventory, player) -> new WarpConfigMenu(id, inventory, ContainerLevelAccess.create(player.level(), info.getPos()), info, container), TITLE);
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if(slot.hasItem()) {
            ItemStack stack = slot.getItem();
            if(index == 0) {
                if(!moveItemStackTo(stack, 1, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if(stack.getItem() instanceof DyeItem && !dyeSlot.hasItem()) {
                if(!moveItemStackTo(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }
        return ItemStack.EMPTY;
    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(levelAccess, player, RegistryHandler.WARP_PAD_BLOCK.get()) || stillValid(levelAccess, player, RegistryHandler.INTERDIMENSIONAL_WARP_PAD_BLOCK.get());
    }
    public WarpPadInfo getInfo() {
        return info;
    }
    public void saveChanges(String name, int priority) {
        if(!name.isEmpty() && (!name.equals(info.getName()) || priority != info.getPriority())) {
            PacketHandler.CHANNEL.sendToServer(new EditWarpPad(info.getPos(), name, priority));
        }
    }
    public boolean hasDye() {
        return !dyeSlot.getItem().isEmpty();
    }
    private static class DyeSlot extends Slot {
        public DyeSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }
        @Override
        public int getMaxStackSize() {
            return 1;
        }
        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof DyeItem;
        }
    }
}