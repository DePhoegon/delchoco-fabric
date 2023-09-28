package com.dephoegon.delchoco.aid.world;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.composable;
import com.dephoegon.delchoco.client.ChocoboSprintingEventHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class serverVariableAccess {
    public static void init() {
        DelChoco.LOGGER.info("(DelChoco Mod) - Server Variable Access Initialized");
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ClientTickEvents.END_CLIENT_TICK.register(client -> { ChocoboSprintingEventHandler.onKeyPress(); });
            composable.addToList();
        });
    }
}