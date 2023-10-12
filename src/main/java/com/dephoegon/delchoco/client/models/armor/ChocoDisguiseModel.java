package com.dephoegon.delchoco.client.models.armor;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;

public class ChocoDisguiseModel extends GeoModel<ChocoDisguiseItem> {
    @Override
    public Identifier getModelResource(ChocoDisguiseItem object) {
        return new Identifier(DelChoco.DELCHOCO_ID, "geo/choco_disguise.geo.json");
    }

    public Identifier getTextureResource(@NotNull ChocoDisguiseItem object) {
        // Default texture location, Overrides in ChocoDisguiseFeatureRenderer.java with itemStack NBT
        return new Identifier(DelChoco.DELCHOCO_ID, "textures/models/armor/leather/yellow.png");
    }
    public Identifier getAnimationResource(ChocoDisguiseItem animatable) {
        return new Identifier(DelChoco.DELCHOCO_ID, "animations/armor_holder.json");
    }
}