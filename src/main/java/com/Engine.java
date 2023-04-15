package com;


/**
 * Chess Engine Class
 * 
 * <p>
 * The type is an integer that represents the type of piece
 * 1 = white pawn
 * 2 = white knight
 * 3 = white bishop
 * 4 = white rook
 * 5 = white queen
 * 6 = white king
 * 7 = black pawn
 * 8 = black knight
 * 9 = black bishop
 * 10 = black rook
 * 11 = black queen
 * 12 = black king
 * <p>
 */
public class Engine {

    private Board board;
    private int depth;

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
            5, 5, 10, 25, 25, 10, 5, 5,
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


    public Engine(Board board) {
        this.board = board;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Evaluate the current board position for the current player. The evaluation is based on the material and the position of the pieces.
     * @return centipawn value of the current board position
     */
    public int evaluate() {

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
                    case 6 -> score += KING_TABLE[i];
                }
            }
            else {
                switch (piece) {
                    case 7 -> score -= PAWN_TABLE[63 - i];
                    case 8 -> score -= KNIGHT_TABLE[63 - i];
                    case 9 -> score -= BISHOP_TABLE[63 - i];
                    case 10 -> score -= ROOK_TABLE[63 - i];
                    case 11 -> score -= QUEEN_TABLE[63 - i];
                    case 12 -> score -= KING_TABLE[63 - i];
                }
            }
        }

        return score;

    }

    public void play() {
        // IA play the best move according to the minimax algorithm
        int[] bestMove = minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn);
        System.out.println(bestMove[2]);
        if(bestMove[0] == 0 && bestMove[1] == 0){
            // Choose another move
            System.out.println("Try another move");
            bestMove = minimax(depth + 1, Integer.MIN_VALUE, Integer.MAX_VALUE, board.whiteTurn);
        }
        if(bestMove[0] == 0 && bestMove[1] == 0){
            throw new IllegalStateException(board.whiteTurn ? "White resigned" : "Black resigned");
        }
        board.pushMove(bestMove[0], bestMove[1]);
        board.buildPGN(bestMove[0], bestMove[1]);
    }

    public int[] minimax(int depth, int alpha, int beta, boolean maximizingPlayer) {
        // If we reached the maximum depth or if the game is over, we return the evaluation of the current board position
        if (depth == 0) {
            return new int[]{-1, -1, evaluate()};
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
                int eval = minimax(depth - 1, alpha, beta, false)[2];

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
                int eval = minimax(depth - 1, alpha, beta, true)[2];

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



    public int getNbLegalMoves(int depth) {
        double time = System.currentTimeMillis();
        int count = board.countLegalMoves(depth);
        System.out.println("\nTime elapsed for depth "+depth+" : " + (System.currentTimeMillis() - time) /1000 + " seconds");
        return count;
    }

    public int getNbValidMoves(int depth) {
        double time = System.currentTimeMillis();
        int count = board.countValidMoves(depth);
        System.out.println("\nTime elapsed for depth "+depth+" : " + (System.currentTimeMillis() - time) /1000 + " seconds");
        return count;
    }

    public void showKnps() {
        new Thread(() -> {
            // Animate the stdout to show the number of legal moves per second (knps) like a
            // loading bar
            int depth = 4;
    
            Board tempBoard = new Board();
            tempBoard.board = board.board.clone();
    
            while (true) {
                double time = System.currentTimeMillis();
                int count = tempBoard.countLegalMoves(depth);
                double knps = count / ((System.currentTimeMillis() - time) / 1000) / 1000;
                knps = Math.round(knps * 100.0) / 100.0;
                System.out.print("\r" + "Currently running at " + knps + " kN/s");
            }
        }).start();

    }
}
