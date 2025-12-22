package com.bitboard;

import java.util.Arrays;

import com.eval.NNUE.NNUEEvaluator;
import com.eval.NNUE.NNUEState;
import com.eval.NNUE.NNUEWeights;

public class Main {
    
    public static void main(String[] args) {
        BitBoard bitBoard = new BitBoard();

        NNUEState state = new NNUEState(256);
        NNUEWeights weights = new NNUEWeights(256);

        NNUEEvaluator.initFromBoard(state, weights, bitBoard);

        int[] a = state.acc.clone();

        NNUEEvaluator.initFromBoard(state, weights, bitBoard);

        assert Arrays.equals(a, state.acc);
    }
}
