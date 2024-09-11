package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;

public interface ChessAlgorithm {

    public Move search(BitBoard board);

    public int evaluate(BitBoard board);

    public String getName();

    public void setDepth(int depth);
}
