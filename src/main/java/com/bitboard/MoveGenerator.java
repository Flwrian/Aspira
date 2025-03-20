package com.bitboard;

public class MoveGenerator {

    public static long generateMask(BitBoard board, boolean white) {

        if (white) {
            return generateWhiteMask(board);
        } else {
            return generateBlackMask(board);
        }

    }

    public static long generateOpponentMask(BitBoard board) {
        if (board.whiteTurn) {
            return generateBlackMask(board);
        } else {
            return generateWhiteMask(board);
        }
    }

    public static long generateMask(BitBoard board) {
        return generateMask(board, board.whiteTurn);
    }

    public static void printMask(BitBoard board) {
        long mask = generateOpponentMask(board);
        board.printBitBoard(mask);
    }

    public static void printMask(BitBoard board, boolean white) {
        long mask = generateMask(board, white);
        board.printBitBoard(mask);
    }

    public static int countBits(long mask) {
        int count = 0;
        while (mask != 0) {
            mask &= mask - 1;
            count++;
        }
        return count;
    }

    public static int isCaptureMove(long to, BitBoard board) {
        if ((to & board.bitboard) != 0L) {
            // we now get the piece on the destination square
            return board.getPieceAt(to);
        }
        return 0;
    }
        

    public static MoveList generatePseudoLegalMoves(BitBoard board) {
        // maximum number of moves is 218
        MoveList moves = new MoveList(218);
        // We will iterate through the board and generate the moves for each piece
        
        // Pawns
        long pawns = board.whiteTurn ? board.getWhitePawns() : board.getBlackPawns();

        // We iterate through the pawns by getting the least significant bit and then removing it from the bitboard
        while (pawns != 0L) {
            long pawn = BitBoard.getLSB(pawns);
            pawns &= pawns - 1;

            // We get the starting square of the pawn
            int from = BitBoard.getSquare(pawn);

            // We generate the moves for the pawn
            long pawnMoves = generatePawnMoves(pawn, board);

            // We iterate through the moves and add them to the list
            while (pawnMoves != 0L) {

                int capturedPiece = BitBoard.EMPTY;
                // We get the least significant bit of the moves (the destination square)
                long move = BitBoard.getLSB(pawnMoves);
                pawnMoves &= pawnMoves - 1;

                int to = BitBoard.getSquare(move);

                // if double pawn push
                if (pawn << 16 == move || pawn >> 16 == move) {
                    Move doublePawnPush = new Move(from, to, BitBoard.PAWN, board.getPiece(to));
                    doublePawnPush.setType(Move.DOUBLE_PAWN_PUSH);
                    doublePawnPush.setWhite(board.whiteTurn);
                    doublePawnPush.setSeeScore(Move.DOUBLE_PAWN_PUSH_SCORE);

                    
                    moves.add(doublePawnPush);
                    continue;
                }

                else {
                    // Before doing anything, we check if the move is colliding with another piece. Since the movegen only gives us legal moves, we don't need to check if its white or black, we can just use the entire bitboard
                    if ((move & board.bitboard) != 0L) {
                        // we now get the piece on the destination square
                        capturedPiece = board.getPieceAt(move);
                    }
                    // If the move is a promotion, we generate all the possible promotions
                    if (to >= 56 || to <= 7) {
                        Move promotionQueen = new Move(from, to, BitBoard.PAWN, BitBoard.QUEEN);
                        promotionQueen.setType(Move.PROMOTION);
                        promotionQueen.setWhite(board.whiteTurn);
                        promotionQueen.setSeeScore(Move.PROMOTION_SCORE + BitBoard.QUEEN + capturedPiece);

                        Move promotionRook = new Move(from, to, BitBoard.PAWN, BitBoard.ROOK);
                        promotionRook.setType(Move.PROMOTION);
                        promotionRook.setWhite(board.whiteTurn);
                        promotionRook.setSeeScore(Move.PROMOTION_SCORE + BitBoard.ROOK + capturedPiece);


                        Move promotionBishop = new Move(from, to, BitBoard.PAWN, BitBoard.BISHOP);
                        promotionBishop.setType(Move.PROMOTION);
                        promotionBishop.setWhite(board.whiteTurn);
                        promotionBishop.setSeeScore(Move.PROMOTION_SCORE + BitBoard.BISHOP + capturedPiece);

                        Move promotionKnight = new Move(from, to, BitBoard.PAWN, BitBoard.KNIGHT);
                        promotionKnight.setType(Move.PROMOTION);
                        promotionKnight.setWhite(board.whiteTurn);
                        promotionKnight.setSeeScore(Move.PROMOTION_SCORE + BitBoard.KNIGHT + capturedPiece);

                        moves.add(promotionQueen);
                        moves.add(promotionRook);
                        moves.add(promotionBishop);
                        moves.add(promotionKnight);
                        continue;
                    }

                    else if (to == BitBoard.getSquare(board.enPassantSquare)) {
                        Move enPassent = new Move(from, to, BitBoard.PAWN, board.getPiece(to));
                        enPassent.setType(Move.EN_PASSENT);
                        enPassent.setWhite(board.whiteTurn);
                        enPassent.setSeeScore(Move.EN_PASSENT);
                        moves.add(enPassent);
                        continue;
                    }
                    else {
                        Move normalMove = new Move(from, to, BitBoard.PAWN, board.getPiece(to));
                        normalMove.setWhite(board.whiteTurn);
                        // If the move is a capture, we set the captured piece so we can use it in the move ordering
                        normalMove.setSeeScore(Move.CAPTURE_SCORE + capturedPiece);
                        moves.add(normalMove);
                    }
                }
            }

        }

        // Knights
        long knights = board.whiteTurn ? board.getWhiteKnights() : board.getBlackKnights();

        while (knights != 0L) {
            long knight = BitBoard.getLSB(knights);
            knights &= knights - 1;

            int from = BitBoard.getSquare(knight);

            long knightMoves = board.whiteTurn ? generateWhiteKnightMoves(knight, board) : generateBlackKnightMoves(knight, board);

            while (knightMoves != 0L) {
                long move = BitBoard.getLSB(knightMoves);
                knightMoves &= knightMoves - 1;

                
                int to = BitBoard.getSquare(move);
                
                Move normalMove = new Move(from, to, board.getPiece(from), board.getPiece(to));
                normalMove.setWhite(board.whiteTurn);

                if (isCaptureMove(move, board) > 0) {
                    normalMove.setSeeScore(Move.CAPTURE_SCORE + isCaptureMove(move, board));
                }

                moves.add(normalMove);
            }
        }

        // Bishops
        long bishops = board.whiteTurn ? board.getWhiteBishops() : board.getBlackBishops();

        while (bishops != 0L) {
            long bishop = BitBoard.getLSB(bishops);
            bishops &= bishops - 1;

            int from = BitBoard.getSquare(bishop);

            long bishopMoves = board.whiteTurn ? generateWhiteBishopMoves(bishop, board) : generateBlackBishopMoves(bishop, board);

            while (bishopMoves != 0L) {
                long move = BitBoard.getLSB(bishopMoves);
                bishopMoves &= bishopMoves - 1;

                int to = BitBoard.getSquare(move);

                Move normalMove = new Move(from, to, board.getPiece(from), board.getPiece(to));
                normalMove.setWhite(board.whiteTurn);

                if (isCaptureMove(move, board) > 0) {
                    normalMove.setSeeScore(Move.CAPTURE_SCORE + isCaptureMove(move, board));
                }

                moves.add(normalMove);
            }
        }

        // Rooks
        long rooks = board.whiteTurn ? board.getWhiteRooks() : board.getBlackRooks();

        while (rooks != 0L) {
            long rook = BitBoard.getLSB(rooks);
            rooks &= rooks - 1;

            int from = BitBoard.getSquare(rook);

            long rookMoves = board.whiteTurn ? generateWhiteRookMoves(rook, board) : generateBlackRookMoves(rook, board);

            while (rookMoves != 0L) {
                long move = BitBoard.getLSB(rookMoves);
                rookMoves &= rookMoves - 1;

                int to = BitBoard.getSquare(move);

                Move normalMove = new Move(from, to, board.getPiece(from), board.getPiece(to));
                normalMove.setWhite(board.whiteTurn);

                if (isCaptureMove(move, board) > 0) {
                    normalMove.setSeeScore(Move.CAPTURE_SCORE + isCaptureMove(move, board));
                }

                
                moves.add(normalMove);
            }
        }

        // Queens
        long queens = board.whiteTurn ? board.getWhiteQueens() : board.getBlackQueens();

        while (queens != 0L) {
            long queen = BitBoard.getLSB(queens);
            queens &= queens - 1;

            int from = BitBoard.getSquare(queen);

            long queenMoves = board.whiteTurn ? generateWhiteQueenMoves(queen, board) : generateBlackQueenMoves(queen, board);

            while (queenMoves != 0L) {
                long move = BitBoard.getLSB(queenMoves);
                queenMoves &= queenMoves - 1;

                int to = BitBoard.getSquare(move);

                Move normalMove = new Move(from, to, board.getPiece(from), board.getPiece(to));
                normalMove.setWhite(board.whiteTurn);

                if (isCaptureMove(move, board) > 0) {
                    normalMove.setSeeScore(Move.CAPTURE_SCORE + isCaptureMove(move, board));
                }

                moves.add(normalMove);
            }
        }

        // Kings
        long kings = board.whiteTurn ? board.getWhiteKing() : board.getBlackKing();
        long king = BitBoard.getLSB(kings);

        int from = BitBoard.getSquare(king);

        long kingMoves = board.whiteTurn ? generateWhiteKingMoves(king, board) : generateBlackKingMoves(king, board);

        while (kingMoves != 0L) {
            long move = BitBoard.getLSB(kingMoves);
            kingMoves &= kingMoves - 1;

            int to = BitBoard.getSquare(move);

            // Castling
            if (Math.abs(from - to) == 2) {
                Move castling = new Move(from, to, board.getPiece(from), board.getPiece(to));
                castling.setType(Move.CASTLING);
                castling.setWhite(board.whiteTurn);
                moves.add(castling);
            }

            else{
                Move normalMove = new Move(from, to, board.getPiece(from), board.getPiece(to));
                normalMove.setWhite(board.whiteTurn);

                if (isCaptureMove(move, board) > 0) {
                    normalMove.setSeeScore(Move.CAPTURE_SCORE + isCaptureMove(move, board));
                }

                moves.add(normalMove);
            }

        }

        return moves;

    }

    public static MoveList generateCaptureMoves(BitBoard board) {
        // maximum number of capture moves is 218
        MoveList moves = new MoveList(218);
    
        // Table MVV-LVA (Most Valuable Victim, Least Valuable Attacker)
        int[][] mvvLva = {
            {105, 205, 305, 405, 505, 605},  // Pawn captures
            {104, 204, 304, 404, 504, 604},  // Knight captures
            {103, 203, 303, 403, 503, 603},  // Bishop captures
            {102, 202, 302, 402, 502, 602},  // Rook captures
            {101, 201, 301, 401, 501, 601},  // Queen captures
            {100, 200, 300, 400, 500, 600}   // King captures
        };
    
        // Pawns
        long pawns = board.whiteTurn ? board.getWhitePawns() : board.getBlackPawns();
        while (pawns != 0L) {
            long pawn = BitBoard.getLSB(pawns);
            pawns &= pawns - 1;
            int from = BitBoard.getSquare(pawn);
            long pawnMoves = generatePawnMoves(pawn, board);
    
            while (pawnMoves != 0L) {
                long move = BitBoard.getLSB(pawnMoves);
                pawnMoves &= pawnMoves - 1;
                int to = BitBoard.getSquare(move);
                int capturedPiece = BitBoard.EMPTY;
    
                if ((move & board.bitboard) != 0L) {
                    capturedPiece = board.getPieceAt(move);
                    int attackerPiece = board.getPiece(from);
    
                    // MVV-LVA calculation
                    int attackerType = BitBoard.getPieceType(attackerPiece) - 1;
                    int capturedType = BitBoard.getPieceType(capturedPiece) - 1;
    
                    if (attackerType >= 0 && attackerType <= 5 && capturedType >= 0 && capturedType <= 5) {
                        int mvvLvaScore = mvvLva[attackerType][capturedType];
                        moves.add(new Move(from, to, board.getPiece(from), capturedPiece, Move.CAPTURE, mvvLvaScore));
                    }
                }
            }
        }
    
        // Knights
        long knights = board.whiteTurn ? board.getWhiteKnights() : board.getBlackKnights();
        while (knights != 0L) {
            long knight = BitBoard.getLSB(knights);
            knights &= knights - 1;
            int from = BitBoard.getSquare(knight);
            long knightMoves = board.whiteTurn ? generateWhiteKnightMoves(knight, board) : generateBlackKnightMoves(knight, board);
    
            while (knightMoves != 0L) {
                long move = BitBoard.getLSB(knightMoves);
                knightMoves &= knightMoves - 1;
                int to = BitBoard.getSquare(move);
                int capturedPiece = BitBoard.EMPTY;
    
                if ((move & board.bitboard) != 0L) {
                    capturedPiece = board.getPieceAt(move);
                    int attackerPiece = board.getPiece(from);
    
                    // MVV-LVA calculation
                    int attackerType = BitBoard.getPieceType(attackerPiece) - 1;
                    int capturedType = BitBoard.getPieceType(capturedPiece) - 1;
    
                    if (attackerType >= 0 && attackerType <= 5 && capturedType >= 0 && capturedType <= 5) {
                        int mvvLvaScore = mvvLva[attackerType][capturedType];
                        moves.add(new Move(from, to, board.getPiece(from), capturedPiece, Move.CAPTURE, mvvLvaScore));
                    }
                }
            }
        }
    
        // Bishops
        long bishops = board.whiteTurn ? board.getWhiteBishops() : board.getBlackBishops();
        while (bishops != 0L) {
            long bishop = BitBoard.getLSB(bishops);
            bishops &= bishops - 1;
            int from = BitBoard.getSquare(bishop);
            long bishopMoves = board.whiteTurn ? generateWhiteBishopMoves(bishop, board) : generateBlackBishopMoves(bishop, board);
    
            while (bishopMoves != 0L) {
                long move = BitBoard.getLSB(bishopMoves);
                bishopMoves &= bishopMoves - 1;
                int to = BitBoard.getSquare(move);
                int capturedPiece = BitBoard.EMPTY;
    
                if ((move & board.bitboard) != 0L) {
                    capturedPiece = board.getPieceAt(move);
                    int attackerPiece = board.getPiece(from);
    
                    // MVV-LVA calculation
                    int attackerType = BitBoard.getPieceType(attackerPiece) - 1;
                    int capturedType = BitBoard.getPieceType(capturedPiece) - 1;
    
                    if (attackerType >= 0 && attackerType <= 5 && capturedType >= 0 && capturedType <= 5) {
                        int mvvLvaScore = mvvLva[attackerType][capturedType];
                        moves.add(new Move(from, to, board.getPiece(from), capturedPiece, Move.CAPTURE, mvvLvaScore));
                    }
                }
            }
        }
    
        // Rooks
        long rooks = board.whiteTurn ? board.getWhiteRooks() : board.getBlackRooks();
        while (rooks != 0L) {
            long rook = BitBoard.getLSB(rooks);
            rooks &= rooks - 1;
            int from = BitBoard.getSquare(rook);
            long rookMoves = board.whiteTurn ? generateWhiteRookMoves(rook, board) : generateBlackRookMoves(rook, board);
    
            while (rookMoves != 0L) {
                long move = BitBoard.getLSB(rookMoves);
                rookMoves &= rookMoves - 1;
                int to = BitBoard.getSquare(move);
                int capturedPiece = BitBoard.EMPTY;
    
                if ((move & board.bitboard) != 0L) {
                    capturedPiece = board.getPieceAt(move);
                    int attackerPiece = board.getPiece(from);
    
                    // MVV-LVA calculation
                    int attackerType = BitBoard.getPieceType(attackerPiece) - 1;
                    int capturedType = BitBoard.getPieceType(capturedPiece) - 1;
    
                    if (attackerType >= 0 && attackerType <= 5 && capturedType >= 0 && capturedType <= 5) {
                        int mvvLvaScore = mvvLva[attackerType][capturedType];
                        moves.add(new Move(from, to, board.getPiece(from), capturedPiece, Move.CAPTURE, mvvLvaScore));
                    }
                }
            }
        }
    
        // Queens
        long queens = board.whiteTurn ? board.getWhiteQueens() : board.getBlackQueens();
        while (queens != 0L) {
            long queen = BitBoard.getLSB(queens);
            queens &= queens - 1;
            int from = BitBoard.getSquare(queen);
            long queenMoves = board.whiteTurn ? generateWhiteQueenMoves(queen, board) : generateBlackQueenMoves(queen, board);
    
            while (queenMoves != 0L) {
                long move = BitBoard.getLSB(queenMoves);
                queenMoves &= queenMoves - 1;
                int to = BitBoard.getSquare(move);
                int capturedPiece = BitBoard.EMPTY;
    
                if ((move & board.bitboard) != 0L) {
                    capturedPiece = board.getPieceAt(move);
                    int attackerPiece = board.getPiece(from);
    
                    // MVV-LVA calculation
                    int attackerType = BitBoard.getPieceType(attackerPiece) - 1;
                    int capturedType = BitBoard.getPieceType(capturedPiece) - 1;
    
                    if (attackerType >= 0 && attackerType <= 5 && capturedType >= 0 && capturedType <= 5) {
                        int mvvLvaScore = mvvLva[attackerType][capturedType];
                        moves.add(new Move(from, to, board.getPiece(from), capturedPiece, Move.CAPTURE, mvvLvaScore));
                    }
                }
            }
        }
    
        // Kings
        long kings = board.whiteTurn ? board.getWhiteKing() : board.getBlackKing();
        long king = BitBoard.getLSB(kings);
        int from = BitBoard.getSquare(king);
        long kingMoves = board.whiteTurn ? generateWhiteKingMoves(king, board) : generateBlackKingMoves(king, board);
    
        while (kingMoves != 0L) {
            long move = BitBoard.getLSB(kingMoves);
            kingMoves &= kingMoves - 1;
            int to = BitBoard.getSquare(move);
            int capturedPiece = BitBoard.EMPTY;
    
            if ((move & board.bitboard) != 0L) {
                capturedPiece = board.getPieceAt(move);
                int attackerPiece = board.getPiece(from);
    
                // MVV-LVA calculation
                int attackerType = BitBoard.getPieceType(attackerPiece) - 1;
                int capturedType = BitBoard.getPieceType(capturedPiece) - 1;
    
                if (attackerType >= 0 && attackerType <= 5 && capturedType >= 0 && capturedType <= 5) {
                    int mvvLvaScore = mvvLva[attackerType][capturedType];
                    moves.add(new Move(from, to, board.getPiece(from), capturedPiece, Move.CAPTURE, mvvLvaScore));
                }
            }
        }
    
        return moves;
    }
    


    private static long generateWhiteMask(BitBoard board) {
        long whiteAttacks = 0L;

        // Générer les mouvements des pions blancs
        long whitePawnMask = generatePawnMask(board.getWhitePawns(), true);
        whiteAttacks |= whitePawnMask;

        // Générer les mouvements des cavaliers blancs
        long whiteKnightMask = generateKnightMask(board.getWhiteKnights());
        whiteAttacks |= whiteKnightMask;

        // Générer les mouvements des fous blancs
        long whiteBishopMask = generateBishopMask(board.getWhiteBishops(), board);
        whiteAttacks |= whiteBishopMask;

        // Générer les mouvements des tours blanches
        long whiteRookMask = generateRookMask(board.getWhiteRooks(), board);
        whiteAttacks |= whiteRookMask;

        // Générer les mouvements des reines blanches
        long whiteQueenMask = generateQueenMask(board.getWhiteQueens(), board);
        whiteAttacks |= whiteQueenMask;

        // Générer les mouvements des rois blancs
        long whiteKingMask = generateKingMask(board.getWhiteKing());
        whiteAttacks |= whiteKingMask;


        return whiteAttacks;
    }

    private static long generateBlackMask(BitBoard board) {
        long blackAttacks = 0L;

        // Générer les mouvements des pions noirs
        long blackPawnMask = generatePawnMask(board.getBlackPawns(), false);
        blackAttacks |= blackPawnMask;

        // Générer les mouvements des cavaliers noirs
        long blackKnightMask = generateKnightMask(board.getBlackKnights());
        blackAttacks |= blackKnightMask;

        // Générer les mouvements des fous noirs
        long blackBishopMask = generateBishopMask(board.getBlackBishops(), board);
        blackAttacks |= blackBishopMask;

        // Générer les mouvements des tours noires
        long blackRookMask = generateRookMask(board.getBlackRooks(), board);
        blackAttacks |= blackRookMask;

        // Générer les mouvements des reines noires
        long blackQueenMask = generateQueenMask(board.getBlackQueens(), board);
        blackAttacks |= blackQueenMask;

        // Générer les mouvements des rois noirs
        long blackKingMask = generateKingMask(board.getBlackKing());
        blackAttacks |= blackKingMask;

        return blackAttacks;
    }

    private static long generatePawnMask(long pawns, boolean white) {
        long pawnMask = 0L;

        if (white) {
            // Captures diagonales, gauche et droite
            long capturesLeft = (pawns << 9) & ~BitBoard.FILE_H;
            long capturesRight = (pawns << 7) & ~BitBoard.FILE_A;

            pawnMask |= capturesLeft | capturesRight;
        } else {
            // Captures diagonales, gauche et droite
            long capturesLeft = (pawns >> 7) & ~BitBoard.FILE_H;
            long capturesRight = (pawns >> 9) & ~BitBoard.FILE_A;

            pawnMask |= capturesLeft | capturesRight;
        }

        return pawnMask;
    }

    public static long generatePawnMoves(long pawns, BitBoard board) {
        long pawnMoves = 0L;

        if (board.whiteTurn) {
            long singlePush = (pawns << 8) & ~board.getBoard();  // Avancer d'une case
            // Avancer de deux cases depuis la rangée initiale mais seulement si les cases sont vides
            long doublePush = ((pawns << 16) & ~board.getBoard() & (singlePush << 8) & BitBoard.RANK_4);
            pawnMoves |= singlePush | doublePush;
            // Captures diagonales, gauche et droite
            long capturesLeft = (pawns << 7) & board.getBlackPieces() & ~BitBoard.FILE_A;
            long capturesRight = (pawns << 9) & board.getBlackPieces() & ~BitBoard.FILE_H;
            pawnMoves |= capturesLeft | capturesRight;

            // en passant
            if (board.enPassantSquare != 0L) {
                long enPassantLeft = (pawns << 7) & board.enPassantSquare & ~BitBoard.FILE_A;
                long enPassantRight = (pawns << 9) & board.enPassantSquare & ~BitBoard.FILE_H;
                pawnMoves |= enPassantLeft | enPassantRight;
            }

        } else {
            long singlePush = (pawns >> 8) & ~board.getBoard();  // Avancer d'une case
            long doublePush = ((pawns >> 16) & ~board.getBoard() & (singlePush >> 8) & BitBoard.RANK_5);  // Avancer de deux cases depuis la rangée initiale
            pawnMoves |= singlePush | doublePush;
            // Captures diagonales, gauche et droite
            long capturesLeft = (pawns >> 7) & board.getWhitePieces() & ~BitBoard.FILE_H;
            long capturesRight = (pawns >> 9) & board.getWhitePieces() & ~BitBoard.FILE_A;
            pawnMoves |= capturesLeft | capturesRight;


            // en passant
            if (board.enPassantSquare != 0L) {
                long enPassantLeft = (pawns >> 7) & board.enPassantSquare & ~BitBoard.FILE_H;
                long enPassantRight = (pawns >> 9) & board.enPassantSquare & ~BitBoard.FILE_A;
                pawnMoves |= enPassantLeft | enPassantRight;
            }
            
        }

        return pawnMoves;

    }

    public static long generateKnightMask(long knights) {
        long knightMask = 0L;

        while (knights != 0L) {
            long knight = BitBoard.getLSB(knights);
            knights &= knights - 1; // 

            // run the knight through the precomputed knight moves
            long knightMoves = BitBoard.KNIGHT_MOVES[BitBoard.getSquare(knight)];

            knightMask |= knightMoves;
        }

        return knightMask;
        
    }

    // Generate knight legal moves
    public static long generateWhiteKnightMoves(long knights, BitBoard board) {
        // Le cavalier peut se déplacer en L, 2 cases dans une direction et 1 case dans une autre et ne peut pas aller sur une case occupée par une pièce de la même couleur
        long knightMoves = 0L;

        // Déplacements en forme de L
        long knightMask = generateKnightMask(knights);

        // On ne peut pas aller sur une case occupée par une pièce de la même couleur
        knightMoves = knightMask & ~board.getWhitePieces();

        return knightMoves;
    }



    private static long generateBishopMask(long bishops, BitBoard board) {
        // Le fou peut se déplacer dans toutes les diagonales mais le mask s'arrête lorsqu'il rencontre une pièce (inclusif)

        long bishopMask = 0L;

        while (bishops != 0L) {
            long bishop = BitBoard.getLSB(bishops);
            bishops &= bishops - 1;

            long upLeft = bishop;
            long upRight = bishop;
            long downLeft = bishop;
            long downRight = bishop;

            // Déplacements en diagonale
            while (upLeft != 0L) {
                upLeft = (upLeft << 7) & ~BitBoard.FILE_A & ~BitBoard.RANK_1;
                bishopMask |= upLeft;
                if ((upLeft & board.getBoard()) != 0L) {
                    break;
                }
            }
            
            while (upRight != 0L) {
                upRight = (upRight << 9) & ~BitBoard.FILE_H & ~BitBoard.RANK_1;
                bishopMask |= upRight;
                if ((upRight & board.getBoard()) != 0L) {
                    break;
                }
            }

            while (downLeft != 0L) {
                downLeft = (downLeft >> 9) & ~BitBoard.FILE_A & ~BitBoard.RANK_8;
                bishopMask |= downLeft;
                if ((downLeft & board.getBoard()) != 0L) {
                    break;
                }
            }

            while (downRight != 0L) {
                downRight = (downRight >> 7) & ~BitBoard.FILE_H & ~BitBoard.RANK_8;
                bishopMask |= downRight;
                if ((downRight & board.getBoard()) != 0L) {
                    break;
                }
            }
        }

        return bishopMask;
        
    }

    private static long generateWhiteBishopMoves(long bishops, BitBoard board) {
        // La seule différence entre les mouvements des fous et les masques des fous est que selon la couleur, les fous ne peuvent pas capturer une pièce de la même couleur

        long bishopMoves = 0L;

        // Déplacements en diagonale
        long bishopMask = generateBishopMask(bishops, board);

        // On ne peut pas capturer une pièce de la même couleur
        bishopMoves = bishopMask & ~board.getWhitePieces();

        return bishopMoves;
    }
    

    private static long generateRookMask(long rooks, BitBoard board) {
        // La tour peut se déplacer en ligne droite et le mask s'arrête lorsqu'il rencontre une pièce (inclusif)
        long rookMoves = 0L;

        while (rooks != 0L) {
            long rook = Long.lowestOneBit(rooks);
            rooks &= rooks - 1;

            long up = rook;
            long down = rook;
            long left = rook;
            long right = rook;

            // Déplacements en ligne droite
            while (up != 0L) {
                up = (up << 8) & ~BitBoard.RANK_1;
                rookMoves |= up;
                if ((up & board.getBoard()) != 0L) {
                    break;
                }
            }

            while (down != 0L) {
                down = (down >> 8) & ~BitBoard.RANK_8;
                rookMoves |= down;
                if ((down & board.getBoard()) != 0L) {
                    break;
                }
            }

            while (left != 0L) {
                left = (left << 1) & ~BitBoard.FILE_H;
                rookMoves |= left;
                if ((left & board.getBoard()) != 0L) {
                    break;
                }
            }

            while (right != 0L) {
                right = (right >> 1) & ~BitBoard.FILE_A;
                rookMoves |= right;
                if ((right & board.getBoard()) != 0L) {
                    break;
                }
            }
        }

        return rookMoves;
        
    }

    public static long generateWhiteRookMoves(long rooks, BitBoard board) {
        // La seule différence entre les mouvements des tours et les masques des tours est que selon la couleur, les tours ne peuvent pas capturer une pièce de la même couleur

        long rookMoves = 0L;

        // Déplacements en ligne droite
        long rookMask = generateRookMask(rooks, board);

        // On ne peut pas capturer une pièce de la même couleur
        rookMoves = rookMask & ~board.getWhitePieces();

        return rookMoves;
    }

    private static long generateQueenMask(long queens, BitBoard board) {
        // La reine peut se déplacer en diagonale et en ligne droite soit les mouvements du fou et de la tour
        long queenMoves = 0L;

        // Déplacements en diagonale
        long straightMoves = generateRookMask(queens, board);
        long diagonalMoves = generateBishopMask(queens, board);

        queenMoves |= straightMoves | diagonalMoves;

        return queenMoves;
    }

    private static long generateWhiteQueenMoves(long queens, BitBoard board) {
        // La seule différence entre les mouvements des reines et les masques des reines est que selon la couleur, les reines ne peuvent pas capturer une pièce de la même couleur

        long queenMoves = 0L;

        // Déplacements en diagonale et en ligne droite
        long queenMask = generateQueenMask(queens, board);

        // On ne peut pas capturer une pièce de la même couleur
        queenMoves = queenMask & ~board.getWhitePieces();

        return queenMoves;
    }

    public static long generateKingMask(long king){
        // Le roi peut se déplacer d'une case dans toutes les directions et peut roquer
        long kingMoves = 0L;

        // Déplacements d'une case
        long up = (king << 8) & ~BitBoard.RANK_1;
        long down = (king >> 8) & ~BitBoard.RANK_8;
        long left = (king << 1) & ~BitBoard.FILE_H;
        long right = (king >> 1) & ~BitBoard.FILE_A;
        
        // Déplacements en diagonale
        long upLeft = (king << 7) & ~BitBoard.FILE_A & ~BitBoard.RANK_1;
        long upRight = (king << 9) & ~BitBoard.FILE_H & ~BitBoard.RANK_1;
        long downLeft = (king >> 9) & ~BitBoard.FILE_A & ~BitBoard.RANK_8;
        long downRight = (king >> 7) & ~BitBoard.FILE_H & ~BitBoard.RANK_8;


        kingMoves |= up | down | left | right | upLeft | upRight | downLeft | downRight;

        return kingMoves;
    }

    public static long generateKingMoves(long king, BitBoard board) {
        // Le roi peut se déplacer d'une case dans toutes les directions et peut roquer
        long kingMoves = generateKingMask(king);
        // pas la meme couleur
        if (board.whiteTurn) {
            kingMoves &= ~board.getWhitePieces();
        } else {
            kingMoves &= ~board.getBlackPieces();
        }

        // La logique du roque est comme suit:
        // Si on possède le droit de roquer, que la tour est à sa place et que les cases entre le roi et la tour sont vides
        // alors on peut roquer
        
        if (board.whiteTurn) {
            // Si on a les droits de roquer du côté de du roi
            if(board.whiteCastleKingSide == 1L) {
                // Si la tour est à sa place
                if((board.whiteRooks & BitBoard.WHITE_KING_SIDE_ROOK_SQUARE) != 0L) {
                    // Si il n'y a pas de pièces entre le roi et la tour
                    if((BitBoard.WHITE_KING_SIDE_CASTLE_EMPTY_SQUARES_MASK & board.getBoard()) == 0L) {
                        // Si les cases ne sont pas attaquées
                        if((BitBoard.WHITE_KING_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK & generateOpponentMask(board)) == 0L) {
                            // On peut roquer
                            kingMoves |= BitBoard.WHITE_KING_SIDE_CASTLE_KING_SQUARE;
                        }
                    }
                }
            }

            // Si on a les droits de roquer du côté de la dame
            if(board.whiteCastleQueenSide == 1L) {
                // Si la tour est à sa place
                if((board.whiteRooks & BitBoard.WHITE_QUEEN_SIDE_ROOK_SQUARE) != 0L) {
                    // Si il n'y a pas de pièces entre le roi et la tour
                    if((BitBoard.WHITE_QUEEN_SIDE_CASTLE_EMPTY_SQUARES_MASK & board.getBoard()) == 0L) {
                        // Si les cases ne sont pas attaquées
                        if((BitBoard.WHITE_QUEEN_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK & generateOpponentMask(board)) == 0L) {
                            // On peut roquer
                            kingMoves |= BitBoard.WHITE_QUEEN_SIDE_CASTLE_KING_SQUARE;
                        }
                    }
                }
            }
        }

        else {
            // Si on a les droits de roquer du côté de du roi
            if(board.blackCastleKingSide == 1L) {
                // Si la tour est à sa place
                if((board.blackRooks & BitBoard.BLACK_KING_SIDE_ROOK_SQUARE) != 0L) {
                    // Si il n'y a pas de pièces entre le roi et la tour
                    if((BitBoard.BLACK_KING_SIDE_CASTLE_EMPTY_SQUARES_MASK & board.getBoard()) == 0L) {
                        // Si les cases ne sont pas attaquées
                        if((BitBoard.BLACK_KING_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK & generateOpponentMask(board)) == 0L) {
                            // On peut roquer
                            kingMoves |= BitBoard.BLACK_KING_SIDE_CASTLE_KING_SQUARE;
                        }
                    }
                }
            }

            // Si on a les droits de roquer du côté de la dame
            if(board.blackCastleQueenSide == 1L) {
                // Si la tour est à sa place
                if((board.blackRooks & BitBoard.BLACK_QUEEN_SIDE_ROOK_SQUARE) != 0L) {
                    // Si il n'y a pas de pièces entre le roi et la tour
                    if((BitBoard.BLACK_QUEEN_SIDE_CASTLE_EMPTY_SQUARES_MASK & board.getBoard()) == 0L) {
                        // Si les cases ne sont pas attaquées
                        if((BitBoard.BLACK_QUEEN_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK & generateOpponentMask(board)) == 0L) {
                            // On peut roquer
                            kingMoves |= BitBoard.BLACK_QUEEN_SIDE_CASTLE_KING_SQUARE;
                        }
                    }
                }
            }
        }

        return kingMoves;
    }

    private static long generateBlackKingMoves(long king, BitBoard board) {
        long kingMoves = generateKingMask(king);
        // On ne peut pas aller sur une case occupée par une pièce de la même couleur
        kingMoves &= ~board.getBlackPieces();

        // La logique du roque est comme suit:
        // Si on a les droits de roquer du côté de du roi
        if(board.blackCastleKingSide == 1L) {
            // Si la tour est à sa place
            if((board.blackRooks & BitBoard.BLACK_KING_SIDE_ROOK_SQUARE) != 0L) {
                // Si il n'y a pas de pièces entre le roi et la tour
                if((BitBoard.BLACK_KING_SIDE_CASTLE_EMPTY_SQUARES_MASK & board.getBoard()) == 0L) {
                    // Si les cases ne sont pas attaquées
                    if((BitBoard.BLACK_KING_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK & generateOpponentMask(board)) == 0L) {
                        // On peut roquer
                        kingMoves |= BitBoard.BLACK_KING_SIDE_CASTLE_KING_SQUARE;
                    }
                }
            }
        }

        // Si on a les droits de roquer du côté de la dame
        if(board.blackCastleQueenSide == 1L) {
            // Si la tour est à sa place
            if((board.blackRooks & BitBoard.BLACK_QUEEN_SIDE_ROOK_SQUARE) != 0L) {
                // Si il n'y a pas de pièces entre le roi et la tour
                if((BitBoard.BLACK_QUEEN_SIDE_CASTLE_EMPTY_SQUARES_MASK & board.getBoard()) == 0L) {
                    // Si les cases ne sont pas attaquées
                    if((BitBoard.BLACK_QUEEN_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK & generateOpponentMask(board)) == 0L) {
                        // On peut roquer
                        kingMoves |= BitBoard.BLACK_QUEEN_SIDE_CASTLE_KING_SQUARE;
                    }
                }
            }
        }

        return kingMoves;
    }

    private static long generateWhiteKingMoves(long king, BitBoard board) {
        long kingMoves = generateKingMask(king);
        // On ne peut pas aller sur une case occupée par une pièce de la même couleur
        kingMoves &= ~board.getWhitePieces();

        // La logique du roque est comme suit:
        // Si on a les droits de roquer du côté de du roi
        if(board.whiteCastleKingSide == 1L) {
            // Si la tour est à sa place
            if((board.whiteRooks & BitBoard.WHITE_KING_SIDE_ROOK_SQUARE) != 0L) {
                // Si il n'y a pas de pièces entre le roi et la tour
                if((BitBoard.WHITE_KING_SIDE_CASTLE_EMPTY_SQUARES_MASK & board.getBoard()) == 0L) {
                    // Si les cases ne sont pas attaquées
                    if((BitBoard.WHITE_KING_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK & generateOpponentMask(board)) == 0L) {
                        // On peut roquer
                        kingMoves |= BitBoard.WHITE_KING_SIDE_CASTLE_KING_SQUARE;
                    }
                }
            }
        }

        // Si on a les droits de roquer du côté de la dame
        if(board.whiteCastleQueenSide == 1L) {
            // Si la tour est à sa place
            if((board.whiteRooks & BitBoard.WHITE_QUEEN_SIDE_ROOK_SQUARE) != 0L) {
                // Si il n'y a pas de pièces entre le roi et la tour
                if((BitBoard.WHITE_QUEEN_SIDE_CASTLE_EMPTY_SQUARES_MASK & board.getBoard()) == 0L) {
                    // Si les cases ne sont pas attaquées
                    if((BitBoard.WHITE_QUEEN_SIDE_CASTLE_NEED_TO_NOT_BE_ATTACKED_MASK & generateOpponentMask(board)) == 0L) {
                        // On peut roquer
                        kingMoves |= BitBoard.WHITE_QUEEN_SIDE_CASTLE_KING_SQUARE;
                    }
                }
            }
        }

        return kingMoves;
    }

    public static long generateBlackKnightMoves(long knights, BitBoard board) {
        // Le cavalier peut se déplacer en L, 2 cases dans une direction et 1 case dans une autre et ne peut pas aller sur une case occupée par une pièce de la même couleur
        long knightMoves = 0L;

        // Déplacements en forme de L
        long knightMask = generateKnightMask(knights);

        // On ne peut pas aller sur une case occupée par une pièce de la même couleur
        knightMoves = knightMask & ~board.getBlackPieces();

        return knightMoves;
    }

    public static long generateBlackBishopMoves(long bishops, BitBoard board) {
        // La seule différence entre les mouvements des fous et les masques des fous est que selon la couleur, les fous ne peuvent pas capturer une pièce de la même couleur

        long bishopMoves = 0L;

        // Déplacements en diagonale
        long bishopMask = generateBishopMask(bishops, board);

        // On ne peut pas capturer une pièce de la même couleur
        bishopMoves = bishopMask & ~board.getBlackPieces();

        return bishopMoves;
    }

    public static long generateBlackRookMoves(long rooks, BitBoard board) {
        // La seule différence entre les mouvements des tours et les masques des tours est que selon la couleur, les tours ne peuvent pas capturer une pièce de la même couleur

        long rookMoves = 0L;

        // Déplacements en ligne droite
        long rookMask = generateRookMask(rooks, board);

        // On ne peut pas capturer une pièce de la même couleur
        rookMoves = rookMask & ~board.getBlackPieces();

        return rookMoves;
    }

    public static long generateBlackQueenMoves(long queens, BitBoard board) {
        // La seule différence entre les mouvements des reines et les masques des reines est que selon la couleur, les reines ne peuvent pas capturer une pièce de la même couleur

        long queenMoves = 0L;

        // Déplacements en diagonale et en ligne droite
        long queenMask = generateQueenMask(queens, board);

        // On ne peut pas capturer une pièce de la même couleur
        queenMoves = queenMask & ~board.getBlackPieces();

        return queenMoves;
    }


    // Magics numbers

    // We use the magic numbers to generate the attack mask for the pieces
    // The key formula is: index = ((occupancy & mask) * magic_number) >>> shift;

    public static final long[][] rookAttacks = new long[64][4096];
    public static final long[][] bishopAttacks = new long[64][512];

    public static void initialize() {
        for (int square = 0; square < 64; square++) {
            generateAttackTables(square);
        }
    }

    private static void generateAttackTables(int square) {
        long rookMask = generateRookMask(square);
        long bishopMask = generateBishopMask(square);
        
        for (int i = 0; i < (1 << Long.bitCount(rookMask)); i++) {
            long occupancy = generateOccupancy(i, rookMask);
            rookAttacks[square][i] = generateRookAttack(square, occupancy);
        }

        for (int i = 0; i < (1 << Long.bitCount(bishopMask)); i++) {
            long occupancy = generateOccupancy(i, bishopMask);
            bishopAttacks[square][i] = generateBishopAttack(square, occupancy);
        }
    }

    private static long generateRookMask(int square) {
        long mask = 0L;
        int rank = square / 8, file = square % 8;
        for (int f = file + 1; f < 7; f++) mask |= (1L << (rank * 8 + f));
        for (int f = file - 1; f > 0; f--) mask |= (1L << (rank * 8 + f));
        for (int r = rank + 1; r < 7; r++) mask |= (1L << (r * 8 + file));
        for (int r = rank - 1; r > 0; r--) mask |= (1L << (r * 8 + file));
        return mask;
    }

    private static long generateBishopMask(int square) {
        long mask = 0L;
        int rank = square / 8, file = square % 8;
        for (int r = rank + 1, f = file + 1; r < 7 && f < 7; r++, f++) mask |= (1L << (r * 8 + f));
        for (int r = rank - 1, f = file - 1; r > 0 && f > 0; r--, f--) mask |= (1L << (r * 8 + f));
        for (int r = rank + 1, f = file - 1; r < 7 && f > 0; r++, f--) mask |= (1L << (r * 8 + f));
        for (int r = rank - 1, f = file + 1; r > 0 && f < 7; r--, f++) mask |= (1L << (r * 8 + f));
        return mask;
    }

    private static long generateRookAttack(int square, long blockers) {
        long attack = 0L;
        int rank = square / 8, file = square % 8;
        for (int f = file + 1; f < 8; f++) {
            attack |= (1L << (rank * 8 + f));
            if ((blockers & (1L << (rank * 8 + f))) != 0) break;
        }
        for (int f = file - 1; f >= 0; f--) {
            attack |= (1L << (rank * 8 + f));
            if ((blockers & (1L << (rank * 8 + f))) != 0) break;
        }
        for (int r = rank - 1; r >= 0; r--) {
            attack |= (1L << (r * 8 + file));
            if ((blockers & (1L << (r * 8 + file))) != 0) break;
        }
        for (int r = rank + 1; r < 8; r++) {
            attack |= (1L << (r * 8 + file));
            if ((blockers & (1L << (r * 8 + file))) != 0) break;
        }
        return attack;
    }

    private static long generateBishopAttack(int square, long blockers) {
        long attack = 0L;
        int rank = square / 8, file = square % 8;
        for (int r = rank + 1, f = file + 1; r < 8 && f < 8; r++, f++) {
            attack |= (1L << (r * 8 + f));
            if ((blockers & (1L << (r * 8 + f))) != 0) break;
        }
        for (int r = rank - 1, f = file - 1; r >= 0 && f >= 0; r--, f--) {
            attack |= (1L << (r * 8 + f));
            if ((blockers & (1L << (r * 8 + f))) != 0) break;
        }
        for (int r = rank + 1, f = file - 1; r < 8 && f >= 0; r++, f--) {
            attack |= (1L << (r * 8 + f));
            if ((blockers & (1L << (r * 8 + f))) != 0) break;
        }
        for (int r = rank - 1, f = file + 1; r >= 0 && f < 8; r--, f++) {
            attack |= (1L << (r * 8 + f));
            if ((blockers & (1L << (r * 8 + f))) != 0) break;
        }
        return attack;
    }

    public static long generateOccupancy(int index, long mask) {
        long occupancy = 0L;
        int bits = Long.bitCount(mask);
        for (int i = 0; i < bits; i++) {
            int square = Long.numberOfTrailingZeros(mask);
            mask &= mask - 1;
            if ((index & (1 << i)) != 0) {
                occupancy |= (1L << square);
            }
        }
        return occupancy;
    }

    public static long getRookAttacks(int square, long blockers) {
        int index = (int) blockers;
        return rookAttacks[square][index];
    }

    public static long getBishopAttacks(int square, long blockers) {
        int index = (int) blockers;
        return bishopAttacks[square][index];
    }

    public static long generateRookAttackBoard(long rookBitboard, BitBoard board){
        long blockers = board.getBoard();
        int square = BitBoard.getSquare(rookBitboard);
        long rookMoves = 0L;
        long rookMask = generateRookMask(square);
        board.printBitBoard(rookMask);
        board.printBitBoard(generateRookMask(rookBitboard, board));
        long rookAttack = rookMask & blockers;
        int index = Long.bitCount(rookAttack);
        long occupancy = generateOccupancy(index, rookMask);
        rookMoves = getRookAttacks(square, occupancy);
        rookMoves &= ~rookBitboard; // remove the rook itself from the attack
        rookMoves &= ~board.getWhitePieces(); // remove the pieces of the same color
        rookMoves &= ~board.getBlackPieces(); // remove the pieces of the same color
        return rookMoves;
    }
    
    
        
    
}
