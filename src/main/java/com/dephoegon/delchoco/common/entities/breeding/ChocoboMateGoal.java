package com.dephoegon.delchoco.common.entities.breeding;

import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

public class ChocoboMateGoal extends Goal {
    private final Chocobo chocobo;
    private final World world;
    private final double moveSpeed;
    private Chocobo targetMate;
    private int spawnBabyDelay;

    public ChocoboMateGoal(@NotNull Chocobo chocobo, double moveSpeed) {
        this.chocobo = chocobo;
        this.world = chocobo.getWorld();
        this.moveSpeed = moveSpeed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }
    public boolean canStart() { return this.chocobo.isInLove() && (this.targetMate = this.getNearbyMate()) != null; }
    public boolean shouldContinue() { return this.targetMate.isAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60; }
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
            if (this.chocobo.canBreedWith(entry) && this.chocobo.squaredDistanceTo(entry) < dist) {
                closestMate = entry;
                dist = this.chocobo.squaredDistanceTo(entry);
            }
        }
        return closestMate;
    }
    private void spawnEgg() {
        if (this.chocobo.isMale()) return;
        this.chocobo.setBreedingAge(6000);
        this.targetMate.setBreedingAge(6000);
        this.chocobo.resetLoveTicks();
        this.targetMate.resetLoveTicks();

        BlockPos pos = this.chocobo.getBlockPos();
        ChocoboBreedInfo breedInfo = new ChocoboBreedInfo(new ChocoboStatSnapshot(this.chocobo), new ChocoboStatSnapshot(this.targetMate));
        Chocobo baby = BreedingHelper.createChild(breedInfo, this.world);
        assert baby != null;
        baby.updatePositionAndAngles(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 0.0F, 0.0F);
        this.world.spawnEntity(baby);
        Random random = baby.getRandom();
        for (int i = 0; i < 7; ++i) {
            double d0 = random.nextGaussian() * 0.02D;
            double d1 = random.nextGaussian() * 0.02D;
            double d2 = random.nextGaussian() * 0.02D;
            double d3 = random.nextDouble() * baby.getWidth() * 2.0D - baby.getWidth();
            double d4 = 0.5D + random.nextDouble() * baby.getHeight();
            double d5 = random.nextDouble() * baby.getWidth() * 2.0D - baby.getWidth();
            this.world.addParticle(ParticleTypes.HEART, baby.getX() + d3, baby.getY() + d4, baby.getZ() + d5, d0, d1, d2);
        }
    }
}
