package com.algorithms;

import com.Board;

public interface ChessAlgorithm {
    
    /**
     * Returns the best move for the current player.
     * @param board
     * @return coordinates of the best move, in the format {toX, toY, eval}
     */
    public int[] play(Board board);

    /**
     * Evaluates the current board position for the current player.
     * @param board
     * @return the evaluation of the board position in centipawns
     */
    public int evaluate(Board board);

    /**
     * Returns the name of the algorithm.
     * @return the name of the algorithm
     */
    public String getName();
}
