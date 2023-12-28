package com.dephoegon.delchoco.aid.world;

import com.dephoegon.delchoco.aid.world.dValues.defaultDoubles;
import com.dephoegon.delchoco.aid.world.dValues.defaultFloats;
import com.dephoegon.delchoco.aid.world.dValues.defaultInts;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

public class WorldConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> MIN_CHOCOBO_PACK_NUM;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_CHOCOBO_PACK_NUM;
    public static final ForgeConfigSpec.ConfigValue<Integer> OVERWORLD_SPAWN_WEIGHT;
    public static final ForgeConfigSpec.ConfigValue<Integer> MUSHROOM_SPAWN_WEIGHT;
    public static final ForgeConfigSpec.ConfigValue<Integer> NETHER_SPAWN_WEIGHT;
    public static final ForgeConfigSpec.ConfigValue<Integer> THE_END_SPAWN_WEIGHT;
    public static final ForgeConfigSpec.ConfigValue<Integer> GYSAHL_GREEN_PATCH_SIZE;
    public static final ForgeConfigSpec.ConfigValue<Double> GYSAHL_GREEN_SPAWN_WEIGHT;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CHOCOBO_SPAWN_SWITCH;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CHOCOBO_SPAWN_SWITCH_OVERWORLD;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CHOCOBO_SPAWN_SWITCH_NETHER;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CHOCOBO_SPAWN_SWITCH_THE_END;
    static {
        BUILDER.push("DelChoco World Configs");

        MIN_CHOCOBO_PACK_NUM = BUILDER.comment("The Minimum number of Chocobos that spawn in a group").defineInRange("Chocobo Min Pack", 1, 1, 4);
        MAX_CHOCOBO_PACK_NUM = BUILDER.comment("The Maximum number of Chocobos that spawn in a group").defineInRange("Chocobo Max Pack", 4, 2, 10);
        OVERWORLD_SPAWN_WEIGHT = BUILDER.comment("The Weight of Chocobos spawning in the Overworld").defineInRange("Overworld Chocobo Spawn Weight", 8, 0, 100);
        MUSHROOM_SPAWN_WEIGHT = BUILDER.comment("The Weight of Chocobos spawning in Mushroom Islands").defineInRange("Mushroom Biomes Chocobo Spawn Weight", 2, 0, 4);
        NETHER_SPAWN_WEIGHT = BUILDER.comment("The Weight of Chocobos spawning in the Nether").defineInRange("Nether Chocobo Spawn Weight", 100, 75, 200);
        THE_END_SPAWN_WEIGHT = BUILDER.comment("The Weight of Chocobos spawning in the End").defineInRange("The End Chocobo Spawn Weight", 100, 75, 200);
        GYSAHL_GREEN_PATCH_SIZE = BUILDER.comment("The Size of Gysahl Green Patches").defineInRange("Gysahl Green Patch Size", 64, 0, 128);
        GYSAHL_GREEN_SPAWN_WEIGHT = BUILDER.comment("The Chance of Gysahl Green spawning in a patch").defineInRange("Gysahl Green Spawn Weight", 0.1D, 0.1D, 1D);
        CHOCOBO_SPAWN_SWITCH = BUILDER.comment("Can Chocobo Spawn or be Summoned, master switch").define("Chocobo Spawn Switch", true);
        CHOCOBO_SPAWN_SWITCH_OVERWORLD = BUILDER.comment("Can Chocobo Spawn in the Overworld").define("Overworld Chocobo Spawn", true);
        CHOCOBO_SPAWN_SWITCH_NETHER = BUILDER.comment("Can Chocobo Spawn in the Nether").define("Nether Chocobo Spawn", true);
        CHOCOBO_SPAWN_SWITCH_THE_END = BUILDER.comment("Can Chocobo Spawn in the The End").define("The End Chocobo Spawn", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }


    public static int ChocoConfigGet(Integer value, int defaultValue) { return value != null ? value : defaultValue; }
    public static double ChocoConfigGet(Double value, double defaultValue) { return value != null ? value : defaultValue; }
    public static boolean ChocoConfigGet(Boolean value, boolean defaultValue) { return value != null ? value : defaultValue; }
    public static float ChocoConfigGet(Float value, float defaultValue) { return value != null ? value : defaultValue; }
    public static float FloatChocoConfigGet(Integer value, int defaultValue) { return value != null ? value : defaultValue; }
    public static float FloatChocoConfigGet(Double value, double defaultValue) { return (float) (value != null ? value : defaultValue); }
    public static int getValueInBounds(@NotNull defaultInts Holder, Integer inValue) {
        int value = ChocoConfigGet(inValue, Holder.getDefault());
        return  Math.max(Holder.getMin(), Math.min(Holder.getMax(), value));
    }
    public static double getValueInBounds(@NotNull defaultDoubles Holder, Double inValue) {
        double value = ChocoConfigGet(inValue, Holder.getDefault());
        return  Math.max(Holder.getMin(), Math.min(Holder.getMax(), value));
    }
    public static float getValueInBounds(@NotNull defaultFloats Holder, Float inValue) {
        float value = ChocoConfigGet(inValue, Holder.getDefault());
        return  Math.max(Holder.getMin(), Math.min(Holder.getMax(), value));
    }
}