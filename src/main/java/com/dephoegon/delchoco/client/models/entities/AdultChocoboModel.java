package com.dephoegon.delchoco.client.models.entities;

import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class AdultChocoboModel<T extends Chocobo> extends EntityModel<Chocobo> {
    private final ModelPart root;
    private final ModelPart wing_left;
    private final ModelPart wing_right;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart leg_left;
    private final ModelPart leg_right;
    public AdultChocoboModel(@NotNull ModelPart root) {
        this.root = root.getChild("root");
        ModelPart body = this.root.getChild("body");

        this.neck = body.getChild("chest").getChild("neck");
        this.head = neck.getChild("head");
        this.leg_left = body.getChild("leg_left");
        this.leg_right = body.getChild("leg_right");
        this.wing_left = body.getChild("wing_left");
        this.wing_right = body.getChild("wing_right");
    }
    public static @NotNull TexturedModelData createBodyLayer() {
        ModelData meshDefinition = new ModelData();
        ModelPartData ModelPartData = meshDefinition.getRoot();

        ModelPartData root = ModelPartData.addChild("root", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 24.0F, -4.0F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -22.0F, 1.0F));

        ModelPartData saddlebag_right_r1 = body.addChild("saddlebag_right_r1", ModelPartBuilder.create()
                        .uv(81, 6).mirrored().cuboid(-7.5F, -5.0F, -3.0F, 2.0F, 8.0F, 6.0F,
                                new Dilation(-0.1F)).mirrored(false)
                        .uv(81, 6).cuboid(5.5F, -5.0F, -3.0F, 2.0F, 8.0F, 6.0F,
                                new Dilation(-0.1F)),
                ModelTransform.of(0.0F, -3.0F, 6.0F, -0.0873F, 0.0F, 0.0F));

        ModelPartData storage_back_r1 = body.addChild("storage_back_r1", ModelPartBuilder.create()
                        .uv(36, 2).cuboid(-5.0F, -6.0F, -3.0F, 10.0F, 6.0F, 12.0F,
                                new Dilation(-0.1F)),
                ModelTransform.of(0.0F, -7.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

        ModelPartData fan_bottom_r1 = body.addChild("fan_bottom_r1", ModelPartBuilder.create()
                        .uv(101, 29).cuboid(-3.5F, -4.5F, 0.0F, 7.0F, 6.0F, 0.0F),
                ModelTransform.of(0.0F, 0.0F, 9.0F, -0.9163F, 0.0F, 0.0F));

        ModelPartData fan_top_r1 = body.addChild("fan_top_r1", ModelPartBuilder.create()
                        .uv(55, 25).cuboid(-5.5F, -10.0F, -2.0F, 11.0F, 10.0F, 0.0F),
                ModelTransform.of(0.0F, -5.0F, 10.0F, -0.5672F, 0.0F, 0.0F));

        ModelPartData fan_right_r1 = body.addChild("fan_right_r1", ModelPartBuilder.create()
                        .uv(45, 28).cuboid(-1.0F, -11.0F, -1.0F, 0.0F, 14.0F, 9.0F),
                ModelTransform.of(-2.0F, 0.0F, 9.0F, -0.1309F, -0.7418F, -0.1309F));

        ModelPartData fan_left_r1 = body.addChild("fan_left_r1", ModelPartBuilder.create()
                        .uv(45, 28).cuboid(1.0F, -11.0F, -1.0F, 0.0F, 14.0F, 9.0F),
                ModelTransform.of(2.0F, 0.0F, 9.0F, -0.1309F, 0.7418F, 0.1309F));

        ModelPartData body_r1 = body.addChild("body_r1", ModelPartBuilder.create()
                        .uv(0, 36).cuboid(-6.0F, -7.0F, -8.0F, 12.0F, 11.0F, 16.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

        ModelPartData armor_body_r1 = body.addChild("armor_body_r1", ModelPartBuilder.create()
                        .uv(0, 100).cuboid(-6.0F, -7.0F, -8.0F, 12.0F, 11.0F, 16.0F,
                                new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

        ModelPartData wing_left = body.addChild("wing_left", ModelPartBuilder.create(),
                ModelTransform.pivot(6.0F, -5.0F, -3.0F));

        ModelPartData wing_left_r1 = wing_left.addChild("wing_left_r1", ModelPartBuilder.create()
                        .uv(83, 21).cuboid(0.0F, -2.0F, -3.0F, 1.0F, 10.0F, 16.0F,
                                new Dilation(0.1F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.0436F, 0.0436F, -0.0873F));

        ModelPartData wing_right = body.addChild("wing_right", ModelPartBuilder.create(),
                ModelTransform.pivot(-6.0F, -5.0F, -3.0F));

        ModelPartData wing_right_r2 = wing_right.addChild("wing_right_r2", ModelPartBuilder.create()
                        .uv(83, 21).cuboid(0.0F, -2.0F, -3.0F, 1.0F, 10.0F, 16.0F,
                                new Dilation(0.1F)),
                ModelTransform.of(-1.0F, 0.0F, 0.0F, -0.0436F, -0.0436F, 0.0873F));

        ModelPartData chest = body.addChild("chest", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -4.0F, -6.0F));

        ModelPartData chest_r1 = chest.addChild("chest_r1", ModelPartBuilder.create()
                        .uv(0, 18).cuboid(-4.0F, -4.0F, -5.0F, 8.0F, 8.0F, 10.0F,
                                new Dilation(0.1F)),
                ModelTransform.of(0.0F, -1.0F, -1.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData armor_chest_r1 = chest.addChild("armor_chest_r1", ModelPartBuilder.create()
                        .uv(0, 82).cuboid(-4.0F, -4.0F, -5.0F, 8.0F, 8.0F, 10.0F,
                                new Dilation(0.35F)),
                ModelTransform.of(0.0F, -1.0F, -1.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData neck = chest.addChild("neck", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -4.0F, -2.0F));

        ModelPartData neck_r1 = neck.addChild("neck_r1", ModelPartBuilder.create()
                        .uv(36, 20).cuboid(-2.0F, -12.0F, -3.0F, 4.0F, 12.0F, 4.0F,
                                new Dilation(0.2F)),
                ModelTransform.of(0.0F, 1.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData armor_neck_r1 = neck.addChild("armor_neck_r1", ModelPartBuilder.create()
                        .uv(36, 20).cuboid(-2.0F, -12.0F, -3.0F, 4.0F, 12.0F, 4.0F,
                                new Dilation(0.45F)),
                ModelTransform.of(0.0F, 1.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData head = neck.addChild("head", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-3.0F, -16.0F, -9.0F, 6.0F, 6.0F, 12.0F)
                        .uv(100, 0).cuboid(2.25F, -15.0F, -3.0F, 1.0F, 3.0F, 3.0F)
                        .uv(100, 0).cuboid(-3.25F, -15.0F, -3.0F, 1.0F, 3.0F, 3.0F).mirrored(),
                ModelTransform.pivot(0.0F, -11.0F, -2.0F));

        ModelPartData crest_top_r1 = head.addChild("crest_top_r1", ModelPartBuilder.create()
                        .uv(20, 0).cuboid(-2.5F, -11.0F, 2.0F, 5.0F, 0.0F, 6.0F),
                ModelTransform.of(0.0F, -5.0F, 3.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData crest_right_r1 = head.addChild("crest_right_r1", ModelPartBuilder.create()
                .uv(2, 2).mirrored().cuboid(0.0F, -16.0F, 0.0F, 0.0F, 6.0F, 4.0F).mirrored(false), ModelTransform.of(-3.0F, 0.0F, 3.0F, 0.0F, -0.2182F, 0.0F));

        ModelPartData crest_left_r1 = head.addChild("crest_left_r1", ModelPartBuilder.create()
                        .uv(2, 2).cuboid(0.0F, -16.0F, 0.0F, 0.0F, 6.0F, 4.0F),
                ModelTransform.of(3.0F, 0.0F, 3.0F, 0.0F, 0.2182F, 0.0F));

        ModelPartData leg_left = body.addChild("leg_left", ModelPartBuilder.create(),
                ModelTransform.pivot(3.5F, 3.0F, 1.0F));

        ModelPartData leg_left_lower_r1 = leg_left.addChild("leg_left_lower_r1", ModelPartBuilder.create()
                        .uv(79, 48).cuboid(-5.5F, -2.0F, 1.0F, 3.0F, 12.0F, 3.0F,
                                new Dilation(0.1F)),
                ModelTransform.of(0.0F, 8.0F, 0.0F, -0.3927F, 0.0F, 0.0F));
        ModelPartData armor_leg_left_lower_r1 = leg_left.addChild("armor_leg_left_lower_r1", ModelPartBuilder.create()
                        .uv(79, 112).cuboid(-5.5F, -2.0F, 1.0F, 3.0F, 12.0F, 3.0F,
                                new Dilation(0.35F)),
                ModelTransform.of(0.0F, 8.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData leg_left_upper_r1 = leg_left.addChild("leg_left_upper_r1", ModelPartBuilder.create()
                        .uv(60, 49).cuboid(-6.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData armor_leg_left_upper_r1 = leg_left.addChild("armor_leg_left_upper_r1", ModelPartBuilder.create()
                        .uv(60, 113).cuboid(-6.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData foot_left = leg_left.addChild("foot_left", ModelPartBuilder.create()
                        .uv(104, 53).cuboid(-5.0F, -2.0F, 1.5F, 2.0F, 2.0F, 5.0F),
                ModelTransform.of(0.0F, 19.0F, -2.0F, 0.0F, -0.2182F, 0.0F));

        ModelPartData toe_right_r1 = foot_left.addChild("toe_right_r1", ModelPartBuilder.create()
                        .uv(92, 54).cuboid(-6.0F, -3.0F, -5.0F, 2.0F, 2.0F, 7.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2182F, 0.2618F, 0.0F));

        ModelPartData toe_left_r1 = foot_left.addChild("toe_left_r1", ModelPartBuilder.create()
                        .uv(92, 54).cuboid(-4.0F, -3.0F, -5.0F, 2.0F, 2.0F, 7.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2182F, -0.2618F, 0.0F));

        ModelPartData leg_right = body.addChild("leg_right", ModelPartBuilder.create(),
                ModelTransform.pivot(-3.5F, 3.0F, 1.0F));

        ModelPartData leg_right_lower_r1 = leg_right.addChild("leg_right_lower_r1", ModelPartBuilder.create()
                        .uv(79, 48).cuboid(2F, -2.0F, 1.0F, 3.0F, 12.0F, 3.0F,
                                new Dilation(0.1F)),
                ModelTransform.of(0.0F, 8.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData armor_leg_right_lower_r1 = leg_right.addChild("armor_leg_right_lower_r1", ModelPartBuilder.create()
                        .uv(79, 112).cuboid(2F, -2.0F, 1.0F, 3.0F, 12.0F, 3.0F,
                                new Dilation(0.35F)),
                ModelTransform.of(0.0F, 8.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData leg_right_upper_r1 = leg_right.addChild("leg_right_upper_r1", ModelPartBuilder.create()
                        .uv(60, 49).cuboid(1.5F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData armor_leg_right_upper_r1 = leg_right.addChild("armor_leg_right_upper_r1", ModelPartBuilder.create()
                        .uv(60, 113).cuboid(1.5F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData foot_right = leg_right.addChild("foot_right", ModelPartBuilder.create()
                        .uv(104, 53).cuboid(2.0F, -2.0F, 1.5F, 2.0F, 2.0F, 5.0F),
                ModelTransform.of(0.0F, 19.0F, -2.0F, 0.0F, 0.2182F, 0.0F));

        ModelPartData toe_right_r2 = foot_right.addChild("toe_right_r2", ModelPartBuilder.create()
                        .uv(92, 54).cuboid(1.0F, -3.0F, -5.0F, 2.0F, 2.0F, 7.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2182F, 0.2618F, 0.0F));

        ModelPartData toe_left_r2 = foot_right.addChild("toe_left_r2", ModelPartBuilder.create()
                        .uv(92, 54).cuboid(3.0F, -3.0F, -5.0F, 2.0F, 2.0F, 7.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2182F, -0.2618F, 0.0F));

        return TexturedModelData.of(meshDefinition, 128, 128);
    }
    public void render(@NotNull MatrixStack poseStack, @NotNull VertexConsumer consumer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { this.root.render(poseStack, consumer, packedLightIn, packedOverlayIn, red, green, blue, alpha); }
    private void setRotateAngle(@NotNull ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.pivotX = x;
        modelRenderer.pivotY = y;
        modelRenderer.pivotZ = z;
    }
    public void setAngles(@NotNull Chocobo entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // ageInTicks = wing z movement (flutter)
        // netHeadYaw = head y movement
        // headPitch = head x movement
        // cos(limbSwing) and limbSwingAmount = leg movement

        float pi = (float) Math.PI;

        // head/neck movement
        head.pivotX = headPitch * (pi / 180F);
        head.pivotY = netHeadYaw * (pi / 180F);
        neck.pivotX = (-0.8F*(netHeadYaw * (pi / 180F)))/8;
        neck.pivotY = (netHeadYaw * (pi / 180F))/9;

        // walking animation
        this.setRightLegXRotation(MathHelper.cos(limbSwing * 0.6662F) * 0.8F * limbSwingAmount);
        this.setLeftLegXRotation(MathHelper.cos(limbSwing * 0.6662F + pi) * 0.8F * limbSwingAmount);

        // riding animation
        Vec3d motion = entityIn.getVelocity();
//		if (Math.abs(motion.x) > 0.1F || Math.abs(motion.z) > 0.1F) {
//			neck.xRot = -0.5F;
//		} else {
//			neck.xRot = 0.8F;
//		}

        // flying animation
        if (Math.abs(motion.y) > 0.1F || !entityIn.isOnGround()) {
            setRotateAngle(wing_right, (pi / 2F) - (pi / 12), -0.0174533F, -90 + MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount);
            setRotateAngle(wing_left, (pi / 2F) - (pi / 12), 0.0174533F, 90 + MathHelper.cos(limbSwing * 0.6662F + pi) * 1.4F * limbSwingAmount);
            this.setLeftLegXRotation(0.6F);
            this.setRightLegXRotation(0.6F);
        } else {
            // reset wings
            setRotateAngle(wing_right, 0F, -0.0174533F, 0F);
            setRotateAngle(wing_left, 0F, 0.0174533F, 0F);
        }
    }
    private void setLeftLegXRotation(float deltaX) { leg_left.pivotX = 0.2094395F + deltaX; }
    private void setRightLegXRotation(float deltaX) { leg_right.pivotX = 0.2094395F + deltaX; }
}
