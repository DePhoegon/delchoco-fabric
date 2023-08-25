package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.inventory.NestContainer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class ModContainers {
    public static final ScreenHandlerType<NestContainer> NEST = ScreenHandlerRegistry.registerSimple(new Identifier(DelChoco.DELCHOCO_ID, "nest"), (syncId, inventory) -> {
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hitResult = client.crosshairTarget;
        BlockPos pos;
        PlayerEntity player = client.player;
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) { pos = ((BlockHitResult) hitResult).getBlockPos(); }
        else if (player != null) { pos = player.getBlockPos(); }
        else { pos = new BlockPos(0, 0, 0); }
        // Get additional data from somewhere
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);

        // Create a new instance of the NestContainer class and pass in the required arguments
        return new NestContainer(syncId, inventory, buf);
    });
}
