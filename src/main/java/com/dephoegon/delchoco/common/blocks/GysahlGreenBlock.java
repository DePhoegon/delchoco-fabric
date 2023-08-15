package com.dephoegon.delchoco.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.dephoegon.delchoco.common.init.ModItems.GYSAHL_GREEN_SEEDS;

public class GysahlGreenBlock extends CropBlock {
    public static final int MAX_AGE = 4;
    public static final IntProperty AGE = IntProperty.of("age", 0, MAX_AGE);
    public GysahlGreenBlock(Settings settings) {
        super(settings);
        this.setDefaultState(((BlockState)this.stateManager.getDefaultState()).with(this.getAgeProperty(), 0));
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
}