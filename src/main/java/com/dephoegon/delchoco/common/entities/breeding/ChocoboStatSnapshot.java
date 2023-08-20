package com.dephoegon.delchoco.common.entities.breeding;

import com.dephoegon.delchoco.aid.world.StaticGlobalVariables;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.init.ModAttributes;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.ChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.dValues.defaultInts.*;

public class ChocoboStatSnapshot {
    public static final ChocoboStatSnapshot DEFAULT;
    public static final String NBTKEY_GENERATION = "Generation";
    public static final String NBTKEY_HEALTH = "Health";
    public static final String NBTKEY_SPEED = "Speed";
    public static final String NBTKEY_STAMINA = "Stamina";
    public static final String NBTKEY_COLOR = "Color";
    public static final String NBTKEY_FLAME_BLOOD = "FlameBlood";
    public static final String NBTKEY_WATER_BREATH = "WaterBreath";
    public static final String NBTKEY_ATTACK = "Damage";
    public static final String NBTKEY_ARMOR = "Armor";
    public static final String NBTKEY_ARMOR_TOUGHNESS = "Toughness";
    private static final String NBTKEY_CHOCOBO_WITHER_IMMUNE = "WitherImmune";
    private static final String NBTKEY_CHOCOBO_POISON_IMMUNE = "PoisonImmune";
    private static final String NBTKEY_CHOCOBO_SCALE = "Scale";

    public int generation;
    public int scale;
    public float health;
    public float speed;
    public float stamina;
    public boolean flameBlood;
    public boolean waterBreath;
    public boolean witherImmune;
    public boolean poisonImmune;
    public ChocoboColor color;
    public double attack;
    public double defense;
    public double toughness;

    static {
        DEFAULT = new ChocoboStatSnapshot();
        DEFAULT.generation = 1;
        DEFAULT.health = ChocoConfigGet(StaticGlobalVariables.getHealth(), dHEALTH.getDefault());
        DEFAULT.stamina = ChocoConfigGet(StaticGlobalVariables.getStamina(), dSTAMINA.getDefault());
        DEFAULT.speed = ChocoConfigGet(StaticGlobalVariables.getSpeed(), dSPEED.getDefault()) / 100f;
        DEFAULT.attack = ChocoConfigGet(StaticGlobalVariables.getAttack(), dATTACK.getDefault());
        DEFAULT.defense = ChocoConfigGet(StaticGlobalVariables.getArmor(), dARMOR.getDefault());
        DEFAULT.toughness = ChocoConfigGet(StaticGlobalVariables.getArmorTough(), dARMOR_TOUGH.getDefault());
        DEFAULT.flameBlood = false;
        DEFAULT.waterBreath = false;
        DEFAULT.color = ChocoboColor.YELLOW;
        DEFAULT.witherImmune = false;
        DEFAULT.poisonImmune = false;
    }
    public ChocoboStatSnapshot() { }
    public ChocoboStatSnapshot(@NotNull Chocobo chocobo) {
        this.generation = chocobo.getGeneration();
        this.health = (float) Objects.requireNonNull(chocobo.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).getBaseValue();
        this.speed = (float) Objects.requireNonNull(chocobo.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getBaseValue();
        this.stamina = (float) Objects.requireNonNull(chocobo.getAttributeInstance(ModAttributes.CHOCOBO_MAX_STAMINA)).getBaseValue();
        this.attack = Objects.requireNonNull(chocobo.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).getBaseValue();
        this.defense = Objects.requireNonNull(chocobo.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).getBaseValue();
        this.toughness = Objects.requireNonNull(chocobo.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).getBaseValue();
        this.flameBlood = chocobo.isFireImmune();
        this.waterBreath = chocobo.isWaterBreather();
        this.witherImmune = chocobo.isWitherImmune();
        this.poisonImmune = chocobo.isPoisonImmune();
        this.scale = 0;
        this.color = chocobo.getChocoboColor();
    }
    public ChocoboStatSnapshot(@NotNull NbtCompound nbt) {
        this.generation = nbt.getInt(NBTKEY_GENERATION);
        this.health = nbt.getFloat(NBTKEY_HEALTH);
        this.speed = nbt.getFloat(NBTKEY_SPEED);
        this.stamina = nbt.getFloat(NBTKEY_STAMINA);
        this.attack = nbt.getDouble(NBTKEY_ATTACK);
        this.attack = nbt.getDouble(NBTKEY_ARMOR);
        this.attack = nbt.getDouble(NBTKEY_ARMOR_TOUGHNESS);
        this.flameBlood = nbt.getBoolean(NBTKEY_FLAME_BLOOD);
        this.waterBreath = nbt.getBoolean(NBTKEY_WATER_BREATH);
        this.witherImmune = nbt.getBoolean(NBTKEY_CHOCOBO_WITHER_IMMUNE);
        this.poisonImmune = nbt.getBoolean(NBTKEY_CHOCOBO_POISON_IMMUNE);
        this.scale = nbt.getInt(NBTKEY_CHOCOBO_SCALE);
        this.color = ChocoboColor.values()[nbt.getByte(NBTKEY_COLOR)];
    }
    public NbtCompound serialize() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt(NBTKEY_GENERATION, this.generation);
        nbt.putFloat(NBTKEY_HEALTH, this.health);
        nbt.putFloat(NBTKEY_SPEED, this.speed);
        nbt.putFloat(NBTKEY_STAMINA, this.stamina);
        nbt.putDouble(NBTKEY_ATTACK, this.attack);
        nbt.putDouble(NBTKEY_ARMOR, this.defense);
        nbt.putDouble(NBTKEY_ARMOR_TOUGHNESS, this.toughness);
        nbt.putBoolean(NBTKEY_FLAME_BLOOD, this.flameBlood);
        nbt.putBoolean(NBTKEY_WATER_BREATH, this.waterBreath);
        nbt.putBoolean(NBTKEY_CHOCOBO_WITHER_IMMUNE, this.witherImmune);
        nbt.putBoolean(NBTKEY_CHOCOBO_POISON_IMMUNE, this.poisonImmune);
        nbt.putInt(NBTKEY_CHOCOBO_SCALE, this.scale);
        nbt.putByte(NBTKEY_COLOR, (byte) this.color.ordinal());
        return nbt;
    }
}