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

public class LayerWeapon extends FeatureRenderer<Chocobo, EntityModel<Chocobo>> {
    private final float hide;
    private final float show;
    private static final Map<String, Identifier> CHOCOBO_WEAPONS = Util.make(Maps.newHashMap(), (map) -> {
        map.put(CHAIN_CHOCO_WEAPON.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/weapon/chocobo_chain.png"));
        map.put(REINFORCED_CHAIN_CHOCO_WEAPON.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/weapon/chocobo_chain.png"));
        map.put(IRON_CHOCO_WEAPON.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/weapon/chocobo_iron.png"));
        map.put(REINFORCED_IRON_CHOCO_WEAPON.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/weapon/chocobo_iron.png"));
        map.put(DIAMOND_CHOCO_WEAPON.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/weapon/chocobo_diamond.png"));
        map.put(REINFORCED_DIAMOND_CHOCO_WEAPON.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/weapon/chocobo_diamond.png"));
        map.put(NETHERITE_CHOCO_WEAPON.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/weapon/chocobo_netherite.png"));
        map.put(REINFORCED_NETHERITE_CHOCO_WEAPON.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/weapon/chocobo_netherite.png"));
        map.put(GILDED_NETHERITE_CHOCO_WEAPON.getTranslationKey(), new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/weapon/chocobo_netherite.png"));
    });
    public LayerWeapon(FeatureRendererContext<Chocobo, EntityModel<Chocobo>> pRenderer, float visibleAlpha, float invisibleAlpha) {
        super(pRenderer);
        this.hide = invisibleAlpha;
        this.show = visibleAlpha;
    }
    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull Chocobo chocoboEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!chocoboEntity.isBaby()) {
            String weaponID = chocoboEntity.isWeaponArmed() ? chocoboEntity.getWeapon().getTranslationKey() : null;
            float alpha = chocoboEntity.isInvisible() ? hide : show;
            if (weaponID != null && alpha != 0F) {
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(CHOCOBO_WEAPONS.get(weaponID), false));
                this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlay(chocoboEntity, 0F), 1F, 1F, 1F, alpha);
            }
        }
    }
}