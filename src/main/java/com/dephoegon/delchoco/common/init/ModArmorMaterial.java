package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.aid.TieredMaterials;
import com.google.common.collect.Maps;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Lazy;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum ModArmorMaterial implements ArmorMaterial {
    CHAIN_CHOCO_DISGUISE("delchoco:chain_choco_disguise", TieredMaterials.ChocoboArmorTiers.CHAIN),
    REINFORCED_CHAIN_CHOCO_DISGUISE("delchoco:reinforced_chain_choco_disguise", TieredMaterials.ChocoboArmorTiers.REINFORCED_CHAIN),
    IRON_CHOCO_DISGUISE("delchoco:iron_choco_disguise", TieredMaterials.ChocoboArmorTiers.IRON),
    REINFORCED_IRON_CHOCO_DISGUISE("delchoco:reinforced_iron_choco_disguise", TieredMaterials.ChocoboArmorTiers.REINFORCED_IRON),
    DIAMOND_CHOCO_DISGUISE("delchoco:diamond_choco_disguise", TieredMaterials.ChocoboArmorTiers.DIAMOND),
    REINFORCED_DIAMOND_CHOCO_DISGUISE("delchoco:reinforced_diamond_choco_disguise", TieredMaterials.ChocoboArmorTiers.REINFORCED_DIAMOND),
    NETHERITE_CHOCO_DISGUISE("delchoco:netherite_choco_disguise", TieredMaterials.ChocoboArmorTiers.NETHERITE),
    REINFORCED_NETHERITE_CHOCO_DISGUISE("delchoco:reinforced_netherite_choco_disguise", TieredMaterials.ChocoboArmorTiers.REINFORCED_NETHERITE),
    GILDED_NETHERITE_CHOCO_DISGUISE("delchoco:gilded_netherite_choco_disguise", TieredMaterials.ChocoboArmorTiers.GILDED_NETHERITE);

    public static final Map<Integer, ArmorMaterial> CHOCO_ARMOR_MATERIALS = Util.make(Maps.newHashMap(), (map) -> {
        map.put(1, CHAIN_CHOCO_DISGUISE);
        map.put(2, REINFORCED_CHAIN_CHOCO_DISGUISE);
        map.put(3, IRON_CHOCO_DISGUISE);
        map.put(4, REINFORCED_IRON_CHOCO_DISGUISE);
        map.put(5, DIAMOND_CHOCO_DISGUISE);
        map.put(6, REINFORCED_DIAMOND_CHOCO_DISGUISE);
        map.put(7, NETHERITE_CHOCO_DISGUISE);
        map.put(8, REINFORCED_NETHERITE_CHOCO_DISGUISE);
        map.put(9, GILDED_NETHERITE_CHOCO_DISGUISE);
    });
    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockBackResistance;
    private final Lazy<Ingredient> repairIngredient;
    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};

    ModArmorMaterial(String name, TieredMaterials.ChocoboArmorTiers tier) {
        this.name = name;
        this.durabilityMultiplier = tier.getDurabilityMultiplier();
        this.slotProtections = new int[]{tier.getProtection(ArmorItem.Type.BOOTS), tier.getProtection(ArmorItem.Type.LEGGINGS), tier.getProtection(ArmorItem.Type.CHESTPLATE), tier.getProtection(ArmorItem.Type.HELMET)};
        this.enchantmentValue = tier.getEnchantability();
        this.sound = tier.getEquipSound();
        this.toughness = tier.getToughness();
        this.knockBackResistance = tier.getKnockbackResistance();
        this.repairIngredient = new Lazy<>(tier::getRepairIngredient);
    }

    @Contract(pure = true)
    public int getDurability(@NotNull ArmorItem.Type type) {
        return HEALTH_PER_SLOT[type.getEquipmentSlot().getEntitySlotId()] * this.durabilityMultiplier;
    }

    public int getProtection(@NotNull ArmorItem.Type type) {
        return this.slotProtections[type.getEquipmentSlot().getEntitySlotId()];
    }

    public int getEnchantability() {
        return this.enchantmentValue;
    }

    public @NotNull SoundEvent getEquipSound() {
        return this.sound;
    }

    public @NotNull Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    public @NotNull String getName() {
        return this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockBackResistance;
    }
}