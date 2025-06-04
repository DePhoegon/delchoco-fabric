package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.common.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GysahlGreenBiomes {
    private static @NotNull ArrayList<RegistryKey<Biome>> LovelyGreenBiomes() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.GROVE);
        out.add(BiomeKeys.FLOWER_FOREST);
        out.add(BiomeKeys.SUNFLOWER_PLAINS);
        out.add(BiomeKeys.MEADOW);
        out.add(BiomeKeys.DRIPSTONE_CAVES);
        out.add(BiomeKeys.LUSH_CAVES);
        out.add(BiomeKeys.LUKEWARM_OCEAN);
        out.add(BiomeKeys.DEEP_LUKEWARM_OCEAN);
        out.add(BiomeKeys.WARM_OCEAN);
        return out;
    }
    private static @NotNull ArrayList<RegistryKey<Biome>> GoldenGysahlBiomes() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.THE_VOID);
        out.add(BiomeKeys.THE_END);
        out.add(BiomeKeys.END_BARRENS);
        out.add(BiomeKeys.END_HIGHLANDS);
        out.add(BiomeKeys.END_MIDLANDS);
        return out;
    }
    private static @NotNull ArrayList<RegistryKey<Biome>> PinkGreenBiomes() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.MUSHROOM_FIELDS);
        out.add(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA);
        out.add(BiomeKeys.OLD_GROWTH_PINE_TAIGA);
        out.add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST);
        out.add(BiomeKeys.WINDSWEPT_FOREST);
        out.add(BiomeKeys.WINDSWEPT_SAVANNA);
        out.add(BiomeKeys.WOODED_BADLANDS);
        out.add(BiomeKeys.CHERRY_GROVE);
        return out;
    }
    private static @NotNull ArrayList<RegistryKey<Biome>> DeadGreenBiomes() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.DESERT);
        out.add(BiomeKeys.STONY_PEAKS);
        out.add(BiomeKeys.ERODED_BADLANDS);
        out.add(BiomeKeys.NETHER_WASTES);
        out.add(BiomeKeys.SOUL_SAND_VALLEY);
        out.add(BiomeKeys.WARPED_FOREST);
        out.add(BiomeKeys.CRIMSON_FOREST);
        out.add(BiomeKeys.BASALT_DELTAS);
        return out;
    }
    private static @NotNull ArrayList<RegistryKey<Biome>> SpikeGreenBiomes() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.JUNGLE);
        out.add(BiomeKeys.SPARSE_JUNGLE);
        out.add(BiomeKeys.SAVANNA);
        out.add(BiomeKeys.SAVANNA_PLATEAU);
        out.add(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS);
        out.add(BiomeKeys.WINDSWEPT_HILLS);
        out.add(BiomeKeys.BAMBOO_JUNGLE);
        out.add(BiomeKeys.JAGGED_PEAKS);
        out.add(BiomeKeys.STONY_SHORE);
        out.add(BiomeKeys.SNOWY_TAIGA);
        out.add(BiomeKeys.SNOWY_PLAINS);
        out.add(BiomeKeys.SNOWY_SLOPES);
        return out;
    }
    public static int getBiomeID(RegistryKey<Biome> biomeRegistryKey) {
        int privBiome = 1;
        if (GoldenGysahlBiomes().contains(biomeRegistryKey)) { privBiome = 2; }
        if (PinkGreenBiomes().contains(biomeRegistryKey)) { privBiome = 3; }
        if (DeadGreenBiomes().contains(biomeRegistryKey)) { privBiome = 4; }
        if (SpikeGreenBiomes().contains(biomeRegistryKey)) { privBiome = 5; }
        if (LovelyGreenBiomes().contains(biomeRegistryKey)) { privBiome = 6; }
        return privBiome;
    }
    // getGreenByproduct is left unused, but is here for future use. Intended for code based Item Dropping
    public static ItemStack getGreenByproduct(int biomeID) {
        return switch (biomeID) {
            case 2 -> ModItems.GOLDEN_GYSAHL_GREEN.getDefaultStack();
            case 3 -> ModItems.PINK_GYSAHL_GREEN.getDefaultStack();
            case 4 -> ModItems.DEAD_PEPPER.getDefaultStack();
            case 5 -> ModItems.SPIKE_FRUIT.getDefaultStack();
            case 6 -> ModItems.LOVELY_GYSAHL_GREEN.getDefaultStack();
            default -> ItemStack.EMPTY;
        };
    }
    public static int getGreenCount() { return 6; }
}