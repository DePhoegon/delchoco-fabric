package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.client.models.entities.ChocoboArmorStandModel;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

/**
 * Class that holds pose data for a chocobo armor stand.
 * It stores rotation data for all body parts and provides
 * methods to save/load from NBT and to apply predefined poses.
 */
public class ChocoboArmorStandPose {
    // NBT keys for pose data
    private static final String NBT_KEY_POSE_DATA = "PoseData";
    private static final String NBT_KEY_POSE_TYPE = "PoseType";

    // NBT keys for adult model parts
    private static final String NBT_KEY_ADULT_HEAD_PITCH = "AdultHeadPitch";
    private static final String NBT_KEY_ADULT_HEAD_YAW = "AdultHeadYaw";
    private static final String NBT_KEY_ADULT_HEAD_ROLL = "AdultHeadRoll";
    private static final String NBT_KEY_ADULT_NECK_PITCH = "AdultNeckPitch";
    private static final String NBT_KEY_ADULT_NECK_YAW = "AdultNeckYaw";
    private static final String NBT_KEY_ADULT_NECK_ROLL = "AdultNeckRoll";
    private static final String NBT_KEY_ADULT_LEFT_WING_PITCH = "AdultLeftWingPitch";
    private static final String NBT_KEY_ADULT_LEFT_WING_YAW = "AdultLeftWingYaw";
    private static final String NBT_KEY_ADULT_LEFT_WING_ROLL = "AdultLeftWingRoll";
    private static final String NBT_KEY_ADULT_RIGHT_WING_PITCH = "AdultRightWingPitch";
    private static final String NBT_KEY_ADULT_RIGHT_WING_YAW = "AdultRightWingYaw";
    private static final String NBT_KEY_ADULT_RIGHT_WING_ROLL = "AdultRightWingRoll";
    private static final String NBT_KEY_ADULT_LEFT_LEG_PITCH = "AdultLeftLegPitch";
    private static final String NBT_KEY_ADULT_LEFT_LEG_YAW = "AdultLeftLegYaw";
    private static final String NBT_KEY_ADULT_LEFT_LEG_ROLL = "AdultLeftLegRoll";
    private static final String NBT_KEY_ADULT_RIGHT_LEG_PITCH = "AdultRightLegPitch";
    private static final String NBT_KEY_ADULT_RIGHT_LEG_YAW = "AdultRightLegYaw";
    private static final String NBT_KEY_ADULT_RIGHT_LEG_ROLL = "AdultRightLegRoll";
    private static final String NBT_KEY_ADULT_LEFT_FOOT_PITCH = "AdultLeftFootPitch";
    private static final String NBT_KEY_ADULT_RIGHT_FOOT_PITCH = "AdultRightFootPitch";
    private static final String NBT_KEY_ADULT_TAIL_FEATHERS_PITCH = "AdultTailFeathersPitch";
    private static final String NBT_KEY_ADULT_TAIL_FEATHERS_YAW = "AdultTailFeathersYaw";
    private static final String NBT_KEY_ADULT_TAIL_FEATHERS_ROLL = "AdultTailFeathersRoll";
    private static final String NBT_KEY_ADULT_HEAD_CREST_PITCH = "AdultHeadCrestPitch";

    // NBT keys for chicobo model parts
    private static final String NBT_KEY_CHICOBO_HEAD_PITCH = "ChicoboHeadPitch";
    private static final String NBT_KEY_CHICOBO_HEAD_YAW = "ChicoboHeadYaw";
    private static final String NBT_KEY_CHICOBO_HEAD_ROLL = "ChicoboHeadRoll";
    private static final String NBT_KEY_CHICOBO_LEFT_WING_PITCH = "ChicoboLeftWingPitch";
    private static final String NBT_KEY_CHICOBO_LEFT_WING_YAW = "ChicoboLeftWingYaw";
    private static final String NBT_KEY_CHICOBO_LEFT_WING_ROLL = "ChicoboLeftWingRoll";
    private static final String NBT_KEY_CHICOBO_RIGHT_WING_PITCH = "ChicoboRightWingPitch";
    private static final String NBT_KEY_CHICOBO_RIGHT_WING_YAW = "ChicoboRightWingYaw";
    private static final String NBT_KEY_CHICOBO_RIGHT_WING_ROLL = "ChicoboRightWingRoll";
    private static final String NBT_KEY_CHICOBO_LEFT_LEG_PITCH = "ChicoboLeftLegPitch";
    private static final String NBT_KEY_CHICOBO_LEFT_LEG_YAW = "ChicoboLeftLegYaw";
    private static final String NBT_KEY_CHICOBO_RIGHT_LEG_PITCH = "ChicoboRightLegPitch";
    private static final String NBT_KEY_CHICOBO_RIGHT_LEG_YAW = "ChicoboRightLegYaw";

    // Adult model rotations
    private float adultHeadPitch = 0f;
    private float adultHeadYaw = 0f;
    private float adultHeadRoll = 0f;
    private float adultNeckPitch = 0f;
    private float adultNeckYaw = 0f;
    private float adultNeckRoll = 0f;
    private float adultLeftWingPitch = 0f;
    private float adultLeftWingYaw = 0f;
    private float adultLeftWingRoll = 0f;
    private float adultRightWingPitch = 0f;
    private float adultRightWingYaw = 0f;
    private float adultRightWingRoll = 0f;
    private float adultLeftLegPitch = 0f;
    private float adultLeftLegYaw = 0f;
    private float adultLeftLegRoll = 0f;
    private float adultRightLegPitch = 0f;
    private float adultRightLegYaw = 0f;
    private float adultRightLegRoll = 0f;
    private float adultLeftFootPitch = 0f;
    private float adultRightFootPitch = 0f;
    private float adultTailFeathersPitch = 0f;
    private float adultTailFeathersYaw = 0f;
    private float adultTailFeathersRoll = 0f;
    private float adultHeadCrestPitch = 0f;

    // Chicobo model rotations
    private float chicoboHeadPitch = 0f;
    private float chicoboHeadYaw = 0f;
    private float chicoboHeadRoll = 0f;
    private float chicoboLeftWingPitch = 0f;
    private float chicoboLeftWingYaw = 0f;
    private float chicoboLeftWingRoll = 0f;
    private float chicoboRightWingPitch = 0f;
    private float chicoboRightWingYaw = 0f;
    private float chicoboRightWingRoll = 0f;
    private float chicoboLeftLegPitch = 0f;
    private float chicoboLeftLegYaw = 0f;
    private float chicoboRightLegPitch = 0f;
    private float chicoboRightLegYaw = 0f;

    // Pose type
    private ChocoboArmorStand.ChocoboModelPose type = ChocoboArmorStand.ChocoboModelPose.DEFAULT;

    // NBT keys for entity facing direction
    private static final String NBT_KEY_PLACEMENT_YAW = "PlacementYaw";
    private static final float DEFAULT_STATIC_YAW = 180.0f;
    private static final float DEFAULT_STATIC_PITCH = 0.0f;

    // Track the placement direction
    private float placementYaw = DEFAULT_STATIC_YAW;

    public ChocoboArmorStandPose() { setDefaultPose(); }
    public ChocoboArmorStand.ChocoboModelPose getType() { return type; }
    public void setType(ChocoboArmorStand.ChocoboModelPose type) { this.type = type; }
    public void applyStaticPoseRotation(@NotNull ChocoboArmorStand armorStand) {
        armorStand.setBodyYaw(placementYaw);
        armorStand.setYaw(placementYaw);
        armorStand.setPitch(DEFAULT_STATIC_PITCH);
        armorStand.setHeadYaw(placementYaw);
    }
    public void setPlacementYaw(float yaw) { this.placementYaw = yaw; }
    public void applyToModel(ChocoboArmorStandModel model, boolean isBaby) {
        if (isBaby) {
            model.setChicoboHeadRotation(chicoboHeadPitch, chicoboHeadYaw, chicoboHeadRoll);
            model.setChicoboLeftWingRotation(chicoboLeftWingPitch, chicoboLeftWingYaw, chicoboLeftWingRoll);
            model.setChicoboRightWingRotation(chicoboRightWingPitch, chicoboRightWingYaw, chicoboRightWingRoll);
            model.setChicoboLeftLegRotation(chicoboLeftLegPitch, chicoboLeftLegYaw);
            model.setChicoboRightLegRotation(chicoboRightLegPitch, chicoboRightLegYaw);
        } else {
            model.setAdultHeadRotation(adultHeadPitch, adultHeadYaw, adultHeadRoll);
            model.setAdultNeckRotation(adultNeckPitch, adultNeckYaw, adultNeckRoll);
            model.setAdultLeftWingRotation(adultLeftWingPitch, adultLeftWingYaw, adultLeftWingRoll);
            model.setAdultRightWingRotation(adultRightWingPitch, adultRightWingYaw, adultRightWingRoll);
            model.setAdultLeftLegRotation(adultLeftLegPitch, adultLeftLegYaw, adultLeftLegRoll);
            model.setAdultRightLegRotation(adultRightLegPitch, adultRightLegYaw, adultRightLegRoll);
            model.setAdultLeftFootRotation(adultLeftFootPitch);
            model.setAdultRightFootRotation(adultRightFootPitch);
            model.setAdultTailFeathersRotation(adultTailFeathersPitch, adultTailFeathersYaw, adultTailFeathersRoll);
            model.setAdultHeadCrestRotation(adultHeadCrestPitch);
        }
    }
    public void setDefaultPose() {
        // Set pose type
        this.type = ChocoboArmorStand.ChocoboModelPose.DEFAULT;

        // Default adult pose
        adultHeadPitch = 0f;
        adultHeadYaw = 0f;
        adultHeadRoll = 0f;
        adultNeckPitch = 0.15f;
        adultNeckYaw = 0f;
        adultNeckRoll = 0f;
        adultLeftWingPitch = 0.1f;
        adultLeftWingYaw = 0.0174533f;
        adultLeftWingRoll = 0.25f;
        adultRightWingPitch = 0.1f;
        adultRightWingYaw = -0.0174533f;
        adultRightWingRoll = -0.25f;
        adultLeftLegPitch = 0.2f;
        adultLeftLegYaw = 0f;
        adultLeftLegRoll = 0f;
        adultRightLegPitch = 0.2f;
        adultRightLegYaw = 0f;
        adultRightLegRoll = 0f;
        adultLeftFootPitch = 0f;
        adultRightFootPitch = 0f;
        adultTailFeathersPitch = 0f;
        adultTailFeathersYaw = 0f;
        adultTailFeathersRoll = 0f;
        adultHeadCrestPitch = 0f;

        // Default chicobo pose
        chicoboHeadPitch = -0.1f;
        chicoboHeadYaw = 0f;
        chicoboHeadRoll = 0f;
        chicoboLeftWingPitch = 0.05f;
        chicoboLeftWingYaw = 0.05f;
        chicoboLeftWingRoll = 0.15f;
        chicoboRightWingPitch = 0.05f;
        chicoboRightWingYaw = -0.05f;
        chicoboRightWingRoll = -0.15f;
        chicoboLeftLegPitch = 0f;
        chicoboLeftLegYaw = -0.1745f;
        chicoboRightLegPitch = 0f;
        chicoboRightLegYaw = 0.1745f;
    }
    public void setSittingPose() {
        this.type = ChocoboArmorStand.ChocoboModelPose.SITTING;

        adultHeadPitch = -0.1f;
        adultHeadYaw = 0f;
        adultHeadRoll = 0f;
        adultNeckPitch = 0.3f;
        adultNeckYaw = 0f;
        adultNeckRoll = 0f;
        adultLeftWingPitch = 0.1f;
        adultLeftWingYaw = 0.05f;
        adultLeftWingRoll = 0.2f;
        adultRightWingPitch = 0.1f;
        adultRightWingYaw = -0.05f;
        adultRightWingRoll = -0.2f;
        adultLeftLegPitch = 1.3f;
        adultLeftLegYaw = -0.1f;
        adultLeftLegRoll = 0f;
        adultRightLegPitch = 1.3f;
        adultRightLegYaw = 0.1f;
        adultRightLegRoll = 0f;
        adultLeftFootPitch = 0.3f;
        adultRightFootPitch = 0.3f;
        adultTailFeathersPitch = -0.2f;
        adultTailFeathersYaw = 0f;
        adultTailFeathersRoll = 0f;
        adultHeadCrestPitch = 0f;

        chicoboHeadPitch = -0.2f;
        chicoboHeadYaw = 0f;
        chicoboHeadRoll = 0f;
        chicoboLeftWingPitch = 0.1f;
        chicoboLeftWingYaw = 0.1f;
        chicoboLeftWingRoll = 0.2f;
        chicoboRightWingPitch = 0.1f;
        chicoboRightWingYaw = -0.1f;
        chicoboRightWingRoll = -0.2f;
        chicoboLeftLegPitch = 1.2f;
        chicoboLeftLegYaw = -0.1f;
        chicoboRightLegPitch = 1.2f;
        chicoboRightLegYaw = 0.1f;
    }
    public void setFlyingPose() {
        this.type = ChocoboArmorStand.ChocoboModelPose.FLYING;

        adultHeadPitch = -0.1f;
        adultHeadYaw = 0f;
        adultHeadRoll = 0f;
        adultNeckPitch = 0.1f;
        adultNeckYaw = 0f;
        adultNeckRoll = 0f;
        adultLeftWingPitch = -0.1f;
        adultLeftWingYaw = 0.05f;
        adultLeftWingRoll = 0.8f;
        adultRightWingPitch = -0.1f;
        adultRightWingYaw = -0.05f;
        adultRightWingRoll = -0.8f;
        adultLeftLegPitch = 0.6f;
        adultLeftLegYaw = 0f;
        adultLeftLegRoll = 0f;
        adultRightLegPitch = 0.6f;
        adultRightLegYaw = 0f;
        adultRightLegRoll = 0f;
        adultLeftFootPitch = -0.2f;
        adultRightFootPitch = -0.2f;
        adultTailFeathersPitch = -0.3f;
        adultTailFeathersYaw = 0f;
        adultTailFeathersRoll = 0f;
        adultHeadCrestPitch = 0f;

        chicoboHeadPitch = -0.2f;
        chicoboHeadYaw = 0f;
        chicoboHeadRoll = 0f;
        chicoboLeftWingPitch = -0.2f;
        chicoboLeftWingYaw = 0.1f;
        chicoboLeftWingRoll = 0.6f;
        chicoboRightWingPitch = -0.2f;
        chicoboRightWingYaw = -0.1f;
        chicoboRightWingRoll = -0.6f;
        chicoboLeftLegPitch = 0.4f;
        chicoboLeftLegYaw = 0f;
        chicoboRightLegPitch = 0.4f;
        chicoboRightLegYaw = 0f;
    }
    public void setSleepingPose() {
        // Set pose type
        this.type = ChocoboArmorStand.ChocoboModelPose.SLEEPING;

        adultHeadPitch = 0.5f;
        adultHeadYaw = 0.4f;
        adultHeadRoll = 0f;
        adultNeckPitch = 0.4f;
        adultNeckYaw = 0.2f;
        adultNeckRoll = 0f;
        adultLeftWingPitch = 0f;
        adultLeftWingYaw = 0f;
        adultLeftWingRoll = 0.1f;
        adultRightWingPitch = 0f;
        adultRightWingYaw = 0f;
        adultRightWingRoll = -0.1f;
        adultLeftLegPitch = 0f;
        adultLeftLegYaw = 0f;
        adultLeftLegRoll = 0f;
        adultRightLegPitch = 0f;
        adultRightLegYaw = 0f;
        adultRightLegRoll = 0f;
        adultLeftFootPitch = 0f;
        adultRightFootPitch = 0f;
        adultTailFeathersPitch = 0f;
        adultTailFeathersYaw = 0f;
        adultTailFeathersRoll = 0f;
        adultHeadCrestPitch = 0f;

        chicoboHeadPitch = 0.4f;
        chicoboHeadYaw = 0.3f;
        chicoboHeadRoll = 0f;
        chicoboLeftWingPitch = 0f;
        chicoboLeftWingYaw = 0f;
        chicoboLeftWingRoll = 0.1f;
        chicoboRightWingPitch = 0f;
        chicoboRightWingYaw = 0f;
        chicoboRightWingRoll = -0.1f;
        chicoboLeftLegPitch = 0f;
        chicoboLeftLegYaw = 0f;
        chicoboRightLegPitch = 0f;
        chicoboRightLegYaw = 0f;
    }
    public void setPoseByType(ChocoboArmorStand.ChocoboModelPose poseType) {
        switch (poseType) {
            case DEFAULT -> setDefaultPose();
            case SITTING -> setSittingPose();
            case FLYING -> setFlyingPose();
            case SLEEPING -> setSleepingPose();
        }
    }

    public void writeToNbt(@NotNull NbtCompound nbt) {
        NbtCompound poseData = new NbtCompound();

        poseData.putString(NBT_KEY_POSE_TYPE, this.type.getId());
        poseData.putFloat(NBT_KEY_PLACEMENT_YAW, this.placementYaw);
        poseData.putFloat(NBT_KEY_ADULT_HEAD_PITCH, this.adultHeadPitch);
        poseData.putFloat(NBT_KEY_ADULT_HEAD_YAW, this.adultHeadYaw);
        poseData.putFloat(NBT_KEY_ADULT_HEAD_ROLL, this.adultHeadRoll);
        poseData.putFloat(NBT_KEY_ADULT_NECK_PITCH, this.adultNeckPitch);
        poseData.putFloat(NBT_KEY_ADULT_NECK_YAW, this.adultNeckYaw);
        poseData.putFloat(NBT_KEY_ADULT_NECK_ROLL, this.adultNeckRoll);
        poseData.putFloat(NBT_KEY_ADULT_LEFT_WING_PITCH, this.adultLeftWingPitch);
        poseData.putFloat(NBT_KEY_ADULT_LEFT_WING_YAW, this.adultLeftWingYaw);
        poseData.putFloat(NBT_KEY_ADULT_LEFT_WING_ROLL, this.adultLeftWingRoll);
        poseData.putFloat(NBT_KEY_ADULT_RIGHT_WING_PITCH, this.adultRightWingPitch);
        poseData.putFloat(NBT_KEY_ADULT_RIGHT_WING_YAW, this.adultRightWingYaw);
        poseData.putFloat(NBT_KEY_ADULT_RIGHT_WING_ROLL, this.adultRightWingRoll);
        poseData.putFloat(NBT_KEY_ADULT_LEFT_LEG_PITCH, this.adultLeftLegPitch);
        poseData.putFloat(NBT_KEY_ADULT_LEFT_LEG_YAW, this.adultLeftLegYaw);
        poseData.putFloat(NBT_KEY_ADULT_LEFT_LEG_ROLL, this.adultLeftLegRoll);
        poseData.putFloat(NBT_KEY_ADULT_RIGHT_LEG_PITCH, this.adultRightLegPitch);
        poseData.putFloat(NBT_KEY_ADULT_RIGHT_LEG_YAW, this.adultRightLegYaw);
        poseData.putFloat(NBT_KEY_ADULT_RIGHT_LEG_ROLL, this.adultRightLegRoll);
        poseData.putFloat(NBT_KEY_ADULT_LEFT_FOOT_PITCH, this.adultLeftFootPitch);
        poseData.putFloat(NBT_KEY_ADULT_RIGHT_FOOT_PITCH, this.adultRightFootPitch);
        poseData.putFloat(NBT_KEY_ADULT_TAIL_FEATHERS_PITCH, this.adultTailFeathersPitch);
        poseData.putFloat(NBT_KEY_ADULT_TAIL_FEATHERS_YAW, this.adultTailFeathersYaw);
        poseData.putFloat(NBT_KEY_ADULT_TAIL_FEATHERS_ROLL, this.adultTailFeathersRoll);
        poseData.putFloat(NBT_KEY_ADULT_HEAD_CREST_PITCH, this.adultHeadCrestPitch);

        poseData.putFloat(NBT_KEY_CHICOBO_HEAD_PITCH, this.chicoboHeadPitch);
        poseData.putFloat(NBT_KEY_CHICOBO_HEAD_YAW, this.chicoboHeadYaw);
        poseData.putFloat(NBT_KEY_CHICOBO_HEAD_ROLL, this.chicoboHeadRoll);
        poseData.putFloat(NBT_KEY_CHICOBO_LEFT_WING_PITCH, this.chicoboLeftWingPitch);
        poseData.putFloat(NBT_KEY_CHICOBO_LEFT_WING_YAW, this.chicoboLeftWingYaw);
        poseData.putFloat(NBT_KEY_CHICOBO_LEFT_WING_ROLL, this.chicoboLeftWingRoll);
        poseData.putFloat(NBT_KEY_CHICOBO_RIGHT_WING_PITCH, this.chicoboRightWingPitch);
        poseData.putFloat(NBT_KEY_CHICOBO_RIGHT_WING_YAW, this.chicoboRightWingYaw);
        poseData.putFloat(NBT_KEY_CHICOBO_RIGHT_WING_ROLL, this.chicoboRightWingRoll);
        poseData.putFloat(NBT_KEY_CHICOBO_LEFT_LEG_PITCH, this.chicoboLeftLegPitch);
        poseData.putFloat(NBT_KEY_CHICOBO_LEFT_LEG_YAW, this.chicoboLeftLegYaw);
        poseData.putFloat(NBT_KEY_CHICOBO_RIGHT_LEG_PITCH, this.chicoboRightLegPitch);
        poseData.putFloat(NBT_KEY_CHICOBO_RIGHT_LEG_YAW, this.chicoboRightLegYaw);

        nbt.put(NBT_KEY_POSE_DATA, poseData);
    }
    public void readFromNbt(@NotNull NbtCompound nbt) {
        if (nbt.contains(NBT_KEY_POSE_DATA)) {
            NbtCompound poseData = nbt.getCompound(NBT_KEY_POSE_DATA);

            if (poseData.contains(NBT_KEY_PLACEMENT_YAW)) { this.placementYaw = poseData.getFloat(NBT_KEY_PLACEMENT_YAW);}
            else {  this.placementYaw = DEFAULT_STATIC_YAW; }

            String poseTypeId = poseData.getString(NBT_KEY_POSE_TYPE);
            this.type = ChocoboArmorStand.ChocoboModelPose.fromId(poseTypeId);

            this.adultHeadPitch = poseData.getFloat(NBT_KEY_ADULT_HEAD_PITCH);
            this.adultHeadYaw = poseData.getFloat(NBT_KEY_ADULT_HEAD_YAW);
            this.adultHeadRoll = poseData.getFloat(NBT_KEY_ADULT_HEAD_ROLL);
            this.adultNeckPitch = poseData.getFloat(NBT_KEY_ADULT_NECK_PITCH);
            this.adultNeckYaw = poseData.getFloat(NBT_KEY_ADULT_NECK_YAW);
            this.adultNeckRoll = poseData.getFloat(NBT_KEY_ADULT_NECK_ROLL);
            this.adultLeftWingPitch = poseData.getFloat(NBT_KEY_ADULT_LEFT_WING_PITCH);
            this.adultLeftWingYaw = poseData.getFloat(NBT_KEY_ADULT_LEFT_WING_YAW);
            this.adultLeftWingRoll = poseData.getFloat(NBT_KEY_ADULT_LEFT_WING_ROLL);
            this.adultRightWingPitch = poseData.getFloat(NBT_KEY_ADULT_RIGHT_WING_PITCH);
            this.adultRightWingYaw = poseData.getFloat(NBT_KEY_ADULT_RIGHT_WING_YAW);
            this.adultRightWingRoll = poseData.getFloat(NBT_KEY_ADULT_RIGHT_WING_ROLL);
            this.adultLeftLegPitch = poseData.getFloat(NBT_KEY_ADULT_LEFT_LEG_PITCH);
            this.adultLeftLegYaw = poseData.getFloat(NBT_KEY_ADULT_LEFT_LEG_YAW);
            this.adultLeftLegRoll = poseData.getFloat(NBT_KEY_ADULT_LEFT_LEG_ROLL);
            this.adultRightLegPitch = poseData.getFloat(NBT_KEY_ADULT_RIGHT_LEG_PITCH);
            this.adultRightLegYaw = poseData.getFloat(NBT_KEY_ADULT_RIGHT_LEG_YAW);
            this.adultRightLegRoll = poseData.getFloat(NBT_KEY_ADULT_RIGHT_LEG_ROLL);
            this.adultLeftFootPitch = poseData.getFloat(NBT_KEY_ADULT_LEFT_FOOT_PITCH);
            this.adultRightFootPitch = poseData.getFloat(NBT_KEY_ADULT_RIGHT_FOOT_PITCH);
            this.adultTailFeathersPitch = poseData.getFloat(NBT_KEY_ADULT_TAIL_FEATHERS_PITCH);
            this.adultTailFeathersYaw = poseData.getFloat(NBT_KEY_ADULT_TAIL_FEATHERS_YAW);
            this.adultTailFeathersRoll = poseData.getFloat(NBT_KEY_ADULT_TAIL_FEATHERS_ROLL);
            this.adultHeadCrestPitch = poseData.getFloat(NBT_KEY_ADULT_HEAD_CREST_PITCH);

            this.chicoboHeadPitch = poseData.getFloat(NBT_KEY_CHICOBO_HEAD_PITCH);
            this.chicoboHeadYaw = poseData.getFloat(NBT_KEY_CHICOBO_HEAD_YAW);
            this.chicoboHeadRoll = poseData.getFloat(NBT_KEY_CHICOBO_HEAD_ROLL);
            this.chicoboLeftWingPitch = poseData.getFloat(NBT_KEY_CHICOBO_LEFT_WING_PITCH);
            this.chicoboLeftWingYaw = poseData.getFloat(NBT_KEY_CHICOBO_LEFT_WING_YAW);
            this.chicoboLeftWingRoll = poseData.getFloat(NBT_KEY_CHICOBO_LEFT_WING_ROLL);
            this.chicoboRightWingPitch = poseData.getFloat(NBT_KEY_CHICOBO_RIGHT_WING_PITCH);
            this.chicoboRightWingYaw = poseData.getFloat(NBT_KEY_CHICOBO_RIGHT_WING_YAW);
            this.chicoboRightWingRoll = poseData.getFloat(NBT_KEY_CHICOBO_RIGHT_WING_ROLL);
            this.chicoboLeftLegPitch = poseData.getFloat(NBT_KEY_CHICOBO_LEFT_LEG_PITCH);
            this.chicoboLeftLegYaw = poseData.getFloat(NBT_KEY_CHICOBO_LEFT_LEG_YAW);
            this.chicoboRightLegPitch = poseData.getFloat(NBT_KEY_CHICOBO_RIGHT_LEG_PITCH);
            this.chicoboRightLegYaw = poseData.getFloat(NBT_KEY_CHICOBO_RIGHT_LEG_YAW);
        } else { setDefaultPose(); }
    }
}

