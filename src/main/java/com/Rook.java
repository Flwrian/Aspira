package com;


public class Rook extends Piece {

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
        // Check if the piece is trying to move to the same position
        if (position == piecePosition) {
            return false;
        }

        // Check if the piece is trying to move to a position with a piece of the same color
        if (board.board[position] != 0 && color == (board.board[position] < 7)) {
            return false;
        }

        // Check if the move is vertical
        if (position % 8 == piecePosition % 8) {
            int difference = position - piecePosition;
            int step;
            if (difference > 0) {
                step = 8;
            } else {
                step = -8;
            }

            for (int i = piecePosition + step; i != position; i += step) {
                if (board.board[i] != 0) {
                    return false;
                }
            }
            return true;
        }

        // Check if the move is horizontal
        else if (position / 8 == piecePosition / 8) {
            int difference = position - piecePosition;
            int step = difference / Math.abs(difference);

            for (int i = piecePosition + step; i != position; i += step) {
                if (board.board[i] != 0) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    
}