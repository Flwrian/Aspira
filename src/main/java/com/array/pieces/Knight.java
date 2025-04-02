package com.array.pieces;

import com.array.Board;

public class Knight {

    public static void move(int position, int newPosition, Board board, boolean color) {
        int type = board.board[position];
        board.board[position] = 0;
        board.board[newPosition] = type;
    }

    public static boolean isValidMove(int piecePosition, int position, Board board) {
        boolean color = board.board[piecePosition] < 7;

        int newRow = position / 8;
        int newCol = position % 8;
        int currentRow = piecePosition / 8;
        int currentCol = piecePosition % 8;

        if (position < 0 || position > 63) {
            return false;
        }
        // Check if the knight is trying to move to the same position
        if (position == piecePosition) {
            return false;
        }
        // Check if the knight is trying to move to a position with a piece of the same
        // color
        if (board.board[position] != 0 && color == board.board[position] < 7) {
            return false;
        }

        // The knight move in an L shape.

        if (Math.abs(newRow - currentRow) == 2 && Math.abs(newCol - currentCol) == 1) {
            return true;
        }

        if (Math.abs(newRow - currentRow) == 1 && Math.abs(newCol - currentCol) == 2) {
            return true;
        }

        return false;
    }
}