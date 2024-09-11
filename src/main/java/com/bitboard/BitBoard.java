package com.bitboard;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Stack;

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

    public static final long A1 = 0b10000000L;
    public static final long B1 = A1 >>> 1;
    public static final long C1 = B1 >>> 1;
    public static final long D1 = C1 >>> 1;
    public static final long E1 = D1 >>> 1;
    public static final long F1 = E1 >>> 1;
    public static final long G1 = F1 >>> 1;
    public static final long H1 = G1 >>> 1;

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
        7, 6, 5, 4, 3, 2, 1, 0,
        15, 14, 13, 12, 11, 10, 9, 8,
        23, 22, 21, 20, 19, 18, 17, 16,
        31, 30, 29, 28, 27, 26, 25, 24,
        39, 38, 37, 36, 35, 34, 33, 32,
        47, 46, 45, 44, 43, 42, 41, 40,
        55, 54, 53, 52, 51, 50, 49, 48,
        63, 62, 61, 60, 59, 58, 57, 56
    };

    // knight pre-encoded moves
    public static final long[] KNIGHT_MOVES = {
        ( B3 | C2), 
        ( C3 | D2 | A3),
        ( D3 | E2 | A2 | B3),
        ( E3 | F2 | B2 | C3),
        ( F3 | G2 | C2 | D3),
        ( G3 | H2 | D2 | E3),
        ( H3 | E2 | F3),
        ( F2 | G3),
        ( B4 | C3 | C1),
        ( C4 | D3 | D1 | A4),
        ( D4 | E3 | E1 | A1 | A3 | B4),
        ( E4 | F3 | F1 | B1 | B3 | C4),
        ( F4 | G3 | G1 | C1 | C3 | D4),
        ( G4 | H3 | H1 | D1 | D3 | E4),
        ( H4 | E1 | E3 | F4),
        ( F1 | F3 | G4),
        ( B5 | C4 | C2 | B1),
        ( C5 | D4 | D2 | C1 | A1 | A5),
        ( D5 | E4 | E2 | D1 | B1 | A2 | A4 | B5),
        ( E5 | F4 | F2 | E1 | C1 | B2 | B4 | C5),
        ( F5 | G4 | G2 | F1 | D1 | C2 | C4 | D5),
        ( G5 | H4 | H2 | G1 | E1 | D2 | D4 | E5),
        ( H5 | H1 | F1 | E2 | E4 | F5),
        ( G1 | F2 | F4 | G5),
        ( B6 | C5 | C3 | B2),
        ( C6 | D5 | D3 | C2 | A2 | A6),
        ( D6 | E5 | E3 | D2 | B2 | A3 | A5 | B6),
        ( E6 | F5 | F3 | E2 | C2 | B3 | B5 | C6),
        ( F6 | G5 | G3 | F2 | D2 | C3 | C5 | D6),
        ( G6 | H5 | H3 | G2 | E2 | D3 | D5 | E6),
        ( H6 | H2 | F2 | E3 | E5 | F6),
        ( G2 | F3 | F5 | G6),
        ( B7 | C6 | C4 | B3),
        ( C7 | D6 | D4 | C3 | A3 | A7),
        ( D7 | E6 | E4 | D3 | B3 | A4 | A6 | B7),
        ( E7 | F6 | F4 | E3 | C3 | B4 | B6 | C7),
        ( F7 | G6 | G4 | F3 | D3 | C4 | C6 | D7),
        ( G7 | H6 | H4 | G3 | E3 | D4 | D6 | E7),
        ( H7 | H3 | F3 | E4 | E6 | F7),
        ( G3 | F4 | F6 | G7),
        ( B8 | C7 | C5 | B4),
        ( C8 | D7 | D5 | C4 | A4 | A8),
        ( D8 | E7 | E5 | D4 | B4 | A5 | A7 | B8),
        ( E8 | F7 | F5 | E4 | C4 | B5 | B7 | C8),
        ( F8 | G7 | G5 | F4 | D4 | C5 | C7 | D8),
        ( G8 | H7 | H5 | G4 | E4 | D5 | D7 | E8),
        ( H8 | H4 | F4 | E5 | E7 | F8),
        ( G4 | F5 | F7 | G8),
        ( C8 | C6 | B5),
        ( D8 | D6 | C5 | A5),
        ( E8 | E6 | D5 | B5 | A6 | A8),
        ( F8 | F6 | E5 | C5 | B6 | B8),
        ( G8 | G6 | F5 | D5 | C6 | C8),
        ( H8 | H6 | G5 | E5 | D6 | D8),
        ( H5 | F5 | E6 | E8), 
        ( G5 | F6 | F8),
        ( C7 | B6),
        ( D7 | C6 | A6),
        ( E7 | D6 | B6 | A7),
        ( F7 | E6 | C6 | B7),
        ( G7 | F6 | D6 | C7),
        ( H7 | G6 | E6 | D7),
        ( H6 | F6 | E7),
        ( G6 | F7)
    };
    
    


    // diagonals and anti-diagonals
    public static final long DIAGONAL_A1_H8 = 0x0102040810204080L;
    public static final long DIAGONAL_A2_G8 = 0x0204081020408000L;
    public static final long DIAGONAL_A3_F8 = 0x0408102040800000L;
    public static final long DIAGONAL_A4_E8 = 0x0810204080000000L;
    public static final long DIAGONAL_A5_D8 = 0x1020408000000000L;
    public static final long DIAGONAL_A6_C8 = 0x2040800000000000L;
    public static final long DIAGONAL_A7_B8 = 0x4080000000000000L;
    public static final long DIAGONAL_A8_A8 = 0x8000000000000000L;

    public static final long DIAGONAL_B1_H7 = 0x0001020408102040L;
    public static final long DIAGONAL_C1_H6 = 0x0000010204081020L;
    public static final long DIAGONAL_D1_H5 = 0x0000000102040810L;
    public static final long DIAGONAL_E1_H4 = 0x0000000001020408L;
    public static final long DIAGONAL_F1_H3 = 0x0000000000010204L;
    public static final long DIAGONAL_G1_H2 = 0x0000000000000102L;
    public static final long DIAGONAL_H1_H1 = 0x0000000000000001L;

    public static final long DIAGONAL_H1_A8 = 0x8040201008040201L;
    public static final long DIAGONAL_G1_A7 = 0x4020100804020100L;
    public static final long DIAGONAL_F1_A6 = 0x2010080402010000L;
    public static final long DIAGONAL_E1_A5 = 0x1008040201000000L;
    public static final long DIAGONAL_D1_A4 = 0x0804020100000000L;
    public static final long DIAGONAL_C1_A3 = 0x0402010000000000L;
    public static final long DIAGONAL_B1_A2 = 0x0201000000000000L;
    public static final long DIAGONAL_A1_A1 = 0x0100000000000000L;


    // valid
    public static final long WHITE_KING_SIDE_CASTLE_KING_SQUARE = 0x0000000000000002L;
    public static final long BLACK_KING_SIDE_CASTLE_KING_SQUARE = 0x0200000000000000L;

    // valid
    public static final long WHITE_QUEEN_SIDE_CASTLE_KING_SQUARE = 0x0000000000000020L;
    public static final long BLACK_QUEEN_SIDE_CASTLE_KING_SQUARE = 0x2000000000000000L;

    // valid
    public static final long WHITE_KING_SIDE_ROOK_SQUARE = 0x0000000000000001L;
    public static final long WHITE_QUEEN_SIDE_ROOK_SQUARE = 0x0000000000000080L;

    // valid
    public static final long BLACK_KING_SIDE_ROOK_SQUARE = 0x0100000000000000L;
    public static final long BLACK_QUEEN_SIDE_ROOK_SQUARE = 0x8000000000000000L;

    // valid
    public static final long WHITE_KING_SIDE_CASTLE_EMPTY_SQUARES_MASK = 0b110L;
    public static final long WHITE_QUEEN_SIDE_CASTLE_EMPTY_SQUARES_MASK = 0b1110000L;

    // valid
    public static final long BLACK_KING_SIDE_CASTLE_EMPTY_SQUARES_MASK = 0x600000000000000L;
    public static final long BLACK_QUEEN_SIDE_CASTLE_EMPTY_SQUARES_MASK = 0x7000000000000000L;

    // valid
    public static final long WHITE_KING_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK = 0b1110L;
    public static final long WHITE_QUEEN_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK = 0b111000L;

    // valid
    public static final long BLACK_KING_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK = 0xE00000000000000L;
    public static final long BLACK_QUEEN_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK = 0x3800000000000000L;

    public static final int EMPTY = 0;
    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;

    private Stack<BoardHistory> history;

    private ArrayDeque<long[]> bbHistory;

    

    public static final String INITIAL_STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";


    public BitBoard() {

        whiteTurn = true;

        // Initialisation des pièces à leurs positions de départ
        whitePawns = 0x000000000000FF00L;
        whiteKnights = 0x0000000000000042L;
        whiteBishops = 0x0000000000000024L;
        whiteRooks = 0x0000000000000081L;
        whiteKing = 0x0000000000000008L;
        whiteQueens = 0x0000000000000010L;

        blackPawns = 0x00FF000000000000L;
        blackKnights = 0x4200000000000000L;
        blackBishops = 0x2400000000000000L;
        blackRooks = 0x8100000000000000L;
        blackKing = 0x0800000000000000L;
        blackQueens = 0x1000000000000000L;

        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;

        bitboard = whitePieces | blackPieces;

        whiteCastleQueenSide = 1L;
        whiteCastleKingSide = 1L;
        blackCastleQueenSide = 1L;
        blackCastleKingSide = 1L;

        enPassantSquare = 0L;

        history = new Stack<>();
        // bbHistory = new ArrayDeque<>();


    }

    private void saveBoardHistory(Move move) {
        BoardHistory boardHistory = new BoardHistory(bitboard, move, whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKing, blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKing, whiteCastleQueenSide, whiteCastleKingSide, blackCastleQueenSide, blackCastleKingSide, enPassantSquare, whiteTurn);
        history.push(boardHistory);
    }

    // private void saveBoardHistoryLONG() {
    //     bbHistory.push(new long[]{whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKing, blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKing, whiteCastleQueenSide, whiteCastleKingSide, blackCastleQueenSide, blackCastleKingSide, enPassantSquare, (whiteTurn ? 1L : 0L)});
    // }

    public void undoMove() {
        if (!history.isEmpty()) {
            BoardHistory boardHistory = history.pop();
            restoreBoardHistory(boardHistory);
            // restoreBoardHistoryLONG(bbHistory.pop());
        }
    }

    public void restoreBoardHistory(BoardHistory boardHistory) {
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
    }

    public void restoreBoardHistoryLONG(long[] boardHistory) {
        whitePawns = boardHistory[0];
        whiteKnights = boardHistory[1];
        whiteBishops = boardHistory[2];
        whiteRooks = boardHistory[3];
        whiteQueens = boardHistory[4];
        whiteKing = boardHistory[5];
        blackPawns = boardHistory[6];
        blackKnights = boardHistory[7];
        blackBishops = boardHistory[8];
        blackRooks = boardHistory[9];
        blackQueens = boardHistory[10];
        blackKing = boardHistory[11];
        whiteCastleQueenSide = boardHistory[12];
        whiteCastleKingSide = boardHistory[13];
        blackCastleQueenSide = boardHistory[14];
        blackCastleKingSide = boardHistory[15];
        enPassantSquare = boardHistory[16];
        whiteTurn = boardHistory[17] == 1L;
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

        for (String r : rows) {
            for (int i = 0; i < r.length(); i++) {
                char c = r.charAt(i);
                if (Character.isDigit(c)) {
                    col += Character.getNumericValue(c);
                } else {
                    long bitboard = 1L << (63 - (row * 8 + col));
                    switch (c) {
                        case 'P': whitePawns |= bitboard; break;
                        case 'N': whiteKnights |= bitboard; break;
                        case 'B': whiteBishops |= bitboard; break;
                        case 'R': whiteRooks |= bitboard; break;
                        case 'Q': whiteQueens |= bitboard; break;
                        case 'K': whiteKing |= bitboard; break;
                        case 'p': blackPawns |= bitboard; break;
                        case 'n': blackKnights |= bitboard; break;
                        case 'b': blackBishops |= bitboard; break;
                        case 'r': blackRooks |= bitboard; break;
                        case 'q': blackQueens |= bitboard; break;
                        case 'k': blackKing |= bitboard; break;
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
            whiteCastleKingSide = 1L;
        } else {
            whiteCastleKingSide = 0L;
        }

        if (fenParts[2].contains("Q")) {
            whiteCastleQueenSide = 1L;
        } else {
            whiteCastleQueenSide = 0L;
        }

        if (fenParts[2].contains("k")) {
            blackCastleKingSide = 1L;
        } else {
            blackCastleKingSide = 0L;
        }

        if (fenParts[2].contains("q")) {
            blackCastleQueenSide = 1L;
        } else {
            blackCastleQueenSide = 0L;
        }

        // en passant square
        if (!fenParts[3].equals("-")) {
            System.out.println(fenParts[3]);
            int file = fenParts[3].charAt(0) - 'a';
            int rank = fenParts[3].charAt(1) - '1';
            enPassantSquare = 1L << (rank * 8 + (7 - file));
        } else {
            enPassantSquare = 0L;
        }
        

        updateBitBoard();
    }

    public String getFen() {
        StringBuilder fen = new StringBuilder();
        for (int i = 0; i < 64; i += 8) {
            int empty = 0;
            for (int j = 0; j < 8; j++) {
                long bitboard = 1L << (63 - (i + j));
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
        if (whiteCastleKingSide == 0L && whiteCastleQueenSide == 0L && blackCastleKingSide == 0L && blackCastleQueenSide == 0L) {
            fen.append("-");
        }
        fen.append(" ");
        if (enPassantSquare != 0L) {
            for (int i = 0; i < 64; i++) {
                if ((enPassantSquare == SQUARES_MAP[i])) {
                    fen.append((char)('a' + (i % 8)));
                    fen.append((char)('1' + (i / 8)));
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
            for (int file = 7; file >= 0; file--) {
                int squareIndex = rank * 8 + file;
                long mask = 1L << squareIndex;
                
                if ((bitBoard & mask) != 0) {
                    writer.print(" 1 ");
                } else {
                    writer.print("   ");
                }
    
                // Ajouter un séparateur "|"
                if (file != 0) {
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

    public static void printBitBoards(long bitBoard) {
        PrintWriter writer = new PrintWriter(System.out);
        
        // Bordure supérieure
        writer.println("   +-------------------------------+");
        writer.println("   | a   b   c   d   e   f   g   h |");
        writer.println("   +-------------------------------+");
    
        // Parcourir les rangées de haut en bas
        for (int rank = 7; rank >= 0; rank--) {
            writer.print((rank + 1) + "  |"); // Numéro de rangée sur le côté gauche
            
            // Parcourir chaque colonne de la rangée
            for (int file = 7; file >= 0; file--) {
                int squareIndex = rank * 8 + file;
                long mask = 1L << squareIndex;
                
                if ((bitBoard & mask) != 0) {
                    writer.print(" 1 ");
                } else {
                    writer.print("   ");
                }
    
                // Ajouter un séparateur "|"
                if (file != 0) {
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
        String[] pieces = {"P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k"};
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
                int squareIndex = rank * 8 + file;
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
        updateBitBoard();
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
        whiteTurn = !whiteTurn;
    }

    public void undoNullMove() {
        whiteTurn = !whiteTurn;
    }

    public void makeMove(Move move) {

        // Save the current board state
        saveBoardHistory(move);
        // saveBoardHistoryLONG();
    
        // Get the source and destination squares
        int fromSquare = move.from;
        int toSquare = move.to;
    
        //! a optimiser
        fromSquare = 7 - fromSquare % 8 + fromSquare / 8 * 8;
        toSquare = 7 - toSquare % 8 + toSquare / 8 * 8;
    
        // Convert squares to bitboards
        long fromBitboard = 1L << fromSquare;
        long toBitboard = 1L << toSquare;
    
        
        // Separate logic for white and black moves
        if (whiteTurn) {
            handleCaptureWhite(toBitboard);
            // Pions blancs
            if ((whitePawns & fromBitboard) != 0) {
    
                // Handle en passant capture
                if (move.type == Move.EN_PASSENT) {
                    long capturedPawn = enPassantSquare >> 8;
                    blackPawns &= ~capturedPawn;

                    // Move pawn
                    whitePawns &= ~fromBitboard;
                    whitePawns |= toBitboard;
                }
    
                // Handle double pawn push
                if (move.type == Move.DOUBLE_PAWN_PUSH) {
                    enPassantSquare = toBitboard >> 8;

                    // Move pawn
                    whitePawns &= ~fromBitboard;
                    whitePawns |= toBitboard;
                } else {
                    enPassantSquare = 0L; // Reset en passant square

                    // Handle promotion
                    if (move.type == Move.PROMOTION) {
                        switch (move.pieceTo) {
                            case KNIGHT:
                                whiteKnights |= toBitboard; break;
                            case BISHOP:
                                whiteBishops |= toBitboard; break;
                            case ROOK:
                                whiteRooks |= toBitboard; break;
                            case QUEEN:
                                whiteQueens |= toBitboard; break;
                        }

                        // remove pawn
                        whitePawns &= ~fromBitboard;
    
                    }
                    else{
                        // Move pawn
                        whitePawns &= ~fromBitboard;
                        whitePawns |= toBitboard;
                    }

                }
    
            // Cavaliers blancs
            } else if ((whiteKnights & fromBitboard) != 0) {
                whiteKnights &= ~fromBitboard;
                whiteKnights |= toBitboard;
    
            // Fous blancs
            } else if ((whiteBishops & fromBitboard) != 0) {
                whiteBishops &= ~fromBitboard;
                whiteBishops |= toBitboard;
    
            // Tours blanches
            } else if ((whiteRooks & fromBitboard) != 0) {
                if (fromSquare == 7) whiteCastleQueenSide = 0L;
                if (fromSquare == 0) whiteCastleKingSide = 0L;
                whiteRooks &= ~fromBitboard;
                whiteRooks |= toBitboard;
    
            // Dames blanches
            } else if ((whiteQueens & fromBitboard) != 0) {
                whiteQueens &= ~fromBitboard;
                whiteQueens |= toBitboard;
    
            // Roi blanc
            } else if ((whiteKing & fromBitboard) != 0) {
                if (Math.abs(fromSquare - toSquare) != 2) {
                    whiteCastleQueenSide = 0L;
                    whiteCastleKingSide = 0L;
                    whiteKing &= ~fromBitboard;
                    whiteKing |= toBitboard;
                } else {
                    // Handle castling for white
                    if (toSquare == 5) {
                        processWhiteCastleQueenSide(fromBitboard);
                    } else if (toSquare == 1) {
                        processWhiteCastleKingSide(fromBitboard);
                    }
                }
            }
        } else {
            handleCaptureBlack(toBitboard);
            // Pions noirs
            if ((blackPawns & fromBitboard) != 0) {
    
                // Handle en passant capture
                if (move.type == Move.EN_PASSENT) {
                    long capturedPawn = enPassantSquare << 8;
                    whitePawns &= ~capturedPawn;

                    // Move pawn
                    blackPawns &= ~fromBitboard;
                    blackPawns |= toBitboard;
                }
    
                // Handle double pawn push
                if (move.type == Move.DOUBLE_PAWN_PUSH) {
                    enPassantSquare = toBitboard << 8;

                    // Move pawn
                    blackPawns &= ~fromBitboard;
                    blackPawns |= toBitboard;
                } else {
                    enPassantSquare = 0L; // Reset en passant square

                    
                    // Handle promotion
                    if (move.type == Move.PROMOTION) {
                        switch (move.pieceTo) {
                            case KNIGHT:
                                blackKnights |= toBitboard; break;
                            case BISHOP:
                                blackBishops |= toBitboard; break;
                            case ROOK:
                                blackRooks |= toBitboard; break;
                            case QUEEN:
                                blackQueens |= toBitboard; break;
                        }

                        // remove pawn
                        blackPawns &= ~fromBitboard;
    
                    }
                    else{
                        // Move pawn
                        blackPawns &= ~fromBitboard;
                        blackPawns |= toBitboard;
                    }
                }
    
    
            // Cavaliers noirs
            } else if ((blackKnights & fromBitboard) != 0) {
                blackKnights &= ~fromBitboard;
                blackKnights |= toBitboard;
    
            // Fous noirs
            } else if ((blackBishops & fromBitboard) != 0) {
                blackBishops &= ~fromBitboard;
                blackBishops |= toBitboard;
    
            // Tours noires
            } else if ((blackRooks & fromBitboard) != 0) {
                if (fromSquare == 63) blackCastleQueenSide = 0L;
                if (fromSquare == 56) blackCastleKingSide = 0L;
                blackRooks &= ~fromBitboard;
                blackRooks |= toBitboard;
    
            // Dames noires
            } else if ((blackQueens & fromBitboard) != 0) {
                blackQueens &= ~fromBitboard;
                blackQueens |= toBitboard;
    
            // Roi noir
            } else if ((blackKing & fromBitboard) != 0) {
                if (Math.abs(fromSquare - toSquare) != 2) {
                    blackCastleQueenSide = 0L;
                    blackCastleKingSide = 0L;
                    blackKing &= ~fromBitboard;
                    blackKing |= toBitboard;
                } else {
                    // Handle castling for black
                    if (toSquare == 61) {
                        processBlackCastleQueenSide(fromBitboard);
                    } else if (toSquare == 57) {
                        processBlackCastleKingSide(fromBitboard);
                    }
                }
            }
        }
    
        // Update turn and reset en passant square if not a double pawn push
        whiteTurn = !whiteTurn;
        if (move.type != Move.DOUBLE_PAWN_PUSH) {
            enPassantSquare = 0L;
        }
    
        // Update the bitboard representation
        updateBitBoard();
    }
    

    public void handleCaptureWhite(long toBitboard) {
        if ((blackPawns & toBitboard) != 0) {
            blackPawns &= ~toBitboard;
        } else if ((blackKnights & toBitboard) != 0) {
            blackKnights &= ~toBitboard;
        } else if ((blackBishops & toBitboard) != 0) {
            blackBishops &= ~toBitboard;
        } else if ((blackRooks & toBitboard) != 0) {
            blackRooks &= ~toBitboard;
        } else if ((blackQueens & toBitboard) != 0) {
            blackQueens &= ~toBitboard;
        } else if ((blackKing & toBitboard) != 0) {
            blackKing &= ~toBitboard;
        }
    }

    public void handleCaptureBlack(long toBitboard) {
        if ((whitePawns & toBitboard) != 0) {
            whitePawns &= ~toBitboard;
        } else if ((whiteKnights & toBitboard) != 0) {
            whiteKnights &= ~toBitboard;
        } else if ((whiteBishops & toBitboard) != 0) {
            whiteBishops &= ~toBitboard;
        } else if ((whiteRooks & toBitboard) != 0) {
            whiteRooks &= ~toBitboard;
        } else if ((whiteQueens & toBitboard) != 0) {
            whiteQueens &= ~toBitboard;
        } else if ((whiteKing & toBitboard) != 0) {
            whiteKing &= ~toBitboard;
        }
    }

    public Move makeRandomMove() {
        MoveList moveList = getLegalMoves();
        int randomIndex = (int) (Math.random() * moveList.size());
        Move move = moveList.get(randomIndex);
        return move;
    }

    public boolean isLegalMove(Move move) {
        MoveList moveList = getLegalMoves();
        // System.out.println("legal moves: " + moveList);
        return moveList.contains(move);
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
            Move m = new Move(fromSquare, toSquare, pieceFrom, pieceTo);
            m.setType(Move.PROMOTION);
            m.setWhite(whiteTurn);
            makeMove(m);
        } else {
            Move m = new Move(fromSquare, toSquare, pieceFrom, pieceFrom);
            makeMove(m);
        }
    }

    // pseudo legal
    public MoveList getPseudoLegalMoves() {
        return MoveGenerator.generateMoves(this);
    }

    // legal moves
    public MoveList getLegalMoves(){
        MoveList moveList = MoveGenerator.generateMoves(this);

        // Pour chaque coup, vérifier si le roi est en échec après le coup
        // Si le roi est en échec, le coup n'est pas légal
        // Sinon, le coup est légal
        for (int i = 0; i < moveList.size(); i++) {
            Move move = moveList.get(i);
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
        MoveList moveList = getLegalMoves();
        return moveList.size() == 0 && !isKingInCheck(whiteTurn);
    }

    // pseudo legal
    public boolean isCheckMate() {
        MoveList moveList = getLegalMoves();
        return moveList.size() == 0 && isKingInCheck(whiteTurn);
    }

    public boolean isKingInCheck(boolean whiteTurn) {
        // Generate opponent mask attack
        long opponentAttacks = MoveGenerator.generateMask(this, !whiteTurn);
        long king = whiteTurn ? whiteKing : blackKing;
        return (opponentAttacks & king) != 0;

    }

    public void processWhiteCastleKingSide(long fromBitboard) {
        // Roque du côté du roi
        whiteKing &= ~fromBitboard;
        whiteKing |= 1L << 1;
        whiteRooks &= ~(1L << 0);
        whiteRooks |= 1L << 2;

        whiteCastleKingSide = 0L;
        whiteCastleQueenSide = 0L;
    }

    public void processWhiteCastleQueenSide(long fromBitboard) {
        // Roque du côté de la reine
        whiteKing &= ~fromBitboard;
        whiteKing |= 1L << 5;
        whiteRooks &= ~(1L << 7);
        whiteRooks |= 1L << 4;

        whiteCastleQueenSide = 0L;
        whiteCastleKingSide = 0L;
    }

    public void processBlackCastleKingSide(long fromBitboard) {
        // Roque du côté du roi
        blackKing &= ~fromBitboard;
        blackKing |= 1L << 57;
        blackRooks &= ~(1L << 56);
        blackRooks |= 1L << 58;

        blackCastleKingSide = 0L;
        blackCastleQueenSide = 0L;
    }

    public void processBlackCastleQueenSide(long fromBitboard) {
        // Roque du côté de la reine
        blackKing &= ~fromBitboard;
        blackKing |= 1L << 61;
        blackRooks &= ~(1L << 63);
        blackRooks |= 1L << 60;

        blackCastleQueenSide = 0L;
        blackCastleKingSide = 0L;
    }

    private void updateBitBoard() {
        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;

        bitboard = whitePieces | blackPieces;
    }

    public static long getLSB(long bitboard) {
        return bitboard & -bitboard;
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

    public void printBitBoardRaw(){
        System.out.println(Long.toBinaryString(bitboard));
    }

    // public static int getSquare(long bitboard) {
    //     // use the square map
    //     for (int i = 0; i < 64; i++) {
    //         if ((SQUARES_MAP[i] & bitboard) != 0) {
    //             return i;
    //         }
    //     }
    //     return -1;
    // }

    public static int getSquare(long bitboard) {
        int lsbPosition = Long.numberOfTrailingZeros(bitboard);

        // Calculer la ligne et la colonne
        int row = lsbPosition / 8;  // Ligne (de 0 à 7)
        int col = lsbPosition % 8;  // Colonne (de 0 à 7)

        // Miroir horizontal: inverser la colonne mais garder la ligne
        int mirroredSquare = (row * 8) + (7 - col);
        
        return mirroredSquare;
    }

    private int getSquare(String position) {
        // Convertit une position comme "e2" en un index 0-63
        int file = position.charAt(0) - 'a';  // 'e' -> 4
        int rank = position.charAt(1) - '1';  // '2' -> 1
        int result = 8 * rank + file;   // 8 * 1 + (7 - 4) = 12
        return result;
    }

    public static int getSquare(int rank, int file) {
        return 8 * rank + (7 - file);
    }

    public static String getSquareIndexNotation(int square) {
        String[] files = {"a", "b", "c", "d", "e", "f", "g", "h"};
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

    public MoveList getCaptureMoves() {
        MoveList moveList = getLegalMoves();
        MoveList captureMoves = new MoveList(218);
        for (Move move : moveList) {
            if (isCaptureMove(move)) {
                captureMoves.add(move);
            }
        }
        System.out.println("Capture moves: " + captureMoves);
        return captureMoves;
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

    public int getPieceAt(long to){
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

    


}