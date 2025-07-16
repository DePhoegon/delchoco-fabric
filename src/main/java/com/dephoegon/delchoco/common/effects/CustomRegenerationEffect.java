package com.dephoegon.delchoco.common.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.util.Objects;

/**
 * Custom Regeneration effect that behaves like Minecraft's Regeneration but with enhanced healing.
 * The amplifier increases both the frequency of healing and the amount healed per tick.
 *
 * Healing progression:
 * - Amplifier 0: Heals 1 health point (0.5 hearts) every 50 ticks (2.5 seconds)
 * * - Amplifier 1: Heals 2 health points (1 heart) every 45 ticks (2.25 seconds)
 * * - Amplifier 2: Heals 3 health points (1.5 hearts) every 40 ticks (2 seconds)
 * * - Amplifier 3: Heals 4 health points (2 hearts) every 35 ticks (1.75 seconds)
 * ...
 * * - Amplifier 9: Heals 10 health points (5 hearts) every 5 ticks (0.25 seconds)
 * * - Amplifier 10: Heals 11 health points (5.5 hearts) every 5 ticks (0.25 seconds)
 * - And so on...
 */
public class CustomRegenerationEffect extends StatusEffect {

    public CustomRegenerationEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xCD5CAE); // Pink color similar to vanilla regeneration
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getHealth() < entity.getMaxHealth()) {
            // Calculate healing amount based on amplifier
            float healingAmount = calculateHealingAmount(amplifier);
            entity.heal(healingAmount);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // Use the same tick intervals as vanilla regeneration
        // Higher amplifiers heal more frequently
        int tickInterval = getTickInterval(amplifier);
        return duration % tickInterval == 0;
    }

    /**
     * Calculate the healing amount based on the amplifier level.
     * Every 2-4 amplifier levels increase healing by 0.5 hearts (1 health point).
     *
     * @param amplifier The effect amplifier (0-based)
     * @return The healing amount in health points (1 point = 0.5 hearts)
     */
    private float calculateHealingAmount(int amplifier) {
        // Base healing is 1 health point (0.5 hearts)
        float baseHealing = 1.0f;

        float bonusHealing = amplifier * 1.0f; // Each bonus level adds 1 health point

        return baseHealing + bonusHealing;
    }
    /**
     * Get the bonus amplification based on the entity's maximum health.
     * This determines how strong the regeneration effect will be.
     * Targeted to full heal in approximately 60 seconds with a range of 40-100 seconds for entities.
     * For use with this CustomRegenerationEffect, which scales healing based on amplifier.
     *
     * @param entity The living entity to check, Must not be null and have generic max health attribute.
     * @return The amplifier level (0-based), uncapped with a minimum of 1.
     */
    public static int getBonusAmplification(LivingEntity entity) {
        double hp = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH) != null ? Objects.requireNonNull(entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).getValue() : 0f;

        int amplifier;
        if (hp <= 65.0) { amplifier = 1; }
        else if (hp <= 100.0) { amplifier = 2; }
        else if (hp <= 150.0) { amplifier = 3; }
        else if (hp <= 200.0) { amplifier = 4; }
        else if (hp <= 450.0) {
            amplifier = Math.max(4, (int) Math.round(4 + (hp - 200.0) / 100.0));
        } else if (hp <= 640.0) {
            amplifier = Math.max(6, (int) Math.round(6 + (hp - 450.0) / 100.0));
            if (hp >= 600) { amplifier -= 1; }
        } else if (hp <= 2000.0) {
            amplifier = Math.max(7, (int) Math.round(7 + (hp - 450.0) / 350.0));
            if (hp < 860) { amplifier -= 1; }
            if (hp >= 971 && hp < 980) { amplifier -= 1; }
            if (hp >= 980) { amplifier -= 1; }
            if (hp >= 1321) { amplifier -= 1; }
        } else if(hp <= 3000) {
            int baseAmp = 0;
            amplifier = Math.max(10, (int) Math.round(10 + (hp - 1500.0) / 400.0))+ baseAmp;
        } else {
            int baseAmpAdjust = 0;
            double targetRate = hp / 60.0;
            double requiredAmp = (targetRate / 4.0) - 1.0;
            amplifier = Math.max(0, (int) Math.round(requiredAmp) + baseAmpAdjust);
        }
        amplifier = Math.max(1, amplifier);
        return amplifier;
    }

    /**
     * Get the tick interval for healing based on amplifier level.
     * Higher amplifiers heal more frequently.
     *
     * @param amplifier The effect amplifier (0-based)
     * @return The number of ticks between healing applications
     */
    private int getTickInterval(int amplifier) {
        // Base interval is 50 ticks (2.5 seconds) like vanilla regeneration
        // Higher amplifiers reduce the interval for more frequent healing
        int baseInterval = 50;
        int reduction = amplifier * 5; // Reduce by 5 ticks per amplifier level

        // Minimum interval of 5 ticks (0.25 seconds) to prevent too frequent healing
        return Math.max(5, baseInterval - reduction);
    }

    @Override
    public boolean isInstant() {
        return false;
    }

    /**
     * Get a user-friendly description of the effect's current strength
     *
     * @param amplifier The effect amplifier
     * @return A description string
     */
    public String getEffectDescription(int amplifier) {
        float healingAmount = calculateHealingAmount(amplifier);
        int tickInterval = getTickInterval(amplifier);

        float heartsPerTick = healingAmount / 2.0f;
        float secondsPerTick = tickInterval / 20.0f;

        return String.format("Heals %.1f hearts every %.1f seconds", heartsPerTick, secondsPerTick);
    }
}