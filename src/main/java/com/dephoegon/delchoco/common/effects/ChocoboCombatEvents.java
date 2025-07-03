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

    private static boolean isChocoDisguiseForSlot(ItemStack stack, EquipmentSlot slot) {
        if (!(stack.getItem() instanceof ChocoDisguiseItem item)) return false;
        return item.getSlotType() == slot;
    }

    /**
     * @return True if all armor slots have ChocoDisguiseItems equipped,
     * False if any are not ChocoDisguiseItems, or if any are not the correct slot type
     * @implNote Requires all armor slot pieces to be ChocoDisguiseItems
     */
    public static boolean armorMatch(@NotNull ItemStack head, @NotNull ItemStack chest, @NotNull ItemStack legs, @NotNull ItemStack feet) {
        return isChocoDisguiseForSlot(head, EquipmentSlot.HEAD)
                && isChocoDisguiseForSlot(chest, EquipmentSlot.CHEST)
                && isChocoDisguiseForSlot(legs, EquipmentSlot.LEGS)
                && isChocoDisguiseForSlot(feet, EquipmentSlot.FEET);
    }

    /**
     * @return True, if all armor slots have ChocoDisguiseItems equipped and are the same color, False if not
     * False if any are not ChocoDisguiseItems, or if any are not the correct slot type
     * @implNote Requires all armor slot pieces to be ChocoDisguiseItems
     */
    public static boolean armorColorMatch(@NotNull ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
        if (!armorMatch(head, chest, legs, feet)) return false;
        ChocoboColor color = getNBTKEY_COLOR(head);
        return color.equals(getNBTKEY_COLOR(chest))
                && color.equals(getNBTKEY_COLOR(legs))
                && color.equals(getNBTKEY_COLOR(feet));
    }
}