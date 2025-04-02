package com.array.pieces;

import com.array.Board;

public class Queen {

    public static void move(int position, int newPosition, Board board, boolean b) {
        int type = board.board[position];
        board.board[position] = 0;
        board.board[newPosition] = type;
    }

    public static boolean isValidMove(int piecePosition, int position, Board board) {
        return Rook.isValidMove(piecePosition, position, board) || Bishop.isValidMove(piecePosition, position, board);
    }
}