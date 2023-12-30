package com.dephoegon.delchoco.aid;

import net.minecraft.item.Item;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.dephoegon.delchoco.common.init.ModItems.*;

public class ChocoItemGroupArrays {
    private static @NotNull ArrayList<Item> chocoItems() {
        ArrayList<Item> out = new ArrayList<>();
        out.add(CHOCOBO_FEATHER);
        out.add(CHOCOBO_WHISTLE);
        out.add(CHOCOBO_LEASH_STICK);
        out.add(GYSAHL_GREEN_SEEDS);
        out.add(GYSAHL_GREEN_ITEM);
        return out;
    }
    private static @NotNull ArrayList<Item> chocoFood() {
        ArrayList<Item> out = new ArrayList<>();
        out.add(CHOCOBO_DRUMSTICK_RAW);
        out.add(CHOCOBO_DRUMSTICK_COOKED);
        out.add(PICKLED_GYSAHL_RAW);
        out.add(PICKLED_GYSAHL_COOKED);
        return out;
    }
    private static @NotNull ArrayList<Item> chocoboFood() {
        ArrayList<Item> out = new ArrayList<>();
        out.add(DEAD_PEPPER);
        out.add(SPIKE_FRUIT);
        out.add(LOVELY_GYSAHL_GREEN);
        out.add(GOLDEN_GYSAHL_GREEN);
        out.add(PINK_GYSAHL_GREEN);
        out.add(GYSAHL_CAKE);
        return out;
    }
    private static @NotNull ArrayList<Item> spawnEggs() {
        ArrayList<Item> out = new ArrayList<>();
        out.add(YELLOW_CHOCOBO_SPAWN_EGG);
        out.add(GREEN_CHOCOBO_SPAWN_EGG);
        out.add(BLUE_CHOCOBO_SPAWN_EGG);
        out.add(WHITE_CHOCOBO_SPAWN_EGG);
        out.add(BLACK_CHOCOBO_SPAWN_EGG);
        out.add(GOLD_CHOCOBO_SPAWN_EGG);
        out.add(PINK_CHOCOBO_SPAWN_EGG);
        out.add(RED_CHOCOBO_SPAWN_EGG);
        out.add(PURPLE_CHOCOBO_SPAWN_EGG);
        out.add(FLAME_CHOCOBO_SPAWN_EGG);
        return out;
    }
    private static @NotNull ArrayList<Item> chocoboGuiseGear() {
        ArrayList<Item> out = new ArrayList<>();
        out.add(LEATHER_CHOCO_DISGUISE_HELMET);
        out.add(LEATHER_CHOCO_DISGUISE_CHEST);
        out.add(LEATHER_CHOCO_DISGUISE_LEGS);
        out.add(LEATHER_CHOCO_DISGUISE_BOOTS);
        out.add(IRON_CHOCO_DISGUISE_HELMET);
        out.add(IRON_CHOCO_DISGUISE_CHEST);
        out.add(IRON_CHOCO_DISGUISE_LEGS);
        out.add(IRON_CHOCO_DISGUISE_BOOTS);
        out.add(DIAMOND_CHOCO_DISGUISE_HELMET);
        out.add(DIAMOND_CHOCO_DISGUISE_CHEST);
        out.add(DIAMOND_CHOCO_DISGUISE_LEGS);
        out.add(DIAMOND_CHOCO_DISGUISE_BOOTS);
        out.add(NETHERITE_CHOCO_DISGUISE_HELMET);
        out.add(NETHERITE_CHOCO_DISGUISE_CHEST);
        out.add(NETHERITE_CHOCO_DISGUISE_LEGS);
        out.add(NETHERITE_CHOCO_DISGUISE_BOOTS);
        return out;
    }
    private static @NotNull ArrayList<Item> chocoboGear() {
        ArrayList<Item> out = new ArrayList<>();
        out.add(STONE_CHOCO_WEAPON);
        out.add(IRON_CHOCO_WEAPON);
        out.add(DIAMOND_CHOCO_WEAPON);
        out.add(NETHERITE_CHOCO_WEAPON);
        out.add(CHAIN_CHOCO_CHEST);
        out.add(IRON_CHOCO_CHEST);
        out.add(DIAMOND_CHOCO_CHEST);
        out.add(NETHERITE_CHOCO_CHEST);
        out.add(CHOCOBO_SADDLE);
        out.add(CHOCOBO_SADDLE_BAGS);
        out.add(CHOCOBO_SADDLE_PACK);
        return out;
    }
    @Contract(pure = true)
    public static @NotNull ArrayList<Item> getDelChocoItems() {
        ArrayList<Item> out = new ArrayList<>();
        out.addAll(chocoItems());
        out.addAll(chocoFood());
        out.addAll(chocoboFood());
        out.addAll(spawnEggs());
        out.addAll(chocoboGear());
        out.addAll(chocoboGuiseGear());
        return out;
    }
}