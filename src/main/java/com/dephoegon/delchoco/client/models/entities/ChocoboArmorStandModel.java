package com.dephoegon.delchoco.client.models.entities;

import com.dephoegon.delchoco.common.entities.ChocoboArmorStand;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;

/**
 * Model for Chocobo Armor Stands supporting both adult and baby (chicobo) models
 * with posing capabilities for various body parts.
 */
public class ChocoboArmorStandModel extends EntityModel<ChocoboArmorStand> {
    private final AdultChocoboModel<?> adultModel;
    private final ChicoboModel<?> chicoboModel;
    private EntityModel<?> currentModel;
    private boolean isCurrentlyBaby = false;

    /**
     * Creates a ChocoboArmorStandModel with separate adult and baby model parts
     */
    public ChocoboArmorStandModel(@NotNull ModelPart adultRoot, @NotNull ModelPart babyRoot) {
        this.adultModel = new AdultChocoboModel<>(adultRoot);
        this.chicoboModel = new ChicoboModel<>(babyRoot);
        this.currentModel = this.adultModel; // Default to the adult model
    }
    public static @NotNull TexturedModelData createAdultBodyLayer() { return AdultChocoboModel.createBodyLayer(); }
    public static @NotNull TexturedModelData createChicoboBodyLayer() { return ChicoboModel.createBodyLayer(); }

    public void setAngles(@NotNull ChocoboArmorStand entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean isBaby = entity.isBaby();

        if (isCurrentlyBaby != isBaby) {
            this.currentModel = isBaby ? chicoboModel : adultModel;
            isCurrentlyBaby = isBaby;
        }

        setDefaultStaticChocoboArmorStandPose(isBaby);

        if (isBaby) { chicoboModel.head.setAngles(netHeadYaw * 0.5F, headPitch * 0.5F, 0.0F); }
        else { adultModel.head.setAngles(netHeadYaw * 0.5F, headPitch * 0.5F, 0.0F); }
    }

    /**
     * Sets the default pose for the adult chocobo model when displayed on an armor stand.
     * This creates a natural-looking static pose.
     */
    private void createDefaultAdultModelPose() {
        setRotateAngle(adultModel.leg_left, 0.2f, 0f, 0f);
        setRotateAngle(adultModel.leg_right, 0.2f, 0f, 0f);
        setRotateAngle(adultModel.neck, 0.15f, 0f, 0f);
    }

    /**
     * Sets the default pose for the chichobo model when displayed on an armor stand.
     * This creates a cute, static pose appropriate for a baby chocobo.
     */
    private void createDefaultChicoboModelPose() {
        setRotateAngle(chicoboModel.leg_left, 0f, -0.1745f, 0f);
        setRotateAngle(chicoboModel.leg_right, 0f, 0.1745f, 0f);

        setRotateAngle(chicoboModel.head, -0.1f, 0f, 0f);
    }

    /**
     * Helper method to set rotation angles for model parts
     */
    private void setRotateAngle(ModelPart modelPart, float x, float y, float z) {
        modelPart.pitch = x;
        modelPart.yaw = y;
        modelPart.roll = z;
    }

    public void render(@NotNull MatrixStack poseStack, @NotNull VertexConsumer consumer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (isCurrentlyBaby) { chicoboModel.render(poseStack, consumer, packedLightIn, packedOverlayIn, red, green, blue, alpha); }
        else { adultModel.render(poseStack, consumer, packedLightIn, packedOverlayIn, red, green, blue, alpha); }
    }

    /**
     * Public method to set the default static pose for the current model.
     * This can be called from the renderer to ensure proper pose.
     *
     * @param isBaby whether to use the baby (chicobo) pose
     */
    public void setDefaultStaticChocoboArmorStandPose(boolean isBaby) {
        if (isBaby) { createDefaultChicoboModelPose(); }
        else { createDefaultAdultModelPose(); }
    }

    // ==================== GETTERS FOR THE RENDERER ====================

    /**
     * @return The adult model head part
     */
    public ModelPart getAdultHead() { return adultModel.head; }

    /**
     * @return The adult model neck part
     */
    public ModelPart getAdultNeck() { return adultModel.neck; }


    /**
     * @return The adult model left leg part
     */
    public ModelPart getAdultLeftLeg() { return adultModel.leg_left; }

    /**
     * @return The adult model right leg part
     */
    public ModelPart getAdultRightLeg() { return adultModel.leg_right; }

    /**
     * @return The adult model left foot part
     */
    public ModelPart getAdultLeftFoot() { return adultModel.foot_left; }

    /**
     * @return The adult model right foot part
     */
    public ModelPart getAdultRightFoot() { return adultModel.foot_right; }

    /**
     * @return The adult model tail feathers part
     */
    public ModelPart getAdultTailFeathers() { return adultModel.tail_feathers; }

    /**
     * @return The adult model head crest part
     */
    public ModelPart getAdultHeadCrest() { return adultModel.head_crest; }

    /**
     * @return The chicobo model head part
     */
    public ModelPart getChicoboHead() { return chicoboModel.head; }

    /**
     * @return The chicobo model left leg part
     */
    public ModelPart getChicoboLeftLeg() { return chicoboModel.leg_left; }

    /**
     * @return The chicobo model right leg part
     */
    public ModelPart getChicoboRightLeg() { return chicoboModel.leg_right; }

    /**
     * @return true if the model is currently in baby (chicobo) mode
     */
    public boolean isInBabyMode() { return isCurrentlyBaby; }

    /**
     * @return The underlying adult chocobo model
     */
    public AdultChocoboModel<?> getAdultModel() {
        return this.adultModel;
    }

    /**
     * @return The underlying baby (chicobo) model
     */
    public ChicoboModel<?> getChicoboModel() {
        return this.chicoboModel;
    }

    // ==================== ADULT MODEL POSING METHODS ====================

    /**
     * Sets the rotation of the adult chocobo's head
     *
     * @param pitch X-axis rotation (up/down)
     * @param yaw Y-axis rotation (left/right)
     * @param roll Z-axis rotation (tilt)
     */
    public void setAdultHeadRotation(float pitch, float yaw, float roll) {
        if (!isCurrentlyBaby) { setRotateAngle(adultModel.head, pitch, yaw, roll); }
    }

    /**
     * Sets the rotation of the adult chocobo's neck
     *
     * @param pitch X-axis rotation (forward/backward bend)
     * @param yaw Y-axis rotation (sideways turn)
     * @param roll Z-axis rotation (twist)
     */
    public void setAdultNeckRotation(float pitch, float yaw, float roll) {
        if (!isCurrentlyBaby) { setRotateAngle(adultModel.neck, pitch, yaw, roll); }
    }

    /**
     * Sets the rotation of the adult chocobo's left leg
     *
     * @param pitch X-axis rotation (forward/backward)
     * @param yaw Y-axis rotation (inward/outward)
     * @param roll Z-axis rotation (twist)
     */
    public void setAdultLeftLegRotation(float pitch, float yaw, float roll) {
        if (!isCurrentlyBaby) { setRotateAngle(adultModel.leg_left, pitch, yaw, roll); }
    }

    /**
     * Sets the rotation of the adult chocobo's right leg
     *
     * @param pitch X-axis rotation (forward/backward)
     * @param yaw Y-axis rotation (inward/outward)
     * @param roll Z-axis rotation (twist)
     */
    public void setAdultRightLegRotation(float pitch, float yaw, float roll) {
        if (!isCurrentlyBaby) { setRotateAngle(adultModel.leg_right, pitch, yaw, roll); }
    }

    /**
     * Sets the rotation of the adult chocobo's left foot
     *
     * @param pitch X-axis rotation (up/down)
     */
    public void setAdultLeftFootRotation(float pitch) {
        if (!isCurrentlyBaby) { adultModel.foot_left.pitch = pitch; }
    }

    /**
     * Sets the rotation of the adult chocobo's right foot
     *
     * @param pitch X-axis rotation (up/down)
     */
    public void setAdultRightFootRotation(float pitch) {
        if (!isCurrentlyBaby) { adultModel.foot_right.pitch = pitch; }
    }

    /**
     * Sets the rotation of the adult chocobo's tail feathers
     *
     * @param pitch X-axis rotation (up/down)
     * @param yaw Y-axis rotation (left/right)
     * @param roll Z-axis rotation (fan out)
     */
    public void setAdultTailFeathersRotation(float pitch, float yaw, float roll) {
        if (!isCurrentlyBaby) { setRotateAngle(adultModel.tail_feathers, pitch, yaw, roll); }
    }

    /**
     * Sets the rotation of the adult chocobo's head crest
     *
     * @param pitch X-axis rotation (forward/backward)
     */
    public void setAdultHeadCrestRotation(float pitch) {
        if (!isCurrentlyBaby && adultModel.head_crest != null) {
            adultModel.head_crest.pitch = pitch;
        }
    }

    // ==================== CHICOBO MODEL POSING METHODS ====================

    /**
     * Sets the rotation of the chicobo's head
     *
     * @param pitch X-axis rotation (up/down)
     * @param yaw Y-axis rotation (left/right)
     * @param roll Z-axis rotation (tilt)
     */
    public void setChicoboHeadRotation(float pitch, float yaw, float roll) {
        if (isCurrentlyBaby) { setRotateAngle(chicoboModel.head, pitch, yaw, roll); }
    }

    /**
     * Sets the rotation of the chicobo's left leg
     *
     * @param pitch X-axis rotation (forward/backward)
     * @param yaw Y-axis rotation (inward/outward)
     */
    public void setChicoboLeftLegRotation(float pitch, float yaw) {
        if (isCurrentlyBaby) {
            chicoboModel.leg_left.pitch = pitch;
            chicoboModel.leg_left.yaw = yaw;
        }
    }

    /**
     * Sets the rotation of the chicobo's right leg
     *
     * @param pitch X-axis rotation (forward/backward)
     * @param yaw Y-axis rotation (inward/outward)
     */
    public void setChicoboRightLegRotation(float pitch, float yaw) {
        if (isCurrentlyBaby) {
            chicoboModel.leg_right.pitch = pitch;
            chicoboModel.leg_right.yaw = yaw;
        }
    }

    // ==================== COMBINED POSING METHODS ====================

    /**
     * Sets the rotation of the head, adapting to whether it's an adult or baby model
     *
     * @param pitch X-axis rotation (up/down)
     * @param yaw Y-axis rotation (left/right)
     * @param roll Z-axis rotation (tilt)
     */
    public void setHeadRotation(float pitch, float yaw, float roll) {
        if (isCurrentlyBaby) { setChicoboHeadRotation(pitch, yaw, roll); }
        else { setAdultHeadRotation(pitch, yaw, roll); }
    }

    /**
     * Sets the rotation of the legs, adapting to whether it's an adult or baby model
     *
     * @param leftPitch Left leg X-axis rotation
     * @param leftYaw Left leg Y-axis rotation
     * @param rightPitch Right leg X-axis rotation
     * @param rightYaw Right leg Y-axis rotation
     */
    public void setLegsRotation(float leftPitch, float leftYaw, float rightPitch, float rightYaw) {
        if (isCurrentlyBaby) {
            setChicoboLeftLegRotation(leftPitch, leftYaw);
            setChicoboRightLegRotation(rightPitch, rightYaw);
        } else {
            setAdultLeftLegRotation(leftPitch, leftYaw, 0);
            setAdultRightLegRotation(rightPitch, rightYaw, 0);
        }
    }
}

