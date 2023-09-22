package com.dephoegon.delchoco.common.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

import static com.dephoegon.delchoco.common.init.ModItems.GYSAHL_GREEN_SEEDS;

public class GysahlGreenBlock extends PlantBlock
        implements Fertilizable {
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};
    public static final int MAX_AGE = 4;
    public static final IntProperty AGE = IntProperty.of("age", 0, MAX_AGE);
    public GysahlGreenBlock(Settings settings) {
        super(settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(this.getAgeProperty(), 0));
    }
    protected ItemConvertible getSeedsItem() { return GYSAHL_GREEN_SEEDS; }
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) { return floor != null && blockPlaceableOnList().contains(floor.getBlock().getDefaultState()); }
    public IntProperty getAgeProperty() { return AGE; }
    public int getMaxAge() { return MAX_AGE; }
    private @NotNull ArrayList<BlockState> blockPlaceableOnList() {
        ArrayList<BlockState> block_set = new ArrayList<>();
        block_set.add(0, Blocks.END_STONE.getDefaultState());
        block_set.add(1, Blocks.DIRT.getDefaultState());
        block_set.add(2, Blocks.COARSE_DIRT.getDefaultState());
        block_set.add(3, Blocks.PODZOL.getDefaultState());
        block_set.add(4, Blocks.GRASS_BLOCK.getDefaultState());
        block_set.add(5, Blocks.NETHERRACK.getDefaultState());
        block_set.add(6, Blocks.WARPED_WART_BLOCK.getDefaultState());
        block_set.add(7, Blocks.NETHER_WART_BLOCK.getDefaultState());
        block_set.add(8, Blocks.CRIMSON_NYLIUM.getDefaultState());
        block_set.add(9, Blocks.WARPED_NYLIUM.getDefaultState());
        block_set.add(10, Blocks.CLAY.getDefaultState());
        block_set.add(11, Blocks.MOSS_BLOCK.getDefaultState());
        block_set.add(12, Blocks.DRIPSTONE_BLOCK.getDefaultState());
        block_set.add(13, Blocks.TUFF.getDefaultState());
        return block_set;
    }
    public VoxelShape getOutlineShape(@NotNull BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[state.get(this.getAgeProperty())];
    }
    protected int getAge(@NotNull BlockState state) { return state.get(this.getAgeProperty()); }
    public BlockState withAge(int age) { return (BlockState)this.getDefaultState().with(this.getAgeProperty(), age); }
    public boolean isMature(@NotNull BlockState state) { return state.get(this.getAgeProperty()) >= this.getMaxAge(); }
    public boolean hasRandomTicks(BlockState state) { return !this.isMature(state); }
    public void randomTick(BlockState state, @NotNull ServerWorld world, BlockPos pos, Random random) {
        float f;
        int i;
        if (world.getBaseLightLevel(pos, 0) >= 9 && (i = this.getAge(state)) < this.getMaxAge() && random.nextInt((int)(25.0f / (f = GysahlGreenBlock.getAvailableMoisture(this, world, pos))) + 1) == 0) {
            world.setBlockState(pos, this.withAge(i + 1), Block.NOTIFY_LISTENERS);
        }
    }
    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int j;
        int i = this.getAge(state) + this.getGrowthAmount(world);
        if (i > (j = this.getMaxAge())) { i = j; }
        world.setBlockState(pos, this.withAge(i), Block.NOTIFY_LISTENERS);
    }
    protected int getGrowthAmount(@NotNull World world) { return MathHelper.nextInt(world.random, 2, 5); }
    protected static float getAvailableMoisture(Block block, BlockView world, @NotNull BlockPos pos) {
        boolean bl2;
        float f = 1.0f;
        BlockPos blockPos = pos.down();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float g = 0.0f;
                BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
                if (blockState.isOf(Blocks.FARMLAND)) {
                    g = 1.0f;
                    if (blockState.get(FarmlandBlock.MOISTURE) > 0) {
                        g = 3.0f;
                    }
                }
                if (i != 0 || j != 0) {
                    g /= 4.0f;
                }
                f += g;
            }
        }
        BlockPos blockPos2 = pos.north();
        BlockPos blockPos3 = pos.south();
        BlockPos blockPos4 = pos.west();
        BlockPos blockPos5 = pos.east();
        boolean bl = world.getBlockState(blockPos4).isOf(block) || world.getBlockState(blockPos5).isOf(block);
        boolean bl3 = bl2 = world.getBlockState(blockPos2).isOf(block) || world.getBlockState(blockPos3).isOf(block);
        if (bl && bl2) {
            f /= 2.0f;
        } else {
            boolean bl32;
            boolean bl4 = bl32 = world.getBlockState(blockPos4.north()).isOf(block) || world.getBlockState(blockPos5.north()).isOf(block) || world.getBlockState(blockPos5.south()).isOf(block) || world.getBlockState(blockPos4.south()).isOf(block);
            if (bl32) {
                f /= 2.0f;
            }
        }
        return f;
    }
    public boolean canPlaceAt(BlockState state, @NotNull WorldView world, BlockPos pos) {
        return (world.getBaseLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos)) && super.canPlaceAt(state, world, pos);
    }
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof RavagerEntity && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            world.breakBlock(pos, true, entity);
        }
        super.onEntityCollision(state, world, pos, entity);
    }
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) { return new ItemStack(this.getSeedsItem()); }
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) { return !this.isMature(state); }
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) { return true; }
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) { this.applyGrowth(world, pos, state); }
    protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) { builder.add(AGE); }
}