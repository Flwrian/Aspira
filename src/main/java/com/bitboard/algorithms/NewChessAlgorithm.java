
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

        for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {
            nodes = 0;
            cutoffs = 0;
            ttHits = 0;
            ttStores = 0;
            long startTime = System.nanoTime();

            MoveValue result = alphaBeta(board, currentDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn,
                    ply);
            long endTime = System.nanoTime();

            // Interrompu ? On garde le dernier coup valide
            if (timeExceeded) {
                if (DEBUG_FLAG) {
                    System.out.println("Time limit exceeded during search. Stopping at depth " + (currentDepth - 1));
                }
                break;
            }

            bestPackedMove = result.move;
            Move bestPrintableMove = PackedMove.unpack(bestPackedMove);

            printSearchInfo(currentDepth, result.value, nodes, endTime - startTime, cutoffs, ttHits, ttStores,
                    bestPrintableMove);
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
                long ttStores, Move bestMove) {
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
                            depth, mateScore, nodes, (long) rawNps, timeMs,
                            bestMove != null ? bestMove.toString() : "(none)");
                } else {
                    System.out.printf(Locale.US, "info depth %d score cp %d nodes %d nps %d time %.0f pv %s\n",
                            depth, score, nodes, (long) rawNps, timeMs,
                            bestMove != null ? bestMove.toString() : "(none)");
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
            return new MoveValue(0L, quiescenceSearch(board, alpha, beta, maximizingPlayer).value);
        }
              
        nodes++;

        

        PackedMoveList moves = board.getLegalMoves();

        if (board.isThreefoldRepetition()) {
            return new MoveValue(0L, 0); // draw by repetition
        }   

        if (moves.size() == 0 && !board.isKingInCheck(board.whiteTurn)) {
            return new MoveValue(0L, 0); // Stalemate
        } else if (moves.size() == 0) {
            return board.whiteTurn ? new MoveValue(0L, -MATE_SCORE + ply) : new MoveValue(0L, MATE_SCORE - ply); // Checkmate
        }

        // Sort tt move first
        long ttMove = (entry != null) ? entry.bestMove : 0L;
        moves.sortByScore();
        if (ttMove != 0L) {
            moves.prioritize(ttMove);
        }

        long bestMove = 0L;
        int bestValue = maximizingPlayer ? -90000 : 90000;

        int originalAlpha = alpha;

        for (int i = 0; i < moves.size(); i++) {
            long move = moves.get(i);
            board.makeMove(move);
            ply++;
            int value = alphaBeta(board, depth - 1, alpha, beta, !maximizingPlayer, ply).value;
            board.undoMove();
            ply--;

            if (timeExceeded)
                return new MoveValue(0L, 0); // stop immediately

            if (maximizingPlayer) {
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
                alpha = Math.max(alpha, value);
            } else {
                if (value < bestValue) {
                    bestValue = value;
                    bestMove = move;
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

        tt.put(key, bestMove, bestValue, depth, flag);
        ttStores++;

        return new MoveValue(bestMove, bestValue);
    }

    private MoveValue quiescenceSearch(BitBoard board, int alpha, int beta, boolean maximizingPlayer) {


        long key = board.zobristKey;
        TranspositionTable.Entry entry = tt.get(key);
        if (entry != null && entry.depth == 0) { // profondeur 0 = quiescence
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

        // Sort tt move first
        long ttMove = (entry != null) ? entry.bestMove : 0L;
        captures.sortByScore();
        if (ttMove != 0L) {
            captures.prioritize(ttMove);
        }
        
        long bestMove = 0L;
        int bestValue = maximizingPlayer ? alpha : beta;

        for (int i = 0; i < captures.size(); i++) {
            long move = captures.get(i);
            board.makeMove(move);
            int value = quiescenceSearch(board, alpha, beta, !maximizingPlayer).value;
            board.undoMove();

            if (timeExceeded)
                return new MoveValue(0L, 0);

            if (maximizingPlayer) {
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
                alpha = Math.max(alpha, value);
            } else {
                if (value < bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
                beta = Math.min(beta, value);
            }
            
            if (alpha >= beta) {
                cutoffs++;
                break;
            }
        }

        return new MoveValue(bestMove, bestValue);
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

        public MoveValue(long move, int value) {
            this.move = move;
            this.value = value;
        }
    }
}
