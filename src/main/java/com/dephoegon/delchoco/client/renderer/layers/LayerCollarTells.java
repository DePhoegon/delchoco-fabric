package com.dephoegon.delchoco.client.renderer.layers;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
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

public class LayerCollarTells extends FeatureRenderer<Chocobo, EntityModel<Chocobo>> {
    private final float hide;
    private final float show;
    private final Identifier FEMALE_CHOCOBO = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_tell.png");
    private final Identifier MALE_CHOCOBO = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_tell.png");
    private final Identifier MALE_CHICOBO = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/m_tell.png");
    private final Identifier FEMALE_CHICOBO = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/f_tell.png");

    public LayerCollarTells(FeatureRendererContext<Chocobo, EntityModel<Chocobo>> rendererIn, float visibleAlpha, float invisibleAlpha) {
        super(rendererIn);
        this.hide = invisibleAlpha;
        this.show = visibleAlpha;
    }

    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull Chocobo chocoboEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (chocoboEntity.isTamed()) {
            Identifier COLLAR = chocoboEntity.isBaby() ? chocoboEntity.isMale() ? MALE_CHICOBO : FEMALE_CHICOBO : chocoboEntity.isMale() ? MALE_CHOCOBO : FEMALE_CHOCOBO;
            float alpha = chocoboEntity.isInvisible() ? hide : show;
            if (alpha != 0F) {
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(COLLAR, false));
                this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlay(chocoboEntity, 0F), 1F, 1F, 1F, alpha);
            }
        }
    }
}