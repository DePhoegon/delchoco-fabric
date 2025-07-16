package com.dephoegon.delchoco.client.gui;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class RenderChocoboOverlay {
    private static final Identifier ICONS = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/icons.png");
    private static final Identifier HEART_ICONS = new Identifier("textures/gui/icons.png"); // Vanilla heart texture
    private static final int MAX_HEART_ROWS = 3; // Maximum number of heart rows to display
    private static final int HEARTS_PER_ROW = 10; // Hearts per row
    private static final int HEART_COLLAPSE_THRESHOLD = 60; // Above 60 HP (3 rows), show as number

    public static void onGuiInGameOverlayRender() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient minecraft = MinecraftClient.getInstance();
            assert minecraft.player != null;
            Entity mountedEntity = minecraft.player.getVehicle();
            if (!(mountedEntity instanceof Chocobo chocobo)) {
                return;
            }

            final int width = minecraft.getWindow().getScaledWidth();
            final int height = minecraft.getWindow().getScaledHeight();

            // Render mount health
            renderMountHealth(matrixStack, minecraft, chocobo, width, height);

            // Render stamina bar
            renderStaminaBar(matrixStack, chocobo, width, height);
        });
    }

    private static void renderMountHealth(net.minecraft.client.gui.DrawContext matrixStack, MinecraftClient minecraft, Chocobo chocobo, int width, int height) {
        float maxHealth = chocobo.getMaxHealth();
        float currentHealth = chocobo.getHealth();

        // If health is above threshold, display as number instead of hearts
        if (maxHealth > HEART_COLLAPSE_THRESHOLD) {
            renderHealthAsNumber(matrixStack, minecraft, currentHealth, maxHealth, width, height);
        } else {
            renderHealthAsHearts(matrixStack, currentHealth, maxHealth, width, height);
        }
    }

    private static void renderHealthAsNumber(net.minecraft.client.gui.DrawContext matrixStack, MinecraftClient minecraft, float currentHealth, float maxHealth, int width, int height) {
        TextRenderer fontRenderer = minecraft.textRenderer;
        String healthText = String.format("%.0f/%.0f", currentHealth, maxHealth);

        // Position the text in the top-left area above the hotbar
        int x = width / 2 - 91; // Left align with hotbar
        int y = height - 49; // Above hotbar

        // Draw background for better readability
        matrixStack.fill(x - 2, y - 2, x + fontRenderer.getWidth(healthText) + 2, y + fontRenderer.fontHeight + 2, 0x80000000);

        // Draw health text in red color
        matrixStack.drawText(fontRenderer, healthText, x, y, 0xFF5555, false);

        // Draw a small heart icon next to the text
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, HEART_ICONS);
        matrixStack.drawTexture(HEART_ICONS, x + fontRenderer.getWidth(healthText) + 4, y, 52, 0, 9, 9, 256, 256);
    }

    private static void renderHealthAsHearts(net.minecraft.client.gui.DrawContext matrixStack, float currentHealth, float maxHealth, int width, int height) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, HEART_ICONS);

        int totalHearts = (int) Math.ceil(maxHealth / 2.0f);
        int filledHearts = (int) Math.ceil(currentHealth / 2.0f);
        boolean halfHeart = (currentHealth % 2.0f) > 0 && (currentHealth % 2.0f) < 2.0f;

        // Calculate rows needed (max 3 rows)
        int rows = Math.min(MAX_HEART_ROWS, (int) Math.ceil(totalHearts / (float) HEARTS_PER_ROW));

        // Starting position (right side, above hotbar)
        int startX = width / 2 + 91;
        int startY = height - 39;

        // Offset based on number of rows
        startY -= (rows - 1) * 10;

        for (int row = 0; row < rows; row++) {
            int heartsInThisRow = Math.min(HEARTS_PER_ROW, totalHearts - (row * HEARTS_PER_ROW));

            for (int i = 0; i < heartsInThisRow; i++) {
                int heartIndex = (row * HEARTS_PER_ROW) + i;
                int x = startX - (i * 8) - 9;
                int y = startY + (row * 10);

                // Draw heart background (empty heart)
                matrixStack.drawTexture(HEART_ICONS, x, y, 16, 0, 9, 9, 256, 256);

                // Draw heart fill based on health
                if (heartIndex < filledHearts) {
                    // Full heart
                    matrixStack.drawTexture(HEART_ICONS, x, y, 52, 0, 9, 9, 256, 256);
                } else if (heartIndex == filledHearts && halfHeart) {
                    // Half heart
                    matrixStack.drawTexture(HEART_ICONS, x, y, 61, 0, 9, 9, 256, 256);
                }
            }
        }
    }

    private static void renderStaminaBar(net.minecraft.client.gui.DrawContext matrixStack, Chocobo chocobo, int width, int height) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, ICONS);

        int left_align = width / 2 + 91;
        int top = height - 39; //right_height = 39

        // Adjust position based on health display
        if (chocobo.getMaxHealth() <= HEART_COLLAPSE_THRESHOLD) {
            // If using heart display, offset by number of heart rows
            int heartRows = Math.min(MAX_HEART_ROWS, (int) Math.ceil(chocobo.getMaxHealth() / 2.0f / HEARTS_PER_ROW));
            top -= heartRows * 10;
        } else {
            // If using number display, offset by text height
            top -= 12;
        }

        float staminaPercentage = 0 * 10; // TODO: Implement stamina system

        for (int i = 0; i < 10; ++i) {
            int x = left_align - i * 8 - 9;
            if (i >= staminaPercentage) {
                // render empty
                matrixStack.drawTexture(ICONS, x, top, 0, 0, 9, 9, 32, 32);
            } else {
                if (i == ((int) staminaPercentage)) {
                    // draw partial
                    matrixStack.drawTexture(ICONS, x, top, 0, 0, 9, 9, 32, 32);
                    int iconHeight = (int) (9 * (staminaPercentage - ((int) staminaPercentage)));
                    matrixStack.drawTexture(ICONS, x, top + (9 - iconHeight), 0, 18 + (9 - iconHeight), 9, iconHeight, 32, 32);
                } else {
                    // draw full
                    matrixStack.drawTexture(ICONS, x, top, 0, 18, 9, 9, 32, 32);
                }
            }
        }
    }
}