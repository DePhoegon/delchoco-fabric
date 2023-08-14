package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.dephoegon.delchoco.common.entities.Chocobo.tier_one_chocobo_inv_slot_count;
import static com.dephoegon.delchoco.common.entities.Chocobo.tier_two_chocobo_inv_slot_count;
import static com.dephoegon.delchoco.common.items.ChocoboArmorItems.CHOCOBO_ARMOR_MATERIALS;
import static com.dephoegon.delchoco.common.items.ChocoboWeaponItem.CHOCOBO_WEAPON_TIERS;


public class ModItems {
    //Eatables
    public static final Item GYSAHL_GREEN_ITEM = registerItem("gysahl_green", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64).food(ModFoods.GYSAHL_GREEN)));
    public static final Item CHOCOBO_DRUMSTICK_RAW = registerItem("chocobo_drumstick_raw", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64).food(ModFoods.CHOCOBO_DRUMSTICK_RAW)));
    public static final Item CHOCOBO_DRUMSTICK_COOKED = registerItem("chocobo_drumstick_cooked", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64).food(ModFoods.CHOCOBO_DRUMSTICK_COOKED)));
    public static final Item PICKLED_GYSAHL_COOKED = registerItem("pickled_gysahl_cooked", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64).food(ModFoods.PICKLED_GYSAHL_COOKED)));
    public static final Item PICKLED_GYSAHL_RAW = registerItem("pickled_gysahl_raw", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64).food(ModFoods.PICKLED_GYSAHL_RAW)));

    // Spawn Eggs


    // Chocobo Gear
    public static final Item STONE_CHOCO_WEAPON = registerItem("chocobo_weapon_stone", new ChocoboWeaponItem(CHOCOBO_WEAPON_TIERS.get(1), -3f, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item IRON_CHOCO_WEAPON = registerItem("chocobo_weapon_iron", new ChocoboWeaponItem(CHOCOBO_WEAPON_TIERS.get(2), -3f, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item DIAMOND_CHOCO_WEAPON = registerItem("chocobo_weapon_diamond", new ChocoboWeaponItem(CHOCOBO_WEAPON_TIERS.get(3), -2.4f, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item NETHERITE_CHOCO_WEAPON = registerItem("chocobo_weapon_netherite", new ChocoboWeaponItem(CHOCOBO_WEAPON_TIERS.get(4), -2.4f, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1).fireproof()));
    public static final Item CHAIN_CHOCO_CHEST = registerItem("chocobo_armor_chain", new ChocoboArmorItems(CHOCOBO_ARMOR_MATERIALS.get(1), EquipmentSlot.CHEST, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item IRON_CHOCO_CHEST = registerItem("chocobo_armor_iron", new ChocoboArmorItems(CHOCOBO_ARMOR_MATERIALS.get(2), EquipmentSlot.CHEST, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item DIAMOND_CHOCO_CHEST = registerItem("chocobo_armor_diamond", new ChocoboArmorItems(CHOCOBO_ARMOR_MATERIALS.get(3), EquipmentSlot.CHEST, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item NETHERITE_CHOCO_CHEST = registerItem("chocobo_armor_netherite", new ChocoboArmorItems(CHOCOBO_ARMOR_MATERIALS.get(4), EquipmentSlot.CHEST, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1).fireproof()));
    public static final Item CHOCOBO_SADDLE = registerItem("chocobo_saddle", new ChocoboSaddleItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(4), 0, false));
    public static final Item CHOCOBO_SADDLE_BAGS = registerItem("chocobo_saddle_bags", new ChocoboSaddleItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(4), tier_one_chocobo_inv_slot_count, false));
    public static final Item CHOCOBO_SADDLE_PACK = registerItem("chocobo_saddle_bags", new ChocoboSaddleItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(2), tier_two_chocobo_inv_slot_count, true));

    // ChocoGuise Gear


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(DelChoco.Mod_ID, name), item);
    }
    public static void registerModItems() { DelChoco.LOGGER.info("Registering "+DelChoco.Mod_ID+" Items"); }
}