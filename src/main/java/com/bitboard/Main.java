package com.bitboard;

public class Main {
    
    public static void main(String[] args) {
        BitBoard bitBoard = new BitBoard();
        MoveGenerator moveGenerator = new MoveGenerator(bitBoard);
        System.out.println("""
 _______________________________________________________________________________________

  ███████╗██╗      ██████╗ ██╗    ██╗ ██████╗ ██╗███╗   ██╗███████╗██╗   ██╗██████╗ 
  ██╔════╝██║     ██╔═══██╗██║    ██║██╔════╝ ██║████╗  ██║██╔════╝██║   ██║╚════██╗
  █████╗  ██║     ██║   ██║██║ █╗ ██║██║  ███╗██║██╔██╗ ██║█████╗  ██║   ██║ █████╔╝
  ██╔══╝  ██║     ██║   ██║██║███╗██║██║   ██║██║██║╚██╗██║██╔══╝  ╚██╗ ██╔╝██╔═══╝ 
  ██║     ███████╗╚██████╔╝╚███╔███╔╝╚██████╔╝██║██║ ╚████║███████╗ ╚████╔╝ ███████╗
  ╚═╝     ╚══════╝ ╚═════╝  ╚══╝╚══╝  ╚═════╝ ╚═╝╚═╝  ╚═══╝╚══════╝  ╚═══╝  ╚══════╝
_______________________________________________________________________________________

                """);

        bitBoard.loadFromFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
        // bitBoard.printBitBoard(bitBoard.bitboard);
        // int moves = moveGenerator.countMoves(true);
        // int legalMoves = moveGenerator.countLegalMoves(true);
        // bitBoard.printBitBoard(moveGenerator.generateKingMoves(bitBoard.whiteKing, true));
        // System.out.println("Number of moves: " + moves);
        // System.out.println("Number of legal moves: " + legalMoves);

        moveGenerator.countMoves(true);
        

    }
}