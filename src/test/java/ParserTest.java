import com.craftinginterpreters.lox.*;
import org.junit.Test;

import java.util.List;

public class ParserTest {

    @Test
    public void test1(){
        String source = "a( b(c)(),2,3);";
        var scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        System.out.println(statements);
    }
}
