package com.dephoegon.delchoco.client.models.entities;

import com.dephoegon.delchoco.client.animation.ChocoboAnimationHandler;
import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;

public class AdultChocoboModel<T extends Chocobo> extends EntityModel<Chocobo> {
    // Constants to avoid magic numbers and repeated calculations
    private static final float DEG_TO_RAD = (float) Math.PI / 180.0F;
    private static final float PI = (float) Math.PI;
    private static final float PI_HALF = PI / 2.0F;
    private static final float DEFAULT_LEG_PITCH = 0.2094395F;

    // Cached angle values for better performance
    private static final float WING_IDLE_Y_RIGHT = -0.0174533F;
    private static final float WING_IDLE_Y_LEFT = 0.0174533F;

    protected final ModelPart root;
    protected final ModelPart head;
    protected final ModelPart neck;
    protected final ModelPart leg_left;
    protected final ModelPart leg_right;
    protected final ModelPart foot_left;
    protected final ModelPart foot_right;
    protected final ModelPart tail_feathers;
    protected final ModelPart head_crest;
    protected final ModelPart wing_left;
    protected final ModelPart wing_right;

    // Animation handler
    private final ChocoboAnimationHandler animationHandler;
    private String currentPose;

    public AdultChocoboModel(@NotNull ModelPart root) {
        ModelPart wingRight1;
        ModelPart wingLeft1;
        this.root = root.getChild("root");
        ModelPart body = this.root.getChild("body");

        this.neck = body.getChild("chest").getChild("neck");
        this.head = neck.getChild("head");
        this.leg_left = body.getChild("leg_left");
        this.leg_right = body.getChild("leg_right");

        // Initialize wing fields and handle potential missing parts
        try {
            wingLeft1 = body.getChild("wing_left");
        } catch (Exception e) {
            wingLeft1 = null;
        }

        this.wing_left = wingLeft1;
        try {
            wingRight1 = body.getChild("wing_right");
        } catch (Exception e) {
            wingRight1 = null;
        }

        this.wing_right = wingRight1;
        this.foot_left = leg_left.getChild("foot_left");
        this.foot_right = leg_right.getChild("foot_right");
        this.tail_feathers = body.getChild("fan_top_r1");
        this.head_crest = head.getChild("crest_top_r1");

        // Initialize animation handler
        this.animationHandler = new ChocoboAnimationHandler();
        this.currentPose = "";
    }

    /**
     * Sets the current pose for the model
     *
     * @param pose The pose name to apply
     */
    public void setPose(String pose) {
        this.currentPose = pose;
    }

    /**
     * Gets the root model part
     *
     * @return The root ModelPart
     */
    public ModelPart getRoot() {
        return root;
    }

    /**
     * Gets the head model part
     *
     * @return The head ModelPart
     */
    public ModelPart getHead() {
        return head;
    }

    /**
     * Gets the neck model part
     *
     * @return The neck ModelPart
     */
    public ModelPart getNeck() {
        return neck;
    }

    /**
     * Gets the left leg model part
     *
     * @return The left leg ModelPart
     */
    public ModelPart getLeftLeg() {
        return leg_left;
    }

    /**
     * Gets the right leg model part
     *
     * @return The right leg ModelPart
     */
    public ModelPart getRightLeg() {
        return leg_right;
    }

    /**
     * Gets the left foot model part
     *
     * @return The left foot ModelPart
     */
    public ModelPart getLeftFoot() {
        return foot_left;
    }

    /**
     * Gets the right foot model part
     *
     * @return The right foot ModelPart
     */
    public ModelPart getRightFoot() {
        return foot_right;
    }

    /**
     * Gets the tail feathers model part
     *
     * @return The tail feathers ModelPart
     */
    public ModelPart getTailFeathers() {
        return tail_feathers;
    }

    /**
     * Gets the head crest model part
     *
     * @return The head crest ModelPart
     */
    public ModelPart getHeadCrest() {
        return head_crest;
    }

    /**
     * Gets the left wing model part
     *
     * @return The left wing ModelPart
     */
    public ModelPart getLeftWing() {
        return wing_left;
    }

    /**
     * Gets the right wing model part
     *
     * @return The right wing ModelPart
     */
    public ModelPart getRightWing() {
        return wing_right;
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

    @Override
    public void animateModel(@NotNull Chocobo entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.animateModel(entityIn, limbSwing, limbSwingAmount, partialTick);
        // Delegate to animation handler
        animationHandler.animate(this, entityIn, limbSwing, limbSwingAmount, partialTick, 0, 0, currentPose);
    }

    @Override
    public void setAngles(@NotNull Chocobo entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Delegate to animation handler
        animationHandler.animate(this, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, currentPose);
    }
}
