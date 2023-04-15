package com;

public class Game {

    static boolean isEnded = false;
    
    public static void main(String[] args) {
        Board board = new Board();
        board.loadFEN(Board.STARTING_FEN);
        board.printBoard();

        ChessAlgorithm algorithm = new MinMaxAlgorithm(5);
        ChessAlgorithm algorithm2 = new MinMaxAlgorithm(5);

        Engine engine = new Engine(board);
        engine.setAlgorithm(algorithm);

        Engine engine2 = new Engine(board);
        engine2.setAlgorithm(algorithm2);

        board.pushMove(3+8, 3+8+16);
        board.buildPGN(3+8, 3+8+16);

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
