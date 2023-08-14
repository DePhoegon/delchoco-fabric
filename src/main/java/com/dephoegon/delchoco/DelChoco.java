package com.dephoegon.delchoco;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dephoegon.delchoco.aid.ChocoList.clientRegOrder;
import static net.minecraft.item.Items.BONE_MEAL;

public class DelChoco implements ModInitializer, ClientModInitializer {
	public static final String Mod_ID = "delchoco";
    public static final Logger LOGGER = LoggerFactory.getLogger(Mod_ID);
	public static final ItemGroup DELCHOCO_ITEMS = FabricItemGroupBuilder.build(new Identifier(Mod_ID, "dephoegon_chocobos"),
			() -> new ItemStack(BONE_MEAL));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	}
	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() { clientRegOrder(); }
}