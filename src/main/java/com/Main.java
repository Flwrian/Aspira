package com;

import com.algorithms.AlphaBetaPruningAlgorithm;
import com.algorithms.RandomAlgorithm;

public class Main {
    
    public static void main(String[] args) {
        Game game = new Game();
        
        Engine engine = new Engine(game.getBoard());
        engine.setAlgorithm(new AlphaBetaPruningAlgorithm(4));

        Engine engine2 = new Engine(game.getBoard());
        engine2.setAlgorithm(new RandomAlgorithm());

        game.setWhite(engine);
        game.setBlack(engine2);

        game.play();
    }
}
