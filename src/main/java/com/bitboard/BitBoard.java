package com.bitboard;

import java.io.PrintWriter;

import com.bitboard.algorithms.Zobrist;

public class BitBoard {

    public boolean whiteTurn;

    public long whitePawns;
    public long whiteKnights;
    public long whiteBishops;
    public long whiteRooks;
    public long whiteQueens;
    public long whiteKing;

    public long blackPawns;
    public long blackKnights;
    public long blackBishops;
    public long blackRooks;
    public long blackQueens;
    public long blackKing;

    public long whitePieces;
    public long blackPieces;

    public long bitboard;

    public long whiteCastleQueenSide;
    public long whiteCastleKingSide;
    public long blackCastleQueenSide;
    public long blackCastleKingSide;

    public long enPassantSquare;

    // === EVAL ===
    public int currentEvalMG;
    public int currentEvalEG;
    public int phase;

    // === ZobristKey ===
    public long zobristKey;

    public short plyCount;

    // valid
    public static final long RANK_1 = 0x00000000000000FFL;
    public static final long RANK_2 = 0x000000000000FF00L;
    public static final long RANK_3 = 0x0000000000FF0000L;
    public static final long RANK_4 = 0x00000000FF000000L;
    public static final long RANK_5 = 0x000000FF00000000L;
    public static final long RANK_6 = 0x0000FF0000000000L;
    public static final long RANK_7 = 0x00FF000000000000L;
    public static final long RANK_8 = 0xFF00000000000000L;

    // valid
    public static final long FILE_H = 0x0101010101010101L;
    public static final long FILE_G = 0x0202020202020202L;
    public static final long FILE_F = 0x0404040404040404L;
    public static final long FILE_E = 0x0808080808080808L;
    public static final long FILE_D = 0x1010101010101010L;
    public static final long FILE_C = 0x2020202020202020L;
    public static final long FILE_B = 0x4040404040404040L;
    public static final long FILE_A = 0x8080808080808080L;

    public static final long A1 = 0b1L;
    public static final long B1 = A1 << 1;
    public static final long C1 = B1 << 1;
    public static final long D1 = C1 << 1;
    public static final long E1 = D1 << 1;
    public static final long F1 = E1 << 1;
    public static final long G1 = F1 << 1;
    public static final long H1 = G1 << 1;

    public static final long A2 = A1 << 8;
    public static final long B2 = B1 << 8;
    public static final long C2 = C1 << 8;
    public static final long D2 = D1 << 8;
    public static final long E2 = E1 << 8;
    public static final long F2 = F1 << 8;
    public static final long G2 = G1 << 8;
    public static final long H2 = H1 << 8;

    public static final long A3 = A2 << 8;
    public static final long B3 = B2 << 8;
    public static final long C3 = C2 << 8;
    public static final long D3 = D2 << 8;
    public static final long E3 = E2 << 8;
    public static final long F3 = F2 << 8;
    public static final long G3 = G2 << 8;
    public static final long H3 = H2 << 8;

    public static final long A4 = A3 << 8;
    public static final long B4 = B3 << 8;
    public static final long C4 = C3 << 8;
    public static final long D4 = D3 << 8;
    public static final long E4 = E3 << 8;
    public static final long F4 = F3 << 8;
    public static final long G4 = G3 << 8;
    public static final long H4 = H3 << 8;

    public static final long A5 = A4 << 8;
    public static final long B5 = B4 << 8;
    public static final long C5 = C4 << 8;
    public static final long D5 = D4 << 8;
    public static final long E5 = E4 << 8;
    public static final long F5 = F4 << 8;
    public static final long G5 = G4 << 8;
    public static final long H5 = H4 << 8;

    public static final long A6 = A5 << 8;
    public static final long B6 = B5 << 8;
    public static final long C6 = C5 << 8;
    public static final long D6 = D5 << 8;
    public static final long E6 = E5 << 8;
    public static final long F6 = F5 << 8;
    public static final long G6 = G5 << 8;
    public static final long H6 = H5 << 8;

    public static final long A7 = A6 << 8;
    public static final long B7 = B6 << 8;
    public static final long C7 = C6 << 8;
    public static final long D7 = D6 << 8;
    public static final long E7 = E6 << 8;
    public static final long F7 = F6 << 8;
    public static final long G7 = G6 << 8;
    public static final long H7 = H6 << 8;

    public static final long A8 = A7 << 8;
    public static final long B8 = B7 << 8;
    public static final long C8 = C7 << 8;
    public static final long D8 = D7 << 8;
    public static final long E8 = E7 << 8;
    public static final long F8 = F7 << 8;
    public static final long G8 = G7 << 8;
    public static final long H8 = H7 << 8;

    // map each square e.g A1 -> 0
    public static final long[] SQUARES_MAP = {
            A1, B1, C1, D1, E1, F1, G1, H1,
            A2, B2, C2, D2, E2, F2, G2, H2,
            A3, B3, C3, D3, E3, F3, G3, H3,
            A4, B4, C4, D4, E4, F4, G4, H4,
            A5, B5, C5, D5, E5, F5, G5, H5,
            A6, B6, C6, D6, E6, F6, G6, H6,
            A7, B7, C7, D7, E7, F7, G7, H7,
            A8, B8, C8, D8, E8, F8, G8, H8
    };

    public static final int[] SQUARES_INDEX_MAP = {
            0, 1, 2, 3, 4, 5, 6, 7,
            8, 9, 10, 11, 12, 13, 14, 15,
            16, 17, 18, 19, 20, 21, 22, 23,
            24, 25, 26, 27, 28, 29, 30, 31,
            32, 33, 34, 35, 36, 37, 38, 39,
            40, 41, 42, 43, 44, 45, 46, 47,
            48, 49, 50, 51, 52, 53, 54, 55,
            56, 57, 58, 59, 60, 61, 62, 63
    };

    // knight pre-encoded moves
    public static final long[] KNIGHT_MOVES = {
            (B3 | C2), // A1
            (C3 | D2 | A3), // B1
            (D3 | E2 | A2 | B3), // C1
            (E3 | F2 | B2 | C3), // D1
            (F3 | G2 | C2 | D3), // E1
            (G3 | H2 | D2 | E3), // F1
            (H3 | E2 | F3), // G1
            (F2 | G3), // H1

            (B4 | C3 | C1),
            (C4 | D3 | D1 | A4),
            (D4 | E3 | E1 | A1 | A3 | B4),
            (E4 | F3 | F1 | B1 | B3 | C4),
            (F4 | G3 | G1 | C1 | C3 | D4),
            (G4 | H3 | H1 | D1 | D3 | E4),
            (H4 | E1 | E3 | F4),
            (F1 | F3 | G4),

            (B5 | C4 | C2 | B1),
            (C5 | D4 | D2 | C1 | A1 | A5),
            (D5 | E4 | E2 | D1 | B1 | A2 | A4 | B5),
            (E5 | F4 | F2 | E1 | C1 | B2 | B4 | C5),
            (F5 | G4 | G2 | F1 | D1 | C2 | C4 | D5),
            (G5 | H4 | H2 | G1 | E1 | D2 | D4 | E5),
            (H5 | H1 | F1 | E2 | E4 | F5),
            (G1 | F2 | F4 | G5),

            (B6 | C5 | C3 | B2),
            (C6 | D5 | D3 | C2 | A2 | A6),
            (D6 | E5 | E3 | D2 | B2 | A3 | A5 | B6),
            (E6 | F5 | F3 | E2 | C2 | B3 | B5 | C6),
            (F6 | G5 | G3 | F2 | D2 | C3 | C5 | D6),
            (G6 | H5 | H3 | G2 | E2 | D3 | D5 | E6),
            (H6 | H2 | F2 | E3 | E5 | F6),
            (G2 | F3 | F5 | G6),

            (B7 | C6 | C4 | B3),
            (C7 | D6 | D4 | C3 | A3 | A7),
            (D7 | E6 | E4 | D3 | B3 | A4 | A6 | B7),
            (E7 | F6 | F4 | E3 | C3 | B4 | B6 | C7),
            (F7 | G6 | G4 | F3 | D3 | C4 | C6 | D7),
            (G7 | H6 | H4 | G3 | E3 | D4 | D6 | E7),
            (H7 | H3 | F3 | E4 | E6 | F7),
            (G3 | F4 | F6 | G7),

            (B8 | C7 | C5 | B4),
            (C8 | D7 | D5 | C4 | A4 | A8),
            (D8 | E7 | E5 | D4 | B4 | A5 | A7 | B8),
            (E8 | F7 | F5 | E4 | C4 | B5 | B7 | C8),
            (F8 | G7 | G5 | F4 | D4 | C5 | C7 | D8),
            (G8 | H7 | H5 | G4 | E4 | D5 | D7 | E8),
            (H8 | H4 | F4 | E5 | E7 | F8),
            (G4 | F5 | F7 | G8),

            (C8 | C6 | B5),
            (D8 | D6 | C5 | A5),
            (E8 | E6 | D5 | B5 | A6 | A8),
            (F8 | F6 | E5 | C5 | B6 | B8),
            (G8 | G6 | F5 | D5 | C6 | C8),
            (H8 | H6 | G5 | E5 | D6 | D8),
            (H5 | F5 | E6 | E8),
            (G5 | F6 | F8),

            (C7 | B6),
            (D7 | C6 | A6),
            (E7 | D6 | B6 | A7),
            (F7 | E6 | C6 | B7),
            (G7 | F6 | D6 | C7),
            (H7 | G6 | E6 | D7),
            (H6 | F6 | E7),
            (G6 | F7)
    };

    // king pre-encoded moves
    public static final long[] KING_MOVES = {
            (B1 | B2 | A2), // A1
            (A1 | C1 | C2 | A2 | B2), // B1
            (B1 | D1 | D2 | B2 | C2), // C1
            (C1 | E1 | E2 | C2 | D2), // D1
            (D1 | F1 | F2 | D2 | E2), // E1
            (E1 | G1 | G2 | E2 | F2), // F1
            (F1 | H1 | H2 | F2 | G2), // G1
            (G1 | G2 | H2), // H1

            (A1 | B1 | B3 | A3 | B2), // A2
            (A2 | C2 | A1 | C1 | B1 | C3 | A3 | B3), // B2
            (B2 | D2 | B1 | D1 | C1 | D3 | B3 | C3), // C2
            (C2 | E2 | C1 | E1 | D1 | E3 | C3 | D3), // D2
            (D2 | F2 | D1 | F1 | E1 | F3 | D3 | E3), // E2
            (E2 | G2 | E1 | G1 | F1 | G3 | E3 | F3), // F2
            (F2 | H2 | F1 | H1 | G1 | H3 | F3 | G3), // G2
            (G2 | G1 | H1 | H3 | G3), // H2

            (A2 | B2 | B4 | A4 | B3), // A3
            (A3 | C3 | A2 | C2 | B2 | C4 | A4 | B4), // B3
            (B3 | D3 | B2 | D2 | C2 | D4 | B4 | C4), // C3
            (C3 | E3 | C2 | E2 | D2 | E4 | C4 | D4), // D3
            (D3 | F3 | D2 | F2 | E2 | F4 | D4 | E4), // E3
            (E3 | G3 | E2 | G2 | F2 | G4 | E4 | F4), // F3
            (F3 | H3 | F2 | H2 | G2 | H4 | F4 | G4), // G3
            (G3 | G2 | H2 | H4 | G4), // H3

            (A3 | B3 | B5 | A5 | B4), // A4
            (A4 | C4 | A3 | C3 | B3 | C5 | A5 | B5), // B4
            (B4 | D4 | B3 | D3 | C3 | D5 | B5 | C5), // C4
            (C4 | E4 | C3 | E3 | D3 | E5 | C5 | D5), // D4
            (D4 | F4 | D3 | F3 | E3 | F5 | D5 | E5), // E4
            (E4 | G4 | E3 | G3 | F3 | G5 | E5 | F5), // F4
            (F4 | H4 | F3 | H3 | G3 | H5 | F5 | G5), // G4
            (G4 | G3 | H3 | H5 | G5), // H4

            (A4 | B4 | B6 | A6 | B5), // A5
            (A5 | C5 | A4 | C4 | B4 | C6 | A6 | B6), // B5
            (B5 | D5 | B4 | D4 | C4 | D6 | B6 | C6), // C5
            (C5 | E5 | C4 | E4 | D4 | E6 | C6 | D6), // D5
            (D5 | F5 | D4 | F4 | E4 | F6 | D6 | E6), // E5
            (E5 | G5 | E4 | G4 | F4 | G6 | E6 | F6), // F5
            (F5 | H5 | F4 | H4 | G4 | H6 | F6 | G6), // G5
            (G5 | G4 | H4 | H6 | G6), // H5

            (A5 | B5 | B7 | A7 | B6), // A6
            (A6 | C6 | A5 | C5 | B5 | C7 | A7 | B7), // B6
            (B6 | D6 | B5 | D5 | C5 | D7 | B7 | C7), // C6
            (C6 | E6 | C5 | E5 | D5 | E7 | C7 | D7), // D6
            (D6 | F6 | D5 | F5 | E5 | F7 | D7 | E7), // E6
            (E6 | G6 | E5 | G5 | F5 | G7 | E7 | F7), // F6
            (F6 | H6 | F5 | H5 | G5 | H7 | F7 | G7), // G6
            (G6 | G5 | H5 | H7 | G7), // H6

            (A6 | B6 | B8 | A8 | B7), // A7
            (A7 | C7 | A6 | C6 | B6 | C8 | A8 | B8), // B7
            (B7 | D7 | B6 | D6 | C6 | D8 | B8 | C8), // C7
            (C7 | E7 | C6 | E6 | D6 | E8 | C8 | D8), // D7
            (D7 | F7 | D6 | F6 | E6 | F8 | D8 | E8), // E7
            (E7 | G7 | E6 | G6 | F6 | G8 | E8 | F8), // F7
            (F7 | H7 | F6 | H6 | G6 | H8 | F8 | G8), // G7
            (G7 | G6 | H6 | H8 | G8), // H7

            (A7 | B7 | B8), // A8
            (A8 | C8 | A7 | C7 | B7), // B8
            (B8 | D8 | B7 | D7 | C7), // C8
            (C8 | E8 | C7 | E7 | D7), // D8
            (D8 | F8 | D7 | F7 | E7), // E8
            (E8 | G8 | E7 | G7 | F7), // F8
            (F8 | H8 | F7 | H7 | G7), // G8
            (G8 | G7 | H7), // H8
            (0L)
    };

    private static final int[] PAWN_TABLE_MG = {
            0,    0,    0,    0,    0,    0,    0,    0,
           -11,   34,  126,   68,   95,   61,  134,   98,
           -20,   25,   56,   65,   31,   26,    7,   -6,
           -23,   17,   12,   23,   21,    6,   13,  -14,
           -25,   10,    6,   17,   18,   -5,   -2,  -27,
           -12,   33,    3,    3,  -10,   -4,   -4,  -26,
           -22,   38,   24,  -15,  -23,  -20,   -1,  -35,
             0,    0,    0,    0,    0,    0,    0,    0,
    };

    private static final int[] PAWN_TABLE_EG = {
            0, 0, 0, 0, 0, 0, 0, 0,
            178, 173, 158, 134, 147, 132, 165, 187,
            94, 100, 85, 67, 56, 53, 82, 84,
            32, 24, 13, 5, -2, 4, 17, 17,
            13, 9, -3, -7, -7, -8, 3, -1,
            4, 7, -6, 1, 0, -5, -1, -8,
            13, 8, 8, 10, 13, 0, 2, -7,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    private static final int[] KNIGHT_TABLE_MG = {
            -167, -89, -34, -49, 61, -97, -15, -107,
            -73, -41, 72, 36, 23, 62, 7, -17,
            -47, 60, 37, 65, 84, 129, 73, 44,
            -9, 17, 19, 53, 37, 69, 18, 22,
            -13, 4, 16, 13, 28, 19, 21, -8,
            -23, -9, 12, 10, 19, 17, 25, -16,
            -29, -53, -12, -3, -1, 18, -14, -19,
            -105, -21, -58, -33, -17, -28, -19, -23,
    };

    private static final int[] KNIGHT_TABLE_EG = {
            -58, -38, -13, -28, -31, -27, -63, -99,
            -25, -8, -25, -2, -9, -25, -24, -52,
            -24, -20, 10, 9, -1, -9, -19, -41,
            -17, 3, 22, 22, 22, 11, 8, -18,
            -18, -6, 16, 25, 16, 17, 4, -18,
            -23, -3, -1, 15, 10, -3, -20, -22,
            -42, -20, -10, -5, -2, -20, -23, -44,
            -29, -51, -23, -15, -22, -18, -50, -64,
    };

    private static final int[] BISHOP_TABLE_EG = {
            -14, -21, -11, -8, -7, -9, -17, -24,
            -8, -4, 7, -12, -3, -13, -4, -14,
            2, -8, 0, -1, -2, 6, 0, 4,
            -3, 9, 12, 9, 14, 10, 3, 2,
            -6, 3, 13, 19, 7, 10, -3, -9,
            -12, -3, 8, 10, 13, 3, -7, -15,
            -14, -18, -7, -1, 4, -9, -15, -27,
            -23, -9, -23, -5, -9, -16, -5, -17,
    };

    private static final int[] ROOK_TABLE_EG = {
            13, 10, 18, 15, 12, 12, 8, 5,
            11, 13, 13, 11, -3, 3, 8, 3,
            7, 7, 7, 5, 4, -3, -5, -3,
            4, 3, 13, 1, 2, 1, -1, 2,
            3, 5, 8, 4, -5, -6, -8, -11,
            -4, 0, -5, -1, -7, -12, -8, -16,
            -6, -6, 0, 2, -9, -9, -11, -3,
            -9, 2, 3, -1, -5, -13, 4, -20,
    };

    private static final int[] QUEEN_TABLE_EG = {
            -9, 22, 22, 27, 27, 19, 10, 20,
            -17, 20, 32, 41, 58, 25, 30, 0,
            -20, 6, 9, 49, 47, 35, 19, 9,
            3, 22, 24, 45, 57, 40, 57, 36,
            -18, 28, 19, 47, 31, 34, 39, 23,
            -16, -27, 15, 6, 9, 17, 10, 5,
            -22, -23, -30, -16, -16, -23, -36, -32,
            -33, -28, -22, -43, -5, -32, -20, -41,
    };

    private static final int[] KING_END_GAME_TABLE_EG = {
            -74, -35, -18, -18, -11, 15, 4, -17,
            -12, 17, 14, 17, 17, 38, 23, 11,
            10, 17, 23, 15, 20, 45, 44, 13,
            -8, 22, 24, 27, 26, 33, 26, 3,
            -18, -4, 21, 24, 27, 23, 9, -11,
            -19, -3, 11, 21, 23, 16, 7, -9,
            -27, -11, 4, 13, 14, 4, -5, -17,
            -53, -34, -21, -11, -28, -14, -24, -43
    };

    private static final int[] BISHOP_TABLE_MG = {
            -29, 4, -82, -37, -25, -42, 7, -8,
            -26, 16, -18, -13, 30, 59, 18, -47,
            -16, 37, 43, 40, 35, 50, 37, -2,
            -4, 5, 19, 50, 37, 37, 7, -2,
            -6, 13, 13, 26, 34, 12, 10, 4,
            0, 15, 15, 15, 14, 27, 18, 10,
            4, 15, 16, 0, 7, 21, 33, 1,
            -33, -3, -14, -21, -13, -12, -39, -21,
    };

    private static final int[] ROOK_TABLE_MG = {
            32, 42, 32, 51, 63, 9, 31, 43,
            27, 32, 58, 62, 80, 67, 26, 44,
            -5, 19, 26, 36, 17, 45, 61, 16,
            -24, -11, 7, 26, 24, 35, -8, -20,
            -36, -26, -12, -1, 9, -7, 6, -23,
            -45, -25, -16, -17, 3, 0, -5, -33,
            -44, -16, -20, -9, -1, 11, -6, -71,
            -19, -13, 1, 17, 16, 7, -37, -26,
    };

    private static final int[] QUEEN_TABLE_MG = {
            -28, 0, 29, 12, 59, 44, 43, 45,
            -24, -39, -5, 1, -16, 57, 28, 54,
            -13, -17, 7, 8, 29, 56, 47, 57,
            -27, -27, -16, -16, -1, 17, -2, 1,
            -9, -26, -9, -10, -2, -4, 3, -3,
            -14, 2, -11, -2, -5, 2, 14, 5,
            -35, -8, 11, 2, 8, 15, -3, 1,
            -1, -18, -9, 10, -15, -25, -31, -50,
    };

    private static final int[] KING_MIDDLE_GAME_TABLE_MG = {
            -65, 23, 16, -15, -56, -34, 2, 13,
            29, -1, -20, -7, -8, -4, -38, -29,
            -9, 24, 2, -16, -20, 6, 22, -22,
            -17, -20, -12, -27, -30, -25, -14, -36,
            -49, -1, -27, -39, -46, -44, -33, -51,
            -14, -14, -22, -46, -44, -30, -15, -27,
            1, 7, -8, -64, -43, -16, 9, 8,
            -15, 36, 12, -54, 8, -28, 24, 14,
    };

    // valid
    public static final long WHITE_KING_SIDE_CASTLE_KING_SQUARE = G1;
    public static final long BLACK_KING_SIDE_CASTLE_KING_SQUARE = G8;

    // valid
    public static final long WHITE_QUEEN_SIDE_CASTLE_KING_SQUARE = C1;
    public static final long BLACK_QUEEN_SIDE_CASTLE_KING_SQUARE = C8;

    // valid
    public static final long WHITE_KING_SIDE_ROOK_SQUARE = H1;
    public static final long WHITE_QUEEN_SIDE_ROOK_SQUARE = A1;

    // valid
    public static final long BLACK_KING_SIDE_ROOK_SQUARE = H8;
    public static final long BLACK_QUEEN_SIDE_ROOK_SQUARE = A8;

    // valid
    public static final long WHITE_KING_SIDE_CASTLE_EMPTY_SQUARES_MASK = F1 | G1;
    public static final long WHITE_QUEEN_SIDE_CASTLE_EMPTY_SQUARES_MASK = B1 | C1 | D1;

    // valid
    public static final long BLACK_KING_SIDE_CASTLE_EMPTY_SQUARES_MASK = F8 | G8;
    public static final long BLACK_QUEEN_SIDE_CASTLE_EMPTY_SQUARES_MASK = B8 | C8 | D8;

    // valid
    public static final long WHITE_KING_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK = G1 | F1 | E1;
    public static final long WHITE_QUEEN_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK = D1 | C1 | E1;

    // valid
    public static final long BLACK_KING_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK = G8 | F8 | E8;
    public static final long BLACK_QUEEN_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK = D8 | C8 | E8;

    public static final int EMPTY = 0;
    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;

    public static final int WHITE_PAWN = 0;
    public static final int WHITE_KNIGHT = 1;
    public static final int WHITE_BISHOP = 2;
    public static final int WHITE_ROOK = 3;
    public static final int WHITE_QUEEN = 4;
    public static final int WHITE_KING = 5;
    public static final int BLACK_PAWN = 6;
    public static final int BLACK_KNIGHT = 7;
    public static final int BLACK_BISHOP = 8;
    public static final int BLACK_ROOK = 9;
    public static final int BLACK_QUEEN = 10;
    public static final int BLACK_KING = 11;

    public static final int PAWN_SCORE = 100;
    public static final int KNIGHT_SCORE = 320;
    public static final int BISHOP_SCORE = 330;
    public static final int ROOK_SCORE = 500;
    public static final int QUEEN_SCORE = 900;
    public static final int KING_SCORE = 20000;

    public final BoardHistoryStack history = new BoardHistoryStack(256); // profondeur max

    public static final String INITIAL_STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public BitBoard() {

        whiteTurn = true;

        // Initialisation des pièces à leurs positions de départ
        whitePawns = A2 | B2 | C2 | D2 | E2 | F2 | G2 | H2;
        whiteKnights = B1 | G1;
        whiteBishops = C1 | F1;
        whiteRooks = A1 | H1;
        whiteQueens = D1;
        whiteKing = E1;

        blackPawns = A7 | B7 | C7 | D7 | E7 | F7 | G7 | H7;
        blackKnights = B8 | G8;
        blackBishops = C8 | F8;
        blackRooks = A8 | H8;
        blackQueens = D8;
        blackKing = E8;

        // printBitBoard(whitePawns);
        // printBitBoard(whiteKnights);
        // printBitBoard(whiteBishops);
        // printBitBoard(whiteRooks);
        // printBitBoard(whiteQueens);
        // printBitBoard(whiteKing);

        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;

        bitboard = whitePieces | blackPieces;

        whiteCastleQueenSide = 1L;
        whiteCastleKingSide = 1L;
        blackCastleQueenSide = 1L;
        blackCastleKingSide = 1L;

        enPassantSquare = 0L;

        plyCount = 0;

    }

    private void saveBoardHistory(long move) {
        history.push(this, move);
    }

    public boolean isThreefoldRepetition() {
        // to check for repetition, we need to check if the current position has been seen (we will check in the history stack if it appears 3 times)
        if (history.isEmpty() || plyCount < 8) {
            return false;
        }
        else{
            // If the position before the last move is the same as the current position, we can return true
            if ((history.stack[plyCount - 4].zobristKey == this.zobristKey) && (history.stack[plyCount - 8].zobristKey == this.zobristKey)) {
                return true;
            }

            return false;
        }
    }

    public void loadFromFen(String fen) {
        String[] fenParts = fen.split(" ");
        String[] rows = fenParts[0].split("/");
        int row = 0;
        int col = 0;

        whitePawns = 0;
        whiteKnights = 0;
        whiteBishops = 0;
        whiteRooks = 0;
        whiteQueens = 0;
        whiteKing = 0;

        blackPawns = 0;
        blackKnights = 0;
        blackBishops = 0;
        blackRooks = 0;
        blackQueens = 0;
        blackKing = 0;

        whitePieces = 0;
        blackPieces = 0;

        whiteCastleQueenSide = 1L;
        whiteCastleKingSide = 1L;
        blackCastleQueenSide = 1L;
        blackCastleKingSide = 1L;

        enPassantSquare = 0L;

        plyCount = 0;

        for (String r : rows) {
            for (int i = 0; i < r.length(); i++) {
                char c = r.charAt(i);
                if (Character.isDigit(c)) {
                    col += Character.getNumericValue(c);
                } else {
                    long bitboard = 1L << 63 - (row * 8 + (7 - col));
                    switch (c) {
                        case 'P':
                            whitePawns |= bitboard;
                            break;
                        case 'N':
                            whiteKnights |= bitboard;
                            break;
                        case 'B':
                            whiteBishops |= bitboard;
                            break;
                        case 'R':
                            whiteRooks |= bitboard;
                            break;
                        case 'Q':
                            whiteQueens |= bitboard;
                            break;
                        case 'K':
                            whiteKing |= bitboard;
                            break;
                        case 'p':
                            blackPawns |= bitboard;
                            break;
                        case 'n':
                            blackKnights |= bitboard;
                            break;
                        case 'b':
                            blackBishops |= bitboard;
                            break;
                        case 'r':
                            blackRooks |= bitboard;
                            break;
                        case 'q':
                            blackQueens |= bitboard;
                            break;
                        case 'k':
                            blackKing |= bitboard;
                            break;
                    }
                    col++;
                }
            }
            row++;
            col = 0;
        }

        // turn
        whiteTurn = fenParts[1].equals("w");

        // castling rights
        if (fenParts[2].contains("K")) {
            // verify that the king has not moved and the rook is on H1
            if ((whiteKing & E1) != 0 && (whiteRooks & H1) != 0) {
                whiteCastleKingSide = 1L;
            } else {
                whiteCastleKingSide = 0L;
            }
        } else {
            whiteCastleKingSide = 0L;
        }

        if (fenParts[2].contains("Q")) {
            // verify that the king has not moved and the rook is on A1
            if ((whiteKing & E1) != 0 && (whiteRooks & A1) != 0) {
                whiteCastleQueenSide = 1L;
            } else {
                whiteCastleQueenSide = 0L;
            }
        } else {
            whiteCastleQueenSide = 0L;
        }

        if (fenParts[2].contains("k")) {
            // verify that the king has not moved and the rook is on H8
            if ((blackKing & E8) != 0 && (blackRooks & H8) != 0) {
                blackCastleKingSide = 1L;
            } else {
                blackCastleKingSide = 0L;
            }
        } else {
            blackCastleKingSide = 0L;
        }

        if (fenParts[2].contains("q")) {
            // verify that the king has not moved and the rook is on A8
            if ((blackKing & E8) != 0 && (blackRooks & A8) != 0) {
                blackCastleQueenSide = 1L;
            } else {
                blackCastleQueenSide = 0L;
            }
        } else {
            blackCastleQueenSide = 0L;
        }

        // en passant square
        if (!fenParts[3].equals("-")) {
            System.out.println(fenParts[3]);
            int file = fenParts[3].charAt(0) - 'a';
            int rank = fenParts[3].charAt(1) - '1';
            enPassantSquare = 1L << (rank * 8 + file);
        } else {
            enPassantSquare = 0L;
        }

        updateBitBoard();

        // === EVAL ===

        // Reset
        currentEvalMG = 0;
        currentEvalEG = 0;
        phase = 0;

        // Blancs
        currentEvalMG += Long.bitCount(whitePawns) * 100;
        currentEvalEG += Long.bitCount(whitePawns) * 100;

        currentEvalMG += Long.bitCount(whiteKnights) * 320;
        currentEvalEG += Long.bitCount(whiteKnights) * 310;
        phase += Long.bitCount(whiteKnights) * 1;

        currentEvalMG += Long.bitCount(whiteBishops) * 330;
        currentEvalEG += Long.bitCount(whiteBishops) * 320;
        phase += Long.bitCount(whiteBishops) * 1;

        currentEvalMG += Long.bitCount(whiteRooks) * 500;
        currentEvalEG += Long.bitCount(whiteRooks) * 510;
        phase += Long.bitCount(whiteRooks) * 2;

        currentEvalMG += Long.bitCount(whiteQueens) * 900;
        currentEvalEG += Long.bitCount(whiteQueens) * 950;
        phase += Long.bitCount(whiteQueens) * 4;

        // Noirs
        currentEvalMG -= Long.bitCount(blackPawns) * 100;
        currentEvalEG -= Long.bitCount(blackPawns) * 100;

        currentEvalMG -= Long.bitCount(blackKnights) * 320;
        currentEvalEG -= Long.bitCount(blackKnights) * 310;
        phase += Long.bitCount(blackKnights) * 1;

        currentEvalMG -= Long.bitCount(blackBishops) * 330;
        currentEvalEG -= Long.bitCount(blackBishops) * 320;
        phase += Long.bitCount(blackBishops) * 1;

        currentEvalMG -= Long.bitCount(blackRooks) * 500;
        currentEvalEG -= Long.bitCount(blackRooks) * 510;
        phase += Long.bitCount(blackRooks) * 2;

        currentEvalMG -= Long.bitCount(blackQueens) * 900;
        currentEvalEG -= Long.bitCount(blackQueens) * 950;
        phase += Long.bitCount(blackQueens) * 4;

        // Clamp phase
        phase = Math.min(phase, 24);

        // PSQT
        long wPawns = whitePawns;
        long wKnights = whiteKnights;
        long wBishops = whiteBishops;
        long wRooks = whiteRooks;
        long wQueens = whiteQueens;
        long wKing = whiteKing;
        long bPawns = blackPawns;
        long bKnights = blackKnights;
        long bBishops = blackBishops;
        long bRooks = blackRooks;
        long bQueens = blackQueens;
        long bKing = blackKing;

        // white pawns
        while (wPawns != 0) {
            int square = Long.numberOfTrailingZeros(wPawns);
            currentEvalMG += PAWN_TABLE_MG[square ^ 56];
            currentEvalEG += PAWN_TABLE_EG[square ^ 56];
            wPawns &= wPawns - 1;
        }
        // black pawns
        while (bPawns != 0) {
            int square = Long.numberOfTrailingZeros(bPawns);
            currentEvalMG -= PAWN_TABLE_MG[square];
            currentEvalEG -= PAWN_TABLE_EG[square];
            bPawns &= bPawns - 1;
        }
        // white knights
        while (wKnights != 0) {
            int square = Long.numberOfTrailingZeros(wKnights);
            currentEvalMG += KNIGHT_TABLE_MG[square ^ 56];
            currentEvalEG += KNIGHT_TABLE_EG[square ^ 56];
            wKnights &= wKnights - 1;
        }
        // black knights

        while (bKnights != 0) {
            int square = Long.numberOfTrailingZeros(bKnights);
            currentEvalMG -= KNIGHT_TABLE_MG[square];
            currentEvalEG -= KNIGHT_TABLE_EG[square];
            bKnights &= bKnights - 1;
        }
        // white bishops
        while (wBishops != 0) {
            int square = Long.numberOfTrailingZeros(wBishops);
            currentEvalMG += BISHOP_TABLE_MG[square ^ 56];
            currentEvalEG += BISHOP_TABLE_EG[square ^ 56];
            wBishops &= wBishops - 1;
        }
        // black bishops
        while (bBishops != 0) {
            int square = Long.numberOfTrailingZeros(bBishops);
            currentEvalMG -= BISHOP_TABLE_MG[square];
            currentEvalEG -= BISHOP_TABLE_EG[square];
            bBishops &= bBishops - 1;
        }
        // white rooks
        while (wRooks != 0) {
            int square = Long.numberOfTrailingZeros(wRooks);
            currentEvalMG += ROOK_TABLE_MG[square ^ 56];
            currentEvalEG += ROOK_TABLE_EG[square ^ 56];
            wRooks &= wRooks - 1;
        }
        // black rooks
        while (bRooks != 0) {
            int square = Long.numberOfTrailingZeros(bRooks);
            currentEvalMG -= ROOK_TABLE_MG[square];
            currentEvalEG -= ROOK_TABLE_EG[square];
            bRooks &= bRooks - 1;
        }
        // white queens
        while (wQueens != 0) {
            int square = Long.numberOfTrailingZeros(wQueens);
            currentEvalMG += QUEEN_TABLE_MG[square ^ 56];
            currentEvalEG += QUEEN_TABLE_EG[square ^ 56];
            wQueens &= wQueens - 1;
        }
        // black queens
        while (bQueens != 0) {
            int square = Long.numberOfTrailingZeros(bQueens);
            currentEvalMG -= QUEEN_TABLE_MG[square];
            currentEvalEG -= QUEEN_TABLE_EG[square];
            bQueens &= bQueens - 1;
        }
        // white king
        while (wKing != 0) {
            int square = Long.numberOfTrailingZeros(wKing);
            currentEvalMG += KING_MIDDLE_GAME_TABLE_MG[square ^ 56];
            currentEvalEG += KING_END_GAME_TABLE_EG[square ^ 56];
            wKing &= wKing - 1;
        }
        // black king
        while (bKing != 0) {
            int square = Long.numberOfTrailingZeros(bKing);
            currentEvalMG -= KING_MIDDLE_GAME_TABLE_MG[square];
            currentEvalEG -= KING_END_GAME_TABLE_EG[square];
            bKing &= bKing - 1;
        }

        // === Zobrist ===
        this.zobristKey = generateZobristKey();
    }

    public final long generateZobristKey() {
        // On hash la position actuelle
        long zobristKey = 0L;

        // On commence par hash toutes les pieces et leur positions
        for (int square = 0; square < 64; square++) {
            int piece = getPiece(square);
            if (piece != BitBoard.EMPTY) {
                zobristKey ^= Zobrist.PIECE_KEYS[piece - 1][square]; // piece - 1 pour l'acces au tableau
            }
        }

        // Side to move
        if(whiteTurn) zobristKey ^= Zobrist.SIDE_TO_MOVE_KEY;

        // roque
        zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];

        // en passant (hash only if there is en passant)
        if (enPassantSquare != 0L) {
            zobristKey ^= Zobrist.EN_PASSANT_KEYS[Long.numberOfTrailingZeros(this.enPassantSquare)];
        }

        return zobristKey;
    }

    public final int calculatePhase() {
        int phase = 0;

        // Blancs
        phase += Long.bitCount(whiteKnights) * 1;
        phase += Long.bitCount(whiteBishops) * 1;
        phase += Long.bitCount(whiteRooks) * 2;
        phase += Long.bitCount(whiteQueens) * 4;

        // Noirs
        phase += Long.bitCount(blackKnights) * 1;
        phase += Long.bitCount(blackBishops) * 1;
        phase += Long.bitCount(blackRooks) * 2;
        phase += Long.bitCount(blackQueens) * 4;

        // Clamp entre 0 et 24
        return Math.min(phase, 24);
    }

    public final int evaluate() {
        if (isThreefoldRepetition()) {
            return 0;
        }
        return (currentEvalMG * phase + currentEvalEG * (24 - phase)) / 24;
    }

    public String getFen() {
        StringBuilder fen = new StringBuilder();
        for (int i = 0; i < 64; i += 8) {
            int empty = 0;
            for (int j = 0; j < 8; j++) {
                long bitboard = 1L << 63 - (i + (7 - j));
                if ((whitePawns & bitboard) != 0) {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append("P");
                } else if ((whiteKnights & bitboard) != 0) {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append("N");
                } else if ((whiteBishops & bitboard) != 0) {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append("B");
                } else if ((whiteRooks & bitboard) != 0) {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append("R");
                } else if ((whiteQueens & bitboard) != 0) {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append("Q");
                } else if ((whiteKing & bitboard) != 0) {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append("K");
                } else if ((blackPawns & bitboard) != 0) {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append("p");
                } else if ((blackKnights & bitboard) != 0) {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append("n");
                } else if ((blackBishops & bitboard) != 0) {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append("b");
                } else if ((blackRooks & bitboard) != 0) {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append("r");
                } else if ((blackQueens & bitboard) != 0) {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append("q");
                } else if ((blackKing & bitboard) != 0) {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append("k");
                } else {
                    empty++;
                }
            }
            if (empty > 0) {
                fen.append(empty);
            }
            if (i < 56) {
                fen.append("/");
            }
        }

        fen.append(" ");
        fen.append(whiteTurn ? "w" : "b");
        fen.append(" ");
        if (whiteCastleKingSide == 1L) {
            fen.append("K");
        }
        if (whiteCastleQueenSide == 1L) {
            fen.append("Q");
        }
        if (blackCastleKingSide == 1L) {
            fen.append("k");
        }
        if (blackCastleQueenSide == 1L) {
            fen.append("q");
        }
        if (whiteCastleKingSide == 0L && whiteCastleQueenSide == 0L && blackCastleKingSide == 0L
                && blackCastleQueenSide == 0L) {
            fen.append("-");
        }
        fen.append(" ");
        if (enPassantSquare != 0L) {
            for (int i = 0; i < 64; i++) {
                if ((enPassantSquare == SQUARES_MAP[i])) {
                    fen.append((char) ('a' + (i % 8)));
                    fen.append((char) ('1' + (i / 8)));
                }
            }
        } else {
            fen.append("-");
        }
        fen.append(" ");
        fen.append("0");
        fen.append(" ");
        fen.append("1");

        return fen.toString();
    }

    public void printBitBoard(long bitBoard) {
        PrintWriter writer = new PrintWriter(System.out);

        // Bordure supérieure
        writer.println("   +-------------------------------+");
        writer.println("   | a   b   c   d   e   f   g   h |");
        writer.println("   +-------------------------------+");

        // Parcourir les rangées de haut en bas
        for (int rank = 7; rank >= 0; rank--) {
            writer.print((rank + 1) + "  |"); // Numéro de rangée sur le côté gauche

            // Parcourir chaque colonne de la rangée
            for (int file = 0; file < 8; file++) {
                int squareIndex = rank * 8 + file;

                long mask = 1L << squareIndex;

                if ((bitBoard & mask) != 0) {
                    writer.print(" 1 ");
                } else {
                    writer.print("   ");
                }

                // Ajouter un séparateur "|"
                if (file != 7) {
                    writer.print("|");
                }
            }

            writer.println("| " + (rank + 1)); // Numéro de rangée sur le côté droit

            // Ajouter des séparateurs entre les rangées sauf pour la dernière
            if (rank > 0) {
                writer.println("   |---|---|---|---|---|---|---|---|");
            }
        }

        // Bordure inférieure
        writer.println("   +-------------------------------+");
        writer.println("   | a   b   c   d   e   f   g   h |");
        writer.println("   +-------------------------------+");

        writer.flush();
    }

    public void printChessBoard() {
        String[] pieces = { "P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k" };
        String[] board = new String[64];

        // Remplir le tableau board avec les pièces ou des points pour les cases vides
        for (int i = 0; i < 64; i++) {
            long bitboard = 1L << i;
            if ((whitePawns & bitboard) != 0) {
                board[i] = pieces[0];
            } else if ((whiteKnights & bitboard) != 0) {
                board[i] = pieces[1];
            } else if ((whiteBishops & bitboard) != 0) {
                board[i] = pieces[2];
            } else if ((whiteRooks & bitboard) != 0) {
                board[i] = pieces[3];
            } else if ((whiteQueens & bitboard) != 0) {
                board[i] = pieces[4];
            } else if ((whiteKing & bitboard) != 0) {
                board[i] = pieces[5];
            } else if ((blackPawns & bitboard) != 0) {
                board[i] = pieces[6];
            } else if ((blackKnights & bitboard) != 0) {
                board[i] = pieces[7];
            } else if ((blackBishops & bitboard) != 0) {
                board[i] = pieces[8];
            } else if ((blackRooks & bitboard) != 0) {
                board[i] = pieces[9];
            } else if ((blackQueens & bitboard) != 0) {
                board[i] = pieces[10];
            } else if ((blackKing & bitboard) != 0) {
                board[i] = pieces[11];
            } else {
                board[i] = " ";
            }
        }

        // Bordure supérieure
        System.out.println("   +-------------------------------+");
        System.out.println("   | a   b   c   d   e   f   g   h |");
        System.out.println("   +-------------------------------+");

        // Parcourir les rangées de haut en bas
        for (int rank = 7; rank >= 0; rank--) {
            System.out.print((rank + 1) + "  |"); // Numéro de rangée sur le côté gauche

            // Parcourir chaque colonne de la rangée
            for (int file = 7; file >= 0; file--) {
                int squareIndex = rank * 8 + (7 - file);
                System.out.print(" " + board[squareIndex] + " ");

                // Ajouter un séparateur "|"
                if (file != 0) {
                    System.out.print("|");
                }
            }

            // Numéro de rangée sur le côté droit
            System.out.println("| " + (rank + 1));

            // Ajouter des séparateurs entre les rangées sauf pour la dernière
            if (rank > 0) {
                System.out.println("   |---|---|---|---|---|---|---|---|");
            }
        }

        // Bordure inférieure
        System.out.println("   +-------------------------------+");
        System.out.println("   | a   b   c   d   e   f   g   h |");
        System.out.println("   +-------------------------------+");

        System.out.println();
        System.out.println("     " + (whiteTurn ? "White" : "Black") + "'s turn");
        System.out.println("     " + getFen());
        System.out.println("    Key: " + Long.toHexString(this.zobristKey));
        System.out.println("    Key generated: " + Long.toHexString(generateZobristKey()));
        System.out.println("     Eval: " + evaluate());
    }

    // Get bitboard for a square
    public long getSquareBitboard(String square) {
        int index = getSquare(square);
        return 1L << index;
    }

    // Get the bitboard
    public long getBoard() {
        return bitboard;
    }

    // Get the white pieces bitboard
    public long getWhitePieces() {
        return whitePieces;
    }

    // Get the black pieces bitboard
    public long getBlackPieces() {
        return blackPieces;
    }

    // Get the white pawns bitboard
    public long getWhitePawns() {
        return whitePawns;
    }

    // Get the white knights bitboard
    public long getWhiteKnights() {
        return whiteKnights;
    }

    // Get the white bishops bitboard
    public long getWhiteBishops() {
        return whiteBishops;
    }

    // Get the white rooks bitboard
    public long getWhiteRooks() {
        return whiteRooks;
    }

    // Get the white queens bitboard
    public long getWhiteQueens() {
        return whiteQueens;
    }

    // Get the white king bitboard
    public long getWhiteKing() {
        return whiteKing;
    }

    // Get the black pawns bitboard
    public long getBlackPawns() {
        return blackPawns;
    }

    // Get the black knights bitboard
    public long getBlackKnights() {
        return blackKnights;
    }

    // Get the black bishops bitboard
    public long getBlackBishops() {
        return blackBishops;
    }

    // Get the black rooks bitboard
    public long getBlackRooks() {
        return blackRooks;
    }

    // Get the black queens bitboard
    public long getBlackQueens() {
        return blackQueens;
    }

    // Get the black king bitboard
    public long getBlackKing() {
        return blackKing;
    }

    public void makeNullMove() {
        makeMove(0L);
    }

    public void undoNullMove() {
        
    }

    public final void makeMove(long move) {
        // Save the current board state
        saveBoardHistory(move);
        // saveBoardHistoryLONG();

        // Convert squares to bitboards
        final long fromBitboard = 1L << PackedMove.getFrom(move);
        final long toBitboard = 1L << PackedMove.getTo(move);

        final int squareFrom;
        final int squareTo;

        // Separate logic for white and black moves
        if (whiteTurn) {

            squareFrom = PackedMove.getFrom(move);
            squareTo = PackedMove.getTo(move);

            handleCaptureWhite(toBitboard);
            // Pions blancs
            if ((whitePawns & fromBitboard) != 0) {

                // Handle en passant capture
                if (PackedMove.getFlags(move) == Move.EN_PASSENT) {
                    final long capturedPawn = enPassantSquare >> 8;
                    blackPawns &= ~capturedPawn;

                    this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_PAWN][Long.numberOfTrailingZeros(capturedPawn)];

                    // Move pawn
                    whitePawns &= ~fromBitboard;
                    whitePawns |= toBitboard;

                    // Update evaluations for both phases
                    currentEvalMG += PAWN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                            - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                    currentEvalEG += PAWN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                            - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];

                    // === Update ZobristKey ===
                    // Remove
                    this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_PAWN][squareFrom];
                    // Add
                    this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_PAWN][squareTo];
                }

                // Handle double pawn push
                else if (PackedMove.getFlags(move) == Move.DOUBLE_PAWN_PUSH) {
                    if (enPassantSquare != 0) {
                        this.zobristKey ^= Zobrist.EN_PASSANT_KEYS[Long.numberOfTrailingZeros(enPassantSquare)];
                    }
                    this.enPassantSquare = toBitboard >> 8;

                    // Move pawn
                    whitePawns &= ~fromBitboard;
                    whitePawns |= toBitboard;

                    // Update evaluations for both phases
                    currentEvalMG += PAWN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                            - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                    currentEvalEG += PAWN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                            - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];

                    // Update ZobristKey
                    this.zobristKey ^= Zobrist.EN_PASSANT_KEYS[Long.numberOfTrailingZeros(this.enPassantSquare)];

                    // === Update ZobristKey ===
                    // Remove
                    this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_PAWN][squareFrom];
                    // Add
                    this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_PAWN][squareTo];
                    
                } else {
                    if (enPassantSquare != 0) {
                        this.zobristKey ^= Zobrist.EN_PASSANT_KEYS[Long.numberOfTrailingZeros(enPassantSquare)];
                    }
                    enPassantSquare = 0L; // Reset en passant square

                    // Handle promotion
                    if (PackedMove.getFlags(move) == Move.PROMOTION) {
                        switch (PackedMove.getPromotion(move)) {
                            case KNIGHT:
                                // Update evaluations for promotion to knight
                                currentEvalMG += KNIGHT_SCORE
                                        + KNIGHT_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56] - PAWN_SCORE
                                        - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                                currentEvalEG += KNIGHT_SCORE
                                        + KNIGHT_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56] - PAWN_SCORE
                                        - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                                phase += 1; // Knights add 1 to phase
                                whiteKnights |= toBitboard;
                                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_PAWN][squareFrom];
                                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_KNIGHT][squareTo];
                                break;
                            case BISHOP:
                                // Update evaluations for promotion to bishop
                                currentEvalMG += BISHOP_SCORE
                                        + BISHOP_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56] - PAWN_SCORE
                                        - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                                currentEvalEG += BISHOP_SCORE
                                        + BISHOP_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56] - PAWN_SCORE
                                        - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                                phase += 1; // Bishops add 1 to phase
                                whiteBishops |= toBitboard;
                                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_PAWN][squareFrom];
                                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_BISHOP][squareTo];
                                break;
                            case ROOK:
                                // Update evaluations for promotion to rook
                                currentEvalMG += ROOK_SCORE + ROOK_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                                        - PAWN_SCORE - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                                currentEvalEG += ROOK_SCORE + ROOK_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                                        - PAWN_SCORE - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                                phase += 2; // Rooks add 2 to phase
                                whiteRooks |= toBitboard;
                                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_PAWN][squareFrom];
                                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_ROOK][squareTo];
                                break;
                            case QUEEN:
                                // Update evaluations for promotion to queen
                                currentEvalMG += QUEEN_SCORE
                                        + QUEEN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56] - PAWN_SCORE
                                        - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                                currentEvalEG += QUEEN_SCORE
                                        + QUEEN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56] - PAWN_SCORE
                                        - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                                phase += 4; // Queens add 4 to phase
                                whiteQueens |= toBitboard;
                                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_PAWN][squareFrom];
                                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_QUEEN][squareTo];
                                break;
                        }

                        // remove pawn
                        whitePawns &= ~fromBitboard;

                    } else {
                        // Move pawn
                        whitePawns &= ~fromBitboard;
                        whitePawns |= toBitboard;

                        // Update evaluations for both phases
                        currentEvalMG += PAWN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                                - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                        currentEvalEG += PAWN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                                - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];

                        // === Update ZobristKey ===
                        // Remove
                        this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_PAWN][squareFrom];
                        // Add
                        this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_PAWN][squareTo];
                    }

                }

                // Cavaliers blancs
            } else if ((whiteKnights & fromBitboard) != 0) {
                whiteKnights &= ~fromBitboard;
                whiteKnights |= toBitboard;

                // Update evaluations for both phases
                currentEvalMG += KNIGHT_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                        - KNIGHT_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                currentEvalEG += KNIGHT_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                        - KNIGHT_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];

                // Update ZobristKey
                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_KNIGHT][squareFrom];
                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_KNIGHT][squareTo];

                // Fous blancs
            } else if ((whiteBishops & fromBitboard) != 0) {
                whiteBishops &= ~fromBitboard;
                whiteBishops |= toBitboard;

                // Update evaluations for both phases
                currentEvalMG += BISHOP_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                        - BISHOP_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                currentEvalEG += BISHOP_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                        - BISHOP_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];

                // Update ZobristKey
                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_BISHOP][squareFrom];
                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_BISHOP][squareTo];

                // Tours blanches
            } else if ((whiteRooks & fromBitboard) != 0) {
                
                if ((A1 & fromBitboard) != 0 && whiteCastleQueenSide != 0) {
                    this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
                    whiteCastleQueenSide = 0L;
                    this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];

                }
                if ((H1 & fromBitboard) != 0 && whiteCastleKingSide != 0) {
                    this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
                    whiteCastleKingSide = 0L;
                    this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];

                }
                whiteRooks &= ~fromBitboard;
                whiteRooks |= toBitboard;
                
                // Update ZobristKey
                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_ROOK][squareFrom];
                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_ROOK][squareTo];

                // Update evaluations for both phases
                currentEvalMG += ROOK_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                        - ROOK_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                currentEvalEG += ROOK_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                        - ROOK_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];


                // Dames blanches
            } else if ((whiteQueens & fromBitboard) != 0) {
                whiteQueens &= ~fromBitboard;
                whiteQueens |= toBitboard;

                // Update evaluations for both phases
                currentEvalMG += QUEEN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                        - QUEEN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                currentEvalEG += QUEEN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                        - QUEEN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];

                // Update ZobristKey
                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_QUEEN][squareFrom];
                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_QUEEN][squareTo];

                // Roi blanc
            } else if ((whiteKing & fromBitboard) != 0) {
                if (((whiteCastleQueenSide != 0) && (C1 & toBitboard) != 0)
                        || ((whiteCastleKingSide != 0) && (G1 & toBitboard) != 0)) {
                    // Handle castling for white
                    if (toBitboard == 1L << 2) {
                        processWhiteCastleQueenSide(fromBitboard);
                    } else if (toBitboard == 1L << 6) {
                        processWhiteCastleKingSide(fromBitboard);
                    }
                } else {
                    whiteKing &= ~fromBitboard;
                    whiteKing |= toBitboard;

                    // Reset castling rights
                    if (whiteCastleKingSide != 0 || whiteCastleQueenSide != 0) {
                        this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
                        whiteCastleQueenSide = 0L;
                        whiteCastleKingSide = 0L;
                        this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
                    }

                    // Update evaluations for both phases
                    currentEvalMG += KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                            - KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                    currentEvalEG += KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56]
                            - KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
                }

                // Update ZobristKey
                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_KING][squareFrom];
                this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_KING][squareTo];
            }
        } else {

            squareFrom = PackedMove.getFrom(move);
            squareTo = PackedMove.getTo(move);

            handleCaptureBlack(toBitboard);
            // Pions noirs
            if ((blackPawns & fromBitboard) != 0) {

                // Handle en passant capture
                if (PackedMove.getFlags(move) == Move.EN_PASSENT) {
                    final long capturedPawn = enPassantSquare << 8;
                    whitePawns &= ~capturedPawn;

                    this.zobristKey ^= Zobrist.PIECE_KEYS[WHITE_PAWN][Long.numberOfTrailingZeros(capturedPawn)];

                    // Move pawn
                    blackPawns &= ~fromBitboard;
                    blackPawns |= toBitboard;

                    // Update evaluations for both phases
                    currentEvalMG -= PAWN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)]
                            - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
                    currentEvalEG -= PAWN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)]
                            - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];

                    // Update ZobristKey
                    this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_PAWN][squareFrom];
                    this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_PAWN][squareTo];
                }

                // Handle double pawn push
                else if (PackedMove.getFlags(move) == Move.DOUBLE_PAWN_PUSH) {
                    if (enPassantSquare != 0) {
                        this.zobristKey ^= Zobrist.EN_PASSANT_KEYS[Long.numberOfTrailingZeros(enPassantSquare)];
                    }
                    enPassantSquare = toBitboard << 8;

                    // Move pawn
                    blackPawns &= ~fromBitboard;
                    blackPawns |= toBitboard;

                    // Update evaluations for both phases
                    currentEvalMG -= PAWN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)]
                            - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
                    currentEvalEG -= PAWN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)]
                            - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];

                    // Update ZobirstKey
                    this.zobristKey ^= Zobrist.EN_PASSANT_KEYS[Long.numberOfTrailingZeros(enPassantSquare)];

                    // === Update ZobristKey ===
                    // Remove
                    this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_PAWN][squareFrom];
                    // Add
                    this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_PAWN][squareTo];

                } else {
                    if (enPassantSquare != 0) {
                        this.zobristKey ^= Zobrist.EN_PASSANT_KEYS[Long.numberOfTrailingZeros(enPassantSquare)];
                    }
                    enPassantSquare = 0L; // Reset en passant square

                    // Handle promotion
                    if (PackedMove.getFlags(move) == Move.PROMOTION) {
                        switch (PackedMove.getPromotion(move)) {
                            case KNIGHT:
                                // Update evaluations for both phases
                                currentEvalMG -= KNIGHT_SCORE + KNIGHT_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)]
                                        - PAWN_SCORE - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
                                currentEvalEG -= KNIGHT_SCORE + KNIGHT_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)]
                                        - PAWN_SCORE - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];
                                phase += 1; // Knights add 1 to phase
                                blackKnights |= toBitboard;
                                
                                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_KNIGHT][squareTo];
                                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_PAWN][squareFrom];
                                break;
                            case BISHOP:
                                // Update evaluations for both phases
                                currentEvalMG -= BISHOP_SCORE + BISHOP_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)]
                                        - PAWN_SCORE - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
                                currentEvalEG -= BISHOP_SCORE + BISHOP_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)]
                                        - PAWN_SCORE - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];
                                phase += 1; // Bishops add 1 to phase
                                blackBishops |= toBitboard;

                                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_BISHOP][squareTo];
                                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_PAWN][squareFrom];
                                break;
                            case ROOK:
                                // Update evaluations for both phases
                                currentEvalMG -= ROOK_SCORE + ROOK_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)]
                                        - PAWN_SCORE - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
                                currentEvalEG -= ROOK_SCORE + ROOK_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)]
                                        - PAWN_SCORE - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];
                                phase += 2; // Rooks add 2 to phase
                                blackRooks |= toBitboard;

                                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_ROOK][squareTo];
                                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_PAWN][squareFrom];
                                break;
                            case QUEEN:
                                // Update evaluations for both phases
                                currentEvalMG -= QUEEN_SCORE + QUEEN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)]
                                        - PAWN_SCORE - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
                                currentEvalEG -= QUEEN_SCORE + QUEEN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)]
                                        - PAWN_SCORE - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];
                                phase += 4; // Queens add 4 to phase
                                blackQueens |= toBitboard;

                                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_QUEEN][squareTo];
                                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_PAWN][squareFrom];
                                break;
                        }

                        // remove pawn
                        blackPawns &= ~fromBitboard;

                    } else {
                        // Move pawn
                        blackPawns &= ~fromBitboard;
                        blackPawns |= toBitboard;

                        // Update evaluations for both phases
                        currentEvalMG -= PAWN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)]
                                - PAWN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
                        currentEvalEG -= PAWN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)]
                                - PAWN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];

                        // Update ZobristKey
                        this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_PAWN][squareFrom];
                        this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_PAWN][squareTo];
                    }
                }

                // Cavaliers noirs
            } else if ((blackKnights & fromBitboard) != 0) {
                blackKnights &= ~fromBitboard;
                blackKnights |= toBitboard;

                // Update evaluations for both phases
                currentEvalMG -= KNIGHT_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)]
                        - KNIGHT_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
                currentEvalEG -= KNIGHT_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)]
                        - KNIGHT_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];

                // Update ZobristKey
                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_KNIGHT][squareFrom];
                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_KNIGHT][squareTo];

                // Fous noirs
            } else if ((blackBishops & fromBitboard) != 0) {
                blackBishops &= ~fromBitboard;
                blackBishops |= toBitboard;

                // Update evaluations for both phases
                currentEvalMG -= BISHOP_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)]
                        - BISHOP_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
                currentEvalEG -= BISHOP_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)]
                        - BISHOP_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];

                // Update ZobristKey
                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_BISHOP][squareFrom];
                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_BISHOP][squareTo];

                // Tours noires
            } else if ((blackRooks & fromBitboard) != 0) {
                
                if ((A8 & fromBitboard) != 0 && blackCastleQueenSide != 0) {
                    this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
                    blackCastleQueenSide = 0L;
                    this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
                }
                if ((H8 & fromBitboard) != 0 && blackCastleKingSide != 0) {
                    this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
                    blackCastleKingSide = 0L;
                    this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
                }

                // Update ZobristKey
                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_ROOK][squareFrom];
                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_ROOK][squareTo];

                blackRooks &= ~fromBitboard;
                blackRooks |= toBitboard;

                // Update evaluations for both phases
                currentEvalMG -= ROOK_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)]
                        - ROOK_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
                currentEvalEG -= ROOK_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)]
                        - ROOK_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];

                // Dames noires
            } else if ((blackQueens & fromBitboard) != 0) {
                blackQueens &= ~fromBitboard;
                blackQueens |= toBitboard;

                // Update evaluations for both phases
                currentEvalMG -= QUEEN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)]
                        - QUEEN_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
                currentEvalEG -= QUEEN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)]
                        - QUEEN_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];

                // Update ZobristKey
                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_QUEEN][squareFrom];
                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_QUEEN][squareTo];

                // Roi noir
            } else if ((blackKing & fromBitboard) != 0) {
                if (((blackCastleQueenSide != 0) && (C8 & toBitboard) != 0)
                        || ((blackCastleKingSide != 0) && (G8 & toBitboard) != 0)) {
                    // Handle castling for black
                    if (toBitboard == 1L << 58) {
                        processBlackCastleQueenSide(fromBitboard);
                    } else if (toBitboard == 1L << 62) {
                        processBlackCastleKingSide(fromBitboard);
                    }
                } else {
                    blackKing &= ~fromBitboard;
                    blackKing |= toBitboard;

                    // Reset castling rights
                    if (blackCastleKingSide != 0 || blackCastleQueenSide != 0) {
                        this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
                        blackCastleQueenSide = 0L;
                        blackCastleKingSide = 0L;
                        this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
                    }

                    // Update evaluations for both phases
                    currentEvalMG -= KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)]
                            - KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
                    currentEvalEG -= KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)]
                            - KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];
                }

                // Update ZobristKey
                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_KING][squareFrom];
                this.zobristKey ^= Zobrist.PIECE_KEYS[BLACK_KING][squareTo];
            }
        }

        // Update turn and reset en passant square if not a double pawn push
        whiteTurn = !whiteTurn;
        if (PackedMove.getFlags(move) != Move.DOUBLE_PAWN_PUSH) {
            if (enPassantSquare != 0) {
                this.zobristKey ^= Zobrist.EN_PASSANT_KEYS[Long.numberOfTrailingZeros(enPassantSquare)];
            }
            enPassantSquare = 0L;
        }
        
        this.zobristKey ^= Zobrist.SIDE_TO_MOVE_KEY;

        plyCount++;

        
        // Update the bitboard representation
        updateBitBoard();

        // Ensure phase doesn't exceed 24
        phase = Math.min(phase, 24);
    }

    public void undoMove() {
        if (!history.isEmpty()) {
            BoardHistory last = history.pop();
            last.restoreTo(this);
            plyCount--;

        } else {
            throw new IllegalStateException("No move to undo");
        }
    }
    

    public final void restoreBoardHistory(BoardHistory boardHistory) {
        bitboard = boardHistory.bitboard;
        whitePawns = boardHistory.whitePawns;
        whiteKnights = boardHistory.whiteKnights;
        whiteBishops = boardHistory.whiteBishops;
        whiteRooks = boardHistory.whiteRooks;
        whiteQueens = boardHistory.whiteQueens;
        whiteKing = boardHistory.whiteKing;
        blackPawns = boardHistory.blackPawns;
        blackKnights = boardHistory.blackKnights;
        blackBishops = boardHistory.blackBishops;
        blackRooks = boardHistory.blackRooks;
        blackQueens = boardHistory.blackQueens;
        blackKing = boardHistory.blackKing;
        whiteCastleQueenSide = boardHistory.whiteCastleQueenSide;
        whiteCastleKingSide = boardHistory.whiteCastleKingSide;
        blackCastleQueenSide = boardHistory.blackCastleQueenSide;
        blackCastleKingSide = boardHistory.blackCastleKingSide;
        enPassantSquare = boardHistory.enPassantSquare;
        whiteTurn = boardHistory.whiteTurn;

        currentEvalMG = boardHistory.evalMG;
        currentEvalEG = boardHistory.evalEG;

        phase = boardHistory.phase;
        zobristKey = boardHistory.zobristKey;

        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;
    }

    public void handleCaptureWhite(long toBitboard) {
        if ((blackPawns & toBitboard) != 0) {
            blackPawns &= ~toBitboard;
            currentEvalMG += PAWN_SCORE + PAWN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)];
            currentEvalEG += PAWN_SCORE + PAWN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)];
            // phase -= 0; // Pawns don't contribute to phase
            this.zobristKey ^= Zobrist.PIECE_KEYS[6][Long.numberOfTrailingZeros(toBitboard)];
        } else if ((blackKnights & toBitboard) != 0) {
            blackKnights &= ~toBitboard;
            currentEvalMG += KNIGHT_SCORE + KNIGHT_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)];
            currentEvalEG += KNIGHT_SCORE + KNIGHT_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)];
            phase -= 1; // Knights contribute 1 to phase
            this.zobristKey ^= Zobrist.PIECE_KEYS[7][Long.numberOfTrailingZeros(toBitboard)];
        } else if ((blackBishops & toBitboard) != 0) {
            blackBishops &= ~toBitboard;
            currentEvalMG += BISHOP_SCORE + BISHOP_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)];
            currentEvalEG += BISHOP_SCORE + BISHOP_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)];
            phase -= 1; // Bishops contribute 1 to phase
            this.zobristKey ^= Zobrist.PIECE_KEYS[8][Long.numberOfTrailingZeros(toBitboard)];
        } else if ((blackRooks & toBitboard) != 0) {
            blackRooks &= ~toBitboard;
            currentEvalMG += ROOK_SCORE + ROOK_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)];
            currentEvalEG += ROOK_SCORE + ROOK_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)];
            phase -= 2; // Rooks contribute 2 to phase
            this.zobristKey ^= Zobrist.PIECE_KEYS[9][Long.numberOfTrailingZeros(toBitboard)];
        } else if ((blackQueens & toBitboard) != 0) {
            blackQueens &= ~toBitboard;
            currentEvalMG += QUEEN_SCORE + QUEEN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)];
            currentEvalEG += QUEEN_SCORE + QUEEN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)];
            phase -= 4; // Queens contribute 4 to phase
            this.zobristKey ^= Zobrist.PIECE_KEYS[10][Long.numberOfTrailingZeros(toBitboard)];
        } else if ((blackKing & toBitboard) != 0) {
            blackKing &= ~toBitboard;
            currentEvalMG += KING_SCORE + KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(toBitboard)];
            currentEvalEG += KING_SCORE + KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(toBitboard)];
            phase -= 6; // King contributes 6 to phase
            this.zobristKey ^= Zobrist.PIECE_KEYS[11][Long.numberOfTrailingZeros(toBitboard)];
        }
    }

    public void handleCaptureBlack(long toBitboard) {
        if ((whitePawns & toBitboard) != 0) {
            whitePawns &= ~toBitboard;
            currentEvalMG -= PAWN_SCORE + PAWN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56];
            currentEvalEG -= PAWN_SCORE + PAWN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56];
            // phase -= 0; // Pawns don't contribute to phase
            this.zobristKey ^= Zobrist.PIECE_KEYS[0][Long.numberOfTrailingZeros(toBitboard)];
        } else if ((whiteKnights & toBitboard) != 0) {
            whiteKnights &= ~toBitboard;
            currentEvalMG -= KNIGHT_SCORE + KNIGHT_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56];
            currentEvalEG -= KNIGHT_SCORE + KNIGHT_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56];
            phase -= 1; // Knights contribute 1 to phase
            this.zobristKey ^= Zobrist.PIECE_KEYS[1][Long.numberOfTrailingZeros(toBitboard)];
        } else if ((whiteBishops & toBitboard) != 0) {
            whiteBishops &= ~toBitboard;
            currentEvalMG -= BISHOP_SCORE + BISHOP_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56];
            currentEvalEG -= BISHOP_SCORE + BISHOP_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56];
            phase -= 1; // Bishops contribute 1 to phase
            this.zobristKey ^= Zobrist.PIECE_KEYS[2][Long.numberOfTrailingZeros(toBitboard)];
        } else if ((whiteRooks & toBitboard) != 0) {
            whiteRooks &= ~toBitboard;
            currentEvalMG -= ROOK_SCORE + ROOK_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56];
            currentEvalEG -= ROOK_SCORE + ROOK_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56];
            phase -= 2; // Rooks contribute 2 to phase
            this.zobristKey ^= Zobrist.PIECE_KEYS[3][Long.numberOfTrailingZeros(toBitboard)];
        } else if ((whiteQueens & toBitboard) != 0) {
            whiteQueens &= ~toBitboard;
            currentEvalMG -= QUEEN_SCORE + QUEEN_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56];
            currentEvalEG -= QUEEN_SCORE + QUEEN_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56];
            phase -= 4; // Queens contribute 4 to phase
            this.zobristKey ^= Zobrist.PIECE_KEYS[4][Long.numberOfTrailingZeros(toBitboard)];
        } else if ((whiteKing & toBitboard) != 0) {
            whiteKing &= ~toBitboard;
            currentEvalMG -= KING_SCORE + KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(toBitboard) ^ 56];
            currentEvalEG -= KING_SCORE + KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(toBitboard) ^ 56];
            phase -= 6; // King contributes 6 to phase
            this.zobristKey ^= Zobrist.PIECE_KEYS[5][Long.numberOfTrailingZeros(toBitboard)];
        }
    }

    public void processWhiteCastleKingSide(long fromBitboard) {
        // Roque du côté du roi
        whiteKing &= ~fromBitboard;
        whiteKing |= 1L << 6;
        whiteRooks &= ~(1L << 7);
        whiteRooks |= 1L << 5;

        this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];

        whiteCastleKingSide = 0L;
        whiteCastleQueenSide = 0L;

        // Update evaluations for both phases
        currentEvalMG += KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(whiteKing) ^ 56]
                - KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
        currentEvalEG += KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(whiteKing) ^ 56]
                - KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];

        // Update evaluations for rook
        currentEvalMG += ROOK_TABLE_MG[Long.numberOfTrailingZeros(whiteRooks & (1L << 5)) ^ 56] - ROOK_TABLE_MG[7 ^ 56];
        currentEvalEG += ROOK_TABLE_EG[Long.numberOfTrailingZeros(whiteRooks & (1L << 5)) ^ 56] - ROOK_TABLE_EG[7 ^ 56];

        // Update ZobristKey for the rook (before and after)
        this.zobristKey ^= Zobrist.PIECE_KEYS[3][7]; // Remove the rook from H1
        this.zobristKey ^= Zobrist.PIECE_KEYS[3][5];

        // castling rights
        this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
    }

    public void processWhiteCastleQueenSide(long fromBitboard) {
        // Roque du côté de la reine
        whiteKing &= ~fromBitboard;
        whiteKing |= 1L << 2;
        whiteRooks &= ~(1L << 0);
        whiteRooks |= 1L << 3;

        this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];

        whiteCastleKingSide = 0L;
        whiteCastleQueenSide = 0L;

        // Update evaluations for both phases
        currentEvalMG += KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(whiteKing) ^ 56]
                - KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];
        currentEvalEG += KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(whiteKing) ^ 56]
                - KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard) ^ 56];

        // Update evaluations for rook
        currentEvalMG += ROOK_TABLE_MG[Long.numberOfTrailingZeros(whiteRooks & (1L << 3)) ^ 56] - ROOK_TABLE_MG[0 ^ 56];
        currentEvalEG += ROOK_TABLE_EG[Long.numberOfTrailingZeros(whiteRooks & (1L << 3)) ^ 56] - ROOK_TABLE_EG[0 ^ 56];

        // Update ZobristKey for the rook (before and after)
        this.zobristKey ^= Zobrist.PIECE_KEYS[3][0]; // Remove the rook from A1
        this.zobristKey ^= Zobrist.PIECE_KEYS[3][3];

        // castling rights
        this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
    }

    public void processBlackCastleKingSide(long fromBitboard) {
        // Roque du côté du roi
        blackKing &= ~fromBitboard;
        blackKing |= 1L << 62;
        blackRooks &= ~(1L << 63);
        blackRooks |= 1L << 61;

        this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];

        blackCastleKingSide = 0L;
        blackCastleQueenSide = 0L;

        // Update evaluations for both phases
        currentEvalMG -= KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(blackKing)]
                - KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
        currentEvalEG -= KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(blackKing)]
                - KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];

        // Update evaluations for rook
        currentEvalMG -= ROOK_TABLE_MG[Long.numberOfTrailingZeros(blackRooks & (1L << 61))] - ROOK_TABLE_MG[63];
        currentEvalEG -= ROOK_TABLE_EG[Long.numberOfTrailingZeros(blackRooks & (1L << 61))] - ROOK_TABLE_EG[63];

        // Update ZobristKey for the rook (before and after)
        this.zobristKey ^= Zobrist.PIECE_KEYS[9][63]; // Remove the rook from H8
        this.zobristKey ^= Zobrist.PIECE_KEYS[9][61];

        // castling rights
        this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
    }

    public void processBlackCastleQueenSide(long fromBitboard) {
        // Roque du côté de la reine
        blackKing &= ~fromBitboard;
        blackKing |= 1L << 58;
        blackRooks &= ~(1L << 56);
        blackRooks |= 1L << 59;

        this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];

        blackCastleKingSide = 0L;
        blackCastleQueenSide = 0L;

        // Update evaluations for both phases
        currentEvalMG -= KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(blackKing)]
                - KING_MIDDLE_GAME_TABLE_MG[Long.numberOfTrailingZeros(fromBitboard)];
        currentEvalEG -= KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(blackKing)]
                - KING_END_GAME_TABLE_EG[Long.numberOfTrailingZeros(fromBitboard)];

        // Update evaluations for rook
        currentEvalMG -= ROOK_TABLE_MG[Long.numberOfTrailingZeros(blackRooks & (1L << 59))] - ROOK_TABLE_MG[56];
        currentEvalEG -= ROOK_TABLE_EG[Long.numberOfTrailingZeros(blackRooks & (1L << 59))] - ROOK_TABLE_EG[56];

        // Update ZobristKey for the rook (before and after)
        this.zobristKey ^= Zobrist.PIECE_KEYS[9][56]; // Remove the rook from A8
        this.zobristKey ^= Zobrist.PIECE_KEYS[9][59];

        // castling rights
        this.zobristKey ^= Zobrist.CASTLING_KEYS[getCastlingRights()];
    }

    public void makeMove(String move) {
        int fromSquare = getSquare(move.substring(0, 2));
        int toSquare = getSquare(move.substring(2, 4));
        int pieceFrom = getPiece(fromSquare);

        // piece to if last character is not a digit
        int pieceTo = 0;

        // if last character is q or r or b or n
        if (move.length() == 5) {
            char lastChar = Character.toLowerCase(move.charAt(4));
            switch (lastChar) {
                case 'q':
                    pieceTo = QUEEN;
                    break;
                case 'r':
                    pieceTo = ROOK;
                    break;
                case 'b':
                    pieceTo = BISHOP;
                    break;
                case 'n':
                    pieceTo = KNIGHT;
                    break;
            }
        }

        if (move.length() == 5) {
            System.out.println("Promotion move");
            System.out.println("to piece: " + pieceTo);
            long moveLong = PackedMove.encode(fromSquare, pieceTo, pieceFrom, pieceTo, toSquare, pieceFrom, pieceTo);
            System.out.println("Move: " + moveLong);
            makeMove(moveLong);
        } else {
            System.out.println("Normal move");
            long moveLong = PackedMove.encode(fromSquare, pieceFrom, pieceFrom, pieceTo, toSquare, pieceFrom, pieceTo);
            System.out.println("Move: " + moveLong);
            makeMove(moveLong);
        }

    }

    // pseudo legal
    public PackedMoveList getPseudoLegalMoves() {
        return MoveGenerator.generatePseudoLegalMoves(this);
    }

    // legal moves
    public PackedMoveList getLegalMoves() {
        PackedMoveList moveList = MoveGenerator.generatePseudoLegalMoves(this);

        // Pour chaque coup, vérifier si le roi est en échec après le coup
        // Si le roi est en échec, le coup n'est pas légal
        // Sinon, le coup est légal
        for (int i = 0; i < moveList.size(); i++) {
            long move = moveList.get(i);
            makeMove(move);
            if (isKingInCheck(!whiteTurn)) {
                moveList.remove(i);
                i--;
            }
            undoMove();

        }

        return moveList;
    }

    public boolean isKingInCheck(boolean whiteTurn) {
        // Generate opponent mask attack
        long opponentAttacks = MoveGenerator.generateMask(this, !whiteTurn);
        long king = whiteTurn ? whiteKing : blackKing;
        return (opponentAttacks & king) != 0;
    }

    public boolean isInCheck() {
        return isKingInCheck(whiteTurn);
    }

    public PackedMoveList getCaptureMoves() {
        PackedMoveList moveList = MoveGenerator.generateCaptureMoves(this);

        // Pour chaque coup, vérifier si le roi est en échec après le coup
        // Si le roi est en échec, le coup n'est pas légal
        // Sinon, le coup est légal
        for (int i = 0; i < moveList.size(); i++) {
            long move = moveList.get(i);
            makeMove(move);
            if (isKingInCheck(!whiteTurn)) {
                moveList.remove(i);
                i--;
            }
            undoMove();

        }

        return moveList;
    }

    public boolean isStaleMate() {
        PackedMoveList moveList = getLegalMoves();
        return moveList.size() == 0 && !isKingInCheck(whiteTurn);
    }

    public boolean isCheckMate() {
        PackedMoveList moveList = getLegalMoves();
        return moveList.size() == 0 && isKingInCheck(whiteTurn);
    }

    private void updateBitBoard() {
        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;

        bitboard = whitePieces | blackPieces;
    }

    public static long getLSB(long bitboard) {
        return Long.lowestOneBit(bitboard);
    }

    public static long getMSB(long bitboard) {
        return Long.highestOneBit(bitboard);
    }

    // Get piece method
    public int getPiece(int square) {
        long bitboard = 1L << square;
        if ((whitePawns & bitboard) != 0) {
            return 1;
        } else if ((whiteKnights & bitboard) != 0) {
            return 2;
        } else if ((whiteBishops & bitboard) != 0) {
            return 3;
        } else if ((whiteRooks & bitboard) != 0) {
            return 4;
        } else if ((whiteQueens & bitboard) != 0) {
            return 5;
        } else if ((whiteKing & bitboard) != 0) {
            return 6;
        } else if ((blackPawns & bitboard) != 0) {
            return 7;
        } else if ((blackKnights & bitboard) != 0) {
            return 8;
        } else if ((blackBishops & bitboard) != 0) {
            return 9;
        } else if ((blackRooks & bitboard) != 0) {
            return 10;
        } else if ((blackQueens & bitboard) != 0) {
            return 11;
        } else if ((blackKing & bitboard) != 0) {
            return 12;
        } else {
            return BitBoard.EMPTY;
        }
    }

    public static int getPieceType(int piece) {
        if (piece == 1 || piece == 7) {
            return PAWN;
        } else if (piece == 2 || piece == 8) {
            return KNIGHT;
        } else if (piece == 3 || piece == 9) {
            return BISHOP;
        } else if (piece == 4 || piece == 10) {
            return ROOK;
        } else if (piece == 5 || piece == 11) {
            return QUEEN;
        } else if (piece == 6 || piece == 12) {
            return KING;
        } else {
            return EMPTY;
        }
    }

    public void printBitBoardRaw() {
        System.out.println(Long.toBinaryString(bitboard));
    }

    public static int getSquare(long bitboard) {
        return Long.numberOfTrailingZeros(bitboard);
    }

    private int getSquare(String position) {
        // Convertit une position comme "e2" en un index 0-63
        int file = position.charAt(0) - 'a'; // 'e' -> 4
        int rank = position.charAt(1) - '1'; // '2' -> 1
        int result = 8 * rank + file;

        return result;
    }

    public static int getSquare(int rank, int file) {
        return 8 * rank + file;
    }

    public static String getSquareIndexNotation(int square) {
        String[] files = { "a", "b", "c", "d", "e", "f", "g", "h" };
        int rank = square / 8;
        int file = square % 8;
        return files[file] + (rank + 1);

    }

    public void printEnPassantSquare() {
        for (int i = 0; i < 64; i++) {
            if ((enPassantSquare == SQUARES_MAP[i])) {
                System.out.println("En passant square: " + getSquareIndexNotation(i));
            }
        }
    }

    @Override
    public String toString() {
        return "Bitboard {" +
                "whitePawns=" + Long.toBinaryString(whitePawns) +
                ", whiteKnights=" + Long.toBinaryString(whiteKnights) +
                ", whiteBishops=" + Long.toBinaryString(whiteBishops) +
                ", whiteRooks=" + Long.toBinaryString(whiteRooks) +
                ", whiteKing=" + Long.toBinaryString(whiteKing) +
                ", whiteQueens=" + Long.toBinaryString(whiteQueens) +
                ", blackPawns=" + Long.toBinaryString(blackPawns) +
                ", blackKnights=" + Long.toBinaryString(blackKnights) +
                ", blackBishops=" + Long.toBinaryString(blackBishops) +
                ", blackRooks=" + Long.toBinaryString(blackRooks) +
                ", blackKing=" + Long.toBinaryString(blackKing) +
                ", blackQueens=" + Long.toBinaryString(blackQueens) +
                ", whitePieces=" + Long.toBinaryString(whitePieces) +
                ", blackPieces=" + Long.toBinaryString(blackPieces) +
                ", bitboard=" + Long.toBinaryString(bitboard) +
                ", whiteCastleQueenSide=" + Long.toBinaryString(whiteCastleQueenSide) +
                ", whiteCastleKingSide=" + Long.toBinaryString(whiteCastleKingSide) +
                ", blackCastleQueenSide=" + Long.toBinaryString(blackCastleQueenSide) +
                ", blackCastleKingSide=" + Long.toBinaryString(blackCastleKingSide) +
                ", enPassantSquare=" + Long.toBinaryString(enPassantSquare) +
                ", whiteTurn=" + whiteTurn +
                '}';
    }

    public boolean isCaptureMove(Move move) {
        int toSquare = move.to;
        long toBitboard = 1L << toSquare;
        if (whiteTurn) {
            if ((blackPawns & toBitboard) != 0) {
                return true;
            } else if ((blackKnights & toBitboard) != 0) {
                return true;
            } else if ((blackBishops & toBitboard) != 0) {
                return true;
            } else if ((blackRooks & toBitboard) != 0) {
                return true;
            } else if ((blackQueens & toBitboard) != 0) {
                return true;
            } else if ((blackKing & toBitboard) != 0) {
                return true;
            }
        } else {
            if ((whitePawns & toBitboard) != 0) {
                return true;
            } else if ((whiteKnights & toBitboard) != 0) {
                return true;
            } else if ((whiteBishops & toBitboard) != 0) {
                return true;
            } else if ((whiteRooks & toBitboard) != 0) {
                return true;
            } else if ((whiteQueens & toBitboard) != 0) {
                return true;
            } else if ((whiteKing & toBitboard) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isWhite(long piece) {
        return (whitePieces & piece) != 0;
    }

    public boolean isBlack(long piece) {
        return (blackPieces & piece) != 0;
    }

    public int getPieceAt(int to) {
        long toBitboard = 1L << to;
        if ((whitePawns & toBitboard) != 0) {
            return PAWN;
        } else if ((whiteKnights & toBitboard) != 0) {
            return KNIGHT;
        } else if ((whiteBishops & toBitboard) != 0) {
            return BISHOP;
        } else if ((whiteRooks & toBitboard) != 0) {
            return ROOK;
        } else if ((whiteQueens & toBitboard) != 0) {
            return QUEEN;
        } else if ((whiteKing & toBitboard) != 0) {
            return KING;
        } else if ((blackPawns & toBitboard) != 0) {
            return PAWN;
        } else if ((blackKnights & toBitboard) != 0) {
            return KNIGHT;
        } else if ((blackBishops & toBitboard) != 0) {
            return BISHOP;
        } else if ((blackRooks & toBitboard) != 0) {
            return ROOK;
        } else if ((blackQueens & toBitboard) != 0) {
            return QUEEN;
        } else if ((blackKing & toBitboard) != 0) {
            return KING;
        } else {
            return 0;
        }
    }

    public int getPieceAt(long to) {
        if ((whitePawns & to) != 0) {
            return PAWN;
        } else if ((whiteKnights & to) != 0) {
            return KNIGHT;
        } else if ((whiteBishops & to) != 0) {
            return BISHOP;
        } else if ((whiteRooks & to) != 0) {
            return ROOK;
        } else if ((whiteQueens & to) != 0) {
            return QUEEN;
        } else if ((whiteKing & to) != 0) {
            return KING;
        } else if ((blackPawns & to) != 0) {
            return PAWN;
        } else if ((blackKnights & to) != 0) {
            return KNIGHT;
        } else if ((blackBishops & to) != 0) {
            return BISHOP;
        } else if ((blackRooks & to) != 0) {
            return ROOK;
        } else if ((blackQueens & to) != 0) {
            return QUEEN;
        } else if ((blackKing & to) != 0) {
            return KING;
        } else {
            return 0;
        }
    }

    private int getCastlingRights() {
        int rights = 0;
        if (whiteCastleKingSide != 0) {
            rights |= 1; // Droit de roque du côté du roi
        }
        if (whiteCastleQueenSide != 0) {
            rights |= 2; // Droit de roque du côté de la reine
        }
        if (blackCastleKingSide != 0) {
            rights |= 4; // Droit de roque du côté du roi
        }
        if (blackCastleQueenSide != 0) {
            rights |= 8; // Droit de roque du côté de la reine
        }
        return rights;
    }

    @Override
    public BitBoard clone() {
        try {
            BitBoard copy = (BitBoard) super.clone();

            copy.whitePawns = this.whitePawns;
            copy.whiteKnights = this.whiteKnights;
            copy.whiteBishops = this.whiteBishops;
            copy.whiteRooks = this.whiteRooks;
            copy.whiteQueens = this.whiteQueens;
            copy.whiteKing = this.whiteKing;

            copy.blackPawns = this.blackPawns;
            copy.blackKnights = this.blackKnights;
            copy.blackBishops = this.blackBishops;
            copy.blackRooks = this.blackRooks;
            copy.blackQueens = this.blackQueens;
            copy.blackKing = this.blackKing;

            copy.whiteCastleKingSide = this.whiteCastleKingSide;
            copy.whiteCastleQueenSide = this.whiteCastleQueenSide;
            copy.blackCastleKingSide = this.blackCastleKingSide;
            copy.blackCastleQueenSide = this.blackCastleQueenSide;

            copy.enPassantSquare = this.enPassantSquare;
            copy.whiteTurn = this.whiteTurn;

            copy.currentEvalMG = this.currentEvalMG;
            copy.currentEvalEG = this.currentEvalEG;
            copy.phase = this.phase;

            copy.zobristKey = this.zobristKey;

            copy.updateBitBoard();

            return copy;

        } catch (CloneNotSupportedException e) {
            throw new AssertionError("BitBoard should be cloneable", e);
        }
    }

    public void copyFrom(BitBoard other) {
        this.whiteTurn = other.whiteTurn;
    
        this.whitePawns = other.whitePawns;
        this.whiteKnights = other.whiteKnights;
        this.whiteBishops = other.whiteBishops;
        this.whiteRooks = other.whiteRooks;
        this.whiteQueens = other.whiteQueens;
        this.whiteKing = other.whiteKing;
    
        this.blackPawns = other.blackPawns;
        this.blackKnights = other.blackKnights;
        this.blackBishops = other.blackBishops;
        this.blackRooks = other.blackRooks;
        this.blackQueens = other.blackQueens;
        this.blackKing = other.blackKing;
    
        this.whitePieces = other.whitePieces;
        this.blackPieces = other.blackPieces;
        this.bitboard = other.bitboard;
    
        this.whiteCastleQueenSide = other.whiteCastleQueenSide;
        this.whiteCastleKingSide = other.whiteCastleKingSide;
        this.blackCastleQueenSide = other.blackCastleQueenSide;
        this.blackCastleKingSide = other.blackCastleKingSide;
    
        this.enPassantSquare = other.enPassantSquare;
    
        this.currentEvalMG = other.currentEvalMG;
        this.currentEvalEG = other.currentEvalEG;
        this.phase = other.phase;
    
        this.zobristKey = other.zobristKey;
    }    

}