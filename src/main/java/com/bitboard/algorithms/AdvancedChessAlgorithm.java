package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.MoveList;

public class AdvancedChessAlgorithm implements ChessAlgorithm {

    int depth = 1;

    // Piece-square tables for evaluation (higher values for better squares)
    private static final int[] PAWN_TABLE = {
        0, 0, 0, 0, 0, 0, 0, 0,
        50, 50, 50, 50, 50, 50, 50, 50,
        10, 10, 20, 30, 30, 20, 10, 10,
        5, 5, 10, 25, 25, 10, 5, 5,
        0, 0, 0, 20, 20, 0, 0, 0,
        5, -5, -10, 0, 0, -10, -5, 5,
        5, 10, 10, -20, -20, 10, 10, 5,
        0, 0, 0, 0, 0, 0, 0, 0
    };
    private static final int[] KNIGHT_TABLE = {
        -50, -40, -30, -30, -30, -30, -40, -50,
        -40, -20, 0, 5, 5, 0, -20, -40,
        -30, 5, 10, 15, 15, 10, 5, -30,
        -30, 0, 15, 20, 20, 15, 0, -30,
        -30, 5, 15, 20, 20, 15, 5, -30,
        -30, -10, 10, 15, 15, 10, -10, -30,
        -40, -20, 0, 0, 0, 0, -20, -40,
        -50, -40, -30, -30, -30, -30, -40, -50
    };
    private static final int[] BISHOP_TABLE = {
        -20, -10, -10, -10, -10, -10, -10, -20,
        -10, 5, 0, 0, 0, 0, 5, -10,
        -10, 10, 10, 10, 10, 10, 10, -10,
        -10, 0, 10, 10, 10, 10, 0, -10,
        -10, 5, 5, 10, 10, 5, 5, -10,
        -10, 0, 5, 10, 10, 5, 0, -10,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -20, -10, -10, -10, -10, -10, -10, -20
    };
    private static final int[] ROOK_TABLE = {
        0, 0, 0, 5, 5, 0, 0, 0,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 10, 10, 10, 10, 10, 10, -5,
        0, 0, 0, 0, 0, 0, 0, 0
    };
    private static final int[] QUEEN_TABLE = {
        -20, -10, -10, -5, -5, -10, -10, -20,
        -10, 0, 5, 5, 5, 5, 0, -10,
        -10, 0, 5, 5, 5, 5, 0, -10,
        -5, 0, 5, 5, 5, 5, 0, -5,
        0, 0, 5, 5, 5, 5, 0, -5,
        -10, 5, 5, 5, 5, 5, 0, -10,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -20, -10, -10, -5, -5, -10, -10, -20
    };
    private static final int[] KING_MIDDLE_GAME_TABLE = {
        20, 30, 10, 0, 0, 10, 30, 20,
        20, 20, 0, 0, 0, 0, 20, 20,
        -10, -20, -20, -20, -20, -20, -20, -10,
        -20, -30, -30, -40, -40, -30, -30, -20,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30
    };
    private static final int[] KING_END_GAME_TABLE = {
        -50, -30, -30, -30, -30, -30, -30, -50,
        -30, -30, 0, 0, 0, 0, -30, -30,
        -30, -10, 20, 30, 30, 20, -10, -30,
        -30, -10, 30, 40, 40, 30, -10, -30,
        -30, -10, 30, 40, 40, 30, -10, -30,
        -30, -10, 20, 30, 30, 20, -10, -30,
        -30, -20, -10, -20, -20, -10, -20, -30,
        -50, -40, -30, -20, -20, -30, -40, -50
    };
    

    private int getPieceValue(int piece, int square) {
        int baseValue;
        switch (piece) {
            case 1: baseValue = 100;  // White Pawn
                break;
            case 2: baseValue = 320;  // White Knight
                break;
            case 3: baseValue = 330;  // White Bishop
                break;
            case 4: baseValue = 500;  // White Rook
                break;
            case 5: baseValue = 900;  // White Queen
                break;
            case 6: baseValue = 20000; // White King
                break;
            case 7: baseValue = -100; // Black Pawn
                break;
            case 8: baseValue = -320; // Black Knight
                break;
            case 9: baseValue = -330; // Black Bishop
                break;
            case 10: baseValue = -500; // Black Rook
                break;
            case 11: baseValue = -900; // Black Queen
                break;
            case 12: baseValue = -20000; // Black King
                break;
            default: return 0;
        }

        
        switch (piece) {
            case 1: return baseValue + PAWN_TABLE[square];
            case 2: return baseValue + KNIGHT_TABLE[square];
            case 3: return baseValue + BISHOP_TABLE[square];
            case 4: return baseValue + ROOK_TABLE[square];
            case 5: return baseValue + QUEEN_TABLE[square];
            case 6: return baseValue + (baseValue > 20000 ? KING_MIDDLE_GAME_TABLE[square] : KING_END_GAME_TABLE[square]);
            case 7: return baseValue - PAWN_TABLE[square];
            case 8: return baseValue - KNIGHT_TABLE[square];
            case 9: return baseValue - BISHOP_TABLE[square];
            case 10: return baseValue - ROOK_TABLE[square];
            case 11: return baseValue - QUEEN_TABLE[square];
            case 12: return baseValue - (baseValue > 20000 ? KING_MIDDLE_GAME_TABLE[square] : KING_END_GAME_TABLE[square]);
            default: return 0;
        }
    }

    public AdvancedChessAlgorithm(int depth) {
        this.depth = depth;
    }

    @Override
    public Move search(BitBoard board) {
        MoveValue move = minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn);
        System.out.println("Eval: " + move.value);
        return move.bestMove;
    }

    @Override
    public int evaluate(BitBoard board) {
        // if (board.isCheckMate()) {
        //     return board.whiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        // }
        // if (board.isStaleMate()) {
        //     return 0;
        // }
        // evaluate position based on material and piece-square tables
        int materialValue = 0;
        
        long pawns = board.getWhitePawns();
        long knights = board.getWhiteKnights();
        long bishops = board.getWhiteBishops();
        long rooks = board.getWhiteRooks();
        long queens = board.getWhiteQueens();
        long kings = board.getWhiteKing();

        // white pst
        while (pawns != 0) {
            long pawn = BitBoard.getLSB(pawns);
            pawns &= pawns - 1;
    
            int from = BitBoard.getSquare(pawn);
            materialValue += getPieceValue(1, from);
        }

        while (knights != 0) {
            long knight = BitBoard.getLSB(knights);
            knights &= knights - 1;
    
            int from = BitBoard.getSquare(knight);
            materialValue += getPieceValue(2, from);
        }

        while (bishops != 0) {
            long bishop = BitBoard.getLSB(bishops);
            bishops &= bishops - 1;
    
            int from = BitBoard.getSquare(bishop);
            materialValue += getPieceValue(3, from);
        }

        while (rooks != 0) {
            long rook = BitBoard.getLSB(rooks);
            rooks &= rooks - 1;
    
            int from = BitBoard.getSquare(rook);
            materialValue += getPieceValue(4, from);
        }

        while (queens != 0) {
            long queen = BitBoard.getLSB(queens);
            queens &= queens - 1;
    
            int from = BitBoard.getSquare(queen);
            materialValue += getPieceValue(5, from);
        }

        while (kings != 0) {
            long king = BitBoard.getLSB(kings);
            kings &= kings - 1;
    
            int from = BitBoard.getSquare(king);
            materialValue += getPieceValue(6, from);
        }

        long blackPawns = board.getBlackPawns();
        long blackKnights = board.getBlackKnights();
        long blackBishops = board.getBlackBishops();
        long blackRooks = board.getBlackRooks();
        long blackQueens = board.getBlackQueens();
        long blackKings = board.getBlackKing();

        // black pst
        while (blackPawns != 0) {
            long pawn = BitBoard.getLSB(blackPawns);
            blackPawns &= blackPawns - 1;
    
            int from = BitBoard.getSquare(pawn);
            materialValue += getPieceValue(7, from);
        }

        while (blackKnights != 0) {
            long knight = BitBoard.getLSB(blackKnights);
            blackKnights &= blackKnights - 1;
    
            int from = BitBoard.getSquare(knight);
            materialValue += getPieceValue(8, from);
        }

        while (blackBishops != 0) {
            long bishop = BitBoard.getLSB(blackBishops);
            blackBishops &= blackBishops - 1;
    
            int from = BitBoard.getSquare(bishop);
            materialValue += getPieceValue(9, from);
        }

        while (blackRooks != 0) {
            long rook = BitBoard.getLSB(blackRooks);
            blackRooks &= blackRooks - 1;
    
            int from = BitBoard.getSquare(rook);
            materialValue += getPieceValue(10, from);
        }

        while (blackQueens != 0) {
            long queen = BitBoard.getLSB(blackQueens);
            blackQueens &= blackQueens - 1;
    
            int from = BitBoard.getSquare(queen);
            materialValue += getPieceValue(11, from);
        }

        while (blackKings != 0) {
            long king = BitBoard.getLSB(blackKings);
            blackKings &= blackKings - 1;
    
            int from = BitBoard.getSquare(king);
            materialValue += getPieceValue(12, from);
        }

        return materialValue;

        
    }

    @Override
    public String getName() {
        return "Material with Piece-Square Tables";
    }

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

    private class MoveValue {
        Move bestMove;
        int value;

        MoveValue(Move bestMove, int value) {
            this.bestMove = bestMove;
            this.value = value;
        }
    }
}
