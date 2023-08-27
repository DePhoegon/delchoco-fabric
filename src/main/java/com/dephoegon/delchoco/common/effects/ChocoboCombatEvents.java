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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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

import static com.dephoegon.delchoco.aid.chocoboChecks.isPoisonImmuneChocobo;
import static com.dephoegon.delchoco.aid.chocoboChecks.isWitherImmuneChocobo;
import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.ChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.dValues.defaultBooleans.*;
import static com.dephoegon.delchoco.common.entities.properties.ChocoboColor.*;
import static com.dephoegon.delchoco.common.init.ModItems.*;
import static com.dephoegon.delchoco.common.items.ChocoDisguiseItem.NBTKEY_COLOR;
import static com.dephoegon.delchoco.utils.RandomHelper.random;
import static net.minecraft.item.Items.*;

public class ChocoboCombatEvents {
    /**
     * @return False if the attack should be canceled, True if it should go through
     * @implNote  Chance of dropping Chocobo feathers on Chocobo being hit
     * Intended to be used in the Chocobo#applyDamageEffects override method
     */
    public static boolean onChocoboCombatGetHit(LivingEntity attackerEntity, Chocobo chocoboTarget){
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
                    if (owner == source || teams) { return ChocoConfigGet(StaticGlobalVariables.getOwnChocoboHittable(), dOwnChocoboHittable); }
                    return ChocoConfigGet(StaticGlobalVariables.getTamedChocoboHittable(), dTamedChocoboHittable);
                }
            }
        }
        return true;
    }

    /**
     * @implNote Chance of dropping items on mob hit, enabled with config setting "extraChocoboResourcesOnHit"
     * Intended to be used in the Entity#applyDamageEffects mixin method
     */
    public static void onChocoboCombatHit(Chocobo chocoboAttacker, Entity targetEntity) {
        if (chocoboAttacker != null) {
            if (ChocoConfigGet(StaticGlobalVariables.getExtraChocoboResourcesOnHit(), dExtraChocoboResourcesOnHit)) {
                LivingEntity target = targetEntity instanceof LivingEntity living ? living : null;
                if (target instanceof SpiderEntity e) { onHitMobChance(10, STRING, e); }
                if (target instanceof CaveSpiderEntity e) { onHitMobChance(5, FERMENTED_SPIDER_EYE, e); }
                if (target instanceof SkeletonEntity e) { onHitMobChance(10, BONE, e); }
                if (target instanceof WitherSkeletonEntity e) { onHitMobChance(10, CHARCOAL, e); }
                if (target instanceof IronGolemEntity e) { onHitMobChance(5, POPPY, e); }
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
        }
    }

    /**
     * @return True if the Player is immune to the DamageSource, False if not
     * @implNote Checks for matching armor set for Player Immunity with ChocoGuise Gear,
     * enabled with config setting "extraChocoboEffects"
     * Intended to be used in the PlayerEntity#isInvulnerableTo mixin method
     */
    public static boolean playerDamageImmunityCheck(ItemStack HeadStack, ItemStack ChestStack, ItemStack LegStack, ItemStack FeetStack, DamageSource source) {
        if (ChocoConfigGet(StaticGlobalVariables.getExtraChocoboEffects(), dExtraChocoboEffects)) {
            if (armorColorMatch(HeadStack, ChestStack, LegStack, FeetStack)) {
                ChocoboColor headColor = getNBTKEY_COLOR(HeadStack);
                if (source == DamageSource.WITHER) { return headColor.equals(BLACK) || headColor.equals(RED) || headColor.equals(PURPLE) || headColor.equals(GOLD) || headColor.equals(PINK); }
                if (source == DamageSource.DRAGON_BREATH) { return headColor.equals(PURPLE) || headColor.equals(GOLD); }
                if (source == DamageSource.FREEZE) { return headColor.equals(WHITE) || headColor.equals(GOLD); }
            }
            if (source == DamageSource.SWEET_BERRY_BUSH) { return armorMatch(HeadStack, ChestStack, LegStack, FeetStack); }
        }
        return false;
    }

    /**
     * @param effect StatusEffectInstance to be applied
     * @return True if the Player is immune to the StatusEffect, False if not
     * @implNote Checks for matching armor set for Player Immunity with ChocoGuise Gear
     */
    public static boolean playerStatusImmunityCheck(StatusEffectInstance effect, ItemStack HeadStack, ItemStack ChestStack, ItemStack LegStack, ItemStack FeetStack) {
        StatusEffect statusEffect;
        if (armorColorMatch(HeadStack, ChestStack, LegStack, FeetStack)) {
            ChocoboColor headColor = getNBTKEY_COLOR(HeadStack);
            statusEffect = effect.getEffectType();
            if (statusEffect == StatusEffects.WITHER) { return isWitherImmuneChocobo(headColor); }
            if (statusEffect == StatusEffects.POISON) { return isPoisonImmuneChocobo(headColor); }
            if (statusEffect == StatusEffects.SLOWNESS) { return headColor.equals(GOLD); }
        }
        return false;
    }

    /**
     * @implNote  Chance of dropping items on mob kill, enabled with config setting "extraChocoboResourcesOnKill"
     * Intended to be used in the LivingEntity#onDeath mixin method
     */
    public static void onChocoboKill(Chocobo chocoboAttacker, Entity targetEntity) {
        if (chocoboAttacker != null && targetEntity != null) {
            if (ChocoConfigGet(StaticGlobalVariables.getExtraChocoboResourcesOnKill(), dExtraChocoboResourcesOnKill)) {
                ChocoboColor color = chocoboAttacker.getChocoboColor();
                if (targetEntity instanceof SpiderEntity) {
                    if (.20f > (float) Math.random()) { targetEntity.dropItem(COBWEB); }
                }
                if (color == ChocoboColor.BLACK) {
                    if (flowerChance()) {
                        if (.50f > (float) Math.random()) { targetEntity.dropItem(WITHER_ROSE); }
                        else { targetEntity.dropItem(DEAD_BUSH); }
                    }
                }
                if (color == ChocoboColor.FLAME) {
                    if (flowerChance()) {
                        if (.50f > (float) Math.random()) { targetEntity.dropItem(CRIMSON_FUNGUS); }
                        else { targetEntity.dropItem(WARPED_FUNGUS); }
                    } else {
                        if (.10f > (float) Math.random()) { targetEntity.dropItem(MAGMA_CREAM); }
                    }
                }
                if (color == ChocoboColor.GREEN) {
                    if (flowerChance()) {
                        if (.34f > (float) Math.random()) { targetEntity.dropItem(SPORE_BLOSSOM); }
                        else {
                            if (.51f > (float) Math.random()) { targetEntity.dropItem(SMALL_DRIPLEAF); }
                            else { targetEntity.dropItem(MOSS_BLOCK); }
                        }
                    }
                }
                if (color == ChocoboColor.WHITE) {
                    if (flowerChance()) {
                        if (.34f > (float) Math.random()) { targetEntity.dropItem(SNOWBALL); }
                        else {
                            if (.51f > (float) Math.random()) { targetEntity.dropItem(LILY_OF_THE_VALLEY); }
                            else { targetEntity.dropItem(OXEYE_DAISY); }
                        }
                    } else if (.41f > (float) Math.random()) { targetEntity.dropItem(BONE_MEAL); }
                }
                if (color == ChocoboColor.GOLD) {
                    if (flowerChance()) { targetEntity.dropItem(SUNFLOWER);}
                    else {
                        if (.03f > (float) Math.random()) { targetEntity.dropItem(GOLD_NUGGET); }
                    }
                }
                if (color == ChocoboColor.BLUE) {
                    if (flowerChance()) {
                        if (.50f > (float) Math.random()) { targetEntity.dropItem(KELP); }
                        else { targetEntity.dropItem(SEA_PICKLE); }
                        if (.10f > (float) Math.random()) { targetEntity.dropItem(NAUTILUS_SHELL); }
                    }
                }
                if (color == ChocoboColor.PINK) {
                    if (flowerChance()) {
                        if (.34f > (float) Math.random()) { targetEntity.dropItem(BROWN_MUSHROOM); }
                        else {
                            if (.51f > (float) Math.random()) { targetEntity.dropItem(RED_MUSHROOM); }
                            else { targetEntity.dropItem(ALLIUM); }
                        }
                    }
                }
                if (color == ChocoboColor.RED) {
                    if (flowerChance()) {
                        if (.34f > (float) Math.random()) { targetEntity.dropItem(STICK); }
                        else {
                            if (.51f > (float) Math.random()) { targetEntity.dropItem(BAMBOO); }
                            else { targetEntity.dropItem(VINE); }
                        }
                    }
                }
                if (color == ChocoboColor.PURPLE) {
                    if (flowerChance()) { targetEntity.dropItem(CHORUS_FLOWER); }
                    else if (.09f > (float) Math.random()) { targetEntity.dropItem(ENDER_PEARL); }
                }
                if (color == ChocoboColor.YELLOW) {
                    if (flowerChance()) {
                        Item flower = switch (random.nextInt(12) + 1) {
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
                    }
                }
            }
        }
    }

    /**
     * @implNote Chance to drop a Chocobo spawn egg of the same color
     * Intended to be used in the Chocobo#onDeath override method
     */
    public static void onChocoboDeath(@NotNull Chocobo dyingChocobo) {
        @NotNull ItemStack egg = switch (dyingChocobo.getChocoboColor()) {
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
        if (random.nextInt(1000)+1 < 85) { dyingChocobo.dropStack(egg); }
    }
    private static boolean flowerChance() { return random.nextInt(100)+1 < 45; }
    private static boolean onHitMobChance(int percentChance) { return random.nextInt(100)+1 < percentChance; }
    private static void onHitMobChance(int percentChance, Item item, Entity e) { if (random.nextInt(100)+1 < percentChance) { e.dropItem(item); }; }
    private static ChocoboColor getNBTKEY_COLOR(@NotNull ItemStack item) {
        NbtCompound tag = item.getNbt();
        if (tag != null && tag.contains(NBTKEY_COLOR)) { return getColorFromName(tag.getString(NBTKEY_COLOR)); }
        else { return ChocoboColor.YELLOW; }
    }

    /**
     * @return True, if all armor slots have ChocoDisguiseItems equipped and are the same color, False if not
     * False if any are not ChocoDisguiseItems, or if any are not the correct slot type
     * @implNote Requires all armor slot pieces to be ChocoDisguiseItems
     */
    private static boolean armorColorMatch(@NotNull ItemStack headItemStack, ItemStack chestItemStack, ItemStack legsItemStack, ItemStack bootItemStack) {
        boolean matched = armorMatch(headItemStack, chestItemStack, legsItemStack, bootItemStack);
        if (!matched) { return false; }
        return getNBTKEY_COLOR(headItemStack).equals(getNBTKEY_COLOR(chestItemStack)) && getNBTKEY_COLOR(headItemStack).equals(getNBTKEY_COLOR(legsItemStack)) && getNBTKEY_COLOR(headItemStack).equals(getNBTKEY_COLOR(bootItemStack));
    }

    /**
     * @return True if all armor slots have ChocoDisguiseItems equipped,
     * False if any are not ChocoDisguiseItems, or if any are not the correct slot type
     * @implNote Requires all armor slot pieces to be ChocoDisguiseItems
     */
    private static boolean armorMatch(@NotNull ItemStack headItemStack, @NotNull ItemStack chestItemStack, @NotNull ItemStack legsItemStack, @NotNull ItemStack bootItemStack) {
        ChocoDisguiseItem headItem = headItemStack.getItem() instanceof ChocoDisguiseItem e ? e.getSlotType() == EquipmentSlot.HEAD ? e : null : null;
        ChocoDisguiseItem chestItem = chestItemStack.getItem() instanceof ChocoDisguiseItem e ? e.getSlotType() == EquipmentSlot.CHEST ? e : null : null;
        ChocoDisguiseItem legsItem = legsItemStack.getItem() instanceof ChocoDisguiseItem e ? e.getSlotType() == EquipmentSlot.LEGS ? e : null : null;
        ChocoDisguiseItem bootItem = bootItemStack.getItem() instanceof ChocoDisguiseItem e ? e.getSlotType() == EquipmentSlot.FEET ? e : null : null;
        return headItem != null && chestItem != null && legsItem != null && bootItem != null;
    }
}