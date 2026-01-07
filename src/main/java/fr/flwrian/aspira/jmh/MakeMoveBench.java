package fr.flwrian.aspira.jmh;

import fr.flwrian.aspira.bench.BenchPositions;
import fr.flwrian.aspira.board.Board;
import fr.flwrian.aspira.move.PackedMoveList;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 7, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Thread)
public class MakeMoveBench {

    /* ================================
       POSITIONS RÉALISTES (À ÉTENDRE)
       ================================ */

    static final String[] FENS = BenchPositions.POSITIONS;
    static final int MAX_DEPTH = 4;

    /* ================================
       DONNÉES PRÉ-CALCULÉES
       ================================ */

    Board[] boards;
    PackedMoveList[] moveLists;
    PackedMoveList[] tempLists; // Pour les benchmarks qui ont besoin de listes temporaires
    int index;

    /* ================================
       SETUP (hors hot path)
       ================================ */

    @Setup(Level.Trial)
    public void setup() {
        int n = FENS.length;

        boards = new Board[n];
        
        // Initialiser moveLists pour chaque position
        moveLists = new PackedMoveList[n];
        for (int i = 0; i < n; i++) {
            moveLists[i] = new PackedMoveList(218);
        }
        
        // Initialiser tempLists pour les benchmarks temporaires
        tempLists = new PackedMoveList[MAX_DEPTH];
        for (int i = 0; i < MAX_DEPTH; i++) {
            tempLists[i] = new PackedMoveList(218);
        }

        for (int i = 0; i < n; i++) {
            Board b = new Board();
            b.loadFromFen(FENS[i]);
            
            // Générer les coups légaux pour chaque position
            b.getLegalMoves(moveLists[i]);

            boards[i] = b;
        }

        index = 0;
    }

    /* ================================
       BENCHMARK : makeMove + undoMove
       ================================ */

    @Benchmark
    public void makeMoveUndo(Blackhole bh) {
        int i = index++ % boards.length;

        Board b = boards[i];

        // rotation simple pour éviter les coups identiques
        int moveIndex = index % moveLists[i].size();
        int move = moveLists[i].get(moveIndex);

        b.makeMove(move);
        bh.consume(b.zobristKey);
        b.undoMove();
    }

    /* ================================
       VARIANTE : mini-search depth 2
       ================================ */

    @Benchmark
    public void depth2MiniSearch(Blackhole bh) {
        Board b = boards[index++ % boards.length];

        PackedMoveList m1 = b.getLegalMoves(tempLists[0]);
        int mv1 = m1.get(0);
        b.makeMove(mv1);

        PackedMoveList m2 = b.getLegalMoves(tempLists[1]);
        int mv2 = m2.get(0);
        b.makeMove(mv2);

        bh.consume(b.zobristKey);

        b.undoMove();
        b.undoMove();
    }

    @Benchmark
    public void generateLegalMoves(Blackhole bh) {
        // Rotation déterministe
        boards[index++ % boards.length].loadFromFen(FENS[index++ % FENS.length]);

        PackedMoveList moves = boards[index++ % boards.length].getLegalMoves(tempLists[2]);

        // Empêche DCE
        bh.consume(moves.size());
    }

    @Benchmark
    public void generatePseudoLegalMoves(Blackhole bh) {
        // Rotation déterministe
        boards[index++ % boards.length].loadFromFen(FENS[index++ % FENS.length]);

        PackedMoveList moves = boards[index++ % boards.length].getPseudoLegalMoves(tempLists[3]);

        // Empêche DCE
        bh.consume(moves.size());
    }
}
