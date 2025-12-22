package com.eval.NNUE;

import java.util.Arrays;

import com.bitboard.BitBoard;

public final class NNUEEvaluator {

    public static void processBitboard(NNUEState state, NNUEWeights weights, int color, int pieceType, long bb) {
        while (bb != 0) {
            long lsb = bb & -bb;
            int square = Long.numberOfTrailingZeros(lsb);

            int f = FeatureMapper.featureIndex(color, pieceType, square);
            short[] row = weights.w1[f];

            for (int i = 0; i < weights.hidden; i++) {
                state.acc[i] += row[i];
            }

            bb ^= lsb;
        }
    }

    public static void initFromBoard(NNUEState state, NNUEWeights weights, BitBoard board) {

        Arrays.fill(state.acc, 0);

        // Bias
        for (int i = 0; i < weights.hidden; i++) {
            state.acc[i] += weights.b1[i];
        }

        // WHITE
        processBitboard(state, weights, 0, BitBoard.PAWN,   board.whitePawns);
        processBitboard(state, weights, 0, BitBoard.KNIGHT, board.whiteKnights);
        processBitboard(state, weights, 0, BitBoard.BISHOP, board.whiteBishops);
        processBitboard(state, weights, 0, BitBoard.ROOK,   board.whiteRooks);
        processBitboard(state, weights, 0, BitBoard.QUEEN,  board.whiteQueens);
        processBitboard(state, weights, 0, BitBoard.KING,   board.whiteKing);

        // BLACK
        processBitboard(state, weights, 1, BitBoard.PAWN,   board.blackPawns);
        processBitboard(state, weights, 1, BitBoard.KNIGHT, board.blackKnights);
        processBitboard(state, weights, 1, BitBoard.BISHOP, board.blackBishops);
        processBitboard(state, weights, 1, BitBoard.ROOK,   board.blackRooks);
        processBitboard(state, weights, 1, BitBoard.QUEEN,  board.blackQueens);
        processBitboard(state, weights, 1, BitBoard.KING,   board.blackKing);

    }

}
