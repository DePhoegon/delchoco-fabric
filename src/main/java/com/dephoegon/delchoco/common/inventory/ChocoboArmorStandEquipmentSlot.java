package com.dephoegon.delchoco.common.inventory;

import com.dephoegon.delchoco.common.entities.ChocoboArmorStand;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class ChocoboArmorStandEquipmentSlot extends Slot {
    private final ChocoboArmorStand armorStand;
    private final int equipmentType;

    public ChocoboArmorStandEquipmentSlot(ChocoboArmorStand armorStand, int equipmentType, int x, int y) {
        super(new DummyInventory(), equipmentType, x, y);
        this.armorStand = armorStand;
        this.equipmentType = equipmentType;
    }

    @Override
    public boolean canInsert(@NotNull ItemStack stack) {
        return armorStand.isValidEquipment(equipmentType, stack);
    }

    @Override
    public ItemStack getStack() {
        return armorStand.getEquipment(equipmentType);
    }

    @Override
    public void setStack(@NotNull ItemStack stack) {
        armorStand.setEquipment(equipmentType, stack);
        this.markDirty();
    }

    @Override
    public ItemStack takeStack(int amount) {
        ItemStack current = getStack();
        if (current.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack taken = current.split(amount);
        setStack(current);
        return taken;
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }

    @Override
    public boolean hasStack() {
        return !getStack().isEmpty();
    }

    // Dummy inventory implementation since we handle storage directly through the armor stand
    private static class DummyInventory implements Inventory {
        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public ItemStack getStack(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            // No-op
        }

        @Override
        public void markDirty() {
            // No-op
        }

        @Override
        public boolean canPlayerUse(net.minecraft.entity.player.PlayerEntity player) {
            return true;
        }

        @Override
        public void clear() {
            // No-op
        }
    }
}
