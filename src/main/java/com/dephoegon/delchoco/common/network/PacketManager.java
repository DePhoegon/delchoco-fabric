package com.dephoegon.delchoco.common.network;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.network.packets.ChocoboSprintingMessage;
import com.dephoegon.delchoco.common.network.packets.MyPacketContext;
import com.dephoegon.delchoco.common.network.packets.OpenChocoboGuiMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PacketManager {
    public static final Identifier OPEN_CHOCOBO_GUI_PACKET_ID = new Identifier(DelChoco.DELCHOCO_ID, "open_chocobo_gui");
    public static final Identifier CHOCOBO_SPRINTING_PACKET_ID = new Identifier(DelChoco.DELCHOCO_ID, "chocobo_sprinting");

    public static void sendToClient(ServerPlayerEntity player, OpenChocoboGuiMessage message) {
        PacketByteBuf buf = PacketByteBufs.create();
        OpenChocoboGuiMessage.encode(message, buf);
        ServerPlayNetworking.send(player, OPEN_CHOCOBO_GUI_PACKET_ID, buf);
    }
    public static void sendToClient(ServerPlayerEntity player, ChocoboSprintingMessage message) {
        PacketByteBuf buf = PacketByteBufs.create();
        ChocoboSprintingMessage.encode(message, buf);
        ServerPlayNetworking.send(player, OPEN_CHOCOBO_GUI_PACKET_ID, buf);
    }
    public static void sendToServer(OpenChocoboGuiMessage message) {
        PacketByteBuf buf = PacketByteBufs.create();
        OpenChocoboGuiMessage.encode(message, buf);
        ClientPlayNetworking.send(OPEN_CHOCOBO_GUI_PACKET_ID, buf);
    }
    public static void sendToServer(ChocoboSprintingMessage message) {
        PacketByteBuf buf = PacketByteBufs.create();
        ChocoboSprintingMessage.encode(message, buf);
        ClientPlayNetworking.send(OPEN_CHOCOBO_GUI_PACKET_ID, buf);
    }

    public static void init() {
        // Register client-side packet handlers
        ClientPlayNetworking.registerGlobalReceiver(OPEN_CHOCOBO_GUI_PACKET_ID, (client, handler, buf, responseSender) -> {
            OpenChocoboGuiMessage message = OpenChocoboGuiMessage.decode(buf);
            client.execute(() -> OpenChocoboGuiMessage.handle(message, null));
        });

        // Register server-side packet handlers
        ServerPlayNetworking.registerGlobalReceiver(CHOCOBO_SPRINTING_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            ChocoboSprintingMessage message = ChocoboSprintingMessage.decode(buf);
            server.execute(() -> ChocoboSprintingMessage.handle(message, new MyPacketContext(player)));
        });
    }
}
