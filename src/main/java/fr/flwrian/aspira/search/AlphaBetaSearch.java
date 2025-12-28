package fr.flwrian.aspira.search;

import java.util.Locale;

import fr.flwrian.aspira.board.Board;
import fr.flwrian.aspira.hash.TranspositionTable;
import fr.flwrian.aspira.move.Move;
import fr.flwrian.aspira.move.PackedMove;
import fr.flwrian.aspira.move.PackedMoveList;

public class AlphaBetaSearch implements SearchAlgorithm {

    private long lastNodeCount = 0;
    private long lastNPS = 0;

    public long getLastNPS() {
        return lastNPS;
    }

    public long getLastNodeCount() {
        return lastNodeCount;
    }

    private long nodes = 0;
    private long cutoffs = 0;

    // Stop search flag
    private boolean stopSearch = false;

    public void setStopSearch(boolean b) {
        stopSearch = b;
    }

    // ==== Node limit ====
    private long maxNodes = 0;
    private boolean nodesExceeded = false;

    // ==== Time control ====
    private long searchStartTime;
    private long timeLimitNanos;
    private boolean timeExceeded = false;

    // ==== Transposition Table ====
    private final TranspositionTable tt = new TranspositionTable(64); // MB
    private long ttHits = 0;
    private long ttStores = 0;

    // ==== Mate / scores ====
    public static final int MATE = 32000;
    public static final int MATE_BOUND = 31000;
    public static final int DRAW = 0;

    private static int toTTScore(int score, int ply) {
        if (score >= MATE_BOUND)
            return score + ply;
        if (score <= -MATE_BOUND)
            return score - ply;
        return score;
    }

    private static int fromTTScore(int score, int ply) {
        if (score >= MATE_BOUND)
            return score - ply;
        if (score <= -MATE_BOUND)
            return score + ply;
        return score;
    }

    private static int preferShorterMates(int s) {
        if (s >= MATE_BOUND)
            return s - 1;
        if (s <= -MATE_BOUND)
            return s + 1;
        return s;
    }

    private int evalSideToMove(Board b) {
        int e = evaluate(b); // + = bon pour Blanc
        return b.whiteTurn ? e : -e; // orienté côté trait
    }

    @Override
    public Move search(Board board, int wtime, int btime, int winc, int binc, int movetime, int depth, long maxNodes) {
        long bestPackedMove = 0L;
        searchStartTime = System.nanoTime();

        // Node limit
        this.maxNodes = maxNodes;
        nodesExceeded = false;

        // Temps
        if (movetime > 0) {
            timeLimitNanos = movetime * 1_000_000L;
        } else {
            int time = board.whiteTurn ? wtime : btime;
            int inc = board.whiteTurn ? winc : binc;
            int timePerMoveMs = (time / 20) + (inc / 2);
            timeLimitNanos = (long) (timePerMoveMs * 0.9 * 1_000_000L);
        }
        timeExceeded = false;

        int window = 30;
        int prevScore = 0;

        for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {
            nodes = 0;
            cutoffs = 0;
            ttHits = 0;
            ttStores = 0;
            long startTime = System.nanoTime();

            int alpha, beta;
            if (Math.abs(prevScore) >= MATE_BOUND - 512) {
                alpha = -MATE;
                beta = +MATE;
            } else {
                alpha = prevScore - window;
                beta = prevScore + window;
            }

            MoveValue result = negamax(board, currentDepth, alpha, beta, 0, true);

            if (!timeExceeded && (result.value <= alpha || result.value >= beta)) {
                // aspiration progressive (évite re-search trop large d’un coup)
                int w = window;
                do {
                    w *= 2;
                    alpha = prevScore - w;
                    beta = prevScore + w;
                    result = negamax(board, currentDepth, alpha, beta, 0, true);
                } while (!timeExceeded && (result.value <= alpha || result.value >= beta));
            }

            long endTime = System.nanoTime();
            if (timeExceeded)
                break;

            bestPackedMove = result.move;
            prevScore = result.value;

            printSearchInfo(currentDepth, result.value, nodes, endTime - startTime, cutoffs, ttHits, ttStores,
                    result.pv);
            lastNodeCount = nodes;
            lastNPS = (nodes * 1_000_000_000L) / (endTime - startTime);
        }

        if (bestPackedMove == 0L) {
            PackedMoveList moves = board.getLegalMoves();
            if (moves.size() > 0)
                bestPackedMove = moves.get(0);
            else
                return null;
        }

        Move best = PackedMove.unpack(bestPackedMove);
        System.out.println("bestmove " + best);
        return best;
    }

    private void printSearchInfo(int depth, int score, long nodes, long durationNanos, long cutoffs, long ttHits,
            long ttStores, String pv) {
        double timeMs = durationNanos / 1_000_000.0;
        double rawNps = nodes / (durationNanos / 1_000_000_000.0);
        double cutoffRatio = 100.0 * cutoffs / Math.max(nodes, 1);
        String npsStr = formatNps(rawNps);
        String ttHitStr = String.format(Locale.US, "%d/%d", ttHits, ttStores);
        String ttHitRatio = String.format(Locale.US, "%.2f%%", 100.0 * ttHits / Math.max(ttStores, 1));

        if (Math.abs(score) >= MATE_BOUND) {
            int mateIn = (MATE - Math.abs(score) + 1) / 2;
            String mateScore = score > 0 ? "mate " + mateIn : "mate -" + mateIn;
            System.out.printf(Locale.US, "info depth %d score %s nodes %d nps %d time %.0f pv %s\n",
                    depth, mateScore, nodes, (long) rawNps, timeMs, pv);
        } else {
            System.out.printf(Locale.US, "info depth %d score cp %d nodes %d nps %d time %.0f hashfull %s pv %s\n",
                    depth, score, nodes, (long) rawNps, timeMs, tt.hashfull(), pv);
        }
    }

    private String formatNps(double nps) {
        if (nps >= 1_000_000)
            return String.format(Locale.US, "%.1fM", nps / 1_000_000);
        if (nps >= 1_000)
            return String.format(Locale.US, "%.1fk", nps / 1_000);
        return String.format(Locale.US, "%.0f", nps);
    }

    // =========================
    // NEGAMAX + PVS
    // =========================
    private MoveValue negamax(Board board, int depth, int alpha, int beta, int ply, boolean isPV) {
        // TIME & NODES
        if (System.nanoTime() - searchStartTime > timeLimitNanos || stopSearch) {
            timeExceeded = true;
            return new MoveValue(0L, 0);
        }

        nodes++;

        if (maxNodes > 0 && nodes >= maxNodes) {
            nodesExceeded = true;
            timeExceeded = true;
            return new MoveValue(0L, 0);
        }

        if (board.isThreefoldRepetition())
            return new MoveValue(0L, DRAW);

        // Mate Distance Pruning
        if (alpha < -MATE + ply + 1)
            alpha = -MATE + ply + 1;
        if (beta > MATE - ply - 1)
            beta = MATE - ply - 1;
        if (alpha >= beta)
            return new MoveValue(0L, alpha);

        // TT probe
        long key = board.zobristKey;
        TranspositionTable.Entry entry = tt.get(key);
        if (entry != null && entry.depth >= depth) {
            ttHits++;
            int ttVal = fromTTScore(entry.value, ply);
            if (entry.flag == TranspositionTable.Entry.EXACT) {
                return new MoveValue(entry.bestMove, ttVal, "");
            } else if (entry.flag == TranspositionTable.Entry.LOWERBOUND) {
                if (ttVal > alpha)
                    alpha = ttVal;
            } else if (entry.flag == TranspositionTable.Entry.UPPERBOUND) {
                if (ttVal < beta)
                    beta = ttVal;
            }
            if (alpha >= beta)
                return new MoveValue(entry.bestMove, ttVal, "");
        }

        // Shawn absolute cinema code
        if (!isPV
                && depth >= 3
                && ply > 0
                && !board.isKingInCheck(board.whiteTurn)
                && board.hasNonPawnMaterial()) {
            if (beta < MATE_BOUND) {
                int R = 4;

                board.makeNullMove();
                int score = -negamax(board, depth - R,
                        -beta, -(beta - 1),
                        ply + 1, false).value;
                board.undoNullMove();

                if (score >= beta && score < MATE_BOUND) {
                    return new MoveValue(0L, score);
                }
            }
        }

        // Feuille => QS
        if (depth <= 0)
            return qsearch(board, alpha, beta, ply);

        // Génération mouvements
        PackedMoveList moves = board.getLegalMoves();
        boolean inCheck = board.isKingInCheck(board.whiteTurn);
        if (moves.size() == 0) {
            if (inCheck)
                return new MoveValue(0L, -MATE + ply);
            return new MoveValue(0L, DRAW);
        }

        // Ordre: ttMove seulement si EXACT (safe)
        final long ttMove = (entry != null) ? entry.bestMove : 0L;
        if (ttMove != 0L)
            moves.prioritize(ttMove);
        moves.sortByScore();

        int originalAlpha = alpha;
        int originalBeta = beta;

        long bestMove = 0L;
        String bestPv = "";
        int bestScore = -MATE;

        // LMR params
        final int LMR_MIN_DEPTH = 3;
        final int LMR_MIN_MOVES = 3;
        final int LMR_REDUCTION = 1;

        boolean firstMove = true;
        final boolean criticalDepth = (depth <= 2); // garde-fous

        for (int i = 0; i < moves.size(); i++) {
            long move = moves.get(i);

            board.makeMove(move);

            boolean givesCheck = board.isKingInCheck(board.whiteTurn);
            int newDepth = depth - 1;

            int scoreRaw;
            String childPv;

            // === Pas de LMR ni PVS en profondeur critique ou si check ===
            boolean isCapture = PackedMove.isCapture(move);
            boolean allowLMR = !criticalDepth && !inCheck && !givesCheck;
            boolean canReduce = allowLMR && depth >= LMR_MIN_DEPTH && i >= LMR_MIN_MOVES && !isCapture;

            if (firstMove || criticalDepth) {
                MoveValue child = negamax(board, newDepth, -beta, -alpha, ply + 1, isPV);
                scoreRaw = -child.value;
                childPv = child.pv;
                firstMove = false;
            } else {
                int searchDepth = canReduce ? (newDepth - LMR_REDUCTION) : newDepth;

                // PVS null-window
                MoveValue red = negamax(board, searchDepth, -(alpha + 1), -alpha, ply + 1, false);
                scoreRaw = -red.value;
                childPv = red.pv;

                if (scoreRaw > alpha) {
                    MoveValue full = negamax(board, newDepth, -beta, -alpha, ply + 1, true);
                    scoreRaw = -full.value;
                    childPv = full.pv;
                }
            }

            board.undoMove();
            if (timeExceeded)
                return new MoveValue(0L, 0);

            int scoreCmp = preferShorterMates(scoreRaw);

            if (scoreCmp > preferShorterMates(bestScore)) {
                bestScore = scoreRaw;
                bestMove = move;
                String pv = PackedMove.unpack(move).toString();
                if (!childPv.isEmpty())
                    pv += " " + childPv;
                bestPv = pv;
            }

            if (scoreCmp > preferShorterMates(alpha))
                alpha = scoreRaw;

            if (alpha >= beta) {
                cutoffs++;
                break;
            }
        }

        int flag;
        if (bestScore <= originalAlpha)
            flag = TranspositionTable.Entry.UPPERBOUND;
        else if (bestScore >= originalBeta)
            flag = TranspositionTable.Entry.LOWERBOUND;
        else
            flag = TranspositionTable.Entry.EXACT;

        tt.put(key, bestMove, toTTScore(bestScore, ply), depth, flag);
        ttStores++;

        return new MoveValue(bestMove, bestScore, bestPv);
    }

    // =========================
    // QS
    // =========================
    private MoveValue qsearch(Board board, int alpha, int beta, int ply) {
        if (System.nanoTime() - searchStartTime > timeLimitNanos || stopSearch) {
            timeExceeded = true;
            return new MoveValue(0L, 0);
        }

        // In-check: évasions complètes
        if (board.isKingInCheck(board.whiteTurn)) {
            PackedMoveList evasions = board.getLegalMoves();
            if (evasions.size() == 0)
                return new MoveValue(0L, -MATE + ply);
            evasions.sortByScore();

            long bestMove = 0L;
            String bestPv = "";
            for (int i = 0; i < evasions.size(); i++) {
                long mv = evasions.get(i);
                board.makeMove(mv);
                MoveValue childRes = qsearch(board, -beta, -alpha, ply + 1);
                int sRaw = -childRes.value;
                board.undoMove();
                if (timeExceeded)
                    return new MoveValue(0L, 0);

                int sCmp = preferShorterMates(sRaw);

                if (sCmp > preferShorterMates(alpha)) {
                    alpha = sRaw;
                    bestMove = mv;
                    String pv = PackedMove.unpack(mv).toString();
                    if (!childRes.pv.isEmpty())
                        pv += " " + childRes.pv;
                    bestPv = pv;
                }
                if (alpha >= beta) {
                    cutoffs++;
                    break;
                }
            }
            return new MoveValue(bestMove, alpha, bestPv);
        }

        // Stand pat
        int standPat = evalSideToMove(board);
        if (standPat >= beta)
            return new MoveValue(0L, beta);
        if (standPat > alpha)
            alpha = standPat;

        // Captures only
        PackedMoveList caps = board.getCaptureMoves();
        if (caps.size() == 0)
            return new MoveValue(0L, alpha);
        caps.sortByScore();

        long bestMove = 0L;
        String bestPv = "";
        for (int i = 0; i < caps.size(); i++) {
            long mv = caps.get(i);
            board.makeMove(mv);
            MoveValue childRes = qsearch(board, -beta, -alpha, ply + 1);
            int sRaw = -childRes.value;
            board.undoMove();
            if (timeExceeded)
                return new MoveValue(0L, 0);

            int sCmp = preferShorterMates(sRaw);

            if (sCmp > preferShorterMates(alpha)) {
                alpha = sRaw;
                bestMove = mv;
                String pv = PackedMove.unpack(mv).toString();
                if (!childRes.pv.isEmpty())
                    pv += " " + childRes.pv;
                bestPv = pv;
            }
            if (alpha >= beta) {
                cutoffs++;
                break;
            }
        }
        return new MoveValue(bestMove, alpha, bestPv);
    }

    @Override
    public int evaluate(Board board) {
        return board.evaluate();
    }

    @Override
    public String getName() {
        return "NewChessAlgorithm";
    }

    public static class MoveValue {
        public final long move;
        public final int value;
        public String pv = "";

        public MoveValue(long move, int value) {
            this.move = move;
            this.value = value;
            if (move != 0L) {
                Move printable = PackedMove.unpack(move);
                this.pv = printable.toString();
            }
        }

        public MoveValue(long move, int value, String pv) {
            this.move = move;
            this.value = value;
            this.pv = (pv == null) ? "" : pv;
        }
    }
}