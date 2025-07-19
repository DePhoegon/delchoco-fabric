package com.dephoegon.delchoco.common.entities.pathing;

import com.dephoegon.delchoco.common.entities.AbstractChocobo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChocoboHybridNavigation extends AmphibiousSwimNavigation {
    AbstractChocobo chocobo;
    public ChocoboHybridNavigation(AbstractChocobo chocobo, World world) {
        super(chocobo, world);
        this.chocobo = chocobo;
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new ChocoboAmphibiousPathNodeMaker((AbstractChocobo) this.entity);
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, range);
    }

    @Override
    public boolean isValidPosition(BlockPos pos) {
        // Allow water surface if chocobo can walk on water
        if (((AbstractChocobo)this.entity).canWalkOnWater()) {
            if (this.entity.getWorld().getFluidState(pos).isIn(net.minecraft.registry.tag.FluidTags.WATER)) {
                // Only allow surface, not underwater
                BlockPos above = pos.up();
                if (this.entity.getWorld().isAir(above)) { return true; }
            }
        }
        if (this.chocobo.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) { return super.isValidPosition(pos); }
        return super.isValidPosition(pos) && !this.entity.getWorld().isAir(pos);
    }

    @Override
    public Path findPathTo(Entity target, int distance) {
        // Allow pathing to flying entities for combat
        if (target instanceof AbstractChocobo && this.entity.getWorld().isAir(target.getBlockPos())) { return null; }
        return super.findPathTo(target, distance);
    }

    @Override
    public Path findPathTo(BlockPos pos, int distance) {
        // Prevent idle/roam from picking air positions
        if (this.entity.getWorld().isAir(pos)) { return null; }
        return super.findPathTo(pos, distance);
    }
}