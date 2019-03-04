/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public final class MoveToFront {
    private static final int R = 256;

    public static void encode() {
        char[] ctoi = new char[R]; // char -> index (index stored as char)
        char[] itoc = new char[R]; // index -> char
        for (char i = 0; i < R; i++) {
            ctoi[i] = i;
            itoc[i] = i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write(ctoi[c]);
            for (char i = ctoi[c]; i > 0; i--) {
                itoc[i] = itoc[i - 1];
                ctoi[itoc[i]] = i;
            }
            itoc[0] = c;
            ctoi[c] = 0;
        }

        BinaryStdOut.close();
    }

    public static void decode() {
        char[] itoc = new char[R];
        for (char i = 0; i < R; i++) itoc[i] = i;
        while (!BinaryStdIn.isEmpty()) {
            char i = BinaryStdIn.readChar();
            char c = itoc[i];
            BinaryStdOut.write(c);
            System.arraycopy(itoc, 0, itoc, 1, i);
            itoc[0] = c;
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("+")) decode();
        else if (args[0].equals("-")) encode();
        else throw new IllegalArgumentException("Illegal argument: " + args[0]);
    }
}
