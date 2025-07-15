package com.dephoegon.delchoco.common.entities.subTypes;

import com.dephoegon.delchoco.common.entities.ArmorStandChocoboPose;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.abs;
import static net.minecraft.sound.SoundCategory.BLOCKS;

public class ArmorStandChocobo extends Chocobo {
    public ArmorStandChocobo(EntityType<? extends Chocobo> entityType, World world) {
        super(entityType, world);
    }
    private static final String NBT_KEY_LIVING = "Living";

    // Data trackers for equipment and properties
    private static final TrackedData<String> POSE_TYPE = DataTracker.registerData(ArmorStandChocobo.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> ALIVE = DataTracker.registerData(ArmorStandChocobo.class, TrackedDataHandlerRegistry.BOOLEAN);

    // Pose data storage
    private ArmorStandChocoboPose chocoboModelPose = new ArmorStandChocoboPose(this);

    // Available pose types for the chocobo model
    public enum ChocoboModelPose {
        DEFAULT("default"),
        SITTING("sitting"),
        FLYING("flying"),
        SLEEPING("sleeping");

        private final String id;

        ChocoboModelPose(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static ChocoboModelPose fromId(String id) {
            for (ChocoboModelPose pose : values()) {
                if (pose.id.equals(id)) {
                    return pose;
                }
            }
            return DEFAULT;
        }
    }

    // Break attempt tracking
    private int breakAttempts = 0;
    private static final int REQUIRED_BREAK_ATTEMPTS = 5; // Requires 5 consistent break attempts
    private long lastBreakAttempt = 0;
    private static final long BREAK_TIMEOUT = 3000; // 3-second timeout between attempts
    private int breedingAgeOverride = 0;

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(POSE_TYPE, ChocoboModelPose.DEFAULT.getId());
        this.dataTracker.startTracking(ALIVE, false); // Track if the armor stand is alive
        super.initDataTracker();
    }


    // Pose management
    public EntityPose getPose() { return getDefaultPose(); }
    public ArmorStandChocoboPose getChocoboModelPose() { return this.chocoboModelPose; }
    public static ArmorStandChocoboPose getChocoboModelPose(ArmorStandChocobo entity) {
        return entity.chocoboModelPose;
    }
    public void setChocoboArmorPose(ArmorStandChocoboPose pose) {
        this.chocoboModelPose = pose;
        this.dataTracker.set(POSE_TYPE, pose.getType().getId());
    }
    public ChocoboModelPose getPoseType() { return ChocoboModelPose.fromId(this.dataTracker.get(POSE_TYPE)); }
    public void setPoseType(ChocoboModelPose type) { this.dataTracker.set(POSE_TYPE, type.getId()); }

    private boolean handleBreakAttempt(PlayerEntity player) {
        if (player.isCreative()) { return true; }
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastBreakAttempt > BREAK_TIMEOUT) { breakAttempts = 0; }

        breakAttempts++;
        lastBreakAttempt = currentTime;

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_STONE_HIT, BLOCKS, 0.5F, 1.0F);

        if (breakAttempts >= REQUIRED_BREAK_ATTEMPTS) {
            this.inventoryDropClear(this.chocoboInventory, this, false);
            this.inventoryDropClear(this.chocoboGearInventory, this, true);
            // TODO: Drop ItemStack for armor stand
            this.discard();
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_STONE_BREAK, BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }


    public void readCustomDataFromNbt(@NotNull NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setAiDisabled(!(nbt.getBoolean(NBT_KEY_LIVING))); // Disable AI for armor stand
        if (this.isArmorStandAlive()) { this.setChocobo(ChocoboColor.ARMOR); }
        this.breedingAgeOverride = nbt.getInt("Age");

        if (this.isArmorStandNotAlive()) { ArmorStandChocoboPose.readFromNbt(nbt, this); }

    }
    public void writeCustomDataToNbt(@NotNull NbtCompound nbt) {
        if (!this.isAiDisabled()) { this.setChocobo(ChocoboColor.ARMOR); } // Ensure color is set for armor stand when alive to be saved by the AbstractChocoboEntity
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean(NBT_KEY_LIVING, !this.isAiDisabled()); // Store AI state
        nbt.putInt("Age", this.breedingAgeOverride); // Store breeding age override


        if (this.isArmorStandNotAlive()) { ArmorStandChocoboPose.writeToNbt(nbt, this); }

    }
    /**
     * Set whether the armor stand AI is disabled
     * @param disabled true to disable AI, false to enable
     */
    public void setAiDisabled(boolean disabled) {
        super.setAiDisabled(disabled);
        this.setArmorAlive(!disabled); // Set armor stand alive state based on AI status
    }
    public boolean isAiDisabled() {
        if (super.isAiDisabled() == this.isArmorAlive()) { setAiDisabled(!this.isAlive()); }
        return super.isAiDisabled();
    }
    private void setArmorAlive(boolean alive) {
        this.dataTracker.set(ALIVE, alive); // This entity is always an armor stand
    }
    private boolean isArmorAlive() {
        return this.dataTracker.get(ALIVE);
    }
    public boolean isAlive() {
        if (this.isArmorAlive()) { return super.isAlive(); }
        return true; // Armor stands are always considered alive in this context
    }
    public boolean isDead() {
        if (this.isArmorAlive()) { return super.isDead(); }
        return false; // Armor stands are never dead in this context
    }
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        if (this.isArmorAlive()) { return super.canHaveStatusEffect(effect); }
        return false; // Armor stands cannot have status effects
    }
    public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
        if (this.isArmorAlive()) { return super.addStatusEffect(effect, source); }
        return false; // Armor stands cannot have status effects
    }
    public boolean isTarget(LivingEntity entity, TargetPredicate predicate) {
        if (this.isArmorAlive()) { return super.isTarget(entity, predicate); }
        return false; // Armor stands cannot be targeted
    }

    /**
     * Maps the ChocoboModelPose to the vanilla EntityPose for rendering
     */
    public EntityPose getDefaultPose() {
        return switch (getPoseType()) {
            case SITTING -> EntityPose.SITTING;
            case FLYING -> EntityPose.SWIMMING; // Use swimming pose for flying
            case SLEEPING -> EntityPose.SLEEPING;
            default -> EntityPose.STANDING;
        };
    }

    public void tick() {
        super.tick();
        if (this.isArmorStandNotAlive()) {
            Vec3d velocity = this.getVelocity();
            this.setVelocity(new Vec3d(0, velocity.y, 0));

            ChocoboModelPose currentPoseType = getPoseType();
            if (this.chocoboModelPose.getType() != currentPoseType) {
                ArmorStandChocoboPose.setPoseByType(currentPoseType, this);
            }
            if (!this.getWorld().isClient()) {
                this.chocoboModelPose.applyStaticPoseRotation(this);
            }
        }
        if (this.age % 1000 == 0) { this.setBreedingAge(this.breedingAgeOverride); }
    }

    // Override entity properties to ensure correct behavior for an armor stand
    public boolean canHit() { return true; }
    public boolean isPushable() { return false; }
    public boolean isPushedByFluids() { return false; }
    public boolean damage(DamageSource source, float amount) {
        if (this.isArmorAlive()) { return super.damage(source, amount); } // Prevent damage if armor stand is not alive
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.GENERIC_KILL)) { return false; }
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.OUT_OF_WORLD)) { this.kill(); return false; }
        if (source.getAttacker() instanceof PlayerEntity player) { return handleBreakAttempt(player); }
        return false;
    }
    protected void dropLoot(@NotNull DamageSource source, boolean causedByPlayer) {
        if (source.getAttacker() instanceof ServerPlayerEntity) { return; } // handled in 'damage' method
        if (damage(source, 1F)) { super.dropLoot(source, causedByPlayer); }
        return; // Prevent default drop behavior
    }
    protected void knockback(LivingEntity entity) {
        if (this.isArmorAlive()) { super.knockback(entity); } // Prevent knockback if armor stand is not alive
    }
    protected void pushOutOfBlocks(double x, double y, double z) { } // Override to prevent suffocation pushout
    public boolean isPersistent() {
        if (this.isArmorAlive()) { return super.isPersistent(); } // Prevent despawn check if armor stand is not alive
        return true; // Armor stands should not despawn
    }
    public boolean canUsePortals() { return this.isArmorAlive() && super.canUsePortals();  }
    public boolean canBreedWith(@NotNull AnimalEntity entity) { return false; }
    public boolean followLure() {
        if (this.isArmorAlive()) { return super.followLure(); }
        return false; // Armor stands do not follow lures
    }

    // prevents drowning effects, but bypasses water breathing effects for the rider
    public boolean canBreatheInWater() { return true; }
    public boolean isWaterBreathing() { return false; }
    public boolean isInvisible() { return false; } // Armor stands are always visible
    /**
     * Override breeding age methods to use the breedingAgeOverride
     * This allows setting a custom breeding age for the armor stand
     * @param callUpdate used to update the breeding age immediately, false when called from setBreedingAge
     */
    public void setBreedingAgeOverride(int age, boolean callUpdate) {
        if ((age < 0 && this.breedingAgeOverride >= 0) || (age >= 0 && this.breedingAgeOverride < 0)) { age = -age; } // Convert age to prevent changing adult to a baby or vice versa
        if (abs(age) < 1500) { age = age < 0 ? -1500 : 1500; } // Ensure breeding age is within valid range outside buffer size for delaying tick updates on server checks 
        this.breedingAgeOverride = age;
        if (callUpdate) { this.breedingAge = age; }
    }
    public void setBreedingAge(int age) {
        setBreedingAgeOverride(age, false); // false to prevent recursive updates
        this.breedingAge = age; // Update override to match breeding age
    }
    public boolean isArmorStandNotAlive() { return !this.isArmorAlive(); }
    public boolean isArmorStandAlive() { return this.isArmorAlive(); }
    public boolean isArmorStand() { return true; }
    public boolean isNotArmorStand() { return false; }
    public boolean isValidChocobo() { return this.isArmorStandAlive() && super.isValidChocobo(); }
    public void growUp(int age) { }
}