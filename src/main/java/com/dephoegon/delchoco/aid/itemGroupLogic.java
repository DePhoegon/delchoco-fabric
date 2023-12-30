package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.DelChoco;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.dephoegon.delchoco.aid.ChocoItemGroupArrays.getDelChocoItems;
import static com.dephoegon.delchoco.common.init.ModItems.CHOCOBO_SADDLE;

public class itemGroupLogic {
    public static final ItemGroup DELCHOCO_ITEMS = Registry.register(Registries.ITEM_GROUP, new Identifier(DelChoco.DELCHOCO_ID, "dephoegon_chocobos"), FabricItemGroup.builder().displayName(Text.translatable("itemGroup.dephoegon_chocobos")).icon(() -> new ItemStack(CHOCOBO_SADDLE)).entries((displayContext, entries) -> getDelChocoItems().forEach(entries::add)).build());

    public static void registerItemGroups() {
        DelChoco.LOGGER.info("Registering Item Groups for " + DelChoco.DELCHOCO_ID);
    }
}