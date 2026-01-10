// package fr.flwrian.aspira.search;

// import java.util.Locale;

// import fr.flwrian.aspira.board.Board;
// import fr.flwrian.aspira.move.Move;
// import fr.flwrian.aspira.move.PackedMove;
// import fr.flwrian.aspira.move.PackedMoveList;

// public class PureNegamax implements SearchAlgorithm {

//     private long nodes = 0;
    
//     // Pre-allocated SearchResult array for recursion depth (no allocations in hot path)
//     private static final int MAX_DEPTH = 128;
//     private final SearchResult[] resultStack = new SearchResult[MAX_DEPTH];
    
//     private static final int MATE = 32000;
//     private static final int DRAW = 0;

//     public PureNegamax() {
//         // Pre-allocate all SearchResult objects once
//         for (int i = 0; i < MAX_DEPTH; i++) {
//             resultStack[i] = new SearchResult();
//         }
//     }

//     @Override
//     public Move search(Board board, int wtime, int btime, int winc, int binc, int movetime, int depth, long maxNodes) {
//         long bestPackedMove = 0L;
//         int bestScore = 0;

//         for (int currentDepth = 1; currentDepth <= depth; currentDepth++) {
//             nodes = 0;
//             long startTime = System.nanoTime();

//             SearchResult result = resultStack[0];
//             negamax(board, currentDepth, -MATE, MATE, 0);
//             bestPackedMove = result.move;
//             bestScore = result.score;

//             long endTime = System.nanoTime();
//             printSearchInfo(currentDepth, bestScore, nodes, endTime - startTime, result);
//         }

//         Move best = PackedMove.unpack(bestPackedMove);
//         System.out.println("bestmove " + best);
//         return best;
//     }

//     private void printSearchInfo(int depth, int score, long nodes, long durationNanos, SearchResult result) {
//         double timeMs = durationNanos / 1_000_000.0;
//         double rawNps = nodes / (durationNanos / 1_000_000_000.0);

//         System.out.printf(Locale.US, "info depth %d score cp %d nodes %d nps %d time %.0f\n",
//                 depth, score, nodes, (long) rawNps, timeMs);
//     }

//     private void negamax(Board board, int depth, int alpha, int beta, int ply) {
//         nodes++;

//         SearchResult result = resultStack[ply];
        
//         if (depth == 0) {
//             result.move = 0L;
//             result.score = evaluate(board);
//             return;
//         }

//         PackedMoveList moves = board.getLegalMoves();
//         boolean inCheck = board.isKingInCheck(board.whiteTurn);
        
//         if (moves.size() == 0) {
//             result.move = 0L;
//             result.score = inCheck ? -MATE : DRAW;
//             return;
//         }

//         moves.sortByScore();

//         long bestMove = 0L;
//         int bestScore = -MATE;

//         SearchResult childResult = resultStack[ply + 1];

//         for (int i = 0; i < moves.size(); i++) {
//             long move = moves.get(i);

//             board.makeMove(move);
//             negamax(board, depth - 1, -beta, -alpha, ply + 1);
//             int score = -childResult.score;
//             board.undoMove();

//             if (score > bestScore) {
//                 bestScore = score;
//                 bestMove = move;
//             }

//             if (bestScore > alpha) {
//                 alpha = bestScore;
//             }

//             if (alpha >= beta) {
//                 break;
//             }
            
//         }

//         result.move = bestMove;
//         result.score = bestScore;
//     }

//     @Override
//     public int evaluate(Board board) {
//         int e = board.evaluate();
//         return board.whiteTurn ? e : -e;
//     }

//     @Override
//     public String getName() {
//         return "PureNegamax";
//     }

//     private static class SearchResult {
//         long move;
//         int score;
//     }

//     @Override
//     public void setStopSearch(boolean b) {
        
//     }

//     @Override
//     public long getLastNodeCount() {
//         return nodes;
//     }

//     @Override
//     public long getLastNPS() {
//         return 0L;
//     }

//     @Override
//     public void flushHashTable() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'flushHashTable'");
//     }
// }