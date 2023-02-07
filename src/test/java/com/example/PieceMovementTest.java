package com.example;

import org.junit.Test;

import com.Bishop;
import com.Board;
import com.King;
import com.Knight;
import com.Pawn;
import com.Queen;
import com.Rook;

import static org.junit.Assert.assertEquals;

/**
 * Chess Board Class
 * 
 * <p>
 * The type is an integer that represents the type of piece
 * 1 = white pawn
 * 2 = white knight
 * 3 = white bishop
 * 4 = white rook
 * 5 = white queen
 * 6 = white king
 * 7 = black pawn
 * 8 = black knight
 * 9 = black bishop
 * 10 = black rook
 * 11 = black queen
 * 12 = black king
 * <p>
 */
public class PieceMovementTest {

    @Test
    public void testWhitePawnMovement() {
        Board board = new Board();
        board.board = new int[64];

        board.board[8] = 1;

        Pawn pawn = (Pawn) board.getPiece(8);

        for (int i = 64; i < board.board.length; i++) {
            if (i == pawn.getPosition() + 8 || i == pawn.getPosition() + 16) {
                assertEquals(true, pawn.isValidMove(i));
            } else {
                assertEquals(false, pawn.isValidMove(i));
            }
        }
    }

    @Test
    public void testBlackPawnMovement() {
        Board board = new Board();
        board.board = new int[64];

        board.flip();

        board.board[63 - 8] = 7;

        Pawn pawn = (Pawn) board.getPiece(63 - 8);

        for (int i = 0; i < 64; i++) {
            if (i == pawn.getPosition() - 8 || i == pawn.getPosition() - 16) {
                assertEquals(true, pawn.isValidMove(i));
            } else {
                assertEquals(false, pawn.isValidMove(i));
            }
        }

    }

    @Test
    public void testKnightMovement() {
        Board board = new Board();
        board.board = new int[64];
        board.board[8] = 2;

        Knight knight = (Knight) board.getPiece(8);

        assertEquals(true, knight.isValidMove(18));
        assertEquals(false, knight.isValidMove(10));
        assertEquals(false, knight.isValidMove(16));
        assertEquals(false, knight.isValidMove(15));
    }

    @Test
    public void testRobustKnightMovementValid() {
        // Create a new empty board
        Board board = new Board();
        board.board = new int[64];

        // Set the knight at position 28
        board.board[28] = 2;

        // Get the knight
        Knight knight = (Knight) board.getPiece(28);

        // Verify the type is correct
        assertEquals(knight.getType(), 2);

        // Verify that every other square is empty
        for (int i = 0; i < 64; i++) {
            if (i != 28) {
                assertEquals(board.getPiece(i), null);
            }
        }

        // Create an array of valid moves
        int[] validMoves = { 11, 13, 22, 38, 45, 43, 34, 18 };

        // Verify that the knight can only move to the valid squares
        for (int i = 0; i < 64; i++) {
            boolean valid = false;
            for (int j = 0; j < validMoves.length; j++) {
                if (i == validMoves[j]) {
                    valid = true;
                }
            }
            assertEquals(valid, knight.isValidMove(i));
        }
    }

    @Test
    public void testBishopMovement() {
        Board board = new Board();
        board.board = new int[64];
        board.board[8] = 3;

        Bishop bishop = (Bishop) board.getPiece(8);

        assertEquals(true, bishop.isValidMove(17));
        assertEquals(false, bishop.isValidMove(16));
        assertEquals(false, bishop.isValidMove(10));
        assertEquals(false, bishop.isValidMove(15));

    }

    @Test
    public void testRobustBishopMovement() {
        // Create a new empty board
        Board board = new Board();
        board.board = new int[64];

        // Set the bishop at position 28
        board.board[28] = 3;

        // Get the bishop
        Bishop bishop = (Bishop) board.getPiece(28);

        // Verify the type is correct
        assertEquals(bishop.getType(), 3);

        // Verify that every other square is empty
        for (int i = 0; i < 64; i++) {
            if (i != 28) {
                assertEquals(board.getPiece(i), null);
            }
        }

        // Create an array of valid moves
        int[] validMoves = { 1, 10, 19, 37, 46, 55, 21, 14, 7, 35, 42, 49, 56 };

        // Verify that the bishop can only move to the valid squares
        for (int i = 0; i < 64; i++) {
            boolean valid = false;
            for (int j = 0; j < validMoves.length; j++) {
                if (i == validMoves[j]) {
                    valid = true;
                }
            }
            assertEquals(valid, bishop.isValidMove(i));
        }
    }

    @Test
    public void testRookMovement() {
        Board board = new Board();
        board.board = new int[64];
        board.board[8] = 4;

        Rook rook = (Rook) board.getPiece(8);

        assertEquals(true, rook.isValidMove(16));
        assertEquals(true, rook.isValidMove(9));
        assertEquals(false, rook.isValidMove(17));
        assertEquals(true, rook.isValidMove(10));
    }

    @Test
    public void testRobustRookMovement() {
        // Create a new empty board
        Board board = new Board();
        board.board = new int[64];

        // Set the rook at position 28
        board.board[28] = 4;

        // Get the rook
        Rook rook = (Rook) board.getPiece(28);

        // Verify the type is correct
        assertEquals(rook.getType(), 4);

        // Verify that every other square is empty
        for (int i = 0; i < 64; i++) {
            if (i != 28) {
                assertEquals(board.getPiece(i), null);
            }
        }

        // Valid moves are all squares in the same row and column of the rook (rook is
        // in pos 28)
        // So we need to check all squares in the same row and column of the square 28
        for (int i = 0; i < 64; i++) {
            if (i != 28) {
                if (i / 8 == rook.getPosition() / 8 || i % 8 == rook.getPosition() % 8) {
                    assertEquals(true, rook.isValidMove(i));
                } else {
                    assertEquals(false, rook.isValidMove(i));
                }
            }
        }
    }

    @Test
    public void testQueenMovement() {
        Board board = new Board();
        board.board = new int[64];
        board.board[8] = 5;

        Queen queen = (Queen) board.getPiece(8);

        assertEquals(true, queen.isValidMove(16));
        assertEquals(true, queen.isValidMove(17));
        assertEquals(true, queen.isValidMove(10));
        assertEquals(true, queen.isValidMove(15));
    }

    @Test
    public void testRobustQueenMovement() {
        // Create a new empty board
        Board board = new Board();
        board.board = new int[64];

        // Set the queen at position 28
        board.board[28] = 5;

        // Get the queen
        Queen queen = (Queen) board.getPiece(28);

        // Verify the type is correct
        assertEquals(queen.getType(), 5);

        // Verify that every other square is empty
        for (int i = 0; i < 64; i++) {
            if (i != 28) {
                assertEquals(board.getPiece(i), null);
            }
        }

        // Valid moves are all squares that the rook and bishop can move to
        // Work smarter not harder :)
        for (int i = 0; i < 64; i++) {
            assertEquals(
                    new Rook(queen.getType(), queen.getPosition(), queen.getBoard()).isValidMove(i)
                            || new Bishop(queen.getType(), queen.getPosition(), queen.getBoard()).isValidMove(i),
                    queen.isValidMove(i));
        }
    }

    @Test
    public void testKingMovement() {
        Board board = new Board();
        board.board = new int[64];
        board.board[8] = 6;

        King king = (King) board.getPiece(8);

        assertEquals(true, king.isValidMove(16));
        assertEquals(true, king.isValidMove(9));
        assertEquals(true, king.isValidMove(17));
        assertEquals(false, king.isValidMove(24));
    }

    @Test
    public void testRobustKingMovement() {
        // Create a new empty board
        Board board = new Board();
        board.board = new int[64];

        // Set the king at position 28
        board.board[28] = 6;

        // Get the king
        King king = (King) board.getPiece(28);

        // Verify the type is correct
        assertEquals(king.getType(), 6);

        // Verify that every other square is empty
        for (int i = 0; i < 64; i++) {
            if (i != 28) {
                assertEquals(board.getPiece(i), null);
            }
        }

        assertEquals(true, king.isValidMove(36));
        assertEquals(true, king.isValidMove(37));

        // The king can move to any square that is 1 square away from it
        // So we need to check all squares that are 1 square away from the king
        for (int i = 0; i < 64; i++) {
            if(i == 28){
                assertEquals(false, king.isValidMove(i));
                continue;   
            }

                
            
            if (((i / 8 == 28 / 8 - 1) || (i / 8 == 28 / 8) || (i / 8 == 28 / 8 + 1)) &&
                    ((i % 8 == 28 % 8 - 1) || (i % 8 == 28 % 8) || (i % 8 == 28 % 8 + 1))) {
                assertEquals(true, king.isValidMove(i));
            } else {
                assertEquals(false, king.isValidMove(i));
            }
        }
    }

    // @Test
    // public void testKingCastling() {
    //     Board board = new Board();
    //     board.board = new int[64];
    //     board.board[4] = 6;
    //     board.board[7] = 2;

    //     King king = (King) board.getPiece(4);

    //     assertEquals(true, king.isValidMove(6));
    // }

    @Test
    public void testPieceMovementBasicBoard() {
        Board board = new Board();
        board.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        int count = 0;

        // Starting position
        
        // Calculate the number of valid moves for each piece

        // First white Rook
        Rook rook = (Rook) board.getPiece(0);
        for (int i = 0; i < 64; i++) {
            if (rook.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(0, count);

        // White Knight
        Knight knight = (Knight) board.getPiece(1);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (knight.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(2, count);

        // White Bishop
        Bishop bishop = (Bishop) board.getPiece(2);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (bishop.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(0, count);

        // White Queen
        Queen queen = (Queen) board.getPiece(3);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (queen.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(0, count);

        // White King
        King king = (King) board.getPiece(4);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (king.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(0, count);

        // White Second Bishop
        bishop = (Bishop) board.getPiece(5);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (bishop.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(0, count);

        // White Second Knight
        knight = (Knight) board.getPiece(6);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (knight.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(2, count);

        // White Second Rook
        rook = (Rook) board.getPiece(7);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (rook.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(0, count);

        // White Pawns
        for (int i = 8; i < 16; i++) {
            Pawn pawn = (Pawn) board.getPiece(i);
            count = 0;
            for (int j = 0; j < 64; j++) {
                if (pawn.isValidMove(j)) {
                    count++;
                }
            }
            assertEquals(2, count);
        }

        // Black Rook
        rook = (Rook) board.getPiece(56);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (rook.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(0, count);

        // Black Knight
        knight = (Knight) board.getPiece(57);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (knight.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(2, count);

        // Black Bishop
        bishop = (Bishop) board.getPiece(58);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (bishop.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(0, count);

        // Black Queen
        queen = (Queen) board.getPiece(59);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (queen.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(0, count);

        // Black King
        king = (King) board.getPiece(60);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (king.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(0, count);

        // Black Second Bishop
        bishop = (Bishop) board.getPiece(61);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (bishop.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(0, count);

        // Black Second Knight
        knight = (Knight) board.getPiece(62);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (knight.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(2, count);

        // Black Second Rook
        rook = (Rook) board.getPiece(63);
        count = 0;
        for (int i = 0; i < 64; i++) {
            if (rook.isValidMove(i)) {
                count++;
            }
        }
        assertEquals(0, count);

        // Black Pawns
        for (int i = 48; i < 56; i++) {
            Pawn pawn = (Pawn) board.getPiece(i);
            count = 0;
            for (int j = 0; j < 64; j++) {
                if (pawn.isValidMove(j)) {
                    count++;
                }
            }
            assertEquals(2, count);
        }

        // GG WP


    }

}