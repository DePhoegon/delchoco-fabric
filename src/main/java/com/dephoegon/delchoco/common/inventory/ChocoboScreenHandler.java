package com.dephoegon.delchoco.common.inventory;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
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
    private static final int chocoboInventoryStart = 6; // Chocobo inventory starts at index 6
    private int inventorySize = 0;
    private int playerInventorySize = 0;
    private int playerHotbarSize = 0;

    public ChocoboScreenHandler(int syncId, PlayerInventory playerInventory, Chocobo chocoboEntity) {
        super(null, syncId);
        this.chocobo = chocoboEntity;
        this.playerInventory = playerInventory;
        refreshSlots(chocobo, playerInventory);
    }

    public Chocobo getChocobo() { return this.chocobo; }

    @Override
    public void onClosed(PlayerEntity player) { super.onClosed(player); }

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
        this.inventorySize = 0;
        this.playerInventorySize = 0;
        this.playerHotbarSize = 0;

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
        bindPlayerInventory(playerInventory);
    }
    private void bindInventorySmall(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                int guiSlotIndex = row * 5 + col;
                int backingSlotIndex;
                if (guiSlotIndex < 5) { backingSlotIndex = guiSlotIndex + 11; }
                else if (guiSlotIndex < 10) { backingSlotIndex = guiSlotIndex + 15;}
                else { backingSlotIndex = guiSlotIndex + 19; }
                this.addSlot(new ChocoboInventorySlot(inventory, backingSlotIndex, 44 + col * 18, 36 + row * 18));
                this.inventorySize += 1;
            }
        }
    }
    private void bindInventoryBig(Inventory inventory) {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new ChocoboInventorySlot(inventory, row * 9 + col, 8 + col * 18, 18 + row * 18));
                this.inventorySize += 1;
            }
        }
    }
    private void bindPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new PlayerInventorySlot(playerInventory, col + row * 9 + 9, 8 + col * 18, 122 + row * 18));
                this.playerInventorySize += 1;
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new PlayerHotbarSlot(playerInventory, i, 8 + i * 18, 180));
            playerHotbarSize += 1;
        }
    }
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasStack()) return ItemStack.EMPTY;

        ItemStack stackInSlot = slot.getStack();
        ItemStack itemStackCopy = stackInSlot.copy();

        boolean moved = false;

        if (slot instanceof ChocoboEquipmentSlot) {
            if (((ChocoboEquipmentSlot) slot).getType() == SADDLE_SLOT) { return ItemStack.EMPTY; }
            if (quickMoveChocoInventory(slot, stackInSlot)) { moved = true; }
            else if (quickMovePlayerInventory(slot, stackInSlot)) { moved = true; }
            else if (quickMovePlayerHotbar(slot, stackInSlot)) { moved = true; }
        }
        if (this.inventorySize > 0 && slot instanceof ChocoboInventorySlot) {
            if (quickMoveChocoGear(slot, stackInSlot)) { moved = true; }
            else if (quickMovePlayerInventory(slot, stackInSlot)) { moved = true; }
            else if (quickMovePlayerHotbar(slot, stackInSlot)) { moved = true; }
        }
        if (slot instanceof PlayerInventorySlot) {
            if (quickMoveChocoGear(slot, stackInSlot)) { moved = true; }
            else if (quickMoveChocoInventory(slot, stackInSlot)) { moved = true; }
            else if (quickMovePlayerHotbar(slot, stackInSlot)) { moved = true; }
        }
        if (slot instanceof PlayerHotbarSlot) {
            if (quickMoveChocoGear(slot, stackInSlot)) { moved = true; }
            else if (quickMoveChocoInventory(slot, stackInSlot)) { moved = true; }
            else if (quickMovePlayerInventory(slot, stackInSlot)) { moved = true; }
        }
        if (stackInSlot.isEmpty()) { slot.setStack(ItemStack.EMPTY); }
        else { slot.markDirty(); }
        return moved ? itemStackCopy : ItemStack.EMPTY;
    }
    // Quick move methods for different slot types
    private boolean quickMoveChocoGear(Slot slot, ItemStack stackInSlot) {
        boolean moved = false;
        if (stackInSlot.isEmpty()) { return moved; }
        // Only process Chocobo gear items
        boolean isChocoboGear = stackInSlot.getItem() instanceof ChocoboArmorItems || stackInSlot.getItem() instanceof ChocoboWeaponItems || stackInSlot.getItem() instanceof ChocoboSaddleItem;
        if (isChocoboGear) {
            for (int i = 0; i < chocoboInventoryStart; i++) {
                Slot equipmentSlot = this.slots.get(i);
                if (equipmentSlot instanceof ChocoboEquipmentSlot) {
                    // Should be meaningless check, but just in case
                    if (((ChocoboEquipmentSlot) equipmentSlot).isItemValid(stackInSlot)) {
                        if (equipmentSlot.getStack().isEmpty()) {
                            equipmentSlot.setStack(stackInSlot.split(1));
                            equipmentSlot.markDirty();
                            moved = true;
                        }
                        break;
                    } else { continue; }
                } else { DelChoco.LOGGER.warn("Unexpected slot type at index {}: {}", i, equipmentSlot.getClass().getName()); }
            }
            if (moved) { slot.markDirty(); }
        }
        return moved;

    }
    private boolean quickMoveChocoInventory(Slot slot, ItemStack stackInSlot) {
        boolean moved = false;
        if (stackInSlot.isEmpty()) { return moved; }
        if (this.inventorySize <= 0) { return moved; }

        int startIndex = chocoboInventoryStart;
        int endIndex = startIndex + this.inventorySize;
        if (this.insertItem(stackInSlot, startIndex, endIndex, false)) {
            slot.markDirty();
            moved = true;
        }

        if (moved) { slot.markDirty(); }
        return moved;
    }
    private boolean quickMovePlayerInventory(Slot slot, ItemStack stackInSlot) {
        boolean moved = false;
        if (stackInSlot.isEmpty()) { return moved; }
        if (this.playerInventorySize <= 0) { return moved; }

        int startIndex = chocoboInventoryStart + this.inventorySize;
        int endIndex = startIndex + this.playerInventorySize;
        if (this.insertItem(stackInSlot, startIndex, endIndex, false)) {
            slot.markDirty();
            moved = true;
        }

        if (moved) { slot.markDirty(); }
        return moved;
    }
    private boolean quickMovePlayerHotbar(Slot slot, ItemStack stackInSlot) {
        boolean moved = false;
        if (stackInSlot.isEmpty()) { return moved; }
        if (this.playerInventorySize <= 0) { return moved; }

        int startIndex = chocoboInventoryStart + this.inventorySize + this.playerInventorySize;
        int endIndex = startIndex + playerHotbarSize;
        if (this.insertItem(stackInSlot, startIndex, endIndex, false)) {
            slot.markDirty();
            moved = true;
        }

        if (moved) { slot.markDirty(); }
        return moved;
    }

    public boolean canUse(PlayerEntity player) { return this.chocobo.isAlive() && this.chocobo.distanceTo(player) < 8.0F; }
}