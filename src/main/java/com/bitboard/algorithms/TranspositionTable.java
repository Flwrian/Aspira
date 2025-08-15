package com.bitboard.algorithms;

/**
 * Transposition Table – 4-way set-associative, depth-preferred replacement.
 * Memory is respected (primitive arrays; no per-entry objects).
 *
 * NOTE: Values must already be encoded/decoded for mate distance (toTTScore/fromTTScore) in the caller.
 */
public class TranspositionTable {

    // ---- Public Entry (API compatible with your current code)
    public static class Entry {
        public static final int EXACT      = 0;
        public static final int LOWERBOUND = 1;
        public static final int UPPERBOUND = 2;

        public final long key;      // full Zobrist (for safety)
        public final long bestMove; // packed move
        public final int  value;    // already encoded for TT by caller
        public final int  depth;    // search depth stored
        public final int  flag;     // EXACT / LOWERBOUND / UPPERBOUND

        public Entry(long key, long bestMove, int value, int depth, int flag) {
            this.key = key;
            this.bestMove = bestMove;
            this.value = value;
            this.depth = depth;
            this.flag = flag;
        }
    }

    // ---- Layout: 4 entries per bucket
    private static final int WAYS = 4;
    private final int buckets;      // number of buckets (power of two)
    private final int bucketMask;   // buckets - 1

    // Parallel primitive arrays (size = buckets * WAYS)
    private final long[] keys;
    private final long[] moves;
    private final int[]  values;
    private final short[] depths;   // depth <= ~32767
    private final byte[]  flags;    // 0..2
    private final byte[]  used;     // 0=empty, 1=used (tiny occupancy marker)

    /**
     * @param sizeMB target memory in MB (best-effort). Actual usage will be <= sizeMB.
     */
    public TranspositionTable(int sizeMB) {
        if (sizeMB < 1) throw new IllegalArgumentException("Size must be at least 1 MB");

        // Rough entry footprint (primitive arrays only):
        // key(8) + move(8) + value(4) + depth(2) + flag(1) + used(1) ~= 24 bytes/entry
        final int ENTRY_BYTES = 24;

        long bytes = (long) sizeMB * 1024L * 1024L;
        long maxEntries = Math.max(256, bytes / ENTRY_BYTES); // avoid tiny tables
        long maxBuckets = Math.max(64, maxEntries / WAYS);

        // buckets = highest power of two <= maxBuckets
        int b = highestPowerOfTwoLE(maxBuckets);
        this.buckets = Math.max(64, b);
        this.bucketMask = this.buckets - 1;

        int capacity = this.buckets * WAYS;

        this.keys   = new long[capacity];
        this.moves  = new long[capacity];
        this.values = new int [capacity];
        this.depths = new short[capacity];
        this.flags  = new byte [capacity];
        this.used   = new byte [capacity];
    }

    // --- Public API ---

    /** Store (depth-preferred; always keeps deeper line). */
    public void put(long zobristKey, long bestMove, int value, int depth, int flag) {
        final int base = baseIndex(zobristKey);

        // 1) If key already present in bucket → replace if depth is >= existing (keep newer/deeper)
        int emptySlot = -1;
        int victim = base; // default victim = first way; will switch to the shallowest
        int victimDepth = Integer.MAX_VALUE;

        for (int w = 0; w < WAYS; w++) {
            int idx = base + w;

            if (used[idx] != 0 && keys[idx] == zobristKey) {
                // same key -> prefer deeper or at least not shallower
                if ((depth & 0xFFFF) >= (depths[idx] & 0xFFFF)) {
                    keys[idx]   = zobristKey;
                    moves[idx]  = bestMove;
                    values[idx] = value;
                    depths[idx] = (short) depth;
                    flags[idx]  = (byte) flag;
                    used[idx]   = 1;
                }
                return;
            }

            if (used[idx] == 0 && emptySlot < 0) emptySlot = idx;

            int d = used[idx] == 0 ? -1 : (depths[idx] & 0xFFFF);
            if (d >= 0 && d < victimDepth) {
                victimDepth = d;
                victim = idx;
            }
        }

        // 2) Free slot in bucket?
        int target = (emptySlot >= 0) ? emptySlot : victim;

        keys[target]   = zobristKey;
        moves[target]  = bestMove;
        values[target] = value;
        depths[target] = (short) depth;
        flags[target]  = (byte) flag;
        used[target]   = 1;
    }

    /** Probe exact bucket (4 ways). Returns null if miss. */
    public Entry get(long zobristKey) {
        final int base = baseIndex(zobristKey);
        for (int w = 0; w < WAYS; w++) {
            int idx = base + w;
            if (used[idx] != 0 && keys[idx] == zobristKey) {
                return new Entry(keys[idx], moves[idx], values[idx], depths[idx] & 0xFFFF, flags[idx] & 0xFF);
            }
        }
        return null;
    }

    // --- Helpers ---

    private int baseIndex(long key) {
        // bucket * WAYS
        int bucket = (int) key & bucketMask;
        return (bucket << 2); // *4 (WAYS)
    }

    private static int highestPowerOfTwoLE(long x) {
        if (x <= 1) return 1;
        int p = 1;
        while ((long) (p << 1) <= x && (p << 1) > 0) p <<= 1;
        return p;
    }

    // Optional: for quick stats
    public int capacity() { return buckets * WAYS; }
    public int buckets()  { return buckets; }
}
