package com.bitboard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

        public static void perftSuiteTest(String fileName) {
        // Open the file
        String line;

        int numberOfTests = 0;
        // Read each line of the file
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            // Count the number of lines in the file
            while ((line = br.readLine()) != null) {
                numberOfTests++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            int passedTests = 0;
            int failedTests = 0;
            long totalExpectedNodes = 0;
            long totalActualNodes = 0;
            long totalTime = 0;
            int testsDone = 0;

            while ((line = br.readLine()) != null) {
            
            // Split the line into FEN and depth results
            String[] parts = line.split(";");
            String fen = parts[0].trim();
            BitBoard bitBoard = new BitBoard();
            bitBoard.loadFromFen(fen);


            System.out.println("Testing FEN: " + fen);

            for (int i = 1; i < parts.length; i++) {
                String[] depthResult = parts[i].trim().split(" ");
                int depth = Integer.parseInt(depthResult[0].substring(1));
                long expectedNodes = Long.parseLong(depthResult[1]);

                long startTime = System.currentTimeMillis();
                long actualNodes = perft(bitBoard, depth);
                long endTime = System.currentTimeMillis();
                double duration = (endTime - startTime) / 1000.0;

                System.out.println("| Depth: " + depth + "   | Expected: " + expectedNodes + " | Actual: " + actualNodes + " | Time: " + duration + "s");
                
                
                totalExpectedNodes += expectedNodes;
                totalActualNodes += actualNodes;
                totalTime += (endTime - startTime);
                
                if (actualNodes == expectedNodes) {
                    passedTests++;
                } else {
                    failedTests++;
                    System.out.println("Test failed for FEN: " + fen + " at depth " + depth);
                    System.out.println("Mismatch at depth " + depth + ": expected " + expectedNodes + " but got " + actualNodes);
                }
            }
            testsDone++;
            // overall progress
            System.out.println("[--------------------------------------------]");
            System.out.println(" | Overall Progress: " + ((testsDone * 100) / numberOfTests) + "%");
            System.out.println(" | Total Tests: " + numberOfTests);
            System.out.println(" | Passed Tests: " + passedTests);
            System.out.println(" | Failed Tests: " + failedTests);
            System.out.println(" | Total Expected Nodes: " + totalExpectedNodes);
            System.out.println(" | Total Actual Nodes: " + totalActualNodes);
            System.out.println(" | Total Time: " + (totalTime / 1000.0) + "s");
            System.out.println("[--------------------------------------------]");
            }

            System.out.println("\nPerft Suite Test Summary:");
            System.out.println("Total Tests: " + numberOfTests);
            System.out.println("Passed Tests: " + passedTests);
            System.out.println("Total Expected Nodes: " + totalExpectedNodes);
            System.out.println("Total Actual Nodes: " + totalActualNodes);
            System.out.println("Total Time: " + (totalTime / 1000.0) + "s");
            System.out.println("Overall Progress: " + ((passedTests * 100) / numberOfTests) + "%");

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        result += "[ === ]\n";

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

    public static String calculateNPS(BitBoard bitBoard, long time) {
        // Search new nodes until time is up
        long nodes = 0;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + time;
        long currentTime = startTime;
        while (currentTime < endTime) {
            nodes += perft(bitBoard, 5);
            currentTime = System.currentTimeMillis();
        }
        long elapsedTime = currentTime - startTime;
        long nps = (nodes * 1000) / elapsedTime;
        // Parse NPS as Millions of nodes per second
        nps = nps / 1000000;
        return "Nodes: " + nodes + " Time: " + elapsedTime + " NPS: " + nps + "M";
    }

    private static long perftPseudoLegal(BitBoard bitBoard, int depth) {
        if (depth == 0) {
            return 1;
        }
        
        MoveList moveList = bitBoard.getPseudoLegalMoves();
        long nodes = 0;
    
        for (int i = 0; i < moveList.size(); i++) {
            bitBoard.makeMove(moveList.get(i));
            nodes += perftPseudoLegal(bitBoard, depth - 1);
            bitBoard.undoMove();
        }

        
        return nodes;
    }

    public static String perftDivideStringPseudoLegal(BitBoard bitBoard, int depth) {
        String result = "";
        result += "[PerftDivide]\n";
        result += "[Depth: " + depth + "]\n";
        result += "[ === ]\n";

        MoveList moveList = bitBoard.getPseudoLegalMoves();
        long totalNodes = 0;
    
        // Itère sur chaque coup possible au premier niveau
        for (int i = 0; i < moveList.size(); i++) {
            Move move = moveList.get(i);
            bitBoard.makeMove(move);
    
            long moveNodes = perftPseudoLegal(bitBoard, depth - 1);
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


        // bitBoard.loadFromFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        // // // b7b5
        // long time = System.currentTimeMillis();
        // System.out.println(perft(bitBoard, 5));
        // System.out.println("Seconds: " + (System.currentTimeMillis() - time) / 1000.0);

        
        bitBoard.loadFromFen("n1n5/1Pk5/8/8/8/8/5Kp1/5N1N b - - 0 1");
        perftDivide(bitBoard, 4);
        
    }

    public static int perftOnCaptures(BitBoard bitBoard, int depth) {
        if (depth == 0) {
            return 1;
        }
        
        MoveList moveList = bitBoard.getCaptureMoves();
        if (moveList.size() == 0) {
            return 0;
        }
        int nodes = 0;
    
        for (int i = 0; i < moveList.size(); i++) { 
            Move move = moveList.get(i);
            bitBoard.makeMove(move);
            nodes += perftOnCaptures(bitBoard, depth - 1);
            bitBoard.undoMove();
        }

        
        return nodes;
    }
}
