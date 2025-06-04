package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.chocoboChecks;
import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.aid.world.WorldConfig;
import com.dephoegon.delchoco.common.effects.ChocoboCombatEvents;
import com.dephoegon.delchoco.common.entities.properties.*;
import com.dephoegon.delchoco.common.entities.properties.MovementType;
import com.dephoegon.delchoco.common.init.ModAttributes;
import com.dephoegon.delchoco.common.init.ModSounds;
import com.dephoegon.delchoco.common.inventory.SaddlebagContainer;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboLeashPointer;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import com.dephoegon.delchoco.mixin.ServerPlayerEntityAccessor;
import com.dephoegon.delchoco.utils.WorldUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.dephoegon.delbase.item.ShiftingDyes.*;
import static com.dephoegon.delchoco.aid.chocoKB.isChocoShiftDown;
import static com.dephoegon.delchoco.aid.chocoKB.isChocoboWaterGlide;
import static com.dephoegon.delchoco.aid.chocoboChecks.*;
import static com.dephoegon.delchoco.aid.dyeList.getDyeList;
import static com.dephoegon.delchoco.aid.world.WorldConfig.FloatChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.*;
import static com.dephoegon.delchoco.common.entities.breeding.BreedingHelper.getChocoName;
import static com.dephoegon.delchoco.common.entities.breeding.ChocoboSnap.setChocoScale;
import static com.dephoegon.delchoco.common.init.ModItems.*;
import static com.dephoegon.delchoco.common.init.ModSounds.AMBIENT_SOUND;
import static java.lang.Math.random;
import static net.minecraft.entity.SpawnGroup.CREATURE;
import static net.minecraft.item.Items.*;
import static net.minecraft.registry.tag.BiomeTags.IS_BADLANDS;
import static net.minecraft.registry.tag.BiomeTags.IS_FOREST;

public class Chocobo extends TameableEntity implements Angerable {
    @Nullable private UUID persistentAngerTarget;
    private int remainingPersistentAngerTime;
    private int ticksUntilNextAlert;
    private int timeToRecalculatePath;
    private float wingRotation;
    private float destPos;
    private boolean isChocoboJumping;
    private float wingRotDelta;
    private BlockPos nestPos;
    protected boolean noRoam;
    public int TimeSinceFeatherChance = 0;
    private int rideTickDelay = 0;
    public int followingMrHuman = 2;
    private final double followSpeedModifier = 2.0D;
    private static final float maxStepUp = 1.5f;
    private final UniformIntProvider ALERT_INTERVAL = TimeHelper.betweenSeconds(4, 6);
    protected static final String NBTKEY_CHOCOBO_COLOR = "Color";
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
    private static final TrackedData<Integer> PARAM_COLOR = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> PARAM_IS_MALE = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> PARAM_FROM_EGG = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> PARAM_IS_FLAME_BLOOD = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> PARAM_IS_WATER_BREATH = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> PARAM_MOVEMENT_TYPE = DataTracker.registerData(Chocobo.class, TrackedDataHandlerRegistry.INTEGER);
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
    public final ChocoboInventory chocoboBackboneInv = new ChocoboInventory(top_tier_chocobo_inv_slot_count, this) {
        public boolean isValid(int slot, ItemStack stack) { return false; }
        public boolean canPlayerUse(PlayerEntity player) { return false; }
        public void setStack(int var1, ItemStack var2) {
            super.setStack(var1, var2);
            chocoboFeatherPick(this, this.getChocobo().chocoboTierOneInv, var1);
            chocoboFeatherPick(this, this.getChocobo().chocoboTierTwoInv, var1);
            this.getChocobo().chocoboBackBoneList.set(var1, var2);
        }
    };
    public final ChocoboInventory chocoboTierOneInv = new ChocoboInventory(tier_one_chocobo_inv_slot_count, this) {
        public void setStack(int var1, ItemStack var2) {
            super.setStack(var1, var2);
            chocoboFeatherPick(this, this.getChocobo().chocoboBackboneInv, var1);
        }
    };
    public final ChocoboInventory chocoboTierTwoInv = new ChocoboInventory(tier_two_chocobo_inv_slot_count, this) {
        public void setStack(int var1, ItemStack var2) {
            super.setStack(var1, var2);
            chocoboFeatherPick(this, this.getChocobo().chocoboBackboneInv, var1);
        }
    };
    public final ChocoboInventory chocoboArmorInv = new ChocoboInventory(1, this) {
        public boolean isValid(int slot, @NotNull ItemStack stack) { return stack.isEmpty() || stack.getItem() instanceof ChocoboArmorItems; }
        public int getMaxCountPerStack() { return 1; }
        public void setStack(int slot, ItemStack itemStack) {
            super.setStack(slot, itemStack);
            this.getChocobo().setArmorType(itemStack);
            this.getChocobo().setChocoboArmorStats(itemStack);
        }
    };
    public final ChocoboInventory chocoboWeaponInv = new ChocoboInventory(1, this) {
        public boolean isValid(int slot, @NotNull ItemStack stack) { return stack.isEmpty() || stack.getItem() instanceof ChocoboWeaponItems; }
        public int getMaxCountPerStack() { return 1; }
        public void setStack(int slot, ItemStack itemStack) {
            super.setStack(slot, itemStack);
            this.getChocobo().setWeaponType(itemStack);
            this.getChocobo().setChocoboWeaponStats(itemStack);
        }
    };
    public final ChocoboInventory chocoboSaddleInv = new ChocoboInventory(1, this) {
        public boolean isValid(int slot, @NotNull ItemStack stack) { return stack.isEmpty() || stack.getItem() instanceof ChocoboSaddleItem; }
        public int getMaxCountPerStack() { return 1; }
        public void setStack(int slot, ItemStack itemStack) {
            super.setStack(slot, itemStack);
            inventoryDropClear(this.getChocobo().chocoboBackboneInv, this.getChocobo());
            this.getChocobo().setSaddleType(itemStack);
        }
    };
    public final DefaultedList<ItemStack> chocoboBackBoneList = DefaultedList.ofSize(top_tier_chocobo_inv_slot_count, ItemStack.EMPTY);
    private Box spawnControlBoxSize(@NotNull Box box, int multi) {
        int xz = 8*5 * Math.max(multi, 1); //8 half a chunk
        int y = 32 * Math.max(multi/2, 1);
        return box.expand(xz, y, xz);
    }
    protected void dropLoot(@NotNull DamageSource source, boolean causedByPlayer)  {
        inventoryDropClear(this.chocoboBackboneInv, this);
        inventoryDropClear(this.chocoboSaddleInv, this);
        inventoryDropClear(this.chocoboWeaponInv, this);
        inventoryDropClear(this.chocoboArmorInv, this);
        this.chocoboTierOneInv.clear();
        this.chocoboTierTwoInv.clear();
        super.dropLoot(source, causedByPlayer);
    }
    protected void inventoryDropClear(@NotNull Inventory inventory, Entity entity) {
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) { entity.dropStack(itemStack); }
        }
        inventory.clear();
    }
    public Chocobo(EntityType<? extends Chocobo> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, this.isFireImmune() ? -0.2F : 32.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, this.isFireImmune() ? -0.1F : 16.0F);
        this.setPathfindingPenalty(PathNodeType.FENCE,6.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_CAUTIOUS, this.isWitherImmune() ? 0.0F : 8.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, this.isWaterBreathing() ? -0.25F : -0.15F);
        this.setPathfindingPenalty(PathNodeType.LAVA, this.isFireImmune() ? 0.0F : 16.0F);
    }
    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        if (!ChocoboBrainAid.isAttackable(target)) { return false; }
        if (target instanceof TameableEntity tamable) {
            if (tamable.isTamed()) {
                if (tamable.getOwner() == owner) {
                    return false; // Don't attack tamed entities owned by the same owner
                }
            }
        }
        if (target instanceof PlayerEntity & owner instanceof PlayerEntity) {
            if (!((PlayerEntity)owner).shouldDamagePlayer((PlayerEntity)target)) {
                return false; // Don't attack players if the owner doesn't want to damage them
            }
        }
        if (target instanceof TameableEntity tameable && tameable.isTamed() && tameable.getOwner() == owner) {
            return false; // Don't attack tamed entities owned by the same owner
        }
        return super.canAttackWithOwner(target, owner);
    }
    protected Brain.Profile<Chocobo> createBrainProfile() {
        return Brain.createProfile(ChocoboBrains.CHOCOBO_MODULES, ChocoboBrains.CHOCOBO_SENSORS);
    }
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return ChocoboBrains.makeBrain(this.createBrainProfile().deserialize(dynamic), this);
    }
    @SuppressWarnings("unchecked")
    public Brain<Chocobo> getBrain() {
        return (Brain<Chocobo>) super.getBrain();
    }
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }
    protected void initGoals() {
        super.initGoals(); // returns super, custom goals & targets are commented out to test Brains
        /*
        this.goalSelector.add(1, new ChocoboGoals.ChocoPanicGoal(this,1.5D));
        this.goalSelector.add(2, new MeleeAttackGoal(this,2F, true));
        this.goalSelector.add(3, new ChocoboMateGoal(this, 1.0D));
        this.goalSelector.add(4, new ChocoboGoals.ChocoboLavaEscape(this));
        this.goalSelector.add(5, new ChocoboGoals.ChocoboFollowOwnerGoal(this, 1.6, 10F, 300F));
        this.goalSelector.add(6, new TemptGoal(this, 1.2D, Ingredient.ofStacks(GYSAHL_GREEN_ITEM.getDefaultStack()), false));
        this.goalSelector.add(8, new ChocoboGoals.ChocoboAvoidPlayer(this));
        this.goalSelector.add(9, new ChocoboGoals.ChocoboRoamWonder(this, 1.0D)); // Roam & Wonder, uses MovementType check to allow to start & if it limits the roaming
        //this.goalSelector.add(9, new FleeEntityGoal<>(this, LlamaEntity.class, 15F, 1.3F, 1.5F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(11, new LookAroundGoal(this)); // moved after Roam, a little too stationary
        this.targetSelector.add(1, new ChocoboGoals.ChocoboOwnerHurtByGoal(this));
        this.targetSelector.add(2, new ChocoboGoals.ChocoboOwnerHurtGoal(this));
        this.targetSelector.add(3, new ChocoboGoals.ChocoboHurtByTargetGoal(this, Chocobo.class).setGroupRevenge(Chocobo.class));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, EndermiteEntity.class, false));
        this.targetSelector.add(6, new ActiveTargetGoal<>(this, SilverfishEntity.class, false));
        this.targetSelector.add(7, new UniversalAngerGoal<>(this, true));
        */
    }
    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        BlockState state = world.getBlockState(pos);
        if (state.getFluidState().isIn(FluidTags.WATER)) {
            // Prefer underwater ground
            return this.isWaterBreathing() ? 0.5F : this.isFireImmune() ? 0.2F : 0.3F;
        }
        if (this.isFireImmune() && state.getFluidState().isIn(FluidTags.LAVA)) {
            // Prefer lava surface
            return 1.0F;
        }
        return super.getPathfindingFavor(pos, world);
    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(ModAttributes.CHOCOBO_STAMINA, ChocoboConfig.DEFAULT_STAMINA.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, ChocoboConfig.DEFAULT_SPEED.get() / 100f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, ChocoboConfig.DEFAULT_HEALTH.get())
                .add(EntityAttributes.GENERIC_ARMOR, ChocoboConfig.DEFAULT_ARMOR.get())
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, ChocoboConfig.DEFAULT_ARMOR_TOUGHNESS.get())
                .add(EntityAttributes.GENERIC_ATTACK_SPEED)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, ChocoboConfig.DEFAULT_ATTACK_DAMAGE.get())
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, EntityAttributes.GENERIC_FOLLOW_RANGE.getDefaultValue()*3);
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(PARAM_IS_FLAME_BLOOD, false);
        this.dataTracker.startTracking(PARAM_IS_WATER_BREATH, false);
        this.dataTracker.startTracking(PARAM_WITHER_IMMUNE, false);
        this.dataTracker.startTracking(PARAM_POISON_IMMUNE, false);
        this.dataTracker.startTracking(PARAM_COLLAR_COLOR, 0);
        this.dataTracker.startTracking(PARAM_COLOR, ChocoboColor.YELLOW.ordinal());
        this.dataTracker.startTracking(PARAM_IS_MALE, false);
        this.dataTracker.startTracking(PARAM_FROM_EGG, false);
        this.dataTracker.startTracking(PARAM_MOVEMENT_TYPE, MovementType.WANDER.ordinal());
        this.dataTracker.startTracking(PARAM_SADDLE_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_ARMOR_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_WEAPON_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(PARAM_STAMINA, (float)ChocoboConfig.DEFAULT_STAMINA.get());
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
        this.chocoboSaddleInv.singleSlotFromNBT(compound.getCompound(NBTKEY_SADDLE_ITEM));
        this.chocoboWeaponInv.singleSlotFromNBT(compound.getCompound(NBTKEY_WEAPON_ITEM));
        this.chocoboArmorInv.singleSlotFromNBT(compound.getCompound(NBTKEY_ARMOR_ITEM));
        ChocoboBackboneFromNBT(compound);
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
        compound.put(NBTKEY_SADDLE_ITEM, this.chocoboSaddleInv.singleSlotToNBT());
        compound.put(NBTKEY_ARMOR_ITEM, this.chocoboArmorInv.singleSlotToNBT());
        compound.put(NBTKEY_WEAPON_ITEM, this.chocoboWeaponInv.singleSlotToNBT());
        ChocoboBackboneToNBT(compound);
        if (this.nestPos != null) { compound.put(NBTKEY_NEST_POSITION, NbtHelper.fromBlockPos(this.nestPos)); }
        compound.putInt(NBTKEY_CHOCOBO_GENERATION, this.getGeneration());
        compound.putBoolean(NBTKEY_CHOCOBO_FLAME_BLOOD, this.isFlameBlood());
        compound.putBoolean(NBTKEY_CHOCOBO_WATER_BREATH, this.isWaterBloodChocobo());
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
    public void ChocoboBackboneToNBT(NbtCompound compound) {
        for (int i = 0; i < this.chocoboBackboneInv.size(); ++i) {
            ItemStack itemStack = this.chocoboBackBoneList.get(i);
            if (!itemStack.isEmpty()) { compound.put(NBTKEY_INVENTORY+"_slot-"+i, itemStack.writeNbt(new NbtCompound())); }
        }
    }
    public void ChocoboBackboneFromNBT(NbtCompound compound) {
        for (int i = 0; i < this.chocoboBackboneInv.size(); ++i) {
            if (compound.contains(NBTKEY_INVENTORY+"_slot-"+i)) {
                ItemStack itemStack = ItemStack.fromNbt(compound.getCompound(NBTKEY_INVENTORY + "_slot-" + i));
                this.chocoboBackboneInv.setStack(i, itemStack);
                this.chocoboBackBoneList.set(i, itemStack);
            }
        }
    }
    // Leashing
    public boolean canBeLeashedBy(PlayerEntity player) { return false; }
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
        return new BlockPos(new Vec3i(x, y, z));
    }
    public boolean canWonder() { return this.getMovementType() == MovementType.WANDER; }
    public boolean isNoRoam() { return this.getMovementType() == MovementType.STANDSTILL; }
    public boolean followOwner() { return this.getMovementType() == MovementType.FOLLOW_OWNER; }
    public boolean followLure() { return this.getMovementType() == MovementType.FOLLOW_LURE; }
    // TODO - Convert double to int for leash distance, Fix where it is called.
    protected void setLeashedDistance(double distance) { this.dataTracker.set(PARAM_LEASH_LENGTH, (int) distance); }
    public int getLeashDistance() { return this.dataTracker.get(PARAM_LEASH_LENGTH); }
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
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) { return null; }
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, @Nullable EntityData spawnDataIn, @Nullable NbtCompound dataTag) {
        this.setMale(this.getWorld().random.nextBoolean());
        boolean skip;

        final RegistryEntry<Biome> currentBiomes = this.getWorld().getBiome(getBlockPos().down());
        //noinspection OptionalGetWithoutIsPresent
        final RegistryKey<Biome> biomeRegistryKey = currentBiomes.getKey().get();
        if (isEnd(worldIn)) { skip = !WorldConfig.CHOCOBO_SPAWN_SWITCH_THE_END.get(); }
        else if (isNether(worldIn)) { skip = !WorldConfig.CHOCOBO_SPAWN_SWITCH_NETHER.get(); }
        else if (isOverworld(worldIn)) { skip = !WorldConfig.CHOCOBO_SPAWN_SWITCH_OVERWORLD.get(); } else { skip = false; }
        if (!fromEgg() && !skip && reason != SpawnReason.SPAWNER) {
            setChocoboSpawnCheck(ChocoboColor.YELLOW);
            if (isNether(worldIn)) { setChocoboSpawnCheck(ChocoboColor.FLAME); }
            if (isEnd(worldIn)){ setChocoboSpawnCheck(ChocoboColor.PURPLE); }
            if (IS_MUSHROOM().contains(biomeRegistryKey)) { setChocoboSpawnCheck(ChocoboColor.PINK); }
            if (isSnowy(biomeRegistryKey) || isWhiteChocoboBiomes(biomeRegistryKey)) { setChocoboSpawnCheck(ChocoboColor.WHITE); }
            if (isBlueChocoboBiomes(biomeRegistryKey)) { setChocoboSpawnCheck(ChocoboColor.BLUE); }
            if (currentBiomes.isIn(IS_FOREST) || currentBiomes.isIn(IS_BADLANDS)) { setChocoboSpawnCheck(ChocoboColor.RED); }
            if (isGreenChocoboBiomes(biomeRegistryKey)) { setChocoboSpawnCheck(ChocoboColor.GREEN); }
            if (isHotOverWorld(biomeRegistryKey) && !isSavanna(biomeRegistryKey)) { setChocoboSpawnCheck(ChocoboColor.BLACK); }
            this.setChocoboScale(this.isMale(), 0, false);
        } else if (reason == SpawnReason.SPAWNER) {
            this.setChocoboScale(this.isMale(), 0, false);
        }
        chocoboStatShake(EntityAttributes.GENERIC_MAX_HEALTH, "health");
        chocoboStatShake(EntityAttributes.GENERIC_ATTACK_DAMAGE, "attack");
        chocoboStatShake(EntityAttributes.GENERIC_ARMOR, "defense");
        chocoboStatShake(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, "toughness");
        if (getChocoboColor() == ChocoboColor.PURPLE) {
            int chance = isEnd(worldIn) ? 60 : 15;
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
        this.setWaterBreath(isWaterBreathingChocobo(color));
        this.setWitherImmune(isWitherImmuneChocobo(color));
        this.setPoisonImmune(chocoboChecks.isPoisonImmuneChocobo(color));
    }
    private void setChocoboSpawnCheck(ChocoboColor color) {
        ChocoboColor chocobo = this.getChocoboColor();
        if ((chocobo == ChocoboColor.YELLOW || color == ChocoboColor.YELLOW) && chocobo != color) { setChocobo(color); }
    }

    // Combat related
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
    public int chocoStatMod() { return ChocoboConfig.DEFAULT_WEAPON_MOD.get(); }
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
    public ChocoboColor getChocoboColor() { return ChocoboColor.values()[this.dataTracker.get(PARAM_COLOR)]; }
    public void setChocoboColor(@NotNull ChocoboColor color) { this.dataTracker.set(PARAM_COLOR, color.ordinal()); }
    public void setCollarColor(Integer color) { this.dataTracker.set(PARAM_COLLAR_COLOR, color); }
    public Integer getCollarColor() { return this.dataTracker.get(PARAM_COLLAR_COLOR); }
    public boolean isFireImmune() { return this.isFlameBlood() || super.isFireImmune(); }
    public boolean isFlameBlood() { return this.dataTracker.get(PARAM_IS_FLAME_BLOOD); }
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
    public boolean isInvulnerableTo(@NotNull DamageSource source) {
        if (source.isIn(DamageTypeTags.IS_FALL)) { return true; }
        if (source.isOf(DamageTypes.SWEET_BERRY_BUSH)) { return true; }
        if (source.isOf(DamageTypes.CACTUS)) { return true; }
        if (source.isOf(DamageTypes.DRAGON_BREATH)) {
            ChocoboColor color = this.getChocoboColor();
            return color == ChocoboColor.GOLD || color == ChocoboColor.PURPLE;
        }
        if (source.isIn(DamageTypeTags.IS_FREEZING)) {
            ChocoboColor color = this.getChocoboColor();
            return color == ChocoboColor.GOLD || color == ChocoboColor.WHITE;
        }
        if (source.isIn(DamageTypeTags.IS_DROWNING)) { return this.isWaterBreathing(); }
        if (source.isOf(DamageTypes.WITHER_SKULL) || source.isOf(DamageTypes.WITHER)) { return this.isWitherImmune(); }
        return super.isInvulnerableTo(source);
    }
    public boolean canHaveStatusEffect(@NotNull StatusEffectInstance potionEffect) {
        if (potionEffect.getEffectType() == StatusEffects.WITHER) return !this.isWitherImmune();
        if (potionEffect.getEffectType() == StatusEffects.POISON) return !this.isPoisonImmune();
        return super.canHaveStatusEffect(potionEffect);
    }
    public void onStatusEffectApplied(@NotNull StatusEffectInstance effect, @Nullable Entity source) {
        super.onStatusEffectApplied(effect, source);
        if (effect.getEffectType() == StatusEffects.WATER_BREATHING) {
            this.setPathfindingPenalty(PathNodeType.WATER, -0.25F);
        }
        if (effect.getEffectType() == StatusEffects.FIRE_RESISTANCE) {
            this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -0.2F);
            this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -0.1F);
            this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        }
    }
    public void onStatusEffectRemoved(@NotNull StatusEffectInstance effect) {
        super.onStatusEffectRemoved(effect);
        if (effect.getEffectType() == StatusEffects.WATER_BREATHING) {
            this.setPathfindingPenalty(PathNodeType.WATER, this.isWaterBreathing() ? -0.25F : -0.15F);
        }
        if (effect.getEffectType() == StatusEffects.FIRE_RESISTANCE) {
            this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, this.isFireImmune() ? -0.2F : 32.0F);
            this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, this.isFireImmune() ? -0.1F : 16.0F);
            this.setPathfindingPenalty(PathNodeType.LAVA, this.isFireImmune() ? 0.0F : 16.0F);
        }
    }
    public void setChocoboScaleMod(float value) { this.dataTracker.set(PARAM_SCALE_MOD, value); }
    public boolean nonFlameFireImmune() { return isFireImmune() && ChocoboColor.FLAME != getChocoboColor(); }
    private int fruitAteTimer = 0;
    public boolean isWaterBreathing() { return this.isWaterBloodChocobo() || this.hasStatusEffect(StatusEffects.WATER_BREATHING); }
    public boolean isWaterBloodChocobo() { return this.dataTracker.get(PARAM_IS_WATER_BREATH); }
    public boolean isWitherImmune() { return this.dataTracker.get(PARAM_WITHER_IMMUNE); }
    public boolean isPoisonImmune() { return this.dataTracker.get(PARAM_POISON_IMMUNE); }
    public int getChocoboScale() { return this.dataTracker.get(PARAM_SCALE); }
    public float getChocoboScaleMod() { return this.dataTracker.get(PARAM_SCALE_MOD); }
    public float ScaleMod(int scale) { return (scale == 0) ? 0 : ((scale < 0) ? (((float) ((scale * -1) - 100) / 100) * -1) : (1f + ((float) scale / 100))); }
    public boolean isMale() { return this.dataTracker.get(PARAM_IS_MALE); }
    public boolean fromEgg() { return this.dataTracker.get(PARAM_FROM_EGG); }
    public void setMale(boolean isMale) { this.dataTracker.set(PARAM_IS_MALE, isMale); }
    public void setFromEgg(boolean fromEgg) { this.dataTracker.set(PARAM_FROM_EGG, fromEgg); }
    public MovementType getMovementType() { return MovementType.values()[this.dataTracker.get(PARAM_MOVEMENT_TYPE)]; }
    public void setMovementType(@NotNull MovementType type) { this.dataTracker.set(PARAM_MOVEMENT_TYPE, type.ordinal()); setMovementAiByType(type); }
    private void setMovementAiByType(@NotNull MovementType type) {
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
    public boolean canBreatheInWater() { return this.isWaterBreathing(); }
    public float getStamina() { return this.dataTracker.get(PARAM_STAMINA); }
    public void setStamina(float value) { this.dataTracker.set(PARAM_STAMINA, value); }
    public float getStaminaPercentage() { return (float) (this.getStamina() / Objects.requireNonNull(this.getAttributeInstance(ModAttributes.CHOCOBO_STAMINA)).getValue()); }
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

        float maxStamina = (float) Objects.requireNonNull(this.getAttributeInstance(ModAttributes.CHOCOBO_STAMINA)).getValue();
        float newStamina = MathHelper.clamp(curStamina - value, 0, maxStamina);
        this.dataTracker.set(PARAM_STAMINA, newStamina);
        return true;
    }
    public double getMountedHeightOffset() {
        double scaleZero = this.getChocoboScale() == 0 ? 1.7D : this.getChocoboScale() > 0 ? 1.55D : 1.85D;
        return (scaleZero * this.getChocoboScaleMod());
    }
    public void dismountVehicle() {
        BlockPos spot = this.getBlockPos();
        double length = 5D;
        this.setLeashSpot(spot);
        this.setLeashedDistance(length);
        this.setMovementType(MovementType.STANDSTILL); // stand still
        this.setMovementTypeByFollowMrHuman(this.followingMrHuman);
        super.dismountVehicle();
    }
    public Entity getPrimaryPassenger() { return this.getPassengerList().isEmpty() ? null : this.getPassengerList().get(0); }
    @Override
    @Nullable
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
    private void updateInWaterStateAndDoWaterCurrentPushing() {
        if (!this.isWaterBreathing()) {
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

            if (this.isOnGround()) { this.isChocoboJumping = false; }

            if (this.isLogicalSideForUpdatingMovement()) {
                if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                    // jump logic
                    if (!this.isChocoboJumping && this.isOnGround() && this.useStamina(FloatChocoConfigGet(ChocoboConfig.STAMINA_JUMP_USE.get(), dSTAMINA_JUMP.getDefault()))) {
                        Vec3d motion = getVelocity();
                        setVelocity(new Vec3d(motion.x, .6f, motion.z));
                        this.isChocoboJumping = true;
                    }
                }
                if (rider.isTouchingWater()) {
                    Vec3d motion = getVelocity();
                    if (MinecraftClient.getInstance().options.jumpKey.isPressed()) { setVelocity(new Vec3d(motion.x, .5f, motion.z)); }
                    else if (this.getVelocity().y < 0 && !this.isWaterBreathing()) {
                        int distance = WorldUtils.getDistanceToSurface(this.getBlockPos(), this.getEntityWorld());
                        if (distance > 0) { setVelocity(new Vec3d(motion.x, .05f, motion.z)); }
                    } else if (this.isWaterBreathing() && isChocoboWaterGlide()) {
                        Vec3d waterMotion = getVelocity();
                        setVelocity(new Vec3d(waterMotion.x, waterMotion.y * 0.65F, waterMotion.z));
                    }
                }
                if (rider.isInLava()) {
                    Vec3d motion = getVelocity();
                    if (MinecraftClient.getInstance().options.jumpKey.isPressed()) { setVelocity(new Vec3d(motion.x, .5f, motion.z)); }
                    else if (this.isFireImmune() && this.getVelocity().y < 0) {
                        int distance = WorldUtils.getDistanceToSurface(this.getBlockPos(), this.getEntityWorld());
                        if (distance > 0) { setVelocity(new Vec3d(motion.x, .05f, motion.z)); }
                    }
                }
                // Insert override for slow-fall Option on Chocobo
                if (!this.isOnGround() && !this.isTouchingWater() && !this.isInLava() && !isChocoShiftDown() && this.getVelocity().y < 0 &&
                        this.useStamina(FloatChocoConfigGet(ChocoboConfig.STAMINA_GLIDE_USE.get(), dSTAMINA_GLIDE.getDefault()))) {
                    if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                        Vec3d motion = getVelocity();
                        setVelocity(new Vec3d(motion.x, motion.y * 0.65F, motion.z));
                    }
                }
                if ((this.isSprinting() && !this.useStamina(FloatChocoConfigGet(ChocoboConfig.STAMINA_SPRINT_USE.get(), dSTAMINA_SPRINT.getDefault())) || (this.isSprinting() && this.isTouchingWater() && this.useStamina(FloatChocoConfigGet(ChocoboConfig.STAMINA_SPRINT_USE.get(), dSTAMINA_SPRINT.getDefault()))))) { this.setSprinting(false); }

                this.setMovementSpeed((float) Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue());
                super.travel(newVector);
            }
        } else {
            if (!this.isOnGround() && !this.isTouchingWater() && !this.isInLava() && this.getVelocity().y < 0 && this.useStamina(FloatChocoConfigGet(ChocoboConfig.STAMINA_GLIDE_USE.get(), dSTAMINA_SPRINT.getDefault()))) {
                Vec3d motion = getVelocity();
                setVelocity(new Vec3d(motion.x, motion.y * 0.65F, motion.z));
            }
            double y = newVector.y;
            if (y > 0) y = y * -1;
            Vec3d cappedNewVector = new Vec3d(newVector.x, y, newVector.z);
            super.travel(cappedNewVector);
        }
    }
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        if (!this.hasPassenger(passenger)) { return; }
        if (passenger instanceof MobEntity && this.getPrimaryPassenger() == passenger) { this.bodyYaw = ((LivingEntity) passenger).bodyYaw; }
        double d = this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset();
        positionUpdater.accept(passenger, this.getX(), d, this.getZ());
    }
    public void tick() {
        super.tick();
        this.fruitAteTimer = this.fruitAteTimer > 0 ? this.fruitAteTimer - 1 : 0;
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
                        heal(ChocoboConfig.DEFAULT_HEALING.get());
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
                    }
                }
            }
        }
    }
    private void floatChocobo() {
        if (this.isInLava()) {
            ShapeContext collisionContext = ShapeContext.of(this);
            if (collisionContext.isAbove(FluidBlock.COLLISION_SHAPE, this.getBlockPos(), true) && !this.getWorld().getFluidState(this.getBlockPos().up()).isIn(FluidTags.LAVA)) { this.setOnGround(true); }
            else { this.setVelocity(this.getVelocity().multiply(.003D).add(0.0D, 0.05D, 0.0D)); }
        }
        if (this.isTouchingWater() && !this.isWaterBreathing()) {
            ShapeContext collisionContext = ShapeContext.of(this);
            if (collisionContext.isAbove(FluidBlock.COLLISION_SHAPE, this.getBlockPos(), true) && !this.getWorld().getFluidState(this.getBlockPos().up()).isIn(FluidTags.WATER)) { this.setOnGround(true); }
            else { this.setVelocity(this.getVelocity().multiply(.003D).add(0.0D, 0.05D, 0.0D)); }
        }
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
        PathNodeType pathNodeTypes = LandPathNodeMaker.getLandNodeType(this.getWorld(), pPos.mutableCopy());
        if (pathNodeTypes != PathNodeType.WALKABLE) { return false; }
        else {
            BlockPos blockpos = pPos.subtract(this.getBlockPos());
            return this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(blockpos));
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
    protected boolean canStartRiding(@NotNull Entity entityIn) { return false; }
    public void tickMovement() {
        super.tickMovement();
        this.setRotation(this.getYaw(), this.getPitch());
        this.regenerateStamina();
        this.setStepHeight(maxStepUp);
        this.fallDistance = 0f;

        if (this.TimeSinceFeatherChance == 3000) {
            this.TimeSinceFeatherChance = 0;
            if ((float) random() < .25) { this.dropFeather(); }
        } else { this.TimeSinceFeatherChance++; }

        //Change effects to chocobo colors
        if (!this.getEntityWorld().isClient()) {
            if (this.age % 60 == 0) {
                if (this.isFireImmune()) {
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 100, 0, true, false));
                    if (this.hasPassengers()) {
                        Entity controller = this.getPrimaryPassenger();
                        if (controller instanceof PlayerEntity) { ((PlayerEntity) controller).addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 100, 0, true, false)); }
                    }
                }
                if (this.isWaterBreathing()) {
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 100, 0, true, false));
                    if (this.hasPassengers()) {
                        Entity controller = this.getPrimaryPassenger();
                        if (controller instanceof PlayerEntity) { ((PlayerEntity) controller).addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 100, 0, true, false)); }
                    }
                }
            }
        } else {
            // Wing rotations, control packet
            // Client side
            this.destPos += (float) ((double) (this.isOnGround() ? -1 : 4) * 0.3D);
            this.destPos = MathHelper.clamp(destPos, 0f, 1f);

            if (!this.isOnGround()) { this.wingRotDelta = Math.min(wingRotation, 1f); }
            this.wingRotDelta *= 0.9F;
            this.wingRotation += this.wingRotDelta * 2.0F;

            if (this.isOnGround()) {
                double d1 = this.getX() - this.prevX;
                double d0 = this.getZ() - this.prevZ;
                float f4 = ((float)Math.sqrt(d1 * d1 + d0 * d0)) * 4.0F;
                if (f4 > 1.0F) { f4 = 1.0F; }
                this.limbAnimator.updateLimbs(f4, 0.4F);
            } else {
                this.limbAnimator.setSpeed(0);
                this.limbAnimator.updateLimbs(0,0);
            }
        }
    }
    private void regenerateStamina() {
        if (!this.isOnGround() && !this.isSprinting()) { return; }
        float regen = FloatChocoConfigGet(ChocoboConfig.STAMINA_REGEN.get(), dSTAMINA_REGEN.getDefault());

        // half the amount of regeneration while moving
        Vec3d motion = getVelocity();
        if (motion.x != 0 || motion.z != 0) { regen *= 0.85F; }

        // TODO: implement regen bonus (another IAttribute?)
        this.useStamina(-regen);
    }
    public boolean isBreedingItem(@NotNull ItemStack stack) { return false; }
    private final Map<Item, Integer> COLLAR_COLOR = Util.make(Maps.newHashMap(), (map) ->{
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
    private boolean interactInvRide(PlayerEntity player, ItemStack stack) {
        Item pStack = stack.getItem();
        if (this.getEntityWorld().isClient()) { return false; }
        if (this.isBaby()) {
            if (pStack == GYSAHL_CAKE.asItem()) {
                this.eat(player, Hand.MAIN_HAND, stack);
                this.growUp(25);
                return true;
            } else { return false; }
        }
        if (!this.isTamed()) { return false; }
        if (player.isSneaking()) {
            if (player instanceof ServerPlayerEntity) { this.displayChocoboInventory((ServerPlayerEntity) player); }
            return true;
        } else if (stack.isEmpty() && this.isSaddled()) {
            if (ChocoboConfig.OWNER_ONLY_ACCESS.get()) {
                if (isOwner(player)) { player.startRiding(this); }
                else { player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.not_owner"), true); }
            } else { player.startRiding(this); }
            return true;
        }
        return false;
    }
    private boolean interactFeed(PlayerEntity player, ItemStack stack, Hand hand) {
        Item pStack = stack.getItem();
        if (this.getEntityWorld().isClient()) { return false; }
        if (pStack == GYSAHL_GREEN_ITEM) {
            if (this.isTamed()) {
                if (this.getHealth() != this.getMaxHealth()) {
                    this.eat(player, hand, stack);
                    heal(ChocoboConfig.DEFAULT_HEALING.get());
                    return true;
                } else { player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.heal_fail"), true); }
            } else {
                this.eat(player, hand, player.getInventory().getMainHandStack());
                if ((float) random() < ChocoboConfig.TAME_CHANCE.get() || player.isCreative()) {
                    this.setOwnerUuid(player.getUuid());
                    this.setTamed(true);
                    this.setCollarColor(16);
                    player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.tame_success"), true);
                    if (!this.hasCustomName()) { this.setCustomName(getChocoName()); }
                    this.setCustomNameVisible(true);
                } else { player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.tame_fail"), true); }
            }
        }
        if (this.fruitAteTimer < 1) {
            boolean ate = false;
            if (pStack == GOLDEN_GYSAHL_GREEN) {
                increaseStat("All", player);
                this.fruitAteTimer = ChocoboConfig.FRUIT_COOL_DOWN.get();
                ate = true;
            }
            if (pStack == PINK_GYSAHL_GREEN) {
                increaseStat("HP", player);
                this.fruitAteTimer = ChocoboConfig.FRUIT_COOL_DOWN.get();
                ate =  true;
            }
            if (pStack == DEAD_PEPPER) {
                increaseStat("Attack", player);
                this.fruitAteTimer = ChocoboConfig.FRUIT_COOL_DOWN.get();
                ate =  true;
            }
            if (pStack == SPIKE_FRUIT) {
                increaseStat("Defence", player);
                this.fruitAteTimer = ChocoboConfig.FRUIT_COOL_DOWN.get();
                ate =  true;
            }
            if (ate) {
                if (this.getHealth() != this.getMaxHealth()) {
                    this.eat(player, hand, stack);
                    heal(ChocoboConfig.DEFAULT_HEALING.get());
                    if (this.isBaby()) { this.growUp(25); }
                }
                return true;
            }
        }
        if (pStack == LOVELY_GYSAHL_GREEN) {
            if (!this.isInLove() && !this.isBaby()) {
                this.eat(player, hand, stack);
                this.lovePlayer(player);
                return true;
            } else { return false; }
        }
        return false;
    }
    private boolean interactEquip(PlayerEntity player, ItemStack stack) {
        Item pStack = stack.getItem();
        if (this.getEntityWorld().isClient()) { return false; }
        if (this.isBaby()) { return false; }
        if (pStack instanceof ChocoboSaddleItem && !this.isSaddled()) {
            this.setSaddleType(stack);
            this.chocoboSaddleInv.setStack(0, stack.copy().split(1));
            player.getMainHandStack().decrement(1);
            return true;
        }
        if (pStack instanceof ChocoboArmorItems && !this.isArmored()) {
            this.setArmorType(stack);
            this.chocoboArmorInv.setStack(0, stack.copy().split(1));
            player.getMainHandStack().decrement(1);
            return true;
        }
        if (pStack instanceof ChocoboWeaponItems && !this.isArmed()) {
            this.setWeaponType(stack);
            this.chocoboWeaponInv.setStack(0, stack.copy().split(1));
            player.getMainHandStack().decrement(1);
            return true;
        }
        return false;
    }
    private boolean interactUtil(PlayerEntity player, ItemStack stack) {
        Item pStack = stack.getItem();
        if (this.getEntityWorld().isClient()) { return false; }
        if (pStack == NAME_TAG) {
            if (!stack.hasCustomName()) { return false; }
            if (isTamed()) {
                if (ChocoboConfig.OWNER_ONLY_ACCESS.get()) {
                    if (isOwner(player)) {
                        this.setCustomName(stack.getName());
                        this.setCustomNameVisible(true);
                        player.getMainHandStack().decrement(1);
                    } else { player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.not_owner"), true); }
                    return true;
                }
            } else {
                this.setCustomName(stack.getName());
                this.setCustomNameVisible(true);
                player.getMainHandStack().decrement(1);
                return true;
            }
        }
        if (this.isTamed()) {
            if (pStack == CHOCOBO_FEATHER.asItem()) {
                if (isOwner(player)) {
                    this.setCustomNameVisible(!this.isCustomNameVisible());
                    player.getMainHandStack().decrement(1);
                    return true;
                } else {
                    player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.not_owner"), true);
                }
            }
            if (pStack instanceof ChocoboLeashPointer item) {
                BlockPos center = item.getCenterPoint();
                BlockPos leash = item.getLeashPoint();
                double dist = (double) Math.max(Math.min(item.getLeashDistance(), 40), 6) /2;
                if (leash == null || center == null) { return false; }
                String name = this.getCustomName() == null ? this.getName().getString() : this.getCustomName().getString();
                player.sendMessage(Text.literal(name + " Area Set: "+dist+ " around X: " + center.getX() + " Z: " + center.getZ()), true);
                this.setLeashSpot(center);
                this.setLeashedDistance(dist);
                return true;
            }
        }
        if (this.isBaby()) { return false; }
        if (pStack == CHOCOBO_WHISTLE) {
            if (isOwner(player)) {
                if (this.followingMrHuman == 3) {
                    this.playSound(ModSounds.WHISTLE_SOUND_FOLLOW, 1.0F, 1.0F);
                    this.setAiDisabled(false);
                    this.setMovementType(MovementType.FOLLOW_OWNER);
                    player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.chocobo_follow_cmd"), true);
                } else if (this.followingMrHuman == 1) {
                    this.playSound(ModSounds.WHISTLE_SOUND_WANDER, 1.0F, 1.0F);
                    this.setMovementType(MovementType.WANDER);
                    player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.chocobo_wander_cmd"), true);
                } else if (this.followingMrHuman == 2) {
                    this.playSound(ModSounds.WHISTLE_SOUND_STAY, 1.0F, 1.0F);
                    this.setMovementType(MovementType.STANDSTILL);
                    BlockPos leashPoint = this.getSteppingPos();
                    double distance = 10D;
                    this.setLeashedDistance(distance);
                    this.setLeashSpot(leashPoint);
                    player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.chocobo_stay_cmd"), true);
                }
            } else { player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.not_owner"), true); }
            return true;
        }
        return false;
    }
    public boolean interactDye(PlayerEntity player, ItemStack stack) {
        Item pStack = stack.getItem();
        if (this.getEntityWorld().isClient()) { return false; }
        if (!this.isTamed()) { return false; }
        if (getDyeList().contains(pStack)) {
            if (!Objects.equals(this.getCollarColor(), COLLAR_COLOR.get(pStack))) {
                this.setCollarColor(COLLAR_COLOR.get(pStack));
                player.getMainHandStack().decrement(1);
                return true;
            }
        }
        return false;
    }
    public ActionResult interactAt(@NotNull PlayerEntity player, Vec3d vec, Hand hand) {
        if (this.getEntityWorld().isClient()) return ActionResult.PASS;
        ItemStack heldItemStack = player.getStackInHand(hand);
        Item defaultHand = heldItemStack.getItem();
        if (interactInvRide(player, heldItemStack)) { return ActionResult.SUCCESS; }
        if (interactFeed(player, heldItemStack, hand)) { return ActionResult.SUCCESS; }
        if (interactEquip(player, heldItemStack)) { return ActionResult.SUCCESS; }
        if (interactUtil(player, heldItemStack)) { return ActionResult.SUCCESS; }
        if (interactDye(player, heldItemStack)) { return ActionResult.SUCCESS; }
        return super.interactAt(player, vec, hand);
    }
    private void increaseStat(@NotNull String type, PlayerEntity player) {
        int statValues = 0;
        if (type.equals("All")) { statValues = 31; }
        if (type.equals("HP")) { statValues = 24; }
        if (type.equals("Attack")) { statValues = 1; }
        if (type.equals("Defence")) {
            if (ChocoboConfig.MAX_ARMOR_TOUGHNESS.get() > this.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)) {
                if (ChocoboConfig.MAX_ARMOR.get() > this.getAttributeValue(EntityAttributes.GENERIC_ARMOR)) { statValues = 6; }
                else { statValues = 4; }
            } else { statValues = 2; }
        }
        if (statValues > 0) { numSplit(statValues, player); }
    }
    private void numSplit(int value, PlayerEntity player) {
        int hold = value;
        String health = "hp";
        String strength = "str";
        String armor = "arm";
        String armorTough = "arm_tough";
        String stamina = "sta";
        String dualDefense = "defences";
        String flipDefense = "xDefence";
        int chk_hold = statCount(hold, 16);
        if (chk_hold >= 0) {
            hold = chk_hold;
            this.statSwitch(stamina, player);
        }
        chk_hold = statCount(hold, 8);
        if (chk_hold >= 0) {
            hold = chk_hold;
            this.statSwitch(health, player);
        }
        chk_hold = statCount(hold, 4);
        if (chk_hold >= 0) {
            hold = chk_hold;
            if (chk_hold > 2) {
                hold = hold-2;
                this.statSwitch(dualDefense, player);
            } else if (chk_hold == 2) {
                hold = 0;
                this.statSwitch(flipDefense, player);
            } else { this.statSwitch(armorTough, player); }
        }
        chk_hold = statCount(hold, 2);
        if (chk_hold >= 0) {
            hold = chk_hold;
            this.statSwitch(armor, player);
        }
        chk_hold = statCount(hold, 1);
        if (chk_hold >= 0) { this.statSwitch(strength, player); }
    }
    private int statCount(int statNumber, int checkNumber) { return statNumber - checkNumber; }
    private void statSwitch(@NotNull String key, PlayerEntity player) {
        String loop = key;
        if (loop.matches("defences")) {
            this.statSwitch("arm", player);
            loop = "arm_tough";
        }
        if (loop.matches("xDefence")) { loop = .50f > (float) random() ? "arm" : "arm_tough"; }
        switch (loop) {
            case "arm" -> this.statPlus(EntityAttributes.GENERIC_ARMOR, ChocoboConfig.MAX_ARMOR.get(), loop, player);
            case "arm_tough" -> this.statPlus(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, ChocoboConfig.MAX_ARMOR_TOUGHNESS.get(), loop, player);
            case "str" -> this.statPlus(EntityAttributes.GENERIC_ATTACK_DAMAGE, ChocoboConfig.MAX_ATTACK.get(), loop, player);
            case "sta" -> this.statPlus(ModAttributes.CHOCOBO_STAMINA, ChocoboConfig.MAX_STAMINA.get(), loop, player);
            case "hp" -> this.statPlus(EntityAttributes.GENERIC_MAX_HEALTH, ChocoboConfig.MAX_HEALTH.get(), loop, player);
        }
    }
    private void statPlus(EntityAttribute stat, double max, String key, @NotNull PlayerEntity player) {
        if (!player.getWorld().isClient()) {
            double base = this.getAttributeInstance(stat) != null ? Objects.requireNonNull(this.getAttributeInstance(stat)).getValue() : -10;
            boolean trip = base + (double) 1 >= max;
            if (trip) { trip = base >= max; }
            if (base != -10 && !trip) {
                Objects.requireNonNull(this.getAttributeInstance(stat)).addPersistentModifier(new EntityAttributeModifier(stat + " food", 1, EntityAttributeModifier.Operation.ADDITION));
            }

            String keys = ".entity_chocobo." + key;
            if (trip) { keys = keys + ".full"; }
            else { keys = keys + ".room"; }
            player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + keys, this.getCustomName()));
        }
    }
    private void displayChocoboInventory(@NotNull ServerPlayerEntity player) {
        if (player.currentScreenHandler != player.playerScreenHandler) {
            player.closeHandledScreen();
        }
        ((ServerPlayerEntityAccessor) player).callIncrementScreenHandlerSyncId();
        int syncId = ((ServerPlayerEntityAccessor) player).getScreenHandlerSyncId();
        player.networkHandler.sendPacket(new OpenHorseScreenS2CPacket(syncId, this.chocoboBackboneInv.size(), this.getId()));
        player.currentScreenHandler = new SaddlebagContainer(syncId, player.getInventory(), this);
        ((ServerPlayerEntityAccessor) player).callOnScreenHandlerOpened(player.currentScreenHandler);
    }
    private void chocoboFeatherPick(@NotNull Inventory sendingInv, @NotNull Inventory receivingInv, int slot) {
        boolean isReverseTierOne = sendingInv.size() > receivingInv.size();
        boolean isTierOne = sendingInv.size() < receivingInv.size();
        boolean pick = true;
        int slotAdjust = slot;
        if (isTierOne) {
            if (slot < 5) { slotAdjust = slot + 11; }
            if (slot > 4 && slot < 10) { slotAdjust = slot + 15; }
            if (slot > 9) { slotAdjust = slot + 19; }
        }
        if (isReverseTierOne) {
            if (slot > 10 && slot < 16) { slotAdjust = slot-11; }
            if (slot > 19 && slot < 25) { slotAdjust = slot-15; }
            if (slot > 28 && slot < 34) { slotAdjust = slot-19; }
            pick = slotAdjust != slot;
        }
        if (pick) { if (receivingInv.getStack(slotAdjust) != sendingInv.getStack(slot)) { receivingInv.setStack(slotAdjust, sendingInv.getStack(slot)); } }
    }
    protected SoundEvent getAmbientSound() { return AMBIENT_SOUND; }
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) { return AMBIENT_SOUND; }
    protected SoundEvent getDeathSound() { return AMBIENT_SOUND; }
    protected float getSoundVolume() { return .6f; }
    public int getMinAmbientSoundDelay() { return (24 * (int) (random() * 100)); }
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public boolean canSpawn(@NotNull WorldAccess worldIn, @NotNull SpawnReason spawnReasonIn) {
        World world = this.getWorld();
        ServerWorldAccess theWorld = Objects.requireNonNull(world.getServer()).getWorld(world.getRegistryKey());
        RegistryKey<Biome> biomes = theWorld.getBiome(getBlockPos().down()).getKey().get();
        int multi = IS_SPARSE().contains(biomes) ? 2 : IS_OCEAN().contains(biomes) ? 3 : 1;
        int sizeCtrl = IS_SPARSE().contains(biomes) ? 8 : IS_OCEAN().contains(biomes) ? 8 : 15;
        List<Chocobo> bob = world.getNonSpectatingEntities(Chocobo.class, spawnControlBoxSize(new Box(getBlockPos()), multi));
        if (bob.size() > sizeCtrl) { return false; }
        if (isEnd(theWorld)) { return !this.getWorld().getBlockState(getBlockPos().down()).isAir(); }
        if (isNether(theWorld)) { return true; }
        if (IS_OCEAN().contains(biomes)) { return !isOceanBlocked(biomes, true); }
        return super.canSpawn(worldIn, spawnReasonIn);
    }
    protected void onTamedChanged() {
        super.onTamedChanged();
    }
    @Override
    public int getAngerTime() { return this.remainingPersistentAngerTime; }
    public void setAngerTime(int angerTime) { this.remainingPersistentAngerTime = angerTime; }
    public double getFollowSpeedModifier() { return this.followSpeedModifier; }
    @Nullable
    public UUID getAngryAt() { return this.persistentAngerTarget; }
    public void setAngryAt(@Nullable UUID angryAt) { this.persistentAngerTarget = angryAt; }
    @Override
    public void chooseRandomAngerTime() { this.setAngerTime(PERSISTENT_ANGER_TIME.get(this.random)); }
    protected void mobTick() {
        Brain<Chocobo> brain = this.getBrain();
        brain.tick((ServerWorld) this.getWorld(), this);
        brain.resetPossibleActivities(ImmutableList.of(Activity.IDLE, Activity.AVOID, Activity.PANIC, Activity.FIGHT));
        this.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
        this.tickAngerLogic((ServerWorld) this.getWorld(), true);
        if (this.getTarget() != null) { this.maybeAlertOthers(); }
        if (this.hasAngerTime()) { this.playerHitTimer = this.age; }
        super.mobTick();
    }
    private void maybeAlertOthers() {
        if (this.ticksUntilNextAlert > 0) { --this.ticksUntilNextAlert; }
        else {
            if (this.getVisibilityCache().canSee(this.getTarget())) { this.alertOthers(); }
            this.ticksUntilNextAlert = ALERT_INTERVAL.get(this.random);
        }
    }
    private void alertOthers_old() {
        double d0 = this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        Box aabb = Box.from(this.getPos()).expand(d0, 10.0D, d0);
        this.getWorld().getNonSpectatingEntities(Chocobo.class, aabb).stream()
                .filter((p_34463_) -> p_34463_ != this)
                .filter((p_34461_) -> p_34461_.getTarget() == null)
                .filter((p_34456_) -> !p_34456_.isTeamPlayer(Objects.requireNonNull(this.getTarget()).getScoreboardTeam()))
                .forEach((p_34440_) -> p_34440_.setTarget(this.getTarget()));
    }
    private void alertOthers() {
        double followRange = this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        Box alertBox = Box.from(this.getPos()).expand(followRange, 10.0D, followRange);

        List<Chocobo> nearbyChocobos = this.getWorld().getNonSpectatingEntities(Chocobo.class, alertBox);

        LivingEntity attacker = this.getTarget();
        if (attacker == null) return;

        for (Chocobo chocobo : nearbyChocobos) {
            if (shouldAlert(chocobo, attacker)) {
                alertChocobo(chocobo, attacker);
            }
        }
    }

    private boolean shouldAlert(Chocobo chocobo, LivingEntity attacker) {
        return chocobo != this
                && !chocobo.isAttacking()
                && chocobo.getOwner() == this.getOwner()
                && chocobo.canSee(attacker)
                && !chocobo.isBaby();
    }

    private void alertChocobo(Chocobo chocobo, LivingEntity attacker) { chocobo.getBrain().remember(MemoryModuleType.ATTACK_TARGET, attacker, 200L);
    }
    public boolean isPersistent() { return this.isTamed() || this.fromEgg() || this.isCustomNameVisible(); }
    public boolean cannotDespawn() {
        return this.hasVehicle() || this.isPersistent();
    }
    public boolean isSitting() { return false; }
    public boolean isDisallowedInPeaceful() { return super.isDisallowedInPeaceful(); }
    public boolean canImmediatelyDespawn(double pDistanceToClosestPlayer) { return true; }
    public void applyDamageEffects(LivingEntity attacker, Entity target) {
        boolean result = ChocoboCombatEvents.onChocoboCombatGetHit(attacker, this);
        if (result) { super.applyDamageEffects(attacker, target); }
    }
    public void onDeath(DamageSource source) {
        ChocoboCombatEvents.onChocoboDeath(this);
        super.onDeath(source);
    }
    public void checkDespawn() {
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) { this.discard(); }
        else if (!this.isPersistent() && !this.cannotDespawn()) {
            Entity entity = this.getWorld().getClosestPlayer(this, -1.0D);
            if (entity != null) {
                double d0 = entity.squaredDistanceTo(this);
                int i = CREATURE.getImmediateDespawnRange()*5;
                int j = i * i;
                if (d0 > (double)j && this.canImmediatelyDespawn(d0)) { this.discard(); }

                int k = (CREATURE.getImmediateDespawnRange()*2);
                int l = k * k;
                if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && d0 > (double)l && this.canImmediatelyDespawn(d0)) { this.discard();}
                else if (d0 < (double)l) { this.despawnCounter = 0; }
            }
        } else { this.despawnCounter = 0; }
    }
    // Ride Related
    public int getRideTickDelay() { return this.rideTickDelay; }
    // Method to get World, Used by 'default public LivingEntity getOwner()' to get Owner by UUID in the world.
    public EntityView method_48926() { return super.getWorld(); }
}