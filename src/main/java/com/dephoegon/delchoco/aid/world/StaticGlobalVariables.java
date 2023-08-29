package com.dephoegon.delchoco.aid.world;

import com.dephoegon.delchoco.aid.world.dValues.defaultDoubles;
import com.dephoegon.delchoco.aid.world.dValues.defaultFloats;
import com.dephoegon.delchoco.aid.world.dValues.defaultInts;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.aid.world.dValues.defaultBooleans.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultFloats.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultInts.*;

public class StaticGlobalVariables {
    private static Integer Stamina;
    public static final String Stamina_name = "default_stamina";
    private static Integer Speed;
    public static final String Speed_name = "default_speed";
    private static Integer Health;
    public static final String Health_name = "default_health";
    private static Integer Armor;
    public static final String Armor_name = "default_armor";
    private static Integer ArmorTough;
    public static final String ArmorTough_name = "default_armor_toughness";
    private static Integer Attack;
    public static final String Attack_name = "default_attack_damage";
    private static Integer WeaponModifier;
    public static final String WeaponModifier_name = "chocobo_weapon_modifier";
    private static Integer HealAmount;
    public static final String HealAmount_name = "chocobo_heal_amount";
    private static Double StaminaRegen;
    public static final String StaminaRegen_name = "chocobo_samina_regen";
    private static Double Tame;
    public static final String Tame_name = "chocobo_tame_chance";
    private static Double StaminaCost;
    public static final String StaminaCost_name = "chocobo_sprint_stamina_cost";
    private static Double StaminaGlide;
    public static final String StaminaGlide_name = "chocobo_glide_stamina_cost";
    private static Double StaminaJump;
    public static final String StaminaJump_name = "chocobo_jump_stamina_cost";
    private static Boolean CanSpawn;
    public static final String CanSpawn_name = "natural_spawning";
    private static Boolean ExtraChocoboEffects;
    public static final String ExtraChocoboEffects_name = "chocobo_effects";
    private static Boolean ExtraChocoboResourcesOnHit;
    public static final String ExtraChocoboResourcesOnHit_name = "chocobo_on_hit_resources";
    private static Boolean ExtraChocoboResourcesOnKill;
    public static final String ExtraChocoboResourcesOnKill_name = "chocobo_on_kill_resource";
    private static Boolean ShiftHitBypass;
    public static final String ShiftHitBypass_name = "chocobo_shift_to_hit_bypass";
    private static Boolean OwnChocoboHittable;
    public static final String OwnChocoboHittable_name = "chocobo_allow_own_hit";
    private static Boolean TamedChocoboHittable;
    public static final String TamedChocoboHittable_name = "chocobo_allow_tamed_hit";
    private static Integer EggHatchTimeTicks;
    public static final String EggHatchTimeTicks_name = "chocobo_egg_hatch_time_ticks";
    private static Double PossLoss;
    public static final String PossLoss_name = "chocobo_potential_stat_loss_breeding";
    private static Double PossGain;
    public static final String PossGain_name = "chocobo_potential_stat_gain_breeding";
    private static Double PossLossHealth;
    public static final String PossLossHealth_name = "chocobo_potential_health_loss_breeding";
    private static Double PossGainHealth;
    public static final String PossGainHealth_name = "chocobo_potential_health_gain_breeding";
    private static Double PossLossSpeed;
    public static final String PossLossSpeed_name = "chocobo_potential_speed_loss_breeding";
    private static Double PossGainSpeed;
    public static final String PossGainSpeed_name = "chocobo_potential_speed_gain_breeding";
    private static Double PossLossStamina;
    public static final String PossLossStamina_name = "chocobo_potential_stamina_loss_breeding";
    private static Double PossGainStamina;
    public static final String PossGainStamina_name = "chocobo_potential_stamina_gain_breeding";
    private static Integer MaxHealth;
    public static final String MaxHealth_name = "chocobo_max_health_limit";
    private static Integer MaxSpeed;
    public static final String MaxSpeed_name = "chocobo_max_speed_limit";
    private static Double MaxStamina;
    public static final String MaxStamina_name = "chocobo_max_stamina_limit";
    private static Double MaxStrength;
    public static final String MaxStrength_name = "chocobo_max_attack_damage_limit";
    private static Double MaxArmor;
    public static final String MaxArmor_name = "chocobo_max_armor_limit";
    private static Double MaxArmorToughness;
    public static final String MaxArmorToughness_name = "chocobo_max_armor_toughness_limit";
    private static Float ArmorAlpha;
    public static final String ArmorAlpha_name = "chocobo_layer_armor_alpha";
    private static Float WeaponAlpha;
    public static final String WeaponAlpha_name = "chocobo_layer_weapon_alpha";
    private static Float CollarAlpha;
    public static final String CollarAlpha_name = "chocobo_layer_collar_alpha";
    private static Float SaddleAlpha;
    public static final String SaddleAlpha_name = "chocobo_layer_saddle_alpha";
    private static Integer ChocoboMinPack;
    public static final String ChocoboMinPack_name = "chocobo_pack_min_size";
    private static Integer ChocoboMaxPack;
    public static final String ChocoboMaxPack_name = "chocobo_pack_max_size";
    private static Integer OverWorldSpawnWeight;
    public static final String OverWorldSpawnWeight_name = "chocobo_spawn_weight_overworld";
    private static Integer MushroomSpawnWeight;
    public static final String MushroomSpawnWeight_name = "chocobo_spawn_weight_mushroom_island";
    private static Integer NetherSpawnWeight;
    public static final String NetherSpawnWeight_name = "chocobo_spawn_weight_nether";
    private static Integer EndSpawnWeight;
    public static final String EndSpawnWeight_name = "chocobo_spawn_weight_end";
    private static Double GysahlGreenSpawnChance;
    public static final String GysahlGreenSpawnChance_name = "gysahl_green_spawn_chance";
    private static Integer GysahlGreenPatchSize;
    public static final String GysahlGreenPatchSize_name = "gysahl_green_spawn_patch_size";
    private static Boolean OverworldSpawn;
    public static final String OverworldSpawn_name = "chocobo_spawn_enable_overworld";
    private static Boolean NetherSpawn;
    public static final String NetherSpawn_name = "chocobo_spawn_enable_nether";
    private static Boolean EndSpawn;
    public static final String  EndSpawn_name = "chocobo_spawn_enable_nether";
    private static Boolean OwnerOnlyInventory;
    public static final String OwnerOnlyInventory_name = "chocobo_inventory_owner_only";
    public StaticGlobalVariables() { }
    public static Integer getStamina() { return Stamina == null ? dSTAMINA.getDefault() : Stamina; }
    public static void setStamina(int stamina) { StaticGlobalVariables.Stamina = stamina; }
    public static Integer getSpeed() { return Speed == null ? dSPEED.getDefault() : Speed; }
    public static void setSpeed(int stamina) { StaticGlobalVariables.Speed = stamina; }
    public static Integer getHealth() { return Health == null ? dHEALTH.getDefault() : Health; }
    public static void setHealth(int health) { StaticGlobalVariables.Health = health; }
    public static Integer getArmor() { return Armor == null ? dARMOR.getDefault() : Armor; }
    public static void setArmor(int armor) { StaticGlobalVariables.Armor = armor; }
    public static Integer getArmorTough() { return ArmorTough == null ? dARMOR_TOUGH.getDefault() : ArmorTough; }
    public static void setArmorTough(int armorTough) { StaticGlobalVariables.ArmorTough = armorTough; }
    public static Integer getAttack() { return Attack == null ? dATTACK.getDefault() : Attack; }
    public static void setAttack(int attack) { StaticGlobalVariables.Attack = attack; }
    public static Integer getWeaponModifier() { return WeaponModifier == null ? dWEAPON_MOD.getDefault() : WeaponModifier; }
    public static void setWeaponModifier(int weaponModifier) { StaticGlobalVariables.WeaponModifier = weaponModifier; }
    public static Integer getHealAmount() { return HealAmount == null ? dHEAL_AMOUNT.getDefault() : HealAmount; }
    public static void setHealAmount(int healAmount) { StaticGlobalVariables.HealAmount = healAmount; }
    public static Integer getEggHatchTimeTicks() { return EggHatchTimeTicks == null ? dEGG_HATCH.getDefault() : EggHatchTimeTicks; }
    public static void setEggHatchTimeTicks(int eggHatchTimeTicks) { StaticGlobalVariables.EggHatchTimeTicks = eggHatchTimeTicks; }
    public static Integer getMaxHealth() { return MaxHealth == null ? dMAX_HEALTH.getDefault() : MaxHealth; }
    public static void setMaxHealth(int maxHealth) { StaticGlobalVariables.MaxHealth = maxHealth; }
    public static Integer getMaxSpeed() { return MaxSpeed == null ? dMAX_SPEED.getDefault() : MaxSpeed; }
    public static void setMaxSpeed(int maxSpeed) { StaticGlobalVariables.MaxSpeed = maxSpeed; }
    public static Integer getChocoboMinPack() { return ChocoboMinPack == null ? dCHOCOBO_PACK_MIN.getDefault() : ChocoboMinPack; }
    public static void setChocoboMinPack(int chocoboMinPack) { StaticGlobalVariables.ChocoboMinPack = chocoboMinPack; }
    public static Integer getChocoboMaxPack() { return ChocoboMaxPack == null ? dCHOCOBO_PACK_MAX.getDefault() : ChocoboMaxPack; }
    public static void setChocoboMaxPack(int chocoboMaxPack) { StaticGlobalVariables.ChocoboMaxPack = chocoboMaxPack; }
    public static Integer getOverWorldSpawnWeight() { return OverWorldSpawnWeight == null ? dOVERWORLD_SPAWN_WEIGHT.getDefault() : OverWorldSpawnWeight; }
    public static void setOverWorldSpawnWeight(int overWorldSpawnWeight) { StaticGlobalVariables.OverWorldSpawnWeight = overWorldSpawnWeight; }
    public static Integer getMushroomSpawnWeight() { return MushroomSpawnWeight == null ? dMUSHROOM_SPAWN_WEIGHT.getDefault() : MushroomSpawnWeight; }
    public static void setMushroomSpawnWeight(int mushroomSpawnWeight) { StaticGlobalVariables.MushroomSpawnWeight = mushroomSpawnWeight; }
    public static Integer getNetherSpawnWeight() { return NetherSpawnWeight == null ? dNETHER_SPAWN_WEIGHT.getDefault() : NetherSpawnWeight; }
    public static void setNetherSpawnWeight(int netherSpawnWeight) { StaticGlobalVariables.NetherSpawnWeight = netherSpawnWeight; }
    public static Integer getEndSpawnWeight() { return EndSpawnWeight == null ? dEND_SPAWN_WEIGHT.getDefault() : EndSpawnWeight; }
    public static void setEndSpawnWeight(int endSpawnWeight) { StaticGlobalVariables.EndSpawnWeight = endSpawnWeight; }
    public static Integer getGysahlGreenPatchSize() { return GysahlGreenPatchSize == null ? dGYSAHL_GREEN_PATCH_SIZE.getDefault() : GysahlGreenPatchSize; }
    public static void setGysahlGreenPatchSize(int gysahlGreenPatchSize) { StaticGlobalVariables.GysahlGreenPatchSize = gysahlGreenPatchSize; }
    public static Double getGysahlGreenSpawnChance() { return GysahlGreenSpawnChance == null ? dGYSAHL_GREEN_SPAWN_CHANCE.getDefault() : GysahlGreenSpawnChance; }
    public static void setGysahlGreenSpawnChance(double gysahlGreenSpawnChance) { StaticGlobalVariables.GysahlGreenSpawnChance = gysahlGreenSpawnChance; }
    public static Double getStaminaRegen() { return StaminaRegen == null ? dSTAMINA_REGEN.getDefault() : StaminaRegen; }
    public static void setStaminaRegen(double staminaRegen) { StaticGlobalVariables.StaminaRegen = staminaRegen; }
    public static Double getTame() { return Tame == null ? dTAME.getDefault() : Tame; }
    public static void setTame(double tame) { StaticGlobalVariables.Tame = tame; }
    public static Double getStaminaCost() { return StaminaCost == null ? dSTAMINA_SPRINT.getDefault() : StaminaCost; }
    public static void setStaminaCost(double staminaCost) { StaticGlobalVariables.StaminaCost = staminaCost; }
    public static Double getStaminaGlide() { return StaminaGlide == null ? dSTAMINA_GLIDE.getDefault() : StaminaGlide; }
    public static void setStaminaGlide(double staminaGlide) { StaticGlobalVariables.StaminaGlide = staminaGlide; }
    public static Double getStaminaJump() { return StaminaJump == null ? dSTAMINA_JUMP.getDefault() : StaminaJump; }
    public static void setStaminaJump(double staminaJump) { StaticGlobalVariables.StaminaJump = staminaJump; }
    public static Double getPossLoss() { return PossLoss == null ? dPOS_LOSS.getDefault() : PossLoss; }
    public static void setPossLoss(double possLoss) { StaticGlobalVariables.PossLoss = possLoss; }
    public static Double getPossGain() { return PossGain == null ? dPOS_GAIN.getDefault() : PossGain; }
    public static void setPossGain(double possGain) { StaticGlobalVariables.PossGain = possGain; }
    public static Double getPossLossHealth() { return PossLossHealth == null ? dPOS_LOSS.getDefault() : PossLossHealth; }
    public static void setPossLossHealth(double possLossHealth) { StaticGlobalVariables.PossLossHealth = possLossHealth; }
    public static Double getPossGainHealth() { return PossGainHealth == null ? dPOS_GAIN.getDefault() : PossGainHealth; }
    public static void setPossGainHealth(double possGainHealth) { StaticGlobalVariables.PossGainHealth = possGainHealth; }
    public static Double getPossLossSpeed() { return PossLossSpeed == null ? dPOS_LOSS.getDefault() : PossLossSpeed; }
    public static void setPossLossSpeed(double possLossSpeed) { StaticGlobalVariables.PossLossSpeed = possLossSpeed; }
    public static Double getPossGainSpeed() { return PossGainSpeed == null ? dPOS_GAIN.getDefault() : PossGainSpeed; }
    public static void setPossGainSpeed(double possGainSpeed) { StaticGlobalVariables.PossGainSpeed = possGainSpeed; }
    public static Double getPossLossStamina() { return PossLossStamina == null ? dPOS_LOSS.getDefault() : PossLossStamina; }
    public static void setPossLossStamina(double possLossStamina) { StaticGlobalVariables.PossLossStamina = possLossStamina; }
    public static Double getPossGainStamina() { return PossGainStamina == null ? dPOS_GAIN.getDefault() : PossGainStamina; }
    public static void setPossGainStamina(double possGainStamina) { StaticGlobalVariables.PossGainStamina = possGainStamina; }
    public static Double getMaxStamina() { return MaxStamina == null ? dMAX_STAMINA.getDefault() : MaxStamina; }
    public static void setMaxStamina(double maxStamina) { StaticGlobalVariables.MaxStamina = maxStamina; }
    public static Double getMaxStrength() { return MaxStrength == null ? dMAX_STRENGTH.getDefault() : MaxStrength; }
    public static void setMaxStrength(double maxStrength) { StaticGlobalVariables.MaxStrength = maxStrength; }
    public static Double getMaxArmor() { return MaxArmor == null ? dMAX_ARMOR.getDefault() : MaxArmor; }
    public static void setMaxArmor(double maxArmor) { StaticGlobalVariables.MaxArmor = maxArmor; }
    public static Double getMaxArmorToughness() { return MaxArmorToughness == null ? dMAX_ARMOR_TOUGH.getDefault() : MaxArmorToughness; }
    public static void setMaxArmorToughness(double maxArmorToughness) { StaticGlobalVariables.MaxArmorToughness = maxArmorToughness; }
    public static Float getArmorAlpha() { return ArmorAlpha == null ? dARMOR_ALPHA.getDefault() : ArmorAlpha; }
    public static void setArmorAlpha(float armorAlpha) { StaticGlobalVariables.ArmorAlpha = armorAlpha; }
    public static Float getWeaponAlpha() { return WeaponAlpha == null ? dWEAPON_ALPHA.getDefault() : WeaponAlpha; }
    public static void setWeaponAlpha(float weaponAlpha) { StaticGlobalVariables.WeaponAlpha = weaponAlpha; }
    public static Float getCollarAlpha() { return CollarAlpha == null ? dCOLLAR_ALPHA.getDefault() : CollarAlpha; }
    public static void setCollarAlpha(float collarAlpha) { StaticGlobalVariables.CollarAlpha = collarAlpha; }
    public static Float getSaddleAlpha() { return SaddleAlpha == null ? dSADDLE_ALPHA.getDefault() : SaddleAlpha; }
    public static void setSaddleAlpha(float saddleAlpha) { StaticGlobalVariables.SaddleAlpha = saddleAlpha; }
    public static Boolean getCanSpawn() { return CanSpawn == null ? dCanSpawn : CanSpawn; }
    public static void setCanSpawn(boolean canSpawn) { StaticGlobalVariables.CanSpawn = canSpawn; }
    public static Boolean getExtraChocoboEffects() { return ExtraChocoboEffects == null ? dExtraChocoboEffects : ExtraChocoboEffects; }
    public static void setExtraChocoboEffects(boolean extraChocoboEffects) { StaticGlobalVariables.ExtraChocoboEffects = extraChocoboEffects; }
    public static Boolean getExtraChocoboResourcesOnHit() { return ExtraChocoboResourcesOnHit == null ? dExtraChocoboResourcesOnHit : ExtraChocoboResourcesOnHit; }
    public static void setExtraChocoboResourcesOnHit(boolean extraChocoboResourcesOnHit) { StaticGlobalVariables.ExtraChocoboResourcesOnHit = extraChocoboResourcesOnHit; }
    public static Boolean getExtraChocoboResourcesOnKill() { return ExtraChocoboResourcesOnKill == null ? dExtraChocoboResourcesOnKill : ExtraChocoboResourcesOnKill; }
    public static void setExtraChocoboResourcesOnKill(boolean extraChocoboResourcesOnKill) { StaticGlobalVariables.ExtraChocoboResourcesOnKill = extraChocoboResourcesOnKill; }
    public static Boolean getShiftHitBypass() { return ShiftHitBypass == null ? dShiftHitBypass : ShiftHitBypass; }
    public static void setShiftHitBypass(boolean shiftHitBypass) { StaticGlobalVariables.ShiftHitBypass = shiftHitBypass; }
    public static Boolean getOwnChocoboHittable() { return OwnChocoboHittable == null ? dOwnChocoboHittable : OwnChocoboHittable; }
    public static void setOwnChocoboHittable(boolean ownChocoboHittable) { StaticGlobalVariables.OwnChocoboHittable = ownChocoboHittable; }
    public static Boolean getTamedChocoboHittable() { return TamedChocoboHittable == null ? dTamedChocoboHittable :  TamedChocoboHittable; }
    public static void setTamedChocoboHittable(boolean tamedChocoboHittable) { StaticGlobalVariables.TamedChocoboHittable = tamedChocoboHittable; }
    public static Boolean getOverworldSpawn() { return OverworldSpawn == null ? dOverworldSpawn : OverworldSpawn; }
    public static void setOverworldSpawn(boolean overworldSpawn) { StaticGlobalVariables.OverworldSpawn = overworldSpawn; }
    public static Boolean getNetherSpawn() { return NetherSpawn == null ? dNetherSpawn : NetherSpawn; }
    public static void setNetherSpawn(boolean netherSpawn) { StaticGlobalVariables.NetherSpawn = netherSpawn; }
    public static Boolean getEndSpawn() { return EndSpawn == null ? dEndSpawn : EndSpawn; }
    public static void setEndSpawn(boolean endSpawn) { StaticGlobalVariables.EndSpawn = endSpawn; }
    public static Boolean getOwnerOnlyInventory() { return OwnerOnlyInventory == null ? dOwnerOnlyInventoryAccess : OwnerOnlyInventory; }
    public static void setOwnerOnlyInventory(boolean ownerOnlyInventory) { StaticGlobalVariables.OwnerOnlyInventory = ownerOnlyInventory; }

    public static int ChocoConfigGet(Integer value, int defaultValue) { return value != null ? value : defaultValue; }
    public static double ChocoConfigGet(Double value, double defaultValue) { return value != null ? value : defaultValue; }
    public static boolean ChocoConfigGet(Boolean value, boolean defaultValue) { return value != null ? value : defaultValue; }
    public static float ChocoConfigGet(Float value, float defaultValue) { return value != null ? value : defaultValue; }
    public static float FloatChocoConfigGet(Integer value, int defaultValue) { return value != null ? value : defaultValue; }
    public static float FloatChocoConfigGet(Double value, double defaultValue) { return (float) (value != null ? value : defaultValue); }
    public static int getValueInBounds(@NotNull defaultInts Holder, Integer inValue) {
        int value = ChocoConfigGet(inValue, Holder.getDefault());
        return  Math.max(Holder.getMin(), Math.min(Holder.getMax(), value));
    }
    public static double getValueInBounds(@NotNull defaultDoubles Holder, Double inValue) {
        double value = ChocoConfigGet(inValue, Holder.getDefault());
        return  Math.max(Holder.getMin(), Math.min(Holder.getMax(), value));
    }
    public static float getValueInBounds(@NotNull defaultFloats Holder, Float inValue) {
        float value = ChocoConfigGet(inValue, Holder.getDefault());
        return  Math.max(Holder.getMin(), Math.min(Holder.getMax(), value));
    }
}