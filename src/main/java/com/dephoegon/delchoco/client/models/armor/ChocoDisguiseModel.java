package com.dephoegon.delchoco.client.models.armor;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class ChocoDisguiseModel extends BipedEntityModel<LivingEntity> {
    public final ModelPart armor_left_boot;
    public final ModelPart armor_right_boot;
    public final ModelPart chocoHead;
    
    private static final float BASE_PITCH = 0.0827F;
    private static final float BASE_YAW = 0.0F;
    private static final float BASE_ROLL = 0.0F;

    private float cachedRelativeRotX = 0.0f;
    private float cachedRelativeRotY = 0.0f;
    private float cachedRelativeRotZ = 0.0f;
    private boolean rotationsCached = false;

    public ChocoDisguiseModel(ModelPart root) {
        super(root);
        this.armor_left_boot = this.leftLeg.getChild("armor_left_boot");
        this.armor_right_boot = this.rightLeg.getChild("armor_right_boot");
        this.chocoHead = this.head.getChild("decoHead");
    }

    // Override render method to handle selective leg geometry rendering
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        // Check if we're in boots-only mode (legs visible but should not render geometry)
        boolean bootsOnlyMode = this.rightLeg.visible && this.leftLeg.visible &&
                               this.armor_right_boot.visible && this.armor_left_boot.visible &&
                               !shouldRenderLegGeometry();

        if (bootsOnlyMode) {
            // Render everything except leg geometry
            renderBootsOnly(matrices, vertices, light, overlay, red, green, blue, alpha);
        } else {
            // Normal rendering
            super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }

    private boolean shouldRenderLegGeometry() {
        // This will be set by the renderer when we want legs without boots
        // We'll track this state in the renderer
        return !isBootsOnlyRender;
    }

    private boolean isBootsOnlyRender = false;

    public void setBootsOnlyRender(boolean bootsOnly) {
        this.isBootsOnlyRender = bootsOnly;
    }

    private void renderBootsOnly(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        // Render all parts except leg geometry
        if (this.head.visible) { this.head.render(matrices, vertices, light, overlay, red, green, blue, alpha); }
        if (this.hat.visible) { this.hat.render(matrices, vertices, light, overlay, red, green, blue, alpha); }
        if (this.body.visible) { this.body.render(matrices, vertices, light, overlay, red, green, blue, alpha); }
        if (this.rightArm.visible) { this.rightArm.render(matrices, vertices, light, overlay, red, green, blue, alpha); }
        if (this.leftArm.visible) { this.leftArm.render(matrices, vertices, light, overlay, red, green, blue, alpha); }

        // For legs: only render the boot children, skip the leg geometry
        if (this.rightLeg.visible) {
            matrices.push();
            this.rightLeg.rotate(matrices);
            // Only render boot children, not the leg geometry itself
            if (this.armor_right_boot.visible) {
                this.armor_right_boot.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            }
            matrices.pop();
        }

        if (this.leftLeg.visible) {
            matrices.push();
            this.leftLeg.rotate(matrices);
            // Only render boot children, not the leg geometry itself
            if (this.armor_left_boot.visible) {
                this.armor_left_boot.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            }
            matrices.pop();
        }
    }

    public void setRelativeRotations(float relativeRotX, float relativeRotY, float relativeRotZ) {
        this.cachedRelativeRotX = relativeRotX;
        this.cachedRelativeRotY = relativeRotY;
        this.cachedRelativeRotZ = relativeRotZ;
        this.rotationsCached = true;
    }

    public void applyRotationsToChocoHead() {
        if (rotationsCached) {
            // Apply base transform + relative NBT rotations
            // DO NOT TREAT THESE AS THE ARMOR HEAD ROTATIONS
            // THEY ARE NOT THE SAME, CHOCOHEAD IS A DECORATION ATTACHED TO THE HEAD
            chocoHead.pitch = BASE_PITCH + cachedRelativeRotX;
            chocoHead.yaw = BASE_YAW + cachedRelativeRotY;
            chocoHead.roll = BASE_ROLL + cachedRelativeRotZ;
        }
    }

    public void clearRotationCache() {
        this.rotationsCached = false;
        this.cachedRelativeRotX = 0.0f;
        this.cachedRelativeRotY = 0.0f;
        this.cachedRelativeRotZ = 0.0f;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = new Dilation(0.15f);

        // Head with chocobo head parts attached to it - use standard armor head dimensions
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 18).cuboid(-4.5F, -8.0F, -4.5F, 9.0F, 9.0F, 9.0F, dilation), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        // Add chocobo head parts as children of the head so they move with it
        ModelPartData decoHead = head.addChild("decoHead", ModelPartBuilder.create(), ModelTransform.of(-0.2063F, -8.867F, -0.919F, 0.0827F, 0.0F, 0.0F));
        decoHead.addChild("choco_head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-2.7937F, -5.06439F, -8.14041F, 6.0F, 6.0F, 12.0F)
                .uv(36, 12).cuboid(2.5F, -4.06439F, -2.05941F, 1.0F, 3.0F, 3.0F)
                .uv(36, 12).mirrored().cuboid(-3.0F, -4.06439F, -2.05941F, 1.0F, 3.0F, 3.0F), ModelTransform.NONE);
        decoHead.addChild("right_fin", ModelPartBuilder.create().uv(0, 1).cuboid(1.7937F, -6.19739F, 3.02159F, 1.0F, 7.0F, 4.0F), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.2094F, 0.0506F, 0.2094F));
        decoHead.addChild("left_fin", ModelPartBuilder.create().uv(0, 1).mirrored().cuboid(-1.9563F, -6.19739F, 3.02159F, 1.0F, 7.0F, 4.0F), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.2094F, -0.1363F, -0.2094F));
        decoHead.addChild("top_fin", ModelPartBuilder.create().uv(25, 0).cuboid(-2.2063F, -3.19739F, 5.0F, 5.0F, 1.0F, 6.0F), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2974F, 0, 0));

        // Empty hat part as direct child of root (required by BipedEntityModel but won't be used for rendering)
        modelPartData.addChild("hat", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 0.0F, 0.0F, 0.0F), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        // Body - use standard armor body dimensions
        modelPartData.addChild("body", ModelPartBuilder.create().uv(36, 18).cuboid(-4.5F, -1.0F, -2.5F, 9.0F, 13.0F, 5.0F, dilation.add(0.1F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        // Arms - use standard armor arm dimensions
        modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(20, 36).mirrored().cuboid(-3.5F, -3.0F, -2.5F, 5.0F, 15.0F, 5.0F, dilation.add(-0.11F)), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));
        modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(0, 36).cuboid(-1.5F, -3.0F, -2.5F, 5.0F, 15.0F, 5.0F, dilation.add(-0.11F)), ModelTransform.pivot(5.0F, 2.0F, 0.0F));

        // Legs - use standard armor leg dimensions with correct positioning
        ModelPartData rightLeg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 56).mirrored().cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 7.0F, 5.0F, dilation), ModelTransform.pivot(-1.9F, 18.0F, 0.0F));
        ModelPartData leftLeg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 56).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 7.0F, 5.0F, dilation), ModelTransform.pivot(1.9F, 18.0F, 0.0F));

        // Boots - positioned correctly at the bottom of the legs
        ModelPartData armorRightBoot = rightLeg.addChild("armor_right_boot", ModelPartBuilder.create()
                        .uv(20, 56).mirrored().cuboid(-2.5F, 7.0F, -2.5F, 5.0F, 6.0F, 5.0F, dilation.add(-0.12F))
                , ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        armorRightBoot.addChild("right_talon_1", ModelPartBuilder.create().uv(40, 37).cuboid(-1.0F, 11.0F, -3.75F, 2.0F, 2.0F, 7.0F), ModelTransform.of(-0.5F, 0.0F, 0.0F, 0.0F, 0.3309F, 0.0F));
        armorRightBoot.addChild("right_talon_2", ModelPartBuilder.create().uv(40, 37).cuboid(-.9F, 11.0F, -4.5F, 2.0F, 2.0F, 7.0F), ModelTransform.of(0.75F, 0.0F, 0.0F, 0.0F, -0.0618F, 0.0F)); // inside talon

        ModelPartData armorLeftBoot = leftLeg.addChild("armor_left_boot", ModelPartBuilder.create()
                        .uv(20, 56).cuboid(-2.5F, 7.0F, -2.5F, 5.0F, 6.0F, 5.0F, dilation.add(-0.12F))
                , ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        armorLeftBoot.addChild("left_talon_1", ModelPartBuilder.create().uv(40, 37).cuboid(-1.0F, 11.0F, -3.75F, 2.0F, 2.0F, 7.0F), ModelTransform.of(0.5F, 0.0F, 0.0F, 0.00F, -0.3309F, 0.0F));
        armorLeftBoot.addChild("left_talon_2", ModelPartBuilder.create().uv(40, 37).cuboid(-1.1F, 11.0F, -4.5F, 2.0F, 2.0F, 7.0F), ModelTransform.of(-0.75F, 0.0F, 0.0F, 0.0F, 0.0618F, 0.0F));// inside talon

        return TexturedModelData.of(modelData, 128, 128);
    }
}
