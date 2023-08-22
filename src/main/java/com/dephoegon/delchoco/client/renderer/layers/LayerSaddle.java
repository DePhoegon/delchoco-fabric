package com.dephoegon.delchoco.client.renderer.layers;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.items.ChocoboSaddleItem;
import com.google.common.collect.Maps;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LayerSaddle extends FeatureRenderer<Chocobo, EntityModel<Chocobo>> {
    private final float hide;
    private final float show;
    private static final Map<Item, Identifier> CHOCOBO_SADDLES = Util.make(Maps.newHashMap(), (map) ->{
        map.put(ModItems.CHOCOBO_SADDLE, new Identifier(DelChoco.DELCHOCO_ID,"textures/entities/chocobos/saddle.png"));
        map.put(ModItems.CHOCOBO_SADDLE_BAGS, new Identifier(DelChoco.DELCHOCO_ID,"textures/entities/chocobos/saddle_bag.png"));
        map.put(ModItems.CHOCOBO_SADDLE_PACK,new Identifier(DelChoco.DELCHOCO_ID,"textures/entities/chocobos/pack_bag.png"));
    });

    public LayerSaddle(FeatureRendererContext<Chocobo, EntityModel<Chocobo>> rendererIn, float visibleAlpha, float invisibleAlpha) {
        super(rendererIn);
        this.hide = invisibleAlpha;
        this.show = visibleAlpha;
    }
    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull Chocobo chocobo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!chocobo.isBaby()) {
            float alpha = chocobo.isInvisible() ? hide : show;
            if (chocobo.getSaddle().getItem() instanceof ChocoboSaddleItem saddleItem && alpha != 0F) {
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(CHOCOBO_SADDLES.get(saddleItem), saddleItem.hasOutline()));
                this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlay(chocobo, 0F), 1F, 1F, 1F, alpha);
            }
        }
    }
}