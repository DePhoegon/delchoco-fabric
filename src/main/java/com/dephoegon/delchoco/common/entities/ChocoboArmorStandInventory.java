package com.dephoegon.delchoco.common.entities;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class ChocoboArmorStandInventory extends SimpleInventory {
    private final ChocoboArmorStand armorStand;

    // Number of gear slots (saddle, chest, head, legs, feet, weapon)
    private static final int GEAR_SLOT_COUNT = 6;

    // Maximum inventory size is the largest possible saddle inventory plus gear slots
    private static final int MAX_INVENTORY_SIZE = AbstractChocobo.top_tier_chocobo_inv_slot_count + GEAR_SLOT_COUNT;

    public ChocoboArmorStandInventory(ChocoboArmorStand armorStand) {
        super(MAX_INVENTORY_SIZE);
        this.armorStand = armorStand;
        this.addListener(sender -> onInventoryChanged());
    }

    private void onInventoryChanged() {
        // Mark the armor stand as dirty when inventory changes
        if (armorStand != null && !armorStand.getWorld().isClient()) { armorStand.markDirty(); }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        // Check if the slot is accessible based on saddle bags
        if (slot >= armorStand.getInventorySize()) {
            return false;
        }

        // Additional validation can be added here if needed
        return super.isValid(slot, stack);
    }

    @Override
    public ItemStack getStack(int slot) {
        // Return empty if slot is not accessible
        if (slot >= armorStand.getInventorySize()) {
            return ItemStack.EMPTY;
        }
        return super.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        // Prevent removal if slot is not accessible
        if (slot >= armorStand.getInventorySize()) {
            return ItemStack.EMPTY;
        }
        return super.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        // Prevent removal if slot is not accessible
        if (slot >= armorStand.getInventorySize()) {
            return ItemStack.EMPTY;
        }
        return super.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        // Prevent setting if slot is not accessible
        if (slot >= armorStand.getInventorySize()) {
            return;
        }
        super.setStack(slot, stack);
    }

    // Get actual accessible size
    public int getAccessibleSize() {
        return armorStand.getInventorySize();
    }

    // Custom NBT methods for saving/loading
    public NbtList toNbtList() {
        NbtList nbtList = new NbtList();
        int accessibleSize = getAccessibleSize();

        for (int i = 0; i < accessibleSize; i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                NbtCompound stackNbt = new NbtCompound();
                stackNbt.putByte("Slot", (byte) i);
                stack.writeNbt(stackNbt);
                nbtList.add(stackNbt);
            }
        }

        return nbtList;
    }

    public void readNbtList(NbtList nbtList) {
        clear();

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound stackNbt = nbtList.getCompound(i);
            int slot = stackNbt.getByte("Slot") & 255;

            if (slot < size()) {
                setStack(slot, ItemStack.fromNbt(stackNbt));
            }
        }
    }
}
