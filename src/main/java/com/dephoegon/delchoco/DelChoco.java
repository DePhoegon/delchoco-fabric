package com.dephoegon.delchoco;

import com.dephoegon.delchoco.aid.ChocoList;
import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.aid.world.WorldConfig;
import com.dephoegon.delchoco.client.clientHandler;
import com.dephoegon.delchoco.client.renderer.entities.ChocoboRenderer;
import com.dephoegon.delchoco.common.init.ModEntities;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class DelChoco implements ModInitializer, ClientModInitializer {
	public static final String DELCHOCO_ID = "delchoco";
    public static final Logger LOGGER = LoggerFactory.getLogger(DELCHOCO_ID);
	public static ChocoboConfig chocoConfigHolder;
	public static WorldConfig worldConfigHolder;

	public void onInitialize() {
		ForgeConfigRegistry.INSTANCE.register(DELCHOCO_ID, ModConfig.Type.COMMON, ChocoboConfig.SPEC, DelChoco.DELCHOCO_ID+"-chocobo_config.toml");
		ForgeConfigRegistry.INSTANCE.register(DELCHOCO_ID, ModConfig.Type.COMMON, WorldConfig.SPEC, DelChoco.DELCHOCO_ID+"-world_config.toml");
		ModEntities.registerAttributes();
		ChocoList.commonRegOrder();
		GeckoLib.initialize();
	}
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ChocoList.clientRegOrder();
		EntityRendererRegistry.register(ModEntities.CHOCOBO_ENTITY, ChocoboRenderer::new);
		EntityRendererRegistry.register(ModEntities.YELLOW_CHOCOBO_ENTITY, ChocoboRenderer::new);
		EntityRendererRegistry.register(ModEntities.GREEN_CHOCOBO_ENTITY, ChocoboRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLUE_CHOCOBO_ENTITY, ChocoboRenderer::new);
		EntityRendererRegistry.register(ModEntities.WHITE_CHOCOBO_ENTITY, ChocoboRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLACK_CHOCOBO_ENTITY, ChocoboRenderer::new);
		EntityRendererRegistry.register(ModEntities.GOLD_CHOCOBO_ENTITY, ChocoboRenderer::new);
		EntityRendererRegistry.register(ModEntities.PINK_CHOCOBO_ENTITY, ChocoboRenderer::new);
		EntityRendererRegistry.register(ModEntities.RED_CHOCOBO_ENTITY, ChocoboRenderer::new);
		EntityRendererRegistry.register(ModEntities.PURPLE_CHOCOBO_ENTITY, ChocoboRenderer::new);
		EntityRendererRegistry.register(ModEntities.FLAME_CHOCOBO_ENTITY, ChocoboRenderer::new);
		clientHandler.ChocoboRendering();
	}
}