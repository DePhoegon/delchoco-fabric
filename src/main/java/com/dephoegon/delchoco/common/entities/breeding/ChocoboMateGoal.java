package com.dephoegon.delchoco.common.entities.breeding;

import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

import static java.lang.Math.random;

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
        if (this.spawnBabyDelay >= 60 && this.chocobo.squaredDistanceTo(this.targetMate) < 9.0D) { this.spawnChicobo(); }
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
    private void spawnChicobo() {
        if (this.chocobo.isMale() && ((int)(random()*(100)+1)) <= 85) { return; } // ~15% chance of bonus chicobo.
        this.chocobo.setBreedingAge(6000);
        this.targetMate.setBreedingAge(6000);
        this.chocobo.resetLoveTicks();
        this.targetMate.resetLoveTicks();

        ServerWorld serverWorld = (ServerWorld) this.world;
        Chocobo baby = (Chocobo) this.chocobo.createChild(serverWorld, this.targetMate); // Create a baby Chocobo from the parents, safely casting to/from PassiveEntity
        if (baby != null) {
            this.chocobo.setBreedingAge(6000);
            this.targetMate.setBreedingAge(6000);
            this.chocobo.resetLoveTicks();
            this.targetMate.resetLoveTicks();
            baby.setBreedingAge(-24000);
            baby.refreshPositionAndAngles(this.chocobo.getX(), this.chocobo.getY(), this.chocobo.getZ(), 0.0f, 0.0f);
            this.world.spawnEntity(baby);
            this.world.sendEntityStatus(this.chocobo, (byte)18);
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.chocobo.getX(), this.chocobo.getY(), this.chocobo.getZ(), this.chocobo.getRandom().nextInt(7) + 1));
            }
        }
    }
}
