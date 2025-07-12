package com.dephoegon.delchoco.client.renderer.entities;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.client.clientHandler;
import com.dephoegon.delchoco.client.models.entities.ChocoboArmorStandModel;
import com.dephoegon.delchoco.client.renderer.layers.*;
import com.dephoegon.delchoco.common.entities.ChocoboArmorStand;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.google.common.collect.Maps;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class ChocoboArmorStandRenderer extends ChocoboLikeRenderer<ChocoboArmorStand, ChocoboArmorStandModel> {
    private static final Map<ChocoboColor, Identifier> CHOCOBO_PER_COLOR = Util.make(Maps.newHashMap(), (map) -> {
        map.put(null, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chocobos/base/colorless.png"));
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
        map.put(null, new Identifier(DelChoco.DELCHOCO_ID, "textures/entities/chicobos/base/colorless.png"));
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

    public static final float armorAlpha = 0.75F;
    public static final float weaponAlpha = 1;
    public static final float collarAlpha = 1;
    public static final float collarTellAlpha = .45F;
    public static final float saddleAlpha = 1;

    // Track which entities have had their poses initialized
    private final Map<UUID, Boolean> initializedEntities = new WeakHashMap<>();

    public ChocoboArmorStandRenderer(EntityRendererFactory.Context context) {
        super(context, new ChocoboArmorStandModel(context.getPart(clientHandler.CHOCOBO_ARMOR_STAND_LAYER), context.getPart(clientHandler.CHICOBO_ARMOR_STAND_LAYER)));

        this.addFeature(new LayerChocoboTrims(this, .75F, .85F));
        this.addFeature(new LayerCollar(this, collarAlpha, ChocoboConfig.configTwist(ChocoboConfig.CHOCOBO_COLLAR_ALPHA.get())));
        this.addFeature(new LayerCollarTells(this, collarTellAlpha, ChocoboConfig.configTwist(ChocoboConfig.CHOCOBO_COLLAR_ALPHA.get())));
        this.addFeature(new LayerBeakClaws(this, true));
        this.addFeature(new LayerArmor(this, armorAlpha, ChocoboConfig.configTwist(ChocoboConfig.CHOCOBO_ARMOR_ALPHA.get())));
        this.addFeature(new LayerSaddle(this, saddleAlpha, ChocoboConfig.configTwist(ChocoboConfig.CHOCOBO_SADDLE_ALPHA.get())));
        this.addFeature(new LayerWeapon(this, weaponAlpha, ChocoboConfig.configTwist(ChocoboConfig.CHOCOBO_WEAPON_ALPHA.get())));
    }

    public void render(@NotNull ChocoboArmorStand armorStand, float entityYaw, float partialTicks,
                       MatrixStack matrixStack, VertexConsumerProvider bufferSource, int packedLight) {
        // Apply scaling like in ChocoboRenderer
        float factor = armorStand.getScaleMod();
        if (factor != 1.0f) {
            matrixStack.scale(factor, factor, factor);
        }

        /*  comented out for now
        // Initialize the model with the appropriate pose string from the armor stand
        if (this.model instanceof ChocoboArmorStandModel armorStandModel) {
            // Get the model instance and access the underlying adult model
            if (armorStand.isAdult()) {
                // Set the pose string to control animations
                armorStandModel.getAdultModel().setPose(armorStand.getPoseString());
            } else {
                // Baby models will use a default pose for now
                armorStandModel.getAdultModel().setPose("default_baby");
            }
        }
        */

        // Let the parent handle the rest of the rendering
        super.render(armorStand, entityYaw, partialTicks, matrixStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull Identifier getTexture(@NotNull ChocoboArmorStand entity) {
        ChocoboColor color = entity.getChocoboColor();
        Map<ChocoboColor, Identifier> textureMap = entity.isAdult() ? CHOCOBO_PER_COLOR : CHICOBO_PER_COLOR;
        return textureMap.get(color);
    }
}
