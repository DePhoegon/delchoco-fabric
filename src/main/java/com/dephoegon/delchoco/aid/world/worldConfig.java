package com.dephoegon.delchoco.aid.world;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.world.dValues.defaultDoubles;
import com.dephoegon.delchoco.aid.world.dValues.defaultFloats;
import com.dephoegon.delchoco.aid.world.dValues.defaultInts;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.aid.world.dValues.defaultBooleans.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.dGYSAHL_GREEN_SPAWN_CHANCE;
import static com.dephoegon.delchoco.aid.world.dValues.defaultInts.*;

@Config(id = DelChoco.DELCHOCO_ID+"-world_config")
public class worldConfig {

    @Configurable.Comment("The Minimum number of Chocobos that spawn in a group")
    @Configurable.Range(min = 1, max = 4)
    public int minChocoboGroupSize = dCHOCOBO_PACK_MIN.getMin();

    @Configurable.Comment("The Maximum number of Chocobos that spawn in a group")
    @Configurable.Range(min = 2, max = 10)
    public int maxChocoboGroupSize = dCHOCOBO_PACK_MAX.getMax();

    @Configurable.Comment("The Weight of Chocobos spawning in the Overworld")
    @Configurable.Range(min = 0, max = 100)
    public int overworldSpawnWeight = dOVERWORLD_SPAWN_WEIGHT.getDefault();

    @Configurable.Comment("The Weight of Chocobos spawning in Mushroom Islands")
    @Configurable.Range(min = 0, max = 4)
    public int mushroomSpawnWeight = dMUSHROOM_SPAWN_WEIGHT.getDefault();

    @Configurable.Comment("The Weight of Chocobos spawning in the Nether")
    @Configurable.Range(min = 75, max = 200)
    public int netherSpawnWeight = dNETHER_SPAWN_WEIGHT.getDefault();

    @Configurable.Comment("The Weight of Chocobos spawning in the End")
    @Configurable.Range(min = 75, max = 200)
    public int endSpawnWeight = dEND_SPAWN_WEIGHT.getDefault();

    @Configurable.Comment("The Size of Gysahl Green Patches")
    @Configurable.Range(min = 0, max = 128)
    public int gysahlGreenPatchSize = dGYSAHL_GREEN_PATCH_SIZE.getDefault();

    @Configurable.Comment("The Chance of Gysahl Green spawning in a patch")
    @Configurable.DecimalRange(min = 0.1, max = 1)
    public double gysahlGreenSpawnChance = dGYSAHL_GREEN_SPAWN_CHANCE.getDefault();

    @Configurable.Comment("Can Chocobo Spawn or be Summoned, master switch")
    public boolean canChocoboSpawn = dCanSpawn;

    @Configurable.Comment("Can Chocobo Spawn in the Overworld")
    public boolean overworldSpawn = dOverworldSpawn;

    @Configurable.Comment("Can Chocobo Spawn in the Nether")
    public boolean netherSpawn = dNetherSpawn;

    @Configurable.Comment("Can Chocobo Spawn in the End")
    public boolean endSpawn = dEndSpawn;


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