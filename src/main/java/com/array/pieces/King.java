package com.array.pieces;

import com.array.Board;

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

        // castling rights for white king are stored between board[64+0] and board[64+3] and for black king between board[64+4] and board[64+7]

        // Check if not already moved
        if (color) {
            if (piecePosition == 60 && position == 62 && board.board[64] == 0) {
                if (board.board[61] == 0 && board.board[62] == 0) {
                    return true;
                }
            } else if (piecePosition == 60 && position == 58 && board.board[64] == 0) {
                if (board.board[59] == 0 && board.board[58] == 0 && board.board[57] == 0) {
                    return true;
                }
            }
        } else {
            if (piecePosition == 4 && position == 6 && board.board[64 + 4] == 0) {
                if (board.board[5] == 0 && board.board[6] == 0) {
                    return true;
                }
            } else if (piecePosition == 4 && position == 2 && board.board[64 + 4] == 0) {
                if (board.board[3] == 0 && board.board[2] == 0 && board.board[1] == 0) {
                    return true;
                }
            }
        }

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