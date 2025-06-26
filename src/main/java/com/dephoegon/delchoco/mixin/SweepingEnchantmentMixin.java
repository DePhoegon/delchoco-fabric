package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.common.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.SweepingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SweepingEnchantment.class)
public class SweepingEnchantmentMixin {

    @Inject(method = "canAccept", at = @At("HEAD"), cancellable = true)
    private void preventChocoboSweepCompatibility(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        // Make Sweeping Edge exclusive with Chocobo Sweep
        if (other == ModEnchantments.CHOCOBO_SWEEP) {
            cir.setReturnValue(false);
        }
    }
}
