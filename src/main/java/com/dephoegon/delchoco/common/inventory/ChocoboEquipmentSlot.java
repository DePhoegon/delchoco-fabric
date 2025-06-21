package com.dephoegon.delchoco.common.inventory;

import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class ChocoboEquipmentSlot extends Slot {
    private final int type;
    public static final int SADDLE = 1;
    public static final int WEAPON = 2;
    public static final int HEAD = 3;
    public static final int CHEST = 4;
    public static final int LEGS = 5;
    public static final int FEET = 6;

    public ChocoboEquipmentSlot(Inventory inventory, int index, int xPosition, int yPosition, int type) {
        super(inventory, index, xPosition, yPosition);
        this.type = type;
    }

    public boolean canInsert(@NotNull ItemStack stack) {
        if (stack.isEmpty()) { return false; }

        return switch (type) {
            case SADDLE -> stack.getItem() instanceof ChocoboSaddleItem;
            case WEAPON -> stack.getItem() instanceof ChocoboWeaponItems;
            case HEAD -> stack.getItem() instanceof ChocoboArmorItems armor && armor.getSlotType() == EquipmentSlot.HEAD;
            case CHEST -> stack.getItem() instanceof ChocoboArmorItems armor && armor.getSlotType() == EquipmentSlot.CHEST;
            case LEGS -> stack.getItem() instanceof ChocoboArmorItems armor && armor.getSlotType() == EquipmentSlot.LEGS;
            case FEET -> stack.getItem() instanceof ChocoboArmorItems armor && armor.getSlotType() == EquipmentSlot.FEET;
            default -> false;
        };
    }
    public int getMaxItemCount() { return 1; }
}