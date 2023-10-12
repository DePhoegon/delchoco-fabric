package com.dephoegon.delchoco.common.world.worldgen;

import com.dephoegon.delchoco.DelChoco;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.dephoegon.delchoco.DelChoco.worldConfigHolder;

public class ModPlacements {
    public static final RegistryKey<PlacedFeature> PATCH_GYSAHL_ALL_BIOMES = registerKey("patch_gysahl_all_biomes");
    public static final RegistryKey<PlacedFeature> PATCH_GYSAHL_UNDERGROUND = registerKey("patch_gysahl_underground");
    static final RarityFilterPlacementModifier normRarity = RarityFilterPlacementModifier.of(getSpawnChance(10));
    static final RarityFilterPlacementModifier underGroundRarity = RarityFilterPlacementModifier.of(getSpawnChance(30));

    private static int getSpawnChance(int MultipleFactor) { return (int) (worldConfigHolder.gysahlGreenSpawnChance * MultipleFactor); }

    public static void bootstrap(@NotNull Registerable<PlacedFeature> context) {
        var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
        register(context, PATCH_GYSAHL_ALL_BIOMES, configuredFeatureRegistryEntryLookup.getOrThrow(ModFeatureConfigs.PATCH_GYSAHL_GRASS), normRarity, CountPlacementModifier.of(UniformIntProvider.create(0, 5)), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_TOP_RANGE);
        register(context, PATCH_GYSAHL_UNDERGROUND, configuredFeatureRegistryEntryLookup.getOrThrow(ModFeatureConfigs.PATCH_GYSAHL_GRASS), underGroundRarity, CountPlacementModifier.of(UniformIntProvider.create(0, 5)), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_TOP_RANGE);
    }
    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(DelChoco.DELCHOCO_ID, name));
    }
    private static void register(@NotNull Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                                                                   RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                                                                   PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }
}