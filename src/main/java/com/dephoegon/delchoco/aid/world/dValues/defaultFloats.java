package com.dephoegon.delchoco.aid.world.dValues;

public enum defaultFloats {
    dARMOR_ALPHA(0F, 0.1F, 0.75F),
    dWEAPON_ALPHA(0F, 0.1F, 1F),
    dCOLLAR_ALPHA(0F, 0.2F, 1F),
    dSADDLE_ALPHA(0F, 0.1F, 1F);
    private final float Min;
    private final float Default;
    private final float Max;
    @SuppressWarnings("SameParameterValue")
    defaultFloats(float Minimum, float Default, float Maximum) {
        this.Min = Minimum;
        this.Default = Default;
        this.Max = Maximum;
    }
    public float getMin() { return this.Min; }
    public float getDefault() { return this.Default; }
    public float getMax() { return this.Max; }
}