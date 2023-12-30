package com.dephoegon.delchoco.client.gui;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class RenderChocoboOverlay {
    private static final Identifier ICONS = new Identifier(DelChoco.DELCHOCO_ID, "textures/gui/icons.png");
    public static void onGuiInGameOverlayRender() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient minecraft = MinecraftClient.getInstance();
            assert minecraft.player != null;
            Entity mountedEntity = minecraft.player.getVehicle();
            if (!(mountedEntity instanceof Chocobo chocobo)) {
                return;
            }

            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, ICONS);

            final int width = minecraft.getWindow().getScaledWidth();
            final int height = minecraft.getWindow().getScaledHeight();
            int left_align = width / 2 + 91;
            int top = height - 39; //right_height = 39
            top -= (int) (Math.ceil(chocobo.getHealth() / 20) * 10); //Offset it based on the amount of health rendered
            float staminaPercentage = chocobo.getStaminaPercentage() * 10;

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
        });
    }
}