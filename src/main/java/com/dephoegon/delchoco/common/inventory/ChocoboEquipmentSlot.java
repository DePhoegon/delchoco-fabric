package com.dephoegon.delchoco.common.inventory;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class ChocoboEquipmentSlot extends Slot {
    public static final int SADDLE_SLOT = 0;
    public static final int ARMOR_SLOT = 1;
    public static final int WEAPON_SLOT = 2;
    public static final int HEAD_SLOT = 3;
    public static final int LEGS_SLOT = 4;
    public static final int FEET_SLOT = 5;

    private final int type;
    private final Chocobo chocobo;

    public ChocoboEquipmentSlot(Chocobo chocobo, Inventory inventory, int chocoboGearIndex, int x, int y) {
        super(inventory, chocoboGearIndex, x, y);
        this.chocobo = chocobo;
        this.type = chocoboGearIndex;
    }

    @Override
    public boolean canInsert(@NotNull ItemStack stack) {
        return isItemValid(stack);
    }
    public boolean isItemValid(@NotNull ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        return switch (type) {
            case SADDLE_SLOT -> stack.getItem() instanceof ChocoboSaddleItem;
            case WEAPON_SLOT -> stack.getItem() instanceof ChocoboWeaponItems;
            case ARMOR_SLOT -> stack.getItem() instanceof ChocoboArmorItems armor && armor.getSlotType() == EquipmentSlot.CHEST;
            case HEAD_SLOT -> stack.getItem() instanceof ChocoboArmorItems armor && armor.getSlotType() == EquipmentSlot.HEAD;
            case LEGS_SLOT -> stack.getItem() instanceof ChocoboArmorItems armor && armor.getSlotType() == EquipmentSlot.LEGS;
            case FEET_SLOT -> stack.getItem() instanceof ChocoboArmorItems armor && armor.getSlotType() == EquipmentSlot.FEET;
            default -> false;
        };
    }
    public int getType() { return type; }
    public int getMaxItemCount() {
        return 1;
    }
}