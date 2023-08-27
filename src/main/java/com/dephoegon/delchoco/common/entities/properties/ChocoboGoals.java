package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import com.dephoegon.delchoco.utils.RandomHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
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
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;

import static com.dephoegon.delchoco.utils.RandomHelper.random;
import static net.minecraft.entity.ai.pathing.NavigationType.LAND;
import static net.minecraft.entity.ai.pathing.PathNodeType.WALKABLE;

public class ChocoboGoals {
    @SuppressWarnings("rawtypes")
    private static boolean doNotAttackClassCheck(Class target) {
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
            PathNodeType PathNodeType = LandPathNodeMaker.getLandNodeType(mob.world, pPos.mutableCopy());
            if (PathNodeType == WALKABLE) {
                BlockPos blockpos = pPos.subtract(this.mob.getBlockPos());
                this.mob.world.isSpaceEmpty(this.mob, this.mob.getBoundingBox().offset(blockpos));
            } else { mob.getMoveControl().moveTo(pPos.getX(), pPos.getY(), pPos.getZ(), this.chocobo.getFollowSpeedModifier()); }
        }
        private void canTeleport() {
            BlockPos block = null;
            BlockState newBlock = null;
            double mobX = this.mob.getX();
            double mobY = this.mob.getY();
            double mobZ = this.mob.getZ();

            for(BlockPos blockPos1 : BlockPos.iterate(MathHelper.floor(mobX - 10.0D), MathHelper.floor(mobY - 10.0D), MathHelper.floor(mobZ - 10.0D), MathHelper.floor(mobX + 10.0D), MathHelper.floor(mobY + 10D), MathHelper.floor(mobZ + 10.0D))) {
                if (!this.mob.world.getFluidState(blockPos1).isIn(FluidTags.LAVA)) {
                    block = blockPos1;
                    newBlock = this.mob.world.getBlockState(blockPos1);
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
    public static class ChocoboRandomStrollGoal extends WanderAroundGoal {
        final BlockPos blockPos;
        final double limit;
        double xSpot;
        double zSpot;
        public ChocoboRandomStrollGoal(Chocobo pMob, double pSpeedModifier, BlockPos position, Double RangeLimit) {
            super(pMob, pSpeedModifier);
            this.blockPos = position;
            this.limit = RangeLimit;
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
        @Nullable
        protected Vec3d getWanderTarget() {
            Vec3d target = NoPenaltyTargeting.find(this.mob, 10, 7);
            return getPosition(target);
        }
    }
    public static class ChocoboLocalizedWonder extends WanderAroundFarGoal {
        final BlockPos blockPos;
        final double limit;
        double xSpot;
        double zSpot;
        public ChocoboLocalizedWonder(Chocobo pMob, double pSpeedModifier, BlockPos position, Double RangeLimit) {
            super(pMob, pSpeedModifier);
            this.blockPos = position;
            this.limit = RangeLimit;
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
        protected Vec3d getRandomPosition() {
            Vec3d target = FuzzyTargeting.find(this.mob, 10, 7);
            return getPosition(target);
        }
        @Override
        @Nullable
        protected Vec3d getWanderTarget() {
            if (this.mob.isInsideWaterOrBubbleColumn()) {
                Vec3d vec3d = FuzzyTargeting.find(this.mob, 15, 7);
                return vec3d == null ? getRandomPosition() : getPosition(vec3d);
            }
            return getRandomPosition();
        }
    }
    public static class ChocoboAvoidBlockGoal extends Goal {
        private final PathAwareEntity mob;
        private final ArrayList<Class<? extends Block>> classes;

        public ChocoboAvoidBlockGoal(PathAwareEntity pMob, ArrayList<Class<? extends Block>> classLists) {
            this.mob = pMob;
            this.classes = classLists;
        }
        public boolean canStart() { return mob.isOnGround(); }

        public void start() {
            BlockPos block = null;
            BlockPos newBlockPos = null;
            double mobX = mob.getX();
            double mobY = mob.getY();
            double mobZ = mob.getZ();

            // Checks ArrayList for Classes of blocks for match, & moves on.
            for(BlockPos blockPos1 : BlockPos.iterate(MathHelper.floor(mobX - 2.0D), MathHelper.floor(mobY - 2.0D), MathHelper.floor(mobZ - 2.0D), MathHelper.floor(mobX + 2.0D), this.mob.getBlockY(), MathHelper.floor(mobZ + 2.0D))) {
                Class<? extends Block> block1 = mob.world.getBlockState(blockPos1).getBlock().getClass();
                if (classes.contains(block1)) {
                    block = blockPos1;
                    break;
                }
            }

            // Gets Distance between Block in X & Z, uses it to check distance in favor of away from the block
            if (block != null) {
                double posX = ((mobX - block.getX())*-1)+mobX;
                double blockY = block.getY();
                double posZ = ((mobZ - block.getZ())*-1)+mobZ;
                // Increases the distance by a block to avoid accidental resource hogging/looping
                posZ = posZ > 0 ? posZ+1 : posZ-1;
                posX = posX > 0 ? posX+1 : posX-1;

                // Looks for Pathfindable ground within range of Chocobo to fence & Y+4 from chocobo, & blockposY-4,
                for(BlockPos blockPos2 : BlockPos.iterate(MathHelper.floor(posX), MathHelper.floor(mobY+4), MathHelper.floor(posZ), MathHelper.floor(mobX), MathHelper.floor(blockY-4), MathHelper.floor(mobZ))) {
                    BlockState blockState = mob.world.getBlockState(blockPos2);
                    if (blockState.canPathfindThrough(mob.world, blockPos2, LAND)){
                        newBlockPos = blockPos2;
                        break;
                    }
                }
                // Sets postion to wonder towards.
                if (newBlockPos != null) { mob.getMoveControl().moveTo(newBlockPos.getX(), newBlockPos.getY(), newBlockPos.getZ(), 1.0D); }
            }
        }
    }
    public static class ChocoboOwnerHurtGoal extends AttackWithOwnerGoal {
        private final TameableEntity tameAnimal;

        public ChocoboOwnerHurtGoal(TameableEntity pTameAnimal) {
            super(pTameAnimal);
            this.tameAnimal = pTameAnimal;
            this.setControls(EnumSet.of(Goal.Control.TARGET));
        }
        public boolean canStart() {
            if (tameAnimal.isTamed()) {
                LivingEntity livingentity = tameAnimal.getOwner();
                if (livingentity == null) { return false; }
                else {
                    Class<? extends LivingEntity> ownerLastHurt;
                    if (livingentity.getAttacking() != null) { ownerLastHurt = livingentity.getAttacking().getClass();}
                    else { ownerLastHurt = null; }
                    return (doNotAttackClassCheck(ownerLastHurt)) && super.canStart();
                }
            } else { return super.canStart(); }
        }
    }
    public static class ChocoboOwnerHurtByGoal extends TrackOwnerAttackerGoal {
        private final TameableEntity tameAnimal;

        public ChocoboOwnerHurtByGoal(TameableEntity pTameAnimal) {
            super(pTameAnimal);
            this.tameAnimal = pTameAnimal;
            this.setControls(EnumSet.of(Goal.Control.TARGET));
        }
        public boolean canStart() {
            if (tameAnimal.isTamed()) {
                LivingEntity livingentity = tameAnimal.getOwner();
                if (livingentity == null) { return false; }
                else {
                    Class<? extends LivingEntity> ownerLastHurtBy;
                    if (livingentity.getAttacking() != null) { ownerLastHurtBy = livingentity.getAttacking().getClass(); }
                    else { ownerLastHurtBy = null; }
                    return doNotAttackClassCheck(ownerLastHurtBy) && super.canStart();
                }
            } else { return super.canStart(); }
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
                    for (ItemStack stack : player.getInventory().armor) { if (stack != null) {
                        if (stack.getItem() instanceof ChocoDisguiseItem) { chance += 25; }
                    } }
                    return !RandomHelper.getChanceResult(chance);
                }
                return false;
            }, 10.0F, 1.0D, 1.2D, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
        }
    }
    public static class ChocoboHurtByTargetGoal extends RevengeGoal {
        public ChocoboHurtByTargetGoal(PathAwareEntity pMob, Class<?>... pToIgnoreDamage) { super(pMob, pToIgnoreDamage); }
        public boolean canStart() {
            LivingEntity livingentity = this.mob.getAttacker();
            boolean canAttack = true;
            if (livingentity != null) { canAttack = doNotAttackClassCheck(livingentity.getClass()); }
            return canAttack && super.canStart();
        }
    }
}