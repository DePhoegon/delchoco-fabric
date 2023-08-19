package com.dephoegon.delchoco.common.entities.breeding;

import com.dephoegon.delchoco.common.blockentities.ChocoboEggBlockEntity;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

public class ChocoboMateGoal extends Goal {
    @Override
    public boolean canStart() {
        return false;
    }
    private final static Vec3i[] LAY_EGG_CHECK_OFFSETS = {
            new Vec3i(0, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(-1, 0, -1),
            new Vec3i(0, 0, -1), new Vec3i(+1, 0, -1), new Vec3i(+1, 0, 0),
            new Vec3i(+1, 0, +1), new Vec3i(0, 0, +1), new Vec3i(-1, 0, +1),
            new Vec3i(0, 1, 0), new Vec3i(-1, 1, 0), new Vec3i(-1, 1, -1),
            new Vec3i(0, 1, -1), new Vec3i(+1, 1, -1), new Vec3i(+1, 1, 0),
            new Vec3i(+1, 1, +1), new Vec3i(0, 1, +1), new Vec3i(-1, 1, +1),
    };

    private final Chocobo chocobo;
    private final World world;
    private final double moveSpeed;
    private Chocobo targetMate;
    private int spawnBabyDelay;

    public ChocoboMateGoal(@NotNull Chocobo chocobo, double moveSpeed) {
        this.chocobo = chocobo;
        this.world = chocobo.world;
        this.moveSpeed = moveSpeed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }
    public boolean canUse() { return this.chocobo.isInLove() && (this.targetMate = this.getNearbyMate()) != null; }
    public boolean canContinueToUse() { return this.targetMate.isAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60; }
    public void stop() {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }
    public void tick() {
        this.chocobo.getLookControl().lookAt(this.targetMate, 10.0F, (float) this.chocobo.getMaxLookPitchChange());
        this.chocobo.getNavigation().startMovingTo(this.targetMate, this.moveSpeed);
        ++this.spawnBabyDelay;
        if (this.spawnBabyDelay >= 60 && this.chocobo.squaredDistanceTo(this.targetMate) < 9.0D) { this.spawnEgg(); }
    }
    private Chocobo getNearbyMate() {
        List<Chocobo> list = this.world.getNonSpectatingEntities(Chocobo.class, this.chocobo.getBoundingBox().expand(8.0D));
        double dist = Double.MAX_VALUE;
        Chocobo closestMate = null;
        for (Chocobo entry : list) {
            if (this.chocobo.canMate(entry) && this.chocobo.squaredDistanceTo(entry) < dist) {
                closestMate = entry;
                dist = this.chocobo.squaredDistanceTo(entry);
            }
        }
        return closestMate;
    }
    private void spawnEgg() {
        if (this.chocobo.isMale()) return;
        this.chocobo.setAge(6000);
        this.targetMate.setAge(6000);
        this.chocobo.resetLove();
        this.targetMate.resetLove();

        BlockPos pos = this.chocobo.getBlockPos();
        for (Vec3i offset : LAY_EGG_CHECK_OFFSETS) {
            BlockPos offsetPos = pos.offset(offset);
            BlockState state = this.world.getBlockState(offsetPos);
            if (state.getMaterial().isReplaceable() && !state.getMaterial().isLiquid() && ModItems.CHOCOBO_EGG.get().canSurvive(state, this.world, offsetPos)) {
                if (!this.world.setBlockState(offsetPos, ModItems.CHOCOBO_EGG.get().defaultBlockState())) { return; }
                BlockEntity tile = this.world.getBlockEntity(offsetPos);
                if(tile instanceof ChocoboEggBlockEntity eggTile) { eggTile.setBreedInfo(new ChocoboBreedInfo(new ChocoboStatSnapshot(this.chocobo), new ChocoboStatSnapshot(this.targetMate))); }
                return;
            }
        }

    }
}
