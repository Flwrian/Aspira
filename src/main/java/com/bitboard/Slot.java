package com.bitboard;

public class Slot {
    public long key;        // zobrist key
    public long bestMove;   // Le move encodé
    public int value;       // Évaluation
    public byte depth;      // Profondeur de recherche
    public byte flag;       // EXACT / LOWER / UPPER

    public Slot() {
        this.key = 0L;  // Key == 0 signifie slot vide
    }
}
