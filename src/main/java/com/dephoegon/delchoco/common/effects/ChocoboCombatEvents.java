package com.dephoegon.delchoco.common.effects;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.common.items.ChocoDisguiseItem.*;
import static com.dephoegon.delchoco.utils.RandomHelper.random;

public class ChocoboCombatEvents {
    void onChocoboCombatDeath(Entity attacker, Entity target){};
    public static boolean onChocoboCombatAttack(LivingEntity attacker, Entity target, DamageSource source){
        Chocobo chocoboAttacker = attacker instanceof Chocobo choco ? choco : null;
        Chocobo chocoboTarget = target instanceof Chocobo choco ? choco : null;
        PlayerEntity playerTarget = target instanceof PlayerEntity player ? player : null;
        DamageSource damageSource = event.getSource();
        if (chocoboAttacker != null && ChocoConfigGet(COMMON.chocoboResourcesOnHit.get(), dExtraChocoboResourcesOnHit)) {
            LivingEntity target = event.getEntityLiving();
            if (target instanceof Spider e) { if (onHitMobChance(10)) { e.spawnAtLocation(STRING); } }
            if (target instanceof CaveSpider e) { if (onHitMobChance(5)) { e.spawnAtLocation(FERMENTED_SPIDER_EYE); } }
            if (target instanceof Skeleton e) { if (onHitMobChance(10)) { e.spawnAtLocation(BONE); } }
            if (target instanceof WitherSkeleton e) { if (onHitMobChance(10)) { e.spawnAtLocation(CHARCOAL); } }
            if (target instanceof IronGolem e) { if (onHitMobChance(5)) { e.spawnAtLocation(POPPY); } }
            if (target.getItemBySlot(EquipmentSlot.MAINHAND) != ItemStack.EMPTY) {
                if (onHitMobChance(30)) {
                    target.spawnAtLocation(target.getItemBySlot(EquipmentSlot.MAINHAND));
                    target.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
            }
            if (target.getItemBySlot(EquipmentSlot.OFFHAND) != ItemStack.EMPTY) {
                if (onHitMobChance(10)) {
                    target.spawnAtLocation(target.getItemBySlot(EquipmentSlot.OFFHAND));
                    target.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                }
            }
        }
        if (chocoboTarget != null && chocoboTarget.isTame()) {
            Player source = event.getSource().getEntity() instanceof Player play ? play : null;
            Player owner = chocoboTarget.getOwner() instanceof Player play ? play : null;
            Team group = owner != null ? owner.getTeam() : null;
            if (source != null) {boolean shift = ChocoConfigGet(COMMON.shiftBypassAllowed.get(), dShiftHitBypass) && source.isShiftKeyDown();
                boolean teams = group != null && source.getTeam() == group;
                if (!shift) {
                    if (!ChocoConfigGet(COMMON.ownChocoboHittable.get(), dOwnChocoboHittable)) {
                        event.setCanceled((owner == source) || teams);
                        return;
                    }
                    if (!ChocoConfigGet(COMMON.tamedChocoboHittable.get(), dTamedChocoboHittable)) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
        if (chocoboTarget != null) {
            if (random.nextInt(100) + 1 > 35) {
                chocoboTarget.dropItem(ModItems.CHOCOBO_FEATHER);
            }
        }
        if (playerTarget != null && ChocoConfigGet(COMMON.extraChocoboEffects.get(), dExtraChocoboEffects)) {
            ItemStack hStack = playerTarget.getItemBySlot(EquipmentSlot.HEAD);
            ItemStack cStack = playerTarget.getItemBySlot(EquipmentSlot.CHEST);
            ItemStack lStack = playerTarget.getItemBySlot(EquipmentSlot.LEGS);
            ItemStack fStack = playerTarget.getItemBySlot(EquipmentSlot.FEET);
            if (armorColorMatch(hStack, cStack, lStack, fStack)) {
                String headColor = getNBTKEY_COLOR(hStack);
                if (damageSource == DamageSource.WITHER) {
                    event.setCanceled(headColor.equals(black) || headColor.equals(red) || headColor.equals(purple) || headColor.equals(gold) || headColor.equals(pink));
                    return;
                }
                if (damageSource == DamageSource.DRAGON_BREATH) {
                    event.setCanceled(headColor.equals(purple) || headColor.equals(gold));
                    return;
                }
                if (damageSource == DamageSource.SWEET_BERRY_BUSH) {
                    event.setCanceled(true);
                    return;
                }
                if (damageSource == DamageSource.FREEZE) {
                    event.setCanceled(headColor.equals(white) || headColor.equals(gold));
                }
            }
        }
    };
    private static String getNBTKEY_COLOR(@NotNull ItemStack item) {
        NbtCompound tag = item.getNbt();
        if (tag != null && tag.contains(NBTKEY_COLOR)) { return tag.getString(NBTKEY_COLOR); }
        else { return yellow; }
    }
    private boolean armorColorMatch(@NotNull ItemStack itemStack1, ItemStack itemStack2, ItemStack itemStack3, ItemStack itemStack4) {
        if (!(itemStack1.getItem() instanceof ChocoDisguiseItem) || !(itemStack2.getItem() instanceof ChocoDisguiseItem) || !(itemStack3.getItem() instanceof ChocoDisguiseItem) || !(itemStack4.getItem() instanceof ChocoDisguiseItem)) { return false; }
        return getNBTKEY_COLOR(itemStack1).equals(getNBTKEY_COLOR(itemStack2)) && getNBTKEY_COLOR(itemStack1).equals(getNBTKEY_COLOR(itemStack3)) && getNBTKEY_COLOR(itemStack1).equals(getNBTKEY_COLOR(itemStack4));
    }
}