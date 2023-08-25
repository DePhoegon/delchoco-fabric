package com.dephoegon.delchoco.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.dephoegon.delchoco.common.effects.ChocoboCombatEvents.onChocoboCombatAttack;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(at = @At("HEAD"), method = "applyDamageEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V", cancellable = true)
    private void onApplyDamageEffects(@NotNull LivingEntity attacker, Entity target, CallbackInfo ci) {
        boolean result = onChocoboCombatAttack(attacker, target);
        if (result) { ci.cancel(); }
    }
}