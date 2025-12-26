package fr.flwrian.aspira.uci;

import java.util.*;

import fr.flwrian.aspira.bench.BenchRunner;
import fr.flwrian.aspira.board.Board;
import fr.flwrian.aspira.engine.Engine;
import fr.flwrian.aspira.move.Move;
import fr.flwrian.aspira.move.MoveGenerator;
import fr.flwrian.aspira.move.PackedMove;
import fr.flwrian.aspira.perft.Perft;
import fr.flwrian.aspira.search.AlphaBetaSearch;
import fr.flwrian.aspira.search.SearchAlgorithm;

/**
 * This class is the UCI interface for the chess engine.
 * It is responsible for communicating with the GUI.
 */
public class UCI {

    private static String ENGINE_NAME = "Aspira";
    private static String AUTHOR = "Flwrian";
    private static String VERSION = "1.0";

    private static String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private static Board board = new Board();
    static Engine engine = new Engine(board);

    private static SearchAlgorithm searchAlgorithm;

    public static void main(String[] args) {

        searchAlgorithm = new AlphaBetaSearch();
        engine.setSearchAlgorithm(searchAlgorithm);

        board.loadFromFen(STARTING_POSITION);

        // init movegenerator
        MoveGenerator.initSlidingAttacks();

        if (args.length == 1 && args[0].equals("bench")) {
            BenchRunner.run(engine, board);
        }

        Scanner scanner = new Scanner(System.in);
        // Handle the UCI commands
        while (true) {
            String input = scanner.nextLine();
            String[] inputArray = input.split(" ");
            String command = inputArray[0];

            switch (command) {
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
                    engine.getSearchAlgorithm().setStopSearch(true);
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
                case "d":
                    d();
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

                |   █████╗ ███████╗██████╗ ██╗██████╗  █████╗                        |
                |  ██╔══██╗██╔════╝██╔══██╗██║██╔══██╗██╔══██╗                       |
                |  ███████║███████╗██████╔╝██║██████╔╝███████║                       |
                |  ██╔══██║╚════██║██╔═══╝ ██║██╔══██╗██╔══██║                       |
                |  ██║  ██║███████║██║     ██║██║  ██║██║  ██║                       |
                |  ╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝                       |
                 _______________________________________________________________________________________

                  uci
                      Print engine identification and available options.

                  isready
                      Check if the engine is ready to receive commands.

                  ucinewgame
                      Reset internal state for a new game.

                  position startpos [moves ...]
                  position fen <fen-string> [moves ...]
                      Set up a position and optionally play moves.

                  go [options]
                      Start the search. Supported options:
                        depth <int>       Fixed search depth
                        wtime <ms>        White remaining time
                        btime <ms>        Black remaining time
                        winc <ms>         White increment
                        binc <ms>         Black increment
                        movetime <ms>     Fixed move time

                  stop
                      Stop the current search.

                  quit
                      Quit the engine.

                  perft <depth>
                      Run perft divide at given depth on current position.

                  perft-test <threads>
                      Run perft test suite from standard.epd.

                  d
                      Display the current board state.

                  help
                      Display this help message.
                """);
    }

    public static void d() {
        board.printChessBoard();
    }

    private static void quit() {
        System.exit(0);
    }

    private static void go(String[] tokens) {
        if (tokens.length < 2) {
            return;
        }

        switch (tokens[1]) {
            case "perft":
                if (tokens.length < 3)
                    return;
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
        int depth = 128;
        int wtime = 99999999;
        int btime = 99999999;
        int winc = 0;
        int binc = 0;
        int movetime = 0;

        for (int i = 1; i < tokens.length; i += 2) {
            if (i + 1 >= tokens.length)
                break;

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
            engine.getSearchAlgorithm().setStopSearch(false);
            engine.getSearchAlgorithm().search(board, finalWtime, finalBtime, finalWinc, finalBinc, finalMovetime,
                    finalDepth);
        });
        searchThread.start();
    }

    private static void setDepth(int depth) {
        engine.setDepth(depth);
        System.out.println("Depth set to " + depth);
    }

    private static void position(String[] inputArray) {
        // Example: position startpos moves e2e4 e7e5
        // Example2: position fen rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0
        // 1 moves e2e4 e7e5
        // So we need to load the starting position and then make the moves

        if (inputArray[1].equals("fen")) {
            // Load the position from the FEN string
            String fen = "";
            for (int i = 2; i < inputArray.length; i++) {
                if (inputArray[i].equals("moves")) {

                }
                fen += inputArray[i] + " ";
            }
            board.history.clear();
            board.loadFromFen(fen);

            // Make the moves
            for (int i = 0; i < inputArray.length; i++) {
                if (inputArray[i].equals("moves")) {
                    for (int j = i + 1; j < inputArray.length; j++) {
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
            for (int i = 0; i < inputArray.length; i++) {
                if (inputArray[i].equals("moves")) {
                    for (int j = i + 1; j < inputArray.length; j++) {
                        Move move = new Move(inputArray[j], board);
                        board.makeMove(PackedMove.encode(move));
                    }
                }
            }
        }

    }

    private static void uciNewGame() {
        board = new Board();
        searchAlgorithm = new AlphaBetaSearch();
        engine = new Engine(board);
        engine.setSearchAlgorithm(searchAlgorithm);
        board.loadFromFen(STARTING_POSITION);
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
