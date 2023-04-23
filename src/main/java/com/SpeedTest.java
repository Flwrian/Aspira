package com;

public class SpeedTest {
    
    public static void main(String[] args) {
        Board board = new Board();
        board.loadFEN(Board.STARTING_FEN);

        Engine engine = new Engine(board);
        
        for (int i = 0; i < 7; i++) {
            engine.getNbLegalMoves(i);
        }
    }
}
