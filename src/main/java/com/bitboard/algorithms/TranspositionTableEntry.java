package com.bitboard.algorithms;

import com.bitboard.Move;

public class TranspositionTableEntry {
    long zobristHash;
    int depth;
    int score;
    byte nodeType; // 0 = exact, 1 = lowerbound, 2 = upperbound
    Move bestMove;

    public TranspositionTableEntry(long zobristHash, int depth, int score, byte nodeType, Move bestMove) {
        this.zobristHash = zobristHash;
        this.depth = depth;
        this.score = score;
        this.nodeType = nodeType;
        this.bestMove = bestMove;
    }
}
