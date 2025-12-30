package fr.flwrian.aspira.uci;

import java.util.Random;

import fr.flwrian.aspira.board.Board;
import fr.flwrian.aspira.move.Move;
import fr.flwrian.aspira.move.PackedMove;

public class PackedMoveTestMain {

    public static void main(String[] args) {
        long toBB = 0x00000000000002L; // C1 square
        int count = 0;

        long start = System.nanoTime();
        for (int i = 0; i < 1000000000; i++) {
            if ((toBB & Board.C1) != 0) {
                count++;
            }
        }
        long end = System.nanoTime();
        System.out.println("Time taken for != check: " + (end - start) + " ms, count = " + count);

        count = 0;
        start = System.nanoTime();
        for (int i = 0; i < 1000000000; i++) {
            if (toBB == Board.A1) {
                count++;
            }
        }
        long end2 = System.nanoTime();
        System.out.println("Time taken for == check: " + (end2 - start) + " ms, count = " + count);
    }

    private static void testEncodeDecode() {
        int from = 12;
        int to = 45;
        int pieceFrom = 3;
        int captured = 5;
        int promotion = 2;
        int flags = 1;

        int packed = PackedMove.encode(
                from, to, pieceFrom, captured, promotion, flags
        );

        assert PackedMove.getFrom(packed) == from : "from mismatch";
        assert PackedMove.getTo(packed) == to : "to mismatch";
        assert PackedMove.getPieceFrom(packed) == pieceFrom : "pieceFrom mismatch";
        assert PackedMove.getCaptured(packed) == captured : "captured mismatch";
        assert PackedMove.getPromotion(packed) == promotion : "promotion mismatch";
        assert PackedMove.getFlags(packed) == flags : "flags mismatch";
    }
}