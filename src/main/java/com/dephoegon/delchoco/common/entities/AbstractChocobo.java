package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.chocoboChecks;
import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.common.entities.pathing.ChocoboAmphibiousSwimNavigation;
import com.dephoegon.delchoco.common.entities.properties.ChocoboBrainAid;
import com.dephoegon.delchoco.common.entities.properties.ChocoboBrains;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.entities.properties.MovementType;
import com.dephoegon.delchoco.common.entities.subTypes.ArmorStandChocobo;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import com.dephoegon.delchoco.utils.RandomHelper;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
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

import java.util.*;

import static com.dephoegon.delbase.item.ShiftingDyes.*;
import static com.dephoegon.delchoco.aid.chocoboChecks.isWaterBreathingChocobo;
import static com.dephoegon.delchoco.aid.chocoboChecks.isWitherImmuneChocobo;
import static com.dephoegon.delchoco.common.entities.breeding.ChocoboTweakedSnapShots.setChocoScale;
import static com.dephoegon.delchoco.common.entities.properties.ChocoboBrainAid.requiresSwimmingToTarget;
import static com.dephoegon.delchoco.common.init.ModItems.*;
import static com.dephoegon.delchoco.common.init.ModSounds.AMBIENT_SOUND;
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
    public static final double CHOCOBO_SWING_DISTANCE = 8D;
    protected ItemStack lastSaddleStack = ItemStack.EMPTY;
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
    protected static final String NBTKEY_CHOCOBO_PROPERTIES = "ChocoboProperties";
    protected static final String NBTKEY_INVENTORY = "Inventory";
    protected static final String NBTKEY_INVENTORY_GEAR = "ChocoboGearInventory";
    protected static final String NBTKEY_CHOCOBO_GENERATION = "Generation";
    protected static final String NBTKEY_CHOCOBO_SCALE = "Scale";
    protected static final String NBTKEY_CHOCOBO_ABILITY_MASK = "AbilityMask";


    protected static final UniformIntProvider PERSISTENT_ANGER_TIME = TimeHelper.betweenSeconds(20, 39);
    protected static final TrackedData<Integer> DATA_REMAINING_ANGER_TIME = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> PARAM_CHOCOBO_PROPERTIES = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.INTEGER);
    /*
     * PARAM_CHOCOBO_PROPERTIES is a single 32-bit integer used to store multiple boolean flags and enum values,
     * which is more efficient for network synchronization than using multiple TrackedData entries.
     * The integer is structured as follows, from right to left (least significant to most significant bits):
     *
     * Bits   | Property        | Details
     * -------|-----------------|-----------------------------------------------------------------
     * 0-3    | Color           | 4 bits for ChocoboColor (up to 16 colors).
     * 4-5    | Movement Type   | 2 bits for MovementType (up to 4 types).
     * 6-10   | Collar Color    | 5 bits for Collar Color (up to 32 colors).
     * 11     | Flame Blood     | 1 bit boolean flag.
     * 12     | Water Breath    | 1 bit boolean flag.
     * 13     | Wither Immune   | 1 bit boolean flag.
     * 14     | Poison Immune   | 1 bit boolean flag.
     * 15     | Is Male         | 1 bit boolean flag.
     * 16     | From Egg        | 1 bit boolean flag.
     * 17     | Can Fly         | 1 bit boolean flag.
     * ...    | (Unused)        | Remaining bits are available for future properties.
     *
     * To read a value, we first right-shift the integer to move the desired bits to the far right,
     * then apply a bitwise AND with a mask to isolate those bits.
     * Example for Movement Type: (properties >> SHIFT_MOVEMENT_TYPE) & MASK_MOVEMENT_TYPE
     *
     * To write a value, we first clear the bits for that property using a negated mask,
     * then set the new value using a bitwise OR.
     * Example for Movement Type:
     * properties &= ~(MASK_MOVEMENT_TYPE << SHIFT_MOVEMENT_TYPE); // Clear bits
     * properties |= (type.ordinal() & MASK_MOVEMENT_TYPE) << SHIFT_MOVEMENT_TYPE; // Set new value
     *
     * For boolean flags, we use bitwise OR to set a flag (properties |= FLAG_...) and
     * bitwise AND with a negated flag to clear it (properties &= ~FLAG_...).
     */
    @SuppressWarnings("GrazieInspection")
    protected static final TrackedData<ItemStack> PARAM_SADDLE_ITEM = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.ITEM_STACK);
    protected static final TrackedData<ItemStack> PARAM_CHEST_ITEM = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.ITEM_STACK);
    protected static final TrackedData<ItemStack> PARAM_WEAPON_ITEM = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.ITEM_STACK);
    protected static final TrackedData<ItemStack> PARAM_HEAD_ITEM = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.ITEM_STACK);
    protected static final TrackedData<ItemStack> PARAM_LEGS_ITEM = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.ITEM_STACK);
    protected static final TrackedData<ItemStack> PARAM_FEET_ITEM = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final int MASK_COLOR = 0xF; // 0b1111 (4 bits)
    private static final int MASK_MOVEMENT_TYPE = 0x3; // 0b11 (2 bits)
    private static final int MASK_COLLAR_COLOR = 0x1F; // 0b11111 (5 bits)

    private static final int SHIFT_COLOR = 0; // Bits 0-3
    private static final int SHIFT_MOVEMENT_TYPE = 4; // Bits 4-5 (shifted by 4)
    private static final int SHIFT_COLLAR_COLOR = 6; // Bits 6-10 (shifted by 6)

    // New ability flags packed into PARAM_CHOCOBO_PROPERTIES
    // Ability flags start after the previous properties. 4 (color) + 2 (movement) + 5 (collar) = 11
    private static final int SHIFT_ABILITIES_START = 11;
    protected static final int FLAG_FLAME_BLOOD     = 1 << (SHIFT_ABILITIES_START); // Bit 11
    protected static final int FLAG_WATER_BREATH    = 1 << (SHIFT_ABILITIES_START + 1); // Bit 12
    protected static final int FLAG_WITHER_IMMUNE   = 1 << (SHIFT_ABILITIES_START + 2); // Bit 13
    protected static final int FLAG_POISON_IMMUNE   = 1 << (SHIFT_ABILITIES_START + 3); // Bit 14
    protected static final int FLAG_IS_MALE         = 1 << (SHIFT_ABILITIES_START + 4); // Bit 15
    protected static final int FLAG_FROM_EGG        = 1 << (SHIFT_ABILITIES_START + 5); // Bit 16
    protected static final int FLAG_CAN_FLY         = 1 << (SHIFT_ABILITIES_START + 6); // Bit 17
    // These are the bitmasks for the legacy AbilityMask byte, used for NBT serialization.
    protected static final byte ABILITY_MASK_FLAME_BLOOD   = 0b00000001;
    protected static final byte ABILITY_MASK_WATER_BREATH  = 0b00000010;
    protected static final byte ABILITY_MASK_WITHER_IMMUNE = 0b00000100;
    protected static final byte ABILITY_MASK_POISON_IMMUNE = 0b00001000;
    protected static final byte ABILITY_MASK_IS_MALE       = 0b00010000;
    protected static final byte ABILITY_MASK_FROM_EGG      = 0b00100000;
    protected static final byte ABILITY_MASK_CAN_FLY       = 0b01000000;

    protected final static TrackedData<Integer> PARAM_GENERATION = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> PARAM_SCALE = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<BlockPos> PARAM_LEASH_BLOCK = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.BLOCK_POS);
    protected static final TrackedData<Integer> PARAM_LEASH_LENGTH = DataTracker.registerData(AbstractChocobo.class, TrackedDataHandlerRegistry.INTEGER);

    // Hardcoded Chocobo Values
    public static final int tier_one_chocobo_inv_slot_count = 15; // 3*5
    public static final int tier_two_chocobo_inv_slot_count = 45; //5*9
    public final int top_tier_chocobo_inv_slot_count = tier_two_chocobo_inv_slot_count;

    protected static final UUID CHOCOBO_CHEST_ARMOR_MOD_UUID = UUID.fromString("c03d8021-8839-4377-ac23-ed723ece6454");
    protected static final UUID CHOCOBO_CHEST_ARMOR_TOUGH_MOD_UUID = UUID.fromString("f7dcb185-7182-4a28-83ae-d1a2de9c022d");
    protected static final UUID CHOCOBO_WEAPON_DAM_MOD_UUID = UUID.fromString("b9f0dc43-15a7-49f5-815c-915322c30402");
    protected static final UUID CHOCOBO_WEAPON_SPD_MOD_UUID = UUID.fromString("46c84540-15f7-4f22-9da9-ebc23d2353af");
    protected static final UUID CHOCOBO_SPRINTING_BOOST_ID = UUID.fromString("03ba3167-393e-4362-92b8-909841047640");
    protected static final UUID CHOCOBO_HEAD_ARMOR_MOD_UUID = UUID.fromString("d03d8021-8839-4377-ac23-ed723ece6454");
    protected static final UUID CHOCOBO_HEAD_ARMOR_TOUGH_MOD_UUID = UUID.fromString("e7dcb185-7182-4a28-83ae-d1a2de9c022d");
    protected static final UUID CHOCOBO_LEGS_ARMOR_MOD_UUID = UUID.fromString("f03d8021-8839-4377-ac23-ed723ece6454");
    protected static final UUID CHOCOBO_LEGS_ARMOR_TOUGH_MOD_UUID = UUID.fromString("07dcb185-7182-4a28-83ae-d1a2de9c022d");
    protected static final UUID CHOCOBO_FEET_ARMOR_MOD_UUID = UUID.fromString("103d8021-8839-4377-ac23-ed723ece6454");
    protected static final UUID CHOCOBO_FEET_ARMOR_TOUGH_MOD_UUID = UUID.fromString("17dcb185-7182-4a28-83ae-d1a2de9c022d");
    protected static final UUID CHOCOBO_CHEST_ARMOR_KNOCKBACK_MOD_UUID = UUID.fromString("c13d8021-8839-4377-ac23-ed723ece6454");
    protected static final UUID CHOCOBO_HEAD_ARMOR_KNOCKBACK_MOD_UUID = UUID.fromString("d13d8021-8839-4377-ac23-ed723ece6454");
    protected static final UUID CHOCOBO_LEGS_ARMOR_KNOCKBACK_MOD_UUID = UUID.fromString("f13d8021-8839-4377-ac23-ed723ece6454");
    protected static final UUID CHOCOBO_FEET_ARMOR_KNOCKBACK_MOD_UUID = UUID.fromString("113d8021-8839-4377-ac23-ed723ece6454");
    protected static final UUID CHOCOBO_WEAPON_KNOCKBACK_MOD_UUID = UUID.fromString("b1f0dc43-15a7-49f5-815c-915322c30402");
    protected static final UUID CHOCOBO_ARMOR_SET_ARMOR_BONUS_UUID = UUID.fromString("a5a2e443-7346-4aa2-9b5a-2b8e9a7e4a4c");
    protected static final UUID CHOCOBO_ARMOR_SET_TOUGHNESS_BONUS_UUID = UUID.fromString("b5a2e443-7346-4aa2-9b5a-2b8e9a7e4a4c");
    protected static final UUID CHOCOBO_ARMOR_SET_KNOCKBACK_BONUS_UUID = UUID.fromString("c5a2e443-7346-4aa2-9b5a-2b8e9a7e4a4c");
    protected static final UUID CHOCOBO_ARMOR_SET_WEAPON_BONUS_UUID = UUID.fromString("d5a2e443-7346-4aa2-9b5a-2b8e9a7e4a4c");
    protected static final EntityAttributeModifier CHOCOBO_SPRINTING_SPEED_BOOST = (new EntityAttributeModifier(CHOCOBO_SPRINTING_BOOST_ID, "Chocobo sprinting speed boost", 0.5, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));


    private boolean hasCheckedArmorSetBonus = false;

    protected AbstractChocobo(EntityType<? extends TameableEntity> entityType, World world) { super(entityType, world); }

    public void travel(@NotNull Vec3d travelInput) {
        if (this.isAlive()) {
            if (this.isSubmergedInWater() && !this.hasPassengers()) {
                Vec3d velocity = this.getVelocity();
                this.setVelocity(velocity.x, .5, velocity.z);
            }
            super.travel(travelInput);
        }
    }
    protected EntityNavigation createNavigation(World world) { return new ChocoboAmphibiousSwimNavigation(this, world); }

    // Initialization of DataTracker for all Chocobo types
    protected void initDataTracker() {
        this.dataTracker.startTracking(PARAM_CHOCOBO_PROPERTIES, 0);
        this.dataTracker.startTracking(PARAM_GENERATION, 0);
        this.dataTracker.startTracking(PARAM_SCALE, 0);
        this.dataTracker.startTracking(DATA_REMAINING_ANGER_TIME, 0);
        this.dataTracker.startTracking(PARAM_LEASH_BLOCK, new BlockPos(0, 50000, 0));
        this.dataTracker.startTracking(PARAM_LEASH_LENGTH, 0);
        this.dataTracker.startTracking(PARAM_SADDLE_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_CHEST_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_WEAPON_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_HEAD_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_LEGS_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_FEET_ITEM, ItemStack.EMPTY);
        super.initDataTracker();
    }
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.lastSaddleStack = this.getSaddle().copy();
    }
    public void onTrackedDataSet(TrackedData<?> data) {
        if (!this.firstUpdate) {
            if (PARAM_SADDLE_ITEM.equals(data)) {
                ItemStack currentSaddle = this.getSaddle();
                if (!this.getWorld().isClient() && !ItemStack.areEqual(currentSaddle, this.lastSaddleStack)) {
                    this.onSaddleChanged();
                }
                this.lastSaddleStack = currentSaddle.copy();
            } else if (PARAM_CHEST_ITEM.equals(data)) {
                this.setChocoboChestArmorStats(this.getChestArmor());
            } else if (PARAM_WEAPON_ITEM.equals(data)) {
                this.setChocoboWeaponStats(this.getWeapon());
            } else if (PARAM_HEAD_ITEM.equals(data)) {
                this.setChocoboHeadArmorStats(this.getHeadArmor());
            } else if (PARAM_LEGS_ITEM.equals(data)) {
                this.setChocoboLegsArmorStats(this.getLegsArmor());
            } else if (PARAM_FEET_ITEM.equals(data)) {
                this.setChocoboFeetArmorStats(this.getFeetArmor());
            }
        }
        super.onTrackedDataSet(data);
    }
    public void onSaddleChanged() { }

    // hook for Chocobo types, left for override
    public boolean isSitting() { return false; }
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) { return false; }
    public boolean isPersistent() { return this.isTamed() || this.isCustomNameVisible(); }
    public boolean cannotDespawn() { return this.hasVehicle() || this.isPersistent(); }
    public boolean canImmediatelyDespawn(double pDistanceToClosestPlayer) { return !this.cannotDespawn(); }
    public boolean canUsePortals() { return true; }
    public boolean isDisallowedInPeaceful() { return false; }
    // Method to get World, Used by 'default public LivingEntity getOwner()' to get Owner by UUID in the world.
    public EntityView method_48926() { return super.getWorld(); }
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
    public void pubUpdateWaterState() { if (this.isTouchingWater()) { this.updateWaterState(); } }
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
    public void onDeath(DamageSource source) {
        // Left in for unique Chocobo Checks able to be done in AbstractChocobo
        super.onDeath(source);
    }
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        // Custom drop logic for Chocobo Items, Overridden to prevent dropping items that are not Chocobo related.
    }
    // applyDamageEffects is used to apply effects after the damage is applied, such as dropping items or applying potion effects.
    public void applyDamageEffects(LivingEntity attacker, Entity target) {
        // Left uncompacted for readability and future expansion
        super.applyDamageEffects(attacker, target);
    }
    public boolean canBeLeashedBy(PlayerEntity player) { return false; }
    protected void dropLoot(@NotNull DamageSource source, boolean causedByPlayer) {
        // Left uncompacted for readability and future expansion
        super.dropLoot(source, causedByPlayer);
    }
    protected SoundEvent getAmbientSound() { return AMBIENT_SOUND; }
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) { return AMBIENT_SOUND; }
    protected SoundEvent getDeathSound() { return AMBIENT_SOUND; }
    protected float getSoundVolume() { return .6f; }

    public ItemEntity dropItem(@NotNull ItemConvertible item) {
        Item itemToDrop = item.asItem();
        if (itemToDrop == null || itemToDrop == Items.AIR || itemToDrop instanceof ChocoboArmorItems || itemToDrop instanceof ChocoboWeaponItems || itemToDrop instanceof ChocoboSaddleItem) { return null; }
        return super.dropItem(item);
    }
    public void tick() {
        if (!this.getWorld().isClient() && !this.hasCheckedArmorSetBonus) {
            this.updateArmorSetBonus();
            this.hasCheckedArmorSetBonus = true;
        }
        if (this.getWorld().isClient) { super.tick(); return; }
        if (this.canWalkOnWater() && this.isTouchingWater() && !this.hasVehicle() && !this.hasPassengers()) { this.ticksOnWater++; }
        else { this.ticksOnWater = 0; }

        if (this.age % 100 == 0){
            // prevents frequent checks.
            if (this.isWeaponArmed()) {
                Box box = this.getBoundingBox();
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                for (int i = MathHelper.floor(box.minX); i < MathHelper.ceil(box.maxX); ++i) {
                    for (int j = MathHelper.floor(box.minY); j < MathHelper.ceil(box.maxY); ++j) {
                        for (int k = MathHelper.floor(box.minZ); k < MathHelper.ceil(box.maxZ); ++k) {
                            mutable.set(i, j, k);
                            BlockState blockState = this.getWorld().getBlockState(mutable);
                            if (blockState.isOf(Blocks.COBWEB)) {
                                // 50% chance to break with silk touch, 50% chance to break normally
                                boolean useSilkTouch = RandomHelper.random.nextBoolean();
                                if (useSilkTouch) {
                                    // Break with silk touch - drop the cobweb item
                                    this.getWorld().breakBlock(mutable, false, this);
                                    this.getWorld().spawnEntity(new net.minecraft.entity.ItemEntity(
                                            this.getWorld(),
                                            mutable.getX() + 0.5,
                                            mutable.getY() + 0.5,
                                            mutable.getZ() + 0.5,
                                            new ItemStack(Blocks.COBWEB)
                                    ));
                                } else {
                                    // Break normally (no drops)
                                    this.getWorld().breakBlock(mutable, true, this);
                                }
                            }
                        }
                    }
                }
            }
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
        if (!this.hasPassengers()) { return true; }
        if (this.isWaterBreathing()) { return !this.isArmored() && !this.isWeaponArmed(); }
        return true;
    }
    public boolean isWaterBloodChocobo() { return (this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES) & FLAG_WATER_BREATH) != 0; }
    public boolean isWitherImmune() { return (this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES) & FLAG_WITHER_IMMUNE) != 0; }
    public boolean isPoisonImmune() { return (this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES) & FLAG_POISON_IMMUNE) != 0; }
    public int getChocoboScale() { return this.dataTracker.get(PARAM_SCALE); }
    public float getChocoboScaleMod() { return ScaleMod(getChocoboScale()); }
    public boolean isMale() { return (this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES) & FLAG_IS_MALE) != 0; }
    public boolean fromEgg() { return (this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES) & FLAG_FROM_EGG) != 0; }
    public boolean canFly() { return (this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES) & FLAG_CAN_FLY) != 0; }

    // getters for Chocobo properties
    public MovementType getMovementType() { return MovementType.values()[(this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES) >> SHIFT_MOVEMENT_TYPE) & MASK_MOVEMENT_TYPE]; }
    public boolean isFireImmune() { return this.isFlameBlood() || super.isFireImmune(); }
    public boolean isFlameBlood() { return (this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES) & FLAG_FLAME_BLOOD) != 0; }
    public byte getChocoboAbilityMask() {
        byte mask = 0;
        int properties = this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES);
        if ((properties & FLAG_FLAME_BLOOD) != 0) mask |= ABILITY_MASK_FLAME_BLOOD;
        if ((properties & FLAG_WATER_BREATH) != 0) mask |= ABILITY_MASK_WATER_BREATH;
        if ((properties & FLAG_WITHER_IMMUNE) != 0) mask |= ABILITY_MASK_WITHER_IMMUNE;
        if ((properties & FLAG_POISON_IMMUNE) != 0) mask |= ABILITY_MASK_POISON_IMMUNE;
        if ((properties & FLAG_IS_MALE) != 0) mask |= ABILITY_MASK_IS_MALE;
        if ((properties & FLAG_FROM_EGG) != 0) mask |= ABILITY_MASK_FROM_EGG;
        if ((properties & FLAG_CAN_FLY) != 0) mask |= ABILITY_MASK_CAN_FLY;
        return mask;
    }
    public boolean isSaddled() { return !this.getSaddle().isEmpty(); }
    public boolean isArmored() { return isChestArmored() || isHeadArmored() || isLegsArmored() || isFeetArmored(); }
    public boolean isWeaponArmed() { return !this.getWeapon().isEmpty(); }
    public boolean isHeadArmored() { return !this.getHeadArmor().isEmpty(); }
    public boolean isLegsArmored() { return !this.getLegsArmor().isEmpty(); }
    public boolean isFeetArmored() { return !this.getFeetArmor().isEmpty(); }
    public boolean isChestArmored() { return !this.getChestArmor().isEmpty(); }
    public int getTicksOnWater() { return this.ticksOnWater; }
    public ItemStack getSaddle() { return this.dataTracker.get(PARAM_SADDLE_ITEM); }
    public ItemStack getWeapon() { return this.dataTracker.get(PARAM_WEAPON_ITEM); }
    public ItemStack getChestArmor() { return this.dataTracker.get(PARAM_CHEST_ITEM); }
    public ItemStack getHeadArmor() { return this.dataTracker.get(PARAM_HEAD_ITEM); }
    public ItemStack getLegsArmor() { return this.dataTracker.get(PARAM_LEGS_ITEM); }
    public ItemStack getFeetArmor() { return this.dataTracker.get(PARAM_FEET_ITEM); }
    public void setSaddle(ItemStack pStack) { this.dataTracker.set(PARAM_SADDLE_ITEM, pStack); }
    public void setWeapon(ItemStack pStack) { this.dataTracker.set(PARAM_WEAPON_ITEM, pStack); }
    public void setChestArmor(ItemStack pStack) { this.dataTracker.set(PARAM_CHEST_ITEM, pStack); }
    public void setHeadArmor(ItemStack pStack) { this.dataTracker.set(PARAM_HEAD_ITEM, pStack); }
    public void setLegsArmor(ItemStack pStack) { this.dataTracker.set(PARAM_LEGS_ITEM, pStack); }
    public void setFeetArmor(ItemStack pStack) { this.dataTracker.set(PARAM_FEET_ITEM, pStack); }
    public boolean canBreatheInWater() {
        // TODO -> logic to enable disable water breathing (controlling walking on water) on a temporary condition.
        return this.isWaterBreathing();
    }
    public int getGeneration() { return this.dataTracker.get(PARAM_GENERATION); }
    public String getGenerationString() {
        int gen = this.getGeneration();
        return Integer.toString(gen);
    }
    public ChocoboColor getChocoboColor() { return ChocoboColor.getChocoboColorFromID((this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES) >> SHIFT_COLOR) & MASK_COLOR, true); }
    public int getAngerTime() { return this.remainingPersistentAngerTime; }
    public UUID getAngryAt() { return this.persistentAngerTarget; }
    public Integer getCollarColor() { return (this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES) >> SHIFT_COLLAR_COLOR) & MASK_COLLAR_COLOR; }
    public BlockPos getLeashSpot() { return this.dataTracker.get(PARAM_LEASH_BLOCK); }
    public boolean canWonder() { return this.getMovementType() == MovementType.WANDER; }
    public boolean isNoRoam() { return this.getMovementType() == MovementType.STANDSTILL; }
    public boolean followOwner() { return this.getMovementType() == MovementType.FOLLOW_OWNER; }
    public boolean followLure() { return this.getMovementType() == MovementType.FOLLOW_LURE; }
    public int getLeashDistance() { return this.dataTracker.get(PARAM_LEASH_LENGTH); }
    public int chocoStatMod() { return ChocoboConfig.DEFAULT_GEAR_MOD.get(); }
    
    // setters for Chocobo properties
    public void setMale(boolean isMale) {
        int properties = this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES);
        if (isMale) { properties |= FLAG_IS_MALE; }
        else { properties &= ~FLAG_IS_MALE; }
        this.dataTracker.set(PARAM_CHOCOBO_PROPERTIES, properties);
    }
    public void setFromEgg(boolean fromEgg) {
        int properties = this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES);
        if (fromEgg) { properties |= FLAG_FROM_EGG; }
        else { properties &= ~FLAG_FROM_EGG; }
        this.dataTracker.set(PARAM_CHOCOBO_PROPERTIES, properties);
    }
    public void setFlame(boolean flame) {
        int properties = this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES);
        if (flame) { properties |= FLAG_FLAME_BLOOD; }
        else { properties &= ~FLAG_FLAME_BLOOD; }
        this.dataTracker.set(PARAM_CHOCOBO_PROPERTIES, properties);
    }
    public void setWaterBreath(boolean waterBreath) {
        int properties = this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES);
        if (waterBreath) { properties |= FLAG_WATER_BREATH; }
        else { properties &= ~FLAG_WATER_BREATH; }
        this.dataTracker.set(PARAM_CHOCOBO_PROPERTIES, properties);
        this.updateWaterNavPenalties();
        this.updateNavigationAndMoveControl(false); // Update based on new innate ability state
    }
    public void setWitherImmune(boolean witherImmune) {
        int properties = this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES);
        if (witherImmune) { properties |= FLAG_WITHER_IMMUNE; }
        else { properties &= ~FLAG_WITHER_IMMUNE; }
        this.dataTracker.set(PARAM_CHOCOBO_PROPERTIES, properties);
    }
    public void setPoisonImmune(boolean poisonImmune) {
        int properties = this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES);
        if (poisonImmune) { properties |= FLAG_POISON_IMMUNE; }
        else { properties &= ~FLAG_POISON_IMMUNE; }
        this.dataTracker.set(PARAM_CHOCOBO_PROPERTIES, properties);
    }
    public void setCanFly(boolean canFly) {
        int properties = this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES);
        if (canFly) { properties |= FLAG_CAN_FLY; }
        else { properties &= ~FLAG_CAN_FLY; }
        this.dataTracker.set(PARAM_CHOCOBO_PROPERTIES, properties);
    }
    public void setChocoboAbilitiesFromMask(byte mask) {
        int properties = this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES);
        // Clear old ability flags first
        properties &= ~(FLAG_FLAME_BLOOD | FLAG_WATER_BREATH | FLAG_WITHER_IMMUNE | FLAG_POISON_IMMUNE | FLAG_IS_MALE | FLAG_FROM_EGG | FLAG_CAN_FLY);

        if ((mask & ABILITY_MASK_FLAME_BLOOD) != 0) { properties |= FLAG_FLAME_BLOOD; }
        if ((mask & ABILITY_MASK_WATER_BREATH) != 0) { properties |= FLAG_WATER_BREATH; }
        if ((mask & ABILITY_MASK_WITHER_IMMUNE) != 0) { properties |= FLAG_WITHER_IMMUNE; }
        if ((mask & ABILITY_MASK_POISON_IMMUNE) != 0) { properties |= FLAG_POISON_IMMUNE; }
        if ((mask & ABILITY_MASK_IS_MALE) != 0) { properties |= FLAG_IS_MALE; }
        if ((mask & ABILITY_MASK_FROM_EGG) != 0) { properties |= FLAG_FROM_EGG; }
        if ((mask & ABILITY_MASK_CAN_FLY) != 0) { properties |= FLAG_CAN_FLY; }

        this.dataTracker.set(PARAM_CHOCOBO_PROPERTIES, properties);
        this.updateNavigationAndMoveControl(false); // Update based on new ability mask state
    }
    public void setChocoboScale(boolean isMale, int overrideValue, boolean override) {
        int scale;
        if (override) { scale = overrideValue; } else { scale = setChocoScale(isMale); }
        this.dataTracker.set(PARAM_SCALE, scale);
    }
    public void setMovementType(@NotNull MovementType type) {
        int properties = this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES);
        properties &= ~(MASK_MOVEMENT_TYPE << SHIFT_MOVEMENT_TYPE);
        properties |= (type.ordinal() & MASK_MOVEMENT_TYPE) << SHIFT_MOVEMENT_TYPE;
        this.dataTracker.set(PARAM_CHOCOBO_PROPERTIES, properties);
        setMovementAiByType(type);
    }
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
        setMovementType(type);
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        if (this.getWorld().isClient()) {
            super.equipStack(slot, stack);
            return;
        }
        switch (slot) {
            case MAINHAND -> {
                if (!ItemStack.areEqual(stack, this.getWeapon())) { this.setWeapon(stack.copy()); }
            }
            case CHEST -> {
                if (!ItemStack.areEqual(stack, this.getChestArmor())) { this.setChestArmor(stack.copy()); }
            }
            case HEAD -> {
                if (!ItemStack.areEqual(stack, this.getHeadArmor())) { this.setHeadArmor(stack.copy()); }
            }
            case LEGS -> {
                if (!ItemStack.areEqual(stack, this.getLegsArmor())) { this.setLegsArmor(stack.copy()); }
            }
            case FEET -> {
                if (!ItemStack.areEqual(stack, this.getFeetArmor())) { this.setFeetArmor(stack.copy()); }
            }
            default -> super.equipStack(slot, stack);
        }
    }
    private void silentUpdateArmorSetBonus(EquipmentSlot slot, ItemStack stack) {
        this.updateArmorSetBonus();
        super.equipStack(slot, stack);
    }
    public void setChocoboChestArmorStats(ItemStack pStack) {
        if (!this.getWorld().isClient()) {
            // Remove all existing modifiers first
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).removeModifier(CHOCOBO_CHEST_ARMOR_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).removeModifier(CHOCOBO_CHEST_ARMOR_TOUGH_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).removeModifier(CHOCOBO_CHEST_ARMOR_KNOCKBACK_MOD_UUID);

            if (pStack.getItem() instanceof ChocoboArmorItems armorItem) {
                this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
                ArmorMaterial material = armorItem.getMaterial();

                double armorValue = (double) ChocoboArmorItems.getTotalDefense(material, armorItem.getType()) * chocoStatMod();
                if (armorValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_CHEST_ARMOR_MOD_UUID, "Chocobo Armor Bonus", armorValue, EntityAttributeModifier.Operation.ADDITION));
                }

                double toughnessValue = ChocoboArmorItems.getTotalToughness(material) * chocoStatMod();
                if (toughnessValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_CHEST_ARMOR_TOUGH_MOD_UUID, "Chocobo Armor Toughness", toughnessValue, EntityAttributeModifier.Operation.ADDITION));
                }

                double knockbackValue = ChocoboArmorItems.getTotalKnockbackResistance(material) * chocoStatMod();
                if (knockbackValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_CHEST_ARMOR_KNOCKBACK_MOD_UUID, "Chocobo Armor Knockback Resistance", knockbackValue, EntityAttributeModifier.Operation.ADDITION));
                }
            }
            this.silentUpdateArmorSetBonus(EquipmentSlot.CHEST, pStack);
        }
    }
    public void setChocoboWeaponStats(ItemStack pStack) {
        if (!this.getWorld().isClient()) {
            // Remove all existing modifiers first
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).removeModifier(CHOCOBO_WEAPON_DAM_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED)).removeModifier(CHOCOBO_WEAPON_SPD_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).removeModifier(CHOCOBO_WEAPON_KNOCKBACK_MOD_UUID);

            if (pStack.getItem() instanceof ChocoboWeaponItems weaponItem) {
                ToolMaterial material = weaponItem.getMaterial();

                double damageValue = (double) ChocoboWeaponItems.getTotalAttackDamage(material) * chocoStatMod();
                if (damageValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_WEAPON_DAM_MOD_UUID, "Chocobo Attack Bonus", damageValue, EntityAttributeModifier.Operation.ADDITION));
                }

                double speedValue = weaponItem.getAttackSpeed() * chocoStatMod();
                if (speedValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_WEAPON_SPD_MOD_UUID, "Chocobo Attack Speed Bonus", speedValue, EntityAttributeModifier.Operation.ADDITION));
                }
            }
            super.equipStack(EquipmentSlot.MAINHAND, pStack);
        }
    }
    public void setChocoboHeadArmorStats(ItemStack pStack) {
        if (!this.getWorld().isClient()) {
            // Remove all existing modifiers first
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).removeModifier(CHOCOBO_HEAD_ARMOR_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).removeModifier(CHOCOBO_HEAD_ARMOR_TOUGH_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).removeModifier(CHOCOBO_HEAD_ARMOR_KNOCKBACK_MOD_UUID);

            if (pStack.getItem() instanceof ChocoboArmorItems armorItem) {
                this.setEquipmentDropChance(EquipmentSlot.HEAD, 0.0F);
                ArmorMaterial material = armorItem.getMaterial();

                double armorValue = (double) ChocoboArmorItems.getTotalDefense(material, armorItem.getType()) * chocoStatMod();
                if (armorValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_HEAD_ARMOR_MOD_UUID, "Chocobo Head Armor Bonus", armorValue, EntityAttributeModifier.Operation.ADDITION));
                }

                double toughnessValue = ChocoboArmorItems.getTotalToughness(material) * chocoStatMod();
                if (toughnessValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_HEAD_ARMOR_TOUGH_MOD_UUID, "Chocobo Head Armor Toughness", toughnessValue, EntityAttributeModifier.Operation.ADDITION));
                }

                double knockbackValue = ChocoboArmorItems.getTotalKnockbackResistance(material) * chocoStatMod();
                if (knockbackValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_HEAD_ARMOR_KNOCKBACK_MOD_UUID, "Chocobo Head Armor Knockback Resistance", knockbackValue, EntityAttributeModifier.Operation.ADDITION));
                }
            }
            this.silentUpdateArmorSetBonus(EquipmentSlot.HEAD, pStack);
        }
    }
    public void setChocoboLegsArmorStats(ItemStack pStack) {
        if (!this.getWorld().isClient()) {
            // Remove all existing modifiers first
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).removeModifier(CHOCOBO_LEGS_ARMOR_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).removeModifier(CHOCOBO_LEGS_ARMOR_TOUGH_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).removeModifier(CHOCOBO_LEGS_ARMOR_KNOCKBACK_MOD_UUID);

            if (pStack.getItem() instanceof ChocoboArmorItems armorItem) {
                this.setEquipmentDropChance(EquipmentSlot.LEGS, 0.0F);
                ArmorMaterial material = armorItem.getMaterial();

                double armorValue = (double) ChocoboArmorItems.getTotalDefense(material, armorItem.getType()) * chocoStatMod();
                if (armorValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_LEGS_ARMOR_MOD_UUID, "Chocobo Legs Armor Bonus", armorValue, EntityAttributeModifier.Operation.ADDITION));
                }

                double toughnessValue = ChocoboArmorItems.getTotalToughness(material) * chocoStatMod();
                if (toughnessValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_LEGS_ARMOR_TOUGH_MOD_UUID, "Chocobo Legs Armor Toughness", toughnessValue, EntityAttributeModifier.Operation.ADDITION));
                }

                double knockbackValue = ChocoboArmorItems.getTotalKnockbackResistance(material) * chocoStatMod();
                if (knockbackValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_LEGS_ARMOR_KNOCKBACK_MOD_UUID, "Chocobo Legs Armor Knockback Resistance", knockbackValue, EntityAttributeModifier.Operation.ADDITION));
                }
            }
            this.silentUpdateArmorSetBonus(EquipmentSlot.LEGS, pStack);
        }
    }
    public void setChocoboFeetArmorStats(ItemStack pStack) {
        if (!this.getWorld().isClient()) {
            // Remove all existing modifiers first
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).removeModifier(CHOCOBO_FEET_ARMOR_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).removeModifier(CHOCOBO_FEET_ARMOR_TOUGH_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).removeModifier(CHOCOBO_FEET_ARMOR_KNOCKBACK_MOD_UUID);

            if (pStack.getItem() instanceof ChocoboArmorItems armorItem) {
                this.setEquipmentDropChance(EquipmentSlot.FEET, 0.0F);
                ArmorMaterial material = armorItem.getMaterial();

                double armorValue = (double) ChocoboArmorItems.getTotalDefense(material, armorItem.getType()) * chocoStatMod();
                if (armorValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_FEET_ARMOR_MOD_UUID, "Chocobo Feet Armor Bonus", armorValue, EntityAttributeModifier.Operation.ADDITION));
                }

                double toughnessValue = ChocoboArmorItems.getTotalToughness(material) * chocoStatMod();
                if (toughnessValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_FEET_ARMOR_TOUGH_MOD_UUID, "Chocobo Feet Armor Toughness", toughnessValue, EntityAttributeModifier.Operation.ADDITION));
                }

                double knockbackValue = ChocoboArmorItems.getTotalKnockbackResistance(material) * chocoStatMod();
                if (knockbackValue != 0) {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE))
                            .addPersistentModifier(new EntityAttributeModifier(CHOCOBO_FEET_ARMOR_KNOCKBACK_MOD_UUID, "Chocobo Feet Armor Knockback Resistance", knockbackValue, EntityAttributeModifier.Operation.ADDITION));
                }
            }
            this.silentUpdateArmorSetBonus(EquipmentSlot.FEET, pStack);
        }
    }
    private void updateArmorSetBonus() {
        if (this.getWorld().isClient()) { return; }
        EntityAttributeInstance armorAttr = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        EntityAttributeInstance toughnessAttr = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        EntityAttributeInstance knockbackAttr = this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
        EntityAttributeInstance attackAttr = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);

        if (armorAttr != null) { armorAttr.removeModifier(CHOCOBO_ARMOR_SET_ARMOR_BONUS_UUID); }
        if (toughnessAttr != null) { toughnessAttr.removeModifier(CHOCOBO_ARMOR_SET_TOUGHNESS_BONUS_UUID); }
        if (knockbackAttr != null) { knockbackAttr.removeModifier(CHOCOBO_ARMOR_SET_KNOCKBACK_BONUS_UUID); }
        if (attackAttr != null) { attackAttr.removeModifier(CHOCOBO_ARMOR_SET_WEAPON_BONUS_UUID); }

        ItemStack head = getHeadArmor();
        ItemStack chest = getChestArmor();
        ItemStack legs = getLegsArmor();
        ItemStack feet = getFeetArmor();

        if (head.isEmpty() || chest.isEmpty() || legs.isEmpty() || feet.isEmpty()) { return; }

        if (head.getItem() instanceof ChocoboArmorItems headArmor &&
                chest.getItem() instanceof ChocoboArmorItems chestArmor &&
                legs.getItem() instanceof ChocoboArmorItems legsArmor &&
                feet.getItem() instanceof ChocoboArmorItems feetArmor) {

            Integer headTier = ChocoboArmorItems.getTier(headArmor.getMaterial());
            Integer chestTier = ChocoboArmorItems.getTier(chestArmor.getMaterial());
            Integer legsTier = ChocoboArmorItems.getTier(legsArmor.getMaterial());
            Integer feetTier = ChocoboArmorItems.getTier(feetArmor.getMaterial());

            if (headTier == null || chestTier == null || legsTier == null || feetTier == null) { return; }

            int minTier = Collections.min(Arrays.asList(headTier, chestTier, legsTier, feetTier));

            if (minTier >= 6) { // Reinforced Diamond or higher
                float bonusPercentage;
                float damageBonus;

                if (minTier == 6) { // Reinforced Diamond
                    bonusPercentage = 0.10f;
                    damageBonus = 2.0f;
                } else if (minTier == 7) { // Netherite
                    bonusPercentage = 0.15f;
                    damageBonus = 2.5f;
                } else if (minTier == 8) { // Reinforced Netherite
                    bonusPercentage = 0.20f;
                    damageBonus = 3.0f;
                } else { // Gilded Netherite
                    bonusPercentage = 0.25f;
                    damageBonus = 4.0f;
                }

                if (armorAttr != null) {
                    armorAttr.addPersistentModifier(new EntityAttributeModifier(CHOCOBO_ARMOR_SET_ARMOR_BONUS_UUID, "Set Bonus", bonusPercentage, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                if (toughnessAttr != null) {
                    toughnessAttr.addPersistentModifier(new EntityAttributeModifier(CHOCOBO_ARMOR_SET_TOUGHNESS_BONUS_UUID, "Set Bonus", bonusPercentage, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                if (knockbackAttr != null) {
                    knockbackAttr.addPersistentModifier(new EntityAttributeModifier(CHOCOBO_ARMOR_SET_KNOCKBACK_BONUS_UUID, "Set Bonus", bonusPercentage, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                }

                if (attackAttr != null) {
                    attackAttr.addPersistentModifier(new EntityAttributeModifier(CHOCOBO_ARMOR_SET_WEAPON_BONUS_UUID, "Armor Set Weapon Boost", damageBonus, EntityAttributeModifier.Operation.ADDITION));
                }
            }
        }
    }
    public void setGeneration(int value) { this.dataTracker.set(PARAM_GENERATION, value); }
    public void setAngerTime(int angerTime) { this.remainingPersistentAngerTime = angerTime; }
    public void setAngryAt(@Nullable UUID angryAt) { this.persistentAngerTarget = angryAt; }
    public void setChocoboColor(@NotNull ChocoboColor color) {
        int properties = this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES);
        properties &= ~(MASK_COLOR << SHIFT_COLOR);
        properties |= (color.getChocoboColorID() & MASK_COLOR) << SHIFT_COLOR;
        this.dataTracker.set(PARAM_CHOCOBO_PROPERTIES, properties);
    }
    public void setCollarColor(Integer color) {
        int properties = this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES);
        properties &= ~(MASK_COLLAR_COLOR << SHIFT_COLLAR_COLOR);
        properties |= (color & MASK_COLLAR_COLOR) << SHIFT_COLLAR_COLOR;
        this.dataTracker.set(PARAM_CHOCOBO_PROPERTIES, properties);
    }
    protected void setLeashedDistance(int distance) { this.dataTracker.set(PARAM_LEASH_LENGTH, distance); }
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
            amountIncrease *= 2;
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
    private void statPlus(EntityAttribute stat, double max, String key, @NotNull PlayerEntity playerEntity, Chocobo chocobo, int amountIncrease) {
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
        double followRange = alertingChocobo.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE); // Get the follow range from the chocobo's attributes

        Box alertBox = Box.from(alertingChocobo.getPos()).expand(followRange, 15.0D, followRange);
        List<Chocobo> nearbyChocobos = alertingChocobo.getWorld().getNonSpectatingEntities(Chocobo.class, alertBox);

        if (forgivenPlayer != null) {
            for (Chocobo chocobo : nearbyChocobos) { chocobo.forgive(forgivenPlayer); } // Forgive all chocobos in the area if a player is specified, kept despite cpu cost to prevent player from being attacked by all chocobos in the area after being forgiven
        } else {
            LivingEntity attacker = alertingChocobo.getTarget();
            if (attacker == null) { return; }

            // Setup lists for filtering
            List<Chocobo> filteredChocobos = new ArrayList<>(nearbyChocobos);
            List<Chocobo> tempList = new ArrayList<>();

            // Filter 1: Basics for all chocobos
            for (Chocobo chocobo : filteredChocobos) {
                if (chocobo != alertingChocobo &&
                    !chocobo.isAttacking() &&
                    !chocobo.isBaby())
                { tempList.add(chocobo); }
            }

            // Filter Reset
            filteredChocobos = new ArrayList<>(tempList);
            tempList.clear();

            if (requiresSwimmingToTarget(attacker)) {
                // Swimming Target Filter
                for (Chocobo chocobo : filteredChocobos) {
                    if (ChocoboBrainAid.isAttackable(attacker, chocobo.canWalkOnWater())) { tempList.add(chocobo); }
                }

                // Filter Reset
                filteredChocobos = new ArrayList<>(tempList);
                tempList.clear();
            }

            // Now determine the alert limit with the final filtered list
            int alertLimit = Math.min(filteredChocobos.size(), 15);

            // Only alert chocobos that are within defined chunk distances
            // 3 chunks (32 blocks, 1024 squared), List limited 48 blocks squared around the alerting chocobo initially and slimmed down to 15 or less chocobos
            for (int i = 0; i < alertLimit; i++) {
                Chocobo chocobo = filteredChocobos.get(i);
                if ((chocobo.squaredDistanceTo(alertingChocobo) < 1024.0 ||
                     chocobo.squaredDistanceTo(attacker) < 1024.0)) {
                    chocobo.ticksUntilNextAlert = 30 + chocobo.getRandom().nextInt(20);
                    chocobo.alertChocobo(chocobo, attacker);
                }
            }
            alertingChocobo.ticksUntilNextAlert = ALERT_INTERVAL.get(alertingChocobo.random);
        }
    }
    protected void maybeAlertOthers(@NotNull Chocobo alertingChocobo) {
        if (alertingChocobo.getWorld().isClient()) { return; }
        if (alertingChocobo.getTarget() == null) { return; } // No target, no alerting
        if (alertingChocobo.isArmorStandNotAlive()) { return; }
        if (alertingChocobo.ticksUntilNextAlert > 0) { --alertingChocobo.ticksUntilNextAlert; }
        else {
            if (alertingChocobo.getVisibilityCache().canSee(alertingChocobo.getTarget())) { alertOthers(null, alertingChocobo); }
            this.ticksUntilNextAlert = ALERT_INTERVAL.get(alertingChocobo.random);
        }
    }
    protected void alertChocobo(@NotNull Chocobo chocobo, LivingEntity attacker) {
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
    @SuppressWarnings("RedundantIfStatement")
    public boolean isTemptable() {
        if (this.isBaby()) { return false; }
        // Left this method empty for now, future use for tempting chocobos in more complex ways
        return true;
    }
    public boolean isValidChocobo() { return this.isAlive(); }
    public boolean isArmorStand() { return false; }
    public boolean isNotArmorStand() { return true; }
    public boolean isArmorStandNotAlive() { return false; }
    public boolean isArmorStandAlive() { return false; }
    public void setChocoboArmorPose(ArmorStandChocoboPose pose) { /* No Op for Base */}
    public ArmorStandChocobo.ChocoboModelPose getPoseType() { return null; } // Null for base chocobo, used in armor stand chocobos
    public void setPoseType(ArmorStandChocobo.ChocoboModelPose type) { /* No Op for Base */ }
}