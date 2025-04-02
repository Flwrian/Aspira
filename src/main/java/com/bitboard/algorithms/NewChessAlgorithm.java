package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.PackedMove;
import com.bitboard.PackedMoveList;

import java.util.Locale;

public class NewChessAlgorithm implements ChessAlgorithm {

    private int depth;
    private final TranspositionTable transpositionTable = new TranspositionTable(); // 64MB

    private long nodes = 0;
    private long cutoffs = 0;
    private final boolean DEBUG_FLAG = true;

    // === Timeout Control ===
    private long searchStartTime;
    private long timeLimitNanos;
    private boolean timeExceeded = false;

    public NewChessAlgorithm(int depth) {
        this.depth = depth;
    }

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
            String separator = "╔" + "═".repeat(83) + "╗";
            String header = String.format("║ %-6s │ %-6s │ %-12s │ %-10s │ %-10s │ %-9s │ %-6s ║",
                    "Depth", "Score", "Nodes", "NPS", "Time (ms)", "Cutoffs", "Cut %");
            String divider = "╟" + "─".repeat(83) + "╢";
            System.out.println(separator);
            System.out.println(header);
            System.out.println(divider);
        }

        for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {
            nodes = 0;
            cutoffs = 0;
            long startTime = System.nanoTime();

            MoveValue result = alphaBeta(board, currentDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn);
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

            printSearchInfo(currentDepth, result.value, nodes, endTime - startTime, cutoffs, bestPrintableMove);
        }

        if (DEBUG_FLAG) {
            System.out.println("╚" + "═".repeat(83) + "╝");
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

    private void printSearchInfo(int depth, int score, long nodes, long durationNanos, long cutoffs, Move bestMove) {
        double timeMs = durationNanos / 1_000_000.0;
        double rawNps = nodes / (durationNanos / 1_000_000_000.0);
        double cutoffRatio = 100.0 * cutoffs / Math.max(nodes, 1);
        String npsStr = formatNps(rawNps);

        if (DEBUG_FLAG) {
            System.out.printf(Locale.US, "║ %-6d │ %-6d │ %-12d │ %-10s │ %-10.1f │ %-9d │ %-5.2f%% ║\n",
                    depth, score, nodes, npsStr, timeMs, cutoffs, cutoffRatio);
        } else {
            System.out.printf(Locale.US, "info depth %d score cp %d nodes %d nps %d time %.0f pv %s\n",
                    depth, score, nodes, (long) rawNps, timeMs,
                    bestMove != null ? bestMove.toString() : "(none)");
        }
    }

    private String formatNps(double nps) {
        if (nps >= 1_000_000) return String.format(Locale.US, "%.1fM", nps / 1_000_000);
        if (nps >= 1_000) return String.format(Locale.US, "%.1fk", nps / 1_000);
        return String.format(Locale.US, "%.0f", nps);
    }

    public MoveValue alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        // === TIME CHECK ===
        if (System.nanoTime() - searchStartTime > timeLimitNanos) {
            timeExceeded = true;
            return new MoveValue(0L, 0); // Valeur arbitraire (ne sera pas utilisée)
        }

        nodes++;

        long zobristKey = board.generateZobristKey();
        TranspositionTableEntry ttEntry = transpositionTable.get(zobristKey);

        if (ttEntry != null && ttEntry.depth >= depth) {
            if (ttEntry.flag == TranspositionTableEntry.EXACT) {
                return new MoveValue(ttEntry.bestMove, ttEntry.value);
            } else if (ttEntry.flag == TranspositionTableEntry.LOWERBOUND && ttEntry.value > alpha) {
                alpha = ttEntry.value;
            } else if (ttEntry.flag == TranspositionTableEntry.UPPERBOUND && ttEntry.value < beta) {
                beta = ttEntry.value;
            }
            if (alpha >= beta) {
                cutoffs++;
                return new MoveValue(ttEntry.bestMove, ttEntry.value);
            }
        }

        if (depth == 0) {
            return new MoveValue(0L, evaluate(board));
        }
        

        PackedMoveList moves = board.getLegalMoves();

        if (ttEntry != null && ttEntry.bestMove != 0L) {
            moves.prioritize(ttEntry.bestMove);
        }

        moves.sortByScore();

        long bestMove = 0L;
        int bestValue = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < moves.size(); i++) {
            long move = moves.get(i);
            board.makeMove(move);
            int value = alphaBeta(board, depth - 1, alpha, beta, !maximizingPlayer).value;
            board.undoMove();

            if (timeExceeded) return new MoveValue(0L, 0); // stop immediately

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

        int flag;
        if (bestValue <= alpha) {
            flag = TranspositionTableEntry.UPPERBOUND;
        } else if (bestValue >= beta) {
            flag = TranspositionTableEntry.LOWERBOUND;
        } else {
            flag = TranspositionTableEntry.EXACT;
        }

        transpositionTable.put(zobristKey, new TranspositionTableEntry(zobristKey, depth, bestValue, flag, bestMove));

        return new MoveValue(bestMove, bestValue);
    }

    public MoveValue quiescence(BitBoard board, int alpha, int beta, boolean maximizingPlayer) {
        if (System.nanoTime() - searchStartTime > timeLimitNanos) {
            timeExceeded = true;
            return new MoveValue(0L, 0);
        }
    
        nodes++;
    
        long zobristKey = board.generateZobristKey();
        TranspositionTableEntry ttEntry = transpositionTable.get(zobristKey);
    
        if (ttEntry != null && ttEntry.depth == 0) {
            if (ttEntry.flag == TranspositionTableEntry.EXACT) {
                return new MoveValue(ttEntry.bestMove, ttEntry.value);
            } else if (ttEntry.flag == TranspositionTableEntry.LOWERBOUND && ttEntry.value > alpha) {
                alpha = ttEntry.value;
            } else if (ttEntry.flag == TranspositionTableEntry.UPPERBOUND && ttEntry.value < beta) {
                beta = ttEntry.value;
            }
            if (alpha >= beta) {
                cutoffs++;
                return new MoveValue(ttEntry.bestMove, ttEntry.value);
            }
        }
    
        int standPat = evaluate(board);
    
        if (maximizingPlayer) {
            if (standPat >= beta) return new MoveValue(0L, beta);
            if (standPat > alpha) alpha = standPat;
        } else {
            if (standPat <= alpha) return new MoveValue(0L, alpha);
            if (standPat < beta) beta = standPat;
        }
    
        PackedMoveList captures = board.getCaptureMoves();
        // captures.sortByScore();
    
        long bestMove = 0L;
    
        for (int i = 0; i < captures.size(); i++) {
            long move = captures.get(i);
            board.makeMove(move);
            int value = quiescence(board, alpha, beta, !maximizingPlayer).value;
            board.undoMove();
    
            if (timeExceeded) return new MoveValue(0L, 0);
    
            if (maximizingPlayer) {
                if (value > alpha) {
                    alpha = value;
                    bestMove = move;
                }
            } else {
                if (value < beta) {
                    beta = value;
                    bestMove = move;
                }
            }
    
            if (alpha >= beta) {
                break;
            }
        }
    
        // === On écrit dans la TT ===
        int flag;
        if (maximizingPlayer) {
            if (alpha <= standPat) flag = TranspositionTableEntry.UPPERBOUND;
            else if (alpha >= beta) flag = TranspositionTableEntry.LOWERBOUND;
            else flag = TranspositionTableEntry.EXACT;
            transpositionTable.put(zobristKey, new TranspositionTableEntry(zobristKey, 0, alpha, flag, bestMove));
            return new MoveValue(bestMove, alpha);
        } else {
            if (beta >= standPat) flag = TranspositionTableEntry.LOWERBOUND;
            else if (beta <= alpha) flag = TranspositionTableEntry.UPPERBOUND;
            else flag = TranspositionTableEntry.EXACT;
            transpositionTable.put(zobristKey, new TranspositionTableEntry(zobristKey, 0, beta, flag, bestMove));
            return new MoveValue(bestMove, beta);
        }
    }
    
    

    @Override
    public int evaluate(BitBoard board) {
        nodes++;
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
