import com.craftinginterpreters.lox.Scanner;
import org.junit.Test;

public class ScannerTest {

    @Test
    public void test1() {
        String source = "({})<= > != , ; +-*/= \"abe\"";
        Scanner scanner = new Scanner(source);
        for (var token : scanner.scanTokens()) {
            System.out.println(token);
        }
    }

    @Test
    public void test2() {
        String source = "\"hello \n world!\"";
        Scanner scanner = new Scanner(source);
        for (var token : scanner.scanTokens()) {
            System.out.println(token);
        }
    }


}
