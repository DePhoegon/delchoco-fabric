package com.dephoegon.delchoco.common.items;

import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;

public class ChocoboSaddleItem extends Item {
    private final int inventorySize;
    private final boolean renderOutline;
    public ChocoboSaddleItem(@NotNull Settings settings, int inventorySize, boolean renderOutline) {
        super(settings);
        this.inventorySize = inventorySize;
        this.renderOutline = renderOutline;
    }
    public boolean hasOutline() { return this.renderOutline; }
    public int getInventorySize() { return inventorySize; }
}
