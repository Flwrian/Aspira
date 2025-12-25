package fr.flwrian.aspira.engine;

import fr.flwrian.aspira.board.Board;
import fr.flwrian.aspira.move.Move;
import fr.flwrian.aspira.search.SearchAlgorithm;

/**
 * This class is the engine for the chess game.
 * It is responsible for finding the best move for the current player.
 */
public class Engine {
    
    private Board board;
    int depth;

    String PGN = "";
    String FEN = "";

    int counter = 0;

    SearchAlgorithm algorithm;

    Move lastMove = null;

    public Engine(Board board) {
        this.board = board;
    }

    public Engine(Board board, int depth) {
        this.board = board;
        this.depth = depth;
    }

    public Engine(Board board, int depth, SearchAlgorithm algorithm) {
        this.board = board;
        this.depth = depth;
        this.algorithm = algorithm;
    }

    public Engine(Board board, SearchAlgorithm algorithm) {
        this.board = board;
        this.algorithm = algorithm;
    }

    public SearchAlgorithm getSearchAlgorithm() {
        return algorithm;
    }

    public Board getBoard() {
        return board;
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

    public void setAlgorithm(SearchAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }
}
