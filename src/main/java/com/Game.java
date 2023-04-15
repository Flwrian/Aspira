package com;

public class Game {

    static boolean isEnded = false;
    
    public static void main(String[] args) {
        Board board = new Board();
        board.loadFEN(Board.STARTING_FEN);
        board.printBoard();

        Engine engine = new Engine(board);
        engine.setDepth(2);

        Engine engine2 = new Engine(board);
        engine2.setDepth(4);

        board.pushMove(4+8, 4+8+16);
        board.buildPGN(4+8, 4+8+16);

        try{
            while(!isEnded){
                isEnded = !board.canPlay;

                if(board.is3FoldRepetition()){
                    System.out.println("3-fold repetition");
                    isEnded = true;
                    break;
                }

                if(board.whiteTurn){
                    engine.play();
                }
                else{
                    engine2.play();
                }
                board.printBoard();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(Board.gamePGN);
    }
}
