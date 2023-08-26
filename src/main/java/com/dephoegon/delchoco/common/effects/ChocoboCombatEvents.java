package com.dephoegon.delchoco.common.effects;

import com.dephoegon.delchoco.aid.world.StaticGlobalVariables;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.items.ChocoDisguiseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.ChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.dValues.defaultBooleans.*;
import static com.dephoegon.delchoco.common.init.ModItems.*;
import static com.dephoegon.delchoco.common.items.ChocoDisguiseItem.*;
import static com.dephoegon.delchoco.utils.RandomHelper.random;
import static net.minecraft.item.Items.*;

public class ChocoboCombatEvents {
    void onChocoboCombatDeath(Entity attacker, Entity target){};
    public static boolean onChocoboCombatAttack(LivingEntity attackerEntity, Entity targetEntity){
        Chocobo chocoboAttacker = attackerEntity instanceof Chocobo choco ? choco : null;
        Chocobo chocoboTarget = targetEntity instanceof Chocobo choco ? choco : null;
        if (chocoboAttacker != null && ChocoConfigGet(StaticGlobalVariables.getExtraChocoboResourcesOnHit(), dExtraChocoboResourcesOnHit)) {
            LivingEntity target = targetEntity instanceof LivingEntity living ? living : null;
            if (target instanceof SpiderEntity e) { if (onHitMobChance(10)) { e.dropItem(STRING); } }
            if (target instanceof CaveSpiderEntity e) { if (onHitMobChance(5)) { e.dropItem(FERMENTED_SPIDER_EYE); } }
            if (target instanceof SkeletonEntity e) { if (onHitMobChance(10)) { e.dropItem(BONE); } }
            if (target instanceof WitherSkeletonEntity e) { if (onHitMobChance(10)) { e.dropItem(CHARCOAL); } }
            if (target instanceof IronGolemEntity e) { if (onHitMobChance(5)) { e.dropItem(POPPY); } }
            if (target != null && target.getEquippedStack(EquipmentSlot.MAINHAND) != ItemStack.EMPTY) {
                if (onHitMobChance(30)) {
                    target.dropItem(target.getEquippedStack(EquipmentSlot.MAINHAND).getItem());
                    target.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                }
            }
            if (target != null && target.getEquippedStack(EquipmentSlot.OFFHAND) != ItemStack.EMPTY) {
                if (onHitMobChance(10)) {
                    target.dropItem(target.getEquippedStack(EquipmentSlot.OFFHAND).getItem());
                    target.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
                }
            }
        }
        if (chocoboTarget != null) {
            if (random.nextInt(100) + 1 > 35) {
                chocoboTarget.dropItem(ModItems.CHOCOBO_FEATHER);
            }
        }
        if (chocoboTarget != null && chocoboTarget.isTamed()) {
            PlayerEntity source = attackerEntity instanceof PlayerEntity play ? play : null;
            PlayerEntity owner = chocoboTarget.getOwner() instanceof PlayerEntity play ? play : null;
            Team group = owner != null ? (Team) owner.getScoreboardTeam() : null;
            if (source != null) { boolean shift = ChocoConfigGet(StaticGlobalVariables.getShiftHitBypass(), dShiftHitBypass) && source.isSneaking();
                boolean teams = group != null && source.getScoreboardTeam() == group;
                if (!shift) {
                    if (!ChocoConfigGet(StaticGlobalVariables.getOwnChocoboHittable(), dOwnChocoboHittable)) { return (owner == source) || teams; }
                    return !ChocoConfigGet(StaticGlobalVariables.getTamedChocoboHittable(), dTamedChocoboHittable);
                }
            }
        }
        return false;
    }
    public static boolean playerDamageImmunityCheck(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet, DamageSource source) {
        if (ChocoConfigGet(StaticGlobalVariables.getExtraChocoboEffects(), dExtraChocoboEffects)) {
            if (armorColorMatch(head, chest, legs, feet)) {
                String headColor = getNBTKEY_COLOR(head);
                if (source == DamageSource.WITHER) { return headColor.equals(black) || headColor.equals(red) || headColor.equals(purple) || headColor.equals(gold) || headColor.equals(pink); }
                if (source == DamageSource.DRAGON_BREATH) { return headColor.equals(purple) || headColor.equals(gold); }
                if (source == DamageSource.SWEET_BERRY_BUSH) { return true; }
                if (source == DamageSource.FREEZE) { return headColor.equals(white) || headColor.equals(gold); }
            }
        }
        return false;
    }
    public static void onChocoboKillOrDie(LivingEntity attackerEntity, Entity targetEntity) {
        Chocobo chocoboKill = attackerEntity instanceof Chocobo choco ? choco : null;
        Chocobo chocoboDie = targetEntity instanceof Chocobo choco ? choco : null;
        if (chocoboKill != null && ChocoConfigGet(StaticGlobalVariables.getExtraChocoboResourcesOnKill(), dExtraChocoboResourcesOnKill)) {
            ChocoboColor color = chocoboKill.getChocoboColor();
            if (targetEntity instanceof SpiderEntity) { if (.20f > (float) Math.random()) { targetEntity.dropItem(COBWEB); } }
            if (color == ChocoboColor.BLACK) { if (flowerChance()) {
                if (.50f > (float) Math.random()) { targetEntity.dropItem(WITHER_ROSE); }
                else { targetEntity.dropItem(DEAD_BUSH); }
            }}
            if (color == ChocoboColor.FLAME) {
                if (flowerChance()) {
                    if (.50f > (float) Math.random()) { targetEntity.dropItem(CRIMSON_FUNGUS); }  else { targetEntity.dropItem(WARPED_FUNGUS); }
                } else { if (.10f > (float) Math.random()) { targetEntity.dropItem(MAGMA_CREAM); } }
            }
            if (color == ChocoboColor.GREEN) { if (flowerChance()) {
                if (.34f > (float) Math.random()) { targetEntity.dropItem(SPORE_BLOSSOM); } else {
                    if (.51f > (float) Math.random()) { targetEntity.dropItem(SMALL_DRIPLEAF); }
                    else { targetEntity.dropItem(MOSS_BLOCK); }
                }
            }}
            if (color == ChocoboColor.WHITE) {
                if (flowerChance()) {
                    if (.34f > (float) Math.random()) { targetEntity.dropItem(SNOWBALL); } else {
                        if (.51f > (float) Math.random()) { targetEntity.dropItem(LILY_OF_THE_VALLEY); }
                        else { targetEntity.dropItem(OXEYE_DAISY); }
                    }
                } else if (.41f > (float) Math.random()) { targetEntity.dropItem(BONE_MEAL); }
            }
            if (color == ChocoboColor.GOLD) {
                if (flowerChance()) { targetEntity.dropItem(SUNFLOWER);}
                else { if (.03f > (float) Math.random()) { targetEntity.dropItem(GOLD_NUGGET); } }
            }
            if (color == ChocoboColor.BLUE) { if (flowerChance()) {
                if (.50f > (float) Math.random()) { targetEntity.dropItem(KELP); } else { targetEntity.dropItem(SEA_PICKLE); }
                if (.10f > (float) Math.random()) { targetEntity.dropItem(NAUTILUS_SHELL); }
            }}
            if (color == ChocoboColor.PINK) { if (flowerChance()) {
                if (.34f > (float) Math.random()) { targetEntity.dropItem(BROWN_MUSHROOM); } else {
                    if (.51f > (float) Math.random()) { targetEntity.dropItem(RED_MUSHROOM); }
                    else { targetEntity.dropItem(ALLIUM); }
                }
            }}
            if (color == ChocoboColor.RED) { if (flowerChance()) {
                if (.34f > (float) Math.random()) { targetEntity.dropItem(STICK); } else {
                    if (.51f > (float) Math.random()) { targetEntity.dropItem(BAMBOO); }
                    else { targetEntity.dropItem(VINE); }
                }
            }}
            if (color == ChocoboColor.PURPLE) {
                if (flowerChance()) { targetEntity.dropItem(CHORUS_FLOWER); }
                else if (.09f > (float) Math.random()) { targetEntity.dropItem(ENDER_PEARL); }
            }
            if (color == ChocoboColor.YELLOW) { if (flowerChance()) {
                Item flower = switch (random.nextInt(12)+1) {
                    default -> DANDELION;
                    case 2 -> POPPY;
                    case 3 -> BLUE_ORCHID;
                    case 4 -> ALLIUM;
                    case 5 -> AZURE_BLUET;
                    case 6 -> RED_TULIP;
                    case 7 -> ORANGE_TULIP;
                    case 8 -> WHITE_TULIP;
                    case 9 -> PINK_TULIP;
                    case 10 -> OXEYE_DAISY;
                    case 11 -> CORNFLOWER;
                    case 12 -> LILY_OF_THE_VALLEY;
                };
                targetEntity.dropItem(flower);
            }}
        }
        if (chocoboDie != null) {
            @NotNull ItemStack egg = switch (chocoboDie.getChocoboColor()) {
                case YELLOW -> new ItemStack(YELLOW_CHOCOBO_SPAWN_EGG);
                case WHITE -> new ItemStack(WHITE_CHOCOBO_SPAWN_EGG);
                case GREEN -> new ItemStack(GREEN_CHOCOBO_SPAWN_EGG);
                case FLAME -> new ItemStack(FLAME_CHOCOBO_SPAWN_EGG);
                case BLACK -> new ItemStack(BLACK_CHOCOBO_SPAWN_EGG);
                case GOLD -> new ItemStack(GOLD_CHOCOBO_SPAWN_EGG);
                case BLUE -> new ItemStack(BLUE_CHOCOBO_SPAWN_EGG);
                case RED -> new ItemStack(RED_CHOCOBO_SPAWN_EGG);
                case PINK -> new ItemStack(PINK_CHOCOBO_SPAWN_EGG);
                case PURPLE -> new ItemStack(PURPLE_CHOCOBO_SPAWN_EGG);
            };
            if (random.nextInt(1000)+1 < 85) { chocoboDie.dropStack(egg); }
        }
    }
    private static boolean flowerChance() { return random.nextInt(100)+1 < 45; }
    private static boolean onHitMobChance(int percentChance) { return random.nextInt(100)+1 < percentChance; }
    private static String getNBTKEY_COLOR(@NotNull ItemStack item) {
        NbtCompound tag = item.getNbt();
        if (tag != null && tag.contains(NBTKEY_COLOR)) { return tag.getString(NBTKEY_COLOR); }
        else { return yellow; }
    }
    private static boolean armorColorMatch(@NotNull ItemStack itemStack1, ItemStack itemStack2, ItemStack itemStack3, ItemStack itemStack4) {
        if (!(itemStack1.getItem() instanceof ChocoDisguiseItem) || !(itemStack2.getItem() instanceof ChocoDisguiseItem) || !(itemStack3.getItem() instanceof ChocoDisguiseItem) || !(itemStack4.getItem() instanceof ChocoDisguiseItem)) { return false; }
        return getNBTKEY_COLOR(itemStack1).equals(getNBTKEY_COLOR(itemStack2)) && getNBTKEY_COLOR(itemStack1).equals(getNBTKEY_COLOR(itemStack3)) && getNBTKEY_COLOR(itemStack1).equals(getNBTKEY_COLOR(itemStack4));
    }
}