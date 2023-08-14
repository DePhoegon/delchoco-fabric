package com.dephoegon.delchoco.common.init;

import net.minecraft.item.FoodComponent;

public class ModFoods {
    public static final FoodComponent GYSAHL_GREEN = new FoodComponent.Builder().hunger(1).saturationModifier(1F).build();
    public static final FoodComponent CHOCOBO_DRUMSTICK_RAW = new FoodComponent.Builder().hunger(2).saturationModifier(1F).build();
    public static final FoodComponent CHOCOBO_DRUMSTICK_COOKED = new FoodComponent.Builder().hunger(4).saturationModifier(1.5F).build();
    public static final FoodComponent PICKLED_GYSAHL_RAW = new FoodComponent.Builder().hunger(2).saturationModifier(1F).build();
    public static final FoodComponent PICKLED_GYSAHL_COOKED = new FoodComponent.Builder().hunger(6).saturationModifier(2F).build();
}