package com.dephoegon.delchoco.client.renderer.layers;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
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

public class LayerBeakClaws<T extends Entity & IChocobo, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private final boolean boxedBeak;
    private static final String curved = "curved";
    private static final String boxed = "boxed";
    private static final Map<String, Identifier> CHOCOBO_BEAKS = Util.make(Maps.newHashMap(), (map) -> {
        map.put(curved, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/flame/curved.png"));
        map.put(boxed, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/flame/boxed.png"));
    });
    public LayerBeakClaws(FeatureRendererContext<T, M> rendererIn, boolean isBoxedBeak) {
        super(rendererIn);
        this.boxedBeak = isBoxedBeak;
    }

    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isBaby()) {
            String beakType = boxedBeak ? boxed : curved;
            VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(CHOCOBO_BEAKS.get(beakType)));
            this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, getOverlay(entity, partialTicks), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
    protected int getOverlay(T entity, float whiteOverlayProgress) {
        if (entity instanceof Chocobo choco) {
            return LivingEntityRenderer.getOverlay(choco, whiteOverlayProgress);
        } else { return packUv(getU(whiteOverlayProgress), getV(false)); }
    }
}