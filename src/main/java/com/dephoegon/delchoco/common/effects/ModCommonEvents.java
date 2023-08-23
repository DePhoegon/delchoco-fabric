package com.dephoegon.delchoco.common.effects;

import com.dephoegon.delchoco.common.commands.chocoboTeams;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public class ModCommonEvents {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        chocoboTeams.commands(dispatcher);
    }
}