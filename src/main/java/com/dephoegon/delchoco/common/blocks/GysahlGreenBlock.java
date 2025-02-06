package com.dephoegon.delchoco.common.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static com.dephoegon.delchoco.aid.GysahlGreenBiomes.getBiomeID;
import static com.dephoegon.delchoco.aid.GysahlGreenBiomes.getGreenCount;
import static com.dephoegon.delchoco.common.init.ModItems.GYSAHL_GREEN_SEEDS;

public class GysahlGreenBlock extends PlantBlock
        implements Fertilizable {
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};
    public static final int MAX_AGE = 4;
    public static final int MAX_BIOME = getGreenCount(); // This is set in the GysahlGreenBiomes class where the biome checks are stored for biome set for each plant possibility to get from a the greens as a bonus.
    public static final IntProperty AGE = IntProperty.of("age", 0, MAX_AGE);
    public static final IntProperty BIOME = IntProperty.of("biome", -1, MAX_BIOME); // -1 is used to indicate that the block has not been placed yet or was placed by world gen and used to allow onBreak (player call) to set the biome property in line with the break and provide the correct biome for the block.
    public GysahlGreenBlock(Settings settings) {
        super(settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(this.getAgeProperty(), 0).with(this.getBiomeProperty(), -1));
    }
    protected ItemConvertible getSeedsItem() { return GYSAHL_GREEN_SEEDS; }
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) { return floor != null && blockPlaceableOnList().contains(floor.getBlock().getDefaultState()); }
    public IntProperty getAgeProperty() { return AGE; }
    public int getMaxAge() { return MAX_AGE; }
    public IntProperty getBiomeProperty() { return BIOME; }
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
    public void onPlaced(@NotNull World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) { super.onPlaced(world, pos, state, placer, itemStack); }
        else { super.onPlaced(world, pos, state.with(BIOME, getBiomePlacedID(world, pos)), placer, itemStack); }
    }
    public void onBreak(@NotNull World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient || state.get(BIOME) != -1) { super.onBreak(world, pos, state, player); }
        else { super.onBreak(world, pos, state.with(BIOME, getBiomePlacedID(world, pos)), player); }
    } // intended to set the biome property for blocks placed by world gen and not updated by grow.
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(@NotNull BlockState state, BlockView world, BlockPos pos, ShapeContext context) { return AGE_TO_SHAPE[state.get(this.getAgeProperty())]; }
    protected int getAge(@NotNull BlockState state) { return state.get(this.getAgeProperty()); }
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    protected int getBiomePlacedID(@NotNull WorldAccess world, @NotNull BlockPos pos) { return getBiomeID(world.getBiome(pos.down()).getKey().get()); }
    public BlockState withAge(int age) { return (BlockState)this.getDefaultState().with(this.getAgeProperty(), age); }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isMature(@NotNull BlockState state) { return state.get(this.getAgeProperty()) >= this.getMaxAge(); }
    public boolean hasRandomTicks(BlockState state) { return !this.isMature(state); }
    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int j;
        int i = this.getAge(state) + this.getGrowthAmount(world);
        if (i > (j = this.getMaxAge())) { i = j; }
        world.setBlockState(pos, this.withAge(i).with(BIOME, getBiomePlacedID(world, pos)), Block.NOTIFY_LISTENERS);
    }
    protected int getGrowthAmount(@NotNull World world) { return MathHelper.nextInt(world.random, 2, 5); }
    public boolean canPlaceAt(BlockState state, @NotNull WorldView world, @NotNull BlockPos pos) { return isValidPlacementPosition(world, pos) && world.getBlockState(pos).isAir(); }
    private boolean isValidPlacementPosition(@NotNull WorldView world, @NotNull BlockPos pos) { return blockPlaceableOnList().contains(world.getBlockState(pos.down()).getBlock().getDefaultState()); } // Checks if the block can be placed on the block below, specifically for the Gysahl Green Block & split from the canPlaceAt method to allow for the calling of getStateForNeighborUpdate to check for the base block & not needing to modify canPlaceAt to include a copy of itself.
    public BlockState getStateForNeighborUpdate(@NotNull BlockState state, Direction direction, BlockState neighborState, @NotNull WorldAccess world, BlockPos pos, @NotNull BlockPos neighborPos) {
        if (state.get(BIOME) == -1) { state.with(BIOME, getBiomePlacedID(world, pos)); } // if the block was placed by world gen, set the biome property.
        return isValidPlacementPosition(world, pos) ? state : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        // The call to super is only needed to ensure use of vanilla code and will always result in failure (within the super) resulting in an Air Block. Vanilla code is used, in case of other mods somehow modifying this call in plantBlock
    }
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof RavagerEntity && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            world.breakBlock(pos, true, entity);
        }
        super.onEntityCollision(state, world, pos, entity);
    }
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) { return new ItemStack(this.getSeedsItem()); }
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) { return !this.isMature(state); }

    public boolean canGrow(World world, net.minecraft.util.math.random.Random random, BlockPos pos, BlockState state) { return true; }
    public void grow(ServerWorld world, net.minecraft.util.math.random.Random random, BlockPos pos, BlockState state) { this.applyGrowth(world, pos, state); }
    protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) { builder.add(AGE).add(BIOME); }
}