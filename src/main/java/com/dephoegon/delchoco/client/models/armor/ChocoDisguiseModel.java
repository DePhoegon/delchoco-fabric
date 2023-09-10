package com.dephoegon.delchoco.client.models.armor;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.jetbrains.annotations.NotNull;

public class ChocoDisguiseModel extends BipedEntityModel<LivingEntity> {
    private final EquipmentSlot slot;
    private final ModelPart chocobo_head;
    private final ModelPart chocobo_body;
    private final ModelPart chocobo_right_arm;
    private final ModelPart chocobo_left_arm;
    private final ModelPart chocobo_leg_right;
    private final ModelPart chocobo_leg_left;
    private final ModelPart chocobo_claw_right;
    private final ModelPart chocobo_claw_left;

    public ChocoDisguiseModel(ModelPart root, EquipmentSlot slot) {
        super(root);
        this.slot = slot;
        chocobo_head = root.getChild("chocobo_head");
        chocobo_body = root.getChild("chocobo_body");
        chocobo_right_arm = root.getChild("chocobo_right_arm");
        chocobo_left_arm = root.getChild("chocobo_left_arm");
        chocobo_leg_right = root.getChild("chocobo_leg_right");
        chocobo_leg_left = root.getChild("chocobo_leg_left");
        chocobo_claw_right = root.getChild("chocobo_claw_right");
        chocobo_claw_left = root.getChild("chocobo_claw_left");
    }
    public static @NotNull TexturedModelData createArmorDefinition() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 1.0F);
        ModelPartData modelPartData = modelData.getRoot();

        //Head
        ModelPartData chocobo_head = modelPartData.addChild("chocobo_head",
                ModelPartBuilder.create()
                        .uv( 0, 18).cuboid(-4.5F, -8.5F, -4.5F, 9.0F, 9.0F, 9.0F)
                , ModelTransform.NONE);

        ModelPartData the_head = chocobo_head.addChild("the_head",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-3.2063F, -3.8667F, -9.081F, 6.0F, 6.0F, 12.0F)
                        .uv(36, 12).cuboid(2.0F, -3.0F, -3.0F, 1.0F, 3.0F, 3.0F)
                        .uv(36, 12).cuboid(-3.5F, -3.0F, -3.0F, 1.0F, 3.0F, 3.0F).mirrored()
                , ModelTransform.of(0.2063F, -10.1333F, -0.919F, 0.2618F, 0.0F, 0.0F));

        the_head.addChild("crest_right",
                ModelPartBuilder.create()
                        .uv(1, 0).cuboid(-0.5F, -4.5F, 0.0F, 1.0F, 7.0F, 4.0F).mirrored()
                , ModelTransform.of(-2.7063F, -0.3667F, 2.919F, 0.2094F, -0.384F, 0.2094F));

        the_head.addChild("crest_top",
                ModelPartBuilder.create()
                        .uv(25, 0).cuboid(-3.0F, -0.5F, 0.0F, 5.0F, 1.0F, 6.0F)
                , ModelTransform.of(0.2937F, -2.8667F, 2.919F, 0.4538F, 0.0F, 0.0F));

        the_head.addChild("crest_left",
                ModelPartBuilder.create()
                        .uv(1, 0).cuboid(-0.5F, -4.5F, 0.0F, 1.0F, 7.0F, 4.0F)
                , ModelTransform.of(2.2937F, -0.3667F, 2.919F, 0.2094F, 0.384F, -0.2094F));

        //Body
        ModelPartData chocobo_body = modelPartData.addChild("chocobo_body",
                ModelPartBuilder.create()
                        .uv( 36, 18).cuboid(-4.5F, -0.5F, -2.5F, 9.0F, 13.0F, 5.0F)
                , ModelTransform.NONE);

        chocobo_body.addChild("feathers_middle",
                ModelPartBuilder.create()
                        .uv( 102, 93).cuboid(-3.5F, 0.5F, -0.5F, 7.0F, 1.0F, 6.0F)
                , ModelTransform.of(-0.5F, 8.0F, 2.5F, 0.3491F, 0.0F, 0.0F));

        chocobo_body.addChild("feathers_left",
                ModelPartBuilder.create()
                        .uv( 44, 92).cuboid(1.5F, -8.5F, -0.5F, 1.0F, 14.0F, 9.0F)
                , ModelTransform.of(-0.5F, 8.0F, 2.5F, -0.2094F, 0.384F, 0.3491F));

        chocobo_body.addChild("feathers_right",
                ModelPartBuilder.create()
                        .uv( 44, 92).cuboid(1.5F, -8.5F, -0.5F, 1.0F, 14.0F, 9.0F)
                , ModelTransform.of(-0.5F, 8.0F, 2.5F, -0.2094F, -0.384F, -0.3491F));

        //Right arm
        modelPartData.addChild("chocobo_right_arm",
                ModelPartBuilder.create()
                        .uv( 0, 36).cuboid(-3.5F, -3.0F, -2.5F, 5.0F, 15.0F, 5.0F, new Dilation(-0.25F)).mirrored()
                , ModelTransform.NONE);

        //Left arm
        modelPartData.addChild("chocobo_left_arm",
                ModelPartBuilder.create()
                        .uv( 20, 36).cuboid(-1.5F, -2.5F, -2.5F, 5.0F, 15.0F, 5.0F, new Dilation(-0.25F)).mirrored()
                , ModelTransform.NONE);

        //Right leg
        modelPartData.addChild("chocobo_leg_right",
                ModelPartBuilder.create()
                        .uv( 0, 56).cuboid(-2.6F, -0.5F, -2.6F, 5.0F, 7.0F, 5.0F, new Dilation(0.125F)).mirrored()
                , ModelTransform.NONE);

        //Left leg
        modelPartData.addChild("chocobo_leg_left",
                ModelPartBuilder.create()
                        .uv( 0, 56).cuboid(-2.5F, -0.5F, -2.5F, 5.0F, 7.0F, 5.0F, new Dilation(0.125F)).mirrored()
                , ModelTransform.NONE);

        //Right Claw
        ModelPartData chocobo_claw_right = modelPartData.addChild("chocobo_claw_right",
                ModelPartBuilder.create()
                        .uv( 20, 56).cuboid(-2.5F, 6.25F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(-0.25F)).mirrored()
                , ModelTransform.NONE);

        chocobo_claw_right.addChild("claw_right",
                ModelPartBuilder.create()
                        .uv( 40, 37).cuboid(-1.0F, -1.25F, -6.5F, 2.0F, 2.0F, 7.0F)
                , ModelTransform.of(-1.0F, 11.0F, -0.5F, 0.0F, 0.3054F, 0.0F));
        chocobo_claw_right.addChild("claw_left",
                ModelPartBuilder.create()
                        .uv( 40, 37).cuboid(0.0F, -1.25F, -5.5F, 2.0F, 2.0F, 7.0F)
                , ModelTransform.of(-1.0F, 11.0F, -0.5F, 0.0F, -0.1745F, 0.0F));

        //Left Claw
        ModelPartData chocobo_claw_left = modelPartData.addChild("chocobo_claw_left",
                ModelPartBuilder.create()
                        .uv( 20, 56).cuboid(-2.4F, 6.25F, -2.6F, 5.0F, 6.0F, 5.0F, new Dilation(-0.25F)).mirrored()
                , ModelTransform.NONE);

        chocobo_claw_left.addChild("claw_right",
                ModelPartBuilder.create()
                        .uv( 40, 37).cuboid(-1.0F, -1.25F, -6.5F, 2.0F, 2.0F, 7.0F)
                , ModelTransform.of(1.0F, 11.0F, -0.5F, 0.0F, -0.3054F, 0.0F));
        chocobo_claw_left.addChild("claw_left",
                ModelPartBuilder.create()
                        .uv( 40, 37).cuboid(-2.0F, -1.25F, -5.5F, 2.0F, 2.0F, 7.0F)
                , ModelTransform.of(1.0F, 11.0F, -0.5F, 0.0F, 0.1745F, 0.0F));

        return TexturedModelData.of(modelData,  128,  128);
    }
    public void setAngles(@NotNull LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (livingEntity instanceof ArmorStandEntity armorStand) {
            this.chocobo_head.pitch = ((float) Math.PI / 180F) * armorStand.getHeadRotation().getPitch();
            this.chocobo_head.yaw = ((float) Math.PI / 180F) * armorStand.getHeadRotation().getYaw();
            this.chocobo_head.roll = ((float) Math.PI / 180F) * armorStand.getHeadRotation().getRoll();
            this.chocobo_head.setPivot(0.0F, 1.0F, 0.0F);
            this.chocobo_body.pitch = ((float) Math.PI / 180F) * armorStand.getBodyRotation().getPitch();
            this.chocobo_body.yaw = ((float) Math.PI / 180F) * armorStand.getBodyRotation().getYaw();
            this.chocobo_body.roll = ((float) Math.PI / 180F) * armorStand.getBodyRotation().getRoll();
            this.chocobo_left_arm.pitch = ((float) Math.PI / 180F) * armorStand.getLeftArmRotation().getPitch();
            this.chocobo_left_arm.yaw = ((float) Math.PI / 180F) * armorStand.getLeftArmRotation().getYaw();
            this.chocobo_left_arm.roll = ((float) Math.PI / 180F) * armorStand.getLeftArmRotation().getRoll();
            this.chocobo_right_arm.pitch = ((float) Math.PI / 180F) * armorStand.getRightArmRotation().getPitch();
            this.chocobo_right_arm.yaw = ((float) Math.PI / 180F) * armorStand.getRightArmRotation().getYaw();
            this.chocobo_right_arm.roll = ((float) Math.PI / 180F) * armorStand.getRightArmRotation().getRoll();
            this.chocobo_leg_left.pitch = ((float) Math.PI / 180F) * armorStand.getLeftLegRotation().getPitch();
            this.chocobo_leg_left.yaw = ((float) Math.PI / 180F) * armorStand.getLeftLegRotation().getYaw();
            this.chocobo_leg_left.roll = ((float) Math.PI / 180F) * armorStand.getLeftLegRotation().getRoll();
            this.chocobo_leg_left.setPivot(1.9F, 11.0F, 0.0F);
            this.chocobo_leg_right.pitch = ((float) Math.PI / 180F) * armorStand.getRightLegRotation().getPitch();
            this.chocobo_leg_right.yaw = ((float) Math.PI / 180F) * armorStand.getRightLegRotation().getYaw();
            this.chocobo_leg_right.roll = ((float) Math.PI / 180F) * armorStand.getRightLegRotation().getRoll();
            this.chocobo_leg_right.setPivot(-1.9F, 11.0F, 0.0F);
            this.hat.copyTransform(this.chocobo_head);
        } else { super.setAngles(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch); }
    }
    public void render(@NotNull MatrixStack matrixStack, @NotNull VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        matrixStack.push();
        this.setHeadRotation();
        this.setChestRotation();
        this.setLegsRotation();
        this.setBootRotation();

        chocobo_head.visible = slot == EquipmentSlot.HEAD;
        chocobo_body.visible = slot == EquipmentSlot.CHEST;
        chocobo_right_arm.visible = slot == EquipmentSlot.CHEST;
        chocobo_left_arm.visible = slot == EquipmentSlot.CHEST;
        chocobo_leg_right.visible = slot == EquipmentSlot.LEGS;
        chocobo_leg_left.visible = slot == EquipmentSlot.LEGS;
        chocobo_claw_right.visible = slot == EquipmentSlot.FEET;
        chocobo_claw_left.visible = slot == EquipmentSlot.FEET;
        if (this.child) {
            float f = 2.0F;
            matrixStack.scale(1.5F / f, 1.5F / f, 1.5F / f);
            matrixStack.translate(0.0F, 16.0F * 1, 0.0F);
            chocobo_head.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            matrixStack.pop();
            matrixStack.push();
            matrixStack.scale(1.0F / f, 1.0F / f, 1.0F / f);
            matrixStack.translate(0.0F, 24.0F * 1, 0.0F);
            chocobo_body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        } else {
            chocobo_head.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            chocobo_body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            chocobo_right_arm.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            chocobo_left_arm.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
        chocobo_leg_right.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        chocobo_leg_left.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        chocobo_claw_right.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        chocobo_claw_left.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        matrixStack.pop();
    }
    public void setHeadRotation() {
        chocobo_head.pitch = head.pitch;
        chocobo_head.yaw = head.yaw;
        chocobo_head.roll = head.roll;
        setRotation(chocobo_head, head.pitch, head.yaw, head.roll);
    }
    public void setChestRotation() {
        /* if (e instanceof EntityPlayer){ ((EntityPlayer)e).get } */
        chocobo_body.pitch = body.pitch;
        chocobo_body.yaw = body.yaw;
        chocobo_body.roll = body.roll;
        chocobo_right_arm.pitch = rightArm.pitch;
        chocobo_right_arm.yaw = rightArm.yaw;
        chocobo_right_arm.roll = rightArm.roll;
        chocobo_left_arm.pitch = leftArm.pitch;
        chocobo_left_arm.yaw = leftArm.yaw;
        chocobo_left_arm.roll = leftArm.roll;
        setRotation(chocobo_body, body.pitch, body.yaw, body.roll);
        setRotation(chocobo_right_arm, rightArm.pitch, rightArm.yaw, rightArm.roll);
        setRotation(chocobo_left_arm, leftArm.pitch, leftArm.yaw, leftArm.roll);
    }
    public void setLegsRotation() {
        chocobo_leg_right.pitch = rightLeg.pitch;
        chocobo_leg_right.yaw = rightLeg.yaw;
        chocobo_leg_right.roll = rightLeg.roll;
        chocobo_leg_left.pitch = leftLeg.pitch;
        chocobo_leg_left.yaw = leftLeg.yaw;
        chocobo_leg_left.roll = leftLeg.roll;
        setRotation(chocobo_leg_right, rightLeg.pitch, rightLeg.yaw, rightLeg.roll);
        setRotation(chocobo_leg_left, leftLeg.pitch, leftLeg.yaw, leftLeg.roll);
    }
    public void setBootRotation() {
        chocobo_claw_right.pitch = rightLeg.pitch;
        chocobo_claw_right.yaw = rightLeg.yaw;
        chocobo_claw_right.roll = rightLeg.roll;
        chocobo_claw_left.pitch = leftLeg.pitch;
        chocobo_claw_left.yaw = leftLeg.yaw;
        chocobo_claw_left.roll = leftLeg.roll;
        setRotation(chocobo_claw_right, rightLeg.pitch, rightLeg.yaw, rightLeg.roll);
        setRotation(chocobo_claw_left, leftLeg.pitch, leftLeg.yaw, leftLeg.roll);
    }
    public void setRotation(@NotNull ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.pitch = x;
        modelRenderer.yaw = y;
        modelRenderer.roll = z;
    }
}