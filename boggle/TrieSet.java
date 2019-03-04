/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Queue;

import java.util.Iterator;

public final class TrieSet implements Iterable<String> {
    private static final int R = 26;
    private Node root;

    static final class Node {
        private Node[] next = new Node[R];
        private int size = 0;
        private boolean isString;

        Node next(char c) {
            if (!Character.isUpperCase(c))
                throw new IllegalArgumentException("Not an upper case char: " + c);

            return next[c - 'A'];
        }

        boolean isString() {
            return isString;
        }

        boolean isPrefix() {
            return size != 0;
        }
    }

    private int size(Node node) {
        return node == null ? 0 : node.size;
    }

    Node root() {
        return root;
    }

    public void add(String str) {
        root = add(root, str, 0);
    }

    private Node add(Node node, String data, int d) {
        if (node == null) node = new Node();
        if (d == data.length()) {
            if (!node.isString) node.size++;
            node.isString = true;
        } else {
            int i = data.charAt(d) - 'A';
            node.size -= size(node.next[i]);
            node.next[i] = add(node.next[i], data, d + 1);
            node.size += size(node.next[i]);
        }

        assert node.size >= 0;
        return node;
    }

    public int size() {
        return size(root);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(String str) {
        Node node = root;
        int d = 0, n = str.length();
        while (node != null && d < n)
            node = node.next[str.charAt(d++) - 'A'];
        return node != null && node.isString;
    }

    public void delete(String str) {
        root = delete(root, str, 0);
    }

    private Node delete(Node node, String data, int d) {
        if (node == null) return null;
        if (d == data.length()) {
            if (node.isString) node.size--;
            node.isString = false;
        } else {
            int i = data.charAt(d) - 'A';
            node.size -= size(node.next[i]);
            node.next[i] = delete(node.next[i], data, d + 1);
            node.size += size(node.next[i]);
        }

        assert node.size >= 0;
        return node.size != 0 ? node : null;
    }

    @Override
    public Iterator<String> iterator() {
        Queue<String> queue = new Queue<>();
        collect(root, new StringBuilder(), queue);
        return queue.iterator();
    }

    private void collect(Node node, StringBuilder word, Queue<String> queue) {
        if (node == null) return;
        if (node.isString) queue.enqueue(word.toString());
        for (int c = 0; c < R; c++) {
            word.append((char) (c + 'A'));
            collect(node.next[c], word, queue);
            word.deleteCharAt(word.length() - 1);
        }
    }

    @Override
    public String toString() {
        Queue<String> queue = new Queue<>();
        collect(root, new StringBuilder(), queue);
        return String.format("{ %s }", queue);
    }
}
