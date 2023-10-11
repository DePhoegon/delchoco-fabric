package com.dephoegon.delchoco;

import com.dephoegon.delchoco.aid.ChocoList;
import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.aid.world.WorldConfig;
import com.dephoegon.delchoco.client.clientHandler;
import com.dephoegon.delchoco.client.models.armor.ChocoDisguiseFeatureRenderer;
import com.dephoegon.delchoco.client.renderer.entities.ChocoboRenderer;
import com.dephoegon.delchoco.common.init.ModEntities;
import com.dephoegon.delchoco.common.init.ModItems;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
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
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import static com.dephoegon.delchoco.common.init.ModItems.CHOCOBO_SADDLE;
import static net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.*;

public class DelChoco implements ModInitializer, ClientModInitializer {
	public static final String DELCHOCO_ID = "delchoco";
    public static final Logger LOGGER = LoggerFactory.getLogger(DELCHOCO_ID);
	public static final ItemGroup DELCHOCO_ITEMS = FabricItemGroupBuilder.build(new Identifier(DELCHOCO_ID, "dephoegon_chocobos"),
			() -> new ItemStack(CHOCOBO_SADDLE));
	public static ChocoboConfig chocoConfigHolder;
	public static WorldConfig worldConfigHolder;

	public void onInitialize() {
		chocoConfigHolder = Configuration.registerConfig(ChocoboConfig.class, ConfigFormats.yaml()).getConfigInstance();
		worldConfigHolder = Configuration.registerConfig(WorldConfig.class, ConfigFormats.yaml()).getConfigInstance();
		ModEntities.registerAttributes();
		ChocoList.commonRegOrder();
	}
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ChocoList.clientRegOrder();
		register(ModEntities.CHOCOBO_ENTITY, ChocoboRenderer::new);
		register(ModEntities.YELLOW_CHOCOBO_ENTITY, ChocoboRenderer::new);
		register(ModEntities.GREEN_CHOCOBO_ENTITY, ChocoboRenderer::new);
		register(ModEntities.BLUE_CHOCOBO_ENTITY, ChocoboRenderer::new);
		register(ModEntities.WHITE_CHOCOBO_ENTITY, ChocoboRenderer::new);
		register(ModEntities.BLACK_CHOCOBO_ENTITY, ChocoboRenderer::new);
		register(ModEntities.GOLD_CHOCOBO_ENTITY, ChocoboRenderer::new);
		register(ModEntities.PINK_CHOCOBO_ENTITY, ChocoboRenderer::new);
		register(ModEntities.RED_CHOCOBO_ENTITY, ChocoboRenderer::new);
		register(ModEntities.PURPLE_CHOCOBO_ENTITY, ChocoboRenderer::new);
		register(ModEntities.FLAME_CHOCOBO_ENTITY, ChocoboRenderer::new);
		clientHandler.ChocoboRendering();
		GeoArmorRenderer.registerArmorRenderer(new ChocoDisguiseFeatureRenderer(), ModItems.LEATHER_CHOCO_DISGUISE_BOOTS, ModItems.LEATHER_CHOCO_DISGUISE_LEGS, ModItems.LEATHER_CHOCO_DISGUISE_CHEST, ModItems.LEATHER_CHOCO_DISGUISE_HELMET, ModItems.IRON_CHOCO_DISGUISE_BOOTS, ModItems.IRON_CHOCO_DISGUISE_LEGS, ModItems.IRON_CHOCO_DISGUISE_CHEST, ModItems.IRON_CHOCO_DISGUISE_HELMET, ModItems.DIAMOND_CHOCO_DISGUISE_BOOTS, ModItems.DIAMOND_CHOCO_DISGUISE_LEGS, ModItems.DIAMOND_CHOCO_DISGUISE_CHEST, ModItems.DIAMOND_CHOCO_DISGUISE_HELMET, ModItems.NETHERITE_CHOCO_DISGUISE_BOOTS, ModItems.NETHERITE_CHOCO_DISGUISE_LEGS, ModItems.NETHERITE_CHOCO_DISGUISE_CHEST, ModItems.NETHERITE_CHOCO_DISGUISE_HELMET);
	}
}