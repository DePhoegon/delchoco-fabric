package com.dephoegon.delchoco.common.inventory;

import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class ChocoboEquipmentSlot extends Slot {
    private boolean saddle = false;
    public static final int saddleType = 1;
    private boolean armor = false;
    public static final int armorType = 2;
    private boolean weapon = false;
    public static final int weaponType = 3;

    public ChocoboEquipmentSlot(Inventory inventory, int index, int xPosition, int yPosition, int type) {
        super(inventory, index, xPosition, yPosition);
        switch (type) {
            case saddleType -> this.saddle = true;
            case armorType -> this.armor = true;
            case weaponType -> this.weapon = true;
        }
    }
    public boolean canInsert(@NotNull ItemStack stack) {
        if (stack.isEmpty()) { return false; } else {
            if (stack.getItem() instanceof ChocoboArmorItems) { return this.armor; }
            if (stack.getItem() instanceof ChocoboSaddleItem) { return this.saddle; }
            if (stack.getItem() instanceof ChocoboWeaponItems) { return this.weapon; }
        }
        return false;
    }
    public int getMaxItemCount() { return 1; }
}