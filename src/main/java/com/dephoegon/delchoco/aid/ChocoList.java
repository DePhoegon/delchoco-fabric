package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.aid.world.serverVariableAccess;
import com.dephoegon.delchoco.client.keybind.KeyBindManager;
import com.dephoegon.delchoco.common.commands.chocoboTeams;
import com.dephoegon.delchoco.common.handler.LootTableEventHandler;
import com.dephoegon.delchoco.common.init.ModEntities;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.init.ModSounds;
import com.dephoegon.delchoco.common.network.PacketManager;
import com.dephoegon.delchoco.common.world.worldgen.ModWorldGen;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ChocoList {
    public static void clientRegOrder() {
        KeyBindManager.registerKeyBinds();
        serverVariableAccess.clientInit();
        // RenderChocoboOverlay.onGuiInGameOverlayRender(); // Uncomment if you want to render the chocobo overlay, Stamina bar
    }
    public static void commonRegOrder() {
        itemGroupLogic.registerItemGroups();
        ModEntities.registerAttributes();
        // MemoryTypes.registerAll(); // Uncomment incase brain use of targeting entities is needed
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> chocoboTeams.commands(dispatcher));
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