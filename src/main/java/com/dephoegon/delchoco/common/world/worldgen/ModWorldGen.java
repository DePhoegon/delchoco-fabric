package com.dephoegon.delchoco.common.world.worldgen;

import com.dephoegon.delchoco.aid.world.WorldConfig;
import com.dephoegon.delchoco.common.init.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

import static com.dephoegon.delchoco.aid.chocoboChecks.vanillaBiomes;

public class ModWorldGen {
    public static void generateGysahl() {
        BiomeModifications.addFeature(BiomeSelectors.all(), GenerationStep.Feature.VEGETAL_DECORATION, ModPlacements.PATCH_GYSAHL_ALL_BIOMES);
        BiomeModifications.addFeature(BiomeSelectors.all(), GenerationStep.Feature.UNDERGROUND_DECORATION, ModPlacements.PATCH_GYSAHL_UNDERGROUND);
    }
    public static void spawnChocobos() {
        if (WorldConfig.CHOCOBO_SPAWN_SWITCH.get()) {
            if (WorldConfig.CHOCOBO_SPAWN_SWITCH_OVERWORLD.get()) {
                BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().and(BiomeSelectors.excludeByKey(BiomeKeys.MUSHROOM_FIELDS)), ModEntities.CHOCOBO_ENTITY.getSpawnGroup(), ModEntities.CHOCOBO_ENTITY, WorldConfig.OVERWORLD_SPAWN_WEIGHT.get(), WorldConfig.MIN_CHOCOBO_PACK_NUM.get(), WorldConfig.MAX_CHOCOBO_PACK_NUM.get());
                BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.MUSHROOM_FIELDS), ModEntities.CHOCOBO_ENTITY.getSpawnGroup(), ModEntities.CHOCOBO_ENTITY, WorldConfig.MUSHROOM_SPAWN_WEIGHT.get(), WorldConfig.MIN_CHOCOBO_PACK_NUM.get(), WorldConfig.MAX_CHOCOBO_PACK_NUM.get());
            }
            if (WorldConfig.CHOCOBO_SPAWN_SWITCH_NETHER.get()) {
                BiomeModifications.addSpawn(BiomeSelectors.foundInTheNether(), ModEntities.CHOCOBO_ENTITY.getSpawnGroup(), ModEntities.CHOCOBO_ENTITY, WorldConfig.NETHER_SPAWN_WEIGHT.get(), WorldConfig.MIN_CHOCOBO_PACK_NUM.get(), WorldConfig.MAX_CHOCOBO_PACK_NUM.get());
            }
            if (WorldConfig.CHOCOBO_SPAWN_SWITCH_THE_END.get()) {
                BiomeModifications.addSpawn(BiomeSelectors.foundInTheEnd(), ModEntities.CHOCOBO_ENTITY.getSpawnGroup(), ModEntities.CHOCOBO_ENTITY, WorldConfig.THE_END_SPAWN_WEIGHT.get(), WorldConfig.MIN_CHOCOBO_PACK_NUM.get(), WorldConfig.MAX_CHOCOBO_PACK_NUM.get());
            }
            // Should be the last one,
            // so that it doesn't override the other ones & spawn in all biomes outside the built-in ones.
            BiomeModifications.addSpawn(BiomeSelectors.excludeByKey(vanillaBiomes()), ModEntities.CHOCOBO_ENTITY.getSpawnGroup(), ModEntities.CHOCOBO_ENTITY, WorldConfig.OVERWORLD_SPAWN_WEIGHT.get(), WorldConfig.MIN_CHOCOBO_PACK_NUM.get(), WorldConfig.MAX_CHOCOBO_PACK_NUM.get());
        }
    }
}