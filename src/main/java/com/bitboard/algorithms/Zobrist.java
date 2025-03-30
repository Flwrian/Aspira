package com.bitboard.algorithms;

import java.util.Random;

public class Zobrist {
    public static final long[][] PIECE_KEYS = new long[12][64]; // 12 types * 64 cases
    public static final long[] CASTLING_KEYS = new long[16];    // 4 bits = 16 combinaisons
    public static final long[] EN_PASSANT_KEYS = new long[64];   // squares
    public static final long SIDE_TO_MOVE_KEY;

    static {
        Random rand = new Random(2025); // seed fixe = déterministe pour debug
        for (int piece = 0; piece < 12; piece++) {
            for (int square = 0; square < 64; square++) {
                PIECE_KEYS[piece][square] = rand.nextLong();
            }
        }

        for (int i = 0; i < CASTLING_KEYS.length; i++) {
            CASTLING_KEYS[i] = rand.nextLong();
        }

        for (int i = 0; i < 64; i++) {
            EN_PASSANT_KEYS[i] = rand.nextLong();
        }

        SIDE_TO_MOVE_KEY = rand.nextLong();
    }
}
