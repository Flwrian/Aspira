package com.bitboard.algorithms;

import com.bitboard.BitBoard;
import com.bitboard.Move;
import com.bitboard.MoveList;

public class RandomAlgorithm implements ChessAlgorithm {

    public Move search(BitBoard board) {
        MoveList moveList = board.getLegalMoves();
        board.printChessBoard();
        if (moveList.size() == 0) {
            return null;
        }
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

    @Override
    public void setRazorDepth(int depth) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRazorDepth'");
    }

    @Override
    public void setNPM(int npm) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setNPM'");
    }

    @Override
    public int getRazorDepth() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRazorDepth'");
    }

    @Override
    public int getNPM() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNPM'");
    }

    @Override
    public Move search(BitBoard board, int wtime, int btime, int winc, int binc, int movestogo, int depth) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'search'");
    }

    
    
}
