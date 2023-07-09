import com.craftinginterpreters.lox.Lox;
import org.junit.Test;

public class ParserTest {

    @Test
    public void test1(){
        String source = "6 - 8 >=0";
        Lox.run(source);
    }
}
