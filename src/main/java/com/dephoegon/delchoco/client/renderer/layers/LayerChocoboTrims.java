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

public class LayerChocoboTrims<T extends Entity & IChocobo, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
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

    public LayerChocoboTrims(FeatureRendererContext<T, M> context, float tamedAlpha, float untamedAlpha) {
        super(context);
        this.tamedShow = tamedAlpha;
        this.untamedShow = untamedAlpha;
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, @NotNull VertexConsumerProvider bufferProvider, int packedLightIn, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) { return; }

        float alpha = entity.isBaby() ? 0.35F : 1F;
        if (!entity.isBaby()) {
            rSwitch(matrixStack, bufferProvider, packedLightIn, untamedShow, entity, UNTAMED_CHOCOBO_PLUMES.get(entity.getChocoboColor()), true);
            if (entity.isTamed()) { rSwitch(matrixStack, bufferProvider, packedLightIn, tamedShow, entity, TAMED_CHOCOBO_PLUMES.get(entity.getChocoboColor()), false); }
        }
        if (entity.isFireImmune()) {
            rSwitch(matrixStack, bufferProvider, packedLightIn, alpha, entity, entity.isBaby() ? CHICOBO_FLAME_EYES : CHOCOBO_FLAME_EYES, false);
        }
    }
    private void rSwitch(MatrixStack matrixStack, VertexConsumerProvider bufferProvider, int packedLightIn, float alpha, T entity, Identifier texture, boolean outline) {
        if (texture != null) {
            VertexConsumer vertexConsumer = bufferProvider.getBuffer(RenderLayer.getEntityTranslucent(texture, outline));
            this.getContextModel().render(matrixStack, vertexConsumer, packedLightIn, getOverlay(entity, 0F), 1F, 1F, 1F, alpha);
        }
    }
    protected int getOverlay(T entity, float whiteOverlayProgress) {
        if (entity instanceof Chocobo choco) {
            return LivingEntityRenderer.getOverlay(choco, whiteOverlayProgress);
        } else { return packUv(getU(whiteOverlayProgress), getV(false)); }
    }
}