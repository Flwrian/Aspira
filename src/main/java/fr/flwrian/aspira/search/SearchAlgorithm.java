package fr.flwrian.aspira.search;

import fr.flwrian.aspira.board.Board;
import fr.flwrian.aspira.move.Move;

public interface SearchAlgorithm {

    public Move search(Board board, int wtime, int btime, int winc, int binc, int movetime, int depth, long maxNodes);

    public int evaluate(Board board);

    public String getName();

    public void setStopSearch(boolean b);

    public long getLastNodeCount();

    public long getLastNPS();

    public void flushHashTable();

    // public void setDepth(int depth);

    // public void setRazorDepth(int depth);

    // public void setNPM(int npm);

    // public int getRazorDepth();

    // public int getNPM();
}
