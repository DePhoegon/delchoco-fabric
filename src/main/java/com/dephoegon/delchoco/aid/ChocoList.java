package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.world.serverVariableAccess;
import com.dephoegon.delchoco.client.gui.RenderChocoboOverlay;
import com.dephoegon.delchoco.client.keybind.KeyBindManager;
import com.dephoegon.delchoco.common.blockentities.ChocoboEggBlockEntity;
import com.dephoegon.delchoco.common.blockentities.ChocoboNestBlockEntity;
import com.dephoegon.delchoco.common.handler.screens;
import com.dephoegon.delchoco.common.init.ModEntities;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.init.ModSounds;
import com.dephoegon.delchoco.common.network.PacketManager;
import com.dephoegon.delchoco.common.world.worldgen.ModWorldGen;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.dephoegon.delchoco.common.init.ModItems.CHOCOBO_EGG;
import static com.dephoegon.delchoco.common.init.ModItems.STRAW_NEST;

public class ChocoList {
    public static void clientRegOrder() {
        KeyBindManager.registerKeyBinds();
        RenderChocoboOverlay.onGuiInGameOverlayRender();
    }
    public static void commonRegOrder() {
        ModEntities.registerAttributes();
        ModSounds.registerSounds();
        PacketManager.init();
        serverVariableAccess.init();
        ModItems.registerModItems();
        tradeAdds.addTrades();
        ModWorldGen.generateGysahl();
        ModWorldGen.spawnChocobos();
        registerBlockEntities();
        screens.init();
    }
    public static BlockEntityType<ChocoboNestBlockEntity> STRAW_NEST_BLOCK_ENTITY;
    public static BlockEntityType<ChocoboEggBlockEntity> CHOCOBO_EGG_BLOCK_ENTITY;
    public static void registerBlockEntities() {
        DelChoco.LOGGER.info("Registering Block Entities");
        STRAW_NEST_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "chocobo_nest"), FabricBlockEntityTypeBuilder.create(ChocoboNestBlockEntity::new, STRAW_NEST).build(null));
        CHOCOBO_EGG_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "chocobo_egg"), FabricBlockEntityTypeBuilder.create(ChocoboEggBlockEntity::new, CHOCOBO_EGG).build(null));
    }
}