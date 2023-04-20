package com.algorithms;

import com.Board;

public class AlphaBetaPruningAlgorithm implements ChessAlgorithm {

    private final int DEFAULT_DEPTH = 1;

    private int depth = DEFAULT_DEPTH;

    // Piece material values
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 300;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 25000;

    // Piece square tables
    private static final int[] PAWN_TABLE = {
            900, 900, 900, 900, 900, 900, 900, 900,
            5, 10, 10, -20, -20, 10, 10, 5,
            5, -5, -10, 0, 0, -10, -5, 5,
            0, 0, 0, 20, 20, 0, 0, 0,
            5, 5, 10, 50, 50, 10, 5, 5,
            10, 10, 20, 30, 30, 20, 10, 10,
            50, 50, 50, 50, 50, 50, 50, 50,
            0, 0, 0, 0, 0, 0, 0, 0
    };

    private static final int[] KNIGHT_TABLE = {
            -50, -40, -30, -30, -30, -30, -40, -50,
            -40, -20, 0, 0, 0, 0, -20, -40,
            -30, 0, 10, 15, 15, 10, 0, -30,
            -30, 5, 15, 20, 20, 15, 5, -30,
            -30, 0, 15, 20, 20, 15, 0, -30,
            -30, 5, 10, 15, 15, 10, 5, -30,
            -40, -20, 0, 5, 5, 0, -20, -40,
            -50, -40, -30, -30, -30, -30, -40, -50
    };

    private static final int[] BISHOP_TABLE = {
            -20, -10, -10, -10, -10, -10, -10, -20,
            -10, 0, 0, 0, 0, 0, 0, -10,
            -10, 0, 5, 10, 10, 5, 0, -10,
            -10, 5, 5, 10, 10, 5, 5, -10,
            -10, 0, 10, 10, 10, 10, 0, -10,
            -10, 10, 10, 10, 10, 10, 10, -10,
            -10, 5, 0, 0, 0, 0, 5, -10,
            -20, -10, -10, -10, -10, -10, -10, -20
    };

    private static final int[] ROOK_TABLE = {
            0, 0, 0, 0, 0, 0, 0, 0,
            5, 10, 10, 10, 10, 10, 10, 5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            0, 0, 0, 5, 5, 0, 0, 0
    };

    private static final int[] QUEEN_TABLE = {
            -20, -10, -10, -5, -5, -10, -10, -20,
            -10, 0, 0, 0, 0, 0, 0, -10,
            -10, 0, 5, 5, 5, 5, 0, -10,
            -5, 0, 5, 5, 5, 5, 0, -5,
            0, 0, 5, 5, 5, 5, 0, -5,
            -10, 5, 5, 5, 5, 5, 0, -10,
            -10, 0, 5, 0, 0, 0, 0, -10,
            -20, -10, -10, -5, -5, -10, -10, -20
    };

    private static final int[] KING_TABLE = {
            20, 30, 10, 0, 0, 10, 30, 20,
            20, 20, 0, 0, 0, 0, 20, 20,
            -10, -20, -20, -20, -20, -20, -20, -10,
            -20, -30, -30, -40, -40, -30, -30, -20,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30
    };

    private static final int[] KING_TABLE_ENDGAME = {
            -50, -30, -30, -30, -30, -30, -30, -50,
            -30, -30, 0, 0, 0, 0, -30, -30,
            -30, -10, 20, 30, 30, 20, -10, -30,
            -30, -10, 30, 40, 40, 30, -10, -30,
            -30, -10, 30, 40, 40, 30, -10, -30,
            -30, -10, 20, 30, 30, 20, -10, -30,
            -30, -20, -10, 0, 0, -10, -20, -30,
            -50, -40, -30, -20, -20, -30, -40, -50
    };


    @Override
    public int[] play(Board board) {
        return minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn, board);
    }

    /**
     * Evaluate the current board position for the current player. The evaluation is based on the material and the position of the pieces.
     * @return centipawn value of the current board position
     */
    public int evaluate(Board board) {

        int score = 0;

        // We're going to loop through the board and count the material
        for (int i = 0; i < 64; i++) {
            int piece = board.board[i];
            if (piece != 0) {
                int pieceValue = 0;
                switch(piece){
                    // We start with pawns because they are the most numerous pieces
                    case 1 -> pieceValue = PAWN_VALUE;
                    case 7 -> pieceValue = -PAWN_VALUE;
                    case 2 -> pieceValue = KNIGHT_VALUE;
                    case 8 -> pieceValue = -KNIGHT_VALUE;
                    case 3 -> pieceValue = BISHOP_VALUE;
                    case 9 -> pieceValue = -BISHOP_VALUE;
                    case 4 -> pieceValue = ROOK_VALUE;
                    case 10 -> pieceValue = -ROOK_VALUE;
                    case 5 -> pieceValue = QUEEN_VALUE;
                    case 11 -> pieceValue = -QUEEN_VALUE;
                    case 6 -> pieceValue = KING_VALUE;
                    case 12 -> pieceValue = -KING_VALUE;
                }

                // We add the piece value to the score
                score += pieceValue;
            }

            // We add the positional value of the piece
            if (piece < 7) {
                switch (piece) {
                    case 1 -> score += PAWN_TABLE[i];
                    case 2 -> score += KNIGHT_TABLE[i];
                    case 3 -> score += BISHOP_TABLE[i];
                    case 4 -> score += ROOK_TABLE[i];
                    case 5 -> score += QUEEN_TABLE[i];
                    case 6 -> score += KING_TABLE_ENDGAME[i];
                }
            }
            else {
                switch (piece) {
                    case 7 -> score -= PAWN_TABLE[63 - i];
                    case 8 -> score -= KNIGHT_TABLE[63 - i];
                    case 9 -> score -= BISHOP_TABLE[63 - i];
                    case 10 -> score -= ROOK_TABLE[63 - i];
                    case 11 -> score -= QUEEN_TABLE[63 - i];
                    case 12 -> score -= KING_TABLE_ENDGAME[63 - i];
                }
            }
        }

        return score;

    }

    private int[] minimax(int depth, int alpha, int beta, boolean maximizingPlayer, Board board) {
        // If we reached the maximum depth or if the game is over, we return the evaluation of the current board position

        if (depth == 0) {
            return new int[]{-1, -1, evaluate(board)};
        }

        // If it's the AI turn, we want to maximize the score
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            int bestMove[] = {-1, -1};

            // We loop through all the legal moves
            for (int[] move : board.getLegalMoves()) {
                // We play the move
                board.pushMove(move[0], move[1]);

                // We get the evaluation of the move
                int eval = minimax(depth - 1, alpha, beta, false, board)[2];

                // We undo the move
                board.popMove();

                // We update the best move
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }

                // We update alpha
                alpha = Math.max(alpha, eval);

                // We prune the tree if we can
                if (beta <= alpha) {
                    break;
                }
            }

            // We return the best move and its evaluation
            return new int[]{bestMove[0], bestMove[1], maxEval};
        }

        // If it's the opponent turn, we want to minimize the score
        else {
            int minEval = Integer.MAX_VALUE;
            int bestMove[] = {-1, -1};

            // We loop through all the legal moves
            for (int[] move : board.getLegalMoves()) {
                // We play the move
                board.pushMove(move[0], move[1]);

                // We get the evaluation of the move
                int eval = minimax(depth - 1, alpha, beta, true, board)[2];

                // We undo the move
                board.popMove();

                // We update the best move
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }

                // We update beta
                beta = Math.min(beta, eval);

                // We prune the tree if we can
                if (beta <= alpha) {
                    break;
                }
            }

            // We return the best move and its evaluation
            return new int[]{bestMove[0], bestMove[1], minEval};
        }
    }

    /**
     * Ponder the next move (predict the opponent's move and play the best move to counter it)
     * @param board
     * @param move
     * @return
     */
    public int[] ponder(Board board, int[] move){
        // We play the move
        board.pushMove(move[0], move[1]);

        // We search for the best move
        int[] bestMove = minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn, board);

        // We undo the move
        board.popMove();

        // We return the best move
        return bestMove;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public AlphaBetaPruningAlgorithm(int depth) {
        this.depth = depth;
    }

    public AlphaBetaPruningAlgorithm() {
        
    }
    
}
