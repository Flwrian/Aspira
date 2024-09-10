package com.bitboard;

import com.bitboard.algorithms.ChessAlgorithm;

/**
 * This class is the engine for the chess game.
 * It is responsible for finding the best move for the current player.
 */
public class Engine {
    
    private BitBoard board;
    int depth;

    String PGN = "";
    String FEN = "";

    int counter = 0;

    ChessAlgorithm algorithm;

    Move lastMove = null;

    public Engine(BitBoard board) {
        this.board = board;
    }

    public Engine(BitBoard board, int depth) {
        this.board = board;
        this.depth = depth;
    }

    public Engine(BitBoard board, int depth, ChessAlgorithm algorithm) {
        this.board = board;
        this.depth = depth;
        this.algorithm = algorithm;
    }

    public Engine(BitBoard board, ChessAlgorithm algorithm) {
        this.board = board;
        this.algorithm = algorithm;
    }

    public ChessAlgorithm getAlgorithm() {
        return algorithm;
    }

    // play
    public void play() {
        Move bestMove = algorithm.search(board);
        if (bestMove == null) {
            System.out.println("No legal moves found. Making random move.");
            Move randMove = board.getLegalMoves().get((int) (Math.random() * board.getLegalMoves().size()));
            board.makeMove(randMove);
            addMoveToPGN(randMove);
            lastMove = randMove;
            return;
        }
        board.makeMove(bestMove);
        addMoveToPGN(bestMove);
        lastMove = bestMove;
    }
    
    public Move getLastMove() {
        return lastMove;
    }

    public void addMoveToPGN(Move move) {
        buildPGN(move);
    }

    // build the PGN string as the game progresses
    public void buildPGN(Move move) {
        counter++;
        if (counter % 2 == 1) {
            PGN += (counter / 2 + 1) + ". ";
        }
        PGN += move.toString() + " ";
    }

    public String getPGN() {
        return PGN;
    }

    public void setAlgorithm(ChessAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
