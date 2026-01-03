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

    static final int MATE_BOUND = 31000;

    final int CHECK_RATE = 1024;

    int[] pvLengths = new int[MAX_PLY];
    int[][] principalVariations = new int[MAX_PLY][MAX_PLY];

    long[] hashHistory = new long[MAX_PLY];
    int repSize = 0;

    int[][] killerMoves = new int[MAX_PLY][2];

    long nodes = 0;
    long lastNps = 0;
    boolean stopSearch = false;
    int checks = CHECK_RATE;

    long nodeLimit = 0;
    long timeLimit = 0;
    long startTime;

    int[][][] historyTable = new int[2][64][64];

    public TranspositionTable transpositionTable = new TranspositionTable(64);

    // ==================== QUIESCENCE SEARCH ====================
    public int qsearch(Board board, int alpha, int beta, int ply) {
        
        if (stopSearch || checkTime(false)) {
            stopSearch = true;
            return 0;
        }

        if (ply >= MAX_PLY) {
            return evaluate(board);
        }

        nodes++;

        // Si en échec, on génère toutes les évasions comme le code 2
        boolean inCheck = board.isKingInCheck(board.whiteTurn);
        
        if (inCheck) {
            PackedMoveList moves = board.getLegalMoves();
            
            if (moves.size() == 0) {
                return matedInPly(ply);
            }
            
            orderMoves(moves, 0, board, ply);
            
            int bestValue = -VALUE_INFINITE;
            
            for (int i = 0; i < moves.size(); i++) {
                board.makeMove(moves.get(i));
                int score = -qsearch(board, -beta, -alpha, ply + 1);
                board.undoMove();

                if (stopSearch) return 0;

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

        // Stand pat
        int bestValue = evaluate(board);

        if (bestValue >= beta) {
            return bestValue;
        }

        if (bestValue > alpha) {
            alpha = bestValue;
        }

        PackedMoveList moves = board.getCaptureMoves();
        orderQMoves(moves);

        for (int i = 0; i < moves.size(); i++) {
            int move = moves.get(i);
            int capturedPiece = PackedMove.getCaptured(move);

            // Delta pruning standard
            if (Board.PIECE_SCORES[capturedPiece] + 400 + bestValue < alpha 
                && !PackedMove.isPromotion(move)) {
                continue;
            }

            board.makeMove(move);
            int score = -qsearch(board, -beta, -alpha, ply + 1);
            board.undoMove();

            if (stopSearch) return 0;

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

    // ==================== MAIN SEARCH ====================
    public int absearch(Board board, int depth, int alpha, int beta, int ply, boolean isPV) {
        
        if (checkTime(false)) {
            stopSearch = true;
            return 0;
        }

        if (ply >= MAX_PLY) {
            return evaluate(board);
        }

        pvLengths[ply] = ply;
        boolean rootNode = (ply == 0);
        long hashKey = board.zobristKey;

        // Draw detection
        if (!rootNode) {
            if (isThreefoldRepetition(hashKey)) {
                return 0;
            }

            // Mate distance pruning
            alpha = Math.max(alpha, matedInPly(ply));
            beta = Math.min(beta, mateInPly(ply + 1));
            if (alpha >= beta) {
                return alpha;
            }
        }

        // Quiescence
        if (depth <= 0) {
            return qsearch(board, alpha, beta, ply);
        }

        boolean inCheck = board.isKingInCheck(board.whiteTurn);

        // TT probe
        TranspositionTable.Entry tte = transpositionTable.get(hashKey);
        boolean ttHit = (tte != null);
        int ttMove = ttHit ? tte.bestMove : 0;
        int ttScore = ttHit ? scoreFromTT(tte.value, ply) : 0;

        // TT cutoff - EXACT uniquement comme le code 2
        if (!rootNode && ttHit && tte.depth >= depth) {
            if (tte.flag == TranspositionTable.Entry.EXACT) {
                return ttScore;
            } else if (!isPV) {
                // Cutoff bounds seulement si non-PV
                if (tte.flag == TranspositionTable.Entry.LOWERBOUND && ttScore >= beta) {
                    return ttScore;
                } else if (tte.flag == TranspositionTable.Entry.UPPERBOUND && ttScore <= alpha) {
                    return ttScore;
                }
            }
        }

        int staticEval = evaluate(board);

        // Null move pruning - FIXED avec depth - R - 1
        if (!isPV && !inCheck && depth >= 3 && board.hasNonPawnMaterial() 
            && staticEval >= beta && Math.abs(beta) < MATE_BOUND) {
            
            int R = 4; // Réduction fixe comme avant mais avec la bonne formule
            
            board.makeNullMove();
            int score = -absearch(board, depth - R - 1, -beta, -beta + 1, ply + 1, false);
            board.undoNullMove();

            if (stopSearch) return 0;

            if (score >= beta && score < MATE_BOUND) {
                return score;
            }
        }

        int oldAlpha = alpha;
        int bestScore = -VALUE_INFINITE;
        int bestMove = 0;
        int madeMoves = 0;

        PackedMoveList moves = board.getLegalMoves();
        orderMoves(moves, ttMove, board, ply);

        boolean firstMove = true;

        for (int i = 0; i < moves.size(); i++) {
            int move = moves.get(i);
            
            madeMoves++;
            nodes++;

            board.makeMove(move);
            hashHistory[repSize++] = board.zobristKey;

            boolean givesCheck = board.isKingInCheck(!board.whiteTurn);
            boolean isCapture = PackedMove.isCapture(move);
            
            int score;

            // PVS (Principal Variation Search) comme le code 2
            if (firstMove) {
                // Premier mouvement: full window
                score = -absearch(board, depth - 1, -beta, -alpha, ply + 1, isPV);
                firstMove = false;
            } else {
                // LMR plus conservateur
                int reduction = 0;
                
                if (depth >= 3 && i >= 4 && !isCapture && !givesCheck && !inCheck) {
                    reduction = 1;
                    
                    if (depth >= 6 && i >= 8) {
                        reduction = 2;
                    }
                }

                int searchDepth = depth - 1 - reduction;

                // Null window search
                score = -absearch(board, searchDepth, -alpha - 1, -alpha, ply + 1, false);

                // Re-search si la réduction était trop optimiste
                if (score > alpha && reduction > 0) {
                    score = -absearch(board, depth - 1, -alpha - 1, -alpha, ply + 1, false);
                }

                // Re-search avec full window si dans la fenêtre
                if (score > alpha && score < beta) {
                    score = -absearch(board, depth - 1, -beta, -alpha, ply + 1, true);
                }
            }

            board.undoMove();
            repSize--;

            if (stopSearch) return 0;

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;

                // Update PV
                principalVariations[ply][ply] = move;
                for (int j = ply + 1; j < pvLengths[ply + 1]; j++) {
                    principalVariations[ply][j] = principalVariations[ply + 1][j];
                }
                pvLengths[ply] = pvLengths[ply + 1];

                if (score > alpha) {
                    alpha = score;

                    if (score >= beta) {
                        // Beta cutoff - update heuristics
                        if (!isCapture) {
                            int bonus = depth * depth;
                            int color = board.whiteTurn ? 0 : 1;
                            int from = PackedMove.getFrom(move);
                            int to = PackedMove.getTo(move);

                            int current = historyTable[color][from][to];
                            int delta = bonus - (current * Math.abs(bonus)) / 16384;
                            historyTable[color][from][to] = current + delta;

                            // Killers
                            if (killerMoves[ply][0] != move) {
                                killerMoves[ply][1] = killerMoves[ply][0];
                                killerMoves[ply][0] = move;
                            }
                        }
                        break;
                    }
                }
            }
        }

        // Checkmate / Stalemate
        if (madeMoves == 0) {
            if (inCheck) {
                return matedInPly(ply);
            } else {
                return 0;
            }
        }

        // TT store
        int flag;
        if (bestScore >= beta) {
            flag = TranspositionTable.Entry.LOWERBOUND;
        } else if (alpha != oldAlpha) {
            flag = TranspositionTable.Entry.EXACT;
        } else {
            flag = TranspositionTable.Entry.UPPERBOUND;
        }

        if (!stopSearch) {
            transpositionTable.put(hashKey, bestMove, scoreToTT(bestScore, ply), depth, flag);
        }

        return bestScore;
    }

    // ==================== HELPERS ====================

    private boolean isThreefoldRepetition(long key) {
        int count = 0;
        for (int i = repSize - 2; i >= 0; i -= 2) {
            if (hashHistory[i] == key) {
                count++;
                if (count == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    // ==================== ITERATIVE DEEPENING ====================
    
    public void iterativeDeepening(Board board, int depthLimit) {
        nodes = 0;
        int score = 0;
        int bestMove = 0;

        startTime = System.nanoTime();

        int window = 30; // Comme le code 2
        int previousScore = 0;

        for (int depth = 1; depth <= depthLimit; depth++) {
            int alpha, beta;
            
            if (Math.abs(previousScore) >= MATE_BOUND - 512) {
                alpha = -VALUE_INFINITE;
                beta = +VALUE_INFINITE;
            } else {
                alpha = previousScore - window;
                beta = previousScore + window;
            }

            score = absearch(board, depth, alpha, beta, 0, true);

            // Aspiration window failed - re-search progressif comme le code 2
            if (!stopSearch && !checkTime(true) && (score <= alpha || score >= beta)) {
                int w = window;
                do {
                    w *= 2;
                    alpha = previousScore - w;
                    beta = previousScore + w;
                    score = absearch(board, depth, alpha, beta, 0, true);
                } while (!stopSearch && !checkTime(true) && (score <= alpha || score >= beta));
            }

            if (stopSearch || checkTime(true)) {
                break;
            }

            bestMove = principalVariations[0][0];
            previousScore = score;

            long endTime = System.nanoTime();
            printSearchInfo(depth, score, nodes, endTime - startTime);
        }

        if (bestMove == 0) {
            bestMove = principalVariations[0][0];
        }

        Move best = PackedMove.unpack(bestMove);
        System.out.println("bestmove " + best);
    }

    // ==================== MOVE ORDERING ====================

    public static final int[][] mvvLva = {
        { 105, 205, 305, 405, 505, 605 },
        { 104, 204, 304, 404, 504, 604 },
        { 103, 203, 303, 403, 503, 603 },
        { 102, 202, 302, 402, 502, 602 },
        { 101, 201, 301, 401, 501, 601 },
        { 100, 200, 300, 400, 500, 600 }
    };

    public int scoreMove(int move, int ttMove, Board board, int ply) {
        if (move == ttMove) {
            return 1_000_000;
        }

        if (PackedMove.isCapture(move)) {
            return 500_000 + mvvLva[PackedMove.getCaptured(move)][PackedMove.getPieceFrom(move)];
        }

        // Killers
        if (move == killerMoves[ply][0]) {
            return 90_000;
        }
        if (move == killerMoves[ply][1]) {
            return 80_000;
        }

        // History
        int from = PackedMove.getFrom(move);
        int to = PackedMove.getTo(move);
        return historyTable[board.whiteTurn ? 0 : 1][from][to];
    }

    public void orderMoves(PackedMoveList moves, int ttMove, Board board, int ply) {
        int size = moves.size();
        int[] m = moves.moves;
        int[] scores = new int[size];

        for (int i = 0; i < size; i++) {
            scores[i] = scoreMove(m[i], ttMove, board, ply);
        }

        // Insertion sort
        for (int i = 1; i < size; i++) {
            int move = m[i];
            int score = scores[i];
            int j = i - 1;

            while (j >= 0 && scores[j] < score) {
                m[j + 1] = m[j];
                scores[j + 1] = scores[j];
                j--;
            }

            m[j + 1] = move;
            scores[j + 1] = score;
        }
    }

    public void orderQMoves(PackedMoveList moves) {
        int size = moves.size();
        int[] m = moves.moves;

        for (int i = 1; i < size; i++) {
            int move = m[i];
            int score = mvvLva[PackedMove.getCaptured(move)][PackedMove.getPieceFrom(move)];
            int j = i - 1;

            while (j >= 0 && mvvLva[PackedMove.getCaptured(m[j])][PackedMove.getPieceFrom(m[j])] < score) {
                m[j + 1] = m[j];
                j--;
            }
            m[j + 1] = move;
        }
    }

    // ==================== UTILITIES ====================

    @Override
    public int evaluate(Board board) {
        return board.whiteTurn ? board.evaluate() : -board.evaluate();
    }

    private boolean checkTime(boolean iter) {
        if (stopSearch) {
            return true;
        }

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
        if (currentTime >= startTime + timeLimit) {
            return true;
        }

        return false;
    }

    public String getPV() {
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
        if (score >= MATE_BOUND) {
            return score - ply;
        } else if (score <= -MATE_BOUND) {
            return score + ply;
        }
        return score;
    }

    public int scoreToTT(int score, int ply) {
        if (score >= MATE_BOUND) {
            return score + ply;
        } else if (score <= -MATE_BOUND) {
            return score - ply;
        }
        return score;
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
                depth, convertScore(score), nodes, lastNps, timeMs, 
                this.transpositionTable.hashfull(), getPV());
    }

    private void resetSearch() {
        for (int i = 0; i < pvLengths.length; i++) {
            pvLengths[i] = 0;
        }
        for (int i = 0; i < principalVariations.length; i++) {
            for (int j = 0; j < principalVariations[i].length; j++) {
                principalVariations[i][j] = 0;
            }
        }

        nodes = 0;
        stopSearch = false;
        startTime = 0;
        checks = CHECK_RATE;
        repSize = 0;

        historyTable = new int[2][64][64];
        killerMoves = new int[MAX_PLY][2];
    }

    @Override
    public Move search(Board board, int wtime, int btime, int winc, int binc, 
                       int movetime, int depth, long maxNodes) {
        resetSearch();
        nodeLimit = maxNodes;

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