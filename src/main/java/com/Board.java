package com;

import java.util.ArrayDeque;
import java.util.Arrays;

import com.pieces.Piece;

/**
 * Chess Board Class
 * 
 * <p>
 * The type is an integer that represents the type of piece
 * 1 = white pawn
 * 2 = white knight
 * 3 = white bishop
 * 4 = white rook
 * 5 = white queen
 * 6 = white king
 * 7 = black pawn
 * 8 = black knight
 * 9 = black bishop
 * 10 = black rook
 * 11 = black queen
 * 12 = black king
 * <p>
 */
public class Board {

    // ! We need to find a better way to store the board / pieces / moves (bitboards)

    public static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static String gamePGN = "";

    public int[] board = new int[64];

    // private Stack<int[]> boardHistory = new Stack<int[]>(); // Stores the board
    // history
    private ArrayDeque<int[]> boardHistory = new ArrayDeque<int[]>();

    public boolean whiteTurn;

    private int moveNumber = 1;

    public boolean canPlay = true;

    public Board() {
        boardHistory.push(board);
    }

    public void setupBoard() {
        for (int i = 0; i < 64; i++) {
            board[i] = 0;
        }

        whiteTurn = true;

        // Set up the board
        board[0] = 4;
        board[1] = 2;
        board[2] = 3;
        board[3] = 5;
        board[4] = 6;
        board[5] = 3;
        board[6] = 2;
        board[7] = 4;

        // Set up the pawns
        for (int i = 8; i < 16; i++) {
            board[i] = 1;
        }

        board[56] = 10;
        board[57] = 8;
        board[58] = 9;
        board[59] = 11;
        board[60] = 12;
        board[61] = 9;
        board[62] = 8;
        board[63] = 10;

        // Set up the pawns
        for (int i = 48; i < 56; i++) {
            board[i] = 7;
        }
    }

    public void setPiece(int position, int type) {
        board[position] = type;
    }

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;
    }
    /**
     * <p>
     * Push a move to the board, and backup the board
     * </p>
     * <p>
     * This method is VERY important, as it allows us to undo moves and it also
     * allows us to check for check and is very important for the AI
     * </p>
     */
    public void pushMove(int from, int destination) {
        if(!canPlay) throw new RuntimeException("Cannot play");
        int[] boardCopy = new int[64];
        System.arraycopy(board, 0, boardCopy, 0, 64);
        boardHistory.push(boardCopy);

        // Replace this
        // Piece piece = getPiece(from);
        // if (piece != null) {
        // piece.move(destination);
        // }
        // Piece.move(from, destination, this);
        Piece.move(from, destination, this);

        whiteTurn = !whiteTurn;

        boardHistory.push(board);

    }

    public void popMove() {
        if(!canPlay) setCanPlay(true);
        boardHistory.pop();
        board = boardHistory.pop();
        whiteTurn = !whiteTurn;
    }

    public void printTurn() {
        if (whiteTurn) {
            System.out.println("White's turn");
        } else {
            System.out.println("Black's turn");
        }
    }

    public void info(int square) {
        System.out.println("Piece: " + board[square]);
    }

    public boolean isStaleMate() {
        if (whiteTurn) {
            return countLegalMoves(1) == 0;
        } else {
            return countLegalMoves(1) == 0;
        }
    }

    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(board);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Board other = (Board) obj;
        if (!Arrays.equals(board, other.board))
            return false;
        return true;
    }

    public boolean is3FoldRepetition() {
        int count = 0;
        for (int[] board : boardHistory) {
            if (Arrays.equals(board, this.board)) {
                count++;
            }
        }
        return count >= 6;
    }

    // ? In addition to the possible way to optimize this, we can also use a
    // bitboard to store the pieces
    public int countLegalMoves(int depth) {
        if (depth == 0) {
            return 1;
        }
        int count = 0;
        for (int i = 0; i < 64; i++) {
            if (board[i] != 0 && board[i] < 7 == whiteTurn) {
                for (int j = 0; j < 64; j++) {
                    if (Piece.isLegalMove(board[i], i, j, this)) {
                        pushMove(i, j);
                        count += countLegalMoves(depth - 1);
                        popMove();
                    }
                }
            }
        }
        return count;
    }

    public int countValidMoves(int depth) {
        if (depth == 0) {
            return 1;
        }
        int count = 0;
        for (int i = 0; i < 64; i++) {
            if (board[i] != 0 && board[i] < 7 == whiteTurn) {
                for (int j = 0; j < 64; j++) {
                    if (Piece.isValidMove(board[i], i, j, this)) {
                        pushMove(i, j);
                        count += countValidMoves(depth - 1);
                        popMove();
                    }
                }
            }
        }
        return count;
    }

    // TODO: fix this
    public boolean isInCheck(boolean white) {
        int kingPosition = 0;
        for (int i = 0; i < 64; i++) {
            if (board[i] == 6 && white == true) {
                kingPosition = i;
                break;
            }
            if (board[i] == 12 && white == false) {
                kingPosition = i;
                break;
            }
        }

        for (int i = 0; i < 64; i++) {
            if (board[i] != 0 && board[i] < 7 != white) {
                if (Piece.isValidMove(board[i], i, kingPosition, this)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int[][] getLegalMoves() {
        int[][] moves = new int[64][64];
        int index = 0;
        for (int i = 0; i < 64; i++) {
            if (board[i] != 0 && board[i] < 7 == whiteTurn) {
                for (int j = 0; j < 64; j++) {
                    if (Piece.isLegalMove(board[i], i, j, this)) {
                        moves[index][0] = i;
                        moves[index][1] = j;
                        index++;
                    }
                }
            }
        }
        return moves;
    }

    public void printValidMoves(int piecePosition) {
        for (int i = 0; i < 64; i++) {
            if (Piece.isValidMove(board[piecePosition], piecePosition, i, this)) {
                System.out.println("Valid move: " + piecePosition + " -> " + i);
            }
        }
    }

    public void flip() {
        whiteTurn = !whiteTurn;
    }

    public void loadFEN(String fen) {
        FENLoader.loadFEN(fen, this);
    }

    public String getFEN() {
        StringBuilder sb = new StringBuilder();
        int emptyCounter = 0;
        for (int i = 0; i < board.length; i++) {
            int square = board[i];
            if (square == 0) {
                emptyCounter++;
            } else {
                if (emptyCounter > 0) {
                    sb.append(emptyCounter);
                    emptyCounter = 0;
                }
                switch (square) {
                    case 1:
                        sb.append("P");
                        break;
                    case 2:
                        sb.append("N");
                        break;
                    case 3:
                        sb.append("B");
                        break;
                    case 4:
                        sb.append("R");
                        break;
                    case 5:
                        sb.append("Q");
                        break;
                    case 6:
                        sb.append("K");
                        break;
                    case 7:
                        sb.append("p");
                        break;
                    case 8:
                        sb.append("n");
                        break;
                    case 9:
                        sb.append("b");
                        break;
                    case 10:
                        sb.append("r");
                        break;
                    case 11:
                        sb.append("q");
                        break;
                    case 12:
                        sb.append("k");
                        break;
                    default:
                        break;
                }
            }
            if ((i + 1) % 8 == 0) {
                if (emptyCounter > 0) {
                    sb.append(emptyCounter);
                    emptyCounter = 0;
                }
                if (i != 63) {
                    sb.append("/");
                }
            }
        }
        return sb.toString();
    }

    public void printBoard() {
        System.out.println("  a b c d e f g h");
        for (int i = 7; i >= 0; i--) {
            System.out.print((1 + i) + " ");
            for (int j = 0; j < 8; j++) {
                int piece = board[8 * i + j];
                String pieceName;
                switch (piece) {
                    case 1:
                        pieceName = "P";
                        break;
                    case 2:
                        pieceName = "N";
                        break;
                    case 3:
                        pieceName = "B";
                        break;
                    case 4:
                        pieceName = "R";
                        break;
                    case 5:
                        pieceName = "Q";
                        break;
                    case 6:
                        pieceName = "K";
                        break;
                    case 7:
                        pieceName = "p";
                        break;
                    case 8:
                        pieceName = "n";
                        break;
                    case 9:
                        pieceName = "b";
                        break;
                    case 10:
                        pieceName = "r";
                        break;
                    case 11:
                        pieceName = "q";
                        break;
                    case 12:
                        pieceName = "k";
                        break;
                    default:
                        pieceName = ".";
                        break;
                }
                System.out.print(pieceName + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printBoardInt() {
        System.out.println("  a b c d e f g h");
        for (int i = 7; i >= 0; i--) {
            System.out.print((1 + i) + " ");
            for (int j = 0; j < 8; j++) {
                int piece = board[8 * i + j];
                String pieceName;
                switch (piece) {
                    case 1:
                        pieceName = "1";
                        break;
                    case 2:
                        pieceName = "2";
                        break;
                    case 3:
                        pieceName = "3";
                        break;
                    case 4:
                        pieceName = "4";
                        break;
                    case 5:
                        pieceName = "5";
                        break;
                    case 6:
                        pieceName = "6";
                        break;
                    case 7:
                        pieceName = "7";
                        break;
                    case 8:
                        pieceName = "8";
                        break;
                    case 9:
                        pieceName = "9";
                        break;
                    case 10:
                        pieceName = "10";
                        break;
                    case 11:
                        pieceName = "11";
                        break;
                    case 12:
                        pieceName = "12";
                        break;
                    default:
                        pieceName = "0";
                        break;
                }
                System.out.print(pieceName + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Make a move on the board.
     * Example: "e2e4" or "e7e5"
     * 
     * @param string
     */
    public void makeMove(String string) {
        int from = 8 * (8 - (string.charAt(1) - '0')) + (string.charAt(0) - 'a');
        int to = 8 * (8 - (string.charAt(3) - '0')) + (string.charAt(2) - 'a');
        board[to] = board[from];
        board[from] = 0;
    }

    /**
     * This method will be used to build the gamePGN string.
     */
    public void buildPGN(int from, int destination) {
        String move = "";
        int pieceType = board[from];

        switch (pieceType) {
            case 1:
                break;
            case 2:
                move += "N";
                break;
            case 3:
                move += "B";
                break;
            case 4:
                move += "R";
                break;
            case 5:
                move += "Q";
                break;
            case 6:
                move += "K";
                break;
            case 7:
                break;
            case 8:
                move += "N";
                break;
            case 9:
                move += "B";
                break;
            case 10:
                move += "R";
                break;
            case 11:
                move += "Q";
                break;
            case 12:
                move += "K";
                break;
            default:
                break;
        }

        move += Piece.getSquareName(from).charAt(0);
        int fromRow = (from / 8) + 1;
        move += fromRow;


        move += Piece.getSquareName(destination);

        if (whiteTurn) {
            if (isInCheck(whiteTurn)) {
                move += "+";
            }

            gamePGN += moveNumber + ". " + move + " ";
            moveNumber++;
        } else {
            if (isInCheck(!whiteTurn)) {
                move += "+";
            }

            gamePGN += moveNumber + ". " + move + " ";
            moveNumber++;

        }

        System.out.println(gamePGN);
    }

}