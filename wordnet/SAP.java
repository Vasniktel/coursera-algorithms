import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public final class SAP {
    private final BreadthFirstSAP bfs;

    public SAP(Digraph digraph) {
        this.bfs = new BreadthFirstSAP(digraph);
    }

    public int length(int v, int w) {
        return this.bfs.execute(v, w).length();
    }

    public int ancestor(int v, int w) {
        return this.bfs.execute(v, w).ancestor();
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return this.bfs.execute(v, w).length();
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return this.bfs.execute(v, w).ancestor();
    }

    public static void main(String... args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
