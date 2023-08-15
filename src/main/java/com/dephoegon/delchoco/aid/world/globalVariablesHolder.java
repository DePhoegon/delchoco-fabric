package com.dephoegon.delchoco.aid.world;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.*;

public class globalVariablesHolder {
    public int Stamina;
    public int Speed;
    public int Health;
    public int Armor;
    public int ArmorTough;
    public int Attack;
    public int WeaponModifier;
    public int HealAmount;
    public double StaminaRegen;
    public double Tame;
    public double StaminaCost;
    public double StaminaGlide;
    public double StaminaJump;
    public boolean CanSpawn;
    public boolean ExtraChocoboEffects;
    public boolean ExtraChocoboResourcesOnHit;
    public boolean ExtraChocoboResourcesOnKill;
    public boolean ShiftHitBypass;
    public boolean OwnChocoboHittable;
    public boolean TamedChocoboHittable;
    public int EggHatchTimeTicks;
    public double PossLoss;
    public double PossGain;
    public int MaxHealth;
    public int MaxSpeed;
    public double MaxStamina;
    public double MaxStrength;
    public double MaxArmor;
    public double MaxArmorToughness;
    public double ArmorAlpha;
    public double WeaponAlpha;
    public double CollarAlpha;
    public double SaddleAlpha;
    public int ChocoboMinPack;
    public int ChocoboMaxPack;
    public int OverWorldSpawnWeight;
    public int MushroomSpawnWeight;
    public int NetherSpawnWeight;
    public int EndSpawnWeight;
    public double GysahlGreenSpawnChance;
    public int GysahlGreenPatchSize;
    public boolean OverworldSpawn;
    public boolean NetherSpawn;
    public boolean EndSpawn;
    public globalVariablesHolder() { }
    public globalVariablesHolder(NbtCompound nbt) { fromNbt(nbt); }

    // NBT functions TODO: Sort toNbt for cleanliness in config File
    public NbtCompound toNbt(@NotNull NbtCompound nbt) {
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
        nbt.putInt(ChocoboMinPack_name, ChocoboMinPack);
        nbt.putInt(ChocoboMaxPack_name, ChocoboMaxPack);
        nbt.putInt(OverWorldSpawnWeight_name, OverWorldSpawnWeight);
        nbt.putInt(MushroomSpawnWeight_name, MushroomSpawnWeight);
        nbt.putInt(NetherSpawnWeight_name, NetherSpawnWeight);
        nbt.putInt(EndSpawnWeight_name, EndSpawnWeight);
        nbt.putInt(GysahlGreenPatchSize_name, GysahlGreenPatchSize);
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
        nbt.putDouble(ArmorAlpha_name, ArmorAlpha);
        nbt.putDouble(WeaponAlpha_name, WeaponAlpha);
        nbt.putDouble(CollarAlpha_name, CollarAlpha);
        nbt.putDouble(SaddleAlpha_name, SaddleAlpha);
        nbt.putDouble(GysahlGreenSpawnChance_name, GysahlGreenSpawnChance);
        nbt.putBoolean(CanSpawn_name, CanSpawn);
        nbt.putBoolean(ExtraChocoboEffects_name, ExtraChocoboEffects);
        nbt.putBoolean(ExtraChocoboResourcesOnHit_name, ExtraChocoboResourcesOnHit);
        nbt.putBoolean(ExtraChocoboResourcesOnKill_name, ExtraChocoboResourcesOnKill);
        nbt.putBoolean(ShiftHitBypass_name, ShiftHitBypass);
        nbt.putBoolean(OwnChocoboHittable_name, OwnChocoboHittable);
        nbt.putBoolean(TamedChocoboHittable_name, TamedChocoboHittable);
        nbt.putBoolean(OverworldSpawn_name, OverworldSpawn);
        nbt.putBoolean(NetherSpawn_name, NetherSpawn);
        nbt.putBoolean(EndSpawn_name, EndSpawn);
        return nbt;
    }

    public void fromNbt(@NotNull NbtCompound nbt) {
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
        ChocoboMinPack = nbt.getInt(ChocoboMinPack_name);
        ChocoboMaxPack = nbt.getInt(ChocoboMaxPack_name);
        OverWorldSpawnWeight = nbt.getInt(OverWorldSpawnWeight_name);
        MushroomSpawnWeight = nbt.getInt(MushroomSpawnWeight_name);
        NetherSpawnWeight = nbt.getInt(NetherSpawnWeight_name);
        EndSpawnWeight = nbt.getInt(EndSpawnWeight_name);
        GysahlGreenPatchSize = nbt.getInt(GysahlGreenPatchSize_name);
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
        ArmorAlpha = nbt.getDouble(ArmorAlpha_name);
        WeaponAlpha = nbt.getDouble(WeaponAlpha_name);
        CollarAlpha = nbt.getDouble(CollarAlpha_name);
        SaddleAlpha = nbt.getDouble(SaddleAlpha_name);
        GysahlGreenSpawnChance = nbt.getDouble(GysahlGreenSpawnChance_name);
        CanSpawn = nbt.getBoolean(CanSpawn_name);
        ExtraChocoboEffects = nbt.getBoolean(ExtraChocoboEffects_name);
        ExtraChocoboResourcesOnHit = nbt.getBoolean(ExtraChocoboResourcesOnHit_name);
        ExtraChocoboResourcesOnKill = nbt.getBoolean(ExtraChocoboResourcesOnKill_name);
        ShiftHitBypass = nbt.getBoolean(ShiftHitBypass_name);
        OwnChocoboHittable = nbt.getBoolean(OwnChocoboHittable_name);
        TamedChocoboHittable = nbt.getBoolean(TamedChocoboHittable_name);
        OverworldSpawn = nbt.getBoolean(OverworldSpawn_name);
        NetherSpawn = nbt.getBoolean(NetherSpawn_name);
        EndSpawn = nbt.getBoolean(EndSpawn_name);
    }

    //setters/getters
    public int getStamina() { return this.Stamina; }
    public int getSpeed() { return this.Speed; }
    public int getHealth() { return this.Health; }
    public int getArmor() { return this.Armor; }
    public int getAttack() { return this.Attack; }
    public int getArmorTough() { return this.ArmorTough; }
    public int getWeaponModifier() { return this.WeaponModifier; }
    public int getHealAmount() { return this.HealAmount; }
    public int getEggHatchTimeTicks() { return this.EggHatchTimeTicks; }
    public int getMaxHealth() { return this.MaxHealth; }
    public int getMaxSpeed() { return this.MaxSpeed; }
    public int getChocoboMinPack() { return this.ChocoboMinPack; }
    public int getChocoboMaxPack() { return this.ChocoboMaxPack; }
    public int getOverWorldSpawnWeight() { return this.OverWorldSpawnWeight; }
    public int getMushroomSpawnWeight() { return this.MushroomSpawnWeight; }
    public int getNetherSpawnWeight() { return this.NetherSpawnWeight; }
    public int getEndSpawnWeight() { return this.EndSpawnWeight; }
    public int getGysahlGreenPatchSize() { return this.GysahlGreenPatchSize; }
    public double getTame() { return this.Tame; }
    public double getStaminaRegen() { return this.StaminaRegen; }
    public double getStaminaCost() { return this.StaminaCost; }
    public double getStaminaGlide() { return this.StaminaGlide; }
    public double getStaminaJump() { return this.StaminaJump; }
    public double getPossLoss() { return this.PossLoss; }
    public double getPossGain() { return this.PossGain; }
    public double getMaxStamina() { return this.MaxStamina; }
    public double getMaxStrength() { return this.MaxStrength; }
    public double getMaxArmor() { return this.MaxArmor; }
    public double getMaxArmorToughness() { return this.MaxArmorToughness; }
    public double getArmorAlpha() { return this.ArmorAlpha; }
    public double getWeaponAlpha() { return this.WeaponAlpha; }
    public double getCollarAlpha() { return this.CollarAlpha; }
    public double getSaddleAlpha() { return this.SaddleAlpha; }
    public double getGysahlGreenSpawnChance() { return this.GysahlGreenSpawnChance; }
    public boolean isCanSpawn() { return this.CanSpawn; }
    public boolean isExtraChocoboEffects() { return this.ExtraChocoboEffects; }
    public boolean isExtraChocoboResourcesOnHit() { return this.ExtraChocoboResourcesOnHit; }
    public boolean isExtraChocoboResourcesOnKill() { return this.ExtraChocoboResourcesOnKill; }
    public boolean isShiftHitBypass() { return this.ShiftHitBypass; }
    public boolean isOwnChocoboHittable() { return this.OwnChocoboHittable; }
    public boolean isTamedChocoboHittable() { return this.TamedChocoboHittable; }
    public boolean isOverworldSpawn() { return this.OverworldSpawn; }
    public boolean isNetherSpawn() { return this.NetherSpawn; }
    public boolean isEndSpawn() { return this.EndSpawn; }
}