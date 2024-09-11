package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.MoveGenerator;
import com.bitboard.MoveList;

/**
 * Custom algorithm for the chess engine.
 * 
 */
public class CustomAlgorithm implements ChessAlgorithm {

    int nodes = 0;
    int depth = 0;
    int quiescenceDepth = 4;

    public void setQuiescenceDepth(int quiescenceDepth) {
        this.quiescenceDepth = quiescenceDepth;
    }

    // Piece-square tables for evaluation (higher values for better squares)
    private static final int[] PAWN_TABLE = {
        0,   0,   0,   0,   0,   0,  0,   0,
     98, 134,  61,  95,  68, 126, 34, -11,
     -6,   7,  26,  31,  65,  56, 25, -20,
    -14,  13,   6,  21,  23,  12, 17, -23,
    -27,  -2,  -5,  12,  17,   6, 10, -25,
    -26,  -4,  -4, -10,   3,   3, 33, -12,
    -35,  -1, -20, -23, -15,  24, 38, -22,
      0,   0,   0,   0,   0,   0,  0,   0,
    };

    // pawn endgame table
    private static final int[] PAWN_ENDGAME_TABLE = {
        0,   0,   0,   0,   0,   0,   0,   0,
    178, 173, 158, 134, 147, 132, 165, 187,
     94, 100,  85,  67,  56,  53,  82,  84,
     32,  24,  13,   5,  -2,   4,  17,  17,
     13,   9,  -3,  -7,  -7,  -8,   3,  -1,
      4,   7,  -6,   1,   0,  -5,  -1,  -8,
     13,   8,   8,  10,  13,   0,   2,  -7,
      0,   0,   0,   0,   0,   0,   0,   0,
    };
    private static final int[] KNIGHT_TABLE = {
        -167, -89, -34, -49,  61, -97, -15, -107,
     -73, -41,  72,  36,  23,  62,   7,  -17,
     -47,  60,  37,  65,  84, 129,  73,   44,
      -9,  17,  19,  53,  37,  69,  18,   22,
     -13,   4,  16,  13,  28,  19,  21,   -8,
     -23,  -9,  12,  10,  19,  17,  25,  -16,
     -29, -53, -12,  -3,  -1,  18, -14,  -19,
    -105, -21, -58, -33, -17, -28, -19,  -23,
    };
    private static final int[] BISHOP_TABLE = {
        -29,   4, -82, -37, -25, -42,   7,  -8,
    -26,  16, -18, -13,  30,  59,  18, -47,
    -16,  37,  43,  40,  35,  50,  37,  -2,
     -4,   5,  19,  50,  37,  37,   7,  -2,
     -6,  13,  13,  26,  34,  12,  10,   4,
      0,  15,  15,  15,  14,  27,  18,  10,
      4,  15,  16,   0,   7,  21,  33,   1,
    -33,  -3, -14, -21, -13, -12, -39, -21,
    };
    private static final int[] ROOK_TABLE = {
        32,  42,  32,  51, 63,  9,  31,  43,
     27,  32,  58,  62, 80, 67,  26,  44,
     -5,  19,  26,  36, 17, 45,  61,  16,
    -24, -11,   7,  26, 24, 35,  -8, -20,
    -36, -26, -12,  -1,  9, -7,   6, -23,
    -45, -25, -16, -17,  3,  0,  -5, -33,
    -44, -16, -20,  -9, -1, 11,  -6, -71,
    -19, -13,   1,  17, 16,  7, -37, -26,
    };
    private static final int[] QUEEN_TABLE = {
        -28,   0,  29,  12,  59,  44,  43,  45,
    -24, -39,  -5,   1, -16,  57,  28,  54,
    -13, -17,   7,   8,  29,  56,  47,  57,
    -27, -27, -16, -16,  -1,  17,  -2,   1,
     -9, -26,  -9, -10,  -2,  -4,   3,  -3,
    -14,   2, -11,  -2,  -5,   2,  14,   5,
    -35,  -8,  11,   2,   8,  15,  -3,   1,
     -1, -18,  -9,  10, -15, -25, -31, -50,
    };
    private static final int[] KING_MIDDLE_GAME_TABLE = {
        -65,  23,  16, -15, -56, -34,   2,  13,
     29,  -1, -20,  -7,  -8,  -4, -38, -29,
     -9,  24,   2, -16, -20,   6,  22, -22,
    -17, -20, -12, -27, -30, -25, -14, -36,
    -49,  -1, -27, -39, -46, -44, -33, -51,
    -14, -14, -22, -46, -44, -30, -15, -27,
      1,   7,  -8, -64, -43, -16,   9,   8,
    -15,  36,  12, -54,   8, -28,  24,  14,
    };

    private static final int[] KING_END_GAME_TABLE = {
        -74, -35, -18, -18, -11,  15,   4, -17,
    -12,  17,  14,  17,  17,  38,  23,  11,
     10,  17,  23,  15,  20,  45,  44,  13,
     -8,  22,  24,  27,  26,  33,  26,   3,
    -18,  -4,  21,  24,  27,  23,   9, -11,
    -19,  -3,  11,  21,  23,  16,   7,  -9,
    -27, -11,   4,  13,  14,   4,  -5, -17,
    -53, -34, -21, -11, -28, -14, -24, -43
    };


    public void setDepth(int depth) {
        this.depth = depth;
    }

    public CustomAlgorithm(int depth) {
        this.depth = depth;
    }

    

    @Override
    public Move search(BitBoard board) {
        nodes = 0;
        Move bestMove = null;
        long time = System.currentTimeMillis();
        

        // Iterative Deepening: Itère sur plusieurs profondeurs, sauvegarde le meilleur coup trouvé à chaque étape.
        for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {
            long time2 = System.currentTimeMillis();
            MoveValue result = minimax(board, currentDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn);
            time2 = System.currentTimeMillis() - time2;
            bestMove = result.bestMove;
            System.out.println("info depth " + currentDepth + " score cp " + result.value + " pv " + result.bestMove + " nodes " + nodes + " time " + time2);
        }

        time = System.currentTimeMillis() - time;
        System.out.println("info nodes " + nodes + " time " + time);
        return bestMove;
    }

    // Minimax avec coupure alpha-beta et recherche de quiescence
    public MoveValue minimax(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        nodes++;

        // NMP: Null Move Pruning
        if (depth >= 3 && !board.isKingInCheck(maximizingPlayer)) {
            board.makeNullMove();
            MoveValue nullMoveResult = minimax(board, depth - 3, -beta, -beta + 1, !maximizingPlayer);
            board.undoNullMove();
            if (nullMoveResult.value >= beta) {
                return new MoveValue(null, beta);
            }
        }
        
        // 
        if (depth == 0) {
            // Quiescence search
            return quiescenceSearch(board, alpha, beta, maximizingPlayer, quiescenceDepth);
        }
    
        MoveList moves = board.getLegalMoves();
        moves.sort((m1, m2) -> m2.getSeeScore() - m1.getSeeScore());
    
        
    
        // Si le joueur actuel est en échec et qu'il n'y a pas de coups légaux : échec et mat ou pat
        if (moves.size() == 0) {
            if (board.isKingInCheck(maximizingPlayer)) {
                return new MoveValue(null, -49000 + depth);  // Mat
            }
            return new MoveValue(null, 0);  // Pat
        }
    
        // Maximization ou minimization selon le joueur
        Move bestMove = null;

        // // Razoring: si la valeur de l'évaluation est inférieure à alpha - marge de razoring, on coupe
        // if (depth <= 2 && evaluate(board) < alpha - razoringMargin(depth)) {
        //     return new MoveValue(null, alpha - razoringMargin(depth));
        // }
        
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                board.makeMove(move);
                MoveValue result = minimax(board, depth - 1, alpha, beta, false);  // Prochain joueur minimise
                board.undoMove();
                
                int eval = result.value;
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }
                
                alpha = Math.max(alpha, eval);  // Mise à jour de alpha pour la coupure alpha-beta
                if (beta <= alpha) {
                    break;  // Coupure beta
                }
            }
            return new MoveValue(bestMove, maxEval);  // Retourne la meilleure valeur pour le joueur maximisant
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                board.makeMove(move);
                MoveValue result = minimax(board, depth - 1, alpha, beta, true);  // Prochain joueur maximise
                board.undoMove();
                
                int eval = result.value;
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }
                
                beta = Math.min(beta, eval);  // Mise à jour de beta pour la coupure alpha-beta
                if (beta <= alpha) {
                    break;  // Coupure alpha
                }
            }
            return new MoveValue(bestMove, minEval);  // Retourne la meilleure valeur pour le joueur minimisant
        }
    }

    // Recherche de quiescence pour limiter l'effet d'horizon
    public MoveValue quiescenceSearch(BitBoard board, int alpha, int beta, boolean maximizingPlayer, int depth) {
        nodes++;
    
        int eval = evaluate(board);
        
        // fail hard if the evaluation is outside the window
        if (eval >= beta) {
            return new MoveValue(null, beta);
        }

        // alpha increases if the evaluation is within the window
        if (eval > alpha) {
            alpha = eval;
        }

        // same as minimax but with a depth of 0
        if (depth == 0) {
            return new MoveValue(null, eval);
        }

        // Generate only captures
        MoveList moves = MoveGenerator.generateCaptureMoves(board);
        moves.sort((m1, m2) -> m2.getSeeScore() - m1.getSeeScore());

        // If the player is in check and there are no legal moves: checkmate or stalemate
        if (moves.size() == 0) {
            if (board.isKingInCheck(maximizingPlayer)) {
                return new MoveValue(null, -49000 + depth);  // Checkmate
            }
            return new MoveValue(null, 0);  // Stalemate
        }

        // Maximization or minimization according to the player
        Move bestMove = null;
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                board.makeMove(move);
                MoveValue result = quiescenceSearch(board, alpha, beta, false, depth - 1);  // Next player minimizes
                board.undoMove();
                
                eval = result.value;
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }
                
                alpha = Math.max(alpha, eval);  // Update alpha for alpha-beta pruning
                if (beta <= alpha) {
                    break;  // Beta cutoff
                }
            }
            return new MoveValue(bestMove, maxEval);  // Return the best value for the maximizing player
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                board.makeMove(move);
                MoveValue result = quiescenceSearch(board, alpha, beta, true, depth - 1);  // Next player maximizes
                board.undoMove();
                
                eval = result.value;
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }
                
                beta = Math.min(beta, eval);  // Update beta for alpha-beta pruning
                if (beta <= alpha) {
                    break;  // Alpha cutoff
                }
            }
            return new MoveValue(bestMove, minEval);  // Return the best value for the minimizing player
        }
        
    }
    
    

    @Override
    public int evaluate(BitBoard board) {
        // if (board.isCheckMate()) {
        //     System.out.println("Checkmate!");
        //     return board.whiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        // }
        // if (board.isStaleMate()) {
        //     // System.out.println("Stalemate!");
        //     // si on a l'avantage, on veut éviter le pat
        //     return board.whiteTurn ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        // }
        // evaluate position based on material and piece-square tables
        int materialValue = getBoardValue(board);
        return materialValue;

        
    }
    
    // Fonction pour déterminer la marge de razoring en fonction de la profondeur
    private int razoringMargin(int depth) {
        // Tu peux ajuster ces valeurs selon ton évaluation
        if (depth == 1) return 100;
        if (depth == 2) return 200;
        return 0;
    }
    

    public int getPieceValuePawn(int square, boolean isWhite, boolean isEndGame) {
        return (isWhite ? 125 : -125) + (isEndGame ? PAWN_ENDGAME_TABLE[square] : PAWN_TABLE[square]);
    }

    public int getPieceValueKnight(int square, boolean isWhite) {
        return (isWhite ? 320 : -320) + KNIGHT_TABLE[square];
    }

    public int getPieceValueBishop(int square, boolean isWhite) {
        return (isWhite ? 330 : -330) + BISHOP_TABLE[square];
    }

    public int getPieceValueRook(int square, boolean isWhite) {
        return (isWhite ? 500 : -500) + ROOK_TABLE[square];
    }

    public int getPieceValueQueen(int square, boolean isWhite) {
        return (isWhite ? 900 : -900) + QUEEN_TABLE[square];
    }

    public int getPieceValueKing(int square, boolean isWhite, boolean isEndGame) {
        return (isWhite ? 20000 : -20000) + (isEndGame ? KING_END_GAME_TABLE[square] : KING_MIDDLE_GAME_TABLE[square]);
    }

    // get board value for white
    private int getBoardValue(BitBoard board) {
        int value = 0;
        long pawns;
        long knights;
        long bishops;
        long rooks;
        long queens;
        long kings;

        if (board.whiteTurn) {
            pawns = board.getWhitePawns();
            knights = board.getWhiteKnights();
            bishops = board.getWhiteBishops();
            rooks = board.getWhiteRooks();
            queens = board.getWhiteQueens();
            kings = board.getWhiteKing();
        } else {
            pawns = board.getBlackPawns();
            knights = board.getBlackKnights();
            bishops = board.getBlackBishops();
            rooks = board.getBlackRooks();
            queens = board.getBlackQueens();
            kings = board.getBlackKing();
        }

        while (pawns != 0) {
            long pawn = BitBoard.getLSB(pawns);
            pawns &= pawns - 1;
            value += getPieceValuePawn(BitBoard.getSquare(pawn), board.whiteTurn, false);
        }

        while (knights != 0) {
            long knight = BitBoard.getLSB(knights);
            knights &= knights - 1;
            value += getPieceValueKnight(BitBoard.getSquare(knight), board.whiteTurn);
        }

        while (bishops != 0) {
            long bishop = BitBoard.getLSB(bishops);
            bishops &= bishops - 1;
            value += getPieceValueBishop(BitBoard.getSquare(bishop), board.whiteTurn);
        }

        while (rooks != 0) {
            long rook = BitBoard.getLSB(rooks);
            rooks &= rooks - 1;
            value += getPieceValueRook(BitBoard.getSquare(rook), board.whiteTurn);
        }

        while (queens != 0) {
            long queen = BitBoard.getLSB(queens);
            queens &= queens - 1;
            value += getPieceValueQueen(BitBoard.getSquare(queen), board.whiteTurn);
        }

        while (kings != 0) {
            long king = BitBoard.getLSB(kings);
            kings &= kings - 1;
            value += getPieceValueKing(BitBoard.getSquare(king), true, false);
        }

        if (!board.whiteTurn) {
            pawns = board.getWhitePawns();
            knights = board.getWhiteKnights();
            bishops = board.getWhiteBishops();
            rooks = board.getWhiteRooks();
            queens = board.getWhiteQueens();
            kings = board.getWhiteKing();
        } else {
            pawns = board.getBlackPawns();
            knights = board.getBlackKnights();
            bishops = board.getBlackBishops();
            rooks = board.getBlackRooks();
            queens = board.getBlackQueens();
            kings = board.getBlackKing();
        }

        while (pawns != 0) {
            long pawn = BitBoard.getLSB(pawns);
            pawns &= pawns - 1;
            value += getPieceValuePawn(BitBoard.getSquare(pawn), !board.whiteTurn, false);
        }

        while (knights != 0) {
            long knight = BitBoard.getLSB(knights);
            knights &= knights - 1;
            value += getPieceValueKnight(BitBoard.getSquare(knight), !board.whiteTurn);
        }

        while (bishops != 0) {
            long bishop = BitBoard.getLSB(bishops);
            bishops &= bishops - 1;
            value += getPieceValueBishop(BitBoard.getSquare(bishop), !board.whiteTurn);
        }

        while (rooks != 0) {
            long rook = BitBoard.getLSB(rooks);
            rooks &= rooks - 1;
            value += getPieceValueRook(BitBoard.getSquare(rook), !board.whiteTurn);
        }

        while (queens != 0) {
            long queen = BitBoard.getLSB(queens);
            queens &= queens - 1;
            value += getPieceValueQueen(BitBoard.getSquare(queen), !board.whiteTurn);
        }

        while (kings != 0) {
            long king = BitBoard.getLSB(kings);
            kings &= kings - 1;
            value += getPieceValueKing(BitBoard.getSquare(king), false, false);
        }

        return value;
    }

    private int getBoardValue2(BitBoard board) {
        int value = 0;
        long pawns = board.getWhitePawns();
        long knights = board.getWhiteKnights();
        long bishops = board.getWhiteBishops();
        long rooks = board.getWhiteRooks();
        long queens = board.getWhiteQueens();
        long kings = board.getWhiteKing();

        while (pawns != 0) {
            pawns &= pawns - 1;
            value += 100;
        }

        while (knights != 0) {
            knights &= knights - 1;
            value += 320;
        }

        while (bishops != 0) {
            bishops &= bishops - 1;
            value += 330;
        }

        while (rooks != 0) {
            rooks &= rooks - 1;
            value += 500;
        }

        while (queens != 0) {
            queens &= queens - 1;
            value += 900;
        }

        while (kings != 0) {
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
            blackPawns &= blackPawns - 1;
            value -= 100;
        }

        while (blackKnights != 0) {
            blackKnights &= blackKnights - 1;
            value -= 320;
        }

        while (blackBishops != 0) {
            blackBishops &= blackBishops - 1;
            value -= 330;
        }

        while (blackRooks != 0) {
            blackRooks &= blackRooks - 1;
            value -= 500;
        }

        while (blackQueens != 0) {
            blackQueens &= blackQueens - 1;
            value -= 900;
        }

        while (blackKings != 0) {
            blackKings &= blackKings - 1;
            value -= 20000;
        }

        return value;
    }

    @Override
    public String getName() {
        return "Custom";
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
    
    
}
