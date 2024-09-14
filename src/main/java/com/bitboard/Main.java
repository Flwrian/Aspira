package com.bitboard;

import com.bitboard.algorithms.AdvancedChessAlgorithm;
import com.bitboard.algorithms.AlphaBeta;
import com.bitboard.algorithms.CustomAlgorithm;
import com.bitboard.algorithms.MaterialAlgorithm;
import com.bitboard.algorithms.RandomAlgorithm;

public class Main {
    
    public static void main(String[] args) {
        BitBoard bitBoard = new BitBoard();
        String fen = "7k/8/R7/1R3p2/8/8/8/K7 w - - 1 1";
        // 3r1rk1/p3qppp/1pnbbn2/2ppp3/8/8/PPPPPPPP/RNBQKBNR w KQ - 0 1
        bitBoard.loadFromFen(fen);
        bitBoard.printChessBoard();
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
        MaterialAlgorithm materialAlgorithm = new MaterialAlgorithm(6);
        CustomAlgorithm customAlgorithm = new CustomAlgorithm(7);
        RandomAlgorithm randomAlgorithm = new RandomAlgorithm();
        AlphaBeta alphaBeta = new AlphaBeta(5);

        Engine engine2 = new Engine(bitBoard);
        engine2.setAlgorithm(randomAlgorithm);

        Engine engine1 = new Engine(bitBoard);
        engine1.setAlgorithm(alphaBeta);
        
        // play 100 random moves 
        for (int i = 0; i < 150; i++) {
            if (engine1.getBoard().isCheckMate()) {
                System.out.println("Checkmate!");
                break;
            }
            if (engine1.getBoard().isStaleMate()) {
                bitBoard.printChessBoard();
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
            bitBoard.printChessBoard();

        }

        System.out.println("[FEN " + fen + "]");
        System.out.println(engine1.getPGN());

        
        

    }
}