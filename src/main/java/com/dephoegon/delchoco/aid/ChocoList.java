package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.client.gui.RenderChocoboOverlay;
import com.dephoegon.delchoco.client.keybind.KeyBindManager;
import com.dephoegon.delchoco.common.entities.properties.ModDataSerializers;
import com.dephoegon.delchoco.common.init.ModEntities;
import com.dephoegon.delchoco.common.init.ModItems;

public class ChocoList {
    public static void clientRegOrder() {
        KeyBindManager.registerKeyBinds();
        RenderChocoboOverlay.onGuiInGameOverlayRender();
    }
    public static void commonRegOrder() {
        ModDataSerializers.init();
        ModEntities.registerAttributes();
        ModItems.registerModItems();
        tradeAdds.addTrades();
    }
}