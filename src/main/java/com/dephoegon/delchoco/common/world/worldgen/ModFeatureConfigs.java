package com.dephoegon.delchoco.common.world.worldgen;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.blocks.GysahlGreenBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;

import static com.dephoegon.delchoco.DelChoco.worldConfigHolder;
import static com.dephoegon.delchoco.common.init.ModItems.GYSAHL_GREEN;

public class ModFeatureConfigs {
    public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_GYSAHL_GRASS = ConfiguredFeatures.register("patch_gysahl_grass", Feature.RANDOM_PATCH, ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(GYSAHL_GREEN.getDefaultState().with(GysahlGreenBlock.AGE, GysahlGreenBlock.MAX_AGE))), List.of(Blocks.GRASS_BLOCK, Blocks.NETHERRACK, Blocks.CLAY, Blocks.MOSS_BLOCK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.TUFF, Blocks.DRIPSTONE_BLOCK, Blocks.END_STONE), worldConfigHolder.gysahlGreenPatchSize));

    public static void register() {
        DelChoco.LOGGER.info("Registering ModFeatureConfigs");
    }
}
