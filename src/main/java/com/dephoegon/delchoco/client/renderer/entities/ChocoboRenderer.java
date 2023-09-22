package com.dephoegon.delchoco.client.renderer.entities;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.client.clientHandler;
import com.dephoegon.delchoco.client.models.entities.AdultChocoboModel;
import com.dephoegon.delchoco.client.models.entities.ChicoboModel;
import com.dephoegon.delchoco.client.renderer.layers.*;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
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

import static com.dephoegon.delchoco.DelChoco.chocoConfigHolder;
import static com.dephoegon.delchoco.aid.chocoKB.hideChocoboMountInFirstPerson;

public class ChocoboRenderer extends MobEntityRenderer<Chocobo, EntityModel<Chocobo>> {
    private static final Map<ChocoboColor, Identifier> CHOCOBO_PER_COLOR = Util.make(Maps.newHashMap(), (map) -> {
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
    private boolean isMale;

    public ChocoboRenderer(Context context) {
        super(context, new AdultChocoboModel<>(context.getPart(clientHandler.CHOCOBO_LAYER)), 0.75f);
        this.chicoboModel = new ChicoboModel<>(context.getPart(clientHandler.CHICOBO_LAYER));

        this.addFeature(new LayerChocoboTrims(this, .75F, .85F));
        this.addFeature(new LayerCollar(this, collarAlpha, chocoConfigHolder.chocoboCollarAlpha));
        this.addFeature(new LayerCollarTells(this, collarTellAlpha, chocoConfigHolder.chocoboCollarAlpha));
        this.addFeature(new LayerBeakClaws(this, true));
        this.addFeature(new LayerArmor(this, armorAlpha, chocoConfigHolder.chocoboArmorAlpha));
        this.addFeature(new LayerSaddle(this, saddleAlpha, chocoConfigHolder.chocoboSaddleAlpha));
        this.addFeature(new LayerWeapon(this, weaponAlpha, chocoConfigHolder.chocoboWeaponAlpha));
    }
    public void render(@NotNull Chocobo chocobo, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider bufferSource, int packedLight) {
        this.model = chocobo.isBaby() ? chicoboModel : chocoboModel;
        if (hideChocoboMountInFirstPerson(chocobo)) { return; }
        float factor = chocobo.getChocoboScale() == 0 ? 1 : Math.max(chocobo.getChocoboScaleMod(), .85F);
        if (factor < .86F) { chocobo.setChocoboScale(true, -15, true); }
        matrixStack.scale(factor, factor, factor);
        super.render(chocobo, entityYaw, partialTicks, matrixStack, bufferSource, packedLight);
    }
    public Identifier getTexture(@NotNull Chocobo chocobo) {
        ChocoboColor color = chocobo.getChocoboColor();
        return chocobo.isBaby() ? CHICOBO_PER_COLOR.get(color) : CHOCOBO_PER_COLOR.get(color);
    }
}