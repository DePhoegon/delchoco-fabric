package com.dephoegon.delchoco.common.items;

import com.dephoegon.delchoco.common.blocks.ChocoboEggBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.DelChoco.chocoConfigHolder;

public class ChocoboEggBlockItem extends BlockItem {
    public ChocoboEggBlockItem(Block block, Item.Settings builder) { super(block, builder); }
    public boolean isItemBarVisible(@NotNull ItemStack stack) {
        if (ChocoboEggBlock.isNotChocoboEgg(stack)) { return super.isItemBarVisible(stack); }
        if (!stack.hasNbt()) { return false; }
        NbtCompound nbtHatchingState = stack.getSubNbt(ChocoboEggBlock.NBTKEY_HATCHINGSTATE);
        return nbtHatchingState != null;
    }
    public int getItemBarStep(@NotNull ItemStack stack) {
        if (ChocoboEggBlock.isNotChocoboEgg(stack)) { return super.getItemBarStep(stack); }
        if (!stack.hasNbt()) { return 0; }

        NbtCompound nbtHatchingState = stack.getSubNbt(ChocoboEggBlock.NBTKEY_HATCHINGSTATE);
        if (nbtHatchingState != null) {
            int time = nbtHatchingState.getInt(ChocoboEggBlock.NBTKEY_HATCHINGSTATE_TIME);
            return Math.round(time * 13.0F / chocoConfigHolder.chocoboEggHatchTime);
        }
        return 1;
    }
    public int getItemBarColor(@NotNull ItemStack stack) {
        if (ChocoboEggBlock.isNotChocoboEgg(stack)) { return super.getItemBarColor(stack); }
        return 0x0000FF00;
    }
}