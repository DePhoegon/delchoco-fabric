package com.dephoegon.delchoco.common.items;

import com.dephoegon.delchoco.aid.TieredMaterials;
import com.dephoegon.delchoco.common.enchantments.ChocoboSweepEnchantment;
import com.dephoegon.delchoco.common.init.ModEnchantments;
import com.google.common.collect.Maps;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class ChocoboWeaponItems extends SwordItem {
    private final float attackSpeed;
    public static final int CHOCOBO_DAMAGE_MODIFIER = 5;
    public static final Map<Integer, ToolMaterial> CHOCOBO_WEAPON_TIERS = Util.make(Maps.newHashMap(), (map) -> {
        map.put(1, TieredMaterials.ChocoboToolTiers.CHAIN);
        map.put(2, TieredMaterials.ChocoboToolTiers.REINFORCED_CHAIN);
        map.put(3, TieredMaterials.ChocoboToolTiers.IRON);
        map.put(4, TieredMaterials.ChocoboToolTiers.REINFORCED_IRON);
        map.put(5, TieredMaterials.ChocoboToolTiers.DIAMOND);
        map.put(6, TieredMaterials.ChocoboToolTiers.REINFORCED_DIAMOND);
        map.put(7, TieredMaterials.ChocoboToolTiers.NETHERITE);
        map.put(8, TieredMaterials.ChocoboToolTiers.REINFORCED_NETHERITE);
        map.put(9, TieredMaterials.ChocoboToolTiers.GILDED_NETHERITE);
    });
    private static final Map<ToolMaterial, Integer> CHOCOBO_WEAPON_TIER = Util.make(Maps.newHashMap(), (map) -> { for (int i = 1; i <= CHOCOBO_WEAPON_TIERS.size(); i++) { map.put(CHOCOBO_WEAPON_TIERS.get(i), i); } });
    public static int getTotalAttackDamage(ToolMaterial tier) {
        if (!(tier instanceof TieredMaterials.ChocoboToolTiers)) { return (int)tier.getAttackDamage(); }
        Integer tierNum = CHOCOBO_WEAPON_TIER.get(tier);
        if (tierNum == null) { return (int)tier.getAttackDamage(); }

        int totalDamage = (int)CHOCOBO_WEAPON_TIERS.get(tierNum).getAttackDamage();
        for (int i = tierNum - 1; i >= 1; i--) {
            totalDamage += (int)(CHOCOBO_WEAPON_TIERS.get(i).getAttackDamage() / 2);
        }
        return totalDamage + CHOCOBO_DAMAGE_MODIFIER;
    }

    public static float getTotalAttackSpeed(ToolMaterial toolMaterial, float baseAttackSpeed) {
        return baseAttackSpeed + toolMaterial.getMiningSpeedMultiplier();
    }

    public ChocoboWeaponItems(ToolMaterial toolMaterial, float attackSpeed, Settings settings) {
        super(toolMaterial, (getTotalAttackDamage(toolMaterial) - (int)toolMaterial.getAttackDamage()), attackSpeed, settings);
        this.attackSpeed = getTotalAttackSpeed(toolMaterial, attackSpeed);
    }
    public float getAttackSpeed() { return this.attackSpeed; }
    public boolean isFireproof() {
        if (this.getMaterial() instanceof TieredMaterials.ChocoboToolTiers material) {
            return material == TieredMaterials.ChocoboToolTiers.NETHERITE || material == TieredMaterials.ChocoboToolTiers.REINFORCED_NETHERITE || material == TieredMaterials.ChocoboToolTiers.GILDED_NETHERITE;
        }
        return super.isFireproof();
    }

    /**
     * Performs a sweep attack with the Chocobo Sweep enchantment
     * @param world The world where the attack occurs
     * @param attacker The entity performing the attack
     * @param hand The hand holding the weapon
     * @param target The primary target that was hit
     * @param stack The weapon ItemStack
     * @param baseDamage The base damage of the weapon
     * @param isCrit Whether this was a critical hit (sweep doesn't trigger on crits)
     */
    public static void performChocoboSweep(World world, LivingEntity attacker, Hand hand, LivingEntity target, ItemStack stack, float baseDamage, boolean isCrit) {
        // Don't perform sweep on critical hits as specified
        if (isCrit) return;

        int sweepLevel = EnchantmentHelper.getLevel(ModEnchantments.CHOCOBO_SWEEP, stack);
        if (sweepLevel <= 0) return;

        float damageMultiplier = ChocoboSweepEnchantment.getDamageMultiplier(sweepLevel);
        if (damageMultiplier <= 0) return;

        // Calculate sweep damage
        float sweepDamage = baseDamage * damageMultiplier;

        // Find nearby entities to hit (similar to vanilla sweep but with different range)
        List<LivingEntity> nearbyEntities = world.getNonSpectatingEntities(LivingEntity.class,
            target.getBoundingBox().expand(1.0D, 0.25D, 1.0D));

        for (LivingEntity nearbyEntity : nearbyEntities) {
            if (nearbyEntity != attacker && nearbyEntity != target &&
                !attacker.isTeammate(nearbyEntity) &&
                attacker.distanceTo(nearbyEntity) < 3.0D) {

                // Apply knockback
                nearbyEntity.takeKnockback(0.4F,
                    MathHelper.sin(attacker.getYaw() * 0.017453292F),
                    -MathHelper.cos(attacker.getYaw() * 0.017453292F));

                // Deal damage
                nearbyEntity.damage(attacker.getDamageSources().mobAttack(attacker), sweepDamage);
            }
        }

        // Play sweep sound and particles
        world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
            SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK,
                attacker.getX(), attacker.getBodyY(0.5), attacker.getZ(),
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Handle the custom sweep attack when a player uses this weapon
        if (attacker instanceof PlayerEntity player && !attacker.getWorld().isClient()) {
            float baseDamage = (float) attacker.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE);

            // Check if this was a critical hit (simplified check)
            boolean isCrit = player.fallDistance > 0.0F && !player.isOnGround() &&
                           !player.isClimbing() && !player.isTouchingWater() &&
                           !player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.BLINDNESS) &&
                           !player.hasVehicle();

            performChocoboSweep(attacker.getWorld(), attacker, player.getActiveHand(), target, stack, baseDamage, isCrit);
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return super.canRepair(stack, ingredient);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantability() {
        return this.getMaterial().getEnchantability();
    }
}