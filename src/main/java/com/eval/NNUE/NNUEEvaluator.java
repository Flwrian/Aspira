package com.eval.NNUE;

import java.util.Arrays;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.PackedMove;

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

    public static void nnueAdd(
        NNUEState state,
        NNUEWeights weights,
        int color01,
        int pieceType,
        int square0to63
    ) {
        int f = FeatureMapper.featureIndex(color01, pieceType, square0to63);
        short[] row = weights.w1[f];

        for (int i = 0; i < weights.hidden; i++) {
            state.acc[i] += row[i];
        }
    }

    public static void nnueRemove(
        NNUEState state,
        NNUEWeights weights,
        int color01,
        int pieceType,
        int square0to63
    ) {
        int f = FeatureMapper.featureIndex(color01, pieceType, square0to63);
        short[] row = weights.w1[f];

        for (int i = 0; i < weights.hidden; i++) {
            state.acc[i] -= row[i];
        }
    }

    public static void nnueApplyQuietMove(
        NNUEState s,
        NNUEWeights w,
        long move,
        boolean whiteTurn
    ) {
        int color = whiteTurn ? 0 : 1;

        int from = PackedMove.getFrom(move);
        int to   = PackedMove.getTo(move);
        int pieceType = PackedMove.getPieceFrom(move);

        NNUEEvaluator.nnueRemove(s, w, color, pieceType, from);
        NNUEEvaluator.nnueAdd(s, w, color, pieceType, to);
    }

    static void nnueApplyCapture(
        NNUEState s,
        NNUEWeights w,
        long move,
        boolean whiteTurn
    ) {
        int color = whiteTurn ? 0 : 1;
        int enemy = 1 - color;

        int from = PackedMove.getFrom(move);
        int to   = PackedMove.getTo(move);

        int movedType   = PackedMove.getPieceFrom(move);
        int capturedType = PackedMove.getCaptured(move);

        // remove captured piece
        NNUEEvaluator.nnueRemove(s, w, enemy, capturedType, to);

        // move piece
        NNUEEvaluator.nnueRemove(s, w, color, movedType, from);
        NNUEEvaluator.nnueAdd(s, w, color, movedType, to);
    }

    static void nnueApplyPromotion(
        NNUEState s,
        NNUEWeights w,
        long move,
        boolean whiteTurn
    ) {
        int color = whiteTurn ? 0 : 1;
        int enemy = 1 - color;

        int from = PackedMove.getFrom(move);
        int to   = PackedMove.getTo(move);

        int promoType = PackedMove.getPromotion(move);
        int flags = PackedMove.getFlags(move);

        // capture?
        if ((flags & Move.CAPTURE) != 0) {
            int capturedType = PackedMove.getCaptured(move);
            NNUEEvaluator.nnueRemove(s, w, enemy, capturedType, to);
        }

        // remove pawn
        NNUEEvaluator.nnueRemove(s, w, color, BitBoard.PAWN, from);

        // add promoted piece
        NNUEEvaluator.nnueAdd(s, w, color, promoType, to);
    }

    static void nnueApplyEnPassant(
        NNUEState s,
        NNUEWeights w,
        long move,
        boolean whiteTurn
    ) {
        int color = whiteTurn ? 0 : 1;
        int enemy = 1 - color;

        int from = PackedMove.getFrom(move);
        int to   = PackedMove.getTo(move);

        int capturedSq = whiteTurn ? (to - 8) : (to + 8);

        // remove captured pawn
        NNUEEvaluator.nnueRemove(s, w, enemy, BitBoard.PAWN, capturedSq);

        // move pawn
        NNUEEvaluator.nnueRemove(s, w, color, BitBoard.PAWN, from);
        NNUEEvaluator.nnueAdd(s, w, color, BitBoard.PAWN, to);
    }

    static void nnueApplyCastle(
        NNUEState s,
        NNUEWeights w,
        long move,
        boolean whiteTurn
    ) {
        int color = whiteTurn ? 0 : 1;

        int kingFrom = PackedMove.getFrom(move);
        int kingTo   = PackedMove.getTo(move);

        // remove king
        NNUEEvaluator.nnueRemove(s, w, color, BitBoard.KING, kingFrom);
        NNUEEvaluator.nnueAdd(s, w, color, BitBoard.KING, kingTo);

        // rook move
        if (whiteTurn) {
            if (kingTo == 6) { // O-O
                NNUEEvaluator.nnueRemove(s, w, color, BitBoard.ROOK, 7);
                NNUEEvaluator.nnueAdd(s, w, color, BitBoard.ROOK, 5);
            } else { // O-O-O
                NNUEEvaluator.nnueRemove(s, w, color, BitBoard.ROOK, 0);
                NNUEEvaluator.nnueAdd(s, w, color, BitBoard.ROOK, 3);
            }
        } else {
            if (kingTo == 62) { // O-O
                NNUEEvaluator.nnueRemove(s, w, color, BitBoard.ROOK, 63);
                NNUEEvaluator.nnueAdd(s, w, color, BitBoard.ROOK, 61);
            } else { // O-O-O
                NNUEEvaluator.nnueRemove(s, w, color, BitBoard.ROOK, 56);
                NNUEEvaluator.nnueAdd(s, w, color, BitBoard.ROOK, 59);
            }
        }
    }

    static void nnueApplyMove(
        NNUEState s,
        NNUEWeights w,
        long move,
        boolean whiteTurn
    ) {
        int flags = PackedMove.getFlags(move);

        if ((flags & Move.PROMOTION) != 0) {
            nnueApplyPromotion(s, w, move, whiteTurn);
        } else if ((flags & Move.EN_PASSENT) != 0) {
            nnueApplyEnPassant(s, w, move, whiteTurn);
        } else if ((flags & Move.CASTLING) != 0) {
            nnueApplyCastle(s, w, move, whiteTurn);
        } else if ((flags & Move.CAPTURE) != 0) {
            nnueApplyCapture(s, w, move, whiteTurn);
        } else {
            nnueApplyQuietMove(s, w, move, whiteTurn);
        }
    }






    public static void main(String[] args) {
        NNUEState s = new NNUEState(256);
        NNUEWeights w = new NNUEWeights(256);
        // Initialize weights to some values
        for (int i = 0; i < FeatureMapper.FEATURES; i++) {
            for (int j = 0; j < w.hidden; j++) {
                w.w1[i][j] = (short)(i + j);
            }
        }

        int square = 12;
        int color = 0;
        int piece = BitBoard.KNIGHT;

        nnueAdd(s, w, color, piece, square);
        int[] snapshot = s.acc.clone();
        nnueRemove(s, w, color, piece, square);

        assert Arrays.equals(s.acc, new int[256]);
    }
}
