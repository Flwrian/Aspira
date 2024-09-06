package com.bitboard;

import java.io.PrintWriter;
import java.util.Stack;

public class BitBoard {

    public boolean whiteTurn;

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

    public long whiteCastleQueenSide;
    public long whiteCastleKingSide;
    public long blackCastleQueenSide;
    public long blackCastleKingSide;
    
    public long enPassantSquare;

    // valid
    public static final long RANK_1 = 0x00000000000000FFL;
    public static final long RANK_2 = 0x000000000000FF00L;
    public static final long RANK_3 = 0x0000000000FF0000L;
    public static final long RANK_4 = 0x00000000FF000000L;
    public static final long RANK_5 = 0x000000FF00000000L;
    public static final long RANK_6 = 0x0000FF0000000000L;
    public static final long RANK_7 = 0x00FF000000000000L;
    public static final long RANK_8 = 0xFF00000000000000L;

    // valid
    public static final long FILE_H = 0x0101010101010101L;
    public static final long FILE_G = 0x0202020202020202L;
    public static final long FILE_F = 0x0404040404040404L;
    public static final long FILE_E = 0x0808080808080808L;
    public static final long FILE_D = 0x1010101010101010L;
    public static final long FILE_C = 0x2020202020202020L;
    public static final long FILE_B = 0x4040404040404040L;
    public static final long FILE_A = 0x8080808080808080L;

    public static final long A1 = 0b10000000L;
    public static final long B1 = A1 >>> 1;
    public static final long C1 = B1 >>> 1;
    public static final long D1 = C1 >>> 1;
    public static final long E1 = D1 >>> 1;
    public static final long F1 = E1 >>> 1;
    public static final long G1 = F1 >>> 1;
    public static final long H1 = G1 >>> 1;

    public static final long A2 = A1 << 8;
    public static final long B2 = B1 << 8;
    public static final long C2 = C1 << 8;
    public static final long D2 = D1 << 8;
    public static final long E2 = E1 << 8;
    public static final long F2 = F1 << 8;
    public static final long G2 = G1 << 8;
    public static final long H2 = H1 << 8;

    public static final long A3 = A2 << 8;
    public static final long B3 = B2 << 8;
    public static final long C3 = C2 << 8;
    public static final long D3 = D2 << 8;
    public static final long E3 = E2 << 8;
    public static final long F3 = F2 << 8;
    public static final long G3 = G2 << 8;
    public static final long H3 = H2 << 8;

    public static final long A4 = A3 << 8;
    public static final long B4 = B3 << 8;
    public static final long C4 = C3 << 8;
    public static final long D4 = D3 << 8;
    public static final long E4 = E3 << 8;
    public static final long F4 = F3 << 8;
    public static final long G4 = G3 << 8;
    public static final long H4 = H3 << 8;

    public static final long A5 = A4 << 8;
    public static final long B5 = B4 << 8;
    public static final long C5 = C4 << 8;
    public static final long D5 = D4 << 8;
    public static final long E5 = E4 << 8;
    public static final long F5 = F4 << 8;
    public static final long G5 = G4 << 8;
    public static final long H5 = H4 << 8;

    public static final long A6 = A5 << 8;
    public static final long B6 = B5 << 8;
    public static final long C6 = C5 << 8;
    public static final long D6 = D5 << 8;
    public static final long E6 = E5 << 8;
    public static final long F6 = F5 << 8;
    public static final long G6 = G5 << 8;
    public static final long H6 = H5 << 8;

    public static final long A7 = A6 << 8;
    public static final long B7 = B6 << 8;
    public static final long C7 = C6 << 8;
    public static final long D7 = D6 << 8;
    public static final long E7 = E6 << 8;
    public static final long F7 = F6 << 8;
    public static final long G7 = G6 << 8;
    public static final long H7 = H6 << 8;

    public static final long A8 = A7 << 8;
    public static final long B8 = B7 << 8;
    public static final long C8 = C7 << 8;
    public static final long D8 = D7 << 8;
    public static final long E8 = E7 << 8;
    public static final long F8 = F7 << 8;
    public static final long G8 = G7 << 8;
    public static final long H8 = H7 << 8;

    // diagonals and anti-diagonals
    public static final long DIAGONAL_A1_H8 = 0x0102040810204080L;
    public static final long DIAGONAL_A2_G8 = 0x0204081020408000L;
    public static final long DIAGONAL_A3_F8 = 0x0408102040800000L;
    public static final long DIAGONAL_A4_E8 = 0x0810204080000000L;
    public static final long DIAGONAL_A5_D8 = 0x1020408000000000L;
    public static final long DIAGONAL_A6_C8 = 0x2040800000000000L;
    public static final long DIAGONAL_A7_B8 = 0x4080000000000000L;
    public static final long DIAGONAL_A8_A8 = 0x8000000000000000L;

    public static final long DIAGONAL_B1_H7 = 0x0001020408102040L;
    public static final long DIAGONAL_C1_H6 = 0x0000010204081020L;
    public static final long DIAGONAL_D1_H5 = 0x0000000102040810L;
    public static final long DIAGONAL_E1_H4 = 0x0000000001020408L;
    public static final long DIAGONAL_F1_H3 = 0x0000000000010204L;
    public static final long DIAGONAL_G1_H2 = 0x0000000000000102L;
    public static final long DIAGONAL_H1_H1 = 0x0000000000000001L;

    public static final long DIAGONAL_H1_A8 = 0x8040201008040201L;
    public static final long DIAGONAL_G1_A7 = 0x4020100804020100L;
    public static final long DIAGONAL_F1_A6 = 0x2010080402010000L;
    public static final long DIAGONAL_E1_A5 = 0x1008040201000000L;
    public static final long DIAGONAL_D1_A4 = 0x0804020100000000L;
    public static final long DIAGONAL_C1_A3 = 0x0402010000000000L;
    public static final long DIAGONAL_B1_A2 = 0x0201000000000000L;
    public static final long DIAGONAL_A1_A1 = 0x0100000000000000L;


    // valid
    public static final long WHITE_KING_SIDE_CASTLE_KING_SQUARE = 0x0000000000000002L;
    public static final long BLACK_KING_SIDE_CASTLE_KING_SQUARE = 0x0200000000000000L;

    // valid
    public static final long WHITE_QUEEN_SIDE_CASTLE_KING_SQUARE = 0x0000000000000020L;
    public static final long BLACK_QUEEN_SIDE_CASTLE_KING_SQUARE = 0x2000000000000000L;

    // valid
    public static final long WHITE_KING_SIDE_ROOK_SQUARE = 0x0000000000000001L;
    public static final long WHITE_QUEEN_SIDE_ROOK_SQUARE = 0x0000000000000080L;

    // valid
    public static final long BLACK_KING_SIDE_ROOK_SQUARE = 0x0100000000000000L;
    public static final long BLACK_QUEEN_SIDE_ROOK_SQUARE = 0x8000000000000000L;

    // valid
    public static final long WHITE_KING_SIDE_CASTLE_EMPTY_SQUARES_MASK = 0b110L;
    public static final long WHITE_QUEEN_SIDE_CASTLE_EMPTY_SQUARES_MASK = 0b1110000L;

    // valid
    public static final long BLACK_KING_SIDE_CASTLE_EMPTY_SQUARES_MASK = 0x600000000000000L;
    public static final long BLACK_QUEEN_SIDE_CASTLE_EMPTY_SQUARES_MASK = 0x7000000000000000L;

    // valid
    public static final long WHITE_KING_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK = 0b1110L;
    public static final long WHITE_QUEEN_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK = 0b111000L;

    // valid
    public static final long BLACK_KING_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK = 0xE00000000000000L;
    public static final long BLACK_QUEEN_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK = 0x3800000000000000L;

    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;

    private Stack<BoardHistory> history;

    

    public static final String INITIAL_STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public BitBoard() {

        whiteTurn = true;

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

        whiteCastleQueenSide = 1L;
        whiteCastleKingSide = 1L;
        blackCastleQueenSide = 1L;
        blackCastleKingSide = 1L;

        enPassantSquare = 0L;

        history = new Stack<>();


    }

    private void saveBoardHistory(Move move) {
        BoardHistory boardHistory = new BoardHistory(bitboard, move, whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKing, blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKing, whiteCastleQueenSide, whiteCastleKingSide, blackCastleQueenSide, blackCastleKingSide, enPassantSquare, whiteTurn);
        history.push(boardHistory);
    }

    public void undoMove() {
        if (!history.isEmpty()) {
            BoardHistory boardHistory = history.pop();
            restoreBoardHistory(boardHistory);
        }
    }

    public void restoreBoardHistory(BoardHistory boardHistory) {
        bitboard = boardHistory.bitboard;
        whitePawns = boardHistory.whitePawns;
        whiteKnights = boardHistory.whiteKnights;
        whiteBishops = boardHistory.whiteBishops;
        whiteRooks = boardHistory.whiteRooks;
        whiteQueens = boardHistory.whiteQueens;
        whiteKing = boardHistory.whiteKing;
        blackPawns = boardHistory.blackPawns;
        blackKnights = boardHistory.blackKnights;
        blackBishops = boardHistory.blackBishops;
        blackRooks = boardHistory.blackRooks;
        blackQueens = boardHistory.blackQueens;
        blackKing = boardHistory.blackKing;
        whiteCastleQueenSide = boardHistory.whiteCastleQueenSide;
        whiteCastleKingSide = boardHistory.whiteCastleKingSide;
        blackCastleQueenSide = boardHistory.blackCastleQueenSide;
        blackCastleKingSide = boardHistory.blackCastleKingSide;
        enPassantSquare = boardHistory.enPassantSquare;
        whiteTurn = boardHistory.whiteTurn;
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
        
        // Bordure supérieure
        writer.println("   +-------------------------------+");
        writer.println("   | a   b   c   d   e   f   g   h |");
        writer.println("   +-------------------------------+");
    
        // Parcourir les rangées de haut en bas
        for (int rank = 7; rank >= 0; rank--) {
            writer.print((rank + 1) + "  |"); // Numéro de rangée sur le côté gauche
            
            // Parcourir chaque colonne de la rangée
            for (int file = 7; file >= 0; file--) {
                int squareIndex = rank * 8 + file;
                long mask = 1L << squareIndex;
                
                if ((bitBoard & mask) != 0) {
                    writer.print(" 1 ");
                } else {
                    writer.print("   ");
                }
    
                // Ajouter un séparateur "|"
                if (file != 0) {
                    writer.print("|");
                }
            }
            
            writer.println("| " + (rank + 1)); // Numéro de rangée sur le côté droit
    
            // Ajouter des séparateurs entre les rangées sauf pour la dernière
            if (rank > 0) {
                writer.println("   |---|---|---|---|---|---|---|---|");
            }
        }
    
        // Bordure inférieure
        writer.println("   +-------------------------------+");
        writer.println("   | a   b   c   d   e   f   g   h |");
        writer.println("   +-------------------------------+");
        
        writer.flush();
    }
    
    
    

    public void printChessBoard() {
        String[] pieces = {"P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k"};
        String[] board = new String[64];
    
        // Remplir le tableau board avec les pièces ou des points pour les cases vides
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
                board[i] = " ";
            }
        }
    
        // Bordure supérieure
        System.out.println("   +-------------------------------+");
        System.out.println("   | a   b   c   d   e   f   g   h |");
        System.out.println("   +-------------------------------+");
    
        // Parcourir les rangées de haut en bas
        for (int rank = 7; rank >= 0; rank--) {
            System.out.print((rank + 1) + "  |"); // Numéro de rangée sur le côté gauche
            
            // Parcourir chaque colonne de la rangée
            for (int file = 7; file >= 0; file--) {
                int squareIndex = rank * 8 + file;
                System.out.print(" " + board[squareIndex] + " ");
                
                // Ajouter un séparateur "|"
                if (file != 0) {
                    System.out.print("|");
                }
            }
    
            // Numéro de rangée sur le côté droit
            System.out.println("| " + (rank + 1));
            
            // Ajouter des séparateurs entre les rangées sauf pour la dernière
            if (rank > 0) {
                System.out.println("   |---|---|---|---|---|---|---|---|");
            }
        }
    
        // Bordure inférieure
        System.out.println("   +-------------------------------+");
        System.out.println("   | a   b   c   d   e   f   g   h |");
        System.out.println("   +-------------------------------+");
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

    public void makeMove(Move move) {
    
        // save 
        saveBoardHistory(move);
        
        // 1. Interpréter le mouvement
        // int fromSquare = getSquare(move.substring(0, 2));  // par exemple, "e2" -> 12 (case de départ)
        // int toSquare = getSquare(move.substring(2, 4));    // par exemple, "e4" -> 28 (case d'arrivée)
        // char promotion = move.length() == 5 ? move.charAt(4) : ' '; // Gestion de la promotion

        int fromSquare = move.from;
        int toSquare = move.to;
    
        // 2. Déterminer quelle pièce se déplace
        long fromBitboard = 1L << fromSquare;
        long toBitboard = 1L << toSquare;
    
        // 3. Déterminer si la pièce est blanche ou noire
        boolean isWhite = (whitePieces & fromBitboard) != 0;
    
        // 4. Déplacer la pièce
        if ((whitePawns & fromBitboard) != 0 || (blackPawns & fromBitboard) != 0) {
            // Pion
            // Gestion de la prise en passant
            if ((enPassantSquare & toBitboard) != 0) {
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

            // Gestion de la promotion
            if(move.type == Move.PROMOTION) {
                if (isWhite) {
                    int piece = move.pieceFrom;
                    switch (piece) {
                        case PAWN: whitePawns &= ~toBitboard; break;
                        case KNIGHT: whiteKnights |= toBitboard; break;
                        case BISHOP: whiteBishops |= toBitboard; break;
                        case ROOK: whiteRooks |= toBitboard; break;
                        case QUEEN: whiteQueens |= toBitboard; break;
                    }
                } else {
                    int piece = move.pieceFrom;
                    switch (piece) {
                        case PAWN: blackPawns &= ~toBitboard; break;
                        case KNIGHT: blackKnights |= toBitboard; break;
                        case BISHOP: blackBishops |= toBitboard; break;
                        case ROOK: blackRooks |= toBitboard; break;
                        case QUEEN: blackQueens |= toBitboard; break;
                    }
                }
            }
        } else if ((whiteKnights & fromBitboard) != 0 || (blackKnights & fromBitboard) != 0) {
            // Cavalier
            movePiece(isWhite ? whiteKnights : blackKnights, fromBitboard, toBitboard);
        } else if ((whiteBishops & fromBitboard) != 0 || (blackBishops & fromBitboard) != 0) {
            // Fou
            movePiece(isWhite ? whiteBishops : blackBishops, fromBitboard, toBitboard);
        } else if ((whiteRooks & fromBitboard) != 0 || (blackRooks & fromBitboard) != 0) {
            // Tour
            // Gestion des roques
            if (isWhite) {
                if (fromSquare == 0) {
                    whiteCastleQueenSide = 0L;
                } else if (fromSquare == 7) {
                    whiteCastleKingSide = 0L;
                }
            } else {
                if (fromSquare == 56) {
                    blackCastleQueenSide = 0L;
                } else if (fromSquare == 63) {
                    blackCastleKingSide = 0L;
                }
            }
            movePiece(isWhite ? whiteRooks : blackRooks, fromBitboard, toBitboard);
        } else if ((whiteQueens & fromBitboard) != 0 || (blackQueens & fromBitboard) != 0) {
            // Reine
            movePiece(isWhite ? whiteQueens : blackQueens, fromBitboard, toBitboard);
        } else if ((whiteKing & fromBitboard) != 0 || (blackKing & fromBitboard) != 0) {
            // Roi
            // si le coup n'est pas un roque
            if (Math.abs(fromSquare - toSquare) != 2) {
                movePiece(isWhite ? whiteKing : blackKing, fromBitboard, toBitboard);
            } else {
                // Roque
                if (isWhite) {
                    if (toSquare == 5 && whiteCastleQueenSide != 0) {
                        processWhiteCastleQueenSide(fromBitboard);
                    } else if (toSquare == 1 && whiteCastleKingSide != 0) {
                        processWhiteCastleKingSide(fromBitboard);
                    }
                } else {
                    if (toSquare == 61 && blackCastleQueenSide != 0) {
                        processBlackCastleQueenSide(fromBitboard);
                    } else if (toSquare == 57 && blackCastleKingSide != 0) {
                        processBlackCastleKingSide(fromBitboard);
                    } else if (toSquare == 57 && blackCastleKingSide != 0) {
                        processBlackCastleKingSide(fromBitboard);
                    }
                }
            }
        }

        // 6. Mettre à jour le tour
        whiteTurn = !whiteTurn;

        updateBitBoard();
    }

    // legal moves
    public MoveList getLegalMoves(){
        MoveList moveList = MoveGenerator.generateMoves(this);
        // Pour chaque coup, vérifier si le roi est en échec après le coup
        // Si le roi est en échec, le coup n'est pas légal
        // Sinon, le coup est légal
        for (int i = 0; i < moveList.size(); i++) {
            Move move = moveList.get(i);
            makeMove(move);
            if (isKingInCheck(whiteTurn)) {
                moveList.remove(i);
                i--;
            }
            undoMove();
        }

        return moveList;
    }

    private boolean isKingInCheck(boolean whiteTurn) {
        // Generate opponent mask attack
        long opponentAttackMask = MoveGenerator.generateOpponentMask(this);

        // Get king square
        long kingSquare = whiteTurn ? whiteKing : blackKing;

        return (opponentAttackMask & kingSquare) != 0;
    }

    public void processWhiteCastleKingSide(long fromBitboard) {
        // Roque du côté du roi
        whiteKing &= ~fromBitboard;
        whiteKing |= 1L << 1;
        whiteRooks &= ~(1L << 0);
        whiteRooks |= 1L << 2;

        whiteCastleKingSide = 0L;
        whiteCastleQueenSide = 0L;
    }

    public void processWhiteCastleQueenSide(long fromBitboard) {
        // Roque du côté de la reine
        whiteKing &= ~fromBitboard;
        whiteKing |= 1L << 5;
        whiteRooks &= ~(1L << 7);
        whiteRooks |= 1L << 4;

        whiteCastleQueenSide = 0L;
        whiteCastleKingSide = 0L;
    }

    public void processBlackCastleKingSide(long fromBitboard) {
        // Roque du côté du roi
        blackKing &= ~fromBitboard;
        blackKing |= 1L << 57;
        blackRooks &= ~(1L << 56);
        blackRooks |= 1L << 58;

        blackCastleKingSide = 0L;
        blackCastleQueenSide = 0L;
    }

    public void processBlackCastleQueenSide(long fromBitboard) {
        // Roque du côté de la reine
        blackKing &= ~fromBitboard;
        blackKing |= 1L << 61;
        blackRooks &= ~(1L << 63);
        blackRooks |= 1L << 60;

        blackCastleQueenSide = 0L;
        blackCastleKingSide = 0L;
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

    public static long getLSB(long bitboard) {
        return bitboard & -bitboard;
    }

    public static long getMSB(long bitboard) {
        return Long.highestOneBit(bitboard);
    }

    // Get piece method
    public int getPiece(int square) {
        long bitboard = 1L << square;
        if ((whitePawns & bitboard) != 0) {
            return 1;
        } else if ((whiteKnights & bitboard) != 0) {
            return 2;
        } else if ((whiteBishops & bitboard) != 0) {
            return 3;
        } else if ((whiteRooks & bitboard) != 0) {
            return 4;
        } else if ((whiteQueens & bitboard) != 0) {
            return 5;
        } else if ((whiteKing & bitboard) != 0) {
            return 6;
        } else if ((blackPawns & bitboard) != 0) {
            return 7;
        } else if ((blackKnights & bitboard) != 0) {
            return 8;
        } else if ((blackBishops & bitboard) != 0) {
            return 9;
        } else if ((blackRooks & bitboard) != 0) {
            return 10;
        } else if ((blackQueens & bitboard) != 0) {
            return 11;
        } else if ((blackKing & bitboard) != 0) {
            return 12;
        } else {
            return 0;
        }
    }

    public void printBitBoardRaw(){
        System.out.println(Long.toBinaryString(bitboard));
    }

    public static int getSquare(long bitboard) {
        return Long.numberOfTrailingZeros(bitboard);
    }

    public static int getSquare(int rank, int file) {
        return 8 * rank + file;
    }

    public static String getSquareIndexNotation(int square) {
        String[] files = {"a", "b", "c", "d", "e", "f", "g", "h"};
        int rank = square / 8;
        int file = square % 8;
        return files[file] + (rank + 1);

    }

    


}