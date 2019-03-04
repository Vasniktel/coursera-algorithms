import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedBag;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class KdTree {
    private Node root = null;
    private int size = 0;

    private static class Node {
        private final Point2D item;
        private Node left;
        private Node right;
        private RectHV rect;

        private Node(Point2D item) {
            this.item = item;
        }
    }

    private static void validate(Object object) {
        if (object == null)
            throw new IllegalArgumentException("Null argument passed.");
    }

    private void draw(Node node, boolean isEven) {
        if (node == null) return;

        StdDraw.setPenRadius();
        if (isEven) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(
                    node.item.x(),
                    node.rect.ymin(),
                    node.item.x(),
                    node.rect.ymax()
            );
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(
                    node.rect.xmin(),
                    node.item.y(),
                    node.rect.xmax(),
                    node.item.y()
            );
        }

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.item.draw();

        this.draw(node.left, !isEven);
        this.draw(node.right, !isEven);
    }

    private void range(Node node, RectHV rect, LinkedBag<Point2D> bag) {
        if (rect.contains(node.item)) bag.add(node.item);
        if (node.left != null && rect.intersects(node.left.rect))
            this.range(node.left, rect, bag);
        if (node.right != null && rect.intersects(node.right.rect))
            this.range(node.right, rect, bag);
    }

    private Point2D nearest(Node node, Point2D point, double min) {
        Point2D result = null;
        double distance = node.item.distanceSquaredTo(point);

        if (distance < min) {
            min = distance;
            result = node.item;
        }

        if (node.left != null && node.right != null) {
            double leftDistance = node.left.rect.distanceSquaredTo(point);
            double rightDistance = node.right.rect.distanceSquaredTo(point);

            if (Double.compare(leftDistance, rightDistance) < 0) {
                Point2D nearestLeft = this.nearest(node.left, point, min);
                if (nearestLeft != null) {
                    result = nearestLeft;
                    min = nearestLeft.distanceSquaredTo(point);
                }

                if (Double.compare(rightDistance, min) < 0) {
                    Point2D nearestRight = this.nearest(node.right, point, min);
                    if (nearestRight != null) result = nearestRight;
                }
            } else {
                Point2D nearestRight = this.nearest(node.right, point, min);
                if (nearestRight != null) {
                    result = nearestRight;
                    min = nearestRight.distanceSquaredTo(point);
                }

                if (Double.compare(leftDistance, min) < 0) {
                    Point2D nearestLeft = this.nearest(node.left, point, min);
                    if (nearestLeft != null) result = nearestLeft;
                }
            }
        } else if (node.left != null || node.right != null) {
            Node child = node.left != null ? node.left : node.right;

            if (Double.compare(child.rect.distanceSquaredTo(point), min) < 0) {
                Point2D nearest = this.nearest(child, point, min);
                if (nearest != null) result = nearest;
            }
        }

        return result;
    }

    private Node insert(Node node, Point2D point, boolean isEven) {
        if (node == null) {
            this.size++;
            return new Node(point);
        }
        if (point.equals(node.item)) return node;

        int comp = isEven
                   ? Double.compare(point.x(), node.item.x())
                   : Double.compare(point.y(), node.item.y());

        if (comp < 0) {
            node.left = this.insert(node.left, point, !isEven);
            if (node.left.rect == null)
                node.left.rect = new RectHV(
                        node.rect.xmin(),
                        node.rect.ymin(),
                        isEven  ? node.item.x() : node.rect.xmax(),
                        !isEven ? node.item.y() : node.rect.ymax()
                );
        } else {
            node.right = this.insert(node.right, point, !isEven);
            if (node.right.rect == null)
                node.right.rect = new RectHV(
                        isEven  ? node.item.x() : node.rect.xmin(),
                        !isEven ? node.item.y() : node.rect.ymin(),
                        node.rect.xmax(),
                        node.rect.ymax()
                );
        }

        return node;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int size() {
        return this.size;
    }

    public void insert(Point2D point) {
        validate(point);
        this.root = this.insert(this.root, point, true);
        if (this.root.rect == null)
            this.root.rect = new RectHV(0, 0, 1, 1);
    }

    public boolean contains(Point2D point) {
        validate(point);

        Node node = this.root;
        boolean isEven = true;
        while (node != null) {
            if (point.equals(node.item)) return true;

            int comp = isEven
                       ? Double.compare(point.x(), node.item.x())
                       : Double.compare(point.y(), node.item.y());

            if (comp < 0) node = node.left;
            else node = node.right;
            isEven = !isEven;
        }

        return false;
    }

    public void draw() {
        this.draw(this.root, true);
    }

    public Iterable<Point2D> range(RectHV rect) {
        validate(rect);
        LinkedBag<Point2D> bag = new LinkedBag<>();
        if (this.root != null) this.range(this.root, rect, bag);
        return bag;
    }

    public Point2D nearest(Point2D point) {
        validate(point);
        if (this.isEmpty()) return null;
        return this.nearest(this.root, point, Double.POSITIVE_INFINITY);
    }

    public static void main(String... args) {
        In in = new In(args[0]);
        KdTree tree = new KdTree();

        while (!in.isEmpty()) {
            Point2D point = new Point2D(in.readDouble(), in.readDouble());
            tree.insert(point);
        }

        tree.draw();

        StdDraw.setPenRadius(0.02);
        Point2D query = new Point2D(StdRandom.uniform(), StdRandom.uniform());
        StdDraw.setPenColor(StdDraw.GREEN);
        query.draw();

        Stopwatch sw = new Stopwatch();
        Point2D result = tree.nearest(query);
        double time = sw.elapsedTime();

        StdDraw.setPenColor(StdDraw.ORANGE);
        result.draw();
        StdOut.println(time);
    }
}
