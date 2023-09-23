package com.dephoegon.delchoco.aid.world.dValues;

public enum defaultDoubles {
    dSTAMINA_REGEN(0.01D, 0.025D, 1D),
    dTAME(0.05D, 0.15D, 1D),
    dSTAMINA_SPRINT(0D, 0.06D, 1D),
    dSTAMINA_GLIDE(0D, 0.005D, 1D),
    dSTAMINA_JUMP(0D, 0D, 1D),
    dPOS_GAIN(0D, .1D, 1D),
    dPOS_LOSS(0D, 1D, 1D),
    dMAX_STAMINA(10D, 200D, 1024D),
    dMAX_STRENGTH(8D, 80D, 200D),
    dMAX_ARMOR(20D, 200D, 5000D),
    dMAX_ARMOR_TOUGH(8D, 40D, 200D),
    dGYSAHL_GREEN_SPAWN_CHANCE(0.1D, 0.1D, 1D);
    private final double Min;
    private final double Default;
    private final double Max;
    defaultDoubles(double Minimum, double Default, double Maximum) {
        this.Min = Minimum;
        this.Default = Default;
        this.Max = Maximum;
    }
    public double getMin() { return this.Min; }
    public double getDefault() { return this.Default; }
    public double getMax() { return this.Max; }
}