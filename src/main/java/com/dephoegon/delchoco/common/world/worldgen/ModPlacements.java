package com.dephoegon.delchoco.common.world.worldgen;

import com.dephoegon.delchoco.aid.world.StaticGlobalVariables;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.ChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.dGYSAHL_GREEN_SPAWN_CHANCE;

public class ModPlacements {
    static final RarityFilterPlacementModifier normRarity = RarityFilterPlacementModifier.of(getSpawnChance(10));
    static final RarityFilterPlacementModifier underGroundRarity = RarityFilterPlacementModifier.of(getSpawnChance(30));
    public static final RegistryEntry<PlacedFeature> PATCH_GYSAHL_ALL_BIOMES = PlacedFeatures.register("patch_gysahl_all_biomes", ModFeatureConfigs.PATCH_GYSAHL_GRASS, normRarity, CountPlacementModifier.of(UniformIntProvider.create(0, 5)), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_TOP_RANGE);
    public static final RegistryEntry<PlacedFeature> PATCH_GYSAHL_UNDERGROUND = PlacedFeatures.register("patch_gysahl_underground", ModFeatureConfigs.PATCH_GYSAHL_GRASS, underGroundRarity, CountPlacementModifier.of(UniformIntProvider.create(0, 5)), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_TOP_RANGE);

    private static int getSpawnChance(int MultipleFactor) { return (int) (ChocoConfigGet(StaticGlobalVariables.getGysahlGreenSpawnChance(), dGYSAHL_GREEN_SPAWN_CHANCE.getDefault()) * MultipleFactor); }
}