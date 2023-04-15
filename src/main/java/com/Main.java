package com;

import com.algorithms.AlphaBetaPruningAlgorithm;

public class Main {
    
    public static void main(String[] args) {
        Game game = new Game();
        
        Engine engine = new Engine(game.board);
        engine.setAlgorithm(new AlphaBetaPruningAlgorithm(4));

        game.setPlayer1(engine);
        game.setPlayer2(engine);

        game.play();
    }
}
