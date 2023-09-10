package com.dephoegon.delchoco.mixin;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityAccessor {
    @Invoker
    void callIncrementScreenHandlerSyncId();
    @Invoker
    void callOnScreenHandlerOpened(ScreenHandler screenHandler);
    @Accessor("screenHandlerSyncId")
    int getScreenHandlerSyncId();
}
