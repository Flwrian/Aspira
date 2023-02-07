package com;


public class Game {
    
    public static void main(String[] args) {
        Board board = new Board();
        board.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        
        Engine engine = new Engine(board);
        for (int i = 0; i < 10; i++) {
            System.out.println(engine.getNbLegalMoves(i));
        }
    }
}
