package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.aid.world.serverVariableAccess;
import com.dephoegon.delchoco.client.gui.RenderChocoboOverlay;
import com.dephoegon.delchoco.client.keybind.KeyBindManager;
import com.dephoegon.delchoco.common.handler.LootTableEventHandler;
import com.dephoegon.delchoco.common.init.ModEntities;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.init.ModSounds;
import com.dephoegon.delchoco.common.network.PacketManager;
import com.dephoegon.delchoco.common.world.worldgen.ModWorldGen;

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
        LootTableEventHandler.modifyLootTables();
    }
}