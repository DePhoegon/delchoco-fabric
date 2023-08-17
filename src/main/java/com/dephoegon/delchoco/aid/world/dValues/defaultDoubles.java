package com.dephoegon.delchoco.aid.world.dValues;

public enum defaultDoubles {
    dSTAMINA_REGEN(0.01D, 0.025D, 1D),
    dTAME(0.05D, 0.15D, 1D),
    dSTAMINA_SPRINT(0D, 0.06D, 1D),
    dSTAMINA_GLIDE(0D, 0.005D, 1D),
    dSTAMINA_JUMP(0D, 0D, 1D),
    dPOS_GAIN(0D, .1D, 1D),
    dPOS_LOSS(0D, 1D, 1D),
    dMAX_STAMINA(20D, 35D, 80D),
    dMAX_STRENGTH(8D, 60D, 100D),
    dMAX_ARMOR(20D, 200D, 500D),
    dMAX_ARMOR_TOUGH(8D, 20D, 100D),
    dGYSAHL_GREEN_SPAWN_CHANCE(0D, 0.1D, 1D);
    private final Double Min;
    private final Double Default;
    private final Double Max;
    defaultDoubles(Double Minimum, Double Default, Double Maximum) {
        this.Min = Minimum;
        this.Default = Default;
        this.Max = Maximum;
    }
    public double getMin() { return this.Min; }
    public double getDefault() { return this.Default; }
    public double getMax() { return this.Max; }
}
