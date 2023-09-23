package com.dephoegon.delchoco.aid;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

import static com.dephoegon.delchoco.common.init.ModItems.*;

public class tradeAdds {
    public static void addTrades() {
        final ItemStack gysahl_green = new ItemStack(GYSAHL_GREEN_ITEM, 8);
        final ItemStack lovely_gysahl = new ItemStack(LOVELY_GYSAHL_GREEN, 2);
        final ItemStack dead_pepper = new ItemStack(DEAD_PEPPER, 1);
        final ItemStack golden_green = new ItemStack(GOLDEN_GYSAHL_GREEN, 1);
        final ItemStack pink_green = new ItemStack(PINK_GYSAHL_GREEN, 1);
        final ItemStack spike_fruit = new ItemStack(SPIKE_FRUIT, 1);

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1, factories -> {
            factories.add((entity, random) -> new TradeOffer(gysahl_green, new ItemStack(Items.EMERALD, 1), 16, 1, 0.02F));
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 2), gysahl_green, 16, 2, 0.03F));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 2, factories -> {
            factories.add((entity, random) -> new TradeOffer(lovely_gysahl, new ItemStack(Items.EMERALD, 1), 16, 3, 0.02F));
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 2), lovely_gysahl, 16, 3, 0.03F));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER,
                3, factories -> {
            factories.add(((entity, random) -> new TradeOffer(pink_green, new ItemStack(Items.EMERALD, 4), 6, 6, 0.05F)));
            factories.add(((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 8), pink_green, 4, 6, 0.05F)));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER,
                4, factories -> {
            factories.add(((entity, random) -> new TradeOffer(dead_pepper, new ItemStack(Items.EMERALD, 6), 6, 12, 0.05F)));
            factories.add(((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 12), dead_pepper, 3, 12, 0.05F)));
            factories.add(((entity, random) -> new TradeOffer(spike_fruit, new ItemStack(Items.EMERALD, 6), 6, 12, 0.05F)));
            factories.add(((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 12), spike_fruit, 3, 12, 0.05F)));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER,
                5, factories -> {
            factories.add(((entity, random) -> new TradeOffer(golden_green, new ItemStack(Items.EMERALD, 16), 4, 18, 0.05F)));
            factories.add(((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 32), golden_green, 2, 18, 0.05F)));
        });
    }
}