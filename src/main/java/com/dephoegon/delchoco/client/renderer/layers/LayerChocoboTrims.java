package com.dephoegon.delchoco.client.renderer.layers;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class LayerChocoboTrims extends FeatureRenderer<Chocobo, EntityModel<Chocobo>> {
    private final float tamedShow;
    private final float untamedShow;
    private static Identifier getChocoboPlume(ChocoboColor color, boolean forTamedPlumes) {
        String plumeLocation = forTamedPlumes ? "tamed_plumes/tamed_" : "untamed_plumes/";
        return new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/" + plumeLocation + color.getColorName() + ".png");
    }
    private static final Identifier CHOCOBO_FLAME_EYES = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/flame/eyes.png");
    private static final Identifier CHICOBO_FLAME_EYES = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/flame/eyes.png");
    public LayerChocoboTrims(FeatureRendererContext<Chocobo, EntityModel<Chocobo>> rendererIn, float tamedAlpha, float untamedAlpha) {
        super(rendererIn);
        this.tamedShow = tamedAlpha;
        this.untamedShow = untamedAlpha;
    }

    // Use logic to use different Plumage on tames of different Colors
    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull Chocobo chocobo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!chocobo.isInvisible()) {
            if (!chocobo.isBaby()) {
                rSwitch(matrixStackIn, bufferIn, packedLightIn, chocobo, untamedShow, getChocoboPlume(chocobo.getChocoboColor(), false), true);
                if (chocobo.isTamed()) { rSwitch(matrixStackIn, bufferIn, packedLightIn, chocobo, tamedShow, getChocoboPlume(chocobo.getChocoboColor(), true), false); }
            }
            if (chocobo.isFireImmune()) { rSwitch(matrixStackIn, bufferIn, packedLightIn, chocobo, 1F, chocobo.isBaby() ? CHICOBO_FLAME_EYES : CHOCOBO_FLAME_EYES, false); }
        }
    }
    private void rSwitch(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull Chocobo chocobo, float alpha, Identifier layer, boolean outline) {
        // Add null check to prevent crash
        if (layer == null) {
            DelChoco.LOGGER.debug("Layer identifier is null for Chocobo: {} of color {}", chocobo.getUuidAsString(), chocobo.getChocoboColor().getColorName());
            return;
        }
        VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(layer, outline));
        this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlay(chocobo, 0F), 1F, 1F, 1F, alpha);
    }
}