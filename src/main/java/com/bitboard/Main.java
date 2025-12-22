package com.bitboard;

import java.util.Arrays;

import com.eval.NNUE.NNUEEvaluator;
import com.eval.NNUE.NNUEState;
import com.eval.NNUE.NNUEWeights;

public class Main {
    
    public static void main(String[] args) {
        BitBoard board = new BitBoard();

        NNUEState s = new NNUEState(256);
        NNUEWeights w = new NNUEWeights(256);

        NNUEEvaluator.initFromBoard(s, w, board);
        int[] rebuild = s.acc.clone();

        // reset
        Arrays.fill(s.acc, 0);
        for (int i = 0; i < w.hidden; i++)
            s.acc[i] = w.b1[i];

        // Pawns
        NNUEEvaluator.nnueAdd(s, w, 0, 0, 8);  // white pawn a2
        NNUEEvaluator.nnueAdd(s, w, 0, 0, 9);  // white pawn b2
        NNUEEvaluator.nnueAdd(s, w, 0, 0, 10); // white pawn c2
        NNUEEvaluator.nnueAdd(s, w, 0, 0, 11); // white pawn d2
        NNUEEvaluator.nnueAdd(s, w, 0, 0, 12); // white pawn e2
        NNUEEvaluator.nnueAdd(s, w, 0, 0, 13); // white pawn f2
        NNUEEvaluator.nnueAdd(s, w, 0, 0, 14); // white pawn g2
        NNUEEvaluator.nnueAdd(s, w, 0, 0, 15); // white pawn h2

        NNUEEvaluator.nnueAdd(s, w, 1, 0, 48); // black pawn a7
        NNUEEvaluator.nnueAdd(s, w, 1, 0, 49); // black pawn b7
        NNUEEvaluator.nnueAdd(s, w, 1, 0, 50); // black pawn c7
        NNUEEvaluator.nnueAdd(s, w, 1, 0, 51); // black pawn d7
        NNUEEvaluator.nnueAdd(s, w, 1, 0, 52); // black pawn e7
        NNUEEvaluator.nnueAdd(s, w, 1, 0, 53); // black pawn f7
        NNUEEvaluator.nnueAdd(s, w, 1, 0, 54); // black pawn g7
        NNUEEvaluator.nnueAdd(s, w, 1, 0, 55); // black pawn h7

        // knights
        NNUEEvaluator.nnueAdd(s, w, 0, 1, 1);  // white knight b1
        NNUEEvaluator.nnueAdd(s, w, 0, 1, 6);  // white knight g1
        NNUEEvaluator.nnueAdd(s, w, 1, 1, 57); // black knight b8
        NNUEEvaluator.nnueAdd(s, w, 1, 1, 62); // black knight g8

        // bishops
        NNUEEvaluator.nnueAdd(s, w, 0, 2, 2);  // white bishop c1
        NNUEEvaluator.nnueAdd(s, w, 0, 2, 5);  // white bishop f1
        NNUEEvaluator.nnueAdd(s, w, 1, 2, 58); // black bishop c8
        NNUEEvaluator.nnueAdd(s, w, 1, 2, 61); // black bishop f8

        // rooks
        NNUEEvaluator.nnueAdd(s, w, 0, 3, 0);  // white rook a1
        NNUEEvaluator.nnueAdd(s, w, 0, 3, 7);  // white rook h1
        NNUEEvaluator.nnueAdd(s, w, 1, 3, 56); // black rook a8
        NNUEEvaluator.nnueAdd(s, w, 1, 3, 63); // black rook h8

        // queens
        NNUEEvaluator.nnueAdd(s, w, 0, 4, 3);  // white queen d1
        NNUEEvaluator.nnueAdd(s, w, 1, 4, 59); // black queen d8
        
        // kings
        NNUEEvaluator.nnueAdd(s, w, 0, 5, 4);  // white king e1
        NNUEEvaluator.nnueAdd(s, w, 1, 5, 60); // black king e8

        assert Arrays.equals(rebuild, s.acc);

    }
}
