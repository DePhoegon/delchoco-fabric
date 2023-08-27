package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.common.effects.ChocoboCombatEvents;
import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(at = @At("HEAD"), method = "applyDamageEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V", cancellable = true)
    private void onApplyDamageEffects(@NotNull LivingEntity attacker, Entity target, CallbackInfo ci) {
        Chocobo chocobo = attacker instanceof Chocobo ? (Chocobo) attacker : null;
        ChocoboCombatEvents.onChocoboCombatHit(chocobo, target);
    }
}