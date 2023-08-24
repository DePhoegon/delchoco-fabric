package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.common.items.ChocoboArmorItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    public abstract Iterable<ItemStack> getArmorItems();

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void onIsInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (source.isFire()) {
            boolean isWearingFullDiamondArmor = true;
            ItemStack helmet = getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chestplate = getEquippedStack(EquipmentSlot.CHEST);
            ItemStack leggings = getEquippedStack(EquipmentSlot.LEGS);
            ItemStack boots = getEquippedStack(EquipmentSlot.FEET);
            boolean match = true;
            if (helmet.getItem() instanceof ChocoboArmorItems) {
                ArmorMaterial material = ((ChocoboArmorItems) helmet.getItem()).getMaterial();
                if (material != ArmorMaterials.DIAMOND) {
                    match = false;
                }
            } else {
                match = false;
            }
            for (ItemStack armorItem : getArmorItems()) {
                if (armorItem.getItem() instanceof ChocoboArmorItems) {
                    ArmorMaterial material = ((ChocoboArmorItems) armorItem.getItem()).getMaterial();
                    if (material != ArmorMaterials.DIAMOND) {
                        isWearingFullDiamondArmor = false;
                        break;
                    }
                } else {
                    isWearingFullDiamondArmor = false;
                    break;
                }
            }
            if (isWearingFullDiamondArmor) {
                cir.setReturnValue(true);
            }
        }
    }

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
}
