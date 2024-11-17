package io.github.thatpreston.warppads.server;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;

public class WarpPadInfo {
    private final BlockPos pos;
    private final String name;
    private final int priority;
    private boolean warping;
    public WarpPadInfo(BlockPos pos, String name, int priority) {
        this.pos = pos;
        this.name = name;
        this.priority = priority;
    }
    public WarpPadInfo(CompoundTag tag) {
        pos = NbtUtils.readBlockPos(tag);
        name = tag.getString("name");
        priority = tag.getInt("priority");
    }
    public WarpPadInfo(FriendlyByteBuf data) {
        pos = data.readBlockPos();
        name = data.readUtf();
        priority = data.readInt();
    }
    public CompoundTag save() {
        CompoundTag tag = NbtUtils.writeBlockPos(pos);
        tag.putString("name", name);
        tag.putInt("priority", priority);
        return tag;
    }
    public void write(FriendlyByteBuf data) {
        data.writeBlockPos(pos);
        data.writeUtf(name);
        data.writeInt(priority);
    }
    public BlockPos getPos() {
        return pos;
    }
    public String getName() {
        return name;
    }
    public int getPriority() {
        return priority;
    }
    public boolean isWarping() {
        return warping;
    }
    public void setWarping(boolean warping) {
        this.warping = warping;
    }
}