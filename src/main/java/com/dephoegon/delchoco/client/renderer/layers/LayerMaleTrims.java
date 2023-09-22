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

public class LayerMaleTrims extends FeatureRenderer<Chocobo, EntityModel<Chocobo>> {
    private final float alpha;

    private final Identifier tamed = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/tamed_plumes/male.png");
    private final Identifier notTamed = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/untamed_plumes/male.png");
    public LayerMaleTrims(FeatureRendererContext<Chocobo, EntityModel<Chocobo>> pRenderer, float layerAlpha) {
        super(pRenderer);
        this.alpha = layerAlpha;
    }
    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull Chocobo chocobo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        float mAlpha = chocobo.isMale() ? alpha : 0F;
        if (!chocobo.isInvisible() && !chocobo.isBaby()) {
            VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(chocobo.isTamed() ? tamed : notTamed, false));
            this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlay(chocobo, 0F), 1F, 1F, 1F, mAlpha);
        }
    }
}