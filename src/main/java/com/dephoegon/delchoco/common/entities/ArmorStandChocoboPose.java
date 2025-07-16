package com.dephoegon.delchoco.common.entities;

import com.dephoegon.delchoco.client.models.entities.AdultChocoboModel;
import com.dephoegon.delchoco.client.models.entities.ChicoboModel;
import com.dephoegon.delchoco.common.entities.subTypes.ArmorStandChocobo;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

/**
 * Class that holds pose data for a chocobo armor stand.
 * It stores rotation data for all body parts and provides
 * methods to save/load from NBT and to apply predefined poses.
 */
public class ArmorStandChocoboPose {
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
    private static final String NBT_KEY_CHICOBO_LEFT_LEG_PITCH = "ChicoboLeftLegPitch";
    private static final String NBT_KEY_CHICOBO_LEFT_LEG_YAW = "ChicoboLeftLegYaw";
    private static final String NBT_KEY_CHICOBO_RIGHT_LEG_PITCH = "ChicoboRightLegPitch";
    private static final String NBT_KEY_CHICOBO_RIGHT_LEG_YAW = "ChicoboRightLegYaw";

    // Adult model rotations - changed from static to instance fields
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

    // Chicobo model rotations - changed from static to instance fields
    private float chicoboHeadPitch = 0f;
    private float chicoboHeadYaw = 0f;
    private float chicoboHeadRoll = 0f;
    private float chicoboLeftLegPitch = 0f;
    private float chicoboLeftLegYaw = 0f;
    private float chicoboRightLegPitch = 0f;
    private float chicoboRightLegYaw = 0f;

    // Pose type
    private ArmorStandChocobo.ChocoboModelPose type = ArmorStandChocobo.ChocoboModelPose.DEFAULT;

    // NBT keys for entity facing directions
    private static final String NBT_KEY_PLACEMENT_YAW = "PlacementYaw";
    private static final float DEFAULT_STATIC_YAW = 180.0f;
    private static final float DEFAULT_STATIC_PITCH = 0.0f;

    // Track the placement direction
    private float placementYaw = DEFAULT_STATIC_YAW;

    public ArmorStandChocoboPose(ArmorStandChocobo chocobo) { setDefaultPose(this, chocobo.isBaby()); }
    public ArmorStandChocobo.ChocoboModelPose getType() { return type; }
    public void setType(ArmorStandChocobo.ChocoboModelPose type) { this.type = type; }
    public void applyStaticPoseRotation(@NotNull ArmorStandChocobo armorStand) {
        armorStand.setBodyYaw(placementYaw);
        armorStand.setYaw(placementYaw);
        armorStand.setPitch(DEFAULT_STATIC_PITCH);
        armorStand.setHeadYaw(placementYaw);
    }
    public void setPlacementYaw(float yaw) { this.placementYaw = yaw; }
    public static void applyToModel(ChicoboModel<Chocobo> model, ArmorStandChocoboPose pose) {
        model.setChicoboHeadRotation(pose.chicoboHeadPitch, pose.chicoboHeadYaw, pose.chicoboHeadRoll);
        model.setChicoboLeftLegRotation(pose.chicoboLeftLegPitch, pose.chicoboLeftLegYaw);
        model.setChicoboRightLegRotation(pose.chicoboRightLegPitch, pose.chicoboRightLegYaw);
    }
    public static void applyToModel(@NotNull AdultChocoboModel<Chocobo> model, @NotNull ArmorStandChocoboPose pose) {
        model.setAdultHeadRotation(pose.adultHeadPitch, pose.adultHeadYaw, pose.adultHeadRoll);
        model.setAdultNeckRotation(pose.adultNeckPitch, pose.adultNeckYaw, pose.adultNeckRoll);
        model.setAdultLeftWingRotation(pose.adultLeftWingPitch, pose.adultLeftWingYaw, pose.adultLeftWingRoll);
        model.setAdultRightWingRotation(pose.adultRightWingPitch, pose.adultRightWingYaw, pose.adultRightWingRoll);
        model.setAdultLeftLegRotation(pose.adultLeftLegPitch, pose.adultLeftLegYaw, pose.adultLeftLegRoll);
        model.setAdultRightLegRotation(pose.adultRightLegPitch, pose.adultRightLegYaw, pose.adultRightLegRoll);
        model.setAdultLeftFootRotation(pose.adultLeftFootPitch);
        model.setAdultRightFootRotation(pose.adultRightFootPitch);
        model.setAdultTailFeathersRotation(pose.adultTailFeathersPitch, pose.adultTailFeathersYaw, pose.adultTailFeathersRoll);
        model.setAdultHeadCrestRotation(pose.adultHeadCrestPitch);
    }
    public static void setDefaultPose(ArmorStandChocoboPose pose, boolean isChicobo) {
        // Set pose type
        pose.type = ArmorStandChocobo.ChocoboModelPose.DEFAULT;

        if (isChicobo) {
            // Default chicobo pose
            pose.chicoboHeadPitch = -0.1f;
            pose.chicoboHeadYaw = 0f;
            pose.chicoboHeadRoll = 0f;
            pose.chicoboLeftLegPitch = 0f;
            pose.chicoboLeftLegYaw = -0.1745f;
            pose.chicoboRightLegPitch = 0f;
            pose.chicoboRightLegYaw = 0.1745f;
        } else {
            // Default adult pose
            pose.adultHeadPitch = 0f;
            pose.adultHeadYaw = 0f;
            pose.adultHeadRoll = 0f;
            pose.adultNeckPitch = 0.15f;
            pose.adultNeckYaw = 0f;
            pose.adultNeckRoll = 0f;
            pose.adultLeftWingPitch = 0.1f;
            pose.adultLeftWingYaw = 0.0174533f;
            pose.adultLeftWingRoll = 0.25f;
            pose.adultRightWingPitch = 0.1f;
            pose.adultRightWingYaw = -0.0174533f;
            pose.adultRightWingRoll = -0.25f;
            pose.adultLeftLegPitch = 0.2f;
            pose.adultLeftLegYaw = 0f;
            pose.adultLeftLegRoll = 0f;
            pose.adultRightLegPitch = 0.2f;
            pose.adultRightLegYaw = 0f;
            pose.adultRightLegRoll = 0f;
            pose.adultLeftFootPitch = 0f;
            pose.adultRightFootPitch = 0f;
            pose.adultTailFeathersPitch = 0f;
            pose.adultTailFeathersYaw = 0f;
            pose.adultTailFeathersRoll = 0f;
            pose.adultHeadCrestPitch = 0f;
        }
    }
    public static void setSittingPose(ArmorStandChocoboPose pose, boolean isChicobo) {
        pose.type = ArmorStandChocobo.ChocoboModelPose.SITTING;

        if (isChicobo) {
            pose.chicoboHeadPitch = -0.2f;
            pose.chicoboHeadYaw = 0f;
            pose.chicoboHeadRoll = 0f;
            pose.chicoboLeftLegPitch = 1.2f;
            pose.chicoboLeftLegYaw = -0.1f;
            pose.chicoboRightLegPitch = 1.2f;
            pose.chicoboRightLegYaw = 0.1f;
        } else {
            pose.adultHeadPitch = -0.1f;
            pose.adultHeadYaw = 0f;
            pose.adultHeadRoll = 0f;
            pose.adultNeckPitch = 0.3f;
            pose.adultNeckYaw = 0f;
            pose.adultNeckRoll = 0f;
            pose.adultLeftWingPitch = 0.1f;
            pose.adultLeftWingYaw = 0.05f;
            pose.adultLeftWingRoll = 0.2f;
            pose.adultRightWingPitch = 0.1f;
            pose.adultRightWingYaw = -0.05f;
            pose.adultRightWingRoll = -0.2f;
            pose.adultLeftLegPitch = 1.3f;
            pose.adultLeftLegYaw = -0.1f;
            pose.adultLeftLegRoll = 0f;
            pose.adultRightLegPitch = 1.3f;
            pose.adultRightLegYaw = 0.1f;
            pose.adultRightLegRoll = 0f;
            pose.adultLeftFootPitch = 0.3f;
            pose.adultRightFootPitch = 0.3f;
            pose.adultTailFeathersPitch = -0.2f;
            pose.adultTailFeathersYaw = 0f;
            pose.adultTailFeathersRoll = 0f;
            pose.adultHeadCrestPitch = 0f;
        }
    }
    public static void setFlyingPose(ArmorStandChocoboPose pose, boolean isChicobo) {
        pose.type = ArmorStandChocobo.ChocoboModelPose.FLYING;

        if (isChicobo) {
            pose.chicoboHeadPitch = -0.2f;
            pose.chicoboHeadYaw = 0f;
            pose.chicoboHeadRoll = 0f;
            pose.chicoboLeftLegPitch = 0.4f;
            pose.chicoboLeftLegYaw = 0f;
            pose.chicoboRightLegPitch = 0.4f;
            pose.chicoboRightLegYaw = 0f;
        } else {
            pose.adultHeadPitch = -0.1f;
            pose.adultHeadYaw = 0f;
            pose.adultHeadRoll = 0f;
            pose.adultNeckPitch = 0.1f;
            pose.adultNeckYaw = 0f;
            pose.adultNeckRoll = 0f;
            pose.adultLeftWingPitch = -0.1f;
            pose.adultLeftWingYaw = 0.05f;
            pose.adultLeftWingRoll = 0.8f;
            pose.adultRightWingPitch = -0.1f;
            pose.adultRightWingYaw = -0.05f;
            pose.adultRightWingRoll = -0.8f;
            pose.adultLeftLegPitch = 0.6f;
            pose.adultLeftLegYaw = 0f;
            pose.adultLeftLegRoll = 0f;
            pose.adultRightLegPitch = 0.6f;
            pose.adultRightLegYaw = 0f;
            pose.adultRightLegRoll = 0f;
            pose.adultLeftFootPitch = -0.2f;
            pose.adultRightFootPitch = -0.2f;
            pose.adultTailFeathersPitch = -0.3f;
            pose.adultTailFeathersYaw = 0f;
            pose.adultTailFeathersRoll = 0f;
            pose.adultHeadCrestPitch = 0f;
        }
    }
    public static void setSleepingPose(ArmorStandChocoboPose pose, boolean isChicobo) {
        // Set pose type
        pose.type = ArmorStandChocobo.ChocoboModelPose.SLEEPING;

        if (isChicobo) {
            pose.chicoboHeadPitch = 0.4f;
            pose.chicoboHeadYaw = 0.3f;
            pose.chicoboHeadRoll = 0f;
            pose.chicoboLeftLegPitch = 0f;
            pose.chicoboLeftLegYaw = 0f;
            pose.chicoboRightLegPitch = 0f;
            pose.chicoboRightLegYaw = 0f;
        } else {
            pose.adultHeadPitch = 0.5f;
            pose.adultHeadYaw = 0.4f;
            pose.adultHeadRoll = 0f;
            pose.adultNeckPitch = 0.4f;
            pose.adultNeckYaw = 0.2f;
            pose.adultNeckRoll = 0f;
            pose.adultLeftWingPitch = 0f;
            pose.adultLeftWingYaw = 0f;
            pose.adultLeftWingRoll = 0.1f;
            pose.adultRightWingPitch = 0f;
            pose.adultRightWingYaw = 0f;
            pose.adultRightWingRoll = -0.1f;
            pose.adultLeftLegPitch = 0f;
            pose.adultLeftLegYaw = 0f;
            pose.adultLeftLegRoll = 0f;
            pose.adultRightLegPitch = 0f;
            pose.adultRightLegYaw = 0f;
            pose.adultRightLegRoll = 0f;
            pose.adultLeftFootPitch = 0f;
            pose.adultRightFootPitch = 0f;
            pose.adultTailFeathersPitch = 0f;
            pose.adultTailFeathersYaw = 0f;
            pose.adultTailFeathersRoll = 0f;
            pose.adultHeadCrestPitch = 0f;
        }
    }
    public static void setPoseByType(ArmorStandChocobo.ChocoboModelPose poseType, ArmorStandChocobo armorStandChocobo) {
        ArmorStandChocoboPose pose = armorStandChocobo.getChocoboModelPose();
        switch (poseType) {
            case DEFAULT -> setDefaultPose(pose, armorStandChocobo.isBaby());
            case SITTING -> setSittingPose(pose, armorStandChocobo.isBaby());
            case FLYING -> setFlyingPose(pose, armorStandChocobo.isBaby());
            case SLEEPING -> setSleepingPose(pose, armorStandChocobo.isBaby());
        }
    }
    public void setPoseByType(ArmorStandChocobo armorStand) {
        ArmorStandChocobo.ChocoboModelPose poseType = armorStand.getChocoboModelPose().getType();
        setPoseByType(poseType, armorStand);
    }

    private static boolean isNonDefaultPose(float floatValue) { return floatValue != 0f; }
    public static void writeToNbt(@NotNull NbtCompound nbt, ArmorStandChocobo armorStand) {
        NbtCompound poseData = new NbtCompound();
        ArmorStandChocoboPose pose = armorStand.getChocoboModelPose();

        if (armorStand.isBaby()) {
            if (isNonDefaultPose(pose.chicoboHeadPitch)) { poseData.putFloat(NBT_KEY_CHICOBO_HEAD_PITCH, pose.chicoboHeadPitch); }
            if (isNonDefaultPose(pose.chicoboHeadYaw)) { poseData.putFloat(NBT_KEY_CHICOBO_HEAD_YAW, pose.chicoboHeadYaw); }
            if (isNonDefaultPose(pose.chicoboHeadRoll)) { poseData.putFloat(NBT_KEY_CHICOBO_HEAD_ROLL, pose.chicoboHeadRoll); }
            if (isNonDefaultPose(pose.chicoboLeftLegPitch)) { poseData.putFloat(NBT_KEY_CHICOBO_LEFT_LEG_PITCH, pose.chicoboLeftLegPitch); }
            if (isNonDefaultPose(pose.chicoboLeftLegYaw)) { poseData.putFloat(NBT_KEY_CHICOBO_LEFT_LEG_YAW, pose.chicoboLeftLegYaw); }
            if (isNonDefaultPose(pose.chicoboRightLegPitch)) { poseData.putFloat(NBT_KEY_CHICOBO_RIGHT_LEG_PITCH, pose.chicoboRightLegPitch); }
            if (isNonDefaultPose(pose.chicoboRightLegYaw)) { poseData.putFloat(NBT_KEY_CHICOBO_RIGHT_LEG_YAW, pose.chicoboRightLegYaw); }
        } else {
            poseData.putString(NBT_KEY_POSE_TYPE, pose.type.getId());
            poseData.putFloat(NBT_KEY_PLACEMENT_YAW, pose.placementYaw);
            if (isNonDefaultPose(pose.adultHeadPitch)) { poseData.putFloat(NBT_KEY_ADULT_HEAD_PITCH, pose.adultHeadPitch); }
            if (isNonDefaultPose(pose.adultHeadYaw)) { poseData.putFloat(NBT_KEY_ADULT_HEAD_YAW, pose.adultHeadYaw); }
            if (isNonDefaultPose(pose.adultHeadRoll)) { poseData.putFloat(NBT_KEY_ADULT_HEAD_ROLL, pose.adultHeadRoll); }
            if (isNonDefaultPose(pose.adultNeckPitch)) { poseData.putFloat(NBT_KEY_ADULT_NECK_PITCH, pose.adultNeckPitch); }
            if (isNonDefaultPose(pose.adultNeckYaw)) { poseData.putFloat(NBT_KEY_ADULT_NECK_YAW, pose.adultNeckYaw); }
            if (isNonDefaultPose(pose.adultNeckRoll)) { poseData.putFloat(NBT_KEY_ADULT_NECK_ROLL, pose.adultNeckRoll); }
            if (isNonDefaultPose(pose.adultLeftWingPitch)) { poseData.putFloat(NBT_KEY_ADULT_LEFT_WING_PITCH, pose.adultLeftWingPitch); }
            if (isNonDefaultPose(pose.adultLeftWingYaw)) { poseData.putFloat(NBT_KEY_ADULT_LEFT_WING_YAW, pose.adultLeftWingYaw); }
            if (isNonDefaultPose(pose.adultLeftWingRoll)) { poseData.putFloat(NBT_KEY_ADULT_LEFT_WING_ROLL, pose.adultLeftWingRoll); }
            if (isNonDefaultPose(pose.adultRightWingPitch)) { poseData.putFloat(NBT_KEY_ADULT_RIGHT_WING_PITCH, pose.adultRightWingPitch); }
            if (isNonDefaultPose(pose.adultRightWingYaw)) { poseData.putFloat(NBT_KEY_ADULT_RIGHT_WING_YAW, pose.adultRightWingYaw); }
            if (isNonDefaultPose(pose.adultRightWingRoll)) { poseData.putFloat(NBT_KEY_ADULT_RIGHT_WING_ROLL, pose.adultRightWingRoll); }
            if (isNonDefaultPose(pose.adultLeftLegPitch)) { poseData.putFloat(NBT_KEY_ADULT_LEFT_LEG_PITCH, pose.adultLeftLegPitch); }
            if (isNonDefaultPose(pose.adultLeftLegYaw)) { poseData.putFloat(NBT_KEY_ADULT_LEFT_LEG_YAW, pose.adultLeftLegYaw); }
            if (isNonDefaultPose(pose.adultLeftLegRoll)) { poseData.putFloat(NBT_KEY_ADULT_LEFT_LEG_ROLL, pose.adultLeftLegRoll); }
            if (isNonDefaultPose(pose.adultRightLegPitch)) { poseData.putFloat(NBT_KEY_ADULT_RIGHT_LEG_PITCH, pose.adultRightLegPitch); }
            if (isNonDefaultPose(pose.adultRightLegYaw)) { poseData.putFloat(NBT_KEY_ADULT_RIGHT_LEG_YAW, pose.adultRightLegYaw); }
            if (isNonDefaultPose(pose.adultRightLegRoll)) { poseData.putFloat(NBT_KEY_ADULT_RIGHT_LEG_ROLL, pose.adultRightLegRoll); }
            if (isNonDefaultPose(pose.adultLeftFootPitch)) { poseData.putFloat(NBT_KEY_ADULT_LEFT_FOOT_PITCH, pose.adultLeftFootPitch); }
            if (isNonDefaultPose(pose.adultRightFootPitch)) { poseData.putFloat(NBT_KEY_ADULT_RIGHT_FOOT_PITCH, pose.adultRightFootPitch); }
            if (isNonDefaultPose(pose.adultTailFeathersPitch)) { poseData.putFloat(NBT_KEY_ADULT_TAIL_FEATHERS_PITCH, pose.adultTailFeathersPitch); }
            if (isNonDefaultPose(pose.adultTailFeathersYaw)) { poseData.putFloat(NBT_KEY_ADULT_TAIL_FEATHERS_YAW, pose.adultTailFeathersYaw); }
            if (isNonDefaultPose(pose.adultTailFeathersRoll)) { poseData.putFloat(NBT_KEY_ADULT_TAIL_FEATHERS_ROLL, pose.adultTailFeathersRoll); }
            if (isNonDefaultPose(pose.adultHeadCrestPitch)) { poseData.putFloat(NBT_KEY_ADULT_HEAD_CREST_PITCH, pose.adultHeadCrestPitch); }
        }
        nbt.put(NBT_KEY_POSE_DATA, poseData);
    }
    public static void readFromNbt(@NotNull NbtCompound nbt, ArmorStandChocobo armorStand) {
        ArmorStandChocoboPose pose = armorStand.getChocoboModelPose();
        if (nbt.contains(NBT_KEY_POSE_DATA)) {
            NbtCompound poseData = nbt.getCompound(NBT_KEY_POSE_DATA);

            if (poseData.contains(NBT_KEY_PLACEMENT_YAW)) { pose.placementYaw = poseData.getFloat(NBT_KEY_PLACEMENT_YAW);}
            else {  pose.placementYaw = DEFAULT_STATIC_YAW; }

            String poseTypeId = poseData.getString(NBT_KEY_POSE_TYPE);
            pose.type = ArmorStandChocobo.ChocoboModelPose.fromId(poseTypeId);

            if (armorStand.isBaby()) {
                if (hasValue(poseData, NBT_KEY_CHICOBO_HEAD_PITCH)) {
                    pose.chicoboHeadPitch = poseData.getFloat(NBT_KEY_CHICOBO_HEAD_PITCH);
                } else { pose.chicoboHeadPitch = 0f; }
                if (hasValue(poseData, NBT_KEY_CHICOBO_HEAD_YAW)) {
                    pose.chicoboHeadYaw = poseData.getFloat(NBT_KEY_CHICOBO_HEAD_YAW);
                } else { pose.chicoboHeadYaw = 0f; }
                if (hasValue(poseData, NBT_KEY_CHICOBO_HEAD_ROLL)) {
                    pose.chicoboHeadRoll = poseData.getFloat(NBT_KEY_CHICOBO_HEAD_ROLL);
                } else { pose.chicoboHeadRoll = 0f; }
                if (hasValue(poseData, NBT_KEY_CHICOBO_LEFT_LEG_PITCH)) {
                    pose.chicoboLeftLegPitch = poseData.getFloat(NBT_KEY_CHICOBO_LEFT_LEG_PITCH);
                } else { pose.chicoboLeftLegPitch = 0f; }
                if (hasValue(poseData, NBT_KEY_CHICOBO_LEFT_LEG_YAW)) {
                    pose.chicoboLeftLegYaw = poseData.getFloat(NBT_KEY_CHICOBO_LEFT_LEG_YAW);
                } else { pose.chicoboLeftLegYaw = 0f; }
                if (hasValue(poseData, NBT_KEY_CHICOBO_RIGHT_LEG_PITCH)) {
                    pose.chicoboRightLegPitch = poseData.getFloat(NBT_KEY_CHICOBO_RIGHT_LEG_PITCH);
                } else { pose.chicoboRightLegPitch = 0f; }
                if (hasValue(poseData, NBT_KEY_CHICOBO_RIGHT_LEG_YAW)) {
                    pose.chicoboRightLegYaw = poseData.getFloat(NBT_KEY_CHICOBO_RIGHT_LEG_YAW);
                } else { pose.chicoboRightLegYaw = 0f; }
            } else {
                if(hasValue(poseData, NBT_KEY_ADULT_HEAD_PITCH)) {
                    pose.adultHeadPitch = poseData.getFloat(NBT_KEY_ADULT_HEAD_PITCH);
                } else { pose.adultHeadPitch = 0f; }
                if(hasValue(poseData, NBT_KEY_ADULT_HEAD_YAW)) {
                    pose.adultHeadYaw = poseData.getFloat(NBT_KEY_ADULT_HEAD_YAW);
                } else { pose.adultHeadYaw = 0f; }
                if(hasValue(poseData, NBT_KEY_ADULT_HEAD_ROLL)) {
                    pose.adultHeadRoll = poseData.getFloat(NBT_KEY_ADULT_HEAD_ROLL);
                } else { pose.adultHeadRoll = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_NECK_PITCH)) {
                    pose.adultNeckPitch = poseData.getFloat(NBT_KEY_ADULT_NECK_PITCH);
                } else { pose.adultNeckPitch = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_NECK_YAW)) {
                    pose.adultNeckYaw = poseData.getFloat(NBT_KEY_ADULT_NECK_YAW);
                } else { pose.adultNeckYaw = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_NECK_ROLL)) {
                    pose.adultNeckRoll = poseData.getFloat(NBT_KEY_ADULT_NECK_ROLL);
                } else { pose.adultNeckRoll = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_LEFT_WING_PITCH)) {
                    pose.adultLeftWingPitch = poseData.getFloat(NBT_KEY_ADULT_LEFT_WING_PITCH);
                } else { pose.adultLeftWingPitch = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_LEFT_WING_YAW)) {
                    pose.adultLeftWingYaw = poseData.getFloat(NBT_KEY_ADULT_LEFT_WING_YAW);
                } else { pose.adultLeftWingYaw = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_LEFT_WING_ROLL)) {
                    pose.adultLeftWingRoll = poseData.getFloat(NBT_KEY_ADULT_LEFT_WING_ROLL);
                } else { pose.adultLeftWingRoll = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_RIGHT_WING_PITCH)) {
                    pose.adultRightWingPitch = poseData.getFloat(NBT_KEY_ADULT_RIGHT_WING_PITCH);
                } else { pose.adultRightWingPitch = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_RIGHT_WING_YAW)) {
                    pose.adultRightWingYaw = poseData.getFloat(NBT_KEY_ADULT_RIGHT_WING_YAW);
                } else { pose.adultRightWingYaw = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_RIGHT_WING_ROLL)) {
                    pose.adultRightWingRoll = poseData.getFloat(NBT_KEY_ADULT_RIGHT_WING_ROLL);
                } else { pose.adultRightWingRoll = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_LEFT_LEG_PITCH)) {
                    pose.adultLeftLegPitch = poseData.getFloat(NBT_KEY_ADULT_LEFT_LEG_PITCH);
                } else { pose.adultLeftLegPitch = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_LEFT_LEG_YAW)) {
                    pose.adultLeftLegYaw = poseData.getFloat(NBT_KEY_ADULT_LEFT_LEG_YAW);
                } else { pose.adultLeftLegYaw = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_LEFT_LEG_ROLL)) {
                    pose.adultLeftLegRoll = poseData.getFloat(NBT_KEY_ADULT_LEFT_LEG_ROLL);
                } else { pose.adultLeftLegRoll = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_RIGHT_LEG_PITCH)) {
                    pose.adultRightLegPitch = poseData.getFloat(NBT_KEY_ADULT_RIGHT_LEG_PITCH);
                } else { pose.adultRightLegPitch = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_RIGHT_LEG_YAW)) {
                    pose.adultRightLegYaw = poseData.getFloat(NBT_KEY_ADULT_RIGHT_LEG_YAW);
                } else { pose.adultRightLegYaw = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_RIGHT_LEG_ROLL)) {
                    pose.adultRightLegRoll = poseData.getFloat(NBT_KEY_ADULT_RIGHT_LEG_ROLL);
                } else { pose.adultRightLegRoll = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_LEFT_FOOT_PITCH)) {
                    pose.adultLeftFootPitch = poseData.getFloat(NBT_KEY_ADULT_LEFT_FOOT_PITCH);
                } else { pose.adultLeftFootPitch = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_RIGHT_FOOT_PITCH)) {
                    pose.adultRightFootPitch = poseData.getFloat(NBT_KEY_ADULT_RIGHT_FOOT_PITCH);
                } else { pose.adultRightFootPitch = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_TAIL_FEATHERS_PITCH)) {
                    pose.adultTailFeathersPitch = poseData.getFloat(NBT_KEY_ADULT_TAIL_FEATHERS_PITCH);
                } else { pose.adultTailFeathersPitch = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_TAIL_FEATHERS_YAW)) {
                    pose.adultTailFeathersYaw = poseData.getFloat(NBT_KEY_ADULT_TAIL_FEATHERS_YAW);
                } else { pose.adultTailFeathersYaw = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_TAIL_FEATHERS_ROLL)) {
                    pose.adultTailFeathersRoll = poseData.getFloat(NBT_KEY_ADULT_TAIL_FEATHERS_ROLL);
                } else { pose.adultTailFeathersRoll = 0f; }
                if (hasValue(poseData, NBT_KEY_ADULT_HEAD_CREST_PITCH)) {
                    pose.adultHeadCrestPitch = poseData.getFloat(NBT_KEY_ADULT_HEAD_CREST_PITCH);
                } else { pose.adultHeadCrestPitch = 0f; }
            }
        } else { setDefaultPose(pose, armorStand.isBaby()); }
    }
    private static boolean hasValue(@NotNull NbtCompound nbt, String key) {
        return nbt.contains(key) && !nbt.getString(key).isEmpty();
    }
}
