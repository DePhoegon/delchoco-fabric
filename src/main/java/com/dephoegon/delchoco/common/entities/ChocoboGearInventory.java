package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class ChocoboGearInventory extends SimpleInventory {
    private final Chocobo chocobo;

    public ChocoboGearInventory(Chocobo chocobo) {
        super(6);
        this.chocobo = chocobo;
        this.addListener(sender -> onInventoryChanged());
    }

    private void onInventoryChanged() {
        if (!chocobo.getWorld().isClient()) {
            chocobo.setSaddle(getStack(Chocobo.SADDLE_SLOT));
            chocobo.setArmor(getStack(Chocobo.ARMOR_SLOT));
            chocobo.setWeapon(getStack(Chocobo.WEAPON_SLOT));
            chocobo.setHeadArmor(getStack(Chocobo.HEAD_SLOT));
            chocobo.setLegsArmor(getStack(Chocobo.LEGS_SLOT));
            chocobo.setFeetArmor(getStack(Chocobo.FEET_SLOT));
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        return switch (slot) {
            case Chocobo.SADDLE_SLOT -> stack.getItem() instanceof ChocoboSaddleItem;
            case Chocobo.WEAPON_SLOT -> stack.getItem() instanceof ChocoboWeaponItems;
            case Chocobo.ARMOR_SLOT -> stack.getItem() instanceof ChocoboArmorItems && ((ChocoboArmorItems) stack.getItem()).getSlotType() == EquipmentSlot.CHEST;
            case Chocobo.HEAD_SLOT -> stack.getItem() instanceof ChocoboArmorItems && ((ChocoboArmorItems) stack.getItem()).getSlotType() == EquipmentSlot.HEAD;
            case Chocobo.LEGS_SLOT -> stack.getItem() instanceof ChocoboArmorItems && ((ChocoboArmorItems) stack.getItem()).getSlotType() == EquipmentSlot.LEGS;
            case Chocobo.FEET_SLOT -> stack.getItem() instanceof ChocoboArmorItems && ((ChocoboArmorItems) stack.getItem()).getSlotType() == EquipmentSlot.FEET;
            default -> false;
        };
    }
}

