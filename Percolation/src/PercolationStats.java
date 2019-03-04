import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdOut;

public class PercolationStats {
    private static final double FACTOR = 1.96D;

    private final double mean;
    private final double stddev;
    private final double confLow;
    private final double confHigh;

    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) throw new IllegalArgumentException("Invalid constructor arguments");

        double[] data = new double[trials];

        for (int i = 0; i < trials; i++) {
            Percolation percolation = new Percolation(n);

            while (!percolation.percolates()) {
                int row = StdRandom.uniform(n) + 1;
                int col = StdRandom.uniform(n) + 1;
                percolation.open(row, col);
            }

            data[i] = (double) percolation.numberOfOpenSites() / (n * n);
        }

        this.mean = StdStats.mean(data);
        this.stddev = StdStats.stddev(data);
        this.confLow = this.mean - this.stddev * FACTOR / Math.sqrt(trials);
        this.confHigh = this.mean + this.stddev * FACTOR / Math.sqrt(trials);
    }

    public double mean() {
        return this.mean;
    }

    public double stddev() {
        return this.stddev;
    }

    public double confidenceLo() {
        return this.confLow;
    }

    public double confidenceHi() {
        return this.confHigh;
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        PercolationStats stats = new PercolationStats(n, trials);

        StdOut.printf("%-23s = %.7f\n", "mean", stats.mean());
        StdOut.printf("%-23s = %.16f\n", "stddev", stats.stddev());
        StdOut.printf("%-23s = [%.16f, %.16f]\n", "95% confidence interval", stats.confidenceLo(), stats.confidenceHi());
    }
}
