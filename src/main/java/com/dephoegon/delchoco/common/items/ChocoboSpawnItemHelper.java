package com.dephoegon.delchoco.common.items;

import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.handler.ChocoboSummoning;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import static com.dephoegon.delchoco.DelChoco.worldConfigHolder;
import static net.minecraft.block.Blocks.*;

public class ChocoboSpawnItemHelper extends Item {
    public ChocoboSpawnItemHelper(Settings settings) { super(settings); }
    public ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        if (worldConfigHolder.canChocoboSpawn) {
            World worldIn = context.getWorld();
            BlockPos alter = context.getBlockPos();
            PlayerEntity player = context.getPlayer();
            ItemStack summonObject = context.getStack();
            new ChocoboSummoning(worldIn, alter, player, summonObject);
            return ActionResult.PASS;
        } else { return ActionResult.FAIL; }
    }
    public static final Map<BlockState, ChocoboColor> TARGETED_ALTERS = Util.make(Maps.newHashMap(), (map) ->{
        map.put(MAGMA_BLOCK.getDefaultState(), ChocoboColor.FLAME);
        map.put(PURPUR_BLOCK.getDefaultState(), ChocoboColor.PURPLE);
        map.put(END_STONE_BRICKS.getDefaultState(), ChocoboColor.PURPLE);
        map.put(MYCELIUM.getDefaultState(), ChocoboColor.PINK);
        map.put(MUSHROOM_STEM.getDefaultState(), ChocoboColor.PINK);
        map.put(RED_MUSHROOM_BLOCK.getDefaultState(), ChocoboColor.PINK);
        map.put(BROWN_MUSHROOM_BLOCK.getDefaultState(), ChocoboColor.PINK);
        map.put(ICE.getDefaultState(), ChocoboColor.WHITE);
        map.put(BLUE_ICE.getDefaultState(), ChocoboColor.WHITE);
        map.put(PACKED_ICE.getDefaultState(), ChocoboColor.WHITE);
        map.put(BIRCH_WOOD.getDefaultState(), ChocoboColor.WHITE);
        map.put(BRAIN_CORAL.getDefaultState(), ChocoboColor.BLUE);
        map.put(BRAIN_CORAL_FAN.getDefaultState(), ChocoboColor.BLUE);
        map.put(BRAIN_CORAL_BLOCK.getDefaultState(), ChocoboColor.BLUE);
        map.put(TUBE_CORAL.getDefaultState(), ChocoboColor.BLUE);
        map.put(TUBE_CORAL_FAN.getDefaultState(), ChocoboColor.BLUE);
        map.put(TUBE_CORAL_BLOCK.getDefaultState(), ChocoboColor.BLUE);
        map.put(BUBBLE_CORAL.getDefaultState(), ChocoboColor.BLUE);
        map.put(BUBBLE_CORAL_FAN.getDefaultState(), ChocoboColor.BLUE);
        map.put(BUBBLE_CORAL_BLOCK.getDefaultState(), ChocoboColor.BLUE);
        map.put(FIRE_CORAL.getDefaultState(), ChocoboColor.BLUE);
        map.put(FIRE_CORAL_FAN.getDefaultState(), ChocoboColor.BLUE);
        map.put(FIRE_CORAL_BLOCK.getDefaultState(), ChocoboColor.BLUE);
        map.put(HORN_CORAL.getDefaultState(), ChocoboColor.BLUE);
        map.put(HORN_CORAL_FAN.getDefaultState(), ChocoboColor.BLUE);
        map.put(HORN_CORAL_BLOCK.getDefaultState(), ChocoboColor.BLUE);
        map.put(RED_SAND.getDefaultState(), ChocoboColor.RED);
        map.put(RED_SANDSTONE.getDefaultState(), ChocoboColor.RED);
        map.put(CUT_RED_SANDSTONE.getDefaultState(), ChocoboColor.RED);
        map.put(CHISELED_RED_SANDSTONE.getDefaultState(), ChocoboColor.RED);
        map.put(SMOOTH_RED_SANDSTONE.getDefaultState(), ChocoboColor.RED);
        map.put(TERRACOTTA.getDefaultState(), ChocoboColor.RED);
        map.put(MOSS_BLOCK.getDefaultState(), ChocoboColor.GREEN);
        map.put(CLAY.getDefaultState(), ChocoboColor.GREEN);
        map.put(JUNGLE_WOOD.getDefaultState(), ChocoboColor.GREEN);
        map.put(SAND.getDefaultState(), ChocoboColor.BLACK);
        map.put(SANDSTONE.getDefaultState(), ChocoboColor.BLACK);
        map.put(SMOOTH_SANDSTONE.getDefaultState(), ChocoboColor.BLACK);
        map.put(CUT_SANDSTONE.getDefaultState(), ChocoboColor.BLACK);
        map.put(CHISELED_SANDSTONE.getDefaultState(), ChocoboColor.BLACK);
        map.put(GOLD_BLOCK.getDefaultState(), ChocoboColor.GOLD);
        map.put(GRASS_BLOCK.getDefaultState(), ChocoboColor.YELLOW);
    });
    public static boolean alterBlocks(BlockState blockState) {
        ArrayList<BlockState> approvedList = new ArrayList<>();
        approvedList.add(HAY_BLOCK.getDefaultState());
        approvedList.add(OAK_WOOD.getDefaultState());
        approvedList.add(SPRUCE_WOOD.getDefaultState());
        approvedList.add(BIRCH_WOOD.getDefaultState());
        approvedList.add(ACACIA_WOOD.getDefaultState());
        approvedList.add(DARK_OAK_WOOD.getDefaultState());
        approvedList.add(STRIPPED_OAK_WOOD.getDefaultState());
        approvedList.add(STRIPPED_SPRUCE_WOOD.getDefaultState());
        approvedList.add(STRIPPED_BIRCH_WOOD.getDefaultState());
        approvedList.add(STRIPPED_JUNGLE_WOOD.getDefaultState());
        approvedList.add(STRIPPED_DARK_OAK_WOOD.getDefaultState());
        approvedList.add(STRIPPED_ACACIA_WOOD.getDefaultState());
        approvedList.add(OAK_LEAVES.getDefaultState());
        approvedList.add(SPRUCE_LEAVES.getDefaultState());
        approvedList.add(BIRCH_LEAVES.getDefaultState());
        approvedList.add(JUNGLE_LEAVES.getDefaultState());
        approvedList.add(ACACIA_LEAVES.getDefaultState());
        approvedList.add(DARK_OAK_LEAVES.getDefaultState());
        approvedList.add(FLOWERING_AZALEA_LEAVES.getDefaultState());
        approvedList.add(AZALEA_LEAVES.getDefaultState());
        approvedList.add(CLAY.getDefaultState());
        approvedList.add(MOSS_BLOCK.getDefaultState());
        approvedList.add(END_STONE.getDefaultState());
        approvedList.add(WARPED_NYLIUM.getDefaultState());
        approvedList.add(CRIMSON_NYLIUM.getDefaultState());
        approvedList.add(SAND.getDefaultState());
        approvedList.add(RED_SAND.getDefaultState());
        return approvedList.contains(blockState);
    }
    public static boolean randomAlter(BlockState blockState) {
        ArrayList<BlockState> listOfRandomAlters = new ArrayList<>();
        listOfRandomAlters.add(DIRT.getDefaultState());
        return listOfRandomAlters.contains(blockState);
    }
}