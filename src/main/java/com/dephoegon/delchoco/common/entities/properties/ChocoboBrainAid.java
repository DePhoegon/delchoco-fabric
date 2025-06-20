package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.*;
import org.jetbrains.annotations.Nullable;

public class ChocoboBrainAid {
    public static boolean isAttackable(@Nullable Entity target, boolean chocoboWaterWalk) {
        if (!(target instanceof LivingEntity)) return false;
        if (target instanceof Chocobo) return false;
        if (target instanceof EnderDragonEntity) return false;
        if (target instanceof PhantomEntity) return false;
        if (target instanceof BatEntity) return false;
        if (target instanceof StriderEntity) return false;
        if (target instanceof GhastEntity) return false;

        if(!chocoboWaterWalk) {
            if (target instanceof DolphinEntity) return false;
            if (target instanceof GuardianEntity) return false;
            if (target instanceof PufferfishEntity) return false;
            if (target instanceof TropicalFishEntity) return false;
            if (target instanceof CodEntity) return false;
            if (target instanceof GlowSquidEntity) return false;
            if (target instanceof SquidEntity) return false;
            if (target instanceof SalmonEntity) return false;
        } // prevent chocobo from attacking water mobs if chocoboWaterWalk is true; No pathing available for chocobo to attack water mobs

        return !((LivingEntity) target).isDead();
    }
    private static final Class<?>[] invalidRevengeTargetsForAll = {
            EnderDragonEntity.class, Chocobo.class
    };
    private static final Class<?>[] flyingRequiredRevengeTargets = {
            PhantomEntity.class, BatEntity.class, StriderEntity.class, GhastEntity.class
    };
    private static final Class<?>[] swimmingRequiredRevengeTargets = {
            DolphinEntity.class, GuardianEntity.class, PufferfishEntity.class,
            TropicalFishEntity.class, CodEntity.class, GlowSquidEntity.class, SquidEntity.class,
            SalmonEntity.class
    };
    private static final Class<?>[] validRevengeAlly = {
            Chocobo.class
    };

    /**
     * Returns an array of entity classes that Chocobos should not revenge target based on abilities
     * @param cannotSwim The Chocobo cannot reach water-based entities (true if it can't swim/ aka it has water walking)
     * @param cannotFly The Chocobo cannot reach flying entities (currently always true)
     * @return Array of entity classes that should be excluded from revenge targeting
     */
    @SuppressWarnings("UnusedAssignment")
    public static Class<?>[] invalidRevengeTargets(boolean cannotSwim, boolean cannotFly) {
        // Calculate total array length based on ability flags
        int arrayLength = invalidRevengeTargetsForAll.length;
        if (cannotFly) { arrayLength += flyingRequiredRevengeTargets.length; }
        if (cannotSwim) { arrayLength += swimmingRequiredRevengeTargets.length; }

        // Create and populate the new array
        Class<?>[] invalidTargets = new Class<?>[arrayLength];
        int position = 0;

        // Always include base invalid targets
        System.arraycopy(invalidRevengeTargetsForAll, 0, invalidTargets, position, invalidRevengeTargetsForAll.length);
        position += invalidRevengeTargetsForAll.length;

        // Add conditional targets based on abilities
        if (cannotFly) {
            System.arraycopy(flyingRequiredRevengeTargets, 0, invalidTargets, position, flyingRequiredRevengeTargets.length);
            position += flyingRequiredRevengeTargets.length;
        }

        if (cannotSwim) {
            System.arraycopy(swimmingRequiredRevengeTargets, 0, invalidTargets, position, swimmingRequiredRevengeTargets.length);
            position += swimmingRequiredRevengeTargets.length;
        }

        return invalidTargets;
    }

    /**
     * Legacy method - automatically assumes chocobo cannot fly
     * @param cannotSwim The Chocobo cannot reach water-based entities
     * @return Array of entity classes that should be excluded from revenge targeting
     */
    public static Class<?>[] invalidRevengeTargets(boolean cannotSwim) {
        return invalidRevengeTargets(cannotSwim, true);
    }
    /**
     * Returns an array of entity classes that are valid Chocobo allies for revenge targeting
     * @return Array of entity classes that are valid Chocobo allies for revenge targeting
     */
    public static Class<?>[] validRevengeAllies() { return validRevengeAlly; }
}