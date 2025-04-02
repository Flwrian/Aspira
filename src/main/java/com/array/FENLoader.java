package com.array;

public class FENLoader {
    
    public static void loadFEN(String fen, Board board){
        String[] fenData = fen.split(" ");
        String[] fenBoard = fenData[0].split("/");
        int index = 56;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < fenBoard[i].length(); j++) {
                char piece = fenBoard[i].charAt(j);
                if (Character.isDigit(piece)) {
                    index += (piece - '0');
                } else {
                    switch (piece) {
                        case 'p':
                            board.board[index++] = 7;
                            break;
                        case 'n':
                            board.board[index++] = 8;
                            break;
                        case 'b':
                            board.board[index++] = 9;
                            break;
                        case 'r':
                            board.board[index++] = 10;
                            break;
                        case 'q':
                            board.board[index++] = 11;
                            break;
                        case 'k':
                            board.board[index++] = 12;
                            break;
                        case 'P':
                            board.board[index++] = 1;
                            break;
                        case 'N':
                            board.board[index++] = 2;
                            break;
                        case 'B':
                            board.board[index++] = 3;
                            break;
                        case 'R':
                            board.board[index++] = 4;
                            break;
                        case 'Q':
                            board.board[index++] = 5;
                            break;
                        case 'K':
                            board.board[index++] = 6;
                            break;
                    }
                }
            }
            index -= 16;
        }

        board.whiteTurn = fenData[1].equals("w");
    }
}
