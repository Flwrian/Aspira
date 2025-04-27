
package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.PackedMove;
import com.bitboard.PackedMoveList;

import java.util.Locale;

public class NewChessAlgorithm implements ChessAlgorithm {

    private long nodes = 0;
    private long cutoffs = 0;
    private final boolean DEBUG_FLAG = false;

    // === Timeout Control ===
    private long searchStartTime;
    private long timeLimitNanos;
    private boolean timeExceeded = false;

    // === Transposition Table ===
    private final TranspositionTable tt = new TranspositionTable(64); // 64MB
    private long ttHits = 0;
    private long ttStores = 0;

    private static final int ply = 0;

    public static final int MATE_SCORE = 80000;



    @Override
    public Move search(BitBoard board, int wtime, int btime, int winc, int binc, int movetime, int depth) {
        long bestPackedMove = 0L;
        searchStartTime = System.nanoTime();

        // ==== Gestion du temps ====
        if (movetime > 0) {
            timeLimitNanos = movetime * 1_000_000L;
        } else {
            int time = board.whiteTurn ? wtime : btime;
            int inc = board.whiteTurn ? winc : binc;
            int timePerMoveMs = (time / 20) + (inc / 2);
            timeLimitNanos = (long) (timePerMoveMs * 0.9 * 1_000_000L);
        }

        timeExceeded = false;

        if (DEBUG_FLAG) {
            String separator = "╔" + "═".repeat(110) + "╗";
            String header = String.format("║ %-6s │ %-6s │ %-12s │ %-10s │ %-10s │ %-9s │ %-7s │ %-17s │ %-7s ║",
                    "Depth", "Score", "Nodes", "NPS", "Time (ms)", "Cutoffs", "Cut %", "TT (hits/stores)", "TT hit%");
            String divider = "╟" + "─".repeat(110) + "╢";
            System.out.println(separator);
            System.out.println(header);
            System.out.println(divider);
        }

        int window = 50; // Aspiration window size (centipawns)
        int prevScore = 0;
        for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {
            nodes = 0;
            cutoffs = 0;
            ttHits = 0;
            ttStores = 0;
            long startTime = System.nanoTime();

            int alpha = prevScore - window;
            int beta = prevScore + window;
            MoveValue result = alphaBeta(board, currentDepth, alpha, beta, board.whiteTurn, ply);

            // If fail-low or fail-high, re-search with full window
            if (!timeExceeded && (result.value <= alpha || result.value >= beta)) {
                if (result.value <= alpha) {
                    alpha = Integer.MIN_VALUE;
                }
                if (result.value >= beta) {
                    beta = Integer.MAX_VALUE;
                }
                result = alphaBeta(board, currentDepth, alpha, beta, board.whiteTurn, ply);
            }

            long endTime = System.nanoTime();

            // Interrompu ? On garde le dernier coup valide
            if (timeExceeded) {
                if (DEBUG_FLAG) {
                    System.out.println("Time limit exceeded during search. Stopping at depth " + (currentDepth - 1));
                }
                break;
            }

            bestPackedMove = result.move;
            prevScore = result.value;

            printSearchInfo(currentDepth, result.value, nodes, endTime - startTime, cutoffs, ttHits, ttStores, result.pv);
        }

        if (DEBUG_FLAG) {
            System.out.println("╚" + "═".repeat(110) + "╝");
        }

        // Si il n'y a pas de coup valide, on renvoie un aléatoire
        if (bestPackedMove == 0L) {
            PackedMoveList moves = board.getLegalMoves();
            if (moves.size() > 0) {
                bestPackedMove = moves.get(0);
            } else {
                return null;
            }
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

            if (DEBUG_FLAG) {
                String scoreStr = Math.abs(score) >= MATE_SCORE - 1000 ? 
                        String.format("M%d", (MATE_SCORE - Math.abs(score) + 1) / 2) : 
                        String.valueOf(score);
                System.out.printf(Locale.US, "║ %-6d │ %-6s │ %-12d │ %-10s │ %-10.1f │ %-9d │ %-6.2f%% │ %-17s │ %-7s ║\n",
                        depth, scoreStr, nodes, npsStr, timeMs, cutoffs, cutoffRatio, ttHitStr, ttHitRatio);
            } else {
                if (Math.abs(score) >= MATE_SCORE - 1000) {
                    int mateIn = (MATE_SCORE - Math.abs(score) + 1) / 2;
                    String mateScore = score > 0 ? "mate " + mateIn : "mate -" + mateIn;
                    System.out.printf(Locale.US, "info depth %d score %s nodes %d nps %d time %.0f pv %s\n",
                            depth, mateScore, nodes, (long) rawNps, timeMs, pv);
                } else {
                    System.out.printf(Locale.US, "info depth %d score cp %d nodes %d nps %d time %.0f pv %s\n",
                            depth, score, nodes, (long) rawNps, timeMs, pv);
                }
            }
        }

    private String formatNps(double nps) {
        if (nps >= 1_000_000)
            return String.format(Locale.US, "%.1fM", nps / 1_000_000);
        if (nps >= 1_000)
            return String.format(Locale.US, "%.1fk", nps / 1_000);
        return String.format(Locale.US, "%.0f", nps);
    }

    public MoveValue alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer, int ply) {

        // === TIME CHECK ===
        if (System.nanoTime() - searchStartTime > timeLimitNanos) {
            timeExceeded = true;
            return new MoveValue(0L, 0); // Valeur arbitraire (ne sera pas utilisée)
        }

        long key = board.zobristKey;
        TranspositionTable.Entry entry = tt.get(key);
        if (entry != null && entry.depth >= depth) {
            ttHits++;
            if (entry.flag == TranspositionTable.Entry.EXACT) {
                return new MoveValue(entry.bestMove, entry.value);
            } else if (entry.flag == TranspositionTable.Entry.LOWERBOUND && entry.value > alpha) {
                alpha = entry.value;
            } else if (entry.flag == TranspositionTable.Entry.UPPERBOUND && entry.value < beta) {
                beta = entry.value;
            }

            if (alpha >= beta) {
                return new MoveValue(entry.bestMove, entry.value);
            }
        }

        if (depth == 0) {
            return quiescenceSearch(board, alpha, beta, maximizingPlayer);
        }
              
        nodes++;

        PackedMoveList moves = board.getLegalMoves();

        if (moves.size() == 0 && !board.isKingInCheck(board.whiteTurn)) {
            return new MoveValue(0L, 0); // Stalemate
        } else if (moves.size() == 0) {
            return board.whiteTurn ? new MoveValue(0L, -MATE_SCORE + ply) : new MoveValue(0L, MATE_SCORE - ply); // Checkmate
        }

        // Sort tt move first
        final long ttMove = (entry != null) ? entry.bestMove : 0L;
        if (ttMove != 0L) {
            moves.prioritize(ttMove);
        }
        moves.sortByScore();

        long bestMove = 0L;
        int bestValue = maximizingPlayer ? -90000 : 90000;
        String bestPv = "";

        int originalAlpha = alpha;

        // === Late Move Reduction (LMR) parameters ===
        final int LMR_MIN_DEPTH = 3; // Only apply LMR at depth >= 3
        final int LMR_MIN_MOVES = 3; // Only reduce after this many moves
        final int LMR_REDUCTION = 1; // Reduce by 1 ply

        for (int i = 0; i < moves.size(); i++) {
            long move = moves.get(i);

            board.makeMove(move);
            ply++;

            MoveValue child;
            boolean reduced = false;

            // LMR: Reduce depth for late, non-capture, non-pv moves
            boolean isFirstMove = (i == 0);
            boolean isCapture = PackedMove.isCapture(move);
            boolean canReduce = depth >= LMR_MIN_DEPTH && i >= LMR_MIN_MOVES && !isFirstMove && !isCapture;

            if (canReduce) {
                // Reduced search
                child = alphaBeta(board, depth - 1 - LMR_REDUCTION, alpha, beta, !maximizingPlayer, ply);
                reduced = true;
                // If reduction produces a new best, re-search at full depth
                int value = child.value;
                boolean failHigh = maximizingPlayer ? value > alpha : value < beta;
                if (failHigh) {
                    child = alphaBeta(board, depth - 1, alpha, beta, !maximizingPlayer, ply);
                }
            } else {
                child = alphaBeta(board, depth - 1, alpha, beta, !maximizingPlayer, ply);
            }

            board.undoMove();
            ply--;

            if (timeExceeded)
                return new MoveValue(0L, 0); // stop immediately

            int value = child.value;

            if (maximizingPlayer) {
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                    // Update PV: current move + child's PV
                    Move printableMove = PackedMove.unpack(move);
                    bestPv = printableMove.toString();
                    if (!child.pv.isEmpty()) {
                        bestPv += " " + child.pv;
                    }
                }
                alpha = Math.max(alpha, value);
            } else {
                if (value < bestValue) {
                    bestValue = value;
                    bestMove = move;
                    // Update PV: current move + child's PV
                    Move printableMove = PackedMove.unpack(move);
                    bestPv = printableMove.toString();
                    if (!child.pv.isEmpty()) {
                        bestPv += " " + child.pv;
                    }
                }
                beta = Math.min(beta, value);
            }

            if (alpha >= beta) {
                cutoffs++;
                break;
            }
        }

        int flag = TranspositionTable.Entry.EXACT;
        if (bestValue <= originalAlpha) {
            flag = TranspositionTable.Entry.UPPERBOUND;
        } else if (bestValue >= beta) {
            flag = TranspositionTable.Entry.LOWERBOUND;
        }

        // Store PV in TT if possible
        tt.put(key, bestMove, bestValue, depth, flag);
        ttStores++;

        return new MoveValue(bestMove, bestValue, bestPv);
    }

    private MoveValue quiescenceSearch(BitBoard board, int alpha, int beta, boolean maximizingPlayer) {
        nodes++;

        // Check if time limit exceeded
        if (System.nanoTime() - searchStartTime > timeLimitNanos) {
            timeExceeded = true;
            return new MoveValue(0L, 0);
        }

        // Stand pat - evaluate the position if we don't make any move
        int standPat = evaluate(board);
        if (maximizingPlayer) {
            if (standPat >= beta) {
                return new MoveValue(0L, beta);
            }
            if (standPat > alpha) {
                alpha = standPat;
            }
        } else {
            if (standPat <= alpha) {
                return new MoveValue(0L, alpha);
            }
            if (standPat < beta) {
                beta = standPat;
            }
        }

        // Get only captures
        PackedMoveList captures = board.getCaptureMoves();
        if (captures.size() == 0) {
            return new MoveValue(0L, standPat);
        }

        captures.sortByScore();
        
        long bestMove = 0L;
        int bestValue = maximizingPlayer ? alpha : beta;
        String bestPv = "";

        for (int i = 0; i < captures.size(); i++) {
            long move = captures.get(i);
            board.makeMove(move);
            MoveValue child = quiescenceSearch(board, alpha, beta, !maximizingPlayer);
            board.undoMove();

            if (timeExceeded)
                return new MoveValue(0L, 0);

            int value = child.value;

            if (maximizingPlayer) {
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                    Move printableMove = PackedMove.unpack(move);
                    bestPv = printableMove.toString();
                    if (!child.pv.isEmpty()) {
                        bestPv += " " + child.pv;
                    }
                }
                alpha = Math.max(alpha, value);
            } else {
                if (value < bestValue) {
                    bestValue = value;
                    bestMove = move;
                    Move printableMove = PackedMove.unpack(move);
                    bestPv = printableMove.toString();
                    if (!child.pv.isEmpty()) {
                        bestPv += " " + child.pv;
                    }
                }
                beta = Math.min(beta, value);
            }
            
            if (alpha >= beta) {
                cutoffs++;
                break;
            }
        }

        return new MoveValue(bestMove, bestValue, bestPv);
    }

    @Override
    public int evaluate(BitBoard board) {
        return board.evaluate();
    }

    @Override
    public String getName() {
        return "NewChessAlgorithm";
    }

    public class MoveValue {
        public final long move;
        public final int value;
        public String pv = ""; // Principal Variation
    
        public MoveValue(long move, int value) {
            this.move = move;
            this.value = value;
            // update pv
            if (move != 0L) {
                Move printableMove = PackedMove.unpack(move);
                String moveStr = printableMove.toString();
                if (pv.isEmpty()) {
                    pv = moveStr;
                } else {
                    pv += " " + moveStr;
                }
            }
        }

        public MoveValue(long move, int value, String pv) {
            this.move = move;
            this.value = value;
            this.pv = pv;
        }
    }
    
}
