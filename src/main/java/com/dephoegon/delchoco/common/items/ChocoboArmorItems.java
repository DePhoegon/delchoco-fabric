package com.dephoegon.delchoco.common.items;

import com.dephoegon.delchoco.aid.TieredMaterials;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class ChocoboArmorItems extends ArmorItem {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    private final Multimap<EntityAttribute, EntityAttributeModifier> customModifiers;
    public static final Map<Integer, ArmorMaterial> CHOCOBO_ARMOR_MATERIALS = Util.make(Maps.newHashMap(), (map) -> {
       map.put(1, TieredMaterials.ChocoboArmorTiers.CHAIN);
       map.put(2, TieredMaterials.ChocoboArmorTiers.REINFORCED_CHAIN);
       map.put(3, TieredMaterials.ChocoboArmorTiers.IRON);
       map.put(4, TieredMaterials.ChocoboArmorTiers.REINFORCED_IRON);
       map.put(5, TieredMaterials.ChocoboArmorTiers.DIAMOND);
       map.put(6, TieredMaterials.ChocoboArmorTiers.REINFORCED_DIAMOND);
       map.put(7, TieredMaterials.ChocoboArmorTiers.NETHERITE);
       map.put(8, TieredMaterials.ChocoboArmorTiers.REINFORCED_NETHERITE);
       map.put(9, TieredMaterials.ChocoboArmorTiers.GILDED_NETHERITE);
    });
    private static final Map<ArmorMaterial, Integer> CHOCOBO_ARMOR_MATERIAL = Util.make(Maps.newHashMap(), (map) -> {for (int i = 1; i <= CHOCOBO_ARMOR_MATERIALS.size(); i++) { map.put(CHOCOBO_ARMOR_MATERIALS.get(i), i); }});

    public static Integer getTier(ArmorMaterial material) {
        return CHOCOBO_ARMOR_MATERIAL.get(material);
    }

    public static int getTotalDefense(@NotNull ArmorMaterial armor, Type slot) {
        if (!(armor instanceof TieredMaterials.ChocoboArmorTiers)) { return armor.getProtection(slot); }
        Integer tier = CHOCOBO_ARMOR_MATERIAL.get(armor);
        if (tier == null) { return armor.getProtection(slot); }
        int totalDefense = CHOCOBO_ARMOR_MATERIALS.get(tier).getProtection(slot);
        for (int i = tier - 1; i >= 1; i--) {
            totalDefense += CHOCOBO_ARMOR_MATERIALS.get(i).getProtection(slot) / 2;
        }
        return totalDefense;
    }
    public static float getTotalToughness(@NotNull ArmorMaterial armor) {
        if (!(armor instanceof TieredMaterials.ChocoboArmorTiers)) { return armor.getToughness(); }
        Integer tier = CHOCOBO_ARMOR_MATERIAL.get(armor);
        if (tier == null) { return armor.getToughness(); }
        float totalToughness = CHOCOBO_ARMOR_MATERIALS.get(tier).getToughness();
        for (int i = tier - 1; i >= 1; i--) {
            totalToughness += CHOCOBO_ARMOR_MATERIALS.get(i).getToughness() / 2;
        }
        return totalToughness;
    }
    public static float getTotalKnockbackResistance(@NotNull ArmorMaterial armor) {
        if (!(armor instanceof TieredMaterials.ChocoboArmorTiers)) { return armor.getKnockbackResistance(); }
        Integer tier = CHOCOBO_ARMOR_MATERIAL.get(armor);
        if (tier == null) { return armor.getKnockbackResistance(); }
        float totalKnockbackResistance = 0;
        for (int i = 1; i <= tier; i++) {
            ArmorMaterial currentMaterial = CHOCOBO_ARMOR_MATERIALS.get(i);
            if (currentMaterial.getKnockbackResistance() > 0) {
                totalKnockbackResistance += currentMaterial.getKnockbackResistance();
            }
        }
        return totalKnockbackResistance;
    }
    public ChocoboArmorItems(@NotNull ArmorMaterial pMaterial, Type pSlot, Item.@NotNull Settings settings) {
        super(pMaterial, pSlot, settings);
        int defense = getTotalDefense(pMaterial, pSlot);
        float toughness = getTotalToughness(pMaterial);
        float knockBackResistance = getTotalKnockbackResistance(pMaterial);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[pSlot.getEquipmentSlot().getEntitySlotId()];
        builder.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uuid, "Armor modifier", defense, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(uuid, "Armor toughness", toughness, EntityAttributeModifier.Operation.ADDITION));
        if (knockBackResistance > 0) { builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(uuid, "Armor knockback resistance", knockBackResistance, EntityAttributeModifier.Operation.ADDITION)); }
        this.customModifiers = builder.build();
    }
    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == this.type.getEquipmentSlot() ? this.customModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public boolean isFireproof() {
        if (this.getMaterial() instanceof TieredMaterials.ChocoboArmorTiers chocoboArmorTiers) {
            return chocoboArmorTiers == TieredMaterials.ChocoboArmorTiers.NETHERITE || chocoboArmorTiers == TieredMaterials.ChocoboArmorTiers.REINFORCED_NETHERITE || chocoboArmorTiers == TieredMaterials.ChocoboArmorTiers.GILDED_NETHERITE;
        }
        return super.isFireproof();
    }
}