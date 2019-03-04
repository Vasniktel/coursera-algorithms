import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Iterator;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] arr;
    private int size;

    private class RandomizedQueueIterator implements Iterator<Item> {
        private final Item[] data;
        private int i;

        private RandomizedQueueIterator(Item[] data) {
            this.data = data;
            this.i = 0;
        }

        @Override
        public boolean hasNext() {
            return this.i != this.data.length;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("'remove' is not supported.");
        }

        @Override
        public Item next() {
            if (!this.hasNext())
                throw new NoSuchElementException("Nothing to iterate in queue.");

            if (this.data.length != RandomizedQueue.this.size)
                throw new ConcurrentModificationException("Iterator was invalidated.");

            return this.data[this.i++];
        }
    }

    public RandomizedQueue() {
        this.arr = (Item[]) new Object[2];
        this.size = 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int size() {
        return this.size;
    }

    private void resize(int capacity) {
        assert this.size <= capacity;

        Item[] resized = (Item[]) new Object[capacity];
        System.arraycopy(this.arr, 0, resized, 0, this.size);
        this.arr = resized;
    }

    public void enqueue(Item item) {
        if (item == null)
            throw new IllegalArgumentException("Argument is 'null'.");

        if (this.size == this.arr.length)
            this.resize(2 * this.size);
        this.arr[this.size++] = item;
    }

    public Item dequeue() {
        if (this.isEmpty())
            throw new NoSuchElementException("Queue is empty.");

        int index = StdRandom.uniform(this.size);
        Item data = this.arr[index];
        this.arr[index] = this.arr[--this.size];
        this.arr[this.size] = null;
        if (this.size > 0 && this.size <= this.arr.length / 4)
            this.resize(this.arr.length / 2);
        return data;
    }

    public Item sample() {
        if (this.isEmpty())
            throw new NoSuchElementException("Queue is empty.");

        return this.arr[StdRandom.uniform(this.size)];
    }

    @Override
    public Iterator<Item> iterator() {
        Item[] data = (Item[]) new Object[this.size];
        System.arraycopy(this.arr, 0, data, 0, this.size);
        StdRandom.shuffle(data);
        return new RandomizedQueueIterator(data);
    }

    public static void main(String[] args) {
        RandomizedQueue<String> queue = new RandomizedQueue<>();

        while (!StdIn.isEmpty()) {
            String[] query = StdIn.readLine().split(" ");

            if (query[0].equals("add")) queue.enqueue(query[1]);
            else StdOut.printf("removed: '%s'\n", queue.dequeue());

            StdOut.printf("Queue is of size: %d\n", queue.size());
            for (String item : queue) {
                StdOut.print(item + " ");
            }
            StdOut.println();
        }
    }
}
