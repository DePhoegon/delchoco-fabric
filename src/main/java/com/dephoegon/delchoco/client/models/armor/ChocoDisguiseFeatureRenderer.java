package com.dephoegon.delchoco.client.models.armor;

import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

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
}