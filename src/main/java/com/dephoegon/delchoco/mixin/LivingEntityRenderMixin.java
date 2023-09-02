package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.client.models.armor.ChocoDisguiseFeatureRenderer;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.dephoegon.delchoco.common.items.ChocoDisguiseItem.NBTKEY_COLOR;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRenderMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow
    protected List<ChocoDisguiseFeatureRenderer> features;
    @Shadow
    protected M model;

    @Inject(method = "addFeature", at = @At("HEAD"))
    private void onAddFeature(FeatureRenderer<T, M> feature, CallbackInfoReturnable<Boolean> cir) {
        M model = this.model;
        FeatureRendererContext<LivingEntity, BipedEntityModel<LivingEntity>> context = new FeatureRendererContext<>() {
            @SuppressWarnings("unchecked")
            @Override
            public BipedEntityModel<LivingEntity> getModel() {
                return (BipedEntityModel<LivingEntity>) model;
            }

            @Override
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
        };
        ChocoDisguiseFeatureRenderer chocoFeature = new ChocoDisguiseFeatureRenderer(context);

        // Add your custom feature renderer to the features list
        this.features.add(chocoFeature);



        //noinspection unchecked
        // this.features.add((FeatureRenderer<T, M>) new ChocoDisguiseFeatureRenderer((FeatureRendererContext<LivingEntity, BipedEntityModel<LivingEntity>>) this));
    }
}