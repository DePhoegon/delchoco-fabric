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
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
public class LivingEntityChocoboSweepMixin {

    @Inject(method = "tryAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.AFTER))
    private void handleChocoboSweepForAllEntities(net.minecraft.entity.Entity target, CallbackInfoReturnable<Boolean> cir) {
        //noinspection DataFlowIssue
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
        boolean isCrit;
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

                // 1. Basic team/alliance protection
                if (attacker.isTeammate(nearbyEntity)) {
                    continue;
                }

                // 2. Enhanced team and ownership protection for Chocobos and tamed entities
                boolean skipDamage = false;

                // Check if attacker is a Chocobo and handle owner team relationships
                if (attacker instanceof AbstractChocobo chocoboAttacker) {
                    LivingEntity owner = chocoboAttacker.getOwner();

                    // Protect the Chocobo's owner
                    if (nearbyEntity == owner) {
                        skipDamage = true;
                    }

                    // Protect teammates of the Chocobo's owner
                    if (owner != null && owner.isTeammate(nearbyEntity)) {
                        skipDamage = true;
                    }

                    // Protect other tamed entities owned by the same player or their teammates
                    if (nearbyEntity instanceof net.minecraft.entity.passive.TameableEntity tameableTarget) {
                        LivingEntity targetOwner = tameableTarget.getOwner();
                        if (targetOwner != null) {
                            // Same owner protection
                            if (targetOwner == owner) {
                                skipDamage = true;
                            }
                            // Owner's teammate protection
                            if (owner != null && owner.isTeammate(targetOwner)) {
                                skipDamage = true;
                            }
                        }
                    }
                }

                // Check if target is a Chocobo and handle owner relationships
                if (nearbyEntity instanceof AbstractChocobo chocoboTarget) {
                    LivingEntity targetOwner = chocoboTarget.getOwner();

                    if (attacker instanceof PlayerEntity playerAttacker) {
                        // Protect player's own Chocobo
                        if (targetOwner == playerAttacker) {
                            skipDamage = true;
                        }
                        // Protect teammate's Chocobo
                        if (targetOwner != null && playerAttacker.isTeammate(targetOwner)) {
                            skipDamage = true;
                        }
                    } else if (attacker instanceof AbstractChocobo attackingChocobo) {
                        LivingEntity attackerOwner = attackingChocobo.getOwner();
                        if (attackerOwner != null) {
                            // Same owner protection
                            if (targetOwner == attackerOwner) {
                                skipDamage = true;
                            }
                            // Owner's teammate protection
                            if (targetOwner != null && attackerOwner.isTeammate(targetOwner)) {
                                skipDamage = true;
                            }
                        }
                    }
                }

                // Check if target is any tamed entity and handle protection
                if (nearbyEntity instanceof net.minecraft.entity.passive.TameableEntity tameableTarget) {
                    LivingEntity targetOwner = tameableTarget.getOwner();

                    if (attacker instanceof PlayerEntity playerAttacker) {
                        // Protect player's own tamed entities
                        if (targetOwner == playerAttacker) {
                            skipDamage = true;
                        }
                        // Protect teammate's tamed entities when friendly fire is disabled
                        if (targetOwner != null && playerAttacker.isTeammate(targetOwner)) {
                            // Check team friendly fire rules for tamed entities
                            if (playerAttacker.getScoreboardTeam() != null &&
                                !playerAttacker.getScoreboardTeam().isFriendlyFireAllowed()) {
                                skipDamage = true;
                            }
                        }
                    }
                }

                if (skipDamage) {
                    continue;
                }

                // 3. Check distance limit
                if (attacker.distanceTo(nearbyEntity) >= 3.0D) {
                    continue;
                }

                // 4. Check PvP rules and team friendly fire (for player attackers)
                if (attacker instanceof PlayerEntity playerAttacker && nearbyEntity instanceof PlayerEntity targetPlayer) {
                    // Check team friendly fire rules
                    if (playerAttacker.getScoreboardTeam() != null && targetPlayer.getScoreboardTeam() != null) {
                        // If both players are on teams, check if they're on the same team
                        if (playerAttacker.getScoreboardTeam() == targetPlayer.getScoreboardTeam()) {
                            // Same team - check friendly fire setting
                            if (!playerAttacker.getScoreboardTeam().isFriendlyFireAllowed()) {
                                continue; // Friendly fire is disabled for this team
                            }
                        }
                    }

                    // Additional PvP protection checks could go here
                    // (spawn protection, claims, etc. - depends on server mods)
                }

                // 5. Check invulnerability and damage immunity
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

                // 6. Check if entity is in creative/spectator mode (for players)
                if (nearbyEntity instanceof PlayerEntity targetPlayer) {
                    if (targetPlayer.isCreative() || targetPlayer.isSpectator()) {
                        continue;
                    }
                }

                // 7. Check mob griefing rules (for mob attackers)
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
