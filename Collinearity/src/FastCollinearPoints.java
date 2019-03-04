import java.util.Arrays;

public class FastCollinearPoints {
    private final LineSegment[] lineSegments;
    private int count = 0;

    private static class MinMaxHolder {
        private final Point min;
        private final Point max;

        private MinMaxHolder(Point min, Point max) {
            this.min = min;
            this.max = max;
        }
    }

    public FastCollinearPoints(Point[] points) {
        validate(points);

        int n = points.length;
        LineSegment[] arr = new LineSegment[n * n];
        Point[] copy = points.clone();
        for (Point origin : points) {
            Arrays.sort(copy, origin.slopeOrder());

            int i = 1;
            while (i < n) {
                int componentLength = collinearComponent(copy, i, origin) - 1;

                if (componentLength >= 3) {
                    MinMaxHolder minMaxPoints = minMax(copy, i, componentLength);

                    if (minMaxPoints.min.compareTo(origin) > 0)
                        arr[this.count++] = new LineSegment(origin, minMaxPoints.max);
                }

                i += componentLength;
            }
        }

        this.lineSegments = new LineSegment[this.count];
        System.arraycopy(arr, 0, this.lineSegments, 0, this.count);
    }

    private static MinMaxHolder minMax(Point[] points, int start, int length) {
        Point minPoint, maxPoint;

        final int end = start + length;
        if (length % 2 != 0) {
            minPoint = points[start];
            maxPoint = points[start];
            start += 2;
        } else {
            Point a = points[start];
            Point b = points[start + 1];

            if (a.compareTo(b) < 0) {
                minPoint = a;
                maxPoint = b;
            } else {
                minPoint = b;
                maxPoint = a;
            }

            start += 3;
        }

        for (int i = start; i < end; i += 2) {
            Point a = points[i];
            Point b = points[i - 1];

            if (a.compareTo(b) < 0) {
                if (a.compareTo(minPoint) < 0)
                    minPoint = a;
                if (b.compareTo(maxPoint) > 0)
                    maxPoint = b;
            } else {
                if (b.compareTo(minPoint) < 0)
                    minPoint = b;
                if (a.compareTo(maxPoint) > 0)
                    maxPoint = a;
            }
        }

        return new MinMaxHolder(minPoint, maxPoint);
    }

    private static int collinearComponent(Point[] points, int start, Point origin) {
        double slope = origin.slopeTo(points[start]);
        int count = 2;

        for (int i = start + 1, n = points.length; i < n; i++) {
            if (origin.slopeTo(points[i]) == slope) count++;
            else break;
        }

        return count;
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

    public int numberOfSegments() {
        return this.count;
    }

    public LineSegment[] segments() {
        return this.lineSegments.clone();
    }
}
