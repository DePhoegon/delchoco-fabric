package com.dephoegon.delchoco.client.renderer.layers;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
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

public class LayerChocoboTrims extends FeatureRenderer<Chocobo, EntityModel<Chocobo>> {
    private final float tamedShow;
    private final float untamedShow;
    private static final Map<ChocoboColor, Identifier> TAMED_CHOCOBO_PLUMES = Util.make(Maps.newHashMap(), (map) -> {
        map.put(ChocoboColor.YELLOW, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/tamed_plumes/tamed_yellow.png"));
        map.put(ChocoboColor.GREEN, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/tamed_plumes/tamed_green.png"));
        map.put(ChocoboColor.BLUE, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/tamed_plumes/tamed_blue.png"));
        map.put(ChocoboColor.BLACK, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/tamed_plumes/tamed_black.png"));
        map.put(ChocoboColor.WHITE, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/tamed_plumes/tamed_white.png"));
        map.put(ChocoboColor.GOLD, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/tamed_plumes/tamed_gold.png"));
        map.put(ChocoboColor.PINK, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/tamed_plumes/tamed_pink.png"));
        map.put(ChocoboColor.RED, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/tamed_plumes/tamed_red.png"));
        map.put(ChocoboColor.PURPLE, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/tamed_plumes/tamed_purple.png"));
        map.put(ChocoboColor.FLAME, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/tamed_plumes/tamed_flame.png"));
    });
    private static final Map<ChocoboColor, Identifier> UNTAMED_CHOCOBO_PLUMES = Util.make(Maps.newHashMap(), (map) -> {
        map.put(ChocoboColor.YELLOW, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/untamed_plumes/yellow.png"));
        map.put(ChocoboColor.GREEN, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/untamed_plumes/green.png"));
        map.put(ChocoboColor.BLUE, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/untamed_plumes/blue.png"));
        map.put(ChocoboColor.BLACK, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/untamed_plumes/black.png"));
        map.put(ChocoboColor.WHITE, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/untamed_plumes/white.png"));
        map.put(ChocoboColor.GOLD, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/untamed_plumes/gold.png"));
        map.put(ChocoboColor.PINK, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/untamed_plumes/pink.png"));
        map.put(ChocoboColor.RED, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/untamed_plumes/red.png"));
        map.put(ChocoboColor.PURPLE, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/untamed_plumes/purple.png"));
        map.put(ChocoboColor.FLAME, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/untamed_plumes/flame.png"));

    });
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
                rSwitch(matrixStackIn, bufferIn, packedLightIn, chocobo, untamedShow, UNTAMED_CHOCOBO_PLUMES.get(chocobo.getChocoboColor()), true);
                if (chocobo.isTamed()) { rSwitch(matrixStackIn, bufferIn, packedLightIn, chocobo, tamedShow, TAMED_CHOCOBO_PLUMES.get(chocobo.getChocoboColor()), false); }
            }
            if (chocobo.nonFlameFireImmune()) { rSwitch(matrixStackIn, bufferIn, packedLightIn, chocobo, 1F, chocobo.isBaby() ? CHICOBO_FLAME_EYES : CHOCOBO_FLAME_EYES, false); }
        }
    }
    private void rSwitch(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull Chocobo chocobo, float alpha, Identifier layer, boolean outline) {
        VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(layer, outline));
        this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlay(chocobo, 0F), 1F, 1F, 1F, alpha);
    }
}
