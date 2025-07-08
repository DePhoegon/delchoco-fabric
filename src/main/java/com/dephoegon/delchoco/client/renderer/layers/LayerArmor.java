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

import static com.dephoegon.delchoco.common.init.ModItems.*;
import static net.minecraft.client.render.OverlayTexture.*;

public class LayerArmor<T extends Entity & IChocobo, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private final float hide;
    private final float show;
    private static final Map<String, Identifier> CHOCOBO_ARMORS = Util.make(Maps.newHashMap(), (map) -> {
        // Chocobo Armor Textures
        map.put(CHAIN_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_head.png"));
        map.put(CHAIN_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_body.png"));
        map.put(CHAIN_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_leg.png"));
        map.put(CHAIN_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_feet.png"));
        // Reinforced Chain Chocobo Armor Textures
        map.put(REINFORCED_CHAIN_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_head.png"));
        map.put(REINFORCED_CHAIN_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_body.png"));
        map.put(REINFORCED_CHAIN_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_leg.png"));
        map.put(REINFORCED_CHAIN_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_chain_feet.png"));
        // Iron Chocobo Armor Textures
        map.put(IRON_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_head.png"));
        map.put(IRON_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_body.png"));
        map.put(IRON_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_leg.png"));
        map.put(IRON_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_feet.png"));
        // Reinforced Iron Chocobo Armor Textures
        map.put(REINFORCED_IRON_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_head.png"));
        map.put(REINFORCED_IRON_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_body.png"));
        map.put(REINFORCED_IRON_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_leg.png"));
        map.put(REINFORCED_IRON_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_iron_feet.png"));
        // Diamond Chocobo Armor Textures
        map.put(DIAMOND_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_head.png"));
        map.put(DIAMOND_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_body.png"));
        map.put(DIAMOND_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_leg.png"));
        map.put(DIAMOND_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_feet.png"));
        // Reinforced Diamond Chocobo Armor Textures
        map.put(REINFORCED_DIAMOND_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_head.png"));
        map.put(REINFORCED_DIAMOND_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_body.png"));
        map.put(REINFORCED_DIAMOND_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_leg.png"));
        map.put(REINFORCED_DIAMOND_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_diamond_feet.png"));
        // Netherite Chocobo Armor Textures
        map.put(NETHERITE_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_head.png"));
        map.put(NETHERITE_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_body.png"));
        map.put(NETHERITE_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_leg.png"));
        map.put(NETHERITE_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_feet.png"));
        // Reinforced Netherite Chocobo Armor Textures
        map.put(REINFORCED_NETHERITE_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_head.png"));
        map.put(REINFORCED_NETHERITE_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_body.png"));
        map.put(REINFORCED_NETHERITE_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_leg.png"));
        map.put(REINFORCED_NETHERITE_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_feet.png"));
        // Gilded Netherite Chocobo Armor Textures
        map.put(GILDED_NETHERITE_CHOCO_HELMET.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_head.png"));
        map.put(GILDED_NETHERITE_CHOCO_CHEST.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_body.png"));
        map.put(GILDED_NETHERITE_CHOCO_LEGGINGS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_leg.png"));
        map.put(GILDED_NETHERITE_CHOCO_BOOTS.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/armor/chocobo_netherite_feet.png"));
    });

    public LayerArmor(FeatureRendererContext<T, M> rendererIn, float visibleAlpha, float invisibleAlpha) {
        super(rendererIn);
        this.hide = invisibleAlpha;
        this.show = visibleAlpha;
    }

    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isArmored()) {
            if (entity.isChestArmored() || entity.isHeadArmored() || entity.isLegsArmored() || entity.isFeetArmored()) {
                String chestID = entity.isChestArmored() ? entity.getChestArmor().getTranslationKey() : null;
                String helmetID = entity.isHeadArmored() ? entity.getHeadArmor().getTranslationKey() : null;
                String leggingsID = entity.isLegsArmored() ? entity.getLegsArmor().getTranslationKey() : null;
                String bootsID = entity.isFeetArmored() ? entity.getFeetArmor().getTranslationKey() : null;
                float alpha = entity.isInvisible() ? hide : show;
                if (alpha > 0F) {
                    if (chestID != null) { renderArmor(matrixStackIn, bufferIn, packedLightIn, entity, alpha, chestID); }
                    if (helmetID != null) { renderArmor(matrixStackIn, bufferIn, packedLightIn, entity, alpha, helmetID); }
                    if (leggingsID != null) { renderArmor(matrixStackIn, bufferIn, packedLightIn, entity, alpha, leggingsID); }
                    if (bootsID != null) { renderArmor(matrixStackIn, bufferIn, packedLightIn, entity, alpha, bootsID); }
                }
            }
        }
    }
    private void renderArmor(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull T entity, float alpha, String armorTranslationID) {
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(CHOCOBO_ARMORS.get(armorTranslationID)));
        this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, getOverlay(entity, 0F), 1F, 1F, 1F, alpha);
    }
    protected int getOverlay(T entity, float whiteOverlayProgress) {
        if (entity instanceof Chocobo choco) {
            return LivingEntityRenderer.getOverlay(choco, whiteOverlayProgress);
        } else { return packUv(getU(whiteOverlayProgress), getV(false)); }
    }
}