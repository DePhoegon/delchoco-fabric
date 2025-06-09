package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import com.dephoegon.delchoco.utils.RandomHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
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

import static com.dephoegon.delchoco.common.entities.properties.ChocoboBrainAid.isAttackable;
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
    protected static int boundsFlip(double vec3, int block, int limit) {
        double positiveDif = positiveDifference(vec3, block);
        if (limit > positiveDif) { return (int) vec3; }
        return vec3 >= block ? block + limit : block - limit;
    }
    protected static int positiveDifference(double vec3, int block) {
        int out = (int) (vec3 < block ? block - vec3 : vec3 - block);
        return out < 0 ? out * -1 : out;
    }
    private static int boundedModifier(double lower, double upper) {
        return (int) (random.nextDouble(upper)-lower);
    }
    public static class ChocoboRoamWonder extends WanderAroundGoal {
        final Chocobo choco;
        BlockPos blockPos;
        int limit = 0;
        double xSpot;
        double zSpot;
        int followMrHuman;

        public void tick() {
            followMrHuman = choco.isTamed() ? choco.followingMrHuman : 2;
            limit = choco.getLeashDistance();
            blockPos = choco.getLeashSpot();
            this.stop();
        }
        public ChocoboRoamWonder(Chocobo mob, double speed) {
            super(mob, speed, 120, !mob.cannotDespawn());
            this.choco = mob;
        }
        // using riding tick delay to limit movement starts to .5 seconds (10 ticks) out of 1.5 seconds (30 ticks)
        public boolean canStart() {
            if (choco.followOwner() || choco.followLure()) { return false; }
            boolean staggeredStart = choco.getRideTickDelay() > 20;
            if (staggeredStart) { return super.canStart(); } else { return false; }
        }
        @Nullable
        protected Vec3d getPosition(Vec3d target) {
            Vec3d vec3 = target;
            this.limit = choco.getLeashDistance();
            if (this.limit < 2) { this.limit = 5; } // if the leash distance is less than 2, set it to 5
            if (this.blockPos.getX() == 0 && this.blockPos.getZ() == 0) { this.blockPos = choco.getBlockPos(); } // if the leash spot is not set, use the chocobo's position
            int x = boundedModifier((double) limit /2, limit);
            int z = boundedModifier((double) limit /2, limit);
            if (vec3 == null) { vec3 = new Vec3d(this.blockPos.getX()+x, this.blockPos.getY(), this.blockPos.getZ()+z); }
            this.xSpot = boundsFlip(vec3.getX(), this.blockPos.getX(), this.limit);
            this.zSpot = boundsFlip(vec3.getZ(), this.blockPos.getZ(), this.limit);
            return new Vec3d(this.xSpot, vec3.getY(), this.zSpot);
        }
        @Override
        @Nullable
        protected Vec3d getWanderTarget() {
            return choco.isNoRoam() ? getPosition(NoPenaltyTargeting.find(this.choco, 10, 7)) : super.getWanderTarget();
        }
        @Override
        public void stop() {
            if (choco == null || choco.isRemoved() || choco.getWorld() == null) { super.stop(); }
            else if (choco.followOwner() || choco.followLure()) { super.stop(); }
        }
    }
    public static class ChocoboOwnerHurtGoal extends AttackWithOwnerGoal {
        private final Chocobo chocobo;
        private LivingEntity attacking;
        private int lastAttackTime;

        public ChocoboOwnerHurtGoal(Chocobo pTameAnimal) {
            super(pTameAnimal);
            this.chocobo = pTameAnimal;
            this.setControls(EnumSet.of(Goal.Control.TARGET));
        }
        public boolean canStart() {
            return super.canStart();
            /*
            if (chocobo.isTamed()) {
                LivingEntity chocoboOwner = chocobo.getOwner();
                if (chocoboOwner == null) { return false; }
                this.attacking = chocoboOwner.getAttacking();
                int i = chocoboOwner.getLastAttackTime();
                DelChoco.LOGGER.info("ChocoboOwnerHurtGoal.canStart() - Chocobo: {} - Attacking: {} - Last Attack Time: {} - Can attack With Owner: {}", chocobo.getName().getString(), this.attacking != null ? this.attacking.getName().getString() : "null", i, chocobo.canAttackWithOwner(this.attacking, chocoboOwner));
                return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.chocobo.canAttackWithOwner(this.attacking, chocoboOwner);
            }
            return false;
            */
        }
    }
    public static class ChocoboOwnerHurtByGoal extends TrackOwnerAttackerGoal {
        private final Chocobo chocobo;
        private LivingEntity attacker;
        private int lastAttackedTime;

        public ChocoboOwnerHurtByGoal(Chocobo pTameAnimal) {
            super(pTameAnimal);
            this.chocobo = pTameAnimal;
            this.setControls(EnumSet.of(Goal.Control.TARGET));
        }
        public boolean canStart() {
            return super.canStart();
            /*
            if (chocobo.isTamed()) {
                LivingEntity chocoboOwner = chocobo.getOwner();
                if (chocoboOwner == null) { return false; }
                this.attacker = chocoboOwner.getAttacker() ;
                int i = chocoboOwner.getLastAttackedTime();
                return  i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT) & isAttackable(this.attacker);
            }
            return false;
            */
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
            if (livingentity == null) { return false; }
            return livingentity.getClass() == EnderDragonEntity.class;
        }
        protected boolean isInDanger() { return entity.shouldEscapePowderSnow() || entity.isOnFire() || dragonDamageCheck(); }
    }
    @SuppressWarnings("rawtypes")
    public static class ChocoboAvoidPlayer extends FleeEntityGoal {
        private final Chocobo mob;
        public ChocoboAvoidPlayer(Chocobo pMob) {
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
            this.mob = pMob;
        }
        public boolean canStart() {
            if (mob.isTamed()) { return false; }
            return super.canStart();
        }
    } // keeping commented out.
    public static class ChocoboHurtByTargetGoal extends RevengeGoal {
        public ChocoboHurtByTargetGoal(PathAwareEntity pMob, Class<?>... pToIgnoreDamage) { super(pMob, pToIgnoreDamage); }
        public boolean canStart() {
            LivingEntity chocoboAttacker = this.mob.getAttacker();
            if (isAttackable(chocoboAttacker)) { return super.canStart(); } else { return false; }
        }
    }
    public static class ChocoboFollowOwnerGoal extends FollowOwnerGoal {
        private final Chocobo chocobo;
        public ChocoboFollowOwnerGoal(Chocobo pMob, double pSpeedModifier, float pMinDist, float pMaxDist) {
            super(pMob, pSpeedModifier, pMinDist, pMaxDist, true);
            this.chocobo = pMob;
        }
        public boolean canStart() {
            if (!chocobo.isTamed()) { return false; }
            if (chocobo.followOwner()) { return super.canStart(); }
            return false;
        }
    }
}