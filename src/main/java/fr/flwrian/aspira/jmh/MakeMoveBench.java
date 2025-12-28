// package fr.flwrian.aspira.jmh;

// import fr.flwrian.aspira.bench.BenchPositions;
// import fr.flwrian.aspira.board.Board;
// import fr.flwrian.aspira.move.PackedMoveList;
// import org.openjdk.jmh.annotations.*;
// import org.openjdk.jmh.infra.Blackhole;

// import java.util.concurrent.TimeUnit;

// @BenchmarkMode(Mode.AverageTime)
// @OutputTimeUnit(TimeUnit.NANOSECONDS)
// @Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
// @Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
// @Fork(2)
// @State(Scope.Thread)
// public class MakeMoveBench {

//     /* ================================
//        POSITIONS RÉALISTES (À ÉTENDRE)
//        ================================ */

//     static final String[] FENS = BenchPositions.POSITIONS;

//     /* ================================
//        DONNÉES PRÉ-CALCULÉES
//        ================================ */

//     Board[] boards;
//     PackedMoveList[] moveLists;
//     int index;

//     /* ================================
//        SETUP (hors hot path)
//        ================================ */

//     @Setup(Level.Trial)
//     public void setup() {
//         int n = FENS.length;

//         boards = new Board[n];
//         moveLists = new PackedMoveList[n];

//         for (int i = 0; i < n; i++) {
//             Board b = new Board();
//             b.loadFromFen(FENS[i]);

//             boards[i] = b;
//             moveLists[i] = b.getLegalMoves();
//         }

//         index = 0;
//     }

//     /* ================================
//        BENCHMARK : makeMove + undoMove
//        ================================ */

//     @Benchmark
//     public void makeMoveUndo(Blackhole bh) {
//         int i = index++ % boards.length;

//         Board b = boards[i];
//         PackedMoveList ml = moveLists[i];

//         // rotation simple pour éviter les coups identiques
//         int moveIndex = index % ml.size();
//         long move = ml.get(moveIndex);

//         b.makeMove(move);
//         bh.consume(b.zobristKey);
//         b.undoMove();
//     }

//     /* ================================
//        VARIANTE : mini-search depth 2
//        ================================ */

//     @Benchmark
//     public void depth2MiniSearch(Blackhole bh) {
//         Board b = boards[index++ % boards.length];

//         PackedMoveList m1 = b.getLegalMoves();
//         long mv1 = m1.get(0);
//         b.makeMove(mv1);

//         PackedMoveList m2 = b.getLegalMoves();
//         long mv2 = m2.get(0);
//         b.makeMove(mv2);

//         bh.consume(b.zobristKey);

//         b.undoMove();
//         b.undoMove();
//     }

//     @Benchmark
//     public void generateLegalMoves(Blackhole bh) {
//         // Rotation déterministe
//         boards[index++ % boards.length].loadFromFen(FENS[index++ % FENS.length]);

//         PackedMoveList moves = boards[index++ % boards.length].getLegalMoves();

//         // Empêche DCE
//         bh.consume(moves.size());
//     }

//     @Benchmark
//     public void generatePseudoLegalMoves(Blackhole bh) {
//         // Rotation déterministe
//         boards[index++ % boards.length].loadFromFen(FENS[index++ % FENS.length]);

//         PackedMoveList moves = boards[index++ % boards.length].getPseudoLegalMoves();

//         // Empêche DCE
//         bh.consume(moves.size());
//     }
// }
