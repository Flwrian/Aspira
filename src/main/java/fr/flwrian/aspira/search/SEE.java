package fr.flwrian.aspira.search;

import fr.flwrian.aspira.board.Board;
import fr.flwrian.aspira.move.Move;
import fr.flwrian.aspira.move.MoveGenerator;
import fr.flwrian.aspira.move.PackedMove;

public final class SEE {

    // valeur SEE
    static final int[] SEE_PIECE_VALUES = {
            100, 300, 300, 500, 900, 20000
    };

    // ordre pour prendre le moins cher
    static final int[] PIECE_ORDER = {
            Board.PAWN,
            Board.KNIGHT,
            Board.BISHOP,
            Board.ROOK,
            Board.QUEEN,
            Board.KING
    };

    // pions qui attaquent une case donnée
    static final long[] WHITE_PAWN_ATTACKERS = new long[64];
    static final long[] BLACK_PAWN_ATTACKERS = new long[64];

    static {
        for (int sq = 0; sq < 64; sq++) {
            long bb = 1L << sq;
            WHITE_PAWN_ATTACKERS[sq] =
                    ((bb >>> 7) & ~Board.FILE_H) |
                    ((bb >>> 9) & ~Board.FILE_A);
            BLACK_PAWN_ATTACKERS[sq] =
                    ((bb << 7) & ~Board.FILE_A) |
                    ((bb << 9) & ~Board.FILE_H);
        }
    }

    public static int moveEstimatedValue(Board b, int move) {
        int captured = PackedMove.getCaptured(move);
        int value = captured != Board.EMPTY ? Board.PIECE_SCORES[captured] : 0;

        if (PackedMove.isPromotion(move)) {
            int promo = PackedMove.getPromotion(move);
            value += Board.PIECE_SCORES[promo] - Board.PAWN_SCORE;
        }
        if (PackedMove.getFlags(move) == Move.EN_PASSANT) {
            value = Board.PAWN_SCORE;
        }
        return value;
    }

    public static boolean staticExchangeEvaluation(Board b, int move, int threshold) {

        int from = PackedMove.getFrom(move);
        int to   = PackedMove.getTo(move);
        int pieceFrom = PackedMove.getPieceFrom(move);
        int flags = PackedMove.getFlags(move);

        boolean isPromotion = PackedMove.isPromotion(move);
        boolean isEnPassant = flags == Move.EN_PASSANT;
        int nextVictim = isPromotion ? PackedMove.getPromotion(move) : pieceFrom;

        int balance = moveEstimatedValue(b, move) - threshold;
        if (balance < 0) return false;

        balance -= SEE_PIECE_VALUES[nextVictim];
        if (balance >= 0) return true;

        long bishops = b.whiteBishops | b.blackBishops | b.whiteQueens | b.blackQueens;
        long rooks   = b.whiteRooks   | b.blackRooks   | b.whiteQueens | b.blackQueens;

        long occupied = b.bitboard;
        occupied ^= (1L << from);
        occupied |= (1L << to);
        if (isEnPassant) occupied ^= b.enPassantSquare;

        long attackers =
                (squareAttackedBy(b, to, true,  occupied)
               | squareAttackedBy(b, to, false, occupied))
               & occupied;

        boolean side = !b.whiteTurn;

        while (true) {
            long myAttackers = attackers & (side ? b.whitePieces : b.blackPieces);
            if (myAttackers == 0) break;

            nextVictim = -1;
            for (int p : PIECE_ORDER) {
                if ((myAttackers & pieceBitboard(b, side, p)) != 0) {
                    nextVictim = p;
                    break;
                }
            }

            long victimBB = myAttackers & pieceBitboard(b, side, nextVictim);
            long attacker = victimBB & -victimBB;
            occupied ^= attacker;

            if (nextVictim <= Board.BISHOP || nextVictim == Board.QUEEN)
                attackers |= MoveGenerator.getBishopAttacks(to, occupied) & bishops;
            if (nextVictim >= Board.ROOK)
                attackers |= MoveGenerator.getRookAttacks(to, occupied) & rooks;

            attackers &= occupied;
            side = !side;

            balance = -balance - 1 - SEE_PIECE_VALUES[nextVictim];
            if (balance >= 0) break;
        }

        return b.whiteTurn != side;
    }

    static long pieceBitboard(Board b, boolean white, int piece) {
        if (white) {
            return switch (piece) {
                case Board.PAWN -> b.whitePawns;
                case Board.KNIGHT -> b.whiteKnights;
                case Board.BISHOP -> b.whiteBishops;
                case Board.ROOK -> b.whiteRooks;
                case Board.QUEEN -> b.whiteQueens;
                case Board.KING -> b.whiteKing;
                default -> 0L;
            };
        } else {
            return switch (piece) {
                case Board.PAWN -> b.blackPawns;
                case Board.KNIGHT -> b.blackKnights;
                case Board.BISHOP -> b.blackBishops;
                case Board.ROOK -> b.blackRooks;
                case Board.QUEEN -> b.blackQueens;
                case Board.KING -> b.blackKing;
                default -> 0L;
            };
        }
    }

    static long squareAttackedBy(Board b, int sq, boolean white, long occ) {
        long attacks = 0L;

        attacks |= (white ? WHITE_PAWN_ATTACKERS[sq] : BLACK_PAWN_ATTACKERS[sq])
                & (white ? b.whitePawns : b.blackPawns);

        attacks |= Board.KNIGHT_ATTACKS[sq] & (white ? b.whiteKnights : b.blackKnights);
        attacks |= Board.KING_ATTACKS[sq]   & (white ? b.whiteKing   : b.blackKing);

        long bishops = white ? (b.whiteBishops | b.whiteQueens)
                             : (b.blackBishops | b.blackQueens);
        attacks |= MoveGenerator.getBishopAttacks(sq, occ) & bishops;

        long rooks = white ? (b.whiteRooks | b.whiteQueens)
                           : (b.blackRooks | b.blackQueens);
        attacks |= MoveGenerator.getRookAttacks(sq, occ) & rooks;

        return attacks;
    }
}
