import java.util.Arrays;

public class BruteCollinearPoints {
    private final LineSegment[] lineSegments;
    private int count = 0;

    public BruteCollinearPoints(Point[] points) {
        validate(points);

        int n = points.length;
        LineSegment[] arr = new LineSegment[n * n];
        for (int i = 0; i < n - 3; i++) {
            for (int j = i + 1; j < n - 2; j++) {
                for (int k = j + 1; k < n - 1; k++) {
                    Point a = points[i];
                    Point b = points[j];
                    Point c = points[k];
                    double ab = a.slopeTo(b);
                    double ac = a.slopeTo(c);

                    if (ab != ac) continue;

                    for (int p = k + 1; p < n; p++) {
                        Point d = points[p];
                        double ad = a.slopeTo(d);

                        if (ad == ab) {
                            arr[this.count++] = makeSegment(a, b, c, d);
                            break;
                        }
                    }
                }
            }
        }

        this.lineSegments = new LineSegment[this.count];
        System.arraycopy(arr, 0, this.lineSegments, 0, this.count);
    }

    private static void validate(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException("Argument is 'null'.");

        for (Point p : points) {
            if (p == null)
                throw new IllegalArgumentException("Array contains a null reference.");
        }

        Point[] cloned = points.clone();
        Arrays.sort(cloned);
        for (int i = 1, n = cloned.length; i < n; i++) {
            if (cloned[i].compareTo(cloned[i - 1]) == 0)
                throw new IllegalArgumentException("Equal points are not allowed.");
        }
    }

    private static Point max(Point a, Point b) {
        return a.compareTo(b) > 0 ? a : b;
    }

    private static Point min(Point a, Point b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    private static LineSegment makeSegment(Point a, Point b, Point c, Point d) {
        Point maxPoint = BruteCollinearPoints.max(a, b);
        Point minPoint = BruteCollinearPoints.min(a, b);
        maxPoint = BruteCollinearPoints.max(maxPoint, BruteCollinearPoints.max(c, d));
        minPoint = BruteCollinearPoints.min(minPoint, BruteCollinearPoints.min(c, d));
        return new LineSegment(minPoint, maxPoint);
    }

    public int numberOfSegments() {
        return this.count;
    }

    public LineSegment[] segments() {
        return this.lineSegments.clone();
    }
}
