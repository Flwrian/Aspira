package com;

/**
 * King
 */
public class King {


    public static void move(int position, int newPosition, Board board, boolean b) {
        int type = board.board[position];
        board.board[position] = 0;
        board.board[newPosition] = type;
    }

    public static boolean isValidMove(int piecePosition, int position, Board board) {
        boolean color = board.board[piecePosition] < 7;

        if (position < 0 || position > 63) {
            return false;
        }

        if (piecePosition == position) {
            return false;
        }

        int rowDiff = Math.abs(position / 8 - piecePosition / 8);
        int colDiff = Math.abs(position % 8 - piecePosition % 8);

        if (rowDiff > 1 || colDiff > 1) {
            // King can only move one square at a time
            return false;
        }

        if (board.board[position] != 0 && board.board[position] < 7 == color) {
            return false;
        }
        
        return true;
    }


    
}