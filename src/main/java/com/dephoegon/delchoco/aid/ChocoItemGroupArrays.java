package com.dephoegon.delchoco.aid;

import net.minecraft.item.Item;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.dephoegon.delchoco.common.init.ModItems.*;

public class ChocoItemGroupArrays {
    private static @NotNull ArrayList<Item> chocoItems() {
        ArrayList<Item> out = new ArrayList<>();
        out.add(CHOCOBO_LEATHER);
        out.add(FEATHER_TREATED_LEATHER);
        out.add(DIAMOND_TREATED_FEATHER_LEATHER);
        out.add(NETHERITE_TREATED_FEATHER_LEATHER);
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
        out.add(CHOCOBO_ARMOR_STAND_SPAWN_EGG);
        return out;
    }
    private static @NotNull ArrayList<Item> chocoboGuiseGear() {
        ArrayList<Item> out = new ArrayList<>();
        out.add(CHAIN_HELMET);
        out.add(CHAIN_CHEST);
        out.add(CHAIN_LEGS);
        out.add(CHAIN_BOOTS);
        out.add(REINFORCED_CHAIN_HELMET);
        out.add(REINFORCED_CHAIN_CHEST);
        out.add(REINFORCED_CHAIN_LEGS);
        out.add(REINFORCED_CHAIN_BOOTS);
        out.add(IRON_HELMET);
        out.add(IRON_CHEST);
        out.add(IRON_LEGS);
        out.add(IRON_BOOTS);
        out.add(REINFORCED_IRON_HELMET);
        out.add(REINFORCED_IRON_CHEST);
        out.add(REINFORCED_IRON_LEGS);
        out.add(REINFORCED_IRON_BOOTS);
        out.add(DIAMOND_HELMET);
        out.add(DIAMOND_CHEST);
        out.add(DIAMOND_LEGS);
        out.add(DIAMOND_BOOTS);
        out.add(REINFORCED_DIAMOND_HELMET);
        out.add(REINFORCED_DIAMOND_CHEST);
        out.add(REINFORCED_DIAMOND_LEGS);
        out.add(REINFORCED_DIAMOND_BOOTS);
        out.add(NETHERITE_HELMET);
        out.add(NETHERITE_CHEST);
        out.add(NETHERITE_LEGS);
        out.add(NETHERITE_BOOTS);
        out.add(REINFORCED_NETHERITE_HELMET);
        out.add(REINFORCED_NETHERITE_CHEST);
        out.add(REINFORCED_NETHERITE_LEGS);
        out.add(REINFORCED_NETHERITE_BOOTS);
        out.add(GILDED_NETHERITE_HELMET);
        out.add(GILDED_NETHERITE_CHEST);
        out.add(GILDED_NETHERITE_LEGS);
        out.add(GILDED_NETHERITE_BOOTS);
        return out;
    }
    private static @NotNull ArrayList<Item> chocoboGear() {
        ArrayList<Item> out = new ArrayList<>();
        out.add(CHAIN_CHOCO_WEAPON);
        out.add(REINFORCED_CHAIN_CHOCO_WEAPON);
        out.add(IRON_CHOCO_WEAPON);
        out.add(REINFORCED_IRON_CHOCO_WEAPON);
        out.add(DIAMOND_CHOCO_WEAPON);
        out.add(REINFORCED_DIAMOND_CHOCO_WEAPON);
        out.add(NETHERITE_CHOCO_WEAPON);
        out.add(REINFORCED_NETHERITE_CHOCO_WEAPON);
        out.add(GILDED_NETHERITE_CHOCO_WEAPON);
        out.add(CHAIN_CHOCO_HELMET);
        out.add(CHAIN_CHOCO_CHEST);
        out.add(CHAIN_CHOCO_LEGGINGS);
        out.add(CHAIN_CHOCO_BOOTS);
        out.add(REINFORCED_CHAIN_CHOCO_HELMET);
        out.add(REINFORCED_CHAIN_CHOCO_CHEST);
        out.add(REINFORCED_CHAIN_CHOCO_LEGGINGS);
        out.add(REINFORCED_CHAIN_CHOCO_BOOTS);
        out.add(IRON_CHOCO_HELMET);
        out.add(IRON_CHOCO_CHEST);
        out.add(IRON_CHOCO_LEGGINGS);
        out.add(IRON_CHOCO_BOOTS);
        out.add(REINFORCED_IRON_CHOCO_HELMET);
        out.add(REINFORCED_IRON_CHOCO_CHEST);
        out.add(REINFORCED_IRON_CHOCO_LEGGINGS);
        out.add(REINFORCED_IRON_CHOCO_BOOTS);
        out.add(DIAMOND_CHOCO_HELMET);
        out.add(DIAMOND_CHOCO_CHEST);
        out.add(DIAMOND_CHOCO_LEGGINGS);
        out.add(DIAMOND_CHOCO_BOOTS);
        out.add(REINFORCED_DIAMOND_CHOCO_HELMET);
        out.add(REINFORCED_DIAMOND_CHOCO_CHEST);
        out.add(REINFORCED_DIAMOND_CHOCO_LEGGINGS);
        out.add(REINFORCED_DIAMOND_CHOCO_BOOTS);
        out.add(NETHERITE_CHOCO_HELMET);
        out.add(NETHERITE_CHOCO_CHEST);
        out.add(NETHERITE_CHOCO_LEGGINGS);
        out.add(NETHERITE_CHOCO_BOOTS);
        out.add(REINFORCED_NETHERITE_CHOCO_HELMET);
        out.add(REINFORCED_NETHERITE_CHOCO_CHEST);
        out.add(REINFORCED_NETHERITE_CHOCO_LEGGINGS);
        out.add(REINFORCED_NETHERITE_CHOCO_BOOTS);
        out.add(GILDED_NETHERITE_CHOCO_HELMET);
        out.add(GILDED_NETHERITE_CHOCO_CHEST);
        out.add(GILDED_NETHERITE_CHOCO_LEGGINGS);
        out.add(GILDED_NETHERITE_CHOCO_BOOTS);
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