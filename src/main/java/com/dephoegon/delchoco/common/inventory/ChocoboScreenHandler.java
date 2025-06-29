package com.dephoegon.delchoco.common.inventory;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.common.inventory.ChocoboEquipmentSlot.*;

public class ChocoboScreenHandler extends ScreenHandler {
    private final Chocobo chocobo;
    private int syncTimer = 0;
    private final PlayerInventory playerInventory;

    public ChocoboScreenHandler(int syncId, PlayerInventory playerInventory, Chocobo chocoboEntity) {
        super(null, syncId);
        this.chocobo = chocoboEntity;
        this.playerInventory = playerInventory;
        refreshSlots(chocobo, playerInventory);
    }

    public Chocobo getChocobo() { return this.chocobo; }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        // The inventory is directly manipulated, so no special sync is needed on close.
        // The sorting is visual only.
    }

    @Override
    public void sendContentUpdates() {
        this.syncTimer++;
        if (this.syncTimer >= 15) { // 15 calls then trigger
            this.syncTimer = 0;
            if (this.chocobo.getWorld().isClient()) { return; } // No sync on the client side
            syncInventory(false); // Sync inventory to server
        }
    }
    public void syncInventory(boolean forceClose) {
        if (playerInventory.player.getWorld().isClient()) { return; } // No sync on the client side
        super.sendContentUpdates();
        if (forceClose) { ((ServerPlayerEntity)playerInventory.player).closeHandledScreen(); }
    }

    public void refreshSlots(@NotNull Chocobo chocobo, PlayerInventory playerInventory) {
        this.slots.clear();

        bindPlayerInventory(playerInventory);
        ItemStack saddleStack = chocobo.getSaddle();
        int slotOneX = -16;
        int slotOneY = 18-20;
        int gearSlotAdjustment = 9;
        this.addSlot(new ChocoboEquipmentSlot(chocobo, chocobo.chocoboGearInventory, HEAD_SLOT, slotOneX, slotOneY));
        this.addSlot(new ChocoboEquipmentSlot(chocobo, chocobo.chocoboGearInventory, ARMOR_SLOT, slotOneX, slotOneY+18));
        this.addSlot(new ChocoboEquipmentSlot(chocobo, chocobo.chocoboGearInventory, LEGS_SLOT, slotOneX, slotOneY+(2*18)));
        this.addSlot(new ChocoboEquipmentSlot(chocobo, chocobo.chocoboGearInventory, FEET_SLOT, slotOneX, slotOneY+(3*18)));
        this.addSlot(new ChocoboEquipmentSlot(chocobo, chocobo.chocoboGearInventory, WEAPON_SLOT, 18+gearSlotAdjustment+slotOneX, slotOneY));
        this.addSlot(new ChocoboEquipmentSlot(chocobo, chocobo.chocoboGearInventory, SADDLE_SLOT, (2*18)+(gearSlotAdjustment*2)+slotOneX, slotOneY));

        if(!saddleStack.isEmpty() && saddleStack.getItem() instanceof ChocoboSaddleItem saddleItem) {
            int saddleSize = saddleItem.getInventorySize();
            switch (saddleSize) {
                case 15 -> bindInventorySmall(chocobo.chocoboInventory);
                case 45 -> bindInventoryBig(chocobo.chocoboInventory);
            }
        }
    }
    private void bindInventorySmall(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                int guiSlotIndex = row * 5 + col;
                int backingSlotIndex;
                if (guiSlotIndex < 5) { backingSlotIndex = guiSlotIndex + 11; }
                else if (guiSlotIndex < 10) { backingSlotIndex = guiSlotIndex + 15;}
                else { backingSlotIndex = guiSlotIndex + 19; }
                this.addSlot(new Slot(inventory, backingSlotIndex, 44 + col * 18, 36 + row * 18));
            }
        }
    }
    private void bindInventoryBig(Inventory inventory) {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, row * 9 + col, 8 + col * 18, 18 + row * 18));
            }
        }
    }
    private void bindPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) { for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 122 + row * 18));
        } }
        for (int i = 0; i < 9; ++i) { this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 180)); }
    }
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        ItemStack saddleStack = chocobo.getSaddle();
        boolean notEmpty;
        int slotSize;
        if (!saddleStack.isEmpty() && saddleStack.getItem() instanceof ChocoboSaddleItem saddleItem) {
            slotSize = saddleItem.getInventorySize();
        } else {
            slotSize = 0;
        }
        notEmpty = !(slot instanceof ChocoboEquipmentSlot);
        if (notEmpty) {
            if (slot.hasStack()) {
                ItemStack itemstack1 = slot.getStack();
                itemstack = itemstack1.copy();

                if (index < slotSize) { if (!this.insertItem(itemstack1, slotSize, this.slots.size(), true)) { return ItemStack.EMPTY; } }
                else if (!this.insertItem(itemstack1, 0, slotSize, false)) { return ItemStack.EMPTY; }
                if (itemstack1.isEmpty()) { slot.setStack(ItemStack.EMPTY); }
                else { slot.markDirty(); }
            }
        }
        if (notEmpty) { return itemstack; } else { return ItemStack.EMPTY; }
    }
    public boolean canUse(PlayerEntity player) { return this.chocobo.isAlive() && this.chocobo.distanceTo(player) < 8.0F; }
}
