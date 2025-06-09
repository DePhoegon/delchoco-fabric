package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;

public class ChocoboAids {
    /**
     * Increases the specified stat of a Chocobo by a given amount.
     *
     * @param chocobo   The Chocobo whose stat is to be increased.
     * @param statName  The name of the stat to increase (e.g., "speed", "stamina", "jump", "health").
     * @param amount    The amount by which to increase the stat.
     * @param playerEntity The player entity performing the action, used for tracking purposes.
     */         // removed stat (all) cost, no update applied
    private static final int STAMINA_COST = 16;       // removed stat (stamina) cost, no update applied
    private static final int HEALTH_COST = 8;           // cost for health update ("hp")
    private static final int ARMOR_TOUGHNESS_COST = 4;    // cost for armor toughness ("arm_tough")
    private static final int ARMOR_COST = 2;              // cost for armor update ("arm")
    private static final int STRENGTH_COST = 1;
    private static final int ALL_COST = STAMINA_COST + HEALTH_COST + ARMOR_TOUGHNESS_COST + ARMOR_COST + STRENGTH_COST;
    private static final String health = "hp";
    private static final String strength = "str";
    private static final String armor = "arm";
    private static final String armorTough = "arm_tough";
    private static final String dualDefense = "defences";
    private static final String flipDefense = "xDefence";
    public void increaseStat(Chocobo chocobo, String statName, int amount, PlayerEntity playerEntity) {
        if (chocobo == null || statName == null || playerEntity == null) return;
        if (amount <= 0) return; // Ensure amount is positive
        if (!chocobo.isAlive() || !chocobo.isTamed()) return; // Ensure chocobo is alive and tamed
        int statValue = 0;
        switch (statName.toLowerCase()) {
            case "all" -> statValue = ALL_COST;
            case "hp", "health" -> statValue = HEALTH_COST;
            case "attack" -> statValue = STRENGTH_COST;
            case "defense" -> statValue = ARMOR_COST;
            default -> { return; }
        }
    }
    private int statCount(int statValue, int amount) { return Math.min(statValue - amount, Integer.MAX_VALUE); }
    private void numberSplit(int value, PlayerEntity playerEntity, Chocobo chocobo, int amountIncrease) {
        if (value <= 0 || playerEntity == null) return; // Ensure value is positive and player is not null
        int hold = value;
        if (statCount(hold, STAMINA_COST) > 0) {
            // Removed stat (Stamina), placeholder for future use
            hold -= STAMINA_COST;
        }
        if (statCount(hold, HEALTH_COST) > 0) {
            playerEntity.sendMessage(ChocoboColor.YELLOW.getEggText().append(" " + health + ": +" + HEALTH_COST), true);
            hold -= HEALTH_COST;
            statSwitch(health, playerEntity, chocobo, amountIncrease);
        }
    }
    private void statSwitch(String key, PlayerEntity playerEntity, Chocobo chocobo, int amountIncrease) {
        if (key == null || playerEntity == null || chocobo == null) return; // Ensure key, player, and chocobo are not null
        switch (key.toLowerCase()) {
            case "hp" -> statPlus(EntityAttributes.GENERIC_MAX_HEALTH, ChocoboConfig.MAX_HEALTH.get(), key, playerEntity, chocobo, amountIncrease);
            case "attack" -> {
                // Logic to increase attack strength
            }
            case "defense" -> {
                // Logic to increase defense
            }
            default -> {
                // Handle unknown stat
            }
        }
    }
    private void statPlus(EntityAttribute stat, double max, String key, PlayerEntity playerEntity, Chocobo chocobo, int amountIncrease) {
        if (playerEntity.getWorld().isClient()) { return; }
        if (chocobo == null || stat == null || key == null) return; // Ensure stat, player, Chocobo, and key are not null
        double currentValue = chocobo.getAttributeInstance(stat) != null ? Objects.requireNonNull(chocobo.getAttributeInstance(stat)).getValue() : -10;

        // allows for once over Max, but not below 0
        boolean trip = currentValue > max;

        if (!trip && currentValue > 0) {
            Objects.requireNonNull(chocobo.getAttributeInstance(stat)).addPersistentModifier(new EntityAttributeModifier(stat + " food", amountIncrease, EntityAttributeModifier.Operation.ADDITION));
        }

        String keys = ".entity_chocobo." + key;
        if (trip) { keys = keys + ".full"; }
        else { keys = keys + ".room"; }
        playerEntity.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + keys, chocobo.getCustomName()));
    }
}