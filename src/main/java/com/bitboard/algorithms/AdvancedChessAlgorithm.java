package com.bitboard.algorithms;

import java.util.LinkedList;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.MoveList;

public class AdvancedChessAlgorithm implements ChessAlgorithm {

    int depth = 1;
    private Move lastMove = null;
    private Move secondLastMove = null;

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

    // pawn endgame table
    private static final int[] PAWN_ENDGAME_TABLE = {
        0, 0, 0, 0, 0, 0, 0, 0,
        -10, -10, -10, -10, -10, -10, -10, -10,
        -5, -5, -5, -5, -5, -5, -5, -5,
        0, 0, 0, 0, 0, 0, 0, 0,
        20, 20, 20, 20, 20, 20, 20, 20,
        40, 40, 40, 40, 40, 40, 40, 40,
        100, 100, 100, 100, 100, 100, 100, 100,
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
            -30, -20, -10, 0, 0, -10, -20, -30,
            -50, -40, -30, -20, -20, -30, -40, -50
    };

    // pst for rewarding moves that put the opponent in corners
    private static final int[] KING_CHECKMATE = {
        -150, -100, -100, -100, -100, -100, -100, -150,
            -100, -50, -25, -25, -25, -25, -25, -50,
            -100, -25, 20, 30, 30, 20, -25, -100,
            -100, -25, 30, 40, 40, 30, -25, -100,
            -100, -25, 30, 40, 40, 30, -25, -100,
            -100, -25, 20, 30, 30, 20, -25, -100,
            -100, -50, -25, -25, -25, -25, -50, -100,
            -150, -100, -100, -100, -100, -100, -100, -150
    };

    private int getBoardValue(BitBoard board) {
        int value = 0;
        long pawns = board.getWhitePawns();
        long knights = board.getWhiteKnights();
        long bishops = board.getWhiteBishops();
        long rooks = board.getWhiteRooks();
        long queens = board.getWhiteQueens();
        long kings = board.getWhiteKing();

        while (pawns != 0) {
            long pawn = BitBoard.getLSB(pawns);
            pawns &= pawns - 1;
            value += 100;
        }

        while (knights != 0) {
            long knight = BitBoard.getLSB(knights);
            knights &= knights - 1;
            value += 320;
        }

        while (bishops != 0) {
            long bishop = BitBoard.getLSB(bishops);
            bishops &= bishops - 1;
            value += 330;
        }

        while (rooks != 0) {
            long rook = BitBoard.getLSB(rooks);
            rooks &= rooks - 1;
            value += 500;
        }

        while (queens != 0) {
            long queen = BitBoard.getLSB(queens);
            queens &= queens - 1;
            value += 900;
        }

        while (kings != 0) {
            long king = BitBoard.getLSB(kings);
            kings &= kings - 1;
            value += 20000;
        }

        long blackPawns = board.getBlackPawns();
        long blackKnights = board.getBlackKnights();
        long blackBishops = board.getBlackBishops();
        long blackRooks = board.getBlackRooks();
        long blackQueens = board.getBlackQueens();
        long blackKings = board.getBlackKing();

        while (blackPawns != 0) {
            long pawn = BitBoard.getLSB(blackPawns);
            blackPawns &= blackPawns - 1;
            value -= 100;
        }

        while (blackKnights != 0) {
            long knight = BitBoard.getLSB(blackKnights);
            blackKnights &= blackKnights - 1;
            value -= 320;
        }

        while (blackBishops != 0) {
            long bishop = BitBoard.getLSB(blackBishops);
            blackBishops &= blackBishops - 1;
            value -= 330;
        }

        while (blackRooks != 0) {
            long rook = BitBoard.getLSB(blackRooks);
            blackRooks &= blackRooks - 1;
            value -= 500;
        }

        while (blackQueens != 0) {
            long queen = BitBoard.getLSB(blackQueens);
            blackQueens &= blackQueens - 1;
            value -= 900;
        }

        while (blackKings != 0) {
            long king = BitBoard.getLSB(blackKings);
            blackKings &= blackKings - 1;
            value -= 20000;
        }

        return value;
    }

    // get board value for white
    private int getBoardValueWhite(BitBoard board) {
        int value = 0;
        long pawns = board.getWhitePawns();
        long knights = board.getWhiteKnights();
        long bishops = board.getWhiteBishops();
        long rooks = board.getWhiteRooks();
        long queens = board.getWhiteQueens();
        long kings = board.getWhiteKing();

        while (pawns != 0) {
            long pawn = BitBoard.getLSB(pawns);
            pawns &= pawns - 1;
            value += 100;
        }

        while (knights != 0) {
            long knight = BitBoard.getLSB(knights);
            knights &= knights - 1;
            value += 320;
        }

        while (bishops != 0) {
            long bishop = BitBoard.getLSB(bishops);
            bishops &= bishops - 1;
            value += 330;
        }

        while (rooks != 0) {
            long rook = BitBoard.getLSB(rooks);
            rooks &= rooks - 1;
            value += 500;
        }

        while (queens != 0) {
            long queen = BitBoard.getLSB(queens);
            queens &= queens - 1;
            value += 900;
        }

        while (kings != 0) {
            long king = BitBoard.getLSB(kings);
            kings &= kings - 1;
            value += 20000;
        }

        return value;
    }

    // get board value for black
    private int getBoardValueBlack(BitBoard board) {
        int value = 0;
        long pawns = board.getBlackPawns();
        long knights = board.getBlackKnights();
        long bishops = board.getBlackBishops();
        long rooks = board.getBlackRooks();
        long queens = board.getBlackQueens();
        long kings = board.getBlackKing();

        while (pawns != 0) {
            long pawn = BitBoard.getLSB(pawns);
            pawns &= pawns - 1;
            value += 100;
        }

        while (knights != 0) {
            long knight = BitBoard.getLSB(knights);
            knights &= knights - 1;
            value += 320;
        }

        while (bishops != 0) {
            long bishop = BitBoard.getLSB(bishops);
            bishops &= bishops - 1;
            value += 330;
        }

        while (rooks != 0) {
            long rook = BitBoard.getLSB(rooks);
            rooks &= rooks - 1;
            value += 500;
        }

        while (queens != 0) {
            long queen = BitBoard.getLSB(queens);
            queens &= queens - 1;
            value += 900;
        }

        while (kings != 0) {
            long king = BitBoard.getLSB(kings);
            kings &= kings - 1;
            value += 20000;
        }

        return value;
    }
    

    private int getPieceValue(int piece, int square, BitBoard board) {
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
            case 1:
                // To determine if it is end game, we check the total material value for white
                int whiteValue = getBoardValueWhite(board);
                boolean isEndGame = whiteValue < 21100;
                // if is end game, we check if black is better so we can use the table that reward moves that put the opponent in corners
                if (isEndGame && board.whiteTurn == false) {
                    int blackValue = getBoardValueBlack(board);
                    if (blackValue > whiteValue + 600) {
                        // black is better so we use the table that reward moves that put the opponent in corners
                        return baseValue + PAWN_ENDGAME_TABLE[square];
                    }
                }

                return baseValue + (isEndGame ? PAWN_ENDGAME_TABLE[square] : PAWN_TABLE[square]);
            case 2: return baseValue + KNIGHT_TABLE[square];
            case 3: return baseValue + BISHOP_TABLE[square];
            case 4: return baseValue + ROOK_TABLE[square];
            case 5: return baseValue + QUEEN_TABLE[square];
            case 6: 
                // To determine if it is end game, we check the total material value for white
                whiteValue = getBoardValueWhite(board);
                isEndGame = whiteValue < 21100;
                // if is end game, we check if black is better so we can use the table that reward moves that put the opponent in corners
                if (isEndGame && board.whiteTurn == false) {
                    int blackValue = getBoardValueBlack(board);
                    if (blackValue > whiteValue + 600) {
                        // black is better so we use the table that reward moves that put the opponent in corners
                        return baseValue + KING_CHECKMATE[square];
                    }
                }

                return baseValue + (isEndGame ? KING_END_GAME_TABLE[square] : KING_MIDDLE_GAME_TABLE[square]);
                
            case 7:
                // To determine if it is end game, we check the total material value for black
                int blackValue = getBoardValueBlack(board);
                boolean isEndGameBlack = blackValue < 21100;
                // if is end game, we check if white is better so we can use the table that reward moves that put the opponent in corners
                if (isEndGameBlack && board.whiteTurn == true) {
                    whiteValue = getBoardValueWhite(board);
                    if (whiteValue > blackValue + 600) {
                        // white is better so we use the table that reward moves that put the opponent in corners
                        return baseValue - PAWN_ENDGAME_TABLE[square];
                    }
                }

                return baseValue - (isEndGameBlack ? PAWN_ENDGAME_TABLE[square] : PAWN_TABLE[square]);
            case 8: return baseValue - KNIGHT_TABLE[square];
            case 9: return baseValue - BISHOP_TABLE[square];
            case 10: return baseValue - ROOK_TABLE[square];
            case 11: return baseValue - QUEEN_TABLE[square];
            case 12: 
                // To determine if it is end game, we check the total material value for black
                blackValue = getBoardValueBlack(board);
                isEndGameBlack = blackValue < 21100;
                // if is end game, we check if white is better so we can use the table that reward moves that put the opponent in corners
                if (isEndGameBlack && board.whiteTurn == true) {
                    whiteValue = getBoardValueWhite(board);
                    if (whiteValue > blackValue + 600) {
                        // white is better so we use the table that reward moves that put the opponent in corners
                        return baseValue - KING_CHECKMATE[square];
                    }
                }

                return baseValue - (isEndGameBlack ? KING_END_GAME_TABLE[square] : KING_MIDDLE_GAME_TABLE[square]);
            default: return 0;
        }
    }

    public AdvancedChessAlgorithm(int depth) {
        this.depth = depth;
    }

    @Override
    public Move search(BitBoard board) {
        MoveValue move = minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn);
        // System.out.println("info depth " + depth + " score cp " + move.value + " pv " + move.bestMove);

        // Update the move history
        secondLastMove = lastMove;
        lastMove = move.bestMove;

        return move.bestMove;
    }

    @Override
    public int evaluate(BitBoard board) {
        if (board.isCheckMate()) {
            return board.whiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
        if (board.isStaleMate()) {
            // System.out.println("Stalemate!");
            // si on a l'avantage, on veut éviter le pat
            return board.whiteTurn ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
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
            materialValue += getPieceValue(1, from, board);
        }

        while (knights != 0) {
            long knight = BitBoard.getLSB(knights);
            knights &= knights - 1;
    
            int from = BitBoard.getSquare(knight);
            materialValue += getPieceValue(2, from, board);
        }

        while (bishops != 0) {
            long bishop = BitBoard.getLSB(bishops);
            bishops &= bishops - 1;
    
            int from = BitBoard.getSquare(bishop);
            materialValue += getPieceValue(3, from, board);
        }

        while (rooks != 0) {
            long rook = BitBoard.getLSB(rooks);
            rooks &= rooks - 1;
    
            int from = BitBoard.getSquare(rook);
            materialValue += getPieceValue(4, from, board);
        }

        while (queens != 0) {
            long queen = BitBoard.getLSB(queens);
            queens &= queens - 1;
    
            int from = BitBoard.getSquare(queen);
            materialValue += getPieceValue(5, from, board);
        }

        while (kings != 0) {
            long king = BitBoard.getLSB(kings);
            kings &= kings - 1;
    
            int from = BitBoard.getSquare(king);
            materialValue += getPieceValue(6, from, board);
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
            materialValue += getPieceValue(7, from, board);
        }

        while (blackKnights != 0) {
            long knight = BitBoard.getLSB(blackKnights);
            blackKnights &= blackKnights - 1;
    
            int from = BitBoard.getSquare(knight);
            materialValue += getPieceValue(8, from, board);
        }

        while (blackBishops != 0) {
            long bishop = BitBoard.getLSB(blackBishops);
            blackBishops &= blackBishops - 1;
    
            int from = BitBoard.getSquare(bishop);
            materialValue += getPieceValue(9, from, board);
        }

        while (blackRooks != 0) {
            long rook = BitBoard.getLSB(blackRooks);
            blackRooks &= blackRooks - 1;
    
            int from = BitBoard.getSquare(rook);
            materialValue += getPieceValue(10, from, board);
        }

        while (blackQueens != 0) {
            long queen = BitBoard.getLSB(blackQueens);
            blackQueens &= blackQueens - 1;
    
            int from = BitBoard.getSquare(queen);
            materialValue += getPieceValue(11, from, board);
        }

        while (blackKings != 0) {
            long king = BitBoard.getLSB(blackKings);
            blackKings &= blackKings - 1;
    
            int from = BitBoard.getSquare(king);
            materialValue += getPieceValue(12, from, board);
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
    
        // move ordering
        MoveList moveList = board.getLegalMoves();
        moveList.sort((m1, m2) -> {
            if (board.isCaptureMove(m1) && !board.isCaptureMove(m2)) {
                return -1;
            }
            if (!board.isCaptureMove(m1) && board.isCaptureMove(m2)) {
                return 1;
            }
            return 0;
        });
    
        // Gestion des situations où il n'y a pas de coups légaux
        if (moveList.size() == 0) {
            if (board.isKingInCheck(maximizingPlayer)) {
                return new MoveValue(null, maximizingPlayer ? -49000 + (depth) : 49000 - (depth)); // Échec et mat
            } else {
                return new MoveValue(null, 0); // Pat
            }
        }
    
        Move bestMove = null;
    
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moveList) {
                if (move.equals(lastMove) || move.equals(secondLastMove)) {
                    continue;
                }
                int eval = 0;
                board.makeMove(move);
                eval = minimax(board, depth - 1, alpha, beta, false).value;
    
                // bonus if the move is castling
                if (move.isCastle_move()) {
                    eval += 50;
                }
                // bonus if the move is check
                if (board.isKingInCheck(!maximizingPlayer)) {
                    eval += 60;
                }
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
                int eval = 0;
                board.makeMove(move);
                eval = minimax(board, depth - 1, alpha, beta, true).value;
    
                // bonus if the move is castling
                if (move.isCastle_move()) {
                    eval += 50;
                }
                // bonus if the move is check
                if (board.isKingInCheck(!maximizingPlayer)) {
                    eval += 60;
                }
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


    @Override
    public void setDepth(int depth) {
        this.depth = depth;
    }
}
