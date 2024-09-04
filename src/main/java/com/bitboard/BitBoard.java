package com.bitboard;

import java.io.PrintWriter;

public class BitBoard {
    public long whitePawns;
    public long whiteKnights;
    public long whiteBishops;
    public long whiteRooks;
    public long whiteQueens;
    public long whiteKing;

    public long blackPawns;
    public long blackKnights;
    public long blackBishops;
    public long blackRooks;
    public long blackQueens;
    public long blackKing;

    public long whitePieces;
    public long blackPieces;

    public long bitboard;

    private long whiteAttacks;
    private long blackAttacks;

    public long whiteCastleQueenSide;
    public long whiteCastleKingSide;
    public long blackCastleQueenSide;
    public long blackCastleKingSide;
    
    public long enPassantSquare;


    private long A_RANK = 0x00000000000000FFL;
    private long H_RANK = 0xFF00000000000000L;

    private long A_FILE = 0x0101010101010101L;
    private long H_FILE = 0x8080808080808080L;

    public static final String INITIAL_STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public BitBoard() {
        // Initialisation des pièces à leurs positions de départ
        whitePawns = 0x000000000000FF00L;
        whiteKnights = 0x0000000000000042L;
        whiteBishops = 0x0000000000000024L;
        whiteRooks = 0x0000000000000081L;
        whiteKing = 0x0000000000000008L;
        whiteQueens = 0x0000000000000010L;

        blackPawns = 0x00FF000000000000L;
        blackKnights = 0x4200000000000000L;
        blackBishops = 0x2400000000000000L;
        blackRooks = 0x8100000000000000L;
        blackKing = 0x0800000000000000L;
        blackQueens = 0x1000000000000000L;

        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;

        bitboard = whitePieces | blackPieces;

        whiteAttacks = 0L;
        blackAttacks = 0L;

        whiteCastleQueenSide = 1L;
        whiteCastleKingSide = 1L;
        blackCastleQueenSide = 1L;
        blackCastleKingSide = 1L;

        printBitBoard(bitboard);

    }

    public void loadFromFen(String fen) {
        String[] fenParts = fen.split(" ");
        String[] rows = fenParts[0].split("/");
        int row = 0;
        int col = 0;

        whitePawns = 0;
        whiteKnights = 0;
        whiteBishops = 0;
        whiteRooks = 0;
        whiteQueens = 0;
        whiteKing = 0;

        blackPawns = 0;
        blackKnights = 0;
        blackBishops = 0;
        blackRooks = 0;
        blackQueens = 0;
        blackKing = 0;

        whitePieces = 0;
        blackPieces = 0;

        whiteCastleQueenSide = 1L;
        whiteCastleKingSide = 1L;
        blackCastleQueenSide = 1L;
        blackCastleKingSide = 1L;

        enPassantSquare = 0L;

        for (String r : rows) {
            for (int i = 0; i < r.length(); i++) {
                char c = r.charAt(i);
                if (Character.isDigit(c)) {
                    col += Character.getNumericValue(c);
                } else {
                    long bitboard = 1L << (63 - (row * 8 + col));
                    switch (c) {
                        case 'P': whitePawns |= bitboard; break;
                        case 'N': whiteKnights |= bitboard; break;
                        case 'B': whiteBishops |= bitboard; break;
                        case 'R': whiteRooks |= bitboard; break;
                        case 'Q': whiteQueens |= bitboard; break;
                        case 'K': whiteKing |= bitboard; break;
                        case 'p': blackPawns |= bitboard; break;
                        case 'n': blackKnights |= bitboard; break;
                        case 'b': blackBishops |= bitboard; break;
                        case 'r': blackRooks |= bitboard; break;
                        case 'q': blackQueens |= bitboard; break;
                        case 'k': blackKing |= bitboard; break;
                    }
                    col++;
                }
            }
            row++;
            col = 0;
        }

        // castling rights
        if (fenParts[2].contains("K")) {
            whiteCastleKingSide = 1L;
        } else {
            whiteCastleKingSide = 0L;
        }

        if (fenParts[2].contains("Q")) {
            whiteCastleQueenSide = 1L;
        } else {
            whiteCastleQueenSide = 0L;
        }

        if (fenParts[2].contains("k")) {
            blackCastleKingSide = 1L;
        } else {
            blackCastleKingSide = 0L;
        }

        if (fenParts[2].contains("q")) {
            blackCastleQueenSide = 1L;
        } else {
            blackCastleQueenSide = 0L;
        }

        // en passant square
        if (!fenParts[3].equals("-")) {
            int square = getSquare(fenParts[3]);
            enPassantSquare = 1L << square;
        } else {
            enPassantSquare = 0L;
        }
        

        updateBitBoard();
    }

    public void printBitBoard(long bitBoard) {
        PrintWriter writer = new PrintWriter(System.out);
        for (int i = 0; i < 64; i++) {
            long mask = 1L << i;
            if ((bitBoard & mask) != 0) {
                writer.print("1 ");
            } else {
                writer.print(". ");
            }
            if ((i + 1) % 8 == 0) {
                writer.println();
            }
        }
        writer.flush();
    }
    

    public void printChessBoard() {
        String[] pieces = {"P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k"};
        String[] board = new String[64];
    
        for (int i = 0; i < 64; i++) {
            long bitboard = 1L << i;
            if ((whitePawns & bitboard) != 0) {
                board[i] = pieces[0];
            } else if ((whiteKnights & bitboard) != 0) {
                board[i] = pieces[1];
            } else if ((whiteBishops & bitboard) != 0) {
                board[i] = pieces[2];
            } else if ((whiteRooks & bitboard) != 0) {
                board[i] = pieces[3];
            } else if ((whiteQueens & bitboard) != 0) {
                board[i] = pieces[4];
            } else if ((whiteKing & bitboard) != 0) {
                board[i] = pieces[5];
            } else if ((blackPawns & bitboard) != 0) {
                board[i] = pieces[6];
            } else if ((blackKnights & bitboard) != 0) {
                board[i] = pieces[7];
            } else if ((blackBishops & bitboard) != 0) {
                board[i] = pieces[8];
            } else if ((blackRooks & bitboard) != 0) {
                board[i] = pieces[9];
            } else if ((blackQueens & bitboard) != 0) {
                board[i] = pieces[10];
            } else if ((blackKing & bitboard) != 0) {
                board[i] = pieces[11];
            } else {
                board[i] = ".";
            }
        }

        System.out.println("  h g f e d c b a");
        System.out.println("  -----------------");
        for (int i = 0; i < 8; i++) {
            System.out.print((i + 1) + "|");
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i * 8 + j] + " ");
            }
            System.out.println("|" + (i + 1));
        }
        System.out.println("  -----------------");
        System.out.println("  h g f e d c b a");
    }
    

            
    
    

    public String getPieceAtSquare(String square) {
        int index = getSquare(square);
        long bitboard = 1L << index;
        if ((whitePawns & bitboard) != 0) {
            return "P";
        } else if ((whiteKnights & bitboard) != 0) {
            return "N";
        } else if ((whiteBishops & bitboard) != 0) {
            return "B";
        } else if ((whiteRooks & bitboard) != 0) {
            return "R";
        } else if ((whiteQueens & bitboard) != 0) {
            return "Q";
        } else if ((whiteKing & bitboard) != 0) {
            return "K";
        } else if ((blackPawns & bitboard) != 0) {
            return "p";
        } else if ((blackKnights & bitboard) != 0) {
            return "n";
        } else if ((blackBishops & bitboard) != 0) {
            return "b";
        } else if ((blackRooks & bitboard) != 0) {
            return "r";
        } else if ((blackQueens & bitboard) != 0) {
            return "q";
        } else if ((blackKing & bitboard) != 0) {
            return "k";
        } else {
            return ".";
        }
    }

    // Get bitboard for a square
    public long getSquareBitboard(String square) {
        int index = getSquare(square);
        return 1L << index;
    }

    // Get the bitboard
    public long getBoard() {
        return bitboard;
    }

    // Get the white pieces bitboard
    public long getWhitePieces() {
        return whitePieces;
    }

    // Get the black pieces bitboard
    public long getBlackPieces() {
        return blackPieces;
    }

    // Get the white pawns bitboard
    public long getWhitePawns() {
        return whitePawns;
    }

    // Get the white knights bitboard
    public long getWhiteKnights() {
        return whiteKnights;
    }

    // Get the white bishops bitboard
    public long getWhiteBishops() {
        return whiteBishops;
    }

    // Get the white rooks bitboard
    public long getWhiteRooks() {
        return whiteRooks;
    }

    // Get the white queens bitboard
    public long getWhiteQueens() {
        return whiteQueens;
    }

    // Get the white king bitboard
    public long getWhiteKing() {
        return whiteKing;
    }

    // Get the black pawns bitboard
    public long getBlackPawns() {
        return blackPawns;
    }

    // Get the black knights bitboard
    public long getBlackKnights() {
        return blackKnights;
    }

    // Get the black bishops bitboard
    public long getBlackBishops() {
        return blackBishops;
    }

    // Get the black rooks bitboard
    public long getBlackRooks() {
        return blackRooks;
    }

    // Get the black queens bitboard
    public long getBlackQueens() {
        return blackQueens;
    }

    // Get the black king bitboard
    public long getBlackKing() {
        return blackKing;
    }

    public void makeMove(String move) {
        // 1. Interpréter le mouvement
        int fromSquare = getSquare(move.substring(0, 2));  // par exemple, "e2" -> 12 (case de départ)
        int toSquare = getSquare(move.substring(2, 4));    // par exemple, "e4" -> 28 (case d'arrivée)
        char promotion = move.length() == 5 ? move.charAt(4) : ' '; // Gestion de la promotion
    
        // 2. Déterminer quelle pièce se déplace
        long fromBitboard = 1L << fromSquare;
        long toBitboard = 1L << toSquare;
    
        // 3. Déterminer si la pièce est blanche ou noire
        boolean isWhite = (whitePieces & fromBitboard) != 0;
    
        // 4. Déplacer la pièce
        if ((whitePawns & fromBitboard) != 0 || (blackPawns & fromBitboard) != 0) {
            System.out.println("Pion");
            System.out.println(enPassantSquare);
            System.out.println(toBitboard);
            // Pion
            // Gestion de la prise en passant
            if ((enPassantSquare & toBitboard) != 0) {
                System.out.println("Prise en passant");
                if (isWhite) {
                    blackPawns &= ~(toBitboard >> 8); // Capturer le pion noir qui est pris en passant
                } else {
                    whitePawns &= ~(toBitboard << 8); // Capturer le pion blanc qui est pris en passant
                }
            }
            movePiece(isWhite ? whitePawns : blackPawns, fromBitboard, toBitboard);
    
            // Mettre à jour la case en passant si le pion avance de deux cases
            if (Math.abs(fromSquare - toSquare) == 16) {
                enPassantSquare = isWhite ? (toBitboard >> 8) : (toBitboard << 8);
            } else {
                enPassantSquare = 0L; // Réinitialiser la case en passant
            }
        } else if ((whiteKnights & fromBitboard) != 0 || (blackKnights & fromBitboard) != 0) {
            // Cavalier
            movePiece(isWhite ? whiteKnights : blackKnights, fromBitboard, toBitboard);
        } else if ((whiteBishops & fromBitboard) != 0 || (blackBishops & fromBitboard) != 0) {
            // Fou
            movePiece(isWhite ? whiteBishops : blackBishops, fromBitboard, toBitboard);
        } else if ((whiteRooks & fromBitboard) != 0 || (blackRooks & fromBitboard) != 0) {
            // Tour
            movePiece(isWhite ? whiteRooks : blackRooks, fromBitboard, toBitboard);
        } else if ((whiteQueens & fromBitboard) != 0 || (blackQueens & fromBitboard) != 0) {
            // Reine
            movePiece(isWhite ? whiteQueens : blackQueens, fromBitboard, toBitboard);
        } else if ((whiteKing & fromBitboard) != 0 || (blackKing & fromBitboard) != 0) {
            System.out.println("King");
            System.out.println(toSquare);
            // Roi
            // si le coup n'est pas un roque
            if (Math.abs(fromSquare - toSquare) != 2) {
                movePiece(isWhite ? whiteKing : blackKing, fromBitboard, toBitboard);
            } else {
                // Roque
                if (isWhite) {
                    if (toSquare == 5 && whiteCastleQueenSide != 0) {
                        System.out.println("Roque du côté de la reine");
                        // Roque du côté de la reine
                        whiteKing &= ~fromBitboard;
                        whiteKing |= 1L << 5;
                        whiteRooks &= ~(1L << 7);
                        whiteRooks |= 1L << 4;
                    } else if (toSquare == 1 && whiteCastleKingSide != 0) {
                        System.out.println("Roque du côté du roi");
                        // Roque du côté du roi
                        whiteKing &= ~fromBitboard;
                        whiteKing |= 1L << 1;
                        whiteRooks &= ~(1L << 0);
                        whiteRooks |= 1L << 2;
                    }
                } else {
                    if (toSquare == 61 && blackCastleQueenSide != 0) {
                        System.out.println("Roque du côté de la reine");
                        // Roque du côté de la reine
                        blackKing &= ~fromBitboard;
                        blackKing |= 1L << 61;
                        blackRooks &= ~(1L << 63);
                        blackRooks |= 1L << 60;
                    } else if (toSquare == 57 && blackCastleKingSide != 0) {
                        System.out.println("Roque du côté du roi");
                        // Roque du côté du roi
                        blackKing &= ~fromBitboard;
                        blackKing |= 1L << 57;
                        blackRooks &= ~(1L << 56);
                        blackRooks |= 1L << 58;
                    }
                }
            }
        }
    
        updateBitBoard();
    }

    private void updateBitBoard() {
        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;

        bitboard = whitePieces | blackPieces;
    }
    
    private int getSquare(String position) {
        // Convertit une position comme "e2" en un index 0-63
        int file = position.charAt(0) - 'a';  // 'e' -> 4
        int rank = position.charAt(1) - '1';  // '2' -> 1
        int result = 8 * rank + (7 - file);   // 8 * 1 + (7 - 4) = 12
        System.out.println(result);
        return result;
    }
    
    private void movePiece(long pieceBitboard, long fromBitboard, long toBitboard) {
        pieceBitboard &= ~fromBitboard;  // Retirer la pièce de la case de départ
        pieceBitboard |= toBitboard;     // Placer la pièce sur la case d'arrivée

        // Update the piece bitboard
        if ((whitePawns & fromBitboard) != 0 || (blackPawns & fromBitboard) != 0) {
            if ((whitePawns & fromBitboard) != 0) {
                whitePawns &= ~fromBitboard;
                whitePawns |= toBitboard;
            } else {
                blackPawns &= ~fromBitboard;
                blackPawns |= toBitboard;
            }
        } else if ((whiteKnights & fromBitboard) != 0 || (blackKnights & fromBitboard) != 0) {
            if ((whiteKnights & fromBitboard) != 0) {
                whiteKnights &= ~fromBitboard;
                whiteKnights |= toBitboard;
            } else {
                blackKnights &= ~fromBitboard;
                blackKnights |= toBitboard;
            }
        } else if ((whiteBishops & fromBitboard) != 0 || (blackBishops & fromBitboard) != 0) {
            if ((whiteBishops & fromBitboard) != 0) {
                whiteBishops &= ~fromBitboard;
                whiteBishops |= toBitboard;
            } else {
                blackBishops &= ~fromBitboard;
                blackBishops |= toBitboard;
            }
        } else if ((whiteRooks & fromBitboard) != 0 || (blackRooks & fromBitboard) != 0) {
            if ((whiteRooks & fromBitboard) != 0) {
                whiteRooks &= ~fromBitboard;
                whiteRooks |= toBitboard;
            } else {
                blackRooks &= ~fromBitboard;
                blackRooks |= toBitboard;
            }
        } else if ((whiteQueens & fromBitboard) != 0 || (blackQueens & fromBitboard) != 0) {
            if ((whiteQueens & fromBitboard) != 0) {
                whiteQueens &= ~fromBitboard;
                whiteQueens |= toBitboard;
            } else {
                blackQueens &= ~fromBitboard;
                blackQueens |= toBitboard;
            }
        } else if ((whiteKing & fromBitboard) != 0 || (blackKing & fromBitboard) != 0) {
            if ((whiteKing & fromBitboard) != 0) {
                whiteKing &= ~fromBitboard;
                whiteKing |= toBitboard;
            } else {
                blackKing &= ~fromBitboard;
                blackKing |= toBitboard;
            }

        }

        updateBitBoard();
        
    }
    
    private void capturePiece(long toBitboard, boolean isWhite) {
        if (isWhite) {
            blackPawns &= ~toBitboard;
            blackKnights &= ~toBitboard;
            blackBishops &= ~toBitboard;
            blackRooks &= ~toBitboard;
            blackQueens &= ~toBitboard;
            blackKing &= ~toBitboard;
        } else {
            whitePawns &= ~toBitboard;
            whiteKnights &= ~toBitboard;
            whiteBishops &= ~toBitboard;
            whiteRooks &= ~toBitboard;
            whiteQueens &= ~toBitboard;
            whiteKing &= ~toBitboard;
        }
    }
    
    private void promotePiece(int square, char promotion, boolean isWhite) {
        long bitboard = 1L << square;
        if (isWhite) {
            whitePawns &= ~bitboard;  // Retirer le pion
            switch (promotion) {
                case 'Q': whiteQueens |= bitboard; break;
                case 'R': whiteRooks |= bitboard; break;
                case 'B': whiteBishops |= bitboard; break;
                case 'N': whiteKnights |= bitboard; break;
            }
        } else {
            blackPawns &= ~bitboard;  // Retirer le pion
            switch (promotion) {
                case 'Q': blackQueens |= bitboard; break;
                case 'R': blackRooks |= bitboard; break;
                case 'B': blackBishops |= bitboard; break;
                case 'N': blackKnights |= bitboard; break;
            }
        }
    }

    


}

