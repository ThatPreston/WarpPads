package io.github.thatpreston.warppads.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WarpPadData extends SavedData {
    private final HashMap<ResourceKey<Level>, WarpPadInfoGroup> groups = new HashMap<>();
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for(WarpPadInfoGroup group : groups.values()) {
            list.add(group.save());
        }
        tag.put("holders", list);
        return tag;
    }
    public static WarpPadData load(CompoundTag tag) {
        WarpPadData data = new WarpPadData();
        ListTag list = tag.getList("holders", Tag.TAG_COMPOUND);
        for(Tag listTag : list) {
            if(listTag instanceof CompoundTag group) {
                data.addGroup(new WarpPadInfoGroup(group));
            }
        }
        return data;
    }
    @Override
    public boolean isDirty() {
        return groups.values().stream().anyMatch(WarpPadInfoGroup::isDirty);
    }
    @Override
    public void setDirty(boolean dirty) {
        groups.values().forEach(group -> group.setDirty(dirty));
    }
    public List<WarpPadInfoGroup> getAllGroups() {
        return new ArrayList<>(groups.values());
    }
    private void addGroup(WarpPadInfoGroup group) {
        groups.put(group.getLevelKey(), group);
    }
    public WarpPadInfoGroup getGroup(ResourceKey<Level> levelKey) {
        return groups.computeIfAbsent(levelKey, WarpPadInfoGroup::new);
    }
    public static WarpPadInfoGroup getGroup(ServerLevel level) {
        return get(level).getGroup(level.dimension());
    }
    public static WarpPadInfoGroup getGroup(ServerLevel level, ResourceKey<Level> levelKey) {
        return get(level).getGroup(levelKey);
    }
    public static WarpPadData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(WarpPadData::load, WarpPadData::new, "warp_pads");
    }
}