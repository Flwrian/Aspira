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

    public Move search(BitBoard board) {
        MoveValue result = negamax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn, 0);
        // System.out.println("info depth " + depth + " score cp " + result.value + " pv " + result.bestMove);
        return result.bestMove;
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

    // Négamax avec élagage alpha-beta
    private MoveValue negamax(BitBoard board, int depth, int alpha, int beta, boolean isWhiteTurn, int ply) {
        if (depth == 0) {
            return new MoveValue(null, evaluate(board) * (isWhiteTurn ? 1 : -1));
        }

        MoveList moveList = board.getLegalMoves();
        if (moveList.size() == 0) {
            // Pas de coups légaux, vérifier si c'est un mat ou une pat
            if (board.isKingInCheck(isWhiteTurn)) {
                return new MoveValue(null, -49000 + ply); // Mat
            } else {
                return new MoveValue(null, 0); // Pat
            }
        }

        Move bestMove = null;
        int maxEval = Integer.MIN_VALUE;

        for (Move move : moveList) {
            board.makeMove(move);
            int eval = -negamax(board, depth - 1, -beta, -alpha, !isWhiteTurn, ply + 1).value;
            board.undoMove();

            if (eval > maxEval) {
                maxEval = eval;
                bestMove = move;
            }

            alpha = Math.max(alpha, eval);
            if (alpha >= beta) {
                break; // Coupure beta
            }
        }

        return new MoveValue(bestMove, maxEval);
    }

    // Classe helper pour stocker un coup et sa valeur d'évaluation
    private class MoveValue {
        Move bestMove;
        int value;

        MoveValue(Move bestMove, int value) {
            this.bestMove = bestMove;
            this.value = value;
        }
    }

    @Override
    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public void setRazorDepth(int depth) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRazorDepth'");
    }

    @Override
    public void setNPM(int npm) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setNPM'");
    }

    @Override
    public int getRazorDepth() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRazorDepth'");
    }

    @Override
    public int getNPM() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNPM'");
    }

    @Override
    public Move search(BitBoard board, int wtime, int btime, int winc, int binc, int movestogo, int depth) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'search'");
    }
}
