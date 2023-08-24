package com.dephoegon.delchoco.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.CowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin
public abstract class MixinLivingEntity {
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        // The entity has died
        // The source of the damage is available in the 'source' parameter
        Entity attacker = source.getAttacker();
        Entity dead = source.getSource();
        if (attacker instanceof CowEntity) {
            // The source of the damage was a CowEntity
        }
    }
}