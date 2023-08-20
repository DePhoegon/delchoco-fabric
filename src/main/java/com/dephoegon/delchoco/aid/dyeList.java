package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.google.common.collect.Maps;
import net.minecraft.item.Item;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import static com.dephoegon.delbase.item.ShiftingDyes.*;
import static net.minecraft.item.Items.*;

public class dyeList {
    private static @NotNull ArrayList<Item> setListOfDyes() {
        ArrayList<Item> dyes = new ArrayList<>();
        dyes.add(RED_DYE.asItem());
        dyes.add(BLACK_DYE.asItem());
        dyes.add(BROWN_DYE.asItem());
        dyes.add(GREEN_DYE.asItem());
        dyes.add(BLUE_DYE.asItem());
        dyes.add(PURPLE_DYE.asItem());
        dyes.add(CYAN_DYE.asItem());
        dyes.add(LIGHT_GRAY_DYE.asItem());
        dyes.add(GRAY_DYE.asItem());
        dyes.add(PINK_DYE.asItem());
        dyes.add(LIME_DYE.asItem());
        dyes.add(YELLOW_DYE.asItem());
        dyes.add(LIGHT_BLUE_DYE.asItem());
        dyes.add(MAGENTA_DYE.asItem());
        dyes.add(ORANGE_DYE.asItem());
        dyes.add(WHITE_DYE.asItem());
        return dyes;
    }
    private static @NotNull ArrayList<Item> setListOfModDyes() {
        ArrayList<Item> dyes = new ArrayList<>();
        dyes.add(CLEANSE_SHIFT_DYE.asItem());
        dyes.add(RED_SHIFT_DYE.asItem());
        dyes.add(BLOOD_SHIFT_DYE.asItem());
        dyes.add(BLACK_SHIFT_DYE.asItem());
        dyes.add(BROWN_SHIFT_DYE.asItem());
        dyes.add(GREEN_SHIFT_DYE.asItem());
        dyes.add(BLUE_SHIFT_DYE.asItem());
        dyes.add(PURPLE_SHIFT_DYE.asItem());
        dyes.add(CYAN_SHIFT_DYE.asItem());
        dyes.add(LIGHT_GRAY_SHIFT_DYE.asItem());
        dyes.add(GRAY_SHIFT_DYE.asItem());
        dyes.add(PINK_SHIFT_DYE.asItem());
        dyes.add(LIME_SHIFT_DYE.asItem());
        dyes.add(YELLOW_SHIFT_DYE.asItem());
        dyes.add(LIGHT_BLUE_SHIFT_DYE.asItem());
        dyes.add(MAGENTA_SHIFT_DYE.asItem());
        dyes.add(ORANGE_SHIFT_DYE.asItem());
        dyes.add(WHITE_SHIFT_DYE.asItem());
        return dyes;
    }
    public static @NotNull ArrayList<Item> getDyeList() {
        ArrayList<Item> out = new ArrayList<>();
        out.addAll(setListOfDyes());
        out.addAll(setListOfModDyes());
        return out;
    }
    public static final Map<Item, ChocoboColor> CHOCO_COLOR_ITEMS = Util.make(Maps.newHashMap(), (map) ->{
        map.put(YELLOW_DYE.asItem(), ChocoboColor.YELLOW);
        map.put(YELLOW_SHIFT_DYE.asItem(), ChocoboColor.YELLOW);
        map.put(GREEN_DYE.asItem(), ChocoboColor.GREEN);
        map.put(GREEN_SHIFT_DYE.asItem(), ChocoboColor.GREEN);
        map.put(BLUE_DYE.asItem(), ChocoboColor.BLUE);
        map.put(BLUE_SHIFT_DYE.asItem(), ChocoboColor.BLUE);
        map.put(WHITE_DYE.asItem(), ChocoboColor.WHITE);
        map.put(WHITE_SHIFT_DYE.asItem(), ChocoboColor.WHITE);
        map.put(BLACK_DYE.asItem(), ChocoboColor.BLACK);
        map.put(BLACK_SHIFT_DYE.asItem(), ChocoboColor.BLACK);
        map.put(CHARCOAL.asItem(), ChocoboColor.BLACK);
        map.put(COAL.asItem(), ChocoboColor.BLACK);
        map.put(GOLD_INGOT.asItem(), ChocoboColor.GOLD);
        map.put(PINK_DYE.asItem(), ChocoboColor.PINK);
        map.put(PINK_SHIFT_DYE.asItem(), ChocoboColor.PINK);
        map.put(RED_DYE.asItem(), ChocoboColor.RED);
        map.put(RED_SHIFT_DYE.asItem(), ChocoboColor.RED);
        map.put(REDSTONE.asItem(), ChocoboColor.RED);
        map.put(PURPLE_DYE.asItem(), ChocoboColor.PURPLE);
        map.put(PINK_SHIFT_DYE.asItem(), ChocoboColor.PURPLE);
        map.put(POPPED_CHORUS_FRUIT.asItem(), ChocoboColor.PURPLE);
        map.put(BLAZE_POWDER.asItem(), ChocoboColor.FLAME);
        map.put(CRIMSON_FUNGUS.asItem(), ChocoboColor.FLAME);
    });
}