package com.dephoegon.delchoco.common.handler;

import com.dephoegon.delchoco.common.init.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class LootTableEventHandler {
    private static final Identifier DESERT_CHEST = new Identifier("minecraft", "chests/desert_pyramid");
    private static final Identifier END_CITY_CHEST = new Identifier("minecraft", "chests/end_city_treasure");
    private static final Identifier JUNGLE_CHEST = new Identifier("minecraft", "chests/jungle_temple");
    private static final Identifier WOODLAND_MANSION_CHEST = new Identifier("minecraft", "chests/woodland_mansion");
    private static final Identifier GRASS_GYSAHL_SEED = new Identifier("minecraft", "blocks/grass");

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (DESERT_CHEST.equals(id)) {
                LootPool.Builder deadPepper = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.45f))
                        .with(ItemEntry.builder(ModItems.DEAD_PEPPER))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1f, 2f)).build());
                tableBuilder.pool(deadPepper.build());
            }
            if (END_CITY_CHEST.equals(id)) {
                LootPool.Builder goldGreen = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.45f))
                        .with(ItemEntry.builder(ModItems.GOLDEN_GYSAHL_GREEN))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1f, 2f)).build());
                tableBuilder.pool(goldGreen.build());
            }
            if (JUNGLE_CHEST.equals(id)) {
                LootPool.Builder pinkGreen = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.5f))
                        .with(ItemEntry.builder(ModItems.PINK_GYSAHL_GREEN))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1f, 5f)).build());
                tableBuilder.pool(pinkGreen.build());
            }
            if (WOODLAND_MANSION_CHEST.equals(id)) {
                LootPool.Builder spikeFruit = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.45f))
                        .with(ItemEntry.builder(ModItems.SPIKE_FRUIT))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1f, 2f)).build());
                tableBuilder.pool(spikeFruit.build());
            }
            if (GRASS_GYSAHL_SEED.equals(id)) {
                LootPool.Builder gysahlGreen = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.15f))
                        .with(ItemEntry.builder(ModItems.GYSAHL_GREEN_SEEDS))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1f, 2f)).build());
                tableBuilder.pool(gysahlGreen.build());
            }
        });
    }
}