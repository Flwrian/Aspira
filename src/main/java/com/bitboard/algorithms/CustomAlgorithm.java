package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.MoveGenerator;
import com.bitboard.MoveList;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom algorithm for the chess engine.
 * 
 */
public class CustomAlgorithm implements ChessAlgorithm {

    int nodes = 0;
    int depth = 0;
    int razorDepth = 7;
    int npm = 5;
    private int MAX_QUIESCENCE_DEPTH = 6;
    private int MAX_DEPTH = 11;

    public void setRazorDepth(int razorDepth) {
        this.razorDepth = razorDepth;
    }

    // @Override
    // public void setNPM(int npm) {
    //     this.npm = npm;
    // }

    // @Override
    // public int getNPM() {
    //     return npm;
    // }

    // @Override
    // public int getRazorDepth() {
    //     return razorDepth;
    // }

    // transposition table
    private TranspositionTable transpositionTable = new TranspositionTable();


    // Piece-square tables for evaluation (higher values for better squares)
    private static final int[] PAWN_TABLE = {
        0,   0,   0,   0,   0,   0,   0,   0,
     -11,  34, 126,  68,  95,  61, 134,  98,
     -20,  25,  56,  65,  31,  26,   7,  -6,
     -23,  17,  12,  23,  21,   6,  13, -14,
     -25,  10,   6,  17,  12,  -5,  -2, -27,
     -12,  33,   3,   3, -10,  -4,  -4, -26,
     -22,  38,  24, -15, -23, -20,  -1, -35,
        0,   0,   0,   0,   0,   0,   0,   0,
    };


    private static final int[] PAWN_ENDGAME_TABLE = {
        0,   0,   0,   0,   0,   0,   0,   0,
     187, 165, 132, 147, 134, 158, 173, 178,
      84,  82,  53,  56,  67,  85, 100,  94,
      17,  17,   4,  -2,   5,  13,  24,  32,
      -1,   3,  -8,  -7,  -7,  -3,   9,  13,
      -8,  -1,  -5,   0,   1,  -6,   7,   4,
      -7,   2,   0,  13,  10,   8,   8,  13,
        0,   0,   0,   0,   0,   0,   0,   0,
    };

    private static final int[] KNIGHT_TABLE = {
        -107, -15, -97,  61, -49, -34, -89, -167,
        -17,   7,  62,  23,  36,  72, -41,  -73,
         44,  73, 129,  84,  65,  37,  60,  -47,
         22,  18,  69,  37,  53,  19,  17,   -9,
         -8,  21,  19,  28,  13,  16,   4,  -13,
        -16,  25,  17,  19,  10,  12,  -9,  -23,
        -19, -14,  18,  -1,  -3, -12, -53,  -29,
        -23, -19, -28, -17, -33, -58, -21, -105,
       };

    private static final int[] ENDGAME_KNIGHT_TABLE = {
        -99, -63, -27, -31, -28, -13, -38, -58,
    -52, -24, -25,  -9,  -2, -25,  -8, -25,
    -41, -19,  -9,  -1,   9,  10, -20, -24,
    -18,   8,  11,  22,  22,  22,   3, -17,
    -18,   4,  17,  16,  25,  16,  -6, -18,
    -22, -20,  -3,  10,  15,  -1,  -3, -23,
    -44, -23, -20,  -2,  -5, -10, -20, -42,
    -64, -50, -18, -22, -15, -23, -51, -29,
    };
       private static final int[] BISHOP_TABLE = {
        -8,   7, -42, -25, -37, -82,   4, -29,
       -47,  18,  59,  30, -13, -18,  16, -26,
        -2,  37,  50,  35,  40,  43,  37, -16,
        -2,   7,  37,  37,  50,  19,   5,  -4,
         4,  10,  12,  34,  26,  13,  13,  -6,
        10,  18,  27,  14,  15,  15,  15,   0,
         1,  33,  21,   7,   0,  16,  15,   4,
       -21, -39, -12, -13, -21, -14,  -3, -33,
      };

      private static final int[] ENDGAME_BISHOP_TABLE = {
        -24, -17,  -9,  -7,  -8, -11, -21, -14,
        -14,  -4, -13,  -3, -12,   7,  -4,  -8,
          4,   0,   6,  -2,  -1,   0,  -8,   2,
          2,   3,  10,  14,   9,  12,   9,  -3,
         -9,  -3,  10,   7,  19,  13,   3,  -6,
        -15,  -7,   3,  13,  10,   8,  -3, -12,
        -27, -15,  -9,   4,  -1,  -7, -18, -14,
        -17,  -5, -16,  -9,  -5, -23,  -9, -23,
     };
  
      private static final int[] ROOK_TABLE = {
        43,  31,   9,  63,  51,  32,  42,  32,
        44,  26,  67,  80,  62,  58,  32,  27,
        16,  61,  45,  17,  36,  26,  19,  -5,
       -20,  -8,  35,  24,  26,   7, -11, -24,
       -23,   6,  -7,   9,  -1, -12, -26, -36,
       -33,  -5,   0,   3, -17, -16, -25, -45,
       -71,  -6,  11,  -1,  -9, -20, -16, -44,
       -26, -37,   7,  16,  17,   1, -13, -19,
      };

        private static final int[] ENDGAME_ROOK_TABLE = {
            5,   8,  12,  12,  15,  18,  10,  13,
     3,   8,   3,  -3,  11,  13,  13,  11,
    -3,  -5,  -3,   4,   5,   7,   7,   7,
     2,  -1,   1,   2,   1,  13,   3,   4,
   -11,  -8,  -6,  -5,   4,   8,   5,   3,
   -16,  -8, -12,  -7,  -1,  -5,   0,  -4,
    -3, -11,  -9,  -9,   2,   0,  -6,  -6,
   -20,   4, -13,  -5,  -1,   3,   2,  -9,
        };
  
      private static final int[] QUEEN_TABLE = {
        45,  43,  44,  59,  12,  29,   0, -28,
        54,  28,  57, -16,   1,  -5, -39, -24,
        57,  47,  56,  29,   8,   7, -17, -13,
         1,  -2,  17,  -1, -16, -16, -27, -27,
        -3,   3,  -4,  -2, -10,  -9, -26,  -9,
         5,  14,   2,  -5,  -2, -11,   2, -14,
         1,  -3,  15,   8,   2,  11,  -8, -35,
       -50, -31, -25, -15,  10,  -9, -18,  -1,
      };

        private static final int[] ENDGAME_QUEEN_TABLE = {
            45,  43,  44,  59,  12,  29,   0, -28,
    54,  28,  57, -16,   1,  -5, -39, -24,
    57,  47,  56,  29,   8,   7, -17, -13,
     1,  -2,  17,  -1, -16, -16, -27, -27,
    -3,   3,  -4,  -2, -10,  -9, -26,  -9,
     5,  14,   2,  -5,  -2, -11,   2, -14,
     1,  -3,  15,   8,   2,  11,  -8, -35,
   -50, -31, -25, -15,  10,  -9, -18,  -1,
        };
  
      private static final int[] KING_MIDDLE_GAME_TABLE = {
        13,   2, -34, -56, -15,  16,  23, -65,
       -29, -38,  -4,  -8,  -7, -20,  -1,  29,
       -22,  22,   6, -20, -16,   2,  24,  -9,
       -36, -14, -25, -30, -27, -12, -20, -17,
       -51, -33, -44, -46, -39, -27,  -1, -49,
       -27, -15, -30, -44, -46, -22, -14, -14,
         8,   9, -16, -43, -64,  -8,   7,   1,
        14,  50, -28,   8, -54,  40,  41, -15,
      };
  

      private static final int[] KING_END_GAME_TABLE = {
        -17,   4,  15,  -11, -18, -18, -35, -74,
         11,  23,  38,   17,  17,  14,  17, -12,
         13,  44,  45,   20,  15,  23,  17,  10,
          3,  26,  33,   26,  27,  24,  22,  -8,
        -11,   9,  23,   27,  24,  21,  -4, -18,
         -9,   7,  16,   23,  21,  11,  -3, -19,
        -17,  -5,   4,   14,  13,   4, -11, -27,
        -43, -24, -14,  -28, -11, -21, -34, -53
    };


    public void setDepth(int depth) {
        this.depth = depth;
    }

    public CustomAlgorithm(int depth) {
        this.depth = depth;
    }

    

    @Override
    public Move search(BitBoard board, int wtime, int btime, int winc, int binc, int movestogo, int a) {
        nodes = 0;
        Move bestMove = null;
        long time = System.currentTimeMillis();

        // Réduire la profondeur si le temps est limité
        if (board.whiteTurn && wtime < 1000 || !board.whiteTurn && btime < 1000) {
            depth = -1;
        }

        // Fenêtre d'aspiration initiale
        int window = 100;

        for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {

            if (getMaterialValue(board, true) + getMaterialValue(board, false) < 43200) {
                System.out.println("Endgame");
                MAX_QUIESCENCE_DEPTH = 10;
                depth = MAX_DEPTH;
            }

            long time2 = System.currentTimeMillis();

            // Recherche avec fenêtre d'aspiration
            int searchAlpha = Integer.MIN_VALUE + window;
            int searchBeta = Integer.MAX_VALUE - window;
            MoveValue result = minimax(board, currentDepth, searchAlpha, searchBeta, board.whiteTurn);

            // Si la recherche échoue, on refait avec des bornes complètes
            if (result.value <= searchAlpha || result.value >= searchBeta) {
                window *= 2;  // Augmente la fenêtre si échec
                result = minimax(board, currentDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn);
            }

            time2 = System.currentTimeMillis() - time2;
            bestMove = result.bestMove;

            // Affichage de la variation principale
            StringBuilder pvString = new StringBuilder();
            for (Move move : result.principalVariation) {
                pvString.append(move).append(" ");
            }

            System.out.println("info depth " + currentDepth + " score cp " + result.value + " pv " + pvString + " nodes " + nodes + " time " + time2);

            // Si le meilleur coup est un mat, arrêter la recherche
            // if (result.value >= 48000 || result.value <= -48000) {
            //     System.out.println("Mate found -> " + pvString);
            //     break;
            // }
        }

        time = System.currentTimeMillis() - time;
        System.out.println("info nodes " + nodes + " time " + time);
        return bestMove;
    }

    // Minimax avec coupure alpha-beta, quiescence et autres optimisations
    public MoveValue minimax(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        nodes++;

        long zobristHash = Zobrist.computeHash(board);
        TranspositionTableEntry entry = transpositionTable.probe(zobristHash);

        // Vérifie si une entrée est trouvée dans la table de transposition
        if (entry != null && entry.depth >= depth) {
            if (entry.nodeType == 0) {
                return new MoveValue(entry.bestMove, entry.score);
            } else if (entry.nodeType == 1 && entry.score <= alpha) {
                return new MoveValue(entry.bestMove, entry.score);
            } else if (entry.nodeType == 2 && entry.score >= beta) {
                return new MoveValue(entry.bestMove, entry.score);
            }
            alpha = Math.max(alpha, entry.score);
            beta = Math.min(beta, entry.score);
            if (alpha >= beta) {
                return new MoveValue(entry.bestMove, entry.score);
            }
        }

        // Null Move Pruning
        if (depth >= 2 && !board.isKingInCheck(maximizingPlayer) && board.getLegalMoves().size() > 0) {
            board.makeNullMove();
            MoveValue result = minimax(board, depth - 2, -beta, -beta + 1, !maximizingPlayer);
            board.undoNullMove();
            if (result.value >= beta) {
                return new MoveValue(null, beta);
            }
        }

        // if (!board.isKingInCheck(maximizingPlayer)) {
        //     // Applique le Razoring pour les faibles profondeurs
        //     if (depth <= razorDepth) {
        //         int eval = evaluate(board);
        //         if (eval + razoringMargin(depth) <= alpha) {
        //             return new MoveValue(null, eval);
        //         }
        //     }
        // }


        if (depth <= 0) {
            // Recherche de quiescence
            return quiescenceSearch(board, alpha, beta, maximizingPlayer);
        }

        MoveList moves = board.getLegalMoves();
        moves.sort((m1, m2) -> m2.getSeeScore() - m1.getSeeScore());

        Move bestMove = null;
        List<Move> bestVariation = new ArrayList<>();

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
                board.makeMove(move);

                // Late Move Reduction (LMR) pour réduire la profondeur des coups tardifs
                int reduction = (i >= 4 && depth > 2) ? 2 : 0;
                MoveValue result = minimax(board, depth - 1 - reduction, alpha, beta, false);

                board.undoMove();

                if (result.value > maxEval) {
                    maxEval = result.value;
                    bestMove = move;
                    bestVariation = new ArrayList<>(result.principalVariation);
                    bestVariation.add(0, move);
                }

                alpha = Math.max(alpha, result.value);
                if (alpha >= beta) {
                    break;  // Coupure beta
                }
            }

            byte nodeType = (maxEval <= alpha) ? (byte) 1 : (maxEval >= beta) ? (byte) 2 : (byte) 0;
            transpositionTable.store(zobristHash, depth, maxEval, nodeType, bestMove);

            return new MoveValue(bestMove, maxEval, bestVariation);
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
                board.makeMove(move);

                int reduction = (i >= 4 && depth > 2) ? 2 : 0;
                MoveValue result = minimax(board, depth - 1 - reduction, alpha, beta, true);

                board.undoMove();

                if (result.value < minEval) {
                    minEval = result.value;
                    bestMove = move;
                    bestVariation = new ArrayList<>(result.principalVariation);
                    bestVariation.add(0, move);
                }

                beta = Math.min(beta, result.value);
                if (beta <= alpha) {
                    break;  // Coupure alpha
                }
            }

            byte nodeType = (minEval <= alpha) ? (byte) 1 : (minEval >= beta) ? (byte) 2 : (byte) 0;
            transpositionTable.store(zobristHash, depth, minEval, nodeType, bestMove);

            return new MoveValue(bestMove, minEval, bestVariation);
        }
    }

    // Recherche de quiescence optimisée
    public MoveValue quiescenceSearch(BitBoard board, int alpha, int beta, boolean maximizingPlayer) {
        int eval = evaluate(board);

        if (eval >= beta) {
            return new MoveValue(null, beta);  // Coupure beta
        }

        if (eval > alpha) {
            alpha = eval;
        }

        MoveList moves = board.getCaptureMoves();
        moves.sort((m1, m2) -> m2.getSeeScore() - m1.getSeeScore());

        for (Move move : moves) {
            board.makeMove(move);
            MoveValue result = quiescenceSearch(board, alpha, beta, !maximizingPlayer);
            board.undoMove();

            if (result.value > alpha) {
                alpha = result.value;
            }

            if (alpha >= beta) {
                break;  // Coupure beta
            }
        }

        return new MoveValue(null, alpha);
    }

    // Évaluation de la position
    @Override
    public int evaluate(BitBoard board) {
        return getBoardValue(board);
    }

    // Fonction de calcul du razoring margin
    private int razoringMargin(int depth) {
        return depth * 100;
    }

    

    public int getPSTValuePawn(int square, boolean isEndGame){
        return (isEndGame ? PAWN_ENDGAME_TABLE[square] : PAWN_TABLE[square]);
    }

    public int getPSTValueKnight(int square, boolean isEndGame){
        return (isEndGame ? ENDGAME_KNIGHT_TABLE[square] : KNIGHT_TABLE[square]);
    }

    public int getPSTValueBishop(int square, boolean isEndGame){
        return (isEndGame ? ENDGAME_BISHOP_TABLE[square] : BISHOP_TABLE[square]);
    }

    public int getPSTValueRook(int square, boolean isEndGame){
        return (isEndGame ? ENDGAME_ROOK_TABLE[square] : ROOK_TABLE[square]);
    }

    public int getPSTValueQueen(int square, boolean isEndGame){
        return (isEndGame ? ENDGAME_QUEEN_TABLE[square] : QUEEN_TABLE[square]);
    }

    public int getPSTValueKing(int square, boolean isEndGame){
        return (isEndGame ? KING_END_GAME_TABLE[square] : KING_MIDDLE_GAME_TABLE[square]);
    }

    private int getMaterialValue(BitBoard board, boolean color) {
        int value = 0;
        long pawns = color ? board.getWhitePawns() : board.getBlackPawns();
        long knights = color ? board.getWhiteKnights() : board.getBlackKnights();
        long bishops = color ? board.getWhiteBishops() : board.getBlackBishops();
        long rooks = color ? board.getWhiteRooks() : board.getBlackRooks();
        long queens = color ? board.getWhiteQueens() : board.getBlackQueens();
        long kings = color ? board.getWhiteKing() : board.getBlackKing();

        while (pawns != 0) {
            pawns &= pawns - 1;
            value += 95;
        }

        while (knights != 0) {
            knights &= knights - 1;
            value += 325;
        }

        while (bishops != 0) {
            bishops &= bishops - 1;
            value += 335;
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

        return value;
    }

    // get board value
    private int getBoardValue(BitBoard board) {
        long pawns = board.getWhitePawns();
        long knights = board.getWhiteKnights();
        long bishops = board.getWhiteBishops();
        long rooks = board.getWhiteRooks();
        long queens = board.getWhiteQueens();
        long kings = board.getWhiteKing();

        int blackMaterial = getMaterialValue(board, false);
        int whiteMaterial = getMaterialValue(board, true);

        boolean isEndGame = whiteMaterial + blackMaterial < 43200;

        while (pawns != 0) {
            long pawn = BitBoard.getLSB(pawns);
            pawns &= pawns - 1;
            whiteMaterial += getPSTValuePawn(63 - BitBoard.getSquare(pawn), isEndGame);
        }

        while (knights != 0) {
            long knight = BitBoard.getLSB(knights);
            knights &= knights - 1;
            whiteMaterial += getPSTValueKnight(63 - BitBoard.getSquare(knight), isEndGame);
        }

        while (bishops != 0) {
            long bishop = BitBoard.getLSB(bishops);
            bishops &= bishops - 1;
            whiteMaterial += getPSTValueBishop(63 - BitBoard.getSquare(bishop), isEndGame);
        }

        while (rooks != 0) {
            long rook = BitBoard.getLSB(rooks);
            rooks &= rooks - 1;
            whiteMaterial += getPSTValueRook(63 - BitBoard.getSquare(rook), isEndGame);
        }

        while (queens != 0) {
            long queen = BitBoard.getLSB(queens);
            queens &= queens - 1;
            whiteMaterial += getPSTValueQueen(63 - BitBoard.getSquare(queen), isEndGame);
        }

        while (kings != 0) {
            long king = BitBoard.getLSB(kings);
            kings &= kings - 1;
            whiteMaterial += getPSTValueKing(63 - BitBoard.getSquare(king), isEndGame);
        }

        pawns = board.getBlackPawns();
        knights = board.getBlackKnights();
        bishops = board.getBlackBishops();
        rooks = board.getBlackRooks();
        queens = board.getBlackQueens();
        kings = board.getBlackKing();

        while (pawns != 0) {
            long pawn = BitBoard.getLSB(pawns);
            pawns &= pawns - 1;
            blackMaterial += getPSTValuePawn(BitBoard.getSquare(pawn), isEndGame);
        }

        while (knights != 0) {
            long knight = BitBoard.getLSB(knights);
            knights &= knights - 1;
            blackMaterial += getPSTValueKnight(BitBoard.getSquare(knight), isEndGame);
        }

        while (bishops != 0) {
            long bishop = BitBoard.getLSB(bishops);
            bishops &= bishops - 1;
            blackMaterial += getPSTValueBishop(BitBoard.getSquare(bishop), isEndGame);
        }

        while (rooks != 0) {
            long rook = BitBoard.getLSB(rooks);
            rooks &= rooks - 1;
            blackMaterial += getPSTValueRook(BitBoard.getSquare(rook), isEndGame);
        }

        while (queens != 0) {
            long queen = BitBoard.getLSB(queens);
            queens &= queens - 1;
            blackMaterial += getPSTValueQueen(BitBoard.getSquare(queen), isEndGame);
        }

        while (kings != 0) {
            long king = BitBoard.getLSB(kings);
            kings &= kings - 1;
            blackMaterial += getPSTValueKing(BitBoard.getSquare(king), isEndGame);
        }


        return whiteMaterial - blackMaterial;

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
    public class MoveValue {
        public Move bestMove;
        public int value;
        public List<Move> principalVariation;  // Ajout de la variation principale
    
        public MoveValue(Move bestMove, int value) {
            this.bestMove = bestMove;
            this.value = value;
            this.principalVariation = new ArrayList<>();
        }
    
        public MoveValue(Move bestMove, int value, List<Move> pv) {
            this.bestMove = bestMove;
            this.value = value;
            this.principalVariation = new ArrayList<>(pv);  // Copie la variation principale
        }

        @Override
        public String toString() {
            return "MoveValue{" +
                    "bestMove=" + bestMove +
                    ", value=" + value +
                    '}';
        }
    }
    
    
}
