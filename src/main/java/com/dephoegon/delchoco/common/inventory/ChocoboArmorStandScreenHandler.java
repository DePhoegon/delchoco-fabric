package com.dephoegon.delchoco.common.inventory;

import com.dephoegon.delchoco.common.entities.ChocoboArmorStand;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class ChocoboArmorStandScreenHandler extends ScreenHandler {
    private final ChocoboArmorStand armorStand;
    private int storageRows;

    public ChocoboArmorStandScreenHandler(int syncId, PlayerInventory playerInventory, ChocoboArmorStand armorStand) {
        super(null, syncId); // behaves like the chocobo screen handler, does not need a specific type or registry
        this.armorStand = armorStand;
        refreshSlots(armorStand, playerInventory);
    }

    public void refreshSlots(@NotNull ChocoboArmorStand armorStand, PlayerInventory playerInventory) {
        this.slots.clear();

        bindPlayerInventory(playerInventory);
        int slotOneX = -16;
        int slotOneY = 18-20;
        int gearSlotAdjustment = 9;
        this.addSlot(new ChocoboArmorStandEquipmentSlot(armorStand, ChocoboEquipmentSlot.HEAD_SLOT, slotOneX, slotOneY));
        this.addSlot(new ChocoboArmorStandEquipmentSlot(armorStand, ChocoboEquipmentSlot.ARMOR_SLOT, slotOneX, slotOneY+18));
        this.addSlot(new ChocoboArmorStandEquipmentSlot(armorStand, ChocoboEquipmentSlot.LEGS_SLOT, slotOneX, slotOneY+(2*18)));
        this.addSlot(new ChocoboArmorStandEquipmentSlot(armorStand, ChocoboEquipmentSlot.FEET_SLOT, slotOneX, slotOneY+(3*18)));
        this.addSlot(new ChocoboArmorStandEquipmentSlot(armorStand, ChocoboEquipmentSlot.WEAPON_SLOT, 18+gearSlotAdjustment+slotOneX, slotOneY));
        this.addSlot(new ChocoboArmorStandEquipmentSlot(armorStand, ChocoboEquipmentSlot.SADDLE_SLOT, (2*18)+(gearSlotAdjustment*2)+slotOneX, slotOneY));

        int saddleSize = armorStand.getInventorySize();
        switch (saddleSize) {
            case 15 -> bindInventorySmall(armorStand.inventory);
            case 45 -> bindInventoryBig(armorStand.inventory);
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
    public ItemStack quickMove(@NotNull PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        ItemStack saddleStack = armorStand.getSaddle();
        boolean notEmpty;
        int slotSize;
        if (!saddleStack.isEmpty() && saddleStack.getItem() instanceof ChocoboSaddleItem saddleItem) {
            slotSize = saddleItem.getInventorySize();
        } else {
            slotSize = 0;
        }
        notEmpty = !(slot instanceof ChocoboArmorStandEquipmentSlot);
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

    @Override
    public boolean canUse(@NotNull PlayerEntity player) {
        return !armorStand.isRemoved() && player.squaredDistanceTo(armorStand) <= 64.0;
    }

    @Override
    public void onClosed(@NotNull PlayerEntity player) {
        super.onClosed(player);
    }

    public ChocoboArmorStand getArmorStand() {
        return armorStand;
    }

    public int getStorageRows() {
        return storageRows;
    }

    // Simple wrapper inventory for equipment to interface with the screen handler
    private static class ArmorStandGearInventory implements Inventory {
        private final ChocoboArmorStand armorStand;

        public ArmorStandGearInventory(ChocoboArmorStand armorStand) {
            this.armorStand = armorStand;
        }

        @Override
        public int size() {
            return 6;
        }

        @Override
        public boolean isEmpty() {
            for (int i = 0; i < 6; i++) {
                if (!armorStand.getEquipment(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getStack(int slot) {
            return armorStand.getEquipment(slot);
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            ItemStack stack = armorStand.getEquipment(slot);
            if (!stack.isEmpty()) {
                ItemStack removed = stack.split(amount);
                armorStand.setEquipment(slot, stack);
                return removed;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot) {
            ItemStack stack = armorStand.getEquipment(slot);
            armorStand.setEquipment(slot, ItemStack.EMPTY);
            return stack;
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            armorStand.setEquipment(slot, stack);
        }

        @Override
        public void markDirty() {
            // Mark the armor stand as dirty
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return !armorStand.isRemoved() && player.squaredDistanceTo(armorStand) <= 64.0;
        }

        @Override
        public void clear() {
            for (int i = 0; i < 6; i++) {
                armorStand.setEquipment(i, ItemStack.EMPTY);
            }
        }
    }
}
