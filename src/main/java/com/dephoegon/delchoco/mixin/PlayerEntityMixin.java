package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.common.effects.ChocoboCombatEvents;
import com.dephoegon.delchoco.common.enchantments.ChocoboSweepEnchantment;
import com.dephoegon.delchoco.common.init.ModDamageTypes;
import com.dephoegon.delchoco.common.init.ModEnchantments;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.dephoegon.delchoco.common.init.ModDamageTypes.knockbackCalculation;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    public abstract Iterable<ItemStack> getArmorItems();

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void onIsInvulnerableTo(DamageSource source, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (this.getWorld().isClient()) { return; }
        ItemStack helmet = this.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestplate = this.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack leggings = this.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack boots = this.getEquippedStack(EquipmentSlot.FEET);
        boolean damageImmune = ChocoboCombatEvents.playerDamageImmunityCheck(helmet, chestplate, leggings, boots, source);
        if (damageImmune) { cir.setReturnValue(true); }
    }

    @Override
    public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
        if (this.getWorld().isClient()) { return super.addStatusEffect(effect, source); }
        ItemStack helmet = this.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestplate = this.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack leggings = this.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack boots = this.getEquippedStack(EquipmentSlot.FEET);
        boolean check = ChocoboCombatEvents.playerStatusImmunityCheck(effect, helmet, chestplate, leggings, boots);
        if (check) { return false; }
        return super.addStatusEffect(effect, source);
    }

    // Chocobo Sweep Attack for Players
    @Inject(method = "attack", at = @At("TAIL"))
    public void onAttack(Entity target, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (self.getWorld().isClient()) { return; }
        if (!(target instanceof LivingEntity living)) { return; }
        if (target.handleAttack(self)) { return; }
        if (!target.isAttackable()) { return; }

        ItemStack heldItem = self.getMainHandStack();
        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof SwordItem) || heldItem.getItem() instanceof ChocoboWeaponItems) { return; }

        int chocoboSweepLevel = EnchantmentHelper.getLevel(ModEnchantments.CHOCOBO_SWEEP, heldItem);
        float damageMultiplier = ChocoboSweepEnchantment.getDamageMultiplier(chocoboSweepLevel);
        if (chocoboSweepLevel <= 0 || damageMultiplier <= 0) { return; }

        boolean isCrit = self.fallDistance > 0.0F && !self.isOnGround() &&
                !self.isClimbing() && !self.isTouchingWater() &&
                !self.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.BLINDNESS) &&
                !self.hasVehicle();
        if (isCrit) { return; }

        float damage = (float) self.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float enchantDamage = EnchantmentHelper.getAttackDamage(self.getMainHandStack(), (living.getGroup()));
        float totalDamage = (damage + enchantDamage) * damageMultiplier;

        List<LivingEntity> targets = self.getWorld().getEntitiesByClass(LivingEntity.class, self.getBoundingBox().expand(2.5D, 1.5D, 2.5D), e -> e != self && e != living && e.isAttackable() && e.isAlive());

        boolean thwack = false;
        boolean isPlayer = target instanceof PlayerEntity;
        for (LivingEntity targetEntity : targets) {
            if (targetEntity.isAlive() && !target.handleAttack(self)) {
                if (targetEntity instanceof PlayerEntity targetPlayer) {
                    if (targetPlayer.isCreative() || targetPlayer.isSpectator() || !isPlayer) { continue; }
                }
                boolean skipAttack = false;
                if (targetEntity instanceof TameableEntity tameTarget) {
                    LivingEntity targetOwner = tameTarget.getOwner();
                    if (targetOwner == self) { skipAttack = true; }
                    if (targetOwner != null && self.isTeammate(targetOwner)) {
                        if (targetOwner instanceof PlayerEntity targetPlayer) {
                            if (self.getScoreboardTeam() != null && targetPlayer.getScoreboardTeam() != null && self.getScoreboardTeam().equals(targetPlayer.getScoreboardTeam())) {
                                skipAttack = true;
                            }
                        }
                    }
                }
                if (skipAttack) { continue; }
                double distance = 4D;
                if (self.distanceTo(targetEntity) >= distance) { continue; }

                DamageSource damageSource = new DamageSource(self.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.CHOCOBO_SWEEP_ATTACK), self);

                if (targetEntity.isInvulnerableTo(damageSource)) { continue; }

                targetEntity.takeKnockback(knockbackCalculation(totalDamage, self), MathHelper.sin(self.getYaw() * 0.017453292F), -MathHelper.cos(self.getYaw() * 0.017453292F));

                targetEntity.damage(damageSource, totalDamage);
                thwack = true;
            }
        }
        if (thwack) {
            self.getWorld().sendEntityStatus(self, (byte) 4);
            self.getWorld().playSound(null, self.getX(), self.getY(), self.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, self.getSoundCategory(), 1.0F, 1.0F);
            if (self.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, self.getX(), self.getBodyY(0.5), self.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            }
            self.resetLastAttackedTicks();
        }
    }

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
}
