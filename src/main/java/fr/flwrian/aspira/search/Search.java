package fr.flwrian.aspira.search;

import java.util.Locale;

import fr.flwrian.aspira.board.Board;
import fr.flwrian.aspira.hash.TranspositionTable;
import fr.flwrian.aspira.move.Move;
import fr.flwrian.aspira.move.PackedMove;
import fr.flwrian.aspira.move.PackedMoveList;

public class Search implements SearchAlgorithm {

    static final int MAX_PLY = 128;

    static final int VALUE_MATE = 32000;
    static final int VALUE_INFINITE = 32001;
    static final int VALUE_NONE = 32002;

    static final int VALUE_MATE_IN_PLY = VALUE_MATE - MAX_PLY;
    static final int VALUE_MATED_IN_PLY = -VALUE_MATE_IN_PLY;

    static final int VALUE_TB_WIN = VALUE_MATE_IN_PLY;
    static final int VALUE_TB_LOSS = -VALUE_TB_WIN;
    static final int VALUE_TB_WIN_IN_MAX_PLY = VALUE_TB_WIN - MAX_PLY;
    static final int VALUE_TB_LOSS_IN_MAX_PLY = -VALUE_TB_WIN_IN_MAX_PLY;

    private static final PackedMoveList[] moveLists = new PackedMoveList[MAX_PLY];

    static {
        // Init stack movelist
        System.out.println("Initializing move lists...");
        for (int i = 0; i < MAX_PLY; i++){
            moveLists[i] = new PackedMoveList(218);
        }
    }

    final int CHECK_RATE = 256;
    final int INFINITE_VALUE = 32001;

    int[] pvLengths = new int[MAX_PLY];
    int[][] principalVariations = new int[MAX_PLY][MAX_PLY];

    int[][] killermoves = new int[MAX_PLY][2];

    long nodes = 0;
    long lastNps = 0;
    boolean stopSearch = false;
    int checks = CHECK_RATE;

    long nodeLimit = 0;
    long timeLimit = 0;

    long startTime;

    // history table
    // Indexed by [color][from][to]
    int[][][] historyTable = new int[2][64][64];

    public TranspositionTable transpositionTable = new TranspositionTable(64);

    public int qsearch(Board board, int alpha, int beta, int ply) {
        
        if (stopSearch || checkTime(false)) {
            stopSearch = true;
            return 0;
        }

        if (ply >= MAX_PLY) {
            return evaluate(board);
        }

        // boolean inCheck = board.isKingInCheck(board.whiteTurn);
        
        // if (inCheck) {
        //     PackedMoveList moves = board.getLegalMoves(moveLists[ply]);

        //     int best = -VALUE_INFINITE;
        //     int evasions = moves.size();
        //     for (int i = 0; i < evasions; i++) {
        //         int move = moves.get(i);
        //         nodes++;

        //         board.makeMove(move);
        //         int score = -qsearch(board, -beta, -alpha, ply + 1);
        //         board.undoMove();

        //         if (score > best) best = score;
        //         if (score > alpha) {
        //             alpha = score;
        //             if (alpha >= beta) break;
        //         }
        //     }

        //     if (evasions == 0) {
        //         // échec et mat
        //         return matedInPly(ply);
        //     }

        //     return best;
        // }

        int bestValue = evaluate(board);

        if (bestValue >= beta) {
            return bestValue;
        }

        if (bestValue > alpha) {
            alpha = bestValue;
        }

        PackedMoveList moves = board.getCaptureMoves(moveLists[ply]);
        orderQMoves(moves);
        for (int i = 0; i < moves.size(); i++) {
            nodes++;

            int capturedPiece = PackedMove.getCaptured(moves.get(i));

            // Delta pruning
            if (Board.PIECE_SCORES[capturedPiece] + 400 + bestValue < alpha && !PackedMove.isPromotion(moves.get(i))) {
                continue;
            }

            board.makeMove(moves.get(i));
            int score = -qsearch(board, -beta, -alpha, ply + 1);
            board.undoMove();

            if (score > bestValue) {
                bestValue = score;

                if (score > alpha) {
                    alpha = score;

                    if (alpha >= beta) {
                        break;
                    }
                }
                
            }
        }

        return bestValue;
    }

    public int absearch(Board board, int depth, int alpha, int beta, int ply) {
        
        if (checkTime(false)) {
            return 0;
        }

        if (ply >= MAX_PLY) {
            return evaluate(board);
        }

        pvLengths [ply] = ply;
        boolean rootNode = (ply == 0);
        long hashKey = board.zobristKey;

        if (!rootNode) {
            if (board.isThreefoldRepetition()) {
                return -5;
            }

            // Mate distance pruning
            alpha = Math.max(alpha, matedInPly(ply));
            beta = Math.min(beta, mateInPly(ply + 1));
            if (alpha >= beta) {
                return alpha;
            }
        }

        if (depth <= 0) {
            return qsearch(board, alpha, beta, ply);
        }

        // TT probe
        TranspositionTable.Entry tte = transpositionTable.get(hashKey);
        boolean ttHit = (tte != null);
        int ttMove = ttHit ? tte.bestMove : 0;
        int ttScore = ttHit ? scoreFromTT(tte.value, ply) : 0;

        if (!rootNode && ttHit && tte.depth >= depth) {
            if (tte.flag == TranspositionTable.Entry.LOWERBOUND) {
                alpha = Math.max(alpha, ttScore);
            } else if (tte.flag == TranspositionTable.Entry.UPPERBOUND) {
                beta = Math.min(beta, ttScore);
            }

            if (alpha >= beta) {
                return ttScore;
            }

        }

        boolean inCheck = board.isKingInCheck(board.whiteTurn);

        if (inCheck && depth < MAX_PLY - 1) {
            // depth++; // Étendre la recherche d'un ply
         }
        // Null move pruning
        if (!inCheck && depth >= 3) {
            board.makeNullMove();
            int score = -absearch(board, depth - 2, -beta, -beta + 1, ply + 1);
            board.undoNullMove();

            if (score >= beta) {
                if (score >= VALUE_TB_WIN_IN_MAX_PLY) {
                    score = beta;
                }
                return score;
            }
        }

        int oldAlpha = alpha;
        int bestScore = -VALUE_INFINITE;
        int bestMove = 0;

        int madeMoves = 0;

        PackedMoveList moves = board.getLegalMoves(moveLists[ply]);
        orderMoves(moves, ttMove, board, ply);


        // final int LMR_REDUCTION = 1;
        // final int LMR_MIN_MOVES = 3;
        // final int LMR_MIN_DEPTH = 3;

        final boolean criticalDepth = (depth <= 2);

        for (int i = 0; i < moves.size(); i++) {
            int move = moves.get(i);
            madeMoves++;
            nodes++;

            board.makeMove(move);
            boolean givesCheck = board.isKingInCheck(!board.whiteTurn);
            boolean isCapture = PackedMove.isCapture(move);
            boolean isPVNode = (beta - alpha > 1);
            int reduction = 0;

            if (!criticalDepth && !inCheck && !givesCheck && !isCapture && !PackedMove.isPromotion(move)) {
                reduction = calculateReduction(depth, madeMoves, isPVNode);
            }


            int searchDepth = Math.max(depth - 1 - reduction, 0);
            int score;

            // ===== PVS : NOUVEAU CODE =====
            if (madeMoves == 1) {
                // Premier coup : recherche normale avec fenêtre complète
                score = -absearch(board, searchDepth, -beta, -alpha, ply + 1);
            } else {
                // Coups suivants : essayer d'abord avec fenêtre nulle (PVS)
                score = -absearch(board, searchDepth, -alpha - 1, -alpha, ply + 1);
        
                // Si le coup s'avère meilleur qu'attendu, re-rechercher avec fenêtre complète
                if (score > alpha && score < beta) {
                    score = -absearch(board, searchDepth, -beta, -alpha, ply + 1);
                }
            }

            // Re-recherche LMR si nécessaire
            if (reduction > 0 && score > alpha) {
                // Appliquer aussi PVS pour la re-recherche
                if (madeMoves == 1) {
                    score = -absearch(board, depth - 1, -beta, -alpha, ply + 1);
                } else {
                    score = -absearch(board, depth - 1, -alpha - 1, -alpha, ply + 1);
                    if (score > alpha && score < beta) {
                        score = -absearch(board, depth - 1, -beta, -alpha, ply + 1);
                    }
                }
            }
            board.undoMove();

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;

                // Update principal variation
                principalVariations[ply][ply] = move;

                for (int j = ply + 1; j < pvLengths[ply + 1]; j++) {
                    principalVariations[ply][j] = principalVariations[ply + 1][j];
                }

                pvLengths[ply] = pvLengths[ply + 1];

                if (score > alpha) {
                    alpha = score;

                    if (score >= beta) {
                        // Update history
                        // Only for non-capture moves
                        if (!PackedMove.isCapture(move)) {
                            int bonus = depth * depth;
                            int color = board.whiteTurn ? 0 : 1;
                            int from = PackedMove.getFrom(move);
                            int to = PackedMove.getTo(move);

                            int current = historyTable[color][from][to];
                            int delta = bonus - (current * Math.abs(bonus)) / 16384;
                            historyTable[color][from][to] = current + delta;

                            if (killermoves[ply][0] != move){
                                killermoves[ply][1] = killermoves[ply][0];
                                killermoves[ply][0] = move;
                            }
                        }
                        break;
                    }
                }

            }
  
        }
        
        // No moves made -> checkmate or stalemate
        if (madeMoves == 0) {
            if (inCheck) {
                return matedInPly(ply);
            } else {
                return 0;
            }
        }

        // Calculate bound for TT
        int flag;
        if (bestScore >= beta) {
            flag = TranspositionTable.Entry.LOWERBOUND;
        } else if (alpha != oldAlpha) {
            flag = TranspositionTable.Entry.EXACT;
        } else {
            flag = TranspositionTable.Entry.UPPERBOUND;
        }

        if (!checkTime(false)) {
            transpositionTable.put(hashKey, bestMove, bestScore, depth, flag);
        }
        
        return bestScore;
    }

    
    // Supprimer les constantes fixes et ajouter :

    private int calculateReduction(int depth, int moveNumber, boolean isPVNode) {
        if (moveNumber < 3 || depth < 3) {
            return 0; // Pas de réduction
        }

        // Formule de base : réduction croissante
        int reduction = 1;

        if (depth >= 6) {
            reduction = 2;
        }

        if (moveNumber >= 6) {
            reduction += 1;
        }

        if (moveNumber >= 12) {
            reduction += 1;
        }

        // Moins de réduction sur les nœuds PV
        if (isPVNode && reduction > 0) {
            reduction -= 1;
        }

        return reduction;
    }

    
        public void iterativeDeepening(Board board, int depthLimit) {
        nodes = 0;
        int score = 0;
        int bestMove = 0;
        startTime = System.nanoTime();

        for (int depth = 1; depth <= depthLimit; depth++) {
        
            int alpha, beta;
        
            // Premières profondeurs : fenêtre infinie pour avoir un score stable
            if (depth <= 4) {
                alpha = -INFINITE_VALUE;
                beta = INFINITE_VALUE;
                score = absearch(board, depth, alpha, beta, 0);
            } else {
                // Aspiration window : fenêtre étroite autour du score précédent
                int window = 15;
                alpha = score - window;
                beta = score + window;
            
                // Boucle de re-recherche si on sort de la fenêtre
                int researches = 0;
                while (true) {
                    score = absearch(board, depth, alpha, beta, 0);

                    if (stopSearch || checkTime(true)) {
                        break;
                    }

                    // Si on sort de la fenêtre, élargir et re-chercher
                    if (score <= alpha) {
                        // Fail-low : le score est plus bas qu'attendu
                        beta = (alpha + beta) / 2;
                        alpha = Math.max(score - window * (1 + researches), -INFINITE_VALUE);
                        researches++;
                    } else if (score >= beta) {
                        // Fail-high : le score est plus haut qu'attendu
                        beta = Math.min(score + window * (1 + researches), INFINITE_VALUE);
                        researches++;
                    } else {
                        // Score dans la fenêtre, on peut continuer
                        break;
                    }
                
                    // Sécurité : après 3 re-recherches, fenêtre infinie
                    if (researches >= 3) {
                        alpha = -INFINITE_VALUE;
                        beta = INFINITE_VALUE;
                    }
                }
            }

            if (stopSearch || checkTime(true)) {
                break;
            }
        
            bestMove = principalVariations[0][0];
            long endTime = System.nanoTime();
            printSearchInfo(depth, score, nodes, endTime - startTime);
        }

        // Last attempt to get best move
        if (bestMove == 0) {
            bestMove = principalVariations[0][0];
        }

        Move best = PackedMove.unpack(bestMove);
        System.out.println("bestmove " + best);
    }

    @Override
    public int evaluate(Board board) {
        return board.whiteTurn ? board.evaluate() : -board.evaluate();
    }

    private boolean checkTime(boolean iter) {
        if (stopSearch) {
            return true;
        }

        // node limit
        if (nodeLimit != 0 && nodes >= nodeLimit) {
            return true;
        }

        if (checks > 0 && !iter) {
            checks--;
            return false;
        }

        checks = CHECK_RATE;

        if (timeLimit == 0) {
            return false;
        }

        long currentTime = System.nanoTime();

        // time limit
        if (currentTime >= startTime + timeLimit) {
            return true;
        }
        
        return false;
    }

    public String getPV(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pvLengths[0]; i++) {
            Move move = PackedMove.unpack(principalVariations[0][i]);
            sb.append(move.toString()).append(" ");
        }
        return sb.toString().trim();
    }
    

    public String convertScore(int score) {
        if (score >= VALUE_MATE_IN_PLY) {
            return "mate " + (((VALUE_MATE - score) / 2) + ((VALUE_MATE - score) & 1));
        } else if (score <= VALUE_MATED_IN_PLY) {
            return "mate " + (-((VALUE_MATE + score) / 2) + ((VALUE_MATE + score) & 1));
        } else {
            return "cp " + score;
        }
    }

    public int scoreFromTT(int score, int ply) {
        if (score >= VALUE_TB_WIN_IN_MAX_PLY) {
            return score - ply;
        }

        else {
            if (score <= VALUE_TB_LOSS_IN_MAX_PLY) {
                return score + ply;
            } else {
                return score;
            }
        }
    }



    public int mateInPly(int ply) {
        return VALUE_MATE - ply;
    }

    public int matedInPly(int ply) {
        return ply - VALUE_MATE;
    }


    private void printSearchInfo(int depth, int score, long nodes, long durationNanos) {
        double timeMs = durationNanos / 1_000_000.0;
        double rawNps = nodes / (durationNanos / 1_000_000_000.0);
        lastNps = (long) rawNps;

        System.out.printf(Locale.US, "info depth %d score %s nodes %d nps %d time %.0f hashfull %d pv %s\n",
                depth, convertScore(score), nodes, lastNps, timeMs, this.transpositionTable.hashfull(), getPV());
    }

    @Override
    public void resetSearch() {
        // reset PV lengths and principal variation table
        for (int i = 0; i < pvLengths.length; i++) {
            pvLengths[i] = 0;
        }
        for (int i = 0; i < principalVariations.length; i++) {
            for (int j = 0; j < principalVariations[i].length; j++) {
                principalVariations[i][j] = 0;
            }
        }

        // reset killers
        killermoves = new int[MAX_PLY][2];

        
        // reset history table and transposition table
        historyTable = new int[2][64][64];
    }

    public static final int[][] mvvLva = {

            { 105, 205, 305, 405, 505, 605 }, // Pawn captures

            { 104, 204, 304, 404, 504, 604 }, // Knight captures

            { 103, 203, 303, 403, 503, 603 }, // Bishop captures

            { 102, 202, 302, 402, 502, 602 }, // Rook captures

            { 101, 201, 301, 401, 501, 601 }, // Queen captures

            { 100, 200, 300, 400, 500, 600 } // King captures

    };

    // public int scoreMove(int move, int ttMove, Board board) {
    //     if (move == ttMove) {
    //         return 1_000_000;
    //     }

    //     if (PackedMove.isCapture(move)) {
    //         return 32_000 + mvvLva[PackedMove.getCaptured(move)][PackedMove.getPieceFrom(move)];
    //     }

    //     return historyTable[board.whiteTurn ? 0 : 1][PackedMove.getFrom(move)][PackedMove.getTo(move)];
    // }

    public int scoreQMove(int move) {
        // System.out.println(PackedMove.unpack(move));
        // int captured = PackedMove.getCaptured(move);
        // int pfrom = PackedMove.getPieceFrom(move);
        // System.out.println(captured + " " + pfrom);
        int score = mvvLva[PackedMove.getCaptured(move)][PackedMove.getPieceFrom(move)];
        // System.out.println(score);
        // System.exit(0);
        return score;
    }

    /**
     * Orders moves in the following order:
     * 1️ -> Transposition table move
     * 2️ -> Captures (MVV-LVA)
     * 3️ -> Quiets (History heuristic)
     * @param moves
     * @param ttMove
     * @param board
     */
    public void orderMoves(PackedMoveList moves, int ttMove, Board board, int ply) {
        int size = moves.size();
        int[] m = moves.moves;

        int idx = 0;

        // 1) TT move
        if (ttMove != 0) {
            for (int i = 0; i < size; i++) {
                if (m[i] == ttMove) {
                    swap(m, idx++, i);
                    break;
                }
            }
        }

        // 2) Captures
        int captureStart = idx;
        for (int i = idx; i < size; i++) {
            if (PackedMove.isCapture(m[i])) {
                swap(m, idx++, i);
            }
        }
        sortCaptures(m, captureStart, idx);

        // 3) Killers (quiet only, no duplicates)
        for (int k = 0; k < 2; k++) {
            int killer = killermoves[ply][k];
            if (killer == 0) continue;
            if (killer == ttMove) continue;
            // if (PackedMove.isCapture(killer)) continue;

            for (int i = idx; i < size; i++) {
                if (m[i] == killer) {
                    swap(m, idx++, i);
                    break;
                }
            }
        }

        // 4) Remaining quiets (history)
        sortQuiets(m, idx, size, board);
    }
    


    private static void swap(int[] arr, int a, int b) {
        int tmp = arr[a];
        arr[a] = arr[b];
        arr[b] = tmp;
    }

    private void sortCaptures(int[] m, int from, int to) {
        for (int i = from + 1; i < to; i++) {
            int move = m[i];
            int score = scoreQMove(move);
            int j = i - 1;

            while (j >= from && scoreQMove(m[j]) < score) {
                m[j + 1] = m[j];
                j--;
            }
            m[j + 1] = move;
        }
    }
    
    private void sortQuiets(int[] m, int from, int to, Board board) {
        int color = board.whiteTurn ? 0 : 1;

        for (int i = from + 1; i < to; i++) {
            int move = m[i];
            int fromSq = PackedMove.getFrom(move);
            int toSq = PackedMove.getTo(move);
            int score = historyTable[color][fromSq][toSq];

            int j = i - 1;
            while (j >= from) {
                int m2 = m[j];
                int s2 = historyTable[color]
                        [PackedMove.getFrom(m2)]
                        [PackedMove.getTo(m2)];

                if (s2 >= score) break;

                m[j + 1] = m[j];
                j--;
            }
            m[j + 1] = move;
        }
    }


    public void orderQMoves(PackedMoveList moves) {
        int size = moves.size();
        int[] m = moves.moves;

        for (int i = 1; i < size; i++) {
            int move = m[i];
            int score = scoreQMove(move);
            int j = i - 1;

            while (j >= 0 && scoreQMove(m[j]) < score) {
                m[j + 1] = m[j];
                j--;
            }
            m[j + 1] = move;
        }
    }


    @Override
    public Move search(Board board, int wtime, int btime, int winc, int binc, int movetime, int depth, long maxNodes) {
        // reset counters and timing
        nodes = 0;
        stopSearch = false;
        startTime = 0;
        checks = CHECK_RATE;
        resetSearch();


        nodeLimit = maxNodes;


        
        // Time management
        if (movetime > 0) {
            timeLimit = movetime * 1_000_000L;
        } else {
            int time = board.whiteTurn ? wtime : btime;
            int inc = board.whiteTurn ? winc : binc;
            int timePerMoveMs = (time / 20) + (inc / 2);
            timeLimit = (long) (timePerMoveMs * 0.9 * 1_000_000L);
        }
        
        iterativeDeepening(board, depth);
        return PackedMove.unpack(principalVariations[0][0]);
    }

    @Override
    public String getName() {
        return "Search";
    }

    @Override
    public void setStopSearch(boolean b) {
        stopSearch = b;
    }

    @Override
    public long getLastNodeCount() {
        return nodes;
    }

    @Override
    public long getLastNPS() {
        return lastNps;
    }

    @Override
    public void flushHashTable() {
        transpositionTable.flush();
    }

    @Override
    public void setHashTable(int sizeMB) {
        this.transpositionTable = null;
        this.transpositionTable = new TranspositionTable(sizeMB);
    }


}
