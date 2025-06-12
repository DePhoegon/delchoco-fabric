package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.aid.world.WorldConfig;
import com.dephoegon.delchoco.common.entities.breeding.ChocoboMateGoal;
import com.dephoegon.delchoco.common.entities.properties.*;
import com.dephoegon.delchoco.common.entities.properties.MovementType;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.init.ModSounds;
import com.dephoegon.delchoco.common.inventory.SaddlebagContainer;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboLeashPointer;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import com.dephoegon.delchoco.mixin.ServerPlayerEntityAccessor;
import com.dephoegon.delchoco.utils.RandomHelper;
import com.dephoegon.delchoco.utils.WorldUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
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
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.dephoegon.delchoco.aid.chocoKB.isChocoboWaterGlide;
import static com.dephoegon.delchoco.aid.chocoboChecks.*;
import static com.dephoegon.delchoco.aid.dyeList.getDyeList;
import static com.dephoegon.delchoco.common.effects.ChocoboCombatEvents.flowerChance;
import static com.dephoegon.delchoco.common.entities.breeding.BreedingHelper.getChocoName;
import static com.dephoegon.delchoco.common.init.ModItems.*;
import static java.lang.Math.random;
import static net.minecraft.entity.SpawnGroup.CREATURE;
import static net.minecraft.item.Items.*;
import static net.minecraft.registry.tag.BiomeTags.IS_BADLANDS;
import static net.minecraft.registry.tag.BiomeTags.IS_FOREST;

public class Chocobo extends AbstractChocobo {
    protected static final EntityAttributeModifier CHOCOBO_SPRINTING_SPEED_BOOST = (new EntityAttributeModifier(CHOCOBO_SPRINTING_BOOST_ID, "Chocobo sprinting speed boost", 0.5, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));

    // Inventory Related
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

    // Chocobo Related
    protected void dropLoot(@NotNull DamageSource source, boolean causedByPlayer)  {
        Inventory[] inventories = new Inventory[] {
                this.chocoboBackboneInv,
                this.chocoboSaddleInv,
                this.chocoboWeaponInv,
                this.chocoboArmorInv
        };
        /*
        inventoryDropClear(this.chocoboBackboneInv, this);
        inventoryDropClear(this.chocoboSaddleInv, this);
        inventoryDropClear(this.chocoboWeaponInv, this);
        inventoryDropClear(this.chocoboArmorInv, this);
        */
        for (Inventory inventory : inventories) {
            this.inventoryDropClear(inventory, this);
        }
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
        this.setPathfindingPenalty(PathNodeType.FENCE,6.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_CAUTIOUS, this.isWitherImmune() ? 0.0F : 8.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, this.isWaterBreathing() ? -0.55F : -0.15F);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, this.isWaterBreathing() ? -0.55F : -0.25F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, this.isFireImmune() ? -0.2F : 32.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, this.isFireImmune() ? -0.1F : 16.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, this.isFireImmune() ? 0.0F : 16.0F);
    }
    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        if (!this.isTamed()) { return false; }
        PlayerEntity chocoboOwner = (owner instanceof PlayerEntity) ? (PlayerEntity) owner : null;
        LivingEntity otherEntity = (target instanceof TameableEntity tameableEntity) ? tameableEntity.getOwner() : null;
        if (otherEntity == null) { otherEntity = (target instanceof AbstractHorseEntity horse) ? horse.getOwner() : null; }
        PlayerEntity otherOwner = (otherEntity instanceof PlayerEntity) ? (PlayerEntity) otherEntity : null;
        if (chocoboOwner == null) {
            DelChoco.LOGGER.info("ChocoboBrainAid.isTameTargetInvalid() - Chocobo has no owner, but is tamed! This should not happen!");
            return false;
        }
        if (chocoboOwner == otherOwner) { return false; } //TODO: introduce the config check to do bypass hit by owner
        if (ChocoboBrainAid.isAttackable(target)) {
            if (otherOwner != null) { return chocoboOwner.shouldDamagePlayer(otherOwner); }
            else { return true; }
        }
        return false;
    }
    protected Brain.Profile<Chocobo> createBrainProfile() { return Brain.createProfile(ChocoboBrains.CHOCOBO_MODULES, ChocoboBrains.CHOCOBO_SENSORS); }
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) { return ChocoboBrains.makeBrain(this.createBrainProfile().deserialize(dynamic), this); }
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

        //this.goalSelector.add(1, new ChocoboGoals.ChocoPanicGoal(this,1.5D));
        this.goalSelector.add(1, new MeleeAttackGoal(this,2F, false));
        this.goalSelector.add(2, new ChocoboMateGoal(this, 1.0D));
        /*
        this.goalSelector.add(4, new ChocoboGoals.ChocoboLavaEscape(this));
        this.goalSelector.add(5, new ChocoboGoals.ChocoboFollowOwnerGoal(this, 1.6, 10F, 300F));
        this.goalSelector.add(6, new TemptGoal(this, 1.2D, Ingredient.ofStacks(GYSAHL_GREEN_ITEM.getDefaultStack()), false));
        this.goalSelector.add(8, new ChocoboGoals.ChocoboAvoidPlayer(this));
        this.goalSelector.add(9, new ChocoboGoals.ChocoboRoamWonder(this, 1.0D)); // Roam & Wonder, uses MovementType check to allow to start & if it limits the roaming
        //this.goalSelector.add(9, new FleeEntityGoal<>(this, LlamaEntity.class, 15F, 1.3F, 1.5F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(11, new LookAroundGoal(this)); // moved after Roam, a little too stationary
        */
        this.targetSelector.add(1, new ChocoboGoals.ChocoboOwnerHurtByGoal(this));
        this.targetSelector.add(2, new ChocoboGoals.ChocoboOwnerHurtGoal(this));
        this.targetSelector.add(3, new ChocoboGoals.ChocoboHurtByTargetGoal(this, Chocobo.class).setGroupRevenge(Chocobo.class));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, EndermiteEntity.class, false));
        this.targetSelector.add(6, new ActiveTargetGoal<>(this, SilverfishEntity.class, false));
        this.targetSelector.add(7, new UniversalAngerGoal<>(this, true));

    }
    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        return super.getPathfindingFavor(pos, world);
    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
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
        // Moved to AbstractChocobo Left in Override for future use
    }
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setChocoboColor(ChocoboColor.values()[compound.getByte(NBTKEY_CHOCOBO_COLOR)]);
        this.setMovementType(MovementType.values()[compound.getByte(NBTKEY_MOVEMENT_TYPE)]);
        this.chocoboSaddleInv.singleSlotFromNBT(compound.getCompound(NBTKEY_SADDLE_ITEM));
        this.chocoboWeaponInv.singleSlotFromNBT(compound.getCompound(NBTKEY_WEAPON_ITEM));
        this.chocoboArmorInv.singleSlotFromNBT(compound.getCompound(NBTKEY_ARMOR_ITEM));
        ChocoboBackboneFromNBT(compound);
        this.setChocoboAbilityMask(compound.getByte(NBTKEY_CHOCOBO_ABILITY_MASK));
        this.setGeneration(compound.getInt(NBTKEY_CHOCOBO_GENERATION));
        this.setChocoboScale(false, compound.getInt(NBTKEY_CHOCOBO_SCALE), true);
        this.setChocoboScaleMod(compound.getFloat(NBTKEY_CHOCOBO_SCALE_MOD));
        this.setCollarColor(compound.getInt(NBTKEY_CHOCOBO_COLLAR));
        //this.setLeashSpot(compound.getInt(NBTKEY_CHOCOBO_LEASH_BLOCK_X), compound.getInt(NBTKEY_CHOCOBO_LEASH_BLOCK_Y), compound.getInt(NBTKEY_CHOCOBO_LEASH_BLOCK_Z));
        this.setLeashSpot(NbtHelper.toBlockPos(compound.getCompound(NBTKEY_CHOCOBO_LEASH_BLOCK)));
        this.setLeashedDistance(compound.getDouble(NBTKEY_CHOCOBO_LEASH_DISTANCE));
    }
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putByte(NBTKEY_CHOCOBO_COLOR, (byte) this.getChocoboColor().ordinal());
        compound.putByte(NBTKEY_MOVEMENT_TYPE, (byte) this.getMovementType().ordinal());
        compound.put(NBTKEY_SADDLE_ITEM, this.chocoboSaddleInv.singleSlotToNBT());
        compound.put(NBTKEY_ARMOR_ITEM, this.chocoboArmorInv.singleSlotToNBT());
        compound.put(NBTKEY_WEAPON_ITEM, this.chocoboWeaponInv.singleSlotToNBT());
        ChocoboBackboneToNBT(compound);
        compound.putByte(NBTKEY_CHOCOBO_ABILITY_MASK, this.getChocoboAbilityMask());
        compound.putInt(NBTKEY_CHOCOBO_GENERATION, this.getGeneration());
        compound.putInt(NBTKEY_CHOCOBO_SCALE, this.getChocoboScale());
        compound.putFloat(NBTKEY_CHOCOBO_SCALE_MOD, this.getChocoboScaleMod());
        compound.putInt(NBTKEY_CHOCOBO_COLLAR, this.getCollarColor());
        //compound.putInt(NBTKEY_CHOCOBO_LEASH_BLOCK_X, this.getLeashSpot().getX());
        //compound.putInt(NBTKEY_CHOCOBO_LEASH_BLOCK_Y, this.getLeashSpot().getY());
        //compound.putInt(NBTKEY_CHOCOBO_LEASH_BLOCK_Z, this.getLeashSpot().getZ());
        compound.put(NBTKEY_CHOCOBO_LEASH_BLOCK, NbtHelper.fromBlockPos(this.getLeashSpot()));
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


    // Spawn/Breeding Related
    @Nullable
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, @Nullable EntityData spawnDataIn, @Nullable NbtCompound dataTag) {
        this.setMale(this.getWorld().random.nextBoolean());
        boolean skip;

        final RegistryEntry<Biome> currentBiomes = this.getWorld().getBiome(getBlockPos().down());
        //noinspection OptionalGetWithoutIsPresent
        final RegistryKey<Biome> biomeRegistryKey = currentBiomes.getKey().get();
        if (isEnd(worldIn)) { skip = !WorldConfig.CHOCOBO_SPAWN_SWITCH_THE_END.get(); }
        else if (isNether(worldIn)) { skip = !WorldConfig.CHOCOBO_SPAWN_SWITCH_NETHER.get(); }
        else if (isOverworld(worldIn)) { skip = !WorldConfig.CHOCOBO_SPAWN_SWITCH_OVERWORLD.get(); }
        else { skip = false; }
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

    // Combat related
    public boolean isInvulnerableTo(@NotNull DamageSource source) {
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        return super.isInvulnerableTo(source);
    }
    public boolean damage(DamageSource source, float amount) {
        boolean result = super.damage(source, amount);
        PlayerEntity player = source.getAttacker() instanceof PlayerEntity ? (PlayerEntity) source.getAttacker() : null;
        PlayerEntity owner = this.getOwner() instanceof PlayerEntity ? (PlayerEntity) this.getOwner() : null;
        if (result && player != null) {
            boolean shift = player.isSneaking() && ChocoboConfig.SHIFT_HIT_BYPASS.get();
            if (RandomHelper.random.nextInt(100) + 1 > 35) { this.dropItem(ModItems.CHOCOBO_FEATHER); }
            if (owner != null) {
                Team group = (Team) owner.getScoreboardTeam();
                Team playerGroup = (Team) player.getScoreboardTeam();
                boolean teams = group != null && playerGroup == group;
                if (!shift) {
                    if (player == owner || teams) { return ChocoboConfig.OWN_CHOCOBO_HITTABLE.get(); }
                    return ChocoboConfig.TAMED_CHOCOBO_HITTABLE.get();
                }
            }
        }
        return result;
    }
    public boolean tryAttack(Entity entity) {
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        return super.tryAttack(entity);
    }
    public boolean canHaveStatusEffect(@NotNull StatusEffectInstance potionEffect) {
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        return super.canHaveStatusEffect(potionEffect);
    }
    public void onStatusEffectApplied(@NotNull StatusEffectInstance effect, @Nullable Entity source) {
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        super.onStatusEffectApplied(effect, source);
    }
    public void onStatusEffectRemoved(@NotNull StatusEffectInstance effect) {
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        super.onStatusEffectRemoved(effect);
    }
    public boolean nonFlameFireImmune() { return isFireImmune() && ChocoboColor.FLAME != getChocoboColor(); }
    public double getMountedHeightOffset() {
        // Returns the height offset when mounted, used for riding Chocobos
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        return super.getMountedHeightOffset();
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
    protected boolean updateWaterState() {
        // left in for unique Chocobo Checks unable to be done in AbstractChocobo
        return super.updateWaterState();
    }
    protected void updateInWaterStateAndDoWaterCurrentPushing() {
        // left in for unique Chocobo Checks unable to be done in AbstractChocobo
        super.updateInWaterStateAndDoWaterCurrentPushing();
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
            if (newVector.getZ() <= 0.0D) { newVector = new Vec3d(newVector.x, newVector.y, newVector.z * 0.25F); }

            if (this.isOnGround()) { this.isChocoboJumping = false; }

            if (this.isLogicalSideForUpdatingMovement()) {
                if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                    // jump logic
                    if (!this.isChocoboJumping && this.isOnGround()) {
                        Vec3d motion = getVelocity();
                        setVelocity(new Vec3d(motion.x, .6f, motion.z));
                        this.isChocoboJumping = true;
                    }
                }
                if (rider.isTouchingWater()) {
                    Vec3d motion = getVelocity();
                    if (MinecraftClient.getInstance().options.jumpKey.isPressed()) { setVelocity(new Vec3d(motion.x, .5f, motion.z)); }
                    else if (this.isWaterBreathing() && isChocoboWaterGlide()) {
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
                if (!this.isOnGround() && !this.isTouchingWater() && !this.isInLava() && this.getVelocity().y < 0) {
                    if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                        Vec3d motion = getVelocity();
                        setVelocity(new Vec3d(motion.x, motion.y * 0.65F, motion.z));
                    }
                }
                if (MinecraftClient.getInstance().options.sprintKey.isPressed()) { this.setSprinting(this.isOnGround()); }
                else { this.setSprinting(false); }

                this.setMovementSpeed((float) Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue());
                super.travel(newVector);
            }
        } else {
            if (!this.isOnGround() && !this.isTouchingWater() && !this.isInLava() && this.getVelocity().y < 0) {
                Vec3d motion = getVelocity();
                setVelocity(new Vec3d(motion.x, motion.y * 0.65F, motion.z));
            }
            double y = newVector.y;
            if (y > 0) y = y * -1;
            Vec3d cappedNewVector = new Vec3d(newVector.x, y, newVector.z);
            super.travel(cappedNewVector);
        }
    }
    public void tick() {
        super.tick();
        this.fruitAteTimer = this.fruitAteTimer > 0 ? this.fruitAteTimer - 1 : 0;
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
    public boolean canBreedWith(@NotNull AnimalEntity otherAnimal) {
        if (otherAnimal == this || !(otherAnimal instanceof Chocobo otherChocobo)) return false;
        if (!this.isInLove() || !otherAnimal.isInLove()) return false;
        return otherChocobo.isMale() != this.isMale();
    }
    protected boolean canStartRiding(@NotNull Entity entityIn) { return false; }
    public void tickMovement() {
        super.tickMovement();
        this.setRotation(this.getYaw(), this.getPitch());
        this.setStepHeight(maxStepUp);
        this.fallDistance = 0f;

        if (this.TimeSinceFeatherChance == 3000) {
            this.TimeSinceFeatherChance = 0;
            if ((float) random() < .25) { this.dropFeather(); }
        } else { this.TimeSinceFeatherChance++; }

        //Change effects to chocobo colors
        if (!this.getEntityWorld().isClient()) {
            if (this.age % 60 == 0) {
                if (this.hasPassengers()) {
                    if (this.isFireImmune()) {
                        Entity controller = this.getPrimaryPassenger();
                        if (controller instanceof PlayerEntity) { ((PlayerEntity) controller).addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 100, 0, true, false)); }
                    }
                    if (this.isWaterBreathing() && this.isSubmergedInWater()) {
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
        if (player.getWorld().isClient()) { return false; }
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
                    this.setOwner(player);
                    if(this.getOwner() == null) { this.setOwnerUuid(player.getUuid()); this.setTamed(true); }
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
                increaseStat(this, all, 1, player);
                this.fruitAteTimer = ChocoboConfig.FRUIT_COOL_DOWN.get();
                ate = true;
            }
            if (pStack == PINK_GYSAHL_GREEN) {
                increaseStat(this, health, 1, player);
                this.fruitAteTimer = ChocoboConfig.FRUIT_COOL_DOWN.get();
                ate =  true;
            }
            if (pStack == DEAD_PEPPER) {
                increaseStat(this, strength, 1, player);
                this.fruitAteTimer = ChocoboConfig.FRUIT_COOL_DOWN.get();
                ate =  true;
            }
            if (pStack == SPIKE_FRUIT) {
                increaseStat(this, dualDefense, 1, player);
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
        if (interactInvRide(player, heldItemStack)) { return ActionResult.SUCCESS; }
        if (interactFeed(player, heldItemStack, hand)) { return ActionResult.SUCCESS; }
        if (interactEquip(player, heldItemStack)) { return ActionResult.SUCCESS; }
        if (interactUtil(player, heldItemStack)) { return ActionResult.SUCCESS; }
        if (interactDye(player, heldItemStack)) { return ActionResult.SUCCESS; }
        return super.interactAt(player, vec, hand);
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
    public int getMinAmbientSoundDelay() { return (24 * (int) (random() * 100)); }
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public boolean canSpawn(@NotNull WorldAccess worldIn, @NotNull SpawnReason spawnReasonIn) {
        World world = this.getWorld();
        ServerWorldAccess theWorld = Objects.requireNonNull(world.getServer()).getWorld(world.getRegistryKey());
        assert theWorld != null;
        RegistryKey<Biome> biomes = theWorld.getBiome(getBlockPos().down()).getKey().get();
        int multi = IS_SPARSE().contains(biomes) ? 2 : IS_OCEAN().contains(biomes) ? 3 : 1;
        int sizeCtrl = IS_SPARSE().contains(biomes) ? 8 : IS_OCEAN().contains(biomes) ? 8 : 15;
        List<Chocobo> bob = world.getNonSpectatingEntities(Chocobo.class, spawnControlBoxSize(new Box(getBlockPos()), multi));
        if (bob.size() > sizeCtrl) { return false; }
        if (this.getClass().equals(Chocobo.class)) {
            if (isEnd(theWorld)) { return !this.getWorld().getBlockState(getBlockPos().down()).isAir(); }
            if (isNether(theWorld)) { return true; }
            if (IS_OCEAN().contains(biomes)) { return !isOceanBlocked(biomes, true); }
        }
        return super.canSpawn(worldIn, spawnReasonIn);
    }
    @Override
    public void chooseRandomAngerTime() { this.setAngerTime(PERSISTENT_ANGER_TIME.get(this.random)); }
    protected void mobTick() {
        tickActivities(this);
        super.mobTick();
    }
    // Mixins & Events brought into entity class
    public boolean onKilledOther(ServerWorld world, LivingEntity targetEntity) {
        if (targetEntity instanceof PlayerEntity player) {
            this.forgive(player);
            this.alertOthers(player, this);
        }
        if (ChocoboConfig.EXTRA_CHOCOBO_RESOURCES_HIT.get()) {
            ChocoboColor color = this.getChocoboColor();
            if (targetEntity instanceof SpiderEntity) {
                if (.20f > (float) random()) { targetEntity.dropItem(COBWEB); }
            }
            if (flowerChance()) {
                if (color == ChocoboColor.BLACK) {
                    if (.50f > (float) Math.random()) { targetEntity.dropItem(WITHER_ROSE); }
                    else { targetEntity.dropItem(DEAD_BUSH); }
                }
                if (color == ChocoboColor.FLAME) {
                    if (.50f > (float) Math.random()) { targetEntity.dropItem(CRIMSON_FUNGUS); }
                    else { targetEntity.dropItem(WARPED_FUNGUS); }
                }
                if (color == ChocoboColor.GREEN) {
                    if (.34f > (float) Math.random()) { targetEntity.dropItem(SPORE_BLOSSOM); }
                    else {
                        if (.51f > (float) Math.random()) { targetEntity.dropItem(SMALL_DRIPLEAF); }
                        else { targetEntity.dropItem(MOSS_BLOCK); }
                    }
                }
                if (color == ChocoboColor.WHITE) {
                    if (.34f > (float) Math.random()) { targetEntity.dropItem(SNOWBALL); }
                    else {
                        if (.51f > (float) Math.random()) { targetEntity.dropItem(LILY_OF_THE_VALLEY); }
                        else { targetEntity.dropItem(OXEYE_DAISY); }
                    }
                }
                if (color == ChocoboColor.GOLD) { targetEntity.dropItem(SUNFLOWER); }
                if (color == ChocoboColor.BLUE) {
                    if (.50f > (float) Math.random()) { targetEntity.dropItem(KELP); }
                    else { targetEntity.dropItem(SEA_PICKLE); }
                    if (.10f > (float) Math.random()) { targetEntity.dropItem(NAUTILUS_SHELL); }
                }
                if (color == ChocoboColor.PINK) {
                    if (.34f > (float) Math.random()) { targetEntity.dropItem(BROWN_MUSHROOM); }
                    else {
                        if (.51f > (float) Math.random()) { targetEntity.dropItem(RED_MUSHROOM); }
                        else { targetEntity.dropItem(ALLIUM); }
                    }
                }
                if (color == ChocoboColor.RED) {
                    if (.34f > (float) Math.random()) { targetEntity.dropItem(STICK); }
                    else {
                        if (.51f > (float) Math.random()) { targetEntity.dropItem(BAMBOO); }
                        else { targetEntity.dropItem(VINE); }
                    }
                }
                if (color == ChocoboColor.PURPLE) { targetEntity.dropItem(CHORUS_FLOWER); }
                if (color == ChocoboColor.YELLOW) {
                    Item flower = switch (RandomHelper.random.nextInt(12) + 1) {
                        case 2 -> POPPY;
                        case 3 -> BLUE_ORCHID;
                        case 4 -> ALLIUM;
                        case 5 -> AZURE_BLUET;
                        case 6 -> RED_TULIP;
                        case 7 -> ORANGE_TULIP;
                        case 8 -> WHITE_TULIP;
                        case 9 -> PINK_TULIP;
                        case 10 -> OXEYE_DAISY;
                        case 11 -> CORNFLOWER;
                        case 12 -> LILY_OF_THE_VALLEY;
                        default -> DANDELION;
                    };
                    targetEntity.dropItem(flower);
                }
            } else {
                if (color == ChocoboColor.PURPLE) {
                    if (.09f > (float) Math.random()) { targetEntity.dropItem(ENDER_PEARL); }
                }
                if (color == ChocoboColor.GOLD) {
                    if (.09f > (float) Math.random()) { targetEntity.dropItem(GOLD_NUGGET); }
                }
                if (color == ChocoboColor.WHITE) {
                    if (.41f > (float) Math.random()) { targetEntity.dropItem(BONE_MEAL); }
                }
                if (color == ChocoboColor.FLAME) {
                    if (.10f > (float) Math.random()) { targetEntity.dropItem(MAGMA_CREAM); }
                }
            }
        }
        return super.onKilledOther(world, targetEntity);
    }
    public void onDeath(DamageSource source) {
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        super.onDeath(source);
    }
    // applyDamageEffects is used to apply effects after the damage is applied, such as dropping items or applying potion effects.
    public void applyDamageEffects(LivingEntity attacker, Entity target) {
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        super.applyDamageEffects(attacker, target);
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
}