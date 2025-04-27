package com.bitboard.algorithms;

public class TranspositionTable {
    private final Entry[] table;
    private final int sizeMask;

    public TranspositionTable(int maxSizeMB) {
        // Not 100% accurate but will change this later
        long bytesPerEntry = 32;
        int maxEntries = (int) ((maxSizeMB * 1024L * 1024L) / bytesPerEntry);

        // Make size a power of two for efficient masking
        int actualSize = 1;
        while (actualSize < maxEntries) {
            actualSize *= 2;
        }

        this.table = new Entry[actualSize];
        this.sizeMask = actualSize - 1;
    }

    public static class Entry {
        public static final int EXACT = 0;
        public static final int LOWERBOUND = 1;
        public static final int UPPERBOUND = 2;

        public final long key;       // Zobrist key
        public final long bestMove;
        public final int value;
        public final int depth;
        public final int flag;

        public Entry(long key, long bestMove, int value, int depth, int flag) {
            this.key = key;
            this.bestMove = bestMove;
            this.value = value;
            this.depth = depth;
            this.flag = flag;
        }
    }

    public void put(long zobristKey, long bestMove, int value, int depth, int flag) {
        int index = indexFor(zobristKey);
        Entry existing = table[index];

        if (existing == null || existing.depth <= depth) {
            table[index] = new Entry(zobristKey, bestMove, value, depth, flag);
        }
    }

    public Entry get(long zobristKey) {
        int index = indexFor(zobristKey);
        Entry entry = table[index];
        return (entry != null && entry.key == zobristKey) ? entry : null;
    }

    private int indexFor(long key) {
         // Efficient modulo for power-of-two size
        return (int) (key & sizeMask);
    }

    public int size() {
        int count = 0;
        for (Entry entry : table) {
            if (entry != null) count++;
        }
        return count;
    }
}
