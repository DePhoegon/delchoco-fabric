package com.dephoegon.delchoco.aid.world;

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.setChocoboMinPack;
import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.setStamina;

public enum defaultVariables {
    dSTAMINA(0, 10, 60),
    dSPEED(0, 20, 80),
    dHEALTH(6, 20, 1000),
    dARMOR(0, 4, 20),
    dARMOR_TOUGH(0, 1, 10),
    dWEAPON_MOD(1, 1, 3),
    dATTACK(1, 2, 10),
    dHEAL_AMOUNT(2, 5, 10),
    dSTAMINA_REGEN(0.01D, 0.025D, 1D),
    dTAME(0.05D, 0.15D, 1D),
    dSTAMINA_SPRINT(0D, 0.06D, 1D),
    dSTAMINA_GLIDE(0D, 0.005D, 1D),
    dSTAMINA_JUMP(0D, 0D, 1D),
    dEGG_HATCH(50, 500, 50000),
    dPOS_GAIN(0D, .1D, 1D),
    dPOS_LOSS(0D, 1D, 1D),
    dMAX_HEALTH(25, 60, 3000),
    dMAX_SPEED(30, 40, 160),
    dMAX_STAMINA(20D, 35D, 80D),
    dMAX_STRENGTH(8D, 60D, 100D),
    dMAX_ARMOR(20D, 200D, 500D),
    dMAX_ARMOR_TOUGH(8D, 20D, 100D),
    dARMOR_ALPHA(0F, 0.1F, 0.75F),
    dWEAPON_ALPHA(0F, 0.1F, 1F),
    dCOLLAR_ALPHA(0F, 0.2F, 1F),
    dSADDLE_ALPHA(0F, 0.1F, 1F),
    dCHOCOBO_PACK_MIN(1, 1, 4),
    dCHOCOBO_PACK_MAX(2, 4, 10),
    dOVERWORLD_SPAWN_WEIGHT(0, 8, 100),
    dNETHER_SPAWN_WEIGHT(75, 100, 200),
    dEND_SPAWN_WEIGHT(75, 100, 200),
    dGYSAHL_GREEN_SPAWN_CHANCE(0D, 0.1D, 1D),
    dGYSAHL_GREEN_PATCH_SIZE(0, 64, 128);

    public static final boolean dOverworldSpawn = true;
    public static final boolean dNetherSpawn = true;
    public static final boolean dEndSpawn = true;
    public static final Boolean dCanSpawn = true;
    public static final Boolean dExtraChocoboEffects = true;
    public static final Boolean dExtraChocoboResourcesOnHit = true;
    public static final Boolean dExtraChocoboResourcesOnKill = true;
    public static final Boolean dShiftHitBypass = true;
    public static final Boolean dOwnChocoboHittable = false;
    public static final Boolean dTamedChocoboHittable = false;
    private final Integer intMin;
    private final Integer intDefault;
    private final Integer intMax;
    private final Double dblMin;
    private final Double dblDefault;
    private final Double dblMax;
    private final Float fltMin;
    private final Float fltDefault;
    private final Float fltMax;
    private final boolean isInt;
    private final boolean isDbl;
    defaultVariables(Integer Minimum, Integer Default, Integer Maximum) {
        this.intMin = Minimum;
        this.intDefault = Default;
        this.intMax = Maximum;
        this.dblMin = 0D;
        this.dblDefault = 0D;
        this.dblMax = 0D;
        this.fltMin = 0F;
        this.fltDefault = 0F;
        this.fltMax = 0F;
        this.isInt = true;
        this.isDbl = false;
    }
    defaultVariables(Double Minimum, Double Default, Double Maximum) {
        this.intMin = 0;
        this.intDefault = 0;
        this.intMax = 0;
        this.dblMin = Minimum;
        this.dblDefault = Default;
        this.dblMax = Maximum;
        this.fltMin = 0F;
        this.fltDefault = 0F;
        this.fltMax = 0F;
        this.isInt = false;
        this.isDbl = true;
    }
    defaultVariables(Float Minimum, Float Default, Float Maximum) {
        this.intMin = 0;
        this.intDefault = 0;
        this.intMax = 0;
        this.dblMin = 0D;
        this.dblDefault = 0D;
        this.dblMax = 0D;
        this.fltMin = Minimum;
        this.fltDefault = Default;
        this.fltMax = Maximum;
        this.isInt = false;
        this.isDbl = false;
    }
    public Number getMin() { return this.isInt ? this.intMin : this.isDbl ? this.dblMin : this.fltMin; }
    public Number getDefault() { return this.isInt ? this.intDefault : this.isDbl ? this.dblDefault : this.fltDefault; }
    public Number getMax() { return this.isInt ? this.intMax : this.isDbl ? this.dblMax : this.fltMax; }

    public static Integer ChocoConfigGet(Integer value, Integer FallBack) { return value == null ? FallBack : value; }
    public static Double ChocoConfigGet(Double value, Double FallBack) { return value == null ? FallBack : value; }
    public static Float ChocoConfigGet(Float value, Float FallBack) { return value == null ? FallBack : value; }
    public static Boolean ChocoConfigGet(Boolean config, Boolean FallBack) { return config == null ? FallBack : config; }
    public static void setDefaultValues(boolean isWorld) {
        if (isWorld) {
            setChocoboMinPack(dCHOCOBO_PACK_MIN.getDefault().intValue());
            // World Configs
        } else {
            setStamina(dSTAMINA.getDefault().intValue());
            // Chocobo Configs
        }
    } // Use StaticGlobalVariables set Methods
}