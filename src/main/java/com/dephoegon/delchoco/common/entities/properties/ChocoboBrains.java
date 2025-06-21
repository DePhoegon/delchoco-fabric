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
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookAtMobTask;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.dephoegon.delchoco.common.entities.properties.ChocoboBrainAid.ChocoboTemptItems;
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

    public static Brain<?> makeBrain(Brain<Chocobo> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        addTemptActivities(brain);
        addPanicActivities(brain);
        addAvoidPlayerActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<Chocobo> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(
                new TryFindLandTask(1.1f),
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

    private static void addTemptActivities(Brain<Chocobo> brain) {
        brain.setTaskList(Activity.IDLE, 5, ImmutableList.of(
                new ChocoboTemptTask() // Higher priority than roaming (10) but lower than panic
        ));
    }

    public static class RoamTask extends MultiTickTask<Chocobo> {
        private static final int MAX_UPDATE_COUNTDOWN = 40;
        private static final int PATH_CACHE_TICKS = 100; // Cache paths for longer periods
        private int pathUpdateCountdownTicks = 0;
        private int pathCacheExpiryTicks = 0;
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

            // Don't recalculate the path if we already have a cached valid path
            if (this.path != null && this.pathCacheExpiryTicks > 0) {
                this.pathCacheExpiryTicks--;
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

                // Only compute a new path if we don't have a valid cached one
                if (this.path == null || this.pathCacheExpiryTicks <= 0 || !this.path.reachesTarget()) {
                    this.path = chocobo.getNavigation().findPathTo(targetPos, 0);
                    if (this.path != null) {
                        this.pathCacheExpiryTicks = PATH_CACHE_TICKS;
                        brain.remember(MemoryModuleType.PATH, this.path);
                        chocobo.getNavigation().startMovingAlong(this.path, this.speed);
                    }
                } else {
                    // Use the cached path
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
            //noinspection OptionalGetWithoutIsPresent
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

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private boolean hasReached(Chocobo chocobo, WalkTarget walkTarget) {
            if (walkTarget == null || walkTarget.getLookTarget() == null) { return true; }
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
                // Panic if there is an attacker, and it is NOT attackable. To include non-living entities (Mod Coverage)
                return !ChocoboBrainAid.isAttackable(attacker, chocobo.canWalkOnWater());
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
                    // No path found to this specific candidate, or a path doesn't reach. Teleport.
                    // isGeometricallySuitableLand should have ensured landPosCandidate is a safe spot.
                    chocobo.refreshPositionAndAngles(landPosCandidate.getX() + 0.5, landPosCandidate.getY(), landPosCandidate.getZ() + 0.5, chocobo.getYaw(), chocobo.getPitch());
                    chocobo.getNavigation().stop(); // Stop any current pathing after teleport
                }
            }
            // If no landPosCandidate found by findGeometricallySuitableLandPos, this task does nothing further.
            // RoamTask might pick up if its conditions are met.
        }

        @Nullable
        private BlockPos findGeometricallySuitableLandPos(Chocobo chocobo, ServerWorld world, int horizontalRange, @SuppressWarnings("SameParameterValue") int verticalRange) {
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

            // Ensure there's space for the chocobo (e.g., not inside a solid block)
            // Check path-ability of current block and block above
            return world.getBlockState(pos).canPathfindThrough(world, pos, LAND) &&
                    world.getBlockState(pos.up()).canPathfindThrough(world, pos.up(), LAND);
            // All checks passed, this is a geometrically suitable land position (path not checked here)
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
        private int bounceCounter;

        public ChocoboSwimMoveControl(AbstractChocobo chocobo) {
            super(chocobo);
            this.chocobo = chocobo;
        }

        @Override
        public void tick() {
            if (this.chocobo.isWaterBreathing() && this.chocobo.isSubmergedInWater()) {
                if (this.state == MoveControl.State.MOVE_TO) {
                    Vec3d vec3d = new Vec3d(this.targetX - this.chocobo.getX(), this.targetY - this.chocobo.getY(), this.targetZ - this.chocobo.getZ());
                    double d = vec3d.length();
                    if (d < 1.0E-7) {
                        this.entity.setForwardSpeed(0.0f);
                        this.state = MoveControl.State.WAIT;
                        return;
                    }

                    // Drowned-like bobbing/bouncing behavior
                    if (vec3d.y > 0 && this.chocobo.getVelocity().y < 0.1) { // Moving up
                        if (this.bounceCounter <= 0) {
                            this.chocobo.addVelocity(0, 0.3, 0); // Larger bounce
                            this.bounceCounter = 10; // Cooldown for the bounce
                        }
                    }
                    if (this.bounceCounter > 0) {
                        this.bounceCounter--;
                    }

                    float yawToTarget = (float)(MathHelper.atan2(vec3d.z, vec3d.x) * 57.2957763671875) - 90.0f;
                    this.chocobo.setYaw(this.wrapDegrees(this.chocobo.getYaw(), yawToTarget, 90.0f));
                    this.chocobo.bodyYaw = this.chocobo.getYaw();

                    float headYaw = this.chocobo.getYaw();
                    if (this.chocobo.getRandom().nextFloat() < 0.15f) {
                        headYaw += (this.chocobo.getRandom().nextFloat() - 0.5f) * 15.0f;
                    }
                    this.chocobo.headYaw = this.wrapDegrees(this.chocobo.headYaw, headYaw, 5.0f);

                    float speed = (float)(this.speed * this.chocobo.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));

                    float pitchToTarget = -((float)(MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 57.2957763671875));
                    this.chocobo.setPitch(this.wrapDegrees(this.chocobo.getPitch(), pitchToTarget, 10.0f));

                    float cosPitch = MathHelper.cos(this.chocobo.getPitch() * ((float)Math.PI / 180));
                    float sinPitch = MathHelper.sin(this.chocobo.getPitch() * ((float)Math.PI / 180));

                    this.chocobo.setForwardSpeed(cosPitch * speed);
                    this.chocobo.setUpwardSpeed(-sinPitch * speed);

                } else {
                    this.chocobo.setSidewaysSpeed(0.0f);
                    this.chocobo.setUpwardSpeed(0.0f);
                    this.chocobo.setForwardSpeed(0.0f);
                }
            } else {
                super.tick();
            }
        }
    }

    public static class ChocoboTemptTask extends MultiTickTask<Chocobo> {
        private static final double TEMPT_SPEED = 1.25;
        private static final float FOLLOW_RANGE = 10.0F;
        private static final ImmutableList<ItemStack> TEMPT_ITEMS = ChocoboTemptItems();

        private final Predicate<Chocobo> canBeTempted;
        private PlayerEntity targetPlayer;
        private int cooldown = 0;

        public ChocoboTemptTask() { this(AbstractChocobo::isTemptable); }

        public ChocoboTemptTask(Predicate<Chocobo> canBeTempted) {
            super(ImmutableMap.of(
                    MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, // Only run if no walk target
                    MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED,
                    MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT // Don't interrupt attacking
            ));
            this.canBeTempted = canBeTempted;
        }
        @Override
        protected boolean shouldRun(ServerWorld world, Chocobo chocobo) {
            if (!this.canBeTempted.test(chocobo)) { return false; }

            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }

            // Special check for tamed chocobos - they should always have an owner
            if (chocobo.isTamed() && chocobo.getOwner() == null) { chocobo.setTamed(false); }

            // Look for players with tempting items within range
            List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(
                    PlayerEntity.class,
                    chocobo.getBoundingBox().expand(FOLLOW_RANGE),
                    player -> !player.isSpectator() && !player.isCreative()
            );

            // For tamed chocobos, only their owner can tempt them
            // Return early if the chocobo is tamed (and has an owner) but the owner isn't nearby
            if (chocobo.isTamed()) {
                boolean ownerPresent = false;
                for (PlayerEntity player : nearbyPlayers) {
                    if (chocobo.isOwner(player)) {
                        ownerPresent = true;
                        break;
                    }
                }
                if (!ownerPresent) { return false; } // Owner is not present, can't be tempted
            }

            // Process players - first with tempting item wins
            for (PlayerEntity player : nearbyPlayers) {
                // Skip if tamed and this player isn't the owner
                if (chocobo.isTamed() && !chocobo.isOwner(player)) { continue; }

                // Checks the main hand and offhand for tempting items
                boolean hasTemptItem = isTemptingItem(player.getMainHandStack()) || isTemptingItem(player.getOffHandStack());

                if (hasTemptItem) {
                    this.targetPlayer = player;
                    return true;
                }
            }

            return false;
        }

        @Override
        protected void run(ServerWorld world, Chocobo chocobo, long time) {
            if (this.targetPlayer != null) {
                // Set walk target to player with tempting item
                Vec3d targetPos = this.targetPlayer.getPos();
                chocobo.getBrain().remember(
                        MemoryModuleType.WALK_TARGET,
                        new WalkTarget(targetPos, (float) TEMPT_SPEED, 2)
                );
            }
        }

        @Override
        protected boolean shouldKeepRunning(ServerWorld world, Chocobo chocobo, long time) {
            if (this.targetPlayer == null || !this.targetPlayer.isAlive() ||
                    this.targetPlayer.isSpectator() || this.targetPlayer.isCreative())
            { return false; }

            // Check if player still has tempting item
            return isTemptingItem(this.targetPlayer.getMainHandStack()) ||
                    isTemptingItem(this.targetPlayer.getOffHandStack());
        }

        @Override
        protected void finishRunning(ServerWorld world, Chocobo chocobo, long time) {
            this.targetPlayer = null;
            this.cooldown = 100; // 5-second cooldown before trying to tempt again
            chocobo.getBrain().forget(MemoryModuleType.WALK_TARGET);
        }

        private boolean isTemptingItem(ItemStack stack) {
            if (stack.isEmpty()) { return false; }

            for (ItemStack temptItem : TEMPT_ITEMS) {
                if (ItemStack.areItemsEqual(temptItem, stack)) { return true; }
            }
            return false;
        }
    }
}
