package com;

public interface ChessAlgorithm {
    
    /**
     * Returns the best move for the current player.
     * @param board
     * @return coordinates of the best move, in the format {toX, toY}
     */
    public int[] play(Board board);

    /**
     * Evaluates the current board position for the current player.
     * @param board
     * @return the evaluation of the board position in centipawns
     */
    public int evaluate(Board board);
}
