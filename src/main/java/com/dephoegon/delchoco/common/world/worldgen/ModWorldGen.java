package com.dephoegon.delchoco.common.world.worldgen;

import com.dephoegon.delchoco.common.init.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

import static com.dephoegon.delchoco.DelChoco.worldConfigHolder;
import static com.dephoegon.delchoco.aid.chocoboChecks.vanillaBiomes;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ModWorldGen {
    public static void generateGysahl() {
        BiomeModifications.addFeature(BiomeSelectors.all(), GenerationStep.Feature.VEGETAL_DECORATION, ModPlacements.PATCH_GYSAHL_ALL_BIOMES.getKey().get());
        BiomeModifications.addFeature(BiomeSelectors.all(), GenerationStep.Feature.UNDERGROUND_DECORATION, ModPlacements.PATCH_GYSAHL_UNDERGROUND.getKey().get());
    }
    public static void spawnChocobos() {
        if (worldConfigHolder.canChocoboSpawn) {
            if (worldConfigHolder.overworldSpawn) {
                BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().and(BiomeSelectors.excludeByKey(BiomeKeys.MUSHROOM_FIELDS)), ModEntities.CHOCOBO_ENTITY.getSpawnGroup(), ModEntities.CHOCOBO_ENTITY, worldConfigHolder.overworldSpawnWeight, worldConfigHolder.minChocoboGroupSize, worldConfigHolder.maxChocoboGroupSize);
                BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.MUSHROOM_FIELDS), ModEntities.CHOCOBO_ENTITY.getSpawnGroup(), ModEntities.CHOCOBO_ENTITY, worldConfigHolder.mushroomSpawnWeight, worldConfigHolder.minChocoboGroupSize, worldConfigHolder.maxChocoboGroupSize);
            }
            if (worldConfigHolder.netherSpawn) {
                BiomeModifications.addSpawn(BiomeSelectors.foundInTheNether(), ModEntities.CHOCOBO_ENTITY.getSpawnGroup(), ModEntities.CHOCOBO_ENTITY, worldConfigHolder.netherSpawnWeight, worldConfigHolder.minChocoboGroupSize, worldConfigHolder.maxChocoboGroupSize);
            }
            if (worldConfigHolder.endSpawn) {
                BiomeModifications.addSpawn(BiomeSelectors.foundInTheEnd(), ModEntities.CHOCOBO_ENTITY.getSpawnGroup(), ModEntities.CHOCOBO_ENTITY, worldConfigHolder.endSpawnWeight, worldConfigHolder.minChocoboGroupSize, worldConfigHolder.maxChocoboGroupSize);
            }
            // Should be the last one,
            // so that it doesn't override the other ones & spawn in all biomes outside the built-in ones.
            BiomeModifications.addSpawn(BiomeSelectors.excludeByKey(vanillaBiomes()), ModEntities.CHOCOBO_ENTITY.getSpawnGroup(), ModEntities.CHOCOBO_ENTITY, worldConfigHolder.overworldSpawnWeight, worldConfigHolder.minChocoboGroupSize, worldConfigHolder.maxChocoboGroupSize);
        }
    }
}