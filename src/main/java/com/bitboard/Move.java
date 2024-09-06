package com.bitboard;


import com.bitboard.BitBoard;
import java.util.Objects;

public final class Move {


    public static final byte DEFAULT = 0;
    public static final byte EN_PASSENT = 1;
    public static final byte PROMOTION = 2;
    public static final byte CASTLING = 3;


    int from;
    int to;

    int pieceFrom;
    int pieceTo;

    byte    type;


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

    @Override
    public String toString() {
        return "SlowMove{" +
                "from=" + from +
                ", to=" + to +
                ", pieceFrom=" + pieceFrom +
                ", pieceTo=" + pieceTo +
                '}';
    }


}