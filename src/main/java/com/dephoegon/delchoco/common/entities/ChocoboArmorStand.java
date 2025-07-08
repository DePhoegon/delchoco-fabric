package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.entities.properties.IChocobo;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import com.dephoegon.delchoco.utils.RandomHelper;
import net.minecraft.entity.Entity;
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
import static net.minecraft.sound.SoundCategory.BLOCKS;

public class ChocoboArmorStand extends Entity implements IChocobo {
    // NBT keys for saving/loading entity data
    private static final String NBT_KEY_SCALE = "Scale";
    private static final String NBT_KEY_IS_ADULT = "IsAdult";
    private static final String NBT_KEY_IS_MALE = "IsMale";
    private static final String NBT_KEY_IS_TAMED = "IsTamed";
    private static final String NBT_KEY_COLLAR_COLOR = "CollarColor";
    private static final String NBT_KEY_FIRE_IMMUNE = "FireImmune";
    private static final String NBT_KEY_SADDLE = "Saddle";
    private static final String NBT_KEY_ARMOR = "Armor";
    private static final String NBT_KEY_WEAPON = "Weapon";
    private static final String NBT_KEY_HEAD = "Head";
    private static final String NBT_KEY_LEGS = "Legs";
    private static final String NBT_KEY_FEET = "Feet";
    private static final String NBT_KEY_INVENTORY = "Inventory";

    // NBT keys for pose data
    private static final String NBT_KEY_POSE_DATA = "PoseData";

    // Data trackers for equipment and properties
    private static final TrackedData<Integer> SCALE = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_ADULT = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> CHOCOBO_COLOR = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> IS_MALE = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_TAMED = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_FIRE_IMMUNE = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> COLLAR_COLOR = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<ItemStack> SADDLE_ITEM = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> ARMOR_ITEM = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> WEAPON_ITEM = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> HEAD_ITEM = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> LEGS_ITEM = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> FEET_ITEM = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<String> POSE_TYPE = DataTracker.registerData(ChocoboArmorStand.class, TrackedDataHandlerRegistry.STRING);

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

    public ChocoboArmorStand(EntityType<?> type, World world) {
        super(type, world);
        this.setInvulnerable(true); // Make invulnerable to most damage
        if (!this.getWorld().isClient()) {
            this.setMale(this.random.nextBoolean());
            this.setTamed(this.random.nextBoolean());
            this.setCollarColor(RandomHelper.random.nextInt(17));
            this.setFireImmune(this.random.nextBoolean());
        }
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(SCALE, 0);
        this.dataTracker.startTracking(IS_ADULT, true);
        this.dataTracker.startTracking(CHOCOBO_COLOR, "");
        this.dataTracker.startTracking(IS_MALE, true);
        this.dataTracker.startTracking(IS_TAMED, false);
        this.dataTracker.startTracking(IS_FIRE_IMMUNE, false);
        this.dataTracker.startTracking(COLLAR_COLOR, 0);
        this.dataTracker.startTracking(SADDLE_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(ARMOR_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(WEAPON_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(HEAD_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(LEGS_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(FEET_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(POSE_TYPE, ChocoboModelPose.DEFAULT.getId());
    }

    // Scale management
    public int getScale() { return this.dataTracker.get(SCALE); }
    public float getScaleMod() { return ScaleMod(getScale()); }
    public void setScale(int scale) {
        this.dataTracker.set(SCALE, Math.max(-15, Math.min(31, scale)));
        this.calculateDimensions();
    }
    public float ScaleMod(int scale) { return AbstractChocobo.ScaleMod(scale); }
    public boolean isAdult() { return this.dataTracker.get(IS_ADULT); }
    public void setIsAdult(boolean isAdult) { this.dataTracker.set(IS_ADULT, isAdult); }
    public ChocoboColor getChocoboColor() {
        String colorName = this.dataTracker.get(CHOCOBO_COLOR);
        return colorName.isEmpty() ? null : ChocoboColor.valueOf(colorName);
    }
    public void setChocoboColor(ChocoboColor color) {
        this.dataTracker.set(CHOCOBO_COLOR, color == null ? "" : color.name());
    }

    // Property getters and setters for rendering
    // Equipment getters and setters
    public ItemStack getSaddle() { return this.dataTracker.get(SADDLE_ITEM); }
    public void setSaddle(ItemStack saddle) { this.dataTracker.set(SADDLE_ITEM, saddle.copy()); }
    public ItemStack getArmor() { return this.dataTracker.get(ARMOR_ITEM); }
    public void setArmor(ItemStack armor) { this.dataTracker.set(ARMOR_ITEM, armor.copy()); }
    public ItemStack getWeapon() { return this.dataTracker.get(WEAPON_ITEM); }
    public void setWeapon(ItemStack weapon) { this.dataTracker.set(WEAPON_ITEM, weapon.copy()); }
    public ItemStack getHead() { return this.dataTracker.get(HEAD_ITEM); }
    public void setHead(ItemStack head) { this.dataTracker.set(HEAD_ITEM, head.copy()); }
    public ItemStack getLegs() { return this.dataTracker.get(LEGS_ITEM); }
    public void setLegs(ItemStack legs) { this.dataTracker.set(LEGS_ITEM, legs.copy()); }
    public ItemStack getFeet() { return this.dataTracker.get(FEET_ITEM); }
    public void setFeet(ItemStack feet) { this.dataTracker.set(FEET_ITEM, feet.copy()); }

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

    protected void readCustomDataFromNbt(@NotNull NbtCompound nbt) {
        setScale(nbt.getInt(NBT_KEY_SCALE));
        setIsAdult(nbt.getBoolean(NBT_KEY_IS_ADULT));
        setMale(nbt.getBoolean(NBT_KEY_IS_MALE));
        setTamed(nbt.getBoolean(NBT_KEY_IS_TAMED));
        setCollarColor(nbt.getInt(NBT_KEY_COLLAR_COLOR));
        setFireImmune(nbt.getBoolean(NBT_KEY_FIRE_IMMUNE));

        if (nbt.contains(NBT_KEY_SADDLE)) setSaddle(ItemStack.fromNbt(nbt.getCompound(NBT_KEY_SADDLE)));
        if (nbt.contains(NBT_KEY_ARMOR)) setArmor(ItemStack.fromNbt(nbt.getCompound(NBT_KEY_ARMOR)));
        if (nbt.contains(NBT_KEY_WEAPON)) setWeapon(ItemStack.fromNbt(nbt.getCompound(NBT_KEY_WEAPON)));
        if (nbt.contains(NBT_KEY_HEAD)) setHead(ItemStack.fromNbt(nbt.getCompound(NBT_KEY_HEAD)));
        if (nbt.contains(NBT_KEY_LEGS)) setLegs(ItemStack.fromNbt(nbt.getCompound(NBT_KEY_LEGS)));
        if (nbt.contains(NBT_KEY_FEET)) setFeet(ItemStack.fromNbt(nbt.getCompound(NBT_KEY_FEET)));

        inventory.readNbtList(nbt.getList(NBT_KEY_INVENTORY, 10));

        if (nbt.contains(NBT_KEY_POSE_DATA)) {
            this.chocoboModelPose.readFromNbt(nbt);
            this.setPoseType(this.chocoboModelPose.getType());
        } else { this.chocoboModelPose.setPoseByType(getPoseType()); }
    }
    protected void writeCustomDataToNbt(@NotNull NbtCompound nbt) {
        nbt.putInt(NBT_KEY_SCALE, getScale());
        nbt.putBoolean(NBT_KEY_IS_ADULT, isAdult());
        nbt.putBoolean(NBT_KEY_IS_MALE, isMale());
        nbt.putBoolean(NBT_KEY_IS_TAMED, isTamed());
        nbt.putInt(NBT_KEY_COLLAR_COLOR, getCollarColor());
        nbt.putBoolean(NBT_KEY_FIRE_IMMUNE, isFireImmune());

        if (!getSaddle().isEmpty()) nbt.put(NBT_KEY_SADDLE, getSaddle().writeNbt(new NbtCompound()));
        if (!getArmor().isEmpty()) nbt.put(NBT_KEY_ARMOR, getArmor().writeNbt(new NbtCompound()));
        if (!getWeapon().isEmpty()) nbt.put(NBT_KEY_WEAPON, getWeapon().writeNbt(new NbtCompound()));
        if (!getHead().isEmpty()) nbt.put(NBT_KEY_HEAD, getHead().writeNbt(new NbtCompound()));
        if (!getLegs().isEmpty()) nbt.put(NBT_KEY_LEGS, getLegs().writeNbt(new NbtCompound()));
        if (!getFeet().isEmpty()) nbt.put(NBT_KEY_FEET, getFeet().writeNbt(new NbtCompound()));

        nbt.put(NBT_KEY_INVENTORY, inventory.toNbtList());

        this.chocoboModelPose.writeToNbt(nbt);
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
    public int getCollarColor() { return this.dataTracker.get(COLLAR_COLOR); }
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

        ChocoboModelPose currentPoseType = getPoseType();
        if (this.chocoboModelPose.getType() != currentPoseType) { this.chocoboModelPose.setPoseByType(currentPoseType); }
        if (!this.getWorld().isClient()) { this.chocoboModelPose.applyStaticPoseRotation(this); }
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