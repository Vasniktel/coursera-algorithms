import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class InputWrapper {
    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("aesop.txt.bwt"));
        // System.setOut(new PrintStream("decoded.gif"));
        BurrowsWheeler.main(new String[] { "+" });
    }
}
