package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.common.init.ModItems;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;

import java.util.function.Supplier;

public class TieredMaterials {
    public enum ChocoboToolTiers implements ToolMaterial {
        CHAIN("chain", 2, 200, 5.0f, 6.0f, 12, () -> Ingredient.ofItems(Items.IRON_INGOT)),
        REINFORCED_CHAIN("reinforced_chain", 2, 275, 5.5f, 7.0f, 15, () -> Ingredient.ofItems(ModItems.CHOCOBO_FEATHER)),
        IRON("iron", 2, 250, 6.0f, 8.0f, 14, () -> Ingredient.ofItems(Items.IRON_INGOT)),
        REINFORCED_IRON("reinforced_iron", 2, 375, 6.5f, 9.0f, 18, () -> Ingredient.ofItems(ModItems.CHOCOBO_LEATHER)),
        DIAMOND("diamond", 3, 1561, 8.0f, 10.0f, 10, () -> Ingredient.ofItems(Items.DIAMOND)),
        REINFORCED_DIAMOND("reinforced_diamond", 3, 1800, 8.5f, 11.0f, 12, () -> Ingredient.ofItems(ModItems.FEATHER_TREATED_LEATHER)),
        NETHERITE("netherite", 4, 2031, 9.0f, 12.0f, 15, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),
        REINFORCED_NETHERITE("reinforced_netherite", 4, 2250, 9.5f, 13.0f, 18, () -> Ingredient.ofItems(ModItems.DIAMOND_TREATED_FEATHER_LEATHER)),
        GILDED_NETHERITE("gilded_netherite", 4, 2500, 10.0f, 14.0f, 22, () -> Ingredient.ofItems(ModItems.NETHERITE_TREATED_FEATHER_LEATHER));

        private final String name;
        private final int miningLevel;
        private final int itemDurability;
        private final float miningSpeed;
        private final float attackDamage;
        private final int enchantability;
        private final Lazy<Ingredient> repairIngredient;

        ChocoboToolTiers(String name, int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
            this.name = name;
            this.miningLevel = miningLevel;
            this.itemDurability = itemDurability;
            this.miningSpeed = miningSpeed;
            this.attackDamage = attackDamage;
            this.enchantability = enchantability;
            this.repairIngredient = new Lazy<>(repairIngredient);
        }

        public String getName() { return this.name; }
        public int getDurability() { return this.itemDurability; }
        public float getMiningSpeedMultiplier() { return this.miningSpeed; }
        public float getAttackDamage() { return this.attackDamage; }
        public int getMiningLevel() { return this.miningLevel; }
        public int getEnchantability() { return this.enchantability; }
        public Ingredient getRepairIngredient() { return this.repairIngredient.get(); }
    }

    public enum ChocoboArmorTiers implements ArmorMaterial {
        CHAIN("chain", 15, new int[]{2, 4, 5, 1}, 12, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0f, 0.0f, () -> Ingredient.ofItems(Items.IRON_INGOT)),
        REINFORCED_CHAIN("reinforced_chain", 18, new int[]{2, 5, 6, 2}, 15, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.5f, 0.0f, () -> Ingredient.ofItems(ModItems.CHOCOBO_FEATHER)),
        IRON("iron", 25, new int[]{3, 7, 8, 3}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0f, 0.0f, () -> Ingredient.ofItems(Items.IRON_INGOT)),
        REINFORCED_IRON("reinforced_iron", 28, new int[]{3, 7, 9, 3}, 12, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0f, 0.0f, () -> Ingredient.ofItems(ModItems.CHOCOBO_LEATHER)),
        DIAMOND("diamond", 33, new int[]{5, 10, 13, 5}, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 5.0f, 0.0f, () -> Ingredient.ofItems(Items.DIAMOND)),
        REINFORCED_DIAMOND("reinforced_diamond", 35, new int[]{5, 10, 14, 5}, 12, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 5.5f, 0.05f, () -> Ingredient.ofItems(ModItems.FEATHER_TREATED_LEATHER)),
        NETHERITE("netherite", 37, new int[]{6, 13, 17, 6}, 15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 10.0f, 0.25f, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),
        REINFORCED_NETHERITE("reinforced_netherite", 39, new int[]{7, 14, 18, 7}, 18, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 11.0f, 0.3f, () -> Ingredient.ofItems(ModItems.DIAMOND_TREATED_FEATHER_LEATHER)),
        GILDED_NETHERITE("gilded_netherite", 42, new int[]{8, 15, 20, 8}, 22, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 12.0f, 0.35f, () -> Ingredient.ofItems(ModItems.NETHERITE_TREATED_FEATHER_LEATHER));

        private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
        private final String name;
        private final int durabilityMultiplier;
        private final int[] protectionAmounts;
        private final int enchantability;
        private final SoundEvent equipSound;
        private final float toughness;
        private final float knockbackResistance;
        private final Lazy<Ingredient> repairIngredient;

        ChocoboArmorTiers(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.protectionAmounts = protectionAmounts;
            this.enchantability = enchantability;
            this.equipSound = equipSound;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
            this.repairIngredient = new Lazy<>(repairIngredient);
        }

        @Override
        public int getDurability(ArmorItem.Type type) { return BASE_DURABILITY[type.getEquipmentSlot().getEntitySlotId()] * this.durabilityMultiplier; }
        public int getProtection(ArmorItem.Type type) { return this.protectionAmounts[type.getEquipmentSlot().getEntitySlotId()]; }
        public int getEnchantability() { return this.enchantability; }
        public SoundEvent getEquipSound() { return this.equipSound; }
        public Ingredient getRepairIngredient() { return this.repairIngredient.get(); }
        public String getName() { return this.name; }
        public float getToughness() { return this.toughness; }
        public float getKnockbackResistance() { return this.knockbackResistance; }
        public int getDurabilityMultiplier() { return this.durabilityMultiplier; }
    }
}
