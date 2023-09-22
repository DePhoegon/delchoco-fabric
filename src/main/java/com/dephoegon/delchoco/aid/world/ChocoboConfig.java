package com.dephoegon.delchoco.aid.world;

import com.dephoegon.delchoco.DelChoco;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;

import static com.dephoegon.delchoco.aid.world.dValues.defaultBooleans.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultFloats.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultInts.*;

@Config(id = DelChoco.DELCHOCO_ID+"-chocobo_config")
public class ChocoboConfig {
    @Configurable
    @Configurable.Comment("The Amount of Stamina a Chocobo has by default. min 5, max 60, default 20")
    @Configurable.Range(min = 5, max = 60)
    public int chocoboStamina = dSTAMINA.getDefault();

    @Configurable
    @Configurable.Comment("The Amount of Speed a Chocobo has by default. min 10, max 80, default 20")
    @Configurable.Range(min = 10, max = 80)
    public int chocoboSpeed = dSPEED.getDefault();

    @Configurable
    @Configurable.Comment("The Amount of Health a Chocobo has by default. min 6, max 1000, default 20")
    @Configurable.Range(min = 6, max = 1000)
    public int chocoboHealth = dHEALTH.getDefault();

    @Configurable
    @Configurable.Comment("The Amount of Armor a Chocobo has by default. min 0, max 20, default 4")
    @Configurable.Range(min = 0, max = 20)
    public int chocoboArmor = dARMOR.getDefault();

    @Configurable
    @Configurable.Comment("The Amount of Toughness a Chocobo has by default. min 0, max 10, default 1")
    @Configurable.Range(min = 0, max = 10)
    public int chocoboArmorToughness = dARMOR_TOUGH.getDefault();

    @Configurable
    @Configurable.Comment("The Amount of Attack Damage a Chocobo has by default. min 1, max 10, default 2")
    @Configurable.Range(min = 1, max = 10)
    public int chocoboAttackDamage = dATTACK.getDefault();

    @Configurable
    @Configurable.Comment("The Amount of Attack Speed as a Weapon Modifier a Chocobo has by default. min 1, max 3, default 1")
    @Configurable.Range(min = 1, max = 3)
    public int chocoboWeaponMod = dWEAPON_MOD.getDefault();

    @Configurable
    @Configurable.Comment("The Amount of Health a Chocobo Heals when fed a Gysahl Green. min 2, max 10, default 5")
    @Configurable.Range(min = 2, max = 10)
    public int chocoboHealAmount = dHEAL_AMOUNT.getDefault();

    @Configurable
    @Configurable.Comment("The Maximum Health a Chocobo can have. min 25, max 3000, default 60")
    @Configurable.Range(min = 25, max = 3000)
    public int chocoboMaxHealth = dMAX_HEALTH.getDefault();

    @Configurable
    @Configurable.Comment("The Maximum Speed a Chocobo can have. min 30, max 160, default 40")
    @Configurable.Range(min = 30, max = 160)
    public int chocoboMaxSpeed = dMAX_SPEED.getDefault();

    @Configurable
    @Configurable.Comment("The Amount of Stamina a Chocobo Regenerates per Tick. min 0.01, max 1, default 0.025 - Double")
    @Configurable.DecimalRange(min = 0.01, max = 1)
    public double chocoboStaminaRegen = dSTAMINA_REGEN.getDefault();

    @Configurable
    @Configurable.Comment("The Chance of Time per attempt to Tame a Chocobo. min 0.05, max 1, default 0.15 - Double")
    @Configurable.DecimalRange(min = 0.05, max = 1)
    public double chocoboTameChance = dTAME.getDefault();

    @Configurable
    @Configurable.Comment("The Amount of Stamina a Chocobo uses per Tick while Sprinting. min 0, max 1, default 0.06 - Double")
    @Configurable.DecimalRange(min = 0, max = 1)
    public double chocoboStaminaCostSprint = dSTAMINA_SPRINT.getDefault();

    @Configurable
    @Configurable.Comment("The Amount of Stamina a Chocobo uses per Tick while Gliding. min 0, max 1, default 0.005 - Double")
    @Configurable.DecimalRange(min = 0, max = 1)
    public double chocoboStaminaCostGlide = dSTAMINA_GLIDE.getDefault();

    @Configurable
    @Configurable.Comment("The Amount of Stamina a Chocobo uses per Jump. min 0, max 1, default 0 - Double")
    @Configurable.DecimalRange(min = 0, max = 1)
    public double chocoboStaminaCostJump = dSTAMINA_JUMP.getDefault();

    @Configurable
    @Configurable.Comment("The Time in Ticks, a Aqua Berry will give a chocobo water breathing, or a aero Shroom will keep WaterBreathing Chocobo Afloat. min 300, max 12000, default 1500")
    @Configurable.Range(min = 300, max = 12000)
    public int chocoboFruitTimer = dGIFTED_WATER_BREATHER_STATUS.getDefault();

    @Configurable
    @Configurable.Comment("The Time in Ticks, the cooldown time a chocobo has between eating non-Gysahl Green. min 40, max 600, default 60")
    @Configurable.Range(min = 40, max = 600)
    public int chocoboFruitCoolDown = dFRUIT_COOL_DOWN.getDefault();

    @Configurable
    @Configurable.Comment("The possible amount of percent Stats passed on the the chicbo gains in the breeding process. min 0, max 1, default 0.1 - Double")
    @Configurable.DecimalRange(min = 0, max = 1)
    public double chocoboPossibleGain = dPOS_GAIN.getDefault();

    @Configurable
    @Configurable.Comment("The possible amount of percent Stats passed on the the chicbo loses in the breeding process. min 0, max 1, default 1 - Double")
    @Configurable.DecimalRange(min = 0, max = 1)
    public double chocoboPossibleLoss = dPOS_LOSS.getDefault();

    @Configurable
    @Configurable.Comment("The possible amount of percent Health passed on the the chicbo gains in the breeding process. min 0, max 1, default 0.1 - Double")
    @Configurable.DecimalRange(min = 0, max = 1)
    public double chocoboPossibleGainHealth = dPOS_GAIN.getDefault();

    @Configurable
    @Configurable.Comment("The possible amount of percent Health passed on the the chicbo loses in the breeding process. min 0, max 1, default 1 - Double")
    @Configurable.DecimalRange(min = 0, max = 1)
    public double chocoboPossibleLossHealth = dPOS_LOSS.getDefault();

    @Configurable
    @Configurable.Comment("The possible amount of percent Speed passed on the the chicbo gains in the breeding process. min 0, max 1, default 0.1 - Double")
    @Configurable.DecimalRange(min = 0, max = 1)
    public double chocoboPossibleGainSpeed = dPOS_GAIN.getDefault();

    @Configurable
    @Configurable.Comment("The possible amount of percent Speed passed on the the chicbo loses in the breeding process. min 0, max 1, default 1 - Double")
    @Configurable.DecimalRange(min = 0, max = 1)
    public double chocoboPossibleLossSpeed = dPOS_LOSS.getDefault();

    @Configurable
    @Configurable.Comment("The possible amount of percent Stamina passed on the the chicbo gains in the breeding process. min 0, max 1, default 0.1 - Double")
    @Configurable.DecimalRange(min = 0, max = 1)
    public double chocoboPossibleGainStamina = dPOS_GAIN.getDefault();

    @Configurable
    @Configurable.Comment("The possible amount of percent Stamina passed on the the chicbo loses in the breeding process. min 0, max 1, default 1 - Double")
    @Configurable.DecimalRange(min = 0, max = 1)
    public double chocoboPossibleLossStamina = dPOS_LOSS.getDefault();

    @Configurable
    @Configurable.Comment("The Maximum Stamina a Chocobo can have. min 10, max 1024, default 150")
    @Configurable.Range(min = 10, max = 1024)
    public double chocoboMaxStamina = dMAX_STAMINA.getDefault();

    @Configurable
    @Configurable.Comment("The Maximum Strength a Chocobo can have. min 8, max 100, default 60")
    @Configurable.Range(min = 8, max = 100)
    public double chocoboMaxStrength = dMAX_STRENGTH.getDefault();

    @Configurable
    @Configurable.Comment("The Maximum Armor a Chocobo can have. min 20, max 500, default 200")
    @Configurable.Range(min = 20, max = 500)
    public double chocoboMaxArmor = dMAX_ARMOR.getDefault();

    @Configurable
    @Configurable.Comment("The Maximum Armor Toughness a Chocobo can have. min 8, max 100, default 20")
    @Configurable.Range(min = 8, max = 100)
    public double chocoboMaxArmorToughness = dMAX_ARMOR_TOUGH.getDefault();

    @Configurable
    @Configurable.Comment("The Alpha Value of the Chocobo Armor. min 0, max 0.75, default 0.1 - Float")
    @Configurable.DecimalRange(min = 0, max = 0.75)
    public float chocoboArmorAlpha = dARMOR_ALPHA.getDefault();

    @Configurable
    @Configurable.Comment("The Alpha Value of the Chocobo Weapon. min 0, max 0.75, default 0.1 - Float")
    @Configurable.DecimalRange(min = 0, max = 0.75)
    public float chocoboWeaponAlpha = dWEAPON_ALPHA.getDefault();

    @Configurable
    @Configurable.Comment("The Alpha Value of the Chocobo Collar. min 0, max 1, default 0.2 - Float")
    @Configurable.DecimalRange(min = 0, max = 1)
    public float chocoboCollarAlpha = dCOLLAR_ALPHA.getDefault();

    @Configurable
    @Configurable.Comment("The Alpha Value of the Chocobo Saddle. min 0, max 1, default 0.1 - Float")
    @Configurable.DecimalRange(min = 0, max = 1)
    public float chocoboSaddleAlpha = dSADDLE_ALPHA.getDefault();

    @Configurable
    @Configurable.Comment("Are there Extra Combat/Side Effects for the Chocobos and ChocoDisguise Armor")
    public boolean extraChocoboEffects = dExtraChocoboEffects;

    @Configurable
    @Configurable.Comment("Are their Resources for Chocobos for combat on hit")
    public boolean extraChocoboResourcesOnHit = dExtraChocoboResourcesOnHit;

    @Configurable
    @Configurable.Comment("Are their Resources for Chocobos for combat on kill")
    public boolean extraChocoboResourcesOnKill = dExtraChocoboResourcesOnKill;

    @Configurable
    @Configurable.Comment("Allow Shift to bypass hit protection on chocobos")
    public boolean shiftHitBypass = dShiftHitBypass;

    @Configurable
    @Configurable.Comment("Allows Owner to hit their Chocobos")
    public boolean ownChocoboHittable = dOwnChocoboHittable;

    @Configurable
    @Configurable.Comment("Allows Tamed Chocobos to be hit by a player")
    public boolean tamedChocoboHittable = dTamedChocoboHittable;

    @Configurable
    @Configurable.Comment("Allows Tamed Chocobos to have their inventory accessed by only the Owner")
    public boolean ownerInventoryAccess = dOwnerOnlyInventoryAccess;
}