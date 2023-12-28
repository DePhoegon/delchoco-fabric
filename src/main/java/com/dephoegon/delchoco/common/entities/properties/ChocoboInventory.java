package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.common.entities.Chocobo;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

public class ChocoboInventory extends SimpleInventory {
    protected DefaultedList<ItemStack> stacks;
    private final Chocobo entity;

    public ChocoboInventory(int size, Chocobo entity) {
        super(size);
        this.entity = entity;
        this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }
    public Chocobo getChocobo() { return this.entity; }
    public boolean canPlayerUse(PlayerEntity player) {
        if (player == null) { return false; }
        if (entity == null) { return true; }
        if (entity.isTamed()) {
            boolean isPlayer = entity.isOwner(player);
            if (ChocoboConfig.OWNER_ONLY_ACCESS.get()) { return isPlayer; } else { return true; }
        } else { return false; }
    }
    public NbtCompound singleSlotToNBT() {
        NbtCompound nbt = new NbtCompound();
        this.getStack(0).writeNbt(nbt);
        return nbt;
    }
    public void singleSlotFromNBT(NbtCompound nbt) {
        if (nbt == null || ItemStack.fromNbt(nbt) == this.getStack(0)) { return; }
        this.setStack(0, ItemStack.fromNbt(nbt));
    }
    public NbtCompound serializeNBT() {
        NbtList nbtTagList = new NbtList();
        for (int i = 0; i < stacks.size(); i++) {
            if (!this.getStack(i).isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                this.getStack(i).writeNbt(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        NbtCompound nbt = new NbtCompound();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", stacks.size());
        return nbt;
    }
    public void deserializeNBT(@NotNull NbtCompound nbt) {
        setSize(nbt.contains("Size", NbtType.COMPOUND) ? nbt.getInt("Size") : this.size());
        NbtList tagList = nbt.getList("Items", NbtType.COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            NbtCompound itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (!this.getStack(slot).isEmpty() && ItemStack.fromNbt(itemTags).isEmpty()) { continue; }
            if (slot >= 0 && slot < stacks.size()) {
                this.setStack(slot, ItemStack.fromNbt(itemTags));
            }
        }
    }
    public void setSize(int size) { stacks = DefaultedList.ofSize(size, ItemStack.EMPTY); }
}