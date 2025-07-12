package com.dephoegon.delchoco.client.renderer.layers;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.google.common.collect.Maps;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LayerBeakClaws extends FeatureRenderer<Chocobo, EntityModel<Chocobo>> {
    private final boolean boxedBeak;
    private static final String curved = "curved";
    private static final String boxed = "boxed";
    private static final Map<String, Identifier> CHOCOBO_BEAKS = Util.make(Maps.newHashMap(), (map) -> {
        map.put(curved, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/flame/curved.png"));
        map.put(boxed, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/flame/boxed.png"));
    });
    public LayerBeakClaws(FeatureRendererContext<Chocobo, EntityModel<Chocobo>> pRenderer, boolean isBoxedBeak) {
        super(pRenderer);
        this.boxedBeak = isBoxedBeak;
    }
    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull Chocobo chocoboEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (!chocoboEntity.isBaby()) {
            String beak = boxedBeak ? boxed : curved;
            VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(CHOCOBO_BEAKS.get(beak)));
            this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlay(chocoboEntity, 0F), 1F, 1F, 1F, 1F);
        }
    }
}
