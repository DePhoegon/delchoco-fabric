package com.dephoegon.delchoco.common.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public class ChocoboSweepEnchantment extends Enchantment {
    public ChocoboSweepEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (level - 1) * 9;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        // Exclusive against Sweeping Edge
        return super.canAccept(other) && other != Enchantments.SWEEPING;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        // Compatible with all Swords (which includes ChocoboWeaponItems since they extend SwordItem)
        return stack.getItem() instanceof SwordItem;
    }

    /**
     * Gets the damage multiplier for adjacent targets based on enchantment level
     * @param level The enchantment level (1 or 2)
     * @return The damage multiplier (0.5 for level 1, 1.0 for level 2)
     */
    public static float getDamageMultiplier(int level) {
        return switch (level) {
            case 1 -> 0.5f;
            case 2 -> 1.0f;
            default -> 0.0f;
        };
    }
}
