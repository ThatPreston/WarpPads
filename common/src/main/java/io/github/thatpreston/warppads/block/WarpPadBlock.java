package io.github.thatpreston.warppads.block;

import dev.architectury.registry.menu.MenuRegistry;
import io.github.thatpreston.warppads.RegistryHandler;
import io.github.thatpreston.warppads.menu.WarpConfigMenu;
import io.github.thatpreston.warppads.menu.WarpSelectionMenu;
import io.github.thatpreston.warppads.server.WarpPadData;
import io.github.thatpreston.warppads.server.WarpPadInfo;
import io.github.thatpreston.warppads.server.WarpPadInfoGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarpPadBlock extends BaseEntityBlock {
    public WarpPadBlock() {
        super(Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof WarpPadBlockEntity warpPad) {
            if(level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                WarpPadInfoGroup group = WarpPadData.getGroup(serverLevel);
                if(!group.hasWarpPad(pos) || player.isCrouching()) {
                    WarpPadInfo info = group.getNewWarpPad(pos);
                    MenuProvider provider = WarpConfigMenu.getMenuProvider(info, warpPad);
                    MenuRegistry.openExtendedMenu(serverPlayer, provider, info::write);
                } else {
                    openSelectionMenu(serverPlayer, serverLevel, pos);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if(entity instanceof WarpPadBlockEntity warpPad) {
                warpPad.handleScheduledTick(level, pos);
            }
        }
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarpPadBlockEntity(pos, state);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof WarpPadBlockEntity warpPad) {
            if(level instanceof ServerLevel serverLevel) {
                warpPad.cancelWarpIn(serverLevel);
                WarpPadData.getGroup(serverLevel).removeWarpPad(pos);
            }
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), warpPad.getItem(0));
        }
        super.onRemove(state, level, pos, newState, moving);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level forLevel, BlockState forState, BlockEntityType<T> type) {
        return forLevel.isClientSide ? createTickerHelper(type, RegistryHandler.WARP_PAD.get(), (level, pos, state, entity) -> entity.tickAnimation(level, pos)) : null;
    }
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    private void openSelectionMenu(ServerPlayer player, ServerLevel level, BlockPos pos) {
        List<WarpPadInfoGroup> groups = getAvailableGroups(level);
        MenuProvider provider = WarpSelectionMenu.getMenuProvider(pos, groups);
        MenuRegistry.openExtendedMenu(player, provider, data -> {
            data.writeBlockPos(pos);
            data.writeInt(groups.size());
            for(WarpPadInfoGroup group : groups) {
                group.write(data);
            }
        });
    }
    public List<WarpPadInfoGroup> getAvailableGroups(ServerLevel level) {
        return List.of(WarpPadData.getGroup(level));
    }
}