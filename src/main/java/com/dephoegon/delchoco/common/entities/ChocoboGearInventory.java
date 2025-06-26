package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.dephoegon.delchoco.common.items.ChocoboWeaponItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

import static com.dephoegon.delchoco.common.inventory.ChocoboEquipmentSlot.*;

public class ChocoboGearInventory extends SimpleInventory {
    private final Chocobo chocobo;

    public ChocoboGearInventory(Chocobo chocobo) {
        super(6);
        this.chocobo = chocobo;
        this.addListener(sender -> onInventoryChanged());
    }

    private void onInventoryChanged() {
        if (!chocobo.getWorld().isClient()) {
            chocobo.setSaddle(getStack(SADDLE_SLOT));
            chocobo.setChestArmor(getStack(ARMOR_SLOT));
            chocobo.setWeapon(getStack(WEAPON_SLOT));
            chocobo.setHeadArmor(getStack(HEAD_SLOT));
            chocobo.setLegsArmor(getStack(LEGS_SLOT));
            chocobo.setFeetArmor(getStack(FEET_SLOT));
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (stack.isEmpty()) { return true; }
        return switch (slot) {
            case SADDLE_SLOT -> stack.getItem() instanceof ChocoboSaddleItem;
            case WEAPON_SLOT -> stack.getItem() instanceof ChocoboWeaponItems;
            case ARMOR_SLOT -> stack.getItem() instanceof ChocoboArmorItems && ((ChocoboArmorItems) stack.getItem()).getSlotType() == EquipmentSlot.CHEST;
            case HEAD_SLOT -> stack.getItem() instanceof ChocoboArmorItems && ((ChocoboArmorItems) stack.getItem()).getSlotType() == EquipmentSlot.HEAD;
            case LEGS_SLOT -> stack.getItem() instanceof ChocoboArmorItems && ((ChocoboArmorItems) stack.getItem()).getSlotType() == EquipmentSlot.LEGS;
            case FEET_SLOT -> stack.getItem() instanceof ChocoboArmorItems && ((ChocoboArmorItems) stack.getItem()).getSlotType() == EquipmentSlot.FEET;
            default -> false;
        };
    }
}

