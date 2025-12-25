package fr.flwrian.aspira.board;

public class BoardStack {
    private final Board[] stack;
    private int top = -1;

    public BoardStack(int maxDepth) {
        stack = new Board[maxDepth];
        for (int i = 0; i < maxDepth; i++) {
            stack[i] = new Board(); // objets pré-alloués pour éviter les new à runtime
        }
    }

    public void push(Board board) {
        if (top + 1 >= stack.length) {
            throw new RuntimeException("BitBoardStack overflow");
        }
        top++;
        stack[top].copyFrom(board); // copie le contenu du board actuel dans celui du stack
    }

    public Board pop() {
        if (top < 0) {
            throw new RuntimeException("BitBoardStack underflow");
        }
        return stack[top--]; // pas besoin de supprimer l'objet, on le réutilisera
    }

    public Board peek() {
        if (top < 0) {
            throw new RuntimeException("BitBoardStack is empty");
        }
        return stack[top];
    }

    public void clear() {
        top = -1;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }
}
