package com.array;

import com.array.algorithms.AlphaBetaPruningAlgorithm;
import com.array.algorithms.RandomAlgorithm;

public class Main {
    
    public static void main(String[] args) {
        Game game = new Game();
        
        Engine engine = new Engine(game.getBoard());
        engine.showKnps();
        engine.setAlgorithm(new AlphaBetaPruningAlgorithm(4));

        Engine engine2 = new Engine(game.getBoard());
        engine2.setAlgorithm(new AlphaBetaPruningAlgorithm(2));

        game.setWhite(engine);
        game.setBlack(engine2);

        game.play();
    }
}
