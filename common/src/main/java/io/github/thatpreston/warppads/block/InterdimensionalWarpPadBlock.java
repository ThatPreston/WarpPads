package io.github.thatpreston.warppads.block;

import io.github.thatpreston.warppads.server.WarpPadData;
import io.github.thatpreston.warppads.server.WarpPadInfoGroup;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class InterdimensionalWarpPadBlock extends WarpPadBlock {
    @Override
    public List<WarpPadInfoGroup> getAvailableGroups(ServerLevel level) {
        return WarpPadData.get(level).getAllGroups();
    }
}