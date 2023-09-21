package com.dephoegon.delchoco.common.blocks;

import com.dephoegon.delchoco.common.blockentities.ChocoboNestBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

import static com.dephoegon.delchoco.aid.ChocoList.STRAW_NEST_BLOCK_ENTITY;

public class StrawNestBlock extends BlockWithEntity {
    public static final BooleanProperty HAS_EGG = BooleanProperty.of("egg");
    private static final VoxelShape EMPTY_SHAPE = Stream.of(
            Block.createCuboidShape(1, 0, 1, 15, 1, 15),
            Block.createCuboidShape(0, 1, 0, 16, 3, 2),
            Block.createCuboidShape(0, 1, 14, 16, 3, 16),
            Block.createCuboidShape(0, 1, 2, 2, 3, 14),
            Block.createCuboidShape(14, 1, 2, 16, 3, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final VoxelShape SHAPE = Stream.of(
            Block.createCuboidShape(5, 0, 5, 11, 1, 11),
            Block.createCuboidShape(4, 1, 4, 12, 6, 12),
            Block.createCuboidShape(5, 6, 5, 11, 8, 11),
            Block.createCuboidShape(6, 8, 6, 10, 10, 10),
            Block.createCuboidShape(14, 1, 2, 16, 3, 14),
            Block.createCuboidShape(1, 0, 1, 15, 1, 15),
            Block.createCuboidShape(0, 1, 0, 16, 3, 2),
            Block.createCuboidShape(0, 1, 14, 16, 3, 16),
            Block.createCuboidShape(0, 1, 2, 2, 3, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    public StrawNestBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HAS_EGG, false));
    }
    protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) { builder.add(HAS_EGG); }
    public @NotNull BlockRenderType getRenderType(@NotNull BlockState state) { return BlockRenderType.MODEL; }
    @Override
    public VoxelShape getOutlineShape(@NotNull BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(HAS_EGG) ? SHAPE : EMPTY_SHAPE;
    }
    public @NotNull ActionResult onUse(@NotNull BlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull PlayerEntity playerIn, @NotNull Hand handIn, @NotNull BlockHitResult hit) {
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if(!(tile instanceof ChocoboNestBlockEntity nest)) { return ActionResult.FAIL; }

        ItemStack heldItem = playerIn.getStackInHand(handIn);
        if (!ChocoboEggBlock.isNotChocoboEgg(heldItem)) {
            if (!nest.getEggItemStack().isEmpty()) { return ActionResult.FAIL; }
            if (worldIn.isClient()) { return ActionResult.SUCCESS; }
            nest.setEggItemStack(playerIn.getStackInHand(handIn).copy().split(1));
            if (!playerIn.isCreative()) { playerIn.getStackInHand(handIn).decrement(1); }
            return ActionResult.SUCCESS;
        } else { BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof NamedScreenHandlerFactory) {
                playerIn.openHandledScreen((NamedScreenHandlerFactory) blockEntity);
            } }
        return ActionResult.FAIL;
    }
    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChocoboNestBlockEntity(pos, state);
    }
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, STRAW_NEST_BLOCK_ENTITY, world.isClient ? null : ChocoboNestBlockEntity::serverTick);
    }
    @Nullable
    public <T extends BlockEntity> GameEventListener getGameEventListener(World world, T blockEntity) {
        return super.getGameEventListener(world, blockEntity);
    }
}