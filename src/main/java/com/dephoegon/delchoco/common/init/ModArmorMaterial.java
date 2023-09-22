package com.dephoegon.delchoco.common.init;

import com.google.common.collect.Maps;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public enum ModArmorMaterial implements ArmorMaterial {
    LEATHER_CHOCO_DISGUISE("delchoco:leather_choco_disguise", 10, new int[] { 3, 4, 5, 3 }, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.5F, 0.0F, () -> Ingredient.ofItems(ModItems.CHOCOBO_FEATHER, Items.LEATHER)),
    IRON_CHOCO_DISGUISE("delchoco:iron_choco_disguise", 30, new int[] { 4, 7, 8, 4 }, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.5F, 0.0F, () -> Ingredient.ofItems(ModItems.CHOCOBO_FEATHER, Items.IRON_INGOT)),
    DIAMOND_CHOCO_DISGUISE("delchoco:diamond_choco_disguise", 66, new int[] { 5, 8, 10, 5 }, 15, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.5F, 0.0F, () -> Ingredient.ofItems(ModItems.CHOCOBO_FEATHER, Items.DIAMOND)),
    NETHERITE_CHOCO_DISGUISE("delchoco:netherite_choco_disguise", 68, new int[] { 5, 8, 10, 5 }, 15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3.5F, 0.1F, () -> Ingredient.ofItems(ModItems.CHOCOBO_FEATHER, Items.NETHERITE_INGOT));
    public static final Map<Integer, ArmorMaterial> CHOCO_ARMOR_MATERIALS = Util.make(Maps.newHashMap(), (map) -> {
        map.put(1, LEATHER_CHOCO_DISGUISE);
        map.put(2, IRON_CHOCO_DISGUISE);
        map.put(3, DIAMOND_CHOCO_DISGUISE);
        map.put(4, NETHERITE_CHOCO_DISGUISE);
    });
    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockBackResistance;
    private final Lazy<Ingredient> repairIngredient;

    @SuppressWarnings("SameParameterValue")
    ModArmorMaterial(String pName, int pDurabilityMultiplier, int[] pSlotProtections, int pEnchantmentValue, SoundEvent soundEvent, float pToughness, float pKnockBackResistance, Supplier<Ingredient> pRepairIngredient) {
        this.name = pName;
        this.durabilityMultiplier = pDurabilityMultiplier;
        this.slotProtections = pSlotProtections;
        this.enchantmentValue = pEnchantmentValue;
        this.sound = soundEvent;
        this.toughness = pToughness;
        this.knockBackResistance = pKnockBackResistance;
        this.repairIngredient = new Lazy<>(pRepairIngredient);
    }
    @Contract(pure = true)
    public int getDurability(@NotNull EquipmentSlot var1) { return HEALTH_PER_SLOT[var1.getEntitySlotId()] * this.durabilityMultiplier; }
    public int getProtectionAmount(@NotNull EquipmentSlot p_40487_) { return this.slotProtections[p_40487_.getEntitySlotId()]; }
    public int getEnchantability() { return this.enchantmentValue; }
    public @NotNull SoundEvent getEquipSound() { return this.sound; }
    public @NotNull Ingredient getRepairIngredient() { return this.repairIngredient.get(); }
    public @NotNull String getName() { return this.name; }
    public float getToughness() { return this.toughness; }
    public float getKnockbackResistance() { return this.knockBackResistance; }
}