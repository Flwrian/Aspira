package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.MoveList;

public class RandomAlgorithm implements ChessAlgorithm {

    @Override
    public Move search(BitBoard board) {
        MoveList moveList = board.getLegalMoves();
        int randomIndex = (int) (Math.random() * moveList.size());
        Move randomMove = moveList.get(randomIndex);
        return randomMove;
    }

    @Override
    public int evaluate(BitBoard board) {
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
    }

    @Override
    public String getName() {
        return "Random";
    }

    @Override
    public void setDepth(int depth) {
        return;
    }

    
    
}
