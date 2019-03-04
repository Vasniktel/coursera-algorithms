import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public final class BaseballElimination {
    private static final int NOT_COMPUTED = -1;

    private final SeparateChainingHashST<String, Integer> map;
    private final String[] invertedMap;
    private final int[] wins;
    private final int[] loss;
    private final int[] left;
    private final int[][] games;
    private final int[] trivial;
    private final Bag<String>[] certificate;

    public BaseballElimination(String filename) {
        In in = new In(filename);

        int n = in.readInt();
        this.map = new SeparateChainingHashST<>(n);
        this.invertedMap = new String[n];
        this.wins = new int[n];
        this.loss = new int[n];
        this.left = new int[n];
        this.games = new int[n][n];
        this.trivial = new int[n];
        Arrays.fill(this.trivial, NOT_COMPUTED);
        // noinspection unchecked
        this.certificate = (Bag<String>[]) new Bag[n];

        this.readData(in, n);
    }

    private void validateTeamName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Team name is null.");

        if (!this.map.contains(name))
            throw new IllegalArgumentException("Team name doesn't exist.");
    }

    private void readData(In in, int n) {
        for (int i = 0; i < n; i++) {
            String team = in.readString();
            this.map.put(team, i);
            this.invertedMap[i] = team;
            this.wins[i] = in.readInt();
            this.loss[i] = in.readInt();
            this.left[i] = in.readInt();

            for (int j = 0; j < n; j++)
                this.games[i][j] = in.readInt();
        }
    }

    private int triviallyEliminate(int id) {
        int eliminatedBy = id;
        int possible = this.wins[id] + this.left[id];

        for (int i = 0, n = this.numberOfTeams(); i < n; i++) {
            if (i != id && possible < this.wins[i]) {
                eliminatedBy = i;
                break;
            }
        }

        return eliminatedBy;
    }

    private FlowNetwork buildNetwork(int id) {
        int n = this.numberOfTeams();
        FlowNetwork result = new FlowNetwork(n - 1 + (n - 2) * (n - 1) / 2 + 2);
        int s = result.V() - 2, t = result.V() - 1;

        for (int i = 0, count = 0; i < n; i++) {
            for (int j = i + 1; j < n && i != id; j++) {
                if (j == id || i == j) continue;

                int game = n - 1 + count++;
                int v = i < id ? i : i - 1;
                int w = j < id ? j : j - 1;

                result.addEdge(new FlowEdge(game, v, Double.POSITIVE_INFINITY));
                result.addEdge(new FlowEdge(game, w, Double.POSITIVE_INFINITY));
                result.addEdge(new FlowEdge(s, game, this.games[i][j]));
            }
        }

        int possible = this.wins[id] + this.left[id];
        for (int i = 0; i < n; i++) {
            if (i == id) continue;
            int v = i < id ? i : i - 1;
            result.addEdge(new FlowEdge(v, t, possible - this.wins[i]));
        }

        return result;
    }

    private Bag<String> nontriviallyEliminate(int id) {
        FlowNetwork network = this.buildNetwork(id);
        int s = network.V() - 2, t = network.V() - 1;
        FordFulkerson flow = new FordFulkerson(network, s, t);

        Bag<String> result = null;
        for (int v = 0, n = this.numberOfTeams(); v < n - 1; v++) {
            if (flow.inCut(v)) {
                if (result == null) result = new Bag<>();
                int i = v < id ? v : v + 1;
                result.add(this.invertedMap[i]);
            }
        }

        return result;
    }

    public int numberOfTeams() {
        return this.invertedMap.length;
    }

    public Iterable<String> teams() {
        return this.map.keys();
    }

    public int wins(String team) {
        this.validateTeamName(team);
        return this.wins[this.map.get(team)];
    }

    public int losses(String team) {
        this.validateTeamName(team);
        return this.loss[this.map.get(team)];
    }

    public int remaining(String team) {
        this.validateTeamName(team);
        return this.left[this.map.get(team)];
    }

    public int against(String team1, String team2) {
        this.validateTeamName(team1);
        this.validateTeamName(team2);
        return this.games[this.map.get(team1)][this.map.get(team2)];
    }

    public boolean isEliminated(String team) {
        return this.certificateOfElimination(team) != null;
    }

    public Iterable<String> certificateOfElimination(String team) {
        this.validateTeamName(team);

        int id = this.map.get(team);
        if (this.trivial[id] == NOT_COMPUTED) {
            this.trivial[id] = this.triviallyEliminate(id);

            if (this.trivial[id] != id) { // eliminated
                String eliminatedBy = this.invertedMap[this.trivial[id]];
                this.certificate[id] = new Bag<>();
                this.certificate[id].add(eliminatedBy);
            } else this.certificate[id] = this.nontriviallyEliminate(id);
        }

        return this.certificate[id];
    }

    public static void main(String... args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
