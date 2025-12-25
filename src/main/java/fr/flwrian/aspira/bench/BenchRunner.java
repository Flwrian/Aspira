package fr.flwrian.aspira.bench;

import fr.flwrian.aspira.board.Board;
import fr.flwrian.aspira.engine.Engine;

public class BenchRunner {

    public static void run(Engine engine, Board board) {

        long nodeCount = 0;
        long nps = 0;

        for(String fen : BenchPositions.POSITIONS) {
            board.loadFromFen(fen);
            engine.getSearchAlgorithm().search(board, 100000, 1000, 100, 100, 67_0000, 6);
            
            nodeCount += engine.getSearchAlgorithm().getLastNodeCount();
            nps += engine.getSearchAlgorithm().getLastNPS();
        }

        System.out.println(nodeCount + " nodes " + nps/BenchPositions.POSITIONS.length + " nps");
    }
    
}
