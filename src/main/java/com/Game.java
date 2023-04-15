package com;

import com.algorithms.ChessAlgorithm;
import com.algorithms.AlphaBetaPruning;

public class Game {

    private PlayableEntity player1;
    private PlayableEntity player2;

    private Board board;

    static boolean isEnded = false;

    public void setPlayer1(PlayableEntity player1) {
        this.player1 = player1;
    }

    public void setPlayer2(PlayableEntity player2) {
        this.player2 = player2;
    }

    public void play() {
        if(player1 == null || player2 == null){
            throw new IllegalStateException("No player selected");
        }

        while(!isEnded){
            isEnded = !board.canPlay;

            if(board.is3FoldRepetition()){
                System.out.println("3-fold repetition");
                isEnded = true;
                break;
            }

            if(board.whiteTurn){
                player1.play();
            }
            else{
                player2.play();
            }
            board.printBoard();
        }

        System.out.println(Board.gamePGN);
    }
}
