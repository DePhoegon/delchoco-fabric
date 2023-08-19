package com.dephoegon.delchoco.common.network.packets;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class OpenChocoboGuiMessage {
    public int entityId;
    public int windowId;
    public NbtCompound saddle;
    public NbtCompound inventory;

    public OpenChocoboGuiMessage(@NotNull Chocobo chocobo, int windowId) {
        this.entityId = chocobo.getId();
        this.windowId = windowId;

        this.saddle = chocobo.saddleItemStackHandler.serializeNBT();
        ItemStack saddleStack = chocobo.getSaddle();
        if(!saddleStack.isEmpty() && saddleStack.getItem() instanceof ChocoboSaddleItem saddleItem) {
            if(saddleItem.getInventorySize() > 0) {
                this.inventory = chocobo.chocoboInventory.serializeNBT();
            }
        }
    }

    public OpenChocoboGuiMessage(int entityID, int windowId, NbtCompound saddle, NbtCompound inventory) {
        this.entityId = entityID;
        this.windowId = windowId;

        this.saddle = saddle;
        this.inventory = inventory;
    }

    public static void encode(@NotNull OpenChocoboGuiMessage message, @NotNull PacketByteBuf buf) {
        buf.writeInt(message.entityId);
        buf.writeInt(message.windowId);
        buf.writeNbt(message.saddle);
        buf.writeBoolean(message.inventory != null);
        if (message.inventory != null) {
            buf.writeNbt(message.inventory);
        }
    }

    @Contract("_ -> new")
    public static @NotNull OpenChocoboGuiMessage decode(@NotNull PacketByteBuf buf) {
        return new OpenChocoboGuiMessage(buf.readInt(), buf.readInt(), buf.readNbt(), buf.readBoolean() ? buf.readNbt() : null);
    }

    public static void handle(@NotNull OpenChocoboGuiMessage message, PacketContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity entity = mc.world.getEntityById(message.entityId);
        if (!(entity instanceof Chocobo chocobo)) {
            DelChoco.LOGGER.warn("Server send OpenGUI for chocobo with id {}, but this entity does not exist on my side", message.entityId);
            return;
        }

        ChocoboInventoryScreen.openInventory(message.windowId, chocobo);
        chocobo.saddleItemStackHandler.deserializeNBT(message.saddle);
        if (message.inventory != null) {
            chocobo.chocoboInventory.deserializeNBT(message.inventory);
        }
    }
}
