package com.dephoegon.delchoco.common.effects;

import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.aid.chocoboChecks.isPoisonImmuneChocobo;
import static com.dephoegon.delchoco.aid.chocoboChecks.isWitherImmuneChocobo;
import static com.dephoegon.delchoco.common.entities.properties.ChocoboColor.*;
import static com.dephoegon.delchoco.common.items.ChocoDisguiseItem.NBTKEY_COLOR;
import static com.dephoegon.delchoco.utils.RandomHelper.random;

public class ChocoboCombatEvents {

    /**
     * @return True if the Player is immune to the DamageSource, False if not
     * @implNote Checks for matching armor set for Player Immunity with ChocoGuise Gear,
     * enabled with config setting "extraChocoboEffects"
     * Intended to be used in the PlayerEntity#isInvulnerableTo mixin method
     */
    public static boolean playerDamageImmunityCheck(ItemStack HeadStack, ItemStack ChestStack, ItemStack LegStack, ItemStack FeetStack, DamageSource source) {
        if (ChocoboConfig.EXTRA_CHOCOBO_EFFECT.get()) {
            if (armorColorMatch(HeadStack, ChestStack, LegStack, FeetStack)) {
                ChocoboColor headColor = getNBTKEY_COLOR(HeadStack);
                if (source.isOf(DamageTypes.WITHER) || source.isOf(DamageTypes.WITHER_SKULL)) { return headColor.equals(BLACK) || headColor.equals(RED) || headColor.equals(PURPLE) || headColor.equals(GOLD) || headColor.equals(PINK); }
                if (source.isOf(DamageTypes.DRAGON_BREATH)) { return headColor.equals(PURPLE) || headColor.equals(GOLD); }
                if (source.isIn(DamageTypeTags.IS_FREEZING)) { return headColor.equals(WHITE) || headColor.equals(GOLD); }
            }
            if (source.isOf(DamageTypes.SWEET_BERRY_BUSH)) { return armorMatch(HeadStack, ChestStack, LegStack, FeetStack); }
        }
        return false;
    }

    /**
     * @param effect StatusEffectInstance to be applied
     * @return True if the Player is immune to the StatusEffect, False if not
     * @implNote Checks for matching armor set for Player Immunity with ChocoGuise Gear
     */
    public static boolean playerStatusImmunityCheck(StatusEffectInstance effect, ItemStack HeadStack, ItemStack ChestStack, ItemStack LegStack, ItemStack FeetStack) {
        StatusEffect statusEffect;
        if (armorColorMatch(HeadStack, ChestStack, LegStack, FeetStack)) {
            ChocoboColor headColor = getNBTKEY_COLOR(HeadStack);
            statusEffect = effect.getEffectType();
            if (statusEffect == StatusEffects.WITHER) { return isWitherImmuneChocobo(headColor); }
            if (statusEffect == StatusEffects.POISON) { return isPoisonImmuneChocobo(headColor); }
            if (statusEffect == StatusEffects.SLOWNESS) { return headColor.equals(GOLD); }
        }
        return false;
    }
    public static boolean flowerChance() { return random.nextInt(100)+1 < 45; }
    private static ChocoboColor getNBTKEY_COLOR(@NotNull ItemStack item) {
        NbtCompound tag = item.getNbt();
        if (tag != null && tag.contains(NBTKEY_COLOR)) { return getColorFromName(tag.getString(NBTKEY_COLOR)); }
        else { return ChocoboColor.YELLOW; }
    }

    /**
     * @return True, if all armor slots have ChocoDisguiseItems equipped and are the same color, False if not
     * False if any are not ChocoDisguiseItems, or if any are not the correct slot type
     * @implNote Requires all armor slot pieces to be ChocoDisguiseItems
     */
    public static boolean armorColorMatch(@NotNull ItemStack headItemStack, ItemStack chestItemStack, ItemStack legsItemStack, ItemStack bootItemStack) {
        boolean matched = armorMatch(headItemStack, chestItemStack, legsItemStack, bootItemStack);
        if (!matched) { return false; }
        return getNBTKEY_COLOR(headItemStack).equals(getNBTKEY_COLOR(chestItemStack)) && getNBTKEY_COLOR(headItemStack).equals(getNBTKEY_COLOR(legsItemStack)) && getNBTKEY_COLOR(headItemStack).equals(getNBTKEY_COLOR(bootItemStack));
    }

    /**
     * @return True if all armor slots have ChocoDisguiseItems equipped,
     * False if any are not ChocoDisguiseItems, or if any are not the correct slot type
     * @implNote Requires all armor slot pieces to be ChocoDisguiseItems
     */
    public static boolean armorMatch(@NotNull ItemStack headItemStack, @NotNull ItemStack chestItemStack, @NotNull ItemStack legsItemStack, @NotNull ItemStack bootItemStack) {
        ChocoDisguiseItem headItem = headItemStack.getItem() instanceof ChocoDisguiseItem e ? e.getSlotType() == EquipmentSlot.HEAD ? e : null : null;
        ChocoDisguiseItem chestItem = chestItemStack.getItem() instanceof ChocoDisguiseItem e ? e.getSlotType() == EquipmentSlot.CHEST ? e : null : null;
        ChocoDisguiseItem legsItem = legsItemStack.getItem() instanceof ChocoDisguiseItem e ? e.getSlotType() == EquipmentSlot.LEGS ? e : null : null;
        ChocoDisguiseItem bootItem = bootItemStack.getItem() instanceof ChocoDisguiseItem e ? e.getSlotType() == EquipmentSlot.FEET ? e : null : null;
        return headItem != null && chestItem != null && legsItem != null && bootItem != null;
    }
}