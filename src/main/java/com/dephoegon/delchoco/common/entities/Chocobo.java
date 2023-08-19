package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.aid.world.StaticGlobalVariables;
import com.dephoegon.delchoco.common.entities.breeding.ChocoboMateGoal;
import com.dephoegon.delchoco.common.entities.properties.*;
import com.dephoegon.delchoco.common.entities.properties.ChocoboGoals.ChocoboLocalizedWonder;
import com.dephoegon.delchoco.common.entities.properties.MovementType;
import com.dephoegon.delchoco.common.entities.properties.ChocoboGoals.ChocoboRandomStrollGoal;
import com.dephoegon.delchoco.common.init.ModAttributes;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.utils.WorldUtils;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.passive.AnimalEntity;
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
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static com.dephoegon.delchoco.aid.chocoKB.isChocoShiftDown;
import static com.dephoegon.delchoco.aid.chocoKB.isChocoboWaterGlide;
import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.ChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.FloatChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultInts.*;
import static com.dephoegon.delchoco.common.entities.breeding.ChocoboSnap.setChocoScale;
import static com.dephoegon.delchoco.common.init.ModItems.GYSAHL_GREEN_ITEM;
import static net.minecraft.item.Items.ENDER_PEARL;
import static net.minecraft.tag.BiomeTags.*;

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

    // Chocobo Arrays - Biomes & Color Checks
    public static @NotNull ArrayList<ChocoboColor> wbChocobos() {
        ArrayList<ChocoboColor> out = new ArrayList<>();
        out.add(ChocoboColor.BLUE);
        out.add(ChocoboColor.GOLD);
        return out;
    }
    public static @NotNull ArrayList<ChocoboColor> wiChocobos() {
        ArrayList<ChocoboColor> out = new ArrayList<>();
        out.add(ChocoboColor.BLACK);
        out.add(ChocoboColor.GOLD);
        return out;
    }
    public static @NotNull ArrayList<ChocoboColor> piChocobos() {
        ArrayList<ChocoboColor> out = new ArrayList<>();
        out.add(ChocoboColor.GREEN);
        out.add(ChocoboColor.GOLD);
        return out;
    }
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
    private @NotNull ArrayList<RegistryKey<Biome>> IS_HOT_OVERWORLD() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.DESERT);
        out.add(BiomeKeys.JUNGLE);
        out.add(BiomeKeys.SPARSE_JUNGLE);
        out.add(BiomeKeys.SAVANNA);
        out.add(BiomeKeys.SAVANNA_PLATEAU);
        out.add(BiomeKeys.STONY_PEAKS);
        out.add(BiomeKeys.WINDSWEPT_SAVANNA);
        out.add(BiomeKeys.ERODED_BADLANDS);
        out.add(BiomeKeys.BAMBOO_JUNGLE);
        return out;
    }
    private @NotNull ArrayList<RegistryKey<Biome>> IS_SAVANNA() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.SAVANNA);
        out.add(BiomeKeys.SAVANNA_PLATEAU);
        out.add(BiomeKeys.WINDSWEPT_SAVANNA);
        return out;
    }
    private @NotNull ArrayList<RegistryKey<Biome>> IS_SNOWY() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.FROZEN_OCEAN);
        out.add(BiomeKeys.FROZEN_RIVER);
        out.add(BiomeKeys.SNOWY_PLAINS);
        out.add(BiomeKeys.SNOWY_BEACH);
        out.add(BiomeKeys.SNOWY_TAIGA);
        out.add(BiomeKeys.GROVE);
        out.add(BiomeKeys.SNOWY_SLOPES);
        out.add(BiomeKeys.JAGGED_PEAKS);
        out.add(BiomeKeys.FROZEN_PEAKS);
        out.add(BiomeKeys.ICE_SPIKES);
        return out;
    }
    private @NotNull ArrayList<RegistryKey<Biome>> IS_MUSHROOM() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.MUSHROOM_FIELDS);
        return out;
    }
    private @NotNull ArrayList<RegistryKey<Biome>> IS_END() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.THE_END);
        out.add(BiomeKeys.SMALL_END_ISLANDS);
        out.add(BiomeKeys.END_MIDLANDS);
        out.add(BiomeKeys.END_HIGHLANDS);
        out.add(BiomeKeys.END_BARRENS);
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
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, @Nullable EntityData spawnDataIn, @Nullable NbtCompound dataTag) {
        this.setMale(this.world.random.nextBoolean());

        final RegistryEntry<Biome> currentBiomes = this.world.getBiome(getBlockPos().down());
        //noinspection OptionalGetWithoutIsPresent
        final RegistryKey<Biome> BiomesKey = currentBiomes.getKey().get();
        if (!fromEgg()) {
            setChocoboSpawnCheck(ChocoboColor.YELLOW);
            if (currentBiomes.isIn(IS_NETHER)) { setChocoboSpawnCheck(ChocoboColor.FLAME); }
            if (IS_END().contains(BiomesKey)){ setChocoboSpawnCheck(ChocoboColor.PURPLE); }
            if (IS_MUSHROOM().contains(BiomesKey)) { setChocoboSpawnCheck(ChocoboColor.PINK); }
            if (IS_SNOWY().contains(BiomesKey) || whiteChocobo().contains(BiomesKey)) { setChocoboSpawnCheck(ChocoboColor.WHITE); }
            if (blueChocobo().contains(BiomesKey)) { setChocoboSpawnCheck(ChocoboColor.BLUE); }
            if (currentBiomes.isIn(IS_FOREST) || currentBiomes.isIn(IS_BADLANDS)) { setChocoboSpawnCheck(ChocoboColor.RED); }
            if (greenChocobo().contains(BiomesKey)) { setChocoboSpawnCheck(ChocoboColor.GREEN); }
            if (IS_HOT_OVERWORLD().contains(BiomesKey) && !IS_SAVANNA().contains(BiomesKey)) { setChocoboSpawnCheck(ChocoboColor.BLACK); }
            this.setChocoboScale(this.isMale(), 0, false);
        }
        chocoboStatShake(EntityAttributes.GENERIC_MAX_HEALTH, "health");
        chocoboStatShake(EntityAttributes.GENERIC_ATTACK_DAMAGE, "attack");
        chocoboStatShake(EntityAttributes.GENERIC_ARMOR, "defense");
        chocoboStatShake(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, "toughness");
        if (getChocoboColor() == ChocoboColor.PURPLE) {
            int chance = IS_END().contains(BiomesKey) ? 60 : 15;
            if (random.nextInt(100)+1 < chance) {
                this.chocoboBackboneInv.setStack(random.nextInt(18), new ItemStack(ENDER_PEARL.getDefaultStack().split(random.nextInt(3) + 1).getItem()));
            }
            if (random.nextInt(100)+1 < chance) {
                this.chocoboBackboneInv.setStack(random.nextInt(9)+18, new ItemStack(ENDER_PEARL.getDefaultStack().split(random.nextInt(3) + 1).getItem()));
            }
            if (random.nextInt(100)+1 < chance) {
                this.chocoboBackboneInv.setStack(random.nextInt(18)+27, new ItemStack(ENDER_PEARL.getDefaultStack().split(random.nextInt(3) + 1).getItem()));
            }
        }
        return super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }
    public void setChocobo(ChocoboColor color) {
        this.setChocoboColor(color);
        this.setFlame(color == ChocoboColor.FLAME);
        this.setWaterBreath(wbChocobos().contains(color));
        this.setWitherImmune(wiChocobos().contains(color));
        this.setPoisonImmune(piChocobos().contains(color));
    }
    private void setChocoboSpawnCheck(ChocoboColor color) {
        ChocoboColor chocobo = this.getChocoboColor();
        if ((chocobo == ChocoboColor.YELLOW || color == ChocoboColor.YELLOW) && chocobo != color) { setChocobo(color); }
    }

    // Combat related
    public boolean canBeControlledByRider() { return this.isTamed(); }
    private void setArmor(ItemStack pStack) {
        this.equipStack(EquipmentSlot.CHEST, pStack);
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
    }
    private void setWeapon(ItemStack pStack) {
        this.equipStack(EquipmentSlot.MAINHAND, pStack);
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }
    public boolean isChocoboArmor(@NotNull ItemStack pStack) { return pStack.getItem() instanceof ChocoboArmorItems; }
    public boolean isChocoWeapon(@NotNull ItemStack pStack) { return pStack.getItem() instanceof ChocoboWeaponItems; }
    public int chocoStatMod() { return ChocoConfigGet(StaticGlobalVariables.getWeaponModifier(), dWEAPON_MOD.getDefault()); }
    private void setChocoboArmorStats(ItemStack pStack) {
        if (!this.world.isClient()) {
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
    private void setChocoboWeaponStats(ItemStack pStack) {
        if (!this.world.isClient()) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).removeModifier(CHOCOBO_WEAPON_DAM_MOD_UUID);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED)).removeModifier(CHOCOBO_WEAPON_SPD_MOD_UUID);
            if (this.isChocoWeapon(pStack)) {
                double a = ((ChocoboWeaponItems)pStack.getItem()).getDamage()*chocoStatMod();
                float s = ((ChocoboWeaponItems)pStack.getItem()).getAttackSpeed()*chocoStatMod();
                if (a != 0) { Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).addPersistentModifier(new EntityAttributeModifier(CHOCOBO_WEAPON_DAM_MOD_UUID, "Chocobo Attack Bonus", a, EntityAttributeModifier.Operation.ADDITION)); }
                if (s != 0) { Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED)).addPersistentModifier(new EntityAttributeModifier(CHOCOBO_WEAPON_SPD_MOD_UUID, "Chocobo Attack Speed Bonus", s, EntityAttributeModifier.Operation.ADDITION)); }
                this.setWeapon(pStack);
            }
        }
    }
    public ChocoboColor getChocoboColor() { return this.dataTracker.get(PARAM_COLOR); }
    public void setChocoboColor(ChocoboColor color) { this.dataTracker.set(PARAM_COLOR, color); }
    public void setCollarColor(Integer color) { this.dataTracker.set(PARAM_COLLAR_COLOR, color); }
    public Integer getCollarColor() { return this.dataTracker.get(PARAM_COLLAR_COLOR); }
    public boolean fireImmune() { return this.dataTracker.get(PARAM_IS_FLAME_BLOOD); }
    public boolean isFireImmune() { return this.fireImmune(); } // TODO: Nameswpaw fire immune after pieces are in
    public void setFlame(boolean flame) { this.dataTracker.set(PARAM_IS_FLAME_BLOOD, flame); }
    public void setWaterBreath(boolean waterBreath) { this.dataTracker.set(PARAM_IS_WATER_BREATH, waterBreath); }
    public void setWitherImmune(boolean witherImmune) { this.dataTracker.set(PARAM_WITHER_IMMUNE, witherImmune); }
    public void setPoisonImmune(boolean poisonImmune) { this.dataTracker.set(PARAM_POISON_IMMUNE, poisonImmune); }
    public void setChocoboScale(boolean isMale, int overrideValue, boolean override) {
        int scale;
        if (override) { scale = overrideValue; } else { scale = setChocoScale(isMale); }
        this.setChocoboScaleMod(ScaleMod(scale));
        this.dataTracker.set(PARAM_SCALE, scale);
    }
    public void setChocoboScaleMod(float value) { this.dataTracker.set(PARAM_SCALE_MOD, value); }
    public boolean nonFlameFireImmune() { return fireImmune() && ChocoboColor.FLAME != getChocoboColor(); }
    public boolean isWaterBreather() { return this.dataTracker.get(PARAM_IS_WATER_BREATH); }
    public boolean isWitherImmune() { return this.dataTracker.get(PARAM_WITHER_IMMUNE); }
    public boolean isPoisonImmune() { return this.dataTracker.get(PARAM_POISON_IMMUNE); }
    public int getChocoboScale() { return this.dataTracker.get(PARAM_SCALE); }
    public float getChocoboScaleMod() { return this.dataTracker.get(PARAM_SCALE_MOD); }
    public float ScaleMod(int scale) { return (scale == 0) ? 0 : ((scale < 0) ? (((float) ((scale * -1) - 100) / 100) * -1) : (1f + ((float) scale / 100))); }
    public boolean canHaveStatusEffect(@NotNull StatusEffectInstance potionEffect) {
        if (potionEffect.getEffectType() == StatusEffects.WITHER) return !this.isWitherImmune();
        if (potionEffect.getEffectType() == StatusEffects.POISON) return !this.isPoisonImmune();
        return super.canHaveStatusEffect(potionEffect);
    }
    public boolean isMale() { return this.dataTracker.get(PARAM_IS_MALE); }
    public boolean fromEgg() { return this.dataTracker.get(PARAM_FROM_EGG); }
    public void setMale(boolean isMale) { this.dataTracker.set(PARAM_IS_MALE, isMale); }
    public void setFromEgg(boolean fromEgg) { this.dataTracker.set(PARAM_FROM_EGG, fromEgg); }
    public MovementType getMovementType() { return this.dataTracker.get(PARAM_MOVEMENT_TYPE); }
    public void setMovementType(MovementType type) { this.dataTracker.set(PARAM_MOVEMENT_TYPE, type); setMovementAiByType(type); }
    private void setMovementAiByType(@NotNull MovementType type) {
        BlockPos leashPoint = this.getLeashSpot();
        double length = this.getLeashDistance();
        this.clearWonders();
        switch (type) {
            case STANDSTILL -> this.followingMrHuman = 3;
            case FOLLOW_OWNER -> {
                this.followingMrHuman = 1;
                if (this.goalSelector.getRunningGoals().noneMatch(t -> t.getGoal() == follow)) { this.goalSelector.add(4,this.follow); }
            }
            default -> this.followingMrHuman = 2;
        }
        boolean skipper = this.followingMrHuman == 2 || length < 2D || length > 20D;
        if (this.isWaterBreather() && !this.world.getBiome(this.getLandingPos()).isIn(IS_NETHER)) {
            if (skipper) { this.goalSelector.add(7, roamAroundWB); }
            else {
                this.localWonderWB = new ChocoboRandomStrollGoal(this, 1D, leashPoint, length);
                this.goalSelector.add(7, this.localWonderWB);
            }
        } else if (followingMrHuman != 1) { if (skipper) { this.goalSelector.add(7, roamAround); }
        else {
            this.localWonder = new ChocoboLocalizedWonder(this, 1D, leashPoint, length);
            this.goalSelector.add(7, this.localWonder);
        }
        }
    }
    public void setMovementTypeByFollowMrHuman(float followingNumber) {
        MovementType type = switch ((int) followingNumber) {
            case 1 -> MovementType.FOLLOW_OWNER;
            case 2 -> MovementType.STANDSTILL;
            default -> MovementType.WANDER;
        };
        this.dataTracker.set(PARAM_MOVEMENT_TYPE, type);
    }
    public boolean isSaddled() { return !this.getSaddle().isEmpty(); }
    public boolean isArmored() { return !this.getArmorItemStack().isEmpty(); }
    public boolean isArmed() { return !this.getWeapon().isEmpty(); }
    public ItemStack getSaddle() { return this.dataTracker.get(PARAM_SADDLE_ITEM); }
    public ItemStack getWeapon() { return this.dataTracker.get(PARAM_WEAPON_ITEM); }
    public ItemStack getArmorItemStack() { return this.dataTracker.get(PARAM_ARMOR_ITEM); }
    private void setSaddleType(@NotNull ItemStack saddleStack) {
        ItemStack oldStack = getSaddle();
        if (oldStack.getItem() != saddleStack.getItem()) { this.dataTracker.set(PARAM_SADDLE_ITEM, saddleStack.copy()); }
    }
    private void setWeaponType(@NotNull ItemStack weaponType) {
        ItemStack oldStack = getWeapon();
        if (oldStack.getItem() != weaponType.getItem()) { this.dataTracker.set(PARAM_WEAPON_ITEM, weaponType.copy()); }
    }
    private void setArmorType(@NotNull ItemStack armorType) {
        ItemStack oldStack = getArmorItemStack();
        if (oldStack.getItem() != armorType.getItem()) { this.dataTracker.set(PARAM_ARMOR_ITEM, armorType.copy()); }
    }
    public boolean canBeRiddenInWater() { return this.canBreatheInWater(); }
    public boolean canBreatheInWater() { return this.isWaterBreather(); }
    public float getStamina() { return this.dataTracker.get(PARAM_STAMINA); }
    public void setStamina(float value) { this.dataTracker.set(PARAM_STAMINA, value); }
    public float getStaminaPercentage() { return (float) (this.getStamina() / Objects.requireNonNull(this.getAttributeInstance(ModAttributes.CHOCOBO_MAX_STAMINA)).getValue()); }
    public int getGeneration() { return this.dataTracker.get(PARAM_GENERATION); }
    public String getGenerationString() {
        int gen = this.getGeneration();
        return Integer.toString(gen);
    }
    public void setGeneration(int value) { this.dataTracker.set(PARAM_GENERATION, value); }
    private boolean useStamina(float value) {
        if (value == 0) return true;
        float curStamina = this.dataTracker.get(PARAM_STAMINA);
        if (curStamina < value) return false;

        float maxStamina = (float) Objects.requireNonNull(this.getAttributeInstance(ModAttributes.CHOCOBO_MAX_STAMINA)).getValue();
        float newStamina = MathHelper.clamp(curStamina - value, 0, maxStamina);
        this.dataTracker.set(PARAM_STAMINA, newStamina);
        return true;
    }
    public double getMountedHeightOffset() {
        double scaleZero = this.getChocoboScale() == 0 ? 1.7D : this.getChocoboScale() > 0 ? 1.55D : 1.85D;
        return (scaleZero * this.getChocoboScaleMod());
    }
    public void dismountVehicle() {
        if (this.followingMrHuman != 1) {
            this.clearWonders();
            BlockPos spot = this.getBlockPos();
            double length = 2D;
            this.setLeashSpot(spot);
            this.setLeashedDistance(length);
            this.followingMrHuman = 3;
            this.setMovementTypeByFollowMrHuman(this.followingMrHuman);
            if (this.isWaterBreather() && !this.world.getBiome(this.getBlockPos()).isIn(IS_NETHER)) {
                this.localWonderWB = new ChocoboRandomStrollGoal(this, 1D, spot, length);
                this.goalSelector.add(7, this.localWonderWB);
            } else {
                this.localWonder = new ChocoboLocalizedWonder(this, 1D, spot, length);
                this.goalSelector.add(7, this.localWonder);
            }
        }
        super.dismountVehicle();
    }
    public Entity getPrimaryPassenger() { return this.getPassengerList().isEmpty() ? null : this.getPassengerList().get(0); }
    protected boolean updateWaterState() {
        this.fluidHeight.clear();
        this.updateInWaterStateAndDoWaterCurrentPushing();
        boolean flag = this.updateMovementInFluid(FluidTags.LAVA, 0.085D);
        return this.isTouchingWater() || flag;
    }
    private void updateInWaterStateAndDoWaterCurrentPushing() {
        if (!this.isWaterBreather()) {
            if (this.getVehicle() instanceof Chocobo) { this.touchingWater = false; }
            else if (this.updateMovementInFluid(FluidTags.WATER, 0.014D)) {
                if (!this.touchingWater && !this.firstUpdate) { this.onSwimmingStart(); }
                this.fallDistance = 0.0F;
                this.touchingWater = true;
                this.extinguish();
            } else { this.touchingWater = false; }
        } else {
            if (this.isTouchingWater()) {
                this.touchingWater = false;
                this.extinguish();
                if (this.getVehicle() instanceof Chocobo) { if (this.getPrimaryPassenger() instanceof PlayerEntity rider) { rider.extinguish(); } }
            }
        }
    }
    public void travel(@NotNull Vec3d travelVector) {
        Vec3d newVector = travelVector;
        if (this.getPrimaryPassenger() instanceof PlayerEntity rider) {
            this.prevY = rider.getYaw();
            this.prevPitch = rider.getPitch();
            this.setYaw(rider.getYaw());
            this.setPitch(rider.getPitch());
            this.setRotation(this.getYaw(), this.getPitch());
            this.headYaw = this.getYaw();
            this.bodyYaw = this.getYaw();

            newVector = new Vec3d(rider.sidewaysSpeed * 0.5F, newVector.y, rider.forwardSpeed); //Strafe - Vertical - Forward

            // reduce movement speed by 75% if moving backwards
            if (newVector.getZ() <= 0.0D)
                newVector = new Vec3d(newVector.x, newVector.y, newVector.z * 0.25F);

            if (this.onGround) { this.isChocoboJumping = false; }

            if (this.isLogicalSideForUpdatingMovement()) {
                if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                    // jump logic
                    if (!this.isChocoboJumping && this.onGround && this.useStamina(FloatChocoConfigGet(StaticGlobalVariables.getStaminaJump(), dSTAMINA_JUMP.getDefault()))) {
                        Vec3d motion = getVelocity();
                        setVelocity(new Vec3d(motion.x, .6f, motion.z));
                        this.isChocoboJumping = true;
                    }
                }
                if (rider.isTouchingWater()) {
                    Vec3d motion = getVelocity();
                    if (MinecraftClient.getInstance().options.jumpKey.isPressed()) { setVelocity(new Vec3d(motion.x, .5f, motion.z)); }
                    else if (this.getVelocity().y < 0 && !this.isWaterBreather()) {
                        int distance = WorldUtils.getDistanceToSurface(this.getBlockPos(), this.getEntityWorld());
                        if (distance > 0) { setVelocity(new Vec3d(motion.x, .05f, motion.z)); }
                    } else if (this.isWaterBreather() && isChocoboWaterGlide()) {
                        Vec3d waterMotion = getVelocity();
                        setVelocity(new Vec3d(waterMotion.x, waterMotion.y * 0.65F, waterMotion.z));
                    }
                }
                if (rider.isInLava()) {
                    Vec3d motion = getVelocity();
                    if (MinecraftClient.getInstance().options.jumpKey.isPressed()) { setVelocity(new Vec3d(motion.x, .5f, motion.z)); }
                    else if (this.fireImmune() && this.getVelocity().y < 0) {
                        int distance = WorldUtils.getDistanceToSurface(this.getBlockPos(), this.getEntityWorld());
                        if (distance > 0) { setVelocity(new Vec3d(motion.x, .05f, motion.z)); }
                    }
                }
                // Insert override for slow-fall Option on Chocobo
                if (!this.onGround && !this.isTouchingWater() && !this.isInLava() && !isChocoShiftDown() && this.getVelocity().y < 0 &&
                        this.useStamina(FloatChocoConfigGet(StaticGlobalVariables.getStaminaGlide(), dSTAMINA_GLIDE.getDefault()))) {
                    if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                        Vec3d motion = getVelocity();
                        setVelocity(new Vec3d(motion.x, motion.y * 0.65F, motion.z));
                    }
                }
                if ((this.isSprinting() && !this.useStamina(FloatChocoConfigGet(StaticGlobalVariables.getStaminaCost(), dSTAMINA_SPRINT.getDefault())) || (this.isSprinting() && this.isTouchingWater() && this.useStamina(FloatChocoConfigGet(StaticGlobalVariables.getStaminaCost(), dSTAMINA_SPRINT.getDefault()))))) { this.setSprinting(false); }

                this.setMovementSpeed((float) Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue());
                super.travel(newVector);
            }
        } else {
            if (!this.onGround && !this.isTouchingWater() && !this.isInLava() && this.getVelocity().y < 0 && this.useStamina(FloatChocoConfigGet(StaticGlobalVariables.getStaminaCost(), dSTAMINA_SPRINT.getDefault()))) {
                Vec3d motion = getVelocity();
                setVelocity(new Vec3d(motion.x, motion.y * 0.65F, motion.z));
            }
            double y = newVector.y;
            if (y > 0) y = y * -1;
            Vec3d cappedNewVector = new Vec3d(newVector.x, y, newVector.z);
            super.travel(cappedNewVector);
        }
    }
    public void updatePassengerPosition(Entity passenger) {
        super.updatePassengerPosition(passenger);
        if (passenger instanceof MobEntity && this.getPrimaryPassenger() == passenger) { this.bodyYaw = ((LivingEntity) passenger).bodyYaw; }
    }
    public void tick() {
        super.tick();
        floatChocobo();
        LivingEntity owner = this.getOwner() != null ? this.getOwner() : null;
        if (this.rideTickDelay < 0) {
            Entity RidingPlayer = this.getPrimaryPassenger();
            if (RidingPlayer != null) {
                this.rideTickDelay = 5;
                if (RidingPlayer instanceof PlayerEntity player) {
                    this.setInvulnerable(player.isCreative());
                    if (this.getHealth() != this.getMaxHealth() && player.getOffHandStack().getItem() == GYSAHL_GREEN_ITEM) {
                        player.getOffHandStack().decrement(1);
                        heal(ChocoConfigGet(StaticGlobalVariables.getHealAmount(), dHEAL_AMOUNT.getDefault()));
                    }
                } else { this.setInvulnerable(false); }
            } else {
                this.setInvulnerable(false);
                this.rideTickDelay = 30;
            }
        } else { this.rideTickDelay--; }
        if (owner != null) {
            if (this.followingMrHuman == 1) {
                if (--this.timeToRecalculatePath <= 0) {
                    this.getLookControl().lookAt(owner, 10.0F, (float) this.getMaxLookPitchChange());
                    if (--this.timeToRecalculatePath <= 0) {
                        this.timeToRecalculatePath = this.randomIntInclusive(10, 40);
                        if (this.squaredDistanceTo(owner) >= 288.0D) { this.teleportToOwner(owner);}
                        else { this.navigation.startMovingTo(owner, this.followSpeedModifier); }
                    }   }   }   }
    }
    private void floatChocobo() {
        if (this.isInLava()) {
            ShapeContext collisionContext = ShapeContext.of(this);
            if (collisionContext.isAbove(FluidBlock.COLLISION_SHAPE, this.getBlockPos(), true) && !this.world.getFluidState(this.getBlockPos().up()).isIn(FluidTags.LAVA)) { this.onGround = true; }
            else { this.setVelocity(this.getVelocity().multiply(.003D).add(0.0D, 0.05D, 0.0D)); }
        }
        if (this.isTouchingWater() && !this.isWaterBreather()) {
            ShapeContext collisionContext = ShapeContext.of(this);
            if (collisionContext.isAbove(FluidBlock.COLLISION_SHAPE, this.getBlockPos(), true) && !this.world.getFluidState(this.getBlockPos().up()).isIn(FluidTags.WATER)) { this.onGround = true; }
            else { this.setVelocity(this.getVelocity().multiply(.003D).add(0.0D, 0.05D, 0.0D)); }
        }
    }
    public float getPathfindingFavor(@NotNull BlockPos pPos, @NotNull WorldView pLevel) {
        if (pLevel.getBlockState(pPos).getFluidState().isIn(FluidTags.LAVA)) { return (float) (Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue() * 10.0F);}
        else if (pLevel.getBlockState(pPos).getFluidState().isIn(FluidTags.WATER)) { return (float) (Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue() * 10.0F); }
        else { return (float) Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue(); }
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
    private void teleportToOwner(@NotNull LivingEntity owner) {
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
        PathNodeType pathNodeTypes = LandPathNodeMaker.getLandNodeType(this.world, pPos.mutableCopy());
        if (pathNodeTypes != PathNodeType.WALKABLE) { return false; }
        else {
            BlockPos blockpos = pPos.subtract(this.getBlockPos());
            return this.world.isSpaceEmpty(this, this.getBoundingBox().offset(blockpos));
        }
    }
    private int randomIntInclusive(int pMin, int pMax) { return this.getRandom().nextInt(pMax - pMin + 1) + pMin; }
    public boolean canBreedWith(@NotNull AnimalEntity otherAnimal) {
        if (otherAnimal == this || !(otherAnimal instanceof Chocobo otherChocobo)) return false;
        if (!this.isInLove() || !otherAnimal.isInLove()) return false;
        return otherChocobo.isMale() != this.isMale();
    }
    public void setSprinting(boolean sprinting) {
        this.setFlag(3, sprinting);
        EntityAttributeInstance attributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        assert attributeInstance != null;
        if (attributeInstance.getModifier(CHOCOBO_SPRINTING_BOOST_ID) != null) { attributeInstance.removeModifier(CHOCOBO_SPRINTING_SPEED_BOOST); }
        if (sprinting) { attributeInstance.addTemporaryModifier(CHOCOBO_SPRINTING_SPEED_BOOST); }
    }
    public void dropFeather() {
        if (this.getEntityWorld().isClient()) { return; }
        if (this.isBaby()) { return; }
        this.dropStack(new ItemStack(CHOCOBO_FEATHER, 1), 0.0F);
    }
    protected boolean canStartRiding(@NotNull Entity entityIn) { return !this.getSaddle().isEmpty() && super.canStartRiding(entityIn); }

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