package com.bitboard;

import com.bitboard.algorithms.AdvancedChessAlgorithm;
import com.bitboard.algorithms.CustomAlgorithm;
import com.bitboard.algorithms.MaterialAlgorithm;
import com.bitboard.algorithms.RandomAlgorithm;

public class Main {
    
    public static void main(String[] args) {
        BitBoard bitBoard = new BitBoard();
        String fen = BitBoard.INITIAL_STARTING_POSITION;
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

        AdvancedChessAlgorithm advancedChessAlgorithm = new AdvancedChessAlgorithm(4);
        MaterialAlgorithm materialAlgorithm = new MaterialAlgorithm(5);
        CustomAlgorithm customAlgorithm = new CustomAlgorithm(6);

        Engine engine2 = new Engine(bitBoard);
        engine2.setAlgorithm(advancedChessAlgorithm);

        Engine engine1 = new Engine(bitBoard);
        engine1.setAlgorithm(customAlgorithm);
        
        // play 100 random moves 
        for (int i = 0; i < 80; i++) {
            if (bitBoard.isCheckMate()) {
                System.out.println("Board is Checkmate!");
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