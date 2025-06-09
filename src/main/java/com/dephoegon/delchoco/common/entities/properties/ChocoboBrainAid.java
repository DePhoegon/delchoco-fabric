package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;

public class ChocoboBrainAid {
    public static boolean isAttackable(Entity target) {
        if (!(target instanceof LivingEntity)) return false;
        if (target instanceof EnderDragonEntity) return false;
        if (target instanceof PhantomEntity) return false;
        if (target instanceof PufferfishEntity) return false;
        if (target instanceof TropicalFishEntity) return false;
        if (target instanceof CodEntity) return false;
        if (target instanceof GlowSquidEntity) return false;
        if (target instanceof SquidEntity) return false;
        if (target instanceof SalmonEntity) return false;
        if (target instanceof BatEntity) return false;
        if (target instanceof DolphinEntity) return false;
        if (target instanceof GuardianEntity) return false;
        if (target instanceof StriderEntity) return false;
        if (target instanceof GhastEntity) return false;

        return !((LivingEntity) target).isDead();
    }
    private static boolean isTameTargetValid(Entity target, Chocobo chocobo) {
        if (target == null) return false;
        PlayerEntity Owner = (target instanceof TameableEntity tameableEntity) ? (PlayerEntity) tameableEntity.getOwner() : null;
        if (Owner == null) { Owner = (target instanceof AbstractHorseEntity horse) ? (PlayerEntity) horse.getOwner() : null; }
        if (Owner == null || chocobo == null) return true;
        if (chocobo.isTamed()) {
            if (chocobo.getOwner() == null) {
                DelChoco.LOGGER.info("ChocoboBrainAid.isTameTargetInvalid() - Chocobo has no owner, but is tamed! This should not happen!");
                return false;
            }
            if (chocobo.getOwner() == Owner) { return false; }
            return ((PlayerEntity)chocobo.getOwner()).shouldDamagePlayer(Owner);
        }
        return false;
    }
    protected static boolean isInvalidTarget(Entity target) { return !isAttackable(target); }
}