package com.bitboard;

public class NewTT {
    private final Slot[] table;
    private final int mask; // Pour les index via modulo puissance de 2

    public NewTT(int mbSize) {
        int bytesPerEntry = 24; // estimation réelle : ~24B (on ajuste à la louche)
        long totalBytes = mbSize * 1024L * 1024L;
        int entries = (int) (totalBytes / bytesPerEntry);

        // arrondi à la puissance de 2 inférieure
        int pow2 = Integer.highestOneBit(entries);
        this.table = new Slot[pow2];
        this.mask = pow2 - 1;

        for (int i = 0; i < pow2; i++) {
            table[i] = new Slot();
        }
    }

    public void put(long zobristKey, long bestMove, int value, int depth, int flag) {
        int index = (int) (zobristKey & mask);
        Slot slot = table[index];

        // Ne remplace pas une entrée plus profonde
        if (slot.key != 0 && slot.depth > depth) {
            return;
        }

        slot.key = zobristKey;
        slot.bestMove = bestMove;
        slot.value = value;
        slot.depth = (byte) depth;
        slot.flag = (byte) flag;
    }
    
    public Slot get(long zobristKey) {
        int index = (int) (zobristKey & mask);
        Slot slot = table[index];
        return (slot.key == zobristKey) ? slot : null;
    }

}