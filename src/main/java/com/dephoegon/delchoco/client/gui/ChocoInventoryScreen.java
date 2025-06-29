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
    private static final Identifier INV_EMPTY_SLOT_HELMET = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/slot/empty_armor_slot_helmet.png");
    private static final Identifier INV_EMPTY_SLOT_CHEST = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/slot/empty_armor_slot_chestplate.png");
    private static final Identifier INV_EMPTY_SLOT_LEGS = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/slot/empty_armor_slot_leggings.png");
    private static final Identifier INV_EMPTY_SLOT_FEET = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/slot/empty_armor_slot_boots.png");
    private static final Identifier INV_EMPTY_SLOT_SADDLE = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/slot/empty_saddle_slot.png");
    private static final Identifier INV_EMPTY_SLOT_WEAPON = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/slot/empty_weapon_slot.png");
    private static final int xAdjust = ((2*18) + (2*9) + 16) + 5; // x adjustment for the inventory title,
    // 2*18 for the first 2 slots, 2*9 for half-spaces between slots, +16 for the last slot, +5 for the right side of the last slot
    private static final int xPlacementGear = -24; // x placement for gear slots
    private static final int yPlacementGear = -10; // y placement for gear slots
    private static final int slotImageAdjustment = 8; // adjustment for the empty slot textures, used to align the empty slot textures with the gear slots
    private final Chocobo chocobo;

    public ChocoInventoryScreen(ChocoboScreenHandler handler, PlayerInventory inventory, @NotNull Chocobo chocobo) {
        super(handler, inventory, chocobo.getDisplayName());
        this.backgroundWidth = 176; // width of the main background texture
        this.backgroundHeight = 204; // height of the main background texture
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
        context.drawTexture(out, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight); // main background texture
        context.drawTexture(out, i + xPlacementGear, j + yPlacementGear, this.backgroundWidth, 0, 27, 86); // armor slots background (might need x/y adjustment)
        context.drawTexture(INV_EMPTY_SLOT_HELMET, i + xPlacementGear + slotImageAdjustment, j + yPlacementGear + slotImageAdjustment, 0, 0, 18, 18); // helmet slot
        context.drawTexture(INV_EMPTY_SLOT_CHEST, i + xPlacementGear + slotImageAdjustment, j + yPlacementGear + 18 + slotImageAdjustment, 0, 0, 18, 18); // chest slot
        context.drawTexture(INV_EMPTY_SLOT_LEGS, i + xPlacementGear + slotImageAdjustment, j + yPlacementGear + (2*18) + slotImageAdjustment, 0, 0, 18, 18); // legs slot
        context.drawTexture(INV_EMPTY_SLOT_FEET, i + xPlacementGear + slotImageAdjustment, j + yPlacementGear + (3*18) + slotImageAdjustment, 0, 0, 18, 18); // feet slot
        context.drawTexture(out, i + xPlacementGear + 27, j + yPlacementGear, 0, this.backgroundHeight, 59, 26); // saddle slots background (might need x/y adjustment)
        context.drawTexture(INV_EMPTY_SLOT_WEAPON, i + xPlacementGear + 27 + slotImageAdjustment, j + yPlacementGear + slotImageAdjustment, 0, 0, 18, 18); // Weapon slot
        context.drawTexture(INV_EMPTY_SLOT_SADDLE, i + xPlacementGear + 27 + slotImageAdjustment + (9 + 18), j + yPlacementGear + slotImageAdjustment, 0, 0, 18, 18); // Saddle slot
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