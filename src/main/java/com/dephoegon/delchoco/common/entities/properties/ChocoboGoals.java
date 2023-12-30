package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import com.dephoegon.delchoco.utils.RandomHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;

import static com.dephoegon.delchoco.utils.RandomHelper.random;
import static net.minecraft.entity.ai.pathing.PathNodeType.WALKABLE;

public class ChocoboGoals {
    @SuppressWarnings("rawtypes")
    private static boolean AttackClassCheck(Class target) {
        ArrayList<Class> out = new ArrayList<>();
        out.add(Chocobo.class);
        out.add(EnderDragonEntity.class);
        out.add(PhantomEntity.class);
        out.add(PufferfishEntity.class);
        out.add(TropicalFishEntity.class);
        out.add(CodEntity.class);
        out.add(GlowSquidEntity.class);
        out.add(SquidEntity.class);
        out.add(SalmonEntity.class);
        out.add(BatEntity.class);
        out.add(DolphinEntity.class);
        out.add(GuardianEntity.class);
        out.add(ElderGuardianEntity.class);
        out.add(StriderEntity.class);
        out.add(GhastEntity.class);
        out.add(SkeletonHorseEntity.class);
        return !out.contains(target);
    }
    public static class ChocoboLavaEscape extends Goal {
        private final PathAwareEntity mob;
        private final Chocobo chocobo;
        public ChocoboLavaEscape(Chocobo pathfinderChocobo) {
            this.chocobo = pathfinderChocobo;
            this.mob = pathfinderChocobo;
        }
        private void TeleportTo(@NotNull BlockPos pPos) {
            PathNodeType PathNodeType = LandPathNodeMaker.getLandNodeType(mob.getWorld(), pPos.mutableCopy());
            if (PathNodeType == WALKABLE) {
                BlockPos blockpos = pPos.subtract(this.mob.getBlockPos());
                this.mob.getWorld().isSpaceEmpty(this.mob, this.mob.getBoundingBox().offset(blockpos));
            } else { mob.getMoveControl().moveTo(pPos.getX(), pPos.getY(), pPos.getZ(), this.chocobo.getFollowSpeedModifier()); }
        }
        private void canTeleport() {
            BlockPos block = null;
            BlockState newBlock = null;
            double mobX = this.mob.getX();
            double mobY = this.mob.getY();
            double mobZ = this.mob.getZ();

            for(BlockPos blockPos1 : BlockPos.iterate(MathHelper.floor(mobX - 10.0D), MathHelper.floor(mobY - 10.0D), MathHelper.floor(mobZ - 10.0D), MathHelper.floor(mobX + 10.0D), MathHelper.floor(mobY + 10D), MathHelper.floor(mobZ + 10.0D))) {
                if (!this.mob.getWorld().getFluidState(blockPos1).isIn(FluidTags.LAVA)) {
                    block = blockPos1;
                    newBlock = this.mob.getWorld().getBlockState(blockPos1);
                    break;
                }
            }
            if (newBlock != null) { this.TeleportTo(block); }
        }

        @Override
        public boolean canStart() { return this.mob.isInLava() && this.chocobo.getRideTickDelay() >= 15; }

        public void start() { canTeleport(); }
    }
    protected static double boundsFlip(Double vec3, int block, double limit) {
        double positiveDif = positiveDifference(vec3, block);
        if (limit > positiveDif) { return vec3; }
        return vec3 >= block ? block + limit : block - limit;
    }
    protected static double positiveDifference(Double vec3, int block) {
        double out = vec3 < block ? block - vec3 : vec3 - block;
        return out < 0 ? out * -1 : out;
    }
    private static int boundedModifier(double lower, double upper) {
        return (int) (random.nextDouble(upper)-lower);
    }
    public static class ChocoboRoamWonder extends WanderAroundGoal {
        final Chocobo choco;

        public ChocoboRoamWonder(Chocobo mob, double speed) {
            super(mob, speed, 120, !mob.cannotDespawn());
            this.choco = mob;
        }
        // using riding tick delay to limit movement starts to .5 seconds (10 ticks) out of 1.5 seconds (30 ticks)
        public boolean canStart() {
            boolean staggeredStart = choco.getRideTickDelay() > 20;
            if (staggeredStart) { return super.canStart(); } else { return false; }
        }
    }
    public static class ChocoboLocalizedWonder extends WanderAroundGoal {
        final Chocobo choco;
        final BlockPos blockPos;
        final double limit;
        double xSpot;
        double zSpot;
        public ChocoboLocalizedWonder(Chocobo pMob, double pSpeedModifier, BlockPos position, Double RangeLimit) {
            super(pMob, pSpeedModifier, 120, !pMob.cannotDespawn());
            this.blockPos = position;
            this.limit = RangeLimit;
            this.choco = pMob;
        }
        // using riding tick delay to limit movement starts to .75 seconds (15 ticks) out of 1.5 seconds (30 ticks)
        public boolean canStart() {
            boolean staggeredStart = choco.getRideTickDelay() > 15;
            if (staggeredStart) { return super.canStart(); } else { return false; }
        }
        @Nullable
        protected Vec3d getPosition(Vec3d target) {
            Vec3d vec3 = target;
            int x = boundedModifier(limit/2, limit);
            int z = boundedModifier(limit/2, limit);
            if (vec3 == null) { vec3 = new Vec3d(this.blockPos.getX()+x, this.blockPos.getY(), this.blockPos.getZ()+z); }
            this.xSpot = boundsFlip(vec3.getX(), this.blockPos.getX(), this.limit);
            this.zSpot = boundsFlip(vec3.getZ(), this.blockPos.getZ(), this.limit);
            return new Vec3d(this.xSpot, vec3.getY(), this.zSpot);
        }
        @Override
        @Nullable
        protected Vec3d getWanderTarget() {
            return getPosition(NoPenaltyTargeting.find(this.mob, 10, 7));
        }
    }
    public static class ChocoboOwnerHurtGoal extends AttackWithOwnerGoal {
        private final TameableEntity tameAnimal;
        private LivingEntity attacking;
        private int lastAttackTime;

        public ChocoboOwnerHurtGoal(TameableEntity pTameAnimal) {
            super(pTameAnimal);
            this.tameAnimal = pTameAnimal;
            this.setControls(EnumSet.of(Goal.Control.TARGET));
        }
        public boolean canStart() {
            if (tameAnimal.isTamed()) {
                LivingEntity livingEntity = tameAnimal.getOwner();
                if (livingEntity == null) { return false; }
                this.attacking = livingEntity.getAttacking();
                int i = livingEntity.getLastAttackTime();
                if (AttackClassCheck(this.attacking.getClass())) {
                    return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.tameAnimal.canAttackWithOwner(this.attacking, livingEntity);
                }
            }
            return false;
        }
        @Override
        public void start() {
            this.mob.setTarget(this.attacking);
            LivingEntity livingEntity = this.tameAnimal.getOwner();
            if (livingEntity != null) { this.lastAttackTime = livingEntity.getLastAttackTime(); }
            super.start();
        }
    }
    public static class ChocoboOwnerHurtByGoal extends TrackOwnerAttackerGoal {
        private final TameableEntity tameAnimal;
        private LivingEntity attacker;
        private int lastAttackedTime;

        public ChocoboOwnerHurtByGoal(TameableEntity pTameAnimal) {
            super(pTameAnimal);
            this.tameAnimal = pTameAnimal;
            this.setControls(EnumSet.of(Goal.Control.TARGET));
        }
        public boolean canStart() {
            if (tameAnimal.isTamed()) {
                LivingEntity livingentity = tameAnimal.getOwner();
                if (livingentity == null) { return false; }
                this.attacker = livingentity.getAttacker();
                int i = livingentity.getLastAttackedTime();
                if (AttackClassCheck(this.attacker.getClass())) {
                    return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT) && this.tameAnimal.canAttackWithOwner(this.attacker, livingentity);
                }
            }
            return false;
        }
        @Override
        public void start() {
            this.mob.setTarget(this.attacker);
            LivingEntity livingEntity = this.tameAnimal.getOwner();
            if (livingEntity != null) {
                this.lastAttackedTime = livingEntity.getLastAttackedTime();
            }
            super.start();
        }
    }
    public static class ChocoPanicGoal extends EscapeDangerGoal {
        final PathAwareEntity entity;

        public ChocoPanicGoal(PathAwareEntity mob, double pSpeed) {
            super(mob, pSpeed);
            this.entity = mob;
        }
        private boolean dragonDamageCheck() {
            LivingEntity livingentity = this.entity.getAttacker();
            boolean tame;
            if (this.entity instanceof Chocobo chocobo) { tame = chocobo.isTamed(); } else { tame = false; }
            if (livingentity != null && !tame) { return livingentity.getClass() == EnderDragonEntity.class; }
            return false;
        }
        protected boolean isInDanger() { return entity.shouldEscapePowderSnow() || entity.isOnFire() || dragonDamageCheck(); }
    }
    @SuppressWarnings("rawtypes")
    public static class ChocoboAvoidPlayer extends FleeEntityGoal {
        public ChocoboAvoidPlayer(PathAwareEntity pMob) {
            //noinspection unchecked
            super(pMob, PlayerEntity.class, livingEntity -> {
                if(livingEntity instanceof PlayerEntity player) {
                    int chance = 0;
                    for (ItemStack stack : player.getInventory().armor) { 
                        if (stack != null) {
                            if (stack.getItem() instanceof ChocoDisguiseItem) { chance += 25; }
                        }
                    }
                    return !RandomHelper.getChanceResult(chance);
                }
                return false;
            }, 10.0F, 1.0D, 1.2D, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
        }
    } // keeping commented out.
    public static class ChocoboHurtByTargetGoal extends RevengeGoal {
        public ChocoboHurtByTargetGoal(PathAwareEntity pMob, Class<?>... pToIgnoreDamage) { super(pMob, pToIgnoreDamage); }
        public boolean canStart() {
            LivingEntity livingentity = this.mob.getAttacker();
            boolean canAttack = true;
            if (livingentity != null) { canAttack = AttackClassCheck(livingentity.getClass()); }
            if (canAttack) { return super.canStart(); } else { return false; }
        }
    }
}