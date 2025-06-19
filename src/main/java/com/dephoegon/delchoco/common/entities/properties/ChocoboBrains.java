// src/main/java/com/dephoegon/delchoco/common/entities/properties/ChocoboBrains.java
package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.common.entities.AbstractChocobo;
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
import net.minecraft.entity.ai.FuzzyPositions;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.dephoegon.delchoco.common.entities.properties.ChocoboBrainAid.isAttackable;
import static net.minecraft.entity.ai.pathing.NavigationType.LAND;

public class ChocoboBrains {

    public static final ImmutableList<? extends MemoryModuleType<?>> CHOCOBO_MODULES = ImmutableList.of(
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.VISIBLE_MOBS
    );
    public static final ImmutableList<? extends SensorType<? extends Sensor<? super Chocobo>>> CHOCOBO_SENSORS = ImmutableList.of(
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.HURT_BY
    );

    public static Brain<?> makeBrain(Brain<Chocobo> brain, Chocobo choco) {
        addCoreActivities(brain, choco);
        addIdleActivities(brain);
        // addFightActivities(brain, choco);
        addPanicActivities(brain);
        addAvoidPlayerActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<Chocobo> brain, Chocobo chocobo) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(
                new TryFindLandTask(1.1f),
                // new OwnerHurtTask(),
                // new OwnerHurtByTask(),
                // new HurtByTargetTask(),
                new RoamTask(1F),
                new FollowOwnerTask(1.6, 10.0F, 300.0F, true)
        ));
    }

    private static void addIdleActivities(Brain<Chocobo> brain) {
        brain.setTaskList(Activity.IDLE, 10, ImmutableList.of(
                new LookAroundTask(45, 90),
                LookAtMobTask.create(EntityType.PLAYER, 8.0f)
        ));
    }

    private static void addFightActivities(Brain<Chocobo> brain, Chocobo chocobo) {
        brain.setTaskList(Activity.FIGHT, 0, ImmutableList.of(
                ForgetAttackTargetTask.create(ChocoboBrainAid::isInvalidTarget),
                RangedApproachTask.create(1.2F), // Consider if this speed is appropriate for water
                AttackTask.create((int) (chocobo.getBoundingBox().getZLength()*1.5F), 1.2F) // And this one
                /*MeleeAttackTask.create(20)*/
        ), MemoryModuleType.ATTACK_TARGET);
    }

    private static void addPanicActivities(Brain<Chocobo> brain) {
        brain.setTaskList(Activity.PANIC, 10, ImmutableList.of(
                new PanicTask(1.3F)
        ));
    }

    private static void addAvoidPlayerActivities(Brain<Chocobo> brain) {
        brain.setTaskList(Activity.AVOID, -1, ImmutableList.of(
                new AvoidPlayerTask(12F, 1.1F, 1.3F)
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
                    MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT // Ensure RoamTask only runs if no walk target
            ));
            this.speed = speed;
        }

        @Override
        protected boolean shouldRun(ServerWorld world, Chocobo chocobo) {
            if (this.pathUpdateCountdownTicks > 0) {
                this.pathUpdateCountdownTicks--;
                return false;
            }
            // TryFindLandTask has priority due to memory conditions.
            // If this task runs, it means TryFindLandTask didn't (or failed to set a target).
            if (chocobo.getRideTickDelay() <= 20 || chocobo.followOwner() || chocobo.followLure()) {
                return false;
            }

            Vec3d pos;
            if (chocobo.isWaterBreathing() && chocobo.isSubmergedInWater()) { // Check if it's a swimmer and in water
                // For swimmers in water, find a target suitable for swimming
                pos = FuzzyTargeting.find(chocobo, 10, 7); // Use vanilla fuzzy targeting for swim
            } else {
                // Original logic for non-swimmers or swimmers on land
                pos = chocobo.isNoRoam() ? getPositionWithinLimit(world, chocobo) : ChocoboNoPenaltyTargeting.find(chocobo, 10, 7);
            }

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
            if (this.path == null || this.lookTargetPos == null) { return false; }
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
            if (walkTarget == null || walkTarget.getLookTarget() == null) {
                return true;
            }
            return walkTarget.getLookTarget().getBlockPos().getManhattanDistance(chocobo.getBlockPos()) <= walkTarget.getCompletionRange();
        }

        private Vec3d getPositionWithinLimit(ServerWorld world, Chocobo chocobo) {
            BlockPos center = chocobo.getLeashSpot();
            int limit = chocobo.getLeashDistance();
            for (int i = 0; i < 10; i++) {
                Vec3d candidate = ChocoboNoPenaltyTargeting.find(chocobo, limit, 7);
                if (candidate == null) { continue; }

                BlockPos candidatePos = BlockPos.ofFloored(candidate);

                // Check 3x3 area for wall or fence
                boolean nearWallOrFence = false;
                for (int dx = -1; dx <= 1 && !nearWallOrFence; dx++) {
                    for (int dz = -1; dz <= 1 && !nearWallOrFence; dz++) {
                        BlockPos checkPos = candidatePos.add(dx, 0, dz);
                        BlockState state = world.getBlockState(checkPos);
                        if (state.getBlock() instanceof FenceBlock || state.getBlock() instanceof WallBlock) {
                            nearWallOrFence = true;
                        }
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
            if (attacker == null) { return false; } // No attacker, no panic
                // Panic if there is an attacker and it is NOT attackable,  To include non-living entities (Mod Coverage)
                return !ChocoboBrainAid.isAttackable(attacker);
            }

            protected boolean isInDanger (Chocobo chocobo){
                return chocobo.shouldEscapePowderSnow() || chocobo.isOnFire() || shouldPanicFromAttacker(chocobo);
            }

            @Override
            protected boolean shouldRun (ServerWorld world, Chocobo chocobo){
                return isInDanger(chocobo);
            }

            @Override
            protected void run (ServerWorld world, Chocobo chocobo,long time){
                Brain<?> brain = chocobo.getBrain();
                // Clear targets and set panic activity
                brain.forget(MemoryModuleType.PATH);
                brain.forget(MemoryModuleType.WALK_TARGET);
                brain.forget(MemoryModuleType.LOOK_TARGET);
                brain.doExclusively(Activity.PANIC);

                // Set a random walk target to make the chocobo run
                Vec3d pos;
                if (chocobo.isWaterBreathing() && chocobo.isSubmergedInWater()) {
                    pos = FuzzyTargeting.find(chocobo, 10, 7); // Find a 3D point in water for swimmers
                } else {
                    pos = ChocoboNoPenaltyTargeting.find(chocobo, 10, 7); // Original logic for land/surface
                }
                if (pos != null) {
                    brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, speed, 0));
                }
            }
        }

    public static class OwnerHurtTask extends MultiTickTask<Chocobo> {
        private LivingEntity ownerTarget;
        private int lastAttackTime;

        public OwnerHurtTask() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean shouldRun(ServerWorld world, Chocobo chocobo) {
            if (chocobo.isTamed()) {
                LivingEntity owner = chocobo.getOwner();
                if (owner == null) { return false; }
                this.ownerTarget = owner.getAttacking();
                int i = owner.getLastAttackTime();
                if (isAttackable(this.ownerTarget)) {
                    // return i != this.lastAttackTime && chocobo.canAttackWithOwner(attacking, owner);
                    return chocobo.canAttackWithOwner(ownerTarget, owner);
                }
            }
            return false;
        }

        @Override
        protected void run(ServerWorld world, Chocobo chocobo, long time) {
            chocobo.getBrain().remember(MemoryModuleType.ATTACK_TARGET, this.ownerTarget, 200L);
        }
    }

    public static class OwnerHurtByTask extends MultiTickTask<Chocobo> {
        private LivingEntity ownerAttacker;
        private int lastAttackedTime;

        public OwnerHurtByTask() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean shouldRun(ServerWorld world, Chocobo chocobo) {
            if (chocobo.isTamed()) {
                LivingEntity owner = chocobo.getOwner();
                if (owner == null) { return false; }
                this.ownerAttacker = owner.getAttacker();
                if (this.ownerAttacker == null) { return false; }
                if (isAttackable(this.ownerAttacker)) {
                    //return i != this.lastAttackedTime && chocobo.canAttackWithOwner(attacker, owner);
                    return chocobo.canAttackWithOwner(ownerAttacker, owner);
                }
            }
            return false;
        }

        @Override
        protected void run(ServerWorld world, Chocobo chocobo, long time) {
            chocobo.getBrain().remember(MemoryModuleType.ATTACK_TARGET, this.ownerAttacker, 200L);
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
            if (chocobo.isTamed() || chocobo.isBaby()) {
                return false;
            }
            Optional<PlayerEntity> playerOpt = chocobo.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
            if (playerOpt.isEmpty()) {
                return false;
            }
            PlayerEntity closest = playerOpt.get();
            // Ignore creative or spectator players
            if (closest.isCreative() || closest.isSpectator()) {
                return false;
            }
            int chance = 0;
            for (ItemStack stack : closest.getInventory().armor) {
                if (stack != null && stack.getItem() instanceof ChocoDisguiseItem) {
                    chance += 25;
                }
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
            if (chocobo.squaredDistanceTo(owner) < (double) (minDistance * minDistance)) return false;
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
            return chocobo.squaredDistanceTo(owner) > (double) (maxDistance * maxDistance);
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
            if (!leavesAllowed && world.getBlockState(pos.down()).getBlock().getTranslationKey().contains("leaves"))
                return false;
            return world.isSpaceEmpty(chocobo, chocobo.getBoundingBox().offset(Vec3d.of(pos.subtract(chocobo.getBlockPos()))));
        }

        private int getRandomInt(Chocobo chocobo, int min, int max) {
            return chocobo.getRandom().nextInt(max - min + 1) + min;
        }
    }

    public static class ChocoboFightTask extends MeleeAttackTask {
        public static SingleTickTask<MobEntity> create(int cooldown) {
            return TaskTriggerer.task(context -> context.group(context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET), context.queryMemoryAbsent(MemoryModuleType.ATTACK_COOLING_DOWN), context.queryMemoryValue(MemoryModuleType.VISIBLE_MOBS)).apply(context, (lookTarget, attackTarget, attackCoolingDown, visibleMobs) -> (world, entity, time) -> {
                LivingEntity livingEntity = (LivingEntity) context.getValue(attackTarget);
                if (ChocoboBrainAid.isInvalidTarget(livingEntity)) {
                    lookTarget.forget();
                    return false;
                }
                if (/* removed ranged check */entity.isInAttackRange(livingEntity) && ((LivingTargetCache) context.getValue(visibleMobs)).contains(livingEntity)) {
                    lookTarget.remember(new EntityLookTarget(livingEntity, true));
                    entity.swingHand(Hand.MAIN_HAND);
                    entity.tryAttack(livingEntity);
                    attackCoolingDown.remember(true, cooldown);
                    return true;
                }
                return false;
            }));
        }
    }

    public static class TryFindLandTask extends MultiTickTask<Chocobo> {
        private static final int SEARCH_RANGE_VERTICAL = 8; // Search up/down a bit
        private final float speed;

        public TryFindLandTask(float speed) {
            super(ImmutableMap.of(
                    MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, // Only run if no walk target
                    MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, // Don't interrupt attacking
                    MemoryModuleType.PATH, MemoryModuleState.VALUE_ABSENT // And no current path
            ));
            this.speed = speed;
        }

        @Override
        protected boolean shouldRun(ServerWorld world, @NotNull Chocobo choco) {
            // 5 minutes = 5 * 60 * 20 = 6000 ticks.
            return choco.canWalkOnWater()
                    && choco.getTicksOnWater() > 6000
                    && (choco.isTouchingWater() || choco.isSubmergedInWater())
                    && (!choco.hasPlayerRider() || choco.getVehicle() == null)
                    && !choco.followOwner();
        }

        @Override
        protected void run(ServerWorld world, @NotNull Chocobo chocobo, long time) {
            int searchRangeHorizontal = (int) chocobo.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
            BlockPos landPosCandidate = findGeometricallySuitableLandPos(chocobo, world, searchRangeHorizontal, SEARCH_RANGE_VERTICAL);

            if (landPosCandidate != null) {
                Path path = chocobo.getNavigation().findPathTo(landPosCandidate, 0);

                if (path != null && path.reachesTarget()) {
                    // Path found to the candidate, set walk target
                    chocobo.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(Vec3d.ofCenter(landPosCandidate), this.speed, 0));
                } else {
                    // No path found to this specific candidate, or path doesn't reach. Teleport.
                    // isGeometricallySuitableLand should have ensured landPosCandidate is a safe spot.
                    chocobo.refreshPositionAndAngles(landPosCandidate.getX() + 0.5, landPosCandidate.getY(), landPosCandidate.getZ() + 0.5, chocobo.getYaw(), chocobo.getPitch());
                    chocobo.getNavigation().stop(); // Stop any current pathing after teleport
                }
            }
            // If no landPosCandidate found by findGeometricallySuitableLandPos, this task does nothing further.
            // RoamTask might pick up if its conditions are met.
        }

        @Nullable
        private BlockPos findGeometricallySuitableLandPos(Chocobo chocobo, ServerWorld world, int horizontalRange, int verticalRange) {
            for (int i = 0; i < 20; ++i) { // Try up to 20 times to find a suitable spot
                BlockPos entityBlockPos = chocobo.getBlockPos();
                // Generate a random position within the search range
                int x = entityBlockPos.getX() + chocobo.getRandom().nextInt(horizontalRange * 2 + 1) - horizontalRange;
                // Search vertically around the Chocobo's current Y level, allowing some up/down variation
                int y = entityBlockPos.getY() + chocobo.getRandom().nextInt(verticalRange * 2 + 1) - verticalRange;
                int z = entityBlockPos.getZ() + chocobo.getRandom().nextInt(horizontalRange * 2 + 1) - horizontalRange;
                BlockPos candidatePos = new BlockPos(x, y, z);

                if (isGeometricallySuitableLand(world, candidatePos, chocobo)) { return candidatePos; }
            }
            return null; // Failed to find suitable land in attempts
        }

        private boolean isGeometricallySuitableLand(ServerWorld world, BlockPos pos, Chocobo chocobo) {
            // Check 1: Position itself should not be water (fluid)
            FluidState fluidAtPos = world.getFluidState(pos);
            if (fluidAtPos.isIn(FluidTags.WATER)) { return false; }

            // Check 2: Block below should be solid enough to stand on.
            BlockPos belowPos = pos.down();
            BlockState belowState = world.getBlockState(belowPos);
            // Material solid check is a good start. PathNodeType can give more info.
            if (!belowState.hasSolidTopSurface(chocobo.getWorld(), belowPos, chocobo) && chocobo.getNavigation().getNodeMaker().getDefaultNodeType(world, belowPos.getX(), belowPos.getY(), belowPos.getZ()) == PathNodeType.BLOCKED)
            { return false; }
            // If the block below is water, then 'pos' is not truly "on land" relative to getting out of water.
            if (world.getFluidState(belowPos).isIn(FluidTags.WATER)) { return false; }

            // Check 3: The node type at the target position must be land-based and safe
            PathNodeType nodeType = chocobo.getNavigation().getNodeMaker().getDefaultNodeType(world, pos.getX(), pos.getY(), pos.getZ());
            if (nodeType == PathNodeType.WATER || nodeType == PathNodeType.BLOCKED) {
                // If the node is water or blocked, we cannot use this position
                return false;
            }
            if ((nodeType == PathNodeType.LAVA || nodeType == PathNodeType.DAMAGE_FIRE || nodeType == PathNodeType.DANGER_FIRE) && !chocobo.isFireImmune())
            { return false; }

            // Ensure there's space for the chocobo (e.g. not inside a solid block)
            // Check path-ability of current block and block above
            if (!world.getBlockState(pos).canPathfindThrough(world, pos, LAND) ||
                !world.getBlockState(pos.up()).canPathfindThrough(world, pos.up(), LAND))
            { return false; }
            // All checks passed, this is a geometrically suitable land position (path not checked here)
            return true;
        }
    }

    public static class ChocoboNoPenaltyTargeting extends NoPenaltyTargeting {
        public static Vec3d find(Chocobo chocobo, int horizontalRange, int verticalRange) {
            boolean bl = NavigationConditions.isPositionTargetInRange(chocobo, horizontalRange);

            // Default behavior for normal terrain
            return FuzzyPositions.guessBestPathTarget(chocobo, () -> {
                BlockPos blockPos = FuzzyPositions.localFuzz(chocobo.getRandom(), horizontalRange, verticalRange);
                return ChocoboNoPenaltyTargeting.tryMake(chocobo, horizontalRange, bl, blockPos);
            });
        }
        @Nullable
        protected static BlockPos tryMake(Chocobo entity, int horizontalRange, boolean posTargetInRange, BlockPos fuzz) {
            BlockPos blockPos = FuzzyPositions.towardTarget(entity, horizontalRange, entity.getRandom(), fuzz);
            if (ChocoboNavigationConditions.isHeightInvalid(blockPos, entity)
                    || ChocoboNavigationConditions.isPositionTargetOutOfWalkRange(posTargetInRange, entity, blockPos)
                    || ChocoboNavigationConditions.isInvalidPosition(blockPos, entity)
                    || ChocoboNavigationConditions.hasPathfindingPenalty(entity, blockPos)) { return null; }
            return blockPos;
        }
    }

    public static class ChocoboNavigationConditions extends NavigationConditions {
        public static boolean isHeightInvalid(BlockPos pos, PathAwareEntity entity) {
            return pos.getY() < entity.getWorld().getBottomY() || pos.getY() > entity.getWorld().getTopY();
        }
        public static boolean isPositionTargetOutOfWalkRange(boolean posTargetInRange, PathAwareEntity entity, BlockPos pos) {
            return posTargetInRange && !entity.isInWalkTargetRange(pos);
        }
        public static boolean isInvalidPosition(BlockPos pos, Chocobo chocobo) {
            return !ChocoboNavigationConditions.isValidPosition(pos, chocobo);
        }
        public static boolean hasPathfindingPenalty(PathAwareEntity entity, BlockPos pos) {
            return entity.getPathfindingPenalty(entity.getNavigation().getNodeMaker().getDefaultNodeType(entity.getWorld(), pos.getX(), pos.getY(), pos.getZ())) > 0.0f;
        }
        public static boolean isValidPosition(BlockPos pos, Chocobo chocobo) {
            BlockPos blockPos = pos.down();
            BlockState blockState = chocobo.getWorld().getBlockState(blockPos);

            // Check for flat surface - including slabs
            boolean isValidSurface = blockState.hasSolidTopSurface(chocobo.getWorld(), blockPos, chocobo);

            return isValidSurface && chocobo.getWorld().isSpaceEmpty(chocobo, chocobo.getBoundingBox().offset(Vec3d.of(pos.subtract(chocobo.getBlockPos()))));
        }
    }

    public static class ChocoboSwimMoveControl extends MoveControl {
        private final AbstractChocobo chocobo;

        public ChocoboSwimMoveControl(AbstractChocobo chocobo) {
            super(chocobo);
            this.chocobo = chocobo;
        }

        @Override
        public void tick() {
            if (this.chocobo.isWaterBreathing() && this.chocobo.isTouchingWater()) {
                if (this.state == MoveControl.State.MOVE_TO) {
                    Vec3d targetPos = new Vec3d(this.targetX, this.targetY, this.targetZ);
                    Vec3d chocoboPos = this.chocobo.getPos();
                    Vec3d directionToTarget = targetPos.subtract(chocoboPos);
                    double distanceToTargetSq = directionToTarget.lengthSquared();

                    if (distanceToTargetSq < 0.01D) { // Arrived at target
                        this.state = MoveControl.State.WAIT;
                        // this.chocobo.setMovementSpeed(0.0F);
                        this.chocobo.setVelocity(this.chocobo.getVelocity().multiply(1.0, 0.5, 1.0)); // Dampen Y
                        return;
                    }

                    double angleToTargetHorizontal = MathHelper.atan2(directionToTarget.z, directionToTarget.x);
                    this.chocobo.setYaw(this.wrapDegrees(this.chocobo.getYaw(), (float) (angleToTargetHorizontal * 57.2957763671875D) - 90.0F, 90.0F));
                    this.chocobo.bodyYaw = this.chocobo.getYaw();

                    float currentSpeedSetting = (float) (this.speed * this.chocobo.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                    // this.chocobo.setMovementSpeed(currentSpeedSetting); // For forward movement in MobEntity.travel

                    // Vertical movement adjustment
                    double dy = directionToTarget.y;
                    if (Math.abs(dy) > 0.02D) { // Only apply if there's a meaningful vertical distance
                        // Adjust Y velocity to move towards targetY
                        // Factor determines how quickly it adjusts vertically.
                        double verticalAdjustFactor = 0.15D; // Tunable
                        double yVelocityChange = MathHelper.clamp(dy * verticalAdjustFactor, -currentSpeedSetting * 0.75D, currentSpeedSetting * 0.75D);
                        this.chocobo.setVelocity(this.chocobo.getVelocity().add(0.0D, yVelocityChange, 0.0D));
                    }
                    // Horizontal movement is primarily driven by MobEntity.travel using the forward speed set above.
                    // The Y velocity added here will be incorporated when MobEntity.travel calls this.entity.move().
                } else { // State is WAIT or other
                    this.chocobo.setMovementSpeed(0.0F);
                }
            } else {
                // Not a swimmer or not in water, fall back to default behavior
                super.tick();
            }
        }
    }
}
