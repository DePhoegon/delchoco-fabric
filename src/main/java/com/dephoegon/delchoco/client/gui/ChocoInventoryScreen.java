package com.dephoegon.delchoco.client.gui;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.inventory.ChocoboScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ChocoInventoryScreen extends HandledScreen<ChocoboScreenHandler> {
    private static final Identifier INV_TEXTURE_NULL = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/chocobo_inventory_null.png");
    private static final Identifier INV_TEXTURE_SMALL = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/chocobo_inventory_small.png");
    private static final Identifier INV_TEXTURE_LARGE = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/chocobo_inventory_large.png");
    private static final int xAdjust = (4*18+16)+5; // (Additional Sizes for slots) + Border buffer
    private final Chocobo chocobo;

    public ChocoInventoryScreen(ChocoboScreenHandler handler, PlayerInventory inventory, @NotNull Chocobo chocobo) {
        super(handler, inventory, chocobo.getDisplayName());
        this.backgroundWidth = 176;
        this.backgroundHeight = 204;
        this.chocobo = chocobo;
    }
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        Identifier out;
        ItemStack saddleStack = chocobo.getSaddle();
        if(!saddleStack.isEmpty()){
            Item item = saddleStack.getItem();
            out = getChocoboSaddleInv(item);
        } else { out = INV_TEXTURE_NULL; }
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(out, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        context.drawTexture(out, i - 24, j - 10, 0, 204, 27+xAdjust, 33);
    }
    private Identifier getChocoboSaddleInv(Item saddleItem) {
        if (saddleItem == ModItems.CHOCOBO_SADDLE_BAGS) { return INV_TEXTURE_SMALL; }
        if (saddleItem == ModItems.CHOCOBO_SADDLE_PACK) { return INV_TEXTURE_LARGE; }
        return INV_TEXTURE_NULL;
    }
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
    protected void drawForeground(@NotNull DrawContext context, int x, int y) {
        context.drawText(this.textRenderer, this.chocobo.getDisplayName().getString(), xAdjust-16, 6, 0x888888, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, 8, this.backgroundHeight - 96 + 2, 0x888888, false);
    }
}