package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.client.gui.ChocoInventoryScreen;
import com.dephoegon.delchoco.client.gui.ChocoboArmorStandScreen;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.ChocoboArmorStand;
import com.dephoegon.delchoco.common.inventory.ChocoboArmorStandScreenHandler;
import com.dephoegon.delchoco.common.inventory.ChocoboScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow
    private ClientWorld world;
    @Shadow
    private MinecraftClient client;

    @Inject(method = "onOpenHorseScreen", at = @At("TAIL"))
    private void onOpenHorseScreen(@NotNull OpenHorseScreenS2CPacket packet, CallbackInfo ci) {
        Entity entity = this.world.getEntityById(packet.getHorseId());
        if (entity instanceof Chocobo chocobo) {
            ClientPlayerEntity clientPlayerEntity = this.client.player;
            assert clientPlayerEntity != null;
            ChocoboScreenHandler saddlebagContainer = new ChocoboScreenHandler(packet.getSyncId(), clientPlayerEntity.getInventory(), chocobo);
            clientPlayerEntity.currentScreenHandler = saddlebagContainer;
            this.client.setScreen(new ChocoInventoryScreen(saddlebagContainer, clientPlayerEntity.getInventory(), chocobo));
        } else if (entity instanceof ChocoboArmorStand armorStand) {
            ClientPlayerEntity clientPlayerEntity = this.client.player;
            assert clientPlayerEntity != null;
            ChocoboArmorStandScreenHandler screenHandler = new ChocoboArmorStandScreenHandler(packet.getSyncId(), clientPlayerEntity.getInventory(), armorStand);
            clientPlayerEntity.currentScreenHandler = screenHandler;
            this.client.setScreen(new ChocoboArmorStandScreen(screenHandler, clientPlayerEntity.getInventory(), armorStand.getDisplayName()));
        }
    }
}