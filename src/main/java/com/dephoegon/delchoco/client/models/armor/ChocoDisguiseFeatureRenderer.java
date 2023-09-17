package com.dephoegon.delchoco.client.models.armor;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import static com.dephoegon.delchoco.common.init.ModArmorMaterial.*;
import static com.dephoegon.delchoco.common.items.ChocoDisguiseItem.*;

public class ChocoDisguiseFeatureRenderer extends GeoArmorRenderer<ChocoDisguiseItem> {
    public ChocoDisguiseFeatureRenderer() {
        super(new ChocoDisguiseModel());

        this.headBone = "armorHead";
        this.bodyBone = "armorBody";
        this.leftArmBone = "armorRightArm";
        this.rightArmBone = "armorLeftArm";
        this.rightLegBone = "armorLeftLeg";
        this.leftLegBone = "armorRightLeg";
        this.rightBootBone = "armorLeftBoot";
        this.leftBootBone = "armorRightBoot";
    }

    public Identifier getTextureLocation(ChocoDisguiseItem instance) {
        String customModelData = ChocoDisguiseItem.getNBTKEY_COLOR(this.itemStack);
        ArmorMaterial armor = ((ChocoDisguiseItem) instance).getMaterial();
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
}