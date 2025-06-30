package com.dephoegon.delchoco.client.models.armor;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

public class ChocoDisguiseModel extends BipedEntityModel<LivingEntity> {
    public final ModelPart armorLeftBoot;
    public final ModelPart armorRightBoot;
    public ChocoDisguiseModel(ModelPart root) {
        super(root);
        this.armorLeftBoot = this.leftLeg.getChild("armorLeftBoot");
        this.armorRightBoot = this.rightLeg.getChild("armorRightBoot");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData decoHead = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 18).cuboid(-4.5F, -9.0F, -4.5F, 9.0F, 9.0F, 9.0F), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData decoHat = decoHead.addChild("hat", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData cubes = decoHat.addChild("cubes", ModelPartBuilder.create(), ModelTransform.of(-0.2063F, -8.867F, -0.919F, 0.0827F, 0.0F, 0.0F));
        cubes.addChild("choco_head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-2.5874F, -0.06439F, -8.14041F, 6.0F, 6.0F, 12.0F)
                .uv(36, 12).cuboid(2.7063F, 1.93561F, -2.05941F, 1.0F, 3.0F, 3.0F)
                .uv(36, 12).mirrored().cuboid(-2.7937F, 1.93561F, -2.05941F, 1.0F, 3.0F, 3.0F), ModelTransform.NONE);
        cubes.addChild("right_fin", ModelPartBuilder.create().uv(0, 1).cuboid(2.0F, -0.19739F, 3.02159F, 1.0F, 7.0F, 4.0F), ModelTransform.of(0,0,0, -0.2094F, 2.884F, 0.2094F));
        cubes.addChild("left_fin", ModelPartBuilder.create().uv(0, 1).mirrored().cuboid(-2.75F, -0.19739F, 3.02159F, 1.0F, 7.0F, 4.0F), ModelTransform.of(0,0,0, -0.2094F, -7.884F, -0.2094F));
        cubes.addChild("top_fin", ModelPartBuilder.create().uv(25, 0).cuboid(-2.0F, 3.800F, 5.0F, 5.0F, 1.0F, 6.0F), ModelTransform.of(0,0,0, 0.2974F, 0, 0));

        modelPartData.addChild("body", ModelPartBuilder.create().uv(36, 18).cuboid(-4.5F, 0.0F, -2.5F, 9.0F, 13.0F, 5.0F), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        modelPartData.addChild("rightArm", ModelPartBuilder.create().uv(20, 36).mirrored().cuboid(-3.5F, -2.1F, -2.5F, 5.0F, 15.0F, 5.0F), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));
        modelPartData.addChild("leftArm", ModelPartBuilder.create().uv(0, 36).mirrored().cuboid(-1.5F, -2.1F, -2.5F, 5.0F, 15.0F, 5.0F), ModelTransform.pivot(5.0F, 2.0F, 0.0F));

        ModelPartData rightLeg = modelPartData.addChild("rightLeg", ModelPartBuilder.create().uv(0, 56).mirrored().cuboid(-2.5F, 4.0F, -2.5F, 5.0F, 7.0F, 5.0F), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));
        ModelPartData armorRightBoot = rightLeg.addChild("armorRightBoot", ModelPartBuilder.create()
                        .uv(20, 56).mirrored().cuboid(-2.5F, -0.5F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(-0.49F))
                , ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        armorRightBoot.addChild("right_talon_1", ModelPartBuilder.create().uv(40, 37).cuboid(-0.5F, 0.0F, -5.5F, 2.0F, 2.0F, 7.0F), ModelTransform.of(0,0,0,0,-0.0873F,0));
        armorRightBoot.addChild("right_talon_2", ModelPartBuilder.create().uv(40, 37).cuboid(-1.25F, 0.0F, -7.0F, 2.0F, 2.0F, 7.0F), ModelTransform.of(0,0,0,0,0.2618F,0));

        ModelPartData leftLeg = modelPartData.addChild("leftLeg", ModelPartBuilder.create().uv(0, 56).cuboid(-2.5F, 4.0F, -2.5F, 5.0F, 7.0F, 5.0F), ModelTransform.pivot(1.9F, 12.0F, 0.0F));
        ModelPartData armorLeftBoot = leftLeg.addChild("armorLeftBoot", ModelPartBuilder.create()
                        .uv(20, 56).mirrored().cuboid(-2.5F, -0.5F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(-0.49F))
                , ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        armorLeftBoot.addChild("left_talon_1", ModelPartBuilder.create().uv(40, 37).cuboid(-1.75F, 0.0F, -5.75F, 2.0F, 2.0F, 7.0F), ModelTransform.of(0,0,0,0,0.0436F,0));
        armorLeftBoot.addChild("left_talon_2", ModelPartBuilder.create().uv(40, 37).cuboid(-3.5F, 0.0F, -7.0F, 2.0F, 2.0F, 7.0F), ModelTransform.of(0,0,0,0,-0.2618F,0));

        return TexturedModelData.of(modelData, 128, 128);
    }
}