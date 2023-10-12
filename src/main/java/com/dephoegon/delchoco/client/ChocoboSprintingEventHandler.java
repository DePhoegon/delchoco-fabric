package com.dephoegon.delchoco.client;

import com.dephoegon.delchoco.common.network.PacketManager;
import com.dephoegon.delchoco.common.network.packets.ChocoboSprintingMessage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class ChocoboSprintingEventHandler {
    private static boolean isSprinting = false;

    public static void onKeyPress() {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        if (minecraft.player != null && minecraft.player.getVehicle() != null) {
            KeyBinding keyBinding = minecraft.options.sprintKey;
            if (keyBinding.wasPressed()) {
                if (!isSprinting) {
                    isSprinting = true;
                    PacketManager.sendToServer(new ChocoboSprintingMessage(true));
                }
            } else {
                if (isSprinting) {
                    isSprinting = false;
                    PacketManager.sendToServer(new ChocoboSprintingMessage(false));
                }
            }
        } else { isSprinting = false; }
    }
}