package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.blockentities.ChocoboEggBlockEntity;
import com.dephoegon.delchoco.common.blockentities.ChocoboNestBlockEntity;
import com.dephoegon.delchoco.common.blocks.ChocoboEggBlock;
import com.dephoegon.delchoco.common.blocks.GysahlGreenBlock;
import com.dephoegon.delchoco.common.blocks.StrawNestBlock;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.items.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.dephoegon.delchoco.common.entities.Chocobo.tier_one_chocobo_inv_slot_count;
import static com.dephoegon.delchoco.common.entities.Chocobo.tier_two_chocobo_inv_slot_count;
import static com.dephoegon.delchoco.common.items.ChocoboArmorItems.CHOCOBO_ARMOR_MATERIALS;
import static com.dephoegon.delchoco.common.items.ChocoboWeaponItems.CHOCOBO_WEAPON_TIERS;


public class ModItems extends BaseReg {
    //Eatables
    public static final Item GYSAHL_GREEN_ITEM = registerItem("gysahl_green", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64).food(ModFoods.GYSAHL_GREEN)));
    public static final Item CHOCOBO_DRUMSTICK_RAW = registerItem("chocobo_drumstick_raw", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64).food(ModFoods.CHOCOBO_DRUMSTICK_RAW)));
    public static final Item CHOCOBO_DRUMSTICK_COOKED = registerItem("chocobo_drumstick_cooked", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64).food(ModFoods.CHOCOBO_DRUMSTICK_COOKED)));
    public static final Item PICKLED_GYSAHL_COOKED = registerItem("pickled_gysahl_cooked", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64).food(ModFoods.PICKLED_GYSAHL_COOKED)));
    public static final Item PICKLED_GYSAHL_RAW = registerItem("pickled_gysahl_raw", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64).food(ModFoods.PICKLED_GYSAHL_RAW)));

    // Plantish
    public static final Block GYSAHL_GREEN = registerBlockWithoutBlockItem("gysahl_green", new GysahlGreenBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP)), DelChoco.DELCHOCO_ITEMS);
    public static final Item GYSAHL_GREEN_SEEDS = registerItem("gysahl_green_seeds", new AliasedBlockItem(GYSAHL_GREEN, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64)));
    public static final Item LOVELY_GYSAHL_GREEN = registerItem("lovely_gysahl_green", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64)));

    // Spawn Eggs
    public static final Item YELLOW_CHOCOBO_SPAWN_EGG = registerItem("yellow_chocobo_spawn_egg", new ChocoboSpawnEggItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64), ChocoboColor.YELLOW));
    public static final Item GREEN_CHOCOBO_SPAWN_EGG = registerItem("green_chocobo_spawn_egg", new ChocoboSpawnEggItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64), ChocoboColor.GREEN));
    public static final Item BLUE_CHOCOBO_SPAWN_EGG = registerItem("blue_chocobo_spawn_egg", new ChocoboSpawnEggItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64), ChocoboColor.BLUE));
    public static final Item WHITE_CHOCOBO_SPAWN_EGG = registerItem("white_chocobo_spawn_egg", new ChocoboSpawnEggItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64), ChocoboColor.WHITE));
    public static final Item BLACK_CHOCOBO_SPAWN_EGG = registerItem("black_chocobo_spawn_egg", new ChocoboSpawnEggItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64), ChocoboColor.BLACK));
    public static final Item GOLD_CHOCOBO_SPAWN_EGG = registerItem("gold_chocobo_spawn_egg", new ChocoboSpawnEggItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64), ChocoboColor.GOLD));
    public static final Item PINK_CHOCOBO_SPAWN_EGG = registerItem("pink_chocobo_spawn_egg", new ChocoboSpawnEggItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64), ChocoboColor.PINK));
    public static final Item RED_CHOCOBO_SPAWN_EGG = registerItem("red_chocobo_spawn_egg", new ChocoboSpawnEggItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64), ChocoboColor.RED));
    public static final Item PURPLE_CHOCOBO_SPAWN_EGG = registerItem("purple_chocobo_spawn_egg", new ChocoboSpawnEggItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64), ChocoboColor.PURPLE));
    public static final Item FLAME_CHOCOBO_SPAWN_EGG = registerItem("silver_chocobo_spawn_egg", new ChocoboSpawnEggItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64), ChocoboColor.FLAME));

    // Chocobo MISCELLANEOUS
    public static final Item CHOCOBO_FEATHER = registerItem("chocobo_feather", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64)));
    public static final Block CHOCOBO_EGG = registerBlock("chocobo_egg", new ChocoboEggBlock(AbstractBlock.Settings.of(Material.EGG).noCollision().ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque()));
    public static final Item CHOCOBO_EGG_ITEM = registerItem("chocobo_egg", new AliasedBlockItem(CHOCOBO_EGG, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64)));
    public static final Block STRAW_NEST = registerBlock("straw_nest", new StrawNestBlock(AbstractBlock.Settings.of(Material.WOOL).noCollision().sounds(BlockSoundGroup.GRASS).nonOpaque()));
    public static final Item STRAW_NEST_ITEM = registerItem("straw_nest", new AliasedBlockItem(STRAW_NEST, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(64)));
    public static BlockEntityType<ChocoboNestBlockEntity> STRAW_NEST_BLOCK_ENTITY;
    public static BlockEntityType<ChocoboEggBlockEntity> CHOCOBO_EGG_BLOCK_ENTITY;
    public static final Item CHOCOBO_WHISTLE = registerItem("chocobo_whistle", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item GYSAHL_CAKE = registerItem("gysahl_cake", new Item(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(8)));
    public static final Item CHOCOBO_LEASH_STICK = registerItem("chocobo_leash_stick", new ChocoboLeashPointer(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));


    // Chocobo Gear
    public static final Item STONE_CHOCO_WEAPON = registerItem("chocobo_weapon_stone", new ChocoboWeaponItems(CHOCOBO_WEAPON_TIERS.get(1), -3f, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item IRON_CHOCO_WEAPON = registerItem("chocobo_weapon_iron", new ChocoboWeaponItems(CHOCOBO_WEAPON_TIERS.get(2), -3f, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item DIAMOND_CHOCO_WEAPON = registerItem("chocobo_weapon_diamond", new ChocoboWeaponItems(CHOCOBO_WEAPON_TIERS.get(3), -2.4f, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item NETHERITE_CHOCO_WEAPON = registerItem("chocobo_weapon_netherite", new ChocoboWeaponItems(CHOCOBO_WEAPON_TIERS.get(4), -2.4f, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1).fireproof()));
    public static final Item CHAIN_CHOCO_CHEST = registerItem("chocobo_armor_chain", new ChocoboArmorItems(CHOCOBO_ARMOR_MATERIALS.get(1), EquipmentSlot.CHEST, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item IRON_CHOCO_CHEST = registerItem("chocobo_armor_iron", new ChocoboArmorItems(CHOCOBO_ARMOR_MATERIALS.get(2), EquipmentSlot.CHEST, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item DIAMOND_CHOCO_CHEST = registerItem("chocobo_armor_diamond", new ChocoboArmorItems(CHOCOBO_ARMOR_MATERIALS.get(3), EquipmentSlot.CHEST, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1)));
    public static final Item NETHERITE_CHOCO_CHEST = registerItem("chocobo_armor_netherite", new ChocoboArmorItems(CHOCOBO_ARMOR_MATERIALS.get(4), EquipmentSlot.CHEST, new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(1).fireproof()));
    public static final Item CHOCOBO_SADDLE = registerItem("chocobo_saddle", new ChocoboSaddleItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(4), 0, false));
    public static final Item CHOCOBO_SADDLE_BAGS = registerItem("chocobo_saddle_bags", new ChocoboSaddleItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(4), tier_one_chocobo_inv_slot_count, false));
    public static final Item CHOCOBO_SADDLE_PACK = registerItem("chocobo_saddle_bags", new ChocoboSaddleItem(new FabricItemSettings().group(DelChoco.DELCHOCO_ITEMS).maxCount(2), tier_two_chocobo_inv_slot_count, true));

    // ChocoGuise Gear


    public static void registerBlockEntities() {
        STRAW_NEST_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "chocobo_nest"), FabricBlockEntityTypeBuilder.create(ChocoboNestBlockEntity::new, STRAW_NEST).build(null));
        CHOCOBO_EGG_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "chocobo_egg"), FabricBlockEntityTypeBuilder.create(ChocoboEggBlockEntity::new, CHOCOBO_EGG).build(null));
    }
}