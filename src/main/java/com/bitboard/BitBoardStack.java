package com.bitboard;

public class BitBoardStack {
    private final BitBoard[] stack;
    private int top = -1;

    public BitBoardStack(int maxDepth) {
        stack = new BitBoard[maxDepth];
        for (int i = 0; i < maxDepth; i++) {
            stack[i] = new BitBoard(); // objets pré-alloués pour éviter les new à runtime
        }
    }

    public void push(BitBoard board) {
        if (top + 1 >= stack.length) {
            throw new RuntimeException("BitBoardStack overflow");
        }
        top++;
        stack[top].copyFrom(board); // copie le contenu du board actuel dans celui du stack
    }

    public BitBoard pop() {
        if (top < 0) {
            throw new RuntimeException("BitBoardStack underflow");
        }
        return stack[top--]; // pas besoin de supprimer l'objet, on le réutilisera
    }

    public BitBoard peek() {
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
