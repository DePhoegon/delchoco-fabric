package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.common.enchantments.ChocoboSweepEnchantment;
import com.dephoegon.delchoco.common.entities.AbstractChocobo;
import com.dephoegon.delchoco.common.init.ModDamageTypes;
import com.dephoegon.delchoco.common.init.ModEnchantments;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.dephoegon.delchoco.common.entities.AbstractChocobo.CHOCOBO_SWING_DISTANCE;
import static com.dephoegon.delchoco.common.init.ModDamageTypes.knockbackCalculation;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
public abstract class LivingEntityChocoboSweepMixin {
    @Inject(method = "damage", at = @At(value = "RETURN", ordinal = 1))
    private void handleChocoboSweepForPlayer(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || source.isOf(ModDamageTypes.CHOCOBO_SWEEP_ATTACK)) { return; }

        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getWorld().isClient() || source.getAttacker() == null) { return; }
        LivingEntity attacker = source.getAttacker() instanceof LivingEntity ? (LivingEntity) source.getAttacker() : null;

        // Get the weapon in main hand
        ItemStack heldItem = attacker != null ? attacker.getMainHandStack() : null;
        if (heldItem == null || !(heldItem.getItem() instanceof SwordItem) || heldItem.getItem() instanceof ChocoboWeaponItems) { return; } // prevent sweep with chocobo weapons by non-chocobo entities, as the weapon is to strong for unrestricted use

        // Check for Chocobo Sweep enchantment
        int chocoboSweepLevel = EnchantmentHelper.getLevel(ModEnchantments.CHOCOBO_SWEEP, heldItem);
        float damageMultiplier = ChocoboSweepEnchantment.getDamageMultiplier(chocoboSweepLevel);
        if (chocoboSweepLevel <= 0 || damageMultiplier <= 0) { return; }

        // Check if this was a critical hit (don't trigger sweep on crits)
        boolean isCrit;
        if (attacker instanceof PlayerEntity player) {
            isCrit = player.fallDistance > 0.0F && !player.isOnGround() &&
                    !player.isClimbing() && !player.isTouchingWater() &&
                    !player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.BLINDNESS) &&
                    !player.hasVehicle();
        } else { isCrit = false; }
        if (isCrit) { return; }

        // Get damage multiplier based on enchantment

        // Calculate base damage
        float baseDamage = (float) attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float sweepDamage = baseDamage * damageMultiplier;

        // Find nearby entities to hit (similar to vanilla sweeping edge)
        List<LivingEntity> nearbyEntities = attacker.getWorld().getNonSpectatingEntities(LivingEntity.class,
            self.getBoundingBox().expand(2.5D, 1.5D, 2.5D));

        for (LivingEntity nearbyEntity : nearbyEntities) {
            if (nearbyEntity != attacker && nearbyEntity != self) {
                if (nearbyEntity instanceof PlayerEntity targetPlayer) {
                    if (targetPlayer.isCreative() || targetPlayer.isSpectator()) { continue; }
                } else {
                    if (attacker.getWorld().getGameRules().getBoolean(net.minecraft.world.GameRules.DO_MOB_GRIEFING)) {
                        if (nearbyEntity instanceof PlayerEntity) { continue; }
                    }
                }

                boolean skipDamage = false;

                if (nearbyEntity instanceof AbstractChocobo chocoboTarget) {
                    LivingEntity targetOwner = chocoboTarget.getOwner();
                    if (targetOwner == attacker) { skipDamage = true; }
                    if (targetOwner != null && attacker.isTeammate(targetOwner)) { skipDamage = true; }
                }

                if (nearbyEntity instanceof net.minecraft.entity.passive.TameableEntity tameableTarget) {
                    LivingEntity targetOwner = tameableTarget.getOwner();
                    if (targetOwner == attacker) { skipDamage = true; }
                    if (targetOwner != null && attacker.isTeammate(targetOwner)) {
                        if (attacker.getScoreboardTeam() != null &&
                            !attacker.getScoreboardTeam().isFriendlyFireAllowed()) {
                            skipDamage = true;
                        }
                    }
                }

                if (skipDamage) { continue; }
                double distance = attacker instanceof AbstractChocobo ? CHOCOBO_SWING_DISTANCE : 4D;
                if (attacker.distanceTo(nearbyEntity) >= 4D) { continue; }

                if (nearbyEntity instanceof PlayerEntity targetPlayer) {
                    if (attacker.getScoreboardTeam() != null && targetPlayer.getScoreboardTeam() != null) {
                        if (attacker.getScoreboardTeam() == targetPlayer.getScoreboardTeam()) {
                            if (!attacker.getScoreboardTeam().isFriendlyFireAllowed()) { continue; }
                        }
                    }
                }

                DamageSource damageSource = new DamageSource(attacker.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.CHOCOBO_SWEEP_ATTACK), attacker);

                if (nearbyEntity.isInvulnerableTo(damageSource)) { continue; }

                nearbyEntity.takeKnockback(knockbackCalculation(sweepDamage, attacker), MathHelper.sin(attacker.getYaw() * 0.017453292F), -MathHelper.cos(attacker.getYaw() * 0.017453292F));

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