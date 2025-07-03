package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.common.enchantments.ChocoboSweepEnchantment;
import com.dephoegon.delchoco.common.entities.AbstractChocobo;
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
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
public class LivingEntityChocoboSweepMixin {
    @Inject(method = "applyDamageEffects", at = @At("TAIL"))
    private void handleChocoboSweepForAllEntities(LivingEntity attacker, net.minecraft.entity.Entity target, CallbackInfo ci) {
        // Only proceed if the original attack was successful
        if (attacker.getWorld().isClient() || !(target instanceof LivingEntity livingTarget)) { return; }

        // Get the weapon in main hand
        ItemStack heldItem = attacker.getMainHandStack();
        if (!(heldItem.getItem() instanceof SwordItem)) { return; }

        // Check for Chocobo Sweep enchantment
        int chocoboSweepLevel = EnchantmentHelper.getLevel(ModEnchantments.CHOCOBO_SWEEP, heldItem);
        if (chocoboSweepLevel <= 0) { return; }

        // Check if this was a critical hit (don't trigger sweep on crits)
        boolean isCrit;
        if (attacker instanceof PlayerEntity player) {
            isCrit = player.fallDistance > 0.0F && !player.isOnGround() &&
                    !player.isClimbing() && !player.isTouchingWater() &&
                    !player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.BLINDNESS) &&
                    !player.hasVehicle();
        } else {
            // For mobs (including Chocobos), use a simpler crit check (falling)
            isCrit = attacker.fallDistance > 0.0F && !attacker.isOnGround();
        }

        if (isCrit) { return; }

        // Get damage multiplier based on enchantment level
        float damageMultiplier = ChocoboSweepEnchantment.getDamageMultiplier(chocoboSweepLevel);
        if (damageMultiplier <= 0) { return; }

        // Calculate base damage
        float baseDamage = (float) attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float sweepDamage = baseDamage * damageMultiplier;

        // Find nearby entities to hit (similar to vanilla sweeping edge)
        List<LivingEntity> nearbyEntities = attacker.getWorld().getNonSpectatingEntities(LivingEntity.class,
            livingTarget.getBoundingBox().expand(2.5D, 2.5D, 2.5D));

        for (LivingEntity nearbyEntity : nearbyEntities) {
            if (nearbyEntity != attacker && nearbyEntity != livingTarget) {

                if (attacker.isTeammate(nearbyEntity)) { continue; }

                boolean skipDamage = false;

                if (attacker instanceof AbstractChocobo chocoboAttacker) {
                    LivingEntity owner = chocoboAttacker.getOwner();

                    if (nearbyEntity == owner) { skipDamage = true; }
                    if (owner != null && owner.isTeammate(nearbyEntity)) { skipDamage = true; }

                    if (nearbyEntity instanceof net.minecraft.entity.passive.TameableEntity tameableTarget) {
                        LivingEntity targetOwner = tameableTarget.getOwner();
                        if (targetOwner != null) {
                            if (targetOwner == owner) { skipDamage = true; }
                            if (owner != null && owner.isTeammate(targetOwner)) { skipDamage = true; }
                        }
                    }
                }

                if (nearbyEntity instanceof AbstractChocobo chocoboTarget) {
                    LivingEntity targetOwner = chocoboTarget.getOwner();

                    if (attacker instanceof PlayerEntity playerAttacker) {
                        if (targetOwner == playerAttacker) { skipDamage = true; }
                        if (targetOwner != null && playerAttacker.isTeammate(targetOwner)) { skipDamage = true; }
                    } else if (attacker instanceof AbstractChocobo attackingChocobo) {
                        LivingEntity attackerOwner = attackingChocobo.getOwner();
                        if (attackerOwner != null) {
                            if (targetOwner == attackerOwner) { skipDamage = true; }
                            if (targetOwner != null && attackerOwner.isTeammate(targetOwner)) { skipDamage = true; }
                        }
                    }
                }

                if (nearbyEntity instanceof net.minecraft.entity.passive.TameableEntity tameableTarget) {
                    LivingEntity targetOwner = tameableTarget.getOwner();

                    if (attacker instanceof PlayerEntity playerAttacker) {
                        if (targetOwner == playerAttacker) { skipDamage = true; }
                        if (targetOwner != null && playerAttacker.isTeammate(targetOwner)) {
                            if (playerAttacker.getScoreboardTeam() != null &&
                                !playerAttacker.getScoreboardTeam().isFriendlyFireAllowed()) {
                                skipDamage = true;
                            }
                        }
                    }
                }

                if (skipDamage) { continue; }
                double distance = attacker instanceof AbstractChocobo ? 8D : 4D;
                if (attacker.distanceTo(nearbyEntity) >= distance) { continue; }

                if (attacker instanceof PlayerEntity playerAttacker && nearbyEntity instanceof PlayerEntity targetPlayer) {
                    if (playerAttacker.getScoreboardTeam() != null && targetPlayer.getScoreboardTeam() != null) {
                        if (playerAttacker.getScoreboardTeam() == targetPlayer.getScoreboardTeam()) {
                            if (!playerAttacker.getScoreboardTeam().isFriendlyFireAllowed()) { continue; }
                        }
                    }
                }

                net.minecraft.entity.damage.DamageSource damageSource;
                if (attacker instanceof PlayerEntity player) { damageSource = attacker.getDamageSources().playerAttack(player);}
                else { damageSource = attacker.getDamageSources().mobAttack(attacker); }

                if (nearbyEntity.isInvulnerableTo(damageSource)) { continue; }
                if (nearbyEntity.timeUntilRegen > 0) { continue; }

                if (nearbyEntity instanceof PlayerEntity targetPlayer) {
                    if (targetPlayer.isCreative() || targetPlayer.isSpectator()) { continue; }
                }
                if (!(attacker instanceof PlayerEntity) && !(attacker instanceof AbstractChocobo)) {
                    if (!attacker.getWorld().getGameRules().getBoolean(net.minecraft.world.GameRules.DO_MOB_GRIEFING)) {
                        // If mob griefing is disabled, mobs shouldn't be able to hurt players with sweep
                        if (nearbyEntity instanceof PlayerEntity) { continue; }
                    }
                }

                nearbyEntity.takeKnockback(0.4F, MathHelper.sin(attacker.getYaw() * 0.017453292F), -MathHelper.cos(attacker.getYaw() * 0.017453292F));

                nearbyEntity.damage(damageSource, sweepDamage);
            }
        }

        // Play sweep sound and particles
        attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);

        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, attacker.getX(), attacker.getBodyY(0.5), attacker.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
