package com.bitboard;

import com.bitboard.algorithms.AdvancedChessAlgorithm;
import com.bitboard.algorithms.MaterialAlgorithm;
import com.bitboard.algorithms.RandomAlgorithm;

public class Main {
    
    public static void main(String[] args) {
        BitBoard bitBoard = new BitBoard();
        String fen = "4k3/p4pp1/8/8/8/8/P4K1P/8 w - - 0 1";
        bitBoard.loadFromFen(fen);
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

        Engine engine2 = new Engine(bitBoard);
        AdvancedChessAlgorithm advancedChessAlgorithm = new AdvancedChessAlgorithm(6);
        engine2.setAlgorithm(advancedChessAlgorithm);

        Engine engine1 = new Engine(bitBoard);
        MaterialAlgorithm materialAlgorithm = new MaterialAlgorithm(5);
        engine1.setAlgorithm(materialAlgorithm);
        // play 100 random moves 
        for (int i = 0; i < 100; i++) {
            if (bitBoard.isCheckMate()) {
                System.out.println("Checkmate!");
                break;
            }
            if (bitBoard.isStaleMate()) {
                System.out.println("Stalemate!");
                break;
            }
            if(bitBoard.whiteTurn) {
                engine1.play();
                engine2.addMoveToPGN(engine1.getLastMove());
            } else {
                engine2.play();
                engine1.addMoveToPGN(engine2.getLastMove());
            }

            // System.out.println(engine1.getPGN());
        }
        bitBoard.printChessBoard();

        System.out.println("[FEN " + fen + "]");
        System.out.println(engine1.getPGN());

        
        

    }
}