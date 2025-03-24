package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.MoveList;
import com.bitboard.PackedMove;
import com.bitboard.PackedMoveList;

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
        long bestPackedMove = 0L;

        // Get initial legal moves (optional)
        PackedMoveList rootMoves = board.getLegalMoves();
        rootMoves.sortByScoreDescending();

        // System.out.println("Root Moves:");
        // for (int i = 0; i < rootMoves.size(); i++) {
        //     Move printable = PackedMove.unpack(rootMoves.get(i));
        //     System.out.println("\t" + printable + " (score: " + PackedMove.getScore(rootMoves.get(i)) + ")");
        // }

        for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {
            long startTime = System.currentTimeMillis();

            MoveValue result = alphaBeta(board, currentDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn);
            long endTime = System.currentTimeMillis();

            bestPackedMove = result.move;

            Move bestPrintableMove = PackedMove.unpack(bestPackedMove);
            System.out.println("info depth " + currentDepth + " score cp " + result.value + " nodes " + nodes + " time " + (endTime - startTime) + " pv " + bestPrintableMove);
        }

        // Return as a Move object for UCI or GUI integration
        return PackedMove.unpack(bestPackedMove);
    }


    public MoveValue alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        nodes++;

        if (depth == 0) {
            return new MoveValue(0L, evaluate(board)); // 0L = null move
        }

        PackedMoveList moves = board.getLegalMoves();
        moves.sortByScoreDescending();

        long bestMove = 0L;
        int bestValue = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < moves.size(); i++) {
            long move = moves.get(i);

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
                break; // alpha-beta pruning
            }
        }

        return new MoveValue(bestMove, bestValue);
    }

    
    

    @Override
    public int evaluate(BitBoard board) {
        return board.currentEvaluation;
    }

    @Override
    public String getName() {
        return "NewChessAlgorithm";
    }

    public class MoveValue {
        public final long move; // packed move
        public final int value;
    
        public MoveValue(long move, int value) {
            this.move = move;
            this.value = value;
        }
    }
    
}
