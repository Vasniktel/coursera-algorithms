import java.util.NoSuchElementException;
import java.util.Iterator;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Deque<Item> implements Iterable<Item> {
    private Node<Item> first;
    private Node<Item> last;
    private int size;

    private static class Node<Item> {
        private final Item item;
        private Node<Item> next;
        private Node<Item> prev;

        private Node(Item item, Node<Item> next, Node<Item> prev) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }

    private static class DequeIterator<Item> implements Iterator<Item> {
        private Node<Item> elem;

        private DequeIterator(Node<Item> elem) {
            this.elem = elem;
        }

        @Override
        public boolean hasNext() {
            return this.elem != null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("'remove' is not supported.");
        }

        @Override
        public Item next() {
            if (!this.hasNext())
                throw new NoSuchElementException("Nothing to iterate in deque.");

            Item data = this.elem.item;
            this.elem = this.elem.next;
            return data;
        }
    }

    public Deque() {
        this.first = null;
        this.last = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int size() {
        return this.size;
    }

    public void addFirst(Item item) {
        if (item == null)
            throw new IllegalArgumentException("Argument is 'null'.");

        Node<Item> old = this.first;
        this.first = new Node<>(item, old, null);
        if (this.isEmpty()) this.last = this.first;
        else old.prev = this.first;
        this.size++;
    }

    public void addLast(Item item) {
        if (item == null)
            throw new IllegalArgumentException("Argument is 'null'.");

        Node<Item> old = this.last;
        this.last = new Node<>(item, null, old);
        if (this.isEmpty()) this.first = this.last;
        else old.next = this.last;
        this.size++;
    }

    public Item removeFirst() {
        if (this.isEmpty())
            throw new NoSuchElementException("Deque is empty.");

        Item data = this.first.item;
        this.first = this.first.next;
        this.size--;
        if (this.isEmpty()) this.last = null;
        else this.first.prev = null;
        return data;
    }

    public Item removeLast() {
        if (this.isEmpty())
            throw new NoSuchElementException("Deque is empty.");

        Item data = this.last.item;
        this.last = this.last.prev;
        this.size--;
        if (this.isEmpty()) this.first = null;
        else this.last.next = null;
        return data;
    }

    @Override
    public Iterator<Item> iterator() {
        return new DequeIterator<>(this.first);
    }

    public static void main(String[] args) {
        Deque<String> deque = new Deque<>();

        while (!StdIn.isEmpty()) {
            String[] query = StdIn.readLine().split(" ");

            if (query[0].equals("add")) {
                if (query[1].equals("front"))
                    deque.addFirst(query[2]);
                else
                    deque.addLast(query[2]);
            } else {
                if (query[1].equals("front"))
                    StdOut.printf("removed front: '%s'\n", deque.removeFirst());
                else
                    StdOut.printf("removed back: '%s'\n", deque.removeLast());
            }

            StdOut.printf("Deque is of size: %d\n", deque.size());
            for (String item : deque) {
                StdOut.print(item + " ");
            }
            StdOut.println();
        }
    }
}
