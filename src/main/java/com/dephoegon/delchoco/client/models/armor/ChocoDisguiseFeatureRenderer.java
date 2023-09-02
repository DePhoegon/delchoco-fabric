package com.dephoegon.delchoco.client.models.armor;

import com.dephoegon.delchoco.client.clientHandler;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import static com.dephoegon.delchoco.common.items.ChocoDisguiseItem.NBTKEY_COLOR;

public class ChocoDisguiseFeatureRenderer extends FeatureRenderer<LivingEntity, BipedEntityModel<LivingEntity>> {
    private final EntityModelLoader modelLoader;
    public ChocoDisguiseFeatureRenderer(FeatureRendererContext<LivingEntity, BipedEntityModel<LivingEntity>> context) {
        super(context);
        this.modelLoader = MinecraftClient.getInstance().getEntityModelLoader();
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack stack = entity.getEquippedStack(slot);
                if (stack.getItem() instanceof ChocoDisguiseItem chocoDisguiseItem) {
                    NbtCompound color = stack.getNbt();
                    if (color != null && color.contains(NBTKEY_COLOR)) {
                        Identifier texture = chocoDisguiseItem.setCustomModel(color.getString(NBTKEY_COLOR));
                        // Render the armor with the custom texture
                        ModelPart root = this.modelLoader.getModelPart(clientHandler.CHOCO_DISGUISE_LAYER);
                        ChocoDisguiseModel model = new ChocoDisguiseModel(root, slot);
                        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture)), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                    }
                }
            }
        }
    }
    public Identifier getTexture(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack stack = entity.getEquippedStack(slot);
                if (stack.getItem() instanceof ChocoDisguiseItem chocoDisguiseItem) {
                    NbtCompound color = stack.getNbt();
                    if (color != null && color.contains(NBTKEY_COLOR)) {
                        return chocoDisguiseItem.setCustomModel(color.getString(NBTKEY_COLOR));
                    } else {
                        return chocoDisguiseItem.setCustomModel(ChocoDisguiseItem.yellow);
                    }
                }
            }
        }
        return null;
    }
}