package fr.flwrian.aspira.move;

import fr.flwrian.aspira.board.Board;

public final class PackedMove {

    // Masks
    private static final int MASK_6  = 0x3F;
    private static final int MASK_4  = 0x0F;

    // Bit shifts
    private static final int TO_SHIFT        = 0;
    private static final int FROM_SHIFT      = 6;
    private static final int PROMO_SHIFT     = 12;
    private static final int CAPTURE_SHIFT   = 16;
    private static final int PIECE_SHIFT     = 20;
    private static final int FLAGS_SHIFT     = 24;

    private PackedMove() {}

    // ---------- ENCODE ----------

    public static int encode(
            int from,
            int to,
            int pieceFrom,
            int capturedPiece,
            int promotion,
            int flags
    ) {
        return  ((to & MASK_6)               << TO_SHIFT)
              | ((from & MASK_6)             << FROM_SHIFT)
              | ((promotion & MASK_4)        << PROMO_SHIFT)
              | ((capturedPiece & MASK_4)    << CAPTURE_SHIFT)
              | ((pieceFrom & MASK_4)        << PIECE_SHIFT)
              | ((flags & MASK_4)             << FLAGS_SHIFT);
    }

    public static int encode(Move move) {
        return encode(
                move.from,
                move.to,
                move.pieceFrom,
                move.capturedPiece,
                move.promotion,
                move.flags
        );
    }

    // ---------- GETTERS ----------

    public static int getTo(int move) {
        return (move >>> TO_SHIFT) & MASK_6;
    }

    public static int getFrom(int move) {
        return (move >>> FROM_SHIFT) & MASK_6;
    }

    public static int getPromotion(int move) {
        return (move >>> PROMO_SHIFT) & MASK_4;
    }

    public static int getCaptured(int move) {
        return (move >>> CAPTURE_SHIFT) & MASK_4;
    }

    public static int getPieceFrom(int move) {
        return (move >>> PIECE_SHIFT) & MASK_4;
    }

    public static int getFlags(int move) {
        return (move >>> FLAGS_SHIFT) & MASK_4;
    }

    // ---------- HELPERS ----------

    public static boolean isPromotion(int move) {
        return getFlags(move) == Move.PROMOTION;
    }

    public static boolean isCapture(int move) {
        return getCaptured(move) != Board.EMPTY;
    }

    // ---------- UNPACK ----------

    public static Move unpack(int move) {
        return new Move(
                getFrom(move),
                getTo(move),
                getPieceFrom(move),
                getCaptured(move),
                getPromotion(move),
                getFlags(move)
        );
    }
}
