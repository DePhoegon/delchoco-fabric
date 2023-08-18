package com.dephoegon.delchoco.aid.world.dValues;

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultFloats.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultInts.*;

public class defaultBooleans {
    public static final boolean dOverworldSpawn = true;
    public static final boolean dNetherSpawn = true;
    public static final boolean dEndSpawn = true;
    public static final boolean dCanSpawn = true;
    public static final boolean dExtraChocoboEffects = true;
    public static final boolean dExtraChocoboResourcesOnHit = true;
    public static final boolean dExtraChocoboResourcesOnKill = true;
    public static final boolean dShiftHitBypass = true;
    public static final boolean dOwnChocoboHittable = false;
    public static final boolean dTamedChocoboHittable = false;
    public static final boolean dOwnerOnlyInventoryAccess = false;
    public static void setDefaultValues(boolean isWorld) {
        if (isWorld) {
            // World Configs
            setChocoboMinPack(dCHOCOBO_PACK_MIN.getDefault());
            setChocoboMaxPack(dCHOCOBO_PACK_MAX.getDefault());
            setOverWorldSpawnWeight(dOVERWORLD_SPAWN_WEIGHT.getDefault());
            setMushroomSpawnWeight(dMUSHROOM_SPAWN_WEIGHT.getDefault());
            setNetherSpawnWeight(dNETHER_SPAWN_WEIGHT.getDefault());
            setEndSpawnWeight(dEND_SPAWN_WEIGHT.getDefault());
            setGysahlGreenPatchSize(dGYSAHL_GREEN_PATCH_SIZE.getDefault());
            setGysahlGreenSpawnChance(dGYSAHL_GREEN_SPAWN_CHANCE.getDefault());
            setCanSpawn(dCanSpawn);
            setOverworldSpawn(dOverworldSpawn);
            setNetherSpawn(dNetherSpawn);
            setEndSpawn(dEndSpawn);
        } else {
            // Chocobo Configs
            setStamina(dSTAMINA.getDefault());
            setSpeed(dSPEED.getDefault());
            setHealth(dHEALTH.getDefault());
            setArmor(dARMOR.getDefault());
            setArmorTough(dARMOR_TOUGH.getDefault());
            setAttack(dATTACK.getDefault());
            setWeaponModifier(dWEAPON_MOD.getDefault());
            setHealAmount(dHEAL_AMOUNT.getDefault());
            setEggHatchTimeTicks(dEGG_HATCH.getDefault());
            setMaxHealth(dMAX_HEALTH.getDefault());
            setMaxSpeed(dMAX_SPEED.getDefault());
            setStaminaRegen(dSTAMINA_REGEN.getDefault());
            setTame(dTAME.getDefault());
            setStaminaCost(dSTAMINA_SPRINT.getDefault());
            setStaminaGlide(dSTAMINA_GLIDE.getDefault());
            setStaminaJump(dSTAMINA_JUMP.getDefault());
            setPossLoss(dPOS_LOSS.getDefault());
            setPossGain(dPOS_GAIN.getDefault());
            setMaxStamina(dMAX_STAMINA.getDefault());
            setMaxStrength(dMAX_STRENGTH.getDefault());
            setMaxArmor(dMAX_ARMOR.getDefault());
            setMaxArmorToughness(dMAX_ARMOR_TOUGH.getDefault());
            setArmorAlpha(dARMOR_ALPHA.getDefault());
            setWeaponAlpha(dWEAPON_ALPHA.getDefault());
            setCollarAlpha(dCOLLAR_ALPHA.getDefault());
            setSaddleAlpha(dSADDLE_ALPHA.getDefault());
            setExtraChocoboEffects(dExtraChocoboEffects);
            setExtraChocoboResourcesOnHit(dExtraChocoboResourcesOnHit);
            setExtraChocoboResourcesOnKill(dExtraChocoboResourcesOnKill);
            setShiftHitBypass(dShiftHitBypass);
            setOwnChocoboHittable(dOwnChocoboHittable);
            setTamedChocoboHittable(dTamedChocoboHittable);
            setOwnerOnlyInventory(dOwnerOnlyInventoryAccess);
        }
    } // Use StaticGlobalVariables set Methods
}