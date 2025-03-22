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

        MoveList moves = board.getLegalMoves();
        moves.sort((m1, m2) -> m2.getSeeScore() - m1.getSeeScore());
        System.out.println(moves);

        for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {
            long startTime = System.currentTimeMillis();
            MoveValue result = alphaBeta(board, currentDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn);
            long endTime = System.currentTimeMillis();
            bestMove = result.bestMove;

            System.out.println("info depth " + currentDepth + " score cp " + result.value + " nodes " + nodes + " time " + (endTime - startTime) + " pv " + bestMove);
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

    public MoveValue quiescenceSearch(BitBoard board, int alpha, int beta, boolean maximizingPlayer) {
        int eval = evaluate(board);
    
        if (eval >= beta) {
            return new MoveValue(null, beta);
        }
        if (eval > alpha) {
            alpha = eval;
        }
    
        MoveList captureMoves = board.getCaptureMoves();
        captureMoves.sort((m1, m2) -> m2.getSeeScore() - m1.getSeeScore()); 
    
        for (Move move : captureMoves) {
            board.makeMove(move);
            int value = quiescenceSearch(board, alpha, beta, !maximizingPlayer).value;
            board.undoMove();
    
            if (maximizingPlayer) {
                if (value > alpha) {
                    alpha = value;
                }
            } else {
                if (value < beta) {
                    beta = value;
                }
            }
    
            if (alpha >= beta) {
                break;
            }
        }
    
        return new MoveValue(null, alpha);
    }
    
    

    @Override
    public int evaluate(BitBoard board) {
        return board.currentEvaluation;
    }

    public int mobility(BitBoard board) {
        int mobility = 0;
        MoveList moves = board.getPseudoLegalMoves();
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            if (move.isCapture()) {
                mobility += 2;
            } else {
                mobility += 1;
            }
        }
        return mobility;
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
