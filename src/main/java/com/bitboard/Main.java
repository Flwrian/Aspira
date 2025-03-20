package com.bitboard;

import com.bitboard.algorithms.AdvancedChessAlgorithm;
import com.bitboard.algorithms.AlphaBeta;
import com.bitboard.algorithms.CustomAlgorithm;
import com.bitboard.algorithms.MaterialAlgorithm;
import com.bitboard.algorithms.NewChessAlgorithm;
import com.bitboard.algorithms.RandomAlgorithm;

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
        bitBoard.printChessBoard();

        // System.out.println(bitBoard.getLegalMoves());

        System.out.println("Testing Magic Bitboards for Rook and Bishop Moves");
        
        MoveGenerator.initialize();
        // bitBoard.printBitBoard(MoveGenerator.rookAttacks[27][4]);
        // bitBoard.printBitBoard(MoveGenerator.getRookAttacks(27, BitBoard.A1));

        bitBoard.printBitBoard(MoveGenerator.generateRookAttackBoard(BitBoard.getLSB(bitBoard.whiteRooks), bitBoard));
        
        // Engine engine1 = new Engine(bitBoard, 4, new AdvancedChessAlgorithm(4));
        // Engine engine2 = new Engine(bitBoard, 4, new AdvancedChessAlgorithm(4));
        
        // int maxMoves = 25; // Nombre maximal de coups
        // int moveCount = 0;
        
        // while (!bitBoard.isCheckMate() && !bitBoard.isStaleMate() && moveCount < maxMoves) {
        //     System.out.println("Engine 1 playing...");
        //     System.out.println(engine1.getBoard().whiteCastleKingSide);
        //     engine1.play();
        //     // engine2.addMoveToPGN(engine1.getLastMove());
        //     bitBoard.printChessBoard();
        //     moveCount++;
        //     if (bitBoard.isCheckMate() || bitBoard.isStaleMate()) break;
            
        //     if (moveCount >= maxMoves) break;
            
        //     System.out.println("Engine 2 playing...");
        //     engine2.play();
        //     engine1.addMoveToPGN(engine2.getLastMove());
        //     bitBoard.printChessBoard();
        //     moveCount++;
        // }
        
        // System.out.println("Game Over.");
        
        // System.out.println("PGN:");
        // System.out.println(engine1.getPGN());

    }
}