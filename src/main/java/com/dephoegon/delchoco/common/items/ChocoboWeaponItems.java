package com.dephoegon.delchoco.common.items;

import com.dephoegon.delchoco.aid.TieredMaterials;
import com.dephoegon.delchoco.common.enchantments.ChocoboSweepEnchantment;
import com.dephoegon.delchoco.common.entities.AbstractChocobo;
import com.dephoegon.delchoco.common.init.ModEnchantments;
import com.google.common.collect.Maps;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static com.dephoegon.delchoco.common.init.ModDamageTypes.knockbackCalculation;

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
        super(toolMaterial, 0, attackSpeed, settings);
        this.attackSpeed = getTotalAttackSpeed(toolMaterial, attackSpeed);
    }
    public float getAttackSpeed() { return this.attackSpeed; }
    public boolean isFireproof() {
        if (this.getMaterial() instanceof TieredMaterials.ChocoboToolTiers material) {
            return material == TieredMaterials.ChocoboToolTiers.NETHERITE || material == TieredMaterials.ChocoboToolTiers.REINFORCED_NETHERITE || material == TieredMaterials.ChocoboToolTiers.GILDED_NETHERITE;
        }
        return super.isFireproof();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        float totalDamage = getTotalAttackDamage(this.getMaterial());

        // Add enchantment damage
        totalDamage += EnchantmentHelper.getAttackDamage(stack, null);

        int playerDamage = (int)(totalDamage * 0.25F);
        int chocoboDamage = (int)totalDamage;
        int mobDamage = (int)(totalDamage * 0.5F);

        tooltip.add(Text.translatable("item.delchoco.chocobo_weapon.tooltip.header").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.delchoco.chocobo_weapon.tooltip.player_damage", playerDamage).formatted(Formatting.GREEN));
        tooltip.add(Text.translatable("item.delchoco.chocobo_weapon.tooltip.chocobo_damage", chocoboDamage).formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("item.delchoco.chocobo_weapon.tooltip.mob_damage", mobDamage).formatted(Formatting.RED));
    }

    private static boolean isChocobo(LivingEntity entity) {
        return entity instanceof AbstractChocobo;
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
            target.getBoundingBox().expand(2.5D, 1.5D, 2.5D));

        for (LivingEntity nearbyEntity : nearbyEntities) {
            if (nearbyEntity != attacker && nearbyEntity != target &&
                !attacker.isTeammate(nearbyEntity) &&
                attacker.distanceTo(nearbyEntity) < 3.0D) {

                // Apply knockback
                nearbyEntity.takeKnockback(knockbackCalculation(sweepDamage, attacker), MathHelper.sin(attacker.getYaw() * 0.017453292F), -MathHelper.cos(attacker.getYaw() * 0.017453292F));

                // Deal damage
                nearbyEntity.damage(attacker.getDamageSources().mobAttack(attacker), sweepDamage);
            }
        }

        // Play sweep sound and particles
        world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, attacker.getX(), attacker.getBodyY(0.5), attacker.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker == null || target == null || stack.isEmpty() || attacker.getWorld().isClient()) {
            return super.postHit(stack, target, attacker);
        }
        // Chocobo will have the damage bonus applied directly in the chocobo class, bypassing the weapon's given damage
        float damage = isChocobo(attacker) ? 0F : getTotalAttackDamage(this.getMaterial());
        damage += EnchantmentHelper.getAttackDamage(stack, target.getGroup());

        float damageMultiplier = attacker instanceof PlayerEntity ? 0.25F : isChocobo(attacker) ? 1.0F : 0.5F;
        float finalDamage = damage * damageMultiplier;

        target.damage(attacker.getDamageSources().mobAttack(attacker), finalDamage);

        //Chocobos will preform their own sweep attack
        if (!isChocobo(attacker)) {
            performChocoboSweep(attacker.getWorld(), attacker, null, target, stack, finalDamage, false);
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