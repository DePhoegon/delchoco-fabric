package com.dephoegon.delchoco.aid.world.dValues;

public enum defaultInts {
    dSTAMINA(5, 10, 60),
    dSPEED(10, 20, 80),
    dHEALTH(6, 20, 1000),
    dARMOR(0, 4, 20),
    dARMOR_TOUGH(0, 1, 10),
    dWEAPON_MOD(1, 1, 3),
    dATTACK(1, 2, 10),
    dEGG_HATCH(50, 500, 50000),
    dHEAL_AMOUNT(2, 5, 10),
    dMAX_HEALTH(25, 60, 3000),
    dMAX_SPEED(30, 40, 160),
    dCHOCOBO_PACK_MIN(1, 1, 4),
    dCHOCOBO_PACK_MAX(2, 4, 10),
    dOVERWORLD_SPAWN_WEIGHT(0, 8, 100),
    dMUSHROOM_SPAWN_WEIGHT(0, 2, 4),
    dNETHER_SPAWN_WEIGHT(75, 100, 200),
    dEND_SPAWN_WEIGHT(75, 100, 200),
    dGYSAHL_GREEN_PATCH_SIZE(0, 64, 128);

    private final int Min;
    private final int Default;
    private final int Max;
    defaultInts(int Minimum, int Default, int Maximum) {
        this.Min = Minimum;
        this.Default = Default;
        this.Max = Maximum;
    }
    public int getMin() { return this.Min; }
    public int getDefault() { return this.Default; }
    public int getMax() { return this.Max; }
}