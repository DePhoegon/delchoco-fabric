package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class chocoboChecks {
    private static @NotNull ArrayList<ChocoboColor> wbChocobos() {
        ArrayList<ChocoboColor> out = new ArrayList<>();
        out.add(ChocoboColor.BLUE);
        out.add(ChocoboColor.GOLD);
        return out;
    }
    private static @NotNull ArrayList<ChocoboColor> wiChocobos() {
        ArrayList<ChocoboColor> out = new ArrayList<>();
        out.add(ChocoboColor.BLACK);
        out.add(ChocoboColor.GOLD);
        return out;
    }
    private static @NotNull ArrayList<ChocoboColor> piChocobos() {
        ArrayList<ChocoboColor> out = new ArrayList<>();
        out.add(ChocoboColor.GREEN);
        out.add(ChocoboColor.GOLD);
        return out;
    }
    private static @NotNull ArrayList<RegistryKey<Biome>> whiteChocobo() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.BIRCH_FOREST);
        out.add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST);
        return out;
    }
    private static @NotNull ArrayList<RegistryKey<Biome>> blueChocobo() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.LUKEWARM_OCEAN);
        out.add(BiomeKeys.DEEP_LUKEWARM_OCEAN);
        out.add(BiomeKeys.WARM_OCEAN);
        out.add(BiomeKeys.RIVER);
        return out;
    }
    private static @NotNull ArrayList<RegistryKey<Biome>> greenChocobo() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.JUNGLE);
        out.add(BiomeKeys.BAMBOO_JUNGLE);
        out.add(BiomeKeys.SWAMP);
        out.add(BiomeKeys.LUSH_CAVES);
        out.add(BiomeKeys.DRIPSTONE_CAVES);
        return out;
    }
    private static @NotNull ArrayList<RegistryKey<Biome>> IS_HOT_OVERWORLD() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.DESERT);
        out.add(BiomeKeys.JUNGLE);
        out.add(BiomeKeys.SPARSE_JUNGLE);
        out.add(BiomeKeys.SAVANNA);
        out.add(BiomeKeys.SAVANNA_PLATEAU);
        out.add(BiomeKeys.STONY_PEAKS);
        out.add(BiomeKeys.WINDSWEPT_SAVANNA);
        out.add(BiomeKeys.ERODED_BADLANDS);
        out.add(BiomeKeys.BAMBOO_JUNGLE);
        return out;
    }
    private static @NotNull ArrayList<RegistryKey<Biome>> IS_SAVANNA() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.SAVANNA);
        out.add(BiomeKeys.SAVANNA_PLATEAU);
        out.add(BiomeKeys.WINDSWEPT_SAVANNA);
        return out;
    }
    private static @NotNull ArrayList<RegistryKey<Biome>> IS_SNOWY() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.FROZEN_OCEAN);
        out.add(BiomeKeys.FROZEN_RIVER);
        out.add(BiomeKeys.SNOWY_PLAINS);
        out.add(BiomeKeys.SNOWY_BEACH);
        out.add(BiomeKeys.SNOWY_TAIGA);
        out.add(BiomeKeys.GROVE);
        out.add(BiomeKeys.SNOWY_SLOPES);
        out.add(BiomeKeys.JAGGED_PEAKS);
        out.add(BiomeKeys.FROZEN_PEAKS);
        out.add(BiomeKeys.ICE_SPIKES);
        return out;
    }
    public static @NotNull ArrayList<RegistryKey<Biome>> IS_MUSHROOM() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.MUSHROOM_FIELDS);
        return out;
    }
    private static @NotNull ArrayList<RegistryKey<Biome>> IS_OCEAN() {
        ArrayList<RegistryKey<Biome>> out = new ArrayList<>();
        out.add(BiomeKeys.WARM_OCEAN);
        out.add(BiomeKeys.LUKEWARM_OCEAN);
        out.add(BiomeKeys.DEEP_LUKEWARM_OCEAN);
        out.add(BiomeKeys.OCEAN);
        out.add(BiomeKeys.DEEP_OCEAN);
        out.add(BiomeKeys.COLD_OCEAN);
        out.add(BiomeKeys.DEEP_COLD_OCEAN);
        out.add(BiomeKeys.FROZEN_OCEAN);
        out.add(BiomeKeys.DEEP_FROZEN_OCEAN);
        return out;
    }
    public static boolean isOverworld(ServerWorldAccess world) {
        if (world == null) { return false; }
        return world.toServerWorld().getRegistryKey().equals(World.OVERWORLD);
    }
    public static boolean isNether(ServerWorldAccess world) {
        if (world == null) { return false; }
        return world.toServerWorld().getRegistryKey().equals(World.NETHER);
    }
    public static boolean isEnd(ServerWorldAccess world) {
        if (world == null) { return false; }
        return world.toServerWorld().getRegistryKey().equals(World.END);
    }
    public static boolean isOceanBlocked(RegistryKey<Biome> biomeRegistryKey, boolean allowBlue) {
        ArrayList<RegistryKey<Biome>> out = IS_OCEAN();
        boolean blocked = out.contains(biomeRegistryKey);
        if (blocked && allowBlue) { return blueChocobo().contains(biomeRegistryKey); }
        return blocked;
    }
    public static boolean isSnowy(RegistryKey<Biome> biomeRegistryKey) { return IS_SNOWY().contains(biomeRegistryKey); }
    public static boolean isSavanna(RegistryKey<Biome> biomeRegistryKey) { return IS_SAVANNA().contains(biomeRegistryKey); }
    public static boolean isHotOverWorld(RegistryKey<Biome> biomeRegistryKey) { return IS_HOT_OVERWORLD().contains(biomeRegistryKey); }
    public static boolean isGreenChocoboBiomes(RegistryKey<Biome> biomeRegistryKey) { return greenChocobo().contains(biomeRegistryKey); }
    public static boolean isBlueChocoboBiomes(RegistryKey<Biome> biomeRegistryKey) { return blueChocobo().contains(biomeRegistryKey); }
    public static boolean isWhiteChocoboBiomes(RegistryKey<Biome> biomeRegistryKey) { return whiteChocobo().contains(biomeRegistryKey); }
    public static boolean isPoisonImmuneChocobo(ChocoboColor chocoboColor) { return piChocobos().contains(chocoboColor); }
    public static boolean isWitherImmuneChocobo(ChocoboColor chocoboColor) { return wiChocobos().contains(chocoboColor); }
    public static boolean isWaterBreathingChocobo(ChocoboColor chocoboColor) { return wbChocobos().contains(chocoboColor); }
}