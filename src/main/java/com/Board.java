package com;

import java.util.Arrays;

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

    public int[] board = new int[64];

    private int[] lastMove = new int[4];

    public boolean whiteTurn;

    public King whiteKing;
    public King blackKing;

    public Board() {
        this.setupBoard();
    }

    private void setupBoard() {
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

        // Set up the kings, so we can check for check
        whiteKing = (King) getPiece(60);
        blackKing = (King) getPiece(4);
    }

    public Piece[] getPieces() {
        Piece[] pieces = new Piece[0];
        int index = 0;
        for (int i = 0; i < 64; i++) {
            if (board[i] != 0) {
                pieces = Arrays.copyOf(pieces, pieces.length + 1);
                pieces[index] = Piece.getPiece(board[i], i, this);
                index++;
            }
        }
        return pieces;
    }

    public Piece getPiece(int position) {
        if (board[position] != 0) {
            return Piece.getPiece(board[position], position, this);
        }
        return null;
    }

    public void setPiece(int position, int type) {
        board[position] = type;
    }

    public void pushMove(int from, int to) {
        lastMove[0] = from;
        lastMove[1] = to;
        lastMove[2] = board[from];
        lastMove[3] = board[to];

        board[to] = board[from];
        board[from] = 0;

        whiteTurn = !whiteTurn;
    }

    public void undoMoveLastMove() {
        board[lastMove[0]] = lastMove[2];
        board[lastMove[1]] = lastMove[3];

        whiteTurn = !whiteTurn;
    }

    public boolean isInCheck(boolean white) {
        if (white) {
            return whiteKing.isInCheck();
        } else {
            return blackKing.isInCheck();
        }
    }

    public void flip(){
        whiteTurn = !whiteTurn;
    }

    public void loadFEN(String fen) {
        board = new int[64];
        String[] parts = fen.split(" ");
        String[] rows = parts[0].split("/");
        int index = 0;
        for (int i = 0; i < 8; i++) {
            String row = rows[i];
            for (int j = 0; j < row.length(); j++) {
                char c = row.charAt(j);
                if (Character.isDigit(c)) {
                    index += Character.getNumericValue(c);
                } else {
                    switch (c) {
                        case 'p':
                            board[index] = 7;
                            break;
                        case 'P':
                            board[index] = 1;
                            break;
                        case 'n':
                            board[index] = 8;
                            break;
                        case 'N':
                            board[index] = 2;
                            break;
                        case 'b':
                            board[index] = 9;
                            break;
                        case 'B':
                            board[index] = 3;
                            break;
                        case 'r':
                            board[index] = 10;
                            break;
                        case 'R':
                            board[index] = 4;
                            break;
                        case 'q':
                            board[index] = 11;
                            break;
                        case 'Q':
                            board[index] = 5;
                            break;
                        case 'k':
                            board[index] = 12;
                            break;
                        case 'K':
                            board[index] = 6;
                            break;
                    }
                    index++;
                }
            }
        }

        whiteTurn = parts[1].equals("w");

        // Mirror the board
        int[] newBoard = new int[64];
        for (int i = 0; i < 64; i++) {
            newBoard[i] = board[63 - i];
        }
        board = newBoard;
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
                        pieceName = " ";
                        break;
                }
                System.out.print(pieceName + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}