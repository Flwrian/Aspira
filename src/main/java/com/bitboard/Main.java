package com.bitboard;

public class Main {
    
    public static void main(String[] args) {
        BitBoard bitBoard = new BitBoard();
        
        // System.out.println("White pieces");
        // bitBoard.printBitBoard(bitBoard.getWhitePieces());

        // System.out.println("Black pieces");
        // bitBoard.printBitBoard(bitBoard.getBlackPieces());

        // System.out.println("White pawns");
        // bitBoard.printBitBoard(bitBoard.getWhitePawns());

        // System.out.println("White knights");
        // bitBoard.printBitBoard(bitBoard.getWhiteKnights());

        // System.out.println("White bishops");
        // bitBoard.printBitBoard(bitBoard.getWhiteBishops());

        // System.out.println("White rooks");
        // bitBoard.printBitBoard(bitBoard.getWhiteRooks());

        // System.out.println("White queens");
        // bitBoard.printBitBoard(bitBoard.getWhiteQueens());

        // System.out.println("White king");
        // bitBoard.printBitBoard(bitBoard.getWhiteKing());

        // System.out.println("Black pawns");
        // bitBoard.printBitBoard(bitBoard.getBlackPawns());

        // System.out.println("Black knights");
        // bitBoard.printBitBoard(bitBoard.getBlackKnights());

        // System.out.println("Black bishops");
        // bitBoard.printBitBoard(bitBoard.getBlackBishops());

        // System.out.println("Black rooks");
        // bitBoard.printBitBoard(bitBoard.getBlackRooks());

        // System.out.println("Black queens");
        // bitBoard.printBitBoard(bitBoard.getBlackQueens());

        // System.out.println("Black king");
        // bitBoard.printBitBoard(bitBoard.getBlackKing());
        
        bitBoard.makeMove("e2e7");
        bitBoard.printChessBoard();


    }
}