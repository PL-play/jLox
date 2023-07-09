import com.craftinginterpreters.lox.AstPrinter;
import com.craftinginterpreters.lox.Expr;
import com.craftinginterpreters.lox.Token;
import com.craftinginterpreters.lox.TokenType;
import org.junit.Test;

public class AstPrinterTest {
    @Test
    public void test1() {
        Expr expr = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(new Expr.Literal(45.67)));
        System.out.println(new AstPrinter().print(expr));

    }
}
