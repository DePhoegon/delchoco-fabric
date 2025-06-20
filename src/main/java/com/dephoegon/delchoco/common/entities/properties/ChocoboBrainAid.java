package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static com.dephoegon.delchoco.common.init.ModItems.*;
import static com.dephoegon.delchoco.common.init.ModItems.DEAD_PEPPER;
import static com.dephoegon.delchoco.common.init.ModItems.GYSAHL_GREEN_SEEDS;
import static com.dephoegon.delchoco.common.init.ModItems.PICKLED_GYSAHL_RAW;
import static com.dephoegon.delchoco.common.init.ModItems.PINK_GYSAHL_GREEN;
import static com.dephoegon.delchoco.common.init.ModItems.SPIKE_FRUIT;

public class ChocoboBrainAid {
    // Constants for entity type classifications
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
     * Checks if the entity is invalid for all chocobos to target
     * @param entity The entity to check
     * @return true if the entity is not a valid attack target for all chocobos
     */
    public static boolean isInvalidForAllChocobos(@Nullable Entity entity) {
        if (entity == null) return true;
        if (!(entity instanceof LivingEntity)) return true;
        if (((LivingEntity) entity).isDead()) return true;

        for (Class<?> invalidClass : invalidRevengeTargetsForAll) {
            if (invalidClass.isInstance(entity)) return true;
        }

        return false;
    }

    /**
     * Checks if the entity requires swimming abilities to target
     * @param entity The entity to check
     * @return true if the entity can only be targeted by swimming chocobos
     */
    public static boolean requiresSwimmingToTarget(@Nullable Entity entity) {
        if (entity == null) return false;

        for (Class<?> swimmingClass : swimmingRequiredRevengeTargets) {
            if (swimmingClass.isInstance(entity)) return true;
        }

        return false;
    }

    /**
     * Checks if the entity requires having flying abilities to target
     * @param entity The entity to check
     * @return true if the entity can only be targeted by flying chocobos
     */
    public static boolean requiresFlyingToTarget(@Nullable Entity entity) {
        if (entity == null) return false;

        for (Class<?> flyingClass : flyingRequiredRevengeTargets) {
            if (flyingClass.isInstance(entity)) return true;
        }

        return false;
    }

    /**
     * Determines if an entity is attackable by a chocobo based on its abilities
     * @param target The entity to check
     * @param chocoboWaterWalk Chocobo is able to walk on water (can't swim)
     * @return true if the entity is attackable by the chocobo
     */
    @SuppressWarnings("RedundantIfStatement")
    public static boolean isAttackable(@Nullable Entity target, boolean chocoboWaterWalk) {
        if (isInvalidForAllChocobos(target)) return false;
        if (requiresFlyingToTarget(target)) return false;
        if (chocoboWaterWalk && requiresSwimmingToTarget(target)) return false;

        return true;
    }

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

    /**
     * Checks if a target is compatible with a chocobo's abilities for alerting purposes
     * @param target The target to check
     * @param canSwim Whether the chocobo can swim (opposite of canWalkOnWater)
     * @return true if the target is compatible with the chocobo's abilities
     */
    public static boolean isTargetCompatibleWithAbilities(@Nullable Entity target, boolean canSwim) {
        if (isInvalidForAllChocobos(target)) return false;

        // Check swimming requirement
        if (!canSwim && requiresSwimmingToTarget(target)) return false;

        // Check flying requirement (currently always false as no chocobos can fly)
        if (requiresFlyingToTarget(target)) return false;

        return true;
    }

    public static ImmutableList<ItemStack> ChocoboTemptItems() {
        return ImmutableList.of(
                new ItemStack(GYSAHL_GREEN_ITEM),
                new ItemStack(LOVELY_GYSAHL_GREEN),
                new ItemStack(GOLDEN_GYSAHL_GREEN),
                new ItemStack(PINK_GYSAHL_GREEN),
                new ItemStack(DEAD_PEPPER),
                new ItemStack(SPIKE_FRUIT),
                new ItemStack(PICKLED_GYSAHL_RAW),
                new ItemStack(GYSAHL_GREEN_SEEDS)
        );
    }
}