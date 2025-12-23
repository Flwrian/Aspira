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
        System.out.println("NNUE Add: color=" + color01 + " pieceType=" + pieceType + " square=" + square0to63);
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
        System.out.println("NNUE Remove: color=" + color01 + " pieceType=" + pieceType + " square=" + square0to63);
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

    public static void nnueApplyCapture(
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

    public static void nnueApplyPromotion(
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
        if (flags == Move.CAPTURE) {
            int capturedType = PackedMove.getCaptured(move);
            NNUEEvaluator.nnueRemove(s, w, enemy, capturedType, to);
        }

        // remove pawn
        NNUEEvaluator.nnueRemove(s, w, color, BitBoard.PAWN, from);

        // add promoted piece
        NNUEEvaluator.nnueAdd(s, w, color, promoType, to);
    }

    public static void nnueApplyEnPassant(
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

    public static void nnueApplyCastle(
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

    public static void nnueApplyMove(
        NNUEState s,
        NNUEWeights w,
        long move,
        boolean whiteTurn
    ) {
        int flags = PackedMove.getFlags(move);


        if (PackedMove.isCapture(move)) {
            nnueApplyCapture(s, w, move, whiteTurn);
        } else if (flags == Move.EN_PASSENT) {
            nnueApplyEnPassant(s, w, move, whiteTurn);
        } else if (flags == Move.CASTLING) {
            nnueApplyCastle(s, w, move, whiteTurn);
        } else if (flags == Move.PROMOTION) {
            nnueApplyPromotion(s, w, move, whiteTurn);
        } else {
            nnueApplyQuietMove(s, w, move, whiteTurn);
        }
    }

    static int crelu(int x) {
        if (x < 0) return 0;
        if (x > 127) return 127;
        return x;
    }

    public static int evaluate(
        NNUEState s,
        NNUEWeights w,
        boolean whiteToMove
    ) {
        int sum = 0;

        for (int i = 0; i < w.hidden; i++) {
            sum += crelu(s.acc[i]) * w.w2[i];
        }

        sum += w.b2;

        // convention side-to-move
        return whiteToMove ? sum : -sum;
    }


    public static void main(String[] args) {
        
        for(int i=1; i<=12; i++) {
            System.out.println((i - 1) % 6);
        }

    }
}
