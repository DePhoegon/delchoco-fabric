package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.client.keybind.KeyBindManager;
import com.dephoegon.delchoco.common.entities.properties.ModDataSerializers;
import com.dephoegon.delchoco.common.init.ModEntities;
import com.dephoegon.delchoco.common.init.ModItems;

public class ChocoList {
    public static void clientRegOrder() {
        KeyBindManager.registerKeyBinds();
    }
    public static void commonRegOrder() {
        ModDataSerializers.init();
        ModEntities.registerAttributes();
        ModItems.registerModItems();
    }
}
