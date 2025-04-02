package com.bitboard;

public class BoardHistory {
    public long bitboard, move;
    public long whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKing;
    public long blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKing;
    public long whiteCastleQueenSide, whiteCastleKingSide, blackCastleQueenSide, blackCastleKingSide;
    public long enPassantSquare;
    public boolean whiteTurn;
    public int evalMG, evalEG, phase;
    public long zobristKey;

    public BoardHistory() {
        // vide
    }

    public void copyFrom(BitBoard b, long move) {
        this.move = move;
        this.bitboard = b.bitboard;

        this.whitePawns = b.whitePawns;
        this.whiteKnights = b.whiteKnights;
        this.whiteBishops = b.whiteBishops;
        this.whiteRooks = b.whiteRooks;
        this.whiteQueens = b.whiteQueens;
        this.whiteKing = b.whiteKing;

        this.blackPawns = b.blackPawns;
        this.blackKnights = b.blackKnights;
        this.blackBishops = b.blackBishops;
        this.blackRooks = b.blackRooks;
        this.blackQueens = b.blackQueens;
        this.blackKing = b.blackKing;

        this.whiteCastleQueenSide = b.whiteCastleQueenSide;
        this.whiteCastleKingSide = b.whiteCastleKingSide;
        this.blackCastleQueenSide = b.blackCastleQueenSide;
        this.blackCastleKingSide = b.blackCastleKingSide;

        this.enPassantSquare = b.enPassantSquare;
        this.whiteTurn = b.whiteTurn;
        this.evalMG = b.currentEvalMG;
        this.evalEG = b.currentEvalEG;
        this.phase = b.phase;
        this.zobristKey = b.zobristKey;
    }

    public void restoreTo(BitBoard b) {
        b.bitboard = this.bitboard;

        b.whitePawns = this.whitePawns;
        b.whiteKnights = this.whiteKnights;
        b.whiteBishops = this.whiteBishops;
        b.whiteRooks = this.whiteRooks;
        b.whiteQueens = this.whiteQueens;
        b.whiteKing = this.whiteKing;

        b.blackPawns = this.blackPawns;
        b.blackKnights = this.blackKnights;
        b.blackBishops = this.blackBishops;
        b.blackRooks = this.blackRooks;
        b.blackQueens = this.blackQueens;
        b.blackKing = this.blackKing;

        b.whiteCastleQueenSide = this.whiteCastleQueenSide;
        b.whiteCastleKingSide = this.whiteCastleKingSide;
        b.blackCastleQueenSide = this.blackCastleQueenSide;
        b.blackCastleKingSide = this.blackCastleKingSide;

        b.enPassantSquare = this.enPassantSquare;
        b.whiteTurn = this.whiteTurn;
        b.currentEvalMG = this.evalMG;
        b.currentEvalEG = this.evalEG;
        b.phase = this.phase;
        b.zobristKey = this.zobristKey;

        b.whitePieces = b.whitePawns | b.whiteKnights | b.whiteBishops | b.whiteRooks | b.whiteQueens | b.whiteKing;
        b.blackPieces = b.blackPawns | b.blackKnights | b.blackBishops | b.blackRooks | b.blackQueens | b.blackKing;
    }
}
