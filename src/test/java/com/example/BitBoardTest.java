// package com.example;

// import static org.junit.Assert.*;
// import org.junit.Before;
// import org.junit.Test;

// import com.Board;
// import com.bitboard.BitBoard;

// public class BitBoardTest {

//     private BitBoard board;

//     @Before
//     public void setUp() {
//         board = new BitBoard();
//     }

//     @Test
//     public void testInitialSetup() {
//         // Vérifier les positions initiales des pièces blanches
//         assertEquals(0x000000000000FF00L, board.getWhitePawns());
//         assertEquals(0x0000000000000042L, board.getWhiteKnights());
//         assertEquals(0x0000000000000024L, board.getWhiteBishops());
//         assertEquals(0x0000000000000081L, board.getWhiteRooks());
//         assertEquals(0x0000000000000008L, board.getWhiteKing());
//         assertEquals(0x0000000000000010L, board.getWhiteQueens());

//         // Vérifier les positions initiales des pièces noires
//         assertEquals(0x00FF000000000000L, board.getBlackPawns());
//         assertEquals(0x4200000000000000L, board.getBlackKnights());
//         assertEquals(0x2400000000000000L, board.getBlackBishops());
//         assertEquals(0x8100000000000000L, board.getBlackRooks());
//         assertEquals(0x0800000000000000L, board.getBlackKing());
//         assertEquals(0x1000000000000000L, board.getBlackQueens());

//         // Vérifier les droits de roque initiaux
//         assertEquals(1L, board.whiteCastleQueenSide);
//         assertEquals(1L, board.whiteCastleKingSide);
//         assertEquals(1L, board.blackCastleQueenSide);
//         assertEquals(1L, board.blackCastleKingSide);

//         // Vérifier l'état du bitboard
//         long expectedBitboard = board.getWhitePieces() | board.getBlackPieces();
//         assertEquals(expectedBitboard, board.getBoard());
//     }

//     @Test
//     public void testLoadFromFen() {
//         String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//         board.loadFromFen(fen);

//         // Vérifier que les pièces sont chargées correctement depuis la chaîne FEN
//         assertEquals(0x000000000000FF00L, board.getWhitePawns());
//         assertEquals(0x0000000000000042L, board.getWhiteKnights());
//         assertEquals(0x0000000000000024L, board.getWhiteBishops());
//         assertEquals(0x0000000000000081L, board.getWhiteRooks());
//         assertEquals(0x0000000000000008L, board.getWhiteKing());
//         assertEquals(0x0000000000000010L, board.getWhiteQueens());

//         assertEquals(0x00FF000000000000L, board.getBlackPawns());
//         assertEquals(0x4200000000000000L, board.getBlackKnights());
//         assertEquals(0x2400000000000000L, board.getBlackBishops());
//         assertEquals(0x8100000000000000L, board.getBlackRooks());
//         assertEquals(0x0800000000000000L, board.getBlackKing());
//         assertEquals(0x1000000000000000L, board.getBlackQueens());

//         assertEquals(0L, board.enPassantSquare);
//     }

//     @Test
//     public void testMakeMove() {
//         // Exemple de test pour un mouvement
//         board.loadFromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 1");
//         board.makeMove("e2e4");

//         // Vérifier les positions après le mouvement
//         String piece = board.getPieceAtSquare("e4");
//         assertEquals("P", piece);

//         piece = board.getPieceAtSquare("e2");
//         assertEquals(".", piece);

//         // Vérifier que le pion blanc a été déplacé
//         long expectedBitboard = -281474842429441L;
//         assertEquals(expectedBitboard, board.bitboard);
//     }

//     @Test
//     public void testGetPieceAtSquare() {
//         board.loadFromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 1");
//         assertEquals("P", board.getPieceAtSquare("e4"));
//         assertEquals(".", board.getPieceAtSquare("d4"));
//     }

//     @Test
//     public void testGetPieceAtSquareWithInvalidSquare() {
//         board.loadFromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 1");
//         assertEquals(".", board.getPieceAtSquare("z9"));
//     }

//     @Test
//     public void testGetPieceAtSquareWithEmptySquare() {
//         board.loadFromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 1");
//         assertEquals(".", board.getPieceAtSquare("e3"));
//     }

//     // castling
//     @Test
//     public void testMakeMoveWithCastlingBlack() {

//         board.loadFromFen("r3k2r/8/8/2PP1pp1/8/8/8/4K3 b kq - 0 1");
//         // Testing black king side castling
//         board.makeMove("e8g8");

//         long expectedBitboard = -8791026240698974200L;
//         long expectedBlackKing = 144115188075855872L;
//         long expectedBlackRooks = -8935141660703064064L;
//         long expectedBlackCastleKingSide = 0;
//         long expectedBlackCastleQueenSide = 0;

//         assertEquals(expectedBitboard, board.bitboard);
//         assertEquals(expectedBlackKing, board.blackKing);
//         assertEquals(expectedBlackRooks, board.blackRooks);
//         assertEquals(expectedBlackCastleKingSide, board.blackCastleKingSide);
//         assertEquals(expectedBlackCastleQueenSide, board.blackCastleQueenSide);

//     }

//     @Test
//     public void testMakeMoveWithCastlingWhite() {

//         board.loadFromFen("4k3/8/8/2PP1pp1/8/8/8/R3K2R w KQ - 0 1");
//         // Testing white king side castling
//         board.makeMove("e1g1");

//         long expectedBitboard = 576460984231657606L;
//         long expectedWhiteKing = 2L;
//         long expectedWhiteRooks = 132L;
//         long expectedWhiteCastleKingSide = 0;
//         long expectedWhiteCastleQueenSide = 0;

//         assertEquals(expectedBitboard, board.bitboard);
//         assertEquals(expectedWhiteKing, board.whiteKing);
//         assertEquals(expectedWhiteRooks, board.whiteRooks);
//         assertEquals(expectedWhiteCastleKingSide, board.whiteCastleKingSide);
//         assertEquals(expectedWhiteCastleQueenSide, board.whiteCastleQueenSide);

//     }

//     // verify enum square values
//     @Test
//     public void testSquare(){
//         assertEquals(0b10000000L, BitBoard.A1);
//         assertEquals(0b1000000L, BitBoard.B1);
//         assertEquals(0b100000L, BitBoard.C1);
//         assertEquals(0b10000L, BitBoard.D1);
//         assertEquals(0b1000L, BitBoard.E1);
//         assertEquals(0b100L, BitBoard.F1);
//         assertEquals(0b10L, BitBoard.G1);
//         assertEquals(0b1L, BitBoard.H1);
//     }

// }