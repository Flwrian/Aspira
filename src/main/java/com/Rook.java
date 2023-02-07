package com;


public class Rook extends Piece {
    public Rook(int type, int position, Board board) {
        super(type, position, board);
    }

    @Override
    public boolean isValidMove(int position) {
        // if (board.whiteTurn != this.getColor()) {
        //     return false;
        // }

        if (position < 0 || position > 63) {
            return false;
        }
        // Check if the rook is trying to move to the same position
        if (position == this.getPosition()) {
            return false;
        }

        // Check if the rook is trying to move to a position with a piece of the same
        // color
        if (this.getBoard().getPiece(position) != null
                && this.getBoard().getPiece(position).getColor() == this.getColor()) {
            return false;
        }

        // Check if the move is vertical
        if (position % 8 == this.getPosition() % 8) {
            int difference = position - this.getPosition();
            int step = difference / Math.abs(difference);

            for (int i = this.getPosition() + step; i != position; i += step) {
                if (this.getBoard().getPiece(i) != null) {
                    return false;
                }
            }
            return true;
        }
        // Check if the move is horizontal
        else if (position / 8 == this.getPosition() / 8) {
            int difference = position - this.getPosition();
            int step = difference / Math.abs(difference);

            for (int i = this.getPosition() + step; i != position; i += step) {
                if (this.getBoard().getPiece(i) != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void move(int position) {
            board.board[this.position] = 0;
            board.board[position] = this.type;
            this.position = position;
    }

    @Override
    public String toString() {
       // Return the type, position, and color of the piece. ex: "Bishop{type=3, position=0, color=0}"
       // color is getColor()
         return "Rook{type=" + this.type + ", position=" + this.position + ", color=" + this.getColor() + "}";
    }
}