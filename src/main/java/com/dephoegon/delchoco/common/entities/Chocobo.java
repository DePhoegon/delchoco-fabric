package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.aid.world.WorldConfig;
import com.dephoegon.delchoco.common.enchantments.ChocoboSweepEnchantment;
import com.dephoegon.delchoco.common.entities.breeding.ChocoboBreedInfo;
import com.dephoegon.delchoco.common.entities.breeding.ChocoboStatSnapshot;
import com.dephoegon.delchoco.common.entities.properties.*;
import com.dephoegon.delchoco.common.entities.properties.MovementType;
import com.dephoegon.delchoco.common.init.*;
import com.dephoegon.delchoco.common.inventory.ChocoboScreenHandler;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboLeashPointer;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import com.dephoegon.delchoco.mixin.ServerPlayerEntityAccessor;
import com.dephoegon.delchoco.utils.RandomHelper;
import com.dephoegon.delchoco.utils.WorldUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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
import static com.dephoegon.delchoco.common.entities.breeding.BreedingHelper.getChicoboFromBreedInfo;
import static com.dephoegon.delchoco.common.entities.breeding.BreedingHelper.getChocoName;
import static com.dephoegon.delchoco.common.init.ModDamageTypes.knockbackCalculation;
import static com.dephoegon.delchoco.common.init.ModItems.*;
import static com.dephoegon.delchoco.common.inventory.ChocoboEquipmentSlot.*;
import static java.lang.Math.floor;
import static java.lang.Math.random;
import static net.minecraft.entity.SpawnGroup.CREATURE;
import static net.minecraft.item.Items.*;
import static net.minecraft.registry.tag.BiomeTags.IS_BADLANDS;
import static net.minecraft.registry.tag.BiomeTags.IS_FOREST;

public class Chocobo extends AbstractChocobo {
    // Inventory Related
    public final ChocoboInventory chocoboInventory = new ChocoboInventory(top_tier_chocobo_inv_slot_count, this);
    public final ChocoboGearInventory chocoboGearInventory = new ChocoboGearInventory(this);
    private boolean fromNBT = false;

    // Chocobo Related
    protected void dropLoot(@NotNull DamageSource source, boolean causedByPlayer)  {
        this.inventoryDropClear(this.chocoboInventory, this, false);
        this.inventoryDropClear(this.chocoboGearInventory, this, true);

        super.dropLoot(source, causedByPlayer);
    }
    protected void inventoryDropClear(@NotNull Inventory inventory, Entity entity, boolean isGear) {
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                boolean hasVanishingCurse = net.minecraft.enchantment.EnchantmentHelper.getLevel(
                        net.minecraft.enchantment.Enchantments.VANISHING_CURSE, itemStack) > 0;

                // Only drop the item if it doesn't have Curse of Vanishing, Only for gear items
                if (!hasVanishingCurse || !isGear) { entity.dropStack(itemStack); }
                // Items with Curse of Vanishing will simply disappear (not be dropped)
            }
        }
        inventory.clear();
    }
    public void onSaddleChanged() {
        if (this.getWorld().isClient()) { return; } // should only run on the server side
        super.onSaddleChanged();
        if (this.fromNBT) { return; }
        for (PlayerEntity playerEntity : this.getWorld().getPlayers()) {
            if (playerEntity.currentScreenHandler instanceof ChocoboScreenHandler container && container.getChocobo() == this) {
                container.syncInventory(true); // Force close the container to update the saddle
            }
        }
        inventoryDropClear(this.chocoboInventory, this, false);
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
        if (ChocoboBrainAid.isAttackable(target, this.canWalkOnWater())) {
            if (otherOwner != null) { return chocoboOwner.shouldDamagePlayer(otherOwner); }
            else { return true; }
        }
        return false;
    }
    protected Brain.Profile<Chocobo> createBrainProfile() { return Brain.createProfile(ChocoboBrains.CHOCOBO_MODULES, ChocoboBrains.CHOCOBO_SENSORS); }
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) { return ChocoboBrains.makeBrain(this.createBrainProfile().deserialize(dynamic)); }
    @SuppressWarnings("unchecked")
    public Brain<Chocobo> getBrain() {
        return (Brain<Chocobo>) super.getBrain();
    }
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }
    protected void initGoals() {
        // super.initGoals();
        // this.goalSelector.add(1, new MeleeAttackGoal(this,2F, false));
        // this.goalSelector.add(2, new ChocoboMateGoal(this, 1.0D));
        // this.goalSelector.add(3, new FollowParentGoal(this, 1.1D));
        // this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        // this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        // this.targetSelector.add(3, new RevengeGoal(this, invalidRevengeTargets(this.canWalkOnWater())).setGroupRevenge(validRevengeAllies()));
        // this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        // this.targetSelector.add(5, new UniversalAngerGoal<>(this, false));
        // this.targetSelector.add(6, new ActiveTargetGoal<>(this, EndermiteEntity.class, false));
        // this.targetSelector.add(7, new ActiveTargetGoal<>(this, SilverfishEntity.class, false));
    }
    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) { return super.getPathfindingFavor(pos, world); }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, ChocoboConfig.DEFAULT_SPEED.get() / 100f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, ChocoboConfig.DEFAULT_HEALTH.get())
                .add(EntityAttributes.GENERIC_ARMOR, ChocoboConfig.DEFAULT_ARMOR.get())
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, ChocoboConfig.DEFAULT_ARMOR_TOUGHNESS.get())
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, ChocoboConfig.DEFAULT_ATTACK_SPEED.get())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, ChocoboConfig.DEFAULT_ATTACK_DAMAGE.get())
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, floor(EntityAttributes.GENERIC_FOLLOW_RANGE.getDefaultValue()*1.5)); // 32 blocks, 1.5x the vanilla value
    }
    protected void initDataTracker() {
        super.initDataTracker();
        // Moved to AbstractChocobo Left in Override for future use
    }

    public void readCustomDataFromNbt(@NotNull NbtCompound compound) {
        this.fromNBT = true;
        this.dataTracker.set(PARAM_CHOCOBO_PROPERTIES, compound.getInt(NBTKEY_CHOCOBO_PROPERTIES));
        this.setGeneration(compound.getInt(NBTKEY_CHOCOBO_GENERATION));
        this.setChocoboAbilitiesFromMask(compound.getByte(NBTKEY_CHOCOBO_ABILITY_MASK));
        this.setChocoboScale(false, compound.getInt(NBTKEY_CHOCOBO_SCALE), true);
        ChocoboInventoryFromNBT(compound);
        // Forced Syncing of the chocobo gear inventory from NBT to prevent onSaddleChanged from being called on loading chocobo gear
        if (compound.contains(NBTKEY_INVENTORY_GEAR, 9)) { // 9 = NbtList.getType()
            NbtList nbtList = compound.getList(NBTKEY_INVENTORY_GEAR, 10); // 10 = NbtCompound.getType()
            this.chocoboGearInventory.clear();
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                int j = nbtCompound.getByte("Slot") & 255;
                if (j < this.chocoboGearInventory.size()) {
                    this.chocoboGearInventory.setStack(j, ItemStack.fromNbt(nbtCompound));
                }
            }
        }
        this.readAngerFromNbt(this.getWorld(), compound);
        super.readCustomDataFromNbt(compound);
        this.fromNBT = false;
    }
    public void writeCustomDataToNbt(@NotNull NbtCompound compound) {
        compound.putInt(NBTKEY_CHOCOBO_PROPERTIES, this.dataTracker.get(PARAM_CHOCOBO_PROPERTIES));
        compound.putInt(NBTKEY_CHOCOBO_GENERATION, this.getGeneration());
        compound.putByte(NBTKEY_CHOCOBO_ABILITY_MASK, this.getChocoboAbilityMask());
        compound.putInt(NBTKEY_CHOCOBO_SCALE, this.getChocoboScale());
        ChocoboInventoryToNBT(compound);
        ChocoboGearInventoryToNBT(compound);
        this.writeAngerToNbt(compound);
        super.writeCustomDataToNbt(compound);
    }
    public void ChocoboInventoryToNBT(NbtCompound compound) {
        NbtList nbtList = new NbtList();
        for (int i = 0; i < this.chocoboInventory.size(); ++i) {
            ItemStack itemStack = this.chocoboInventory.getStack(i);
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte)i);
                itemStack.writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }
        if (!nbtList.isEmpty()) { compound.put(NBTKEY_INVENTORY, nbtList); }
    }
    public void ChocoboGearInventoryToNBT(NbtCompound compound) {
        NbtList nbtList = new NbtList();
        for (int i = 0; i < this.chocoboGearInventory.size(); ++i) {
            ItemStack itemStack = this.chocoboGearInventory.getStack(i);
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte)i);
                itemStack.writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }
        if (!nbtList.isEmpty()) { compound.put(NBTKEY_INVENTORY_GEAR, nbtList); }
    }
    public void ChocoboInventoryFromNBT(@NotNull NbtCompound compound) {
        if (compound.contains(NBTKEY_INVENTORY, 9)) { // 9 = NbtList.getType()
            NbtList nbtList = compound.getList(NBTKEY_INVENTORY, 10); // 10 = NbtCompound.getType()
            this.chocoboInventory.clear();
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                int j = nbtCompound.getByte("Slot") & 255;
                if (j < this.chocoboInventory.size()) {
                    this.chocoboInventory.setStack(j, ItemStack.fromNbt(nbtCompound));
                }
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
        } else if (reason == SpawnReason.SPAWNER) { this.setChocoboScale(this.isMale(), 0, false); }
        chocoboStatShake(EntityAttributes.GENERIC_MAX_HEALTH, "health");
        chocoboStatShake(EntityAttributes.GENERIC_ATTACK_DAMAGE, "attack");
        chocoboStatShake(EntityAttributes.GENERIC_ARMOR, "defense");
        chocoboStatShake(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, "toughness");
        if (getChocoboColor() == ChocoboColor.PURPLE) {
            int chance = isEnd(worldIn) ? 60 : 15;
            if (random.nextInt(100)+1 < chance) {
                this.chocoboInventory.setStack(random.nextInt(18), new ItemStack(ENDER_PEARL.getDefaultStack().split(random.nextInt(3) + 1).getItem()));
            }
            if (random.nextInt(100)+1 < chance) {
                this.chocoboInventory.setStack(random.nextInt(9)+18, new ItemStack(ENDER_PEARL.getDefaultStack().split(random.nextInt(3) + 1).getItem()));
            }
            if (random.nextInt(100)+1 < chance) {
                this.chocoboInventory.setStack(random.nextInt(18)+27, new ItemStack(ENDER_PEARL.getDefaultStack().split(random.nextInt(3) + 1).getItem()));
            }
        }
        return super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }
    public PassiveEntity createChild(ServerWorld world, PassiveEntity other) {
        if (!(other instanceof Chocobo)) {
            DelChoco.LOGGER.warn("Chocobo breeding failed: Other entity is not a Chocobo!");
            return null;
        }
        Chocobo mother, father;
        if (this.isMale() != ((Chocobo)other).isMale()) {
            if (this.isMale()) { //noinspection RedundantCast
                return ((Chocobo)other).createChild(world, this); }  // Ensure the mother is always the one calling the method.
            else {
                mother = this;
                father = ((Chocobo)other);
            }
        } else { return null; } // Cannot breed if both parents are the same
        // Only allowed to run on the Mother Chocobo
        ChocoboBreedInfo breedInfo = new ChocoboBreedInfo(new ChocoboStatSnapshot(mother), new ChocoboStatSnapshot(father));
        return getChicoboFromBreedInfo(breedInfo, world);
    }

    // Combat related
    public boolean isInvulnerableTo(@NotNull DamageSource source) {
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        return super.isInvulnerableTo(source);
    }
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) { return false; }

        Entity attacker = source.getAttacker();
        if (attacker instanceof PlayerEntity player) {
            if (this.isOwner(player)) {
                if (!ChocoboConfig.OWN_CHOCOBO_HITTABLE.get()) { return false; }
            } else if (this.isTamed()) {
                if (!ChocoboConfig.TAMED_CHOCOBO_HITTABLE.get()) { return false; }
            }
        }

        boolean result = super.damage(source, amount);
        if (result && attacker instanceof PlayerEntity player) {
            if (RandomHelper.random.nextInt(100) + 1 > 35) { this.dropItem(ModItems.CHOCOBO_FEATHER); }
        }
        return result;
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
    public double getMountedHeightOffset() {
        // Returns the height offset when mounted, used for riding Chocobos
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        return super.getMountedHeightOffset();
    }
    public void dismountVehicle() {
        BlockPos spot = this.getBlockPos();
        double length = 5D;
        this.setLeashSpot(spot);
        this.setLeashedDistance((int) length);
        this.setMovementType(MovementType.STANDSTILL); // stand still
        this.setMovementTypeByFollowMrHuman(this.followingMrHuman);
        super.dismountVehicle();
    }
    protected void updateInWaterStateAndDoWaterCurrentPushing() {
        // left in for unique Chocobo Checks unable to be done in AbstractChocobo
        super.updateInWaterStateAndDoWaterCurrentPushing();
    }
    public void travel(@NotNull Vec3d travelVector) {
        if (this.isAlive()) {
            if (this.getPrimaryPassenger() instanceof PlayerEntity rider) {
                this.prevY = rider.getYaw();
                this.prevPitch = rider.getPitch();
                this.setYaw(rider.getYaw());
                this.setPitch(rider.getPitch());
                this.setRotation(this.getYaw(), this.getPitch());
                this.headYaw = this.getYaw();
                this.bodyYaw = this.getYaw();

                Vec3d newVector = new Vec3d(rider.sidewaysSpeed * 0.5F, travelVector.y, rider.forwardSpeed); //Strafe - Vertical - Forward

                // reduce movement speed by 75% if moving backwards
                if (newVector.getZ() <= 0.0D) {
                    newVector = new Vec3d(newVector.x, newVector.y, newVector.z * 0.25F);
                }

                if (this.isOnGround()) {
                    this.isChocoboJumping = false;
                }

                if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                    // jump logic
                    if (!this.isChocoboJumping && this.isOnGround()) {
                        Vec3d motion = getVelocity();
                        setVelocity(new Vec3d(motion.x, .6f, motion.z));
                        this.isChocoboJumping = true;
                    }
                }
                if (rider.isTouchingWater() && this.isWaterBreathing()) { // Player is in the water AND Chocobo can breathe underwater
                    Vec3d motion = getVelocity();
                    float verticalMovement = 0.0f;
                    if (MinecraftClient.getInstance().options.jumpKey.isPressed()) { // Move up
                        verticalMovement = 0.5f;
                    } else if (MinecraftClient.getInstance().options.sneakKey.isPressed()) { // Move down
                        verticalMovement = -0.5f;
                    } else if (isChocoboWaterGlide()) { // Glide/neutral buoyancy
                        motion = getVelocity();
                        verticalMovement = (float) (motion.y * 0.65F);
                    }
                    if (MinecraftClient.getInstance().options.jumpKey.isPressed() || MinecraftClient.getInstance().options.sneakKey.isPressed() || isChocoboWaterGlide()) {
                        setVelocity(new Vec3d(motion.x, verticalMovement, motion.z));
                    }
                } else if (rider.isTouchingWater()) {  rider.dismountVehicle(); }
                if (rider.isInLava()) {
                    Vec3d motion = getVelocity();
                    if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                        setVelocity(new Vec3d(motion.x, .5f, motion.z));
                    } else if (this.isFireImmune() && this.getVelocity().y < 0) {
                        int distance = WorldUtils.getDistanceToSurface(this.getBlockPos(), this.getEntityWorld());
                        if (distance > 0) {
                            setVelocity(new Vec3d(motion.x, .05f, motion.z));
                        }
                    }
                }
                // Implement gliding mechanics when mounted
                if (!this.isOnGround() && !this.isTouchingWater() && !this.isInLava() && this.getVelocity().y < 0) {
                    Vec3d motion = getVelocity();
                    if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                        // When the space key is pressed while falling, reduce fall speed by 60% (to 40% of normal)
                        setVelocity(new Vec3d(motion.x, motion.y * 0.4F, motion.z));
                    } else {
                        // When mounted but not pressing space, reduce fall speed by 20% (to 80% of normal)
                        setVelocity(new Vec3d(motion.x, motion.y * 0.8F, motion.z));
                    }
                }
                if (MinecraftClient.getInstance().options.sprintKey.isPressed()) {
                    this.setSprinting(this.isOnGround());
                } else { this.setSprinting(false); }

                this.setMovementSpeed((float) Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue());
                super.travel(newVector);
            } else {
                // Unmounted chocobo - apply slow fall effect when falling
                if (!this.isOnGround() && !this.isTouchingWater() && !this.isInLava() && this.getVelocity().y < 0) {
                    Vec3d motion = getVelocity();
                    // Slow fall by 40-60% when not mounted (using 50% as middle ground)
                    float fallReductionFactor = 0.5F; // Fall at 50% normal speed
                    setVelocity(new Vec3d(motion.x, motion.y * fallReductionFactor, motion.z));
                }
                super.travel(travelVector);
            }
        }
    }
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) { return; }
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
        this.setStepHeight(maxStepUp);
        this.fallDistance = 0f;

        if (this.TimeSinceFeatherChance == 3000) {
            this.TimeSinceFeatherChance = 0;
            if ((float) random() < .25) { this.dropFeather(); }
        } else { this.TimeSinceFeatherChance++; }

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
        }
    }
    private boolean interactInvRide(PlayerEntity player, @NotNull ItemStack stack) {
        if (this.getEntityWorld().isClient() || !stack.isEmpty() || !this.isTamed()) { return false; }
        if (player.isSneaking() && player instanceof ServerPlayerEntity serverPlayer) {
            if (ChocoboConfig.OWNER_ONLY_ACCESS.get()) {
                if (isOwner(player)) { this.displayChocoboInventory(serverPlayer); }
                else { player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.not_owner"), true); }
            } else { this.displayChocoboInventory(serverPlayer); }
            return true;
        } else if (this.isSaddled()) {
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
        if (this.isBaby()) {
            if (pStack == GYSAHL_CAKE.asItem()) {
                this.eat(player, Hand.MAIN_HAND, stack);
                this.growUp(25);
                return true;
            } else { return false; }
        }
        if (pStack == GYSAHL_GREEN_ITEM) {
            if (this.isTamed()) {
                if (this.getHealth() != this.getMaxHealth()) {
                    this.eat(player, hand, stack);
                    double health = this.getHealth();
                    heal(ChocoboConfig.DEFAULT_HEALING.get());
                    double newHealth = this.getHealth();
                    player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".chocobo.heal.amount", this.getName(), (int) health, (int) newHealth), true);
                } else {
                    player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.heal_fail"), true);
                }
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
            return true;
        }
        if (this.fruitAteTimer < 1) {
            boolean ate = false;
            if (pStack == GOLDEN_GYSAHL_GREEN) {
                increaseStat(this, all, 2, player);
                this.fruitAteTimer = ChocoboConfig.FRUIT_COOL_DOWN.get();
                ate = true;
            }
            if (pStack == PINK_GYSAHL_GREEN) {
                increaseStat(this, health, 2, player);
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
        if (this.getEntityWorld().isClient() || this.isBaby()) { return false; }
        if (pStack instanceof ChocoboSaddleItem && !this.isSaddled()) {
            this.chocoboGearInventory.setStack(SADDLE_SLOT, stack.copy().split(1));
            if (!player.isCreative()) { player.getMainHandStack().decrement(1); }
            this.onSaddleChanged();
            return true;
        }
        if (pStack instanceof ChocoboArmorItems armorItem) {
            EquipmentSlot slotType = armorItem.getSlotType();
            if (slotType == EquipmentSlot.CHEST && !this.isChestArmored()) {
                this.chocoboGearInventory.setStack(ARMOR_SLOT, stack.copy().split(1));
                if (!player.isCreative()) { player.getMainHandStack().decrement(1); }
                return true;
            }
            if (slotType == EquipmentSlot.HEAD && !this.isHeadArmored()) {
                this.chocoboGearInventory.setStack(HEAD_SLOT, stack.copy().split(1));
                if (!player.isCreative()) { player.getMainHandStack().decrement(1); }
                return true;
            }
            if (slotType == EquipmentSlot.LEGS && !this.isLegsArmored()) {
                this.chocoboGearInventory.setStack(LEGS_SLOT, stack.copy().split(1));
                if (!player.isCreative()) { player.getMainHandStack().decrement(1); }
                return true;
            }
            if (slotType == EquipmentSlot.FEET && !this.isFeetArmored()) {
                this.chocoboGearInventory.setStack(FEET_SLOT, stack.copy().split(1));
                if (!player.isCreative()) { player.getMainHandStack().decrement(1); }
                return true;
            }
        }
        if (pStack instanceof ChocoboWeaponItems && !this.isWeaponArmed()) {
            this.chocoboGearInventory.setStack(WEAPON_SLOT, stack.copy().split(1));
            if (!player.isCreative()) { player.getMainHandStack().decrement(1); }
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
                        if (!player.isCreative()) {
                            player.getMainHandStack().decrement(1);
                        }
                    } else { player.sendMessage(Text.translatable(DelChoco.DELCHOCO_ID + ".entity_chocobo.not_owner"), true); }
                    return true;
                }
            } else {
                this.setCustomName(stack.getName());
                this.setCustomNameVisible(true);
                if (!player.isCreative()) {
                    player.getMainHandStack().decrement(1);
                }
                return true;
            }
        }
        if (this.isTamed()) {
            if (pStack == CHOCOBO_FEATHER.asItem()) {
                if (isOwner(player)) {
                    this.setCustomNameVisible(!this.isCustomNameVisible());
                    if (!player.isCreative()) {
                        player.getMainHandStack().decrement(1);
                    }
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
                this.setLeashedDistance((int) dist);
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
                    this.setLeashedDistance((int) distance);
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
                if (!player.isCreative()) {
                    player.getMainHandStack().decrement(1);
                }
                return true;
            }
        }
        return false;
    }
    public ActionResult interactAt(@NotNull PlayerEntity player, Vec3d vec, Hand hand) {
        if (this.getEntityWorld().isClient()) { return super.interactAt(player, vec, hand); }
        ItemStack heldItemStack = player.getStackInHand(hand);

        if (this.isSaddled() && interactInvRide(player, heldItemStack)) { return ActionResult.SUCCESS; }
        if (interactEquip(player, heldItemStack)) { return ActionResult.SUCCESS; }
        if (interactFeed(player, heldItemStack, hand)) { return ActionResult.SUCCESS; }
        if (interactUtil(player, heldItemStack)) { return ActionResult.SUCCESS; }
        if (interactDye(player, heldItemStack)) { return ActionResult.SUCCESS; }

        return super.interactAt(player, vec, hand);
    }
    private void displayChocoboInventory(@NotNull ServerPlayerEntity player) {
        if (player.currentScreenHandler != player.playerScreenHandler) { player.closeHandledScreen(); }
        ((ServerPlayerEntityAccessor) player).callIncrementScreenHandlerSyncId();
        int syncId = ((ServerPlayerEntityAccessor) player).getScreenHandlerSyncId();
        player.networkHandler.sendPacket(new OpenHorseScreenS2CPacket(syncId, this.chocoboInventory.size(), this.getId()));
        player.currentScreenHandler = new ChocoboScreenHandler(syncId, player.getInventory(), this);
        ((ServerPlayerEntityAccessor) player).callOnScreenHandlerOpened(player.currentScreenHandler);
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
    public boolean tryAttack(Entity entity) {
        boolean result = super.tryAttack(entity);

        if (result && entity instanceof LivingEntity target) {
            handleChocoboSweep(this, target);
            // These effects are applied only if the config is enabled and regardless of the weapon used.
            boolean config = ChocoboConfig.EXTRA_CHOCOBO_EFFECT.get();
            if (config) {
                if (target instanceof SpiderEntity e) { onHitMobChance(10, STRING, e); }
                if (target instanceof CaveSpiderEntity e) { onHitMobChance(5, FERMENTED_SPIDER_EYE, e); }
                if (target instanceof SkeletonEntity e) { onHitMobChance(10, BONE, e); }
                if (target instanceof WitherSkeletonEntity e) { onHitMobChance(10, CHARCOAL, e); }
                if (target instanceof IronGolemEntity e) { onHitMobChance(5, POPPY, e); }
                if (target.getEquippedStack(EquipmentSlot.MAINHAND) != ItemStack.EMPTY && !(target instanceof AbstractChocobo)) {
                    if (onHitMobChance(30)) {
                        target.dropItem(target.getEquippedStack(EquipmentSlot.MAINHAND).getItem());
                        target.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    }
                }
                if (target.getEquippedStack(EquipmentSlot.OFFHAND) != ItemStack.EMPTY && !(target instanceof AbstractChocobo)) {
                    if (onHitMobChance(10)) {
                        target.dropItem(target.getEquippedStack(EquipmentSlot.OFFHAND).getItem());
                        target.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
                    }
                }
            }
        }
        return result;
    }
    public void onDeath(DamageSource source) {
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        super.onDeath(source);
    }
    public void applyDamageEffects(LivingEntity attacker, Entity target) {
        // Left in for unique Chocobo Checks unable to be done in AbstractChocobo
        super.applyDamageEffects(attacker, target);
    }

    private void handleChocoboSweep(Chocobo attacker, LivingEntity target) {
        if (attacker.getWorld().isClient()) { return; }
        if (target == null || !attacker.isAlive()) { return; }

        ItemStack heldItem = attacker.getWeapon();
        if (!(heldItem.getItem() instanceof SwordItem)) { return; }
        boolean isPlayer = target instanceof PlayerEntity;

        int chocoboSweepLevel = EnchantmentHelper.getLevel(ModEnchantments.CHOCOBO_SWEEP, heldItem);
        float damageMultiplier = ChocoboSweepEnchantment.getDamageMultiplier(chocoboSweepLevel);
        if (chocoboSweepLevel <= 0 || damageMultiplier <= 0) { return; }

        float baseDamage = (float) attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        baseDamage += EnchantmentHelper.getAttackDamage(heldItem, target.getGroup());
        float sweepDamage = baseDamage * damageMultiplier;

        List<LivingEntity> nearbyEntities = attacker.getWorld().getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(3.5D, 2.5D, 3.5D));

        for (LivingEntity nearbyEntity : nearbyEntities) {
            if (nearbyEntity != attacker && nearbyEntity != target) {

                if (attacker.isTeammate(nearbyEntity)) { continue; }
                boolean skipDamage = false;

                LivingEntity owner = attacker.getOwner();
                if (nearbyEntity instanceof PlayerEntity targetPlayer) {
                    if (targetPlayer.isCreative() || targetPlayer.isSpectator() || !isPlayer) { continue; }
                    // Only apply sweep to players if the attacker is a player
                }

                if (nearbyEntity == owner) { skipDamage = true; }
                if (owner != null && owner.isTeammate(nearbyEntity)) { skipDamage = true; }

                if (nearbyEntity instanceof TameableEntity tameableTarget) {
                    LivingEntity targetOwner = tameableTarget.getOwner();

                    if (owner != null) {
                        if (targetOwner == owner) { skipDamage = true; }
                        if (targetOwner != null && owner.isTeammate(targetOwner)) {
                            skipDamage = owner.getScoreboardTeam() != null && !owner.getScoreboardTeam().isFriendlyFireAllowed();
                        }
                    }
                    if (targetOwner != null) {
                        if (targetOwner == owner) { skipDamage = true; }
                        if (owner != null && owner.isTeammate(targetOwner)) { skipDamage = true; }
                    }
                }

                if (skipDamage) { continue; }
                if (attacker.distanceTo(nearbyEntity) >= CHOCOBO_SWING_DISTANCE) { continue; }

                if (owner != null && nearbyEntity instanceof PlayerEntity targetPlayer) {
                    if (owner.getScoreboardTeam() != null && targetPlayer.getScoreboardTeam() != null) {
                        if (owner.getScoreboardTeam() == targetPlayer.getScoreboardTeam()) {
                            if (!owner.getScoreboardTeam().isFriendlyFireAllowed()) { continue; }
                        }
                    }
                }

                DamageSource damageSource = new DamageSource(attacker.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.CHOCOBO_SWEEP_ATTACK), attacker);

                if (nearbyEntity.isInvulnerableTo(damageSource)) { continue; }
                
                nearbyEntity.takeKnockback(knockbackCalculation(sweepDamage, attacker), MathHelper.sin(attacker.getYaw() * 0.017453292F), -MathHelper.cos(attacker.getYaw() * 0.017453292F));

                nearbyEntity.damage(damageSource, sweepDamage);
            }
        }

        attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);

        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, attacker.getX(), attacker.getBodyY(0.5), attacker.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    /**
     * Counts all loaded Chocobos in the world and returns counts of wild and tamed
     * @return int[2] array with [0]=wild count, [1]=tamed count
     */
    private int[] countLoadedChocobos() {
        // Performance optimization: Only do this check occasionally for wild chocobos
        if ((this.isTamed() || this.isBaby()) && this.age % 400 != 0) { return null; }

        int wildCount = 0;
        int tamedCount = 0;

        // Use entity type predicate for faster filtering
        for (Chocobo chocobo : this.getWorld().getEntitiesByType(
                ModEntities.CHOCOBO_ENTITY,
                new Box(-Double.MAX_VALUE/2, this.getWorld().getBottomY(), -Double.MAX_VALUE/2, Double.MAX_VALUE/2, this.getWorld().getTopY(), Double.MAX_VALUE/2),
                entity -> true)) {
            if (chocobo.isTamed()) { tamedCount++; }
            else if (!chocobo.isBaby()) { wildCount++; }
        }
        return new int[]{wildCount, tamedCount};
    }
    public void checkDespawn() {
        // Standard despawn logic
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
            this.discard();
            return;
        }

        // Only apply population control to non-persistent chocobos (CannotDespawn includes persistent ones)
        if (!this.cannotDespawn()) {
            // Global population control
            if (WorldConfig.CHOCOBO_ENABLE_WILD_SPAWN_LIMIT.get() && !this.isTamed() && !this.isBaby()) {
                int[] counts = countLoadedChocobos();
                if (counts != null) {
                    int wildCount = counts[0];
                    int tamedCount = counts[1];

                    // Determine the current wild chocobo limit based on tamed count
                    int currentWildLimit = tamedCount >= WorldConfig.CHOCOBO_SPAWN_MAX_WILD_NUMBER.get() ?
                                           WorldConfig.CHOCOBO_SPAWN_REDUCED_WILD_NUMBER.get() :
                                           WorldConfig.CHOCOBO_SPAWN_MAX_WILD_NUMBER.get();

                    // If we're over the limit, increase despawn chance significantly
                    if (wildCount > currentWildLimit) {
                        // More aggressive despawn if we're significantly over the limit
                        int excessChocobos = wildCount - currentWildLimit;
                        int limitGraceBreedChocobos = this.getGeneration() > 1 ? WorldConfig.CHOCOBO_SPAWN_DESPAWN_CHANCE_FOR_BREED.get() : 75;
                        int despawnChance = Math.min(5 + (excessChocobos * 2), limitGraceBreedChocobos); // Cap at 75% chance

                        if (this.random.nextInt(100) < despawnChance) {
                            this.discard();
                            return;
                        }
                    }
                }
            }

            // Standard vanilla-like despawn logic
            Entity entity = this.getWorld().getClosestPlayer(this, -1.0D);
            if (entity != null) {
                double d0 = entity.squaredDistanceTo(this);
                int i = CREATURE.getImmediateDespawnRange()*5;
                int j = i * i;
                if (d0 > (double)j && this.canImmediatelyDespawn(d0)) { this.discard(); }

                int k = (CREATURE.getImmediateDespawnRange()*2);
                int l = k * k;
                if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && d0 > (double)l && this.canImmediatelyDespawn(d0)) { this.discard(); }
                else if (d0 < (double)l) { this.despawnCounter = 0; }
            }
        } else { this.despawnCounter = 0; }
    }
    // Ride Related
    public int getRideTickDelay() { return this.rideTickDelay; }
}
