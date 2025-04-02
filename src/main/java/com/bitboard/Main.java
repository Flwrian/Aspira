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
        bitBoard.printChessBoard();

        // Test Zobrist
        Move move1 = new Move("h2h4", bitBoard);
        Move move2 = new Move("g8h6", bitBoard);
        Move move3 = new Move("h4h5", bitBoard);
        Move move4 = new Move("g7g5", bitBoard);
        Move move5 = new Move("h5g6", bitBoard);
        move5.setType(Move.EN_PASSENT);
        bitBoard.makeMove(PackedMove.encode(move1));
        bitBoard.makeMove(PackedMove.encode(move2));
        bitBoard.makeMove(PackedMove.encode(move3));
        bitBoard.makeMove(PackedMove.encode(move4));
        bitBoard.makeMove(PackedMove.encode(move5));
        bitBoard.printChessBoard();

        // System.out.println(Perft.perft(bitBoard, 2));



    }
}