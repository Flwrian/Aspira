package com.bitboard.algorithms;
public class TranspositionTableEntry {
    public final long zobristKey;
    public final int depth;
    public final int value;
    public final int flag;
    public final long bestMove;

    public static final int EXACT = 0;
    public static final int LOWERBOUND = 1;
    public static final int UPPERBOUND = 2;

    public TranspositionTableEntry(long zobristKey, int depth, int value, int flag, long bestMove) {
        this.zobristKey = zobristKey;
        this.depth = depth;
        this.value = value;
        this.flag = flag;
        this.bestMove = bestMove;
    }
}
