/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public final class CircularSuffixArray {
    private static final int CUTOFF = 15;

    private final char[] data;
    private final int length;
    private final int[] array;

    public CircularSuffixArray(String s) {
        validateArgument(s);
        data = s.toCharArray();
        length = s.length();
        array = new int[length];
        for (int i = 0; i < length; i++) array[i] = i;
        sort(0, length - 1, 0);
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private void validateArgument(String s) {
        if (s == null)
            throw new IllegalArgumentException("String is 'null'");
    }

    private void validateArgument(int i) {
        if (i < 0 || i >= length)
            throw new IllegalArgumentException("Invalid index value");
    }

    private void sort(int lower, int upper, int d) {
        if (upper <= lower + CUTOFF) {
            insertion(lower, upper, d);
            return;
        }

        char pivot = charAt(lower, d);
        int lt = lower, gt = upper;
        int i = lower + 1;

        while (i <= gt) {
            char c = charAt(i, d);
            if      (c > pivot) swap(array, i, gt--);
            else if (c < pivot) swap(array, i++, lt++);
            else                i++;
        }

        sort(lower, lt - 1, d);
        sort(lt, gt, d + 1);
        sort(gt + 1, upper, d);
    }

    private void insertion(int lower, int upper, int d) {
        for (int i = lower; i <= upper; i++) {
            for (int j = i; j > lower && less(j, j - 1, d); j--)
                swap(array, j, j - 1);
        }
    }

    private char charAt(int i, int d) {
        return data[(array[i] + d) % length];
    }

    private boolean less(int i, int j, int d) {
        for (int k = d; k < length; k++) {
            char a = charAt(i, k), b = charAt(j, k);
            if (a < b) return true;
            if (a > b) return false;
        }

        return false;
    }

    public int length() {
        return length;
    }

    public int index(int i) {
        validateArgument(i);
        return array[i];
    }

    public static void main(String[] args) {
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            String[] a = new String[s.length()];
            String[] b = new String[s.length()];

            for (int i = 0; i < a.length; i++)
                a[i] = s.substring(i) + s.substring(0, i);
            Arrays.sort(a);


            CircularSuffixArray array = new CircularSuffixArray(s);
            for (int i = 0; i < array.length(); i++) {
                int index = array.index(i);
                String suffix = s.substring(index) + s.substring(0, index);
                b[i] = suffix;
            }

            boolean equals = Arrays.equals(a, b);
            StdOut.println(equals);

            if (!equals) {
                StdOut.println("Arrays.sort:");
                for (String item : a) StdOut.println(item);
                StdOut.println();

                StdOut.println("CircularSuffixArray:");
                for (String item : b) StdOut.println(item);
                StdOut.println();
            }
        }
    }
}
