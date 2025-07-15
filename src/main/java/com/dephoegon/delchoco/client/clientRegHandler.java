package com.dephoegon.delchoco.client;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.client.models.ModModelLayers;
import com.dephoegon.delchoco.client.models.armor.ChocoDisguiseModel;
import com.dephoegon.delchoco.client.models.entities.AdultChocoboModel;
import com.dephoegon.delchoco.client.models.entities.ChicoboModel;
import com.dephoegon.delchoco.common.init.ModItems;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class clientRegHandler {
    public static final EntityModelLayer CHOCOBO_LAYER = new EntityModelLayer(new Identifier(DelChoco.DELCHOCO_ID, "main"), "chocobo");
    public static final EntityModelLayer CHICOBO_LAYER = new EntityModelLayer(new Identifier(DelChoco.DELCHOCO_ID, "main"), "chicobo");

    public static void ChocoboRendering() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModItems.GYSAHL_GREEN, RenderLayer.getCutoutMipped());
        registerLayerDefinitions();
    }
    public static void registerLayerDefinitions() {
        EntityModelLayerRegistry.registerModelLayer(CHOCOBO_LAYER, AdultChocoboModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CHICOBO_LAYER, ChicoboModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.CHOCO_DISGUISE_ARMOR, ChocoDisguiseModel::getTexturedModelData);
    }
}
