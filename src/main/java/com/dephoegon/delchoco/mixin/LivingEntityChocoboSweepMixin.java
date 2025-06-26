package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.common.enchantments.ChocoboSweepEnchantment;
import com.dephoegon.delchoco.common.init.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityChocoboSweepMixin {

    @Inject(method = "tryAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.AFTER))
    private void handleChocoboSweepForAllEntities(net.minecraft.entity.Entity target, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity attacker = (LivingEntity) (Object) this;

        // Skip if client-side or target is not a living entity
        if (attacker.getWorld().isClient() || !(target instanceof LivingEntity livingTarget)) {
            return;
        }

        // Get the weapon in main hand
        ItemStack heldItem = attacker.getStackInHand(Hand.MAIN_HAND);
        if (!(heldItem.getItem() instanceof SwordItem)) {
            return;
        }

        // Check for Chocobo Sweep enchantment
        int chocoboSweepLevel = EnchantmentHelper.getLevel(ModEnchantments.CHOCOBO_SWEEP, heldItem);
        if (chocoboSweepLevel <= 0) {
            return;
        }

        // Check if this was a critical hit (don't trigger sweep on crits)
        boolean isCrit = false;
        if (attacker instanceof PlayerEntity player) {
            isCrit = player.fallDistance > 0.0F && !player.isOnGround() &&
                    !player.isClimbing() && !player.isTouchingWater() &&
                    !player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.BLINDNESS) &&
                    !player.hasVehicle();
        } else {
            // For mobs, use a simpler crit check (falling)
            isCrit = attacker.fallDistance > 0.0F && !attacker.isOnGround();
        }

        if (isCrit) {
            return;
        }

        // Get damage multiplier based on enchantment level
        float damageMultiplier = ChocoboSweepEnchantment.getDamageMultiplier(chocoboSweepLevel);
        if (damageMultiplier <= 0) {
            return;
        }

        // Calculate base damage
        float baseDamage = (float) attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float sweepDamage = baseDamage * damageMultiplier;

        // Find nearby entities to hit (similar to vanilla sweeping edge)
        List<LivingEntity> nearbyEntities = attacker.getWorld().getNonSpectatingEntities(LivingEntity.class,
            livingTarget.getBoundingBox().expand(1.0D, 0.25D, 1.0D));

        for (LivingEntity nearbyEntity : nearbyEntities) {
            if (nearbyEntity != attacker && nearbyEntity != livingTarget) {

                // CRITICAL PROTECTION CHECKS - prevent bypassing game rules and protections

                // 1. Check team/alliance protection
                if (attacker.isTeammate(nearbyEntity)) {
                    continue;
                }

                // 2. Check distance limit
                if (attacker.distanceTo(nearbyEntity) >= 3.0D) {
                    continue;
                }

                // 3. Check PvP rules (for player attackers)
                if (attacker instanceof PlayerEntity playerAttacker && nearbyEntity instanceof PlayerEntity targetPlayer) {
                    // Check if PvP is disabled on the server
                    if (!attacker.getWorld().getGameRules().getBoolean(net.minecraft.world.GameRules.DO_PVP)) {
                        continue;
                    }

                    // Additional PvP protection checks could go here
                    // (spawn protection, claims, etc. - depends on server mods)
                }

                // 4. Check invulnerability and damage immunity
                net.minecraft.entity.damage.DamageSource damageSource;
                if (attacker instanceof PlayerEntity player) {
                    damageSource = attacker.getDamageSources().playerAttack(player);
                } else {
                    damageSource = attacker.getDamageSources().mobAttack(attacker);
                }

                // Check if the entity is invulnerable to this damage type
                if (nearbyEntity.isInvulnerableTo(damageSource)) {
                    continue;
                }

                // Check if the entity is in invincibility frames
                if (nearbyEntity.timeUntilRegen > 0) {
                    continue;
                }

                // 5. Check if entity is in creative/spectator mode (for players)
                if (nearbyEntity instanceof PlayerEntity targetPlayer) {
                    if (targetPlayer.isCreative() || targetPlayer.isSpectator()) {
                        continue;
                    }
                }

                // 6. Check mob griefing rules (for mob attackers)
                if (!(attacker instanceof PlayerEntity)) {
                    if (!attacker.getWorld().getGameRules().getBoolean(net.minecraft.world.GameRules.DO_MOB_GRIEFING)) {
                        // If mob griefing is disabled, mobs shouldn't be able to hurt players with sweep
                        if (nearbyEntity instanceof PlayerEntity) {
                            continue;
                        }
                    }
                }

                // All protection checks passed - apply damage and knockback

                // Apply knockback (similar to vanilla sweep)
                nearbyEntity.takeKnockback(0.4F,
                    MathHelper.sin(attacker.getYaw() * 0.017453292F),
                    -MathHelper.cos(attacker.getYaw() * 0.017453292F));

                // Deal damage - this will go through normal damage processing
                // which includes armor, resistance effects, etc.
                nearbyEntity.damage(damageSource, sweepDamage);
            }
        }

        // Play sweep sound and particles
        attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
            SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);

        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK,
                attacker.getX(), attacker.getBodyY(0.5), attacker.getZ(),
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
