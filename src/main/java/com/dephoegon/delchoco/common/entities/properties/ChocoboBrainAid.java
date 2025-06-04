package com.dephoegon.delchoco.common.entities.properties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.*;

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
        if (target instanceof AbstractHorseEntity) return false;

        return !((LivingEntity) target).isDead();
    }
    protected static boolean isInvalidTarget(Entity target) { return !isAttackable(target); }
}