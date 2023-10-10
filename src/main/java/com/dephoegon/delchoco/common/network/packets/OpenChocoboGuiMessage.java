package com.dephoegon.delchoco.common.network.packets;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.client.gui.ChocoInventoryScreen;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboInventory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// TODO: 2021-10-07 - 10:00:00 - Fix this class,
//  use a new class for the static methods to treat it as if it was bound to this class and not Static
public class OpenChocoboGuiMessage {
    public int entityId;
    public int windowId;
    public NbtCompound saddle;
    public NbtCompound weapon;
    public NbtCompound armor;
    public NbtCompound inventory;

    public OpenChocoboGuiMessage(@NotNull Chocobo chocobo, int windowId) {
        this.entityId = chocobo.getId();
        this.windowId = windowId;

        this.saddle = chocobo.chocoboSaddleInv.singleSlotToNBT();
        this.weapon = chocobo.chocoboWeaponInv.singleSlotToNBT();
        this.armor = chocobo.chocoboArmorInv.singleSlotToNBT();
        this.inventory = toNbt(chocobo.chocoboBackBoneList);
    }

    public OpenChocoboGuiMessage(int entityID, int windowId, NbtCompound saddle, NbtCompound weapon, NbtCompound armor, NbtCompound inventory) {
        this.entityId = entityID;
        this.windowId = windowId;

        this.saddle = saddle;
        this.weapon = weapon;
        this.armor = armor;
        this.inventory = inventory;
    }

    public static void encode(@NotNull OpenChocoboGuiMessage message, @NotNull PacketByteBuf buf) {
        buf.writeInt(message.entityId);
        buf.writeInt(message.windowId);
        buf.writeNbt(message.saddle);
        buf.writeNbt(message.weapon);
        buf.writeNbt(message.armor);
        buf.writeBoolean(message.inventory != null);
        if (message.inventory != null) { buf.writeNbt(message.inventory); }
    }

    @Contract("_ -> new")
    public static @NotNull OpenChocoboGuiMessage decode(@NotNull PacketByteBuf buf) { return new OpenChocoboGuiMessage(buf.readInt(), buf.readInt(), buf.readNbt(), buf.readNbt(), buf.readNbt(), buf.readBoolean() ? buf.readNbt() : null); }

    public static void handle(@NotNull OpenChocoboGuiMessage message, MyPacketContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.world != null;
        Entity entity = mc.world.getEntityById(message.entityId);
        if (!(entity instanceof Chocobo chocobo)) {
            DelChoco.LOGGER.warn("Server send OpenGUI for chocobo with id {}, but this entity does not exist on my side", message.entityId);
            return;
        }

        ChocoInventoryScreen.openInventory(message.windowId, chocobo);
        chocobo.chocoboWeaponInv.singleSlotFromNBT(message.weapon);
        chocobo.chocoboArmorInv.singleSlotFromNBT(message.armor);
        chocobo.chocoboSaddleInv.singleSlotFromNBT(message.saddle);
        if (message.inventory != null)  { fromNbt(chocobo.chocoboBackboneInv, message.inventory); }
    }
    private @NotNull NbtCompound toNbt(@NotNull DefaultedList<ItemStack> inventory) {
        NbtCompound nbt = new NbtCompound();
        for(int i = 0; i < inventory.size(); i++) {
            if(!inventory.get(i).isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                inventory.get(i).writeNbt(itemTag);
                nbt.put("Slot-"+i, itemTag);
            }
        }
        return nbt;
    }
    private static void fromNbt(@NotNull ChocoboInventory inventory, @NotNull NbtCompound nbt) {
        for(int i = 0; i < inventory.size(); i++) {
            if(nbt.contains("Slot-"+i)) { inventory.setStack(i, ItemStack.fromNbt(nbt.getCompound("Slot-"+i))); }
        }
    }
}