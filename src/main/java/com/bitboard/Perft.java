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
    
            // Obtenir le nombre de noeuds pour ce coup
            long moveNodes = perft(bitBoard, depth - 1);
    
            // Affiche le coup du premier niveau
            System.out.println(move.toString() + ": " + moveNodes);
    
            // Si on est à une profondeur suffisante, afficher les coups du second niveau
            if (depth > 1) {
                MoveList secondLevelMoves = bitBoard.getLegalMoves(); // coups après ce move
                for (int j = 0; j < secondLevelMoves.size(); j++) {
                    Move secondMove = secondLevelMoves.get(j);
                    bitBoard.makeMove(secondMove);
    
                    // Obtenir le nombre de noeuds pour le second coup
                    long secondMoveNodes = perft(bitBoard, depth - 2);
    
                    // Affiche le coup du second niveau avec indentation
                    System.out.println("    - " + secondMove.toString() + ": " + secondMoveNodes);
    
                    bitBoard.undoMove();
                }
            }
    
            bitBoard.undoMove();
            totalNodes += moveNodes;
        }
    
        // Affiche le nombre total de nœuds
        System.out.println();
        System.out.println("Nodes searched: " + totalNodes);
    }
    

    public static String perftDivideString(BitBoard bitBoard, int depth) {
        String result = "";
        result += "[PerftDivide]\n";
        result += "[Depth: " + depth + "]\n";
        result += "[x]\n";

        MoveList moveList = bitBoard.getLegalMoves();
        long totalNodes = 0;
    
        // Itère sur chaque coup possible au premier niveau
        for (int i = 0; i < moveList.size(); i++) {
            Move move = moveList.get(i);
            bitBoard.makeMove(move);
    
            long moveNodes = perft(bitBoard, depth - 1);
            bitBoard.undoMove();
    
            // Affiche le coup et le nombre de nœuds associés
            result += move.toString() + ": " + moveNodes + "\n";
    
            totalNodes += moveNodes;
        }
    
        // Affiche le nombre total de nœuds
        result += "\nNodes searched: " + totalNodes + "\n";
        return result;
    }

    // Version pour afficher les nœuds à partir d'une position donnée
    public static void perftStart(BitBoard bitBoard, int depth) {
        long nodes = perft(bitBoard, depth);
        System.out.println("Nodes searched: " + nodes);
    }
    

    public static void main(String[] args) {
        BitBoard bitBoard = new BitBoard();

        // // e2e3
        // Move move = new Move(4+8, 4+8+8, BitBoard.PAWN, BitBoard.PAWN);
        // bitBoard.makeMove(move);

        // // f7f5
        // move = new Move(5+6*8, 5+4*8, BitBoard.PAWN, BitBoard.PAWN);
        // bitBoard.makeMove(move);

        // // b1a3
        // Move move = new Move(1, 16, BitBoard.KNIGHT, BitBoard.KNIGHT);
        // bitBoard.makeMove(move);

        // // b8a6
        // move = new Move(57, 40, BitBoard.KNIGHT, BitBoard.KNIGHT);
        // bitBoard.makeMove(move);

        // load a position


        // bitBoard.loadFromFen("8/8/8/8/8/8/3P4/8 w - - 0 1");
        // // b7b5
        long time = System.currentTimeMillis();
        System.out.println(perft(bitBoard, 6));
        System.out.println("Seconds: " + (System.currentTimeMillis() - time) / 1000.0);



    }
}
