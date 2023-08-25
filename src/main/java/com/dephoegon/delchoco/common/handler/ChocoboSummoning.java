package com.dephoegon.delchoco.common.handler;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.init.ModEntities;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.dephoegon.delchoco.aid.chocoboChecks.*;
import static com.dephoegon.delchoco.common.items.ChocoboSpawnItemHelper.*;
import static com.dephoegon.delchoco.utils.RandomHelper.random;
import static net.minecraft.block.Blocks.*;
import static net.minecraft.entity.attribute.EntityAttributes.*;
import static net.minecraft.item.Items.ENDER_PEARL;

public class ChocoboSummoning {
    public final boolean isRandomAlter;
    private final ChocoboColor color;
    private final ItemStack summonItem;
    private int damage;

    public ChocoboSummoning(@NotNull World worldIn, @NotNull BlockPos alterBlock, PlayerEntity player, ItemStack summonItem) {
        BlockState Alter = worldIn.getBlockState(alterBlock).getBlock().getDefaultState();
        this.color = TARGETED_ALTERS.get(Alter);
        this.isRandomAlter = randomAlter(Alter);
        this.summonItem = summonItem;
        if (isRandomAlter || color != null) alterSwitch(color, alterBlock, worldIn, player);
    }
    private void alterSwitch(ChocoboColor validAlterBlock, BlockPos pos, World worldIn, PlayerEntity player) {
        BlockState pillar;
        if(validAlterBlock != null)  {
            pillar = switch (validAlterBlock) {
                case RED -> RED_TERRACOTTA.getDefaultState();
                case PURPLE -> PURPUR_PILLAR.getDefaultState();
                case PINK -> MUSHROOM_STEM.getDefaultState();
                case BLUE -> DRIED_KELP_BLOCK.getDefaultState();
                case GOLD -> GOLD_BLOCK.getDefaultState();
                case BLACK -> CACTUS.getDefaultState();
                case FLAME -> MAGMA_BLOCK.getDefaultState();
                case GREEN -> MOSS_BLOCK.getDefaultState();
                case WHITE -> SNOW_BLOCK.getDefaultState();
                default -> HAY_BLOCK.getDefaultState();
            };
        } else { pillar = null; }
        boolean summon = pillarCheck(pos, worldIn, pillar, player) && baseCheck(pos, worldIn, player);
        if (summon) { cost(player, pos, worldIn); summonChocobo(worldIn, pos, player); }
    }
    private boolean pillarCheck(BlockPos alterPOS, World worldIn, BlockState pillar, PlayerEntity player) {
        for (int x = -3; x < 4; x++) {
            for (int z = -3; z < 4; z++) {
                for (int y = -2; y < 2; y++) {
                    BlockState blockState = worldIn.getBlockState(new BlockPos(alterPOS.getX()+x, alterPOS.getY()+y, alterPOS.getZ()+z));
                    if ((x == -3 || x == 3) && (z == -3 || z == 3)) {
                        boolean swapCheck = pillar == null ? !blockState.isAir() : blockState != pillar;
                        String out = swapCheck ? pillar == null ? ".alter.invalid_air" : ".alter.invalid_pillar" : null;
                        if (out != null) { player.sendMessage(new TranslatableText(DelChoco.DELCHOCO_ID + out), true); return false; }
                        continue;
                    }
                    if (x == 0 && y == 0 && z ==0) { continue; }
                    if (y < 0) { if (x < 2 && x > -2 && z < 2 && z > -2) { continue; } }
                    if (y < -1) { if (x < 3 && x > -3 && z < 3 && z > -3) { continue; } }
                    if (!blockState.isAir()) {
                        player.sendMessage(new TranslatableText(DelChoco.DELCHOCO_ID + ".alter.invalid_air"), true);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    private boolean baseCheck(BlockPos alterPOS, World worldIn, PlayerEntity player) {
        for (int x = -3; x < 4; x++) {
            for (int z = -3; z < 4; z++) {
                for (int y = -3; y < 0; y++) {
                    BlockState baseCheck = worldIn.getBlockState(new BlockPos(alterPOS.getX()+x, alterPOS.getY()+y, alterPOS.getZ()+z));
                    if (x == 0 && z == 0) { if (!baseCheck.getFluidState().isStill()) {
                        player.sendMessage(new TranslatableText(DelChoco.DELCHOCO_ID + ".alter.invalid_center"), true);
                        return false;
                    } continue; }
                    else if (y > -3 && (x < -2 || x > 2 || z < -2 || z > 2)) { continue; }
                    else if (y > -2 && (x < -1 || x > 1 || z < -1 || z > 1)) { continue; }
                    if (!alterBlocks(baseCheck.getBlock().getDefaultState())) {
                        String isAir = baseCheck.isAir() ? ".invalid_air_base" : ".alter.invalid_base";
                        player.sendMessage(new TranslatableText(DelChoco.DELCHOCO_ID + isAir), true);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    private @NotNull Text customName() { return summonItem.getName(); }
    private void summonChocobo(@NotNull World worldIn, BlockPos pos, PlayerEntity player) {
        if (!worldIn.isClient()) {
            ServerWorldAccess serverWorldAccess = (ServerWorldAccess) worldIn;
            final Chocobo chocobo = ModEntities.CHOCOBO_ENTITY.create(worldIn);
            if (chocobo != null) {
                if (player != null) { if (player.isInSneakingPose()) { chocobo.setBreedingAge(-7500); } }
                chocobo.updatePositionAndAngles(pos.getX() + placeRange(random.nextInt(100) + 1), pos.getY() + 1.5F, pos.getZ() + placeRange(random.nextInt(100) + 1), MathHelper.wrapDegrees(worldIn.random.nextFloat() * 360.0F), 0.0F);
                chocobo.headYaw = chocobo.getYaw();
                chocobo.bodyYaw = chocobo.getYaw();
                if (!isRandomAlter) {
                    chocobo.setChocoboColor(color);
                    chocobo.setFromEgg(true);
                    chocobo.setFlame(color == ChocoboColor.FLAME);
                    chocobo.setWaterBreath(isWaterBreathingChocobo(color));
                    chocobo.setWitherImmune(isWitherImmuneChocobo(color));
                    chocobo.setPoisonImmune(isPoisonImmuneChocobo(color));
                }
                if (summonItem.hasCustomName()) { chocobo.setCustomName(customName()); }
                chocoboStatShake(GENERIC_MAX_HEALTH, "health", chocobo);
                chocoboStatShake(GENERIC_ATTACK_DAMAGE, "attack", chocobo);
                chocoboStatShake(GENERIC_ARMOR, "defense", chocobo);
                chocoboStatShake(GENERIC_ARMOR_TOUGHNESS, "toughness", chocobo);
                if (chocobo.getChocoboColor() == ChocoboColor.PURPLE) {
                    int chance = isEnd(serverWorldAccess) ? 60 : 15;
                    if (random.nextInt(100)+1 < chance) {
                        chocobo.chocoboBackboneInv.setStack(random.nextInt(18), new ItemStack(ENDER_PEARL.getDefaultStack().split(random.nextInt(3) + 1).getItem()));
                    }
                    if (random.nextInt(100)+1 < chance) {
                        chocobo.chocoboBackboneInv.setStack(random.nextInt(9)+18, new ItemStack(ENDER_PEARL.getDefaultStack().split(random.nextInt(3) + 1).getItem()));
                    }
                    if (random.nextInt(100)+1 < chance) {
                        chocobo.chocoboBackboneInv.setStack(random.nextInt(18)+27, new ItemStack(ENDER_PEARL.getDefaultStack().split(random.nextInt(3) + 1).getItem()));
                    }
                }
                chocobo.setChocoboScale(chocobo.isMale(), 0, false);
                chocobo.initialize((ServerWorld)worldIn, worldIn.getLocalDifficulty(chocobo.getBlockPos()), SpawnReason.SPAWN_EGG, null, null);
                worldIn.spawnEntity(chocobo);
                chocobo.playAmbientSound();
                summonItem.decrement(1);
            }
        }
    }
    private void chocoboStatShake(EntityAttribute attribute, String text, @NotNull Chocobo chocobo) {
        int aValue = ChocoboShaker(text);
        Objects.requireNonNull(chocobo.getAttributeInstance(attribute)).addPersistentModifier(new EntityAttributeModifier(text+" variance", aValue, EntityAttributeModifier.Operation.ADDITION));
    }
    private int ChocoboShaker(@NotNull String stat) {
        return switch (stat) {
            case "health" -> boundedRangeModifier(5, 10);
            case "attack", "toughness" -> boundedRangeModifier(1, 3);
            case "defense" -> boundedRangeModifier(2, 4);
            default -> 0;
        };
    }
    private int boundedRangeModifier(int lower, int upper) {
        int range = lower+upper;
        return random.nextInt(range)-lower;
    }
    private int placeRange(int chanceOf100) {
        int negPos = chanceOf100 > 50 ? -1 : 1;
        return random.nextInt(2)+1 + negPos;
    }
    private void cost(PlayerEntity player, BlockPos pos, World worldIn) {
        boolean eatAlter = eatAlter(player);
        if (eatAlter) { worldIn.setBlockState(pos, AIR.getDefaultState()); }
        else {
            if (player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ChocoDisguiseItem) {
                player.getEquippedStack(EquipmentSlot.CHEST).damage(this.damage, player, (event) -> event.sendEquipmentBreakStatus(EquipmentSlot.CHEST));
            }
            if (player.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof ChocoDisguiseItem) {
                player.getEquippedStack(EquipmentSlot.HEAD).damage(this.damage, player, (event) -> event.sendEquipmentBreakStatus(EquipmentSlot.HEAD));
            }
            if (player.getEquippedStack(EquipmentSlot.LEGS).getItem() instanceof ChocoDisguiseItem) {
                player.getEquippedStack(EquipmentSlot.LEGS).damage(this.damage, player, (event) -> event.sendEquipmentBreakStatus(EquipmentSlot.LEGS));
            }
            if (player.getEquippedStack(EquipmentSlot.FEET).getItem() instanceof ChocoDisguiseItem) {
                player.getEquippedStack(EquipmentSlot.FEET).damage(this.damage, player, (event) -> event.sendEquipmentBreakStatus(EquipmentSlot.FEET));
            }
        }
    }
    private boolean eatAlter(@NotNull PlayerEntity player){
        int alterEatChance = 100;
        boolean eatAlter;
        if (player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ChocoDisguiseItem armor && armor.getSlotType() == EquipmentSlot.CHEST) { alterEatChance = alterEatChance-25; }
        if (player.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof ChocoDisguiseItem armor && armor.getSlotType() == EquipmentSlot.HEAD) { alterEatChance = alterEatChance-25; }
        if (player.getEquippedStack(EquipmentSlot.LEGS).getItem() instanceof ChocoDisguiseItem armor && armor.getSlotType() == EquipmentSlot.LEGS) { alterEatChance = alterEatChance-25; }
        if (player.getEquippedStack(EquipmentSlot.FEET).getItem() instanceof ChocoDisguiseItem armor && armor.getSlotType() == EquipmentSlot.FEET) { alterEatChance = alterEatChance-25; }
        eatAlter = random.nextInt(100) + 1 <= alterEatChance;
        this.damage = switch (alterEatChance) {
            case 75 -> 4;
            case 50 -> 3;
            case 25 -> 2;
            case 0 -> 1;
            default -> 0;
        };
        return eatAlter;
    }
}