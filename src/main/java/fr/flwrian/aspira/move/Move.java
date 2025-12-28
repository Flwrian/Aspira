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


    public int from;
    public int to;

    int pieceFrom;
    int capturedPiece;

    int promotion;
    int flags;
    
    long orderPriority;
    int seeScore;
    

    public Move(int from, int to, int pieceFrom, int capturedPiece) {
        this.from = from;
        this.to = to;
        this.pieceFrom = pieceFrom;
        this.capturedPiece = capturedPiece;
    }

    public Move(int from, int to, Board board) {
        this.from = from;
        this.to = to;
        this.pieceFrom = board.getPiece(from);
        this.capturedPiece = board.getPiece(to);
    }

    public Move(int from, int to, int pieceFrom, int capturedPiece, int promotion, int flags) {
        this.from = from;
        this.to = to;
        this.pieceFrom = pieceFrom;
        this.capturedPiece = capturedPiece;
        this.promotion = promotion;
        this.flags = flags;
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
        this.capturedPiece = board.getPiece(to);

        // check if double pawn push
        if (pieceFrom == Board.PAWN && Math.abs(from - to) == 16) {
            this.flags = DOUBLE_PAWN_PUSH;
            return;
        }

        // check if the move is en passant
        if (pieceFrom == Board.PAWN && capturedPiece == Board.EMPTY) {
            // check if the move is en passant
            if (Long.numberOfTrailingZeros(board.enPassantSquare) == to) {
                this.flags = EN_PASSANT;
                return;
            }
        }

        // if piece from is king and it tries to move two squares, it is a castling move.
        // We could check the destination square one by one but if the king move two squares, it is a castling move or else it is illegal.
        if (pieceFrom == Board.KING && Math.abs(from - to) >= 2) {
            this.flags = CASTLING;
            return;
        }

        // Handle promotion
        if (move.length() == 5) {
            char promotionPiece = move.charAt(4);
            switch (promotionPiece) {
            case 'Q':
                this.promotion = Board.QUEEN;
                break;
            case 'R':
                this.promotion = Board.ROOK;
                break;
            case 'B':
                this.promotion = Board.BISHOP;
                break;
            case 'N':
                this.promotion = Board.KNIGHT;
                break;
            case 'q':
                this.promotion = Board.QUEEN;
                break;
            case 'r':
                this.promotion = Board.ROOK;
                break;
            case 'b':
                this.promotion = Board.BISHOP;
                break;
            case 'n':
                this.promotion = Board.KNIGHT;
                break;
            }
            this.flags = PROMOTION;
        }

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

    public int getCapturedPiece() {
        return capturedPiece;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getFlags() {
        return flags;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return from == move.from &&
                to == move.to &&
                pieceFrom == move.pieceFrom &&
                capturedPiece == move.capturedPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, pieceFrom, capturedPiece);
    }

    // public String toString() {
    //     return "SlowMove{" +
    //             "from=" + from +
    //             ", to=" + to +
    //             ", pieceFrom=" + pieceFrom +
    //             ", capturedPiece=" + capturedPiece +
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

        if (flags == PROMOTION) {
            // get if the piece is white or black
            
            String promotionPiece = "";
            switch (promotion) {
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

            return Board.getSquareIndexNotation(from) + Board.getSquareIndexNotation(to) + promotionPiece;
        }

        return Board.getSquareIndexNotation(from) + Board.getSquareIndexNotation(to);
    }


}