package com.dephoegon.delchoco.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.dephoegon.delchoco.common.effects.ChocoboCombatEvents.playerDamageImmunityCheck;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    public abstract Iterable<ItemStack> getArmorItems();

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void onIsInvulnerableTo(DamageSource source, @NotNull CallbackInfoReturnable<Boolean> cir) {
        ItemStack helmet = getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestplate = getEquippedStack(EquipmentSlot.CHEST);
        ItemStack leggings = getEquippedStack(EquipmentSlot.LEGS);
        ItemStack boots = getEquippedStack(EquipmentSlot.FEET);
        boolean damageImmune = playerDamageImmunityCheck(helmet, chestplate, leggings, boots, source);
        if (damageImmune) { cir.setReturnValue(true); }
    }

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
}
