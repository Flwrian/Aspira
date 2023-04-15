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
    private ChessAlgorithm algorithm;


    public Engine(Board board) {
        this.board = board;
    }
    
    public void setAlgorithm(ChessAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void play() {
        if(algorithm == null){
            throw new IllegalStateException("No algorithm selected");
        }
        
        // IA play the best move according to the algorithm
        int[] bestMove = algorithm.play(board);
        System.out.println(bestMove[2]);

        if(bestMove[0] == 0 && bestMove[1] == 0){
            // Choose another move
            System.out.println("Try another move");
            bestMove = algorithm.play(board);
        }

        if(bestMove[0] == 0 && bestMove[1] == 0){
            throw new IllegalStateException(board.whiteTurn ? "White resigned" : "Black resigned");
        }

        board.pushMove(bestMove[0], bestMove[1]);
        board.buildPGN(bestMove[0], bestMove[1]);
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
