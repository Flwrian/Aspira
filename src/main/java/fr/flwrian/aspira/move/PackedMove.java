package fr.flwrian.aspira.move;

import fr.flwrian.aspira.board.Board;

public class PackedMove {

    // Bit layout (64 bits):
    // 0–5   : to
    // 6–11  : from
    // 12–15 : promotion piece
    // 16–19 : captured piece
    // 20–23 : moving piece
    // 24–27 : flags (castle, capture, promo...)
    // 28–47 : SEE score or move score
    // 48–63 : optional (can be 0, or store sort priority, phase, history info...)

    // Bit layout (64 bits):
    // 1-6:   to (6 bits)
    // 7-12:  from (6 bits)
    // 13-16: promotion piece (4 bits)
    // 17-20: captured piece (4 bits)
    // 21-24: piece from (4 bits)
    // 25-28: flags (4 bits)
    // 29-48: score for move ordering (20 bits)
    public static long encode(
        int from, int to,
        int pieceFrom, int capturedPiece,
        int promotion, int flag,
        int seeScore
    ) {
        return ((long) to & 0x3FL)
             | (((long) from & 0x3FL) << 6)
             | (((long) promotion & 0xFL) << 12)
             | (((long) capturedPiece & 0xFL) << 16)
             | (((long) pieceFrom & 0xFL) << 20)
             | (((long) flag & 0xFL) << 24)
             | (((long) seeScore & 0xFFFFF) << 28); // 20 bits
    }

    public static long encode(Move move) {
        return encode(
            move.from,
            move.to,
            move.pieceFrom,
            move.capturedPiece,
            move.promotion,
            move.flags,
            move.seeScore
        );
    }

    public static int getFrom(long move)        { return (int)((move >> 6) & 0x3F); }
    public static int getTo(long move)          { return (int)(move & 0x3F); }
    public static int getPromotion(long move)   { return (int)((move >> 12) & 0xF); }
    public static int getCaptured(long move)    { return (int)((move >> 16) & 0xF); }
    public static int getPieceFrom(long move)   { return (int)((move >> 20) & 0xF); }
    public static int getFlags(long move)       { return (int)((move >> 24) & 0xF); }
    public static int getScore(long move)       { return (int)((move >> 28) & 0xFFFFF); }
    public static boolean isPromotion(long move) {
        return (getPromotion(move) != 0);
    }
    public static boolean isCapture(long move) {
        return (getCaptured(move) != Board.EMPTY);
    }

    public static long setScore(long move, int newScore) {
        return (move & ~(0xFFFFFL << 28)) | (((long) newScore & 0xFFFFF) << 28);
    }

    public static Move unpack(long packedMove) {
        return new Move(
            getFrom(packedMove),
            getTo(packedMove),
            getPieceFrom(packedMove),
            getCaptured(packedMove),
            getPromotion(packedMove),
            getFlags(packedMove),
            getScore(packedMove)
        );
    }
}
