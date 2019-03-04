import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

import java.util.HashSet;

public final class BreadthFirstSAP {
    private static final int INFINITY = Integer.MAX_VALUE;

    private final Digraph graph;
    private final HashSet<Integer> marked1;
    private final HashSet<Integer> marked2;
    private final int[] distTo1;
    private final int[] distTo2;
    private int length = INFINITY;
    private int ancestor = -1;

    public BreadthFirstSAP(Digraph graph) {
        this.validateArgument(graph);
        this.graph = new Digraph(graph);
        this.marked1 = new HashSet<>();
        this.marked2 = new HashSet<>();
        this.distTo1 = new int[graph.V()];
        this.distTo2 = new int[graph.V()];
    }

    private void validateArgument(Digraph digraph) {
        if (digraph == null)
            throw new IllegalArgumentException("Null argument passed.");
    }

    private void validateArgument(int arg) {
        if (arg < 0 || arg >= this.graph.V())
            throw new IllegalArgumentException("Vertex id out of range.");
    }

    private void validateArgument(Iterable<Integer> arg) {
        if (arg == null)
            throw new IllegalArgumentException("Null argument passed.");

        // boolean isEmpty = true; // BUG
        // noinspection CheckStyle
        for (Integer item : arg) {
            // isEmpty = false;
            if (item == null)
                throw new IllegalArgumentException("Iterable has 'null' item.");
            this.validateArgument(item);
        }

        // BUG
        // if (isEmpty)
        //  throw new IllegalArgumentException("Iterable has no elements.");
    }

    private void bfsLoop(Queue<Integer> queue1, Queue<Integer> queue2) {
        while (!queue1.isEmpty() || !queue2.isEmpty()) {
            if (!queue1.isEmpty()) {
                int v = queue1.dequeue();

                if (this.marked2.contains(v)) {
                    int distance = this.distTo1[v] + this.distTo2[v];
                    if (distance < this.length) {
                        this.length = distance;
                        this.ancestor = v;
                    }
                }

                for (int w : this.graph.adj(v)) {
                    if (!this.marked1.contains(w)) {
                        this.marked1.add(w);
                        this.distTo1[w] = this.distTo1[v] + 1;
                        queue1.enqueue(w);
                    }
                }
            }

            if (!queue2.isEmpty()) {
                int v = queue2.dequeue();

                if (this.marked1.contains(v)) {
                    int distance = this.distTo1[v] + this.distTo2[v];
                    if (distance < this.length) {
                        this.length = distance;
                        this.ancestor = v;
                    }
                }

                for (int w : this.graph.adj(v)) {
                    if (!this.marked2.contains(w)) {
                        this.marked2.add(w);
                        this.distTo2[w] = this.distTo2[v] + 1;
                        queue2.enqueue(w);
                    }
                }
            }
        }
    }

    private void bfs(int a, int b) {
        Queue<Integer> queue1 = new Queue<>();
        Queue<Integer> queue2 = new Queue<>();
        queue1.enqueue(a);
        queue2.enqueue(b);
        this.marked1.add(a);
        this.marked2.add(b);
        this.distTo1[a] = 0;
        this.distTo2[b] = 0;

        this.bfsLoop(queue1, queue2);
    }

    private void bfs(Iterable<Integer> a, Iterable<Integer> b) {
        Queue<Integer> queue1 = new Queue<>();
        Queue<Integer> queue2 = new Queue<>();

        for (int x : a) {
            queue1.enqueue(x);
            this.marked1.add(x);
            this.distTo1[x] = 0;
        }

        for (int x : b) {
            queue2.enqueue(x);
            this.marked2.add(x);
            this.distTo2[x] = 0;
        }

        this.bfsLoop(queue1, queue2);
    }

    public boolean hasPath() {
        return this.length != INFINITY;
    }

    public BreadthFirstSAP execute(int v, int w) {
        this.validateArgument(v);
        this.validateArgument(w);
        this.marked1.clear();
        this.marked2.clear();
        this.ancestor = -1;
        this.length = INFINITY;
        this.bfs(v, w);
        return this;
    }

    public BreadthFirstSAP execute(Iterable<Integer> v, Iterable<Integer> w) {
        this.validateArgument(v);
        this.validateArgument(w);
        this.marked1.clear();
        this.marked2.clear();
        this.ancestor = -1;
        this.length = INFINITY;
        this.bfs(v, w);
        return this;
    }

    public int length() {
        return this.hasPath() ? this.length : -1;
    }

    public int ancestor() {
        return this.ancestor;
    }
}
