package com.dephoegon.delchoco.common.entities.subTypes;

import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.common.entities.ArmorStandChocoboPose;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static net.minecraft.entity.EquipmentSlot.*;
import static net.minecraft.sound.SoundCategory.BLOCKS;

public class ArmorStandChocobo extends Chocobo {
    public ArmorStandChocobo(EntityType<? extends Chocobo> entityType, World world) {
        super(entityType, world);
        setChocobo(ChocoboColor.ARMOR);
        // Initialize pose after parent constructor
        this.chocoboModelPose = new ArmorStandChocoboPose(this);
        if (this.breedingAgeOverride == initBreed){ this.setBreedingAgeOverride(this.getBreedingAge()); } // Set breeding age override to match breeding age
    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, (ChocoboConfig.DEFAULT_SPEED.get() * 0.75) / 100f) // Slower for golem feel, but not too slow with gear
                .add(EntityAttributes.GENERIC_MAX_HEALTH, ChocoboConfig.DEFAULT_HEALTH.get() * 2.0) // 2x health base (gear will add more)
                .add(EntityAttributes.GENERIC_ARMOR, ChocoboConfig.DEFAULT_ARMOR.get() + 4) // +4 base armor (gear can add significant amounts)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, ChocoboConfig.DEFAULT_ARMOR_TOUGHNESS.get() + 2) // +2 base toughness (gear provides more)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, ChocoboConfig.DEFAULT_ATTACK_SPEED.get() * 0.8) // Slightly slower base attacks
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, ChocoboConfig.DEFAULT_ATTACK_DAMAGE.get() * 1.2) // Modest damage increase (weapons provide main damage)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6) // 60% base knockback resistance (armor can add more)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, floor(EntityAttributes.GENERIC_FOLLOW_RANGE.getDefaultValue() * 1.1)); // Minimal follow range increase
    }
    private static final String NBT_KEY_LIVING = "Living";

    // Data trackers for equipment and properties
    private static final TrackedData<String> POSE_TYPE = DataTracker.registerData(ArmorStandChocobo.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> ALIVE = DataTracker.registerData(ArmorStandChocobo.class, TrackedDataHandlerRegistry.BOOLEAN);

    // Pose data storage - initialize to null, will be set in constructor
    private ArmorStandChocoboPose chocoboModelPose;

    // Available pose types for the chocobo model
    public enum ChocoboModelPose {
        DEFAULT("default"),
        SITTING("sitting"),
        FLYING("flying"),
        SLEEPING("sleeping");

        private final String id;

        ChocoboModelPose(String id) { this.id = id; }

        public String getId() { return id; }

        public static ChocoboModelPose fromId(String id) {
            for (ChocoboModelPose pose : values()) {
                if (pose.id.equals(id)) { return pose; }
            }
            return DEFAULT;
        }
    }

    // Break attempt tracking
    private int breakAttempts = 0;
    private static final int REQUIRED_BREAK_ATTEMPTS = 5; // Requires 5 consistent break attempts
    private long lastBreakAttempt = 0;
    private static final long BREAK_TIMEOUT = 3000; // 3-second timeout between
    private static final int initBreed = 99999999; // attempts
    private int breedingAgeOverride = initBreed;

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(POSE_TYPE, ChocoboModelPose.DEFAULT.getId());
        this.dataTracker.startTracking(ALIVE, false); // Track if the armor stand is alive
        super.initDataTracker();
    }
    public boolean isBaby() { return this.breedingAgeOverride < 0; }

    // Pose management
    public EntityPose getPose() { return getDefaultPose(); }
    public ArmorStandChocoboPose getChocoboModelPose() {
         if (this.chocoboModelPose == null) { this.chocoboModelPose = new ArmorStandChocoboPose(this); }
         return this.chocoboModelPose;
    }
    public static ArmorStandChocoboPose getChocoboModelPose(ArmorStandChocobo entity) {
        return entity.chocoboModelPose;
    }
    public void setChocoboArmorPose(ArmorStandChocoboPose pose) {
        this.chocoboModelPose = pose;
        this.dataTracker.set(POSE_TYPE, pose.getType().getId());
    }
    public ChocoboModelPose getPoseType() { return ChocoboModelPose.fromId(this.dataTracker.get(POSE_TYPE)); }
    public void setPoseType(ChocoboModelPose type) { this.dataTracker.set(POSE_TYPE, type.getId()); }

    private boolean handleBreakAttempt(boolean isPlayerCreative) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastBreakAttempt > BREAK_TIMEOUT) { breakAttempts = 0; }

        breakAttempts++;
        lastBreakAttempt = currentTime;

        boolean breakSuccess = breakAttempts >= REQUIRED_BREAK_ATTEMPTS;

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_STONE_HIT, BLOCKS, 0.5F, 1.0F);

        if (isPlayerCreative || breakSuccess) {
            this.inventoryDropClear(this.chocoboInventory, this, false);
            this.inventoryDropClear(this.chocoboGearInventory, this, true);
            // TODO: Drop ItemStack for armor stand
            this.dropItem(ModItems.CHOCOBO_ARMOR_STAND_SPAWN_EGG);
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
        // Ensure the actual breedingAge field matches the override for proper isBaby() behavior
        this.breedingAge = this.breedingAgeOverride;

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
        this.setArmorAlive(!disabled);
        super.setAiDisabled(disabled);
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
    public boolean canHaveStatusEffect(@NotNull StatusEffectInstance effect) {
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
        // Ensure breeding age stays synchronized every tick for consistent rendering
        if (this.breedingAge != this.breedingAgeOverride) { this.breedingAge = this.breedingAgeOverride; }
    }

    // Override entity properties to ensure correct behavior for an armor stand
    public boolean canHit() { return super.canHit(); }
    public boolean isPushable() { return false; }
    public boolean isPushedByFluids() { return false; }
    public boolean damage(DamageSource source, float amount) {
        if (this.isArmorStandNotAlive()) {
            if (source.isOf(net.minecraft.entity.damage.DamageTypes.OUT_OF_WORLD)) {
                this.kill();
                return false;
            }
            if (source.getAttacker() instanceof PlayerEntity) {
                float damageAmount = handleBreakAttempt(((PlayerEntity) source.getAttacker()).isCreative()) ? 0 : amount;
                return super.damage(source, damageAmount);
            }
            return false;
        }
        return super.damage(source, amount);
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
        if (age >= 0 && abs(age) < 1500) { age = 1500; } // Ensure adult breeding age is within valid range
        this.breedingAgeOverride = age;
        if (callUpdate) { this.breedingAge = age; }
    }
    public void setBreedingAgeOverride(int age) {
        this.breedingAgeOverride = age; // Update override to match breeding age
        this.breedingAge = age; // Update breeding age to match override
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

    // Enhanced gear bonus system for ArmorStandChocobo - Additional bonuses on top of base bonuses
    private static final float ADDITIONAL_ARMOR_BONUS = 0.15f; // +15% additional armor value from equipment
    private static final float ADDITIONAL_TOUGHNESS_BONUS = 0.15f; // +15% additional toughness from equipment
    private static final float ADDITIONAL_KNOCKBACK_BONUS = 0.15f; // +15% additional knockback resistance from equipment
    private static final float ADDITIONAL_WEAPON_DAMAGE_BONUS = 0.15f; // +15% additional damage from weapons
    private static final float ADDITIONAL_WEAPON_SPEED_BONUS = 0.15f; // +15% additional attack speed from weapons

    /**
     * Override chest armor stat application to provide enhanced bonuses
     */
    @Override
    public void setChocoboChestArmorStats(ItemStack pStack) {
        if (!this.getWorld().isClient()) {
            // Remove all existing modifiers first (same as parent)
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).removeModifier(CHOCOBO_CHEST_ARMOR_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).removeModifier(CHOCOBO_CHEST_ARMOR_TOUGH_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).removeModifier(CHOCOBO_CHEST_ARMOR_KNOCKBACK_MOD_UUID);

            if (pStack.getItem() instanceof ChocoboArmorItems armorItem) {
                this.setEquipmentDropChance(CHEST, 0.0F);
                ArmorMaterial material = armorItem.getMaterial();

                // Apply enhanced bonuses using same UUIDs as parent but with increased values
                double baseArmorValue = (double) ChocoboArmorItems.getTotalDefense(material, armorItem.getType()) * chocoStatMod();
                double enhancedArmorValue = baseArmorValue * (1.0 + ADDITIONAL_ARMOR_BONUS);
                if (enhancedArmorValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_CHEST_ARMOR_MOD_UUID,
                                    "Chocobo Armor Bonus",
                                    enhancedArmorValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }

                double baseToughnessValue = ChocoboArmorItems.getTotalToughness(material) * chocoStatMod();
                double enhancedToughnessValue = baseToughnessValue * (1.0 + ADDITIONAL_TOUGHNESS_BONUS);
                if (enhancedToughnessValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_CHEST_ARMOR_TOUGH_MOD_UUID,
                                    "Chocobo Armor Toughness",
                                    enhancedToughnessValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }

                double baseKnockbackValue = ChocoboArmorItems.getTotalKnockbackResistance(material) * chocoStatMod();
                double enhancedKnockbackValue = baseKnockbackValue * (1.0 + ADDITIONAL_KNOCKBACK_BONUS);
                if (enhancedKnockbackValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_CHEST_ARMOR_KNOCKBACK_MOD_UUID,
                                    "Chocobo Armor Knockback Resistance",
                                    enhancedKnockbackValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }
            }
            this.silentUpdateArmorSetBonus(CHEST, pStack);
        }
    }

    /**
     * Override head armor stat application to provide enhanced bonuses
     */
    @Override
    public void setChocoboHeadArmorStats(ItemStack pStack) {
        if (!this.getWorld().isClient()) {
            // Remove all existing modifiers first (same as parent)
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).removeModifier(CHOCOBO_HEAD_ARMOR_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).removeModifier(CHOCOBO_HEAD_ARMOR_TOUGH_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).removeModifier(CHOCOBO_HEAD_ARMOR_KNOCKBACK_MOD_UUID);

            if (pStack.getItem() instanceof ChocoboArmorItems armorItem) {
                this.setEquipmentDropChance(HEAD, 0.0F);
                ArmorMaterial material = armorItem.getMaterial();

                // Apply enhanced bonuses using same UUIDs as parent but with increased values
                double baseArmorValue = (double) ChocoboArmorItems.getTotalDefense(material, armorItem.getType()) * chocoStatMod();
                double enhancedArmorValue = baseArmorValue * (1.0 + ADDITIONAL_ARMOR_BONUS);
                if (enhancedArmorValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_HEAD_ARMOR_MOD_UUID,
                                    "Chocobo Head Armor Bonus",
                                    enhancedArmorValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }

                double baseToughnessValue = ChocoboArmorItems.getTotalToughness(material) * chocoStatMod();
                double enhancedToughnessValue = baseToughnessValue * (1.0 + ADDITIONAL_TOUGHNESS_BONUS);
                if (enhancedToughnessValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_HEAD_ARMOR_TOUGH_MOD_UUID,
                                    "Chocobo Head Armor Toughness",
                                    enhancedToughnessValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }

                double baseKnockbackValue = ChocoboArmorItems.getTotalKnockbackResistance(material) * chocoStatMod();
                double enhancedKnockbackValue = baseKnockbackValue * (1.0 + ADDITIONAL_KNOCKBACK_BONUS);
                if (enhancedKnockbackValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_HEAD_ARMOR_KNOCKBACK_MOD_UUID,
                                    "Chocobo Head Armor Knockback Resistance",
                                    enhancedKnockbackValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }
            }
            this.silentUpdateArmorSetBonus(HEAD, pStack);
        }
    }

    /**
     * Override legs armor stat application to provide enhanced bonuses
     */
    @Override
    public void setChocoboLegsArmorStats(ItemStack pStack) {
        if (!this.getWorld().isClient()) {
            // Remove all existing modifiers first (same as parent)
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).removeModifier(CHOCOBO_LEGS_ARMOR_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).removeModifier(CHOCOBO_LEGS_ARMOR_TOUGH_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).removeModifier(CHOCOBO_LEGS_ARMOR_KNOCKBACK_MOD_UUID);

            if (pStack.getItem() instanceof ChocoboArmorItems armorItem) {
                this.setEquipmentDropChance(LEGS, 0.0F);
                ArmorMaterial material = armorItem.getMaterial();

                // Apply enhanced bonuses using same UUIDs as parent but with increased values
                double baseArmorValue = (double) ChocoboArmorItems.getTotalDefense(material, armorItem.getType()) * chocoStatMod();
                double enhancedArmorValue = baseArmorValue * (1.0 + ADDITIONAL_ARMOR_BONUS);
                if (enhancedArmorValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_LEGS_ARMOR_MOD_UUID,
                                    "Chocobo Legs Armor Bonus",
                                    enhancedArmorValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }

                double baseToughnessValue = ChocoboArmorItems.getTotalToughness(material) * chocoStatMod();
                double enhancedToughnessValue = baseToughnessValue * (1.0 + ADDITIONAL_TOUGHNESS_BONUS);
                if (enhancedToughnessValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_LEGS_ARMOR_TOUGH_MOD_UUID,
                                    "Chocobo Legs Armor Toughness",
                                    enhancedToughnessValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }

                double baseKnockbackValue = ChocoboArmorItems.getTotalKnockbackResistance(material) * chocoStatMod();
                double enhancedKnockbackValue = baseKnockbackValue * (1.0 + ADDITIONAL_KNOCKBACK_BONUS);
                if (enhancedKnockbackValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_LEGS_ARMOR_KNOCKBACK_MOD_UUID,
                                    "Chocobo Legs Armor Knockback Resistance",
                                    enhancedKnockbackValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }
            }
            this.silentUpdateArmorSetBonus(LEGS, pStack);
        }
    }

    /**
     * Override feet armor stat application to provide enhanced bonuses
     */
    @Override
    public void setChocoboFeetArmorStats(ItemStack pStack) {
        if (!this.getWorld().isClient()) {
            // Remove all existing modifiers first (same as parent)
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).removeModifier(CHOCOBO_FEET_ARMOR_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).removeModifier(CHOCOBO_FEET_ARMOR_TOUGH_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).removeModifier(CHOCOBO_FEET_ARMOR_KNOCKBACK_MOD_UUID);

            if (pStack.getItem() instanceof ChocoboArmorItems armorItem) {
                this.setEquipmentDropChance(FEET, 0.0F);
                ArmorMaterial material = armorItem.getMaterial();

                // Apply enhanced bonuses using same UUIDs as parent but with increased values
                double baseArmorValue = (double) ChocoboArmorItems.getTotalDefense(material, armorItem.getType()) * chocoStatMod();
                double enhancedArmorValue = baseArmorValue * (1.0 + ADDITIONAL_ARMOR_BONUS);
                if (enhancedArmorValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_FEET_ARMOR_MOD_UUID,
                                    "Chocobo Feet Armor Bonus",
                                    enhancedArmorValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }

                double baseToughnessValue = ChocoboArmorItems.getTotalToughness(material) * chocoStatMod();
                double enhancedToughnessValue = baseToughnessValue * (1.0 + ADDITIONAL_TOUGHNESS_BONUS);
                if (enhancedToughnessValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_FEET_ARMOR_TOUGH_MOD_UUID,
                                    "Chocobo Feet Armor Toughness",
                                    enhancedToughnessValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }

                double baseKnockbackValue = ChocoboArmorItems.getTotalKnockbackResistance(material) * chocoStatMod();
                double enhancedKnockbackValue = baseKnockbackValue * (1.0 + ADDITIONAL_KNOCKBACK_BONUS);
                if (enhancedKnockbackValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_FEET_ARMOR_KNOCKBACK_MOD_UUID,
                                    "Chocobo Feet Armor Knockback Resistance",
                                    enhancedKnockbackValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }
            }
            this.silentUpdateArmorSetBonus(FEET, pStack);
        }
    }

    /**
     * Override weapon stat application to provide enhanced bonuses
     */
    @Override
    public void setChocoboWeaponStats(ItemStack pStack) {
        if (!this.getWorld().isClient()) {
            // Remove all existing modifiers first (same as parent)
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).removeModifier(CHOCOBO_WEAPON_DAM_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED)).removeModifier(CHOCOBO_WEAPON_SPD_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).removeModifier(CHOCOBO_WEAPON_KNOCKBACK_MOD_UUID);

            if (pStack.getItem() instanceof ChocoboWeaponItems weaponItem) {
                ToolMaterial material = weaponItem.getMaterial();

                // Apply enhanced bonuses using same UUIDs as parent but with increased values
                double baseDamageValue = (double) ChocoboWeaponItems.getTotalAttackDamage(material) * chocoStatMod();
                double enhancedDamageValue = baseDamageValue * (1.0 + ADDITIONAL_WEAPON_DAMAGE_BONUS);
                if (enhancedDamageValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_WEAPON_DAM_MOD_UUID,
                                    "Chocobo Attack Bonus",
                                    enhancedDamageValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }

                double baseSpeedValue = weaponItem.getAttackSpeed() * chocoStatMod();
                double enhancedSpeedValue = baseSpeedValue * (1.0 + ADDITIONAL_WEAPON_SPEED_BONUS);
                if (enhancedSpeedValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
                            .addPersistentModifier(new EntityAttributeModifier(
                                    CHOCOBO_WEAPON_SPD_MOD_UUID,
                                    "Chocobo Attack Speed Bonus",
                                    enhancedSpeedValue,
                                    EntityAttributeModifier.Operation.ADDITION));
                }
            }
            super.silentUpdateArmorSetBonus(EquipmentSlot.MAINHAND, pStack);
        }
    }
}