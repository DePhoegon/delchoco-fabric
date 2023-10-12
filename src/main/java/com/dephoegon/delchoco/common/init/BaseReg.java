package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.DelChoco;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.dephoegon.delchoco.aid.itemGroupLogic.DELCHOCO_ITEMS;

public class BaseReg {
    protected static Block registerBlock(String name, Block block) { return registerBlock(name, block, false, 0, 0, 0); }
    protected static Block registerBlock(String name, Block block, int burnChance, int burnSpread) { return registerBlock(name, block, true, 0, burnChance, burnSpread); }
    protected static Block registerBlock(String name, Block block, int fuelTime) { return registerBlock(name, block, false, fuelTime, 0, 0); }
    private static Block registerBlock(String name, Block block, boolean burnable, int fuelTime, int burnChance, int burnSpread) {
        if (fuelTime > 0) { registerBlockItem(name, block, fuelTime); } else { registerBlockItem(name, block); }
        Block hold = Registry.register(Registries.BLOCK, new Identifier(DelChoco.DELCHOCO_ID, name), block);
        if (burnable) { FlammableBlockRegistry.getDefaultInstance().add(hold, burnChance, burnSpread); }
        return hold;
    }
    public static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(DelChoco.DELCHOCO_ID, name), block);
    }
    private static Item registerBlockItem(String name, Block block) {
        Item hold = Registry.register(Registries.ITEM, new Identifier(DelChoco.DELCHOCO_ID, name), new BlockItem(block, new FabricItemSettings()));
        ItemGroupEvents.modifyEntriesEvent(DELCHOCO_ITEMS).register(entries -> entries.add(hold));
        return hold;
    }
    private static void registerBlockItem(String name, Block block, int fuelTime) {
        Item hold = registerBlockItem(name, block);
        ItemGroupEvents.modifyEntriesEvent(DELCHOCO_ITEMS).register(entries -> entries.add(hold));
        FuelRegistry.INSTANCE.add(hold, fuelTime);
    }
    protected static Item registerItem(String name, Item item) {
        Item hold = Registry.register(Registries.ITEM, new Identifier(DelChoco.DELCHOCO_ID, name), item);
        ItemGroupEvents.modifyEntriesEvent(DELCHOCO_ITEMS).register(entries -> entries.add(hold));
        return hold;
    }
}