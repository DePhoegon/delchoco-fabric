package com.dephoegon.delchoco.common.inventory;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.common.inventory.ChocoboEquipmentSlot.*;

public class SaddlebagContainer extends ScreenHandler {
    private final Chocobo chocobo;
    private int syncTimer = 0;

    public SaddlebagContainer(int syncId, PlayerInventory playerInventory, Chocobo chocoboEntity) {
        super(null, syncId);
        this.chocobo = chocoboEntity;
        refreshSlots(chocobo, playerInventory);
    }

    public Chocobo getChocobo() {
        return this.chocobo;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        // The inventory is directly manipulated, so no special sync is needed on close.
        // The sorting is visual only.
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        this.syncTimer++;
        if (this.syncTimer >= 200 + chocobo.getRandom().nextInt(100)) { // 10-15 seconds
            this.syncTimer = 0;
            // No explicit sync needed as we are directly using the chocobo's inventory.
            // If there were a temporary inventory, we would sync it here.
        }
    }

    public void refreshSlots(@NotNull Chocobo chocobo, PlayerInventory playerInventory) {
        this.slots.clear();

        bindPlayerInventory(playerInventory);
        ItemStack saddleStack = chocobo.getSaddle();
        int slotOneX = -16;
        int slotOneY = 18-20;
        this.addSlot(new ChocoboEquipmentSlot(chocobo.chocoboGearInventory, Chocobo.SADDLE_SLOT, slotOneX, slotOneY, SADDLE));
        this.addSlot(new ChocoboEquipmentSlot(chocobo.chocoboGearInventory, Chocobo.ARMOR_SLOT, 2*18+slotOneX, slotOneY, CHEST));
        this.addSlot(new ChocoboEquipmentSlot(chocobo.chocoboGearInventory, Chocobo.WEAPON_SLOT, 4*18+slotOneX, slotOneY, WEAPON));
        /*
        // Commented out as these slots are not used in the current implementation - The gear for them does not exist currently. I will uncomment this when the gear is added.
        this.addSlot(new ChocoboEquipmentSlot(chocobo.chocoboGearInventory, Chocobo.HEAD_SLOT, 6*18+slotOneX, slotOneY, HEAD));
        this.addSlot(new ChocoboEquipmentSlot(chocobo.chocoboGearInventory, Chocobo.LEGS_SLOT, 8*18+slotOneX, slotOneY, LEGS));
        this.addSlot(new ChocoboEquipmentSlot(chocobo.chocoboGearInventory, Chocobo.FEET_SLOT, 10*18+slotOneX, slotOneY, FEET));
        */
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
                if (guiSlotIndex < 5) {
                    backingSlotIndex = guiSlotIndex + 11;
                } else if (guiSlotIndex < 10) {
                    backingSlotIndex = guiSlotIndex + 15;
                } else {
                    backingSlotIndex = guiSlotIndex + 19;
                }
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