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

        // Test Zobrist
        Move move1 = new Move("b1c3", bitBoard);
        Move move2 = new Move("b8c6", bitBoard);
        Move move3 = new Move("c3b1", bitBoard);
        Move move4 = new Move("c6b8", bitBoard);
        bitBoard.makeMove(PackedMove.encode(new Move("e2e4", bitBoard)));
        bitBoard.makeMove(PackedMove.encode(new Move("a7a6", bitBoard)));
        bitBoard.makeMove(PackedMove.encode(move1));
        bitBoard.makeMove(PackedMove.encode(move2));
        bitBoard.makeMove(PackedMove.encode(move3));
        bitBoard.makeMove(PackedMove.encode(move4));
        bitBoard.printChessBoard();
        System.out.println(bitBoard.isThreefoldRepetition());
        bitBoard.history.printStack();
        // System.out.println(Perft.perft(bitBoard, 2));



    }
}