package com.dephoegon.delchoco.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.AliasedBlockItem;

public class CustomBlockNamedItem extends AliasedBlockItem {
    private final Block blockSupplier;
    public CustomBlockNamedItem(Block block, Settings settings) {
        super(null, settings);
        this.blockSupplier = block;
    }
    public Block getBlock() { return this.blockSupplier; }
}