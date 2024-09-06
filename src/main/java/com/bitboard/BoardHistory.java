package com.bitboard;

public class BoardHistory {

    public long bitboard;
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
    public long whiteCastleQueenSide;
    public long whiteCastleKingSide;
    public long blackCastleQueenSide;
    public long blackCastleKingSide;
    public long enPassantSquare;
    public boolean whiteTurn;

    public Move move;
    

    public BoardHistory(long bitboard, Move move, long whitePawns, long whiteKnights, long whiteBishops, long whiteRooks,
            long whiteQueens, long whiteKing, long blackPawns, long blackKnights, long blackBishops, long blackRooks,
            long blackQueens, long blackKing, long whiteCastleQueenSide, long whiteCastleKingSide,
            long blackCastleQueenSide, long blackCastleKingSide, long enPassantSquare, boolean whiteTurn) {
        this.bitboard = bitboard;
        this.whitePawns = whitePawns;
        this.whiteKnights = whiteKnights;
        this.whiteBishops = whiteBishops;
        this.whiteRooks = whiteRooks;
        this.whiteQueens = whiteQueens;
        this.whiteKing = whiteKing;
        this.blackPawns = blackPawns;
        this.blackKnights = blackKnights;
        this.blackBishops = blackBishops;
        this.blackRooks = blackRooks;
        this.blackQueens = blackQueens;
        this.blackKing = blackKing;
        this.whiteCastleQueenSide = whiteCastleQueenSide;
        this.whiteCastleKingSide = whiteCastleKingSide;
        this.blackCastleQueenSide = blackCastleQueenSide;
        this.blackCastleKingSide = blackCastleKingSide;
        this.enPassantSquare = enPassantSquare;
        this.whiteTurn = whiteTurn;

        this.move = move;
    }

}
