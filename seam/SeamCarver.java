import edu.princeton.cs.algs4.Picture;

import java.util.Arrays;

public class SeamCarver {
    private static final double BORDER_ENERGY = 1000.0;

    private Picture picture;
    private double[][] energy;
    private boolean transposed = false;

    public SeamCarver(Picture picture) {
        validateArgument(picture);

        this.picture = new Picture(picture);
        this.energy = new double[picture.height()][picture.width()];

        for (int row = 0, h = picture.height(); row < h; row++) {
            for (int col = 0, w = picture.width(); col < w; col++)
                this.energy[row][col] = this.calcEnergy(col, row);
        }
    }

    private static void validateArgument(Object object) {
        if (object == null)
            throw new IllegalArgumentException("Null argument passed.");
    }

    private static int r(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    private static int g(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    private static int b(int rgb) {
        return rgb & 0xFF;
    }

    private static double deltaXSquared(Picture picture, int col, int row) {
        int left = picture.getRGB(col - 1, row);
        int right = picture.getRGB(col + 1, row);

        double rxSquared = Math.pow(r(left) - r(right), 2);
        double gxSquared = Math.pow(g(left) - g(right), 2);
        double bxSquared = Math.pow(b(left) - b(right), 2);

        return rxSquared + gxSquared + bxSquared;
    }

    private static double deltaYSquared(Picture picture, int col, int row) {
        int up = picture.getRGB(col, row - 1);
        int down = picture.getRGB(col, row + 1);

        double rySquared = Math.pow(r(up) - r(down), 2);
        double gySquared = Math.pow(g(up) - g(down), 2);
        double bySquared = Math.pow(b(up) - b(down), 2);

        return rySquared + gySquared + bySquared;
    }

    private static double[][] transpose(double[][] arr) {
        double[][] result = new double[arr[0].length][arr.length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++)
                result[j][i] = arr[i][j];
        }

        return result;
    }

    private static int[] traceSeam(int[][] edgeTo, double[][] distTo) {
        int rows = distTo.length;
        int cols = distTo[0].length;
        int[] seam = new int[rows];

        int index = -1;
        double min = Double.POSITIVE_INFINITY;

        for (int i = 0; i < cols; i++) {
            if (distTo[rows - 1][i] < min) {
                min = distTo[rows - 1][i];
                index = i;
            }
        }

        assert index != -1;

        for (int i = rows - 1, e = index; i >= 0; e = edgeTo[i--][e])
            seam[i] = e;

        return seam;
    }

    private double calcEnergy(int col, int row) {
        int c = !this.transposed ? col : row;
        int r = !this.transposed ? row : col;

        if (r == 0 || r == this.picture.height() - 1) return BORDER_ENERGY;
        if (c == 0 || c == this.picture.width() - 1) return BORDER_ENERGY;

        double x = deltaXSquared(this.picture, c, r);
        double y = deltaYSquared(this.picture, c, r);
        return Math.sqrt(x + y);
    }

    private void validateSeam(int[] seam, boolean isVertical) {
        int a = isVertical ? this.height() : this.width();
        int b = isVertical ? this.width() : this.height();

        if (seam.length != a) {
            throw new IllegalArgumentException("Invalid seam length.");
        }

        if (seam[0] < 0 || seam[0] >= b)
            throw new IllegalArgumentException("Invalid seam value.");

        for (int i = 1, n = seam.length; i < n; i++) {
            if (seam[i] < 0 || seam[i] >= b)
                throw new IllegalArgumentException("Invalid seam value.");

            if (Math.abs(seam[i] - seam[i - 1]) > 1)
                throw new IllegalArgumentException("Seam values differ > 1.");
        }
    }

    private void relax(int row1, int col1, int col2, int[][] edgeTo, double[][] distTo) {
        int row2 = row1 + 1;
        double dist = distTo[row1][col1] + this.energy[row2][col2];
        if (distTo[row2][col2] > dist) {
            distTo[row2][col2] = dist;
            edgeTo[row2][col2] = col1;
        }
    }

    private void dagSP(int[][] edgeTo, double[][] distTo) {
        int rows = this.energy.length;
        int cols = this.energy[0].length;

        for (int row = 0; row < rows - 1; row++) {
            for (int col = 0; col < cols; col++) {
                this.relax(row, col, col, edgeTo, distTo);
                if (col != 0)
                    this.relax(row, col, col - 1, edgeTo, distTo);
                if (col != cols - 1)
                    this.relax(row, col, col + 1, edgeTo, distTo);
            }
        }
    }

    private int[] findSeam() {
        int rows = this.energy.length;
        int cols = this.energy[0].length;

        int[][] edgeTo = new int[rows][cols];
        for (int i = 0; i < cols; i++) edgeTo[0][i] = i;

        double[][] distTo = new double[rows][cols];
        Arrays.fill(distTo[0], BORDER_ENERGY);
        for (int i = 1; i < rows; i++)
            Arrays.fill(distTo[i], Double.POSITIVE_INFINITY);

        this.dagSP(edgeTo, distTo);

        return traceSeam(edgeTo, distTo);
    }

    private void fixEnergy(int[] seam) {
        double[][] old = this.energy;
        int rows = old.length;
        int cols = old[0].length;

        this.energy = new double[rows][cols - 1];

        for (int i = 0; i < rows; i++) {
            int j = seam[i];
            System.arraycopy(old[i], 0, this.energy[i], 0, j);
            if (j != cols - 1)
                System.arraycopy(old[i], j + 1, this.energy[i], j, cols - j - 1);
        }

        for (int row = 0; row < rows; row++) {
            int col = seam[row];
            if (col != cols - 1)
                this.energy[row][col] = this.calcEnergy(col, row);
            if (col != 0)
                this.energy[row][col - 1] = this.calcEnergy(col - 1, row);
        }
    }

    public Picture picture() {
        return new Picture(this.picture);
    }

    public int width() {
        return this.picture.width();
    }

    public int height() {
        return this.picture.height();
    }

    public double energy(int col, int row) {
        if (col < 0 || col >= this.width() || row < 0 || row >= this.height())
            throw new IllegalArgumentException("Invalid indices passed.");

        return !this.transposed ? this.energy[row][col] : this.energy[col][row];
    }

    public int[] findHorizontalSeam() {
        if (!this.transposed) {
            this.energy = transpose(this.energy);
            this.transposed = !this.transposed;
        }
        return this.findSeam();
    }

    public int[] findVerticalSeam() {
        if (this.transposed) {
            this.energy = transpose(this.energy);
            this.transposed = !this.transposed;
        }
        return this.findSeam();
    }

    public void removeHorizontalSeam(int[] seam) {
        validateArgument(seam);
        this.validateSeam(seam, false);
        if (this.height() <= 1)
            throw new IllegalArgumentException("Picture height <= 1.");

        Picture old = this.picture;
        this.picture = new Picture(old.width(), old.height() - 1);

        for (int col = 0; col < old.width(); col++) {
            for (int row = 0, index = 0; row < old.height(); row++) {
                if (row != seam[col])
                    this.picture.setRGB(col, index++, old.getRGB(col, row));
            }
        }

        if (!this.transposed) {
            this.energy = transpose(this.energy);
            this.transposed = !this.transposed;
        }

        this.fixEnergy(seam);
    }

    public void removeVerticalSeam(int[] seam) {
        validateArgument(seam);
        this.validateSeam(seam, true);
        if (this.width() <= 1)
            throw new IllegalArgumentException("Picture width <= 1.");

        Picture old = this.picture;
        this.picture = new Picture(old.width() - 1, old.height());

        for (int row = 0; row < old.height(); row++) {
            for (int col = 0, index = 0; col < old.width(); col++) {
                if (col != seam[row])
                    this.picture.setRGB(index++, row, old.getRGB(col, row));
            }
        }

        if (this.transposed) {
            this.energy = transpose(this.energy);
            this.transposed = !this.transposed;
        }

        this.fixEnergy(seam);
    }
}
