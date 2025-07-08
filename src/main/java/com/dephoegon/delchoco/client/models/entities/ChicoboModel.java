package com.dephoegon.delchoco.client.models.entities;

import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class ChicoboModel<T extends Chocobo> extends EntityModel<Chocobo> {
    // Adding constant for conversion from degrees to radians
    private static final float DEG_TO_RAD = (float) Math.PI / 180.0F;

    private final ModelPart root;

    protected final ModelPart head;
    protected final ModelPart leg_left;
    protected final ModelPart leg_right;
    protected final ModelPart left_wing;
    protected final ModelPart right_wing;
    public ChicoboModel(@NotNull ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.leg_left = this.root.getChild("leg_left");
        this.leg_right = this.root.getChild("leg_right");
        this.left_wing = this.root.getChild("wing_left");
        this.right_wing = this.root.getChild("wing_right");
    }
    public static @NotNull TexturedModelData createBodyLayer() {
        ModelData meshDefinition = new ModelData();
        ModelPartData partDefinition = meshDefinition.getRoot();

        ModelPartData root = partDefinition.addChild("root", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 19.0F, 0.0F));

        ModelPartData fan_bottom_r1 = root.addChild("fan_bottom_r1", ModelPartBuilder.create()
                        .uv(16, 26).cuboid(-1.5F, -2.5F, 2.0F, 3.0F, 3.0F, 0.0F),
                ModelTransform.of(0.0F, -5.0F, 4.0F, -1.0036F, 0.0F, 0.0F));

        ModelPartData fan_top_r1 = root.addChild("fan_top_r1", ModelPartBuilder.create()
                        .uv(5, 25).cuboid(-2.5F, -4.5F, 0.0F, 5.0F, 4.0F, 0.0F),
                ModelTransform.of(0.0F, -5.0F, 4.0F, -0.6981F, 0.0F, 0.0F));

        ModelPartData body_r1 = root.addChild("body_r1", ModelPartBuilder.create()
                        .uv(30, 14).cuboid(-4.0F, -7.5F, -4.0F, 8.0F, 9.0F, 9.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData leg_left = root.addChild("leg_left", ModelPartBuilder.create(),
                ModelTransform.of(2.0F, 1.0F, 0.0F, 0.0F, -0.1745F, 0.0F));

        ModelPartData toe_right_r1 = leg_left.addChild("toe_right_r1", ModelPartBuilder.create()
                        .uv(1, 17).cuboid(-1.0F, -1.5F, -2.75F, 1.0F, 1.0F, 4.0F,
                                new Dilation(0.2F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.1309F, 0.2182F, 0.0F));

        ModelPartData toe_left_r1 = leg_left.addChild("toe_left_r1", ModelPartBuilder.create()
                        .uv(1, 17).cuboid(0.0F, -1.5F, -3.0F, 1.0F, 1.0F, 4.0F,
                                new Dilation(0.2F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.1309F, -0.2182F, 0.0F));

        ModelPartData toe_back_r1 = leg_left.addChild("toe_back_r1", ModelPartBuilder.create()
                        .uv(0, 10).cuboid(-0.5F, -1.0F, -2.5F, 1.0F, 1.0F, 3.0F,
                                new Dilation(0.2F)),
                ModelTransform.of(0.0F, 4.0F, 1.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData leg_r1 = leg_left.addChild("leg_r1", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 5.0F, 2.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

        ModelPartData leg_right = root.addChild("leg_right", ModelPartBuilder.create(),
                ModelTransform.of(-2.0F, 1.0F, 0.0F, 0.0F, 0.1745F, 0.0F));

        ModelPartData toe_right_r2 = leg_right.addChild("toe_right_r2", ModelPartBuilder.create()
                        .uv(1, 17).cuboid(-1.0F, -1.5F, -3.0F, 1.0F, 1.0F, 4.0F,
                                new Dilation(0.2F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.1309F, 0.2182F, 0.0F));

        ModelPartData toe_left_r2 = leg_right.addChild("toe_left_r2", ModelPartBuilder.create()
                        .uv(1, 17).cuboid(0.0F, -1.5F, -3.0F, 1.0F, 1.0F, 4.0F,
                                new Dilation(0.2F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.1309F, -0.2182F, 0.0F));

        ModelPartData toe_back_r2 = leg_right.addChild("toe_back_r2", ModelPartBuilder.create()
                        .uv(0, 10).cuboid(-0.5F, -1.0F, -2.5F, 1.0F, 1.0F, 3.0F,
                                new Dilation(0.2F)),
                ModelTransform.of(0.0F, 4.0F, 1.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData leg_r2 = leg_right.addChild("leg_r2", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 5.0F, 2.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

        ModelPartData head = root.addChild("head", ModelPartBuilder.create()
                        .uv(42, 0).cuboid(-2.5F, -4.5F, -5.5F, 5.0F, 6.0F, 6.0F,
                                new Dilation(0.2F)),
                ModelTransform.pivot(0.0F, -6.0F, -2.0F));

        ModelPartData crest_r1 = head.addChild("crest_r1", ModelPartBuilder.create()
                        .uv(16, 26).cuboid(-1.5F, -4.0F, 1.0F, 3.0F, 3.0F, 0.0F),
                ModelTransform.of(0.0F, -5.0F, -1.0F, -1.0472F, 0.0F, 0.0F));

        ModelPartData beak_r1 = head.addChild("beak_r1", ModelPartBuilder.create()
                        .uv(22, 5).cuboid(-2.5F, -3.0F, -2.0F, 5.0F, 4.0F, 3.0F),
                ModelTransform.of(0.0F, 0.0F, -6.0F, -0.0873F, 0.0F, 0.0F));

        return TexturedModelData.of(meshDefinition, 64, 32);
    }
    @Override
    public void setAngles(Chocobo entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Convert angles directly using the constant instead of magic numbers
        head.pitch = -headPitch * DEG_TO_RAD;
        head.yaw = netHeadYaw * DEG_TO_RAD;

        float limbSwingFactor = limbSwing * 0.6662F;
        leg_right.pitch = MathHelper.cos(limbSwingFactor) * 1.4F * limbSwingAmount;
        leg_left.yaw = MathHelper.cos(limbSwingFactor + MathHelper.PI) * 1.4F * limbSwingAmount;
    }

    @Override
    public void render(MatrixStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) { root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha); }
}
