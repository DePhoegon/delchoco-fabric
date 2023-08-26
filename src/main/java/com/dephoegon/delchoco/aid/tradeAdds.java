package com.dephoegon.delchoco.aid;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

import static com.dephoegon.delchoco.common.init.ModItems.GYSAHL_GREEN_ITEM;
import static com.dephoegon.delchoco.common.init.ModItems.LOVELY_GYSAHL_GREEN;

public class tradeAdds {
    public static void addTrades() {
        final ItemStack gysahl_green = new ItemStack(GYSAHL_GREEN_ITEM, 8);
        final ItemStack lovely_gysahl = new ItemStack(LOVELY_GYSAHL_GREEN, 2);

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1, factories -> {
            factories.add((entity, random) -> new TradeOffer(gysahl_green, new ItemStack(Items.EMERALD, 2), 16, 1, 0.02F));
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 1), gysahl_green, 16, 2, 0.03F));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 2, factories -> {
            factories.add((entity, random) -> new TradeOffer(lovely_gysahl, new ItemStack(Items.EMERALD, 2), 16, 2, 0.02F));
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 1), lovely_gysahl, 16, 2, 0.03F));
        });
    }
}
