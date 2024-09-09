package com;

import com.algorithms.AlphaBetaPruningAlgorithm;
import com.algorithms.RandomAlgorithm;

public class Main {
    
    public static void main(String[] args) {
        Game game = new Game();
        
        Engine engine = new Engine(game.getBoard());
        // engine.showKnps();
        int valid = engine.getNbValidMoves(6);
        System.out.println(valid);
        valid = engine.getNbLegalMoves(6);
        System.out.println(valid);
        
        // engine.setAlgorithm(new AlphaBetaPruningAlgorithm(6));

        // Engine engine2 = new Engine(game.getBoard());
        // engine2.setAlgorithm(new AlphaBetaPruningAlgorithm(2));

        // game.setWhite(engine);
        // game.setBlack(engine2);

        // game.play();
    }
}
