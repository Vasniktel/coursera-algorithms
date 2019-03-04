import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Percolation {
    private final byte[] grid;
    private final WeightedQuickUnionUF uf;
    private int opened;
    private boolean percolates;
    private final int n;

    private class State {
        private static final byte TOPPED = 1;
        private static final byte BOTTOMED = 2;
        private static final byte ACTIVE = 4;
    }

    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("Invalid 'n' = '" + n + "' value");

        this.n = n;
        this.grid = new byte[n * n];
        for (int i = 0; i < this.grid.length; i++) {
            byte topped = i < n ? State.TOPPED : 0;
            byte bottomed = i >= this.grid.length - n ? State.BOTTOMED : 0;
            this.grid[i] = (byte) (topped | bottomed);
        }

        this.uf = new WeightedQuickUnionUF(n * n);
        this.opened = 0;
        this.percolates = false;
    }

    private int indexed(int row, int col) {
        return row * this.n + col;
    }

    private void validate(int row, int col) {
        if (row > this.n || row < 1 || col > this.n || col < 1)
            throw new IllegalArgumentException("Invalid 'col' and/or 'row' args.");
    }

    public void open(int row, int col) {
        this.validate(row, col);

        int index = this.indexed(row - 1, col - 1);
        if ((this.grid[index] & State.ACTIVE) == 0) {
            this.grid[index] |= State.ACTIVE;

            int up = index - this.n;
            int down = index + this.n;
            int left = index - 1;
            int right = index + 1;
            int topped = this.grid[index] & State.TOPPED;
            int bottomed = this.grid[index] & State.BOTTOMED;
            boolean changed = false;

            if (up >= 0 && (this.grid[up] & State.ACTIVE) != 0) {
                int root = this.uf.find(up);
                topped |= this.grid[root] & State.TOPPED;
                bottomed |= this.grid[root] & State.BOTTOMED;
                this.uf.union(index, up);
                changed = true;
            }
            if (down < this.grid.length && (this.grid[down] & State.ACTIVE) != 0) {
                int root = this.uf.find(down);
                topped |= this.grid[root] & State.TOPPED;
                bottomed |= this.grid[root] & State.BOTTOMED;
                this.uf.union(index, down);
                changed = true;
            }
            if (col > 1 && (this.grid[left] & State.ACTIVE) != 0) {
                int root = this.uf.find(left);
                topped |= this.grid[root] & State.TOPPED;
                bottomed |= this.grid[root] & State.BOTTOMED;
                this.uf.union(index, left);
                changed = true;
            }
            if (col < this.n && (this.grid[right] & State.ACTIVE) != 0) {
                int root = this.uf.find(right);
                topped |= this.grid[root] & State.TOPPED;
                bottomed |= this.grid[root] & State.BOTTOMED;
                this.uf.union(index, right);
                changed = true;
            }

            if (changed) {
                int root = this.uf.find(index);
                this.grid[root] |= bottomed | topped;
            }
            if (bottomed != 0 && topped != 0) this.percolates = true;

            this.opened++;
        }
    }

    public boolean isOpen(int row, int col) {
        this.validate(row, col);
        int index = this.indexed(row - 1, col - 1);
        return (this.grid[index] & State.ACTIVE) != 0;
    }

    public boolean isFull(int row, int col) {
        this.validate(row, col);
        int index = this.indexed(row - 1, col - 1);
        int root = this.uf.find(index);
        return (this.grid[index] & State.ACTIVE) != 0 && (this.grid[root] & State.TOPPED) != 0;
    }

    public int numberOfOpenSites() {
        return this.opened;
    }

    public boolean percolates() {
        return this.percolates;
    }

    public static void main(String[] args) {
        Percolation percolation = new Percolation(StdIn.readInt());

        while (!StdIn.isEmpty()) {
            int row = StdIn.readInt();
            int col = StdIn.readInt();

            StdOut.println("Opened: " + percolation.isOpen(row, col));
            percolation.open(row, col);
            StdOut.println("Opened: " + percolation.isOpen(row, col));
            StdOut.println("Amount: " + percolation.numberOfOpenSites());
            StdOut.println("Full: " + percolation.isFull(row, col));
            StdOut.println("Percolates: " + percolation.percolates());
        }
    }
}
