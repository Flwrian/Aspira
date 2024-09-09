package com.bitboard;

import java.util.ArrayList;
import java.util.List;

public class KnightMoves {
    // Méthode pour obtenir toutes les coordonnées possibles pour un cavalier
    public static List<String> getKnightMoves(String position) {
        List<String> possibleMoves = new ArrayList<>();

        // Conversion de la position donnée (par exemple, A1)
        char file = position.charAt(0); // Lettre de la colonne (A-H)
        int rank = Character.getNumericValue(position.charAt(1)); // Chiffre de la rangée (1-8)

        // Table des décalages pour les mouvements en "L" du cavalier
        int[] fileOffsets = {1, 2, 2, 1, -1, -2, -2, -1}; // Déplacements en colonne (lettres)
        int[] rankOffsets = {2, 1, -1, -2, -2, -1, 1, 2}; // Déplacements en rangée (chiffres)

        // Vérification des mouvements possibles
        for (int i = 0; i < 8; i++) {
            char newFile = (char) (file + fileOffsets[i]);
            int newRank = rank + rankOffsets[i];

            // Vérifier si les nouvelles coordonnées sont valides (dans les limites A-H et 1-8)
            if (newFile >= 'A' && newFile <= 'H' && newRank >= 1 && newRank <= 8) {
                possibleMoves.add("" + newFile + newRank); // Ajoute la position possible
            }
        }

        return possibleMoves;
    }

    // convert square to position
    public static String convertSquareToPosition(int square) {
        int file = square % 8;
        int rank = square / 8;
        char fileChar = (char) ('A' + file);
        return "" + fileChar + (rank + 1);
    }

    // Test de la fonction
    public static void main(String[] args) {
        // generate all possible moves for a knight in the format (position1 | position2, ...) so i can store the moves in an array
        for (int i = 0; i < 64; i++) {
            System.out.print("( ");
            String position = convertSquareToPosition(i);
            List<String> moves = getKnightMoves(position);
            for (String move : moves) {
                // if its the last move, dont add the pipe
                if (moves.indexOf(move) == moves.size() - 1) {
                    System.out.print(move);
                } else {
                    System.out.print(move + " | ");
                }
            }
            System.out.print("), ");
            System.out.println();
        }
    }
}
