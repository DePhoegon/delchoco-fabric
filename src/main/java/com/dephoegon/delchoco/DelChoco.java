package com.dephoegon.delchoco;

import com.dephoegon.delchoco.aid.ChocoList;
import com.dephoegon.delchoco.client.clientHandler;
import com.dephoegon.delchoco.client.renderer.entities.ChocoboRenderer;
import com.dephoegon.delchoco.common.init.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.item.Items.BONE_MEAL;

public class DelChoco implements ModInitializer, ClientModInitializer {
	public static final String DELCHOCO_ID = "delchoco";
    public static final Logger LOGGER = LoggerFactory.getLogger(DELCHOCO_ID);
	public static final ItemGroup DELCHOCO_ITEMS = FabricItemGroupBuilder.build(new Identifier(DELCHOCO_ID, "dephoegon_chocobos"),
			() -> new ItemStack(BONE_MEAL));

	public void onInitialize() {
		ChocoList.commonRegOrder();

		LOGGER.info("Hello Fabric world!");
	}
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ChocoList.clientRegOrder();
		EntityRendererRegistry.INSTANCE.register(ModEntities.CHOCOBO_ENTITY, ChocoboRenderer::new);
		clientHandler.ChocoboRendering();
	}
}