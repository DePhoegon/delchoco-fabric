package com.dephoegon.delchoco.aid.world;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.*;

public class globalVariablesHolder {
    private int Stamina;
    private int Speed;
    private int Health;
    private int Armor;
    private int ArmorTough;
    private int Attack;
    private int WeaponModifier;
    private int HealAmount;
    private double StaminaRegen;
    private double Tame;
    private double StaminaCost;
    private double StaminaGlide;
    private double StaminaJump;
    private boolean CanSpawn; // natural & summoned
    private boolean ExtraChocoboEffects;
    private boolean ExtraChocoboResourcesOnHit;
    private boolean ExtraChocoboResourcesOnKill;
    private boolean ShiftHitBypass;
    private boolean OwnChocoboHittable;
    private boolean TamedChocoboHittable;
    private int EggHatchTimeTicks;
    private double PossLoss;
    private double PossGain;
    private int MaxHealth;
    private int MaxSpeed;
    private double MaxStamina;
    private double MaxStrength;
    private double MaxArmor;
    private double MaxArmorToughness;
    private float ArmorAlpha;
    private float WeaponAlpha;
    private float CollarAlpha;
    private float SaddleAlpha;
    private int ChocoboMinPack;
    private int ChocoboMaxPack;
    private int OverWorldSpawnWeight;
    private int MushroomSpawnWeight;
    private int NetherSpawnWeight;
    private int EndSpawnWeight;
    private double GysahlGreenSpawnChance;
    private int GysahlGreenPatchSize;
    private boolean OverworldSpawn;
    private boolean NetherSpawn;
    private boolean EndSpawn;
    public globalVariablesHolder() { }
    public globalVariablesHolder(NbtCompound nbt, boolean isWorld) {
        if (isWorld) { worldFromNbt(nbt); } else { chocoboFromNbt(nbt); }
    }

    // NBT functions TODO: Sort toNbtS' for cleanliness in config File
    public NbtCompound worldToNbt(@NotNull NbtCompound nbt) {
        nbt.putInt(ChocoboMinPack_name, ChocoboMinPack);
        nbt.putInt(ChocoboMaxPack_name, ChocoboMaxPack);
        nbt.putInt(OverWorldSpawnWeight_name, OverWorldSpawnWeight);
        nbt.putInt(MushroomSpawnWeight_name, MushroomSpawnWeight);
        nbt.putInt(NetherSpawnWeight_name, NetherSpawnWeight);
        nbt.putInt(EndSpawnWeight_name, EndSpawnWeight);
        nbt.putInt(GysahlGreenPatchSize_name, GysahlGreenPatchSize);
        nbt.putDouble(GysahlGreenSpawnChance_name, GysahlGreenSpawnChance);
        nbt.putBoolean(CanSpawn_name, CanSpawn);
        nbt.putBoolean(OverworldSpawn_name, OverworldSpawn);
        nbt.putBoolean(NetherSpawn_name, NetherSpawn);
        nbt.putBoolean(EndSpawn_name, EndSpawn);
        return nbt;
    }
    public void worldFromNbt(@NotNull NbtCompound nbt) {
        ChocoboMinPack = nbt.getInt(ChocoboMinPack_name);
        ChocoboMaxPack = nbt.getInt(ChocoboMaxPack_name);
        OverWorldSpawnWeight = nbt.getInt(OverWorldSpawnWeight_name);
        MushroomSpawnWeight = nbt.getInt(MushroomSpawnWeight_name);
        NetherSpawnWeight = nbt.getInt(NetherSpawnWeight_name);
        EndSpawnWeight = nbt.getInt(EndSpawnWeight_name);
        GysahlGreenPatchSize = nbt.getInt(GysahlGreenPatchSize_name);
        GysahlGreenSpawnChance = nbt.getDouble(GysahlGreenSpawnChance_name);
        CanSpawn = nbt.getBoolean(CanSpawn_name);
        OverworldSpawn = nbt.getBoolean(OverworldSpawn_name);
        NetherSpawn = nbt.getBoolean(NetherSpawn_name);
        EndSpawn = nbt.getBoolean(EndSpawn_name);
    }
    public NbtCompound chocoboToNbt(@NotNull NbtCompound nbt) {
        nbt.putInt(Stamina_name, Stamina);
        nbt.putInt(Speed_name, Speed);
        nbt.putInt(Health_name, Health);
        nbt.putInt(Armor_name, Armor);
        nbt.putInt(ArmorTough_name, ArmorTough);
        nbt.putInt(Attack_name, Attack);
        nbt.putInt(WeaponModifier_name, WeaponModifier);
        nbt.putInt(HealAmount_name, HealAmount);
        nbt.putInt(EggHatchTimeTicks_name, EggHatchTimeTicks);
        nbt.putInt(MaxHealth_name, MaxHealth);
        nbt.putInt(MaxSpeed_name, MaxSpeed);
        nbt.putDouble(StaminaRegen_name, StaminaRegen);
        nbt.putDouble(Tame_name, Tame);
        nbt.putDouble(StaminaCost_name, StaminaCost);
        nbt.putDouble(StaminaGlide_name, StaminaGlide);
        nbt.putDouble(StaminaJump_name, StaminaJump);
        nbt.putDouble(PossLoss_name, PossLoss);
        nbt.putDouble(PossGain_name, PossGain);
        nbt.putDouble(MaxStamina_name, MaxStamina);
        nbt.putDouble(MaxStrength_name, MaxStrength);
        nbt.putDouble(MaxArmor_name, MaxArmor);
        nbt.putDouble(MaxArmorToughness_name, MaxArmorToughness);
        nbt.putFloat(ArmorAlpha_name, ArmorAlpha);
        nbt.putFloat(WeaponAlpha_name, WeaponAlpha);
        nbt.putFloat(CollarAlpha_name, CollarAlpha);
        nbt.putFloat(SaddleAlpha_name, SaddleAlpha);
        nbt.putBoolean(ExtraChocoboEffects_name, ExtraChocoboEffects);
        nbt.putBoolean(ExtraChocoboResourcesOnHit_name, ExtraChocoboResourcesOnHit);
        nbt.putBoolean(ExtraChocoboResourcesOnKill_name, ExtraChocoboResourcesOnKill);
        nbt.putBoolean(ShiftHitBypass_name, ShiftHitBypass);
        nbt.putBoolean(OwnChocoboHittable_name, OwnChocoboHittable);
        nbt.putBoolean(TamedChocoboHittable_name, TamedChocoboHittable);
        return nbt;
    }

    public void chocoboFromNbt(@NotNull NbtCompound nbt) {
        Stamina = nbt.getInt(Stamina_name);
        Speed = nbt.getInt(Speed_name);
        Health = nbt.getInt(Health_name);
        Armor = nbt.getInt(Attack_name);
        ArmorTough = nbt.getInt(ArmorTough_name);
        Attack = nbt.getInt(Attack_name);
        WeaponModifier = nbt.getInt(WeaponModifier_name);
        HealAmount = nbt.getInt(HealAmount_name);
        EggHatchTimeTicks = nbt.getInt(EggHatchTimeTicks_name);
        MaxHealth = nbt.getInt(MaxHealth_name);
        MaxSpeed = nbt.getInt(MaxSpeed_name);
        StaminaRegen = nbt.getDouble(StaminaRegen_name);
        Tame = nbt.getDouble(Tame_name);
        StaminaCost = nbt.getDouble(StaminaCost_name);
        StaminaGlide = nbt.getDouble(StaminaGlide_name);
        StaminaJump = nbt.getDouble(StaminaJump_name);
        PossLoss = nbt.getDouble(PossLoss_name);
        PossGain = nbt.getDouble(PossGain_name);
        MaxStamina = nbt.getDouble(MaxStamina_name);
        MaxStrength = nbt.getDouble(MaxStrength_name);
        MaxArmor = nbt.getDouble(MaxArmor_name);
        MaxArmorToughness = nbt.getDouble(MaxArmorToughness_name);
        ArmorAlpha = nbt.getFloat(ArmorAlpha_name);
        WeaponAlpha = nbt.getFloat(WeaponAlpha_name);
        CollarAlpha = nbt.getFloat(CollarAlpha_name);
        SaddleAlpha = nbt.getFloat(SaddleAlpha_name);
        ExtraChocoboEffects = nbt.getBoolean(ExtraChocoboEffects_name);
        ExtraChocoboResourcesOnHit = nbt.getBoolean(ExtraChocoboResourcesOnHit_name);
        ExtraChocoboResourcesOnKill = nbt.getBoolean(ExtraChocoboResourcesOnKill_name);
        ShiftHitBypass = nbt.getBoolean(ShiftHitBypass_name);
        OwnChocoboHittable = nbt.getBoolean(OwnChocoboHittable_name);
        TamedChocoboHittable = nbt.getBoolean(TamedChocoboHittable_name);
    }

    //setters/getters
    public int getStamina() { return this.Stamina; }
    public void setStamina(int value) { this.Stamina = value; }
    public int getSpeed() { return this.Speed; }
    public void setSpeed(int value) { this.Speed = value; }
    public int getHealth() { return this.Health; }
    public void setHealth(int value) { this.Health = value; }
    public int getArmor() { return this.Armor; }
    public void setArmor(int value) { this.Armor = value; }
    public int getAttack() { return this.Attack; }
    public void setAttack(int value) { this.Attack = value; }
    public int getArmorTough() { return this.ArmorTough; }
    public void setArmorTough(int value) { this.ArmorTough = value; }
    public int getWeaponModifier() { return this.WeaponModifier; }
    public void setWeaponModifier(int value) { this.WeaponModifier = value; }
    public int getHealAmount() { return this.HealAmount; }
    public void setHealAmount(int value) { this.HealAmount = value; }
    public int getEggHatchTimeTicks() { return this.EggHatchTimeTicks; }
    public void setEggHatchTimeTicks(int value) { this.EggHatchTimeTicks = value; }
    public int getMaxHealth() { return this.MaxHealth; }
    public void setMaxHealth(int value) { this.MaxHealth = value; }
    public int getMaxSpeed() { return this.MaxSpeed; }
    public void setMaxSpeed(int value) { this.MaxSpeed = value; }
    public int getChocoboMinPack() { return this.ChocoboMinPack; }
    public void setChocoboMinPack(int value) { this.ChocoboMinPack = value; }
    public int getChocoboMaxPack() { return this.ChocoboMaxPack; }
    public void setChocoboMaxPack(int value) { this.ChocoboMaxPack = value; }
    public int getOverWorldSpawnWeight() { return this.OverWorldSpawnWeight; }
    public void setOverWorldSpawnWeight(int value) { this.OverWorldSpawnWeight = value; }
    public int getMushroomSpawnWeight() { return this.MushroomSpawnWeight; }
    public void setMushroomSpawnWeight(int value) { this.MushroomSpawnWeight = value; }
    public int getNetherSpawnWeight() { return this.NetherSpawnWeight; }
    public void setNetherSpawnWeight(int value) { this.NetherSpawnWeight = value; }
    public int getEndSpawnWeight() { return this.EndSpawnWeight; }
    public void setEndSpawnWeight(int value) { this.EndSpawnWeight = value; }
    public int getGysahlGreenPatchSize() { return this.GysahlGreenPatchSize; }
    public void setGysahlGreenPatchSize(int value) { this.GysahlGreenPatchSize = value; }
    public double getTame() { return this.Tame; }
    public void setTame(double value) { this.Tame = value; }
    public double getStaminaRegen() { return this.StaminaRegen; }
    public void setStaminaRegen(double value) { this.StaminaRegen = value; }
    public double getStaminaCost() { return this.StaminaCost; }
    public void setStaminaCost(double value) { this.StaminaCost = value; }
    public double getStaminaGlide() { return this.StaminaGlide; }
    public void setStaminaGlide(double value) { this.StaminaGlide = value; }
    public double getStaminaJump() { return this.StaminaJump; }
    public void setStaminaJump(double value) { this.StaminaJump = value; }
    public double getPossLoss() { return this.PossLoss; }
    public void setPossLoss(double value) { this.PossLoss = value; }
    public double getPossGain() { return this.PossGain; }
    public void setPossGain(double value) { this.PossGain = value; }
    public double getMaxStamina() { return this.MaxStamina; }
    public void setMaxStamina(double value) { this.MaxStamina = value; }
    public double getMaxStrength() { return this.MaxStrength; }
    public void setMaxStrength(double value) { this.MaxStrength = value; }
    public double getMaxArmor() { return this.MaxArmor; }
    public void setMaxArmor(double value) { this.MaxArmor = value; }
    public double getMaxArmorToughness() { return this.MaxArmorToughness; }
    public void setMaxArmorToughness(double value) { this.MaxArmorToughness = value; }
    public float getArmorAlpha() { return this.ArmorAlpha; }
    public void setArmorAlpha(float value) { this.ArmorAlpha = value; }
    public float getWeaponAlpha() { return this.WeaponAlpha; }
    public void setWeaponAlpha(float value) { this.WeaponAlpha = value; }
    public float getCollarAlpha() { return this.CollarAlpha; }
    public void setCollarAlpha(float value) { this.CollarAlpha = value; }
    public float getSaddleAlpha() { return this.SaddleAlpha; }
    public void setSaddleAlpha(float value) { this.SaddleAlpha = value; }
    public double getGysahlGreenSpawnChance() { return this.GysahlGreenSpawnChance; }
    public void setGysahlGreenSpawnChance(double value) { this.GysahlGreenSpawnChance = value; }
    public boolean isCanSpawn() { return this.CanSpawn; }
    public void setCanSpawn(boolean value) { this.CanSpawn = value; }
    public boolean isExtraChocoboEffects() { return this.ExtraChocoboEffects; }
    public void setExtraChocoboEffects(boolean value) { this.ExtraChocoboEffects = value; }
    public boolean isExtraChocoboResourcesOnHit() { return this.ExtraChocoboResourcesOnHit; }
    public void setExtraChocoboResourcesOnHit(boolean value) { this.ExtraChocoboResourcesOnHit = value; }
    public boolean isExtraChocoboResourcesOnKill() { return this.ExtraChocoboResourcesOnKill; }
    public void setExtraChocoboResourcesOnKill(boolean value) { this.ExtraChocoboResourcesOnKill = value; }
    public boolean isShiftHitBypass() { return this.ShiftHitBypass; }
    public void setShiftHitBypass(boolean value) { this.ShiftHitBypass = value; }
    public boolean isOwnChocoboHittable() { return this.OwnChocoboHittable; }
    public void setOwnChocoboHittable(boolean value) { this.OwnChocoboHittable = value; }
    public boolean isTamedChocoboHittable() { return this.TamedChocoboHittable; }
    public void setTamedChocoboHittable(boolean value) { this.TamedChocoboHittable = value; }
    public boolean isOverworldSpawn() { return this.OverworldSpawn; }
    public void setOverworldSpawn(boolean value) { this.OverworldSpawn = value; }
    public boolean isNetherSpawn() { return this.NetherSpawn; }
    public void setNetherSpawn(boolean value) { this.NetherSpawn = value; }
    public boolean isEndSpawn() { return this.EndSpawn; }
    public void setEndSpawn(boolean value) { this.EndSpawn = value; }
}