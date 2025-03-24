package com.array.pieces;

import com.array.Board;

public class Pawn extends Piece {

    // TODO: add promotion
    public static void move(int position, int destination, Board board, boolean color) {
        // If on the last row, promote to queen
        if (color && destination > 55) {
            board.board[position] = 0;
            board.board[destination] = 5;
            return;
        } else if (!color && destination < 8) {
            board.board[position] = 0;
            board.board[destination] = 11;
            return;
        }
        int type = board.board[position];
        board.board[position] = 0;
        board.board[destination] = type;
    }

    public static boolean isValidMove(int piecePosition, int destination, Board board) {
        boolean color = board.board[piecePosition] < 7;

        // Check that the move is inside the board
        if (destination < 0 || destination > 63) {
            return false;
        }

        // Check that the move is not on the same square
        if (piecePosition == destination) {
            return false;
        }

        // Check that the move is not on a square occupied by a piece of the same color
        if (board.board[destination] != 0 && (board.board[destination] < 7 == board.board[piecePosition] < 7)) {
            return false;
        }

        // For white pawns
        if (color) {

            // Check if the pawn is trying to move en passant
            // if (board.getPiece(position) == null && position - piecePosition == 7 &&
            // board.getPiece(piecePosition).hasMoved == 1) {
            // if (board.getPiece(position - 8) != null && board.getPiece(position -
            // 8).getType() == 1) {
            // Pawn pawn = (Pawn) board.getPiece(position - 8);
            // if (pawn.startingPosition == position - 16) {
            // return true;
            // }
            // }
            // }

            // Check if the pawn is moving one square in front of it and the square is empty
            if (destination == piecePosition + 8 && board.board[destination] == 0) {
                return true;
            }

            // Check if the pawn is moving two squares in front of it and the squares are empty
            if (piecePosition < 8 && destination == piecePosition + 16 && board.board[piecePosition + 8] == 0
                    && board.board[destination] == 0) {
                return true;
            }

            
            int col = piecePosition % 8;

            // Check if the pawn is trying to capture a piece diagonally
            if (board.board[destination] != 0 && (destination - piecePosition == 9 || destination - piecePosition == 7)) {
                // Check if the pawn is capturing to the right column
                if ((destination % 8 == col + 1) && (col < 7)) {
                    return true;
                }
                // Check if the pawn is capturing to the left column
                if ((destination % 8 == col - 1) && (col > 0)) {
                    return true;
                }
            }

            return false;

            // For black pawns
        } else {

            // Check if the pawn is trying to move en passant
            // if (board.getPiece(position) == null && piecePosition - position == 7 &&
            // board.getPiece(piecePosition).hasMoved == 1) {
            // if (board.getPiece(position + 8) != null && board.getPiece(position +
            // 8).getType() == 1) {
            // Pawn pawn = (Pawn) board.getPiece(position + 8);
            // if (pawn.startingPosition == position + 16) {
            // return true;
            // }
            // }
            // }

            // Check if the pawn is moving one square in front of it and the square is empty
            if (destination == piecePosition - 8 && board.board[destination] == 0) {
                return true;
            }

            // Check if the pawn is moving two squares in front of it and the squares are empty
            if (piecePosition > 55 && (destination == piecePosition - 16) && board.board[piecePosition - 8] == 0
                    && board.board[destination] == 0) {
                return true;
            }

            int col = piecePosition % 8;

            // Check if the pawn is trying to capture a piece diagonally
            if (board.board[destination] != 0 && (piecePosition - destination == 9 || piecePosition - destination == 7)) {
                // Check if the pawn is capturing to the right column
                if ((destination % 8 == col + 1) && (col < 7)) {
                    return true;
                }
                // Check if the pawn is capturing to the left column
                if ((destination % 8 == col - 1) && (col > 0)) {
                    return true;
                }
            }

            

            return false;

        }
    }
}
