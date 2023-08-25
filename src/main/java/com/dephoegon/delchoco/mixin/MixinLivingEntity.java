package com.dephoegon.delchoco.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.dephoegon.delchoco.common.effects.ChocoboCombatEvents.onChocoboKillOrDie;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(@NotNull DamageSource source, CallbackInfo ci) {
        // The entity has died
        // The source of the damage is available in the 'source' parameter
        LivingEntity attacker = source.getAttacker() instanceof LivingEntity ? (LivingEntity) source.getAttacker() : null;
        LivingEntity dead = source.getSource() instanceof LivingEntity ? (LivingEntity) source.getSource() : null;
        onChocoboKillOrDie(attacker, dead);
    }
}