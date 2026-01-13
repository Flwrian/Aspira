package fr.flwrian.aspira.search;

import java.util.Locale;

import fr.flwrian.aspira.board.Board;
import fr.flwrian.aspira.move.Move;
import fr.flwrian.aspira.move.PackedMove;
import fr.flwrian.aspira.move.PackedMoveList;

public class PureNegamax implements SearchAlgorithm {

    static final int MAX_PLY = 128;
    static final int MATE = 32000;
    static final int INFINITE = 32001;

    private static final PackedMoveList[] moveLists = new PackedMoveList[MAX_PLY];

    static {
        for (int i = 0; i < MAX_PLY; i++) {
            moveLists[i] = new PackedMoveList(218);
        }
    }

    int[] pvLengths = new int[MAX_PLY];
    int[][] principalVariations = new int[MAX_PLY][MAX_PLY];

    long nodes = 0;
    long lastNps = 0;
    boolean stopSearch = false;
    long startTime;

    public int negamax(Board board, int depth, int ply) {
        if (ply >= MAX_PLY) {
            return evaluate(board);
        }

        pvLengths[ply] = ply;

        if (depth <= 0) {
            return evaluate(board);
        }

        int bestScore = -INFINITE;
        PackedMoveList moves = board.getLegalMoves(moveLists[ply]);

        // Checkmate ou stalemate
        if (moves.size() == 0) {
            if (board.isKingInCheck(board.whiteTurn)) {
                return -MATE + ply;
            } else {
                return 0;
            }
        }

        for (int i = 0; i < moves.size(); i++) {
            int move = moves.get(i);
            nodes++;

            board.makeMove(move);
            int score = -negamax(board, depth - 1, ply + 1);
            board.undoMove();

            if (score > bestScore) {
                bestScore = score;

                // Update principal variation
                principalVariations[ply][ply] = move;
                for (int j = ply + 1; j < pvLengths[ply + 1]; j++) {
                    principalVariations[ply][j] = principalVariations[ply + 1][j];
                }
                pvLengths[ply] = pvLengths[ply + 1];
            }
        }

        return bestScore;
    }

    public void iterativeDeepening(Board board, int depthLimit) {
        nodes = 0;
        startTime = System.nanoTime();

        for (int depth = 1; depth <= depthLimit; depth++) {
            int score = negamax(board, depth, 0);

            if (stopSearch) {
                break;
            }

            long endTime = System.nanoTime();
            printSearchInfo(depth, score, nodes, endTime - startTime);
        }

        Move best = PackedMove.unpack(principalVariations[0][0]);
        System.out.println("bestmove " + best);
    }

    @Override
    public int evaluate(Board board) {
        return board.whiteTurn ? board.evaluate() : -board.evaluate();
    }

    public String getPV() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pvLengths[0]; i++) {
            Move move = PackedMove.unpack(principalVariations[0][i]);
            sb.append(move.toString()).append(" ");
        }
        return sb.toString().trim();
    }

    private void printSearchInfo(int depth, int score, long nodes, long durationNanos) {
        double timeMs = durationNanos / 1_000_000.0;
        double rawNps = nodes / (durationNanos / 1_000_000_000.0);
        lastNps = (long) rawNps;

        System.out.printf(Locale.US, "info depth %d score cp %d nodes %d nps %d time %.0f pv %s\n",
                depth, score, nodes, lastNps, timeMs, getPV());
    }

    @Override
    public void resetSearch() {
        pvLengths = new int[MAX_PLY];
        principalVariations = new int[MAX_PLY][MAX_PLY];
    }

    @Override
    public Move search(Board board, int wtime, int btime, int winc, int binc, int movetime, int depth, long maxNodes) {
        nodes = 0;
        stopSearch = false;
        startTime = 0;
        resetSearch();

        iterativeDeepening(board, depth);
        return PackedMove.unpack(principalVariations[0][0]);
    }

    @Override
    public String getName() {
        return "PureNegamax";
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
        // Pas de hash table
    }

    @Override
    public void setHashTable(int sizeMB) {
        // Pas de hash table
    }
}
