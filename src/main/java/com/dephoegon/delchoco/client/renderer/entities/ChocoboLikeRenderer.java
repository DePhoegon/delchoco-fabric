package com.dephoegon.delchoco.client.renderer.entities;

import com.google.common.collect.Lists;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ChocoboLikeRenderer<T extends Entity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    protected M model;
    protected final List<FeatureRenderer<T, M>> features = Lists.newArrayList();

    public ChocoboLikeRenderer(EntityRendererFactory.Context ctx, M model) {
        super(ctx);
        this.model = model;
    }

    public boolean addFeature(FeatureRenderer<T, M> feature) {
        return this.features.add(feature);
    }
    public M getModel() { return this.model; }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, @NotNull MatrixStack matrixStack, @NotNull VertexConsumerProvider vertexConsumers, int light) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - entityYaw));

        // All the logic from LivingEntityRenderer.render
        float f;
        float g;
        float h;
        float j;
        f = 0.0f;
        g = 0.0f;
        h = 0.0f;
        j = 0.0f;

        this.model.setAngles(entity, j, h, f, g, 0.0f);
        this.model.render(matrixStack, vertexConsumers.getBuffer(this.model.getLayer(this.getTexture(entity))), light, getOverlay(entity, 0.0f), 1.0F, 1.0F, 1.0F, 1.0F);

        if (!entity.isInvisible()) {
            for (FeatureRenderer<T, M> featureRenderer : this.features) {
                featureRenderer.render(matrixStack, vertexConsumers, light, entity, j, h, partialTicks, f, g, 0.0f);
            }
        }

        matrixStack.pop();
        super.render(entity, entityYaw, partialTicks, matrixStack, vertexConsumers, light);
    }

    protected int getOverlay(T entity, float whiteOverlayProgress) { return net.minecraft.client.render.OverlayTexture.DEFAULT_UV; }
}