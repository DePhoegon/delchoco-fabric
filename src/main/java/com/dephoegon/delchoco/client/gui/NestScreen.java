package com.dephoegon.delchoco.client.gui;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.inventory.NestContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class NestScreen extends HandledScreen<NestContainer> {
    private final static Identifier TEXTURE = new Identifier(DelChoco.Mod_ID, "textures/gui/chocobo_nest.png");
    private final static Identifier TEXTURE_SHELTERED = new Identifier(DelChoco.Mod_ID, "textures/gui/chocobo_nest_sheltered.png");
    public NestScreen(NestContainer handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
    }
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.getScreenHandler().getTile().isSheltered() ? TEXTURE_SHELTERED : TEXTURE);
        drawTexture(matrices, this.titleX, this.titleY, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
    protected void drawForeground(@NotNull MatrixStack matrixStack, int mouseX, int mouseY) { super.drawForeground(matrixStack, mouseX, mouseY); }
}