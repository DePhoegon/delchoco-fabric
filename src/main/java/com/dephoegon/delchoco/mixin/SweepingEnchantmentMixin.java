package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.common.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.SweepingEnchantment;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SweepingEnchantment.class)
public abstract class SweepingEnchantmentMixin extends Enchantment {

    // Shadow constructor to satisfy extends requirement
    protected SweepingEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    // Override the canAccept method to add Chocobo Sweep exclusion
    @Override
    public boolean canAccept(Enchantment other) {
        // Make Sweeping Edge exclusive with Chocobo Sweep
        if (other == ModEnchantments.CHOCOBO_SWEEP) { return false; }

        // For all other enchantments, use the default behavior from the parent class
        return super.canAccept(other);
    }
}