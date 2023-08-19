package com.dephoegon.delchoco.common.network.packets;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.thread.ThreadExecutor;

public class MyPacketContext implements PacketContext {
    private final ServerPlayerEntity player;

    public MyPacketContext(ServerPlayerEntity player) { this.player = player; }
    public EnvType getPacketEnvironment() { return EnvType.SERVER; }
    public PlayerEntity getPlayer() { return player; }
    public ThreadExecutor<?> getTaskQueue() { return player.server; }
}
