/* *****************************************************************************
 *  Name: BoggleSolver
 *  Date: 07.09.2018
 *  Description: Boggle Solver class for programming assignment
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public final class BoggleSolver {
    private final TrieSet dict;
    private boolean[][] onStack;
    private TrieSet found;
    private BoggleBoard curr;

    public BoggleSolver(String[] dictionary) {
        dict = new TrieSet();
        for (String word : dictionary) dict.add(word);
    }

    private boolean isValid(int row, int col) {
        return row >= 0 && row < curr.rows() && col >= 0 && col < curr.cols();
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        int n = board.rows(), m = board.cols();
        onStack = new boolean[n][m];
        found = new TrieSet();
        curr = board;
        TrieSet.Node root = dict.root();

        if (root != null) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    char c = curr.getLetter(i, j);
                    TrieSet.Node node = root.next(c);
                    String y = c + "";

                    if (c == 'Q') {
                        if (node != null) {
                            node = node.next('U');
                            y += 'U';
                        } else continue;
                    }

                    dfs(node, i, j, new StringBuilder(y));
                }
            }
        }

        return found;
    }

    private void dfs(TrieSet.Node node, int row, int col, StringBuilder word) {
        if (node == null) return;
        onStack[row][col] = true;

        if (node.isString() && word.length() >= 3)
            found.add(word.toString());

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i == row && j == col) continue;
                if (isValid(i, j) && !onStack[i][j]) {
                    char c = curr.getLetter(i, j);
                    TrieSet.Node x = node.next(c);
                    String y = c + "";

                    if (c == 'Q') {
                        if (x != null) {
                            x = x.next('U');
                            y += 'U';
                        } else continue;
                    }

                    dfs(x, i, j, word.append(y));
                    word.delete(word.length() - y.length(), word.length());
                }
            }
        }

        onStack[row][col] = false;
    }

    public int scoreOf(String word) {
        if (!dict.contains(word)) return 0;
        switch (word.length()) {
            case 0:
            case 1:
            case 2:  return 0;
            case 3:
            case 4:  return 1;
            case 5:  return 2;
            case 6:  return 3;
            case 7:  return 5;
            default: return 11;
        }
    }

    public static void main(String... args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
