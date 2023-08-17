package com.dephoegon.delchoco.aid.world;

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
    public static void setStaminaRegen(double staminaRegen) { StaticGlobalVariables.StaminaRegen = staminaRegen; }
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
    public static Float getArmorAlpha() { return ArmorAlpha; }
    public static void setArmorAlpha(float armorAlpha) { StaticGlobalVariables.ArmorAlpha = armorAlpha; }
    public static Float getWeaponAlpha() { return WeaponAlpha; }
    public static void setWeaponAlpha(float weaponAlpha) { StaticGlobalVariables.WeaponAlpha = weaponAlpha; }
    public static Float getCollarAlpha() { return CollarAlpha; }
    public static void setCollarAlpha(float collarAlpha) { StaticGlobalVariables.CollarAlpha = collarAlpha; }
    public static Float getSaddleAlpha() { return SaddleAlpha; }
    public static void setSaddleAlpha(float saddleAlpha) { StaticGlobalVariables.SaddleAlpha = saddleAlpha; }
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
    public static float getValueOrDefault(Float value, float defaultValue) { return value != null ? value : defaultValue; }
}