package com.dephoegon.delchoco.aid;

import net.minecraft.item.ItemConvertible;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.common.init.ModItems.*;
import static net.minecraft.block.ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE;

public class composable {
    private static void compost() {
        ITEM_TO_LEVEL_INCREASE_CHANCE.defaultReturnValue();
        float f = 0.3F;
        float f1 = 0.5F;
        float f2 = 0.65F;
        float f3 = 0.85F;
        float f4 = 1.0F;
        add(f, GYSAHL_GREEN_SEEDS);
        add(f1, CHOCOBO_FEATHER);
        add(f2, GYSAHL_GREEN_ITEM);
        add(f3, PICKLED_GYSAHL_COOKED);
        add(f3, PICKLED_GYSAHL_RAW);
        add(f3, CHOCOBO_DRUMSTICK_COOKED);
        add(f3, CHOCOBO_DRUMSTICK_RAW);
        add(f4, LOVELY_GYSAHL_GREEN);
        add(f4, GYSAHL_CAKE);
    }
    private static void add(float pChance, @NotNull ItemConvertible pItem) { if (!ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(pItem.asItem())) { ITEM_TO_LEVEL_INCREASE_CHANCE.put(pItem.asItem(), pChance); } }
    public static void addToList(){ compost(); }
}
