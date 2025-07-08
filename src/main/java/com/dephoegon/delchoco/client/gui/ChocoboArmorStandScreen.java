package com.dephoegon.delchoco.client.gui;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.ChocoboArmorStand;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.inventory.ChocoboArmorStandScreenHandler;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ChocoboArmorStandScreen extends HandledScreen<ChocoboArmorStandScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/chocobo_armor_stand.png");
    private static final Identifier INV_TEXTURE_NULL = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/chocobo_inventory_null.png");
    private static final Identifier INV_TEXTURE_SMALL = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/chocobo_inventory_small.png");
    private static final Identifier INV_TEXTURE_LARGE = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/chocobo_inventory_large.png");
    private static final int xAdjust = ((2*18) + (2*9) + 16) + 5; // x adjustment for the inventory title,
    // 2*18 for the first 2 slots, 2*9 for half-spaces between slots, +16 for the last slot, +5 for the right side of the last slot
    private static final int xPlacementGear = -24; // x placement for gear slots
    private static final int yPlacementGear = -10; // y placement for gear slots
    private static final int slotImageAdjustment = 8; // adjustment for the empty slot textures, used to align the empty slot textures with the gear slots
    private final ChocoboArmorStand armorStand;

    public ChocoboArmorStandScreen(ChocoboArmorStandScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176; // width of the main background texture
        this.backgroundHeight = 204; // height of the main background texture
        this.armorStand = handler.getArmorStand();
    }

    @Override
    protected void drawBackground(@NotNull DrawContext context, float delta, int mouseX, int mouseY) {
        Identifier out;
        ItemStack saddleStack = armorStand.getSaddle();
        if(!saddleStack.isEmpty()){
            Item item = saddleStack.getItem();
            out = getChocoboSaddleInv(item);
        } else { out = INV_TEXTURE_NULL; }
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(out, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight); // main background texture
        context.drawTexture(out, i + xPlacementGear, j + yPlacementGear, this.backgroundWidth, 0, 27, 86); // armor slots background (might need x/y adjustment)
        context.drawTexture(out, i + xPlacementGear + 27, j + yPlacementGear, 0, this.backgroundHeight, 59, 26); // saddle slots background (might need x/y adjustment)
    }
    private Identifier getChocoboSaddleInv(Item saddleItem) {
        if (!(saddleItem instanceof ChocoboSaddleItem chocoboSaddleItem)) { return INV_TEXTURE_NULL; }
        int chocoboSaddle = chocoboSaddleItem.getInventorySize();
        switch (chocoboSaddle) {
            case 15 -> { return INV_TEXTURE_SMALL; }
            case 45 -> { return INV_TEXTURE_LARGE; }
        }
        return INV_TEXTURE_NULL;
    }
    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(@NotNull DrawContext context, int x, int y) {
        context.drawText(this.textRenderer, this.armorStand.getDisplayName().getString(), xAdjust-16, 6, 0x888888, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, 8, this.backgroundHeight - 96 + 2, 0x888888, false);
    }
}
