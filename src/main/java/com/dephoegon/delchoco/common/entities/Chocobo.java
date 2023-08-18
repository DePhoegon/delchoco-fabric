package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.entities.properties.ChocoboGoals.ChocoboLocalizedWonder;
import com.dephoegon.delchoco.common.entities.properties.ChocoboGoals.ChocoboRandomStrollGoal;
import com.dephoegon.delchoco.common.entities.properties.ChocoboInventory;
import com.dephoegon.delchoco.common.entities.properties.ModDataSerializers;
import com.dephoegon.delchoco.common.entities.properties.MovementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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
    public Chocobo(EntityType<? extends Chocobo> entityType, World world) {
        super(entityType, world);
    }
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
    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;  // Temporary
    }

    @Override
    public int getAngerTime() { return this.remainingPersistentAngerTime; }
    public void setAngerTime(int angerTime) { this.remainingPersistentAngerTime = angerTime; }
    public double getFollowSpeedModifier() { return this.followSpeedModifier; }
    public int getRideTickDelay() { return this.rideTickDelay; }
    @Nullable
    public UUID getAngryAt() { return this.persistentAngerTarget; }
    public void setAngryAt(@Nullable UUID angryAt) { this.persistentAngerTarget = angryAt; }
    @Override
    public void chooseRandomAngerTime() { this.setAngerTime(PERSISTENT_ANGER_TIME.get(this.random)); }
    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
}