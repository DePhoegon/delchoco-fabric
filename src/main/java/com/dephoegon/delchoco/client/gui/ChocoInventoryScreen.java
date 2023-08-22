package com.dephoegon.delchoco.client.gui;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.inventory.SaddlebagContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ChocoInventoryScreen extends HandledScreen<SaddlebagContainer> {
    private static final Identifier INV_TEXTURE_NULL = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/chocobo_inventory_null.png");
    private static final Identifier INV_TEXTURE_SMALL = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/chocobo_inventory_small.png");
    private static final Identifier INV_TEXTURE_LARGE = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/chocobo_inventory_large.png");
    private static final int xAdjust = (4*18+16)+5; // (Additional Sizes for slots) + Border buffer
    private final Chocobo chocobo;

    public ChocoInventoryScreen(SaddlebagContainer handler, PlayerInventory inventory, @NotNull Chocobo chocobo) {
        super(handler, inventory, chocobo.getDisplayName());
        this.backgroundWidth = 176;
        this.backgroundHeight = 204;
        this.chocobo = chocobo;
    }
    public static void openInventory(int windowId, Chocobo chocobo) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        SaddlebagContainer saddleContainer = new SaddlebagContainer(windowId, player.getInventory(), chocobo);
        player.currentScreenHandler = saddleContainer;
        MinecraftClient.getInstance().setScreen(new ChocoInventoryScreen(saddleContainer, player.getInventory(), chocobo));
    }
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        ItemStack saddleStack = chocobo.getSaddle();
        if(!saddleStack.isEmpty()){
            Item item = saddleStack.getItem();
            if(item == ModItems.CHOCOBO_SADDLE) { RenderSystem.setShaderTexture(0, INV_TEXTURE_NULL); }
            else if(item == ModItems.CHOCOBO_SADDLE_BAGS) { RenderSystem.setShaderTexture(0, INV_TEXTURE_SMALL); }
            else if(item == ModItems.CHOCOBO_SADDLE_PACK) { RenderSystem.setShaderTexture(0, INV_TEXTURE_LARGE); }
        } else { RenderSystem.setShaderTexture(0, INV_TEXTURE_NULL); }
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawTexture(matrices, i - 24, j - 10, 0, 204, 27+xAdjust, 33);
    }
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
    protected void drawForeground(@NotNull MatrixStack matrixStack, int x, int y) {
        this.textRenderer.draw(matrixStack, this.chocobo.getDisplayName().getString(), xAdjust-16, 6, 0x888888);
        this.textRenderer.draw(matrixStack, this.playerInventoryTitle, 8, this.backgroundHeight - 96 + 2, 0x888888);
    }
}