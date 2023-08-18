package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.aid.world.StaticGlobalVariables;
import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.ChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.dValues.defaultBooleans.dOwnerOnlyInventoryAccess;

public class ChocoboInventory extends SimpleInventory {
    private final Chocobo entity;

    public ChocoboInventory(int size, Chocobo entity) {
        super(size);
        this.entity = entity;
    }
    public Chocobo getChocobo() { return this.entity; }
    public boolean canPlayerUse(PlayerEntity player) {
        if (player == null) { return false; }
        if (entity.isTamed()) {
            boolean isPlayer = entity.isOwner(player);
            if (ChocoConfigGet(StaticGlobalVariables.getOwnerOnlyInventory(), dOwnerOnlyInventoryAccess)) { return isPlayer; } else { return true; }
        } else { return false; }
    }
}
