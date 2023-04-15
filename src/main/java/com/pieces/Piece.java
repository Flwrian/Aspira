package com.pieces;

import com.Board;

/**
 * Chess Piece Class
 * Every piece has a color, a type, and a position
 * 
 * the type is an integer that represents the type of piece
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
 * 
 * @author Florian Montourcy
 * @version 1.0
 */
public abstract class Piece {

    /**
     * Move the piece according to its type
     */
    public static void move(int position, int newPosition, Board board) {
        int type = board.board[position];

        switch (type) {
            case 1:
                Pawn.move(position, newPosition, board, type < 7);
                break;
            case 2:
                Knight.move(position, newPosition, board, type < 7);
                break;
            case 3:
                Bishop.move(position, newPosition, board, type < 7);
                break;
            case 4:
                Rook.move(position, newPosition, board, type < 7);
                break;
            case 5:
                Queen.move(position, newPosition, board, type < 7);
                break;
            case 6:
                King.move(position, newPosition, board, type < 7);
                break;
            case 7:
                Pawn.move(position, newPosition, board, type < 7);
                break;
            case 8:
                Knight.move(position, newPosition, board, type < 7);
                break;
            case 9:
                Bishop.move(position, newPosition, board, type < 7);
                break;
            case 10:
                Rook.move(position, newPosition, board, type < 7);
                break;
            case 11:
                Queen.move(position, newPosition, board, type < 7);
                break;
            case 12:
                King.move(position, newPosition, board, type < 7);
                break;
            default:
                break;
        }
    }

    public static int countValidMoves(int piecePosition, Board board) {
        // Count the number of valid moves for the piece

        int pieceType = board.board[piecePosition];
        int count = 0;
        for (int i = 0; i < 64; i++) {
            if (Piece.isValidMove(pieceType, piecePosition, i, board)) {
                count++;
            }
        }
        return count;
    }

    /**
     * <p>Check if the move is valid for the piece</p>
     * <p>Valid move = inside the board, not on the same square, not on a square occupied by a piece of the same color, and valid for the piece</p>
     */
    public static boolean isValidMove(int pieceType, int piecePosition, int position, Board board) {
        // Check that the move is valid for the piece
        
        // Check that the move is inside the board
        // if (position < 0 || position > 63) {
        //     return false;
        // }

        // // Check that the move is not on the same square
        // if (piecePosition == position) {
        //     return false;
        // }

        // // Check that the move is not on a square occupied by a piece of the same color
        // if (board.board[position] != 0 && board.board[position] < 7 == board.board[piecePosition] < 7) {
        //     return false;
        // }

        // Check that the move is valid for the piece
        switch (pieceType) {
            case 1:
                return Pawn.isValidMove(piecePosition, position, board);
            case 2:
                return Knight.isValidMove(piecePosition, position, board);
            case 3:
                return Bishop.isValidMove(piecePosition, position, board);
            case 4:
                return Rook.isValidMove(piecePosition, position, board);
            case 5:
                return Queen.isValidMove(piecePosition, position, board);
            case 6:
                return King.isValidMove(piecePosition, position, board);
            case 7:
                return Pawn.isValidMove(piecePosition, position, board);
            case 8:
                return Knight.isValidMove(piecePosition, position, board);
            case 9:
                return Bishop.isValidMove(piecePosition, position, board);
            case 10:
                return Rook.isValidMove(piecePosition, position, board);
            case 11:
                return Queen.isValidMove(piecePosition, position, board);
            case 12:
                return King.isValidMove(piecePosition, position, board);
            default:
                return false;
        }
        
    }

    /**
     * <p>Check if the move is legal for the piece</p>
     * <p>Legal move = valid move and doesn't put the king in check</p>
     */
    public static boolean isLegalMove(int pieceType, int piecePosition, int destination, Board board) {
        // Check that the move is legal for the piece by checking if the move is valid
        // and if the move puts the king in check
        if (!Piece.isValidMove(pieceType, piecePosition, destination, board)) {
            return false;
        }

        // * Double the time of node exploration
        // ? Is it really necessary to check if the move puts the king in check for a chess engine?
        // ? After all, the engine is supposed to be able to find the best move, if the move lose the king it's not the best move so the engine won't choose it.
        // ? Probably not necessary, maybe we will remove this in the future
        board.pushMove(piecePosition, destination);
        boolean inCheck = board.isInCheck(pieceType < 7);
        board.popMove();

        return !inCheck;
    }

    public int countLegalMoves(int position, Board board) {
        // Count the number of legal moves for the piece
        int count = 0;
        for (int i = 0; i < 64; i++) {
            if (Piece.isLegalMove(board.board[position], position, i, board)) {
                count++;
            }
        }
        return count;
    }

    public static String getSquareName(int from) {
        // Get the name of the square (inverse of the board array)
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        return letters[from % 8] + (from / 8 + 1);
    }

}