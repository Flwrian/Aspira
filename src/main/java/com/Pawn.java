package com;

public class Pawn extends Piece {


    public Pawn(int type, int position, Board board) {
        super(type, position, board);
    }

    @Override
    public boolean isValidMove(int position) {
        // Check if the pawn is trying to move to a position that is not on the board
        if (position < 0 || position > 63) {
            return false;
        }
        // Check if the pawn is trying to move to the same position
        if (position == this.getPosition()) {
            return false;
        }

        // Check if the pawn is trying to move to a position with a piece of the same color
        if (this.getBoard().getPiece(position) != null && this.getBoard().getPiece(position).getColor() == this.getColor()) {
            return false;
        }

        // For white pawns
        if (this.getColor()) {

            // Check if the pawn is trying to move en passant
            // if (this.getBoard().getPiece(position) == null && position - this.getPosition() == 7 && this.hasMoved == 1) {
            //     if (this.getBoard().getPiece(position - 8) != null && this.getBoard().getPiece(position - 8).getType() == 1) {
            //         Pawn pawn = (Pawn) this.getBoard().getPiece(position - 8);
            //         if (pawn.startingPosition == position - 16) {
            //             return true;
            //         }
            //     }
            // }

            // Check if the pawn is trying to move to a position that is not in front of it
            if (position < this.getPosition() || position > this.getPosition() + 16) {
                return false;
            }

            // Check if the pawn is trying to move to a position that is diagonal to it
            if (this.getBoard().getPiece(position) != null && (position - this.getPosition() == 9 || position - this.getPosition() == 7)) {
                return true;
            }
            // Check if the pawn is trying to move to a position that is more than 2 squares in front of it
            if (this.getPosition() < 8 && position > this.getPosition() + 7) {
                return false;
            }

            int row = this.getPosition() / 8;

            if (position == this.getPosition() + 8 || (position == this.getPosition() + 16 && row == 1)) {
                return true;
            }

        // For black pawns
        } else {

            // Check if the pawn is trying to move en passant
            // if (this.getBoard().getPiece(position) == null && this.getPosition() - position == 7 && this.hasMoved == 1) {
            //     if (this.getBoard().getPiece(position + 8) != null && this.getBoard().getPiece(position + 8).getType() == 1) {
            //         Pawn pawn = (Pawn) this.getBoard().getPiece(position + 8);
            //         if (pawn.startingPosition == position + 16) {
            //             return true;
            //         }
            //     }
            // }


            if (position > this.getPosition() || position < this.getPosition() - 16) {
                return false;
            }
            
            if (this.getBoard().getPiece(position) != null && (this.getPosition() - position == 9 || this.getPosition() - position == 7)) {
                return true;
            }
            if (this.getPosition() > 55 && position < this.getPosition() - 7) {
                return false;
            }
            int row = this.getPosition() / 8;
            if (position == this.getPosition() - 8 || (position == this.getPosition() - 16 && row == 6)) {
                return true;
            }
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
       return "Pawn{" +
               "type=" + type +
               ", position=" + position +
               ", color=" + getColor() +
               '}';
    }
}
