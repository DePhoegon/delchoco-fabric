package com.dephoegon.delchoco.client.renderer.entities;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.client.clientHandler;
import com.dephoegon.delchoco.client.models.entities.AdultChocoboModel;
import com.dephoegon.delchoco.client.models.entities.ChicoboModel;
import com.dephoegon.delchoco.client.renderer.layers.*;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.entities.subTypes.ArmorStandChocobo;
import com.google.common.collect.Maps;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.dephoegon.delchoco.aid.chocoKB.showChocobo;
import static com.dephoegon.delchoco.common.entities.ArmorStandChocoboPose.applyToModel;
import static com.dephoegon.delchoco.common.entities.ArmorStandChocoboPose.setPoseByType;

public class ChocoboRenderer extends MobEntityRenderer<Chocobo, EntityModel<Chocobo>> {
    private static final Map<ChocoboColor, Identifier> CHOCOBO_PER_COLOR = Util.make(Maps.newHashMap(), (map) -> {
        map.put(null, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/base/leather.png"));
        map.put(ChocoboColor.YELLOW, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/base/yellow.png"));
        map.put(ChocoboColor.GREEN, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/base/green.png"));
        map.put(ChocoboColor.BLUE, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/base/blue.png"));
        map.put(ChocoboColor.WHITE, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/base/white.png"));
        map.put(ChocoboColor.BLACK, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/base/black.png"));
        map.put(ChocoboColor.GOLD, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/base/gold.png"));
        map.put(ChocoboColor.PINK, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/base/pink.png"));
        map.put(ChocoboColor.RED, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/base/red.png"));
        map.put(ChocoboColor.PURPLE, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/base/purple.png"));
        map.put(ChocoboColor.FLAME, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/flame/flame.png"));
    });
    private static final Map<ChocoboColor, Identifier> CHICOBO_PER_COLOR = Util.make(Maps.newHashMap(), (map) -> {
        map.put(null, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/base/leather.png"));
        map.put(ChocoboColor.YELLOW, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/base/yellow.png"));
        map.put(ChocoboColor.GREEN, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/base/green.png"));
        map.put(ChocoboColor.BLUE, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/base/blue.png"));
        map.put(ChocoboColor.WHITE, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/base/white.png"));
        map.put(ChocoboColor.BLACK, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/base/black.png"));
        map.put(ChocoboColor.GOLD, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/base/gold.png"));
        map.put(ChocoboColor.PINK, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/base/pink.png"));
        map.put(ChocoboColor.RED, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/base/red.png"));
        map.put(ChocoboColor.PURPLE, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/base/purple.png"));
        map.put(ChocoboColor.FLAME, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/flame/flame.png"));
    });
    private final EntityModel<Chocobo> chicoboModel;
    private final EntityModel<Chocobo> chocoboModel = this.getModel();
    public static final float armorAlpha = 0.75F;
    public static final float weaponAlpha = 1;
    public static final float collarAlpha = 1;
    public static final float collarTellAlpha = .45F;
    public static final float saddleAlpha = 1;

    public ChocoboRenderer(Context context) {
        super(context, new AdultChocoboModel<>(context.getPart(clientHandler.CHOCOBO_LAYER)), 0.75f);
        this.chicoboModel = new ChicoboModel<>(context.getPart(clientHandler.CHICOBO_LAYER));

        this.addFeature(new LayerChocoboTrims(this, .75F, .85F));
        this.addFeature(new LayerCollar(this, collarAlpha, ChocoboConfig.configTwist(ChocoboConfig.CHOCOBO_COLLAR_ALPHA.get())));
        this.addFeature(new LayerCollarTells(this, collarTellAlpha, ChocoboConfig.configTwist(ChocoboConfig.CHOCOBO_COLLAR_ALPHA.get())));
        this.addFeature(new LayerBeakClaws(this, true));
        this.addFeature(new LayerArmor(this, armorAlpha, ChocoboConfig.configTwist(ChocoboConfig.CHOCOBO_ARMOR_ALPHA.get())));
        this.addFeature(new LayerSaddle(this, saddleAlpha, ChocoboConfig.configTwist(ChocoboConfig.CHOCOBO_SADDLE_ALPHA.get())));
        this.addFeature(new LayerWeapon(this, weaponAlpha, ChocoboConfig.configTwist(ChocoboConfig.CHOCOBO_WEAPON_ALPHA.get())));
    }
    public void render(@NotNull Chocobo chocobo, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider bufferSource, int packedLight) {
        this.model = chocobo.isBaby() ? chicoboModel : chocoboModel;
        if (showChocobo(chocobo)) {
            float factor = chocobo.getChocoboScale() == 0 ? 1 : Math.max(chocobo.getChocoboScaleMod(), .85F);
            if (factor < .86F) { chocobo.setChocoboScale(true, -15, true); }
            matrixStack.scale(factor, factor, factor);
            if (chocobo.isArmorStandNotAlive()) {
                setPoseByType(chocobo.getPoseType(), (ArmorStandChocobo) chocobo);
                if (chocobo.isBaby()) { applyToModel((ChicoboModel<Chocobo>)this.model, ((ArmorStandChocobo) chocobo).getChocoboModelPose()); }
                else { applyToModel((AdultChocoboModel<Chocobo>) this.model, ((ArmorStandChocobo) chocobo).getChocoboModelPose()); }
            }
            super.render(chocobo, entityYaw, partialTicks, matrixStack, bufferSource, packedLight);
        }
    }
    public Identifier getTexture(@NotNull Chocobo chocobo) {
        ChocoboColor color = chocobo.getChocoboColor();
        return chocobo.isBaby() ? CHICOBO_PER_COLOR.get(color) : CHOCOBO_PER_COLOR.get(color);
    }
}