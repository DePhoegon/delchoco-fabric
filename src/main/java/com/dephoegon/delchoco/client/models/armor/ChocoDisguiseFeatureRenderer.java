package com.dephoegon.delchoco.client.models.armor;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import static com.dephoegon.delchoco.common.init.ModArmorMaterial.*;
import static com.dephoegon.delchoco.common.items.ChocoDisguiseItem.*;

public class ChocoDisguiseFeatureRenderer extends GeoArmorRenderer<ChocoDisguiseItem> {
    public ChocoDisguiseFeatureRenderer() {
        super(new ChocoDisguiseModel());

        this.head = getHeadBone();
        this.body = getBodyBone();
        this.leftArm = getLeftArmBone();
        this.rightArm = getRightArmBone();
        this.leftLeg = getLeftLegBone();
        this.rightLeg = getRightLegBone();
        this.leftBoot = getLeftBootBone();
        this.rightBoot = getRightBootBone();
    }

    public Identifier getTextureLocation(ChocoDisguiseItem instance) {
        String customModelData = ChocoDisguiseItem.getNBTKEY_COLOR(this.currentStack);
        ArmorMaterial armor = instance.getMaterial();
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