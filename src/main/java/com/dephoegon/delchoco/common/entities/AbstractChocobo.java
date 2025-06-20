package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.chocoboChecks;
import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.common.entities.pathing.ChocoboAmphibiousSwimNavigation;
import com.dephoegon.delchoco.common.entities.properties.ChocoboBrainAid;
import com.dephoegon.delchoco.common.entities.properties.ChocoboBrains;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.entities.properties.MovementType;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import com.dephoegon.delchoco.utils.RandomHelper;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.EntityView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.dephoegon.delbase.item.ShiftingDyes.*;
import static com.dephoegon.delchoco.aid.chocoboChecks.isWaterBreathingChocobo;
import static com.dephoegon.delchoco.aid.chocoboChecks.isWitherImmuneChocobo;
import static com.dephoegon.delchoco.common.entities.Chocobo.CHOCOBO_SPRINTING_SPEED_BOOST;
import static com.dephoegon.delchoco.common.entities.breeding.ChocoboSnap.setChocoScale;
import static com.dephoegon.delchoco.common.init.ModItems.*;
import static com.dephoegon.delchoco.common.init.ModSounds.AMBIENT_SOUND;
import static net.minecraft.entity.MovementType.SELF;
import static net.minecraft.item.Items.*;

public abstract class AbstractChocobo extends TameableEntity implements Angerable {
    // Chocobo Variables to be used by all Chocobo types
    @Nullable
    protected UUID persistentAngerTarget;
    protected int remainingPersistentAngerTime;
    protected int ticksUntilNextAlert;
    protected int timeToRecalculatePath;
    public float wingRotation;
    public float destinationPos;
    public boolean isChocoboJumping;
    public float wingRotDelta;
    public int ticksOnWater = 0;
    // protected BlockPos nestPos;
    public int TimeSinceFeatherChance = 0;
    protected int rideTickDelay = 0;
    public int followingMrHuman = 2;
    protected final double followSpeedModifier = 2.0D;
    protected static final float maxStepUp = 1.5f;
    protected final UniformIntProvider ALERT_INTERVAL = TimeHelper.betweenSeconds(4, 6);
    protected int fruitAteTimer = 0;
    protected static final int SPRINTING_FLAG_INDEX = 3;
    protected final Map<Item, Integer> COLLAR_COLOR = Util.make(Maps.newHashMap(), (map) ->{
        map.put(CLEANSE_SHIFT_DYE.asItem(), 0);
        map.put(RED_SHIFT_DYE.asItem(), 16);
        map.put(RED_DYE.asItem(), 16);
        map.put(BLOOD_SHIFT_DYE.asItem(), 16);
        map.put(WHITE_SHIFT_DYE.asItem(), 15);
        map.put(WHITE_DYE.asItem(), 15);
        map.put(ORANGE_SHIFT_DYE.asItem(), 14);
        map.put(ORANGE_DYE.asItem(), 14);
        map.put(MAGENTA_SHIFT_DYE.asItem(), 13);
        map.put(MAGENTA_DYE.asItem(), 13);
        map.put(LIGHT_BLUE_SHIFT_DYE.asItem(), 12);
        map.put(LIGHT_BLUE_DYE.asItem(), 12);
        map.put(YELLOW_SHIFT_DYE.asItem(), 11);
        map.put(YELLOW_DYE.asItem(), 11);
        map.put(LIME_SHIFT_DYE.asItem(), 10);
        map.put(LIME_DYE.asItem(), 10);
        map.put(PINK_SHIFT_DYE.asItem(), 9);
        map.put(PINK_DYE.asItem(), 9);
        map.put(GRAY_SHIFT_DYE.asItem(), 8);
        map.put(GRAY_DYE.asItem(), 8);
        map.put(LIGHT_GRAY_SHIFT_DYE.asItem(), 7);
        map.put(LIGHT_GRAY_DYE.asItem(), 7);
        map.put(CYAN_SHIFT_DYE.asItem(), 6);
        map.put(CYAN_DYE.asItem(), 6);
        map.put(PURPLE_SHIFT_DYE.asItem(), 5);
        map.put(PURPLE_DYE.asItem(), 5);
        map.put(BLUE_SHIFT_DYE.asItem(), 4);
        map.put(BLUE_DYE.asItem(), 4);
        map.put(GREEN_SHIFT_DYE.asItem(), 3);
        map.put(GREEN_DYE.asItem(), 3);
        map.put(BROWN_SHIFT_DYE.asItem(), 2);
        map.put(BROWN_DYE.asItem(), 2);
        map.put(BLACK_SHIFT_DYE.asItem(), 1);
        map.put(BLACK_DYE.asItem(), 1);
    });

    // NBT Keys for Chocobo
    protected static final String NBTKEY_CHOCOBO_COLOR = "Color";
    protected static final String NBTKEY_MOVEMENT_TYPE = "MovementType";
    protected static final String NBTKEY_SADDLE_ITEM = "Saddle";
    protected static final String NBTKEY_WEAPON_ITEM = "Weapon";
    protected static final String NBTKEY_ARMOR_ITEM = "Armor";
    protected static final String NBTKEY_INVENTORY = "Inventory";
    protected static final String NBTKEY_CHOCOBO_GENERATION = "Generation";
    protected static final String NBTKEY_CHOCOBO_COLLAR = "Collar";
    protected static final String NBTKEY_CHOCOBO_SCALE = "Scale";
    protected static final String NBTKEY_CHOCOBO_SCALE_MOD = "ScaleMod";
    protected static final String NBTKEY_CHOCOBO_LEASH_BLOCK = "LeashBlock";
    protected static final String NBTKEY_CHOCOBO_LEASH_DISTANCE = "LeashDistance";
    protected static final String NBTKEY_CHOCOBO_ABILITY_MASK = "AbilityMask";


    protected static final UniformIntProvider PERSISTENT_ANGER_TIME = TimeHelper.betweenSeconds(20, 39);
    protected static final TrackedData<Integer> DATA_REMAINING_ANGER_TIME = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> PARAM_COLOR = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> PARAM_MOVEMENT_TYPE = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<ItemStack> PARAM_SADDLE_ITEM = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.ITEM_STACK);
    protected static final TrackedData<ItemStack> PARAM_WEAPON_ITEM = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.ITEM_STACK);
    protected static final TrackedData<ItemStack> PARAM_ARMOR_ITEM = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.ITEM_STACK);
    protected static final TrackedData<Integer> PARAM_COLLAR_COLOR = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.INTEGER);
    protected final static TrackedData<Integer> PARAM_GENERATION = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.INTEGER);
    protected final static TrackedData<Byte> PARAM_CHOCOBO_ABILITY_MASK = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Integer> PARAM_SCALE = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Float> PARAM_SCALE_MOD = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<BlockPos> PARAM_LEASH_BLOCK = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.BLOCK_POS);
    protected static final TrackedData<Integer> PARAM_LEASH_LENGTH = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.INTEGER);



    // Hardcoded Chocobo Values
    public static final int tier_one_chocobo_inv_slot_count = 15; // 3*5
    public static final int tier_two_chocobo_inv_slot_count = 45; //5*9
    public final int top_tier_chocobo_inv_slot_count = tier_two_chocobo_inv_slot_count;
    protected static final byte MASK_CHOCOBO_FLAME_BLOOD = 0b00000001;
    protected static final byte MASK_CHOCOBO_WATER_BREATH = 0b00000010;
    protected static final byte MASK_CHOCOBO_WITHER_IMMUNE = 0b00000100;
    protected static final byte MASK_CHOCOBO_POISON_IMMUNE = 0b00001000;
    protected static final byte MASK_CHOCOBO_IS_MALE = 0b00010000;
    protected static final byte MASK_CHOCOBO_FROM_EGG = 0b00100000;
    protected static final UUID CHOCOBO_CHEST_ARMOR_MOD_UUID = UUID.fromString("c03d8021-8839-4377-ac23-ed723ece6454");
    protected static final UUID CHOCOBO_CHEST_ARMOR_TOUGH_MOD_UUID = UUID.fromString("f7dcb185-7182-4a28-83ae-d1a2de9c022d");
    protected static final UUID CHOCOBO_WEAPON_DAM_MOD_UUID = UUID.fromString("b9f0dc43-15a7-49f5-815c-915322c30402");
    protected static final UUID CHOCOBO_WEAPON_SPD_MOD_UUID = UUID.fromString("46c84540-15f7-4f22-9da9-ebc23d2353af");
    protected static final UUID CHOCOBO_SPRINTING_BOOST_ID = UUID.fromString("03ba3167-393e-4362-92b8-909841047640");


    protected AbstractChocobo(EntityType<? extends TameableEntity> entityType, World world) { super(entityType, world); }

    @Override
    public void travel(@NotNull Vec3d travelInput) {
        if (this.isAlive() && this.isSubmergedInWater() && this.isWaterBreathing()) {
            // Check if the chocobo is near a land edge and trying to climb out
            boolean tryingToClimbOut = false;

            if (this.getTarget() != null || this.hasPassengers()) { // In combat or with rider,
                // Check if there's land nearby and slightly above the current position
                BlockPos currentPos = this.getBlockPos();
                for (int dx = -1; dx <= 1 && !tryingToClimbOut; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dz == 0) { continue; } // Skip current position
                        BlockPos checkPos = currentPos.add(dx, 1, dz);
                        if (!this.getWorld().getFluidState(checkPos).isIn(FluidTags.WATER) &&
                            this.getWorld().getBlockState(checkPos.down()).hasSolidTopSurface(this.getWorld(), checkPos.down(), this)) {
                            // Found solid land nearby and above water level
                            tryingToClimbOut = true;
                            Vec3d motion = this.getVelocity();
                            // Give a boost upward and slightly toward the land
                            double upwardBoost = 0.4; // Additional vertical momentum
                            this.setVelocity(motion.x + (dx * 0.1), Math.max(motion.y, upwardBoost), motion.z + (dz * 0.1));
                            break;
                        }
                    }
                }
            }

            if (this.getControllingPassenger() == null && this.moveControl instanceof ChocoboBrains.ChocoboSwimMoveControl) { // AI-controlled swimmer
                this.updateVelocity(this.getMovementSpeed(), travelInput);
                Vec3d velocity = this.getVelocity();
                this.setVelocity(velocity.x, this.upwardSpeed, velocity.z);
                this.move(SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.9D));
            } else { // Player-controlled or not a swimmer
                super.travel(travelInput);
            }
        } else {
            super.travel(travelInput);
        }
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new ChocoboAmphibiousSwimNavigation(this, world);
    }

    // Initialization of DataTracker for all Chocobo types
    protected void initDataTracker() {
        this.dataTracker.startTracking(PARAM_COLLAR_COLOR, 0);
        this.dataTracker.startTracking(PARAM_COLOR, ChocoboColor.YELLOW.ordinal());
        this.dataTracker.startTracking(PARAM_MOVEMENT_TYPE, MovementType.WANDER.ordinal());
        this.dataTracker.startTracking(PARAM_SADDLE_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_ARMOR_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_WEAPON_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_GENERATION, 0);
        this.dataTracker.startTracking(PARAM_CHOCOBO_ABILITY_MASK, (byte) 0);
        this.dataTracker.startTracking(PARAM_SCALE, 0);
        this.dataTracker.startTracking(PARAM_SCALE_MOD, 1f);
        this.dataTracker.startTracking(DATA_REMAINING_ANGER_TIME, 0);
        this.dataTracker.startTracking(PARAM_LEASH_BLOCK, new BlockPos(0, 50000, 0));
        this.dataTracker.startTracking(PARAM_LEASH_LENGTH, 0);
        super.initDataTracker();
    }

    // hook for Chocobo types, left for override
    public boolean isSitting() { return false; }
    public boolean isPersistent() { return this.isTamed() || this.fromEgg() || this.isCustomNameVisible(); }
    public boolean cannotDespawn() { return this.hasVehicle() || this.isPersistent(); }
    public boolean canImmediatelyDespawn(double pDistanceToClosestPlayer) { return !this.cannotDespawn(); }
    public boolean isDisallowedInPeaceful() { return false; }
    // Method to get World, Used by 'default public LivingEntity getOwner()' to get Owner by UUID in the world.
    public EntityView method_48926() { return super.getWorld(); }
    protected void onTamedChanged() { super.onTamedChanged(); }
    public boolean isBreedingItem(@NotNull ItemStack stack) { return false; }
    public boolean isInvulnerableTo(@NotNull DamageSource source) {
        // Left uncompacted for readability and future expansion
        ChocoboColor color = this.getChocoboColor();
        if (source.isOf(DamageTypes.STARVE)
                || source.isOf(DamageTypes.CACTUS)
                || source.isOf(DamageTypes.STALAGMITE)
                || source.isOf(DamageTypes.FALLING_STALACTITE)
                || source.isOf(DamageTypes.SWEET_BERRY_BUSH)
                || source.isOf(DamageTypes.FALL))
        { return true; }
        if (source.isOf(DamageTypes.ON_FIRE)
                || source.isOf(DamageTypes.IN_FIRE)
                || source.isOf(DamageTypes.LAVA)
                || source.isOf(DamageTypes.FIREBALL))
        { return this.isFireImmune(); }
        if (source.isOf(DamageTypes.WITHER)
                || source.isOf(DamageTypes.WITHER_SKULL))
        { return this.isWitherImmune(); }
        if (source.isOf(DamageTypes.DROWN))
        { return this.isWaterBreathing(); }
        if (source.isOf(DamageTypes.LIGHTNING_BOLT)
                || source.isOf(DamageTypes.DRAGON_BREATH)) {
            return color == ChocoboColor.GOLD
                || color == ChocoboColor.PURPLE;
        }
        if (source.isOf(DamageTypes.FREEZE)) {
            return color == ChocoboColor.GOLD
                    || color == ChocoboColor.WHITE
                    || color == ChocoboColor.PURPLE
                    || color == ChocoboColor.FLAME
                    || color == ChocoboColor.BLACK;
        }
        return super.isInvulnerableTo(source);
    }
    public boolean canHaveStatusEffect(@NotNull StatusEffectInstance potionEffect) {
        // Left uncompacted for readability and future expansion
        if (potionEffect.getEffectType() == StatusEffects.SLOWNESS
                || potionEffect.getEffectType() == StatusEffects.WEAKNESS
                || potionEffect.getEffectType() == StatusEffects.MINING_FATIGUE)
        { return false; } // Chocobos are not affected by slowness, weakness, or mining fatigue
        if (potionEffect.getEffectType() == StatusEffects.WITHER) { return !this.isWitherImmune(); }
        if (potionEffect.getEffectType() == StatusEffects.POISON) { return !this.isPoisonImmune(); }
        return super.canHaveStatusEffect(potionEffect);
    }
    public void onStatusEffectApplied(@NotNull StatusEffectInstance effect, @Nullable Entity source) {
        super.onStatusEffectApplied(effect, source);
        if (effect.getEffectType() == StatusEffects.WATER_BREATHING) {
            this.updateWaterNavPenalties();
            this.updateNavigationAndMoveControl(true); // Force amphibious for effect duration
        }
        if (effect.getEffectType() == StatusEffects.FIRE_RESISTANCE) {
            this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -0.2F);
            this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -0.1F);
            this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        }
        this.setPathfindingPenalty(PathNodeType.DAMAGE_CAUTIOUS, this.isWitherImmune() ? 0.0F : 8.0F);
    }
    public void onStatusEffectRemoved(@NotNull StatusEffectInstance effect) {
        super.onStatusEffectRemoved(effect);
        if (effect.getEffectType() == StatusEffects.WATER_BREATHING) {
            this.updateWaterNavPenalties();
            this.updateNavigationAndMoveControl(false); // Revert to default based on current state (innate abilities)
        }
        if (effect.getEffectType() == StatusEffects.FIRE_RESISTANCE) {
            this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, this.isFireImmune() ? -0.2F : 32.0F);
            this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, this.isFireImmune() ? -0.1F : 16.0F);
            this.setPathfindingPenalty(PathNodeType.LAVA, this.isFireImmune() ? 0.0F : 16.0F);
        }
        this.setPathfindingPenalty(PathNodeType.DAMAGE_CAUTIOUS, this.isWitherImmune() ? 0.0F : 8.0F);
    }
    public double getMountedHeightOffset() {
        double scaleZero = this.getChocoboScale() == 0 ? 1.7D : this.getChocoboScale() > 0 ? 1.55D : 1.85D;
        return (scaleZero * this.getChocoboScaleMod());
    }
    public Entity getPrimaryPassenger() { return this.getPassengerList().isEmpty() ? null : this.getPassengerList().get(0); }
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getPrimaryPassenger();
        if (entity instanceof MobEntity mobEntity) { return mobEntity; }
        if (this.isSaddled() && entity instanceof PlayerEntity playerEntity) { return playerEntity; }
        return null;
    }
    protected boolean updateWaterState() {
        this.fluidHeight.clear();
        this.updateInWaterStateAndDoWaterCurrentPushing();
        boolean flag = this.updateMovementInFluid(FluidTags.LAVA, 0.085D);
        return this.isTouchingWater() || flag;
    }
    protected void updateInWaterStateAndDoWaterCurrentPushing() {
        if (!this.isWaterBreathing()) {
            if (this.updateMovementInFluid(FluidTags.WATER, 0.014D)) {
                if (!this.touchingWater && !this.firstUpdate) { this.onSwimmingStart(); }
                this.fallDistance = 0.0F;
                this.touchingWater = true;
                this.extinguish();
            } else { this.touchingWater = false; }
        } else {
            if (this.updateMovementInFluid(FluidTags.WATER, 1D)) {
                this.extinguish();
                this.touchingWater = true;
                if (this.getPrimaryPassenger() instanceof PlayerEntity rider) {  rider.extinguish(); }
            } else { this.touchingWater = false; }
        }
    }
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        if (!this.hasPassenger(passenger)) { return; }
        if (passenger instanceof MobEntity && this.getPrimaryPassenger() == passenger) { this.bodyYaw = ((LivingEntity) passenger).bodyYaw; }
        double d = this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset();
        positionUpdater.accept(passenger, this.getX(), d, this.getZ());
    }
    public boolean onKilledOther(ServerWorld world, LivingEntity targetEntity) {
        // Left uncompacted for readability and future expansion
        return super.onKilledOther(world, targetEntity);
    }
    public void onDeath(DamageSource source) {
        // Left in for unique Chocobo Checks able to be done in AbstractChocobo
        if (this.getClass().equals(Chocobo.class)) {
            @NotNull ItemStack egg = switch (this.getChocoboColor()) {
                case YELLOW -> new ItemStack(YELLOW_CHOCOBO_SPAWN_EGG);
                case WHITE -> new ItemStack(WHITE_CHOCOBO_SPAWN_EGG);
                case GREEN -> new ItemStack(GREEN_CHOCOBO_SPAWN_EGG);
                case FLAME -> new ItemStack(FLAME_CHOCOBO_SPAWN_EGG);
                case BLACK -> new ItemStack(BLACK_CHOCOBO_SPAWN_EGG);
                case GOLD -> new ItemStack(GOLD_CHOCOBO_SPAWN_EGG);
                case BLUE -> new ItemStack(BLUE_CHOCOBO_SPAWN_EGG);
                case RED -> new ItemStack(RED_CHOCOBO_SPAWN_EGG);
                case PINK -> new ItemStack(PINK_CHOCOBO_SPAWN_EGG);
                case PURPLE -> new ItemStack(PURPLE_CHOCOBO_SPAWN_EGG);
            };
            if (RandomHelper.random.nextInt(1000)+1 < 45) { this.dropStack(egg); }
        }
        super.onDeath(source);
    }
    // applyDamageEffects is used to apply effects after the damage is applied, such as dropping items or applying potion effects.
    public void applyDamageEffects(LivingEntity attacker, Entity target) {
        // Left uncompacted for readability and future expansion
        super.applyDamageEffects(attacker, target);
    }
    public boolean canBeLeashedBy(PlayerEntity player) { return false; }
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) { return null; }
    protected void dropLoot(@NotNull DamageSource source, boolean causedByPlayer) {
        // Left uncompacted for readability and future expansion
        super.dropLoot(source, causedByPlayer);
    }
    protected SoundEvent getAmbientSound() { return AMBIENT_SOUND; }
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) { return AMBIENT_SOUND; }
    protected SoundEvent getDeathSound() { return AMBIENT_SOUND; }
    protected float getSoundVolume() { return .6f; }
    public boolean tryAttack(Entity entity) {
        // Left uncompacted for readability and future expansion
        boolean result = super.tryAttack(entity);
        boolean config = ChocoboConfig.EXTRA_CHOCOBO_EFFECT.get();
        if (result && config) {
            if (entity instanceof LivingEntity target) {
                if (target instanceof SpiderEntity e) {
                    onHitMobChance(10, STRING, e);
                }
                if (target instanceof CaveSpiderEntity e) {
                    onHitMobChance(5, FERMENTED_SPIDER_EYE, e);
                }
                if (target instanceof SkeletonEntity e) {
                    onHitMobChance(10, BONE, e);
                }
                if (target instanceof WitherSkeletonEntity e) {
                    onHitMobChance(10, CHARCOAL, e);
                }
                if (target instanceof IronGolemEntity e) {
                    onHitMobChance(5, POPPY, e);
                }
                if (target.getEquippedStack(EquipmentSlot.MAINHAND) != ItemStack.EMPTY) {
                    if (onHitMobChance(30)) {
                        target.dropItem(target.getEquippedStack(EquipmentSlot.MAINHAND).getItem());
                        target.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    }
                }
                if (target.getEquippedStack(EquipmentSlot.OFFHAND) != ItemStack.EMPTY) {
                    if (onHitMobChance(10)) {
                        target.dropItem(target.getEquippedStack(EquipmentSlot.OFFHAND).getItem());
                        target.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
                    }
                }
            }
        }
        return result;
    }
    public ItemEntity dropItem(@NotNull ItemConvertible item) {
        Item itemToDrop = item.asItem();
        if (itemToDrop == null || itemToDrop == Items.AIR || itemToDrop instanceof ChocoboArmorItems || itemToDrop instanceof ChocoboWeaponItems || itemToDrop instanceof ChocoboSaddleItem) { return null; }
        return super.dropItem(item);
    }
    public void tick() {
        if (this.getWorld().isClient) { super.tick(); return; }
        if (this.canWalkOnWater() && this.isTouchingWater() && !this.hasVehicle() && !this.hasPassengers()) { this.ticksOnWater++; }
        else { this.ticksOnWater = 0; }
        if (this.age % 100 == 0) {
            this.gearSlotChecks();
            this.verifyChocoboGearModifiers();
        }
        super.tick();
    }
    public void setSprinting(boolean sprinting) {
        this.setFlag(SPRINTING_FLAG_INDEX, sprinting);
        EntityAttributeInstance attributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        assert attributeInstance != null;
        if (attributeInstance.getModifier(CHOCOBO_SPRINTING_BOOST_ID) != null) { attributeInstance.removeModifier(CHOCOBO_SPRINTING_SPEED_BOOST); }
        if (sprinting) { attributeInstance.addTemporaryModifier(CHOCOBO_SPRINTING_SPEED_BOOST); }
        // Set the sprinting speed boost, bypassing the LivingEntity's sprinting speed boost
    }
    public boolean canWalkOnFluid(FluidState state) {
        if (state.isIn(FluidTags.LAVA)) { return this.isFireImmune(); }
        if (state.isIn(FluidTags.WATER)) { return this.canWalkOnWater(); }
        return super.canWalkOnFluid(state);
    }
    public boolean shouldDismountUnderwater() { return this.canWalkOnWater(); }
    protected boolean shouldSwimInFluids() {
        // Left uncompacted for readability and future expansion
        return super.shouldSwimInFluids();
    }
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        BlockState state = world.getBlockState(pos);
        if (state.getFluidState().isIn(FluidTags.WATER)) {
            if (this.canWalkOnWater()) { return 4.0F; }
            else { return this.isWaterBreathing() ? 0.5F : 8.0F; }
        }
        if (this.isFireImmune() && state.getFluidState().isIn(FluidTags.LAVA)) { return 0.5F; }
        return super.getPathfindingFavor(pos, world);
    }

    // boolean checks for Chocobo properties
    public boolean isWaterBreathing() { return this.isWaterBloodChocobo() || this.hasStatusEffect(StatusEffects.WATER_BREATHING); }
    public boolean canWalkOnWater() {
        // TODO -> logic to enable disable walking on water (controlling water breathing) on a temporary condition.
        return !this.isWaterBreathing();
    }
    public boolean isWaterBloodChocobo() { return (this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK) & MASK_CHOCOBO_WATER_BREATH) != 0; }
    public boolean isWitherImmune() { return (this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK) & MASK_CHOCOBO_WITHER_IMMUNE) != 0; }
    public boolean isPoisonImmune() { return (this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK) & MASK_CHOCOBO_POISON_IMMUNE) != 0; }
    public int getChocoboScale() { return this.dataTracker.get(PARAM_SCALE); }
    public float getChocoboScaleMod() { return this.dataTracker.get(PARAM_SCALE_MOD); }
    public boolean isMale() { return (this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK) & MASK_CHOCOBO_IS_MALE) != 0; }
    public boolean fromEgg() { return (this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK) & MASK_CHOCOBO_FROM_EGG) != 0; }
    
    // getters for Chocobo properties
    public MovementType getMovementType() { return MovementType.values()[this.dataTracker.get(PARAM_MOVEMENT_TYPE)]; }
    public boolean isFireImmune() { return this.isFlameBlood() || super.isFireImmune(); }
    public boolean isFlameBlood() { return (this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK) & MASK_CHOCOBO_FLAME_BLOOD) != 0; }
    public byte getChocoboAbilityMask() { return this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK); }
    public boolean isSaddled() { return !this.getSaddle().isEmpty(); }
    public boolean isArmored() { return !this.getArmorItemStack().isEmpty(); }
    public boolean isArmed() { return !this.getWeapon().isEmpty(); }
    public boolean isChocoboArmor(@NotNull ItemStack pStack) { return pStack.getItem() instanceof ChocoboArmorItems; }
    public boolean isChocoWeapon(@NotNull ItemStack pStack) { return pStack.getItem() instanceof ChocoboWeaponItems; }
    public int getTicksOnWater() { return this.ticksOnWater; }
    public ItemStack getSaddle() { return this.dataTracker.get(PARAM_SADDLE_ITEM); }
    public ItemStack getWeapon() { return this.dataTracker.get(PARAM_WEAPON_ITEM); }
    public ItemStack getArmorItemStack() { return this.dataTracker.get(PARAM_ARMOR_ITEM); }
    public boolean canBreatheInWater() {
        // TODO -> logic to enable disable water breathing (controlling walking on water) on a temporary condition.
        return this.isWaterBreathing();
    }
    public int getGeneration() { return this.dataTracker.get(PARAM_GENERATION); }
    public String getGenerationString() {
        int gen = this.getGeneration();
        return Integer.toString(gen);
    }
    public ChocoboColor getChocoboColor() { return ChocoboColor.values()[this.dataTracker.get(PARAM_COLOR)]; }
    public int getAngerTime() { return this.remainingPersistentAngerTime; }
    public double getFollowSpeedModifier() { return this.followSpeedModifier; }
    public UUID getAngryAt() { return this.persistentAngerTarget; }
    public Integer getCollarColor() { return this.dataTracker.get(PARAM_COLLAR_COLOR); }
    public BlockPos getLeashSpot() { return this.dataTracker.get(PARAM_LEASH_BLOCK); }
    public boolean canWonder() { return this.getMovementType() == MovementType.WANDER; }
    public boolean isNoRoam() { return this.getMovementType() == MovementType.STANDSTILL; }
    public boolean followOwner() { return this.getMovementType() == MovementType.FOLLOW_OWNER; }
    public boolean followLure() { return this.getMovementType() == MovementType.FOLLOW_LURE; }
    public int getLeashDistance() { return this.dataTracker.get(PARAM_LEASH_LENGTH); }
    public int chocoStatMod() { return ChocoboConfig.DEFAULT_GEAR_MOD.get(); }
    
    // setters for Chocobo properties
    public void setMale(boolean isMale) {
        byte abilityMask = this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK);
        if (isMale) { abilityMask |= MASK_CHOCOBO_IS_MALE; }
        else { abilityMask &= ~MASK_CHOCOBO_IS_MALE; }
        this.dataTracker.set(PARAM_CHOCOBO_ABILITY_MASK, abilityMask);
    }
    public void setFromEgg(boolean fromEgg) {
        byte abilityMask = this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK);
        if (fromEgg) { abilityMask |= MASK_CHOCOBO_FROM_EGG; }
        else { abilityMask &= ~MASK_CHOCOBO_FROM_EGG; }
        this.dataTracker.set(PARAM_CHOCOBO_ABILITY_MASK, abilityMask);
    }
    public void setFlame(boolean flame) {
        byte abilityMask = this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK);
        if (flame) { abilityMask |= MASK_CHOCOBO_FLAME_BLOOD; }
        else { abilityMask &= ~MASK_CHOCOBO_FLAME_BLOOD; }
        this.dataTracker.set(PARAM_CHOCOBO_ABILITY_MASK, abilityMask);
    }
    public void setWaterBreath(boolean waterBreath) {
        byte abilityMask = this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK);
        if (waterBreath) { abilityMask |= MASK_CHOCOBO_WATER_BREATH; }
        else { abilityMask &= ~MASK_CHOCOBO_WATER_BREATH; }
        this.dataTracker.set(PARAM_CHOCOBO_ABILITY_MASK, abilityMask);
        this.updateWaterNavPenalties();
        this.updateNavigationAndMoveControl(false); // Update based on new innate ability state
    }
    public void setWitherImmune(boolean witherImmune) {
        byte abilityMask = this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK);
        if (witherImmune) { abilityMask |= MASK_CHOCOBO_WITHER_IMMUNE; }
        else { abilityMask &= ~MASK_CHOCOBO_WITHER_IMMUNE; }
        this.dataTracker.set(PARAM_CHOCOBO_ABILITY_MASK, abilityMask);
    }
    public void setPoisonImmune(boolean poisonImmune) {
        byte abilityMask = this.dataTracker.get(PARAM_CHOCOBO_ABILITY_MASK);
        if (poisonImmune) { abilityMask |= MASK_CHOCOBO_POISON_IMMUNE; }
        else { abilityMask &= ~MASK_CHOCOBO_POISON_IMMUNE; }
        this.dataTracker.set(PARAM_CHOCOBO_ABILITY_MASK, abilityMask);
    }
    public void setChocoboAbilityMask(byte mask) {
        this.dataTracker.set(PARAM_CHOCOBO_ABILITY_MASK, mask);
        this.updateNavigationAndMoveControl(false); // Update based on new ability mask state
    }
    public void setChocoboScale(boolean isMale, int overrideValue, boolean override) {
        int scale;
        if (override) { scale = overrideValue; } else { scale = setChocoScale(isMale); }
        this.setChocoboScaleMod(ScaleMod(scale));
        this.dataTracker.set(PARAM_SCALE, scale);
    }
    public void setChocoboScaleMod(float value) { this.dataTracker.set(PARAM_SCALE_MOD, value); }
    public void setMovementType(@NotNull MovementType type) { this.dataTracker.set(PARAM_MOVEMENT_TYPE, type.ordinal()); setMovementAiByType(type); }
    protected void setMovementAiByType(@NotNull MovementType type) {
        switch (type) {
            case STANDSTILL -> this.followingMrHuman = 3;
            case FOLLOW_OWNER -> this.followingMrHuman = 1;
            default -> this.followingMrHuman = 2;
        }
    }
    public void setMovementTypeByFollowMrHuman(float followingNumber) {
        MovementType type = switch ((int) followingNumber) {
            case 1 -> MovementType.FOLLOW_OWNER;
            case 2 -> MovementType.STANDSTILL;
            default -> MovementType.WANDER;
        };
        this.dataTracker.set(PARAM_MOVEMENT_TYPE, type.ordinal());
    }
    protected void setSaddleType(@NotNull ItemStack saddleStack) {
        ItemStack oldStack = getSaddle();
        if (oldStack.getItem() != saddleStack.getItem()) { this.dataTracker.set(PARAM_SADDLE_ITEM, saddleStack.copy()); }
    }
    protected void gearSlotChecks() {
        ItemStack armor = this.getArmorItemStack();
        ItemStack weapon = this.getWeapon();
        // Chest/Main_hand Items should not be different from the stored stacks.
        if (armor != this.getEquippedStack(EquipmentSlot.CHEST)) { this.setArmor(armor); }
        if (weapon != this.getEquippedStack(EquipmentSlot.MAINHAND)) { this.setWeapon(weapon); }
    }
    protected void setWeaponType(@NotNull ItemStack weaponType) {
        ItemStack oldStack = getWeapon();
        if (oldStack.getItem() != weaponType.getItem()) { this.dataTracker.set(PARAM_WEAPON_ITEM, weaponType.copy()); }
    }
    protected void setArmorType(@NotNull ItemStack armorType) {
        ItemStack oldStack = getArmorItemStack();
        if (oldStack.getItem() != armorType.getItem()) { this.dataTracker.set(PARAM_ARMOR_ITEM, armorType.copy()); }
    }
    protected void setArmor(ItemStack pStack) {
        this.equipStack(EquipmentSlot.CHEST, pStack);
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
    }
    protected void setWeapon(ItemStack pStack) {
        this.equipStack(EquipmentSlot.MAINHAND, pStack);
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }
    public void setChocoboArmorStats(ItemStack pStack) {
        if (!this.getWorld().isClient()) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).removeModifier(CHOCOBO_CHEST_ARMOR_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).removeModifier(CHOCOBO_CHEST_ARMOR_TOUGH_MOD_UUID);
            if (this.isChocoboArmor(pStack)) {
                this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
                int p = ((ChocoboArmorItems)pStack.getItem()).getDefense()*chocoStatMod();
                float t = ((ChocoboArmorItems)pStack.getItem()).getToughness()*chocoStatMod();
                if (p != 0) { Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).addPersistentModifier(new EntityAttributeModifier(CHOCOBO_CHEST_ARMOR_MOD_UUID, "Chocobo Armor Bonus", p, EntityAttributeModifier.Operation.ADDITION)); }
                if (t != 0) { Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).addPersistentModifier(new EntityAttributeModifier(CHOCOBO_CHEST_ARMOR_TOUGH_MOD_UUID, "Chocobo Armor Toughness", t, EntityAttributeModifier.Operation.ADDITION)); }
                this.setArmor(pStack);
            }
        }
    }
    public void setChocoboWeaponStats(ItemStack pStack) {
        if (!this.getWorld().isClient()) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).removeModifier(CHOCOBO_WEAPON_DAM_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED)).removeModifier(CHOCOBO_WEAPON_SPD_MOD_UUID);
            if (this.isChocoWeapon(pStack)) {
                double a = ((ChocoboWeaponItems)pStack.getItem()).getAttackDamage()*chocoStatMod();
                float s = ((ChocoboWeaponItems)pStack.getItem()).getAttackSpeed()*chocoStatMod();
                if (a != 0) { Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).addPersistentModifier(new EntityAttributeModifier(CHOCOBO_WEAPON_DAM_MOD_UUID, "Chocobo Attack Bonus", a, EntityAttributeModifier.Operation.ADDITION)); }
                if (s != 0) { Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED)).addPersistentModifier(new EntityAttributeModifier(CHOCOBO_WEAPON_SPD_MOD_UUID, "Chocobo Attack Speed Bonus", s, EntityAttributeModifier.Operation.ADDITION)); }
                this.setWeapon(pStack);
            }
        }
    }
    protected void verifyChocoboGearModifiers() {
        if (!this.getWorld().isClient()) {
            this.setChocoboArmorStats(this.getArmorItemStack());
            this.setChocoboWeaponStats(this.getWeapon());
        }
    }
    public void setGeneration(int value) { this.dataTracker.set(PARAM_GENERATION, value); }
    public void setAngerTime(int angerTime) { this.remainingPersistentAngerTime = angerTime; }
    public void setAngryAt(@Nullable UUID angryAt) { this.persistentAngerTarget = angryAt; }
    public void setChocoboColor(@NotNull ChocoboColor color) { this.dataTracker.set(PARAM_COLOR, color.ordinal()); }
    public void setCollarColor(Integer color) { this.dataTracker.set(PARAM_COLLAR_COLOR, color); }
    // TODO - Convert double to int for leash distance, Fix where it is called.
    protected void setLeashedDistance(double distance) { this.dataTracker.set(PARAM_LEASH_LENGTH, (int) distance); }
    protected void setLeashSpot(BlockPos blockPos) { this.dataTracker.set(PARAM_LEASH_BLOCK, blockPos); }
    public void setChocobo(ChocoboColor color) {
        this.setChocoboColor(color);
        this.setFlame(color == ChocoboColor.FLAME);
        this.setWaterBreath(isWaterBreathingChocobo(color));
        this.setWitherImmune(isWitherImmuneChocobo(color));
        this.setPoisonImmune(chocoboChecks.isPoisonImmuneChocobo(color));
        this.updateWaterNavPenalties();
        this.updateNavigationAndMoveControl(false); // Sets Default for chocobos on spawn, and updates based on new innate ability state
    }
    protected void setChocoboSpawnCheck(ChocoboColor color) {
        ChocoboColor chocobo = this.getChocoboColor();
        if ((chocobo == ChocoboColor.YELLOW || color == ChocoboColor.YELLOW) && chocobo != color) { setChocobo(color); }
    }


    // Helper methods for Chocobos
    public void updateWaterNavPenalties() {
        if (this.isWaterBreathing()) { // this now correctly checks for innate ability OR status effect
            this.setPathfindingPenalty(PathNodeType.WATER, -0.8F);
            this.setPathfindingPenalty(PathNodeType.WATER_BORDER, -0.5F);
        } else { // This is a water-walker
            this.setPathfindingPenalty(PathNodeType.WATER, 8.0F);
            this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 0.0F);
        }
    }
    public float ScaleMod(int scale) { return (scale == 0) ? 0 : ((scale < 0) ? (((float) ((scale * -1) - 100) / 100) * -1) : (1f + ((float) scale / 100))); }
    protected Box spawnControlBoxSize(@NotNull Box box, int multi) {
        int xz = 8*5 * Math.max(multi, 1); //8 half a chunk
        int y = 32 * Math.max(multi/2, 1);
        return box.expand(xz, y, xz);
    }
    private boolean maybeTeleportTo(int pX, int pY, int pZ, @NotNull LivingEntity owner) {
        if (Math.abs((double)pX - owner.getX()) < 2.0D && Math.abs((double)pZ - owner.getZ()) < 2.0D) { return false; }
        else if (!this.canTeleportTo(new BlockPos(pX, pY, pZ))) { return false; }
        else {
            this.updatePositionAndAngles((double)pX + 0.5D, pY, (double)pZ + 0.5D, this.getYaw(), this.getPitch());
            this.navigation.stop();
            return true;
        }
    }
    protected void teleportToOwner(@NotNull LivingEntity owner) {
        BlockPos blockpos = owner.getBlockPos();

        for(int i = 0; i < 10; ++i) {
            int j = this.randomIntInclusive(-3, 3);
            int k = this.randomIntInclusive(-1, 1);
            int l = this.randomIntInclusive(-3, 3);
            boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l, owner);
            if (flag) { return; }
        }
    }
    private boolean canTeleportTo(@NotNull BlockPos pPos) {
        PathNodeType pathNodeTypes = LandPathNodeMaker.getLandNodeType(this.getWorld(), pPos.mutableCopy());
        if (pathNodeTypes != PathNodeType.WALKABLE) { return false; }
        else {
            BlockPos blockpos = pPos.subtract(this.getBlockPos());
            return this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(blockpos));
        }
    }
    protected int randomIntInclusive(int pMin, int pMax) { return this.getRandom().nextInt(pMax - pMin + 1) + pMin; }
    protected static void onHitMobChance(int percentChance, Item item, Entity e) { if (RandomHelper.random.nextInt(100)+1 < percentChance) { e.dropItem(item); } }
    protected static boolean onHitMobChance(int percentChance) { return RandomHelper.random.nextInt(100)+1 < percentChance; }
    protected boolean onDeathCheck(int upper, int barrier) {
        if (barrier <= 0 || upper < barrier) { return false; }
        return (RandomHelper.random.nextInt(upper) + 1) < barrier;
    }
    protected int statCount(int stat, int check) { return stat - check; }
    protected void tickActivities(@NotNull Chocobo chocobo) {
        // Brain Logic
        Brain<Chocobo> brain = chocobo.getBrain();
        brain.tick((ServerWorld) chocobo.getWorld(), chocobo);
        // brain.resetPossibleActivities(ImmutableList.of(Activity.IDLE, Activity.AVOID, Activity.PANIC, Activity.FIGHT));
        chocobo.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
        chocobo.tickAngerLogic((ServerWorld) chocobo.getWorld(), false);
        if (chocobo.getTarget() != null) { chocobo.maybeAlertOthers(chocobo); }
        else if (chocobo.ticksUntilNextAlert > 0) { --chocobo.ticksUntilNextAlert; }
        if (chocobo.hasAngerTime()) { chocobo.playerHitTimer = chocobo.age; }
    }

    // Stat Increase Methods
    private static final int STAMINA_COST = 16;       // removed stat (stamina) cost, no update applied
    private static final int HEALTH_COST = 8;           // cost for health update ("hp")
    private static final int ARMOR_TOUGHNESS_COST = 4;    // cost for armor toughness ("arm_tough")
    private static final int ARMOR_COST = 2;              // cost for armor update ("arm")
    private static final int STRENGTH_COST = 1;
    private static final int ALL_COST = STAMINA_COST + HEALTH_COST + ARMOR_TOUGHNESS_COST + ARMOR_COST + STRENGTH_COST;
    protected static final String all = "all";
    protected static final String health = "hp";
    protected static final String strength = "str";
    protected static final String armor = "arm";
    protected static final String armorTough = "arm_tough";
    protected static final String dualDefense = "defences";

    protected void increaseStat(Chocobo chocobo, String statName, @SuppressWarnings("SameParameterValue") int amount, PlayerEntity playerEntity) {
        if (chocobo == null || statName == null || playerEntity == null) { return; }
        if (amount <= 0) { return; } // Ensure amount is positive
        if (!chocobo.isAlive() || !chocobo.isTamed()) { return; } // Ensure chocobo is alive and tamed
        int statValue;
        boolean isArmorToughnessNotMaxed = false;
        boolean isArmorNotMaxed = false;
        switch (statName) {
            case all -> statValue = ALL_COST;
            case health -> statValue = HEALTH_COST;
            case strength -> statValue = STRENGTH_COST;
            case dualDefense -> {
                if (ChocoboConfig.MAX_ARMOR_TOUGHNESS.get() <= chocobo.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)) { isArmorToughnessNotMaxed = true; }
                if (ChocoboConfig.MAX_ARMOR.get() <= chocobo.getAttributeValue(EntityAttributes.GENERIC_ARMOR)) { isArmorNotMaxed = true; }
                if (isArmorToughnessNotMaxed && isArmorNotMaxed) { statValue = ARMOR_TOUGHNESS_COST + ARMOR_COST; }
                else if (isArmorNotMaxed)  { statValue = ARMOR_TOUGHNESS_COST; }
                else if (isArmorToughnessNotMaxed) { statValue = ARMOR_COST;}
                else { statValue = 0; }
            }
            case armor -> statValue = ARMOR_COST;
            case armorTough -> statValue = ARMOR_TOUGHNESS_COST;
            default -> { return; } // Invalid stat name, do nothing
        }
        numberSplit(statValue, playerEntity, chocobo, amount);
    }
    private void numberSplit(int value, PlayerEntity playerEntity, Chocobo chocobo, int amountIncrease) {
        if (value <= 0 || playerEntity == null) { return; } // Ensure value is positive and player is not null
        int hold = value;
        if (statCount(hold, STAMINA_COST) >= 0) {
            // Removed stat (Stamina), placeholder for future use
            hold -= STAMINA_COST;
        }
        if (statCount(hold, HEALTH_COST) >= 0) {
            hold -= HEALTH_COST;
            if (amountIncrease % 2 != 0) { amountIncrease +=1; } // Ensure even number for health increase
            else { amountIncrease = (amountIncrease / 2) + 1; } // If odd, round up
            statSwitch(health, playerEntity, chocobo, amountIncrease);
        }
        if (statCount(hold, ARMOR_TOUGHNESS_COST) >= 0) {
            hold -= ARMOR_TOUGHNESS_COST;
            statSwitch(armorTough, playerEntity, chocobo, amountIncrease);
        }
        if (statCount(hold, ARMOR_COST) >= 0) {
            hold -= ARMOR_COST;
            statSwitch(armor, playerEntity, chocobo, amountIncrease);
        }
        if (statCount(hold, STRENGTH_COST) >= 0) {
            hold -= STRENGTH_COST;
            statSwitch(strength, playerEntity, chocobo, amountIncrease);
        }
        if (hold > 0) {
            DelChoco.LOGGER.info("Something went wrong with stat increase, value: {}, hold: {}", value, hold);
        }
    }
    private void statSwitch(String key, PlayerEntity playerEntity, Chocobo chocobo, int amountIncrease) {
        if (key == null || playerEntity == null || chocobo == null) return; // Ensure key, player, and chocobo are not null
        switch (key) {
            case health -> statPlus(EntityAttributes.GENERIC_MAX_HEALTH, ChocoboConfig.MAX_HEALTH.get(), key, playerEntity, chocobo, amountIncrease);
            case strength -> statPlus(EntityAttributes.GENERIC_ATTACK_DAMAGE, ChocoboConfig.MAX_ATTACK.get(), key, playerEntity, chocobo, amountIncrease);
            case armor -> statPlus(EntityAttributes.GENERIC_ARMOR, ChocoboConfig.MAX_ARMOR.get(), key, playerEntity, chocobo, amountIncrease);
            case armorTough -> statPlus(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, ChocoboConfig.MAX_ARMOR_TOUGHNESS.get(), key, playerEntity, chocobo, amountIncrease);
        }
    }
    private void statPlus(EntityAttribute stat, double max, String key, PlayerEntity playerEntity, Chocobo chocobo, int amountIncrease) {
        if (playerEntity.getWorld().isClient()) { return; }
        if (chocobo == null || stat == null || key == null) return; // Ensure stat, player, Chocobo, and key are not null
        double currentValue = chocobo.getAttributeInstance(stat) != null ? Objects.requireNonNull(chocobo.getAttributeInstance(stat)).getValue() : -10;

        // allows for once over Max, but not below 0
        boolean trip = currentValue > max;
        if (!trip && currentValue > 0) {
            Objects.requireNonNull(chocobo.getAttributeInstance(stat)).addPersistentModifier(new EntityAttributeModifier(stat + " food", amountIncrease, EntityAttributeModifier.Operation.ADDITION));
            trip = currentValue + amountIncrease > max;
        }

        String keys = ".entity_chocobo." + key;
        if (trip) { keys = keys + ".full"; }
        else { keys = keys + ".room"; }
        playerEntity.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + keys, chocobo.getCustomName()));
    }
    public int ChocoboShaker(@NotNull String stat) {
        return switch (stat) {
            case "health" -> boundedRangeModifier(5, 10);
            case "attack", "toughness", "defense" -> boundedRangeModifier(1, 4);
            default -> 0;
        };
    }
    protected void chocoboStatShake(EntityAttribute attribute, String text) {
        int aValue = ChocoboShaker(text);
        Objects.requireNonNull(this.getAttributeInstance(attribute)).addPersistentModifier(new EntityAttributeModifier(text + " variance", aValue, EntityAttributeModifier.Operation.ADDITION));
    }
    protected int boundedRangeModifier(int lower, int upper) {
        int range = lower+upper;
        return random.nextInt(range)-lower;
    }
    protected void dropFeather() {
        if (this.getEntityWorld().isClient()) { return; }
        if (this.isBaby()) { return; }
        this.dropStack(new ItemStack(CHOCOBO_FEATHER, 1), 0.0F);
    }
    public boolean updateMovementInFluid(TagKey<Fluid> tag, double speed) {
        if (this.isRegionUnloaded()) { return false; }
        Box box = this.getBoundingBox().contract(0.001);

        // Performance optimization: Only check a subset of blocks in the bounding box
        // Sample key points instead of every block in the box
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.maxY);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);

        // Only sample up to 12 points in the bounding box instead of all blocks
        int sampleCountX = Math.max(1, (j - i) / 2);
        int sampleCountY = Math.max(1, (l - k) / 2);
        int sampleCountZ = Math.max(1, (n - m) / 2);

        double d = 0.0;
        boolean bl = this.isPushedByFluids(tag);
        boolean bl2 = false;
        Vec3d vec3d = Vec3d.ZERO;
        int o = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        // Sample points throughout the bounding box rather than checking every block
        for (int dx = 0; dx <= sampleCountX; ++dx) {
            for (int dy = 0; dy <= sampleCountY; ++dy) {
                for (int dz = 0; dz <= sampleCountZ; ++dz) {
                    double e;
                    int px = i + (dx * (j - i)) / sampleCountX;
                    int py = k + (dy * (l - k)) / sampleCountY;
                    int pz = m + (dz * (n - m)) / sampleCountZ;

                    mutable.set(px, py, pz);
                    FluidState fluidState = this.getWorld().getFluidState(mutable);

                    if (!fluidState.isIn(tag) || !((e = (float)py + fluidState.getHeight(this.getWorld(), mutable)) >= box.minY)) {
                        continue;
                    }

                    bl2 = true;
                    d = Math.max(e - box.minY, d);

                    if (!bl) continue;
                    Vec3d vec3d2 = fluidState.getVelocity(this.getWorld(), mutable);
                    if (d < 0.4) { vec3d2 = vec3d2.multiply(d); }
                    vec3d = vec3d.add(vec3d2);
                    ++o;
                }
            }
        }

        if (vec3d.length() > 0.0) {
            if (o > 0) {
                vec3d = vec3d.multiply(1.0 / (double)o);
            }
            vec3d = vec3d.normalize();
            Vec3d vec3d3 = this.getVelocity();
            vec3d = vec3d.multiply(speed);

            // Apply fluid physics only when meaningful movement is possible
            if (Math.abs(vec3d3.x) < 0.003 && Math.abs(vec3d3.z) < 0.003 && vec3d.length() < 0.0045) {
                vec3d = vec3d.normalize().multiply(0.0045);
            }
            this.setVelocity(this.getVelocity().add(vec3d));
        }
        this.fluidHeight.put(tag, d);
        return bl2;
    }
    public boolean isPushedByFluids(TagKey<Fluid> key) {
        if (this.isRegionUnloaded()) { return false; }
        if (key == null) { return true; }
        if (this.hasPassengers()) { return false; } // Fluids cannot push a Chocobo if it has passengers
        if (key == FluidTags.WATER) { return !this.isWaterBreathing(); }
        else if (key == FluidTags.LAVA) { return !this.isFireImmune(); }
        return this.isPushedByFluids();
    }

    // Alert methods for Chocobo
    protected void alertOthers(PlayerEntity forgivenPlayer, @NotNull Chocobo alertingChocobo) {
        if ((alertingChocobo.ticksUntilNextAlert > 0 && forgivenPlayer == null) || (forgivenPlayer != null && !this.getWorld().getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS))) { return; }
        // double followRange = Math.min(alertingChocobo.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE), 16.0);
        double followRange = EntityAttributes.GENERIC_FOLLOW_RANGE.getDefaultValue();

        Box alertBox = Box.from(alertingChocobo.getPos()).expand(followRange, 15.0D, followRange);
        List<Chocobo> nearbyChocobos = alertingChocobo.getWorld().getNonSpectatingEntities(Chocobo.class, alertBox);
        int alertLimit = Math.min(nearbyChocobos.size(), 15);

        if (forgivenPlayer != null) {
            for (Chocobo chocobo : nearbyChocobos) { chocobo.forgive(forgivenPlayer); } // Forgive all chocobos in the area if a player is specified, kept despite cpu cost to prevent player from being attacked by all chocobos in the area after being forgiven
        } else {
            LivingEntity attacker = alertingChocobo.getTarget();
            if (attacker == null) { return; }

            // Only alert chocobos that are within defined chunk distances
            // 1.5 chunks from alerting chocobo (24 blocks, 576 squared)
            // 3 chunks from attacker (32 blocks, 1024 squared)
            for (int i = 0; i < alertLimit; i++) {
                Chocobo chocobo = nearbyChocobos.get(i);
                if (chocobo != alertingChocobo &&
                    chocobo.shouldAlert(chocobo, attacker) &&
                    (chocobo.squaredDistanceTo(alertingChocobo) < 576.0 ||
                     chocobo.squaredDistanceTo(attacker) < 1024.0)) {
                    chocobo.ticksUntilNextAlert = 30 + chocobo.getRandom().nextInt(20);
                    chocobo.alertChocobo(chocobo, attacker);
                }
            }
            alertingChocobo.ticksUntilNextAlert = ALERT_INTERVAL.get(alertingChocobo.random);
        }
    }
    protected void maybeAlertOthers(@NotNull Chocobo alertingChocobo) {
        if (alertingChocobo.ticksUntilNextAlert > 0) { --alertingChocobo.ticksUntilNextAlert; }
        else {
            if (alertingChocobo.getVisibilityCache().canSee(alertingChocobo.getTarget())) { alertOthers(null, alertingChocobo); }
            this.ticksUntilNextAlert = ALERT_INTERVAL.get(alertingChocobo.random);
        }
    }
    protected boolean shouldAlert(Chocobo chocobo, LivingEntity attacker) {
        return chocobo != attacker
                && !chocobo.isAttacking()
                && ChocoboBrainAid.isAttackable(attacker, chocobo.canWalkOnWater())
                && !chocobo.isBaby();
    }
    protected void alertChocobo(Chocobo chocobo, LivingEntity attacker) {
        // kept in for unique Chocobo Checks unable to be done in AbstractChocobo (Brains)
        //chocobo.getBrain().remember(MemoryModuleType.ATTACK_TARGET, attacker, 50L);
        chocobo.setTarget(attacker);
    }

    private void updateNavigationAndMoveControl(boolean forceAmphibiousDuringEffect) {
        if (this.getWorld().isClient()) { return; }

        EntityNavigation oldNavInstance = this.navigation;
        Class<? extends EntityNavigation> oldNavClass = oldNavInstance.getClass();
        EntityNavigation newNavInstance;

        if (forceAmphibiousDuringEffect) { newNavInstance = new ChocoboAmphibiousSwimNavigation(this, this.getWorld()); }
        else { newNavInstance = this.createNavigation(this.getWorld()); }

        if (newNavInstance.getClass() != oldNavClass) {
            this.navigation = newNavInstance;
            oldNavInstance.stop();
        }

        boolean shouldUseSwimControl;
        if (forceAmphibiousDuringEffect) { shouldUseSwimControl = true; }
        else { shouldUseSwimControl = !this.canWalkOnWater(); }

        if (shouldUseSwimControl) {
            if (!(this.moveControl instanceof ChocoboBrains.ChocoboSwimMoveControl)) {
                this.moveControl = new ChocoboBrains.ChocoboSwimMoveControl(this);
                this.getNavigation().setCanSwim(true);
            }
        } else {
            if (this.moveControl == null || this.moveControl instanceof ChocoboBrains.ChocoboSwimMoveControl) { this.moveControl = new MoveControl(this); }
            if (this.navigation instanceof AmphibiousSwimNavigation mobNav) { mobNav.setCanSwim(false); }
        }
    }
}
