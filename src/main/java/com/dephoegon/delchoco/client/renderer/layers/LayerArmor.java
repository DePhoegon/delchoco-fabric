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

import static com.dephoegon.delchoco.common.init.ModItems.*;

public class LayerArmor extends FeatureRenderer<Chocobo, EntityModel<Chocobo>> {
    private final float hide;
    private final float show;
    public LayerArmor(FeatureRendererContext<Chocobo, EntityModel<Chocobo>> pRenderer, float visibleAlpha, float invisibleAlpha) {
        super(pRenderer);
        this.hide = invisibleAlpha;
        this.show = visibleAlpha;
    }
    private static final Map<String, Identifier> CHOCOBO_ARMORS = Util.make(Maps.newHashMap(), (map) -> {
        map.put(CHAIN_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_head.png"));
        map.put(CHAIN_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_body.png"));
        map.put(CHAIN_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_leg.png"));
        map.put(CHAIN_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_feet.png"));
        map.put(REINFORCED_CHAIN_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_head.png"));
        map.put(REINFORCED_CHAIN_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_body.png"));
        map.put(REINFORCED_CHAIN_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_leg.png"));
        map.put(REINFORCED_CHAIN_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_feet.png"));
        map.put(IRON_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_head.png"));
        map.put(IRON_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_body.png"));
        map.put(IRON_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_leg.png"));
        map.put(IRON_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_feet.png"));
        map.put(REINFORCED_IRON_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_head.png"));
        map.put(REINFORCED_IRON_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_body.png"));
        map.put(REINFORCED_IRON_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_leg.png"));
        map.put(REINFORCED_IRON_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_feet.png"));
        map.put(DIAMOND_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_head.png"));
        map.put(DIAMOND_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_body.png"));
        map.put(DIAMOND_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_leg.png"));
        map.put(DIAMOND_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_feet.png"));
        map.put(REINFORCED_DIAMOND_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_head.png"));
        map.put(REINFORCED_DIAMOND_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_body.png"));
        map.put(REINFORCED_DIAMOND_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_leg.png"));
        map.put(REINFORCED_DIAMOND_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_feet.png"));
        map.put(NETHERITE_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_head.png"));
        map.put(NETHERITE_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_body.png"));
        map.put(NETHERITE_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_leg.png"));
        map.put(NETHERITE_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_feet.png"));
        map.put(REINFORCED_NETHERITE_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_head.png"));
        map.put(REINFORCED_NETHERITE_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_body.png"));
        map.put(REINFORCED_NETHERITE_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_leg.png"));
        map.put(REINFORCED_NETHERITE_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_feet.png"));
        map.put(GILDED_NETHERITE_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_head.png"));
        map.put(GILDED_NETHERITE_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_body.png"));
        map.put(GILDED_NETHERITE_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_leg.png"));
        map.put(GILDED_NETHERITE_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_feet.png"));
    });
    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull Chocobo chocoboEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!chocoboEntity.isBaby() && chocoboEntity.isAlive() && chocoboEntity.isArmored()) {
            String chestID = chocoboEntity.isChestArmored() ? chocoboEntity.getChestArmor().getTranslationKey() : null;
            String helmetID = chocoboEntity.isHeadArmored() ? chocoboEntity.getHeadArmor().getTranslationKey() : null;
            String leggingsID = chocoboEntity.isLegsArmored() ? chocoboEntity.getLegsArmor().getTranslationKey() : null;
            String bootsID = chocoboEntity.isFeetArmored() ? chocoboEntity.getFeetArmor().getTranslationKey() : null;
            float alpha = chocoboEntity.isInvisible() ? hide : show;
            if (chestID != null && alpha != 0F) {
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(CHOCOBO_ARMORS.get(chestID), false));
                this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlay(chocoboEntity, 0F), 1F, 1F, 1F, alpha);
            }
            if (helmetID != null && alpha != 0F) {
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(CHOCOBO_ARMORS.get(helmetID), false));
                this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlay(chocoboEntity, 0F), 1F, 1F, 1F, alpha);
            }
            if (leggingsID != null && alpha != 0F) {
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(CHOCOBO_ARMORS.get(leggingsID), false));
                this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlay(chocoboEntity, 0F), 1F, 1F, 1F, alpha);
            }
            if (bootsID != null && alpha != 0F) {
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(CHOCOBO_ARMORS.get(bootsID), false));
                this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlay(chocoboEntity, 0F), 1F, 1F, 1F, alpha);
            }
        }
    }
}
