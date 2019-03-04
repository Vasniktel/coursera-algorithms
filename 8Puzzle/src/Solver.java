import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.ResizingArrayStack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public final class Solver {
    private final boolean solvable;
    private final int moves;
    private final Iterable<Board> solution;

    private static final class SearchNode {
        private static final Comparator<SearchNode> MANHATTAN_ORDER = Comparator.comparingInt(a -> a.manhattan);
        private static final Comparator<SearchNode> HAMMING_ORDER = Comparator.comparingInt(a -> a.hamming);

        private final Board board;
        private final SearchNode prev;
        private final int moves;
        private final int manhattan;
        private final int hamming;

        private SearchNode(Board board, SearchNode prev, int moves) {
            this.board = board;
            this.prev = prev;
            this.moves = moves;
            this.manhattan = moves + board.manhattan();
            this.hamming = moves + board.hamming();
        }
    }

    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException("Null argument passed.");

        MinPQ<SearchNode> pq = new MinPQ<>(SearchNode.MANHATTAN_ORDER);
        pq.insert(new SearchNode(initial, null, 0));

        MinPQ<SearchNode> twinPQ = new MinPQ<>(SearchNode.MANHATTAN_ORDER);
        twinPQ.insert(new SearchNode(initial.twin(), null, 0));

        SearchNode node, twin;
        do {
            node = pq.delMin();
            twin = twinPQ.delMin();
            if (node.board.isGoal() || twin.board.isGoal()) break;

            for (Board board : node.board.neighbors()) {
                if (node.prev == null || !board.equals(node.prev.board))
                    pq.insert(new SearchNode(board, node, node.moves + 1));
            }

            for (Board board : twin.board.neighbors()) {
                if (twin.prev == null || !board.equals(twin.prev.board))
                    twinPQ.insert(new SearchNode(board, twin, twin.moves + 1));
            }
        } while (!pq.isEmpty() && !twinPQ.isEmpty());

        if (node.board.isGoal()) {
            this.solvable = true;
            this.moves = node.moves;

            ResizingArrayStack<Board> stack = new ResizingArrayStack<>();
            for (; node != null; node = node.prev) stack.push(node.board);

            this.solution = stack;
        } else {
            this.solvable = false;
            this.moves = -1;
            this.solution = null;
        }
    }

    public boolean isSolvable() {
        return this.solvable;
    }

    public int moves() {
        return this.moves;
    }

    public Iterable<Board> solution() {
        return this.solution;
    }

    public static void main(String... args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
