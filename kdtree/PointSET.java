import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedBag;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class PointSET {
    private final SET<Point2D> set = new SET<>();

    private static void validate(Object object) {
        if (object == null)
            throw new IllegalArgumentException("Null argument passed.");
    }

    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    public int size() {
        return this.set.size();
    }

    public void insert(Point2D point) {
        validate(point);
        this.set.add(point);
    }

    public boolean contains(Point2D point) {
        validate(point);
        return this.set.contains(point);
    }

    public void draw() {
        for (Point2D point : this.set) point.draw();
    }

    public Iterable<Point2D> range(RectHV rect) {
        validate(rect);
        LinkedBag<Point2D> bag = new LinkedBag<>();

        for (Point2D point : this.set)
            if (rect.contains(point)) bag.add(point);

        return bag;
    }

    public Point2D nearest(Point2D point) {
        validate(point);

        Point2D result = null;
        double min = Double.POSITIVE_INFINITY;

        for (Point2D item : this.set) {
            double distance = item.distanceSquaredTo(point);
            if (distance < min) {
                min = distance;
                result = item;
            }
        }

        return result;
    }

    public static void main(String... args) {
        In in = new In(args[0]);
        PointSET set = new PointSET();

        while (!in.isEmpty()) {
            Point2D point = new Point2D(in.readDouble(), in.readDouble());
            set.insert(point);
        }

        set.draw();

        StdDraw.setPenRadius(0.02);
        Point2D query = new Point2D(StdRandom.uniform(), StdRandom.uniform());
        StdDraw.setPenColor(StdDraw.GREEN);
        query.draw();

        Stopwatch sw = new Stopwatch();
        Point2D result = set.nearest(query);
        double time = sw.elapsedTime();

        StdDraw.setPenColor(StdDraw.ORANGE);
        result.draw();
        StdOut.println(time);
    }
}
