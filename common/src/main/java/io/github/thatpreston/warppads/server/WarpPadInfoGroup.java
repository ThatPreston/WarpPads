package io.github.thatpreston.warppads.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.*;

public class WarpPadInfoGroup {
    private final List<WarpPadInfo> warpPadList = new ArrayList<>();
    private final ResourceKey<Level> levelKey;
    private Map<BlockPos, WarpPadInfo> warpPadMap;
    private boolean dirty;
    public WarpPadInfoGroup(ResourceKey<Level> key) {
        this.levelKey = key;
        this.warpPadMap = new HashMap<>();
    }
    public WarpPadInfoGroup(CompoundTag tag) {
        this(Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get("level")).result().orElse(Level.OVERWORLD));
        ListTag list = tag.getList("warpPads", Tag.TAG_COMPOUND);
        for(Tag listTag : list) {
            if(listTag instanceof CompoundTag infoTag) {
                WarpPadInfo info = new WarpPadInfo(infoTag);
                if(!warpPadMap.containsKey(info.getPos())) {
                    warpPadList.add(info);
                    warpPadMap.put(info.getPos(), info);
                }
            }
        }
    }
    public WarpPadInfoGroup(FriendlyByteBuf data) {
        levelKey = data.readResourceKey(Registries.DIMENSION);
        int count = data.readInt();
        for(int i = 0; i < count; i++) {
            warpPadList.add(new WarpPadInfo(data));
        }
    }
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, levelKey).result().ifPresent(key -> tag.put("level", key));
        ListTag list = new ListTag();
        for(WarpPadInfo info : warpPadList) {
            list.add(info.save());
        }
        tag.put("warpPads", list);
        return tag;
    }
    public void write(FriendlyByteBuf data) {
        data.writeResourceKey(levelKey);
        data.writeInt(warpPadList.size());
        for(WarpPadInfo info : warpPadList) {
            info.write(data);
        }
    }
    public static List<WarpPadInfoGroup> readList(FriendlyByteBuf data) {
        ArrayList<WarpPadInfoGroup> list = new ArrayList<>();
        int count = data.readInt();
        for(int i = 0; i < count; i++) {
            list.add(new WarpPadInfoGroup(data));
        }
        return list;
    }
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    public boolean isDirty() {
        return dirty;
    }
    public void addWarpPad(WarpPadInfo info) {
        WarpPadInfo old = warpPadMap.put(info.getPos(), info);
        if(old != null) {
            warpPadList.remove(old);
        }
        warpPadList.add(info);
        setDirty(true);
    }
    public void removeWarpPad(BlockPos pos) {
        WarpPadInfo info = warpPadMap.remove(pos);
        warpPadList.remove(info);
        setDirty(true);
    }
    public boolean hasWarpPad(BlockPos pos) {
        return warpPadMap.containsKey(pos);
    }
    public WarpPadInfo getWarpPad(BlockPos pos) {
        return warpPadMap.get(pos);
    }
    public WarpPadInfo getNewWarpPad(BlockPos pos) {
        WarpPadInfo info = getWarpPad(pos);
        return info != null ? info : new WarpPadInfo(pos, "", 0);
    }
    public List<WarpPadInfo> getWarpPads() {
        return warpPadList;
    }
    public ResourceKey<Level> getLevelKey() {
        return levelKey;
    }
}