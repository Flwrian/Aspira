package com.bitboard.algorithms;

import java.util.HashMap;

public class TranspositionTable {
    private final HashMap<Long, TranspositionTableEntry> table = new HashMap<>();

    public void put(long key, TranspositionTableEntry entry) {
        table.put(key, entry);
    }

    public TranspositionTableEntry get(long key) {
        return table.get(key);
    }

    public boolean contains(long key) {
        return table.containsKey(key);
    }

    public void clear() {
        table.clear();
    }
}
