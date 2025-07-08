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

public class LayerWeapon<T extends Entity & IChocobo, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
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
    public LayerWeapon(FeatureRendererContext<T, M> rendererIn, float visibleAlpha, float invisibleAlpha) {
        super(rendererIn);
        this.hide = invisibleAlpha;
        this.show = visibleAlpha;
    }

    @Override
    public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isWeaponArmed()) {
            float alpha = entity.isInvisible() ? hide : show;
            if (alpha != 0F) {
                Identifier resourcelocation = getWeaponTexture(entity);
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(resourcelocation));
                this.getContextModel().render(matrixStackIn, vertexconsumer, packedLightIn, getOverlay(entity, 0F), 1F, 1F, 1F, alpha);
            }
        }
    }

    private Identifier getWeaponTexture(T entity) {
        String weaponID = entity.isWeaponArmed() ? entity.getWeapon().getTranslationKey() : null;
        return CHOCOBO_WEAPONS.get(weaponID);
    }
    protected int getOverlay(T entity, float whiteOverlayProgress) {
        if (entity instanceof Chocobo choco) {
            return LivingEntityRenderer.getOverlay(choco, whiteOverlayProgress);
        } else { return packUv(getU(whiteOverlayProgress), getV(false)); }
    }
}