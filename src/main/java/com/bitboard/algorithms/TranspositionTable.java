package com.bitboard.algorithms;

import com.bitboard.Move;

import java.util.HashMap;

public class TranspositionTable {
    private final HashMap<Long, TranspositionTableEntry> table;

    public TranspositionTable() {
        table = new HashMap<>();
    }

    public void store(long zobristHash, int depth, int score, byte nodeType, Move bestMove) {
        table.put(zobristHash, new TranspositionTableEntry(zobristHash, depth, score, nodeType, bestMove));
    }

    public TranspositionTableEntry probe(long zobristHash) {
        return table.get(zobristHash);
    }

    public boolean contains(long zobristHash) {
        return table.containsKey(zobristHash);
    }
}
