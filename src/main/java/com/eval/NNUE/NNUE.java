package com.eval.NNUE;

public final class NNUE {

    public static final NNUEWeights WEIGHTS;

    static {
        WEIGHTS = NNUEWeights.createDummy(256);
    }

    private NNUE() {}
}
