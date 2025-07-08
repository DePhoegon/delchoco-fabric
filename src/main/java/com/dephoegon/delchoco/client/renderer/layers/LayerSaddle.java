package com.dephoegon.delchoco.client.renderer.layers;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.IChocobo;
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
import org.jetbrains.annotations.NotNull;

import static net.minecraft.client.render.OverlayTexture.*;

public class LayerSaddle<T extends Entity & IChocobo, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private final float hide;
    private final float show;

    private static final Identifier TEXTURE_SADDLE = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/saddles/saddle.png");
    private static final Identifier TEXTURE_SADDLE_BAGS = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/saddles/saddle_bags.png");
    private static final Identifier TEXTURE_SADDLE_PACK = new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/saddles/saddle_pack.png");

    public LayerSaddle(FeatureRendererContext<T, M> rendererIn, float visibleAlpha, float invisibleAlpha) {
        super(rendererIn);
        this.hide = invisibleAlpha;
        this.show = visibleAlpha;
    }

    @Override
    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isSaddled()) {
            float alpha = entity.isInvisible() ? hide : show;
            if (alpha != 0F) {
                Identifier resourcelocation = getSaddleTexture(entity);
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(resourcelocation));
                this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, getOverlay(entity, 0F), 1F, 1F, 1F, alpha);
            }
        }
    }
    private Identifier getSaddleTexture(T entity) {
        String path = entity.getSaddle().getItem().toString();
        if (path.contains("bag")) { return TEXTURE_SADDLE_BAGS; }
        else if (path.contains("pack")) { return TEXTURE_SADDLE_PACK; }
        else { return TEXTURE_SADDLE; }
    }
    protected int getOverlay(T entity, float whiteOverlayProgress) {
        if (entity instanceof Chocobo choco) {
            return LivingEntityRenderer.getOverlay(choco, whiteOverlayProgress);
        } else { return packUv(getU(whiteOverlayProgress), getV(false)); }
    }
}