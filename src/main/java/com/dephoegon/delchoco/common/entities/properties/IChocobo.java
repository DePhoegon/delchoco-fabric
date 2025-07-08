package com.dephoegon.delchoco.common.entities.properties;

import net.minecraft.item.ItemStack;

public interface IChocobo {
    boolean isBaby();
    boolean isTamed();
    boolean isMale();
    int getCollarColor();
    ChocoboColor getChocoboColor();
    boolean isInvisible();
    boolean isFireImmune();
    boolean isArmored();
    boolean isChestArmored();
    boolean isHeadArmored();
    boolean isLegsArmored();
    boolean isFeetArmored();
    ItemStack getChestArmor();
    ItemStack getHeadArmor();
    ItemStack getLegsArmor();
    ItemStack getFeetArmor();
    boolean isSaddled();
    ItemStack getSaddle();
    boolean isWeaponArmed();
    ItemStack getWeapon();
}