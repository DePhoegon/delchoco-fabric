package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.aid.world.StaticGlobalVariables;
import com.dephoegon.delchoco.common.entities.Chocobo;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.NotNull;

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
        if (entity == null) { return true; }
        if (entity.isTamed()) {
            boolean isPlayer = entity.isOwner(player);
            if (ChocoConfigGet(StaticGlobalVariables.getOwnerOnlyInventory(), dOwnerOnlyInventoryAccess)) { return isPlayer; } else { return true; }
        } else { return false; }
    }
    private @NotNull NbtCompound saveFirstItemStack() {
        NbtCompound tag = new NbtCompound();
        ItemStack stack = this.getStack(0);
        if(!stack.isEmpty()) {
            NbtCompound itemTag = new NbtCompound();
            stack.writeNbt(itemTag);
            tag.put("Item", itemTag);
        }
        return tag;
    }
    private void loadFirstItemStack(@NotNull NbtCompound tag) {
        if(tag.contains("Item", NbtType.COMPOUND)) {
            NbtCompound itemTag = tag.getCompound("Item");
            this.setStack(0, ItemStack.fromNbt(itemTag));
        }
    }
    public NbtCompound save() {
        if (this.size() > 1) { return this.saveAll(); }
        else { return this.saveFirstItemStack(); }
    }
    public void load(@NotNull NbtCompound tag) {
        if (this.size() > 1) { this.loadFirstItemStack(tag); }
        else { this.loadAll(tag); }
    }
    private @NotNull NbtCompound saveAll() {
        NbtCompound tag = new NbtCompound();
        NbtList list = new NbtList();
        for(int i = 0; i < this.size(); ++i) {
            ItemStack stack = this.getStack(i);
            if(!stack.isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putByte("Slot", (byte)i);
                stack.writeNbt(itemTag);
                list.add(itemTag);
            }
        }
        tag.put("Items", list);
        return tag;
    }
    private void loadAll(@NotNull NbtCompound tag) {
        NbtList list = tag.getList("Items", NbtType.COMPOUND);
        for(int i = 0; i < list.size(); ++i) {
            NbtCompound itemTag = list.getCompound(i);
            int j = itemTag.getByte("Slot") & 255;
            if(j < this.size()) {
                this.setStack(j, ItemStack.fromNbt(itemTag));
            }
        }
    }
}