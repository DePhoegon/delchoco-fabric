package com.dephoegon.delchoco.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class MixinEntity {
    /*
    @Inject(at = @At("HEAD"), method = "applyDamageEffects", cancellable = true)
    private void onApplyDamageEffects(@NotNull LivingEntity attacker, Entity target, CallbackInfo ci) {
        Chocobo chocobo = attacker instanceof Chocobo ? (Chocobo) attacker : null;
        if (chocobo != null) { ChocoboCombatEvents.onChocoboCombatHit(chocobo, target); }
    }
    */
}
