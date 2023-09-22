package com.dephoegon.delchoco.client;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.client.models.entities.AdultChocoboModel;
import com.dephoegon.delchoco.client.models.entities.ChicoboModel;
import com.dephoegon.delchoco.common.init.ModItems;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class clientHandler {
    public static final EntityModelLayer CHOCOBO_LAYER = new EntityModelLayer(new Identifier(DelChoco.DELCHOCO_ID, "main"), "chocobo");
    public static final EntityModelLayer CHICOBO_LAYER = new EntityModelLayer(new Identifier(DelChoco.DELCHOCO_ID, "main"), "chicobo");
    public static final EntityModelLayer CHOCO_DISGUISE_LAYER = new EntityModelLayer(new Identifier(DelChoco.DELCHOCO_ID, "main"), "choco_disguise");

    public static void ChocoboRendering() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModItems.GYSAHL_GREEN, RenderLayer.getCutout());
        registerLayerDefinitions();
    }
    public static void registerLayerDefinitions() {
        EntityModelLayerRegistry.registerModelLayer(CHOCOBO_LAYER, AdultChocoboModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CHICOBO_LAYER, ChicoboModel::createBodyLayer);
    }
}
