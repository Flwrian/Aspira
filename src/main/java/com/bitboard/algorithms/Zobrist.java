package com.bitboard.algorithms;

import com.bitboard.BitBoard;

import java.util.Random;

public class Zobrist {
    private static long[][][] zobristTable = new long[12][8][8]; // 12 pièces, 8x8 plateau

    static {
        Random rand = new Random();
        for (int piece = 0; piece < 12; piece++) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    zobristTable[piece][row][col] = rand.nextLong();
                }
            }
        }
    }

    public static long computeHash(BitBoard board) {
        long hash = 0L;
        for (int square = 0; square < 64; square++) {
            int piece = board.getPiece(square);
            if (piece != BitBoard.EMPTY) {
                int row = square / 8;
                int col = square % 8;
                hash ^= zobristTable[piece - 1][row][col];
            }
        }
        return hash;
    }
}
