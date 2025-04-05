package com.bitboard.algorithms;
import java.util.*;

public class TranspositionTable {
    private final Map<Long, Entry> table;
    private final int maxEntries;

    public TranspositionTable(int maxSizeMB) {
        long bytesPerEntry = 32;
        this.maxEntries = (int) ((maxSizeMB * 1024L * 1024L) / bytesPerEntry);
        this.table = new LinkedHashMap<>(maxEntries, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Entry> eldest) {
                return size() > maxEntries;
            }
        };
    }

    public static class Entry {
        public static final int EXACT = 0;
        public static final int LOWERBOUND = 1;
        public static final int UPPERBOUND = 2;

        public final long bestMove;
        public final int value;
        public final int depth;
        public final int flag;

        public Entry(long bestMove, int value, int depth, int flag) {
            this.bestMove = bestMove;
            this.value = value;
            this.depth = depth;
            this.flag = flag;
        }
    }

    public void put(long zobristKey, Entry entry) {
        table.put(zobristKey, entry);
    }

    public Entry get(long zobristKey) {
        return table.get(zobristKey);
    }

    public int size() {
        return table.size();
    }
}
