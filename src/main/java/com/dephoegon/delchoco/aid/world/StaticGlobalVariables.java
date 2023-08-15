package com.dephoegon.delchoco.aid.world;

public class StaticGlobalVariables {
    public static Integer Stamina;
    public static final String Stamina_name = "default_stamina";
    public static Integer Speed;
    public static final String Speed_name = "default_speed";
    public static Integer Health;
    public static final String Health_name = "default_health";
    public static Integer Armor;
    public static final String Armor_name = "default_armor";
    public static Integer ArmorTough;
    public static final String ArmorTough_name = "default_armor_toughness";
    public static Integer Attack;
    public static final String Attack_name = "default_attack_damage";
    public static Integer WeaponModifier;
    public static final String WeaponModifier_name = "chocobo_weapon_modifier";
    public static Integer HealAmount;
    public static final String HealAmount_name = "chocobo_heal_amount";
    public static Double StaminaRegen;
    public static final String StaminaRegen_name = "chocobo_samina_regen";
    public static Double Tame;
    public static final String Tame_name = "chocobo_tame_chance";
    public static Double StaminaCost;
    public static final String StaminaCost_name = "chocobo_sprint_stamina_cost";
    public static Double StaminaGlide;
    public static final String StaminaGlide_name = "chocobo_glide_stamina_cost";
    public static Double StaminaJump;
    public static final String StaminaJump_name = "chocobo_jump_stamina_cost";
    public static Boolean CanSpawn;
    public static final String CanSpawn_name = "natural_spawning";
    public static Boolean ExtraChocoboEffects;
    public static final String ExtraChocoboEffects_name = "chocobo_effects";
    public static Boolean ExtraChocoboResourcesOnHit;
    public static final String ExtraChocoboResourcesOnHit_name = "chocobo_on_hit_resources";
    public static Boolean ExtraChocoboResourcesOnKill;
    public static final String ExtraChocoboResourcesOnKill_name = "chocobo_on_kill_resource";
    public static Boolean ShiftHitBypass;
    public static final String ShiftHitBypass_name = "chocobo_shift_to_hit_bypass";
    public static Boolean OwnChocoboHittable;
    public static final String OwnChocoboHittable_name = "chocobo_allow_own_hit";
    public static Boolean TamedChocoboHittable;
    public static final String TamedChocoboHittable_name = "chocobo_allow_tamed_hit";
    public static Integer EggHatchTimeTicks;
    public static final String EggHatchTimeTicks_name = "chocobo_egg_hatch_time_ticks";
    public static Double PossLoss;
    public static final String PossLoss_name = "chocobo_potential_stat_loss_breeding";
    public static Double PossGain;
    public static final String PossGain_name = "chocobo_potential_stat_gain_breeding";
    public static Integer MaxHealth;
    public static final String MaxHealth_name = "chocobo_max_health_limit";
    public static Integer MaxSpeed;
    public static final String MaxSpeed_name = "chocobo_max_speed_limit";
    public static Double MaxStamina;
    public static final String MaxStamina_name = "chocobo_max_stamina_limit";
    public static Double MaxStrength;
    public static final String MaxStrength_name = "chocobo_max_attack_damage_limit";
    public static Double MaxArmor;
    public static final String MaxArmor_name = "chocobo_max_armor_limit";
    public static Double MaxArmorToughness;
    public static final String MaxArmorToughness_name = "chocobo_max_armor_toughness_limit";
    public static Double ArmorAlpha;
    public static final String ArmorAlpha_name = "chocobo_layer_armor_alpha";
    public static Double WeaponAlpha;
    public static final String WeaponAlpha_name = "chocobo_layer_weapon_alpha";
    public static Double CollarAlpha;
    public static final String CollarAlpha_name = "chocobo_layer_collar_alpha";
    public static Double SaddleAlpha;
    public static final String SaddleAlpha_name = "chocobo_layer_saddle_alpha";
    public static Integer ChocoboMinPack;
    public static final String ChocoboMinPack_name = "chocobo_pack_min_size";
    public static Integer ChocoboMaxPack;
    public static final String ChocoboMaxPack_name = "chocobo_pack_max_size";
    public static Integer OverWorldSpawnWeight;
    public static final String OverWorldSpawnWeight_name = "chocobo_spawn_weight_overworld";
    public static Integer MushroomSpawnWeight;
    public static final String MushroomSpawnWeight_name = "chocobo_spawn_weight_mushroom_island";
    public static Integer NetherSpawnWeight;
    public static final String NetherSpawnWeight_name = "chocobo_spawn_weight_nether";
    public static Integer EndSpawnWeight;
    public static final String EndSpawnWeight_name = "chocobo_spawn_weight_end";
    public static Double GysahlGreenSpawnChance;
    public static final String GysahlGreenSpawnChance_name = "gysahl_green_spawn_chance";
    public static Integer GysahlGreenPatchSize;
    public static final String GysahlGreenPatchSize_name = "gysahl_green_spawn_patch_size";
    public static Boolean OverworldSpawn;
    public static final String OverworldSpawn_name = "chocobo_spawn_enable_overworld";
    public static Boolean NetherSpawn;
    public static final String NetherSpawn_name = "chocobo_spawn_enable_nether";
    public static Boolean EndSpawn;
    public static final String  EndSpawn_name = "chocobo_spawn_enable_nether";
    public StaticGlobalVariables() { }
    public static Integer getStamina() { return Stamina; }
    public static void setStamina(int stamina) { StaticGlobalVariables.Stamina = stamina; }
    public static Integer getSpeed() { return Speed; }
    public static void setSpeed(int stamina) { StaticGlobalVariables.Speed = stamina; }
    public static Integer getHealth() { return Health; }
    public static void setHealth(int health) { StaticGlobalVariables.Health = health; }
    public static Integer getArmor() { return Armor; }
    public static void setArmor(int armor) { StaticGlobalVariables.Armor = armor; }
    public static Integer getArmorTough() { return ArmorTough; }
    public static void setArmorTough(int armorTough) { StaticGlobalVariables.ArmorTough = armorTough; }
    public static Integer getAttack() { return Attack; }
    public static void setAttack(int attack) { StaticGlobalVariables.Attack = attack; }
    public static Integer getWeaponModifier() { return WeaponModifier; }
    public static void setWeaponModifier(int weaponModifier) { StaticGlobalVariables.WeaponModifier = weaponModifier; }
    public static Integer getHealAmount() { return HealAmount; }
    public static void setHealAmount(int healAmount) { StaticGlobalVariables.HealAmount = healAmount; }
    public static Integer getEggHatchTimeTicks() { return EggHatchTimeTicks; }
    public static void setEggHatchTimeTicks(int eggHatchTimeTicks) { StaticGlobalVariables.EggHatchTimeTicks = eggHatchTimeTicks; }
    public static Integer getMaxHealth() { return MaxHealth; }
    public static void setMaxHealth(int maxHealth) { StaticGlobalVariables.MaxHealth = maxHealth; }
    public static Integer getMaxSpeed() { return MaxSpeed; }
    public static void setMaxSpeed(int maxSpeed) { StaticGlobalVariables.MaxSpeed = maxSpeed; }
    public static Integer getChocoboMinPack() { return ChocoboMinPack; }
    public static void setChocoboMinPack(int chocoboMinPack) { StaticGlobalVariables.ChocoboMinPack = chocoboMinPack; }
    public static Integer getChocoboMaxPack() { return ChocoboMaxPack; }
    public static void setChocoboMaxPack(int chocoboMaxPack) { StaticGlobalVariables.ChocoboMaxPack = chocoboMaxPack; }
    public static Integer getOverWorldSpawnWeight() { return OverWorldSpawnWeight; }
    public static void setOverWorldSpawnWeight(int overWorldSpawnWeight) { StaticGlobalVariables.OverWorldSpawnWeight = overWorldSpawnWeight; }
    public static Integer getMushroomSpawnWeight() { return MushroomSpawnWeight; }
    public static void setMushroomSpawnWeight(int mushroomSpawnWeight) { StaticGlobalVariables.MushroomSpawnWeight = mushroomSpawnWeight; }
    public static Integer getNetherSpawnWeight() { return NetherSpawnWeight; }
    public static void setNetherSpawnWeight(int netherSpawnWeight) { StaticGlobalVariables.NetherSpawnWeight = netherSpawnWeight; }
    public static Integer getEndSpawnWeight() { return EndSpawnWeight; }
    public static void setEndSpawnWeight(int endSpawnWeight) { StaticGlobalVariables.EndSpawnWeight = endSpawnWeight; }
    public static Integer getGysahlGreenPatchSize() { return GysahlGreenPatchSize; }
    public static void setGysahlGreenPatchSize(int gysahlGreenPatchSize) { StaticGlobalVariables.GysahlGreenPatchSize = gysahlGreenPatchSize; }
    public static Double getGysahlGreenSpawnChance() { return GysahlGreenSpawnChance; }
    public static void setGysahlGreenSpawnChance(double gysahlGreenSpawnChance) { StaticGlobalVariables.GysahlGreenSpawnChance = gysahlGreenSpawnChance; }
    public static Double getStaminaRegen() { return StaminaRegen; }
    public static void getStaminaRegen(double staminaRegen) { StaticGlobalVariables.StaminaRegen = staminaRegen; }
    public static Double getTame() { return Tame; }
    public static void setTame(double tame) { StaticGlobalVariables.Tame = tame; }
    public static Double getStaminaCost() { return StaminaCost; }
    public static void setStaminaCost(double staminaCost) { StaticGlobalVariables.StaminaCost = staminaCost; }
    public static Double getStaminaGlide() { return StaminaGlide; }
    public static void setStaminaGlide(double staminaGlide) { StaticGlobalVariables.StaminaGlide = staminaGlide; }
    public static Double getStaminaJump() { return StaminaJump; }
    public static void setStaminaJump(double staminaJump) { StaticGlobalVariables.StaminaJump = staminaJump; }
    public static Double getPossLoss() { return PossLoss; }
    public static void setPossLoss(double possLoss) { StaticGlobalVariables.PossLoss = possLoss; }
    public static Double getPossGain() { return PossGain; }
    public static void setPossGain(double possGain) { StaticGlobalVariables.PossGain = possGain; }
    public static Double getMaxStamina() { return MaxStamina; }
    public static void setMaxStamina(double maxStamina) { StaticGlobalVariables.MaxStamina = maxStamina; }
    public static Double getMaxStrength() { return MaxStrength; }
    public static void setMaxStrength(double maxStrength) { StaticGlobalVariables.MaxStrength = maxStrength; }
    public static Double getMaxArmor() { return MaxArmor; }
    public static void setMaxArmor(double maxArmor) { StaticGlobalVariables.MaxArmor = maxArmor; }
    public static Double getMaxArmorToughness() { return MaxArmorToughness; }
    public static void setMaxArmorToughness(double maxArmorToughness) { StaticGlobalVariables.MaxArmorToughness = maxArmorToughness; }
    public static Double getArmorAlpha() { return ArmorAlpha; }
    public static void setArmorAlpha(double armorAlpha) { StaticGlobalVariables.ArmorAlpha = armorAlpha; }
    public static Double getWeaponAlpha() { return WeaponAlpha; }
    public static void setWeaponAlpha(double weaponAlpha) { StaticGlobalVariables.WeaponAlpha = weaponAlpha; }
    public static Double getCollarAlpha() { return CollarAlpha; }
    public static void setCollarAlpha(double collarAlpha) { StaticGlobalVariables.CollarAlpha = collarAlpha; }
    public static Double getSaddleAlpha() { return SaddleAlpha; }
    public static void setSaddleAlpha(double saddleAlpha) { StaticGlobalVariables.SaddleAlpha = saddleAlpha; }
    public static Boolean getCanSpawn() { return CanSpawn; }
    public static void setCanSpawn(boolean canSpawn) { StaticGlobalVariables.CanSpawn = canSpawn; }
    public static Boolean getExtraChocoboEffects() { return ExtraChocoboEffects; }
    public static void setExtraChocoboEffects(boolean extraChocoboEffects) { StaticGlobalVariables.ExtraChocoboEffects = extraChocoboEffects; }
    public static Boolean getExtraChocoboResourcesOnHit() { return ExtraChocoboResourcesOnHit; }
    public static void setExtraChocoboResourcesOnHit(boolean extraChocoboResourcesOnHit) { StaticGlobalVariables.ExtraChocoboResourcesOnHit = extraChocoboResourcesOnHit; }
    public static Boolean getExtraChocoboResourcesOnKill() { return ExtraChocoboResourcesOnKill; }
    public static void setExtraChocoboResourcesOnKill(boolean extraChocoboResourcesOnKill) { StaticGlobalVariables.ExtraChocoboResourcesOnKill = extraChocoboResourcesOnKill; }
    public static Boolean getShiftHitBypass() { return ShiftHitBypass; }
    public static void setShiftHitBypass(boolean shiftHitBypass) { StaticGlobalVariables.ShiftHitBypass = shiftHitBypass; }
    public static Boolean getOwnChocoboHittable() { return OwnChocoboHittable; }
    public static void setOwnChocoboHittable(boolean ownChocoboHittable) { StaticGlobalVariables.OwnChocoboHittable = ownChocoboHittable; }
    public static Boolean getTamedChocoboHittable() { return TamedChocoboHittable; }
    public static void setTamedChocoboHittable(boolean tamedChocoboHittable) { StaticGlobalVariables.TamedChocoboHittable = tamedChocoboHittable; }
    public static Boolean getOverworldSpawn() { return OverworldSpawn; }
    public static void setOverworldSpawn(boolean overworldSpawn) { StaticGlobalVariables.OverworldSpawn = overworldSpawn; }
    public static Boolean getNetherSpawn() { return NetherSpawn; }
    public static void setNetherSpawn(boolean netherSpawn) { StaticGlobalVariables.NetherSpawn = netherSpawn; }
    public static Boolean getEndSpawn() { return EndSpawn; }
    public static void setEndSpawn(boolean endSpawn) { StaticGlobalVariables.EndSpawn = endSpawn; }

    public static int getValueOrDefault(Integer value, int defaultValue) { return value != null ? value : defaultValue; }
    public static double getValueOrDefault(Double value, double defaultValue) { return value != null ? value : defaultValue; }
    public static boolean getValueOrDefault(Boolean value, boolean defaultValue) { return value != null ? value : defaultValue; }
}