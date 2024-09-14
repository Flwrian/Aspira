package com.bitboard;


import com.bitboard.BitBoard;
import java.util.Objects;

public final class Move {


    public static final byte DEFAULT = 0;
    public static final byte DOUBLE_PAWN_PUSH = 2;
    public static final byte EN_PASSENT = 3;
    public static final byte PROMOTION = 4;
    public static final byte CASTLING = 5;
    public static final byte CAPTURE = 6;

    // scores for ordering moves later
    public static final int PROMOTION_SCORE = 150;
    public static final int CAPTURE_SCORE = 100;
    public static final int CASTLING_SCORE = 150;
    public static final int DOUBLE_PAWN_PUSH_SCORE = 220;
    public static final int IS_CHECK_SCORE = 150;


    public int from;
    public int to;

    int pieceFrom;
    int pieceTo;

    byte    type;
    boolean isWhite;


    long orderPriority;
    int seeScore;



    public Move(int from, int to, int pieceFrom, int pieceTo) {
        this.from = from;
        this.to = to;
        this.pieceFrom = pieceFrom;
        this.pieceTo = pieceTo;
    }

    public Move(int from, int to, BitBoard board) {
        this.from = from;
        this.to = to;
        this.pieceFrom = board.getPiece(from);
        this.pieceTo = board.getPiece(to);
    }

    public Move(int from, int to, int pieceFrom, int pieceTo, byte type, int seeScore) {
        this.from = from;
        this.to = to;
        this.pieceFrom = pieceFrom;
        this.pieceTo = pieceTo;
        this.type = type;
        this.seeScore = seeScore;
    }

    public Move (String move) {
        // example move: e2e4
        int rankFrom = 8 - Character.getNumericValue(move.charAt(1));
        int fileFrom = move.charAt(0) - 'a';
        int rankTo = 8 - Character.getNumericValue(move.charAt(3));
        int fileTo = move.charAt(2) - 'a';

        this.from = (7 - rankFrom) * 8 + fileFrom;
        this.to = (7 - rankTo) * 8 + fileTo;

        this.pieceFrom = 0;
        this.pieceTo = 0;
        System.out.println("Move: " + move + " from: " + from + " to: " + to);
    }


    public Move copy() {
        Move copy = new Move(from, to, pieceFrom, pieceTo);
        copy.setType(type);
        copy.setOrderPriority(orderPriority);
        return copy;
    }

    public int getSeeScore() {
        return seeScore;
    }

    public void setSeeScore(int seeScore) {
        this.seeScore = seeScore;
    }

    public long getOrderPriority() { return orderPriority; }

    public void setOrderPriority(long orderPriority) {
        this.orderPriority = orderPriority;
    }

    public int getPieceFrom() {
        return pieceFrom;
    }

    public int getPieceTo() {
        return pieceTo;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public void setWhite(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public void setPieceFrom(int pieceFrom) {
        this.pieceFrom = pieceFrom;
    }

    public void setPieceTo(int pieceTo) {
        this.pieceTo = pieceTo;
    }


    public boolean isCastle_move() {
        return type == CASTLING;
    }

    public boolean isEn_passent_capture() {
        return type == EN_PASSENT;
    }

    public boolean isPromotion() {
        return type == PROMOTION;
    }

    public boolean isCapture() {
        return getPieceTo() != 0;
    }

    public int packMove(){
        int i = 0;
        i |= (this.getFrom());
        i |= (this.getTo()) << 6;
        i |= (this.getPieceFrom()+6) << 12;
        i |= (this.getPieceTo()+6) << 16;
        i |= (this.getType()) << 20;
        return i;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return from == move.from &&
                to == move.to &&
                pieceFrom == move.pieceFrom &&
                pieceTo == move.pieceTo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, pieceFrom, pieceTo);
    }

    // public String toString() {
    //     return "SlowMove{" +
    //             "from=" + from +
    //             ", to=" + to +
    //             ", pieceFrom=" + pieceFrom +
    //             ", pieceTo=" + pieceTo +
    //             '}';
    // }

    // string representation of the move
    @Override
    public String toString() {
        // if (type == CASTLING) {
        //     return "O-O";
        // }

        // if (type == EN_PASSENT) {
        //     return BitBoard.getSquareIndexNotation(from) + "x" + BitBoard.getSquareIndexNotation(to);
        // }

        if (type == PROMOTION) {
            // get if the piece is white or black
            
            String promotionPiece = "";
            if (isWhite) {
                switch (pieceTo) {
                    case BitBoard.QUEEN:
                        promotionPiece = "Q";
                        break;
                    case BitBoard.ROOK:
                        promotionPiece = "R";
                        break;
                    case BitBoard.BISHOP:
                        promotionPiece = "B";
                        break;
                    case BitBoard.KNIGHT:
                        promotionPiece = "N";
                        break;
                }
            } 

            else {
                switch (pieceTo) {
                    case BitBoard.QUEEN:
                        promotionPiece = "q";
                        break;
                    case BitBoard.ROOK:
                        promotionPiece = "r";
                        break;
                    case BitBoard.BISHOP:
                        promotionPiece = "b";
                        break;
                    case BitBoard.KNIGHT:
                        promotionPiece = "n";
                        break;
                }
            }

            return BitBoard.getSquareIndexNotation(from) + BitBoard.getSquareIndexNotation(to) + promotionPiece;
        }

        return BitBoard.getSquareIndexNotation(from) + BitBoard.getSquareIndexNotation(to);
    }


}