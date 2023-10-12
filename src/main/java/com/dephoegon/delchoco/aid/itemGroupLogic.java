package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.DelChoco;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.dephoegon.delchoco.common.init.ModItems.CHOCOBO_SADDLE;

public class itemGroupLogic {
    public static ItemGroup DELCHOCO_ITEMS;

    public static void registerItemGroups() {
        DELCHOCO_ITEMS = FabricItemGroup.builder(new Identifier(DelChoco.DELCHOCO_ID, "dephoegon_chocobos")).displayName(Text.translatable("itemGroup.dephoegon_chocobos")).icon(() -> new ItemStack(CHOCOBO_SADDLE)).build();
    }
    public static void addItemToAdditionalGroup(ItemGroup group, Item item) { ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item)); }
}