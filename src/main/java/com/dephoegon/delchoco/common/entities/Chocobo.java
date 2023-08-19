package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.aid.world.StaticGlobalVariables;
import com.dephoegon.delchoco.common.entities.breeding.ChocoboMateGoal;
import com.dephoegon.delchoco.common.entities.properties.*;
import com.dephoegon.delchoco.common.entities.properties.ChocoboGoals.ChocoboLocalizedWonder;
import com.dephoegon.delchoco.common.entities.properties.ChocoboGoals.ChocoboRandomStrollGoal;
import com.dephoegon.delchoco.common.init.ModAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.ChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.FloatChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.dValues.defaultInts.*;
import static com.dephoegon.delchoco.common.init.ModItems.GYSAHL_GREEN_ITEM;

public class Chocobo extends TameableEntity implements Angerable, NamedScreenHandlerFactory {
    @Nullable private UUID persistentAngerTarget;
    private int remainingPersistentAngerTime;
    private int ticksUntilNextAlert;
    private int timeToRecalculatePath;
    private FleeEntityGoal chocoboAvoidPlayerGoal;
    private WanderAroundFarGoal roamAround;
    private WanderAroundGoal roamAroundWB;
    private ChocoboLocalizedWonder localWonder;
    private ChocoboRandomStrollGoal localWonderWB;
    private FollowOwnerGoal follow;
    private Goal avoidBlocks;
    private float wingRotation;
    private float destPos;
    private boolean isChocoboJumping;
    private float wingRotDelta;
    private BlockPos nestPos;
    private boolean noRoam;
    public int TimeSinceFeatherChance = 0;
    private int rideTickDelay = 0;
    public float followingMrHuman = 2;
    private final double followSpeedModifier = 2.0D;
    private static final float maxStepUp = 1.5f;
    private final UniformIntProvider ALERT_INTERVAL = TimeHelper.betweenSeconds(4, 6);
    private static final String NBTKEY_CHOCOBO_COLOR = "Color";
    private static final String NBTKEY_CHOCOBO_IS_MALE = "Male";
    private static final String NBTKEY_CHOCOBO_FROM_EGG = "Egg";
    private static final String NBTKEY_MOVEMENT_TYPE = "MovementType";
    private static final String NBTKEY_SADDLE_ITEM = "Saddle";
    private static final String NBTKEY_WEAPON_ITEM = "Weapon";
    private static final String NBTKEY_ARMOR_ITEM = "Armor";
    private static final String NBTKEY_INVENTORY = "Inventory";
    private static final String NBTKEY_NEST_POSITION = "NestPos";
    private static final String NBTKEY_CHOCOBO_GENERATION = "Generation";
    private static final String NBTKEY_CHOCOBO_STAMINA = "Stamina";
    private static final String NBTKEY_CHOCOBO_FLAME_BLOOD = "FlameBlood";
    private static final String NBTKEY_CHOCOBO_WATER_BREATH = "WaterBreath";
    private static final String NBTKEY_CHOCOBO_COLLAR = "Collar";
    private static final String NBTKEY_CHOCOBO_WITHER_IMMUNE = "WitherImmune";
    private static final String NBTKEY_CHOCOBO_POISON_IMMUNE = "PoisonImmune";
    private static final String NBTKEY_CHOCOBO_SCALE = "Scale";
    private static final String NBTKEY_CHOCOBO_SCALE_MOD = "ScaleMod";
    private static final String NBTKEY_CHOCOBO_LEASH_BLOCK_X = "LeashBlockX";
    private static final String NBTKEY_CHOCOBO_LEASH_BLOCK_Y = "LeashBlockY";
    private static final String NBTKEY_CHOCOBO_LEASH_BLOCK_Z = "LeashBlockZ";
    private static final String NBTKEY_CHOCOBO_LEASH_DISTANCE = "LeashDistance";
    private static final UUID CHOCOBO_CHEST_ARMOR_MOD_UUID = UUID.fromString("c03d8021-8839-4377-ac23-ed723ece6454");
    private static final UUID CHOCOBO_CHEST_ARMOR_TOUGH_MOD_UUID = UUID.fromString("f7dcb185-7182-4a28-83ae-d1a2de9c022d");
    private static final UUID CHOCOBO_WEAPON_DAM_MOD_UUID = UUID.fromString("b9f0dc43-15a7-49f5-815c-915322c30402");
    private static final UUID CHOCOBO_WEAPON_SPD_MOD_UUID = UUID.fromString("46c84540-15f7-4f22-9da9-ebc23d2353af");
    private static final UUID CHOCOBO_SPRINTING_BOOST_ID = UUID.fromString("03ba3167-393e-4362-92b8-909841047640");
    private static final UniformIntProvider PERSISTENT_ANGER_TIME = TimeHelper.betweenSeconds(20, 39);
    private static final TrackedData<Integer> DATA_REMAINING_ANGER_TIME = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<ChocoboColor> PARAM_COLOR = DataTracker.registerData(Chocobo.class, ModDataSerializers.CHOCOBO_COLOR.getType());
    private static final TrackedData<Boolean> PARAM_IS_MALE = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> PARAM_FROM_EGG = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> PARAM_IS_FLAME_BLOOD = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> PARAM_IS_WATER_BREATH = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<MovementType> PARAM_MOVEMENT_TYPE = DataTracker.registerData(Chocobo.class, ModDataSerializers.MOVEMENT_TYPE.getType());
    private static final TrackedData<ItemStack> PARAM_SADDLE_ITEM = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> PARAM_WEAPON_ITEM = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> PARAM_ARMOR_ITEM = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Integer> PARAM_COLLAR_COLOR = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.INTEGER);
    private final static TrackedData<Integer> PARAM_GENERATION = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.INTEGER);
    private final static TrackedData<Float> PARAM_STAMINA = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.FLOAT);
    private final static TrackedData<Byte> PARAM_ABILITY_MASK = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.BYTE);
    private final static TrackedData<Boolean> PARAM_WITHER_IMMUNE = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final static TrackedData<Boolean> PARAM_POISON_IMMUNE = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> PARAM_SCALE = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> PARAM_SCALE_MOD = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> PARAM_LEASH_BLOCK_X = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PARAM_LEASH_BLOCK_Y = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PARAM_LEASH_BLOCK_Z = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PARAM_LEASH_LENGTH = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.INTEGER);
    private static final EntityAttributeModifier CHOCOBO_SPRINTING_SPEED_BOOST = (new EntityAttributeModifier(CHOCOBO_SPRINTING_BOOST_ID, "Chocobo sprinting speed boost", 1, EntityAttributeModifier.Operation.MULTIPLY_BASE));

    public static final int tier_one_chocobo_inv_slot_count = 15; // 3*5
    public static final int tier_two_chocobo_inv_slot_count = 45; //5*9
    private final int top_tier_chocobo_inv_slot_count = tier_two_chocobo_inv_slot_count;
    public final Inventory chocoboBackboneInv = new ChocoboInventory(top_tier_chocobo_inv_slot_count, this) {
        public boolean isValid(int slot, ItemStack stack) { return false; }
        public boolean canPlayerUse(PlayerEntity player) { return false; }
        public void setStack(int var1, ItemStack var2) {
            // BackboneLogic
        }
    };
    public final Inventory chocoboTierOneInv = new ChocoboInventory(tier_one_chocobo_inv_slot_count, this) {
        public void setStack(int var1, ItemStack var2) {
            // Tier One Logic
        }
    };
    public final Inventory chocoboTierTwoInv = new ChocoboInventory(tier_two_chocobo_inv_slot_count, this) {
        public void setStack(int var1, ItemStack var2) {
            // Tier Two Logic
        }
    };
    public final Inventory chocoboArmorInv = new ChocoboInventory(1, this) {
        public boolean isValid(int slot, ItemStack stack) {
            return false; // holder, replace with armorItemCheck
        }
    };
    public final Inventory chocoboWeaponInv = new ChocoboInventory(1, this) {
        public boolean isValid(int slot, ItemStack stack) {
            return false; // holder, replace with weaponItemCheck
        }
    };
    public final Inventory chocoboSaddleInv = new ChocoboInventory(1, this) {
        public boolean isValid(int slot, ItemStack stack) {
            return false; // holder, replace with saddleItemCheck
        }
    };
    protected void dropInventory() {
        dropInventory(this.chocoboBackboneInv);
        dropInventory(this.chocoboSaddleInv);
        dropInventory(this.chocoboWeaponInv);
        dropInventory(this.chocoboArmorInv);
        this.chocoboTierOneInv.clear();
        this.chocoboTierTwoInv.clear();
    }
    protected void dropInventory(Inventory inventory) {
        ItemScatterer.spawn(this.world, this, inventory);
        inventory.clear();
    }
    public Chocobo(EntityType<? extends Chocobo> entityType, World world) {
        super(entityType, world);
    }
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ChocoboGoals.ChocoPanicGoal(this,1.5D));
        this.goalSelector.add(2, new MeleeAttackGoal(this,2F, true));
        this.goalSelector.add(3, new ChocoboMateGoal(this, 1.0D));
        // toggleable Goal 4, - Follow owner (whistle [tamed])
        this.goalSelector.add(5, new ChocoboGoals.ChocoboLavaEscape(this));
        // toggleable Goal 5, - Avoid Player Goal (non-tamed goal)
        // toggleable Goal 6, - Roam Around Goal (whistle toggle [tamed/non-tamed])
        this.goalSelector.add(8, new TemptGoal(this, 1.2D, Ingredient.ofStacks(GYSAHL_GREEN_ITEM.getDefaultStack()), false));
        this.goalSelector.add(9, new FleeEntityGoal<>(this, LlamaEntity.class, 15F, 1.3F, 1.5F));
        // toggleable Goal 10, - Avoid Blocks by Class<? extends Block>
        this.goalSelector.add(11, new LookAroundGoal(this)); // moved after Roam, a little too stationary
        this.goalSelector.add(12, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.add(1, new ChocoboGoals.ChocoboOwnerHurtByGoal(this));
        this.targetSelector.add(2, new ChocoboGoals.ChocoboOwnerHurtGoal(this));
        this.targetSelector.add(3, (new ChocoboGoals.ChocoboHurtByTargetGoal(this, Chocobo.class)).setGroupRevenge(Chocobo.class));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
        this.targetSelector.add(5, new UniversalAngerGoal<>(this, true));
        this.targetSelector.add(6, new ActiveTargetGoal<>(this, EndermiteEntity.class, false));
        this.targetSelector.add(7, new ActiveTargetGoal<>(this, SilverfishEntity.class, false));
    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(ModAttributes.CHOCOBO_MAX_STAMINA,  ChocoConfigGet(StaticGlobalVariables.getStamina(), dSTAMINA.getDefault()))
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, ChocoConfigGet(StaticGlobalVariables.getSpeed(), dSPEED.getDefault()) / 100f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, ChocoConfigGet(StaticGlobalVariables.getHealth(), dHEALTH.getDefault()))
                .add(EntityAttributes.GENERIC_ARMOR, ChocoConfigGet(StaticGlobalVariables.getArmor(), dARMOR.getDefault()))
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, ChocoConfigGet(StaticGlobalVariables.getArmorTough(), dARMOR_TOUGH.getDefault()))
                .add(EntityAttributes.GENERIC_ATTACK_SPEED)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, ChocoConfigGet(StaticGlobalVariables.getAttack(), dATTACK.getDefault()))
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, EntityAttributes.GENERIC_FOLLOW_RANGE.getDefaultValue()*3);
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(PARAM_IS_FLAME_BLOOD, false);
        this.dataTracker.startTracking(PARAM_IS_WATER_BREATH, false);
        this.dataTracker.startTracking(PARAM_WITHER_IMMUNE, false);
        this.dataTracker.startTracking(PARAM_POISON_IMMUNE, false);
        this.dataTracker.startTracking(PARAM_COLLAR_COLOR, 0);
        this.dataTracker.startTracking(PARAM_COLOR, ChocoboColor.YELLOW);
        this.dataTracker.startTracking(PARAM_IS_MALE, false);
        this.dataTracker.startTracking(PARAM_FROM_EGG, false);
        this.dataTracker.startTracking(PARAM_MOVEMENT_TYPE, MovementType.WANDER);
        this.dataTracker.startTracking(PARAM_SADDLE_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_ARMOR_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_WEAPON_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_STAMINA, FloatChocoConfigGet(StaticGlobalVariables.getStamina(), dSTAMINA.getDefault()));
        this.dataTracker.startTracking(PARAM_GENERATION, 0);
        this.dataTracker.startTracking(PARAM_ABILITY_MASK, (byte) 0);
        this.dataTracker.startTracking(PARAM_SCALE, 0);
        this.dataTracker.startTracking(PARAM_SCALE_MOD, 1f);
        this.dataTracker.startTracking(DATA_REMAINING_ANGER_TIME, 0);
        this.dataTracker.startTracking(PARAM_LEASH_BLOCK_Z, 0);
        this.dataTracker.startTracking(PARAM_LEASH_BLOCK_Y, 50000);
        this.dataTracker.startTracking(PARAM_LEASH_BLOCK_X, 0);
        this.dataTracker.startTracking(PARAM_LEASH_LENGTH, 0);
    }
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setChocoboColor(ChocoboColor.values()[compound.getByte(NBTKEY_CHOCOBO_COLOR)]);
        this.setMale(compound.getBoolean(NBTKEY_CHOCOBO_IS_MALE));
        this.setFromEgg(compound.getBoolean(NBTKEY_CHOCOBO_FROM_EGG));
        this.setMovementType(MovementType.values()[compound.getByte(NBTKEY_MOVEMENT_TYPE)]);
        this.saddleItemStackHandler.deserializeNBT(compound.getCompound(NBTKEY_SADDLE_ITEM));
        this.chocoboWeaponHandler.deserializeNBT(compound.getCompound(NBTKEY_WEAPON_ITEM));
        this.chocoboArmorHandler.deserializeNBT(compound.getCompound(NBTKEY_ARMOR_ITEM));
        this.chocoboInventory.deserializeNBT(compound.getCompound(NBTKEY_INVENTORY));
        if (compound.contains(NBTKEY_NEST_POSITION)) { this.nestPos = NbtHelper.toBlockPos(compound.getCompound(NBTKEY_NEST_POSITION)); }
        this.setGeneration(compound.getInt(NBTKEY_CHOCOBO_GENERATION));
        this.setStamina(compound.getFloat(NBTKEY_CHOCOBO_STAMINA));
        this.setFlame(compound.getBoolean(NBTKEY_CHOCOBO_FLAME_BLOOD));
        this.setWaterBreath(compound.getBoolean(NBTKEY_CHOCOBO_WATER_BREATH));
        this.setWitherImmune(compound.getBoolean(NBTKEY_CHOCOBO_WITHER_IMMUNE));
        this.setPoisonImmune(compound.getBoolean(NBTKEY_CHOCOBO_POISON_IMMUNE));
        this.setChocoboScale(false, compound.getInt(NBTKEY_CHOCOBO_SCALE), true);
        this.setChocoboScaleMod(compound.getFloat(NBTKEY_CHOCOBO_SCALE_MOD));
        this.setCollarColor(compound.getInt(NBTKEY_CHOCOBO_COLLAR));
        this.setLeashSpot(compound.getInt(NBTKEY_CHOCOBO_LEASH_BLOCK_X), compound.getInt(NBTKEY_CHOCOBO_LEASH_BLOCK_Y), compound.getInt(NBTKEY_CHOCOBO_LEASH_BLOCK_Z));
        this.setLeashedDistance(compound.getDouble(NBTKEY_CHOCOBO_LEASH_DISTANCE));
    }
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putByte(NBTKEY_CHOCOBO_COLOR, (byte) this.getChocoboColor().ordinal());
        compound.putBoolean(NBTKEY_CHOCOBO_IS_MALE, this.isMale());
        compound.putBoolean(NBTKEY_CHOCOBO_FROM_EGG, this.fromEgg());
        compound.putByte(NBTKEY_MOVEMENT_TYPE, (byte) this.getMovementType().ordinal());
        compound.put(NBTKEY_SADDLE_ITEM, this.saddleItemStackHandler.serializeNBT());
        compound.put(NBTKEY_ARMOR_ITEM, this.chocoboArmorHandler.serializeNBT());
        compound.put(NBTKEY_WEAPON_ITEM, this.chocoboWeaponHandler.serializeNBT());
        compound.put(NBTKEY_INVENTORY, this.chocoboInventory.serializeNBT());
        if (this.nestPos != null) { compound.put(NBTKEY_NEST_POSITION, NbtHelper.fromBlockPos(this.nestPos)); }
        compound.putInt(NBTKEY_CHOCOBO_GENERATION, this.getGeneration());
        compound.putBoolean(NBTKEY_CHOCOBO_FLAME_BLOOD, this.fireImmune());
        compound.putBoolean(NBTKEY_CHOCOBO_WATER_BREATH, this.isWaterBreather());
        compound.putBoolean(NBTKEY_CHOCOBO_WITHER_IMMUNE, this.isWitherImmune());
        compound.putBoolean(NBTKEY_CHOCOBO_POISON_IMMUNE, this.isPoisonImmune());
        compound.putInt(NBTKEY_CHOCOBO_SCALE, this.getChocoboScale());
        compound.putFloat(NBTKEY_CHOCOBO_SCALE_MOD, this.getChocoboScaleMod());
        compound.putFloat(NBTKEY_CHOCOBO_STAMINA, this.getStamina());
        compound.putInt(NBTKEY_CHOCOBO_COLLAR, this.getCollarColor());
        compound.putInt(NBTKEY_CHOCOBO_LEASH_BLOCK_X, this.getLeashSpot().getX());
        compound.putInt(NBTKEY_CHOCOBO_LEASH_BLOCK_Y, this.getLeashSpot().getY());
        compound.putInt(NBTKEY_CHOCOBO_LEASH_BLOCK_Z, this.getLeashSpot().getZ());
        compound.putDouble(NBTKEY_CHOCOBO_LEASH_DISTANCE, this.getLeashDistance());
    }
    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;  // Temporary
    }
    // Leashing
    private void setLeashSpot(int x, int y, int z) {
        this.dataTracker.set(PARAM_LEASH_BLOCK_Z, z);
        this.dataTracker.set(PARAM_LEASH_BLOCK_Y, y);
        this.dataTracker.set(PARAM_LEASH_BLOCK_X, x);
    }
    private void setLeashSpot(@NotNull BlockPos blockPos) {
        this.dataTracker.set(PARAM_LEASH_BLOCK_Z, blockPos.getZ());
        this.dataTracker.set(PARAM_LEASH_BLOCK_Y, blockPos.getY());
        this.dataTracker.set(PARAM_LEASH_BLOCK_X, blockPos.getX());
    }
    public BlockPos getLeashSpot() {
        int x = this.dataTracker.get(PARAM_LEASH_BLOCK_X);
        int z = this.dataTracker.get(PARAM_LEASH_BLOCK_Z);
        int y = this.dataTracker.get(PARAM_LEASH_BLOCK_Y);
        return new BlockPos(new Position() {
            public double getX() { return x; }
            public double getY() { return y; }
            public double getZ() { return z; }
        });
    }
    private void setLeashedDistance(double distance) { this.dataTracker.set(PARAM_LEASH_LENGTH, (int) distance); }
    private double getLeashDistance() { return (double) this.dataTracker.get(PARAM_LEASH_LENGTH); }

    // Chocobo Arrays - Biomes Checks
    private @NotNull ArrayList<RegistryKey<Biome>> whiteChocobo() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.BIRCH_FOREST);
        out.add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST);
        return out;
    }

    private @NotNull ArrayList<RegistryKey<Biome>> blueChocobo() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.LUKEWARM_OCEAN);
        out.add(BiomeKeys.DEEP_LUKEWARM_OCEAN);
        out.add(BiomeKeys.WARM_OCEAN);
        out.add(BiomeKeys.RIVER);
        return out;
    }

    private @NotNull ArrayList<RegistryKey<Biome>> greenChocobo() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.JUNGLE);
        out.add(BiomeKeys.BAMBOO_JUNGLE);
        out.add(BiomeKeys.SWAMP);
        out.add(BiomeKeys.LUSH_CAVES);
        out.add(BiomeKeys.DRIPSTONE_CAVES);
        return out;
    }
    public int ChocoboShaker(@NotNull String stat) {
        return switch (stat) {
            case "health" -> boundedRangeModifier(5, 10);
            case "attack", "toughness", "defense" -> boundedRangeModifier(1, 4);
            default -> 0;
        };
    }
    private void chocoboStatShake(EntityAttribute attribute, String text) {
        int aValue = ChocoboShaker(text);
        Objects.requireNonNull(this.getAttributeInstance(attribute)).addPersistentModifier(new EntityAttributeModifier(text + " variance", aValue, EntityAttributeModifier.Operation.ADDITION));
    }
    private int boundedRangeModifier(int lower, int upper) {
        int range = lower+upper;
        return random.nextInt(range)-lower;
    }
    // Spawn/Breeding Related
    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }


    // Combat related
    @Override
    public int getAngerTime() { return this.remainingPersistentAngerTime; }
    public void setAngerTime(int angerTime) { this.remainingPersistentAngerTime = angerTime; }
    public double getFollowSpeedModifier() { return this.followSpeedModifier; }
    @Nullable
    public UUID getAngryAt() { return this.persistentAngerTarget; }
    public void setAngryAt(@Nullable UUID angryAt) { this.persistentAngerTarget = angryAt; }
    @Override
    public void chooseRandomAngerTime() { this.setAngerTime(PERSISTENT_ANGER_TIME.get(this.random)); }

    // Ride Related
    public int getRideTickDelay() { return this.rideTickDelay; }

}