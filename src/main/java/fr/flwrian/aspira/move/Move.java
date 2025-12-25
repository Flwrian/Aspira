package fr.flwrian.aspira.move;


import java.util.Objects;

import fr.flwrian.aspira.board.Board;

public final class Move {


    public static final byte DEFAULT = 0;
    public static final byte DOUBLE_PAWN_PUSH = 2;
    public static final byte EN_PASSANT = 3;
    public static final byte PROMOTION = 4;
    public static final byte CASTLING = 5;
    public static final byte CAPTURE = 6;

    // scores for ordering moves later
    public static final int PROMOTION_SCORE = 150;
    public static final int CAPTURE_SCORE = 100;
    public static final int CASTLING_SCORE = 150;
    public static final int DOUBLE_PAWN_PUSH_SCORE = 0;
    public static final int IS_CHECK_SCORE = 500;


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

    public Move(int from, int to, Board board) {
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

    public Move(String move) {
        // example move: e2e4 or e7e8Q for promotion
        int rankFrom = 8 - Character.getNumericValue(move.charAt(1));
        int fileFrom = move.charAt(0) - 'a';
        int rankTo = 8 - Character.getNumericValue(move.charAt(3));
        int fileTo = move.charAt(2) - 'a';

        this.from = (7 - rankFrom) * 8 + fileFrom;
        this.to = (7 - rankTo) * 8 + fileTo;

        this.pieceFrom = 0;
        this.pieceTo = 0;

        // Handle promotion
        if (move.length() == 5) {
            char promotionPiece = move.charAt(4);
            switch (promotionPiece) {
            case 'Q':
                this.pieceTo = Board.QUEEN;
                break;
            case 'R':
                this.pieceTo = Board.ROOK;
                break;
            case 'B':
                this.pieceTo = Board.BISHOP;
                break;
            case 'N':
                this.pieceTo = Board.KNIGHT;
                break;
            case 'q':
                this.pieceTo = Board.QUEEN;
                break;
            case 'r':
                this.pieceTo = Board.ROOK;
                break;
            case 'b':
                this.pieceTo = Board.BISHOP;
                break;
            case 'n':
                this.pieceTo = Board.KNIGHT;
                break;
            }
            this.type = PROMOTION;
        }

    }

    public Move(String move, Board board) {
        // example move: e2e4 or e7e8Q for promotion
        int rankFrom = 8 - Character.getNumericValue(move.charAt(1));
        int fileFrom = move.charAt(0) - 'a';
        int rankTo = 8 - Character.getNumericValue(move.charAt(3));
        int fileTo = move.charAt(2) - 'a';

        this.from = (7 - rankFrom) * 8 + fileFrom;
        this.to = (7 - rankTo) * 8 + fileTo;

        this.pieceFrom = board.getPiece(from);
        this.pieceTo = board.getPiece(to);
        this.isWhite = board.whiteTurn;

        // check if double pawn push
        if ((pieceFrom == 1 || pieceFrom == 7) && Math.abs(from - to) == 16) {
            this.type = DOUBLE_PAWN_PUSH;
            return;
        }

        // check if the move is en passant
        if ((pieceFrom == 1 || pieceFrom == 7) && pieceTo == 0) {
            // check if the move is en passant
            if (Long.numberOfTrailingZeros(board.enPassantSquare) == to) {
                this.type = EN_PASSANT;
                return;
            }
        }

        // if piece from is king and it tries to move two squares, it is a castling move
        //! ce code pue sa mere va falloir le changer
        if (pieceFrom == Board.KING || pieceFrom == Board.KING*2 && Math.abs(from - to) >= 2) {
            this.type = CASTLING;
            return;
        }

        // Handle promotion
        if (move.length() == 5) {
            char promotionPiece = move.charAt(4);
            switch (promotionPiece) {
            case 'Q':
                this.pieceTo = Board.QUEEN;
                break;
            case 'R':
                this.pieceTo = Board.ROOK;
                break;
            case 'B':
                this.pieceTo = Board.BISHOP;
                break;
            case 'N':
                this.pieceTo = Board.KNIGHT;
                break;
            case 'q':
                this.pieceTo = Board.QUEEN;
                break;
            case 'r':
                this.pieceTo = Board.ROOK;
                break;
            case 'b':
                this.pieceTo = Board.BISHOP;
                break;
            case 'n':
                this.pieceTo = Board.KNIGHT;
                break;
            }
            this.type = PROMOTION;
        }

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

    public boolean isEn_passant_capture() {
        return type == EN_PASSANT;
    }

    public boolean isPromotion() {
        return type == PROMOTION;
    }

    public boolean isCapture() {
        return type == CAPTURE;
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

        // if (type == EN_PASSANT) {
        //     return BitBoard.getSquareIndexNotation(from) + "x" + BitBoard.getSquareIndexNotation(to);
        // }

        if (type == PROMOTION) {
            // get if the piece is white or black
            
            String promotionPiece = "";
            if (isWhite) {
                switch (pieceTo) {
                    case Board.QUEEN:
                        promotionPiece = "q";
                        break;
                    case Board.ROOK:
                        promotionPiece = "r";
                        break;
                    case Board.BISHOP:
                        promotionPiece = "b";
                        break;
                    case Board.KNIGHT:
                        promotionPiece = "n";
                        break;
                }
            } 

            else {
                switch (pieceTo) {
                    case Board.QUEEN:
                        promotionPiece = "q";
                        break;
                    case Board.ROOK:
                        promotionPiece = "r";
                        break;
                    case Board.BISHOP:
                        promotionPiece = "b";
                        break;
                    case Board.KNIGHT:
                        promotionPiece = "n";
                        break;
                }
            }

            return Board.getSquareIndexNotation(from) + Board.getSquareIndexNotation(to) + promotionPiece;
        }

        return Board.getSquareIndexNotation(from) + Board.getSquareIndexNotation(to);
    }


}