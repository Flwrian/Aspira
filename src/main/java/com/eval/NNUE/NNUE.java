package com.eval.NNUE;

public final class NNUE {

    public static final NNUEWeights WEIGHTS;

    static {
        System.out.println("Loading NNUE weights...");
        WEIGHTS = NNUEWeights.load("./nnue/beans.bin");
        System.out.println("Loaded NNUE weights:");
    }

    private NNUE() {}
}
