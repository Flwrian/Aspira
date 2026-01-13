package fr.flwrian.aspira.bench;

import fr.flwrian.aspira.board.Board;
import fr.flwrian.aspira.engine.Engine;
import fr.flwrian.aspira.perft.Perft;

public class BenchRunner {

    public static void run(Engine engine, Board board) {

        long nodeCount = 0;
        long nps = 0;

        // perft to warmup the JIT
        Perft.perft(board, 6);

        for(String fen : BenchPositions.POSITIONS) {
            board.loadFromFen(fen);
            engine.getSearchAlgorithm().search(board, 100000, 1000, 100, 100, 67_0000, 12, 0L);
            
            nodeCount += engine.getSearchAlgorithm().getLastNodeCount();
            nps += engine.getSearchAlgorithm().getLastNPS();
            engine.getSearchAlgorithm().flushHashTable();
        }

        System.out.println(nodeCount + " nodes " + nps/BenchPositions.POSITIONS.length + " nps");
    }
    
}
