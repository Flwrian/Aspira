package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.MoveList;

public class MaterialAlgorithm implements ChessAlgorithm {

    int materialValue = 0;
    int depth = 3;

    private int getPieceValue(int piece) {
        switch (piece) {
            case 1: return 100;  // White Pawn
            case 2: return 320;  // White Knight
            case 3: return 330;  // White Bishop
            case 4: return 500;  // White Rook
            case 5: return 900;  // White Queen
            case 6: return 20000; // White King
            case 7: return -100; // Black Pawn
            case 8: return -320; // Black Knight
            case 9: return -330; // Black Bishop
            case 10: return -500; // Black Rook
            case 11: return -900; // Black Queen
            case 12: return -20000; // Black King
            default: return 0;
        }
    }

    public MaterialAlgorithm(int depth) {
        this.depth = depth;
    }

    @Override
    public Move search(BitBoard board) {
        // Simple search algorithm using a minimax with alpha-beta pruning for depth 3
        return minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn).bestMove;
    }

    @Override
    public int evaluate(BitBoard board) {
        materialValue = 0;
        for (int i = 0; i < 64; i++) {
            int piece = board.getPiece(i);
            if (piece != 0) {
                materialValue += getPieceValue(piece);
            }
        }
        return materialValue;
    }

    @Override
    public String getName() {
        return "Material";
    }

    // Minimax search algorithm with alpha-beta pruning
    private MoveValue minimax(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0) {
            return new MoveValue(null, evaluate(board));
        }

        MoveList moveList = board.getLegalMoves();
        Move bestMove = null;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moveList) {
                board.makeMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, false).value;
                board.undoMove();
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Beta cut-off
                }
            }
            return new MoveValue(bestMove, maxEval);
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moveList) {
                board.makeMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, true).value;
                board.undoMove();
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha cut-off
                }
            }
            return new MoveValue(bestMove, minEval);
        }
    }

    // Helper class to store a move and its evaluation value
    private class MoveValue {
        Move bestMove;
        int value;

        MoveValue(Move bestMove, int value) {
            this.bestMove = bestMove;
            this.value = value;
        }
    }
}
