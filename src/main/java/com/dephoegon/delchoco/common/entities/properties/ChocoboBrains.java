// src/main/java/com/dephoegon/delchoco/common/entities/properties/ChocoboBrains.java
package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import com.dephoegon.delchoco.utils.RandomHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

import static com.dephoegon.delchoco.common.entities.properties.ChocoboBrainAid.isAttackable;

public class ChocoboBrains {

    public static final ImmutableList<? extends MemoryModuleType<?>> CHOCOBO_MODULES = ImmutableList.of(
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.VISIBLE_MOBS
    );
    public static final ImmutableList<? extends SensorType<? extends Sensor<? super Chocobo>>> CHOCOBO_SENSORS = ImmutableList.of(
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.HURT_BY
    );

    public static Brain<?> makeBrain(Brain<Chocobo> brain, Chocobo chocobo) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        addFightActivities(brain);
        addPanicActivities(brain);
        addAvoidPlayerActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }
    private static void addCoreActivities(Brain<Chocobo> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(
                new LookAroundTask(45, 90),
                new RoamTask(1F),
                new FollowOwnerTask(1.6, 10.0F, 300.0F, true)
        ));
    }
    private static void addIdleActivities(Brain<Chocobo> brain) {
        brain.setTaskList(Activity.IDLE, 5, ImmutableList.of(
                new OwnerHurtTask(),
                new OwnerHurtByTask(),
                new HurtByTargetTask(),
                LookAtMobTask.create(EntityType.PLAYER, 8.0f)
        ));
    }
    private static void addFightActivities(Brain<Chocobo> brain) {
        brain.setTaskList(Activity.FIGHT, 5, ImmutableList.of(
                ForgetAttackTargetTask.create(ChocoboBrainAid::isInvalidTarget),
                new ChocoboFightTask()
        ), MemoryModuleType.ATTACK_TARGET);
    }
    private static void addPanicActivities(Brain<Chocobo> brain) {
        brain.setTaskList(Activity.PANIC, 10, ImmutableList.of(
                new PanicTask(1.3F)
        ));
    }
    private static void addAvoidPlayerActivities(Brain<Chocobo> brain) {
        brain.setTaskList(Activity.AVOID, -1, ImmutableList.of(
                new AvoidPlayerTask(12F, 1.3F, 1.6F)
        ), MemoryModuleType.AVOID_TARGET);
    }

    public static class RoamTask extends MultiTickTask<Chocobo> {
        private static final int MAX_UPDATE_COUNTDOWN = 40;
        private int pathUpdateCountdownTicks = 0;
        private Path path;
        private BlockPos lookTargetPos;
        private final double speed;

        public RoamTask(float speed) {
            super(ImmutableMap.of(
                    MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED,
                    MemoryModuleType.PATH, MemoryModuleState.VALUE_ABSENT,
                    MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT
            ));
            this.speed = speed;
        }

        @Override
        protected boolean shouldRun(ServerWorld world, Chocobo chocobo) {
            if (this.pathUpdateCountdownTicks > 0) {
                this.pathUpdateCountdownTicks--;
                return false;
            }
            if (chocobo.getRideTickDelay() <= 20 || chocobo.followOwner() || chocobo.followLure()) { return false; }
            Vec3d pos = chocobo.isNoRoam() ? getPositionWithinLimit(world, chocobo) : NoPenaltyTargeting.find(chocobo, 10, 7);
            if (pos != null) {
                chocobo.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, (float) speed, 0));
                this.lookTargetPos = BlockPos.ofFloored(pos);
                return true;
            }
            return false;
        }

        @Override
        protected void run(ServerWorld world, Chocobo chocobo, long time) {
            Brain<?> brain = chocobo.getBrain();
            Optional<WalkTarget> walkTargetOpt = brain.getOptionalRegisteredMemory(MemoryModuleType.WALK_TARGET);
            if (walkTargetOpt.isPresent()) {
                WalkTarget walkTarget = walkTargetOpt.get();
                BlockPos targetPos = walkTarget.getLookTarget().getBlockPos();
                this.path = chocobo.getNavigation().findPathTo(targetPos, 0);
                if (this.path != null) {
                    brain.remember(MemoryModuleType.PATH, this.path);
                    chocobo.getNavigation().startMovingAlong(this.path, this.speed);
                }
            }
        }

        @Override
        protected boolean shouldKeepRunning(ServerWorld world, Chocobo chocobo, long time) {
            if (this.path == null || this.lookTargetPos == null) return false;
            Optional<WalkTarget> walkTargetOpt = chocobo.getBrain().getOptionalRegisteredMemory(MemoryModuleType.WALK_TARGET);
            EntityNavigation nav = chocobo.getNavigation();
            return walkTargetOpt.isPresent() && !nav.isIdle() && !hasReached(chocobo, walkTargetOpt.get());
        }

        @Override
        protected void keepRunning(ServerWorld world, Chocobo chocobo, long time) {
            Path currentPath = chocobo.getNavigation().getCurrentPath();
            Brain<?> brain = chocobo.getBrain();
            if (this.path != currentPath) {
                this.path = currentPath;
                brain.remember(MemoryModuleType.PATH, currentPath);
            }
        }

        @Override
        protected void finishRunning(ServerWorld world, Chocobo chocobo, long time) {
            if (chocobo.getBrain().hasMemoryModule(MemoryModuleType.WALK_TARGET)
                    && !hasReached(chocobo, chocobo.getBrain().getOptionalRegisteredMemory(MemoryModuleType.WALK_TARGET).get())
                    && chocobo.getNavigation().isNearPathStartPos()) {
                this.pathUpdateCountdownTicks = world.getRandom().nextInt(MAX_UPDATE_COUNTDOWN);
            }
            chocobo.getNavigation().stop();
            chocobo.getBrain().forget(MemoryModuleType.WALK_TARGET);
            chocobo.getBrain().forget(MemoryModuleType.PATH);
            this.path = null;
        }

        private boolean hasReached(Chocobo chocobo, WalkTarget walkTarget) {
            if (walkTarget == null || walkTarget.getLookTarget() == null) { return true; }
            return walkTarget.getLookTarget().getBlockPos().getManhattanDistance(chocobo.getBlockPos()) <= walkTarget.getCompletionRange();
        }
        private Vec3d getPositionWithinLimit(ServerWorld world, Chocobo chocobo) {
            BlockPos center = chocobo.getLeashSpot();
            int limit = chocobo.getLeashDistance();
            for (int i = 0; i < 10; i++) {
                Vec3d candidate = NoPenaltyTargeting.find(chocobo, limit, 7);
                if (candidate == null) { continue; }

                BlockPos candidatePos = BlockPos.ofFloored(candidate);

                // Check 3x3 area for wall or fence
                boolean nearWallOrFence = false;
                for (int dx = -1; dx <= 1 && !nearWallOrFence; dx++) {
                    for (int dz = -1; dz <= 1 && !nearWallOrFence; dz++) {
                        BlockPos checkPos = candidatePos.add(dx, 0, dz);
                        BlockState state = world.getBlockState(checkPos);
                        if (state.getBlock() instanceof FenceBlock || state.getBlock() instanceof WallBlock) { nearWallOrFence = true; }
                    }
                }
                if (nearWallOrFence) { continue; }
                if (chocobo.canWonder()) { return candidate; }

                double distSq = center.getSquaredDistance(candidate.x, candidate.y, candidate.z);
                if (distSq > limit * limit) { continue; }

                // Pathfinding check (optional, for extra safety)
                Path path = chocobo.getNavigation().findPathTo(candidatePos, 0);
                if (path != null && path.reachesTarget()) { return candidate; }
            }
            return null;
        }
    }
    public static class PanicTask extends MultiTickTask<Chocobo> {
        private final float speed;

        public PanicTask(float speed) {
            super(ImmutableMap.of());
            this.speed = speed;
        }

        private boolean shouldPanicFromAttacker(Chocobo chocobo) {
            LivingEntity attacker = chocobo.getAttacker();
            // Panic if there is an attacker and it is NOT attackable,  To include non-living entities (Mod Coverage)
            return attacker != null && !ChocoboBrainAid.isAttackable(attacker);
        }

        protected boolean isInDanger(Chocobo chocobo) {
            return chocobo.shouldEscapePowderSnow() || chocobo.isOnFire() || shouldPanicFromAttacker(chocobo);
        }

        @Override
        protected boolean shouldRun(ServerWorld world, Chocobo chocobo) {
            return isInDanger(chocobo);
        }

        @Override
        protected void run(ServerWorld world, Chocobo chocobo, long time) {
            Brain<?> brain = chocobo.getBrain();
            // Clear targets and set panic activity
            brain.forget(MemoryModuleType.PATH);
            brain.forget(MemoryModuleType.WALK_TARGET);
            brain.forget(MemoryModuleType.LOOK_TARGET);
            brain.doExclusively(Activity.PANIC);

            // Set a random walk target to make the chocobo run
            Vec3d pos = NoPenaltyTargeting.find(chocobo, 10, 7);
            if (pos != null) {
                brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, speed, 0));
            }
        }
    }
    public static class OwnerHurtTask extends MultiTickTask<Chocobo> {
        private LivingEntity attacking;
        private int lastAttackTime;

        public OwnerHurtTask() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean shouldRun(ServerWorld world, Chocobo chocobo) {
            if (chocobo.isTamed()) {
                LivingEntity owner = chocobo.getOwner();
                if (owner == null) return false;
                this.attacking = owner.getAttacking();
                int i = owner.getLastAttackTime();
                if (isAttackable(this.attacking)) {
                    return i != this.lastAttackTime && chocobo.canAttackWithOwner(attacking, owner);
                }
            }
            return false;
        }

        @Override
        protected void run(ServerWorld world, Chocobo chocobo, long time) {
            chocobo.getBrain().remember(MemoryModuleType.ATTACK_TARGET, this.attacking, 200L);
            LivingEntity owner = chocobo.getOwner();
            if (owner != null) this.lastAttackTime = owner.getLastAttackTime();
        }
    }
    public static class OwnerHurtByTask extends MultiTickTask<Chocobo> {
        private LivingEntity attacker;
        private int lastAttackedTime;

        public OwnerHurtByTask() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean shouldRun(ServerWorld world, Chocobo chocobo) {
            if (chocobo.isTamed()) {
                LivingEntity owner = chocobo.getOwner();
                if (owner == null) return false;
                this.attacker = owner.getAttacker();
                int i = owner.getLastAttackedTime();
                if (isAttackable(this.attacker)) {
                    return i != this.lastAttackedTime && chocobo.canAttackWithOwner(attacker, owner);
                }
            }
            return false;
        }

        @Override
        protected void run(ServerWorld world, Chocobo chocobo, long time) {
            chocobo.getBrain().remember(MemoryModuleType.ATTACK_TARGET, this.attacker, 200L);
            LivingEntity owner = chocobo.getOwner();
            if (owner != null) this.lastAttackedTime = owner.getLastAttackedTime();
        }
    }
    public static class HurtByTargetTask extends MultiTickTask<Chocobo> {
        public HurtByTargetTask() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean shouldRun(ServerWorld world, Chocobo chocobo) {
            return chocobo.getBrain()
                    .getOptionalRegisteredMemory(MemoryModuleType.HURT_BY_ENTITY)
                    .filter(ChocoboBrainAid::isAttackable)
                    .isPresent();
        }

        @Override
        protected void run(ServerWorld world, Chocobo chocobo, long time) {
            chocobo.getBrain()
                    .getOptionalRegisteredMemory(MemoryModuleType.HURT_BY_ENTITY)
                    .ifPresent(target -> chocobo.getBrain().remember(MemoryModuleType.ATTACK_TARGET, target, 200L));
        }
    }
    public static class AvoidPlayerTask extends MultiTickTask<Chocobo> {
        private final float distance;
        private final double slowSpeed;
        private final double fastSpeed;

        public AvoidPlayerTask(float distance, double slowSpeed, double fastSpeed) {
            super(ImmutableMap.of());
            this.distance = distance;
            this.slowSpeed = slowSpeed;
            this.fastSpeed = fastSpeed;
        }

        @Override
        protected boolean shouldRun(ServerWorld world, Chocobo chocobo) {
            if (chocobo.isTamed() || chocobo.isBaby()) { return false; }
            Optional<PlayerEntity> playerOpt = chocobo.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
            if (playerOpt.isEmpty()) { return false; }
            PlayerEntity closest = playerOpt.get();
            // Ignore creative or spectator players
            if (closest.isCreative() || closest.isSpectator()) { return false; }
            int chance = 0;
            for (ItemStack stack : closest.getInventory().armor) {
                if (stack != null && stack.getItem() instanceof ChocoDisguiseItem) { chance += 25; }
            }
            if (RandomHelper.getChanceResult(chance)) return false;
            // Set avoid target in memory
            chocobo.getBrain().remember(MemoryModuleType.AVOID_TARGET, closest, 200L);
            return true;
        }

        @Override
        protected void run(ServerWorld world, Chocobo chocobo, long time) {
            Optional<LivingEntity> avoidOpt = chocobo.getBrain().getOptionalRegisteredMemory(MemoryModuleType.AVOID_TARGET);
            if (avoidOpt.isPresent()) {
                LivingEntity avoid = avoidOpt.get();
                double dist = chocobo.squaredDistanceTo(avoid);
                double speed = dist < (distance * distance) / 2 ? fastSpeed : slowSpeed;
                Vec3d away = chocobo.getPos().subtract(avoid.getPos()).normalize().multiply(distance).add(chocobo.getPos());
                chocobo.getBrain().remember(
                        MemoryModuleType.WALK_TARGET,
                        new WalkTarget(away, (float) speed, 0), 50L
                );
            }
        }
    }
    public static class FollowOwnerTask extends MultiTickTask<Chocobo> {
        private final double speed;
        private final float minDistance;
        private final float maxDistance;
        private final boolean leavesAllowed;
        private LivingEntity owner;

        public FollowOwnerTask(double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
            super(ImmutableMap.of());
            this.speed = speed;
            this.minDistance = minDistance;
            this.maxDistance = maxDistance;
            this.leavesAllowed = leavesAllowed;
        }

        @Override
        protected boolean shouldRun(ServerWorld world, Chocobo chocobo) {
            // Built-in checks for taming, sitting, vehicle, and leashing
            if (!chocobo.isTamed() || chocobo.isSitting() || chocobo.hasVehicle() || chocobo.isLeashed()) return false;
            if (!chocobo.followOwner()) return false;
            LivingEntity owner = chocobo.getOwner();
            if (owner == null || owner.isSpectator()) return false;
            if (chocobo.squaredDistanceTo(owner) < (double)(minDistance * minDistance)) return false;
            this.owner = owner;
            return true;
        }

        @Override
        protected void run(ServerWorld world, Chocobo chocobo, long time) {
            if (owner == null) return;
            double distSq = chocobo.squaredDistanceTo(owner);
            if (distSq >= 144.0) {
                tryTeleport(world, chocobo);
            } else {
                chocobo.getBrain().remember(
                        MemoryModuleType.WALK_TARGET,
                        new WalkTarget(owner, (float) speed, (int) minDistance)
                );
            }
        }

        @Override
        protected boolean shouldKeepRunning(ServerWorld world, Chocobo chocobo, long time) {
            if (owner == null) return false;
            if (chocobo.isSitting() || chocobo.hasVehicle() || chocobo.isLeashed()) return false;
            return chocobo.squaredDistanceTo(owner) > (double)(maxDistance * maxDistance);
        }

        @Override
        protected void finishRunning(ServerWorld world, Chocobo chocobo, long time) {
            this.owner = null;
            chocobo.getBrain().forget(MemoryModuleType.WALK_TARGET);
        }

        private void tryTeleport(ServerWorld world, Chocobo chocobo) {
            BlockPos ownerPos = owner.getBlockPos();
            for (int i = 0; i < 10; ++i) {
                int dx = getRandomInt(chocobo, -3, 3);
                int dy = getRandomInt(chocobo, -1, 1);
                int dz = getRandomInt(chocobo, -3, 3);
                BlockPos pos = ownerPos.add(dx, dy, dz);
                if (Math.abs(pos.getX() - ownerPos.getX()) < 2 && Math.abs(pos.getZ() - ownerPos.getZ()) < 2) continue;
                if (canTeleportTo(world, chocobo, pos)) {
                    chocobo.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, chocobo.getYaw(), chocobo.getPitch());
                    chocobo.getNavigation().stop();
                    break;
                }
            }
        }

        private boolean canTeleportTo(ServerWorld world, Chocobo chocobo, BlockPos pos) {
            // PathNodeType type = LandPathNodeMaker.getLandNodeType(world, pos.mutableCopy());
            PathNodeType type = chocobo.getNavigation().getNodeMaker().getDefaultNodeType(world, pos.getX(), pos.getY(), pos.getZ());
            if (type != PathNodeType.WALKABLE) return false;
            if (!leavesAllowed && world.getBlockState(pos.down()).getBlock().getTranslationKey().contains("leaves")) return false;
            return world.isSpaceEmpty(chocobo, chocobo.getBoundingBox().offset(Vec3d.of(pos.subtract(chocobo.getBlockPos()))));
        }

        private int getRandomInt(Chocobo chocobo, int min, int max) { return chocobo.getRandom().nextInt(max - min + 1) + min; }
    }
    public static class ChocoboFightTask extends MultiTickTask<Chocobo> {
        private static final int ATTACK_INTERVAL = 20; // ticks between attacks
        private int attackCooldown = 0;
        public ChocoboFightTask() {
            super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT));
        }

        @Override
        protected boolean shouldRun(ServerWorld world, Chocobo chocobo) {
            return chocobo.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET)
                    .filter(ChocoboBrainAid::isAttackable)
                    .isPresent();
        }

        @Override
        protected void run(ServerWorld world, Chocobo chocobo, long time) {
            LivingEntity target = chocobo.getBrain()
                    .getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET)
                    .orElse(null);
            if (target == null) return;

            double attackReach = chocobo.getWidth() * 1.5F + target.getWidth();
            double distanceSq = chocobo.squaredDistanceTo(target);

            // Face the target
            chocobo.getLookControl().lookAt(target, 30.0F, 30.0F);

            // Approach if not in range
            if (distanceSq > attackReach * attackReach) {
                chocobo.getNavigation().startMovingTo(target, 1.2D);
            } else {
                chocobo.getNavigation().stop();
                // Attack if cooldown is ready
                if (attackCooldown <= 0) {
                    chocobo.swingHand(chocobo.getActiveHand());
                    chocobo.tryAttack(target); // Uses main hand weapon
                    attackCooldown = ATTACK_INTERVAL;
                }
            }
            if (attackCooldown > 0) attackCooldown--;
        }
        @Override
        protected void finishRunning(ServerWorld world, Chocobo chocobo, long time) {
            chocobo.getNavigation().stop();
            attackCooldown = 0;
        }
    }
}