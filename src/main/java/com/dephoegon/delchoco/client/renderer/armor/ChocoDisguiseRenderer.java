package com.dephoegon.delchoco.client.renderer.armor;

import com.dephoegon.delchoco.client.models.ModModelLayers;
import com.dephoegon.delchoco.client.models.armor.ChocoDisguiseModel;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ChocoDisguiseRenderer implements ArmorRenderer {
    private ChocoDisguiseModel model;
    // Cache the last stack hash to detect when rotations need updating
    private int lastStackHash = 0;

    public ChocoDisguiseRenderer() {
        // Model will be initialized lazily when first needed
    }

    private ChocoDisguiseModel getModel() {
        if (this.model == null) {
            this.model = new ChocoDisguiseModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(ModModelLayers.CHOCO_DISGUISE_ARMOR));
        }
        return this.model;
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, @NotNull ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
        if (!(stack.getItem() instanceof ChocoDisguiseItem)) { return; }

        ChocoDisguiseModel model = getModel();
        model.child = contextModel.child;
        model.sneaking = contextModel.sneaking;
        model.riding = contextModel.riding;

        prepareTransforms(contextModel);
        applyHatRotation(stack, slot);
        setVisibility(stack, entity, slot);
        renderModel(matrices, vertexConsumers, getTexture(stack), light);
    }

    private void prepareTransforms(BipedEntityModel<LivingEntity> contextModel) {
        ChocoDisguiseModel model = getModel();
        // All transformations should be copied from the context model to ensure sync.
        model.body.copyTransform(contextModel.body);
        model.head.copyTransform(contextModel.head);
        model.hat.copyTransform(contextModel.hat);
        model.leftArm.copyTransform(contextModel.leftArm);
        model.leftLeg.copyTransform(contextModel.leftLeg);
        model.rightArm.copyTransform(contextModel.rightArm);
        model.rightLeg.copyTransform(contextModel.rightLeg);

        // IMPORTANT: Boots inherit transforms from their parent legs
        // Since boots are children of legs, they automatically inherit leg transforms
        // But we need to ensure the leg transforms are applied first
        // This is already handled by the parent-child relationship in the model
    }

    private void applyHatRotation(ItemStack stack, EquipmentSlot slot) {
        if (slot != EquipmentSlot.HEAD) { return; }

        ChocoDisguiseModel model = getModel();

        // Only update rotations if the stack has changed
        int currentStackHash = stack.hashCode();
        if (currentStackHash != lastStackHash) {
            // Get NBT values as relative offsets from the base transform
            float relativeRotX = ChocoDisguiseItem.getHatRotX(stack);
            float relativeRotY = ChocoDisguiseItem.getHatRotY(stack);
            float relativeRotZ = ChocoDisguiseItem.getHatRotZ(stack);
            model.setRelativeRotations(relativeRotX, relativeRotY, relativeRotZ);
            lastStackHash = currentStackHash;
        }

        // Apply cached rotations (base + relative)
        model.applyRotationsToChocoHead();
    }

    private void setVisibility(ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
        setInvisible(); // Set all parts to invisible first
        if (entity.isInvisible()) { return; }
        setVisible( ChocoDisguiseItem.getRenderMain(stack), ChocoDisguiseItem.getRenderLeft(stack), ChocoDisguiseItem.getRenderRight(stack), slot);
    }
    private Identifier getTexture(ItemStack stack) {
        String color = ChocoDisguiseItem.getCustomModelColor(stack);
        return ChocoDisguiseItem.setCustomModel(color, (ChocoDisguiseItem) stack.getItem());
    }
    private void renderModel(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier texture, int light) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(texture));
        getModel().render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    public void setVisible(boolean renderMain, boolean renderLeft, boolean renderRight, EquipmentSlot slot) {
        // Only control visibility for the specific slot being rendered
        // Don't touch other slots - they will be handled by their own render calls

        switch (slot) {
            case HEAD -> {
                // Control head-related parts only
                getModel().head.visible = renderMain;
                getModel().hat.visible = false; // Always hide hat
                // Only show chocobo head decorations if renderLeft or renderRight is true
                if (getModel().chocoHead != null) {
                    getModel().chocoHead.visible = renderMain && (renderLeft || renderRight);
                }
                getModel().setBootsOnlyRender(false); // Normal rendering
            }
            case CHEST -> {
                // Control chest-related parts only
                getModel().body.visible = renderMain;
                getModel().rightArm.visible = renderRight;
                getModel().leftArm.visible = renderLeft;
                getModel().setBootsOnlyRender(false); // Normal rendering
            }
            case LEGS -> {
                // Control leg-related parts only - keep as is since they work properly
                getModel().rightLeg.visible = renderRight;
                getModel().leftLeg.visible = renderLeft;
                getModel().setBootsOnlyRender(false); // Normal rendering (show leg geometry)
            }
            case FEET -> {
                // For boots: legs must be visible as parents, but we don't want leg geometry
                getModel().rightLeg.visible = renderRight;  // Parent must be visible
                getModel().leftLeg.visible = renderLeft;   // Parent must be visible

                // Control only the boot parts visibility
                getModel().armor_right_boot.visible = renderRight;
                getModel().armor_left_boot.visible = renderLeft;

                // Enable boots-only rendering mode to skip leg geometry
                getModel().setBootsOnlyRender(true);
            }
        }
    }

    private void setInvisible() {
        // This method is now only used when the entity is invisible
        ChocoDisguiseModel model = getModel();
        model.head.visible = false;
        model.hat.visible = false;
        model.body.visible = false;
        model.rightArm.visible = false;
        model.leftArm.visible = false;
        model.rightLeg.visible = false;
        model.leftLeg.visible = false;
        model.armor_right_boot.visible = false;
        model.armor_left_boot.visible = false;
        if (model.chocoHead != null) {
            model.chocoHead.visible = false;
        }
    }
}
