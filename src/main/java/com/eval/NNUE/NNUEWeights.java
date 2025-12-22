package com.eval.NNUE;

public final class NNUEWeights {

    public final int hidden;
    public final short[][] w1; // [768][hidden]
    public final int[] b1;     // [hidden]

    public NNUEWeights(int hidden) {
        this.hidden = hidden;
        this.w1 = new short[FeatureMapper.FEATURES][hidden];
        this.b1 = new int[hidden];
    }
}
