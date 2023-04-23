package com;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.algorithms.AlphaBetaPruningAlgorithm;

/**
 * This class is the UCI interface for the chess engine.
 * It is responsible for communicating with the GUI.
 */
public class UCI {
    

    private static String ENGINE_NAME = "FlowBot";
    private static String AUTHOR = "Flwrian";
    private static String VERSION = "1.0";
    
    private static Board board;
    private static Engine engine;
    

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        // Handle the UCI commands
        while(true){
            String input = scanner.nextLine();
            String[] inputArray = input.split(" ");
            String command = inputArray[0];

            // Write the command to file for debugging
            try {
                FileWriter myWriter = new FileWriter("debug.log", true);
                myWriter.write(input);
                myWriter.write("\r\n");
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            switch(command){
                case "uci":
                    uci();
                    break;
                case "isready":
                    isReady();
                    break;
                case "ucinewgame":
                    uciNewGame();
                    break;
                case "position":
                    position(inputArray);
                    break;
                case "go":
                    go(inputArray);
                    break;
                case "quit":
                    quit();
                    break;
                case "stop":
                    stop();
                    break;
                case "option":
                    option(inputArray);
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    break;
            }
        }
    }

    private static void option(String[] inputArray) {
        if(inputArray[1].equals("name")){
            if(inputArray[2].equals("Debug Log File")){
                if(inputArray[3].equals("value")){
                    String fileName = inputArray[4];
                    try {
                        FileWriter myWriter = new FileWriter(fileName, true);
                        myWriter.write("Debug Log File: " + fileName);
                        myWriter.write("\r\n");
                        myWriter.close();
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void stop() {
        
    }

    private static void quit() {
        System.exit(0);
    }

    private static void go(String[] inputArray) {
        int depth = 0;
        int time = 0;
        for(int i = 1; i < inputArray.length; i++){
            if(inputArray[i].equals("depth")){
                depth = Integer.parseInt(inputArray[i + 1]);
            }
            else if(inputArray[i].equals("wtime")){
                time = Integer.parseInt(inputArray[i + 1]);
            }
            else if(inputArray[i].equals("btime")){
                time = Integer.parseInt(inputArray[i + 1]);
            }
        }
        if(depth == 0){
            depth = 6;
        }
        if(time == 0){
            time = 10000;
        }

        engine = new Engine(board);
        engine.setAlgorithm(new AlphaBetaPruningAlgorithm(depth));
        int[] move = engine.play();
        System.out.println("info score cp " + move[2] + " depth " + depth);
        System.out.println("bestmove " + engine.parseMove(move));

        // Move bestMove = board.getBestMove(depth, time);
        // System.out.println("bestmove " + bestMove);
    }

    private static void position(String[] inputArray) {
        // Example: position startpos moves e2e4 e7e5
        // So we need to load the starting position and then make the moves

        if(inputArray[1].equals("startpos")){
            board.loadFEN(Board.STARTING_FEN);
        }
        else if(inputArray[1].equals("fen")){
            String fen = "";
            for(int i = 2; i < inputArray.length; i++){
                if(inputArray[i].equals("moves")){
                    break;
                }
                fen += inputArray[i] + " ";
            }
            board.loadFEN(fen);
            board.printBoard();
        }
        if(inputArray.length > 2){
            for(int i = 2; i < inputArray.length; i++){
                if(inputArray[i].equals("moves")){
                    for(int j = i + 1; j < inputArray.length; j++){
                        board.makeMove(inputArray[j]);
                        board.flip();
                    }
                    break;
                }
            }
        }
    }

    private static void uciNewGame() {
        board = new Board();
    }

    private static void isReady() {
        System.out.println("readyok");
    }

    private static void uci() {
        System.out.println("id name " + ENGINE_NAME);
        System.out.println("id author " + AUTHOR);
        System.out.println("id version " + VERSION);
        System.out.println("option name Hash type spin default 16 min 1 max 1024");
        System.out.println("option name Threads type spin default 1 min 1 max 1024");
        System.out.println("option name Ponder type check default false");
        System.out.println("option name Debug Log File type string default debug.log");
        System.out.println();
        System.out.println("uciok");
    }

}
