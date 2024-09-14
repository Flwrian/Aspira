package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;

public interface ChessAlgorithm {

    public Move search(BitBoard board, int wtime, int btime, int winc, int binc, int movestogo, int depth);

    public int evaluate(BitBoard board);

    public String getName();

    public void setDepth(int depth);

    public void setRazorDepth(int depth);

    public void setNPM(int npm);

    public int getRazorDepth();

    public int getNPM();
}
