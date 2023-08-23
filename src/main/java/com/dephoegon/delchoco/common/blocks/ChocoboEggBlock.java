package com.dephoegon.delchoco.common.blocks;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.blockentities.ChocoboEggBlockEntity;
import com.dephoegon.delchoco.common.entities.breeding.ChocoboBreedInfo;
import com.dephoegon.delchoco.common.entities.breeding.ChocoboStatSnapshot;
import com.dephoegon.delchoco.common.init.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

import static com.dephoegon.delchoco.common.entities.breeding.ChocoboBreedInfo.getFromNbtOrDefault;

public class ChocoboEggBlock extends BlockWithEntity {
    public final static String NBTKEY_HATCHINGSTATE_TIME = "Time";
    public final static String NBTKEY_HATCHINGSTATE = "HatchingState";
    public final static String NBTKEY_BREEDINFO = "BreedInfo";

    private static final VoxelShape SHAPE = Stream.of(
            Block.createCuboidShape(5, 0, 5, 11, 1, 11),
            Block.createCuboidShape(4, 1, 4, 12, 6, 12),
            Block.createCuboidShape(5, 6, 5, 11, 8, 11),
            Block.createCuboidShape(6, 8, 6, 10, 10, 10)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    public ChocoboEggBlock(Settings properties) { super(properties); }
    public @NotNull BlockRenderType getRenderType(@NotNull BlockState state) { return BlockRenderType.MODEL; }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) { return SHAPE; }
    public static boolean isNotChocoboEgg(@NotNull ItemStack itemStack) { return !(itemStack.getItem() instanceof BlockItem) || !(((BlockItem) itemStack.getItem()).getBlock() instanceof ChocoboEggBlock); }
    public BlockEntity createBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) { return new ChocoboEggBlockEntity(pos, state); }
    public void onPlaced(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        if (!worldIn.isClient()) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (!(tile instanceof ChocoboEggBlockEntity)) { return; }
            ChocoboBreedInfo breedInfo = getFromNbtOrDefault(stack.getSubNbt(NBTKEY_BREEDINFO));
            ((ChocoboEggBlockEntity) tile).setBreedInfo(breedInfo);
        }
        super.onPlaced(worldIn, pos, state, placer, stack);
    }
    public void afterBreak(@NotNull World worldIn, @NotNull PlayerEntity player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity te, @NotNull ItemStack stack) {
        if (worldIn.isClient()) { return; }
        if (te instanceof ChocoboEggBlockEntity) {
            player.incrementStat(Stats.MINED.getOrCreateStat(this));
            player.addExhaustion(0.005F);
            ItemStack itemStack = new ItemStack(ModItems.CHOCOBO_EGG);
            ChocoboBreedInfo breedInfo = ((ChocoboEggBlockEntity) te).getBreedInfo();
            if (breedInfo == null) { breedInfo = getFromNbtOrDefault(null); }
            itemStack.setSubNbt(NBTKEY_BREEDINFO, breedInfo.serialize());
            dropStack(worldIn, pos, itemStack);
            return;
        }
        super.afterBreak(worldIn, player, pos, state, te, stack);
    }
    public void appendTooltip(ItemStack stack, BlockView worldIn, List<Text> tooltip, TooltipContext flagIn) {
        super.appendTooltip(stack, worldIn, tooltip, flagIn);
        NbtCompound nbtBreedInfo = stack.getSubNbt(NBTKEY_BREEDINFO);
        if (nbtBreedInfo != null) {
            final ChocoboBreedInfo info = new ChocoboBreedInfo(nbtBreedInfo);
            final ChocoboStatSnapshot mother = info.getMother();
            final ChocoboStatSnapshot father = info.getFather();
            tooltip.add(new TranslatableText("item." + DelChoco.DELCHOCO_ID + ".chocobo_egg.tooltip.mother_info", (int) mother.health, (int) (mother.speed * 100), (int) mother.stamina, mother.color.getEggText()));
            tooltip.add(new TranslatableText("item." + DelChoco.DELCHOCO_ID + ".chocobo_egg.tooltip.father_info", (int) father.health, (int) (father.speed * 100), (int) father.stamina, father.color.getEggText()));
        } else { tooltip.add(new TranslatableText("item." + DelChoco.DELCHOCO_ID + ".chocobo_egg.tooltip.invalid_egg")); }
    }
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return super.getTicker(world, state, type);
    }
    @Nullable
    public <T extends BlockEntity> GameEventListener getGameEventListener(World world, T blockEntity) {
        return super.getGameEventListener(world, blockEntity);
    }
}