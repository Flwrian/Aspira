package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.MoveList;

import java.util.ArrayList;
import java.util.List;

/**
 * New optimized chess algorithm.
 */
public class NewChessAlgorithm implements ChessAlgorithm {

    private int depth;
    private TranspositionTable transpositionTable = new TranspositionTable();
    private int nodes;

    public NewChessAlgorithm(int depth) {
        this.depth = depth;
    }

    @Override
    public Move search(BitBoard board, int wtime, int btime, int winc, int binc, int movestogo, int depth) {
        nodes = 0;
        Move bestMove = null;

        for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {
            MoveValue result = alphaBeta(board, currentDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn);
            bestMove = result.bestMove;

            System.out.println("info depth " + currentDepth + " score cp " + result.value + " nodes " + nodes);
        }
        return bestMove;
    }

    public MoveValue alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        nodes++;

        if (depth == 0) {
            return new MoveValue(null, evaluate(board));
        }

        MoveList moves = board.getLegalMoves();
        moves.sort((m1, m2) -> m2.getSeeScore() - m1.getSeeScore());

        Move bestMove = null;
        int bestValue = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : moves) {
            board.makeMove(move);
            int value = alphaBeta(board, depth - 1, alpha, beta, !maximizingPlayer).value;
            board.undoMove();

            if (maximizingPlayer) {
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
                alpha = Math.max(alpha, value);
            } else {
                if (value < bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
                beta = Math.min(beta, value);
            }

            if (alpha >= beta) {
                break;
            }
        }

        return new MoveValue(bestMove, bestValue);
    }

    @Override
    public int evaluate(BitBoard board) {
        return getMaterialValue(board, true) - getMaterialValue(board, false);
    }

    private int getMaterialValue(BitBoard board, boolean color) {
        int value = 0;
        long pawns = color ? board.getWhitePawns() : board.getBlackPawns();
        long knights = color ? board.getWhiteKnights() : board.getBlackKnights();
        long bishops = color ? board.getWhiteBishops() : board.getBlackBishops();
        long rooks = color ? board.getWhiteRooks() : board.getBlackRooks();
        long queens = color ? board.getWhiteQueens() : board.getBlackQueens();

        while (pawns != 0) { pawns &= pawns - 1; value += 100; }
        while (knights != 0) { knights &= knights - 1; value += 320; }
        while (bishops != 0) { bishops &= bishops - 1; value += 330; }
        while (rooks != 0) { rooks &= rooks - 1; value += 500; }
        while (queens != 0) { queens &= queens - 1; value += 900; }

        return value;
    }

    @Override
    public String getName() {
        return "NewChessAlgorithm";
    }

    private class MoveValue {
        Move bestMove;
        int value;

        public MoveValue(Move bestMove, int value) {
            this.bestMove = bestMove;
            this.value = value;
        }
    }
}
