import com.craftinginterpreters.lox.Lox;
import org.junit.Test;

public class InterpreterTest {
    @Test
    public void test1() {
        Lox.run("print 1;");
    }

    @Test
    public void test2() {
        Lox.run("print 1+1 ;");
    }

    @Test
    public void test3() {
        Lox.run("-1;");
    }

    @Test
    public void test4() {
        Lox.run("print 2*4+(13-23+122/3)-8+(-1)*3+(32-2*2);");
    }

    @Test
    public void test5() {
        Lox.run("print 2>=1;");
    }

    @Test
    public void test6() {
        Lox.run("print \"hello\"+\" world\";");
    }

    @Test
    public void test7() {
        Lox.run("print 1!=nil;");
    }

    @Test
    public void test8() {
        Lox.run("print 1+\"2\";");
    }

    @Test
    public void test9() {
        Lox.run("print !false;");
    }

    @Test
    public void test10() {
        Lox.run("print !true;");
    }

    @Test
    public void test11() {
        Lox.run("print \"a\" == \"a\";");
    }

    @Test
    public void test12() {
        Lox.run("print 1 == \"a\";");
    }

    @Test
    public void test13() {
        Lox.run("print   nil == nil;");
    }

    @Test
    public void test14() {
        Lox.run("var a=; var b=; print a+b;");
    }

    @Test
    public void test15() {
        Lox.run("-a=1; print a;");
    }

    @Test
    public void test16() {
        Lox.run(
                "var a = \"global a\";\n" +
                        "var b = \"global b\";\n" +
                        "var c = \"global c\";\n" +
                        "{\n" +
                        "var a = \"outer a\";\n" +
                        "var b = \"outer b\";\n" +
                        "{\n" +
                        "var a = \"inner a\";\n" +
                        "print a;\n" +
                        "print b;\n" +
                        "print c;\n" +
                        "}\n" +
                        "print a;\n" +
                        "print b;\n" +
                        "print c;\n" +
                        "}\n" +
                        "print a;\n" +
                        "print b;\n" +
                        "print c;");
    }

    @Test
    public void test17() {
        Lox.run(
                "print \"hi\" or false;" +
                        "print true and false;" +
                        "print nil or \"yes\";" +
                        "if (2>=3) {print 2; print 1; } else print 3;");
    }


    @Test
    public void test18() {
        Lox.run(
                "var a = 5;" +
                        "while (a>0){" +
                        "print a;" +
                        "a = a-1;" +
                        "}");
    }

}
