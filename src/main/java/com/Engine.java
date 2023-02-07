package com;

/*
 * This file is used to create a Chess Engine that will be used to play chess.
 * It will have a depth, and will use the minimax algorithm to determine the best
 * move.
 */
public class Engine {

    private Board board;

    public Engine(Board board) {
        this.board = board;
    }

    public int getNbLegalMoves() {
        int count = 0;
        for (int i = 0; i < board.board.length; i++) {
            Piece piece = board.getPiece(i);
            if (piece != null) {
                count += piece.countLegalMoves();
            }
        }
        return count;
    }

    public int getNbLegalMoves(int depth){
        double time = System.currentTimeMillis();
        int count = board.countLegalMoves(depth);
        System.out.println("Time elapsed for depth "+depth+" : " + (System.currentTimeMillis() - time) /1000 + " seconds");
        return count;
    }

    public int getNbValidMoves() {
        int count = 0;
        for (int i = 0; i < board.board.length; i++) {
            Piece piece = board.getPiece(i);
            if (piece != null) {
                count += piece.countValidMoves();
            }
        }
        return count;
    }
}
