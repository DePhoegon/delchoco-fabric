package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.common.inventory.ChocoboArmorStandScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class ChocoboArmorStandScreenHandlerFactory implements NamedScreenHandlerFactory {
    private final ChocoboArmorStand armorStand;

    public ChocoboArmorStandScreenHandlerFactory(@NotNull ChocoboArmorStand armorStand) { this.armorStand = armorStand; }
    public Text getDisplayName() { return armorStand.getDisplayName(); }
    public ScreenHandler createMenu(int syncId, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity player) { return new ChocoboArmorStandScreenHandler(syncId, playerInventory, armorStand); }
}