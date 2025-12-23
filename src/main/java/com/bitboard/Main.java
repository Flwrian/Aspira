package com.bitboard;

public class Main {
    
    public static void main(String[] args) {
        BitBoard board = new BitBoard();
        
        board.makeMove("a2a4");
        board.makeMove("b7b5");
        board.makeMove("a4a5");
        board.makeMove("b5a4");
    }
}
