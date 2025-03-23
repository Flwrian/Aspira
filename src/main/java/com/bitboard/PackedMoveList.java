package com.bitboard;
import java.util.Arrays;

public class PackedMoveList {
    private final long[] moves;
    private int size = 0;

    public PackedMoveList(int maxSize) {
        this.moves = new long[maxSize];
    }

    public void clear() {
        size = 0;
    }

    public void add(long packedMove) {
        moves[size++] = packedMove;
    }

    public long get(int index) {
        return moves[index];
    }

    public int size() {
        return size;
    }

    public long[] raw() {
        return moves;
    }

    public void remove(int index) {
        if (index < 0 || index >= size) return;
        moves[index] = moves[size - 1]; // Swap with last
        size--; // Shrink list
    }

    public void sortByScore() {
        Arrays.sort(moves, 0, size); // if score is part of the encoding
    }
}
