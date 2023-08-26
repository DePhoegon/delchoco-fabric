package com.dephoegon.delchoco.common.world.worldgen;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.blocks.GysahlGreenBlock;
import com.dephoegon.delchoco.common.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;

public class ModFeatureConfigs {
    private static final BlockState GYSAHL_GREEN = ModItems.GYSAHL_GREEN.getDefaultState().with(GysahlGreenBlock.AGE, GysahlGreenBlock.MAX_AGE);
    public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_GYSAHL_GRASS = ConfiguredFeatures.register("patch_gysahl_grass", Feature.RANDOM_PATCH, ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of((BlockState)GYSAHL_GREEN.getBlock().getDefaultState().with(GysahlGreenBlock.AGE, 4))), List.of(Blocks.GRASS_BLOCK, Blocks.NETHERRACK, Blocks.CLAY, Blocks.MOSS_BLOCK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.TUFF, Blocks.DRIPSTONE_BLOCK, Blocks.END_STONE)));

    public static void register() {
        DelChoco.LOGGER.info("Registering ModFeatureConfigs");
    }
}
