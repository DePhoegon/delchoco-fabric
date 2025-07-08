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

public class LayerCollar<T extends Entity & IChocobo, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private final float hide;
    private final float show;
    private static final Map<Integer, Identifier> FEMALE_CHOCOBOS = Util.make(Maps.newHashMap(), (map) ->{
        map.put(1, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_black_collar.png"));
        map.put(2, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_brown_collar.png"));
        map.put(3, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_green_collar.png"));
        map.put(4, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_blue_collar.png"));
        map.put(5, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_purple_collar.png"));
        map.put(6, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_cyan_collar.png"));
        map.put(7, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_light_gray_collar.png"));
        map.put(8, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_gray_collar.png"));
        map.put(9, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_pink_collar.png"));
        map.put(10, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_lime_collar.png"));
        map.put(11, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_yellow_collar.png"));
        map.put(12, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_light_blue_collar.png"));
        map.put(13, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_magenta_collar.png"));
        map.put(14, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_orange_collar.png"));
        map.put(15, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_white_collar.png"));
        map.put(16, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/f_red_collar.png"));
    });
    private static final Map<Integer, Identifier> MALE_CHOCOBOS = Util.make(Maps.newHashMap(), (map) ->{
        map.put(1, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_black_collar.png"));
        map.put(2, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_brown_collar.png"));
        map.put(3, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_green_collar.png"));
        map.put(4, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_blue_collar.png"));
        map.put(5, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_purple_collar.png"));
        map.put(6, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_cyan_collar.png"));
        map.put(7, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_light_gray_collar.png"));
        map.put(8, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_gray_collar.png"));
        map.put(9, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_pink_collar.png"));
        map.put(10, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_lime_collar.png"));
        map.put(11, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_yellow_collar.png"));
        map.put(12, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_light_blue_collar.png"));
        map.put(13, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_magenta_collar.png"));
        map.put(14, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_orange_collar.png"));
        map.put(15, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_white_collar.png"));
        map.put(16, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/collars/m_red_collar.png"));
    });
    private static final Map<Integer, Identifier> CHICOBOS = Util.make(Maps.newHashMap(), (map) ->{
        map.put(1, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/black_collar.png"));
        map.put(2, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/brown_collar.png"));
        map.put(3, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/green_collar.png"));
        map.put(4, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/blue_collar.png"));
        map.put(5, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/purple_collar.png"));
        map.put(6, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/cyan_collar.png"));
        map.put(7, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/light_gray_collar.png"));
        map.put(8, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/gray_collar.png"));
        map.put(9, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/pink_collar.png"));
        map.put(10, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/lime_collar.png"));
        map.put(11, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/yellow_collar.png"));
        map.put(12, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/light_blue_collar.png"));
        map.put(13, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/magenta_collar.png"));
        map.put(14, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/orange_collar.png"));
        map.put(15, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/white_collar.png"));
        map.put(16, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/collars/red_collar.png"));
    });

    public LayerCollar(FeatureRendererContext<T, M> rendererIn, float visibleAlpha, float invisibleAlpha) {
        super(rendererIn);
        this.hide = invisibleAlpha;
        this.show = visibleAlpha;
    }

    @Override
    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isTamed()) {
            int color = entity.getCollarColor();
            Identifier COLLAR = color != 0 ? entity.isBaby() ? CHICOBOS.get(color) : entity.isMale() ? MALE_CHOCOBOS.get(color) : FEMALE_CHOCOBOS.get(color) : null;
            float alpha = entity.isInvisible() ? hide : show;
            if (COLLAR != null && alpha != 0F) {
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(COLLAR, false));
                this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, getOverlay(entity, 0F), 1F, 1F, 1F, alpha);
            }
        }
    }
    protected int getOverlay(T entity, float whiteOverlayProgress) {
        if (entity instanceof Chocobo choco) {
            return LivingEntityRenderer.getOverlay(choco, whiteOverlayProgress);
        } else { return packUv(getU(whiteOverlayProgress), getV(false)); }
    }
}