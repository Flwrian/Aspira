package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.MoveList;
import com.bitboard.PackedMove;
import com.bitboard.PackedMoveList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * New optimized chess algorithm.
 */
public class NewChessAlgorithm implements ChessAlgorithm {

    private int depth;
    private TranspositionTable transpositionTable = new TranspositionTable();

    private long nodes = 0;
    private long cutoffs = 0;
    private long startTime = 0;


    

    public NewChessAlgorithm(int depth) {
        this.depth = depth;
    }

    @Override
    public Move search(BitBoard board, int wtime, int btime, int winc, int binc, int movestogo, int depth) {
        long bestPackedMove = 0L;

        String separator = "╔" + "═".repeat(83) + "╗";
        String header = String.format("║ %-6s │ %-6s │ %-12s │ %-10s │ %-10s │ %-9s │ %-6s ║",
            "Depth", "Score", "Nodes", "NPS", "Time (ms)", "Cutoffs", "Cut %");
        String divider = "╟" + "─".repeat(83) + "╢";

        System.out.println(separator);
        System.out.println(header);
        System.out.println(divider);

        for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {
            nodes = 0;
            cutoffs = 0;
            long startTime = System.nanoTime();

            MoveValue result = alphaBeta(board, currentDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn);
            long endTime = System.nanoTime();

            bestPackedMove = result.move;
            Move bestPrintableMove = PackedMove.unpack(bestPackedMove);

            double timeMs = (endTime - startTime) / 1_000_000.0;
            double rawNps = nodes / ((endTime - startTime) / 1_000_000_000.0);
            double cutoffRatio = 100.0 * cutoffs / Math.max(nodes, 1);

            // Formate NPS avec unité
            String npsStr = formatNps(rawNps);

            System.out.printf(Locale.US,"║ %-6d │ %-6d │ %-12d │ %-10s │ %-10.1f │ %-9d │ %-5.2f%% ║\n",
                currentDepth, result.value, nodes, npsStr, timeMs, cutoffs, cutoffRatio);
        }

        System.out.println("╚" + "═".repeat(83) + "╝");

        Move best = PackedMove.unpack(bestPackedMove);
        System.out.println("bestmove " + best);
        return best;
    }

    // Utilitaire : formate NPS comme "2.5M", "982k", "15"
    private String formatNps(double nps) {
        if (nps >= 1_000_000) return String.format(Locale.US, "%.1fM", nps / 1_000_000);
        if (nps >= 1_000) return String.format(Locale.US, "%.1fk", nps / 1_000);
        return String.format("%.0f", nps);
    }




    public MoveValue alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        
        
        if (depth == 0) {
            return new MoveValue(0L, evaluate(board)); // Base case: evaluate position
        }       
         

        PackedMoveList moves = board.getLegalMoves();
        moves.sortByScore();

        long bestMove = 0L;
        int bestValue = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < moves.size(); i++) {
            long move = moves.get(i);

            board.makeMove(move);
            int value = alphaBeta(board, depth - 1, alpha, beta, !maximizingPlayer).value;
            board.undoMove();

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
                cutoffs++; // Count cutoffs
                break; // alpha-beta pruning
            }
        }

        return new MoveValue(bestMove, bestValue);
    }

    public MoveValue quiescence(BitBoard board, int alpha, int beta, boolean maximizingPlayer) {
        // nodes++;
    
        int eval = evaluate(board);

        if(eval >= beta) {
            return new MoveValue(0L, beta); // Beta cutoff
        }
        if(eval > alpha) {
            alpha = eval; // Update alpha
        }

        PackedMoveList moves = board.getCaptureMoves();
        // moves.sortByScoreDescending();

        for (int i = 0; i < moves.size(); i++) {
            long move = moves.get(i);
            board.makeMove(move);
            int value = quiescence(board, alpha, beta, !maximizingPlayer).value;
            board.undoMove();

            if (maximizingPlayer) {
                if (value > alpha) {
                    alpha = value; // Update alpha
                }
            } else {
                if (value < beta) {
                    beta = value; // Update beta
                }
            }

            if (alpha >= beta) {
                return new MoveValue(move, beta); // Beta cutoff
            }
        }

        return new MoveValue(0L, alpha); // Return best value found
    }
    

    
    

    @Override
    public int evaluate(BitBoard board) {
        nodes++;
        return board.currentEvaluation;
    }

    @Override
    public String getName() {
        return "NewChessAlgorithm";
    }

    public class MoveValue {
        public final long move; // packed move
        public final int value;
    
        public MoveValue(long move, int value) {
            this.move = move;
            this.value = value;
        }
    }
    
}
