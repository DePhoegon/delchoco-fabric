package com.dephoegon.delchoco.common.inventory;

import com.dephoegon.delchoco.common.blockentities.ChocoboNestBlockEntity;
import com.dephoegon.delchoco.common.init.ModContainers;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NestContainer extends ScreenHandler {
    private final ChocoboNestBlockEntity tile;

    public NestContainer(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, getTileEntity(playerInventory, buf));
    }

    public NestContainer(int syncId, PlayerInventory playerInventory, ChocoboNestBlockEntity chocoboNest) {
        super(ModContainers.NEST, syncId);
        this.tile = chocoboNest;
        this.addSlot(new Slot(tile.getInventory(), 0, 80, 35));
        this.bindPlayerInventory(playerInventory);
    }

    private static @NotNull ChocoboNestBlockEntity getTileEntity(final PlayerInventory playerInventory, final PacketByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.getWorld().getBlockEntity(data.readBlockPos());

        if (tileAtPos instanceof ChocoboNestBlockEntity) {return (ChocoboNestBlockEntity) tileAtPos; }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }
    private void bindPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; ++row) { for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        } }
        for (int i = 0; i < 9; ++i) { this.addSlot(new Slot(inventory, i, 8 + i * 18, 142)); }
    }
    public ChocoboNestBlockEntity getTile() { return tile; }
    public boolean canUse(@NotNull PlayerEntity playerIn) { return true; }
    public @NotNull ItemStack transferSlot(@NotNull PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            final int tileSize = 1;

            if (index < tileSize) {
                if (!this.insertItem(itemstack1, tileSize, slots.size(), true)) { return ItemStack.EMPTY; }
            } else if (!this.insertItem(itemstack1, 0, tileSize, false)) { return ItemStack.EMPTY; }
            this.tile.onInventoryChanged();
            if (itemstack1.isEmpty()) { slot.setStack(ItemStack.EMPTY);}
            else { slot.markDirty(); }
            if (itemstack1.getCount() == itemstack.getCount()) { return ItemStack.EMPTY; }
        }
        return itemstack;
    }
}