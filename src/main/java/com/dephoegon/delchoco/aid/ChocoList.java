package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.aid.world.serverVariableAccess;
import com.dephoegon.delchoco.client.keybind.KeyBindManager;
import com.dephoegon.delchoco.common.commands.chocoboTeams;
import com.dephoegon.delchoco.common.handler.LootTableEventHandler;
import com.dephoegon.delchoco.common.init.ModEnchantments;
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