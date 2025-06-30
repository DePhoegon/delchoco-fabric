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
    private final ChocoDisguiseModel model;
    public ChocoDisguiseRenderer() {
        this.model = new ChocoDisguiseModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(ModModelLayers.CHOCO_DISGUISE_ARMOR));
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, @NotNull ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
        if (!(stack.getItem() instanceof ChocoDisguiseItem armorItem)) return;
        prepareTransforms(contextModel);
        applyHatRotation(stack, slot);
        setVisibility(stack, entity, slot);
        renderModel(matrices, vertexConsumers, stack, armorItem, light);
    }

    private void prepareTransforms(BipedEntityModel<LivingEntity> contextModel) {
        this.model.body.copyTransform(contextModel.body);
        this.model.head.copyTransform(contextModel.head);
        this.model.hat.copyTransform(contextModel.hat);
        this.model.leftArm.copyTransform(contextModel.leftArm);
        this.model.leftLeg.copyTransform(contextModel.leftLeg);
        this.model.rightArm.copyTransform(contextModel.rightArm);
        this.model.rightLeg.copyTransform(contextModel.rightLeg);
    }

    private void applyHatRotation(ItemStack stack, EquipmentSlot slot) {
        if (slot != EquipmentSlot.HEAD || !(stack.getItem() instanceof ChocoDisguiseItem armor && armor.getSlotType() == EquipmentSlot.HEAD)) return;
        this.model.hat.pitch += ChocoDisguiseItem.getHatRotX(stack);
        this.model.hat.yaw += ChocoDisguiseItem.getHatRotY(stack);
        this.model.hat.roll += ChocoDisguiseItem.getHatRotZ(stack);
    }

    private void setVisibility(ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
        if (entity.isInvisible()) { setInvisible(); }
        else { setVisible( ChocoDisguiseItem.getRenderMain(stack), ChocoDisguiseItem.getRenderLeft(stack), ChocoDisguiseItem.getRenderRight(stack), slot); }
    }

    private void renderModel(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, ChocoDisguiseItem armorItem, int light) {
        String color = ChocoDisguiseItem.getCustomModelColor(stack);
        Identifier texture = ChocoDisguiseItem.setCustomModel(color, armorItem);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(texture));
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    public void setVisible(boolean renderMain, boolean renderLeft, boolean renderRight, EquipmentSlot slot) {
        switch (slot) {
            case HEAD -> setHeadVisible(renderMain, renderLeft || renderRight);
            case CHEST -> setChestVisible(renderMain, renderRight, renderLeft);
            case LEGS -> setLegsVisible(renderRight, renderLeft);
            case FEET -> setFeetVisible(renderRight, renderLeft);
            default -> setInvisible();
        }
    }
    private void setInvisible() {
        this.model.head.visible = false;
        this.model.hat.visible = false;
        this.model.body.visible = false;
        this.model.rightArm.visible = false;
        this.model.leftArm.visible = false;
        this.model.rightLeg.visible = false;
        this.model.leftLeg.visible = false;
    }
    private void setHeadVisible(boolean head, boolean hat) {
        this.model.head.visible = head;
        this.model.hat.visible = hat;
    }
    private void setChestVisible(boolean chest, boolean right, boolean left) {
        this.model.body.visible = chest;
        this.model.rightArm.visible = right;
        this.model.leftArm.visible = left;
    }
    private void setLegsVisible(boolean right, boolean left) {
        this.model.rightLeg.visible = right;
        this.model.leftLeg.visible = left;
    }
    private void setFeetVisible(boolean right, boolean left) {
        this.model.armorRightBoot.visible = right;
        this.model.armorLeftBoot.visible = left;
    }
}