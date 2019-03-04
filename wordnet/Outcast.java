import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public final class Outcast {
    private final WordNet net;

    public Outcast(WordNet wordNet) {
        this.net = wordNet;
    }

    public String outcast(String[] nouns) {
        int max = Integer.MIN_VALUE;
        String result = null;

        for (String a : nouns) {
            int sum = 0;
            for (String b : nouns) sum += this.net.distance(a, b);

            if (sum > max) {
                max = sum;
                result = a;
            }
        }

        return result;
    }

    public static void main(String... args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
