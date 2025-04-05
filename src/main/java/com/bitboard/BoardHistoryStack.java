package com.bitboard;

public class BoardHistoryStack {
    public final BoardHistory[] stack;
    private int top = -1;

    public BoardHistoryStack(int maxDepth) {
        stack = new BoardHistory[maxDepth];
        for (int i = 0; i < maxDepth; i++) {
            stack[i] = new BoardHistory();
        }
    }

    public void push(BitBoard board, long move) {
        if (top + 1 >= stack.length) {
            throw new RuntimeException("BoardHistoryStack overflow");
        }
        top++;
        stack[top].copyFrom(board, move);
    }

    public BoardHistory pop() {
        if (top < 0) {
            throw new RuntimeException("BoardHistoryStack underflow");
        }
        return stack[top--];
    }

    public void clear() {
        top = -1;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public void printStack() {
        for (int i = 0; i <= top; i++) {
            System.out.println("Stack[" + i + "]: Hash -> " + Long.toHexString(stack[i].zobristKey) + " Move -> " + PackedMove.unpack(stack[i].move));
        }
    }
}
