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

    public BitBoard getBoard() {
        return board;
    }

    // play
    public void play() {
        Move bestMove = algorithm.search(board, 0, 0, 0, 0, 0, depth);
        if (bestMove == null) {
            // System.out.println("No legal moves found. Game over.");
            if (board.isStaleMate()){
                // System.out.println("Stalemate!");
                return;
            } else if (board.isCheckMate()) {
                // System.out.println("Checkmate!");
                return;
            }
            // play random move
            bestMove = board.makeRandomMove();
            // System.out.println("Random move: " + bestMove);
            boolean isLegal = board.isLegalMove(bestMove);
            // System.out.println("Is legal: " + isLegal);
            if (!isLegal) {
                // System.out.println("Illegal move: " + bestMove);
                return;
            }
            lastMove = bestMove;
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
        // algorithm.setDepth(depth);
    }

    public int getDepth() {
        return depth;
    }

    // public void setRazorDepth(int depth2) {
    //     algorithm.setRazorDepth(depth2);
    // }

    // public void setNPM(int npm) {
    //     algorithm.setNPM(npm);
    // }

    // public int getRazorDepth() {
    //     return algorithm.getRazorDepth();
    // }

    // public int getNPM() {
    //     return algorithm.getNPM();
    // }
}
