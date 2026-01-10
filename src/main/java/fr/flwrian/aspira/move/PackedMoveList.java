package fr.flwrian.aspira.move;

public class PackedMoveList {
    public final int[] moves;
    public int size = 0;

    public PackedMoveList(int maxSize) {
        this.moves = new int[maxSize];
    }

    public void clear() {
        size = 0;
    }

    public void add(int packedMove) {
        moves[size++] = packedMove;
    }

    public int get(int index) {
        return moves[index];
    }

    public int size() {
        return size;
    }

    public int[] raw() {
        return moves;
    }

    public void remove(int index) {
        if (index < 0 || index >= size) return;
        moves[index] = moves[size - 1]; // Swap with last
        size--; // Shrink list
    }

    // public void sortByScore() {
    //     // Custom sorting implementation for primitive int array
    //     for (int i = 0; i < size - 1; i++) {
    //         for (int j = 0; j < size - i - 1; j++) {
    //             int scoreA = PackedMove.getScore(moves[j]);
    //             int scoreB = PackedMove.getScore(moves[j + 1]);
    //             if (scoreA < scoreB) {
    //                 // Swap
    //                 int temp = moves[j];
    //                 moves[j] = moves[j + 1];
    //                 moves[j + 1] = temp;
    //             }
    //         }
    //     }
    // }

    // // hash move
    // public void prioritize(int move) {
    //     for (int i = 0; i < size; i++) {
    //         if (moves[i] == move) {
    //             PackedMove.setScore(moves[i], 1_000_000); // Set high score
    //         }
    //     }
    // }

    public void shuffle() {
        for (int i = size - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            // Swap moves[i] and moves[j]
            int temp = moves[i];
            moves[i] = moves[j];
            moves[j] = temp;
        }
    }
    
    @Override
    public String toString() {
        // Convert back into a MoveList
        MoveList moveList = new MoveList(size);
        for (int i = 0; i < size; i++) {
            moveList.add(PackedMove.unpack(moves[i]));
        }
        return moveList.toString();
    }

    public boolean contains(int killer1) {
        for (int i = 0; i < size; i++) {
            if (moves[i] == killer1) {
                return true;
            }
        }
        return false;
    }
}
