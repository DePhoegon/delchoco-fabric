package com.dephoegon.delchoco.common.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class PlayerInventorySlot extends Slot {
    // This class is used to represent a slot in the player's inventory.
    public PlayerInventorySlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }
}