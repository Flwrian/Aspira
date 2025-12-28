package fr.flwrian.aspira.move;

import java.util.Random;

import fr.flwrian.aspira.board.Board;

public class MoveGenerator {

    // bishop relevant occupancy bit count for every square
    private static final int[] bishopRelevantOccupancyBitCount = {
            6, 5, 5, 5, 5, 5, 5, 6,
            5, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 5, 5, 5, 5, 5, 5,
            6, 5, 5, 5, 5, 5, 5, 6
    };

    // rook relevant occupancy bit count for every square
    private static final int[] rookRelevantOccupancyBitCount = {
            12, 11, 11, 11, 11, 11, 11, 12,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            12, 11, 11, 11, 11, 11, 11, 12
    };

    private final static long rooksMagicNumbers[] = {
            0x4A80004004102080L, 0x8540044410002000L, 0x0200082600408010L, 0x0080080010008006L, 0x8100080010020500L,
            0x0200011004020008L, 0x8080010000800200L, 0x2080002040800100L, 0x0820800080204012L, 0xD401002100400080L,
            0x2002002200401081L, 0x604200220040100AL, 0x0004800800800400L, 0x0202001008040200L, 0x0881000100040200L,
            0x0001000220805100L, 0x40C0038025C08001L, 0x10082A0042010080L, 0x0200808020001000L, 0x0805010008100020L,
            0x0888004004004200L, 0x0001010004000802L, 0x0408808001000200L, 0x88E802000442A114L, 0x0280400180018030L,
            0x8006400880200080L, 0x00A0080440100040L, 0x0806100100092100L, 0x1110080080800400L, 0x0608040080800200L,
            0x000C8A2400190810L, 0x2000840200008041L, 0x8018400420800080L, 0x0020100161400044L, 0x0210080020200401L,
            0x1000100082804800L, 0x0440080080800400L, 0x1100800200800400L, 0x0080028104005810L, 0x020210408A00010CL,
            0x2040400080008021L, 0x0060100040244000L, 0x2009002000410010L, 0x0110010008110020L, 0x0840040801010010L,
            0x108C000200808004L, 0x0000010002008080L, 0x0840204081020004L, 0x0240046080094080L, 0x0008400020009080L,
            0x1010001420088080L, 0x0048040880100080L, 0x8800800800040280L, 0x0A00041040200801L, 0x0442000188040200L,
            0x2010004314068200L, 0xC050208000401905L, 0x0000208015084001L, 0x00A000A00A114101L, 0x0094080410010021L,
            0xA012000820041002L, 0x4802000410010802L, 0x0008021000812804L, 0x2004002900841042L
    };

    private final static long bishopsMagicNumbers[] = {
            0x49004091C082200L, 0x430322A082081L, 0x50410228201000L, 0x80020A02080C0040L, 0x4044050400444900L,
            0x830D6010144040L, 0x2004008424220080L, 0x210110012020L, 0x10400204012201L, 0x8A6255004004084L,
            0x8018820A4008000L, 0x8040404008A0488L, 0x41141045000080L, 0x9001289004200021L, 0x1C10802110440L,
            0xA000084202108200L, 0x40001002020410L, 0x930004801881280L, 0x8030C41440480L, 0x802202802084000L,
            0x1083000A90400080L, 0x410900020100B200L, 0x11000048182400L, 0x2541002048421000L, 0x12400818D00490L,
            0x128040008012800L, 0x4100022008010L, 0x181040008040810L, 0x2040002008200L, 0x8202102008404L,
            0x14C8020082862100L, 0x3020041108488L, 0x16104205100E80L, 0x1102206090800L, 0x211008010404L,
            0x2600110800040040L, 0x81100400008020L, 0x308020011A200L, 0x10620040108440L, 0x40020A02004C2082L,
            0x8801101005084CL, 0x4420610802100L, 0x200904C008800L, 0x4041214202202805L, 0x900200411102400L,
            0xA40180083000020L, 0x204080204324040L, 0x12442110210A00L, 0x80A4A2210C000C0L, 0x3000240404044000L,
            0x1800204208111800L, 0x1880484240218L, 0x20C01044500AL, 0x102002042288L, 0x246200401260108L,
            0x184280A00420801L, 0xAC840400820804L, 0xA011020041084800L, 0x8002012021080800L, 0xA0000100840400L,
            0x8100000084250410L, 0x101C40409104100L, 0x1120841110012106L, 0x40100082008020L
    };

    private static final long bishopMasks[] = new long[64];
    private static final long rookMasks[] = new long[64];
    private static final long bishopAttacks[][] = new long[64][512];
    private static final long rookAttacks[][] = new long[64][4096];

    // init slider piece attacks
    public static void initSliderAttacks(boolean bishop) {
        for (int square = 0; square < 64; square++) {
            bishopMasks[square] = maskBishopAttacks(square);
            rookMasks[square] = maskRookAttacks(square);

            // init current mask
            long mask = bishop ? bishopMasks[square] : rookMasks[square];

            // init relevant occupancy bit count
            int relevantOccupancyBitCount = bishop ? bishopRelevantOccupancyBitCount[square]
                    : rookRelevantOccupancyBitCount[square];

            // init occupancy index
            int occupancyIndex = 1 << relevantOccupancyBitCount;

            // loop over all possible occupancies
            for (int index = 0; index < occupancyIndex; index++) {

                if (bishop) {
                    long occupancy = setOccupancy(index, relevantOccupancyBitCount, mask);

                    // init magic index
                    int magicIndex = (int) ((occupancy * bishopsMagicNumbers[square]) >>> (64
                            - relevantOccupancyBitCount));

                    // set the attack for the current occupancy
                    bishopAttacks[square][magicIndex] = generateBishopAttacks(square, occupancy);
                } else {
                    long occupancy = setOccupancy(index, relevantOccupancyBitCount, mask);

                    // init magic index
                    int magicIndex = (int) ((occupancy * rooksMagicNumbers[square]) >>> (64
                            - relevantOccupancyBitCount));

                    // set the attack for the current occupancy
                    rookAttacks[square][magicIndex] = generateRookAttacks(square, occupancy);
                }
            }
        }
    }

    public static void initSlidingAttacks() {
        initSliderAttacks(false);
        initSliderAttacks(true);
    }

    // get bishop attacks
    public static long getBishopAttacks(int square, long occupancy) {
        occupancy &= bishopMasks[square];
        occupancy *= bishopsMagicNumbers[square];
        occupancy >>>= (64 - bishopRelevantOccupancyBitCount[square]);
        return bishopAttacks[square][(int) occupancy];
    }

    // get rook attacks
    public static long getRookAttacks(int square, long occupancy) {
        occupancy &= rookMasks[square];
        occupancy *= rooksMagicNumbers[square];
        occupancy >>>= (64 - rookRelevantOccupancyBitCount[square]);
        return rookAttacks[square][(int) occupancy];
    }

    // get queen attacks
    public static long getQueenAttacks(int square, long occupancy) {
        return getBishopAttacks(square, occupancy) | getRookAttacks(square, occupancy);
    }

    public static Random rng = new Random(545644564644546456L);

    public static long generateMagicNumber(int square, int relevantOccupancyBitCount, boolean isBishop) {

        long occupancies[] = new long[4096];
        long attacks[] = new long[4096];
        long usedAttacks[] = new long[4096];
        long attackMask = isBishop ? maskBishopAttacks(square) : maskRookAttacks(square);
        int occupanciesIndex = 1 << relevantOccupancyBitCount;

        for (int i = 0; i < occupanciesIndex; i++) {
            occupancies[i] = setOccupancy(i, relevantOccupancyBitCount, attackMask);
            attacks[i] = isBishop ? generateBishopAttacks(square, occupancies[i])
                    : generateRookAttacks(square, occupancies[i]);
        }

        for (int randomCount = 0; randomCount < 10000000; randomCount++) {
            long randomMagic = rng.nextLong() & rng.nextLong() & rng.nextLong();

            if (Long.bitCount((attackMask * randomMagic) & 0xFF00000000000000L) < 6) {
                continue;
            }

            // init used attack
            for (int i = 0; i < occupanciesIndex; i++) {
                usedAttacks[i] = 0L;
            }

            // init index & fail flag
            int index;
            boolean fail;

            // test magic index loop
            for (index = 0, fail = false; !fail && index < occupanciesIndex; index++) {
                // get magic index
                int magicIndex = (int) ((occupancies[index] * randomMagic) >>> (64 - relevantOccupancyBitCount));

                // check if the attack is already used
                if (usedAttacks[magicIndex] == 0L) {
                    usedAttacks[magicIndex] = attacks[index];
                } else if (usedAttacks[magicIndex] != attacks[index]) {
                    fail = true;
                }
            }

            // if we reach here, it means we found a valid magic number
            if (!fail) {
                return (randomMagic);
            }

        }

        // if we reach here, it means we didn't find a valid magic number
        System.out.println("Failed to find a magic number for square " + square + " with relevant occupancy bit count "
                + relevantOccupancyBitCount);
        return 0;
    }

    public static void initMagicNumbers() {
        // Rook
        for (int square = 0; square < 64; square++) {
            int relevantOccupancyBitCount = rookRelevantOccupancyBitCount[square];
            long magicNumber = generateMagicNumber(square, relevantOccupancyBitCount, false);
            // print the numbers so i can copy paste them
            System.out.printf("0x%XL, ", magicNumber);
        }
        System.out.println("");
        // Bishop
        for (int square = 0; square < 64; square++) {
            int relevantOccupancyBitCount = bishopRelevantOccupancyBitCount[square];
            long magicNumber = generateMagicNumber(square, relevantOccupancyBitCount, true);
            // print the numbers so i can copy paste them
            System.out.printf("0x%XL, ", magicNumber);
        }

    }

    public static long generateMask(Board board, boolean white) {

        if (white) {
            return generateWhiteMask(board);
        } else {
            return generateBlackMask(board);
        }

    }

    public static long generateOpponentMask(Board board) {
        if (board.whiteTurn) {
            return generateBlackMask(board);
        } else {
            return generateWhiteMask(board);
        }
    }

    public static long generateMask(Board board) {
        return generateMask(board, board.whiteTurn);
    }

    public static void printMask(Board board) {
        long mask = generateOpponentMask(board);
        board.printBitBoard(mask);
    }

    public static void printMask(Board board, boolean white) {
        long mask = generateMask(board, white);
        board.printBitBoard(mask);
    }

    public static int countBits(long mask) {
        int count = 0;
        while (mask != 0) {
            mask &= mask - 1;
            count++;
        }
        return count;
    }

    private static final int[] PROMO_PIECES = {
            Board.QUEEN, Board.ROOK, Board.BISHOP, Board.KNIGHT
    };

    public static PackedMoveList generatePseudoLegalMoves(Board board) {
        PackedMoveList moves = new PackedMoveList(218);

        // =========================
        // PAWNS
        // =========================
        long pawns = board.whiteTurn ? board.whitePawns : board.blackPawns;

        while (pawns != 0L) {
            long pawn = pawns & -pawns;
            pawns &= pawns - 1;

            int from = Board.getSquare(pawn);
            long pawnMoves = generatePawnMoves(pawn, board);

            while (pawnMoves != 0L) {
                long move = pawnMoves & -pawnMoves;
                pawnMoves &= pawnMoves - 1;

                int to = Board.getSquare(move);
                boolean isPromotion = (to >= 56 || to <= 7);
                boolean isEnPassant = (move & board.enPassantSquare) != 0L;

                // double pawn push
                if (((pawn << 16) == move) || ((pawn >> 16) == move)) {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.PAWN,
                            Board.EMPTY,
                            0,
                            Move.DOUBLE_PAWN_PUSH));
                    continue;
                }

                int captured = isEnPassant
                        ? Board.PAWN
                        : board.getPiece(to);

                boolean isCapture = isEnPassant || captured != Board.EMPTY;

                // promotion
                if (isPromotion) {
                    for (int promo : PROMO_PIECES) {

                        moves.add(PackedMove.encode(
                                from, to,
                                Board.PAWN,
                                captured,
                                promo,
                                Move.PROMOTION));
                    }
                    continue;
                }

                // en passant
                if (isEnPassant) {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.PAWN,
                            Board.PAWN,
                            0,
                            Move.EN_PASSANT));
                    continue;
                }

                // normal move / capture
                if (captured != Board.EMPTY) {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.PAWN,
                            captured,
                            0,
                            Move.CAPTURE));
                } else {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.PAWN,
                            Board.EMPTY,
                            0,
                            Move.DEFAULT));
                }
            }
        }

        // =========================
        // KNIGHTS
        // =========================
        long knights = board.whiteTurn ? board.whiteKnights : board.blackKnights;

        while (knights != 0L) {
            long knight = knights & -knights;
            knights &= knights - 1;

            int from = Board.getSquare(knight);
            long movesBB = board.whiteTurn
                    ? generateWhiteKnightMoves(knight, board)
                    : generateBlackKnightMoves(knight, board);

            while (movesBB != 0L) {
                long move = movesBB & -movesBB;
                movesBB &= movesBB - 1;

                int to = Board.getSquare(move);
                int captured = board.getPiece(to);

                if (captured != Board.EMPTY) {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.KNIGHT,
                            captured,
                            0,
                            Move.CAPTURE));
                } else {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.KNIGHT,
                            Board.EMPTY,
                            0,
                            Move.DEFAULT));
                }
            }
        }

        // =========================
        // BISHOPS
        // =========================
        long bishops = board.whiteTurn ? board.whiteBishops : board.blackBishops;

        while (bishops != 0L) {
            long bishop = bishops & -bishops;
            bishops &= bishops - 1;

            int from = Board.getSquare(bishop);
            long movesBB = board.whiteTurn
                    ? generateWhiteBishopMoves(bishop, board)
                    : generateBlackBishopMoves(bishop, board);

            while (movesBB != 0L) {
                long move = movesBB & -movesBB;
                movesBB &= movesBB - 1;

                int to = Board.getSquare(move);
                int captured = board.getPiece(to);

                if (captured != Board.EMPTY) {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.BISHOP,
                            captured,
                            0,
                            Move.CAPTURE));
                } else {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.BISHOP,
                            Board.EMPTY,
                            0,
                            Move.DEFAULT));
                }
            }
        }

        // =========================
        // ROOKS
        // =========================
        long rooks = board.whiteTurn ? board.whiteRooks : board.blackRooks;

        while (rooks != 0L) {
            long rook = rooks & -rooks;
            rooks &= rooks - 1;

            int from = Board.getSquare(rook);
            long movesBB = board.whiteTurn
                    ? generateWhiteRookMoves(rook, board)
                    : generateBlackRookMoves(rook, board);

            while (movesBB != 0L) {
                long move = movesBB & -movesBB;
                movesBB &= movesBB - 1;

                int to = Board.getSquare(move);
                int captured = board.getPiece(to);

                if (captured != Board.EMPTY) {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.ROOK,
                            captured,
                            0,
                            Move.CAPTURE));
                } else {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.ROOK,
                            Board.EMPTY,
                            0,
                            Move.DEFAULT));
                }
            }
        }

        // =========================
        // QUEENS
        // =========================
        long queens = board.whiteTurn ? board.whiteQueens : board.blackQueens;

        while (queens != 0L) {
            long queen = queens & -queens;
            queens &= queens - 1;

            int from = Board.getSquare(queen);
            long movesBB = board.whiteTurn
                    ? generateWhiteQueenMoves(queen, board)
                    : generateBlackQueenMoves(queen, board);

            while (movesBB != 0L) {
                long move = movesBB & -movesBB;
                movesBB &= movesBB - 1;

                int to = Board.getSquare(move);
                int captured = board.getPiece(to);

                if (captured != Board.EMPTY) {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.QUEEN,
                            captured,
                            0,
                            Move.CAPTURE));
                } else {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.QUEEN,
                            Board.EMPTY,
                            0,
                            Move.DEFAULT));
                }
            }
        }

        // =========================
        // KING
        // =========================
        long king = board.whiteTurn ? board.whiteKing : board.blackKing;
        int from = Board.getSquare(king);

        long kingMoves = board.whiteTurn
                ? generateWhiteKingMoves(king, board)
                : generateBlackKingMoves(king, board);

        while (kingMoves != 0L) {
            long move = kingMoves & -kingMoves;
            kingMoves &= kingMoves - 1;

            int to = Board.getSquare(move);
            int captured = board.getPiece(to);

            int flag;

            if (Math.abs(from - to) == 2) {
                flag = Move.CASTLING;
            } else if (captured != Board.EMPTY) {
                flag = Move.CAPTURE;
            } else {
                flag = Move.DEFAULT;
            }

            moves.add(PackedMove.encode(
                    from, to,
                    Board.KING,
                    captured,
                    0,
                    flag));
        }

        return moves;
    }

    public static PackedMoveList generateCaptureMoves(Board board) {
        // maximum number of capture moves is 218
        PackedMoveList moves = new PackedMoveList(218);

        // Pawns
        long pawns = board.whiteTurn ? board.getWhitePawns() : board.getBlackPawns();

        while (pawns != 0L) {
            long pawn = pawns & -pawns;
            pawns &= pawns - 1;

            int from = Board.getSquare(pawn);
            long pawnMoves = generatePawnMoves(pawn, board);

            while (pawnMoves != 0L) {
                long move = pawnMoves & -pawnMoves;
                pawnMoves &= pawnMoves - 1;

                int to = Board.getSquare(move);
                final boolean isPromotion = (to >= 56 || to <= 7);
                final boolean isEnPassant = (move & board.enPassantSquare) != 0L;

                int capturedPiece;
                if (isEnPassant) {
                    capturedPiece = Board.PAWN;
                } else {
                    capturedPiece = board.getPiece(to);
                    if (capturedPiece == Board.EMPTY)
                        continue;
                }

                if (isPromotion) {
                    for (int promo : PROMO_PIECES) {
                        moves.add(PackedMove.encode(
                                from, to,
                                Board.PAWN,
                                capturedPiece,
                                promo,
                                Move.PROMOTION));
                    }
                } else {
                    moves.add(PackedMove.encode(
                            from, to,
                            Board.PAWN,
                            capturedPiece,
                            0,
                            isEnPassant ? Move.EN_PASSANT : Move.CAPTURE));
                }
            }
        }

        // Knights
        long knights = board.whiteTurn ? board.getWhiteKnights() : board.getBlackKnights();
        while (knights != 0L) {
            long knight = knights & -knights;
            knights &= knights - 1;
            int from = Board.getSquare(knight);
            long knightMoves = board.whiteTurn ? generateWhiteKnightMoves(knight, board)
                    : generateBlackKnightMoves(knight, board);

            while (knightMoves != 0L) {
                long move = Board.getLSB(knightMoves);
                knightMoves &= knightMoves - 1;
                int to = Board.getSquare(move);

                if ((move & board.bitboard) != 0L) {
                    int capturedPiece = board.getPiece(to);

                    int packed = PackedMove.encode(from, to, Board.KNIGHT, capturedPiece, 0, 0);
                    moves.add(packed);
                }
            }
        }

        // Bishops
        long bishops = board.whiteTurn ? board.getWhiteBishops() : board.getBlackBishops();

        while (bishops != 0L) {
            long bishop = bishops & -bishops;
            bishops &= bishops - 1;

            int from = Board.getSquare(bishop);
            long movesBB = board.whiteTurn
                    ? generateWhiteBishopMoves(bishop, board)
                    : generateBlackBishopMoves(bishop, board);

            // capture-only filter
            movesBB &= board.bitboard;

            while (movesBB != 0L) {
                long move = movesBB & -movesBB;
                movesBB &= movesBB - 1;

                int to = Board.getSquare(move);
                int captured = board.getPiece(to);


                moves.add(PackedMove.encode(
                        from, to,
                        Board.BISHOP,
                        captured,
                        0,
                        Move.CAPTURE));
            }
        }

        // Rooks
        long rooks = board.whiteTurn ? board.getWhiteRooks() : board.getBlackRooks();

        while (rooks != 0L) {
            long rook = rooks & -rooks;
            rooks &= rooks - 1;

            int from = Board.getSquare(rook);
            long movesBB = board.whiteTurn
                    ? generateWhiteRookMoves(rook, board)
                    : generateBlackRookMoves(rook, board);

            movesBB &= board.bitboard;

            while (movesBB != 0L) {
                long move = movesBB & -movesBB;
                movesBB &= movesBB - 1;

                int to = Board.getSquare(move);
                int captured = board.getPiece(to);

                moves.add(PackedMove.encode(
                        from, to,
                        Board.ROOK,
                        captured,
                        0,
                        Move.CAPTURE));
            }
        }

        // Queens
        long queens = board.whiteTurn ? board.getWhiteQueens() : board.getBlackQueens();

        while (queens != 0L) {
            long queen = queens & -queens;
            queens &= queens - 1;

            int from = Board.getSquare(queen);
            long movesBB = board.whiteTurn
                    ? generateWhiteQueenMoves(queen, board)
                    : generateBlackQueenMoves(queen, board);

            movesBB &= board.bitboard;

            while (movesBB != 0L) {
                long move = movesBB & -movesBB;
                movesBB &= movesBB - 1;

                int to = Board.getSquare(move);
                int captured = board.getPiece(to);

                moves.add(PackedMove.encode(
                        from, to,
                        Board.QUEEN,
                        captured,
                        0,
                        Move.CAPTURE));
            }
        }

        // Kings
        long king = board.whiteTurn ? board.getWhiteKing() : board.getBlackKing();
        int from = Board.getSquare(king);

        long kingMoves = board.whiteTurn
                ? generateWhiteKingMoves(king, board)
                : generateBlackKingMoves(king, board);

        kingMoves &= board.bitboard;

        while (kingMoves != 0L) {
            long move = kingMoves & -kingMoves;
            kingMoves &= kingMoves - 1;

            int to = Board.getSquare(move);
            int captured = board.getPiece(to);

            moves.add(PackedMove.encode(
                    from, to,
                    Board.KING,
                    captured,
                    0,
                    Move.CAPTURE));
        }

        return moves;
    }

    public static long generateWhiteMask(Board board) {
        long whiteAttacks = 0L;

        // Générer les mouvements des pions blancs
        long whitePawnMask = generatePawnMask(board.whitePawns, true);
        whiteAttacks |= whitePawnMask;

        // Générer les mouvements des cavaliers blancs
        long whiteKnightMask = generateKnightMask(board.whiteKnights);
        whiteAttacks |= whiteKnightMask;

        // Générer les mouvements des fous blancs
        long whiteBishops = board.whiteBishops;
        while (whiteBishops != 0L) {
            int bishopSquare = Long.numberOfTrailingZeros(whiteBishops);
            whiteBishops &= whiteBishops - 1;
            whiteAttacks |= getBishopAttacks(bishopSquare, board.bitboard);
        }

        // Générer les mouvements des tours blanches
        long whiteRooks = board.whiteRooks;
        while (whiteRooks != 0L) {
            int rookSquare = Long.numberOfTrailingZeros(whiteRooks);
            whiteRooks &= whiteRooks - 1;
            whiteAttacks |= getRookAttacks(rookSquare, board.bitboard);
        }

        // Générer les mouvements des reines blanches
        long whiteQueens = board.whiteQueens;
        while (whiteQueens != 0L) {
            int queenSquare = Long.numberOfTrailingZeros(whiteQueens);
            whiteQueens &= whiteQueens - 1;
            whiteAttacks |= getBishopAttacks(queenSquare, board.bitboard);
            whiteAttacks |= getRookAttacks(queenSquare, board.bitboard);
        }

        // Générer les mouvements des rois blancs
        long whiteKingMask = generateKingMask(board.whiteKing);
        whiteAttacks |= whiteKingMask;

        return whiteAttacks;
    }

    private static long generateBlackMask(Board board) {
        long blackAttacks = 0L;

        // Générer les mouvements des pions noirs
        long blackPawnMask = generatePawnMask(board.blackPawns, false);
        blackAttacks |= blackPawnMask;

        // Générer les mouvements des cavaliers noirs
        long blackKnightMask = generateKnightMask(board.blackKnights);
        blackAttacks |= blackKnightMask;

        // Générer les mouvements des fous noirs
        long blackBishops = board.blackBishops;
        while (blackBishops != 0L) {
            int bishopSquare = Long.numberOfTrailingZeros(blackBishops);
            blackBishops &= blackBishops - 1;
            blackAttacks |= getBishopAttacks(bishopSquare, board.bitboard);
        }

        // Générer les mouvements des tours noirs
        long blackRooks = board.blackRooks;
        while (blackRooks != 0L) {
            int rookSquare = Long.numberOfTrailingZeros(blackRooks);
            blackRooks &= blackRooks - 1;
            blackAttacks |= getRookAttacks(rookSquare, board.bitboard);
        }

        // Générer les mouvements des reines noires
        long blackQueens = board.blackQueens;
        while (blackQueens != 0L) {
            int queenSquare = Long.numberOfTrailingZeros(blackQueens);
            blackQueens &= blackQueens - 1;
            blackAttacks |= getBishopAttacks(queenSquare, board.bitboard);
            blackAttacks |= getRookAttacks(queenSquare, board.bitboard);
        }

        // Générer les mouvements des rois noirs
        long blackKingMask = generateKingMask(board.blackKing);
        blackAttacks |= blackKingMask;

        return blackAttacks;
    }

    private static long generatePawnMask(long pawns, boolean white) {
        long pawnMask = 0L;

        if (white) {
            // Captures diagonales, gauche et droite
            long capturesLeft = (pawns << 9) & ~Board.FILE_H;
            long capturesRight = (pawns << 7) & ~Board.FILE_A;

            pawnMask |= capturesLeft | capturesRight;
        } else {
            // Captures diagonales, gauche et droite
            long capturesLeft = (pawns >> 7) & ~Board.FILE_H;
            long capturesRight = (pawns >> 9) & ~Board.FILE_A;

            pawnMask |= capturesLeft | capturesRight;
        }

        return pawnMask;
    }

    private static long generatePawnAttacks(long pawns, boolean white) {
        // Mask + capture and en passant moves
        long pawnAttacks = 0L;
        if (white) {
            // Captures diagonales, gauche et droite
            long capturesLeft = (pawns << 9) & ~Board.FILE_H;
            long capturesRight = (pawns << 7) & ~Board.FILE_A;

            pawnAttacks |= capturesLeft | capturesRight;
        } else {
            // Captures diagonales, gauche et droite
            long capturesLeft = (pawns >> 7) & ~Board.FILE_H;
            long capturesRight = (pawns >> 9) & ~Board.FILE_A;

            pawnAttacks |= capturesLeft | capturesRight;
        }

        // En passant
        if (white) {
            long enPassantLeft = (pawns << 9) & Board.FILE_H;
            long enPassantRight = (pawns << 7) & Board.FILE_A;
            pawnAttacks |= enPassantLeft | enPassantRight;
        } else {
            long enPassantLeft = (pawns >> 7) & Board.FILE_H;
            long enPassantRight = (pawns >> 9) & Board.FILE_A;
            pawnAttacks |= enPassantLeft | enPassantRight;
        }
        return pawnAttacks;
    }

    public static long generatePawnMoves(long pawns, Board board) {
        long pawnMoves = 0L;

        if (board.whiteTurn) {
            long singlePush = (pawns << 8) & ~board.getBoard(); // Avancer d'une case
            // Avancer de deux cases depuis la rangée initiale mais seulement si les cases
            // sont vides
            long doublePush = ((pawns << 16) & ~board.getBoard() & (singlePush << 8) & Board.RANK_4);
            pawnMoves |= singlePush | doublePush;
            // Captures diagonales, gauche et droite
            long capturesLeft = (pawns << 7) & board.getBlackPieces() & ~Board.FILE_A;
            long capturesRight = (pawns << 9) & board.getBlackPieces() & ~Board.FILE_H;
            pawnMoves |= capturesLeft | capturesRight;

            // en passant
            if (board.enPassantSquare != 0L) {
                long enPassantLeft = (pawns << 7) & board.enPassantSquare & ~Board.FILE_A;
                long enPassantRight = (pawns << 9) & board.enPassantSquare & ~Board.FILE_H;
                pawnMoves |= enPassantLeft | enPassantRight;
            }

        } else {
            long singlePush = (pawns >> 8) & ~board.getBoard(); // Avancer d'une case
            long doublePush = ((pawns >> 16) & ~board.getBoard() & (singlePush >> 8) & Board.RANK_5); // Avancer de
                                                                                                      // deux cases
                                                                                                      // depuis la
                                                                                                      // rangée
                                                                                                      // initiale
            pawnMoves |= singlePush | doublePush;
            // Captures diagonales, gauche et droite
            long capturesLeft = (pawns >> 7) & board.getWhitePieces() & ~Board.FILE_H;
            long capturesRight = (pawns >> 9) & board.getWhitePieces() & ~Board.FILE_A;
            pawnMoves |= capturesLeft | capturesRight;

            // en passant
            if (board.enPassantSquare != 0L) {
                long enPassantLeft = (pawns >> 7) & board.enPassantSquare & ~Board.FILE_H;
                long enPassantRight = (pawns >> 9) & board.enPassantSquare & ~Board.FILE_A;
                pawnMoves |= enPassantLeft | enPassantRight;
            }

        }

        return pawnMoves;

    }

    public static long generateKnightMask(long knights) {
        long knightMask = 0L;

        while (knights != 0L) {
            long knight = Board.getLSB(knights);
            knights &= knights - 1; //

            // run the knight through the precomputed knight moves
            long knightMoves = Board.KNIGHT_MOVES[Long.numberOfTrailingZeros(knight)];

            knightMask |= knightMoves;
        }

        return knightMask;

    }

    // Generate knight legal moves
    public static long generateWhiteKnightMoves(long knights, Board board) {
        // Le cavalier peut se déplacer en L, 2 cases dans une direction et 1 case dans
        // une autre et ne peut pas aller sur une case occupée par une pièce de la même
        // couleur
        long knightMoves = 0L;

        // Déplacements en forme de L
        long knightMask = generateKnightMask(knights);

        // On ne peut pas aller sur une case occupée par une pièce de la même couleur
        knightMoves = knightMask & ~board.getWhitePieces();

        return knightMoves;
    }

    public static long maskBishopAttacks(int square) {
        long attacks = 0L;
        int f, r;

        int tr = square / 8;
        int tf = square % 8;

        // generate attacks
        for (r = tr + 1, f = tf + 1; r <= 6 && f <= 6; r++, f++) {
            attacks |= 1L << (r * 8 + f);
        }
        for (r = tr + 1, f = tf - 1; r <= 6 && f >= 1; r++, f--) {
            attacks |= 1L << (r * 8 + f);
        }
        for (r = tr - 1, f = tf + 1; r >= 1 && f <= 6; r--, f++) {
            attacks |= 1L << (r * 8 + f);
        }
        for (r = tr - 1, f = tf - 1; r >= 1 && f >= 1; r--, f--) {
            attacks |= 1L << (r * 8 + f);
        }

        return attacks;

    }

    public static long maskRookAttacks(int square) {
        long attacks = 0L;
        int f, r;

        int tr = square / 8;
        int tf = square % 8;

        // generate attacks
        for (r = tr + 1; r <= 6; r++) {
            attacks |= 1L << (r * 8 + tf);
        }
        for (r = tr - 1; r >= 1; r--) {
            attacks |= 1L << (r * 8 + tf);
        }
        for (f = tf + 1; f <= 6; f++) {
            attacks |= 1L << (tr * 8 + f);
        }
        for (f = tf - 1; f >= 1; f--) {
            attacks |= 1L << (tr * 8 + f);
        }

        return attacks;

    }

    public static long generateBishopAttacks(int square, long blockers) {
        long attacks = 0L;
        int f, r;

        int tr = square / 8;
        int tf = square % 8;

        // generate attacks
        for (r = tr + 1, f = tf + 1; r <= 7 && f <= 7; r++, f++) {
            attacks |= 1L << (r * 8 + f);
            if ((blockers & (1L << (r * 8 + f))) != 0L) {
                break;
            }
        }
        for (r = tr + 1, f = tf - 1; r <= 7 && f >= 0; r++, f--) {
            attacks |= 1L << (r * 8 + f);
            if ((blockers & (1L << (r * 8 + f))) != 0L) {
                break;
            }
        }
        for (r = tr - 1, f = tf + 1; r >= 0 && f <= 7; r--, f++) {
            attacks |= 1L << (r * 8 + f);
            if ((blockers & (1L << (r * 8 + f))) != 0L) {
                break;
            }
        }
        for (r = tr - 1, f = tf - 1; r >= 0 && f >= 0; r--, f--) {
            attacks |= 1L << (r * 8 + f);
            if ((blockers & (1L << (r * 8 + f))) != 0L) {
                break;
            }
        }

        return attacks;

    }

    public static long generateRookAttacks(int square, long blockers) {
        long attacks = 0L;
        int f, r;

        int tr = square / 8;
        int tf = square % 8;

        // generate attacks
        for (r = tr + 1; r <= 7; r++) {
            attacks |= 1L << (r * 8 + tf);
            if ((blockers & (1L << (r * 8 + tf))) != 0L) {
                break;
            }
        }
        for (r = tr - 1; r >= 0; r--) {
            attacks |= 1L << (r * 8 + tf);
            if ((blockers & (1L << (r * 8 + tf))) != 0L) {
                break;
            }
        }
        for (f = tf + 1; f <= 7; f++) {
            attacks |= 1L << (tr * 8 + f);
            if ((blockers & (1L << (tr * 8 + f))) != 0L) {
                break;
            }
        }
        for (f = tf - 1; f >= 0; f--) {
            attacks |= 1L << (tr * 8 + f);
            if ((blockers & (1L << (tr * 8 + f))) != 0L) {
                break;
            }
        }

        return attacks;

    }

    public static long setOccupancy(int index, int bitsInMask, long attackMask) {
        long occupancy = 0L;
        for (int i = 0; i < bitsInMask; i++) {
            int square = Long.numberOfTrailingZeros(attackMask);
            if ((index & (1 << i)) != 0) {
                occupancy |= (1L << square);
            }
            attackMask &= attackMask - 1;
        }
        return occupancy;
    }

    private static long generateWhiteBishopMoves(long bishops, Board board) {
        // La seule différence entre les mouvements des fous et les masques des fous est
        // que selon la couleur, les fous ne peuvent pas capturer une pièce de la même
        // couleur

        long bishopMoves = 0L;

        while (bishops != 0L) {
            int bishopSquare = Long.numberOfTrailingZeros(bishops);
            bishops &= bishops - 1;

            bishopMoves |= getBishopAttacks(bishopSquare, board.bitboard);

        }

        // On ne peut pas capturer une pièce de la même couleur
        bishopMoves &= ~board.getWhitePieces();

        return bishopMoves;
    }

    public static long generateWhiteRookMoves(long rooks, Board board) {
        // La seule différence entre les mouvements des tours et les masques des tours
        // est que selon la couleur, les tours ne peuvent pas capturer une pièce de la
        // même couleur

        long rookMoves = 0L;

        while (rooks != 0L) {
            int rookSquare = Long.numberOfTrailingZeros(rooks);
            rooks &= rooks - 1;

            rookMoves |= getRookAttacks(rookSquare, board.bitboard);

        }

        // On ne peut pas capturer une pièce de la même couleur
        rookMoves &= ~board.getWhitePieces();

        return rookMoves;
    }

    private static long generateWhiteQueenMoves(long queens, Board board) {
        // La seule différence entre les mouvements des reines et les masques des reines
        // est que selon la couleur, les reines ne peuvent pas capturer une pièce de la
        // même couleur

        long queenMoves = 0L;

        while (queens != 0L) {
            int queenSquare = Long.numberOfTrailingZeros(queens);
            queens &= queens - 1;

            queenMoves |= generateBishopAttacks(queenSquare, board.bitboard);
            queenMoves |= generateRookAttacks(queenSquare, board.bitboard);

        }

        // On ne peut pas capturer une pièce de la même couleur
        queenMoves &= ~board.getWhitePieces();
        return queenMoves;
    }

    public static long generateKingMask(long king) {
        return Board.KING_MOVES[Long.numberOfTrailingZeros(king)];
    }

    public static final long generateWhiteKingMoves(long king, Board board) {
        // Le roi peut se déplacer d'une case dans toutes les directions et peut roquer
        long kingMoves = generateKingMask(king);
        // pas la meme couleur
        kingMoves &= ~board.getWhitePieces();

        // La logique du roque est comme suit:
        // Si on possède le droit de roquer, que la tour est à sa place et que les cases
        // entre le roi et la tour sont vides
        // alors on peut roquer

        // Si on a les droits de roquer du côté de du roi
        if (board.whiteCastleKingSide == 1L) {
            // Si la tour est à sa place
            if ((board.whiteRooks & Board.WHITE_KING_SIDE_ROOK_SQUARE) != 0L) {
                // Si il n'y a pas de pièces entre le roi et la tour
                if ((Board.WHITE_KING_SIDE_CASTLE_EMPTY_SQUARES_MASK & board.getBoard()) == 0L) {
                    // Si les cases ne sont pas attaquées
                    if ((Board.WHITE_KING_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK
                            & generateOpponentMask(board)) == 0L) {
                        // On peut roquer
                        kingMoves |= Board.WHITE_KING_SIDE_CASTLE_KING_SQUARE;
                    }
                }
            }
        }

        // Si on a les droits de roquer du côté de la dame
        if (board.whiteCastleQueenSide == 1L) {
            // Si la tour est à sa place
            if ((board.whiteRooks & Board.WHITE_QUEEN_SIDE_ROOK_SQUARE) != 0L) {
                // Si il n'y a pas de pièces entre le roi et la tour
                if ((Board.WHITE_QUEEN_SIDE_CASTLE_EMPTY_SQUARES_MASK & board.getBoard()) == 0L) {
                    // Si les cases ne sont pas attaquées
                    if ((Board.WHITE_QUEEN_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK
                            & generateOpponentMask(board)) == 0L) {
                        // On peut roquer
                        kingMoves |= Board.WHITE_QUEEN_SIDE_CASTLE_KING_SQUARE;
                    }
                }
            }
        }
        return kingMoves;
    }

    public static long generateBlackKingMoves(long king, Board board) {
        // Le roi peut se déplacer d'une case dans toutes les directions et peut roquer
        long kingMoves = generateKingMask(king);
        // pas la meme couleur
        kingMoves &= ~board.getBlackPieces();

        // La logique du roque est comme suit:
        // Si on possède le droit de roquer, que la tour est à sa place et que les cases
        // entre le roi et la tour sont vides
        // alors on peut roquer

        // Si on a les droits de roquer du côté de du roi
        if (board.blackCastleKingSide == 1L) {
            // Si la tour est à sa place
            if ((board.blackRooks & Board.BLACK_KING_SIDE_ROOK_SQUARE) != 0L) {
                // Si il n'y a pas de pièces entre le roi et la tour
                if ((Board.BLACK_KING_SIDE_CASTLE_EMPTY_SQUARES_MASK & board.getBoard()) == 0L) {
                    // Si les cases ne sont pas attaquées
                    if ((Board.BLACK_KING_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK
                            & generateOpponentMask(board)) == 0L) {
                        // On peut roquer
                        kingMoves |= Board.BLACK_KING_SIDE_CASTLE_KING_SQUARE;
                    }
                }
            }
        }

        // Si on a les droits de roquer du côté de la dame
        if (board.blackCastleQueenSide == 1L) {
            // Si la tour est à sa place
            if ((board.blackRooks & Board.BLACK_QUEEN_SIDE_ROOK_SQUARE) != 0L) {
                // Si il n'y a pas de pièces entre le roi et la tour
                if ((Board.BLACK_QUEEN_SIDE_CASTLE_EMPTY_SQUARES_MASK & board.getBoard()) == 0L) {
                    // Si les cases ne sont pas attaquées
                    if ((Board.BLACK_QUEEN_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK
                            & generateOpponentMask(board)) == 0L) {
                        // On peut roquer
                        kingMoves |= Board.BLACK_QUEEN_SIDE_CASTLE_KING_SQUARE;
                    }
                }
            }
        }

        return kingMoves;
    }

    public static long generateBlackKnightMoves(long knights, Board board) {
        // Le cavalier peut se déplacer en L, 2 cases dans une direction et 1 case dans
        // une autre et ne peut pas aller sur une case occupée par une pièce de la même
        // couleur
        long knightMoves = 0L;

        // Déplacements en forme de L
        long knightMask = generateKnightMask(knights);

        // On ne peut pas aller sur une case occupée par une pièce de la même couleur
        knightMoves = knightMask & ~board.getBlackPieces();

        return knightMoves;
    }

    public static long generateBlackBishopMoves(long bishops, Board board) {
        // La seule différence entre les mouvements des fous et les masques des fous est
        // que selon la couleur, les fous ne peuvent pas capturer une pièce de la même
        // couleur

        long bishopMoves = 0L;

        while (bishops != 0L) {
            int bishopSquare = Long.numberOfTrailingZeros(bishops);
            bishops &= bishops - 1;

            bishopMoves |= generateBishopAttacks(bishopSquare, board.bitboard);

        }

        // On ne peut pas capturer une pièce de la même couleur
        bishopMoves &= ~board.getBlackPieces();

        return bishopMoves;
    }

    public static long generateBlackRookMoves(long rooks, Board board) {
        // La seule différence entre les mouvements des tours et les masques des tours
        // est que selon la couleur, les tours ne peuvent pas capturer une pièce de la
        // même couleur

        long rookMoves = 0L;

        while (rooks != 0L) {
            int rookSquare = Long.numberOfTrailingZeros(rooks);
            rooks &= rooks - 1;

            rookMoves |= generateRookAttacks(rookSquare, board.bitboard);

        }

        // On ne peut pas capturer une pièce de la même couleur
        rookMoves &= ~board.getBlackPieces();

        return rookMoves;
    }

    public static long generateBlackQueenMoves(long queens, Board board) {
        // La seule différence entre les mouvements des reines et les masques des reines
        // est que selon la couleur, les reines ne peuvent pas capturer une pièce de la
        // même couleur

        long queenMoves = 0L;

        while (queens != 0L) {
            int queenSquare = Long.numberOfTrailingZeros(queens);
            queens &= queens - 1;

            queenMoves |= generateBishopAttacks(queenSquare, board.bitboard);
            queenMoves |= generateRookAttacks(queenSquare, board.bitboard);

        }

        // On ne peut pas capturer une pièce de la même couleur
        queenMoves &= ~board.getBlackPieces();

        return queenMoves;
    }
}
