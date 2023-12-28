package com.dephoegon.delchoco.aid.world;

import net.minecraftforge.common.ForgeConfigSpec;

public class ChocoboConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_STAMINA;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_HEALTH;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_ARMOR;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_WEAPON_MOD;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_HEALING;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_HEALTH;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_STAMINA;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_ATTACK;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_ARMOR;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.ConfigValue<Double> STAMINA_REGEN;
    public static final ForgeConfigSpec.ConfigValue<Double> TAME_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Double> STAMINA_SPRINT_USE;
    public static final ForgeConfigSpec.ConfigValue<Double> STAMINA_GLIDE_USE;
    public static final ForgeConfigSpec.ConfigValue<Double> STAMINA_JUMP_USE;
    public static final ForgeConfigSpec.ConfigValue<Integer> FRUIT_COOL_DOWN;
    public static final ForgeConfigSpec.ConfigValue<Double> POSS_STAT_GAIN;
    public static final ForgeConfigSpec.ConfigValue<Double> POSS_STAT_LOSS;
    public static final ForgeConfigSpec.ConfigValue<Double> POSS_HEALTH_GAIN;
    public static final ForgeConfigSpec.ConfigValue<Double> POSS_HEALTH_LOSS;
    public static final ForgeConfigSpec.ConfigValue<Double> POSS_SPEED_GAIN;
    public static final ForgeConfigSpec.ConfigValue<Double> POSS_SPEED_LOSS;
    public static final ForgeConfigSpec.ConfigValue<Double> POSS_STAMINA_GAIN;
    public static final ForgeConfigSpec.ConfigValue<Double> POSS_STAMINA_LOSS;
    public static final ForgeConfigSpec.ConfigValue<Double> CHOCOBO_ARMOR_ALPHA;
    public static final ForgeConfigSpec.ConfigValue<Double> CHOCOBO_WEAPON_ALPHA;
    public static final ForgeConfigSpec.ConfigValue<Double> CHOCOBO_COLLAR_ALPHA;
    public static final ForgeConfigSpec.ConfigValue<Double> CHOCOBO_SADDLE_ALPHA;
    public static final ForgeConfigSpec.ConfigValue<Boolean> EXTRA_CHOCOBO_EFFECT;
    public static final ForgeConfigSpec.ConfigValue<Boolean> EXTRA_CHOCOBO_RESOURCES_HIT;
    public static final ForgeConfigSpec.ConfigValue<Boolean> EXTRA_CHOCOBO_RESOURCES_KILL;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SHIFT_HIT_BYPASS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OWN_CHOCOBO_HITTABLE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TAMED_CHOCOBO_HITTABLE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OWNER_ONLY_ACCES;
    static {
        BUILDER.push("Chocobo Configurations");
        BUILDER.push("Default Stats");
        DEFAULT_STAMINA = BUILDER.comment("The Amount of Stamina a Chocobo has by default. min 5, max 60, default 20").defineInRange("Default Stamina", 10, 5, 60);
        DEFAULT_SPEED = BUILDER.comment("The Amount of Speed a Chocobo has by default. min 10, max 80, default 20").defineInRange("Default Speed", 20, 10, 80);
        DEFAULT_HEALTH = BUILDER.comment("The Amount of Health a Chocobo has by default. min 6, max 1000, default 20"). defineInRange("Default Health", 20, 6, 1000);
        DEFAULT_ARMOR = BUILDER.comment("The Amount of Armor a Chocobo has by default. min 0, max 20, default 4").defineInRange("Default Armor", 4, 0 , 20);
        DEFAULT_ARMOR_TOUGHNESS = BUILDER.comment("The Amount of Toughness a Chocobo has by default. min 0, max 10, default 1").defineInRange("Default ArmorToughness", 1, 0, 10);
        DEFAULT_ATTACK_DAMAGE = BUILDER.comment("The Amount of Attack Damage a Chocobo has by default. min 1, max 10, default 2").defineInRange("Default Attack Damage", 2, 1, 10);
        DEFAULT_WEAPON_MOD = BUILDER.comment("The Amount of Attack Speed as a Weapon Modifier a Chocobo has by default. min 1, max 3, default 1").defineInRange("Default Attack Mod", 1, 1, 3);
        DEFAULT_HEALING = BUILDER.comment("The Amount of Health a Chocobo Heals when fed a Gysahl Green in Half Hearts. min 2, max 10, default 5").defineInRange("Default Healing", 5, 2, 10);
        BUILDER.pop();
        BUILDER.push("Max Stats");
        MAX_HEALTH = BUILDER.comment("The Maximum Health a Chocobo can have. min 25, max 3000, default 200").defineInRange("Max Health", 200, 25, 3000);
        MAX_SPEED = BUILDER.comment("The Maximum Speed a Chocobo can have. min 30, max 160, default 60").defineInRange("Max Speed", 60, 20, 160);
        MAX_STAMINA = BUILDER.comment("The Maximum Stamina a Chocobo can have. min 10, max 1024, default 200").defineInRange("Max Stamina", 200, 10, 1024);
        MAX_ATTACK = BUILDER.comment("The Maximum Strength a Chocobo can have. min 8, max 200, default 80").defineInRange("Max Attack", 80, 8, 200);
        MAX_ARMOR = BUILDER.comment("The Maximum Armor a Chocobo can have. min 20, max 500, default 200").defineInRange("Max Armor", 200, 20, 500);
        MAX_ARMOR_TOUGHNESS = BUILDER.comment("The Maximum Armor Toughness a Chocobo can have. min 8, max 200, default 40").defineInRange("Max ArmorToughness", 40, 8, 200);
        BUILDER.pop();
        BUILDER.push("Chocobo Stamina");
        STAMINA_REGEN = BUILDER.comment("The Amount of Stamina a Chocobo Regenerates per Tick. min 0.01, max 1, default 0.025 - Double").defineInRange("Stamina Regen", 0.025D, 0.01D, 1D);
        STAMINA_SPRINT_USE = BUILDER.comment("The Amount of Stamina a Chocobo uses per Tick while Sprinting. min 0, max 1, default 0.06 - Double").defineInRange("Stamina Sprint Cost", 0.06D, 0D, 1D);
        STAMINA_GLIDE_USE = BUILDER.comment("The Amount of Stamina a Chocobo uses per Tick while Gliding. min 0, max 1, default 0.005 - Double").defineInRange("Stamina Glide Cost", 0.005D, 0D, 1D);
        STAMINA_JUMP_USE = BUILDER.comment("The Amount of Stamina a Chocobo uses per Jump. min 0, max 1, default 0 - Double").defineInRange("Stamina Jump Cost", 0D, 0D, 1D);
        BUILDER.pop();
        BUILDER.push("Stat Changes");
        FRUIT_COOL_DOWN = BUILDER.comment("The Time in Ticks, the cool down time a chocobo has between eating non-Gysahl Green. min 40, max 600, default 60").defineInRange("Fruit Cool Down", 60, 10, 600);
        POSS_STAT_GAIN = BUILDER.comment("The possible amount of percent Stats passed on the the chicbo gains in the breeding process. min 0, max 1, default 0.1 - Double").defineInRange("Positive Stat Gain", 0.1D, 0D, 1D);
        POSS_STAT_LOSS = BUILDER.comment("The possible amount of percent Stats passed on the the chicbo loses in the breeding process. min 0, max 1, default 1 - Double").defineInRange("Negative Stat Gain", 1D, 0D, 1D);
        POSS_HEALTH_GAIN = BUILDER.comment("The possible amount of percent Health passed on the the chicbo gains in the breeding process. min 0, max 1, default 0.1 - Double").defineInRange("Positive Health Gain", 0.1D, 0D, 1D);
        POSS_HEALTH_LOSS = BUILDER.comment("The possible amount of percent Health passed on the the chicbo loses in the breeding process. min 0, max 1, default 1 - Double").defineInRange("Negative Health Gain", 1D, 0D, 1D);
        POSS_SPEED_GAIN = BUILDER.comment("The possible amount of percent Speed passed on the the chicbo gains in the breeding process. min 0, max 1, default 0.1 - Double").defineInRange("Positive Speed Gain", 0.1D, 0D, 1D);
        POSS_SPEED_LOSS = BUILDER.comment("The possible amount of percent Speed passed on the the chicbo loses in the breeding process. min 0, max 1, default 1 - Double").defineInRange("Negative Speed Gain", 1D, 0D, 1D);
        POSS_STAMINA_GAIN = BUILDER.comment("The possible amount of percent Stamina passed on the the chicbo gains in the breeding process. min 0, max 1, default 0.1 - Double").defineInRange("Positive Stamina Gain", 0.1D, 0D, 1D);
        POSS_STAMINA_LOSS = BUILDER.comment("The possible amount of percent Stamina passed on the the chicbo loses in the breeding process. min 0, max 1, default 1 - Double").defineInRange("Negative Stamina Gain", 1D, 0D, 1D);
        BUILDER.pop();
        BUILDER.push("Alpha Controls");
        CHOCOBO_ARMOR_ALPHA = BUILDER.comment("The Alpha Value of the Chocobo Armor. min 0, max 0.75, default 0.1 - Double").defineInRange("Armor Alpha", 0.1D, 0D, 0.75D);
        CHOCOBO_WEAPON_ALPHA = BUILDER.comment("The Alpha Value of the Chocobo Weapon. min 0, max 0.75, default 0.1 - Double").defineInRange("Weapon Alpha", 0.1D, 0D, 0.75D);
        CHOCOBO_COLLAR_ALPHA = BUILDER.comment("The Alpha Value of the Chocobo Collar. min 0, max 1, default 0.2 - Double").defineInRange("Collar Alpha", 0.2D, 0D, 0.75D);
        CHOCOBO_SADDLE_ALPHA = BUILDER.comment("The Alpha Value of the Chocobo Saddle. min 0, max 1, default 0.1 - Double").defineInRange("Saddle Alpha", 0.1D, 0D, 0.75D);
        BUILDER.pop();
        BUILDER.push("Misc Chocobo Controls");
        TAME_CHANCE = BUILDER.comment("The Chance of Time per attempt to Tame a Chocobo. min 0.05, max 1, default 0.15 - Double").defineInRange("Tame Chance", 0.15D, 0.05D, 1D);
        EXTRA_CHOCOBO_EFFECT = BUILDER.comment("Are there Extra Combat/Side Effects for the Chocobos and ChocoDisguise Armor").define("Extra Effects", true);
        EXTRA_CHOCOBO_RESOURCES_HIT = BUILDER.comment("Are their Resources for Chocobos for combat on hit").define("Resources on Hit", true);
        EXTRA_CHOCOBO_RESOURCES_KILL = BUILDER.comment("Are their Resources for Chocobos for combat on kill").define("Resources on Kill", true);
        SHIFT_HIT_BYPASS = BUILDER.comment("Allow Shift to bypass hit protection on chocobos").define("Shift Bypass", true);
        OWN_CHOCOBO_HITTABLE = BUILDER.comment("Allows Owner to hit their Chocobos").define("Owner Chocobo Hittable", false);
        TAMED_CHOCOBO_HITTABLE = BUILDER.comment("Allows Tamed Chocobos to be hit by a player").define("Tamed Chocobo Hittable", false);
        OWNER_ONLY_ACCES = BUILDER.comment("Allows Tamed Chocobos to have their inventory accessed by only the Owner").define("Owner Only Access", false);
        BUILDER.pop();
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}