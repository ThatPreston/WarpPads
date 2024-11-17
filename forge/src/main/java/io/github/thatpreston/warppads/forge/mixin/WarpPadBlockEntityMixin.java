package io.github.thatpreston.warppads.forge.mixin;

import io.github.thatpreston.warppads.WarpPadUtils;
import io.github.thatpreston.warppads.block.WarpPadBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WarpPadBlockEntity.class)
public abstract class WarpPadBlockEntityMixin extends BaseContainerBlockEntity implements IForgeBlockEntity {
    protected WarpPadBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Override
    public AABB getRenderBoundingBox() {
        Vec3 pos = Vec3.atBottomCenterOf(getBlockPos());
        return WarpPadUtils.getBoxAbovePosition(pos, 3, 7);
    }
}