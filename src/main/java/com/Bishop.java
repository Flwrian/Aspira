package com;

public class Bishop extends Piece {
    public Bishop(int type, int position, Board board) {
        super(type, position, board);
    }

    @Override
    public boolean isValidMove(int destination) {
        // if (board.whiteTurn != this.getColor()) {
        //     return false;
        // }
        if (destination < 0 || destination > 63) {
            return false;
        }
        if (destination == this.getPosition()) {
            return false;
        }
        if (this.getBoard().getPiece(destination) != null
                && this.getBoard().getPiece(destination).getColor() == this.getColor()) {
            return false;
        }

        int currentRow = this.getPosition() / 8;
        int currentCol = this.getPosition() % 8;
        int newRow = destination / 8;
        int newCol = destination % 8;

        if (currentRow == newRow || currentCol == newCol) {
            // Did not move diagonally
            return false;
        }

        if (Math.abs(newRow - currentRow) != Math.abs(newCol - currentCol)) {
            return false;
        }

        int rowOffset, colOffset;

        if (currentRow < newRow) {
            rowOffset = 1;
        } else {
            rowOffset = -1;
        }

        if (currentCol < newCol) {
            colOffset = 1;
        } else {
            colOffset = -1;
        }

        int y = currentCol + colOffset;
        for (int x = currentRow + rowOffset; x != newRow; x += rowOffset) {

            if (getBoard().getPiece(x * 8 + y) != null) {
                return false;
            }

            y += colOffset;
        }

        return true;

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
         return "Bishop{" +
                "type=" + type +
                ", position=" + position +
                ", color=" + getColor() +
                '}';
        
   }
}