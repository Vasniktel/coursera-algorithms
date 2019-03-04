import java.io.FileInputStream;
import java.io.IOException;

public class InputWrapper {
    public static void main(String[] args) throws IOException {
        System.setIn(new FileInputStream("queues/tale.txt"));
        Permutation.main("20");
    }
}
