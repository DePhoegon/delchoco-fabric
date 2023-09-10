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
    public SaddlebagContainer(int syncId, PlayerInventory playerInventory, Chocobo chocoboEntity) {
        super(null, syncId);
        this.chocobo = chocoboEntity;
        refreshSlots(chocobo, playerInventory);
    }
    public void refreshSlots(@NotNull Chocobo chocobo, PlayerInventory playerInventory) {
        this.slots.clear();

        bindPlayerInventory(playerInventory);
        ItemStack saddleStack = chocobo.getSaddle();
        int slotOneX = -16;
        int slotOneY = 18-20;
        this.addSlot(new ChocoboEquipmentSlot(chocobo.chocoboSaddleInv, 0, slotOneX, slotOneY, saddleType));
        this.addSlot(new ChocoboEquipmentSlot(chocobo.chocoboArmorInv, 0, 2*18+slotOneX, slotOneY, armorType));
        this.addSlot(new ChocoboEquipmentSlot(chocobo.chocoboWeaponInv, 0, 4*18+slotOneX, slotOneY, weaponType));
        if(!saddleStack.isEmpty() && saddleStack.getItem() instanceof ChocoboSaddleItem saddleItem) {
            int saddleSize = saddleItem.getInventorySize();
            switch (saddleSize) {
                case 15 -> bindInventorySmall(saddleStack, chocobo.chocoboTierOneInv);
                case 45 -> bindInventoryBig(saddleStack, chocobo.chocoboTierTwoInv);
            }
        }
    }
    private void bindInventorySmall(@NotNull ItemStack saddle, Inventory inventory) {
        if (!(saddle.isEmpty())) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 5; col++) {
                    this.addSlot(new Slot(inventory, row * 5 + col, 44 + col * 18, 36 + row * 18));
                    if (row * 5 + col < 5) { inventory.setStack(row * 5 + col, chocobo.chocoboBackboneInv.getStack(row * 5 + col+11)); }
                    if (row * 5 + col > 4 && row * 5 + col < 10) { inventory.setStack(row * 5 + col, chocobo.chocoboBackboneInv.getStack(row * 5 + col+15)); }
                    if (row * 5 + col > 9) { inventory.setStack(row * 5 + col, chocobo.chocoboBackboneInv.getStack(row * 5 + col+19)); }
                }
            }
        }
    }
    private void bindInventoryBig(@NotNull ItemStack saddle, Inventory inventory) {
        if (!(saddle.isEmpty())) {
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 9; col++) {
                    this.addSlot(new Slot(inventory, row * 9 + col, 8 + col * 18, 18 + row * 18));
                    inventory.setStack(row * 9 + col, chocobo.chocoboBackboneInv.getStack(row * 9 + col));
                }
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
    public ItemStack transferSlot(PlayerEntity player, int index) {
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