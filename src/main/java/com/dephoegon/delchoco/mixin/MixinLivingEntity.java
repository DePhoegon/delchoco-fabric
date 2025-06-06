package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.common.effects.ChocoboCombatEvents;
import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void onAddStatusEffect(StatusEffectInstance effect, @Nullable Entity source, CallbackInfoReturnable<Boolean> cir) {
        // The entity has died
        // The source of the damage is available in the 'source' parameter
        PlayerEntity player = source instanceof PlayerEntity ? (PlayerEntity) source : null;
        if (player != null) {
            ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
            boolean check = ChocoboCombatEvents.playerStatusImmunityCheck(effect, helmet, chestplate, leggings, boots);
            if (check) { cir.setReturnValue(false); }
        }
    }
}