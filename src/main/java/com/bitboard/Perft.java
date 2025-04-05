package com.bitboard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Perft {

    public static long perft(BitBoard bitBoard, int depth) {

        if (depth == 0) {
            return 1;
        }

        PackedMoveList moveList = bitBoard.getLegalMoves();
        long nodes = 0;
        
        for (int i = 0; i < moveList.size(); i++) {
            bitBoard.makeMove(moveList.get(i));
            // Compare hash key
            // if (bitBoard.generateZobristKey() != bitBoard.zobristKey) {
            //     // le soucis c'est le pieceType dans la movegen qui n'est pas le bon (Bitboard.PAWN au lieu de WHITEPAWN ou BLACKPAWN)
            //     System.out.println("Hash key mismatch!");
            //     System.out.println("Expected: " + Long.toHexString(bitBoard.zobristKey) + " Found: "
            //             + Long.toHexString(bitBoard.generateZobristKey()));
            //     System.out.println("Depth: " + depth);
            //     System.out.println("FEN: " + bitBoard.getFen());
            //     System.out.println("move: " + i);
            // }
            nodes += perft(bitBoard, depth - 1);
            bitBoard.undoMove();
        }

        return nodes;
    }

    public static String perftDivideString(BitBoard bitBoard, int depth) {
        String result = "";
        result += "[PerftDivide]\n";
        result += "[Depth: " + depth + "]\n";
        result += "[ === ]\n";

        PackedMoveList moveList = bitBoard.getLegalMoves();
        long totalNodes = 0;

        long time = System.currentTimeMillis();

        // Itère sur chaque coup possible au premier niveau
        for (int i = 0; i < moveList.size(); i++) {
            long move = moveList.get(i);
            bitBoard.makeMove(move);
            // Compare hash key
            // if (bitBoard.generateZobristKey() != bitBoard.zobristKey) {
            //     System.out.println("Hash key mismatch!");
            //     System.out.println("Expected: " + Long.toHexString(bitBoard.zobristKey) + " Found: "
            //             + Long.toHexString(bitBoard.generateZobristKey()));
            //     System.out.println("Depth: " + depth);
            //     System.out.println("FEN: " + bitBoard.getFen());
            //     System.out.println("move: " + i);
            //     bitBoard.printChessBoard();
            //     System.exit(1);
            // }
            long moveNodes = perft(bitBoard, depth - 1);
            bitBoard.undoMove();

            // Affiche le coup et le nombre de nœuds associés
            Move m2 = PackedMove.unpack(move);
            result += m2.toString() + ": " + moveNodes + "\n";

            totalNodes += moveNodes;
        }
        long time2 = System.currentTimeMillis();

        result += "|--------------------------------------------|\n";
        result += "| Time: " + (time2 - time) + "ms\n";
        result += "| Nodes: " + totalNodes + "\n";
        result += "| Nodes/second (M): " + (float) (totalNodes * 1000) / (time2 - time) / 1000000 + "\n";
        result += "|--------------------------------------------|\n";
        return result;
    }

    public static void perftSuiteTest(String fileName, int cores) {
        // Open the file
        String line;
        int numberOfTests = 0;

        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║             PERFORMANCE TEST SUITE RUNNER            ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println("► File: " + fileName);
        System.out.println("► Cores: " + cores);

        // Read each line of the file to count tests
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while ((line = br.readLine()) != null) {
                numberOfTests++;
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
            return;
        }

        System.out.println("► Total positions to test: " + numberOfTests);
        System.out.println();

        long startingTime = System.currentTimeMillis();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(cores);
            List<Future<TestResult>> futures = new ArrayList<>();

            int testsDone = 0;
            int passedTests = 0;
            int failedTests = 0;
            long totalExpectedNodes = 0;
            long totalActualNodes = 0;
            long totalTime = 0;

            // Submit all tests to the executor
            while ((line = br.readLine()) != null) {
                final String testLine = line;
                Future<TestResult> future = executor.submit(() -> {
                    TestResult result = new TestResult();
                    StringBuilder outputBuilder = new StringBuilder();

                    // Split the line into FEN and depth results
                    String[] parts = testLine.split(";");
                    String fen = parts[0].trim();
                    BitBoard bitBoard = new BitBoard();
                    bitBoard.loadFromFen(fen);

                    outputBuilder.append("┌────────────────────────────────────────────────────────┐\n");
                    outputBuilder.append("│ FEN: ").append(formatFen(fen)).append("\n");
                    outputBuilder.append("├─────────┬────────────────┬────────────────┬────────────┤\n");
                    outputBuilder.append("│  Depth  │    Expected    │     Actual     │    Time    │\n");
                    outputBuilder.append("├─────────┼────────────────┼────────────────┼────────────┤\n");

                    for (int i = 1; i < parts.length; i++) {
                        String[] depthResult = parts[i].trim().split(" ");
                        int depth = Integer.parseInt(depthResult[0].substring(1));
                        long expectedNodes = Long.parseLong(depthResult[1]);

                        long startTime = System.currentTimeMillis();
                        long actualNodes = perft(bitBoard, depth);
                        long endTime = System.currentTimeMillis();
                        double duration = (endTime - startTime) / 1000.0;

                        String status = (actualNodes == expectedNodes) ? "✓" : "✗";
                        outputBuilder.append(String.format("│   %2d    │ %,14d │ %,14d │%8.3fs %s │%n",
                                depth, expectedNodes, actualNodes, duration, status));

                        result.expectedNodes += expectedNodes;
                        result.actualNodes += actualNodes;
                        result.time += (endTime - startTime);

                        if (actualNodes == expectedNodes) {
                            result.passedTests++;
                        } else {
                            result.failedTests++;
                            outputBuilder.append("├─────────┴────────────────┴────────────────┴────────────┤\n");
                            outputBuilder.append("│ ❌ ERROR: Mismatch at depth ").append(depth).append("\n");
                            outputBuilder.append("│ Expected: ").append(expectedNodes).append(", Actual: ")
                                    .append(actualNodes).append("\n");
                            outputBuilder.append("└─────────────────────────────────────────────────────────┘\n");
                        }
                    }

                    if (result.failedTests == 0) {
                        outputBuilder.append("└─────────┴────────────────┴────────────────┴────────────┘\n");
                    }

                    // Print the complete output at once
                    synchronized (System.out) {
                        System.out.print(outputBuilder.toString());
                    }

                    return result;
                });

                futures.add(future);
            }

            // Wait for all tests to complete
            for (Future<TestResult> future : futures) {
                try {
                    TestResult result = future.get();
                    testsDone++;
                    passedTests += result.passedTests;
                    failedTests += result.failedTests;
                    totalExpectedNodes += result.expectedNodes;
                    totalActualNodes += result.actualNodes;
                    totalTime += result.time;

                    // Display progress
                    int progressPercent = (testsDone * 100) / numberOfTests;
                    String progressBar = createProgressBar(progressPercent);

                    System.out.println("\n╔══════════════════════════════════════════════════════╗");
                    System.out.println("║                   PROGRESS UPDATE                    ║");
                    System.out.println("╠══════════════════════════════════════════════════════╣");
                    System.out.println("║         " + progressBar + " " + progressPercent + "%          ║");
                    System.out.println("╠═══════════════════════════╦══════════════════════════╣");
                    System.out.printf("║ Positions: %3d/%-3d        ║ Pass Rate: %3d%%          ║%n",
                            testsDone, numberOfTests, (passedTests * 100) / (passedTests + failedTests));
                    System.out.printf("║ Tests Passed: %-10d  ║ Tests Failed: %-9d  ║%n",
                            passedTests, failedTests);
                    System.out.println("╠═══════════════════════════╩═════════════════════════╣");
                    System.out.printf("║ Elapsed: %-8.2fs          Speed: %,.2f MN/s        ║%n",
                            (System.currentTimeMillis() - startingTime) / 1000.0,
                            (totalActualNodes / 1000000.0) / (totalTime / 1000.0));
                    System.out.println("╚══════════════════════════════════════════════════════╝\n");
                } catch (Exception e) {
                    System.out.println("❌ Error in test execution: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            executor.shutdown();

            // Final summary
            double totalRealTime = (System.currentTimeMillis() - startingTime) / 1000.0;
            double nodesPerSecond = (totalActualNodes / 1000000.0) / totalRealTime;

            System.out.println("\n╔══════════════════════════════════════════════════════╗");
            System.out.println("║                 PERFORMANCE SUMMARY                  ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
            System.out.printf("► Total Positions Tested: %d%n", numberOfTests);
            System.out.printf("► Tests Passed: %d (%d%%)%n", passedTests,
                    (passedTests * 100) / (passedTests + failedTests));
            System.out.printf("► Tests Failed: %d%n", failedTests);
            System.out.printf("► Total Time: %.2fs%n", totalRealTime);
            System.out.printf("► Total Nodes: %,d%n", totalActualNodes);
            System.out.printf("► Nodes/Second: %.2fM%n", nodesPerSecond);
            System.out.printf("► Expected Nodes: %,d%n", totalExpectedNodes);
            System.out.printf("► Actual Nodes: %,d%n", totalActualNodes);
            System.out.printf("► Time per Test: %.2fs%n", totalRealTime / numberOfTests);
            System.out.printf("► Speed: %.2f MN/s%n", nodesPerSecond);
            System.out.printf("► Parallel Speed: %.2f MN/s%n", (totalActualNodes / 1000000.0) / (totalTime / 1000.0));
            System.out.printf("► Parallel Efficiency: %.2f%%%n", (totalTime / 1000.0) / (totalRealTime * cores) * 100);
            System.out.printf("► Thread(s) Used: %d%n", cores);

        } catch (IOException e) {
            System.out.println("❌ Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String formatFen(String fen) {
        if (fen.length() > 45) {
            return fen.substring(0, 42) + "...";
        }
        return fen;
    }

    private static String createProgressBar(int percent) {
        int width = 30;
        int completed = width * percent / 100;

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < width; i++) {
            if (i < completed) {
                sb.append("#");
            } else {
                sb.append("-");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static class TestResult {
        int passedTests = 0;
        int failedTests = 0;
        long expectedNodes = 0;
        long actualNodes = 0;
        long time = 0;
    }
    // // Version pour afficher les nœuds de chaque coup de départ
    // public static void perftDivide(BitBoard bitBoard, int depth) {
    // MoveList moveList = bitBoard.getLegalMoves();
    // long totalNodes = 0;

    // // Itère sur chaque coup possible au premier niveau
    // for (int i = 0; i < moveList.size(); i++) {
    // Move move = moveList.get(i);
    // bitBoard.makeMove(move);

    // // Obtenir le nombre de noeuds pour ce coup
    // long moveNodes = perft(bitBoard, depth - 1);

    // // Affiche le coup du premier niveau
    // System.out.println(move.toString() + ": " + moveNodes);

    // // Si on est à une profondeur suffisante, afficher les coups du second niveau
    // if (depth > 1) {
    // MoveList secondLevelMoves = bitBoard.getLegalMoves(); // coups après ce move
    // for (int j = 0; j < secondLevelMoves.size(); j++) {
    // Move secondMove = secondLevelMoves.get(j);
    // bitBoard.makeMove(secondMove);

    // // Obtenir le nombre de noeuds pour le second coup
    // long secondMoveNodes = perft(bitBoard, depth - 2);

    // // Affiche le coup du second niveau avec indentation
    // System.out.println(" - " + secondMove.toString() + ": " + secondMoveNodes);

    // bitBoard.undoMove();
    // }
    // }

    // bitBoard.undoMove();
    // totalNodes += moveNodes;
    // }

    // // Affiche le nombre total de nœuds
    // System.out.println();
    // System.out.println("Nodes searched: " + totalNodes);
    // }

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

    // private static long perftPseudoLegal(BitBoard bitBoard, int depth) {
    // if (depth == 0) {
    // return 1;
    // }

    // MoveList moveList = bitBoard.getPseudoLegalMoves();
    // long nodes = 0;

    // for (int i = 0; i < moveList.size(); i++) {
    // bitBoard.makeMove(moveList.get(i));
    // nodes += perftPseudoLegal(bitBoard, depth - 1);
    // bitBoard.undoMove();
    // }

    // return nodes;
    // }

    // public static String perftDivideStringPseudoLegal(BitBoard bitBoard, int
    // depth) {
    // String result = "";
    // result += "[PerftDivide]\n";
    // result += "[Depth: " + depth + "]\n";
    // result += "[ === ]\n";

    // MoveList moveList = bitBoard.getPseudoLegalMoves();
    // long totalNodes = 0;

    // // Itère sur chaque coup possible au premier niveau
    // for (int i = 0; i < moveList.size(); i++) {
    // Move move = moveList.get(i);
    // bitBoard.makeMove(move);

    // long moveNodes = perftPseudoLegal(bitBoard, depth - 1);
    // bitBoard.undoMove();

    // // Affiche le coup et le nombre de nœuds associés
    // result += move.toString() + ": " + moveNodes + "\n";

    // totalNodes += moveNodes;
    // }

    // // Affiche le nombre total de nœuds
    // result += "\nNodes searched: " + totalNodes + "\n";
    // return result;
    // }

    // // Version pour afficher les nœuds à partir d'une position donnée
    // public static void perftStart(BitBoard bitBoard, int depth) {
    // long nodes = perft(bitBoard, depth);
    // System.out.println("Nodes searched: " + nodes);
    // }

    // public static void main(String[] args) {
    // BitBoard bitBoard = new BitBoard();

    // // // e2e3
    // // Move move = new Move(4+8, 4+8+8, BitBoard.PAWN, BitBoard.PAWN);
    // // bitBoard.makeMove(move);

    // // // f7f5
    // // move = new Move(5+6*8, 5+4*8, BitBoard.PAWN, BitBoard.PAWN);
    // // bitBoard.makeMove(move);

    // // // b1a3
    // // Move move = new Move(1, 16, BitBoard.KNIGHT, BitBoard.KNIGHT);
    // // bitBoard.makeMove(move);

    // // // b8a6
    // // move = new Move(57, 40, BitBoard.KNIGHT, BitBoard.KNIGHT);
    // // bitBoard.makeMove(move);

    // // load a position

    // //
    // bitBoard.loadFromFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R
    // w KQkq - 0 1");
    // // // // b7b5
    // // long time = System.currentTimeMillis();
    // // System.out.println(perft(bitBoard, 5));
    // // System.out.println("Seconds: " + (System.currentTimeMillis() - time) /
    // 1000.0);

    // bitBoard.loadFromFen("n1n5/1Pk5/8/8/8/8/5Kp1/5N1N b - - 0 1");
    // perftDivide(bitBoard, 4);

    // }

    // public static int perftOnCaptures(BitBoard bitBoard, int depth) {
    // if (depth == 0) {
    // return 1;
    // }

    // MoveList moveList = bitBoard.getCaptureMoves();
    // if (moveList.size() == 0) {
    // return 0;
    // }
    // int nodes = 0;

    // for (int i = 0; i < moveList.size(); i++) {
    // Move move = moveList.get(i);
    // bitBoard.makeMove(move);
    // nodes += perftOnCaptures(bitBoard, depth - 1);
    // bitBoard.undoMove();
    // }

    // return nodes;
    // }
}
