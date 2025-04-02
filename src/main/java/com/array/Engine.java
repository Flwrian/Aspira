package com.array;

import com.array.algorithms.ChessAlgorithm;
import com.array.pieces.Piece;

/**
 * <h1>Engine</h1>
 * The Engine class is responsible for playing the best move according to the algorithm on a given board.
 * 
 * It also provides methods to count the number of legal moves and valid moves for a given depth and to show the number of positions reached per second.
 * 
 * <p>
 * To use the Engine class, you need to create a new instance of the class and pass the board to the constructor.
 * Then you can set the algorithm to use with the <code>setAlgorithm</code> method.
 * Finally, you can play the best move with the play method.
 * </p>
 * 
 * @author Montourcy Florian
 * @version 1.0
 * 
 * @see Board
 * @see ChessAlgorithm
 */
public class Engine implements PlayableEntity {

    private Board board;
    private ChessAlgorithm algorithm;


    public Engine(Board board) {
        this.board = board;
    }

    public void setAlgorithm(ChessAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public int[] play() {
        if(algorithm == null){
            throw new IllegalStateException("No algorithm selected");
        }
        
        // IA play the best move according to the algorithm
        int[] bestMove = algorithm.play(board);
        System.out.println(bestMove[2]);

        if(bestMove[0] == 0 && bestMove[1] == 0){
            throw new IllegalStateException(board.whiteTurn ? "White resigned" : "Black resigned");
        }

        board.pushMove(bestMove[0], bestMove[1]);
        board.buildPGN(bestMove[0], bestMove[1]);

        return bestMove;
    }

    public String parseMove(int[] bestMove){  
        String moveString = "";
        moveString += Piece.getSquareName(bestMove[0]);
        moveString += Piece.getSquareName(bestMove[1]);
        return moveString;
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
