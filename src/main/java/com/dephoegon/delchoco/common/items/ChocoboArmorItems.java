package com.dephoegon.delchoco.common.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class ChocoboArmorItems extends Item implements Equipment {
    private static final UUID CHOCO_ARMOR_SLOT = UUID.fromString("02a4a813-7afd-4073-bf47-6dcffdf18fca");
    protected final ArmorItem.Type type;
    private final int defense;
    private final float toughness;
    protected final float knockBackResistance;
    protected final ArmorMaterial material;
    private final int enchantmentValue;
    private final Multimap<EntityAttribute, EntityAttributeModifier> defaultModifiers;
    public static final Map<Integer, ArmorMaterial> CHOCOBO_ARMOR_MATERIALS = Util.make(Maps.newHashMap(), (map) -> {
       map.put(1, ArmorMaterials.CHAIN);
       map.put(2, ArmorMaterials.IRON);
       map.put(3, ArmorMaterials.DIAMOND);
       map.put(4, ArmorMaterials.NETHERITE);
    });
    private static final Map<ArmorMaterial, Integer> CHOCOBO_ARMOR_MATERIAL = Util.make(Maps.newHashMap(), (map) -> {for (int i = 0; !(i > CHOCOBO_ARMOR_MATERIALS.size()); i++) { map.put(CHOCOBO_ARMOR_MATERIALS.get(i), i); }});
    private static final float setMod = 2.5F;

    private static int totalArmorMaterialDefence(ArmorMaterial armor, ArmorItem.Type slot, int additive, boolean initialMaterial) {
        int out = initialMaterial ? armor.getProtection(slot) + additive : (armor.getProtection(slot) / 2) + additive;
        int nextLowestArmor = CHOCOBO_ARMOR_MATERIAL.get(armor)-1;
        return nextLowestArmor > 0 ? totalArmorMaterialDefence(CHOCOBO_ARMOR_MATERIALS.get(nextLowestArmor), slot, out, false) : out;
    }
    private static float totalArmorMaterialToughness(ArmorMaterial armor, float additive, boolean initialMaterial) {
        float out = initialMaterial ? armor.getToughness()*setMod + additive : ((armor.getToughness() / 2)*setMod) + additive;
        int nextLowestArmor = CHOCOBO_ARMOR_MATERIAL.get(armor)-1;
        return nextLowestArmor > 0 ? totalArmorMaterialToughness(CHOCOBO_ARMOR_MATERIALS.get(nextLowestArmor), out, false) : out;
    }
    private static float totalArmorMaterialKnockBackResistance(@NotNull ArmorMaterial armor, float additive) {
        float out = armor.getKnockbackResistance() > 0 ? armor.getKnockbackResistance()*setMod + additive : additive;
        int nextLowestArmor = CHOCOBO_ARMOR_MATERIAL.get(armor)-1;
        return nextLowestArmor > 0 ? totalArmorMaterialKnockBackResistance(CHOCOBO_ARMOR_MATERIALS.get(nextLowestArmor), out) : out;
    }
    public ChocoboArmorItems(@NotNull ArmorMaterial pMaterial, ArmorItem.Type pSlot, Item.@NotNull Settings settings) {
        super(settings.maxDamage(pMaterial.getDurability(pSlot)));
        this.material = pMaterial;
        this.type = pSlot;
        this.defense = totalArmorMaterialDefence(pMaterial, pSlot, 0, true);
        this.toughness = totalArmorMaterialToughness(pMaterial, 0, true);
        this.enchantmentValue = pMaterial.getEnchantability();
        this.knockBackResistance = totalArmorMaterialKnockBackResistance(pMaterial, 0);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = CHOCO_ARMOR_SLOT;
        builder.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uuid, "Armor modifier", this.defense, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier("Armor toughness", this.toughness, EntityAttributeModifier.Operation.ADDITION));
        if (this.knockBackResistance > 0) { builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(uuid, "Armor knockback resistance", this.knockBackResistance, EntityAttributeModifier.Operation.ADDITION)); }
        this.defaultModifiers = builder.build();
    }
    public EquipmentSlot getSlotType() { return this.type.getEquipmentSlot(); }
    public int getEnchantability() { return this.enchantmentValue; }
    public ArmorMaterial getMaterial() { return this.material; }
    public boolean canRepair(ItemStack stack, ItemStack ingredient)  { return this.material.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient); }
    public TypedActionResult<ItemStack> use(World world, @NotNull PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        return TypedActionResult.fail(itemStack);
    }
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ArmorItem.Type slot) { return slot == this.type ? this.defaultModifiers : super.getAttributeModifiers(slot.getEquipmentSlot()); }
    public int getDefense() {
        return this.defense;
    }
    public float getToughness() {
        return this.toughness;
    }
    @Override
    @Nullable
    public SoundEvent getEquipSound() {
        return this.getMaterial().getEquipSound();
    }
    public boolean isFireproof() {
        boolean netherite = this.getMaterial() == ArmorMaterials.NETHERITE;
        if (netherite) { return true; }
        return super.isFireproof();
    }
}