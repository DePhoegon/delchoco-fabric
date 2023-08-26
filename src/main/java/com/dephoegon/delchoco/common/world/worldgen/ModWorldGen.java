package com.dephoegon.delchoco.common.world.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ModWorldGen {
    public static void generateGysahl() {
        BiomeModifications.addFeature(BiomeSelectors.all(), GenerationStep.Feature.VEGETAL_DECORATION, ModPlacements.PATCH_GYSAHL_ALL_BIOMES.getKey().get());
        BiomeModifications.addFeature(BiomeSelectors.categories(Biome.Category.UNDERGROUND), GenerationStep.Feature.UNDERGROUND_DECORATION, ModPlacements.PATCH_GYSAHL_UNDERGROUND.getKey().get());
    }
}