package com.bitboard;

public class Perft {
    
    public static long perft(BitBoard bitBoard, int depth) {
        if (depth == 0) {
            return 1;
        }
        
        MoveList moveList = bitBoard.getLegalMoves();
        long nodes = 0;
    
        for (int i = 0; i < moveList.size(); i++) {
            bitBoard.makeMove(moveList.get(i));
            nodes += perft(bitBoard, depth - 1);
            bitBoard.undoMove();
        }
        
        return nodes;
    }
    
    // Version pour afficher les nœuds de chaque coup de départ
    public static void perftDivide(BitBoard bitBoard, int depth) {
        MoveList moveList = bitBoard.getLegalMoves();
        long totalNodes = 0;
    
        // Itère sur chaque coup possible au premier niveau
        for (int i = 0; i < moveList.size(); i++) {
            Move move = moveList.get(i);
            bitBoard.makeMove(move);
    
            long moveNodes = perft(bitBoard, depth - 1);
            bitBoard.undoMove();
    
            // Affiche le coup et le nombre de nœuds associés
            System.out.println(move.toString() + ": " + moveNodes);
    
            totalNodes += moveNodes;
        }
    
        // Affiche le nombre total de nœuds
        System.out.println("Nodes searched: " + totalNodes);
    }

    // Version pour afficher les nœuds à partir d'une position donnée
    public static void perftStart(BitBoard bitBoard, int depth) {
        long nodes = perft(bitBoard, depth);
        System.out.println("Nodes searched: " + nodes);
    }
    

    public static void main(String[] args) {
        BitBoard bitBoard = new BitBoard();

        // c2c3
        bitBoard.makeMove(new Move(5+8, 5+16, BitBoard.PAWN, BitBoard.PAWN));
        // e7e5
        bitBoard.makeMove(new Move(60-8, 60-8-16, BitBoard.PAWN, BitBoard.PAWN));

        System.out.println(bitBoard.getLegalMoves());
        bitBoard.printChessBoard();
        bitBoard.printBitBoardRaw();
    }
}
