package com.dephoegon.delchoco.client.renderer.layers;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.entities.properties.IChocobo;
import com.google.common.collect.Maps;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static net.minecraft.client.render.OverlayTexture.*;

public class LayerCollarTells<T extends Entity & IChocobo, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private final float hide;
    private final float show;
    private final Identifier FEMALE_CHOCOBO = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_tell.png");
    private final Identifier MALE_CHOCOBO = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_tell.png");
    private final Identifier MALE_CHICOBO = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/m_tell.png");
    private final Identifier FEMALE_CHICOBO = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/f_tell.png");
    private final Identifier tamed = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/tamed_plumes/male.png");
    private final Identifier notTamed = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/untamed_plumes/male.png");

    public LayerCollarTells(FeatureRendererContext<T, M> rendererIn, float visibleAlpha, float invisibleAlpha) {
        super(rendererIn);
        this.hide = invisibleAlpha;
        this.show = visibleAlpha;
    }

    @Override
    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Identifier TELLS = notTamed;
        float mTellAlpha = entity.isMale() ? .35F : 0F;
        float alpha = entity.isInvisible() ? hide : show;
        if (entity.isTamed() && !entity.isBaby()) {
            TELLS = tamed;
            int color = entity.getCollarColor();
            Identifier COLLAR_TELLS = entity.isBaby() ? entity.isMale() ? MALE_CHICOBO : FEMALE_CHICOBO : entity.isMale() ? MALE_CHOCOBO : FEMALE_CHOCOBO;
            if (alpha != 0F) {
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(COLLAR_TELLS));
                this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, getOverlay(entity, 0F), 1F, 1F, 1F, alpha);
            }
        }
        if (!entity.isInvisible() && !entity.isBaby()) {
            VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(TELLS, false));
            this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, getOverlay(entity, 0F), 1F, 1F, 1F, mTellAlpha);
        }
    }
    protected int getOverlay(T entity, float whiteOverlayProgress) {
        if (entity instanceof Chocobo choco) {
            return LivingEntityRenderer.getOverlay(choco, whiteOverlayProgress);
        } else { return packUv(getU(whiteOverlayProgress), getV(false)); }
    }
}