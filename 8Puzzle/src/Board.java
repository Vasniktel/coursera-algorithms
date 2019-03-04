import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedBag;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public final class Board {
    private final short[] board;
    private final int hamming;
    private final int manhattan;
    private final int dimension;
    private final int zero;

    private Board(short[] board, int hamming, int manhattan, int dimension, int zero) {
        this.board = board;
        this.hamming = hamming;
        this.manhattan = manhattan;
        this.dimension = dimension;
        this.zero = zero;
    }

    public Board(int[][] blocks) {
        if (blocks == null)
            throw new IllegalArgumentException("Null argument passed.");

        this.dimension = blocks.length;
        this.hamming = calculateHamming(blocks);
        this.manhattan = calculateManhattan(blocks);
        this.board = new short[this.dimension * this.dimension];

        int zeroIndex = -1;
        for (int i = 0, index = 0, n = blocks.length; i < n; i++) {
            for (int j = 0; j < n; j++, index++) {
                short elem = (short) blocks[i][j];
                if (elem == 0) zeroIndex = index;
                this.board[index] = elem;
            }
        }

        this.zero = zeroIndex;

        assert this.zero != -1;
    }

    private static int calculateHamming(int[][] blocks) {
        int result = 0, block = 1;

        for (int[] row : blocks) {
            for (int elem : row) {
                if (elem != 0 && elem != block) result++;
                block++;
            }
        }

        return result;
    }

    private static int calculateManhattan(int[][] blocks) {
        int result = 0;

        for (int i = 0, n = blocks.length, block = 1; i < n; i++) {
            for (int j = 0; j < n; j++, block++) {
                int elem = blocks[i][j];

                if (elem != 0 && elem != block) {
                    int elemI = (elem - 1) / n;
                    int elemJ = (elem - 1) % n;
                    result += Math.abs(i - elemI) + Math.abs(j - elemJ);
                }
            }
        }

        return result;
    }

    private static int distanceFactor(short elem, int currIndex, int prevIndex, int n) {
        int row = currIndex / n; // current location
        int col = currIndex % n; // current location
        int prevRow = prevIndex / n; // previous location
        int prevCol = prevIndex % n; // previous location
        int validRow = (elem - 1) / n; // valid (expected) location
        int validCol = (elem - 1) % n; // valid (expected) location
        int distance = Math.abs(row - validRow) + Math.abs(col - validCol); // current distance
        int prevDistance = Math.abs(prevRow - validRow) + Math.abs(prevCol - validCol); // previous distance
        return distance - prevDistance;
    }

    private static void swap(short[] arr, int i, int j) {
        short temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private int updateHamming(short[] swapped, int i, int j) {
        int result = this.hamming;
        short a = swapped[i];
        short b = swapped[j];

        if (a != 0) {
            if (a == i + 1) result--; // in its place
            else if (this.board[j] == j + 1) result++; // was in its place
        }
        if (b != 0) {
            if (b == j + 1) result--;
            else if (this.board[i] == i + 1) result++;
        }

        return result;
    }

    private int updateManhattan(short[] swapped, int i, int j) {
        int result = this.manhattan;
        short a = swapped[i];
        short b = swapped[j];

        if (a != 0) result += distanceFactor(a, i, j, this.dimension);
        if (b != 0) result += distanceFactor(b, j, i, this.dimension);

        return result;
    }

    private Board getNeighbor(int toSwap) {
        short[] blocks = this.board.clone();
        swap(blocks, toSwap, this.zero);
        int manhattanUpdated = this.updateManhattan(blocks, toSwap, this.zero);
        int hammingUpdated = this.updateHamming(blocks, toSwap, this.zero);
        return new Board(blocks, hammingUpdated, manhattanUpdated, this.dimension, toSwap);
    }

    public int dimension() {
        return this.dimension;
    }

    public int hamming() {
        return this.hamming;
    }

    public int manhattan() {
        return this.manhattan;
    }

    public boolean isGoal() {
        return this.hamming == 0;
    }

    public Board twin() {
        short[] twinBlocks = this.board.clone();
        int a = -1, b = -1;

        for (int i = 0, n = twinBlocks.length; i < n && b == -1; i++) {
            if (twinBlocks[i] != 0) {
                if (a == -1) a = i;
                else b = i;
            }
        }

        assert a != -1 && b != -1 && a != b;

        swap(twinBlocks, a, b);
        int twinHamming = this.updateHamming(twinBlocks, a, b);
        int twinManhattan = this.updateManhattan(twinBlocks, a, b);

        return new Board(twinBlocks, twinHamming, twinManhattan, this.dimension, this.zero);
    }

    @Override
    public boolean equals(Object that) {
        if (that == null) return false;
        if (that == this) return true;
        if (that.getClass() != this.getClass()) return false;
        return Arrays.equals(this.board, ((Board) that).board);
    }

    public Iterable<Board> neighbors() {
        LinkedBag<Board> bag = new LinkedBag<>();
        int i = this.zero / this.dimension;
        int j = this.zero % this.dimension;

        if (i != 0)
            bag.add(this.getNeighbor(this.zero - this.dimension));
        if (i != this.dimension - 1)
            bag.add(this.getNeighbor(this.zero + this.dimension));
        if (j != 0)
            bag.add(this.getNeighbor(this.zero - 1));
        if (j != this.dimension - 1)
            bag.add(this.getNeighbor(this.zero + 1));

        return bag;
    }

    @Override
    public String toString() {
        int n = this.board.length;
        final String format = "%" + ((int) Math.log10(n) + 1) + "d ";
        final StringBuilder builder = new StringBuilder(this.dimension + "\n");

        for (int i = 0, col = 0; i < n; i++, col++) {
            builder.append(String.format(format, this.board[i]));
            if (col == this.dimension - 1) {
                col = -1;
                builder.deleteCharAt(builder.length() - 1).append('\n');
            }
        }

        return builder.toString();
    }

    public static void main(String... args) {
        for (String file : args) {
            In in = new In(file);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tiles[i][j] = in.readInt();
                }
            }

            Board board = new Board(tiles);
            Board twin = board.twin();
            StdOut.println("board:\n" + board);
            StdOut.println("dimension: " + board.dimension());
            StdOut.println("hamming: " + board.hamming());
            StdOut.println("manhattan: " + board.manhattan());
            StdOut.println("isGoal: " + board.isGoal());
            StdOut.println("twin:\n" + twin);
            StdOut.println("dimension: " + twin.dimension());
            StdOut.println("hamming: " + twin.hamming());
            StdOut.println("manhattan: " + twin.manhattan());
            StdOut.println("isGoal: " + twin.isGoal());
            StdOut.println("neighbors:");
            board.neighbors().forEach(elem -> StdOut.println("-:\n" + elem));
        }
    }
}
