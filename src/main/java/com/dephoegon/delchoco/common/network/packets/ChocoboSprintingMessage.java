package com.dephoegon.delchoco.common.network.packets;

import com.dephoegon.delchoco.common.entities.Chocobo;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ChocoboSprintingMessage {
    private final boolean sprinting;

    public ChocoboSprintingMessage(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public static void encode(@NotNull ChocoboSprintingMessage message, @NotNull PacketByteBuf buf) {
        buf.writeBoolean(message.sprinting);
    }

    @Contract("_ -> new")
    public static @NotNull ChocoboSprintingMessage decode(@NotNull PacketByteBuf buf) {
        return new ChocoboSprintingMessage(buf.readBoolean());
    }

    public static void handle(ChocoboSprintingMessage message, @NotNull PacketContext context) {
        ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
        if (player != null) {
            if (player.getVehicle() == null) { return; }

            Entity mount = player.getVehicle();
            if (!(mount instanceof Chocobo)) { return; }

            mount.setSprinting(message.sprinting);
        }
    }
}