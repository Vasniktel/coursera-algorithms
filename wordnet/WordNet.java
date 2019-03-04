import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;

public final class WordNet {
    private final SeparateChainingHashST<String, HashSet<Integer>> ids;
    private final String[] synsets;
    private final SAP sap;

    public WordNet(String synsetsFilename, String hypernymsFilename) {
        this.validateArgument(synsetsFilename);
        this.validateArgument(hypernymsFilename);

        this.ids = new SeparateChainingHashST<>();
        this.synsets = this.initSynsetsData(synsetsFilename);
        Digraph digraph = createDigraph(hypernymsFilename, this.synsets.length);
        validateDigraph(digraph);
        this.sap = new SAP(digraph);
    }

    private static Digraph createDigraph(String filename, int num) {
        Digraph digraph = new Digraph(num);

        for (String line : new In(filename).readAllLines()) {
            String[] data = line.split(",");
            int v = Integer.parseInt(data[0]);

            for (int i = 1, n = data.length; i < n; i++)
                digraph.addEdge(v, Integer.parseInt(data[i]));
        }

        return digraph;
    }

    private static void validateDigraph(Digraph digraph) {
        DirectedCycle cycle = new DirectedCycle(digraph);
        if (cycle.hasCycle())
            throw new IllegalArgumentException("Digraph isn't a DAG.");

        for (int v = 0, count = 0, n = digraph.V(); v < n; v++)
            if (digraph.outdegree(v) == 0 && ++count > 1)
                throw new IllegalArgumentException("Digraph has many roots.");
    }

    private String[] initSynsetsData(String filename) {
        String[] lines = new In(filename).readAllLines();
        String[] synsetsData = new String[lines.length];

        for (String line : lines) {
            String[] items = line.split(",");
            String[] words = items[1].split("\\s");
            int i = Integer.parseInt(items[0]);

            synsetsData[i] = items[1];
            for (String word : words) {
                if (!this.ids.contains(word))
                    this.ids.put(word, new HashSet<>());
                this.ids.get(word).add(i);
            }
        }

        return synsetsData;
    }

    private void validateArgument(String string) {
        if (string == null)
            throw new IllegalArgumentException("Null argument passed.");
    }

    private void validateArgument(String wordA, String wordB) {
        this.validateArgument(wordA);
        this.validateArgument(wordB);
        if (!this.ids.contains(wordA) || !this.ids.contains(wordB))
            throw new IllegalArgumentException("Invalid word(s) passed.");
    }

    public Iterable<String> nouns() {
        return this.ids.keys();
    }

    public boolean isNoun(String word) {
        this.validateArgument(word);
        return this.ids.contains(word);
    }

    public int distance(String nounA, String nounB) {
        this.validateArgument(nounA, nounB);
        Iterable<Integer> a = this.ids.get(nounA);
        Iterable<Integer> b = this.ids.get(nounB);
        return this.sap.length(a, b);
    }

    public String sap(String nounA, String nounB) {
        this.validateArgument(nounA, nounB);
        Iterable<Integer> a = this.ids.get(nounA);
        Iterable<Integer> b = this.ids.get(nounB);
        return this.synsets[this.sap.ancestor(a, b)];
    }

    public static void main(String... args) {
        WordNet net = new WordNet(args[0], args[1]);
        StdOut.println(net.sap("Barak_Obama", "monkey"));
    }
}
