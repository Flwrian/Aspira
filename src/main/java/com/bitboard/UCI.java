package com.bitboard;
import java.util.*;


import com.bitboard.algorithms.NewChessAlgorithm;


/**
 * This class is the UCI interface for the chess engine.
 * It is responsible for communicating with the GUI.
 */
public class UCI {
    

    private static String ENGINE_NAME = "Aspira";
    private static String AUTHOR = "Flwrian";
    private static String VERSION = "1.0";

    private static String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private static BitBoard board = new BitBoard();
    // private static Engine engine;
    static Engine engine = new Engine(board);
    
    private static int nbCommands = 0;

    public static void main(String[] args) {

//         System.out.println("""
//  _______________________________________________________________________________________

// |  ███████╗██╗      ██████╗ ██╗    ██╗ ██████╗ ██╗███╗   ██╗███████╗██╗   ██╗██████╗   |
// |  ██╔════╝██║     ██╔═══██╗██║    ██║██╔════╝ ██║████╗  ██║██╔════╝██║   ██║╚════██╗  |
// |  █████╗  ██║     ██║   ██║██║ █╗ ██║██║  ███╗██║██╔██╗ ██║█████╗  ██║   ██║ █████╔╝  |
// |  ██╔══╝  ██║     ██║   ██║██║███╗██║██║   ██║██║██║╚██╗██║██╔══╝  ╚██╗ ██╔╝██╔═══╝   |
// |  ██║     ███████╗╚██████╔╝╚███╔███╔╝╚██████╔╝██║██║ ╚████║███████╗ ╚████╔╝ ███████╗  |
// |  ╚═╝     ╚══════╝ ╚═════╝  ╚══╝╚══╝  ╚═════╝ ╚═╝╚═╝  ╚═══╝╚══════╝  ╚═══╝  ╚══════╝  |
// _______________________________________________________________________________________

//         """);

        // // print available commands
        // System.out.println("Available commands:");
        // System.out.println("uci - start the engine");
        // System.out.println("isready - check if the engine is ready");
        // System.out.println("ucinewgame - start a new game");
        // System.out.println("position - set up the position");
        // System.out.println("go - start calculating the best move");
        // System.out.println("quit - stop the engine");
        // System.out.println("stop - stop calculating the best move");
        // System.out.println("option - set engine options");
        // System.out.println();

        NewChessAlgorithm advancedChessAlgorithm = new NewChessAlgorithm();
        engine.setAlgorithm(advancedChessAlgorithm);

        board.loadFromFen(STARTING_POSITION);

        // init movegenerator
        MoveGenerator.initSlidingAttacks();

        
        Scanner scanner = new Scanner(System.in);
        // Handle the UCI commands
        while(true){
            String input = scanner.nextLine();
            String[] inputArray = input.split(" ");
            String command = inputArray[0];

            nbCommands++;

            


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
                case "stop":
                    engine.getAlgorithm().setStopSearch(true);
                    break;
                case "perft-test":
                    Perft.perftSuiteTest("./perft-suite/standard.epd", Integer.parseInt(inputArray[1]));
                    break;
                case "quit":
                    quit();
                    break;
                case "option":
                    option(inputArray);
                    break;
                case "setdepth":
                    setDepth(Integer.parseInt(inputArray[1]));
                    break;
                case "setrazordepth":
                    // setRazorDepth(Integer.parseInt(inputArray[1]));
                    break;
                case "setnpm":
                    // setNPM(Integer.parseInt(inputArray[1]));
                    break;
                case "d":
                    d();
                    break;
                case "calculateNPS":
                    System.out.println(calculateNPS(Integer.parseInt(inputArray[1])));
                    break;
                case "help":
                    help();
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    break;
            }
        }
    }

    private static void option(String[] inputArray) {
    }

    private static void help() {
        System.out.println("""
         _______________________________________________________________________________________
    
        |  ███████╗██╗      ██████╗ ██╗    ██╗ ██████╗ ██╗███╗   ██╗███████╗██╗   ██╗██████╗   |
        |  ██╔════╝██║     ██╔═══██╗██║    ██║██╔════╝ ██║████╗  ██║██╔════╝██║   ██║╚════██╗  |
        |  █████╗  ██║     ██║   ██║██║ █╗ ██║██║  ███╗██║██╔██╗ ██║█████╗  ██║   ██║ █████╔╝  |
        |  ██╔══╝  ██║     ██║   ██║██║███╗██║██║   ██║██║██║╚██╗██║██╔══╝  ╚██╗ ██╔╝██╔═══╝   |
        |  ██║     ███████╗╚██████╔╝╚███╔███╔╝╚██████╔╝██║██║ ╚████║███████╗ ╚████╔╝ ███████╗  |
        |  ╚═╝     ╚══════╝ ╚═════╝  ╚══╝╚══╝  ╚═════╝ ╚═╝╚═╝  ╚═══╝╚══════╝  ╚═══╝  ╚══════╝  |
         _______________________________________________________________________________________
    
        Available commands:
        -----------------------------------------------------------------------------------------
        | uci                  | Start the engine and display information about the engine.      |
        -----------------------------------------------------------------------------------------
        | isready              | Check if the engine is ready to receive commands.               |
        -----------------------------------------------------------------------------------------
        | ucinewgame           | Start a new game and reset the board.                           |
        -----------------------------------------------------------------------------------------
        | position [args]      | Set up the position. Examples:                                  |
        |                      | - position startpos                                            |
        |                      | - position fen <fen-string> moves <move1> <move2> ...          |
        -----------------------------------------------------------------------------------------
        | go [args]            | Start calculating the best move. Optional arguments:            |
        |                      | - perft <depth>                                                |
        |                      | - depth <depth>                                                |
        |                      | - wtime <ms> btime <ms> winc <ms> binc <ms>                    |
        -----------------------------------------------------------------------------------------
        | stop                 | Stop calculating the best move.                                |
        -----------------------------------------------------------------------------------------
        | quit                 | Quit the engine and terminate the process.                     |
        -----------------------------------------------------------------------------------------
        | option [args]        | Set engine options. Examples:                                   |
        |                      | - option name Hash type spin default 16 min 1 max 1024         |
        |                      | - option name Threads type spin default 1 min 1 max 1024       |
        -----------------------------------------------------------------------------------------
        | setdepth <int>       | Set the search depth to the given integer value.               |
        -----------------------------------------------------------------------------------------
        | setrazordepth <int>  | Set the razoring depth for shallow pruning.                    |
        -----------------------------------------------------------------------------------------
        | setnpm <int>         | Set the Null-Pruning Move threshold.                           |
        -----------------------------------------------------------------------------------------
        | d                    | Display the current chess board.                               |
        -----------------------------------------------------------------------------------------
        | calculateNPS <int>   | Calculate the Nodes Per Second over the given milliseconds.    |
        -----------------------------------------------------------------------------------------
        """);
    }
    

    public static void d(){
        board.printChessBoard();
        // var legalMoves = board.getLegalMoves();
        // legalMoves.sortByScore();
        // System.out.println("Legal moves: " + legalMoves);
    }

    private static void quit() {
        System.exit(0);
    }

    private static String calculateNPS(int timeMS) {
        return Perft.calculateNPS(board, timeMS);
    }

    private static void go(String[] tokens) {
        if (tokens.length < 2) {
            return;
        }
        
        switch (tokens[1]) {
            case "perft":
                if (tokens.length < 3) return;
                handlePerftCommand(tokens);
                break;
                
            default:
                handleSearchCommand(tokens);
                break;
        }
    }

    private static void handlePerftCommand(String[] tokens) {
        int depth = Integer.parseInt(tokens[2]);
        long time = System.currentTimeMillis();
        String perft = Perft.perftDivideString(board, depth);
        time = System.currentTimeMillis() - time;
        
        System.out.println(perft);
        System.out.println("Time: " + time + "ms");
    }

    private static void handleSearchCommand(String[] tokens) {
        int depth = 11;
        int wtime = 99999999;
        int btime = 99999999;
        int winc = 0;
        int binc = 0;
        int movetime = 0;
        
        for (int i = 1; i < tokens.length; i += 2) {
            if (i + 1 >= tokens.length) break;
            
            String arg = tokens[i + 1];
            switch (tokens[i]) {
                case "depth" -> depth = Integer.parseInt(arg);
                case "wtime" -> wtime = Integer.parseInt(arg);
                case "btime" -> btime = Integer.parseInt(arg);
                case "winc" -> winc = Integer.parseInt(arg);
                case "binc" -> binc = Integer.parseInt(arg);
                case "movetime" -> movetime = Integer.parseInt(arg);
            }
        }
        
        final int finalWtime = wtime;
        final int finalBtime = btime;
        final int finalWinc = winc;
        final int finalBinc = binc;
        final int finalMovetime = movetime;
        final int finalDepth = depth;
        
        Thread searchThread = new Thread(() -> {
            engine.getAlgorithm().setStopSearch(false);
            engine.getAlgorithm().search(board, finalWtime, finalBtime, finalWinc, finalBinc, finalMovetime, finalDepth);
        });
        searchThread.start();
    }

    private static void setDepth(int depth) {
        engine.setDepth(depth);
        System.out.println("Depth set to " + depth);
    }

    private static void position(String[] inputArray) {
        // Example: position startpos moves e2e4 e7e5
        // Example2: position fen rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 moves e2e4 e7e5
        // So we need to load the starting position and then make the moves

        if(inputArray[1].equals("fen")){
            // Load the position from the FEN string
            String fen = "";
            for(int i = 2; i < inputArray.length; i++){
                if(inputArray[i].equals("moves")){
                    
                }
                fen += inputArray[i] + " ";
            }
            board.history.clear();
            board.loadFromFen(fen);

            // Make the moves
            for(int i = 0; i < inputArray.length; i++){
                if(inputArray[i].equals("moves")){
                    for(int j = i + 1; j < inputArray.length; j++){
                        Move move = new Move(inputArray[j], board);
                        board.makeMove(PackedMove.encode(move));
                    }
                }
            }
        } else if (inputArray[1].equals("startpos")) {
            // Load the starting position
            board.history.clear();
            board.loadFromFen(STARTING_POSITION);

            // Make the moves
            for(int i = 0; i < inputArray.length; i++){
                if(inputArray[i].equals("moves")){
                    for(int j = i + 1; j < inputArray.length; j++){
                        Move move = new Move(inputArray[j], board);
                        board.makeMove(PackedMove.encode(move));
                    }
                }
            }
        }

    }

    private static void uciNewGame() {
        board = new BitBoard();
    }

    private static void isReady() {
        System.out.println("readyok");
    }

    private static void uci() {
        System.out.println("id name " + ENGINE_NAME);
        System.out.println("id author " + AUTHOR);
        System.out.println("id version " + VERSION);
        System.out.println("option name Hash type spin default 1 min 1 max 1");
        System.out.println("option name Threads type spin default 1 min 1 max 1");
        System.out.println();
        System.out.println("uciok");
    }

}
