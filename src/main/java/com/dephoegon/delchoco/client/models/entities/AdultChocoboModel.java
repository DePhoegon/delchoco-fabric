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
    // Constants to avoid magic numbers and repeated calculations
    private static final float DEG_TO_RAD = (float) Math.PI / 180.0F;
    private static final float PI = (float) Math.PI;
    private static final float PI_HALF = PI / 2.0F;
    private static final float DEFAULT_LEG_PITCH = 0.2094395F;
    private static final float LIMB_SWING_SPEED = 0.6662F;

    // Cached angle values for better performance
    private static final float WING_IDLE_Y_RIGHT = -0.0174533F;
    private static final float WING_IDLE_Y_LEFT = 0.0174533F;

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart wing_left;
    private final ModelPart wing_right;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart leg_left;
    private final ModelPart leg_right;
    private final ModelPart foot_left;
    private final ModelPart foot_right;
    private float headPitchModifier;

    // Internal animation variables
    private float wingRotation;
    private float destinationPos;
    private float wingRotDelta;
    private boolean isChocoboJumping;
    private float swimAnimationTicks = 0.0F;
    private float rainShakeTimer = 0.0F;

    public AdultChocoboModel(@NotNull ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");

        this.neck = body.getChild("chest").getChild("neck");
        this.head = neck.getChild("head");
        this.leg_left = body.getChild("leg_left");
        this.leg_right = body.getChild("leg_right");
        this.wing_left = body.getChild("wing_left");
        this.wing_right = body.getChild("wing_right");
        this.foot_left = leg_left.getChild("foot_left");
        this.foot_right = leg_right.getChild("foot_right");
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
                                new Dilation(0.15F)),
                ModelTransform.of(0.0F, 1.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData armor_neck_r1 = neck.addChild("armor_neck_r1", ModelPartBuilder.create()
                        .uv(36, 84).cuboid(-2.0F, -12.0F, -3.0F, 4.0F, 12.0F, 4.0F,
                                new Dilation(0.3F)),
                ModelTransform.of(0.0F, 1.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData head = neck.addChild("head", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-3.0F, -5.0F, -9.0F, 6.0F, 6.0F, 12.0F)
                        .uv(100, 0).cuboid(2.25F, -4.0F, -3.0F, 1.0F, 3.0F, 3.0F)
                        .uv(100, 0).cuboid(-3.25F, -4.0F, -3.0F, 1.0F, 3.0F, 3.0F).mirrored(),
                ModelTransform.pivot(0.0F, -11.0F, -2.0F));

        ModelPartData armor_head = head.addChild("armor_head", ModelPartBuilder.create()
                        .uv(0, 64).cuboid(-3.0F, 6.0F, -7.0F, 6.0F, 6.0F, 12.0F,
                                new Dilation(0.25F)), // 3D eyes aren't included for armor layer
                ModelTransform.pivot(0.0F, -11.0F, -2.0F));

        ModelPartData crest_top_r1 = head.addChild("crest_top_r1", ModelPartBuilder.create()
                        .uv(20, 0).cuboid(-2.5F, 0.0F, 0.0F, 5.0F, 0.0F, 6.0F),
                ModelTransform.of(0.0F, -5.0F, 3.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData crest_right_r1 = head.addChild("crest_right_r1", ModelPartBuilder.create()
                .uv(2, 2).mirrored().cuboid(0.0F, -5.0F, 0.0F, 0.0F, 6.0F, 4.0F).mirrored(false), ModelTransform.of(-3.0F, 0.0F, 3.0F, 0.0F, -0.2182F, 0.0F));

        ModelPartData crest_left_r1 = head.addChild("crest_left_r1", ModelPartBuilder.create()
                        .uv(2, 2).cuboid(0.0F, -5.0F, 0.0F, 0.0F, 6.0F, 4.0F),
                ModelTransform.of(3.0F, 0.0F, 3.0F, 0.0F, 0.2182F, 0.0F));

        ModelPartData leg_left = body.addChild("leg_left", ModelPartBuilder.create(),
                ModelTransform.pivot(3.5F, 3.0F, 1.0F));

        ModelPartData leg_left_lower_r1 = leg_left.addChild("leg_left_lower_r1", ModelPartBuilder.create()
                        .uv(79, 48).cuboid(-1.5F, -2.0F, 1.0F, 3.0F, 12.0F, 3.0F,
                                new Dilation(0.1F)),
                ModelTransform.of(0.0F, 8.0F, 0.0F, -0.3927F, 0.0F, 0.0F));
        ModelPartData armor_leg_left_lower_r1 = leg_left.addChild("armor_leg_left_lower_r1", ModelPartBuilder.create()
                        .uv(79, 112).cuboid(-1.5F, -2.0F, 1.0F, 3.0F, 12.0F, 3.0F,
                                new Dilation(0.35F)),
                ModelTransform.of(0.0F, 8.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData leg_left_upper_r1 = leg_left.addChild("leg_left_upper_r1", ModelPartBuilder.create()
                        .uv(60, 49).cuboid(-2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData armor_leg_left_upper_r1 = leg_left.addChild("armor_leg_left_upper_r1", ModelPartBuilder.create()
                        .uv(60, 113).cuboid(-2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData foot_left = leg_left.addChild("foot_left", ModelPartBuilder.create()
                        .uv(104, 53).cuboid(-1.0F, -2.0F, 1.5F, 2.0F, 2.0F, 5.0F),
                ModelTransform.of(0.0F, 19.0F, -2.0F, 0.0F, -0.2182F, 0.0F));

        ModelPartData toe_right_r1 = foot_left.addChild("toe_right_r1", ModelPartBuilder.create()
                        .uv(92, 54).cuboid(-2.0F, -3.0F, -5.0F, 2.0F, 2.0F, 7.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2182F, 0.2618F, 0.0F));

        ModelPartData toe_left_r1 = foot_left.addChild("toe_left_r1", ModelPartBuilder.create()
                        .uv(92, 54).cuboid(0.0F, -3.0F, -5.0F, 2.0F, 2.0F, 7.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2182F, -0.2618F, 0.0F));

        ModelPartData leg_right = body.addChild("leg_right", ModelPartBuilder.create(),
                ModelTransform.pivot(-3.5F, 3.0F, 1.0F));

        ModelPartData leg_right_lower_r1 = leg_right.addChild("leg_right_lower_r1", ModelPartBuilder.create()
                        .uv(79, 48).cuboid(-1.5F, -2.0F, 1.0F, 3.0F, 12.0F, 3.0F,
                                new Dilation(0.1F)),
                ModelTransform.of(0.0F, 8.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData armor_leg_right_lower_r1 = leg_right.addChild("armor_leg_right_lower_r1", ModelPartBuilder.create()
                        .uv(79, 112).cuboid(-1.5F, -2.0F, 1.0F, 3.0F, 12.0F, 3.0F,
                                new Dilation(0.35F)),
                ModelTransform.of(0.0F, 8.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData leg_right_upper_r1 = leg_right.addChild("leg_right_upper_r1", ModelPartBuilder.create()
                        .uv(60, 49).cuboid(-2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData armor_leg_right_upper_r1 = leg_right.addChild("armor_leg_right_upper_r1", ModelPartBuilder.create()
                        .uv(60, 113).cuboid(-2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData foot_right = leg_right.addChild("foot_right", ModelPartBuilder.create()
                        .uv(104, 53).cuboid(-1.0F, -2.0F, 1.5F, 2.0F, 2.0F, 5.0F),
                ModelTransform.of(0.0F, 19.0F, -2.0F, 0.0F, 0.2182F, 0.0F));

        ModelPartData toe_right_r2 = foot_right.addChild("toe_right_r2", ModelPartBuilder.create()
                        .uv(92, 54).cuboid(-2.0F, -3.0F, -5.0F, 2.0F, 2.0F, 7.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2182F, 0.2618F, 0.0F));

        ModelPartData toe_left_r2 = foot_right.addChild("toe_left_r2", ModelPartBuilder.create()
                        .uv(92, 54).cuboid(0.0F, -3.0F, -5.0F, 2.0F, 2.0F, 7.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2182F, -0.2618F, 0.0F));

        return TexturedModelData.of(meshDefinition, 128, 128);
    }
    public void render(@NotNull MatrixStack poseStack, @NotNull VertexConsumer consumer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, consumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    private void setRotateAngle(@NotNull ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.pitch = x;
        modelRenderer.yaw = y;
        modelRenderer.roll = z;
    }

    public void animateModel(@NotNull Chocobo entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        if (entityIn.isArmorStandNotAlive()) { return; }
        super.animateModel(entityIn, limbSwing, limbSwingAmount, partialTick);

        // Update animation variables from entity - this remains necessary
        this.wingRotation = entityIn.wingRotation;
        this.destinationPos = entityIn.destinationPos;
        this.wingRotDelta = entityIn.wingRotDelta;
        this.isChocoboJumping = entityIn.isChocoboJumping;

        // Cache state checks to avoid repeated method calls
        boolean isSwimming = entityIn.isTouchingWater() && entityIn.isWaterBreathing();
        boolean isRainedOn = entityIn.isTouchingWaterOrRain(); // isSwiming goes first, then isRainedOn & does not matter if the wings get animated from swimming or rain
        boolean isOnGround = entityIn.isOnGround();

        // Update wing animation variables - optimized logic
        if (isSwimming) {
            this.destinationPos = Math.min(this.destinationPos + 0.2F, 1.0F);
            this.wingRotDelta = Math.max(this.wingRotDelta, 1.0F);
            this.swimAnimationTicks += 0.15F;
            this.rainShakeTimer = 0.0F;
        } else if (isRainedOn) {
            this.rainShakeTimer += 0.1F;
            this.wingRotDelta = Math.max(this.wingRotDelta, 0.8F);
            this.destinationPos = Math.min(this.destinationPos + 0.1F, 1.0F);
            this.swimAnimationTicks = 0.0F;
        } else {
            this.swimAnimationTicks = 0.0F;
            this.rainShakeTimer = 0.0F;

            // Optimize by using direct math instead of casting to double and back
            float destPosChange = isOnGround ? -0.3F : 1.2F;
            this.destinationPos += destPosChange;

            if (!isOnGround) {
                this.wingRotDelta = Math.min(this.wingRotation, 1f);
            }
        }

        // Common calculations done once
        this.destinationPos = MathHelper.clamp(this.destinationPos, 0.0F, 1.0F);
        this.wingRotDelta *= 0.9F;
        this.wingRotation += this.wingRotDelta * 2.0F;

        // Leg animations with optimized logic
        animateLegs(entityIn, limbSwing, limbSwingAmount, isSwimming);

        // Wing animations
        animateWings(entityIn, limbSwing, limbSwingAmount, isOnGround, isSwimming, isRainedOn);
    }

    // Extract leg animation logic to a separate method for better organization
    private void animateLegs(Chocobo entityIn, float limbSwing, float limbSwingAmount, boolean isSwimming) {
        if (isSwimming) {
            // Swimming leg animation - optimize calculations
            float swimSpeed = LIMB_SWING_SPEED * 1.5F;
            float swimStrength = Math.max(limbSwingAmount, 0.4F);


            boolean offGround = !entityIn.isOnGround();

            if (offGround) {
                // Stationary swimming animation
                float paddleSpeed = LIMB_SWING_SPEED * 1.2F;
                float paddleStrength = 0.6F;

                setRightLegXRotation(MathHelper.cos(this.swimAnimationTicks * paddleSpeed) * paddleStrength);
                setLeftLegXRotation(MathHelper.cos(this.swimAnimationTicks * paddleSpeed + PI) * paddleStrength);
            } else {
                // Moving swimming animation
                setRightLegXRotation(MathHelper.cos(limbSwing * swimSpeed) * 1.2F * swimStrength);
                setLeftLegXRotation(MathHelper.cos(limbSwing * swimSpeed + PI) * 1.2F * swimStrength);
            }
        } else {
            // Normal walking animation
            float walkSpeed = limbSwing * LIMB_SWING_SPEED;
            float walkStrength = 0.8F * limbSwingAmount;

            setRightLegXRotation(MathHelper.cos(walkSpeed) * walkStrength);
            setLeftLegXRotation(MathHelper.cos(walkSpeed + PI) * walkStrength);
        }
    }

    // Extract wing animation logic to a separate method
    private void animateWings(Chocobo entityIn, float limbSwing, float limbSwingAmount,
                              boolean isOnGround, boolean isSwimming, boolean isRainedOn) {
        if (entityIn.isArmorStandNotAlive()) { return; }
        // Calculate values used for both wings once
        float limbSwingCos = MathHelper.cos(limbSwing * LIMB_SWING_SPEED);
        float limbSwingCosPi = MathHelper.cos(limbSwing * LIMB_SWING_SPEED + PI);
        float limbSwingMod = 1.4F * limbSwingAmount;

        float leftSwingMod = 90 + limbSwingCosPi * limbSwingMod;
        float rightSwingMod = -90 + limbSwingCos * limbSwingMod;

        if (!isOnGround) {
            if (isSwimming) {
                // Swimming wing position - more horizontal
                float horizontalPitch = PI_HALF - (PI / 12);
                setRotateAngle(this.wing_right, horizontalPitch, WING_IDLE_Y_RIGHT, rightSwingMod);
                setRotateAngle(this.wing_left, horizontalPitch, WING_IDLE_Y_LEFT, leftSwingMod);
            } else {
                // Flying wing animation - flapping
                float wingCycle = MathHelper.sin(entityIn.age * this.wingRotation);
                float basePitch = 0.15F;

                // Wing extension calculations
                float rightWingExtension = 0.6F - wingCycle * 0.5F;
                float leftWingExtension = -0.6F + wingCycle * 0.5F;
                float randomVariation = MathHelper.sin(this.rainShakeTimer * 1.6F) * 0.05F;

                setRotateAngle(this.wing_right, basePitch, WING_IDLE_Y_RIGHT, rightWingExtension - randomVariation);
                setRotateAngle(this.wing_left, basePitch, WING_IDLE_Y_LEFT, leftWingExtension + randomVariation);

                // Different leg positions for flying - constant value instead of recalculation
                this.leg_left.pitch = DEFAULT_LEG_PITCH + 0.6F;
                this.leg_right.pitch = DEFAULT_LEG_PITCH + 0.6F;
            }
        } else if (isRainedOn) {
            // Rain shake animation - light wing movements
            float rainShakeCycle = MathHelper.sin(this.rainShakeTimer * 0.8F);
            float rightWingExtension = 0.4F - rainShakeCycle * 0.15F;
            float leftWingExtension = -0.4F + rainShakeCycle * 0.15F;
            float randomVariation = MathHelper.sin(this.rainShakeTimer * 1.6F) * 0.05F;

            setRotateAngle(this.wing_right, 0.1F, WING_IDLE_Y_RIGHT, rightWingExtension - randomVariation);
            setRotateAngle(this.wing_left, 0.1F, WING_IDLE_Y_LEFT, leftWingExtension + randomVariation);
        } else {
            // Idle animation - gentle wing movements when grounded
            float idleWingMovement = MathHelper.sin(entityIn.age * 0.05F) * 0.05F;
            float rightWingIdle = -0.25F - idleWingMovement * 0.5F;
            float leftWingIdle = 0.25F + idleWingMovement * 0.5F;

            setRotateAngle(this.wing_right, 0.1F, WING_IDLE_Y_RIGHT, rightWingIdle);
            setRotateAngle(this.wing_left, 0.1F, WING_IDLE_Y_LEFT, leftWingIdle);
        }
    }

    public void setAngles(@NotNull Chocobo entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Head rotation - convert angles once
        float headYawRad = netHeadYaw * DEG_TO_RAD;
        float headPitchRad = headPitch * DEG_TO_RAD;

        head.pitch = headPitchRad;
        head.yaw = headYawRad;

        // Neck follows head slightly - optimize calculation
        neck.pitch = (-0.8F * headYawRad) / 8;
        neck.yaw = headYawRad / 9;

        // Adjust neck position based on movement
        Vec3d velocity = entityIn.getVelocity();
        float velocityMagnitude = (float)(Math.abs(velocity.x) + Math.abs(velocity.z));

        if (velocityMagnitude > 0.1F) {
            // When moving, neck is more forward
            neck.pitch = entityIn.hasPassengers() ? -0.2F : -0.3F;
        } else {
            // When idle, neck is more upright
            neck.pitch = 0.1F;
        }

        // Additional wing flutter for idle animation when on ground
        if (entityIn.isOnGround() && velocityMagnitude < 0.05F) {
            float idleWingFlutter = MathHelper.sin(ageInTicks * 0.05F) * 0.05F;
            setRotateAngle(this.wing_right, idleWingFlutter, WING_IDLE_Y_RIGHT, 0F);
            setRotateAngle(this.wing_left, idleWingFlutter, WING_IDLE_Y_LEFT, 0F);
        }
    }

    private void setLeftLegXRotation(float deltaX) {
        this.leg_left.pitch = DEFAULT_LEG_PITCH + deltaX;
    }

    /**
     * Sets the rotation of the adult chocobo's left wing
     * @param pitch X-axis rotation (up/down)
     * @param yaw Y-axis rotation (forward/backward)
     * @param roll Z-axis rotation (fold/unfold)
     *
     */
    public void setAdultLeftWingRotation(float pitch, float yaw, float roll) {
        setRotateAngle(this.wing_left, pitch, yaw, roll);
    }
    private void setRightLegXRotation(float deltaX) {
        this.leg_right.pitch = DEFAULT_LEG_PITCH + deltaX;
    }

    /**
     * Sets the rotation of the adult chocobo's head
     *
     * @param pitch X-axis rotation (up/down)
     * @param yaw Y-axis rotation (left/right)
     * @param roll Z-axis rotation (tilt)
     */
    public void setAdultHeadRotation(float pitch, float yaw, float roll) {
        setRotateAngle(this.head, pitch, yaw, roll);
    }

    /**
     * Sets the rotation of the adult chocobo's neck
     *
     * @param pitch X-axis rotation (forward/backward bend)
     * @param yaw Y-axis rotation (sideways turn)
     * @param roll Z-axis rotation (twist)
     */
    public void setAdultNeckRotation(float pitch, float yaw, float roll) {
        setRotateAngle(this.neck, pitch, yaw, roll);
    }

    /**
     * Sets the rotation of the adult chocobo's right wing
     *
     * @param pitch X-axis rotation (up/down)
     * @param yaw Y-axis rotation (forward/backward)
     * @param roll Z-axis rotation (fold/unfold)
     */
    public void setAdultRightWingRotation(float pitch, float yaw, float roll) {
        setRotateAngle(this.wing_right, pitch, yaw, roll);
    }

    /**
     * Sets the rotation of the adult chocobo's left leg
     *
     * @param pitch X-axis rotation (forward/backward)
     * @param yaw Y-axis rotation (inward/outward)
     * @param roll Z-axis rotation (twist)
     */
    public void setAdultLeftLegRotation(float pitch, float yaw, float roll) {
        setRotateAngle(this.leg_left, pitch, yaw, roll);
    }

    /**
     * Sets the rotation of the adult chocobo's right leg
     *
     * @param pitch X-axis rotation (forward/backward)
     * @param yaw Y-axis rotation (inward/outward)
     * @param roll Z-axis rotation (twist)
     */
    public void setAdultRightLegRotation(float pitch, float yaw, float roll) {
        setRotateAngle(this.leg_right, pitch, yaw, roll);
    }

    /**
     * Sets the rotation of the adult chocobo's left foot
     *
     * @param pitch X-axis rotation (up/down)
     */
    public void setAdultLeftFootRotation(float pitch) {
        this.foot_left.pitch = pitch;
    }

    /**
     * Sets the rotation of the adult chocobo's right foot
     *
     * @param pitch X-axis rotation (up/down)
     */
    public void setAdultRightFootRotation(float pitch) {
        this.foot_right.pitch = pitch;
    }

    /**
     * Sets the rotation of the adult chocobo's tail feathers
     *
     * @param pitch X-axis rotation (up/down)
     * @param yaw Y-axis rotation (left/right)
     * @param roll Z-axis rotation (fan out)
     */
    public void setAdultTailFeathersRotation(float pitch, float yaw, float roll) {
        setRotateAngle(this.body.getChild("fan_top_r1"), pitch, yaw, roll);
    }

    /**
     * Sets the rotation of the adult chocobo's head crest
     *
     * @param pitch X-axis rotation (forward/backward)
     */
    public void setAdultHeadCrestRotation(float pitch) {
        this.head.getChild("crest_top_r1").pitch = pitch;
    }
}
