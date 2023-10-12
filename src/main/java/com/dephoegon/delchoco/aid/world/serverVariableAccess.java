package com.dephoegon.delchoco.aid.world;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.composable;
import com.dephoegon.delchoco.client.ChocoboSprintingEventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class serverVariableAccess {
    public static void init() {
        DelChoco.LOGGER.info("(DelChoco Mod) - Server Variable Access Initialized");
        ServerLifecycleEvents.SERVER_STARTED.register(server -> composable.addToList());
    }
    public static void clientInit() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ClientTickEvents.END_WORLD_TICK.register(client -> ChocoboSprintingEventHandler.onKeyPress()));
    }
}