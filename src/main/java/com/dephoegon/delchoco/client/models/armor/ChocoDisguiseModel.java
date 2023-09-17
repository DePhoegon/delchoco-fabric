package com.dephoegon.delchoco.client.models.armor;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static com.dephoegon.delchoco.common.init.ModArmorMaterial.*;
import static com.dephoegon.delchoco.common.items.ChocoDisguiseItem.*;

public class ChocoDisguiseModel extends AnimatedGeoModel<ChocoDisguiseItem> {
    @Override
    public Identifier getModelLocation(ChocoDisguiseItem object) {
        return new Identifier(DelChoco.DELCHOCO_ID, "geo/choco_disguise.geo.json");
    }

    public Identifier getTextureLocation(@NotNull ChocoDisguiseItem object) {
        String customModelData = object.getNBTKEY_COLOR();
        ArmorMaterial armor = object.getMaterial();
        String folder = "textures/models/armor/leather/";
        if (armor == IRON_CHOCO_DISGUISE) { folder = "textures/models/armor/iron/"; }
        if (armor == DIAMOND_CHOCO_DISGUISE) { folder = "textures/models/armor/diamond/"; }
        if (armor == NETHERITE_CHOCO_DISGUISE) { folder = "textures/models/armor/netherite/"; }
        String check = folder;
        if (customModelData.equals(green)) { folder = folder + "green.png"; }
        if (customModelData.equals(pink)) { folder = folder + "pink.png"; }
        if (customModelData.equals(red)) { folder = folder + "red.png"; }
        if (customModelData.equals(blue)) { folder = folder + "blue.png"; }
        if (customModelData.equals(gold)) { folder = folder + "gold.png"; }
        if (customModelData.equals(black)) { folder = folder + "black.png"; }
        if (customModelData.equals(flame)) { folder = folder + "flame.png"; }
        if (customModelData.equals(white)) { folder = folder + "white.png"; }
        if (customModelData.equals(purple)) { folder = folder + "purple.png"; }
        if (check.equals(folder)) { folder = folder + "yellow.png"; }
        return new Identifier(DelChoco.DELCHOCO_ID, folder);
    }
    public Identifier getAnimationFileLocation(ChocoDisguiseItem animatable) {
        return new Identifier(DelChoco.DELCHOCO_ID, "animations/armor_holder.json");
    }
}