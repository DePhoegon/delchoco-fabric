package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.client.models.armor.ChocoDisguiseFeatureRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ALL")
@Mixin(PlayerEntityRenderer.class)
abstract class PlayerEntityRendererMixin extends LivingEntityRenderer {
    @SuppressWarnings("unchecked")
    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, EntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.features.add(new ChocoDisguiseFeatureRenderer(this));
    }
}