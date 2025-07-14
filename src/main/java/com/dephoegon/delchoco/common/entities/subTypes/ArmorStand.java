package com.dephoegon.delchoco.common.entities.subTypes;

import com.dephoegon.delchoco.common.entities.AbstractChocobo;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import com.dephoegon.delchoco.utils.RandomHelper;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.common.inventory.ChocoboEquipmentSlot.*;
import static com.dephoegon.delchoco.common.inventory.ChocoboEquipmentSlot.ARMOR_SLOT;
import static com.dephoegon.delchoco.common.inventory.ChocoboEquipmentSlot.FEET_SLOT;
import static com.dephoegon.delchoco.common.inventory.ChocoboEquipmentSlot.HEAD_SLOT;
import static com.dephoegon.delchoco.common.inventory.ChocoboEquipmentSlot.LEGS_SLOT;
import static com.dephoegon.delchoco.common.inventory.ChocoboEquipmentSlot.SADDLE_SLOT;
import static com.dephoegon.delchoco.common.inventory.ChocoboEquipmentSlot.WEAPON_SLOT;
import static net.minecraft.sound.SoundCategory.BLOCKS;

public class ArmorStand extends Chocobo {
    public ArmorStand(EntityType<? extends Chocobo> entityType, World world) {
        super(entityType, world);
    }

    // NBT keys for pose data
    private static final String NBT_KEY_POSE_DATA = "PoseData";

    // Data trackers for equipment and properties
    private static final TrackedData<String> POSE_TYPE = DataTracker.registerData(ArmorStand.class, TrackedDataHandlerRegistry.STRING);

    // Pose data storage
    private ChocoboArmorStandPose chocoboModelPose = new ChocoboArmorStandPose();

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

    // Inventory for saddlebags/backpack items
    public final ChocoboArmorStandInventory inventory = new ChocoboArmorStandInventory(this);

    // Break attempt tracking
    private int breakAttempts = 0;
    private static final int REQUIRED_BREAK_ATTEMPTS = 5; // Requires 5 consistent break attempts
    private long lastBreakAttempt = 0;
    private static final long BREAK_TIMEOUT = 3000; // 3-second timeout between attempts

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(POSE_TYPE, ChocoboModelPose.DEFAULT.getId());
        super.initDataTracker();
    }


    // Pose management
    public EntityPose getPose() { return getDefaultPose(); }
    public ChocoboArmorStandPose getChocoboModelPose() { return this.chocoboModelPose; }
    public void setChocoboArmorPose(ChocoboArmorStandPose pose) {
        this.chocoboModelPose = pose;
        this.dataTracker.set(POSE_TYPE, pose.getType().getId());
    }
    public ChocoboModelPose getPoseType() { return ChocoboModelPose.fromId(this.dataTracker.get(POSE_TYPE)); }
    public void setPoseType(ChocoboModelPose type) { this.dataTracker.set(POSE_TYPE, type.getId()); }

    // Equipment by slot
    public ItemStack getEquipment(int slot) {
        return switch (slot) {
            case SADDLE_SLOT -> getSaddle();
            case ARMOR_SLOT -> getArmor();
            case WEAPON_SLOT -> getWeapon();
            case HEAD_SLOT -> getHead();
            case LEGS_SLOT -> getLegs();
            case FEET_SLOT -> getFeet();
            default -> ItemStack.EMPTY;
        };
    }
    public void setEquipment(int slot, ItemStack stack) {
        switch (slot) {
            case SADDLE_SLOT -> setSaddle(stack);
            case ARMOR_SLOT -> setArmor(stack);
            case WEAPON_SLOT -> setWeapon(stack);
            case HEAD_SLOT -> setHead(stack);
            case LEGS_SLOT -> setLegs(stack);
            case FEET_SLOT -> setFeet(stack);
        }
    }

    // Check if item is valid for slot
    public boolean isValidEquipment(int slot, ItemStack stack) {
        if (stack.isEmpty()) { return true; }

        return switch (slot) {
            case SADDLE_SLOT -> stack.getItem() instanceof ChocoboSaddleItem;
            case WEAPON_SLOT -> stack.getItem() instanceof ChocoboWeaponItems;
            case ARMOR_SLOT -> stack.getItem() instanceof ChocoboArmorItems armor && armor.getSlotType() == EquipmentSlot.CHEST;
            case HEAD_SLOT -> stack.getItem() instanceof ChocoboArmorItems armor && armor.getSlotType() == EquipmentSlot.HEAD;
            case LEGS_SLOT -> stack.getItem() instanceof ChocoboArmorItems armor && armor.getSlotType() == EquipmentSlot.LEGS;
            case FEET_SLOT -> stack.getItem() instanceof ChocoboArmorItems armor && armor.getSlotType() == EquipmentSlot.FEET;
            default -> false;
        };
    }
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient()) { return ActionResult.PASS; }

        ItemStack heldItemStack = player.getStackInHand(hand);

        if (player.isSneaking() && heldItemStack.isEmpty()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                this.displayChocoboArmorStandInventory(serverPlayer);
                return ActionResult.SUCCESS;
            }
        }
        // Other interactions can be placed here, for now, none.

        return ActionResult.PASS;
    }

    private void displayChocoboArmorStandInventory(@NotNull ServerPlayerEntity player) {
        if (player.currentScreenHandler != player.playerScreenHandler) { player.closeHandledScreen(); }
        player.openHandledScreen(new ChocoboArmorStandScreenHandlerFactory(this));
    }
    private boolean handleBreakAttempt(PlayerEntity player) {
        if (player.isCreative()) { return true; }
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastBreakAttempt > BREAK_TIMEOUT) { breakAttempts = 0; }

        breakAttempts++;
        lastBreakAttempt = currentTime;

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_STONE_HIT, BLOCKS, 0.5F, 1.0F);

        if (breakAttempts >= REQUIRED_BREAK_ATTEMPTS) {
            dropAllItems();
            this.discard();
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_STONE_BREAK, BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }
    private void dropAllItems() {
        // Drop all equipped items
        for (int i = 0; i < 6; i++) {
            ItemStack equipment = getEquipment(i);
            if (!equipment.isEmpty()) {
                this.dropStack(equipment);
            }
        }

        // Drop inventory items
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                this.dropStack(stack);
            }
        }
    }

    public void readCustomDataFromNbt(@NotNull NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        /*
        if (nbt.contains(NBT_KEY_POSE_DATA)) {
            this.chocoboModelPose.readFromNbt(nbt);
            this.setPoseType(this.chocoboModelPose.getType());
        } else { this.chocoboModelPose.setPoseByType(getPoseType()); }
        */
    }
    public void writeCustomDataToNbt(@NotNull NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        /*
        this.chocoboModelPose.writeToNbt(nbt);
        */
    }

    /**
     * Apply a predefined pose to the armor stand
     * @param poseType the pose type to apply
     */
    public void applyPredefinedPose(ChocoboModelPose poseType) {
        this.chocoboModelPose.setPoseByType(poseType);
        setPoseType(poseType);

        this.markDirty();
    }

    /**
     * Apply the current pose to a model
     * @param model the model to apply the pose to
     */
    public void applyPoseToModel(com.dephoegon.delchoco.client.models.entities.ChocoboArmorStandModel model) {
        this.chocoboModelPose.applyToModel(model, this.isBaby());
    }

    // Add entity dirty marking
    public void markDirty() {
        if (!this.getWorld().isClient()) { this.getEntityWorld().getChunk(this.getBlockPos()).setNeedsSaving(true); }
    }

    // Equipment state methods (copied from AbstractChocobo for compatibility)
    public boolean isSaddled() { return !this.getSaddle().isEmpty(); }
    public boolean isArmored() { return isChestArmored() || isHeadArmored() || isLegsArmored() || isFeetArmored(); }
    public boolean isWeaponArmed() { return !this.getWeapon().isEmpty(); }
    public boolean isHeadArmored() { return !this.getHead().isEmpty(); }
    public boolean isLegsArmored() { return !this.getLegs().isEmpty(); }
    public boolean isFeetArmored() { return !this.getFeetArmor().isEmpty(); }
    public boolean isChestArmored() { return !this.getArmor().isEmpty(); }

    // Alternative names for compatibility with existing layers
    public ItemStack getChestArmor() { return getArmor(); }
    public ItemStack getHeadArmor() { return getHead(); }
    public ItemStack getLegsArmor() { return getLegs(); }
    public ItemStack getFeetArmor() { return getFeet(); }

    // Required for compatibility with existing chocobo layers
    public boolean isBaby() { return !isAdult(); }
    public boolean isInvisible() { return false; } // Armor stands are always visible

    // Override to mimic Chocobo calls (for rendering purposes)
    public void setMale(boolean isMale) { this.dataTracker.set(IS_MALE, isMale); }
    public void setTamed(boolean isTamed) { this.dataTracker.set(IS_TAMED, isTamed); }
    public void setCollarColor(int color) { this.dataTracker.set(COLLAR_COLOR, color); }
    public void setFireImmune(boolean immune) { this.dataTracker.set(IS_FIRE_IMMUNE, immune); } // is immune to all damage, implemented for visibility in layers
    public boolean isMale() { return this.dataTracker.get(IS_MALE); }
    public boolean isTamed() { return this.dataTracker.get(IS_TAMED); }
    public Integer getCollarColor() { return this.dataTracker.get(COLLAR_COLOR); }
    public boolean isFireImmune() { return this.dataTracker.get(IS_FIRE_IMMUNE);}

    /**
     * Gets the current inventory size based on the equipped saddle
     * @return the number of available inventory slots
     */
    public int getInventorySize() {
        ItemStack saddleStack = this.getSaddle();
        if (saddleStack.isEmpty()) { return 0; }
        if (saddleStack.getItem() instanceof ChocoboSaddleItem saddleItem) { return saddleItem.getInventorySize(); }
        return 0;
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
        /*
        ChocoboModelPose currentPoseType = getPoseType();
        if (this.chocoboModelPose.getType() != currentPoseType) { this.chocoboModelPose.setPoseByType(currentPoseType); }
        if (!this.getWorld().isClient()) { this.chocoboModelPose.applyStaticPoseRotation(this); }
        */
    }

    // Override entity properties to ensure correct behavior for an armor stand
    public boolean canHit() { return true; }
    public boolean isPushable() { return false; }
    public boolean isPushedByFluids() { return false; }
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.GENERIC_KILL)) { return false; }
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.OUT_OF_WORLD)) { this.kill(); return false; }
        if (source.getAttacker() instanceof PlayerEntity player) { return handleBreakAttempt(player); }
        return false;
    }
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) { return false; }
    protected void pushOutOfBlocks(double x, double y, double z) { } // Override to prevent suffocation pushout
}