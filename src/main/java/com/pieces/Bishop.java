package com.pieces;

import com.Board;

public class Bishop {

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

        if (position == piecePosition) {
            return false;
        }

        if (board.board[position] != 0 && color == board.board[position] < 7) {
            return false;
        }

        int currentRow = piecePosition / 8;
        int currentCol = piecePosition % 8;
        int newRow = position / 8;
        int newCol = position % 8;

        if (currentRow == newRow || currentCol == newCol) {
            // Did not move diagonally
            return false;
        }

        if (Math.abs(newRow - currentRow) != Math.abs(newCol - currentCol)) {
            return false;
        }

        int rowOffset, colOffset;

        if (currentRow < newRow) {
            rowOffset = 1;
        } else {
            rowOffset = -1;
        }

        if (currentCol < newCol) {
            colOffset = 1;
        } else {
            colOffset = -1;
        }

        int y = currentCol + colOffset;
        for (int x = currentRow + rowOffset; x != newRow; x += rowOffset) {

            if (board.board[x * 8 + y] != 0) {
                return false;
            }

            y += colOffset;
        }

        return true;
    }
}