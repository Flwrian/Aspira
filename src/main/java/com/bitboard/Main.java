package com.bitboard;

import com.bitboard.algorithms.NewChessAlgorithm;
import com.bitboard.algorithms.Zobrist;

public class Main {
    
    public static void main(String[] args) {
        BitBoard bitBoard = new BitBoard();
        // String fen = "4b1k1/5n2/4P1p1/3p2P1/3Pq1B1/2p1BRKQ/1r6/8 b - - 0 1";
        // 3r1rk1/p3qppp/1pnbbn2/2ppp3/8/8/PPPPPPPP/RNBQKBNR w KQ - 0 1
        // bitBoard.loadFromFen(fen);
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
        bitBoard.loadFromFen(BitBoard.INITIAL_STARTING_POSITION);

        NewTT tt = new NewTT(16); // 16 Mo de TT



    }
}