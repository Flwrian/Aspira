package fr.flwrian.aspira.search;

import java.util.Locale;

import fr.flwrian.aspira.board.Board;
import fr.flwrian.aspira.hash.TranspositionTable;
import fr.flwrian.aspira.move.Move;
import fr.flwrian.aspira.move.PackedMove;
import fr.flwrian.aspira.move.PackedMoveList;

public class Search implements SearchAlgorithm {

    static final int MAX_PLY = 60;

    static final int VALUE_MATE = 32000;
    static final int VALUE_INFINITE = 32001;
    static final int VALUE_NONE = 32002;

    static final int VALUE_MATE_IN_PLY = VALUE_MATE - MAX_PLY;
    static final int VALUE_MATED_IN_PLY = -VALUE_MATE_IN_PLY;

    static final int VALUE_TB_WIN = VALUE_MATE_IN_PLY;
    static final int VALUE_TB_LOSS = -VALUE_TB_WIN;
    static final int VALUE_TB_WIN_IN_MAX_PLY = VALUE_TB_WIN - MAX_PLY;
    static final int VALUE_TB_LOSS_IN_MAX_PLY = -VALUE_TB_WIN_IN_MAX_PLY;


    final int CHECK_RATE = 236;
    final int INFINITE_VALUE = 32001;

    int[] pvLengths = new int[MAX_PLY];
    int[][] principalVariations = new int[MAX_PLY][MAX_PLY];

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
        nodes++;
        boolean rootNode = (ply == 0);

        if (!rootNode) {
            if (board.isThreefoldRepetition()) {
                return -5;
            }

            if (board.isStaleMate()) {
                return 0;
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
        long ttkey = board.zobristKey;
        TranspositionTable.Entry tte = transpositionTable.get(ttkey);
        boolean ttHit = (tte != null);
        int ttMove = tte != null ? tte.bestMove : 0;
        int ttScore = ttHit ? tte.value : VALUE_NONE;

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

        PackedMoveList moves = board.getLegalMoves();
        orderMoves(moves, ttMove, board);

        for (int i = 0; i < moves.size(); i++) {
            int move = moves.get(i);
            madeMoves++;
            nodes++;

            board.makeMove(move);

            // Search
            int score = -absearch(board, depth - 1, -beta, -alpha, ply + 1);
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
                            int from = PackedMove.getFrom(move);
                            int to = PackedMove.getTo(move);
                            int hhbonus = historyTable[board.whiteTurn ? 0 : 1][from][to] * Math.abs(bonus) / 16384;
                            historyTable[board.whiteTurn ? 0 : 1][from][to] += hhbonus;
                        }
                        break;
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
            transpositionTable.put(ttkey, bestMove, bestScore, depth, flag);
        }
        
        return bestScore;
    }

    public void iterativeDeepening(Board board, int depthLimit) {
        nodes = 0;
        int score = -INFINITE_VALUE;
        int bestMove = 0;

        startTime = System.nanoTime();

        

        for (int depth = 1; depth <= depthLimit; depth++) {

            score = absearch(board, depth, -INFINITE_VALUE, INFINITE_VALUE, 0);

            if (stopSearch || checkTime(true)) {
                break;
            }

            // Save best move from principal variation
            bestMove = principalVariations[0][0];

            // Print info
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

        System.out.printf(Locale.US, "info depth %d score %s nodes %d nps %d time %.0f pv %s\n",
                depth, convertScore(score), nodes, lastNps, timeMs, getPV());
    }

    private void resetSearch() {
        // reset PV lengths and principal variation table
        for (int i = 0; i < pvLengths.length; i++) {
            pvLengths[i] = 0;
        }
        for (int i = 0; i < principalVariations.length; i++) {
            for (int j = 0; j < principalVariations[i].length; j++) {
                principalVariations[i][j] = 0;
            }
        }

        // reset counters and timing
        nodes = 0;
        stopSearch = false;
        startTime = 0;
        checks = CHECK_RATE;

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

    public int scoreMove(int move, int ttMove, Board board) {
        if (move == ttMove) {
            return 1_000_000;
        }

        if (PackedMove.isCapture(move)) {
            return 32_000 + mvvLva[PackedMove.getCaptured(move)][PackedMove.getPieceFrom(move)];
        }

        return historyTable[board.whiteTurn ? 0 : 1][PackedMove.getFrom(move)][PackedMove.getTo(move)];
    }

    public int scoreQMove(int move) {
        return mvvLva[PackedMove.getCaptured(move)][PackedMove.getPieceFrom(move)];
    }

    public void orderMoves(PackedMoveList moves, int ttMove, Board board) {
        // Simple bubble sort based on score
        for (int i = 0; i < moves.size() - 1; i++) {
            for (int j = 0; j < moves.size() - i - 1; j++) {
                int scoreA = scoreMove(moves.moves[j], ttMove, board);
                int scoreB = scoreMove(moves.moves[j + 1], ttMove, board);
                if (scoreA < scoreB) {
                    // Swap
                    int temp = moves.moves[j];
                    moves.moves[j] = moves.moves[j + 1];
                    moves.moves[j + 1] = temp;
                }
            }
        }
    }

    public void orderQMoves(PackedMoveList moves) {
        // Simple bubble sort based on score
        for (int i = 0; i < moves.size() - 1; i++) {
            for (int j = 0; j < moves.size() - i - 1; j++) {
                int scoreA = scoreQMove(moves.moves[j]);
                int scoreB = scoreQMove(moves.moves[j + 1]);
                if (scoreA < scoreB) {
                    // Swap
                    int temp = moves.moves[j];
                    moves.moves[j] = moves.moves[j + 1];
                    moves.moves[j + 1] = temp;
                }
            }
        }
    }

    @Override
    public Move search(Board board, int wtime, int btime, int winc, int binc, int movetime, int depth, long maxNodes) {
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


}
