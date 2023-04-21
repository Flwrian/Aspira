package com.algorithms;

import com.Board;

public class RandomAlgorithm implements ChessAlgorithm {

    @Override
    public int[] play(Board board) {
        int[][] moves = board.getLegalMoves();

        // Get random number between 0 and moves.length
        int random = (int) (Math.random() * moves.length);
        return moves[random];
    }

    @Override
    public int evaluate(Board board) {
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
    }

    @Override
    public String getName() {
        return "Random";
    }
    
}
