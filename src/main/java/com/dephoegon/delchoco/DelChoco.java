package com.dephoegon.delchoco;

import com.dephoegon.delchoco.aid.ChocoList;
import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.aid.world.WorldConfig;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelChoco implements ModInitializer, ClientModInitializer {
	public static final String DELCHOCO_ID = "delchoco";
    public static final Logger LOGGER = LoggerFactory.getLogger(DELCHOCO_ID);

	public void onInitialize() {
		ForgeConfigRegistry.INSTANCE.register(DELCHOCO_ID, ModConfig.Type.COMMON, ChocoboConfig.SPEC, DelChoco.DELCHOCO_ID+"-chocobo_config.toml");
		ForgeConfigRegistry.INSTANCE.register(DELCHOCO_ID, ModConfig.Type.COMMON, WorldConfig.SPEC, DelChoco.DELCHOCO_ID+"-world_config.toml");
		ChocoList.commonRegOrder();
	}
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ChocoList.clientRegOrder();
		ChocoList.clientRendering();
	}
}