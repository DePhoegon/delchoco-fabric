package com.dephoegon.delchoco.common.entities.pathing;

import com.dephoegon.delchoco.common.entities.AbstractChocobo;
import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.registry.tag.FluidTags.LAVA;
import static net.minecraft.registry.tag.FluidTags.WATER;

public class ChocoboAmphibiousPathNodeMaker extends AmphibiousPathNodeMaker {
    private final AbstractChocobo chocobo;

    public ChocoboAmphibiousPathNodeMaker(AbstractChocobo chocobo) {
        super(false);
        this.chocobo = chocobo;
    }

    @Override
    public PathNodeType getDefaultNodeType(@NotNull BlockView world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        FluidState fluidState = world.getFluidState(pos);
        FluidState aboveFluidState = world.getFluidState(pos.up());
        if (this.chocobo.canWalkOnWater() && fluidState.isIn(WATER)) {
            if (aboveFluidState.isEmpty()) { return PathNodeType.WALKABLE; }
            else { return PathNodeType.BLOCKED; }
        }
        if (this.chocobo.isFireImmune() && fluidState.isIn(LAVA)) {
            if (aboveFluidState.isEmpty()) { return PathNodeType.WALKABLE; }
            else { return PathNodeType.BLOCKED; }
        }
        return super.getDefaultNodeType(world, x, y, z);
    }
}