package com.dephoegon.delchoco.common.handler;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.inventory.SaddlebagContainer;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import static com.dephoegon.delchoco.common.init.ModEntities.CHOCOBO_ENTITY;

public class screens {
    public static final ScreenHandlerType<SaddlebagContainer> SADDLEBAG_CONTAINER_TYPE = ScreenHandlerRegistry.registerSimple(new Identifier(DelChoco.DELCHOCO_ID, "open_chocobo_gui"), (syncId, playerInventory) -> new SaddlebagContainer(syncId, playerInventory, new Chocobo(CHOCOBO_ENTITY, playerInventory.player.world)));

    public static void init() {
        DelChoco.LOGGER.info("Registering Screens");
    }
}
