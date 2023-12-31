package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    final Environment globals = new Environment();
    private final Map<Expr, Integer> locals = new HashMap<>();
    private Environment environment = globals;


    Interpreter() {
        globals.define("clock", new LoxCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
        globals.define("Array", new LoxCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                int size = (int) (double) arguments.get(0);
                return new LoxArray(size);
            }
        });
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
        return value;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case GRATER -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left > (double) right;
            }
            case GRATER_EQUAL -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left >= (double) right;
            }
            case LESS -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left < (double) right;
            }
            case LESS_EQUAL -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left <= (double) right;
            }
            case BANG_EQUAL -> {
                return !isEqual(left, right);
            }
            case EQUAL_EQUAL -> {
                return isEqual(left, right);
            }
            case MINUS -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left - (double) right;
            }
            case SLASH -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left / (double) right;
            }
            case STAR -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left * (double) right;
            }
            case PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return left + (String) right;
                }
                if (left instanceof String && right instanceof Double) {
                    return left + String.valueOf((double) right);
                }
                if (left instanceof Double && right instanceof String) {
                    return String.valueOf((double) left) + right;
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            }
        }
        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }
        if (!(callee instanceof LoxCallable function)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }
        return function.call(this, arguments);

    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof LoxInstance) {
            return ((LoxInstance) object).get(expr.name);
        }
        throw new RuntimeError(expr.name, "Only instances have properties.");
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) {
                return left;
            }
        } else {
            if (!isTruthy(left)) {
                return left;
            }
        }
        return evaluate(expr.right);
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);
        if (!(object instanceof LoxInstance)) {
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }
        Object value = evaluate(expr.value);
        ((LoxInstance) object).set(expr.name, value);
        return value;
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = locals.get(expr);
        LoxClass superClass = (LoxClass) environment.getAt(distance, "super");
        LoxInstance object = (LoxInstance) environment.getAt(distance - 1, "this");
        LoxFunction method = superClass.findMethod(expr.method.lexeme);
        if (method == null) {
            throw new RuntimeError(expr.method, "Undefined property '" + expr.method.lexeme + "'.");
        }
        return method.bind(object);
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case BANG -> {
                return !isTruthy(right);
            }
            case MINUS -> {
                checkNumberOperand(expr.operator, right);
                return -(double) right;
            }
        }
        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
        // return environment.get(expr.name);
    }

    @Override
    public Object visitFunctionExpr(Expr.Function expr) {
        return new LoxFunction(null, expr, environment, false);
    }

    @Override
    public Object visitTernaryExpr(Expr.Ternary expr) {
        if (isTruthy(evaluate(expr.expr))) {
            return evaluate(expr.thenBranch);
        }
        return evaluate(expr.elseBranch);
    }

    @Override
    public Object visitArrayExpr(Expr.Array expr) {
        LoxArray array = new LoxArray(expr.elements.size());
        List<Object> elements = new ArrayList<>();
        for (Expr e : expr.elements) {
            elements.add(evaluate(e));
        }
        array.setElements(elements);
        return array;
    }

    @Override
    public Object visitArrayGetExpr(Expr.ArrayGet expr) {
        // TODO index out of bounds check.
        Object object = evaluate(expr.object);
        if (object instanceof LoxArray) {
            Object index = evaluate(expr.index);
            if (!(index instanceof Double)) {
                throw new RuntimeError(expr.bracket, "array index must be a int.");
            }
            return ((LoxArray) object).getElementByIndex((int) (double) index);
        }
        throw new RuntimeError(expr.bracket, "Only array can be accessed by index.");
    }

    @Override
    public Object visitArraySetExpr(Expr.ArraySet expr) {
        // TODO index out of bounds check.
        Object object = evaluate(expr.object);
        if (!(object instanceof LoxArray)) {
            throw new RuntimeError(expr.bracket, "Only array can be set.");
        }
        Object index = evaluate(expr.index);
        if (!(index instanceof Double)) {
            throw new RuntimeError(expr.bracket, "array index must be a int.");
        }
        Object value = evaluate(expr.value);
        ((LoxArray) object).setElementByIndex((int) (double) index, value);
        return value;
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    private String stringify(Object object) {
        if (object == null) {
            return "nil";
        }
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }


    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return (boolean) object;
        }
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }
        return a.equals(b);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperand(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operands must be a numbers.");
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        throw new BreakException();
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        throw new ContinueException();
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superClass = null;
        if (stmt.superClass != null) {
            superClass = evaluate(stmt.superClass);
            if (!(superClass instanceof LoxClass)) {
                throw new RuntimeError(stmt.superClass.name, "Superclass must be a class.");
            }
        }
        environment.define(stmt.name.lexeme, null);

        if (stmt.superClass != null) {
            environment = new Environment(environment);
            environment.define("super", superClass);
        }

        Map<String, LoxFunction> classMethods = new HashMap<>();
        for (Stmt.Function method : stmt.classMethods) {
            LoxFunction function = new LoxFunction(method.name.lexeme, method.function, environment, false);
            classMethods.put(method.name.lexeme, function);
        }

        LoxClass metaClass = new LoxClass(null, stmt.name.lexeme + "_metaclass", null, classMethods);

        Map<String, LoxFunction> methods = applyTraits(stmt.traits);
        for (Stmt.Function method : stmt.methods) {
            LoxFunction function = new LoxFunction(method.name.lexeme, method.function, environment, method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, function);
        }
        LoxClass klass = new LoxClass(metaClass, stmt.name.lexeme, (LoxClass) superClass, methods);
        if (superClass != null) {
            environment = environment.enclosing;
        }
        environment.assign(stmt.name, klass);
        return null;
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        String fnName = stmt.name.lexeme;
        environment.define(fnName, new LoxFunction(fnName, stmt.function, environment, false));
        return null;
    }


    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);

        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) {
            value = evaluate(stmt.value);
        }
        throw new Return(value);
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }
        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {

        while (isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);
            } catch (BreakException ex) {
                break;
            } catch (ContinueException ex) {
                // TODO fix bug, increment statement in for statement lookup variable distance incorrect?
                // Should it be continue statement and break statement in Resolver do something with scope?
                if (stmt.increment != null) {
                    Environment previous = this.environment;
                    try {
                        this.environment = new Environment(environment);
                        ;
                        execute(stmt.increment);
                    } finally {
                        this.environment = previous;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Void visitTraitStmt(Stmt.Trait stmt) {
        environment.define(stmt.name.lexeme, null);

        Map<String, LoxFunction> methods = applyTraits(stmt.traits);

        for (Stmt.Function method : stmt.methods) {
            if (methods.containsKey(method.name.lexeme)) {
                throw new RuntimeError(method.name,
                        "A previous trait declares a method named '" +
                                method.name.lexeme + "'.");
            }

            LoxFunction function = new LoxFunction(method.name.lexeme, method.function, environment, false);
            methods.put(method.name.lexeme, function);
        }

        LoxTrait trait = new LoxTrait(stmt.name, methods);

        environment.assign(stmt.name, trait);
        return null;
    }

    private Map<String, LoxFunction> applyTraits(List<Expr.Variable> traits) {
        Map<String, LoxFunction> methods = new HashMap<>();

        for (Expr.Variable traitExpr : traits) {
            Object traitObject = evaluate(traitExpr);
            if (!(traitObject instanceof LoxTrait trait)) {
                Token name = traitExpr.name;
                throw new RuntimeError(name,
                        "'" + name.lexeme + "' is not a trait.");
            }

            for (String name : trait.methods.keySet()) {
                if (methods.containsKey(name)) {
                    throw new RuntimeError(trait.name,
                            "A previous trait declares a method named '" +
                                    name + "'.");
                }

                methods.put(name, trait.methods.get(name));
            }
        }
        return methods;
    }
}
