import com.craftinginterpreters.lox.Scanner;
import org.junit.Test;

public class ScannerTest {

    @Test
    public void test1() {
        String source = "{}";
        Scanner scanner = new Scanner(source);
        for (var token : scanner.scanTokens()) {
            System.out.println(token);
        }
    }
}
