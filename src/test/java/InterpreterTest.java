import com.craftinginterpreters.lox.Lox;
import org.junit.Test;

public class InterpreterTest {
    @Test
    public void test1(){
        Lox.run("print 1;");
    }

    @Test
    public void test2(){
        Lox.run("print 1+1 ;");
    }

    @Test
    public void test3(){
        Lox.run("-1;");
    }

    @Test
    public void test4(){
        Lox.run("print 2*4+(13-23+122/3)-8+(-1)*3+(32-2*2);");
    }

    @Test
    public void test5(){
        Lox.run("print 2>=1;");
    }

    @Test
    public void test6(){
        Lox.run("print \"hello\"+\" world\";");
    }

    @Test
    public void test7(){
        Lox.run("print 1!=nil;");
    }

    @Test
    public void test8(){
        Lox.run("print 1+\"2\";");
    }

    @Test
    public void test9(){
        Lox.run("print !false;");
    }
    @Test
    public void test10(){
        Lox.run("print !true;");
    }

    @Test
    public void test11(){
        Lox.run("print \"a\" == \"a\";");
    }
    @Test
    public void test12(){
        Lox.run("print 1 == \"a\";");
    }

    @Test
    public void test13(){
        Lox.run("print   nil == nil;");
    }

    @Test
    public void test14(){
        Lox.run("var a=; var b=; print a+b;");
    }

    @Test
    public void test15(){
        Lox.run("var a=1; a=3; print a;");
    }

}
