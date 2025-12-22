package com.eval.NNUE;

public final class NNUEState {

    public final int[] acc;

    public NNUEState(int hidden) {
        this.acc = new int[hidden];
    }
}