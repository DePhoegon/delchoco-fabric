package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.aid.world.serverVariableAccess;
import com.dephoegon.delchoco.client.clientHandler;
import com.dephoegon.delchoco.client.keybind.KeyBindManager;
import com.dephoegon.delchoco.client.renderer.armor.ChocoDisguiseRenderer;
import com.dephoegon.delchoco.client.renderer.entities.ChocoboRenderer;
import com.dephoegon.delchoco.common.commands.chocoboTeams;
import com.dephoegon.delchoco.common.handler.LootTableEventHandler;
import com.dephoegon.delchoco.common.init.ModEnchantments;
import com.dephoegon.delchoco.common.init.ModEntities;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.init.ModSounds;
import com.dephoegon.delchoco.common.network.PacketManager;
import com.dephoegon.delchoco.common.world.worldgen.ModWorldGen;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ChocoList {
    public static void clientRegOrder() {
        KeyBindManager.registerKeyBinds();
        serverVariableAccess.clientInit();
    }
    public static void clientRendering() {
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
        // EntityRendererRegistry.register(ModEntities.CHOCOBO_ARMOR_STAND_ENTITY, ChocoboArmorStandRenderer::new);

        clientHandler.ChocoboRendering();
        registerChocoDisguiseArmorRenderers();
    }
    private static void registerChocoDisguiseArmorRenderers() {
        ArmorRenderer.register(new ChocoDisguiseRenderer(), ModItems.CHAIN_HELMET, ModItems.CHAIN_CHEST, ModItems.CHAIN_LEGS, ModItems.CHAIN_BOOTS);
        ArmorRenderer.register(new ChocoDisguiseRenderer(), ModItems.REINFORCED_CHAIN_HELMET, ModItems.REINFORCED_CHAIN_CHEST, ModItems.REINFORCED_CHAIN_LEGS, ModItems.REINFORCED_CHAIN_BOOTS);
        ArmorRenderer.register(new ChocoDisguiseRenderer(), ModItems.IRON_HELMET, ModItems.IRON_CHEST, ModItems.IRON_LEGS, ModItems.IRON_BOOTS);
        ArmorRenderer.register(new ChocoDisguiseRenderer(), ModItems.REINFORCED_IRON_HELMET, ModItems.REINFORCED_IRON_CHEST, ModItems.REINFORCED_IRON_LEGS, ModItems.REINFORCED_IRON_BOOTS);
        ArmorRenderer.register(new ChocoDisguiseRenderer(), ModItems.DIAMOND_HELMET, ModItems.DIAMOND_CHEST, ModItems.DIAMOND_LEGS, ModItems.DIAMOND_BOOTS);
        ArmorRenderer.register(new ChocoDisguiseRenderer(), ModItems.REINFORCED_DIAMOND_HELMET, ModItems.REINFORCED_DIAMOND_CHEST, ModItems.REINFORCED_DIAMOND_LEGS, ModItems.REINFORCED_DIAMOND_BOOTS);
        ArmorRenderer.register(new ChocoDisguiseRenderer(), ModItems.NETHERITE_HELMET, ModItems.NETHERITE_CHEST, ModItems.NETHERITE_LEGS, ModItems.NETHERITE_BOOTS);
        ArmorRenderer.register(new ChocoDisguiseRenderer(), ModItems.REINFORCED_NETHERITE_HELMET, ModItems.REINFORCED_NETHERITE_CHEST, ModItems.REINFORCED_NETHERITE_LEGS, ModItems.REINFORCED_NETHERITE_BOOTS);
        ArmorRenderer.register(new ChocoDisguiseRenderer(), ModItems.GILDED_NETHERITE_HELMET, ModItems.GILDED_NETHERITE_CHEST, ModItems.GILDED_NETHERITE_LEGS, ModItems.GILDED_NETHERITE_BOOTS);
    }
    public static void commonRegOrder() {
        itemGroupLogic.registerItemGroups();
        ModEntities.registerAttributes();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> chocoboTeams.commands(dispatcher));
        ModSounds.registerSounds();
        PacketManager.init();
        serverVariableAccess.init();
        ModItems.registerModItems();
        ModEnchantments.registerModEnchantments();
        tradeAdds.addTrades();
        ModWorldGen.generateGysahl();
        ModWorldGen.spawnChocobos();
        LootTableEventHandler.modifyLootTables();
    }
}