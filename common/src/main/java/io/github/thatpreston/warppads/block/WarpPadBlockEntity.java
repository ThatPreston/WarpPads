package io.github.thatpreston.warppads.block;

import io.github.thatpreston.warppads.RegistryHandler;
import io.github.thatpreston.warppads.WarpPadUtils;
import io.github.thatpreston.warppads.server.WarpPadData;
import io.github.thatpreston.warppads.server.WarpPadInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarpPadBlockEntity extends BaseContainerBlockEntity {
    private NonNullList<ItemStack> items;
    private boolean warping = false;
    private BlockPos targetPos;
    private ResourceKey<Level> targetLevelKey;
    private boolean render = false;
    private int animation = 0;
    private float[] beamColor;
    public WarpPadBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryHandler.WARP_PAD.get(), pos, state);
        items = NonNullList.withSize(1, ItemStack.EMPTY);
    }
    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.warppads.warp_selection");
    }
    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return null;
    }
    @Override
    public int getContainerSize() {
        return items.size();
    }
    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }
    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
    }
    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }
    @Override
    public void setItem(int slot, ItemStack stack) {
        if(slot >= 0 && slot < items.size()) {
            items.set(slot, stack);
        }
    }
    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }
    @Override
    public void clearContent() {
        items.clear();
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
        warping = tag.getBoolean("warping");
        if(warping) {
            render = true;
            animation = 0;
        }
        updateBeamColor();
    }
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
    }
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        ContainerHelper.saveAllItems(tag, items);
        tag.putBoolean("warping", warping);
        return tag;
    }
    public ServerLevel getTargetLevel(ServerLevel level) {
        return level.getServer().getLevel(targetLevelKey);
    }
    public void sync(ServerLevel level, BlockPos pos) {
        BlockState state = getBlockState();
        setChanged(level, pos, state);
        level.sendBlockUpdated(pos, state, state, 0);
    }
    public void scheduleTick(ServerLevel level, BlockPos pos, int delay) {
        level.scheduleTick(pos, getBlockState().getBlock(), delay);
    }
    private void warpOut(ServerPlayer player, ServerLevel level, BlockPos pos, BlockPos targetPos, ResourceKey<Level> targetLevelKey, ServerLevel targetLevel) {
        warping = true;
        sync(level, pos);
        level.playSound(null, pos, RegistryHandler.WARP_OUT_SOUND.get(), SoundSource.BLOCKS);
        targetLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(targetPos), 1, player.getId());
        scheduleTick(level, pos, 30);
        this.targetPos = targetPos;
        this.targetLevelKey = targetLevelKey;
    }
    private void teleport(ServerLevel level, BlockPos pos) {
        ServerLevel targetLevel = getTargetLevel(level);
        if(targetLevel != null) {
            AABB box = WarpPadUtils.getBoxAbovePosition(WarpPadUtils.getTopCenter(pos), 3, 6);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box);
            int players = 0;
            for(LivingEntity entity : entities) {
                double x = entity.xo - pos.getX() + targetPos.getX();
                double y = entity.yo - pos.getY() + targetPos.getY();
                double z = entity.zo - pos.getZ() + targetPos.getZ();
                entity.teleportTo(targetLevel, x, y, z, RelativeMovement.ALL, entity.getYRot(), entity.getXRot());
                if(entity instanceof ServerPlayer) {
                    players++;
                }
            }
            scheduleTick(level, pos, 10);
            if(players == 0 && !targetLevel.isLoaded(targetPos)) {
                cancelWarpIn(targetLevel);
            } else if(targetLevel.getBlockEntity(targetPos) instanceof WarpPadBlockEntity toPad) {
                toPad.tryWarpIn(targetLevel);
            }
        }
        targetPos = null;
        targetLevelKey = null;
    }
    public void cancelWarpIn(ServerLevel level) {
        if(targetPos != null) {
            WarpPadInfo info = WarpPadData.getGroup(level, targetLevelKey).getWarpPad(targetPos);
            if(info != null) {
                info.setWarping(false);
            }
        }
    }
    private void tryWarpIn(ServerLevel level) {
        WarpPadInfo info = WarpPadData.getGroup(level).getWarpPad(worldPosition);
        if(info != null && info.isWarping()) {
            warpIn(level, worldPosition);
            info.setWarping(false);
        }
    }
    private void warpIn(ServerLevel level, BlockPos pos) {
        warping = true;
        sync(level, pos);
        level.playSound(null, pos, RegistryHandler.WARP_IN_SOUND.get(), SoundSource.BLOCKS);
        scheduleTick(level, pos, 40);
    }
    private void setIdle(ServerLevel level, BlockPos pos) {
        warping = false;
        sync(level, pos);
    }
    public void handleScheduledTick(ServerLevel level, BlockPos pos) {
        if(targetPos != null) {
            teleport(level, pos);
        } else {
            setIdle(level, pos);
        }
    }
    public static void handleWarpRequest(ServerPlayer player, BlockPos fromPos, BlockPos toPos, ResourceKey<Level> toLevelKey) {
        ServerLevel level = player.serverLevel();
        BlockEntity fromEntity = level.getBlockEntity(fromPos);
        if(fromEntity instanceof WarpPadBlockEntity fromPad && !fromPad.isWarping()) {
            WarpPadInfo info = WarpPadData.get(level).getGroup(toLevelKey).getWarpPad(toPos);
            if(info != null && !info.isWarping()) {
                ServerLevel toLevel = level.getServer().getLevel(toLevelKey);
                if(toLevel != null) {
                    info.setWarping(true);
                    fromPad.warpOut(player, level, fromPos, toPos, toLevelKey, toLevel);
                }
            }
        }
    }
    public boolean isWarping() {
        return warping;
    }
    public boolean shouldRender() {
        return render;
    }
    public int getAnimation() {
        return animation;
    }
    public void tickAnimation(Level level, BlockPos pos) {
        animation++;
        if(render && animation < 30) {
            Vec3 top = WarpPadUtils.getTopCenter(pos);
            for(int i = 0; i < 6; i++) {
                Vec3 newPos = WarpPadUtils.getPositionOnSquare(top, 1.5F);
                level.addParticle(RegistryHandler.WARP_PARTICLE.get(), newPos.x, newPos.y + 0.1F, newPos.z, beamColor[0], beamColor[1], beamColor[2]);
            }
        } else if(animation > 40) {
            render = false;
        }
    }
    private void updateBeamColor() {
        ItemStack dye = items.get(0);
        if(!dye.isEmpty() && dye.getItem() instanceof DyeItem item) {
            float[] color = item.getDyeColor().getTextureDiffuseColors();
            beamColor = WarpPadUtils.brightenColor(color, 0.2F);
        } else {
            beamColor = new float[]{0.5F, 1, 1};
        }
    }
    public float[] getBeamColor() {
        return beamColor;
    }
}