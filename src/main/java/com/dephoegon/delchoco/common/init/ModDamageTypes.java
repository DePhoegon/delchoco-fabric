package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.DelChoco;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> CHOCOBO_SWEEP_ATTACK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "chocobo_sweep_attack"));

    public static DamageSource of(@NotNull World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
    public static float knockbackCalculation(Float sweepDamage, LivingEntity attacker) {
        float knockbackStrength = (sweepDamage / 10.0F) * 0.4F;
        if (knockbackStrength < 0.1F) { knockbackStrength = 0.1F; }
        knockbackStrength = (float) (Math.round(knockbackStrength * 10) / 10.0);
        knockbackStrength += EnchantmentHelper.getKnockback(attacker);
        return knockbackStrength;
    }
}