package fr.flwrian.aspira.move;



import java.util.*;
import java.util.function.Consumer;

import fr.flwrian.aspira.board.Board;

public class MoveList implements List<Move> {

    private Move[] moves;
    private int size = 0;


    public MoveList(int maxSize) {
        this.moves = new Move[maxSize];
        for(int i = 0; i < this.moves.length; i++){
            this.moves[i] = new Move(0,0,0,0);
        }
    }

    public void clear(){
        this.size = 0;
    }

    @Override
    public Move get(int index) {
        return this.moves[index];
    }

    @Override
    public Move set(int index, Move element) {
        Move old = this.moves[index];
        this.moves[index] = element;
        return old;
    }

    @Override
    public void add(int index, Move element) {
        this.swap(index, size);
        this.set(index, element);
    }

    public void swap(int index1, int index2){
        Move m = moves[index1];
        moves[index1] = moves[index2];
        moves[index2] = m;
    }

    @Override
    public Move remove(int index) {
        Move m = moves[index];
        moves[index] = moves[size-1];
        moves[size-1] = m;
        size--;
        return m;
    }

    @Override
    public int indexOf(Object o) {
        for(int i = 0; i < size; i++){
            if(moves[i].equals(o)){
                return i;
            }
        }return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for(int i = size-1; i >= 0; i++){
            if(o.equals(moves[i])){
                return i;
            }
        }return -1;
    }

    public void setCapacity(int newCap){
        Move[] ar = new Move[newCap];
        for(int i = 0; i < this.moves.length; i++){
            ar[i] = this.moves[i];
        }
        for(int i = this.moves.length; i < ar.length; i++){
            ar[i] = new Move(0,0,0,0);
        }
        this.moves = ar;
    }

    @Override
    public ListIterator<Move> listIterator() {
        return this.listIterator(-1);
    }

    @Override
    public ListIterator<Move> listIterator(int pindex) {
        return new ListIterator<Move>() {

            int index = pindex;

            @Override
            public boolean hasNext() {
                return index < size - 1;
            }

            @Override
            public Move next() {
                return moves[++index];
            }

            @Override
            public boolean hasPrevious() {
                return index > 0;
            }

            @Override
            public Move previous() {
                return moves[--index];
            }

            @Override
            public int nextIndex() {
                return index + 1;
            }

            @Override
            public int previousIndex() {
                return index - 1;
            }

            @Override
            public void remove() {
                throw new RuntimeException();
            }

            @Override
            public void set(Move move) {
                throw new RuntimeException();
            }

            @Override
            public void add(Move move) {
                throw new RuntimeException();
            }
        };
    }

    @Override
    public void sort(Comparator<? super Move> c) {
        if (this.size == 0) return;
        quicksort(0, this.size-1);
    }

    private void quicksort(int low, int high) {
        int i = low, j = high;
        // Get the pivot element from the middle of the list
        long pivot = moves[low + (high-low)/2].getSeeScore();

        // Divide into two lists
        while (i <= j) {
            // If the current value from the left list is smaller than the pivot
            // element then get the next element from the left list
            while (moves[i].getSeeScore() > pivot) {
                i++;
            }
            // If the current value from the right list is larger than the pivot
            // element then get the next element from the right list
            while (moves[j].getSeeScore() < pivot) {
                j--;
            }

            // If we have found a value in the left list which is larger than
            // the pivot element and if we have found a value in the right list
            // which is smaller than the pivot element then we exchange the
            // values.
            // As we are done we can increase i and j
            if (i <= j) {
                swap(i, j);
                i++;
                j--;
            }
        }
        // Recursion
        if (low < j)
            quicksort(low, j);
        if (i < high)
            quicksort(i, high);
    }

        @Override
    public List<Move> subList(int fromIndex, int toIndex) {
        throw new RuntimeException();
    }


    public Move add(int from, int to, Board board) {
        return this.add(from,to,board.getPiece(from),board.getPiece(to));
    }

    public Move add(int from, int to, int pieceFrom, int pieceTo) {
        if(this.size == moves.length){
            this.setCapacity(moves.length * 2);
        }
        Move m = this.moves[size];
        m.from = from;
        m.to = to;
        m.pieceFrom = pieceFrom;
        m.pieceTo = pieceTo;
        m.type = 0;
        this.size ++;
        return m;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        for(int i = 0; i < this.size; i++){
            if(o.equals(moves[i])) return true;
        }
        return false;
    }

    @Override
    public Iterator<Move> iterator() {
        return new Iterator<Move>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public Move next() {
                return moves[index++];
            }
        };
    }


    @Override
    public Object[] toArray() {
        return Arrays.copyOf(moves, size);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public boolean add(Move move) {
        if(size == this.moves.length){
            setCapacity(size * 2);
        }
        this.moves[size] = move;
        size++;
        return true;
    }

    @Override
    public String toString() {


        StringBuilder builder = new StringBuilder();
        builder.append("MoveList:\n");
        for(int i = 0; i < size(); i++){
            builder.append("\t"+moves[i]+" - seeScore: "+moves[i].getSeeScore()+"\n");
        }

        // add size
        builder.append("Size: "+size()+"\n");

        return builder.toString();
    }

    @Override
    public boolean remove(Object o) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public boolean addAll(Collection<? extends Move> c) {
        for(Move m:c){
            moves[size] = m;
            size++;
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Move> c) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public void forEach(Consumer<? super Move> action) {
        for(int i = 0; i < size; i++){
            action.accept(moves[i]);
        }
    }

    @Override
    public Spliterator<Move> spliterator() {
        throw new RuntimeException("Not yet implemented");
    }

    public static void main(String[] args) {
        MoveList moves = new MoveList(10);
        for(int i = 0; i < 10; i++){
            moves.add(i,6,3,4).setOrderPriority(i);
        }
        moves.sort(null);
        System.out.println(moves);
    }

    
}