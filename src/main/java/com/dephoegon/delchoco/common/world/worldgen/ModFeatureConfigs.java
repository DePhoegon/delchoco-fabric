package com.dephoegon.delchoco.common.world.worldgen;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.world.WorldConfig;
import com.dephoegon.delchoco.common.blocks.GysahlGreenBlock;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.dephoegon.delchoco.common.init.ModItems.GYSAHL_GREEN;

public class ModFeatureConfigs {
    public static final RegistryKey<ConfiguredFeature<?,?>> PATCH_GYSAHL_GRASS = registerKey("patch_gysahl_grass");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        register(context, PATCH_GYSAHL_GRASS, Feature.RANDOM_PATCH, ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(GYSAHL_GREEN.getDefaultState().with(GysahlGreenBlock.AGE, GysahlGreenBlock.MAX_AGE))), List.of(Blocks.GRASS_BLOCK, Blocks.NETHERRACK, Blocks.CLAY, Blocks.MOSS_BLOCK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.TUFF, Blocks.DRIPSTONE_BLOCK, Blocks.END_STONE), WorldConfig.GYSAHL_GREEN_PATCH_SIZE.get()));
    }
    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(@NotNull Registerable<ConfiguredFeature<?, ?>> context, RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(DelChoco.DELCHOCO_ID, name));
    }
}
