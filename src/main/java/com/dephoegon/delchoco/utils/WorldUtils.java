package com.dephoegon.delchoco.utils;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class WorldUtils {
    public static int getDistanceToSurface(BlockPos startPos, @NotNull World world) {
        BlockPos lastLiquidPos = null;

        for (BlockPos pos = startPos; pos.getY() < world.getTopY(); pos = pos.up()) {
            BlockState state = world.getBlockState(pos);
            if (!state.getMaterial().isLiquid()) { break; }
            lastLiquidPos = pos;
        }
        return lastLiquidPos == null ? -1 : lastLiquidPos.getY() - startPos.getY();
    }
}