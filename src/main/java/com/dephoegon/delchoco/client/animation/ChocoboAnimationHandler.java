package com.dephoegon.delchoco.client.animation;

import com.dephoegon.delchoco.client.models.entities.AdultChocoboModel;
import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

/**
 * Handles animations for chocobo entities and armor stands.
 * This class separates animation logic from the model classes.
 */
public class ChocoboAnimationHandler {
    // Constants to avoid magic numbers and repeated calculations
    private static final float PI = (float) Math.PI;
    private static final float PI_HALF = PI / 2.0F;
    private static final float DEFAULT_LEG_PITCH = 0.2094395F;
    private static final float LIMB_SWING_SPEED = 0.6662F;
    private static final float DEG_TO_RAD = (float) Math.PI / 180.0F;

    // Cached angle values for better performance
    private static final float WING_IDLE_Y_RIGHT = -0.0174533F;
    private static final float WING_IDLE_Y_LEFT = 0.0174533F;

    // Animation state variables
    private float wingRotation;
    private float destinationPos;
    private float wingRotDelta;
    private boolean isChocoboJumping;
    private float swimAnimationTicks = 0.0F;
    private float rainShakeTimer = 0.0F;

    /**
     * Animates the model based on entity state and animation pose string
     *
     * @param model The chocobo model to animate
     * @param entity The chocobo entity (or null for armor stands)
     * @param limbSwing The limb swing animation parameter
     * @param limbSwingAmount The limb swing amount parameter
     * @param ageInTicks The entity age in ticks parameter
     * @param netHeadYaw The head yaw parameter
     * @param headPitch The head pitch parameter
     * @param animationPose Optional string identifier for static poses (used by armor stands)
     */
    public void animate(AdultChocoboModel<?> model, Chocobo entity, float limbSwing, float limbSwingAmount,
                        float ageInTicks, float netHeadYaw, float headPitch, String animationPose) {
        // If a specific animation pose is specified, apply it instead of dynamic animations
        if (animationPose != null && !animationPose.isEmpty()) {
            applyStaticPose(model, animationPose);
            return;
        }

        if (entity == null) {
            // Default pose if no entity or pose string provided
            applyStaticPose(model, "default");
            return;
        }

        // Update animation variables from entity
        this.wingRotation = entity.wingRotation;
        this.destinationPos = entity.destinationPos;
        this.wingRotDelta = entity.wingRotDelta;
        this.isChocoboJumping = entity.isChocoboJumping;

        // Cache state checks to avoid repeated method calls
        boolean isSwimming = entity.isTouchingWater() && entity.isWaterBreathing();
        boolean isRainedOn = entity.isTouchingWaterOrRain();
        boolean isOnGround = entity.isOnGround();

        // Update wing animation variables
        updateWingAnimationVars(entity, isSwimming, isRainedOn, isOnGround);

        // Animate legs and wings
        animateLegs(model, entity, limbSwing, limbSwingAmount, isSwimming);
        animateWings(model, entity, limbSwing, limbSwingAmount, isOnGround, isSwimming, isRainedOn, ageInTicks);

        // Animate head and neck
        animateHeadAndNeck(model, entity, netHeadYaw, headPitch);
    }

    /**
     * Updates the wing animation variables based on entity state
     */
    private void updateWingAnimationVars(Chocobo entity, boolean isSwimming, boolean isRainedOn, boolean isOnGround) {
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
    }

    /**
     * Animates the legs based on entity state
     */
    private void animateLegs(AdultChocoboModel<?> model, Chocobo entity, float limbSwing, float limbSwingAmount, boolean isSwimming) {
        if (isSwimming) {
            // Swimming leg animation
            float swimSpeed = LIMB_SWING_SPEED * 1.5F;
            float swimStrength = Math.max(limbSwingAmount, 0.4F);

            boolean offGround = !entity.isOnGround();

            if (offGround) {
                // Stationary swimming animation
                float paddleSpeed = LIMB_SWING_SPEED * 1.2F;
                float paddleStrength = 0.6F;

                setRightLegXRotation(model, MathHelper.cos(this.swimAnimationTicks * paddleSpeed) * paddleStrength);
                setLeftLegXRotation(model, MathHelper.cos(this.swimAnimationTicks * paddleSpeed + PI) * paddleStrength);
            } else {
                // Moving swimming animation
                setRightLegXRotation(model, MathHelper.cos(limbSwing * swimSpeed) * 1.2F * swimStrength);
                setLeftLegXRotation(model, MathHelper.cos(limbSwing * swimSpeed + PI) * 1.2F * swimStrength);
            }
        } else {
            // Normal walking animation
            float walkSpeed = limbSwing * LIMB_SWING_SPEED;
            float walkStrength = 0.8F * limbSwingAmount;

            setRightLegXRotation(model, MathHelper.cos(walkSpeed) * walkStrength);
            setLeftLegXRotation(model, MathHelper.cos(walkSpeed + PI) * walkStrength);
        }
    }

    /**
     * Animates the wings based on entity state
     */
    private void animateWings(AdultChocoboModel<?> model, Chocobo entity, float limbSwing, float limbSwingAmount,
                              boolean isOnGround, boolean isSwimming, boolean isRainedOn, float ageInTicks) {
        // If wings are null, don't try to animate them
        if (model.getLeftWing() == null || model.getRightWing() == null) {
            return;
        }

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
                setRotateAngle(model.getRightWing(), horizontalPitch, WING_IDLE_Y_RIGHT, rightSwingMod);
                setRotateAngle(model.getLeftWing(), horizontalPitch, WING_IDLE_Y_LEFT, leftSwingMod);
            } else {
                // Flying wing animation - flapping
                float wingCycle = MathHelper.sin(entity.age * this.wingRotation);
                float basePitch = 0.15F;

                // Wing extension calculations
                float rightWingExtension = 0.6F - wingCycle * 0.5F;
                float leftWingExtension = -0.6F + wingCycle * 0.5F;
                float randomVariation = MathHelper.sin(this.rainShakeTimer * 1.6F) * 0.05F;

                setRotateAngle(model.getRightWing(), basePitch, WING_IDLE_Y_RIGHT, rightWingExtension - randomVariation);
                setRotateAngle(model.getLeftWing(), basePitch, WING_IDLE_Y_LEFT, leftWingExtension + randomVariation);

                // Different leg positions for flying - use the getter methods
                model.getLeftLeg().pitch = DEFAULT_LEG_PITCH + 0.6F;
                model.getRightLeg().pitch = DEFAULT_LEG_PITCH + 0.6F;
            }
        } else if (isRainedOn) {
            // Rain shake animation - light wing movements
            float rainShakeCycle = MathHelper.sin(this.rainShakeTimer * 0.8F);
            float rightWingExtension = 0.4F - rainShakeCycle * 0.15F;
            float leftWingExtension = -0.4F + rainShakeCycle * 0.15F;
            float randomVariation = MathHelper.sin(this.rainShakeTimer * 1.6F) * 0.05F;

            setRotateAngle(model.getRightWing(), 0.1F, WING_IDLE_Y_RIGHT, rightWingExtension - randomVariation);
            setRotateAngle(model.getLeftWing(), 0.1F, WING_IDLE_Y_LEFT, leftWingExtension + randomVariation);
        } else {
            // Idle animation - gentle wing movements when grounded
            float idleWingMovement = MathHelper.sin(entity.age * 0.05F) * 0.05F;
            float rightWingIdle = -0.25F - idleWingMovement * 0.5F;
            float leftWingIdle = 0.25F + idleWingMovement * 0.5F;

            setRotateAngle(model.getRightWing(), 0.1F, WING_IDLE_Y_RIGHT, rightWingIdle);
            setRotateAngle(model.getLeftWing(), 0.1F, WING_IDLE_Y_LEFT, leftWingIdle);
        }

        // Additional wing flutter for idle animation when on ground
        if (isOnGround && entity != null && entity.getVelocity().lengthSquared() < 0.0025) {
            float idleWingFlutter = MathHelper.sin(ageInTicks * 0.05F) * 0.05F;
            setRotateAngle(model.getRightWing(), idleWingFlutter, WING_IDLE_Y_RIGHT, 0F);
            setRotateAngle(model.getLeftWing(), idleWingFlutter, WING_IDLE_Y_LEFT, 0F);
        }
    }

    /**
     * Animates the head and neck based on entity state
     */
    private void animateHeadAndNeck(AdultChocoboModel<?> model, Chocobo entity, float netHeadYaw, float headPitch) {
        // Head rotation - convert angles once
        float headYawRad = netHeadYaw * DEG_TO_RAD;
        float headPitchRad = headPitch * DEG_TO_RAD;

        model.getHead().pitch = headPitchRad;
        model.getHead().yaw = headYawRad;

        // Neck follows head slightly - optimize calculation
        model.getNeck().pitch = (-0.8F * headYawRad) / 8;
        model.getNeck().yaw = headYawRad / 9;

        // Adjust neck position based on movement
        float velocityMagnitude = (float)(Math.abs(entity.getVelocity().x) + Math.abs(entity.getVelocity().z));

        if (velocityMagnitude > 0.1F) {
            // When moving, neck is more forward
            model.getNeck().pitch = entity.hasPassengers() ? -0.2F : -0.3F;
        } else {
            // When idle, neck is more upright
            model.getNeck().pitch = 0.1F;
        }
    }

    /**
     * Applies a static pose to the model based on a string identifier
     */
    public void applyStaticPose(AdultChocoboModel<?> model, String poseName) {
        switch (poseName) {
            case "default" -> applyDefaultPose(model);
            case "standing" -> applyStandingPose(model);
            case "sitting" -> applySittingPose(model);
            case "sleeping" -> applySleepingPose(model);
            case "alert" -> applyAlertPose(model);
            // Add more poses as needed
        }
    }

    /**
     * Applies a default neutral pose
     */
    private void applyDefaultPose(AdultChocoboModel<?> model) {
        // Legs
        setRotateAngle(model.getLeftLeg(), DEFAULT_LEG_PITCH, 0f, 0f);
        setRotateAngle(model.getRightLeg(), DEFAULT_LEG_PITCH, 0f, 0f);
/*
        // Wings (if available)
        if (model.wing_left != null && model.wing_right != null) {
            setRotateAngle(model.wing_left, 0.1f, WING_IDLE_Y_LEFT, 0.25f);
            setRotateAngle(model.wing_right, 0.1f, WING_IDLE_Y_RIGHT, -0.25f);
        }
*/
        // Neck and head
        setRotateAngle(model.getNeck(), 0.15f, 0f, 0f);
        setRotateAngle(model.getHead(), 0f, 0f, 0f);
    }

    /**
     * Applies a standing alert pose with head high
     */
    private void applyStandingPose(AdultChocoboModel<?> model) {
        // Legs
        setRotateAngle(model.getLeftLeg(), DEFAULT_LEG_PITCH, 0f, 0f);
        setRotateAngle(model.getRightLeg(), DEFAULT_LEG_PITCH, 0f, 0f);
/*
        // Wings (if available)
        if (model.wing_left != null && model.wing_right != null) {
            setRotateAngle(model.wing_left, 0.1f, WING_IDLE_Y_LEFT, 0.1f);
            setRotateAngle(model.wing_right, 0.1f, WING_IDLE_Y_RIGHT, -0.1f);
        }
*/
        // Neck and head
        setRotateAngle(model.getNeck(), -0.2f, 0f, 0f);
        setRotateAngle(model.getHead(), -0.1f, 0f, 0f);
    }

    /**
     * Applies a sitting pose with legs tucked
     */
    private void applySittingPose(AdultChocoboModel<?> model) {
        // Legs
        setRotateAngle(model.getLeftLeg(), 0.8f, -0.1f, 0f);
        setRotateAngle(model.getRightLeg(), 0.8f, 0.1f, 0f);
/*
        // Wings (if available)
        if (model.wing_left != null && model.wing_right != null) {
            setRotateAngle(model.wing_left, 0.2f, WING_IDLE_Y_LEFT, 0.1f);
            setRotateAngle(model.wing_right, 0.2f, WING_IDLE_Y_RIGHT, -0.1f);
        }
*/
        // Neck and head
        setRotateAngle(model.getNeck(), 0.3f, 0f, 0f);
        setRotateAngle(model.getHead(), -0.2f, 0f, 0f);
    }

    /**
     * Applies a sleeping pose with head tucked
     */
    private void applySleepingPose(AdultChocoboModel<?> model) {
        // Legs
        setRotateAngle(model.getLeftLeg(), 0.9f, -0.1f, 0f);
        setRotateAngle(model.getRightLeg(), 0.9f, 0.1f, 0f);
/*
        // Wings (if available)
        if (model.wing_left != null && model.wing_right != null) {
            setRotateAngle(model.wing_left, 0.05f, WING_IDLE_Y_LEFT, 0.05f);
            setRotateAngle(model.wing_right, 0.05f, WING_IDLE_Y_RIGHT, -0.05f);
        }
*/
        // Neck and head
        setRotateAngle(model.getNeck(), 0.7f, 0f, 0f);
        setRotateAngle(model.getHead(), 0.5f, 0f, 0f);
    }

    /**
     * Applies an alert pose with wings slightly raised
     */
    private void applyAlertPose(AdultChocoboModel<?> model) {
        // Legs
        setRotateAngle(model.getLeftLeg(), DEFAULT_LEG_PITCH + 0.1f, 0f, 0f);
        setRotateAngle(model.getRightLeg(), DEFAULT_LEG_PITCH + 0.1f, 0f, 0f);
/*
        // Wings (if available)
        if (model.wing_left != null && model.wing_right != null) {
            setRotateAngle(model.wing_left, 0f, WING_IDLE_Y_LEFT, 0.4f);
            setRotateAngle(model.wing_right, 0f, WING_IDLE_Y_RIGHT, -0.4f);
        }
*/
        // Neck and head
        setRotateAngle(model.getNeck(), -0.3f, 0f, 0f);
        setRotateAngle(model.getHead(), -0.2f, 0f, 0f);
    }

    /**
     * Helper method to set rotation angles for model parts with null check
     */
    private void setRotateAngle(ModelPart modelPart, float x, float y, float z) {
        if (modelPart != null) {
            modelPart.pitch = x;
            modelPart.yaw = y;
            modelPart.roll = z;
        }
    }

    /**
     * Helper method to set left leg X rotation
     */
    private void setLeftLegXRotation(AdultChocoboModel<?> model, float deltaX) {
        model.getLeftLeg().pitch = DEFAULT_LEG_PITCH + deltaX;
    }

    /**
     * Helper method to set right leg X rotation
     */
    private void setRightLegXRotation(AdultChocoboModel<?> model, float deltaX) {
        model.getRightLeg().pitch = DEFAULT_LEG_PITCH + deltaX;
    }
}
